// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.seals;

import thaumcraft.api.golems.EnumGolemTrait;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.player.EntityPlayerMP;
import thaumcraft.common.lib.network.FakeNetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.EnumPacketDirection;
import net.minecraftforge.common.util.FakePlayerFactory;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.world.WorldServer;
import thaumcraft.api.golems.IGolemAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.common.lib.utils.BlockUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import java.util.Iterator;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.api.golems.seals.ISealEntity;
import net.minecraft.world.World;
import java.util.Random;
import net.minecraft.util.ResourceLocation;
import java.util.HashMap;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealConfigArea;

public class SealBreaker extends SealFiltered implements ISealConfigArea, ISealConfigToggles
{
    int delay;
    HashMap<Integer, Long> cache;
    ResourceLocation icon;
    protected SealToggle[] props;
    
    public SealBreaker() {
        this.delay = new Random(System.nanoTime()).nextInt(42);
        this.cache = new HashMap<Integer, Long>();
        this.icon = new ResourceLocation("thaumcraft", "items/seals/seal_breaker");
        this.props = new SealToggle[] { new SealToggle(true, "pmeta", "golem.prop.meta") };
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:breaker";
    }
    
    @Override
    public void tickSeal(final World world, final ISealEntity seal) {
        if (this.delay % 100 == 0) {
            final Iterator<Integer> it = this.cache.keySet().iterator();
            while (it.hasNext()) {
                final Task t = TaskHandler.getTask(world.provider.getDimension(), it.next());
                if (t == null) {
                    it.remove();
                }
            }
        }
        ++this.delay;
        final BlockPos p = GolemHelper.getPosInArea(seal, this.delay);
        if (!this.cache.containsValue(p.toLong()) && this.isValidBlock(world, p)) {
            final Task task = new Task(seal.getSealPos(), p);
            task.setPriority(seal.getPriority());
            task.setData((int)(world.getBlockState(p).getBlockHardness(world, p) * 10.0f));
            TaskHandler.addTask(world.provider.getDimension(), task);
            this.cache.put(task.getId(), p.toLong());
        }
    }
    
    private boolean isValidBlock(final World world, final BlockPos p) {
        final IBlockState bs = world.getBlockState(p);
        if (!world.isAirBlock(p) && bs.getBlockHardness(world, p) >= 0.0f) {
            for (final ItemStack ts : this.getInv()) {
                if (ts != null && !ts.isEmpty()) {
                    ItemStack fs = BlockUtils.getSilkTouchDrop(bs);
                    if (fs == null || !fs.isEmpty()) {
                        fs = new ItemStack(bs.getBlock(), 1, this.getToggles()[0].value ? bs.getBlock().getMetaFromState(bs) : 32767);
                    }
                    if (!this.getToggles()[0].value) {
                        fs.setItemDamage(32767);
                    }
                    if (this.isBlacklist()) {
                        if (OreDictionary.itemMatches(fs, ts, this.getToggles()[0].value)) {
                            return false;
                        }
                        continue;
                    }
                    else {
                        if (!OreDictionary.itemMatches(fs, ts, this.getToggles()[0].value)) {
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
    public boolean onTaskCompletion(final World world, final IGolemAPI golem, final Task task) {
        final IBlockState bs = world.getBlockState(task.getPos());
        if (this.cache.containsKey(task.getId()) && this.isValidBlock(world, task.getPos())) {
            final FakePlayer fp = FakePlayerFactory.get((WorldServer)world, new GameProfile(null, "FakeThaumcraftGolem"));
            fp.connection = new FakeNetHandlerPlayServer(fp.mcServer, new NetworkManager(EnumPacketDirection.CLIENTBOUND), fp);
            fp.setPosition(golem.getGolemEntity().posX, golem.getGolemEntity().posY, golem.getGolemEntity().posZ);
            golem.swingArm();
            final boolean silky = this.getToggles().length > 1 && this.getToggles()[1].value;
            final int bspd = silky ? 7 : 21;
            if (task.getData() > bspd) {
                final float bh = bs.getBlockHardness(world, task.getPos()) * 10.0f;
                task.setLifespan((short)Math.max(task.getLifespan(), 10L));
                task.setData(task.getData() - bspd);
                final int progress = (int)(9.0f * (1.0f - task.getData() / bh));
                world.playSound(null, task.getPos(), bs.getBlock().getSoundType().getBreakSound(), SoundCategory.BLOCKS, (bs.getBlock().getSoundType().getVolume() + 0.7f) / 8.0f, bs.getBlock().getSoundType().getPitch() * 0.5f);
                BlockUtils.destroyBlockPartially(world, golem.getGolemEntity().getEntityId(), task.getPos(), progress);
                return false;
            }
            BlockUtils.destroyBlockPartially(world, golem.getGolemEntity().getEntityId(), task.getPos(), 10);
            BlockUtils.harvestBlock(world, fp, task.getPos(), true, silky, 0, true);
            golem.addRankXp(1);
            this.cache.remove(task.getId());
        }
        task.setSuspended(true);
        return true;
    }
    
    @Override
    public boolean canGolemPerformTask(final IGolemAPI golem, final Task task) {
        if (this.cache.containsKey(task.getId()) && this.isValidBlock(golem.getGolemWorld(), task.getPos())) {
            return true;
        }
        task.setSuspended(true);
        return false;
    }
    
    @Override
    public void onTaskSuspension(final World world, final Task task) {
        this.cache.remove(task.getId());
    }
    
    @Override
    public boolean canPlaceAt(final World world, final BlockPos pos, final EnumFacing side) {
        return !world.isAirBlock(pos);
    }
    
    @Override
    public ResourceLocation getSealIcon() {
        return this.icon;
    }
    
    @Override
    public void onRemoval(final World world, final BlockPos pos, final EnumFacing side) {
    }
    
    @Override
    public Object returnContainer(final World world, final EntityPlayer player, final BlockPos pos, final EnumFacing side, final ISealEntity seal) {
        return new SealBaseContainer(player.inventory, world, seal);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Object returnGui(final World world, final EntityPlayer player, final BlockPos pos, final EnumFacing side, final ISealEntity seal) {
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
    public void onTaskStarted(final World world, final IGolemAPI golem, final Task task) {
    }
    
    @Override
    public SealToggle[] getToggles() {
        return this.props;
    }
    
    @Override
    public void setToggle(final int indx, final boolean value) {
        this.props[indx].setValue(value);
    }
}
