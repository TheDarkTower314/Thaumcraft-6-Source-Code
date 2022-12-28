package thaumcraft.common.tiles.devices;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraft;
import thaumcraft.common.world.aura.AuraHandler;


public class TileVisGenerator extends TileThaumcraft implements ITickable, IEnergyStorage
{
    protected int energy;
    protected int capacity = 1000;
    protected int maxExtract = 20;
    
    public void update() {
        if (!world.isRemote && BlockStateUtils.isEnabled(getBlockMetadata())) {
            recharge();
            EnumFacing face = BlockStateUtils.getFacing(getBlockMetadata());
            IBlockState state = getWorld().getBlockState(getPos().offset(face));
            Block block = state.getBlock();
            if (block.hasTileEntity(state)) {
                TileEntity tileentity = getWorld().getTileEntity(getPos().offset(face));
                if (tileentity != null && tileentity.hasCapability(CapabilityEnergy.ENERGY, face.getOpposite())) {
                    IEnergyStorage capability = tileentity.getCapability(CapabilityEnergy.ENERGY, face.getOpposite());
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
            float vis = AuraHandler.drainVis(getWorld(), getPos(), 1.0f, false);
            energy = (int)(vis * 1000.0f);
            markDirty();
            syncTile(false);
        }
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbt) {
        energy = nbt.getInteger("energy");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
        nbt.setInteger("energy", energy);
        return nbt;
    }
    
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        EnumFacing face = BlockStateUtils.getFacing(getBlockMetadata());
        return (face == facing && capability == CapabilityEnergy.ENERGY) || super.hasCapability(capability, facing);
    }
    
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        EnumFacing face = BlockStateUtils.getFacing(getBlockMetadata());
        if (face == facing && capability == CapabilityEnergy.ENERGY) {
            return (T)this;
        }
        return (T)super.getCapability((Capability)capability, facing);
    }
    
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }
    
    public int extractEnergy(int maxExtract, boolean simulate) {
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
