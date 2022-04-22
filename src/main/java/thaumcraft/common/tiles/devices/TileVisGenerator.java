// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.common.world.aura.AuraHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraft.util.ITickable;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileVisGenerator extends TileThaumcraft implements ITickable, IEnergyStorage
{
    protected int energy;
    protected final int capacity = 1000;
    protected final int maxExtract = 20;
    
    public void update() {
        if (!world.isRemote && BlockStateUtils.isEnabled(getBlockMetadata())) {
            recharge();
            final EnumFacing face = BlockStateUtils.getFacing(getBlockMetadata());
            final IBlockState state = getWorld().getBlockState(getPos().offset(face));
            final Block block = state.getBlock();
            if (block.hasTileEntity(state)) {
                final TileEntity tileentity = getWorld().getTileEntity(getPos().offset(face));
                if (tileentity != null && tileentity.hasCapability(CapabilityEnergy.ENERGY, face.getOpposite())) {
                    final IEnergyStorage capability = tileentity.getCapability(CapabilityEnergy.ENERGY, face.getOpposite());
                    if (capability.canReceive()) {
                        int energyExtracted = Math.min(energy, 20);
                        energyExtracted = capability.receiveEnergy(energyExtracted, false);
                        if (energyExtracted > 0) {
                            energy -= energyExtracted;
                            markDirty();
                            if (energy == 0) {
                                syncTile(false);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void recharge() {
        if (energy == 0) {
            final float vis = AuraHandler.drainVis(getWorld(), getPos(), 1.0f, false);
            energy = (int)(vis * 1000.0f);
            markDirty();
            syncTile(false);
        }
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbt) {
        energy = nbt.getInteger("energy");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbt) {
        nbt.setInteger("energy", energy);
        return nbt;
    }
    
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        final EnumFacing face = BlockStateUtils.getFacing(getBlockMetadata());
        return (face == facing && capability == CapabilityEnergy.ENERGY) || super.hasCapability(capability, facing);
    }
    
    @Nullable
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        final EnumFacing face = BlockStateUtils.getFacing(getBlockMetadata());
        if (face == facing && capability == CapabilityEnergy.ENERGY) {
            return (T)this;
        }
        return (T)super.getCapability((Capability)capability, facing);
    }
    
    public int receiveEnergy(final int maxReceive, final boolean simulate) {
        return 0;
    }
    
    public int extractEnergy(final int maxExtract, final boolean simulate) {
        return 0;
    }
    
    public int getEnergyStored() {
        return energy;
    }
    
    public int getMaxEnergyStored() {
        return 1000;
    }
    
    public boolean canExtract() {
        return true;
    }
    
    public boolean canReceive() {
        return false;
    }
}
