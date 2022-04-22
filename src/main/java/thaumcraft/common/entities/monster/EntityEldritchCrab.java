// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.util.DamageSource;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.entity.SharedMonsterAttributes;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.world.World;
import net.minecraft.network.datasync.DataParameter;
import thaumcraft.api.entities.IEldritchMob;
import net.minecraft.entity.monster.EntityMob;

public class EntityEldritchCrab extends EntityMob implements IEldritchMob
{
    private static final DataParameter<Boolean> HELM;
    private static final DataParameter<Integer> RIDING;
    private int attackTime;
    
    public EntityEldritchCrab(final World par1World) {
        super(par1World);
        this.attackTime = 0;
        this.setSize(0.8f, 0.6f);
        this.experienceValue = 6;
        ((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
    }
    
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAILeapAtTarget(this, 0.63f));
        this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.0, false));
        this.tasks.addTask(7, new EntityAIWander(this, 0.8));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityCultist.class, true));
    }
    
    public double getYOffset() {
        return this.isRiding() ? 0.5 : 0.0;
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.hasHelm() ? 0.275 : 0.3);
    }
    
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(EntityEldritchCrab.HELM, false);
        this.getDataManager().register(EntityEldritchCrab.RIDING, (-1));
    }
    
    public int getRiding() {
        return (int)this.getDataManager().get((DataParameter)EntityEldritchCrab.RIDING);
    }
    
    public void setRiding(final int s) {
        this.getDataManager().set(EntityEldritchCrab.RIDING, s);
    }
    
    public boolean canPickUpLoot() {
        return false;
    }
    
    public int getTotalArmorValue() {
        return this.hasHelm() ? 5 : 0;
    }
    
    public IEntityLivingData onInitialSpawn(final DifficultyInstance diff, IEntityLivingData data) {
        if (this.world.getDifficulty() == EnumDifficulty.HARD) {
            this.setHelm(true);
        }
        else {
            this.setHelm(this.rand.nextFloat() < 0.33f);
        }
        if (data == null) {
            data = new EntitySpider.GroupData();
            if (this.world.getDifficulty() == EnumDifficulty.HARD && this.world.rand.nextFloat() < 0.1f * diff.getClampedAdditionalDifficulty()) {
                ((EntitySpider.GroupData)data).setRandomEffect(this.world.rand);
            }
        }
        if (data instanceof EntitySpider.GroupData) {
            final Potion potion = ((EntitySpider.GroupData)data).effect;
            if (potion != null) {
                this.addPotionEffect(new PotionEffect(potion, Integer.MAX_VALUE));
            }
        }
        return super.onInitialSpawn(diff, data);
    }
    
    public boolean hasHelm() {
        return (boolean)this.getDataManager().get((DataParameter)EntityEldritchCrab.HELM);
    }
    
    public void setHelm(final boolean par1) {
        this.getDataManager().set(EntityEldritchCrab.HELM, par1);
    }
    
    public void onUpdate() {
        super.onUpdate();
        --this.attackTime;
        if (this.ticksExisted < 20) {
            this.fallDistance = 0.0f;
        }
        if (!this.world.isRemote) {
            if (this.getRidingEntity() == null && this.getAttackTarget() != null && !this.getAttackTarget().isBeingRidden() && !this.onGround && !this.hasHelm() && !this.getAttackTarget().isDead && this.posY - this.getAttackTarget().posY >= this.getAttackTarget().height / 2.0f && this.getDistanceSq(this.getAttackTarget()) < 4.0) {
                this.startRiding(this.getAttackTarget());
                this.setRiding(this.getAttackTarget().getEntityId());
            }
            if (this.getRidingEntity() != null && !this.isDead && this.attackTime <= 0) {
                this.attackTime = 10 + this.rand.nextInt(10);
                this.attackEntityAsMob(this.getRidingEntity());
                if (this.rand.nextFloat() < 0.2) {
                    this.dismountRidingEntity();
                    this.setRiding(-1);
                }
            }
            if (this.getRidingEntity() == null && this.getRiding() != -1) {
                this.setRiding(-1);
            }
        }
        else if (this.getRidingEntity() == null && this.getRiding() != -1) {
            final Entity e = this.world.getEntityByID(this.getRiding());
            if (e != null) {
                this.startRiding(e);
            }
        }
        else if (this.getRidingEntity() != null && this.getRiding() == -1) {
            this.dismountRidingEntity();
        }
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected void dropFewItems(final boolean p_70628_1_, final int p_70628_2_) {
        super.dropFewItems(p_70628_1_, p_70628_2_);
        if (p_70628_1_ && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + p_70628_2_) > 0)) {
            this.dropItem(Items.ENDER_PEARL, 1);
        }
    }
    
    public boolean attackEntityAsMob(final Entity p_70652_1_) {
        if (super.attackEntityAsMob(p_70652_1_)) {
            this.playSound(SoundsTC.crabclaw, 1.0f, 0.9f + this.world.rand.nextFloat() * 0.2f);
            return true;
        }
        return false;
    }
    
    public boolean attackEntityFrom(final DamageSource source, final float damage) {
        final boolean b = super.attackEntityFrom(source, damage);
        if (this.hasHelm() && this.getHealth() / this.getMaxHealth() <= 0.5f) {
            this.setHelm(false);
            this.renderBrokenItemStack(new ItemStack(ItemsTC.crimsonPlateChest));
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
        }
        return b;
    }
    
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.setHelm(par1NBTTagCompound.getBoolean("helm"));
        if (!this.hasHelm()) {
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
        }
    }
    
    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("helm", this.hasHelm());
    }
    
    public int getTalkInterval() {
        return 160;
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundsTC.crabtalk;
    }
    
    protected SoundEvent getHurtSound(final DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_GENERIC_HURT;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundsTC.crabdeath;
    }
    
    protected void playStepSound(final BlockPos p_180429_1_, final Block p_180429_2_) {
        this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15f, 1.0f);
    }
    
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }
    
    public boolean isPotionApplicable(final PotionEffect p_70687_1_) {
        return !p_70687_1_.getPotion().equals(MobEffects.POISON) && super.isPotionApplicable(p_70687_1_);
    }
    
    public boolean isOnSameTeam(final Entity el) {
        return el instanceof EntityEldritchCrab;
    }
    
    static {
        HELM = EntityDataManager.createKey(EntityEldritchCrab.class, DataSerializers.BOOLEAN);
        RIDING = EntityDataManager.createKey(EntityEldritchCrab.class, DataSerializers.VARINT);
    }
}
