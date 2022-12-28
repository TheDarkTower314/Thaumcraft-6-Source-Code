package thaumcraft.common.tiles.essentia;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileAlembic extends TileThaumcraft implements IAspectContainer, IEssentiaTransport
{
    public Aspect aspect;
    public Aspect aspectFilter;
    public int amount;
    public int maxAmount;
    public int facing;
    public boolean aboveFurnace;
    EnumFacing fd;
    
    public TileAlembic() {
        aspectFilter = null;
        amount = 0;
        maxAmount = 128;
        facing = EnumFacing.DOWN.ordinal();
        aboveFurnace = false;
        fd = null;
    }
    
    @Override
    public AspectList getAspects() {
        return (aspect != null) ? new AspectList().add(aspect, amount) : new AspectList();
    }
    
    @Override
    public void setAspects(AspectList aspects) {
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().getX() - 0.1, getPos().getY() - 0.1, getPos().getZ() - 0.1, getPos().getX() + 1.1, getPos().getY() + 1.1, getPos().getZ() + 1.1);
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbttagcompound) {
        facing = nbttagcompound.getByte("facing");
        aspectFilter = Aspect.getAspect(nbttagcompound.getString("AspectFilter"));
        String tag = nbttagcompound.getString("aspect");
        if (tag != null) {
            aspect = Aspect.getAspect(tag);
        }
        amount = nbttagcompound.getShort("amount");
        fd = EnumFacing.VALUES[facing];
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
        if (aspect != null) {
            nbttagcompound.setString("aspect", aspect.getTag());
        }
        if (aspectFilter != null) {
            nbttagcompound.setString("AspectFilter", aspectFilter.getTag());
        }
        nbttagcompound.setShort("amount", (short) amount);
        nbttagcompound.setByte("facing", (byte) facing);
        return nbttagcompound;
    }
    
    @Override
    public int addToContainer(Aspect tt, int am) {
        if (aspectFilter != null && tt != aspectFilter) {
            return am;
        }
        if ((amount < maxAmount && tt == aspect) || amount == 0) {
            aspect = tt;
            int added = Math.min(am, maxAmount - amount);
            amount += added;
            am -= added;
        }
        markDirty();
        syncTile(false);
        return am;
    }
    
    @Override
    public boolean takeFromContainer(Aspect tt, int am) {
        if (amount == 0 || aspect == null) {
            aspect = null;
            amount = 0;
        }
        if (aspect != null && amount >= am && tt == aspect) {
            amount -= am;
            if (amount <= 0) {
                aspect = null;
                amount = 0;
            }
            markDirty();
            syncTile(false);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean doesContainerContain(AspectList ot) {
        return amount > 0 && aspect != null && ot.getAmount(aspect) > 0;
    }
    
    @Override
    public boolean doesContainerContainAmount(Aspect tt, int am) {
        return amount >= am && tt == aspect;
    }
    
    @Override
    public int containerContains(Aspect tt) {
        return (tt == aspect) ? amount : 0;
    }
    
    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }
    
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }
    
    @Override
    public boolean isConnectable(EnumFacing face) {
        return face != EnumFacing.VALUES[facing] && face != EnumFacing.DOWN;
    }
    
    @Override
    public boolean canInputFrom(EnumFacing face) {
        return false;
    }
    
    @Override
    public boolean canOutputTo(EnumFacing face) {
        return face != EnumFacing.VALUES[facing] && face != EnumFacing.DOWN;
    }
    
    @Override
    public void setSuction(Aspect aspect, int amount) {
    }
    
    @Override
    public Aspect getSuctionType(EnumFacing loc) {
        return null;
    }
    
    @Override
    public int getSuctionAmount(EnumFacing loc) {
        return 0;
    }
    
    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        return aspect;
    }
    
    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return amount;
    }
    
    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        return (canOutputTo(face) && takeFromContainer(aspect, amount)) ? amount : 0;
    }
    
    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing loc) {
        return 0;
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    protected static boolean processAlembics(World world, BlockPos pos, Aspect aspect) {
        int deep = 1;
        while (true) {
            TileEntity te = world.getTileEntity(pos.up(deep));
            if (te != null && te instanceof TileAlembic) {
                TileAlembic alembic = (TileAlembic)te;
                if (alembic.amount > 0 && alembic.aspect == aspect && alembic.addToContainer(aspect, 1) == 0) {
                    return true;
                }
                ++deep;
            }
            else {
                deep = 1;
                while (true) {
                    te = world.getTileEntity(pos.up(deep));
                    if (te == null || !(te instanceof TileAlembic)) {
                        return false;
                    }
                    TileAlembic alembic = (TileAlembic)te;
                    if ((alembic.aspectFilter == null || alembic.aspectFilter == aspect) && alembic.addToContainer(aspect, 1) == 0) {
                        return true;
                    }
                    ++deep;
                }
            }
        }
    }
}
