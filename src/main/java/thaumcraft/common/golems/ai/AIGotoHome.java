// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import net.minecraft.entity.ai.EntityAIBase;

public class AIGotoHome extends EntityAIBase
{
    protected EntityThaumcraftGolem golem;
    private double movePosX;
    private double movePosY;
    private double movePosZ;
    protected int idleCounter;
    
    public AIGotoHome(final EntityThaumcraftGolem g) {
        this.idleCounter = 10;
        this.golem = g;
        this.setMutexBits(5);
    }
    
    public boolean shouldExecute() {
        if (this.idleCounter > 0) {
            --this.idleCounter;
            return false;
        }
        this.idleCounter = 50;
        final double dd = this.golem.getDistanceSqToCenter(this.golem.getHomePosition());
        if (dd < 5.0) {
            return false;
        }
        if (dd <= 1024.0) {
            this.movePosX = this.golem.getHomePosition().getX();
            this.movePosY = this.golem.getHomePosition().getY();
            this.movePosZ = this.golem.getHomePosition().getZ();
            return true;
        }
        final Vec3d vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(this.golem, 16, 7, new Vec3d(this.golem.getHomePosition().getX(), this.golem.getHomePosition().getY(), this.golem.getHomePosition().getZ()));
        if (vec3 == null) {
            return false;
        }
        this.movePosX = vec3.x;
        this.movePosY = vec3.y;
        this.movePosZ = vec3.z;
        return true;
    }
    
    public void startExecuting() {
        this.golem.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.golem.getGolemMoveSpeed());
    }
    
    public boolean shouldContinueExecuting() {
        return this.golem.getTask() == null && !this.golem.getNavigator().noPath() && this.golem.getDistanceSqToCenter(this.golem.getHomePosition()) > 3.0;
    }
    
    public void resetTask() {
        this.idleCounter = 50;
        this.golem.getNavigator().clearPath();
    }
}
