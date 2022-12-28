package thaumcraft.common.golems.ai;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


public class PathNavigateGolemAir extends PathNavigate
{
    public PathNavigateGolemAir(EntityLiving p_i45873_1_, World worldIn) {
        super(p_i45873_1_, worldIn);
    }
    
    protected PathFinder getPathFinder() {
        return new PathFinder(new FlightNodeProcessor());
    }
    
    protected boolean canNavigate() {
        return true;
    }
    
    protected Vec3d getEntityPosition() {
        return new Vec3d(entity.posX, entity.posY + entity.height * 0.5, entity.posZ);
    }
    
    protected void pathFollow() {
        Vec3d vec3 = getEntityPosition();
        float f = entity.width * entity.width;
        byte b0 = 6;
        if (vec3.squareDistanceTo(currentPath.getVectorFromIndex(entity, currentPath.getCurrentPathIndex())) < f) {
            currentPath.incrementPathIndex();
        }
        for (int i = Math.min(currentPath.getCurrentPathIndex() + b0, currentPath.getCurrentPathLength() - 1); i > currentPath.getCurrentPathIndex(); --i) {
            Vec3d vec4 = currentPath.getVectorFromIndex(entity, i);
            if (vec4.squareDistanceTo(vec3) <= 36.0 && isDirectPathBetweenPoints(vec3, vec4, 0, 0, 0)) {
                currentPath.setCurrentPathIndex(i);
                break;
            }
        }
        checkForStuck(vec3);
    }
    
    protected void removeSunnyPath() {
        super.removeSunnyPath();
    }
    
    protected boolean isDirectPathBetweenPoints(Vec3d p_75493_1_, Vec3d p_75493_2_, int p_75493_3_, int p_75493_4_, int p_75493_5_) {
        RayTraceResult RayTraceResult = world.rayTraceBlocks(p_75493_1_, new Vec3d(p_75493_2_.x, p_75493_2_.y + entity.height * 0.5, p_75493_2_.z), false, true, false);
        return RayTraceResult == null || RayTraceResult.typeOfHit == net.minecraft.util.math.RayTraceResult.Type.MISS;
    }
}
