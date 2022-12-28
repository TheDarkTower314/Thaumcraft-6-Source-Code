package thaumcraft.common.golems.seals;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.golems.tasks.TaskHandler;


public class SealButcher implements ISeal, ISealGui, ISealConfigArea
{
    int delay;
    boolean wait;
    ResourceLocation icon;
    
    public SealButcher() {
        delay = new Random(System.nanoTime()).nextInt(200);
        wait = false;
        icon = new ResourceLocation("thaumcraft", "items/seals/seal_butcher");
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:butcher";
    }
    
    @Override
    public void tickSeal(World world, ISealEntity seal) {
        if (delay++ % 200 != 0 || wait) {
            return;
        }
        AxisAlignedBB area = GolemHelper.getBoundsForArea(seal);
        List list = world.getEntitiesWithinAABB(EntityLivingBase.class, area);
        if (list.size() > 0) {
            for (Object e : list) {
                EntityLivingBase target = (EntityLivingBase)e;
                if (isValidTarget(target)) {
                    List<EntityLivingBase> var55 = world.getEntitiesWithinAABB(target.getClass(), area);
                    Iterator<EntityLivingBase> var56;
                    int count;
                    EntityLivingBase var57;
                    for (var56 = var55.iterator(), count = 0; var56.hasNext() && count < 3; ++count) {
                        var57 = var56.next();
                        if (isValidTarget(var57)) {}
                    }
                    if (count > 2) {
                        Task task = new Task(seal.getSealPos(), target);
                        task.setPriority(seal.getPriority());
                        task.setLifespan((short)10);
                        TaskHandler.addTask(world.provider.getDimension(), task);
                        wait = true;
                        break;
                    }
                    continue;
                }
            }
        }
    }
    
    private boolean isValidTarget(EntityLivingBase target) {
        return (target instanceof EntityAnimal || target instanceof IAnimals) && !(target instanceof IMob) && (!(target instanceof EntityTameable) || !((EntityTameable)target).isTamed()) && !(target instanceof EntityGolem) && (!(target instanceof EntityAnimal) || !target.isChild());
    }
    
    @Override
    public void onTaskStarted(World world, IGolemAPI golem, Task task) {
        if (task.getEntity() != null && task.getEntity() instanceof EntityLivingBase && isValidTarget((EntityLivingBase)task.getEntity())) {
            ((EntityLiving)golem).setAttackTarget((EntityLivingBase)task.getEntity());
            golem.addRankXp(1);
        }
        task.setSuspended(true);
        wait = false;
    }
    
    @Override
    public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
        task.setSuspended(true);
        wait = false;
        return true;
    }
    
    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        return true;
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
        return new EnumGolemTrait[] { EnumGolemTrait.FIGHTER, EnumGolemTrait.SMART };
    }
    
    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return null;
    }
    
    @Override
    public void onTaskSuspension(World world, Task task) {
        wait = false;
    }
    
    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
    }
    
    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
    }
    
    @Override
    public void onRemoval(World world, BlockPos pos, EnumFacing side) {
        wait = false;
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
