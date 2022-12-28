package thaumcraft.common.entities.ai.pech;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.entities.monster.EntityPech;


public class AIPechItemEntityGoto extends EntityAIBase
{
    private EntityPech pech;
    private Entity targetEntity;
    float maxTargetDistance;
    private int count;
    private int failedPathFindingPenalty;
    
    public AIPechItemEntityGoto(EntityPech par1EntityCreature) {
        maxTargetDistance = 16.0f;
        pech = par1EntityCreature;
        setMutexBits(3);
    }
    
    public boolean shouldExecute() {
        int count = this.count - 1;
        this.count = count;
        if (count > 0) {
            return false;
        }
        double range = Double.MAX_VALUE;
        List<Entity> targets = pech.world.getEntitiesWithinAABBExcludingEntity(pech, pech.getEntityBoundingBox().grow(maxTargetDistance, maxTargetDistance, maxTargetDistance));
        if (targets.size() == 0) {
            return false;
        }
        for (Entity e : targets) {
            if (e instanceof EntityItem && pech.canPickup(((EntityItem)e).getItem())) {
                NBTTagCompound itemData = e.getEntityData();
                String username = ((EntityItem)e).getThrower();
                if (username != null && username.equals("PechDrop")) {
                    continue;
                }
                double distance = e.getDistanceSq(pech.posX, pech.posY, pech.posZ);
                if (distance >= range || distance > maxTargetDistance * maxTargetDistance) {
                    continue;
                }
                range = distance;
                targetEntity = e;
            }
        }
        return targetEntity != null;
    }
    
    public boolean shouldContinueExecuting() {
        return targetEntity != null && targetEntity.isEntityAlive() && (!pech.getNavigator().noPath() && targetEntity.getDistanceSq(pech) < maxTargetDistance * maxTargetDistance);
    }
    
    public void resetTask() {
        targetEntity = null;
    }
    
    public void startExecuting() {
        pech.getNavigator().setPath(pech.getNavigator().getPathToEntityLiving(targetEntity), pech.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 1.5);
        count = 0;
    }
    
    public void updateTask() {
        pech.getLookHelper().setLookPositionWithEntity(targetEntity, 30.0f, 30.0f);
        if (pech.getEntitySenses().canSee(targetEntity) && --count <= 0) {
            count = failedPathFindingPenalty + 4 + pech.getRNG().nextInt(4);
            pech.getNavigator().tryMoveToEntityLiving(targetEntity, pech.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 1.5);
            if (pech.getNavigator().getPath() != null) {
                PathPoint finalPathPoint = pech.getNavigator().getPath().getFinalPathPoint();
                if (finalPathPoint != null && targetEntity.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1.0) {
                    failedPathFindingPenalty = 0;
                }
                else {
                    failedPathFindingPenalty += 10;
                }
            }
            else {
                failedPathFindingPenalty += 10;
            }
        }
        double distance = pech.getDistanceSq(targetEntity.posX, targetEntity.getEntityBoundingBox().minY, targetEntity.posZ);
        if (distance <= 1.5) {
            count = 0;
            int am = ((EntityItem) targetEntity).getItem().getCount();
            ItemStack is = pech.pickupItem(((EntityItem) targetEntity).getItem());
            if (is != null && !is.isEmpty() && is.getCount() > 0) {
                ((EntityItem) targetEntity).setItem(is);
            }
            else {
                targetEntity.setDead();
            }
            if (is == null || is.isEmpty() || is.getCount() != am) {
                targetEntity.world.playSound(null, targetEntity.posX, targetEntity.posY, targetEntity.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.NEUTRAL, 0.2f, ((targetEntity.world.rand.nextFloat() - targetEntity.world.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
            }
        }
    }
}
