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
        this.MAX = 100;
        this.count = 0;
        this.history = new ArrayList<Long>();
        this.blockList = new ArrayList<Long>();
        this.uncloggedList = new ArrayList<Long>();
        this.latticeCount = -1.0f;
        this.interval = 0;
        this.cost = 0;
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        this.essentia = nbttagcompound.getShort("essentia");
        this.flux = nbttagcompound.getShort("flux");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("essentia", (short)this.essentia);
        nbttagcompound.setShort("flux", (short)this.flux);
        return nbttagcompound;
    }
    
    public void update() {
        if (this.latticeCount < 0.0f) {
            this.triggerCheck();
        }
        ++this.count;
        if (BlockStateUtils.isEnabled(this.getBlockMetadata()) && this.latticeCount > 0.0f) {
            if (this.world.isRemote) {
                if (this.essentia > 0 && this.uncloggedList.size() > 0 && this.count % Math.max(3, this.interval / 50) == 0) {
                    final BlockPos p = BlockPos.fromLong(this.uncloggedList.get(this.world.rand.nextInt(this.uncloggedList.size())));
                    if (p != null) {
                        FXDispatcher.INSTANCE.spark(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, 4.5f + this.world.rand.nextFloat(), 0.33f + this.world.rand.nextFloat() * 0.66f, 0.33f + this.world.rand.nextFloat() * 0.66f, 0.33f + this.world.rand.nextFloat() * 0.66f, 0.8f);
                    }
                }
            }
            else {
                if (this.count % 5 == 0 && this.essentia < this.MAX) {
                    this.fill();
                }
                if (this.interval > 0 && this.essentia >= this.cost && this.flux < this.MAX && this.count % this.interval == 0 && AuraHelper.getFlux(this.getWorld(), this.getPos()) >= 1.0f) {
                    AuraHelper.drainFlux(this.getWorld(), this.getPos(), 1.0f, false);
                    this.essentia -= this.cost;
                    ++this.flux;
                    if (this.world.rand.nextInt(50) == 0) {
                        this.makeLatticeDirty();
                    }
                    this.syncTile(false);
                    this.markDirty();
                }
            }
        }
    }
    
    private void makeLatticeDirty() {
        if (this.uncloggedList.size() > 0) {
            int q = this.world.rand.nextInt(this.uncloggedList.size());
            if (q == 0) {
                q = this.world.rand.nextInt(this.uncloggedList.size());
            }
            final BlockPos p = BlockPos.fromLong(this.uncloggedList.get(q));
            if (p != null) {
                final IBlockState bs = this.world.getBlockState(p);
                if (bs.getBlock() == BlocksTC.condenserlattice) {
                    this.world.setBlockState(p, BlocksTC.condenserlatticeDirty.getDefaultState(), 3);
                    ((BlockCondenserLattice)bs.getBlock()).triggerUpdate(this.world, p);
                }
            }
        }
    }
    
    private void fill() {
        for (final EnumFacing face : EnumFacing.HORIZONTALS) {
            final TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.world, this.pos, face);
            if (te != null) {
                final IEssentiaTransport ic = (IEssentiaTransport)te;
                Aspect ta = null;
                if (!ic.canOutputTo(face.getOpposite())) {
                    return;
                }
                if (ic.getEssentiaAmount(face.getOpposite()) > 0 && ic.getSuctionAmount(face.getOpposite()) < this.getSuctionAmount(face) && this.getSuctionAmount(face) >= ic.getMinimumSuction()) {
                    ta = ic.getEssentiaType(face.getOpposite());
                }
                if (ta != null) {
                    if (ta != Aspect.FLUX) {
                        this.essentia += ic.takeEssentia(ta, 1, face.getOpposite());
                    }
                    else {
                        this.makeLatticeDirty();
                    }
                    this.syncTile(false);
                    this.markDirty();
                    if (this.essentia >= this.MAX) {
                        break;
                    }
                }
            }
        }
    }
    
    public void triggerCheck() {
        this.history.clear();
        this.blockList.clear();
        this.uncloggedList.clear();
        this.latticeCount = 0.0f;
        this.interval = 0;
        this.performCheck(this.pos, true, false);
        this.history.clear();
        if (this.latticeCount <= 0.0f) {
            this.latticeCount = 0.0f;
        }
        else {
            if (this.latticeCount > 40.0f) {
                this.latticeCount = 40.0f;
            }
            this.interval = Math.round(600.0f - this.latticeCount * 15.0f);
            if (this.interval < 5) {
                this.interval = 5;
            }
            this.cost = (int)(4.0 + Math.sqrt(this.blockList.size()));
        }
    }
    
    private void performCheck(final BlockPos pos, final boolean skip, boolean clogged) {
        if (this.latticeCount < 0.0f) {
            return;
        }
        this.history.add(pos.toLong());
        boolean found = false;
        int sides = 0;
        for (final EnumFacing face : EnumFacing.VALUES) {
            if (!skip || face == EnumFacing.UP) {
                final BlockPos p2 = pos.offset(face);
                final IBlockState bs = this.world.getBlockState(p2);
                final boolean lattice = bs.getBlock() == BlocksTC.condenserlattice;
                final boolean latticeDirty = bs.getBlock() == BlocksTC.condenserlatticeDirty;
                if (skip && latticeDirty) {
                    clogged = true;
                }
                if (lattice || latticeDirty) {
                    ++sides;
                }
                if (!this.history.contains(p2.toLong())) {
                    if (face == EnumFacing.DOWN && this.world.getBlockState(p2).getBlock() == BlocksTC.condenser) {
                        this.latticeCount = -99.0f;
                        return;
                    }
                    if (this.getPos().getY() < p2.getY()) {
                        if (this.getPos().distanceSq(p2) <= 74.0) {
                            if (lattice || latticeDirty) {
                                this.blockList.add(p2.toLong());
                                if (lattice) {
                                    this.uncloggedList.add(p2.toLong());
                                }
                                found = true;
                                this.performCheck(p2, false, clogged || latticeDirty);
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
            this.latticeCount += f;
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
        return (face == EnumFacing.DOWN || this.essentia >= this.MAX) ? 0 : 128;
    }
    
    public int takeEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        final int amt = (this.canOutputTo(face) && (aspect == null || aspect == Aspect.FLUX)) ? Math.min(amount, this.flux) : 0;
        if (amt > 0) {
            this.flux -= amt;
            this.syncTile(false);
            this.markDirty();
        }
        return amt;
    }
    
    public int addEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        final int amt = this.canInputFrom(face) ? Math.min(amount, this.MAX - this.essentia) : 0;
        if (amt > 0) {
            this.syncTile(false);
            this.markDirty();
        }
        return amt;
    }
    
    public Aspect getEssentiaType(final EnumFacing face) {
        return Aspect.FLUX;
    }
    
    public int getEssentiaAmount(final EnumFacing face) {
        return this.flux;
    }
    
    public int getMinimumSuction() {
        return 0;
    }
}
