package thaumcraft.common.entities.monster;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.lib.SoundsTC;


public class EntityInhabitedZombie extends EntityZombie implements IEldritchMob
{
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
        getEntityAttribute(EntityInhabitedZombie.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0);
    }
    
    public EntityInhabitedZombie(World world) {
        super(world);
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityCultist.class, true));
    }
    
    public void onKillEntity(EntityLivingBase par1EntityLivingBase) {
    }
    
    public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData data) {
        float d = (world.getDifficulty() == EnumDifficulty.HARD) ? 0.9f : 0.6f;
        setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ItemsTC.crimsonPlateHelm));
        if (rand.nextFloat() <= d) {
            setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemsTC.crimsonPlateChest));
        }
        if (rand.nextFloat() <= d) {
            setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ItemsTC.crimsonPlateLegs));
        }
        return super.onInitialSpawn(diff, data);
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
    }
    
    protected void onDeathUpdate() {
        if (!world.isRemote) {
            EntityEldritchCrab crab = new EntityEldritchCrab(world);
            crab.setPositionAndRotation(posX, posY + getEyeHeight(), posZ, rotationYaw, rotationPitch);
            crab.setHelm(true);
            world.spawnEntity(crab);
            if ((recentlyHit > 0 || isPlayer()) && canDropLoot() && world.getGameRules().getBoolean("doMobLoot")) {
                int i = getExperiencePoints(attackingPlayer);
                while (i > 0) {
                    int j = EntityXPOrb.getXPSplit(i);
                    i -= j;
                    world.spawnEntity(new EntityXPOrb(world, posX, posY, posZ, j));
                }
            }
        }
        for (int k = 0; k < 20; ++k) {
            double d2 = rand.nextGaussian() * 0.02;
            double d3 = rand.nextGaussian() * 0.02;
            double d4 = rand.nextGaussian() * 0.02;
            world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, posX + rand.nextFloat() * width * 2.0f - width, posY + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0f - width, d2, d3, d4);
        }
        setDead();
    }
    
    public void onDeath(DamageSource p_70645_1_) {
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundsTC.crabtalk;
    }
    
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_GENERIC_HURT;
    }
    
    public boolean getCanSpawnHere() {
        List ents = world.getEntitiesWithinAABB(EntityInhabitedZombie.class, new AxisAlignedBB(posX, posY, posZ, posX + 1.0, posY + 1.0, posZ + 1.0).grow(32.0, 16.0, 32.0));
        return ents.size() <= 0 && super.getCanSpawnHere();
    }
}
