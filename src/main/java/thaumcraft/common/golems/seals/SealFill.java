package thaumcraft.common.golems.seals;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.utils.InventoryUtils;


public class SealFill extends SealFiltered
{
    int delay;
    int watchedTask;
    protected ISealConfigToggles.SealToggle[] props;
    ResourceLocation icon;
    
    public SealFill() {
        delay = new Random(System.nanoTime()).nextInt(50);
        watchedTask = Integer.MIN_VALUE;
        props = new ISealConfigToggles.SealToggle[] { new ISealConfigToggles.SealToggle(true, "pmeta", "golem.prop.meta"), new ISealConfigToggles.SealToggle(true, "pnbt", "golem.prop.nbt"), new ISealConfigToggles.SealToggle(false, "pore", "golem.prop.ore"), new ISealConfigToggles.SealToggle(false, "pmod", "golem.prop.mod"), new ISealConfigToggles.SealToggle(false, "pexist", "golem.prop.exist") };
        icon = new ResourceLocation("thaumcraft", "items/seals/seal_fill");
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:fill";
    }
    
    @Override
    public void tickSeal(World world, ISealEntity seal) {
        if (delay++ % 20 != 0) {
            return;
        }
        Task oldTask = TaskHandler.getTask(world.provider.getDimension(), watchedTask);
        if (oldTask == null || oldTask.isReserved() || oldTask.isSuspended() || oldTask.isCompleted()) {
            Task task = new Task(seal.getSealPos(), seal.getSealPos().pos);
            task.setPriority(seal.getPriority());
            TaskHandler.addTask(world.provider.getDimension(), task);
            watchedTask = task.getId();
        }
    }
    
    @Override
    public void onTaskStarted(World world, IGolemAPI golem, Task task) {
        ISealEntity se = SealHandler.getSealEntity(world.provider.getDimension(), task.getSealPos());
        if (se != null && !se.isStoppedByRedstone(world)) {
            Task newTask = new Task(task.getSealPos(), task.getSealPos().pos);
            newTask.setPriority(se.getPriority());
            TaskHandler.addTask(world.provider.getDimension(), newTask);
            watchedTask = newTask.getId();
        }
    }
    
    @Override
    public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
        ThaumcraftInvHelper.InvFilter filter = new ThaumcraftInvHelper.InvFilter(!props[0].value, !props[1].value, props[2].value, props[3].value);
        Tuple<ItemStack, Integer> tuple = InventoryUtils.findFirstMatchFromFilterTuple(getInv(), getSizes(), isBlacklist(), golem.getCarrying(), filter);
        if (tuple.getFirst() != null && !tuple.getFirst().isEmpty()) {
            IItemHandler inv = ThaumcraftInvHelper.getItemHandlerAt(world, task.getSealPos().pos, task.getSealPos().face);
            int limit = tuple.getFirst().getCount();
            if (hasStacksizeLimiters() && tuple.getSecond() != null && tuple.getSecond() > 0) {
                int c = (inv == null) ? InventoryUtils.countStackInWorld(golem.getGolemWorld(), task.getSealPos().pos, tuple.getFirst(), 1.5, filter) : ThaumcraftInvHelper.countTotalItemsIn(inv, tuple.getFirst(), filter);
                if (c < tuple.getSecond()) {
                    limit = tuple.getSecond() - c;
                }
                else {
                    limit = 0;
                }
            }
            if (limit > 0) {
                ItemStack t = tuple.getFirst().copy();
                t.setCount(limit);
                ItemStack s = golem.dropItem(t);
                if (inv == null) {
                    EntityItem entityItem;
                    EntityItem ie = entityItem = new EntityItem(world, task.getSealPos().pos.getX() + 0.5 + task.getSealPos().face.getFrontOffsetX(), task.getSealPos().pos.getY() + 0.5 + task.getSealPos().face.getFrontOffsetY(), task.getSealPos().pos.getZ() + 0.5 + task.getSealPos().face.getFrontOffsetZ(), s);
                    entityItem.motionX /= 5.0;
                    EntityItem entityItem2 = ie;
                    entityItem2.motionY /= 2.0;
                    EntityItem entityItem3 = ie;
                    entityItem3.motionZ /= 5.0;
                    world.spawnEntity(ie);
                }
                else {
                    golem.holdItem(ItemHandlerHelper.insertItemStacked(inv, s, false));
                }
                ((Entity)golem).playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.125f, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7f + 1.0f) * 1.0f);
                golem.addRankXp(1);
                golem.swingArm();
            }
        }
        task.setSuspended(true);
        return true;
    }
    
    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        ThaumcraftInvHelper.InvFilter filter = new ThaumcraftInvHelper.InvFilter(!props[0].value, !props[1].value, props[2].value, props[3].value);
        Tuple<ItemStack, Integer> tuple = InventoryUtils.findFirstMatchFromFilterTuple(getInv(), getSizes(), isBlacklist(), golem.getCarrying(), filter);
        if (tuple.getFirst() != null && !tuple.getFirst().isEmpty()) {
            IItemHandler inv = ThaumcraftInvHelper.getItemHandlerAt(golem.getGolemWorld(), task.getSealPos().pos, task.getSealPos().face);
            if (inv != null) {
                if (tuple.getFirst() != null && !tuple.getFirst().isEmpty() && props[4].value && ThaumcraftInvHelper.countTotalItemsIn(inv, tuple.getFirst(), filter) <= 0) {
                    return false;
                }
                if (tuple.getFirst() != null && !tuple.getFirst().isEmpty() && ThaumcraftInvHelper.hasRoomForSome(golem.getGolemWorld(), task.getSealPos().pos, task.getSealPos().face, tuple.getFirst())) {
                    if (!hasStacksizeLimiters() || tuple.getSecond() == null || tuple.getSecond() <= 0) {
                        return true;
                    }
                    if (ThaumcraftInvHelper.countTotalItemsIn(inv, tuple.getFirst(), filter) < tuple.getSecond()) {
                        return true;
                    }
                }
            }
            else if (tuple.getFirst() != null && !tuple.getFirst().isEmpty()) {
                return !hasStacksizeLimiters() || tuple.getSecond() == null || tuple.getSecond() <= 0 || InventoryUtils.countStackInWorld(golem.getGolemWorld(), task.getSealPos().pos, tuple.getFirst(), 1.5, filter) < tuple.getSecond();
            }
        }
        return false;
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
    public void onTaskSuspension(World world, Task task) {
    }
    
    @Override
    public void onRemoval(World world, BlockPos pos, EnumFacing side) {
    }
    
    @Override
    public boolean hasStacksizeLimiters() {
        return !isBlacklist();
    }
}
