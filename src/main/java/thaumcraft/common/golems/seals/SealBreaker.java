package thaumcraft.common.golems.seals;
import com.mojang.authlib.GameProfile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.network.FakeNetHandlerPlayServer;
import thaumcraft.common.lib.utils.BlockUtils;


public class SealBreaker extends SealFiltered implements ISealConfigArea, ISealConfigToggles
{
    int delay;
    HashMap<Integer, Long> cache;
    ResourceLocation icon;
    protected SealToggle[] props;
    
    public SealBreaker() {
        delay = new Random(System.nanoTime()).nextInt(42);
        cache = new HashMap<Integer, Long>();
        icon = new ResourceLocation("thaumcraft", "items/seals/seal_breaker");
        props = new SealToggle[] { new SealToggle(true, "pmeta", "golem.prop.meta") };
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:breaker";
    }
    
    @Override
    public void tickSeal(World world, ISealEntity seal) {
        if (delay % 100 == 0) {
            Iterator<Integer> it = cache.keySet().iterator();
            while (it.hasNext()) {
                Task t = TaskHandler.getTask(world.provider.getDimension(), it.next());
                if (t == null) {
                    it.remove();
                }
            }
        }
        ++delay;
        BlockPos p = GolemHelper.getPosInArea(seal, delay);
        if (!cache.containsValue(p.toLong()) && isValidBlock(world, p)) {
            Task task = new Task(seal.getSealPos(), p);
            task.setPriority(seal.getPriority());
            task.setData((int)(world.getBlockState(p).getBlockHardness(world, p) * 10.0f));
            TaskHandler.addTask(world.provider.getDimension(), task);
            cache.put(task.getId(), p.toLong());
        }
    }
    
    private boolean isValidBlock(World world, BlockPos p) {
        IBlockState bs = world.getBlockState(p);
        if (!world.isAirBlock(p) && bs.getBlockHardness(world, p) >= 0.0f) {
            for (ItemStack ts : getInv()) {
                if (ts != null && !ts.isEmpty()) {
                    ItemStack fs = BlockUtils.getSilkTouchDrop(bs);
                    if (fs == null || !fs.isEmpty()) {
                        fs = new ItemStack(bs.getBlock(), 1, getToggles()[0].value ? bs.getBlock().getMetaFromState(bs) : 32767);
                    }
                    if (!getToggles()[0].value) {
                        fs.setItemDamage(32767);
                    }
                    if (isBlacklist()) {
                        if (OreDictionary.itemMatches(fs, ts, getToggles()[0].value)) {
                            return false;
                        }
                        continue;
                    }
                    else {
                        if (!OreDictionary.itemMatches(fs, ts, getToggles()[0].value)) {
                            return false;
                        }
                        continue;
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
        IBlockState bs = world.getBlockState(task.getPos());
        if (cache.containsKey(task.getId()) && isValidBlock(world, task.getPos())) {
            FakePlayer fp = FakePlayerFactory.get((WorldServer)world, new GameProfile(null, "FakeThaumcraftGolem"));
            fp.connection = new FakeNetHandlerPlayServer(fp.mcServer, new NetworkManager(EnumPacketDirection.CLIENTBOUND), fp);
            fp.setPosition(golem.getGolemEntity().posX, golem.getGolemEntity().posY, golem.getGolemEntity().posZ);
            golem.swingArm();
            boolean silky = getToggles().length > 1 && getToggles()[1].value;
            int bspd = silky ? 7 : 21;
            if (task.getData() > bspd) {
                float bh = bs.getBlockHardness(world, task.getPos()) * 10.0f;
                task.setLifespan((short)Math.max(task.getLifespan(), 10L));
                task.setData(task.getData() - bspd);
                int progress = (int)(9.0f * (1.0f - task.getData() / bh));
                world.playSound(null, task.getPos(), bs.getBlock().getSoundType().getBreakSound(), SoundCategory.BLOCKS, (bs.getBlock().getSoundType().getVolume() + 0.7f) / 8.0f, bs.getBlock().getSoundType().getPitch() * 0.5f);
                BlockUtils.destroyBlockPartially(world, golem.getGolemEntity().getEntityId(), task.getPos(), progress);
                return false;
            }
            BlockUtils.destroyBlockPartially(world, golem.getGolemEntity().getEntityId(), task.getPos(), 10);
            BlockUtils.harvestBlock(world, fp, task.getPos(), true, silky, 0, true);
            golem.addRankXp(1);
            cache.remove(task.getId());
        }
        task.setSuspended(true);
        return true;
    }
    
    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        if (cache.containsKey(task.getId()) && isValidBlock(golem.getGolemWorld(), task.getPos())) {
            return true;
        }
        task.setSuspended(true);
        return false;
    }
    
    @Override
    public void onTaskSuspension(World world, Task task) {
        cache.remove(task.getId());
    }
    
    @Override
    public boolean canPlaceAt(World world, BlockPos pos, EnumFacing side) {
        return !world.isAirBlock(pos);
    }
    
    @Override
    public ResourceLocation getSealIcon() {
        return icon;
    }
    
    @Override
    public void onRemoval(World world, BlockPos pos, EnumFacing side) {
    }
    
    @Override
    public Object returnContainer(World world, EntityPlayer player, BlockPos pos, EnumFacing side, ISealEntity seal) {
        return new SealBaseContainer(player.inventory, world, seal);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Object returnGui(World world, EntityPlayer player, BlockPos pos, EnumFacing side, ISealEntity seal) {
        return new SealBaseGUI(player.inventory, world, seal);
    }
    
    @Override
    public int[] getGuiCategories() {
        return new int[] { 2, 1, 3, 0, 4 };
    }
    
    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.BREAKER };
    }
    
    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return null;
    }
    
    @Override
    public void onTaskStarted(World world, IGolemAPI golem, Task task) {
    }
    
    @Override
    public SealToggle[] getToggles() {
        return props;
    }
    
    @Override
    public void setToggle(int indx, boolean value) {
        props[indx].setValue(value);
    }
}
