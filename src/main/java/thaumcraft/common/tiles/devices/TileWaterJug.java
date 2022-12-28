package thaumcraft.common.tiles.devices;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.tiles.TileThaumcraft;
import vazkii.botania.api.item.IPetalApothecary;


public class TileWaterJug extends TileThaumcraft implements ITickable, IFluidHandler
{
    int zone;
    int counter;
    ArrayList<Integer> handlers;
    int zc;
    int tcount;
    public FluidTank tank;
    
    public TileWaterJug() {
        zone = 0;
        counter = 0;
        handlers = new ArrayList<Integer>();
        zc = 0;
        tcount = 0;
        tank = new FluidTank(new FluidStack(FluidRegistry.WATER, 0), 1000);
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbttagcompound) {
        tank.readFromNBT(nbttagcompound);
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
        tank.writeToNBT(nbttagcompound);
        return nbttagcompound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList nbttaglist = nbt.getTagList("handlers", 3);
        handlers = new ArrayList<Integer>();
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagInt tag = (NBTTagInt)nbttaglist.get(i);
            handlers.add(tag.getInt());
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < handlers.size(); ++i) {
            NBTTagInt nbtTagInt = new NBTTagInt(handlers.get(i));
        }
        nbt.setTag("handlers", nbttaglist);
        return nbt;
    }
    
    public void update() {
        ++counter;
        if (world.isRemote) {
            if (tcount > 0) {
                if (tcount % 5 == 0) {
                    int x = zc / 5 % 5;
                    int y = zc / 5 / 5 % 3;
                    int z = zc % 5;
                    FXDispatcher.INSTANCE.waterTrailFx(getPos(), getPos().add(x - 2, y - 1, z - 2), counter, 2650102, 0.1f);
                }
                --tcount;
            }
        }
        else if (counter % 5 == 0) {
            ++zone;
            int x = zone / 5 % 5;
            int y = zone / 5 / 5 % 3;
            int z = zone % 5;
            IBlockState bs = world.getBlockState(getPos().add(x - 2, y - 1, z - 2));
            TileEntity te = world.getTileEntity(getPos().add(x - 2, y - 1, z - 2));
            if (((te != null && (te instanceof IFluidHandler || te instanceof IPetalApothecary || te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP))) || bs.getBlock() == Blocks.CAULDRON) && !handlers.contains(zone)) {
                handlers.add(zone);
                markDirty();
            }
            int i = 0;
            while (i < handlers.size() && tank.getFluidAmount() >= 25) {
                int zz = handlers.get(i);
                x = zz / 5 % 5;
                y = zz / 5 / 5 % 3;
                z = zz % 5;
                IBlockState bs2 = world.getBlockState(getPos().add(x - 2, y - 1, z - 2));
                TileEntity tile = world.getTileEntity(getPos().add(x - 2, y - 1, z - 2));
                if (tile != null && (tile instanceof IFluidHandler || tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP))) {
                    IFluidHandler fh = null;
                    if (tile instanceof IFluidHandler) {
                        fh = (IFluidHandler)tile;
                    }
                    else {
                        fh = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);
                    }
                    if (fh != null) {
                        int q = fh.fill(new FluidStack(FluidRegistry.WATER, 25), true);
                        if (q > 0) {
                            drain(new FluidStack(FluidRegistry.WATER, q), true);
                            markDirty();
                            world.addBlockEvent(getPos(), getBlockType(), 1, zz);
                            break;
                        }
                    }
                }
                else if (tile != null && tile instanceof IPetalApothecary && tank.getFluidAmount() >= 1000) {
                    IPetalApothecary pa = (IPetalApothecary)tile;
                    if (!pa.hasWater()) {
                        pa.setWater(true);
                        world.addBlockEvent(getPos(), getBlockType(), 1, zz);
                        drain(new FluidStack(FluidRegistry.WATER, 1000), true);
                    }
                }
                else {
                    if (bs2.getBlock() != Blocks.CAULDRON || tank.getFluidAmount() < 333) {
                        handlers.remove(i);
                        markDirty();
                        continue;
                    }
                    if ((int)bs2.getValue((IProperty)BlockCauldron.LEVEL) < 3) {
                        BlockPos pp = getPos().add(x - 2, y - 1, z - 2);
                        world.setBlockState(pp, bs2.cycleProperty((IProperty)BlockCauldron.LEVEL), 2);
                        world.updateComparatorOutputLevel(pp, bs2.getBlock());
                        world.addBlockEvent(getPos(), getBlockType(), 1, zz);
                        drain(new FluidStack(FluidRegistry.WATER, 333), true);
                    }
                }
                ++i;
            }
            if (tank.getFluidAmount() < 1000) {
                float da = (1000 - tank.getFluidAmount()) / 1000.0f;
                if (da > 0.1f) {
                    da = 0.1f;
                }
                float dv = AuraHelper.drainVis(getWorld(), getPos(), da, false);
                int wa = (int)(1000.0f * dv);
                if (wa > 0) {
                    tank.fill(new FluidStack(FluidRegistry.WATER, wa), true);
                    markDirty();
                    if (tank.getFluidAmount() >= tank.getCapacity()) {
                        syncTile(false);
                    }
                }
            }
        }
    }
    
    public boolean receiveClientEvent(int i, int j) {
        if (i == 1) {
            if (world.isRemote) {
                zc = j;
                tcount = 5;
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
    
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return (facing == EnumFacing.UP && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) || super.hasCapability(capability, facing);
    }
    
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (facing == EnumFacing.UP && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) tank;
        }
        return (T)super.getCapability((Capability)capability, facing);
    }
    
    public IFluidTankProperties[] getTankProperties() {
        return tank.getTankProperties();
    }
    
    public int fill(FluidStack resource, boolean doFill) {
        return 0;
    }
    
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        boolean f = tank.getFluidAmount() >= tank.getCapacity();
        FluidStack fs = tank.drain(resource, doDrain);
        markDirty();
        if (f && tank.getFluidAmount() < tank.getCapacity()) {
            syncTile(false);
        }
        return fs;
    }
    
    public FluidStack drain(int maxDrain, boolean doDrain) {
        boolean f = tank.getFluidAmount() >= tank.getCapacity();
        FluidStack fs = tank.drain(maxDrain, doDrain);
        markDirty();
        if (f && tank.getFluidAmount() < tank.getCapacity()) {
            syncTile(false);
        }
        return fs;
    }
}
