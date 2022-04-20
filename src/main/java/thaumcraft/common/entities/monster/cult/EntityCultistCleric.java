// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.cult;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.entities.projectile.EntityGolemOrb;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.AbstractIllager;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import thaumcraft.common.entities.ai.combat.AICultistHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.ai.misc.AIAltarFocus;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.world.World;
import net.minecraft.network.datasync.DataParameter;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.IRangedAttackMob;

public class EntityCultistCleric extends EntityCultist implements IRangedAttackMob, IEntityAdditionalSpawnData
{
    public int rage;
    private static final DataParameter<Boolean> RITUALIST;
    
    public EntityCultistCleric(final World p_i1745_1_) {
        super(p_i1745_1_);
        this.rage = 0;
    }
    
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new AIAltarFocus(this));
        this.tasks.addTask(2, new AILongRangeAttack(this, 2.0, 1.0, 20, 40, 24.0f));
        this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.0, false));
        this.tasks.addTask(4, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(5, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.8));
        this.tasks.addTask(7, new EntityAIWander(this, 0.8));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new AICultistHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityEldritchGuardian.class, true));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, AbstractIllager.class, true));
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0);
    }
    
    @Override
    protected void setLoot(final DifficultyInstance diff) {
        this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ItemsTC.crimsonRobeHelm));
        this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemsTC.crimsonRobeChest));
        this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ItemsTC.crimsonRobeLegs));
        if (this.rand.nextFloat() < ((this.world.getDifficulty() == EnumDifficulty.HARD) ? 0.3f : 0.1f)) {
            this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(ItemsTC.crimsonBoots));
        }
    }
    
    public void attackEntityWithRangedAttack(final EntityLivingBase entitylivingbase, final float f) {
        final double d0 = entitylivingbase.posX - this.posX;
        final double d2 = entitylivingbase.getEntityBoundingBox().minY + entitylivingbase.height / 2.0f - (this.posY + this.height / 2.0f);
        final double d3 = entitylivingbase.posZ - this.posZ;
        this.swingArm(this.getActiveHand());
        final float rf = this.rand.nextFloat();
        if (rf > 0.66f) {
            final EntityGolemOrb blast = new EntityGolemOrb(this.world, this, entitylivingbase, true);
            final Vec3d v = entitylivingbase.getPositionVector().addVector(entitylivingbase.motionX * 10.0, entitylivingbase.motionY * 10.0, entitylivingbase.motionZ * 10.0).subtract(this.getPositionVector()).normalize();
            blast.setPosition(blast.posX + v.x, blast.posY + v.y, blast.posZ + v.z);
            blast.shoot(v.x, v.y, v.z, 0.66f, 3.0f);
            this.playSound(SoundsTC.egattack, 1.0f, 1.0f + this.rand.nextFloat() * 0.1f);
            this.world.spawnEntity(blast);
        }
        else {
            final float f2 = MathHelper.sqrt(f) * 0.5f;
            this.world.playEvent(null, 1009, this.getPosition(), 0);
            for (int i = 0; i < 3; ++i) {
                final EntitySmallFireball entitysmallfireball = new EntitySmallFireball(this.world, this, d0 + this.rand.nextGaussian() * f2, d2, d3 + this.rand.nextGaussian() * f2);
                entitysmallfireball.posY = this.posY + this.height / 2.0f + 0.5;
                this.world.spawnEntity(entitysmallfireball);
            }
        }
    }
    
    @Override
    protected boolean canDespawn() {
        return !this.getIsRitualist();
    }
    
    public void entityInit() {
        super.entityInit();
        this.getDataManager().register(EntityCultistCleric.RITUALIST, false);
    }
    
    public boolean getIsRitualist() {
        return (boolean)this.getDataManager().get((DataParameter)EntityCultistCleric.RITUALIST);
    }
    
    public void setIsRitualist(final boolean par1) {
        this.getDataManager().set(EntityCultistCleric.RITUALIST, par1);
    }
    
    public boolean attackEntityFrom(final DamageSource p_70097_1_, final float p_70097_2_) {
        if (this.isEntityInvulnerable(p_70097_1_)) {
            return false;
        }
        this.setIsRitualist(false);
        return super.attackEntityFrom(p_70097_1_, p_70097_2_);
    }
    
    @Override
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.setIsRitualist(par1NBTTagCompound.getBoolean("ritualist"));
    }
    
    @Override
    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("ritualist", this.getIsRitualist());
    }
    
    public void writeSpawnData(final ByteBuf data) {
        data.writeInt(this.getHomePosition().getX());
        data.writeInt(this.getHomePosition().getY());
        data.writeInt(this.getHomePosition().getZ());
    }
    
    public void readSpawnData(final ByteBuf data) {
        this.setHomePosAndDistance(new BlockPos(data.readInt(), data.readInt(), data.readInt()), 8);
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (this.world.isRemote && this.getIsRitualist()) {
            final double d0 = this.getHomePosition().getX() + 0.5 - this.posX;
            final double d2 = this.getHomePosition().getY() + 1.5 - (this.posY + this.getEyeHeight());
            final double d3 = this.getHomePosition().getZ() + 0.5 - this.posZ;
            final double d4 = MathHelper.sqrt(d0 * d0 + d3 * d3);
            final float f = (float)(Math.atan2(d3, d0) * 180.0 / 3.141592653589793) - 90.0f;
            final float f2 = (float)(-(Math.atan2(d2, d4) * 180.0 / 3.141592653589793));
            this.rotationPitch = this.updateRotation(this.rotationPitch, f2, 10.0f);
            this.rotationYawHead = this.updateRotation(this.rotationYawHead, f, (float)this.getVerticalFaceSpeed());
        }
        if (!this.world.isRemote && this.getIsRitualist() && this.rage >= 5) {
            this.setIsRitualist(false);
        }
    }
    
    private float updateRotation(final float p_75652_1_, final float p_75652_2_, final float p_75652_3_) {
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
    public void handleStatusUpdate(final byte par1) {
        if (par1 == 19) {
            for (int i = 0; i < 3; ++i) {
                final double d0 = this.rand.nextGaussian() * 0.02;
                final double d2 = this.rand.nextGaussian() * 0.02;
                final double d3 = this.rand.nextGaussian() * 0.02;
                this.world.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, this.posX + this.rand.nextFloat() * this.width * 2.0f - this.width, this.posY + 0.5 + this.rand.nextFloat() * this.height, this.posZ + this.rand.nextFloat() * this.width * 2.0f - this.width, d0, d2, d3, new int[0]);
            }
        }
        else {
            super.handleStatusUpdate(par1);
        }
    }
    
    public void setSwingingArms(final boolean swingingArms) {
    }
    
    static {
        RITUALIST = EntityDataManager.createKey(EntityCultistCleric.class, DataSerializers.BOOLEAN);
    }
}
