package thaumcraft.common.lib.events;
import com.google.common.base.Predicate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockBamf;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.devices.TileArcaneEar;
import thaumcraft.common.world.ThaumcraftWorldGenerator;
import thaumcraft.common.world.aura.AuraHandler;
import thaumcraft.common.world.aura.AuraThread;


@Mod.EventBusSubscriber
public class ServerEvents
{
    long lastcheck;
    static HashMap<Integer, Integer> serverTicks;
    public static ConcurrentHashMap<Integer, AuraThread> auraThreads;
    DecimalFormat myFormatter;
    public static HashMap<Integer, LinkedBlockingQueue<BreakData>> breakList;
    public static HashMap<Integer, LinkedBlockingQueue<VirtualSwapper>> swapList;
    public static HashMap<Integer, ArrayList<ChunkPos>> chunksToGenerate;
    public static Predicate<SwapperPredicate> DEFAULT_PREDICATE;
    private static HashMap<Integer, LinkedBlockingQueue<RunnableEntry>> serverRunList;
    private static LinkedBlockingQueue<RunnableEntry> clientRunList;
    
    public ServerEvents() {
        lastcheck = 0L;
        myFormatter = new DecimalFormat("#######.##");
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void clientWorldTick(TickEvent.ClientTickEvent event) {
        if (event.side == Side.SERVER) {
            return;
        }
        if (event.phase == TickEvent.Phase.END && !ServerEvents.clientRunList.isEmpty()) {
            LinkedBlockingQueue<RunnableEntry> temp = new LinkedBlockingQueue<RunnableEntry>();
            while (!ServerEvents.clientRunList.isEmpty()) {
                RunnableEntry current = ServerEvents.clientRunList.poll();
                if (current != null) {
                    if (current.delay > 0) {
                        RunnableEntry runnableEntry = current;
                        --runnableEntry.delay;
                        temp.offer(current);
                    }
                    else {
                        try {
                            current.runnable.run();
                        }
                        catch (Exception ex) {}
                    }
                }
            }
            while (!temp.isEmpty()) {
                ServerEvents.clientRunList.offer(temp.poll());
            }
        }
    }
    
    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event) {
        if (event.side == Side.CLIENT) {
            return;
        }
        int dim = event.world.provider.getDimension();
        if (event.phase == TickEvent.Phase.START) {
            if (!ServerEvents.auraThreads.containsKey(dim) && AuraHandler.getAuraWorld(dim) != null) {
                AuraThread at = new AuraThread(dim);
                Thread thread = new Thread(at);
                thread.start();
                ServerEvents.auraThreads.put(dim, at);
            }
        }
        else {
            if (!ServerEvents.serverTicks.containsKey(dim)) {
                ServerEvents.serverTicks.put(dim, 0);
            }
            LinkedBlockingQueue<RunnableEntry> rlist = ServerEvents.serverRunList.get(dim);
            if (rlist == null) {
                ServerEvents.serverRunList.put(dim, rlist = new LinkedBlockingQueue<RunnableEntry>());
            }
            else if (!rlist.isEmpty()) {
                LinkedBlockingQueue<RunnableEntry> temp = new LinkedBlockingQueue<RunnableEntry>();
                while (!rlist.isEmpty()) {
                    RunnableEntry current = rlist.poll();
                    if (current != null) {
                        if (current.delay > 0) {
                            RunnableEntry runnableEntry = current;
                            --runnableEntry.delay;
                            temp.offer(current);
                        }
                        else {
                            try {
                                current.runnable.run();
                            }
                            catch (Exception ex) {}
                        }
                    }
                }
                while (!temp.isEmpty()) {
                    rlist.offer(temp.poll());
                }
            }
            int ticks = ServerEvents.serverTicks.get(dim);
            tickChunkRegeneration(event);
            tickBlockSwap(event.world);
            tickBlockBreak(event.world);
            ArrayList<Integer[]> nbe = TileArcaneEar.noteBlockEvents.get(dim);
            if (nbe != null) {
                nbe.clear();
            }
            if (ticks % 20 == 0) {
                CopyOnWriteArrayList<ChunkPos> dc = AuraHandler.dirtyChunks.get(dim);
                if (dc != null && dc.size() > 0) {
                    for (ChunkPos pos : dc) {
                        event.world.markChunkDirty(pos.getBlock(5, 5, 5), null);
                    }
                    dc.clear();
                }
                if (AuraHandler.riftTrigger.containsKey(dim)) {
                    if (!ModConfig.CONFIG_MISC.wussMode) {
                        EntityFluxRift.createRift(event.world, AuraHandler.riftTrigger.get(dim));
                    }
                    AuraHandler.riftTrigger.remove(dim);
                }
                TaskHandler.clearSuspendedOrExpiredTasks(event.world);
            }
            SealHandler.tickSealEntities(event.world);
            ServerEvents.serverTicks.put(dim, ticks + 1);
        }
    }
    
    public static void tickChunkRegeneration(TickEvent.WorldTickEvent event) {
        int dim = event.world.provider.getDimension();
        int count = 0;
        ArrayList<ChunkPos> chunks = ServerEvents.chunksToGenerate.get(dim);
        if (chunks != null && chunks.size() > 0) {
            for (int a = 0; a < 10; ++a) {
                chunks = ServerEvents.chunksToGenerate.get(dim);
                if (chunks == null || chunks.size() <= 0) {
                    break;
                }
                ++count;
                ChunkPos loc = chunks.get(0);
                long worldSeed = event.world.getSeed();
                Random fmlRandom = new Random(worldSeed);
                long xSeed = fmlRandom.nextLong() >> 3;
                long zSeed = fmlRandom.nextLong() >> 3;
                fmlRandom.setSeed(xSeed * loc.x + zSeed * loc.z ^ worldSeed);
                ThaumcraftWorldGenerator.INSTANCE.worldGeneration(fmlRandom, loc.x, loc.z, event.world, false);
                chunks.remove(0);
                ServerEvents.chunksToGenerate.put(dim, chunks);
            }
        }
        if (count > 0) {
            FMLCommonHandler.instance().getFMLLogger().log(Level.INFO, "[Thaumcraft] Regenerated " + count + " chunks. " + Math.max(0, chunks.size()) + " chunks left");
        }
    }
    
    private static void tickBlockSwap(World world) {
        int dim = world.provider.getDimension();
        LinkedBlockingQueue<VirtualSwapper> queue = ServerEvents.swapList.get(dim);
        LinkedBlockingQueue<VirtualSwapper> queue2 = new LinkedBlockingQueue<VirtualSwapper>();
        if (queue != null) {
            while (!queue.isEmpty()) {
                VirtualSwapper vs = queue.poll();
                if (vs != null) {
                    IBlockState bs = world.getBlockState(vs.pos);
                    boolean allow = bs.getBlockHardness(world, vs.pos) >= 0.0f;
                    if ((vs.source != null && vs.source instanceof IBlockState && vs.source != bs) || (vs.source != null && vs.source instanceof Material && vs.source != bs.getMaterial())) {
                        allow = false;
                    }
                    if (vs.visCost > 0.0f && AuraHelper.getVis(world, vs.pos) < vs.visCost) {
                        allow = false;
                    }
                    if (!world.canMineBlockBody(vs.player, vs.pos) || !allow || (vs.target != null && !vs.target.isEmpty() && vs.target.isItemEqual(new ItemStack(bs.getBlock(), 1, bs.getBlock().getMetaFromState(bs)))) || ForgeEventFactory.onPlayerBlockPlace(vs.player, new BlockSnapshot(world, vs.pos, bs), EnumFacing.UP, EnumHand.MAIN_HAND).isCanceled() || !vs.allowSwap.apply(new SwapperPredicate(world, vs.player, vs.pos))) {
                        continue;
                    }
                    int slot = -1;
                    if (!vs.consumeTarget || vs.target == null || vs.target.isEmpty()) {
                        slot = 1;
                    }
                    else {
                        slot = InventoryUtils.getPlayerSlotFor(vs.player, vs.target);
                    }
                    if (vs.player.capabilities.isCreativeMode) {
                        slot = 1;
                    }
                    boolean matches = false;
                    if (vs.source instanceof Material) {
                        matches = (bs.getMaterial() == vs.source);
                    }
                    if (vs.source instanceof IBlockState) {
                        matches = (bs == vs.source);
                    }
                    if ((vs.source != null && !matches) || slot < 0) {
                        continue;
                    }
                    if (!vs.player.capabilities.isCreativeMode) {
                        if (vs.consumeTarget) {
                            vs.player.inventory.decrStackSize(slot, 1);
                        }
                        if (vs.pickup) {
                            List<ItemStack> ret = new ArrayList<ItemStack>();
                            if (vs.silk && bs.getBlock().canSilkHarvest(world, vs.pos, bs, vs.player)) {
                                ItemStack itemstack = BlockUtils.getSilkTouchDrop(bs);
                                if (itemstack != null && !itemstack.isEmpty()) {
                                    ret.add(itemstack);
                                }
                            }
                            else {
                                ret = bs.getBlock().getDrops(world, vs.pos, bs, vs.fortune);
                            }
                            if (ret.size() > 0) {
                                for (ItemStack is : ret) {
                                    if (!vs.player.inventory.addItemStackToInventory(is)) {
                                        world.spawnEntity(new EntityItem(world, vs.pos.getX() + 0.5, vs.pos.getY() + 0.5, vs.pos.getZ() + 0.5, is));
                                    }
                                }
                            }
                        }
                        if (vs.visCost > 0.0f) {
                            ThaumcraftApi.internalMethods.drainVis(world, vs.pos, vs.visCost, false);
                        }
                    }
                    if (vs.target == null || vs.target.isEmpty()) {
                        world.setBlockToAir(vs.pos);
                    }
                    else {
                        Block tb = Block.getBlockFromItem(vs.target.getItem());
                        if (tb != null && tb != Blocks.AIR) {
                            world.setBlockState(vs.pos, tb.getStateFromMeta(vs.target.getItemDamage()), 3);
                        }
                        else {
                            world.setBlockToAir(vs.pos);
                            EntitySpecialItem entityItem = new EntitySpecialItem(world, vs.pos.getX() + 0.5, vs.pos.getY() + 0.1, vs.pos.getZ() + 0.5, vs.target.copy());
                            entityItem.motionY = 0.0;
                            entityItem.motionX = 0.0;
                            entityItem.motionZ = 0.0;
                            world.spawnEntity(entityItem);
                        }
                    }
                    if (vs.fx) {
                        PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockBamf(vs.pos, vs.color, true, vs.fancy, null), new NetworkRegistry.TargetPoint(world.provider.getDimension(), vs.pos.getX(), vs.pos.getY(), vs.pos.getZ(), 32.0));
                    }
                    if (vs.lifespan <= 0) {
                        continue;
                    }
                    for (int xx = -1; xx <= 1; ++xx) {
                        for (int yy = -1; yy <= 1; ++yy) {
                            for (int zz = -1; zz <= 1; ++zz) {
                                matches = false;
                                if (vs.source instanceof Material) {
                                    IBlockState bb = world.getBlockState(vs.pos.add(xx, yy, zz));
                                    matches = (bb.getBlock().getMaterial(bb) == vs.source);
                                }
                                if (vs.source instanceof IBlockState) {
                                    matches = (world.getBlockState(vs.pos.add(xx, yy, zz)) == vs.source);
                                }
                                if ((xx != 0 || yy != 0 || zz != 0) && matches && BlockUtils.isBlockExposed(world, vs.pos.add(xx, yy, zz))) {
                                    queue2.offer(new VirtualSwapper(vs.pos.add(xx, yy, zz), vs.source, vs.target, vs.consumeTarget, vs.lifespan - 1, vs.player, vs.fx, vs.fancy, vs.color, vs.pickup, vs.silk, vs.fortune, vs.allowSwap, vs.visCost));
                                }
                            }
                        }
                    }
                }
            }
            ServerEvents.swapList.put(dim, queue2);
        }
    }
    
    private static void tickBlockBreak(World world) {
        int dim = world.provider.getDimension();
        LinkedBlockingQueue<BreakData> queue = ServerEvents.breakList.get(dim);
        LinkedBlockingQueue<BreakData> queue2 = new LinkedBlockingQueue<BreakData>();
        if (queue != null) {
            while (!queue.isEmpty()) {
                BreakData vs = queue.poll();
                if (vs != null) {
                    IBlockState bs = world.getBlockState(vs.pos);
                    if (bs == vs.source) {
                        if (vs.visCost > 0.0f && AuraHelper.getVis(world, vs.pos) < vs.visCost) {
                            continue;
                        }
                        if (!world.canMineBlockBody(vs.player, vs.pos) || bs.getBlockHardness(world, vs.pos) < 0.0f) {
                            continue;
                        }
                        if (vs.fx) {
                            world.sendBlockBreakProgress(vs.pos.hashCode(), vs.pos, (int)((1.0f - vs.durabilityCurrent / vs.durabilityMax) * 10.0f));
                        }
                        BreakData breakData = vs;
                        breakData.durabilityCurrent -= vs.strength;
                        if (vs.durabilityCurrent <= 0.0f) {
                            BlockUtils.harvestBlock(world, vs.player, vs.pos, true, vs.silk, vs.fortune, false);
                            if (vs.fx) {
                                world.sendBlockBreakProgress(vs.pos.hashCode(), vs.pos, -1);
                            }
                            if (vs.visCost <= 0.0f) {
                                continue;
                            }
                            ThaumcraftApi.internalMethods.drainVis(world, vs.pos, vs.visCost, false);
                        }
                        else {
                            queue2.offer(new BreakData(vs.strength, vs.durabilityCurrent, vs.durabilityMax, vs.pos, vs.source, vs.player, vs.fx, vs.silk, vs.fortune, vs.visCost));
                        }
                    }
                    else {
                        if (!vs.fx) {
                            continue;
                        }
                        world.sendBlockBreakProgress(vs.pos.hashCode(), vs.pos, -1);
                    }
                }
            }
            ServerEvents.breakList.put(dim, queue2);
        }
    }
    
    public static void addSwapper(World world, BlockPos pos, Object source, ItemStack target, boolean consumeTarget, int life, EntityPlayer player, boolean fx, boolean fancy, int color, boolean pickup, boolean silk, int fortune, Predicate<SwapperPredicate> allowSwap, float visCost) {
        int dim = world.provider.getDimension();
        LinkedBlockingQueue<VirtualSwapper> queue = ServerEvents.swapList.get(dim);
        if (queue == null) {
            ServerEvents.swapList.put(dim, new LinkedBlockingQueue<VirtualSwapper>());
            queue = ServerEvents.swapList.get(dim);
        }
        queue.offer(new VirtualSwapper(pos, source, target, consumeTarget, life, player, fx, fancy, color, pickup, silk, fortune, allowSwap, visCost));
        ServerEvents.swapList.put(dim, queue);
    }
    
    public static void addBreaker(World world, BlockPos pos, IBlockState source, EntityPlayer player, boolean fx, boolean silk, int fortune, float str, float durabilityCurrent, float durabilityMax, int delay, float vis, Runnable run) {
        int dim = world.provider.getDimension();
        if (delay > 0) {
            addRunnableServer(world, new Runnable() {
                @Override
                public void run() {
                    ServerEvents.addBreaker(world, pos, source, player, fx, silk, fortune, str, durabilityCurrent, durabilityMax, 0, vis, run);
                }
            }, delay);
        }
        else {
            LinkedBlockingQueue<BreakData> queue = ServerEvents.breakList.get(dim);
            if (queue == null) {
                ServerEvents.breakList.put(dim, new LinkedBlockingQueue<BreakData>());
                queue = ServerEvents.breakList.get(dim);
            }
            queue.offer(new BreakData(str, durabilityCurrent, durabilityMax, pos, source, player, fx, silk, fortune, vis));
            ServerEvents.breakList.put(dim, queue);
            if (run != null) {
                run.run();
            }
        }
    }
    
    public static void addRunnableServer(World world, Runnable runnable, int delay) {
        if (world.isRemote) {
            return;
        }
        LinkedBlockingQueue<RunnableEntry> rlist = ServerEvents.serverRunList.get(world.provider.getDimension());
        if (rlist == null) {
            ServerEvents.serverRunList.put(world.provider.getDimension(), rlist = new LinkedBlockingQueue<RunnableEntry>());
        }
        rlist.add(new RunnableEntry(runnable, delay));
    }
    
    public static void addRunnableClient(World world, Runnable runnable, int delay) {
        if (!world.isRemote) {
            return;
        }
        ServerEvents.clientRunList.add(new RunnableEntry(runnable, delay));
    }
    
    static {
        ServerEvents.serverTicks = new HashMap<Integer, Integer>();
        ServerEvents.auraThreads = new ConcurrentHashMap<Integer, AuraThread>();
        ServerEvents.breakList = new HashMap<Integer, LinkedBlockingQueue<BreakData>>();
        ServerEvents.swapList = new HashMap<Integer, LinkedBlockingQueue<VirtualSwapper>>();
        ServerEvents.chunksToGenerate = new HashMap<Integer, ArrayList<ChunkPos>>();
        DEFAULT_PREDICATE = new Predicate<SwapperPredicate>() {
            public boolean apply(@Nullable SwapperPredicate pred) {
                return true;
            }
        };
        ServerEvents.serverRunList = new HashMap<Integer, LinkedBlockingQueue<RunnableEntry>>();
        ServerEvents.clientRunList = new LinkedBlockingQueue<RunnableEntry>();
    }
    
    public static class BreakData
    {
        float strength;
        float durabilityCurrent;
        float durabilityMax;
        IBlockState source;
        BlockPos pos;
        EntityPlayer player;
        boolean fx;
        boolean silk;
        int fortune;
        float visCost;
        
        public BreakData(float strength, float durabilityCurrent, float durabilityMax, BlockPos pos, IBlockState source, EntityPlayer player, boolean fx, boolean silk, int fortune, float vis) {
            this.strength = 0.0f;
            this.durabilityCurrent = 1.0f;
            this.durabilityMax = 1.0f;
            this.player = null;
            this.strength = strength;
            this.source = source;
            this.pos = pos;
            this.player = player;
            this.fx = fx;
            this.silk = silk;
            this.fortune = fortune;
            this.durabilityCurrent = durabilityCurrent;
            this.durabilityMax = durabilityMax;
            visCost = vis;
        }
    }
    
    public static class SwapperPredicate
    {
        public World world;
        public EntityPlayer player;
        public BlockPos pos;
        
        public SwapperPredicate(World world, EntityPlayer player, BlockPos pos) {
            this.world = world;
            this.player = player;
            this.pos = pos;
        }
    }
    
    public static class VirtualSwapper
    {
        int color;
        boolean fancy;
        Predicate<SwapperPredicate> allowSwap;
        int lifespan;
        BlockPos pos;
        Object source;
        ItemStack target;
        EntityPlayer player;
        boolean fx;
        boolean silk;
        boolean pickup;
        boolean consumeTarget;
        int fortune;
        float visCost;
        
        VirtualSwapper(BlockPos pos, Object source, ItemStack t, boolean consumeTarget, int life, EntityPlayer p, boolean fx, boolean fancy, int color, boolean pickup, boolean silk, int fortune, Predicate<SwapperPredicate> allowSwap, float cost) {
            lifespan = 0;
            player = null;
            this.pos = pos;
            this.source = source;
            target = t;
            lifespan = life;
            player = p;
            this.consumeTarget = consumeTarget;
            this.fx = fx;
            this.fancy = fancy;
            this.allowSwap = allowSwap;
            this.silk = silk;
            this.fortune = fortune;
            this.pickup = pickup;
            this.color = color;
            visCost = cost;
        }
    }
    
    public static class RunnableEntry
    {
        Runnable runnable;
        int delay;
        
        public RunnableEntry(Runnable runnable, int delay) {
            this.runnable = runnable;
            this.delay = delay;
        }
    }
}
