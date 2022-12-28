package thaumcraft.common.entities.ai.combat;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackRanged;


public class AILongRangeAttack extends EntityAIAttackRanged
{
    private EntityLiving wielder;
    double minDistance;
    
    public AILongRangeAttack(IRangedAttackMob par1IRangedAttackMob, double min, double p_i1650_2_, int p_i1650_4_, int p_i1650_5_, float p_i1650_6_) {
        super(par1IRangedAttackMob, p_i1650_2_, p_i1650_4_, p_i1650_5_, p_i1650_6_);
        minDistance = 0.0;
        minDistance = min;
        wielder = (EntityLiving)par1IRangedAttackMob;
    }
    
    public boolean shouldExecute() {
        boolean ex = super.shouldExecute();
        if (ex) {
            EntityLivingBase var1 = wielder.getAttackTarget();
            if (var1 == null) {
                return false;
            }
            if (var1.isDead) {
                wielder.setAttackTarget(null);
                return false;
            }
            double ra = wielder.getDistanceSq(var1.posX, var1.getEntityBoundingBox().minY, var1.posZ);
            if (ra < minDistance * minDistance) {
                return false;
            }
        }
        return ex;
    }
}
