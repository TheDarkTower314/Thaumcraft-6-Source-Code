// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.misc;

import thaumcraft.common.blocks.basic.BlockBannerTC;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileBanner extends TileThaumcraft
{
    private byte facing;
    private Aspect aspect;
    private boolean onWall;
    
    public TileBanner() {
        this.facing = 0;
        this.aspect = null;
        this.onWall = false;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.getPos().getX(), this.getPos().getY() - 1, this.getPos().getZ(), this.getPos().getX() + 1, this.getPos().getY() + 2, this.getPos().getZ() + 1);
    }
    
    public byte getBannerFacing() {
        return this.facing;
    }
    
    public void setBannerFacing(final byte face) {
        this.facing = face;
        this.markDirty();
    }
    
    public boolean getWall() {
        return this.onWall;
    }
    
    public void setWall(final boolean b) {
        this.onWall = b;
        this.markDirty();
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        this.facing = nbttagcompound.getByte("facing");
        final String as = nbttagcompound.getString("aspect");
        if (as != null && as.length() > 0) {
            this.setAspect(Aspect.getAspect(as));
        }
        else {
            this.aspect = null;
        }
        this.onWall = nbttagcompound.getBoolean("wall");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("facing", this.facing);
        nbttagcompound.setString("aspect", (this.getAspect() == null) ? "" : this.getAspect().getTag());
        nbttagcompound.setBoolean("wall", this.onWall);
        return nbttagcompound;
    }
    
    public Aspect getAspect() {
        return this.aspect;
    }
    
    public void setAspect(final Aspect aspect) {
        this.aspect = aspect;
    }
    
    @SideOnly(Side.CLIENT)
    public int getColor() {
        return (this.getBlockType() == null || !(this.getBlockType() instanceof BlockBannerTC) || ((BlockBannerTC)this.getBlockType()).dye == null) ? -1 : ((BlockBannerTC)this.getBlockType()).dye.getColorValue();
    }
}
