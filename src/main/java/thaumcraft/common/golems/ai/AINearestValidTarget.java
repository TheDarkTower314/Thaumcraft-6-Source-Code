// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.ai;

import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import com.google.common.base.Predicates;
import net.minecraft.util.EntitySelectors;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.EntityLiving;
import net.minecraft.scoreboard.Team;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import com.google.common.base.Predicate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;

public class AINearestValidTarget extends EntityAITarget
{
    protected final Class<EntityLivingBase> targetClass;
    private final int targetChance;
    protected final EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
    protected Predicate targetEntitySelector;
    protected EntityLivingBase targetEntity;
    private int targetUnseenTicks;
    
    public AINearestValidTarget(final EntityCreature p_i45878_1_, final Class p_i45878_2_, final boolean p_i45878_3_) {
        this(p_i45878_1_, p_i45878_2_, p_i45878_3_, false);
    }
    
    public AINearestValidTarget(final EntityCreature p_i45879_1_, final Class p_i45879_2_, final boolean p_i45879_3_, final boolean p_i45879_4_) {
        this(p_i45879_1_, p_i45879_2_, 10, p_i45879_3_, p_i45879_4_, null);
    }

    public AINearestValidTarget(final EntityCreature owner, final Class<EntityLivingBase> targetClass, final int targetChance, final boolean checkSight, final boolean nearbyOnly, final Predicate tselector) {
        super(owner, checkSight, nearbyOnly);
        this.targetClass = targetClass;
        this.targetChance = targetChance;
        theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(owner);
        setMutexBits(1);
        targetEntitySelector = new Predicate<EntityLivingBase>() {
            private static final String __OBFID = "CL_00001621";
            
            public boolean applySelection(final EntityLivingBase entity) {
                if (tselector != null && !tselector.apply(entity)) {
                    return false;
                }
                if (entity instanceof EntityPlayer) {
                    double d0 = AINearestValidTarget.access$000(AINearestValidTarget.this);
                    if (entity.isSneaking()) {
                        d0 *= 0.800000011920929;
                    }
                    if (entity.isInvisible()) {
                        float f = ((EntityPlayer)entity).getArmorVisibility();
                        if (f < 0.1f) {
                            f = 0.1f;
                        }
                        d0 *= 0.7f * f;
                    }
                    if (entity.getDistance(taskOwner) > d0) {
                        return false;
                    }
                }
                return isSuitableTarget(entity, false);
            }
            
            public boolean apply(final EntityLivingBase p_apply_1_) {
                return applySelection(p_apply_1_);
            }
        };
    }
    
    public boolean shouldContinueExecuting() {
        final EntityLivingBase entitylivingbase = taskOwner.getAttackTarget();
        if (entitylivingbase == null) {
            return false;
        }
        if (!entitylivingbase.isEntityAlive()) {
            return false;
        }
        final Team team = taskOwner.getTeam();
        final Team team2 = entitylivingbase.getTeam();
        if (team != null && team2 == team && !((ITargets) taskOwner).getTargetFriendly()) {
            return false;
        }
        if (team != null && team2 != team && ((ITargets) taskOwner).getTargetFriendly()) {
            return false;
        }
        final double d0 = getTargetDistance();
        if (taskOwner.getDistanceSq(entitylivingbase) > d0 * d0) {
            return false;
        }
        if (shouldCheckSight) {
            if (taskOwner.getEntitySenses().canSee(entitylivingbase)) {
                targetUnseenTicks = 0;
            }
            else if (++targetUnseenTicks > 60) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean isSuitableTarget(final EntityLivingBase p_75296_1_, final boolean p_75296_2_) {
        return isGoodTarget(taskOwner, p_75296_1_, p_75296_2_, shouldCheckSight) && taskOwner.isWithinHomeDistanceFromPosition(new BlockPos(p_75296_1_));
    }
    
    private boolean isGoodTarget(final EntityLiving attacker, final EntityLivingBase posTar, final boolean p_179445_2_, final boolean checkSight) {
        if (posTar == null) {
            return false;
        }
        if (posTar == attacker) {
            return false;
        }
        if (!posTar.isEntityAlive()) {
            return false;
        }
        if (!attacker.canAttackClass(posTar.getClass())) {
            return false;
        }
        final Team team = attacker.getTeam();
        final Team team2 = posTar.getTeam();
        if (team != null && team2 == team && !((ITargets)attacker).getTargetFriendly()) {
            return false;
        }
        if (team != null && team2 != team && ((ITargets)attacker).getTargetFriendly()) {
            return false;
        }
        if (attacker instanceof IEntityOwnable && StringUtils.isNotEmpty(((IEntityOwnable)attacker).getOwnerId().toString())) {
            if (posTar instanceof IEntityOwnable && ((IEntityOwnable)attacker).getOwnerId().equals(((IEntityOwnable)posTar).getOwnerId()) && !((ITargets)attacker).getTargetFriendly()) {
                return false;
            }
            if (!(posTar instanceof IEntityOwnable) && !(posTar instanceof EntityPlayer) && ((ITargets)attacker).getTargetFriendly()) {
                return false;
            }
            if (posTar instanceof IEntityOwnable && !((IEntityOwnable)attacker).getOwnerId().equals(((IEntityOwnable)posTar).getOwnerId()) && ((ITargets)attacker).getTargetFriendly()) {
                return false;
            }
            if (posTar == ((IEntityOwnable)attacker).getOwner() && !((ITargets)attacker).getTargetFriendly()) {
                return false;
            }
        }
        else if (posTar instanceof EntityPlayer && !p_179445_2_ && ((EntityPlayer)posTar).capabilities.disableDamage && !((ITargets)attacker).getTargetFriendly()) {
            return false;
        }
        return !checkSight || attacker.getEntitySenses().canSee(posTar);
    }
    
    public boolean shouldExecute() {
        if (targetChance > 0 && taskOwner.getRNG().nextInt(targetChance) != 0) {
            return false;
        }
        final double d0 = getTargetDistance();
        final List<EntityLivingBase> list = taskOwner.world.getEntitiesWithinAABB(targetClass, taskOwner.getEntityBoundingBox().grow(d0, 4.0, d0), Predicates.and(targetEntitySelector, EntitySelectors.NOT_SPECTATING));
        Collections.sort(list, theNearestAttackableTargetSorter);
        if (list.isEmpty()) {
            return false;
        }
        targetEntity = list.get(0);
        return true;
    }
    
    public void startExecuting() {
        taskOwner.setAttackTarget(targetEntity);
        targetUnseenTicks = 0;
        super.startExecuting();
    }
    
    static /* synthetic */ double access$000(final AINearestValidTarget x0) {
        return x0.getTargetDistance();
    }
    
    public class Sorter implements Comparator
    {
        private final Entity theEntity;
        private static final String __OBFID = "CL_00001622";
        
        public Sorter(final Entity p_i1662_1_) {
            theEntity = p_i1662_1_;
        }
        
        public int compare(final Entity p_compare_1_, final Entity p_compare_2_) {
            final double d0 = theEntity.getDistanceSq(p_compare_1_);
            final double d2 = theEntity.getDistanceSq(p_compare_2_);
            return (d0 < d2) ? -1 : ((d0 > d2) ? 1 : 0);
        }
        
        @Override
        public int compare(final Object p_compare_1_, final Object p_compare_2_) {
            return compare((Entity)p_compare_1_, (Entity)p_compare_2_);
        }
    }
}
