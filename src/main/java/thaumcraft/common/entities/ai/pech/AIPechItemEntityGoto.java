// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.ai.pech;

import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.Entity;
import thaumcraft.common.entities.monster.EntityPech;
import net.minecraft.entity.ai.EntityAIBase;

public class AIPechItemEntityGoto extends EntityAIBase
{
    private EntityPech pech;
    private Entity targetEntity;
    float maxTargetDistance;
    private int count;
    private int failedPathFindingPenalty;
    
    public AIPechItemEntityGoto(final EntityPech par1EntityCreature) {
        this.maxTargetDistance = 16.0f;
        this.pech = par1EntityCreature;
        this.setMutexBits(3);
    }
    
    public boolean shouldExecute() {
        final int count = this.count - 1;
        this.count = count;
        if (count > 0) {
            return false;
        }
        double range = Double.MAX_VALUE;
        final List<Entity> targets = this.pech.world.getEntitiesWithinAABBExcludingEntity(this.pech, this.pech.getEntityBoundingBox().grow(this.maxTargetDistance, this.maxTargetDistance, this.maxTargetDistance));
        if (targets.size() == 0) {
            return false;
        }
        for (final Entity e : targets) {
            if (e instanceof EntityItem && this.pech.canPickup(((EntityItem)e).getItem())) {
                final NBTTagCompound itemData = e.getEntityData();
                final String username = ((EntityItem)e).getThrower();
                if (username != null && username.equals("PechDrop")) {
                    continue;
                }
                final double distance = e.getDistanceSq(this.pech.posX, this.pech.posY, this.pech.posZ);
                if (distance >= range || distance > this.maxTargetDistance * this.maxTargetDistance) {
                    continue;
                }
                range = distance;
                this.targetEntity = e;
            }
        }
        return this.targetEntity != null;
    }
    
    public boolean shouldContinueExecuting() {
        return this.targetEntity != null && this.targetEntity.isEntityAlive() && (!this.pech.getNavigator().noPath() && this.targetEntity.getDistanceSq(this.pech) < this.maxTargetDistance * this.maxTargetDistance);
    }
    
    public void resetTask() {
        this.targetEntity = null;
    }
    
    public void startExecuting() {
        this.pech.getNavigator().setPath(this.pech.getNavigator().getPathToEntityLiving(this.targetEntity), this.pech.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 1.5);
        this.count = 0;
    }
    
    public void updateTask() {
        this.pech.getLookHelper().setLookPositionWithEntity(this.targetEntity, 30.0f, 30.0f);
        if (this.pech.getEntitySenses().canSee(this.targetEntity) && --this.count <= 0) {
            this.count = this.failedPathFindingPenalty + 4 + this.pech.getRNG().nextInt(4);
            this.pech.getNavigator().tryMoveToEntityLiving(this.targetEntity, this.pech.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 1.5);
            if (this.pech.getNavigator().getPath() != null) {
                final PathPoint finalPathPoint = this.pech.getNavigator().getPath().getFinalPathPoint();
                if (finalPathPoint != null && this.targetEntity.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1.0) {
                    this.failedPathFindingPenalty = 0;
                }
                else {
                    this.failedPathFindingPenalty += 10;
                }
            }
            else {
                this.failedPathFindingPenalty += 10;
            }
        }
        final double distance = this.pech.getDistanceSq(this.targetEntity.posX, this.targetEntity.getEntityBoundingBox().minY, this.targetEntity.posZ);
        if (distance <= 1.5) {
            this.count = 0;
            final int am = ((EntityItem)this.targetEntity).getItem().getCount();
            final ItemStack is = this.pech.pickupItem(((EntityItem)this.targetEntity).getItem());
            if (is != null && !is.isEmpty() && is.getCount() > 0) {
                ((EntityItem)this.targetEntity).setItem(is);
            }
            else {
                this.targetEntity.setDead();
            }
            if (is == null || is.isEmpty() || is.getCount() != am) {
                this.targetEntity.world.playSound(null, this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.NEUTRAL, 0.2f, ((this.targetEntity.world.rand.nextFloat() - this.targetEntity.world.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
            }
        }
    }
}
