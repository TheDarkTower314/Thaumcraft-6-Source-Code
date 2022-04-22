// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.seals;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.golems.EnumGolemTrait;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.EntityLiving;
import thaumcraft.api.golems.IGolemAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.common.golems.tasks.TaskHandler;
import net.minecraft.entity.Entity;
import thaumcraft.api.golems.tasks.Task;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.seals.ISealEntity;
import net.minecraft.world.World;
import java.util.Random;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.seals.ISeal;

public class SealGuard implements ISeal, ISealGui, ISealConfigArea
{
    int delay;
    protected ISealConfigToggles.SealToggle[] props;
    ResourceLocation icon;
    
    public SealGuard() {
        this.delay = new Random(System.nanoTime()).nextInt(22);
        this.props = new ISealConfigToggles.SealToggle[] { new ISealConfigToggles.SealToggle(true, "pmob", "golem.prop.mob"), new ISealConfigToggles.SealToggle(false, "panimal", "golem.prop.animal"), new ISealConfigToggles.SealToggle(false, "pplayer", "golem.prop.player") };
        this.icon = new ResourceLocation("thaumcraft", "items/seals/seal_guard");
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:guard";
    }
    
    @Override
    public void tickSeal(final World world, final ISealEntity seal) {
        if (this.delay++ % 20 != 0) {
            return;
        }
        final AxisAlignedBB area = GolemHelper.getBoundsForArea(seal);
        final List list = world.getEntitiesWithinAABB(EntityLivingBase.class, area);
        if (list.size() > 0) {
            for (final Object e : list) {
                final EntityLivingBase target = (EntityLivingBase)e;
                if (this.isValidTarget(target)) {
                    final Task task = new Task(seal.getSealPos(), target);
                    task.setPriority(seal.getPriority());
                    task.setLifespan((short)10);
                    TaskHandler.addTask(world.provider.getDimension(), task);
                }
            }
        }
    }
    
    private boolean isValidTarget(final EntityLivingBase target) {
        boolean valid = false;
        if (this.props[0].value && (target instanceof IMob || target instanceof EntityMob)) {
            valid = true;
        }
        if (this.props[1].value && (target instanceof EntityAnimal || target instanceof IAnimals)) {
            valid = true;
        }
        if (this.props[2].value && FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled() && target instanceof EntityPlayer) {
            valid = true;
        }
        return valid;
    }
    
    @Override
    public void onTaskStarted(final World world, final IGolemAPI golem, final Task task) {
        if (task.getEntity() != null && task.getEntity() instanceof EntityLivingBase && this.isValidTarget((EntityLivingBase)task.getEntity())) {
            ((EntityLiving)golem).setAttackTarget((EntityLivingBase)task.getEntity());
            golem.addRankXp(1);
        }
        task.setSuspended(true);
    }
    
    @Override
    public boolean onTaskCompletion(final World world, final IGolemAPI golem, final Task task) {
        task.setSuspended(true);
        return true;
    }
    
    @Override
    public boolean canGolemPerformTask(final IGolemAPI golem, final Task task) {
        return !golem.getGolemEntity().isOnSameTeam(task.getEntity());
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
    public int[] getGuiCategories() {
        return new int[] { 2, 0, 4 };
    }
    
    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.FIGHTER };
    }
    
    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return null;
    }
    
    @Override
    public void onTaskSuspension(final World world, final Task task) {
    }
    
    @Override
    public void readCustomNBT(final NBTTagCompound nbt) {
    }
    
    @Override
    public void writeCustomNBT(final NBTTagCompound nbt) {
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
}
