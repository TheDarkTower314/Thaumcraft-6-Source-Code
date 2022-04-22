// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.essentia;

import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import java.util.List;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.common.tiles.devices.TileBellows;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.util.EnumFacing;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

public class TileTubeBuffer extends TileTube implements IAspectContainer
{
    public AspectList aspects;
    public final int MAXAMOUNT = 10;
    public byte[] chokedSides;
    int count;
    int bellows;
    
    public TileTubeBuffer() {
        aspects = new AspectList();
        chokedSides = new byte[] { 0, 0, 0, 0, 0, 0 };
        count = 0;
        bellows = -1;
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        aspects.readFromNBT(nbttagcompound);
        final byte[] sides = nbttagcompound.getByteArray("open");
        if (sides != null && sides.length == 6) {
            for (int a = 0; a < 6; ++a) {
                openSides[a] = (sides[a] == 1);
            }
        }
        chokedSides = nbttagcompound.getByteArray("choke");
        if (chokedSides == null || chokedSides.length < 6) {
            chokedSides = new byte[] { 0, 0, 0, 0, 0, 0 };
        }
        facing = EnumFacing.VALUES[nbttagcompound.getInteger("side")];
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        aspects.writeToNBT(nbttagcompound);
        final byte[] sides = new byte[6];
        for (int a = 0; a < 6; ++a) {
            sides[a] = (byte)(openSides[a] ? 1 : 0);
        }
        nbttagcompound.setByteArray("open", sides);
        nbttagcompound.setByteArray("choke", chokedSides);
        nbttagcompound.setInteger("side", facing.ordinal());
        return nbttagcompound;
    }
    
    @Override
    public AspectList getAspects() {
        return aspects;
    }
    
    @Override
    public void setAspects(final AspectList aspects) {
    }
    
    @Override
    public int addToContainer(final Aspect tt, final int am) {
        if (am != 1) {
            return am;
        }
        if (aspects.visSize() < 10) {
            aspects.add(tt, am);
            markDirty();
            syncTile(false);
            return 0;
        }
        return am;
    }
    
    @Override
    public boolean takeFromContainer(final Aspect tt, final int am) {
        if (aspects.getAmount(tt) >= am) {
            aspects.remove(tt, am);
            markDirty();
            syncTile(false);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean takeFromContainer(final AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContainAmount(final Aspect tag, final int amt) {
        return aspects.getAmount(tag) >= amt;
    }
    
    @Override
    public boolean doesContainerContain(final AspectList ot) {
        return false;
    }
    
    @Override
    public int containerContains(final Aspect tag) {
        return aspects.getAmount(tag);
    }
    
    @Override
    public boolean doesContainerAccept(final Aspect tag) {
        return true;
    }
    
    @Override
    public boolean isConnectable(final EnumFacing face) {
        return openSides[face.ordinal()];
    }
    
    @Override
    public boolean canInputFrom(final EnumFacing face) {
        return openSides[face.ordinal()];
    }
    
    @Override
    public boolean canOutputTo(final EnumFacing face) {
        return openSides[face.ordinal()];
    }
    
    @Override
    public void setSuction(final Aspect aspect, final int amount) {
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    @Override
    public Aspect getSuctionType(final EnumFacing loc) {
        return null;
    }
    
    @Override
    public int getSuctionAmount(final EnumFacing loc) {
        return (chokedSides[loc.ordinal()] == 2) ? 0 : ((bellows <= 0 || chokedSides[loc.ordinal()] == 1) ? 1 : (bellows * 32));
    }
    
    @Override
    public Aspect getEssentiaType(final EnumFacing loc) {
        return (aspects.size() > 0) ? aspects.getAspects()[world.rand.nextInt(aspects.getAspects().length)] : null;
    }
    
    @Override
    public int getEssentiaAmount(final EnumFacing loc) {
        return aspects.visSize();
    }
    
    @Override
    public int takeEssentia(final Aspect aspect, int amount, final EnumFacing face) {
        if (!canOutputTo(face)) {
            return 0;
        }
        TileEntity te = null;
        IEssentiaTransport ic = null;
        int suction = 0;
        te = ThaumcraftApiHelper.getConnectableTile(world, pos, face);
        if (te != null) {
            ic = (IEssentiaTransport)te;
            suction = ic.getSuctionAmount(face.getOpposite());
        }
        for (final EnumFacing dir : EnumFacing.VALUES) {
            if (canOutputTo(dir)) {
                if (dir != face) {
                    te = ThaumcraftApiHelper.getConnectableTile(world, pos, dir);
                    if (te != null) {
                        ic = (IEssentiaTransport)te;
                        final int sa = ic.getSuctionAmount(dir.getOpposite());
                        final Aspect su = ic.getSuctionType(dir.getOpposite());
                        if ((su == aspect || su == null) && suction < sa && getSuctionAmount(dir) < sa) {
                            return 0;
                        }
                    }
                }
            }
        }
        if (amount > aspects.getAmount(aspect)) {
            amount = aspects.getAmount(aspect);
        }
        return takeFromContainer(aspect, amount) ? amount : 0;
    }
    
    @Override
    public int addEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        return canInputFrom(face) ? (amount - addToContainer(aspect, amount)) : 0;
    }
    
    @Override
    public void update() {
        ++count;
        if (bellows < 0 || count % 20 == 0) {
            getBellows();
        }
        if (!world.isRemote && count % 5 == 0) {
            final int visSize = aspects.visSize();
            getClass();
            if (visSize < 10) {
                fillBuffer();
            }
        }
    }
    
    void fillBuffer() {
        TileEntity te = null;
        IEssentiaTransport ic = null;
        for (final EnumFacing dir : EnumFacing.VALUES) {
            if (canInputFrom(dir)) {
                te = ThaumcraftApiHelper.getConnectableTile(world, pos, dir);
                if (te != null) {
                    ic = (IEssentiaTransport)te;
                    if (ic.getEssentiaAmount(dir.getOpposite()) > 0 && ic.getSuctionAmount(dir.getOpposite()) < getSuctionAmount(dir) && getSuctionAmount(dir) >= ic.getMinimumSuction()) {
                        final Aspect ta = ic.getEssentiaType(dir.getOpposite());
                        addToContainer(ta, ic.takeEssentia(ta, 1, dir.getOpposite()));
                        return;
                    }
                }
            }
        }
    }
    
    public void getBellows() {
        bellows = TileBellows.getBellows(world, pos, EnumFacing.VALUES);
    }
    
    @Override
    public boolean onCasterRightClick(final World world, final ItemStack wandstack, final EntityPlayer player, final BlockPos bp, final EnumFacing side, final EnumHand hand) {
        final RayTraceResult hit = RayTracer.retraceBlock(world, player, pos);
        if (hit == null) {
            return false;
        }
        if (hit.subHit >= 0 && hit.subHit < 6) {
            player.swingArm(hand);
            if (player.isSneaking()) {
                player.world.playSound(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5, SoundsTC.squeek, SoundCategory.BLOCKS, 0.6f, 2.0f + world.rand.nextFloat() * 0.2f, false);
                if (!world.isRemote) {
                    final byte[] chokedSides = this.chokedSides;
                    final int subHit = hit.subHit;
                    ++chokedSides[subHit];
                    if (this.chokedSides[hit.subHit] > 2) {
                        this.chokedSides[hit.subHit] = 0;
                    }
                    markDirty();
                    syncTile(true);
                }
            }
            else {
                player.world.playSound(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5, SoundsTC.tool, SoundCategory.BLOCKS, 0.5f, 0.9f + player.world.rand.nextFloat() * 0.2f, false);
                openSides[hit.subHit] = !openSides[hit.subHit];
                final EnumFacing dir = EnumFacing.VALUES[hit.subHit];
                final TileEntity tile = world.getTileEntity(pos.offset(dir));
                if (tile != null && tile instanceof TileTube) {
                    ((TileTube)tile).openSides[dir.getOpposite().ordinal()] = openSides[hit.subHit];
                    ((TileTube)tile).syncTile(true);
                    tile.markDirty();
                }
                markDirty();
                syncTile(true);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean canConnectSide(final EnumFacing side) {
        final TileEntity tile = world.getTileEntity(pos.offset(side));
        return tile != null && tile instanceof IEssentiaTransport;
    }
    
    @Override
    public void addTraceableCuboids(final List<IndexedCuboid6> cuboids) {
        final float min = 0.375f;
        final float max = 0.625f;
        if (canConnectSide(EnumFacing.DOWN)) {
            cuboids.add(new IndexedCuboid6(0, new Cuboid6(pos.getX() + min, pos.getY(), pos.getZ() + min, pos.getX() + max, pos.getY() + 0.5, pos.getZ() + max)));
        }
        if (canConnectSide(EnumFacing.UP)) {
            cuboids.add(new IndexedCuboid6(1, new Cuboid6(pos.getX() + min, pos.getY() + 0.5, pos.getZ() + min, pos.getX() + max, pos.getY() + 1, pos.getZ() + max)));
        }
        if (canConnectSide(EnumFacing.NORTH)) {
            cuboids.add(new IndexedCuboid6(2, new Cuboid6(pos.getX() + min, pos.getY() + min, pos.getZ(), pos.getX() + max, pos.getY() + max, pos.getZ() + 0.5)));
        }
        if (canConnectSide(EnumFacing.SOUTH)) {
            cuboids.add(new IndexedCuboid6(3, new Cuboid6(pos.getX() + min, pos.getY() + min, pos.getZ() + 0.5, pos.getX() + max, pos.getY() + max, pos.getZ() + 1)));
        }
        if (canConnectSide(EnumFacing.WEST)) {
            cuboids.add(new IndexedCuboid6(4, new Cuboid6(pos.getX(), pos.getY() + min, pos.getZ() + min, pos.getX() + 0.5, pos.getY() + max, pos.getZ() + max)));
        }
        if (canConnectSide(EnumFacing.EAST)) {
            cuboids.add(new IndexedCuboid6(5, new Cuboid6(pos.getX() + 0.5, pos.getY() + min, pos.getZ() + min, pos.getX() + 1, pos.getY() + max, pos.getZ() + max)));
        }
        cuboids.add(new IndexedCuboid6(6, new Cuboid6(pos.getX() + 0.25f, pos.getY() + 0.25f, pos.getZ() + 0.25f, pos.getX() + 0.75f, pos.getY() + 0.75f, pos.getZ() + 0.75f)));
    }
}
