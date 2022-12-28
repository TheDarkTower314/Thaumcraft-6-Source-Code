package thaumcraft.common.golems.ai;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;


public class AINearestValidTarget extends EntityAITarget
{
    protected Class<EntityLivingBase> targetClass;
    private int targetChance;
    protected EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
    protected Predicate targetEntitySelector;
    protected EntityLivingBase targetEntity;
    private int targetUnseenTicks;
    
    public AINearestValidTarget(EntityCreature p_i45878_1_, Class p_i45878_2_, boolean p_i45878_3_) {
        this(p_i45878_1_, p_i45878_2_, p_i45878_3_, false);
    }
    
    public AINearestValidTarget(EntityCreature p_i45879_1_, Class p_i45879_2_, boolean p_i45879_3_, boolean p_i45879_4_) {
        this(p_i45879_1_, p_i45879_2_, 10, p_i45879_3_, p_i45879_4_, null);
    }

    public AINearestValidTarget(EntityCreature owner, Class<EntityLivingBase> targetClass, int targetChance, boolean checkSight, boolean nearbyOnly, Predicate tselector) {
        super(owner, checkSight, nearbyOnly);
        this.targetClass = targetClass;
        this.targetChance = targetChance;
        theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(owner);
        setMutexBits(1);
        targetEntitySelector = new Predicate<EntityLivingBase>() {
            public boolean applySelection(EntityLivingBase entity) {
                if (tselector != null && !tselector.apply(entity)) {
                    return false;
                }
                if (entity instanceof EntityPlayer) {
                    double d0 = getTargetDistance();
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
            
            public boolean apply(EntityLivingBase p_apply_1_) {
                return applySelection(p_apply_1_);
            }
        };
    }
    
    public boolean shouldContinueExecuting() {
        EntityLivingBase entitylivingbase = taskOwner.getAttackTarget();
        if (entitylivingbase == null) {
            return false;
        }
        if (!entitylivingbase.isEntityAlive()) {
            return false;
        }
        Team team = taskOwner.getTeam();
        Team team2 = entitylivingbase.getTeam();
        if (team != null && team2 == team && !((ITargets) taskOwner).getTargetFriendly()) {
            return false;
        }
        if (team != null && team2 != team && ((ITargets) taskOwner).getTargetFriendly()) {
            return false;
        }
        double d0 = getTargetDistance();
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
    
    protected boolean isSuitableTarget(EntityLivingBase p_75296_1_, boolean p_75296_2_) {
        return isGoodTarget(taskOwner, p_75296_1_, p_75296_2_, shouldCheckSight) && taskOwner.isWithinHomeDistanceFromPosition(new BlockPos(p_75296_1_));
    }
    
    private boolean isGoodTarget(EntityLiving attacker, EntityLivingBase posTar, boolean p_179445_2_, boolean checkSight) {
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
        Team team = attacker.getTeam();
        Team team2 = posTar.getTeam();
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
        double d0 = getTargetDistance();
        List<EntityLivingBase> list = taskOwner.world.getEntitiesWithinAABB(targetClass, taskOwner.getEntityBoundingBox().grow(d0, 4.0, d0), Predicates.and(targetEntitySelector, EntitySelectors.NOT_SPECTATING));
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
    
    public class Sorter implements Comparator
    {
        private Entity theEntity;
        
        public Sorter(Entity p_i1662_1_) {
            theEntity = p_i1662_1_;
        }
        
        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = theEntity.getDistanceSq(p_compare_1_);
            double d2 = theEntity.getDistanceSq(p_compare_2_);
            return (d0 < d2) ? -1 : ((d0 > d2) ? 1 : 0);
        }
        
        @Override
        public int compare(Object p_compare_1_, Object p_compare_2_) {
            return compare((Entity)p_compare_1_, (Entity)p_compare_2_);
        }
    }
}
