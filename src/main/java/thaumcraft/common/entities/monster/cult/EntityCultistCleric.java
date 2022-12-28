package thaumcraft.common.entities.monster.cult;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.ai.combat.AICultistHurtByTarget;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.ai.misc.AIAltarFocus;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.projectile.EntityGolemOrb;
import thaumcraft.common.lib.SoundsTC;


public class EntityCultistCleric extends EntityCultist implements IRangedAttackMob, IEntityAdditionalSpawnData
{
    public int rage;
    private static DataParameter<Boolean> RITUALIST;
    
    public EntityCultistCleric(World p_i1745_1_) {
        super(p_i1745_1_);
        rage = 0;
    }
    
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new AIAltarFocus(this));
        tasks.addTask(2, new AILongRangeAttack(this, 2.0, 1.0, 20, 40, 24.0f));
        tasks.addTask(3, new EntityAIAttackMelee(this, 1.0, false));
        tasks.addTask(4, new EntityAIRestrictOpenDoor(this));
        tasks.addTask(5, new EntityAIOpenDoor(this, true));
        tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.8));
        tasks.addTask(7, new EntityAIWander(this, 0.8));
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        tasks.addTask(8, new EntityAILookIdle(this));
        targetTasks.addTask(1, new AICultistHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityEldritchGuardian.class, true));
        targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, AbstractIllager.class, true));
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0);
    }
    
    @Override
    protected void setLoot(DifficultyInstance diff) {
        setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ItemsTC.crimsonRobeHelm));
        setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemsTC.crimsonRobeChest));
        setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ItemsTC.crimsonRobeLegs));
        if (rand.nextFloat() < ((world.getDifficulty() == EnumDifficulty.HARD) ? 0.3f : 0.1f)) {
            setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(ItemsTC.crimsonBoots));
        }
    }
    
    public void attackEntityWithRangedAttack(EntityLivingBase entitylivingbase, float f) {
        double d0 = entitylivingbase.posX - posX;
        double d2 = entitylivingbase.getEntityBoundingBox().minY + entitylivingbase.height / 2.0f - (posY + height / 2.0f);
        double d3 = entitylivingbase.posZ - posZ;
        swingArm(getActiveHand());
        float rf = rand.nextFloat();
        if (rf > 0.66f) {
            EntityGolemOrb blast = new EntityGolemOrb(world, this, entitylivingbase, true);
            Vec3d v = entitylivingbase.getPositionVector().addVector(entitylivingbase.motionX * 10.0, entitylivingbase.motionY * 10.0, entitylivingbase.motionZ * 10.0).subtract(getPositionVector()).normalize();
            blast.setPosition(blast.posX + v.x, blast.posY + v.y, blast.posZ + v.z);
            blast.shoot(v.x, v.y, v.z, 0.66f, 3.0f);
            playSound(SoundsTC.egattack, 1.0f, 1.0f + rand.nextFloat() * 0.1f);
            world.spawnEntity(blast);
        }
        else {
            float f2 = MathHelper.sqrt(f) * 0.5f;
            world.playEvent(null, 1009, getPosition(), 0);
            for (int i = 0; i < 3; ++i) {
                EntitySmallFireball entitysmallfireball = new EntitySmallFireball(world, this, d0 + rand.nextGaussian() * f2, d2, d3 + rand.nextGaussian() * f2);
                entitysmallfireball.posY = posY + height / 2.0f + 0.5;
                world.spawnEntity(entitysmallfireball);
            }
        }
    }
    
    @Override
    protected boolean canDespawn() {
        return !getIsRitualist();
    }
    
    public void entityInit() {
        super.entityInit();
        getDataManager().register(EntityCultistCleric.RITUALIST, false);
    }
    
    public boolean getIsRitualist() {
        return (boolean) getDataManager().get((DataParameter)EntityCultistCleric.RITUALIST);
    }
    
    public void setIsRitualist(boolean par1) {
        getDataManager().set(EntityCultistCleric.RITUALIST, par1);
    }
    
    public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
        if (isEntityInvulnerable(p_70097_1_)) {
            return false;
        }
        setIsRitualist(false);
        return super.attackEntityFrom(p_70097_1_, p_70097_2_);
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        setIsRitualist(par1NBTTagCompound.getBoolean("ritualist"));
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("ritualist", getIsRitualist());
    }
    
    public void writeSpawnData(ByteBuf data) {
        data.writeInt(getHomePosition().getX());
        data.writeInt(getHomePosition().getY());
        data.writeInt(getHomePosition().getZ());
    }
    
    public void readSpawnData(ByteBuf data) {
        setHomePosAndDistance(new BlockPos(data.readInt(), data.readInt(), data.readInt()), 8);
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote && getIsRitualist()) {
            double d0 = getHomePosition().getX() + 0.5 - posX;
            double d2 = getHomePosition().getY() + 1.5 - (posY + getEyeHeight());
            double d3 = getHomePosition().getZ() + 0.5 - posZ;
            double d4 = MathHelper.sqrt(d0 * d0 + d3 * d3);
            float f = (float)(Math.atan2(d3, d0) * 180.0 / 3.141592653589793) - 90.0f;
            float f2 = (float)(-(Math.atan2(d2, d4) * 180.0 / 3.141592653589793));
            rotationPitch = updateRotation(rotationPitch, f2, 10.0f);
            rotationYawHead = updateRotation(rotationYawHead, f, (float) getVerticalFaceSpeed());
        }
        if (!world.isRemote && getIsRitualist() && rage >= 5) {
            setIsRitualist(false);
        }
    }
    
    private float updateRotation(float p_75652_1_, float p_75652_2_, float p_75652_3_) {
        float f3 = MathHelper.wrapDegrees(p_75652_2_ - p_75652_1_);
        if (f3 > p_75652_3_) {
            f3 = p_75652_3_;
        }
        if (f3 < -p_75652_3_) {
            f3 = -p_75652_3_;
        }
        return p_75652_1_ + f3;
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundsTC.chant;
    }
    
    public int getTalkInterval() {
        return 500;
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte par1) {
        if (par1 == 19) {
            for (int i = 0; i < 3; ++i) {
                double d0 = rand.nextGaussian() * 0.02;
                double d2 = rand.nextGaussian() * 0.02;
                double d3 = rand.nextGaussian() * 0.02;
                world.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, posX + rand.nextFloat() * width * 2.0f - width, posY + 0.5 + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0f - width, d0, d2, d3);
            }
        }
        else {
            super.handleStatusUpdate(par1);
        }
    }
    
    public void setSwingingArms(boolean swingingArms) {
    }
    
    static {
        RITUALIST = EntityDataManager.createKey(EntityCultistCleric.class, DataSerializers.BOOLEAN);
    }
}
