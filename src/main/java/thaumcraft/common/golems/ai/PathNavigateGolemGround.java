// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.ai;

import java.util.Iterator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.block.material.Material;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.world.World;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigateGround;

public class PathNavigateGolemGround extends PathNavigateGround
{
    public PathNavigateGolemGround(final EntityLiving entitylivingIn, final World worldIn) {
        super(entitylivingIn, worldIn);
    }
    
    protected PathFinder getPathFinder() {
        (nodeProcessor = new GolemNodeProcessor()).setCanEnterDoors(true);
        return new PathFinder(nodeProcessor);
    }
    
    protected boolean canNavigate() {
        return entity.onGround || (getCanSwim() && isInLiquid()) || entity.isRiding();
    }
    
    protected Vec3d getEntityPosition() {
        return new Vec3d(entity.posX, getPathablePosY(), entity.posZ);
    }
    
    public Path getPathToPos(BlockPos pos) {
        if (world.getBlockState(pos).getMaterial() == Material.AIR) {
            BlockPos blockpos;
            for (blockpos = pos.down(); blockpos.getY() > 0 && world.getBlockState(blockpos).getMaterial() == Material.AIR; blockpos = blockpos.down()) {}
            if (blockpos.getY() > 0) {
                return super.getPathToPos(blockpos.up());
            }
            while (blockpos.getY() < world.getHeight() && world.getBlockState(blockpos).getMaterial() == Material.AIR) {
                blockpos = blockpos.up();
            }
            pos = blockpos;
        }
        if (!world.getBlockState(pos).getMaterial().isSolid()) {
            return super.getPathToPos(pos);
        }
        BlockPos blockpos2;
        for (blockpos2 = pos.up(); blockpos2.getY() < world.getHeight() && world.getBlockState(blockpos2).getMaterial().isSolid(); blockpos2 = blockpos2.up()) {}
        return super.getPathToPos(blockpos2);
    }
    
    public Path getPathToEntityLiving(final Entity entityIn) {
        final BlockPos blockpos = new BlockPos(entityIn);
        return getPathToPos(blockpos);
    }
    
    private int getPathablePosY() {
        if (entity.isInWater() && getCanSwim()) {
            int i = (int) entity.getEntityBoundingBox().minY;
            Block block = world.getBlockState(new BlockPos(MathHelper.floor(entity.posX), i, MathHelper.floor(entity.posZ))).getBlock();
            int j = 0;
            while (block == Blocks.FLOWING_WATER || block == Blocks.WATER) {
                ++i;
                block = world.getBlockState(new BlockPos(MathHelper.floor(entity.posX), i, MathHelper.floor(entity.posZ))).getBlock();
                if (++j > 16) {
                    return (int) entity.getEntityBoundingBox().minY;
                }
            }
            return i;
        }
        return (int)(entity.getEntityBoundingBox().minY + 0.5);
    }
    
    protected void removeSunnyPath() {
        super.removeSunnyPath();
        for (int i = 0; i < currentPath.getCurrentPathLength(); ++i) {
            final PathPoint pathpoint = currentPath.getPathPointFromIndex(i);
            final PathPoint pathpoint2 = (i + 1 < currentPath.getCurrentPathLength()) ? currentPath.getPathPointFromIndex(i + 1) : null;
            final IBlockState iblockstate = world.getBlockState(new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z));
            final Block block = iblockstate.getBlock();
            if (block == Blocks.CAULDRON) {
                currentPath.setPoint(i, pathpoint.cloneMove(pathpoint.x, pathpoint.y + 1, pathpoint.z));
                if (pathpoint2 != null && pathpoint.y >= pathpoint2.y) {
                    currentPath.setPoint(i + 1, pathpoint2.cloneMove(pathpoint2.x, pathpoint.y + 1, pathpoint2.z));
                }
            }
        }
    }
    
    protected boolean isDirectPathBetweenPoints(final Vec3d posVec31, final Vec3d posVec32, int sizeX, final int sizeY, int sizeZ) {
        int i = MathHelper.floor(posVec31.x);
        int j = MathHelper.floor(posVec31.z);
        double d0 = posVec32.x - posVec31.x;
        double d2 = posVec32.z - posVec31.z;
        final double d3 = d0 * d0 + d2 * d2;
        if (d3 < 1.0E-8) {
            return false;
        }
        final double d4 = 1.0 / Math.sqrt(d3);
        d0 *= d4;
        d2 *= d4;
        sizeX += 2;
        sizeZ += 2;
        if (!isSafeToStandAt(i, (int)posVec31.y, j, sizeX, sizeY, sizeZ, posVec31, d0, d2)) {
            return false;
        }
        sizeX -= 2;
        sizeZ -= 2;
        final double d5 = 1.0 / Math.abs(d0);
        final double d6 = 1.0 / Math.abs(d2);
        double d7 = i - posVec31.x;
        double d8 = j - posVec31.z;
        if (d0 >= 0.0) {
            ++d7;
        }
        if (d2 >= 0.0) {
            ++d8;
        }
        d7 /= d0;
        d8 /= d2;
        final int k = (d0 < 0.0) ? -1 : 1;
        final int l = (d2 < 0.0) ? -1 : 1;
        final int i2 = MathHelper.floor(posVec32.x);
        final int j2 = MathHelper.floor(posVec32.z);
        int k2 = i2 - i;
        int l2 = j2 - j;
        while (k2 * k > 0 || l2 * l > 0) {
            if (d7 < d8) {
                d7 += d5;
                i += k;
                k2 = i2 - i;
            }
            else {
                d8 += d6;
                j += l;
                l2 = j2 - j;
            }
            if (!isSafeToStandAt(i, (int)posVec31.y, j, sizeX, sizeY, sizeZ, posVec31, d0, d2)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isSafeToStandAt(final int x, final int y, final int z, final int sizeX, final int sizeY, final int sizeZ, final Vec3d vec31, final double p_179683_8_, final double p_179683_10_) {
        final int i = x - sizeX / 2;
        final int j = z - sizeZ / 2;
        if (!isPositionClear(i, y, j, sizeX, sizeY, sizeZ, vec31, p_179683_8_, p_179683_10_)) {
            return false;
        }
        for (int k = i; k < i + sizeX; ++k) {
            for (int l = j; l < j + sizeZ; ++l) {
                final double d0 = k + 0.5 - vec31.x;
                final double d2 = l + 0.5 - vec31.z;
                if (d0 * p_179683_8_ + d2 * p_179683_10_ >= 0.0) {
                    PathNodeType pathnodetype = nodeProcessor.getPathNodeType(world, k, y - 1, l, entity, sizeX, sizeY, sizeZ, true, true);
                    if (pathnodetype == PathNodeType.WATER) {
                        return false;
                    }
                    if (pathnodetype == PathNodeType.LAVA) {
                        return false;
                    }
                    if (pathnodetype == PathNodeType.OPEN) {
                        return false;
                    }
                    pathnodetype = nodeProcessor.getPathNodeType(world, k, y, l, entity, sizeX, sizeY, sizeZ, true, true);
                    final float f = entity.getPathPriority(pathnodetype);
                    if (f < 0.0f || f >= 8.0f) {
                        return false;
                    }
                    if (pathnodetype == PathNodeType.DAMAGE_FIRE || pathnodetype == PathNodeType.DANGER_FIRE || pathnodetype == PathNodeType.DAMAGE_OTHER) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private boolean isPositionClear(final int p_179692_1_, final int p_179692_2_, final int p_179692_3_, final int p_179692_4_, final int p_179692_5_, final int p_179692_6_, final Vec3d p_179692_7_, final double p_179692_8_, final double p_179692_10_) {
        for (final BlockPos blockpos : BlockPos.getAllInBox(new BlockPos(p_179692_1_, p_179692_2_, p_179692_3_), new BlockPos(p_179692_1_ + p_179692_4_ - 1, p_179692_2_ + p_179692_5_ - 1, p_179692_3_ + p_179692_6_ - 1))) {
            final double d0 = blockpos.getX() + 0.5 - p_179692_7_.x;
            final double d2 = blockpos.getZ() + 0.5 - p_179692_7_.z;
            if (d0 * p_179692_8_ + d2 * p_179692_10_ >= 0.0) {
                final Block block = world.getBlockState(blockpos).getBlock();
                if (!block.isPassable(world, blockpos)) {
                    return false;
                }
                continue;
            }
        }
        return true;
    }
    
    public void setEnterDoors(final boolean enterDoors) {
        nodeProcessor.setCanEnterDoors(enterDoors);
    }
    
    public boolean getEnterDoors() {
        return nodeProcessor.getCanEnterDoors();
    }
    
    public void setCanSwim(final boolean canSwim) {
        nodeProcessor.setCanSwim(canSwim);
    }
    
    public boolean getCanSwim() {
        return nodeProcessor.getCanSwim();
    }
    
    public void setAvoidSun(final boolean avoidSun) {
    }
}
