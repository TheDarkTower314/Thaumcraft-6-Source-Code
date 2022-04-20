// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import net.minecraft.entity.ai.EntityAITarget;

public class AIOwnerHurtTarget extends EntityAITarget
{
    EntityOwnedConstruct theEntityTameable;
    EntityLivingBase theTarget;
    private int timestamp;
    
    public AIOwnerHurtTarget(final EntityOwnedConstruct p_i1668_1_) {
        super(p_i1668_1_, false);
        this.theEntityTameable = p_i1668_1_;
        this.setMutexBits(1);
    }
    
    public boolean shouldExecute() {
        if (!this.theEntityTameable.isOwned()) {
            return false;
        }
        final EntityLivingBase entitylivingbase = this.theEntityTameable.getOwnerEntity();
        if (entitylivingbase == null) {
            return false;
        }
        this.theTarget = entitylivingbase.getLastAttackedEntity();
        final int i = entitylivingbase.getLastAttackedEntityTime();
        return i != this.timestamp && this.isSuitableTarget(this.theTarget, false);
    }
    
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.theTarget);
        final EntityLivingBase entitylivingbase = this.theEntityTameable.getOwnerEntity();
        if (entitylivingbase != null) {
            this.timestamp = entitylivingbase.getLastAttackedEntityTime();
        }
        super.startExecuting();
    }
}
