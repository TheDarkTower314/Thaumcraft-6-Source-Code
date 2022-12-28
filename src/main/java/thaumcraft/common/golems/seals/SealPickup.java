package thaumcraft.common.golems.seals;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.utils.InventoryUtils;


public class SealPickup extends SealFiltered implements ISealConfigArea
{
    int delay;
    HashMap<Integer, Integer> itemEntities;
    ResourceLocation icon;
    protected ISealConfigToggles.SealToggle[] props;
    
    public SealPickup() {
        delay = new Random(System.nanoTime()).nextInt(100);
        itemEntities = new HashMap<Integer, Integer>();
        icon = new ResourceLocation("thaumcraft", "items/seals/seal_pickup");
        props = new ISealConfigToggles.SealToggle[] { new ISealConfigToggles.SealToggle(true, "pmeta", "golem.prop.meta"), new ISealConfigToggles.SealToggle(true, "pnbt", "golem.prop.nbt"), new ISealConfigToggles.SealToggle(false, "pore", "golem.prop.ore"), new ISealConfigToggles.SealToggle(false, "pmod", "golem.prop.mod") };
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:pickup";
    }
    
    @Override
    public void tickSeal(World world, ISealEntity seal) {
        if (delay++ % 5 != 0) {
            return;
        }
        AxisAlignedBB area = GolemHelper.getBoundsForArea(seal);
        List list = world.getEntitiesWithinAABB(EntityItem.class, area);
        if (list.size() > 0) {
            for (Object e : list) {
                EntityItem ent = (EntityItem)e;
                if (ent != null && ent.onGround && !ent.cannotPickup() && ent.getItem() != null && !itemEntities.containsValue(ent.getEntityId())) {
                    ItemStack stack = InventoryUtils.findFirstMatchFromFilter(filter, filterSize, isBlacklist(), NonNullList.withSize(1, ent.getItem()), new ThaumcraftInvHelper.InvFilter(!props[0].value, !props[1].value, props[2].value, props[3].value));
                    if (stack != null && !stack.isEmpty()) {
                        Task task = new Task(seal.getSealPos(), ent);
                        task.setPriority(seal.getPriority());
                        itemEntities.put(task.getId(), ent.getEntityId());
                        TaskHandler.addTask(world.provider.getDimension(), task);
                        break;
                    }
                    continue;
                }
            }
        }
        if (delay % 100 != 0) {
            Iterator<Integer> it = itemEntities.values().iterator();
            while (it.hasNext()) {
                Entity e2 = world.getEntityByID(it.next());
                if (e2 != null) {
                    if (!e2.isDead) {
                        continue;
                    }
                }
                try {
                    it.remove();
                }
                catch (Exception ex) {}
            }
        }
    }
    
    @Override
    public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
        EntityItem ei = getItemEntity(world, task);
        if (ei != null && !ei.getItem().isEmpty()) {
            ItemStack stack = InventoryUtils.findFirstMatchFromFilter(filter, filterSize, isBlacklist(), NonNullList.withSize(1, ei.getItem()), new ThaumcraftInvHelper.InvFilter(!props[0].value, !props[1].value, props[2].value, props[3].value));
            if (stack != null && !stack.isEmpty()) {
                ItemStack is = golem.holdItem(ei.getItem());
                if (is != null && !is.isEmpty() && is.getCount() > 0) {
                    ei.setItem(is);
                }
                if (is == null || is.isEmpty() || is.getCount() <= 0) {
                    ei.setDead();
                }
                ((Entity)golem).playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.125f, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
                golem.swingArm();
            }
        }
        task.setSuspended(true);
        itemEntities.remove(task.getId());
        ArrayList<Task> localTasks = TaskHandler.getEntityTasksSorted(world.provider.getDimension(), null, (Entity)golem);
        for (Task ticket : localTasks) {
            if (itemEntities.containsKey(ticket.getId()) && ticket.canGolemPerformTask(golem) && ((EntityThaumcraftGolem)golem).isWithinHomeDistanceFromPosition(ticket.getEntity().getPosition())) {
                ((EntityThaumcraftGolem)golem).setTask(ticket);
                ((EntityThaumcraftGolem)golem).getTask().setReserved(true);
                if (ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
                    world.setEntityState((Entity)golem, (byte)5);
                    break;
                }
                break;
            }
        }
        return true;
    }
    
    protected EntityItem getItemEntity(World world, Task task) {
        Integer ei = itemEntities.get(task.getId());
        if (ei != null) {
            Entity ent = world.getEntityByID(ei);
            if (ent != null && ent instanceof EntityItem) {
                return (EntityItem)ent;
            }
        }
        return null;
    }
    
    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        EntityItem ei = getItemEntity(golem.getGolemWorld(), task);
        if (ei == null || ei.getItem() == null) {
            return false;
        }
        if (ei.isDead) {
            task.setSuspended(true);
            return false;
        }
        return golem.canCarry(ei.getItem(), true);
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
        return new int[] { 2, 1, 0, 4 };
    }
    
    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return null;
    }
    
    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.CLUMSY };
    }
    
    @Override
    public void onTaskStarted(World world, IGolemAPI golem, Task task) {
    }
    
    @Override
    public void onTaskSuspension(World world, Task task) {
    }
    
    @Override
    public void onRemoval(World world, BlockPos pos, EnumFacing side) {
    }
}
