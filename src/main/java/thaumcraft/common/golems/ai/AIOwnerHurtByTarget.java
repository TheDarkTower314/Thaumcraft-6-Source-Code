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
    
    public AIOwnerHurtByTarget(EntityOwnedConstruct p_i1667_1_) {
        super(p_i1667_1_, false);
        theDefendingTameable = p_i1667_1_;
        setMutexBits(1);
    }
    
    public boolean shouldExecute() {
        if (!theDefendingTameable.isOwned()) {
            return false;
        }
        EntityLivingBase entitylivingbase = theDefendingTameable.getOwnerEntity();
        if (entitylivingbase == null) {
            return false;
        }
        theOwnerAttacker = entitylivingbase.getRevengeTarget();
        int i = entitylivingbase.getRevengeTimer();
        return i != timestamp && isSuitableTarget(theOwnerAttacker, false);
    }
    
    public void startExecuting() {
        taskOwner.setAttackTarget(theOwnerAttacker);
        EntityLivingBase entitylivingbase = theDefendingTameable.getOwnerEntity();
        if (entitylivingbase != null) {
            timestamp = entitylivingbase.getRevengeTimer();
        }
        super.startExecuting();
    }
}
