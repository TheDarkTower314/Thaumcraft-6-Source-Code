// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraft.block.state.IBlockState;
import thaumcraft.common.lib.utils.BlockUtils;
import net.minecraft.init.Blocks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import thaumcraft.common.items.consumables.ItemBathSalts;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import thaumcraft.common.tiles.TileThaumcraftInventory;

public class TileSpa extends TileThaumcraftInventory implements IFluidHandler
{
    private boolean mix;
    private int counter;
    public FluidTank tank;
    
    public TileSpa() {
        super(1);
        mix = true;
        counter = 0;
        tank = new FluidTank(5000);
    }
    
    public void toggleMix() {
        mix = !mix;
        syncTile(false);
        markDirty();
    }
    
    public boolean getMix() {
        return mix;
    }
    
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        mix = nbttagcompound.getBoolean("mix");
        tank.readFromNBT(nbttagcompound);
    }
    
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("mix", mix);
        tank.writeToNBT(nbttagcompound);
        return nbttagcompound;
    }
    
    @Override
    public boolean isItemValidForSlot(final int par1, final ItemStack stack) {
        return stack.getItem() instanceof ItemBathSalts;
    }
    
    @Override
    public String getName() {
        return "thaumcraft.spa";
    }
    
    @Override
    public boolean hasCustomName() {
        return false;
    }
    
    @Override
    public ITextComponent getDisplayName() {
        return null;
    }
    
    @Override
    public int[] getSlotsForFace(final EnumFacing side) {
        return (side != EnumFacing.UP) ? new int[] { 0 } : new int[0];
    }
    
    @Override
    public boolean canInsertItem(final int index, final ItemStack itemStackIn, final EnumFacing side) {
        return side != EnumFacing.UP;
    }
    
    @Override
    public boolean canExtractItem(final int index, final ItemStack stack, final EnumFacing side) {
        return side != EnumFacing.UP;
    }
    
    @Override
    public void update() {
        super.update();
        Label_0267: {
            if (!world.isRemote && counter++ % 40 == 0 && !world.isBlockPowered(pos) && hasIngredients()) {
                final Block b = world.getBlockState(pos.up()).getBlock();
                final int m = b.getMetaFromState(world.getBlockState(pos.up()));
                Block tb = null;
                if (mix) {
                    tb = BlocksTC.purifyingFluid;
                }
                else {
                    tb = tank.getFluid().getFluid().getBlock();
                }
                if (b == tb && m == 0) {
                    for (int xx = -2; xx <= 2; ++xx) {
                        for (int zz = -2; zz <= 2; ++zz) {
                            final BlockPos p = getPos().add(xx, 1, zz);
                            if (isValidLocation(p, true, tb)) {
                                consumeIngredients();
                                world.setBlockState(p, tb.getDefaultState());
                                checkQuanta(p);
                                break Label_0267;
                            }
                        }
                    }
                }
                else if (isValidLocation(pos.up(), false, tb)) {
                    consumeIngredients();
                    world.setBlockState(pos.up(), tb.getDefaultState());
                    checkQuanta(pos.up());
                }
            }
        }
    }
    
    private void checkQuanta(final BlockPos pos) {
        final Block b = world.getBlockState(pos).getBlock();
        if (b instanceof BlockFluidBase) {
            final float p = ((BlockFluidBase)b).getQuantaPercentage(world, pos);
            if (p < 1.0f) {
                final int md = (int)(1.0f / p) - 1;
                if (md >= 0 && md < 16) {
                    world.setBlockState(pos, b.getStateFromMeta(md));
                }
            }
        }
    }
    
    private boolean hasIngredients() {
        if (mix) {
            if (tank.getInfo().fluid == null || !tank.getInfo().fluid.containsFluid(new FluidStack(FluidRegistry.WATER, 1000))) {
                return false;
            }
            if (!(getStackInSlot(0).getItem() instanceof ItemBathSalts)) {
                return false;
            }
        }
        else if (tank.getInfo().fluid == null || !tank.getFluid().getFluid().canBePlacedInWorld() || tank.getFluidAmount() < 1000) {
            return false;
        }
        return true;
    }
    
    private void consumeIngredients() {
        if (mix) {
            decrStackSize(0, 1);
        }
        drain(1000, true);
    }
    
    private boolean isValidLocation(final BlockPos pos, final boolean mustBeAdjacent, final Block target) {
        if ((target == Blocks.WATER || target == Blocks.FLOWING_WATER) && world.provider.doesWaterVaporize()) {
            return false;
        }
        final Block b = world.getBlockState(pos).getBlock();
        final IBlockState bb = world.getBlockState(pos.down());
        final int m = b.getMetaFromState(world.getBlockState(pos));
        return bb.isSideSolid(world, pos.down(), EnumFacing.UP) && b.isReplaceable(world, pos) && (b != target || m != 0) && (!mustBeAdjacent || BlockUtils.isBlockTouching(world, pos, target.getStateFromMeta(0)));
    }
    
    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
    
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) tank;
        }
        return super.getCapability(capability, facing);
    }
    
    public IFluidTankProperties[] getTankProperties() {
        return tank.getTankProperties();
    }
    
    public int fill(final FluidStack resource, final boolean doFill) {
        markDirty();
        syncTile(false);
        return tank.fill(resource, doFill);
    }
    
    public FluidStack drain(final FluidStack resource, final boolean doDrain) {
        final FluidStack fs = tank.drain(resource, doDrain);
        markDirty();
        syncTile(false);
        return fs;
    }
    
    public FluidStack drain(final int maxDrain, final boolean doDrain) {
        final FluidStack fs = tank.drain(maxDrain, doDrain);
        markDirty();
        syncTile(false);
        return fs;
    }
}
