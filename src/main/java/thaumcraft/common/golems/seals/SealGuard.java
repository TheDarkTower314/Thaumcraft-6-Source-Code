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
        delay = new Random(System.nanoTime()).nextInt(22);
        props = new ISealConfigToggles.SealToggle[] { new ISealConfigToggles.SealToggle(true, "pmob", "golem.prop.mob"), new ISealConfigToggles.SealToggle(false, "panimal", "golem.prop.animal"), new ISealConfigToggles.SealToggle(false, "pplayer", "golem.prop.player") };
        icon = new ResourceLocation("thaumcraft", "items/seals/seal_guard");
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:guard";
    }
    
    @Override
    public void tickSeal(World world, ISealEntity seal) {
        if (delay++ % 20 != 0) {
            return;
        }
        AxisAlignedBB area = GolemHelper.getBoundsForArea(seal);
        List list = world.getEntitiesWithinAABB(EntityLivingBase.class, area);
        if (list.size() > 0) {
            for (Object e : list) {
                EntityLivingBase target = (EntityLivingBase)e;
                if (isValidTarget(target)) {
                    Task task = new Task(seal.getSealPos(), target);
                    task.setPriority(seal.getPriority());
                    task.setLifespan((short)10);
                    TaskHandler.addTask(world.provider.getDimension(), task);
                }
            }
        }
    }
    
    private boolean isValidTarget(EntityLivingBase target) {
        boolean valid = false;
        if (props[0].value && (target instanceof IMob || target instanceof EntityMob)) {
            valid = true;
        }
        if (props[1].value && (target instanceof EntityAnimal || target instanceof IAnimals)) {
            valid = true;
        }
        if (props[2].value && FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled() && target instanceof EntityPlayer) {
            valid = true;
        }
        return valid;
    }
    
    @Override
    public void onTaskStarted(World world, IGolemAPI golem, Task task) {
        if (task.getEntity() != null && task.getEntity() instanceof EntityLivingBase && isValidTarget((EntityLivingBase)task.getEntity())) {
            ((EntityLiving)golem).setAttackTarget((EntityLivingBase)task.getEntity());
            golem.addRankXp(1);
        }
        task.setSuspended(true);
    }
    
    @Override
    public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
        task.setSuspended(true);
        return true;
    }
    
    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        return !golem.getGolemEntity().isOnSameTeam(task.getEntity());
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
    public void onTaskSuspension(World world, Task task) {
    }
    
    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
    }
    
    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
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
}
