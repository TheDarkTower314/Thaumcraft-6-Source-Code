package thaumcraft.common.golems.ai;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;
import thaumcraft.common.golems.EntityThaumcraftGolem;


public class AIGotoHome extends EntityAIBase
{
    protected EntityThaumcraftGolem golem;
    private double movePosX;
    private double movePosY;
    private double movePosZ;
    protected int idleCounter;
    
    public AIGotoHome(EntityThaumcraftGolem g) {
        idleCounter = 10;
        golem = g;
        setMutexBits(5);
    }
    
    public boolean shouldExecute() {
        if (idleCounter > 0) {
            --idleCounter;
            return false;
        }
        idleCounter = 50;
        double dd = golem.getDistanceSqToCenter(golem.getHomePosition());
        if (dd < 5.0) {
            return false;
        }
        if (dd <= 1024.0) {
            movePosX = golem.getHomePosition().getX();
            movePosY = golem.getHomePosition().getY();
            movePosZ = golem.getHomePosition().getZ();
            return true;
        }
        Vec3d vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(golem, 16, 7, new Vec3d(golem.getHomePosition().getX(), golem.getHomePosition().getY(), golem.getHomePosition().getZ()));
        if (vec3 == null) {
            return false;
        }
        movePosX = vec3.x;
        movePosY = vec3.y;
        movePosZ = vec3.z;
        return true;
    }
    
    public void startExecuting() {
        golem.getNavigator().tryMoveToXYZ(movePosX, movePosY, movePosZ, golem.getGolemMoveSpeed());
    }
    
    public boolean shouldContinueExecuting() {
        return golem.getTask() == null && !golem.getNavigator().noPath() && golem.getDistanceSqToCenter(golem.getHomePosition()) > 3.0;
    }
    
    public void resetTask() {
        idleCounter = 50;
        golem.getNavigator().clearPath();
    }
}
