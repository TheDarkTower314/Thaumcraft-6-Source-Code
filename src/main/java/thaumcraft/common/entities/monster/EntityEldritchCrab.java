package thaumcraft.common.entities.monster;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.lib.SoundsTC;


public class EntityEldritchCrab extends EntityMob implements IEldritchMob
{
    private static DataParameter<Boolean> HELM;
    private static DataParameter<Integer> RIDING;
    private int attackTime;
    
    public EntityEldritchCrab(World par1World) {
        super(par1World);
        attackTime = 0;
        setSize(0.8f, 0.6f);
        experienceValue = 6;
        ((PathNavigateGround) getNavigator()).setBreakDoors(true);
    }
    
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAILeapAtTarget(this, 0.63f));
        tasks.addTask(3, new EntityAIAttackMelee(this, 1.0, false));
        tasks.addTask(7, new EntityAIWander(this, 0.8));
        tasks.addTask(8, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityCultist.class, true));
    }
    
    public double getYOffset() {
        return isRiding() ? 0.5 : 0.0;
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(hasHelm() ? 0.275 : 0.3);
    }
    
    protected void entityInit() {
        super.entityInit();
        getDataManager().register(EntityEldritchCrab.HELM, false);
        getDataManager().register(EntityEldritchCrab.RIDING, (-1));
    }
    
    public int getRiding() {
        return (int) getDataManager().get((DataParameter)EntityEldritchCrab.RIDING);
    }
    
    public void setRiding(int s) {
        getDataManager().set(EntityEldritchCrab.RIDING, s);
    }
    
    public boolean canPickUpLoot() {
        return false;
    }
    
    public int getTotalArmorValue() {
        return hasHelm() ? 5 : 0;
    }
    
    public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData data) {
        if (world.getDifficulty() == EnumDifficulty.HARD) {
            setHelm(true);
        }
        else {
            setHelm(rand.nextFloat() < 0.33f);
        }
        if (data == null) {
            data = new EntitySpider.GroupData();
            if (world.getDifficulty() == EnumDifficulty.HARD && world.rand.nextFloat() < 0.1f * diff.getClampedAdditionalDifficulty()) {
                ((EntitySpider.GroupData)data).setRandomEffect(world.rand);
            }
        }
        if (data instanceof EntitySpider.GroupData) {
            Potion potion = ((EntitySpider.GroupData)data).effect;
            if (potion != null) {
                addPotionEffect(new PotionEffect(potion, Integer.MAX_VALUE));
            }
        }
        return super.onInitialSpawn(diff, data);
    }
    
    public boolean hasHelm() {
        return (boolean) getDataManager().get((DataParameter)EntityEldritchCrab.HELM);
    }
    
    public void setHelm(boolean par1) {
        getDataManager().set(EntityEldritchCrab.HELM, par1);
    }
    
    public void onUpdate() {
        super.onUpdate();
        --attackTime;
        if (ticksExisted < 20) {
            fallDistance = 0.0f;
        }
        if (!world.isRemote) {
            if (getRidingEntity() == null && getAttackTarget() != null && !getAttackTarget().isBeingRidden() && !onGround && !hasHelm() && !getAttackTarget().isDead && posY - getAttackTarget().posY >= getAttackTarget().height / 2.0f && getDistanceSq(getAttackTarget()) < 4.0) {
                startRiding(getAttackTarget());
                setRiding(getAttackTarget().getEntityId());
            }
            if (getRidingEntity() != null && !isDead && attackTime <= 0) {
                attackTime = 10 + rand.nextInt(10);
                attackEntityAsMob(getRidingEntity());
                if (rand.nextFloat() < 0.2) {
                    dismountRidingEntity();
                    setRiding(-1);
                }
            }
            if (getRidingEntity() == null && getRiding() != -1) {
                setRiding(-1);
            }
        }
        else if (getRidingEntity() == null && getRiding() != -1) {
            Entity e = world.getEntityByID(getRiding());
            if (e != null) {
                startRiding(e);
            }
        }
        else if (getRidingEntity() != null && getRiding() == -1) {
            dismountRidingEntity();
        }
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
        super.dropFewItems(p_70628_1_, p_70628_2_);
        if (p_70628_1_ && (rand.nextInt(3) == 0 || rand.nextInt(1 + p_70628_2_) > 0)) {
            dropItem(Items.ENDER_PEARL, 1);
        }
    }
    
    public boolean attackEntityAsMob(Entity p_70652_1_) {
        if (super.attackEntityAsMob(p_70652_1_)) {
            playSound(SoundsTC.crabclaw, 1.0f, 0.9f + world.rand.nextFloat() * 0.2f);
            return true;
        }
        return false;
    }
    
    public boolean attackEntityFrom(DamageSource source, float damage) {
        boolean b = super.attackEntityFrom(source, damage);
        if (hasHelm() && getHealth() / getMaxHealth() <= 0.5f) {
            setHelm(false);
            renderBrokenItemStack(new ItemStack(ItemsTC.crimsonPlateChest));
            getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
        }
        return b;
    }
    
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        setHelm(par1NBTTagCompound.getBoolean("helm"));
        if (!hasHelm()) {
            getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
        }
    }
    
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("helm", hasHelm());
    }
    
    public int getTalkInterval() {
        return 160;
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundsTC.crabtalk;
    }
    
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_GENERIC_HURT;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundsTC.crabdeath;
    }
    
    protected void playStepSound(BlockPos p_180429_1_, Block p_180429_2_) {
        playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15f, 1.0f);
    }
    
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }
    
    public boolean isPotionApplicable(PotionEffect p_70687_1_) {
        return !p_70687_1_.getPotion().equals(MobEffects.POISON) && super.isPotionApplicable(p_70687_1_);
    }
    
    public boolean isOnSameTeam(Entity el) {
        return el instanceof EntityEldritchCrab;
    }
    
    static {
        HELM = EntityDataManager.createKey(EntityEldritchCrab.class, DataSerializers.BOOLEAN);
        RIDING = EntityDataManager.createKey(EntityEldritchCrab.class, DataSerializers.VARINT);
    }
}
