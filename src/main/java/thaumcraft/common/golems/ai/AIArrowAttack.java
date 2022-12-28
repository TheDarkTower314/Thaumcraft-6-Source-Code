package thaumcraft.common.golems.ai;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.util.math.MathHelper;


public class AIArrowAttack extends EntityAIAttackRanged
{
    private EntityLiving entityHost;
    private IRangedAttackMob rangedAttackEntityHost;
    private int rangedAttackTime;
    private double entityMoveSpeed;
    private int seeTime;
    private int attackIntervalMin;
    private int maxRangedAttackTime;
    private float attackRadius;
    private float maxAttackDistance;
    
    public AIArrowAttack(IRangedAttackMob attacker, double movespeed, int p_i1650_4_, int maxAttackTime, float maxAttackDistanceIn) {
        super(attacker, movespeed, p_i1650_4_, maxAttackTime, maxAttackDistanceIn);
        rangedAttackTime = -1;
        if (!(attacker instanceof EntityLivingBase)) {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        rangedAttackEntityHost = attacker;
        entityHost = (EntityLiving)attacker;
        entityMoveSpeed = movespeed;
        attackIntervalMin = p_i1650_4_;
        maxRangedAttackTime = maxAttackTime;
        attackRadius = maxAttackDistanceIn;
        maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
        setMutexBits(3);
    }
    
    public boolean shouldExecute() {
        return entityHost.getAttackTarget() != null;
    }
    
    public boolean shouldContinueExecuting() {
        return shouldExecute() || !entityHost.getNavigator().noPath();
    }
    
    public void resetTask() {
        seeTime = 0;
        rangedAttackTime = -1;
    }
    
    public void updateTask() {
        if (entityHost.getAttackTarget() == null) {
            return;
        }
        double d0 = entityHost.getDistanceSq(entityHost.getAttackTarget().posX, entityHost.getAttackTarget().getEntityBoundingBox().minY, entityHost.getAttackTarget().posZ);
        boolean flag = entityHost.getEntitySenses().canSee(entityHost.getAttackTarget());
        if (flag) {
            ++seeTime;
        }
        else {
            seeTime = 0;
        }
        if (d0 <= maxAttackDistance && seeTime >= 20) {
            entityHost.getNavigator().clearPath();
        }
        else {
            entityHost.getNavigator().tryMoveToEntityLiving(entityHost.getAttackTarget(), entityMoveSpeed);
        }
        entityHost.getLookHelper().setLookPositionWithEntity(entityHost.getAttackTarget(), 10.0f, 30.0f);
        int rangedAttackTime = this.rangedAttackTime - 1;
        this.rangedAttackTime = rangedAttackTime;
        if (rangedAttackTime == 0) {
            if (d0 > maxAttackDistance || !flag) {
                return;
            }
            float f = MathHelper.sqrt(d0) / attackRadius;
            float lvt_5_1_ = MathHelper.clamp(f, 0.1f, 1.0f);
            rangedAttackEntityHost.attackEntityWithRangedAttack(entityHost.getAttackTarget(), lvt_5_1_);
            this.rangedAttackTime = MathHelper.floor(f * (maxRangedAttackTime - attackIntervalMin) + attackIntervalMin);
        }
        else if (this.rangedAttackTime < 0) {
            float f2 = MathHelper.sqrt(d0) / attackRadius;
            this.rangedAttackTime = MathHelper.floor(f2 * (maxRangedAttackTime - attackIntervalMin) + attackIntervalMin);
        }
    }
}
