// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.seals;

import thaumcraft.api.golems.EnumGolemTrait;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import java.util.UUID;
import net.minecraft.init.SoundEvents;
import thaumcraft.api.golems.IGolemAPI;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.common.golems.tasks.TaskHandler;
import net.minecraft.entity.Entity;
import thaumcraft.api.golems.tasks.Task;
import net.minecraft.item.ItemStack;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.api.ThaumcraftInvHelper;
import net.minecraft.util.NonNullList;
import net.minecraft.entity.item.EntityItem;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.seals.ISealEntity;
import net.minecraft.world.World;
import java.util.Random;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import net.minecraft.util.ResourceLocation;
import java.util.HashMap;
import thaumcraft.api.golems.seals.ISealConfigArea;

public class SealPickup extends SealFiltered implements ISealConfigArea
{
    int delay;
    HashMap<Integer, Integer> itemEntities;
    ResourceLocation icon;
    protected ISealConfigToggles.SealToggle[] props;
    
    public SealPickup() {
        this.delay = new Random(System.nanoTime()).nextInt(100);
        this.itemEntities = new HashMap<Integer, Integer>();
        this.icon = new ResourceLocation("thaumcraft", "items/seals/seal_pickup");
        this.props = new ISealConfigToggles.SealToggle[] { new ISealConfigToggles.SealToggle(true, "pmeta", "golem.prop.meta"), new ISealConfigToggles.SealToggle(true, "pnbt", "golem.prop.nbt"), new ISealConfigToggles.SealToggle(false, "pore", "golem.prop.ore"), new ISealConfigToggles.SealToggle(false, "pmod", "golem.prop.mod") };
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:pickup";
    }
    
    @Override
    public void tickSeal(final World world, final ISealEntity seal) {
        if (this.delay++ % 5 != 0) {
            return;
        }
        final AxisAlignedBB area = GolemHelper.getBoundsForArea(seal);
        final List list = world.getEntitiesWithinAABB(EntityItem.class, area);
        if (list.size() > 0) {
            for (final Object e : list) {
                final EntityItem ent = (EntityItem)e;
                if (ent != null && ent.onGround && !ent.cannotPickup() && ent.getItem() != null && !this.itemEntities.containsValue(ent.getEntityId())) {
                    final ItemStack stack = InventoryUtils.findFirstMatchFromFilter(this.filter, this.filterSize, this.isBlacklist(), NonNullList.withSize(1, ent.getItem()), new ThaumcraftInvHelper.InvFilter(!this.props[0].value, !this.props[1].value, this.props[2].value, this.props[3].value));
                    if (stack != null && !stack.isEmpty()) {
                        final Task task = new Task(seal.getSealPos(), ent);
                        task.setPriority(seal.getPriority());
                        this.itemEntities.put(task.getId(), ent.getEntityId());
                        TaskHandler.addTask(world.provider.getDimension(), task);
                        break;
                    }
                    continue;
                }
            }
        }
        if (this.delay % 100 != 0) {
            final Iterator<Integer> it = this.itemEntities.values().iterator();
            while (it.hasNext()) {
                final Entity e2 = world.getEntityByID(it.next());
                if (e2 != null) {
                    if (!e2.isDead) {
                        continue;
                    }
                }
                try {
                    it.remove();
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    @Override
    public boolean onTaskCompletion(final World world, final IGolemAPI golem, final Task task) {
        final EntityItem ei = this.getItemEntity(world, task);
        if (ei != null && !ei.getItem().isEmpty()) {
            final ItemStack stack = InventoryUtils.findFirstMatchFromFilter(this.filter, this.filterSize, this.isBlacklist(), NonNullList.withSize(1, ei.getItem()), new ThaumcraftInvHelper.InvFilter(!this.props[0].value, !this.props[1].value, this.props[2].value, this.props[3].value));
            if (stack != null && !stack.isEmpty()) {
                final ItemStack is = golem.holdItem(ei.getItem());
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
        this.itemEntities.remove(task.getId());
        final ArrayList<Task> localTasks = TaskHandler.getEntityTasksSorted(world.provider.getDimension(), null, (Entity)golem);
        for (final Task ticket : localTasks) {
            if (this.itemEntities.containsKey(ticket.getId()) && ticket.canGolemPerformTask(golem) && ((EntityThaumcraftGolem)golem).isWithinHomeDistanceFromPosition(ticket.getEntity().getPosition())) {
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
    
    protected EntityItem getItemEntity(final World world, final Task task) {
        final Integer ei = this.itemEntities.get(task.getId());
        if (ei != null) {
            final Entity ent = world.getEntityByID(ei);
            if (ent != null && ent instanceof EntityItem) {
                return (EntityItem)ent;
            }
        }
        return null;
    }
    
    @Override
    public boolean canGolemPerformTask(final IGolemAPI golem, final Task task) {
        final EntityItem ei = this.getItemEntity(golem.getGolemWorld(), task);
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
    public boolean canPlaceAt(final World world, final BlockPos pos, final EnumFacing side) {
        return !world.isAirBlock(pos);
    }
    
    @Override
    public ResourceLocation getSealIcon() {
        return this.icon;
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
    public void onTaskStarted(final World world, final IGolemAPI golem, final Task task) {
    }
    
    @Override
    public void onTaskSuspension(final World world, final Task task) {
    }
    
    @Override
    public void onRemoval(final World world, final BlockPos pos, final EnumFacing side) {
    }
}
