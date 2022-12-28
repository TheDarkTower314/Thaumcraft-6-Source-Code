package thaumcraft.common.entities.monster;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
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
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.entities.projectile.EntityEldritchOrb;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXSonic;
import thaumcraft.common.lib.network.misc.PacketMiscEvent;


public class EntityEldritchGuardian extends EntityMob implements IRangedAttackMob, IEldritchMob
{
    public float armLiftL;
    public float armLiftR;
    boolean lastBlast;
    
    public EntityEldritchGuardian(World p_i1745_1_) {
        super(p_i1745_1_);
        armLiftL = 0.0f;
        armLiftR = 0.0f;
        lastBlast = false;
        ((PathNavigateGround) getNavigator()).setBreakDoors(true);
        setSize(0.8f, 2.25f);
        experienceValue = 20;
    }
    
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(2, new AILongRangeAttack(this, 8.0, 1.0, 20, 40, 24.0f));
        tasks.addTask(3, new EntityAIAttackMelee(this, 1.0, false));
        tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.8));
        tasks.addTask(7, new EntityAIWander(this, 1.0));
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        tasks.addTask(8, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityCultist.class, true));
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0);
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
    
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (source.isMagicDamage()) {
            damage /= 2.0f;
        }
        return super.attackEntityFrom(source, damage);
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            if (armLiftL > 0.0f) {
                armLiftL -= 0.05f;
            }
            if (armLiftR > 0.0f) {
                armLiftR -= 0.05f;
            }
            float x = (float)(posX + (rand.nextFloat() - rand.nextFloat()) * 0.2f);
            float z = (float)(posZ + (rand.nextFloat() - rand.nextFloat()) * 0.2f);
            FXDispatcher.INSTANCE.wispFXEG(x, (float)(posY + 0.22 * height), z, this);
        }
        else if (world.provider.getDimension() != ModConfig.CONFIG_WORLD.dimensionOuterId && (ticksExisted == 0 || ticksExisted % 100 == 0) && world.getDifficulty() != EnumDifficulty.EASY) {
            double d6 = (world.getDifficulty() == EnumDifficulty.HARD) ? 576.0 : 256.0;
            for (int i = 0; i < world.playerEntities.size(); ++i) {
                EntityPlayer entityplayer1 = world.playerEntities.get(i);
                if (entityplayer1.isEntityAlive()) {
                    double d7 = entityplayer1.getDistanceSq(posX, posY, posZ);
                    if (d7 < d6) {
                        PacketHandler.INSTANCE.sendTo(new PacketMiscEvent((byte)2), (EntityPlayerMP)entityplayer1);
                    }
                }
            }
        }
    }
    
    public boolean attackEntityAsMob(Entity p_70652_1_) {
        boolean flag = super.attackEntityAsMob(p_70652_1_);
        if (flag) {
            int i = world.getDifficulty().getDifficultyId();
            if (getHeldItemMainhand() == null && isBurning() && rand.nextFloat() < i * 0.3f) {
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
    
    protected void dropFewItems(boolean flag, int i) {
        super.dropFewItems(flag, i);
    }
    
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }
    
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        if (getHomePosition() != null && getMaximumHomeDistance() > 0.0f) {
            nbt.setInteger("HomeD", (int) getMaximumHomeDistance());
            nbt.setInteger("HomeX", getHomePosition().getX());
            nbt.setInteger("HomeY", getHomePosition().getY());
            nbt.setInteger("HomeZ", getHomePosition().getZ());
        }
    }
    
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("HomeD")) {
            setHomePosAndDistance(new BlockPos(nbt.getInteger("HomeX"), nbt.getInteger("HomeY"), nbt.getInteger("HomeZ")), nbt.getInteger("HomeD"));
        }
    }
    
    public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData data) {
        IEntityLivingData dd = super.onInitialSpawn(diff, data);
        float f = diff.getClampedAdditionalDifficulty();
        if (world.provider.getDimension() == ModConfig.CONFIG_WORLD.dimensionOuterId) {
            int bh = (int) getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() / 2;
            setAbsorptionAmount(getAbsorptionAmount() + bh);
        }
        return dd;
    }
    
    protected void updateAITasks() {
        super.updateAITasks();
        if (world.provider.getDimension() == ModConfig.CONFIG_WORLD.dimensionOuterId && hurtResistantTime <= 0 && ticksExisted % 25 == 0) {
            int bh = (int) getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() / 2;
            if (getAbsorptionAmount() < bh) {
                setAbsorptionAmount(getAbsorptionAmount() + 1.0f);
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte p_70103_1_) {
        if (p_70103_1_ == 15) {
            armLiftL = 0.5f;
        }
        else if (p_70103_1_ == 16) {
            armLiftR = 0.5f;
        }
        else if (p_70103_1_ == 17) {
            armLiftL = 0.9f;
            armLiftR = 0.9f;
        }
        else {
            super.handleStatusUpdate(p_70103_1_);
        }
    }
    
    protected boolean canDespawn() {
        return !hasHome();
    }
    
    public float getEyeHeight() {
        return 2.1f;
    }
    
    public boolean getCanSpawnHere() {
        List ents = world.getEntitiesWithinAABB(EntityEldritchGuardian.class, new AxisAlignedBB(posX, posY, posZ, posX + 1.0, posY + 1.0, posZ + 1.0).grow(32.0, 16.0, 32.0));
        return ents.size() <= 0 && super.getCanSpawnHere();
    }
    
    protected boolean isValidLightLevel() {
        return true;
    }
    
    protected float getSoundVolume() {
        return 1.5f;
    }
    
    public void attackEntityWithRangedAttack(EntityLivingBase entitylivingbase, float f) {
        if (rand.nextFloat() > 0.15f) {
            EntityEldritchOrb blast = new EntityEldritchOrb(world, this);
            lastBlast = !lastBlast;
            world.setEntityState(this, (byte)(lastBlast ? 16 : 15));
            int rr = lastBlast ? 90 : 180;
            double xx = MathHelper.cos((rotationYaw + rr) % 360.0f / 180.0f * 3.1415927f) * 0.5f;
            double yy = 0.057777777 * height;
            double zz = MathHelper.sin((rotationYaw + rr) % 360.0f / 180.0f * 3.1415927f) * 0.5f;
            blast.setPosition(blast.posX - xx, blast.posY, blast.posZ - zz);
            Vec3d v = entitylivingbase.getPositionVector().addVector(entitylivingbase.motionX * 10.0, entitylivingbase.motionY * 10.0, entitylivingbase.motionZ * 10.0).subtract(getPositionVector()).normalize();
            blast.shoot(v.x, v.y, v.z, 1.1f, 2.0f);
            playSound(SoundsTC.egattack, 2.0f, 1.0f + rand.nextFloat() * 0.1f);
            world.spawnEntity(blast);
        }
        else if (canEntityBeSeen(entitylivingbase)) {
            PacketHandler.INSTANCE.sendToAllAround(new PacketFXSonic(getEntityId()), new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 32.0));
            try {
                entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.WITHER, 400, 0));
            }
            catch (Exception ex) {}
            if (entitylivingbase instanceof EntityPlayer) {
                ThaumcraftApi.internalMethods.addWarpToPlayer((EntityPlayer)entitylivingbase, 1 + world.rand.nextInt(3), IPlayerWarp.EnumWarpType.TEMPORARY);
            }
            playSound(SoundsTC.egscreech, 3.0f, 1.0f + rand.nextFloat() * 0.1f);
        }
    }
    
    public boolean isOnSameTeam(Entity el) {
        return el instanceof IEldritchMob;
    }
    
    public void setSwingingArms(boolean swingingArms) {
    }
}
