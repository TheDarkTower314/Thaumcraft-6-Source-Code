// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster;

import java.util.List;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.init.SoundEvents;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.world.World;
import net.minecraft.entity.SharedMonsterAttributes;
import thaumcraft.api.entities.IEldritchMob;
import net.minecraft.entity.monster.EntityZombie;

public class EntityInhabitedZombie extends EntityZombie implements IEldritchMob
{
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
        this.getEntityAttribute(EntityInhabitedZombie.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0);
    }
    
    public EntityInhabitedZombie(final World world) {
        super(world);
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityCultist.class, true));
    }
    
    public void onKillEntity(final EntityLivingBase par1EntityLivingBase) {
    }
    
    public IEntityLivingData onInitialSpawn(final DifficultyInstance diff, final IEntityLivingData data) {
        final float d = (this.world.getDifficulty() == EnumDifficulty.HARD) ? 0.9f : 0.6f;
        this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ItemsTC.crimsonPlateHelm));
        if (this.rand.nextFloat() <= d) {
            this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemsTC.crimsonPlateChest));
        }
        if (this.rand.nextFloat() <= d) {
            this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ItemsTC.crimsonPlateLegs));
        }
        return super.onInitialSpawn(diff, data);
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected void dropFewItems(final boolean p_70628_1_, final int p_70628_2_) {
    }
    
    protected void onDeathUpdate() {
        if (!this.world.isRemote) {
            final EntityEldritchCrab crab = new EntityEldritchCrab(this.world);
            crab.setPositionAndRotation(this.posX, this.posY + this.getEyeHeight(), this.posZ, this.rotationYaw, this.rotationPitch);
            crab.setHelm(true);
            this.world.spawnEntity(crab);
            if ((this.recentlyHit > 0 || this.isPlayer()) && this.canDropLoot() && this.world.getGameRules().getBoolean("doMobLoot")) {
                int i = this.getExperiencePoints(this.attackingPlayer);
                while (i > 0) {
                    final int j = EntityXPOrb.getXPSplit(i);
                    i -= j;
                    this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
                }
            }
        }
        for (int k = 0; k < 20; ++k) {
            final double d2 = this.rand.nextGaussian() * 0.02;
            final double d3 = this.rand.nextGaussian() * 0.02;
            final double d4 = this.rand.nextGaussian() * 0.02;
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + this.rand.nextFloat() * this.width * 2.0f - this.width, this.posY + this.rand.nextFloat() * this.height, this.posZ + this.rand.nextFloat() * this.width * 2.0f - this.width, d2, d3, d4);
        }
        this.setDead();
    }
    
    public void onDeath(final DamageSource p_70645_1_) {
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundsTC.crabtalk;
    }
    
    protected SoundEvent getHurtSound(final DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_GENERIC_HURT;
    }
    
    public boolean getCanSpawnHere() {
        final List ents = this.world.getEntitiesWithinAABB(EntityInhabitedZombie.class, new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0).grow(32.0, 16.0, 32.0));
        return ents.size() <= 0 && super.getCanSpawnHere();
    }
}
