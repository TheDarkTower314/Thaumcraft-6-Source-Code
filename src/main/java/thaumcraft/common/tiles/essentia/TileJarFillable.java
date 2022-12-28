package thaumcraft.common.tiles.essentia;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;


public class TileJarFillable extends TileJar implements IAspectSource, IEssentiaTransport
{
    public static int CAPACITY = 250;
    public Aspect aspect;
    public Aspect aspectFilter;
    public int amount;
    public int facing;
    public boolean blocked;
    int count;
    
    public TileJarFillable() {
        aspect = null;
        aspectFilter = null;
        amount = 0;
        facing = 2;
        blocked = false;
        count = 0;
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbttagcompound) {
        aspect = Aspect.getAspect(nbttagcompound.getString("Aspect"));
        aspectFilter = Aspect.getAspect(nbttagcompound.getString("AspectFilter"));
        amount = nbttagcompound.getShort("Amount");
        facing = nbttagcompound.getByte("facing");
        blocked = nbttagcompound.getBoolean("blocked");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
        if (aspect != null) {
            nbttagcompound.setString("Aspect", aspect.getTag());
        }
        if (aspectFilter != null) {
            nbttagcompound.setString("AspectFilter", aspectFilter.getTag());
        }
        nbttagcompound.setShort("Amount", (short) amount);
        nbttagcompound.setByte("facing", (byte) facing);
        nbttagcompound.setBoolean("blocked", blocked);
        return nbttagcompound;
    }
    
    public AspectList getAspects() {
        AspectList al = new AspectList();
        if (aspect != null && amount > 0) {
            al.add(aspect, amount);
        }
        return al;
    }
    
    public void setAspects(AspectList aspects) {
        if (aspects != null && aspects.size() > 0) {
            aspect = aspects.getAspectsSortedByAmount()[0];
            amount = aspects.getAmount(aspects.getAspectsSortedByAmount()[0]);
        }
    }
    
    public int addToContainer(Aspect tt, int am) {
        if (am == 0) {
            return am;
        }
        if ((amount < 250 && tt == aspect) || amount == 0) {
            aspect = tt;
            int added = Math.min(am, 250 - amount);
            amount += added;
            am -= added;
        }
        syncTile(false);
        markDirty();
        return am;
    }
    
    public boolean takeFromContainer(Aspect tt, int am) {
        if (amount >= am && tt == aspect) {
            amount -= am;
            if (amount <= 0) {
                aspect = null;
                amount = 0;
            }
            syncTile(false);
            markDirty();
            return true;
        }
        return false;
    }
    
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }
    
    public boolean doesContainerContainAmount(Aspect tag, int amt) {
        return amount >= amt && tag == aspect;
    }
    
    public boolean doesContainerContain(AspectList ot) {
        for (Aspect tt : ot.getAspects()) {
            if (amount > 0 && tt == aspect) {
                return true;
            }
        }
        return false;
    }
    
    public int containerContains(Aspect tag) {
        if (tag == aspect) {
            return amount;
        }
        return 0;
    }
    
    public boolean doesContainerAccept(Aspect tag) {
        return aspectFilter == null || tag.equals(aspectFilter);
    }
    
    @Override
    public boolean isConnectable(EnumFacing face) {
        return face == EnumFacing.UP;
    }
    
    @Override
    public boolean canInputFrom(EnumFacing face) {
        return face == EnumFacing.UP;
    }
    
    @Override
    public boolean canOutputTo(EnumFacing face) {
        return face == EnumFacing.UP;
    }
    
    @Override
    public void setSuction(Aspect aspect, int amount) {
    }
    
    @Override
    public int getMinimumSuction() {
        return (aspectFilter != null) ? 64 : 32;
    }
    
    @Override
    public Aspect getSuctionType(EnumFacing loc) {
        return (aspectFilter != null) ? aspectFilter : aspect;
    }
    
    @Override
    public int getSuctionAmount(EnumFacing loc) {
        if (amount >= 250) {
            return 0;
        }
        if (aspectFilter != null) {
            return 64;
        }
        return 32;
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
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        return canInputFrom(face) ? (amount - addToContainer(aspect, amount)) : 0;
    }
    
    @Override
    public void update() {
        if (!world.isRemote && ++count % 5 == 0 && amount < 250) {
            fillJar();
        }
    }
    
    void fillJar() {
        TileEntity te = ThaumcraftApiHelper.getConnectableTile(world, pos, EnumFacing.UP);
        if (te != null) {
            IEssentiaTransport ic = (IEssentiaTransport)te;
            if (!ic.canOutputTo(EnumFacing.DOWN)) {
                return;
            }
            Aspect ta = null;
            if (aspectFilter != null) {
                ta = aspectFilter;
            }
            else if (aspect != null && amount > 0) {
                ta = aspect;
            }
            else if (ic.getEssentiaAmount(EnumFacing.DOWN) > 0 && ic.getSuctionAmount(EnumFacing.DOWN) < getSuctionAmount(EnumFacing.UP) && getSuctionAmount(EnumFacing.UP) >= ic.getMinimumSuction()) {
                ta = ic.getEssentiaType(EnumFacing.DOWN);
            }
            if (ta != null && ic.getSuctionAmount(EnumFacing.DOWN) < getSuctionAmount(EnumFacing.UP)) {
                addToContainer(ta, ic.takeEssentia(ta, 1, EnumFacing.DOWN));
            }
        }
    }
    
    @Override
    public boolean isBlocked() {
        return blocked;
    }
}
