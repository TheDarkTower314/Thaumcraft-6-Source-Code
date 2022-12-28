package thaumcraft.common.golems.ai;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;


public class FlightNodeProcessor extends NodeProcessor
{
    public PathPoint getPathPointToCoords(double x, double y, double z) {
        return openPoint(MathHelper.floor(x - entity.width / 2.0f), MathHelper.floor(y + 0.5), MathHelper.floor(z - entity.width / 2.0f));
    }
    
    public PathPoint getStart() {
        return openPoint(MathHelper.floor(entity.getEntityBoundingBox().minX), MathHelper.floor(entity.getEntityBoundingBox().minY + 0.5), MathHelper.floor(entity.getEntityBoundingBox().minZ));
    }
    
    public int findPathOptions(PathPoint[] p_186320_1_, PathPoint p_186320_2_, PathPoint p_186320_3_, float p_186320_4_) {
        int i = 0;
        for (EnumFacing enumfacing : EnumFacing.values()) {
            PathPoint pathpoint = getWaterNode(p_186320_2_.x + enumfacing.getFrontOffsetX(), p_186320_2_.y + enumfacing.getFrontOffsetY(), p_186320_2_.z + enumfacing.getFrontOffsetZ());
            if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(p_186320_3_) < p_186320_4_) {
                p_186320_1_[i++] = pathpoint;
            }
        }
        return i;
    }
    
    public PathNodeType getPathNodeType(IBlockAccess p_186319_1_, int p_186319_2_, int p_186319_3_, int p_186319_4_, EntityLiving p_186319_5_, int p_186319_6_, int p_186319_7_, int p_186319_8_, boolean p_186319_9_, boolean p_186319_10_) {
        return PathNodeType.WATER;
    }
    
    public PathNodeType getPathNodeType(IBlockAccess x, int y, int z, int p_186330_4_) {
        return PathNodeType.WATER;
    }
    
    private PathPoint getWaterNode(int p_186328_1_, int p_186328_2_, int p_186328_3_) {
        PathNodeType pathnodetype = isFree(p_186328_1_, p_186328_2_, p_186328_3_);
        return (pathnodetype == PathNodeType.WALKABLE) ? openPoint(p_186328_1_, p_186328_2_, p_186328_3_) : null;
    }
    
    private PathNodeType isFree(int p_186327_1_, int p_186327_2_, int p_186327_3_) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        for (int i = p_186327_1_; i < p_186327_1_ + entitySizeX; ++i) {
            for (int j = p_186327_2_; j < p_186327_2_ + entitySizeY; ++j) {
                for (int k = p_186327_3_; k < p_186327_3_ + entitySizeZ; ++k) {
                    IBlockState iblockstate = blockaccess.getBlockState(blockpos$mutableblockpos.setPos(i, j, k));
                    if (!blockaccess.isAirBlock(blockpos$mutableblockpos.setPos(i, j, k)) && !iblockstate.getBlock().isPassable(blockaccess, blockpos$mutableblockpos.setPos(i, j, k))) {
                        return PathNodeType.BLOCKED;
                    }
                }
            }
        }
        return PathNodeType.WALKABLE;
    }
}
