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
        this.mix = true;
        this.counter = 0;
        this.tank = new FluidTank(5000);
    }
    
    public void toggleMix() {
        this.mix = !this.mix;
        this.syncTile(false);
        this.markDirty();
    }
    
    public boolean getMix() {
        return this.mix;
    }
    
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        this.mix = nbttagcompound.getBoolean("mix");
        this.tank.readFromNBT(nbttagcompound);
    }
    
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("mix", this.mix);
        this.tank.writeToNBT(nbttagcompound);
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
            if (!this.world.isRemote && this.counter++ % 40 == 0 && !this.world.isBlockPowered(this.pos) && this.hasIngredients()) {
                final Block b = this.world.getBlockState(this.pos.up()).getBlock();
                final int m = b.getMetaFromState(this.world.getBlockState(this.pos.up()));
                Block tb = null;
                if (this.mix) {
                    tb = BlocksTC.purifyingFluid;
                }
                else {
                    tb = this.tank.getFluid().getFluid().getBlock();
                }
                if (b == tb && m == 0) {
                    for (int xx = -2; xx <= 2; ++xx) {
                        for (int zz = -2; zz <= 2; ++zz) {
                            final BlockPos p = this.getPos().add(xx, 1, zz);
                            if (this.isValidLocation(p, true, tb)) {
                                this.consumeIngredients();
                                this.world.setBlockState(p, tb.getDefaultState());
                                this.checkQuanta(p);
                                break Label_0267;
                            }
                        }
                    }
                }
                else if (this.isValidLocation(this.pos.up(), false, tb)) {
                    this.consumeIngredients();
                    this.world.setBlockState(this.pos.up(), tb.getDefaultState());
                    this.checkQuanta(this.pos.up());
                }
            }
        }
    }
    
    private void checkQuanta(final BlockPos pos) {
        final Block b = this.world.getBlockState(pos).getBlock();
        if (b instanceof BlockFluidBase) {
            final float p = ((BlockFluidBase)b).getQuantaPercentage(this.world, pos);
            if (p < 1.0f) {
                final int md = (int)(1.0f / p) - 1;
                if (md >= 0 && md < 16) {
                    this.world.setBlockState(pos, b.getStateFromMeta(md));
                }
            }
        }
    }
    
    private boolean hasIngredients() {
        if (this.mix) {
            if (this.tank.getInfo().fluid == null || !this.tank.getInfo().fluid.containsFluid(new FluidStack(FluidRegistry.WATER, 1000))) {
                return false;
            }
            if (!(this.getStackInSlot(0).getItem() instanceof ItemBathSalts)) {
                return false;
            }
        }
        else if (this.tank.getInfo().fluid == null || !this.tank.getFluid().getFluid().canBePlacedInWorld() || this.tank.getFluidAmount() < 1000) {
            return false;
        }
        return true;
    }
    
    private void consumeIngredients() {
        if (this.mix) {
            this.decrStackSize(0, 1);
        }
        this.drain(1000, true);
    }
    
    private boolean isValidLocation(final BlockPos pos, final boolean mustBeAdjacent, final Block target) {
        if ((target == Blocks.WATER || target == Blocks.FLOWING_WATER) && this.world.provider.doesWaterVaporize()) {
            return false;
        }
        final Block b = this.world.getBlockState(pos).getBlock();
        final IBlockState bb = this.world.getBlockState(pos.down());
        final int m = b.getMetaFromState(this.world.getBlockState(pos));
        return bb.isSideSolid(this.world, pos.down(), EnumFacing.UP) && b.isReplaceable(this.world, pos) && (b != target || m != 0) && (!mustBeAdjacent || BlockUtils.isBlockTouching(this.world, pos, target.getStateFromMeta(0)));
    }
    
    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
    
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T)this.tank;
        }
        return super.getCapability(capability, facing);
    }
    
    public IFluidTankProperties[] getTankProperties() {
        return this.tank.getTankProperties();
    }
    
    public int fill(final FluidStack resource, final boolean doFill) {
        this.markDirty();
        this.syncTile(false);
        return this.tank.fill(resource, doFill);
    }
    
    public FluidStack drain(final FluidStack resource, final boolean doDrain) {
        final FluidStack fs = this.tank.drain(resource, doDrain);
        this.markDirty();
        this.syncTile(false);
        return fs;
    }
    
    public FluidStack drain(final int maxDrain, final boolean doDrain) {
        final FluidStack fs = this.tank.drain(maxDrain, doDrain);
        this.markDirty();
        this.syncTile(false);
        return fs;
    }
}
