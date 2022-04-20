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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.lib.utils.BlockUtils;
import net.minecraftforge.common.util.FakePlayerFactory;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.world.WorldServer;
import thaumcraft.api.golems.IGolemAPI;
import net.minecraft.util.math.BlockPos;
import java.util.Iterator;
import thaumcraft.api.golems.tasks.Task;
import net.minecraft.world.IBlockAccess;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.api.golems.seals.ISealEntity;
import net.minecraft.world.World;
import java.util.Random;
import net.minecraft.util.ResourceLocation;
import java.util.HashMap;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.seals.ISeal;

public class SealLumber implements ISeal, ISealGui, ISealConfigArea
{
    int delay;
    HashMap<Integer, Long> cache;
    ResourceLocation icon;
    
    public SealLumber() {
        this.delay = new Random(System.nanoTime()).nextInt(33);
        this.cache = new HashMap<Integer, Long>();
        this.icon = new ResourceLocation("thaumcraft", "items/seals/seal_lumber");
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:lumber";
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
        if (!this.cache.containsValue(p.toLong()) && Utils.isWoodLog(world, p)) {
            final Task task = new Task(seal.getSealPos(), p);
            task.setPriority(seal.getPriority());
            TaskHandler.addTask(world.provider.getDimension(), task);
            this.cache.put(task.getId(), p.toLong());
        }
    }
    
    @Override
    public boolean onTaskCompletion(final World world, final IGolemAPI golem, final Task task) {
        if (this.cache.containsKey(task.getId()) && Utils.isWoodLog(world, task.getPos())) {
            final FakePlayer fp = FakePlayerFactory.get((WorldServer)world, new GameProfile(null, "FakeThaumcraftGolem"));
            fp.setPosition(golem.getGolemEntity().posX, golem.getGolemEntity().posY, golem.getGolemEntity().posZ);
            final IBlockState bs = world.getBlockState(task.getPos());
            golem.swingArm();
            if (BlockUtils.breakFurthestBlock(world, task.getPos(), bs, fp)) {
                task.setLifespan((short)Math.max(task.getLifespan(), 10L));
                golem.addRankXp(1);
                return false;
            }
            this.cache.remove(task.getId());
        }
        task.setSuspended(true);
        return true;
    }
    
    @Override
    public boolean canGolemPerformTask(final IGolemAPI golem, final Task task) {
        if (this.cache.containsKey(task.getId()) && Utils.isWoodLog(golem.getGolemWorld(), task.getPos())) {
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
    public void readCustomNBT(final NBTTagCompound nbt) {
    }
    
    @Override
    public void writeCustomNBT(final NBTTagCompound nbt) {
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
        return new int[] { 2, 0, 4 };
    }
    
    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.BREAKER, EnumGolemTrait.SMART };
    }
    
    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return null;
    }
    
    @Override
    public void onTaskStarted(final World world, final IGolemAPI golem, final Task task) {
    }
}
