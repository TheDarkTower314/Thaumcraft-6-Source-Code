// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.ai;

import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackRanged;

public class AIArrowAttack extends EntityAIAttackRanged
{
    private final EntityLiving entityHost;
    private final IRangedAttackMob rangedAttackEntityHost;
    private int rangedAttackTime;
    private double entityMoveSpeed;
    private int seeTime;
    private int attackIntervalMin;
    private int maxRangedAttackTime;
    private float attackRadius;
    private float maxAttackDistance;
    
    public AIArrowAttack(final IRangedAttackMob attacker, final double movespeed, final int p_i1650_4_, final int maxAttackTime, final float maxAttackDistanceIn) {
        super(attacker, movespeed, p_i1650_4_, maxAttackTime, maxAttackDistanceIn);
        this.rangedAttackTime = -1;
        if (!(attacker instanceof EntityLivingBase)) {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        this.rangedAttackEntityHost = attacker;
        this.entityHost = (EntityLiving)attacker;
        this.entityMoveSpeed = movespeed;
        this.attackIntervalMin = p_i1650_4_;
        this.maxRangedAttackTime = maxAttackTime;
        this.attackRadius = maxAttackDistanceIn;
        this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
        this.setMutexBits(3);
    }
    
    public boolean shouldExecute() {
        return this.entityHost.getAttackTarget() != null;
    }
    
    public boolean shouldContinueExecuting() {
        return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
    }
    
    public void resetTask() {
        this.seeTime = 0;
        this.rangedAttackTime = -1;
    }
    
    public void updateTask() {
        if (this.entityHost.getAttackTarget() == null) {
            return;
        }
        final double d0 = this.entityHost.getDistanceSq(this.entityHost.getAttackTarget().posX, this.entityHost.getAttackTarget().getEntityBoundingBox().minY, this.entityHost.getAttackTarget().posZ);
        final boolean flag = this.entityHost.getEntitySenses().canSee(this.entityHost.getAttackTarget());
        if (flag) {
            ++this.seeTime;
        }
        else {
            this.seeTime = 0;
        }
        if (d0 <= this.maxAttackDistance && this.seeTime >= 20) {
            this.entityHost.getNavigator().clearPath();
        }
        else {
            this.entityHost.getNavigator().tryMoveToEntityLiving(this.entityHost.getAttackTarget(), this.entityMoveSpeed);
        }
        this.entityHost.getLookHelper().setLookPositionWithEntity(this.entityHost.getAttackTarget(), 10.0f, 30.0f);
        final int rangedAttackTime = this.rangedAttackTime - 1;
        this.rangedAttackTime = rangedAttackTime;
        if (rangedAttackTime == 0) {
            if (d0 > this.maxAttackDistance || !flag) {
                return;
            }
            final float f = MathHelper.sqrt(d0) / this.attackRadius;
            final float lvt_5_1_ = MathHelper.clamp(f, 0.1f, 1.0f);
            this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.entityHost.getAttackTarget(), lvt_5_1_);
            this.rangedAttackTime = MathHelper.floor(f * (this.maxRangedAttackTime - this.attackIntervalMin) + this.attackIntervalMin);
        }
        else if (this.rangedAttackTime < 0) {
            final float f2 = MathHelper.sqrt(d0) / this.attackRadius;
            this.rangedAttackTime = MathHelper.floor(f2 * (this.maxRangedAttackTime - this.attackIntervalMin) + this.attackIntervalMin);
        }
    }
}
