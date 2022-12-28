package thaumcraft.common.tiles.misc;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blocks.basic.BlockBannerTC;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileBanner extends TileThaumcraft
{
    private byte facing;
    private Aspect aspect;
    private boolean onWall;
    
    public TileBanner() {
        facing = 0;
        aspect = null;
        onWall = false;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().getX(), getPos().getY() - 1, getPos().getZ(), getPos().getX() + 1, getPos().getY() + 2, getPos().getZ() + 1);
    }
    
    public byte getBannerFacing() {
        return facing;
    }
    
    public void setBannerFacing(byte face) {
        facing = face;
        markDirty();
    }
    
    public boolean getWall() {
        return onWall;
    }
    
    public void setWall(boolean b) {
        onWall = b;
        markDirty();
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbttagcompound) {
        facing = nbttagcompound.getByte("facing");
        String as = nbttagcompound.getString("aspect");
        if (as != null && as.length() > 0) {
            setAspect(Aspect.getAspect(as));
        }
        else {
            aspect = null;
        }
        onWall = nbttagcompound.getBoolean("wall");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("facing", facing);
        nbttagcompound.setString("aspect", (getAspect() == null) ? "" : getAspect().getTag());
        nbttagcompound.setBoolean("wall", onWall);
        return nbttagcompound;
    }
    
    public Aspect getAspect() {
        return aspect;
    }
    
    public void setAspect(Aspect aspect) {
        this.aspect = aspect;
    }
    
    @SideOnly(Side.CLIENT)
    public int getColor() {
        return (getBlockType() == null || !(getBlockType() instanceof BlockBannerTC) || ((BlockBannerTC) getBlockType()).dye == null) ? -1 : ((BlockBannerTC) getBlockType()).dye.getColorValue();
    }
}
