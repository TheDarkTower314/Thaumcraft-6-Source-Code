// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.utils.Utils;
import net.minecraftforge.common.DimensionManager;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileMirrorEssentia extends TileThaumcraft implements IAspectSource, ITickable
{
    public boolean linked;
    public int linkX;
    public int linkY;
    public int linkZ;
    public int linkDim;
    public EnumFacing linkedFacing;
    public int instability;
    int count;
    int inc;
    
    public TileMirrorEssentia() {
        this.linked = false;
        this.linkedFacing = EnumFacing.DOWN;
        this.count = 0;
        this.inc = 40;
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        this.linked = nbttagcompound.getBoolean("linked");
        this.linkX = nbttagcompound.getInteger("linkX");
        this.linkY = nbttagcompound.getInteger("linkY");
        this.linkZ = nbttagcompound.getInteger("linkZ");
        this.linkDim = nbttagcompound.getInteger("linkDim");
        this.instability = nbttagcompound.getInteger("instability");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("linked", this.linked);
        nbttagcompound.setInteger("linkX", this.linkX);
        nbttagcompound.setInteger("linkY", this.linkY);
        nbttagcompound.setInteger("linkZ", this.linkZ);
        nbttagcompound.setInteger("linkDim", this.linkDim);
        nbttagcompound.setInteger("instability", this.instability);
        return nbttagcompound;
    }
    
    protected void addInstability(final World targetWorld, final int amt) {
        this.instability += amt;
        this.markDirty();
        if (targetWorld != null) {
            final TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
            if (te != null && te instanceof TileMirrorEssentia) {
                final TileMirrorEssentia tileMirrorEssentia = (TileMirrorEssentia)te;
                tileMirrorEssentia.instability += amt;
                if (((TileMirrorEssentia)te).instability < 0) {
                    ((TileMirrorEssentia)te).instability = 0;
                }
                te.markDirty();
            }
        }
    }
    
    public void restoreLink() {
        if (this.isDestinationValid()) {
            final World targetWorld = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(this.linkDim);
            if (targetWorld == null) {
                return;
            }
            final TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
            if (te != null && te instanceof TileMirrorEssentia) {
                final TileMirrorEssentia tm = (TileMirrorEssentia)te;
                tm.linked = true;
                tm.linkX = this.getPos().getX();
                tm.linkY = this.getPos().getY();
                tm.linkZ = this.getPos().getZ();
                tm.linkDim = this.world.provider.getDimension();
                tm.syncTile(false);
                this.linkedFacing = BlockStateUtils.getFacing(targetWorld.getBlockState(new BlockPos(this.linkX, this.linkY, this.linkZ)));
                this.linked = true;
                this.markDirty();
                tm.markDirty();
                this.syncTile(false);
            }
        }
    }
    
    public void invalidateLink() {
        final World targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) {
            return;
        }
        if (!Utils.isChunkLoaded(targetWorld, this.linkX, this.linkZ)) {
            return;
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (te != null && te instanceof TileMirrorEssentia) {
            final TileMirrorEssentia tm = (TileMirrorEssentia)te;
            tm.linked = false;
            tm.linkedFacing = EnumFacing.DOWN;
            this.markDirty();
            tm.markDirty();
            tm.syncTile(false);
        }
    }
    
    public boolean isLinkValid() {
        if (!this.linked) {
            return false;
        }
        final World targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) {
            return false;
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (te == null || !(te instanceof TileMirrorEssentia)) {
            this.linked = false;
            this.markDirty();
            this.syncTile(false);
            return false;
        }
        final TileMirrorEssentia tm = (TileMirrorEssentia)te;
        if (!tm.linked) {
            this.linked = false;
            this.markDirty();
            this.syncTile(false);
            return false;
        }
        if (tm.linkX != this.getPos().getX() || tm.linkY != this.getPos().getY() || tm.linkZ != this.getPos().getZ() || tm.linkDim != this.world.provider.getDimension()) {
            this.linked = false;
            this.markDirty();
            this.syncTile(false);
            return false;
        }
        return true;
    }
    
    public boolean isLinkValidSimple() {
        if (!this.linked) {
            return false;
        }
        final World targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) {
            return false;
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (te == null || !(te instanceof TileMirrorEssentia)) {
            return false;
        }
        final TileMirrorEssentia tm = (TileMirrorEssentia)te;
        return tm.linked && tm.linkX == this.getPos().getX() && tm.linkY == this.getPos().getY() && tm.linkZ == this.getPos().getZ() && tm.linkDim == this.world.provider.getDimension();
    }
    
    public boolean isDestinationValid() {
        final World targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) {
            return false;
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (te == null || !(te instanceof TileMirrorEssentia)) {
            this.linked = false;
            this.markDirty();
            this.syncTile(false);
            return false;
        }
        final TileMirrorEssentia tm = (TileMirrorEssentia)te;
        return !tm.isLinkValid();
    }
    
    public AspectList getAspects() {
        return null;
    }
    
    public void setAspects(final AspectList aspects) {
    }
    
    public boolean doesContainerAccept(final Aspect tag) {
        final World targetWorld = DimensionManager.getWorld(this.linkDim);
        if (this.linkedFacing == EnumFacing.DOWN && targetWorld != null) {
            this.linkedFacing = BlockStateUtils.getFacing(targetWorld.getBlockState(new BlockPos(this.linkX, this.linkY, this.linkZ)));
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        return te == null || !(te instanceof TileMirrorEssentia) || EssentiaHandler.canAcceptEssentia(te, tag, this.linkedFacing, 8, true);
    }
    
    public int addToContainer(final Aspect tag, final int amount) {
        if (!this.isLinkValid() || amount > 1) {
            return amount;
        }
        final World targetWorld = DimensionManager.getWorld(this.linkDim);
        if (this.linkedFacing == EnumFacing.DOWN && targetWorld != null) {
            this.linkedFacing = BlockStateUtils.getFacing(targetWorld.getBlockState(new BlockPos(this.linkX, this.linkY, this.linkZ)));
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (te != null && te instanceof TileMirrorEssentia) {
            final boolean b = EssentiaHandler.addEssentia(te, tag, this.linkedFacing, 8, true, 5);
            if (b) {
                this.addInstability(null, amount);
            }
            return b ? 0 : 1;
        }
        return amount;
    }
    
    public boolean takeFromContainer(final Aspect tag, final int amount) {
        if (!this.isLinkValid() || amount > 1) {
            return false;
        }
        final World targetWorld = DimensionManager.getWorld(this.linkDim);
        if (this.linkedFacing == EnumFacing.DOWN && targetWorld != null) {
            this.linkedFacing = BlockStateUtils.getFacing(targetWorld.getBlockState(new BlockPos(this.linkX, this.linkY, this.linkZ)));
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (te != null && te instanceof TileMirrorEssentia) {
            final boolean b = EssentiaHandler.drainEssentia(te, tag, this.linkedFacing, 8, true, 5);
            if (b) {
                this.addInstability(null, amount);
            }
            return b;
        }
        return false;
    }
    
    public boolean takeFromContainer(final AspectList ot) {
        return false;
    }
    
    public boolean doesContainerContainAmount(final Aspect tag, final int amount) {
        if (!this.isLinkValid() || amount > 1) {
            return false;
        }
        final World targetWorld = DimensionManager.getWorld(this.linkDim);
        if (this.linkedFacing == EnumFacing.DOWN && targetWorld != null) {
            this.linkedFacing = BlockStateUtils.getFacing(targetWorld.getBlockState(new BlockPos(this.linkX, this.linkY, this.linkZ)));
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        return te != null && te instanceof TileMirrorEssentia && EssentiaHandler.findEssentia(te, tag, this.linkedFacing, 8, true);
    }
    
    public boolean doesContainerContain(final AspectList ot) {
        return false;
    }
    
    public int containerContains(final Aspect tag) {
        return 0;
    }
    
    public void update() {
        if (!this.world.isRemote) {
            this.checkInstability();
            if (this.count++ % this.inc == 0) {
                if (!this.isLinkValidSimple()) {
                    if (this.inc < 600) {
                        this.inc += 20;
                    }
                    this.restoreLink();
                }
                else {
                    this.inc = 40;
                }
            }
        }
    }
    
    public void checkInstability() {
        if (this.instability > 64) {
            AuraHelper.polluteAura(this.world, this.pos, 1.0f, true);
            this.instability -= 64;
            this.markDirty();
        }
        if (this.instability > 0 && this.count % 100 == 0) {
            --this.instability;
        }
    }
    
    @Override
    public boolean isBlocked() {
        return false;
    }
}
