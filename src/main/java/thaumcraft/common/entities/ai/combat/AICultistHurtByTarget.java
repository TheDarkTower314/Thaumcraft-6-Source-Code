// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.ai.combat;

import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITarget;

public class AICultistHurtByTarget extends EntityAITarget
{
    boolean entityCallsForHelp;
    private int revengeTimerOld;
    
    public AICultistHurtByTarget(final EntityCreature owner, final boolean callsHelp) {
        super(owner, false);
        entityCallsForHelp = callsHelp;
        setMutexBits(1);
    }
    
    public boolean shouldExecute() {
        final int i = taskOwner.getRevengeTimer();
        final EntityLivingBase entitylivingbase = taskOwner.getRevengeTarget();
        return i != revengeTimerOld && entitylivingbase != null && isSuitableTarget(entitylivingbase, false);
    }
    
    public void startExecuting() {
        taskOwner.setAttackTarget(taskOwner.getRevengeTarget());
        target = taskOwner.getAttackTarget();
        revengeTimerOld = taskOwner.getRevengeTimer();
        unseenMemoryTicks = 300;
        if (entityCallsForHelp) {
            alertOthers();
        }
        super.startExecuting();
    }
    
    protected void alertOthers() {
        final double d0 = getTargetDistance();
        for (final EntityCreature entitycreature : taskOwner.world.getEntitiesWithinAABB(EntityCultist.class, new AxisAlignedBB(taskOwner.posX, taskOwner.posY, taskOwner.posZ, taskOwner.posX + 1.0, taskOwner.posY + 1.0, taskOwner.posZ + 1.0).grow(d0, 10.0, d0))) {
            if (taskOwner != entitycreature && entitycreature.getAttackTarget() == null && (!(taskOwner instanceof EntityTameable) || ((EntityTameable) taskOwner).getOwner() == ((EntityTameable)entitycreature).getOwner()) && !entitycreature.isOnSameTeam(taskOwner.getRevengeTarget())) {
                setEntityAttackTarget(entitycreature, taskOwner.getRevengeTarget());
            }
        }
    }
    
    protected void setEntityAttackTarget(final EntityCreature creatureIn, final EntityLivingBase entityLivingBaseIn) {
        creatureIn.setAttackTarget(entityLivingBaseIn);
    }
}
