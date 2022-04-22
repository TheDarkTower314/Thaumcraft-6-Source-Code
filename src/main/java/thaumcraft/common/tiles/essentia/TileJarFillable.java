// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.essentia;

import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.ThaumcraftApiHelper;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.aspects.AspectList;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aspects.IAspectSource;

public class TileJarFillable extends TileJar implements IAspectSource, IEssentiaTransport
{
    public static final int CAPACITY = 250;
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
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        aspect = Aspect.getAspect(nbttagcompound.getString("Aspect"));
        aspectFilter = Aspect.getAspect(nbttagcompound.getString("AspectFilter"));
        amount = nbttagcompound.getShort("Amount");
        facing = nbttagcompound.getByte("facing");
        blocked = nbttagcompound.getBoolean("blocked");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
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
        final AspectList al = new AspectList();
        if (aspect != null && amount > 0) {
            al.add(aspect, amount);
        }
        return al;
    }
    
    public void setAspects(final AspectList aspects) {
        if (aspects != null && aspects.size() > 0) {
            aspect = aspects.getAspectsSortedByAmount()[0];
            amount = aspects.getAmount(aspects.getAspectsSortedByAmount()[0]);
        }
    }
    
    public int addToContainer(final Aspect tt, int am) {
        if (am == 0) {
            return am;
        }
        if ((amount < 250 && tt == aspect) || amount == 0) {
            aspect = tt;
            final int added = Math.min(am, 250 - amount);
            amount += added;
            am -= added;
        }
        syncTile(false);
        markDirty();
        return am;
    }
    
    public boolean takeFromContainer(final Aspect tt, final int am) {
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
    
    public boolean takeFromContainer(final AspectList ot) {
        return false;
    }
    
    public boolean doesContainerContainAmount(final Aspect tag, final int amt) {
        return amount >= amt && tag == aspect;
    }
    
    public boolean doesContainerContain(final AspectList ot) {
        for (final Aspect tt : ot.getAspects()) {
            if (amount > 0 && tt == aspect) {
                return true;
            }
        }
        return false;
    }
    
    public int containerContains(final Aspect tag) {
        if (tag == aspect) {
            return amount;
        }
        return 0;
    }
    
    public boolean doesContainerAccept(final Aspect tag) {
        return aspectFilter == null || tag.equals(aspectFilter);
    }
    
    @Override
    public boolean isConnectable(final EnumFacing face) {
        return face == EnumFacing.UP;
    }
    
    @Override
    public boolean canInputFrom(final EnumFacing face) {
        return face == EnumFacing.UP;
    }
    
    @Override
    public boolean canOutputTo(final EnumFacing face) {
        return face == EnumFacing.UP;
    }
    
    @Override
    public void setSuction(final Aspect aspect, final int amount) {
    }
    
    @Override
    public int getMinimumSuction() {
        return (aspectFilter != null) ? 64 : 32;
    }
    
    @Override
    public Aspect getSuctionType(final EnumFacing loc) {
        return (aspectFilter != null) ? aspectFilter : aspect;
    }
    
    @Override
    public int getSuctionAmount(final EnumFacing loc) {
        if (amount >= 250) {
            return 0;
        }
        if (aspectFilter != null) {
            return 64;
        }
        return 32;
    }
    
    @Override
    public Aspect getEssentiaType(final EnumFacing loc) {
        return aspect;
    }
    
    @Override
    public int getEssentiaAmount(final EnumFacing loc) {
        return amount;
    }
    
    @Override
    public int takeEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        return (canOutputTo(face) && takeFromContainer(aspect, amount)) ? amount : 0;
    }
    
    @Override
    public int addEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        return canInputFrom(face) ? (amount - addToContainer(aspect, amount)) : 0;
    }
    
    @Override
    public void update() {
        if (!world.isRemote && ++count % 5 == 0 && amount < 250) {
            fillJar();
        }
    }
    
    void fillJar() {
        final TileEntity te = ThaumcraftApiHelper.getConnectableTile(world, pos, EnumFacing.UP);
        if (te != null) {
            final IEssentiaTransport ic = (IEssentiaTransport)te;
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
