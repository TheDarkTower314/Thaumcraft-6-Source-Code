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
        linked = false;
        linkedFacing = EnumFacing.DOWN;
        count = 0;
        inc = 40;
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        linked = nbttagcompound.getBoolean("linked");
        linkX = nbttagcompound.getInteger("linkX");
        linkY = nbttagcompound.getInteger("linkY");
        linkZ = nbttagcompound.getInteger("linkZ");
        linkDim = nbttagcompound.getInteger("linkDim");
        instability = nbttagcompound.getInteger("instability");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("linked", linked);
        nbttagcompound.setInteger("linkX", linkX);
        nbttagcompound.setInteger("linkY", linkY);
        nbttagcompound.setInteger("linkZ", linkZ);
        nbttagcompound.setInteger("linkDim", linkDim);
        nbttagcompound.setInteger("instability", instability);
        return nbttagcompound;
    }
    
    protected void addInstability(final World targetWorld, final int amt) {
        instability += amt;
        markDirty();
        if (targetWorld != null) {
            final TileEntity te = targetWorld.getTileEntity(new BlockPos(linkX, linkY, linkZ));
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
        if (isDestinationValid()) {
            final World targetWorld = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(linkDim);
            if (targetWorld == null) {
                return;
            }
            final TileEntity te = targetWorld.getTileEntity(new BlockPos(linkX, linkY, linkZ));
            if (te != null && te instanceof TileMirrorEssentia) {
                final TileMirrorEssentia tm = (TileMirrorEssentia)te;
                tm.linked = true;
                tm.linkX = getPos().getX();
                tm.linkY = getPos().getY();
                tm.linkZ = getPos().getZ();
                tm.linkDim = world.provider.getDimension();
                tm.syncTile(false);
                linkedFacing = BlockStateUtils.getFacing(targetWorld.getBlockState(new BlockPos(linkX, linkY, linkZ)));
                linked = true;
                markDirty();
                tm.markDirty();
                syncTile(false);
            }
        }
    }
    
    public void invalidateLink() {
        final World targetWorld = DimensionManager.getWorld(linkDim);
        if (targetWorld == null) {
            return;
        }
        if (!Utils.isChunkLoaded(targetWorld, linkX, linkZ)) {
            return;
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(linkX, linkY, linkZ));
        if (te != null && te instanceof TileMirrorEssentia) {
            final TileMirrorEssentia tm = (TileMirrorEssentia)te;
            tm.linked = false;
            tm.linkedFacing = EnumFacing.DOWN;
            markDirty();
            tm.markDirty();
            tm.syncTile(false);
        }
    }
    
    public boolean isLinkValid() {
        if (!linked) {
            return false;
        }
        final World targetWorld = DimensionManager.getWorld(linkDim);
        if (targetWorld == null) {
            return false;
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(linkX, linkY, linkZ));
        if (te == null || !(te instanceof TileMirrorEssentia)) {
            linked = false;
            markDirty();
            syncTile(false);
            return false;
        }
        final TileMirrorEssentia tm = (TileMirrorEssentia)te;
        if (!tm.linked) {
            linked = false;
            markDirty();
            syncTile(false);
            return false;
        }
        if (tm.linkX != getPos().getX() || tm.linkY != getPos().getY() || tm.linkZ != getPos().getZ() || tm.linkDim != world.provider.getDimension()) {
            linked = false;
            markDirty();
            syncTile(false);
            return false;
        }
        return true;
    }
    
    public boolean isLinkValidSimple() {
        if (!linked) {
            return false;
        }
        final World targetWorld = DimensionManager.getWorld(linkDim);
        if (targetWorld == null) {
            return false;
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(linkX, linkY, linkZ));
        if (te == null || !(te instanceof TileMirrorEssentia)) {
            return false;
        }
        final TileMirrorEssentia tm = (TileMirrorEssentia)te;
        return tm.linked && tm.linkX == getPos().getX() && tm.linkY == getPos().getY() && tm.linkZ == getPos().getZ() && tm.linkDim == world.provider.getDimension();
    }
    
    public boolean isDestinationValid() {
        final World targetWorld = DimensionManager.getWorld(linkDim);
        if (targetWorld == null) {
            return false;
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(linkX, linkY, linkZ));
        if (te == null || !(te instanceof TileMirrorEssentia)) {
            linked = false;
            markDirty();
            syncTile(false);
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
        final World targetWorld = DimensionManager.getWorld(linkDim);
        if (linkedFacing == EnumFacing.DOWN && targetWorld != null) {
            linkedFacing = BlockStateUtils.getFacing(targetWorld.getBlockState(new BlockPos(linkX, linkY, linkZ)));
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(linkX, linkY, linkZ));
        return te == null || !(te instanceof TileMirrorEssentia) || EssentiaHandler.canAcceptEssentia(te, tag, linkedFacing, 8, true);
    }
    
    public int addToContainer(final Aspect tag, final int amount) {
        if (!isLinkValid() || amount > 1) {
            return amount;
        }
        final World targetWorld = DimensionManager.getWorld(linkDim);
        if (linkedFacing == EnumFacing.DOWN && targetWorld != null) {
            linkedFacing = BlockStateUtils.getFacing(targetWorld.getBlockState(new BlockPos(linkX, linkY, linkZ)));
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(linkX, linkY, linkZ));
        if (te != null && te instanceof TileMirrorEssentia) {
            final boolean b = EssentiaHandler.addEssentia(te, tag, linkedFacing, 8, true, 5);
            if (b) {
                addInstability(null, amount);
            }
            return b ? 0 : 1;
        }
        return amount;
    }
    
    public boolean takeFromContainer(final Aspect tag, final int amount) {
        if (!isLinkValid() || amount > 1) {
            return false;
        }
        final World targetWorld = DimensionManager.getWorld(linkDim);
        if (linkedFacing == EnumFacing.DOWN && targetWorld != null) {
            linkedFacing = BlockStateUtils.getFacing(targetWorld.getBlockState(new BlockPos(linkX, linkY, linkZ)));
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(linkX, linkY, linkZ));
        if (te != null && te instanceof TileMirrorEssentia) {
            final boolean b = EssentiaHandler.drainEssentia(te, tag, linkedFacing, 8, true, 5);
            if (b) {
                addInstability(null, amount);
            }
            return b;
        }
        return false;
    }
    
    public boolean takeFromContainer(final AspectList ot) {
        return false;
    }
    
    public boolean doesContainerContainAmount(final Aspect tag, final int amount) {
        if (!isLinkValid() || amount > 1) {
            return false;
        }
        final World targetWorld = DimensionManager.getWorld(linkDim);
        if (linkedFacing == EnumFacing.DOWN && targetWorld != null) {
            linkedFacing = BlockStateUtils.getFacing(targetWorld.getBlockState(new BlockPos(linkX, linkY, linkZ)));
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(linkX, linkY, linkZ));
        return te != null && te instanceof TileMirrorEssentia && EssentiaHandler.findEssentia(te, tag, linkedFacing, 8, true);
    }
    
    public boolean doesContainerContain(final AspectList ot) {
        return false;
    }
    
    public int containerContains(final Aspect tag) {
        return 0;
    }
    
    public void update() {
        if (!world.isRemote) {
            checkInstability();
            if (count++ % inc == 0) {
                if (!isLinkValidSimple()) {
                    if (inc < 600) {
                        inc += 20;
                    }
                    restoreLink();
                }
                else {
                    inc = 40;
                }
            }
        }
    }
    
    public void checkInstability() {
        if (instability > 64) {
            AuraHelper.polluteAura(world, pos, 1.0f, true);
            instability -= 64;
            markDirty();
        }
        if (instability > 0 && count % 100 == 0) {
            --instability;
        }
    }
    
    @Override
    public boolean isBlocked() {
        return false;
    }
}
