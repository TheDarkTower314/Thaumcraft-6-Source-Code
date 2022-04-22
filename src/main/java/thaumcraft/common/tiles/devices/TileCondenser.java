// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import net.minecraft.util.math.Vec3i;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.ThaumcraftApiHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.state.IBlockState;
import thaumcraft.common.blocks.devices.BlockCondenserLattice;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.nbt.NBTTagCompound;
import java.util.ArrayList;
import thaumcraft.api.aspects.IEssentiaTransport;
import net.minecraft.util.ITickable;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileCondenser extends TileThaumcraft implements ITickable, IEssentiaTransport
{
    private int essentia;
    private int flux;
    private int MAX;
    private int count;
    private ArrayList<Long> history;
    private ArrayList<Long> blockList;
    private ArrayList<Long> uncloggedList;
    public float latticeCount;
    public int interval;
    public int cost;
    
    public TileCondenser() {
        MAX = 100;
        count = 0;
        history = new ArrayList<Long>();
        blockList = new ArrayList<Long>();
        uncloggedList = new ArrayList<Long>();
        latticeCount = -1.0f;
        interval = 0;
        cost = 0;
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        essentia = nbttagcompound.getShort("essentia");
        flux = nbttagcompound.getShort("flux");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("essentia", (short) essentia);
        nbttagcompound.setShort("flux", (short) flux);
        return nbttagcompound;
    }
    
    public void update() {
        if (latticeCount < 0.0f) {
            triggerCheck();
        }
        ++count;
        if (BlockStateUtils.isEnabled(getBlockMetadata()) && latticeCount > 0.0f) {
            if (world.isRemote) {
                if (essentia > 0 && uncloggedList.size() > 0 && count % Math.max(3, interval / 50) == 0) {
                    final BlockPos p = BlockPos.fromLong(uncloggedList.get(world.rand.nextInt(uncloggedList.size())));
                    if (p != null) {
                        FXDispatcher.INSTANCE.spark(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, 4.5f + world.rand.nextFloat(), 0.33f + world.rand.nextFloat() * 0.66f, 0.33f + world.rand.nextFloat() * 0.66f, 0.33f + world.rand.nextFloat() * 0.66f, 0.8f);
                    }
                }
            }
            else {
                if (count % 5 == 0 && essentia < MAX) {
                    fill();
                }
                if (interval > 0 && essentia >= cost && flux < MAX && count % interval == 0 && AuraHelper.getFlux(getWorld(), getPos()) >= 1.0f) {
                    AuraHelper.drainFlux(getWorld(), getPos(), 1.0f, false);
                    essentia -= cost;
                    ++flux;
                    if (world.rand.nextInt(50) == 0) {
                        makeLatticeDirty();
                    }
                    syncTile(false);
                    markDirty();
                }
            }
        }
    }
    
    private void makeLatticeDirty() {
        if (uncloggedList.size() > 0) {
            int q = world.rand.nextInt(uncloggedList.size());
            if (q == 0) {
                q = world.rand.nextInt(uncloggedList.size());
            }
            final BlockPos p = BlockPos.fromLong(uncloggedList.get(q));
            if (p != null) {
                final IBlockState bs = world.getBlockState(p);
                if (bs.getBlock() == BlocksTC.condenserlattice) {
                    world.setBlockState(p, BlocksTC.condenserlatticeDirty.getDefaultState(), 3);
                    ((BlockCondenserLattice)bs.getBlock()).triggerUpdate(world, p);
                }
            }
        }
    }
    
    private void fill() {
        for (final EnumFacing face : EnumFacing.HORIZONTALS) {
            final TileEntity te = ThaumcraftApiHelper.getConnectableTile(world, pos, face);
            if (te != null) {
                final IEssentiaTransport ic = (IEssentiaTransport)te;
                Aspect ta = null;
                if (!ic.canOutputTo(face.getOpposite())) {
                    return;
                }
                if (ic.getEssentiaAmount(face.getOpposite()) > 0 && ic.getSuctionAmount(face.getOpposite()) < getSuctionAmount(face) && getSuctionAmount(face) >= ic.getMinimumSuction()) {
                    ta = ic.getEssentiaType(face.getOpposite());
                }
                if (ta != null) {
                    if (ta != Aspect.FLUX) {
                        essentia += ic.takeEssentia(ta, 1, face.getOpposite());
                    }
                    else {
                        makeLatticeDirty();
                    }
                    syncTile(false);
                    markDirty();
                    if (essentia >= MAX) {
                        break;
                    }
                }
            }
        }
    }
    
    public void triggerCheck() {
        history.clear();
        blockList.clear();
        uncloggedList.clear();
        latticeCount = 0.0f;
        interval = 0;
        performCheck(pos, true, false);
        history.clear();
        if (latticeCount <= 0.0f) {
            latticeCount = 0.0f;
        }
        else {
            if (latticeCount > 40.0f) {
                latticeCount = 40.0f;
            }
            interval = Math.round(600.0f - latticeCount * 15.0f);
            if (interval < 5) {
                interval = 5;
            }
            cost = (int)(4.0 + Math.sqrt(blockList.size()));
        }
    }
    
    private void performCheck(final BlockPos pos, final boolean skip, boolean clogged) {
        if (latticeCount < 0.0f) {
            return;
        }
        history.add(pos.toLong());
        boolean found = false;
        int sides = 0;
        for (final EnumFacing face : EnumFacing.VALUES) {
            if (!skip || face == EnumFacing.UP) {
                final BlockPos p2 = pos.offset(face);
                final IBlockState bs = world.getBlockState(p2);
                final boolean lattice = bs.getBlock() == BlocksTC.condenserlattice;
                final boolean latticeDirty = bs.getBlock() == BlocksTC.condenserlatticeDirty;
                if (skip && latticeDirty) {
                    clogged = true;
                }
                if (lattice || latticeDirty) {
                    ++sides;
                }
                if (!history.contains(p2.toLong())) {
                    if (face == EnumFacing.DOWN && world.getBlockState(p2).getBlock() == BlocksTC.condenser) {
                        latticeCount = -99.0f;
                        return;
                    }
                    if (getPos().getY() < p2.getY()) {
                        if (getPos().distanceSq(p2) <= 74.0) {
                            if (lattice || latticeDirty) {
                                blockList.add(p2.toLong());
                                if (lattice) {
                                    uncloggedList.add(p2.toLong());
                                }
                                found = true;
                                performCheck(p2, false, clogged || latticeDirty);
                                if (skip) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (found && !clogged) {
            final float f = 1.0f - 0.15f * sides;
            latticeCount += f;
        }
    }
    
    public boolean isConnectable(final EnumFacing face) {
        return face != EnumFacing.UP;
    }
    
    public boolean canInputFrom(final EnumFacing face) {
        return face != EnumFacing.UP && face != EnumFacing.DOWN;
    }
    
    public boolean canOutputTo(final EnumFacing face) {
        return face == EnumFacing.DOWN;
    }
    
    public void setSuction(final Aspect aspect, final int amount) {
    }
    
    public Aspect getSuctionType(final EnumFacing face) {
        return null;
    }
    
    public int getSuctionAmount(final EnumFacing face) {
        return (face == EnumFacing.DOWN || essentia >= MAX) ? 0 : 128;
    }
    
    public int takeEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        final int amt = (canOutputTo(face) && (aspect == null || aspect == Aspect.FLUX)) ? Math.min(amount, flux) : 0;
        if (amt > 0) {
            flux -= amt;
            syncTile(false);
            markDirty();
        }
        return amt;
    }
    
    public int addEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        final int amt = canInputFrom(face) ? Math.min(amount, MAX - essentia) : 0;
        if (amt > 0) {
            syncTile(false);
            markDirty();
        }
        return amt;
    }
    
    public Aspect getEssentiaType(final EnumFacing face) {
        return Aspect.FLUX;
    }
    
    public int getEssentiaAmount(final EnumFacing face) {
        return flux;
    }
    
    public int getMinimumSuction() {
        return 0;
    }
}
