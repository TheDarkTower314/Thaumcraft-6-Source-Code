// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.ai;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.BlockFence;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockRailBase;
import java.util.EnumSet;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.block.Block;
import com.google.common.collect.Sets;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.IBlockAccess;
import net.minecraft.pathfinding.NodeProcessor;

public class GolemNodeProcessor extends NodeProcessor
{
    private float avoidsWater;
    
    public void init(final IBlockAccess sourceIn, final EntityLiving mob) {
        super.init(sourceIn, mob);
        avoidsWater = mob.getPathPriority(PathNodeType.WATER);
    }
    
    public void postProcess() {
        entity.setPathPriority(PathNodeType.WATER, avoidsWater);
        super.postProcess();
    }
    
    public PathPoint getStart() {
        int i;
        if (getCanSwim() && entity.isInWater()) {
            i = (int) entity.getEntityBoundingBox().minY;
            final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(MathHelper.floor(entity.posX), i, MathHelper.floor(entity.posZ));
            for (Block block = blockaccess.getBlockState(blockpos$mutableblockpos).getBlock(); block == Blocks.FLOWING_WATER || block == Blocks.WATER; block = blockaccess.getBlockState(blockpos$mutableblockpos).getBlock()) {
                ++i;
                blockpos$mutableblockpos.setPos(MathHelper.floor(entity.posX), i, MathHelper.floor(entity.posZ));
            }
        }
        else if (entity.onGround) {
            i = MathHelper.floor(entity.getEntityBoundingBox().minY + 0.5);
        }
        else {
            BlockPos blockpos;
            for (blockpos = new BlockPos(entity); (blockaccess.getBlockState(blockpos).getMaterial() == Material.AIR || blockaccess.getBlockState(blockpos).getBlock().isPassable(blockaccess, blockpos)) && blockpos.getY() > 0; blockpos = blockpos.down()) {}
            i = blockpos.up().getY();
        }
        final BlockPos blockpos2 = new BlockPos(entity);
        final PathNodeType pathnodetype1 = getPathNodeType(entity, blockpos2.getX(), i, blockpos2.getZ());
        if (entity.getPathPriority(pathnodetype1) < 0.0f) {
            final Set<BlockPos> set = Sets.newHashSet();
            set.add(new BlockPos(entity.getEntityBoundingBox().minX, i, entity.getEntityBoundingBox().minZ));
            set.add(new BlockPos(entity.getEntityBoundingBox().minX, i, entity.getEntityBoundingBox().maxZ));
            set.add(new BlockPos(entity.getEntityBoundingBox().maxX, i, entity.getEntityBoundingBox().minZ));
            set.add(new BlockPos(entity.getEntityBoundingBox().maxX, i, entity.getEntityBoundingBox().maxZ));
            for (final BlockPos blockpos3 : set) {
                final PathNodeType pathnodetype2 = getPathNodeType(entity, blockpos3);
                if (entity.getPathPriority(pathnodetype2) >= 0.0f) {
                    return openPoint(blockpos3.getX(), blockpos3.getY(), blockpos3.getZ());
                }
            }
        }
        return openPoint(blockpos2.getX(), i, blockpos2.getZ());
    }
    
    public PathPoint getPathPointToCoords(final double x, final double y, final double z) {
        return openPoint(MathHelper.floor(x - entity.width / 2.0f), MathHelper.floor(y), MathHelper.floor(z - entity.width / 2.0f));
    }
    
    public int findPathOptions(final PathPoint[] pathOptions, final PathPoint currentPoint, final PathPoint targetPoint, final float maxDistance) {
        int i = 0;
        int j = 0;
        final PathNodeType pathnodetype = getPathNodeType(entity, currentPoint.x, currentPoint.y + 1, currentPoint.z);
        if (entity.getPathPriority(pathnodetype) >= 0.0f) {
            j = MathHelper.floor(Math.max(1.0f, entity.stepHeight));
        }
        final BlockPos blockpos = new BlockPos(currentPoint.x, currentPoint.y, currentPoint.z).down();
        final double d0 = currentPoint.y - (1.0 - blockaccess.getBlockState(blockpos).getBoundingBox(blockaccess, blockpos).maxY);
        final PathPoint pathpoint = getSafePoint(currentPoint.x, currentPoint.y, currentPoint.z + 1, j, d0, EnumFacing.SOUTH);
        final PathPoint pathpoint2 = getSafePoint(currentPoint.x - 1, currentPoint.y, currentPoint.z, j, d0, EnumFacing.WEST);
        final PathPoint pathpoint3 = getSafePoint(currentPoint.x + 1, currentPoint.y, currentPoint.z, j, d0, EnumFacing.EAST);
        final PathPoint pathpoint4 = getSafePoint(currentPoint.x, currentPoint.y, currentPoint.z - 1, j, d0, EnumFacing.NORTH);
        if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint;
        }
        if (pathpoint2 != null && !pathpoint2.visited && pathpoint2.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint2;
        }
        if (pathpoint3 != null && !pathpoint3.visited && pathpoint3.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint3;
        }
        if (pathpoint4 != null && !pathpoint4.visited && pathpoint4.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint4;
        }
        final boolean flag = pathpoint4 == null || pathpoint4.nodeType == PathNodeType.OPEN || pathpoint4.costMalus != 0.0f;
        final boolean flag2 = pathpoint == null || pathpoint.nodeType == PathNodeType.OPEN || pathpoint.costMalus != 0.0f;
        final boolean flag3 = pathpoint3 == null || pathpoint3.nodeType == PathNodeType.OPEN || pathpoint3.costMalus != 0.0f;
        final boolean flag4 = pathpoint2 == null || pathpoint2.nodeType == PathNodeType.OPEN || pathpoint2.costMalus != 0.0f;
        if (flag && flag4) {
            final PathPoint pathpoint5 = getSafePoint(currentPoint.x - 1, currentPoint.y, currentPoint.z - 1, j, d0, EnumFacing.NORTH);
            if (pathpoint5 != null && !pathpoint5.visited && pathpoint5.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint5;
            }
        }
        if (flag && flag3) {
            final PathPoint pathpoint6 = getSafePoint(currentPoint.x + 1, currentPoint.y, currentPoint.z - 1, j, d0, EnumFacing.NORTH);
            if (pathpoint6 != null && !pathpoint6.visited && pathpoint6.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint6;
            }
        }
        if (flag2 && flag4) {
            final PathPoint pathpoint7 = getSafePoint(currentPoint.x - 1, currentPoint.y, currentPoint.z + 1, j, d0, EnumFacing.SOUTH);
            if (pathpoint7 != null && !pathpoint7.visited && pathpoint7.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint7;
            }
        }
        if (flag2 && flag3) {
            final PathPoint pathpoint8 = getSafePoint(currentPoint.x + 1, currentPoint.y, currentPoint.z + 1, j, d0, EnumFacing.SOUTH);
            if (pathpoint8 != null && !pathpoint8.visited && pathpoint8.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint8;
            }
        }
        return i;
    }
    
    private PathPoint getSafePoint(final int x, int y, final int z, final int p_186332_4_, final double p_186332_5_, final EnumFacing facing) {
        PathPoint pathpoint = null;
        final BlockPos blockpos = new BlockPos(x, y, z);
        final BlockPos blockpos2 = blockpos.down();
        final double d0 = y - (1.0 - blockaccess.getBlockState(blockpos2).getBoundingBox(blockaccess, blockpos2).maxY);
        if (d0 - p_186332_5_ > 1.125) {
            return null;
        }
        PathNodeType pathnodetype = getPathNodeType(entity, x, y, z);
        float f = entity.getPathPriority(pathnodetype);
        final double d2 = entity.width / 2.0;
        if (f >= 0.0f) {
            pathpoint = openPoint(x, y, z);
            pathpoint.nodeType = pathnodetype;
            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
        }
        if (pathnodetype == PathNodeType.WALKABLE) {
            return pathpoint;
        }
        if (pathpoint == null && p_186332_4_ > 0 && pathnodetype != PathNodeType.FENCE && pathnodetype != PathNodeType.TRAPDOOR) {
            pathpoint = getSafePoint(x, y + 1, z, p_186332_4_ - 1, p_186332_5_, facing);
            if (pathpoint != null && (pathpoint.nodeType == PathNodeType.OPEN || pathpoint.nodeType == PathNodeType.WALKABLE) && entity.width < 1.0f) {
                final double d3 = x - facing.getFrontOffsetX() + 0.5;
                final double d4 = z - facing.getFrontOffsetZ() + 0.5;
                final AxisAlignedBB axisalignedbb = new AxisAlignedBB(d3 - d2, y + 0.001, d4 - d2, d3 + d2, y + entity.height, d4 + d2);
                final AxisAlignedBB axisalignedbb2 = blockaccess.getBlockState(blockpos).getBoundingBox(blockaccess, blockpos);
                final AxisAlignedBB axisalignedbb3 = axisalignedbb.expand(0.0, axisalignedbb2.maxY - 0.002, 0.0);
                if (entity.world.collidesWithAnyBlock(axisalignedbb3)) {
                    pathpoint = null;
                }
            }
        }
        if (pathnodetype == PathNodeType.OPEN) {
            final AxisAlignedBB axisalignedbb4 = new AxisAlignedBB(x - d2 + 0.5, y + 0.001, z - d2 + 0.5, x + d2 + 0.5, y + entity.height, z + d2 + 0.5);
            if (entity.world.collidesWithAnyBlock(axisalignedbb4)) {
                return null;
            }
            if (entity.width >= 1.0f) {
                final PathNodeType pathnodetype2 = getPathNodeType(entity, x, y - 1, z);
                if (pathnodetype2 == PathNodeType.BLOCKED) {
                    pathpoint = openPoint(x, y, z);
                    pathpoint.nodeType = PathNodeType.WALKABLE;
                    pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                    return pathpoint;
                }
            }
            int i = 0;
            while (y > 0 && pathnodetype == PathNodeType.OPEN) {
                --y;
                if (i++ >= entity.getMaxFallHeight()) {
                    return null;
                }
                pathnodetype = getPathNodeType(entity, x, y, z);
                f = entity.getPathPriority(pathnodetype);
                if (pathnodetype != PathNodeType.OPEN && f >= 0.0f) {
                    pathpoint = openPoint(x, y, z);
                    pathpoint.nodeType = pathnodetype;
                    pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                    break;
                }
                if (f < 0.0f) {
                    return null;
                }
            }
        }
        return pathpoint;
    }
    
    public PathNodeType getPathNodeType(final IBlockAccess blockaccessIn, final int x, final int y, final int z, final EntityLiving entitylivingIn, final int xSize, final int ySize, final int zSize, final boolean canBreakDoorsIn, final boolean canEnterDoorsIn) {
        final EnumSet<PathNodeType> enumset = EnumSet.noneOf(PathNodeType.class);
        PathNodeType pathnodetype = PathNodeType.BLOCKED;
        final double d0 = entitylivingIn.width / 2.0;
        final BlockPos blockpos = new BlockPos(entitylivingIn);
        for (int i = 0; i < xSize; ++i) {
            for (int j = 0; j < ySize; ++j) {
                for (int k = 0; k < zSize; ++k) {
                    final int l = i + x;
                    final int i2 = j + y;
                    final int j2 = k + z;
                    PathNodeType pathnodetype2 = getPathNodeType(blockaccessIn, l, i2, j2);
                    if (pathnodetype2 == PathNodeType.DOOR_WOOD_CLOSED && canBreakDoorsIn && canEnterDoorsIn) {
                        pathnodetype2 = PathNodeType.WALKABLE;
                    }
                    if (pathnodetype2 == PathNodeType.DOOR_OPEN && !canEnterDoorsIn) {
                        pathnodetype2 = PathNodeType.BLOCKED;
                    }
                    if (pathnodetype2 == PathNodeType.RAIL && !(blockaccessIn.getBlockState(blockpos).getBlock() instanceof BlockRailBase) && !(blockaccessIn.getBlockState(blockpos.down()).getBlock() instanceof BlockRailBase)) {
                        pathnodetype2 = PathNodeType.FENCE;
                    }
                    if (i == 0 && j == 0 && k == 0) {
                        pathnodetype = pathnodetype2;
                    }
                    enumset.add(pathnodetype2);
                }
            }
        }
        if (enumset.contains(PathNodeType.FENCE)) {
            return PathNodeType.FENCE;
        }
        PathNodeType pathnodetype3 = PathNodeType.BLOCKED;
        for (final PathNodeType pathnodetype4 : enumset) {
            if (entitylivingIn.getPathPriority(pathnodetype4) < 0.0f) {
                return pathnodetype4;
            }
            if (entitylivingIn.getPathPriority(pathnodetype4) < entitylivingIn.getPathPriority(pathnodetype3)) {
                continue;
            }
            pathnodetype3 = pathnodetype4;
        }
        if (pathnodetype == PathNodeType.OPEN && entitylivingIn.getPathPriority(pathnodetype3) == 0.0f) {
            return PathNodeType.OPEN;
        }
        return pathnodetype3;
    }
    
    private PathNodeType getPathNodeType(final EntityLiving entitylivingIn, final BlockPos pos) {
        return getPathNodeType(entitylivingIn, pos.getX(), pos.getY(), pos.getZ());
    }
    
    private PathNodeType getPathNodeType(final EntityLiving entitylivingIn, final int x, final int y, final int z) {
        return getPathNodeType(blockaccess, x, y, z, entitylivingIn, entitySizeX, entitySizeY, entitySizeZ, false, getCanEnterDoors());
    }
    
    public PathNodeType getPathNodeType(final IBlockAccess blockaccessIn, final int x, final int y, final int z) {
        PathNodeType pathnodetype = getPathNodeTypeRaw(blockaccessIn, x, y, z);
        if (pathnodetype == PathNodeType.OPEN && y >= 1) {
            final Block block = blockaccessIn.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
            final PathNodeType pathnodetype2 = getPathNodeTypeRaw(blockaccessIn, x, y - 1, z);
            pathnodetype = ((pathnodetype2 != PathNodeType.WALKABLE && pathnodetype2 != PathNodeType.OPEN && pathnodetype2 != PathNodeType.WATER && pathnodetype2 != PathNodeType.LAVA) ? PathNodeType.WALKABLE : PathNodeType.OPEN);
            if (pathnodetype2 == PathNodeType.DAMAGE_FIRE || block == Blocks.MAGMA) {
                pathnodetype = PathNodeType.DAMAGE_FIRE;
            }
            if (pathnodetype2 == PathNodeType.DAMAGE_CACTUS) {
                pathnodetype = PathNodeType.DAMAGE_CACTUS;
            }
        }
        final BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
        if (pathnodetype == PathNodeType.WALKABLE) {
            for (int j = -1; j <= 1; ++j) {
                for (int i = -1; i <= 1; ++i) {
                    if (j != 0 || i != 0) {
                        final Block block2 = blockaccessIn.getBlockState(blockpos$pooledmutableblockpos.setPos(j + x, y, i + z)).getBlock();
                        if (block2 == Blocks.CACTUS) {
                            pathnodetype = PathNodeType.DANGER_CACTUS;
                        }
                        else if (block2 == Blocks.FIRE) {
                            pathnodetype = PathNodeType.DANGER_FIRE;
                        }
                    }
                }
            }
        }
        blockpos$pooledmutableblockpos.release();
        return pathnodetype;
    }
    
    private PathNodeType getPathNodeTypeRaw(final IBlockAccess p_189553_1_, final int p_189553_2_, final int p_189553_3_, final int p_189553_4_) {
        final BlockPos blockpos = new BlockPos(p_189553_2_, p_189553_3_, p_189553_4_);
        final IBlockState iblockstate = p_189553_1_.getBlockState(blockpos);
        final Block block = iblockstate.getBlock();
        final Material material = iblockstate.getMaterial();
        return (material == Material.AIR) ? PathNodeType.OPEN : ((block != Blocks.TRAPDOOR && block != Blocks.IRON_TRAPDOOR && block != Blocks.WATERLILY) ? ((block == Blocks.FIRE) ? PathNodeType.DAMAGE_FIRE : ((block == Blocks.CACTUS) ? PathNodeType.DAMAGE_CACTUS : ((block instanceof BlockDoor && material == Material.WOOD && !(boolean)iblockstate.getValue((IProperty)BlockDoor.OPEN)) ? PathNodeType.DOOR_WOOD_CLOSED : ((block instanceof BlockDoor && material == Material.IRON && !(boolean)iblockstate.getValue((IProperty)BlockDoor.OPEN)) ? PathNodeType.DOOR_IRON_CLOSED : ((block instanceof BlockDoor && (boolean)iblockstate.getValue((IProperty)BlockDoor.OPEN)) ? PathNodeType.DOOR_OPEN : ((block instanceof BlockRailBase) ? PathNodeType.RAIL : ((!(block instanceof BlockFence) && !(block instanceof BlockWall) && (!(block instanceof BlockFenceGate) || (boolean)iblockstate.getValue((IProperty)BlockFenceGate.OPEN))) ? ((material == Material.WATER) ? PathNodeType.WATER : ((material == Material.LAVA) ? PathNodeType.LAVA : (block.isPassable(p_189553_1_, blockpos) ? PathNodeType.OPEN : PathNodeType.BLOCKED))) : PathNodeType.FENCE))))))) : PathNodeType.TRAPDOOR);
    }
}
