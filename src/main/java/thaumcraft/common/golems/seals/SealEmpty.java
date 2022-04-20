// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.seals;

import thaumcraft.api.golems.EnumGolemTrait;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.Entity;
import thaumcraft.api.golems.IGolemAPI;
import java.util.Iterator;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.api.golems.seals.ISealEntity;
import net.minecraft.world.World;
import java.util.Random;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import java.util.HashMap;

public class SealEmpty extends SealFiltered
{
    int delay;
    int filterInc;
    HashMap<Integer, ItemStack> cache;
    ResourceLocation icon;
    protected ISealConfigToggles.SealToggle[] props;
    
    public SealEmpty() {
        this.delay = new Random(System.nanoTime()).nextInt(30);
        this.filterInc = 0;
        this.cache = new HashMap<Integer, ItemStack>();
        this.icon = new ResourceLocation("thaumcraft", "items/seals/seal_empty");
        this.props = new ISealConfigToggles.SealToggle[] { new ISealConfigToggles.SealToggle(true, "pmeta", "golem.prop.meta"), new ISealConfigToggles.SealToggle(true, "pnbt", "golem.prop.nbt"), new ISealConfigToggles.SealToggle(false, "pore", "golem.prop.ore"), new ISealConfigToggles.SealToggle(false, "pmod", "golem.prop.mod"), new ISealConfigToggles.SealToggle(false, "pcycle", "golem.prop.cycle"), new ISealConfigToggles.SealToggle(false, "pleave", "golem.prop.leave") };
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:empty";
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
        if (this.delay++ % 20 != 0) {
            return;
        }
        final ItemStack stack = InventoryUtils.findFirstMatchFromFilter(this.getInv(this.filterInc), this.isBlacklist(), ThaumcraftInvHelper.getItemHandlerAt(world, seal.getSealPos().pos, seal.getSealPos().face), seal.getSealPos().face, new ThaumcraftInvHelper.InvFilter(!this.props[0].value, !this.props[1].value, this.props[2].value, this.props[3].value), this.props[5].value);
        if (stack != null && !stack.isEmpty()) {
            final Task task = new Task(seal.getSealPos(), seal.getSealPos().pos);
            task.setPriority(seal.getPriority());
            task.setLifespan((short)5);
            TaskHandler.addTask(world.provider.getDimension(), task);
            this.cache.put(task.getId(), stack);
        }
    }
    
    @Override
    public boolean onTaskCompletion(final World world, final IGolemAPI golem, final Task task) {
        ItemStack stack = this.cache.get(task.getId());
        final int sa = ThaumcraftInvHelper.countTotalItemsIn(world, task.getSealPos().pos, task.getSealPos().face, stack, new ThaumcraftInvHelper.InvFilter(!this.props[0].value, !this.props[1].value, this.props[2].value, this.props[3].value));
        if (stack != null && !stack.isEmpty() && this.props[5].value && sa <= stack.getCount()) {
            stack = stack.copy();
            stack.setCount(sa - 1);
        }
        if (stack != null && !stack.isEmpty()) {
            final int limit = golem.canCarryAmount(stack);
            if (limit > 0) {
                final ItemStack s = golem.holdItem(InventoryUtils.removeStackFrom(world, task.getSealPos().pos, task.getSealPos().face, InventoryUtils.copyLimitedStack(stack, limit), new ThaumcraftInvHelper.InvFilter(!this.props[0].value, !this.props[1].value, this.props[2].value, this.props[3].value), false));
                if (!s.isEmpty()) {
                    InventoryUtils.ejectStackAt(world, task.getSealPos().pos.offset(task.getSealPos().face), task.getSealPos().face.getOpposite(), s);
                }
                ((Entity)golem).playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.125f, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
                golem.swingArm();
            }
        }
        this.cache.remove(task.getId());
        ++this.filterInc;
        task.setSuspended(true);
        return true;
    }
    
    @Override
    public boolean canGolemPerformTask(final IGolemAPI golem, final Task task) {
        final ItemStack stack = this.cache.get(task.getId());
        return stack != null && !stack.isEmpty() && golem.canCarry(stack, true);
    }
    
    @Override
    public boolean canPlaceAt(final World world, final BlockPos pos, final EnumFacing side) {
        final IItemHandler inv = ThaumcraftInvHelper.getItemHandlerAt(world, pos, side);
        return inv != null;
    }
    
    public NonNullList<ItemStack> getInv(final int c) {
        return super.getInv();
    }
    
    @Override
    public ResourceLocation getSealIcon() {
        return this.icon;
    }
    
    @Override
    public int[] getGuiCategories() {
        return new int[] { 1, 0, 4 };
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
        this.cache.remove(task.getId());
    }
    
    @Override
    public void onRemoval(final World world, final BlockPos pos, final EnumFacing side) {
    }
}
