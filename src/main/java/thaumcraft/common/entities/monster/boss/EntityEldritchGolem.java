package thaumcraft.common.entities.monster.boss;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.world.BlockLoot;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.common.entities.projectile.EntityGolemOrb;
import thaumcraft.common.lib.SoundsTC;


public class EntityEldritchGolem extends EntityThaumcraftBoss implements IEldritchMob, IRangedAttackMob
{
    private static DataParameter<Boolean> HEADLESS;
    int beamCharge;
    boolean chargingBeam;
    int arcing;
    int ax;
    int ay;
    int az;
    private int attackTimer;
    
    public EntityEldritchGolem(World p_i1745_1_) {
        super(p_i1745_1_);
        beamCharge = 0;
        chargingBeam = false;
        arcing = 0;
        ax = 0;
        ay = 0;
        az = 0;
        setSize(1.75f, 3.5f);
        isImmuneToFire = true;
    }
    
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(3, new EntityAIAttackMelee(this, 1.1, false));
        tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.8));
        tasks.addTask(7, new EntityAIWander(this, 0.8));
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        tasks.addTask(8, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }
    
    @Override
    public void generateName() {
        int t = (int) getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue();
        if (t >= 0) {
            setCustomNameTag(String.format(I18n.translateToLocal("entity.Thaumcraft.EldritchGolem.name.custom"), ChampionModifier.mods[t].getModNameLocalized()));
        }
    }
    
    @Override
    protected void entityInit() {
        super.entityInit();
        getDataManager().register(EntityEldritchGolem.HEADLESS, false);
    }
    
    public boolean isHeadless() {
        return (boolean) getDataManager().get((DataParameter)EntityEldritchGolem.HEADLESS);
    }
    
    public void setHeadless(boolean par1) {
        getDataManager().set(EntityEldritchGolem.HEADLESS, par1);
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("headless", isHeadless());
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setHeadless(nbt.getBoolean("headless"));
        if (isHeadless()) {
            makeHeadless();
        }
    }
    
    public float getEyeHeight() {
        return isHeadless() ? 3.33f : 3.0f;
    }
    
    public int getTotalArmorValue() {
        return super.getTotalArmorValue() + 6;
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(400.0);
    }
    
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_IRONGOLEM_HURT;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRONGOLEM_DEATH;
    }
    
    protected void playStepSound(BlockPos p_180429_1_, Block p_180429_2_) {
        playSound(SoundEvents.ENTITY_IRONGOLEM_STEP, 1.0f, 1.0f);
    }
    
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData data) {
        spawnTimer = 100;
        return super.onInitialSpawn(diff, data);
    }
    
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (attackTimer > 0) {
            --attackTimer;
        }
        if (motionX * motionX + motionZ * motionZ > 2.500000277905201E-7 && rand.nextInt(5) == 0) {
            IBlockState bs = world.getBlockState(getPosition());
            if (bs.getMaterial() != Material.AIR) {
                world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, posX + (rand.nextFloat() - 0.5) * width, getEntityBoundingBox().minY + 0.1, posZ + (rand.nextFloat() - 0.5) * width, 4.0 * (rand.nextFloat() - 0.5), 0.5, (rand.nextFloat() - 0.5) * 4.0, Block.getStateId(bs));
            }
            if (!world.isRemote && bs.getBlock() instanceof BlockLoot) {
                world.destroyBlock(getPosition(), true);
            }
        }
        if (!world.isRemote) {
            IBlockState bs = world.getBlockState(getPosition());
            float h = bs.getBlockHardness(world, getPosition());
            if (h >= 0.0f && h <= 0.15f) {
                world.destroyBlock(getPosition(), true);
            }
        }
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (!world.isRemote && damage > getHealth() && !isHeadless()) {
            setHeadless(true);
            spawnTimer = 100;
            double xx = MathHelper.cos(rotationYaw % 360.0f / 180.0f * 3.1415927f) * 0.75f;
            double zz = MathHelper.sin(rotationYaw % 360.0f / 180.0f * 3.1415927f) * 0.75f;
            world.createExplosion(this, posX + xx, posY + getEyeHeight(), posZ + zz, 2.0f, false);
            makeHeadless();
            return false;
        }
        return super.attackEntityFrom(source, damage);
    }
    
    void makeHeadless() {
        tasks.addTask(2, new AILongRangeAttack(this, 3.0, 1.0, 5, 5, 24.0f));
    }
    
    public boolean attackEntityAsMob(Entity target) {
        if (attackTimer > 0) {
            return false;
        }
        attackTimer = 10;
        world.setEntityState(this, (byte)4);
        boolean flag = target.attackEntityFrom(DamageSource.causeMobDamage(this), (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 0.75f);
        if (flag) {
            target.motionY += 0.2000000059604645;
            if (isHeadless()) {
                target.addVelocity(-MathHelper.sin(rotationYaw * 3.1415927f / 180.0f) * 1.5f, 0.1, MathHelper.cos(rotationYaw * 3.1415927f / 180.0f) * 1.5f);
            }
        }
        return flag;
    }
    
    public void attackEntityWithRangedAttack(EntityLivingBase entitylivingbase, float f) {
        if (canEntityBeSeen(entitylivingbase) && !chargingBeam && beamCharge > 0) {
            beamCharge -= 15 + rand.nextInt(5);
            getLookHelper().setLookPosition(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY + entitylivingbase.height / 2.0f, entitylivingbase.posZ, 30.0f, 30.0f);
            Vec3d v = getLook(1.0f);
            EntityGolemOrb entityGolemOrb;
            EntityGolemOrb blast = entityGolemOrb = new EntityGolemOrb(world, this, entitylivingbase, false);
            entityGolemOrb.posX += v.x;
            EntityGolemOrb entityGolemOrb2 = blast;
            entityGolemOrb2.posZ += v.z;
            blast.setPosition(blast.posX, blast.posY, blast.posZ);
            double d0 = entitylivingbase.posX + entitylivingbase.motionX - posX;
            double d2 = entitylivingbase.posY - posY - entitylivingbase.height / 2.0f;
            double d3 = entitylivingbase.posZ + entitylivingbase.motionZ - posZ;
            blast.shoot(d0, d2, d3, 0.66f, 5.0f);
            playSound(SoundsTC.egattack, 1.0f, 1.0f + rand.nextFloat() * 0.1f);
            world.spawnEntity(blast);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte p_70103_1_) {
        if (p_70103_1_ == 4) {
            attackTimer = 10;
            playSound(SoundEvents.ENTITY_IRONGOLEM_ATTACK, 1.0f, 1.0f);
        }
        else if (p_70103_1_ == 18) {
            spawnTimer = 150;
        }
        else if (p_70103_1_ == 19) {
            if (arcing == 0) {
                float radius = 2.0f + rand.nextFloat() * 2.0f;
                double radians = Math.toRadians(rand.nextInt(360));
                double deltaX = radius * Math.cos(radians);
                double deltaZ = radius * Math.sin(radians);
                int bx = MathHelper.floor(posX + deltaX);
                int by = MathHelper.floor(posY);
                int bz = MathHelper.floor(posZ + deltaZ);
                BlockPos bp = new BlockPos(bx, by, bz);
                for (int c = 0; c < 5 && world.isAirBlock(bp); ++c, --by) {}
                if (world.isAirBlock(bp.up()) && !world.isAirBlock(bp)) {
                    ax = bx;
                    ay = by;
                    az = bz;
                    arcing = 8 + rand.nextInt(5);
                    playSound(SoundsTC.jacobs, 0.8f, 1.0f + (rand.nextFloat() - rand.nextFloat()) * 0.05f);
                }
            }
        }
        else {
            super.handleStatusUpdate(p_70103_1_);
        }
    }
    
    @Override
    public void onUpdate() {
        if (getSpawnTimer() == 150) {
            world.setEntityState(this, (byte)18);
        }
        if (getSpawnTimer() > 0) {
            heal(2.0f);
        }
        super.onUpdate();
        if (world.isRemote) {
            if (isHeadless()) {
                rotationPitch = 0.0f;
                float f1 = MathHelper.cos(-renderYawOffset * 0.017453292f - 3.1415927f);
                float f2 = MathHelper.sin(-renderYawOffset * 0.017453292f - 3.1415927f);
                float f3 = -MathHelper.cos(-rotationPitch * 0.017453292f);
                float f4 = MathHelper.sin(-rotationPitch * 0.017453292f);
                Vec3d v = new Vec3d(f2 * f3, f4, f1 * f3);
                if (rand.nextInt(20) == 0) {
                    float a = (rand.nextFloat() - rand.nextFloat()) / 3.0f;
                    float b = (rand.nextFloat() - rand.nextFloat()) / 3.0f;
                    FXDispatcher.INSTANCE.spark((float)(posX + v.x + a), (float) posY + getEyeHeight() - 0.75f, (float)(posZ + v.z + b), 3.0f, 0.65f + rand.nextFloat() * 0.1f, 1.0f, 1.0f, 0.8f);
                }
                FXDispatcher.INSTANCE.drawVentParticles((float) posX + v.x * 0.4, (float) posY + getEyeHeight() - 1.25f, (float) posZ + v.z * 0.4, 0.0, 0.001, 0.0, 5592405, 4.0f);
                if (arcing > 0) {
                    FXDispatcher.INSTANCE.arcLightning(posX, posY + height / 2.0f, posZ, ax + 0.5, ay + 1, az + 0.5, 0.65f + rand.nextFloat() * 0.1f, 1.0f, 1.0f, 1.0f - arcing / 10.0f);
                    --arcing;
                }
            }
        }
        else {
            if (isHeadless() && beamCharge <= 0) {
                chargingBeam = true;
            }
            if (isHeadless() && chargingBeam) {
                ++beamCharge;
                world.setEntityState(this, (byte)19);
                if (beamCharge == 150) {
                    chargingBeam = false;
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public int getAttackTimer() {
        return attackTimer;
    }
    
    public void setSwingingArms(boolean swingingArms) {
    }
    
    static {
        HEADLESS = EntityDataManager.createKey(EntityEldritchGolem.class, DataSerializers.BOOLEAN);
    }
}
