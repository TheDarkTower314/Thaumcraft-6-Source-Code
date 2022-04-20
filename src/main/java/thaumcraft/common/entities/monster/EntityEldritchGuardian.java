// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster;

import net.minecraft.util.math.Vec3d;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.ThaumcraftApi;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.fx.PacketFXSonic;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.projectile.EntityEldritchOrb;
import net.minecraft.entity.EntityLivingBase;
import java.util.List;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.Item;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import thaumcraft.common.lib.network.misc.PacketMiscEvent;
import thaumcraft.common.lib.network.PacketHandler;
import net.minecraft.world.EnumDifficulty;
import thaumcraft.common.config.ModConfig;
import net.minecraft.entity.Entity;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.SharedMonsterAttributes;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.world.World;
import thaumcraft.api.entities.IEldritchMob;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.monster.EntityMob;

public class EntityEldritchGuardian extends EntityMob implements IRangedAttackMob, IEldritchMob
{
    public float armLiftL;
    public float armLiftR;
    boolean lastBlast;
    
    public EntityEldritchGuardian(final World p_i1745_1_) {
        super(p_i1745_1_);
        this.armLiftL = 0.0f;
        this.armLiftR = 0.0f;
        this.lastBlast = false;
        ((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
        this.setSize(0.8f, 2.25f);
        this.experienceValue = 20;
    }
    
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new AILongRangeAttack(this, 8.0, 1.0, 20, 40, 24.0f));
        this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.0, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.8));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityCultist.class, true));
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0);
    }
    
    protected void entityInit() {
        super.entityInit();
    }
    
    public int getTotalArmorValue() {
        return 4;
    }
    
    public boolean canPickUpLoot() {
        return false;
    }
    
    public boolean attackEntityFrom(final DamageSource source, float damage) {
        if (source.isMagicDamage()) {
            damage /= 2.0f;
        }
        return super.attackEntityFrom(source, damage);
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (this.world.isRemote) {
            if (this.armLiftL > 0.0f) {
                this.armLiftL -= 0.05f;
            }
            if (this.armLiftR > 0.0f) {
                this.armLiftR -= 0.05f;
            }
            final float x = (float)(this.posX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f);
            final float z = (float)(this.posZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f);
            FXDispatcher.INSTANCE.wispFXEG(x, (float)(this.posY + 0.22 * this.height), z, this);
        }
        else if (this.world.provider.getDimension() != ModConfig.CONFIG_WORLD.dimensionOuterId && (this.ticksExisted == 0 || this.ticksExisted % 100 == 0) && this.world.getDifficulty() != EnumDifficulty.EASY) {
            final double d6 = (this.world.getDifficulty() == EnumDifficulty.HARD) ? 576.0 : 256.0;
            for (int i = 0; i < this.world.playerEntities.size(); ++i) {
                final EntityPlayer entityplayer1 = this.world.playerEntities.get(i);
                if (entityplayer1.isEntityAlive()) {
                    final double d7 = entityplayer1.getDistanceSq(this.posX, this.posY, this.posZ);
                    if (d7 < d6) {
                        PacketHandler.INSTANCE.sendTo(new PacketMiscEvent((byte)2), (EntityPlayerMP)entityplayer1);
                    }
                }
            }
        }
    }
    
    public boolean attackEntityAsMob(final Entity p_70652_1_) {
        final boolean flag = super.attackEntityAsMob(p_70652_1_);
        if (flag) {
            final int i = this.world.getDifficulty().getDifficultyId();
            if (this.getHeldItemMainhand() == null && this.isBurning() && this.rand.nextFloat() < i * 0.3f) {
                p_70652_1_.setFire(2 * i);
            }
        }
        return flag;
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundsTC.egidle;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundsTC.egdeath;
    }
    
    public int getTalkInterval() {
        return 500;
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected void dropFewItems(final boolean flag, final int i) {
        super.dropFewItems(flag, i);
    }
    
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }
    
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        if (this.getHomePosition() != null && this.getMaximumHomeDistance() > 0.0f) {
            nbt.setInteger("HomeD", (int)this.getMaximumHomeDistance());
            nbt.setInteger("HomeX", this.getHomePosition().getX());
            nbt.setInteger("HomeY", this.getHomePosition().getY());
            nbt.setInteger("HomeZ", this.getHomePosition().getZ());
        }
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("HomeD")) {
            this.setHomePosAndDistance(new BlockPos(nbt.getInteger("HomeX"), nbt.getInteger("HomeY"), nbt.getInteger("HomeZ")), nbt.getInteger("HomeD"));
        }
    }
    
    public IEntityLivingData onInitialSpawn(final DifficultyInstance diff, final IEntityLivingData data) {
        final IEntityLivingData dd = super.onInitialSpawn(diff, data);
        final float f = diff.getClampedAdditionalDifficulty();
        if (this.world.provider.getDimension() == ModConfig.CONFIG_WORLD.dimensionOuterId) {
            final int bh = (int)this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() / 2;
            this.setAbsorptionAmount(this.getAbsorptionAmount() + bh);
        }
        return dd;
    }
    
    protected void updateAITasks() {
        super.updateAITasks();
        if (this.world.provider.getDimension() == ModConfig.CONFIG_WORLD.dimensionOuterId && this.hurtResistantTime <= 0 && this.ticksExisted % 25 == 0) {
            final int bh = (int)this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() / 2;
            if (this.getAbsorptionAmount() < bh) {
                this.setAbsorptionAmount(this.getAbsorptionAmount() + 1.0f);
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(final byte p_70103_1_) {
        if (p_70103_1_ == 15) {
            this.armLiftL = 0.5f;
        }
        else if (p_70103_1_ == 16) {
            this.armLiftR = 0.5f;
        }
        else if (p_70103_1_ == 17) {
            this.armLiftL = 0.9f;
            this.armLiftR = 0.9f;
        }
        else {
            super.handleStatusUpdate(p_70103_1_);
        }
    }
    
    protected boolean canDespawn() {
        return !this.hasHome();
    }
    
    public float getEyeHeight() {
        return 2.1f;
    }
    
    public boolean getCanSpawnHere() {
        final List ents = this.world.getEntitiesWithinAABB((Class)EntityEldritchGuardian.class, new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0).grow(32.0, 16.0, 32.0));
        return ents.size() <= 0 && super.getCanSpawnHere();
    }
    
    protected boolean isValidLightLevel() {
        return true;
    }
    
    protected float getSoundVolume() {
        return 1.5f;
    }
    
    public void attackEntityWithRangedAttack(final EntityLivingBase entitylivingbase, final float f) {
        if (this.rand.nextFloat() > 0.15f) {
            final EntityEldritchOrb blast = new EntityEldritchOrb(this.world, this);
            this.lastBlast = !this.lastBlast;
            this.world.setEntityState(this, (byte)(this.lastBlast ? 16 : 15));
            final int rr = this.lastBlast ? 90 : 180;
            final double xx = MathHelper.cos((this.rotationYaw + rr) % 360.0f / 180.0f * 3.1415927f) * 0.5f;
            final double yy = 0.057777777 * this.height;
            final double zz = MathHelper.sin((this.rotationYaw + rr) % 360.0f / 180.0f * 3.1415927f) * 0.5f;
            blast.setPosition(blast.posX - xx, blast.posY, blast.posZ - zz);
            final Vec3d v = entitylivingbase.getPositionVector().addVector(entitylivingbase.motionX * 10.0, entitylivingbase.motionY * 10.0, entitylivingbase.motionZ * 10.0).subtract(this.getPositionVector()).normalize();
            blast.shoot(v.x, v.y, v.z, 1.1f, 2.0f);
            this.playSound(SoundsTC.egattack, 2.0f, 1.0f + this.rand.nextFloat() * 0.1f);
            this.world.spawnEntity(blast);
        }
        else if (this.canEntityBeSeen(entitylivingbase)) {
            PacketHandler.INSTANCE.sendToAllAround(new PacketFXSonic(this.getEntityId()), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.posX, this.posY, this.posZ, 32.0));
            try {
                entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.WITHER, 400, 0));
            }
            catch (final Exception ex) {}
            if (entitylivingbase instanceof EntityPlayer) {
                ThaumcraftApi.internalMethods.addWarpToPlayer((EntityPlayer)entitylivingbase, 1 + this.world.rand.nextInt(3), IPlayerWarp.EnumWarpType.TEMPORARY);
            }
            this.playSound(SoundsTC.egscreech, 3.0f, 1.0f + this.rand.nextFloat() * 0.1f);
        }
    }
    
    public boolean isOnSameTeam(final Entity el) {
        return el instanceof IEldritchMob;
    }
    
    public void setSwingingArms(final boolean swingingArms) {
    }
}
