// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.seals;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.golems.EnumGolemTrait;
import java.util.Collection;
import java.util.Arrays;
import net.minecraft.entity.IEntityOwnable;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.Entity;
import thaumcraft.api.golems.IGolemAPI;
import net.minecraft.item.ItemStack;
import java.util.ListIterator;
import net.minecraftforge.items.IItemHandler;
import java.util.Iterator;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.lib.utils.InventoryUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3i;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.golems.ProvisionRequest;
import java.util.ArrayList;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.seals.ISealEntity;
import net.minecraft.world.World;
import java.util.Random;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.seals.ISealConfigToggles;

public class SealProvide extends SealFiltered implements ISealConfigToggles
{
    int delay;
    ResourceLocation icon;
    protected SealToggle[] props;
    
    public SealProvide() {
        this.delay = new Random(System.nanoTime()).nextInt(88);
        this.icon = new ResourceLocation("thaumcraft", "items/seals/seal_provider");
        this.props = new SealToggle[] { new SealToggle(true, "pmeta", "golem.prop.meta"), new SealToggle(true, "pnbt", "golem.prop.nbt"), new SealToggle(false, "pore", "golem.prop.ore"), new SealToggle(false, "pmod", "golem.prop.mod"), new SealToggle(false, "psing", "golem.prop.single"), new SealToggle(false, "pleave", "golem.prop.leave") };
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:provider";
    }
    
    @Override
    public int getFilterSize() {
        return 9;
    }
    
    @Override
    public void tickSeal(final World world, final ISealEntity seal) {
        if (this.delay % 100 == 0 && GolemHelper.provisionRequests.containsKey(world.provider.getDimension())) {
            final Iterator<ProvisionRequest> it = GolemHelper.provisionRequests.get(world.provider.getDimension()).iterator();
            while (it.hasNext()) {
                final ProvisionRequest pr = it.next();
                if (pr.isInvalid() || pr.getLinkedTask() == null || pr.getLinkedTask().isSuspended() || pr.getLinkedTask().isCompleted() || pr.getTimeout() < System.currentTimeMillis()) {
                    it.remove();
                }
            }
        }
        if (this.delay++ % 20 != 0) {
            return;
        }
        final IItemHandler inv = ThaumcraftInvHelper.getItemHandlerAt(world, seal.getSealPos().pos, seal.getSealPos().face);
        if (inv != null && GolemHelper.provisionRequests.containsKey(world.provider.getDimension())) {
            final ListIterator<ProvisionRequest> it2 = GolemHelper.provisionRequests.get(world.provider.getDimension()).listIterator();
            while (it2.hasNext()) {
                final ProvisionRequest pr2 = it2.next();
                if (pr2.isInvalid()) {
                    it2.remove();
                }
                else {
                    if (pr2.getLinkedTask() != null) {
                        continue;
                    }
                    if ((pr2.getSeal() == null || pr2.getSeal().getSealPos().pos.distanceSq(seal.getSealPos().pos) >= 4096.0) && (pr2.getEntity() == null || seal.getSealPos().pos.distanceSq(pr2.getEntity().posX, pr2.getEntity().posY, pr2.getEntity().posZ) >= 4096.0) && (pr2.getPos() == null || seal.getSealPos().pos.distanceSq(pr2.getPos()) >= 4096.0)) {
                        continue;
                    }
                    final NonNullList<ItemStack> stacks = NonNullList.withSize(1, pr2.getStack());
                    if (!InventoryUtils.findFirstMatchFromFilter(this.getInv(), this.getSizes(), this.blacklist, stacks, new ThaumcraftInvHelper.InvFilter(!this.props[0].value, !this.props[1].value, this.props[2].value, this.props[3].value)).isEmpty() && ThaumcraftInvHelper.countTotalItemsIn(inv, pr2.getStack(), ThaumcraftInvHelper.InvFilter.STRICT) > (this.props[5].value ? 1 : 0)) {
                        final Task task = new Task(seal.getSealPos(), seal.getSealPos().pos);
                        task.setPriority((pr2.getSeal() != null) ? pr2.getSeal().getPriority() : 5);
                        task.setLifespan((short)((pr2.getSeal() != null) ? 10 : 31000));
                        TaskHandler.addTask(world.provider.getDimension(), task);
                        pr2.setLinkedTask(task);
                        task.setLinkedProvision(pr2);
                        break;
                    }
                    continue;
                }
            }
        }
    }
    
    public boolean matchesFilters(final ItemStack stack) {
        return InventoryUtils.matchesFilters(this.getInv(), this.blacklist, stack, new ThaumcraftInvHelper.InvFilter(!this.props[0].value, !this.props[1].value, this.props[2].value, this.props[3].value));
    }
    
    @Override
    public boolean onTaskCompletion(final World world, final IGolemAPI golem, final Task task) {
        if (task.getLinkedProvision() != null) {
            if (task.getData() == 0) {
                final IItemHandler inv = ThaumcraftInvHelper.getItemHandlerAt(world, task.getSealPos().pos, task.getSealPos().face);
                if (inv != null) {
                    ItemStack stack = ItemStack.EMPTY;
                    try {
                        stack = task.getLinkedProvision().getStack().copy();
                    }
                    catch (final Exception ex) {}
                    if (stack != null && this.props[4].value) {
                        stack.setCount(1);
                    }
                    int sa = 0;
                    if (stack != null && !stack.isEmpty() && this.props[5].value && (sa = ThaumcraftInvHelper.countTotalItemsIn(inv, stack, ThaumcraftInvHelper.InvFilter.STRICT)) <= stack.getCount()) {
                        stack.setCount(sa - 1);
                    }
                    if (stack != null && !stack.isEmpty()) {
                        final int limit = golem.canCarryAmount(stack);
                        if (limit > 0) {
                            final ItemStack s = golem.holdItem(InventoryUtils.removeStackFrom(inv, InventoryUtils.copyLimitedStack(stack, limit), ThaumcraftInvHelper.InvFilter.STRICT, false));
                            if (s != null && !s.isEmpty()) {
                                InventoryUtils.ejectStackAt(world, task.getSealPos().pos.offset(task.getSealPos().face), task.getSealPos().face.getOpposite(), s);
                            }
                            ((Entity)golem).playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.125f, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
                            golem.addRankXp(1);
                            golem.swingArm();
                            final ProvisionRequest pr2 = task.getLinkedProvision();
                            if (pr2.getEntity() != null || pr2.getPos() != null) {
                                Task task2 = null;
                                if (pr2.getEntity() != null) {
                                    task2 = new Task(task.getSealPos(), pr2.getEntity());
                                }
                                else {
                                    task2 = new Task(task.getSealPos(), pr2.getPos());
                                }
                                task2.setPriority(task.getPriority());
                                task2.setData((pr2.getEntity() != null) ? 1 : 2);
                                task2.setLifespan((short)31000);
                                TaskHandler.addTask(world.provider.getDimension(), task2);
                                pr2.setLinkedTask(task2);
                                task2.setLinkedProvision(pr2);
                            }
                        }
                    }
                }
            }
            else if (task.getLinkedProvision() != null) {
                final ProvisionRequest pr3 = task.getLinkedProvision();
                final ItemStack cs = pr3.getStack();
                final ItemStack s2 = golem.dropItem(cs);
                if (s2.getCount() < cs.getCount()) {
                    final ItemStack ps = cs.copy();
                    ps.setCount(cs.getCount() - s2.getCount());
                    if (task.getData() == 1) {
                        GolemHelper.requestProvisioning(world, pr3.getEntity(), ps);
                    }
                    else {
                        GolemHelper.requestProvisioning(world, pr3.getPos(), pr3.getSide(), ps);
                    }
                }
                if (task.getData() == 1) {
                    InventoryUtils.dropItemAtEntity(world, s2, pr3.getEntity());
                }
                else {
                    final ItemStack back = InventoryUtils.ejectStackAt(world, pr3.getPos().offset(pr3.getSide()), pr3.getSide().getOpposite(), s2, true);
                    if (!back.isEmpty()) {
                        golem.holdItem(back);
                    }
                }
                ((Entity)golem).playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.125f, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7f + 1.0f) * 1.0f);
                golem.swingArm();
                pr3.setInvalid(true);
            }
        }
        task.setSuspended(true);
        return true;
    }
    
    @Override
    public boolean canGolemPerformTask(final IGolemAPI golem, final Task task) {
        final ProvisionRequest pr = task.getLinkedProvision();
        final boolean b = pr != null && ((pr.getSeal() != null && ((EntityThaumcraftGolem)golem).isWithinHomeDistanceFromPosition(pr.getSeal().getSealPos().pos)) || (pr.getEntity() != null && ((EntityThaumcraftGolem)golem).isWithinHomeDistanceFromPosition(pr.getEntity().getPosition())) || (pr.getPos() != null && ((EntityThaumcraftGolem)golem).isWithinHomeDistanceFromPosition(pr.getPos())));
        if (task.getData() == 0) {
            return b && this.areGolemTagsValidForTask(pr.getSeal(), golem) && pr.getStack() != null && !golem.isCarrying(pr.getStack()) && golem.canCarry(pr.getStack(), true);
        }
        return b && this.areGolemTagsValidForTask(pr.getSeal(), golem) && pr.getStack() != null && golem.isCarrying(pr.getStack());
    }
    
    private boolean areGolemTagsValidForTask(final ISealEntity se, final IGolemAPI golem) {
        if (se == null) {
            return true;
        }
        if (se.isLocked() && !((IEntityOwnable)golem).getOwnerId().equals(se.getOwner())) {
            return false;
        }
        if (se.getSeal().getRequiredTags() != null && !golem.getProperties().getTraits().containsAll(Arrays.asList(se.getSeal().getRequiredTags()))) {
            return false;
        }
        if (se.getSeal().getForbiddenTags() != null) {
            for (final EnumGolemTrait tag : se.getSeal().getForbiddenTags()) {
                if (golem.getProperties().getTraits().contains(tag)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public void onTaskSuspension(final World world, final Task task) {
        if (task.getLinkedProvision() != null) {
            task.getLinkedProvision().setLinkedTask(null);
        }
        task.setLinkedProvision(null);
    }
    
    @Override
    public boolean canPlaceAt(final World world, final BlockPos pos, final EnumFacing side) {
        final IItemHandler inv = ThaumcraftInvHelper.getItemHandlerAt(world, pos, side);
        return inv != null;
    }
    
    @Override
    public ResourceLocation getSealIcon() {
        return this.icon;
    }
    
    @Override
    public int[] getGuiCategories() {
        return new int[] { 1, 3, 0, 4 };
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
    public void onRemoval(final World world, final BlockPos pos, final EnumFacing side) {
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
