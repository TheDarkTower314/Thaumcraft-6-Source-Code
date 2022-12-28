package thaumcraft.common.golems.ai;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;


public class AIOwnerHurtTarget extends EntityAITarget
{
    EntityOwnedConstruct theEntityTameable;
    EntityLivingBase theTarget;
    private int timestamp;
    
    public AIOwnerHurtTarget(EntityOwnedConstruct p_i1668_1_) {
        super(p_i1668_1_, false);
        theEntityTameable = p_i1668_1_;
        setMutexBits(1);
    }
    
    public boolean shouldExecute() {
        if (!theEntityTameable.isOwned()) {
            return false;
        }
        EntityLivingBase entitylivingbase = theEntityTameable.getOwnerEntity();
        if (entitylivingbase == null) {
            return false;
        }
        theTarget = entitylivingbase.getLastAttackedEntity();
        int i = entitylivingbase.getLastAttackedEntityTime();
        return i != timestamp && isSuitableTarget(theTarget, false);
    }
    
    public void startExecuting() {
        taskOwner.setAttackTarget(theTarget);
        EntityLivingBase entitylivingbase = theEntityTameable.getOwnerEntity();
        if (entitylivingbase != null) {
            timestamp = entitylivingbase.getLastAttackedEntityTime();
        }
        super.startExecuting();
    }
}
