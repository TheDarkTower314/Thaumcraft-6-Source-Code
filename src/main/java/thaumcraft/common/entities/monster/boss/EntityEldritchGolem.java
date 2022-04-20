// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.boss;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.Vec3d;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.entities.projectile.EntityGolemOrb;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.state.IBlockState;
import thaumcraft.common.blocks.world.BlockLoot;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.block.material.Material;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.ThaumcraftApiHelper;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.world.World;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.entity.IRangedAttackMob;
import thaumcraft.api.entities.IEldritchMob;

public class EntityEldritchGolem extends EntityThaumcraftBoss implements IEldritchMob, IRangedAttackMob
{
    private static final DataParameter<Boolean> HEADLESS;
    int beamCharge;
    boolean chargingBeam;
    int arcing;
    int ax;
    int ay;
    int az;
    private int attackTimer;
    
    public EntityEldritchGolem(final World p_i1745_1_) {
        super(p_i1745_1_);
        this.beamCharge = 0;
        this.chargingBeam = false;
        this.arcing = 0;
        this.ax = 0;
        this.ay = 0;
        this.az = 0;
        this.setSize(1.75f, 3.5f);
        this.isImmuneToFire = true;
    }
    
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.1, false));
        this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.8));
        this.tasks.addTask(7, new EntityAIWander(this, 0.8));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }
    
    @Override
    public void generateName() {
        final int t = (int)this.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue();
        if (t >= 0) {
            this.setCustomNameTag(String.format(I18n.translateToLocal("entity.Thaumcraft.EldritchGolem.name.custom"), ChampionModifier.mods[t].getModNameLocalized()));
        }
    }
    
    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(EntityEldritchGolem.HEADLESS, false);
    }
    
    public boolean isHeadless() {
        return (boolean)this.getDataManager().get((DataParameter)EntityEldritchGolem.HEADLESS);
    }
    
    public void setHeadless(final boolean par1) {
        this.getDataManager().set(EntityEldritchGolem.HEADLESS, par1);
    }
    
    @Override
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("headless", this.isHeadless());
    }
    
    @Override
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setHeadless(nbt.getBoolean("headless"));
        if (this.isHeadless()) {
            this.makeHeadless();
        }
    }
    
    public float getEyeHeight() {
        return this.isHeadless() ? 3.33f : 3.0f;
    }
    
    public int getTotalArmorValue() {
        return super.getTotalArmorValue() + 6;
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(400.0);
    }
    
    protected SoundEvent getHurtSound(final DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_IRONGOLEM_HURT;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRONGOLEM_DEATH;
    }
    
    protected void playStepSound(final BlockPos p_180429_1_, final Block p_180429_2_) {
        this.playSound(SoundEvents.ENTITY_IRONGOLEM_STEP, 1.0f, 1.0f);
    }
    
    @Override
    public IEntityLivingData onInitialSpawn(final DifficultyInstance diff, final IEntityLivingData data) {
        this.spawnTimer = 100;
        return super.onInitialSpawn(diff, data);
    }
    
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.attackTimer > 0) {
            --this.attackTimer;
        }
        if (this.motionX * this.motionX + this.motionZ * this.motionZ > 2.500000277905201E-7 && this.rand.nextInt(5) == 0) {
            final IBlockState bs = this.world.getBlockState(this.getPosition());
            if (bs.getMaterial() != Material.AIR) {
                this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + (this.rand.nextFloat() - 0.5) * this.width, this.getEntityBoundingBox().minY + 0.1, this.posZ + (this.rand.nextFloat() - 0.5) * this.width, 4.0 * (this.rand.nextFloat() - 0.5), 0.5, (this.rand.nextFloat() - 0.5) * 4.0, new int[] { Block.getStateId(bs) });
            }
            if (!this.world.isRemote && bs.getBlock() instanceof BlockLoot) {
                this.world.destroyBlock(this.getPosition(), true);
            }
        }
        if (!this.world.isRemote) {
            final IBlockState bs = this.world.getBlockState(this.getPosition());
            final float h = bs.getBlockHardness(this.world, this.getPosition());
            if (h >= 0.0f && h <= 0.15f) {
                this.world.destroyBlock(this.getPosition(), true);
            }
        }
    }
    
    @Override
    public boolean attackEntityFrom(final DamageSource source, final float damage) {
        if (!this.world.isRemote && damage > this.getHealth() && !this.isHeadless()) {
            this.setHeadless(true);
            this.spawnTimer = 100;
            final double xx = MathHelper.cos(this.rotationYaw % 360.0f / 180.0f * 3.1415927f) * 0.75f;
            final double zz = MathHelper.sin(this.rotationYaw % 360.0f / 180.0f * 3.1415927f) * 0.75f;
            this.world.createExplosion(this, this.posX + xx, this.posY + this.getEyeHeight(), this.posZ + zz, 2.0f, false);
            this.makeHeadless();
            return false;
        }
        return super.attackEntityFrom(source, damage);
    }
    
    void makeHeadless() {
        this.tasks.addTask(2, new AILongRangeAttack(this, 3.0, 1.0, 5, 5, 24.0f));
    }
    
    public boolean attackEntityAsMob(final Entity target) {
        if (this.attackTimer > 0) {
            return false;
        }
        this.attackTimer = 10;
        this.world.setEntityState(this, (byte)4);
        final boolean flag = target.attackEntityFrom(DamageSource.causeMobDamage(this), (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 0.75f);
        if (flag) {
            target.motionY += 0.2000000059604645;
            if (this.isHeadless()) {
                target.addVelocity(-MathHelper.sin(this.rotationYaw * 3.1415927f / 180.0f) * 1.5f, 0.1, MathHelper.cos(this.rotationYaw * 3.1415927f / 180.0f) * 1.5f);
            }
        }
        return flag;
    }
    
    public void attackEntityWithRangedAttack(final EntityLivingBase entitylivingbase, final float f) {
        if (this.canEntityBeSeen(entitylivingbase) && !this.chargingBeam && this.beamCharge > 0) {
            this.beamCharge -= 15 + this.rand.nextInt(5);
            this.getLookHelper().setLookPosition(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY + entitylivingbase.height / 2.0f, entitylivingbase.posZ, 30.0f, 30.0f);
            final Vec3d v = this.getLook(1.0f);
            final EntityGolemOrb entityGolemOrb;
            final EntityGolemOrb blast = entityGolemOrb = new EntityGolemOrb(this.world, this, entitylivingbase, false);
            entityGolemOrb.posX += v.x;
            final EntityGolemOrb entityGolemOrb2 = blast;
            entityGolemOrb2.posZ += v.z;
            blast.setPosition(blast.posX, blast.posY, blast.posZ);
            final double d0 = entitylivingbase.posX + entitylivingbase.motionX - this.posX;
            final double d2 = entitylivingbase.posY - this.posY - entitylivingbase.height / 2.0f;
            final double d3 = entitylivingbase.posZ + entitylivingbase.motionZ - this.posZ;
            blast.shoot(d0, d2, d3, 0.66f, 5.0f);
            this.playSound(SoundsTC.egattack, 1.0f, 1.0f + this.rand.nextFloat() * 0.1f);
            this.world.spawnEntity(blast);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(final byte p_70103_1_) {
        if (p_70103_1_ == 4) {
            this.attackTimer = 10;
            this.playSound(SoundEvents.ENTITY_IRONGOLEM_ATTACK, 1.0f, 1.0f);
        }
        else if (p_70103_1_ == 18) {
            this.spawnTimer = 150;
        }
        else if (p_70103_1_ == 19) {
            if (this.arcing == 0) {
                final float radius = 2.0f + this.rand.nextFloat() * 2.0f;
                final double radians = Math.toRadians(this.rand.nextInt(360));
                final double deltaX = radius * Math.cos(radians);
                final double deltaZ = radius * Math.sin(radians);
                final int bx = MathHelper.floor(this.posX + deltaX);
                int by = MathHelper.floor(this.posY);
                final int bz = MathHelper.floor(this.posZ + deltaZ);
                final BlockPos bp = new BlockPos(bx, by, bz);
                for (int c = 0; c < 5 && this.world.isAirBlock(bp); ++c, --by) {}
                if (this.world.isAirBlock(bp.up()) && !this.world.isAirBlock(bp)) {
                    this.ax = bx;
                    this.ay = by;
                    this.az = bz;
                    this.arcing = 8 + this.rand.nextInt(5);
                    this.playSound(SoundsTC.jacobs, 0.8f, 1.0f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05f);
                }
            }
        }
        else {
            super.handleStatusUpdate(p_70103_1_);
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.getSpawnTimer() == 150) {
            this.world.setEntityState(this, (byte)18);
        }
        if (this.getSpawnTimer() > 0) {
            this.heal(2.0f);
        }
        super.onUpdate();
        if (this.world.isRemote) {
            if (this.isHeadless()) {
                this.rotationPitch = 0.0f;
                final float f1 = MathHelper.cos(-this.renderYawOffset * 0.017453292f - 3.1415927f);
                final float f2 = MathHelper.sin(-this.renderYawOffset * 0.017453292f - 3.1415927f);
                final float f3 = -MathHelper.cos(-this.rotationPitch * 0.017453292f);
                final float f4 = MathHelper.sin(-this.rotationPitch * 0.017453292f);
                final Vec3d v = new Vec3d(f2 * f3, f4, f1 * f3);
                if (this.rand.nextInt(20) == 0) {
                    final float a = (this.rand.nextFloat() - this.rand.nextFloat()) / 3.0f;
                    final float b = (this.rand.nextFloat() - this.rand.nextFloat()) / 3.0f;
                    FXDispatcher.INSTANCE.spark((float)(this.posX + v.x + a), (float)this.posY + this.getEyeHeight() - 0.75f, (float)(this.posZ + v.z + b), 3.0f, 0.65f + this.rand.nextFloat() * 0.1f, 1.0f, 1.0f, 0.8f);
                }
                FXDispatcher.INSTANCE.drawVentParticles((float)this.posX + v.x * 0.4, (float)this.posY + this.getEyeHeight() - 1.25f, (float)this.posZ + v.z * 0.4, 0.0, 0.001, 0.0, 5592405, 4.0f);
                if (this.arcing > 0) {
                    FXDispatcher.INSTANCE.arcLightning(this.posX, this.posY + this.height / 2.0f, this.posZ, this.ax + 0.5, this.ay + 1, this.az + 0.5, 0.65f + this.rand.nextFloat() * 0.1f, 1.0f, 1.0f, 1.0f - this.arcing / 10.0f);
                    --this.arcing;
                }
            }
        }
        else {
            if (this.isHeadless() && this.beamCharge <= 0) {
                this.chargingBeam = true;
            }
            if (this.isHeadless() && this.chargingBeam) {
                ++this.beamCharge;
                this.world.setEntityState(this, (byte)19);
                if (this.beamCharge == 150) {
                    this.chargingBeam = false;
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public int getAttackTimer() {
        return this.attackTimer;
    }
    
    public void setSwingingArms(final boolean swingingArms) {
    }
    
    static {
        HEADLESS = EntityDataManager.createKey(EntityEldritchGolem.class, DataSerializers.BOOLEAN);
    }
}
