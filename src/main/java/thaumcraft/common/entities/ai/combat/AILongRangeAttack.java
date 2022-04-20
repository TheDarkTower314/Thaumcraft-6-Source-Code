// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.ai.combat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackRanged;

public class AILongRangeAttack extends EntityAIAttackRanged
{
    private final EntityLiving wielder;
    double minDistance;
    
    public AILongRangeAttack(final IRangedAttackMob par1IRangedAttackMob, final double min, final double p_i1650_2_, final int p_i1650_4_, final int p_i1650_5_, final float p_i1650_6_) {
        super(par1IRangedAttackMob, p_i1650_2_, p_i1650_4_, p_i1650_5_, p_i1650_6_);
        this.minDistance = 0.0;
        this.minDistance = min;
        this.wielder = (EntityLiving)par1IRangedAttackMob;
    }
    
    public boolean shouldExecute() {
        final boolean ex = super.shouldExecute();
        if (ex) {
            final EntityLivingBase var1 = this.wielder.getAttackTarget();
            if (var1 == null) {
                return false;
            }
            if (var1.isDead) {
                this.wielder.setAttackTarget(null);
                return false;
            }
            final double ra = this.wielder.getDistanceSq(var1.posX, var1.getEntityBoundingBox().minY, var1.posZ);
            if (ra < this.minDistance * this.minDistance) {
                return false;
            }
        }
        return ex;
    }
}
