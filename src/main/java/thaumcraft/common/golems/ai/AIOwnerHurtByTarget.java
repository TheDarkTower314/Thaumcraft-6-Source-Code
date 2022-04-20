// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import net.minecraft.entity.ai.EntityAITarget;

public class AIOwnerHurtByTarget extends EntityAITarget
{
    EntityOwnedConstruct theDefendingTameable;
    EntityLivingBase theOwnerAttacker;
    private int timestamp;
    
    public AIOwnerHurtByTarget(final EntityOwnedConstruct p_i1667_1_) {
        super(p_i1667_1_, false);
        this.theDefendingTameable = p_i1667_1_;
        this.setMutexBits(1);
    }
    
    public boolean shouldExecute() {
        if (!this.theDefendingTameable.isOwned()) {
            return false;
        }
        final EntityLivingBase entitylivingbase = this.theDefendingTameable.getOwnerEntity();
        if (entitylivingbase == null) {
            return false;
        }
        this.theOwnerAttacker = entitylivingbase.getRevengeTarget();
        final int i = entitylivingbase.getRevengeTimer();
        return i != this.timestamp && this.isSuitableTarget(this.theOwnerAttacker, false);
    }
    
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.theOwnerAttacker);
        final EntityLivingBase entitylivingbase = this.theDefendingTameable.getOwnerEntity();
        if (entitylivingbase != null) {
            this.timestamp = entitylivingbase.getRevengeTimer();
        }
        super.startExecuting();
    }
}
