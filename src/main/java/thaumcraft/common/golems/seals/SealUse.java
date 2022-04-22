// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.seals;

import thaumcraft.api.golems.EnumGolemTrait;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.golems.GolemHelper;
import net.minecraftforge.common.util.FakePlayer;
import thaumcraft.common.golems.GolemInteractionHelper;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.api.ThaumcraftInvHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.api.golems.seals.ISealEntity;
import net.minecraft.world.World;
import java.util.Random;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.seals.ISealConfigToggles;

public class SealUse extends SealFiltered implements ISealConfigToggles
{
    int delay;
    int watchedTask;
    ResourceLocation icon;
    protected SealToggle[] props;
    
    public SealUse() {
        delay = new Random(System.nanoTime()).nextInt(49);
        watchedTask = Integer.MIN_VALUE;
        icon = new ResourceLocation("thaumcraft", "items/seals/seal_use");
        props = new SealToggle[] { new SealToggle(true, "pmeta", "golem.prop.meta"), new SealToggle(true, "pnbt", "golem.prop.nbt"), new SealToggle(false, "pore", "golem.prop.ore"), new SealToggle(false, "pmod", "golem.prop.mod"), new SealToggle(false, "pleft", "golem.prop.left"), new SealToggle(false, "pempty", "golem.prop.empty"), new SealToggle(false, "pemptyhand", "golem.prop.emptyhand"), new SealToggle(false, "psneak", "golem.prop.sneak"), new SealToggle(false, "ppro", "golem.prop.provision.wl") };
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:use";
    }
    
    @Override
    public void tickSeal(final World world, final ISealEntity seal) {
        if (delay++ % 5 != 0) {
            return;
        }
        final Task oldTask = TaskHandler.getTask(world.provider.getDimension(), watchedTask);
        if (oldTask == null || oldTask.isSuspended() || oldTask.isCompleted()) {
            if (getToggles()[5].value != world.isAirBlock(seal.getSealPos().pos)) {
                return;
            }
            final Task task = new Task(seal.getSealPos(), seal.getSealPos().pos);
            task.setPriority(seal.getPriority());
            TaskHandler.addTask(world.provider.getDimension(), task);
            watchedTask = task.getId();
        }
    }
    
    @Override
    public void onTaskStarted(final World world, final IGolemAPI golem, final Task task) {
    }
    
    public boolean mayPlace(final World world, final Block blockIn, final BlockPos pos, final EnumFacing side) {
        final IBlockState block = world.getBlockState(pos);
        final AxisAlignedBB axisalignedbb = blockIn.getBoundingBox(blockIn.getDefaultState(), world, pos);
        return axisalignedbb == null || world.checkNoEntityCollision(axisalignedbb, null);
    }
    
    @Override
    public boolean onTaskCompletion(final World world, final IGolemAPI golem, final Task task) {
        if (getToggles()[5].value == world.isAirBlock(task.getPos())) {
            ItemStack clickStack = golem.getCarrying().get(0);
            if (!filter.get(0).isEmpty()) {
                clickStack = InventoryUtils.findFirstMatchFromFilter(filter, filterSize, blacklist, golem.getCarrying(), new ThaumcraftInvHelper.InvFilter(!props[0].value, !props[1].value, props[2].value, props[3].value));
            }
            if (!clickStack.isEmpty() || props[6].value) {
                ItemStack ss = ItemStack.EMPTY;
                if (!clickStack.isEmpty()) {
                    ss = clickStack.copy();
                    golem.dropItem(clickStack.copy());
                }
                GolemInteractionHelper.golemClick(world, golem, task.getPos(), task.getSealPos().face, props[6].value ? ItemStack.EMPTY : ss, props[7].value, !getToggles()[4].value);
            }
        }
        task.setSuspended(true);
        return true;
    }
    
    private void dropSomeItems(final FakePlayer fp2, final IGolemAPI golem) {
        for (int i = 0; i < fp2.inventory.mainInventory.size(); ++i) {
            if (!fp2.inventory.mainInventory.get(i).isEmpty()) {
                if (golem.canCarry(fp2.inventory.mainInventory.get(i), true)) {
                    fp2.inventory.mainInventory.set(i, golem.holdItem(fp2.inventory.mainInventory.get(i)));
                }
                if (!fp2.inventory.mainInventory.get(i).isEmpty() && fp2.inventory.mainInventory.get(i).getCount() > 0) {
                    InventoryUtils.dropItemAtEntity(golem.getGolemWorld(), fp2.inventory.mainInventory.get(i), golem.getGolemEntity());
                }
                fp2.inventory.mainInventory.set(i, ItemStack.EMPTY);
            }
        }
        for (int i = 0; i < fp2.inventory.armorInventory.size(); ++i) {
            if (!fp2.inventory.armorInventory.get(i).isEmpty()) {
                if (golem.canCarry(fp2.inventory.armorInventory.get(i), true)) {
                    fp2.inventory.armorInventory.set(i, golem.holdItem(fp2.inventory.armorInventory.get(i)));
                }
                if (!fp2.inventory.mainInventory.get(i).isEmpty() && fp2.inventory.armorInventory.get(i).getCount() > 0) {
                    InventoryUtils.dropItemAtEntity(golem.getGolemWorld(), fp2.inventory.armorInventory.get(i), golem.getGolemEntity());
                }
                fp2.inventory.armorInventory.set(i, ItemStack.EMPTY);
            }
        }
    }
    
    @Override
    public boolean canGolemPerformTask(final IGolemAPI golem, final Task task) {
        if (!props[6].value) {
            final boolean found = !InventoryUtils.findFirstMatchFromFilter(filter, filterSize, blacklist, golem.getCarrying(), new ThaumcraftInvHelper.InvFilter(!props[0].value, !props[1].value, props[2].value, props[3].value)).isEmpty();
            if (!found && getToggles()[8].value && !blacklist && getInv().get(0) != null) {
                final ISealEntity se = SealHandler.getSealEntity(golem.getGolemWorld().provider.getDimension(), task.getSealPos());
                if (se != null) {
                    final ItemStack stack = getInv().get(0).copy();
                    if (!props[0].value) {
                        stack.setItemDamage(32767);
                    }
                    GolemHelper.requestProvisioning(golem.getGolemWorld(), se, stack);
                }
            }
            return found;
        }
        return true;
    }
    
    @Override
    public void onTaskSuspension(final World world, final Task task) {
    }
    
    @Override
    public boolean canPlaceAt(final World world, final BlockPos pos, final EnumFacing side) {
        return true;
    }
    
    @Override
    public ResourceLocation getSealIcon() {
        return icon;
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
        return new int[] { 1, 3, 0, 4 };
    }
    
    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.DEFT, EnumGolemTrait.SMART };
    }
    
    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return null;
    }
    
    @Override
    public SealToggle[] getToggles() {
        return props;
    }
    
    @Override
    public void setToggle(final int indx, final boolean value) {
        props[indx].setValue(value);
    }
}
