// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.DamageSource;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.world.World;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.monster.EntityMob;

public class EntityFireBat extends EntityMob
{
    private BlockPos currentFlightTarget;
    public EntityLivingBase owner;
    private static final DataParameter<Boolean> HANGING;
    public int damBonus;
    private int attackTime;
    
    public EntityFireBat(final World par1World) {
        super(par1World);
        this.owner = null;
        this.damBonus = 0;
        this.setSize(0.5f, 0.9f);
        this.setIsBatHanging(true);
        this.isImmuneToFire = true;
    }
    
    public void entityInit() {
        super.entityInit();
        this.getDataManager().register(EntityFireBat.HANGING, false);
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
    
    public float getBrightness() {
        return 1.0f;
    }
    
    protected float getSoundVolume() {
        return 0.1f;
    }
    
    protected float getSoundPitch() {
        return super.getSoundPitch() * 0.95f;
    }
    
    protected SoundEvent getAmbientSound() {
        return (this.getIsBatHanging() && this.rand.nextInt(4) != 0) ? null : SoundEvents.ENTITY_BAT_AMBIENT;
    }
    
    protected SoundEvent getHurtSound(final DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_BAT_HURT;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BAT_DEATH;
    }
    
    public boolean canBePushed() {
        return false;
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
    }
    
    public boolean getIsBatHanging() {
        return (boolean)this.getDataManager().get((DataParameter)EntityFireBat.HANGING);
    }
    
    public void setIsBatHanging(final boolean par1) {
        this.getDataManager().set(EntityFireBat.HANGING, par1);
    }
    
    public void onLivingUpdate() {
        if (this.isWet()) {
            this.attackEntityFrom(DamageSource.DROWN, 1.0f);
        }
        super.onLivingUpdate();
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (this.getIsBatHanging()) {
            final double motionX = 0.0;
            this.motionZ = motionX;
            this.motionY = motionX;
            this.motionX = motionX;
            this.posY = MathHelper.floor(this.posY) + 1.0 - this.height;
        }
        else {
            this.motionY *= 0.6000000238418579;
        }
    }
    
    protected void updateAITasks() {
        super.updateAITasks();
        if (this.attackTime > 0) {
            --this.attackTime;
        }
        final BlockPos blockpos = new BlockPos(this);
        final BlockPos blockpos2 = blockpos.up();
        if (this.getIsBatHanging()) {
            if (!this.world.getBlockState(blockpos2).isNormalCube()) {
                this.setIsBatHanging(false);
                this.world.playEvent(null, 1025, blockpos, 0);
            }
            else {
                if (this.rand.nextInt(200) == 0) {
                    this.rotationYawHead = (float)this.rand.nextInt(360);
                }
                if (this.world.getClosestPlayerToEntity(this, 4.0) != null) {
                    this.setIsBatHanging(false);
                    this.world.playEvent(null, 1025, blockpos, 0);
                }
            }
        }
        else if (this.getAttackTarget() == null) {
            if (this.currentFlightTarget != null && (!this.world.isAirBlock(this.currentFlightTarget) || this.currentFlightTarget.getY() < 1)) {
                this.currentFlightTarget = null;
            }
            if (this.currentFlightTarget == null || this.rand.nextInt(30) == 0 || this.getDistanceSqToCenter(this.currentFlightTarget) < 4.0) {
                this.currentFlightTarget = new BlockPos((int)this.posX + this.rand.nextInt(7) - this.rand.nextInt(7), (int)this.posY + this.rand.nextInt(6) - 2, (int)this.posZ + this.rand.nextInt(7) - this.rand.nextInt(7));
            }
            final double var1 = this.currentFlightTarget.getX() + 0.5 - this.posX;
            final double var2 = this.currentFlightTarget.getY() + 0.1 - this.posY;
            final double var3 = this.currentFlightTarget.getZ() + 0.5 - this.posZ;
            this.motionX += (Math.signum(var1) * 0.5 - this.motionX) * 0.10000000149011612;
            this.motionY += (Math.signum(var2) * 0.699999988079071 - this.motionY) * 0.10000000149011612;
            this.motionZ += (Math.signum(var3) * 0.5 - this.motionZ) * 0.10000000149011612;
            final float var4 = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0 / 3.141592653589793) - 90.0f;
            final float var5 = MathHelper.wrapDegrees(var4 - this.rotationYaw);
            this.moveForward = 0.5f;
            this.rotationYaw += var5;
            if (this.rand.nextInt(100) == 0 && this.world.getBlockState(blockpos2).isNormalCube()) {
                this.setIsBatHanging(true);
            }
        }
        else {
            final double var1 = this.getAttackTarget().posX - this.posX;
            final double var2 = this.getAttackTarget().posY + this.getAttackTarget().getEyeHeight() * 0.66f - this.posY;
            final double var3 = this.getAttackTarget().posZ - this.posZ;
            this.motionX += (Math.signum(var1) * 0.5 - this.motionX) * 0.10000000149011612;
            this.motionY += (Math.signum(var2) * 0.699999988079071 - this.motionY) * 0.10000000149011612;
            this.motionZ += (Math.signum(var3) * 0.5 - this.motionZ) * 0.10000000149011612;
            final float var4 = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0 / 3.141592653589793) - 90.0f;
            final float var5 = MathHelper.wrapDegrees(var4 - this.rotationYaw);
            this.moveForward = 0.5f;
            this.rotationYaw += var5;
        }
        if (this.getAttackTarget() == null) {
            this.setAttackTarget(this.findPlayerToAttack());
        }
        else if (this.getAttackTarget().isEntityAlive()) {
            final float f = this.getAttackTarget().getDistance(this);
            if (this.isEntityAlive() && this.canEntityBeSeen(this.getAttackTarget())) {
                this.attackEntity(this.getAttackTarget(), f);
            }
        }
        else {
            this.setAttackTarget(null);
        }
        if (this.getAttackTarget() instanceof EntityPlayer && ((EntityPlayer)this.getAttackTarget()).capabilities.disableDamage) {
            this.setAttackTarget(null);
        }
    }
    
    protected boolean canTriggerWalking() {
        return false;
    }
    
    public void fall(final float par1, final float damageMultiplier) {
    }
    
    protected void updateFallState(final double p_180433_1_, final boolean p_180433_3_, final IBlockState state, final BlockPos pos) {
    }
    
    public boolean doesEntityNotTriggerPressurePlate() {
        return true;
    }
    
    public boolean attackEntityFrom(final DamageSource par1DamageSource, final float par2) {
        if (this.isEntityInvulnerable(par1DamageSource) || par1DamageSource.isFireDamage() || par1DamageSource.isExplosion()) {
            return false;
        }
        if (!this.world.isRemote && this.getIsBatHanging()) {
            this.setIsBatHanging(false);
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }
    
    protected void attackEntity(final Entity entity, final float par2) {
        if (this.attackTime <= 0 && par2 < Math.max(2.5f, entity.width * 1.1f) && entity.getEntityBoundingBox().maxY > this.getEntityBoundingBox().minY && entity.getEntityBoundingBox().minY < this.getEntityBoundingBox().maxY) {
            this.attackTime = 20 + this.world.rand.nextInt(20);
            if (this.world.rand.nextInt(10) == 0 && !this.world.isRemote) {
                entity.hurtResistantTime = 0;
                this.world.newExplosion(this, this.posX, this.posY, this.posZ, 1.5f, false, false);
                this.setDead();
            }
            this.playSound(SoundEvents.ENTITY_BAT_HURT, 0.5f, 0.9f + this.world.rand.nextFloat() * 0.2f);
            this.attackEntityAsMob(entity);
        }
    }
    
    protected EntityLivingBase findPlayerToAttack() {
        final double var1 = 12.0;
        return this.world.getClosestPlayerToEntity(this, var1);
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setIsBatHanging(nbt.getBoolean("hang"));
        this.damBonus = nbt.getByte("damBonus");
    }
    
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("hang", this.getIsBatHanging());
        nbt.setByte("damBonus", (byte)this.damBonus);
    }
    
    public boolean getCanSpawnHere() {
        final int i = MathHelper.floor(this.posX);
        final int j = MathHelper.floor(this.getEntityBoundingBox().minY);
        final int k = MathHelper.floor(this.posZ);
        final BlockPos blockpos = new BlockPos(i, j, k);
        final int var4 = this.world.getLight(blockpos);
        final byte var5 = 7;
        return var4 <= this.rand.nextInt(var5) && super.getCanSpawnHere();
    }
    
    protected Item getDropItem() {
        return Items.GUNPOWDER;
    }
    
    protected boolean isValidLightLevel() {
        return true;
    }
    
    static {
        HANGING = EntityDataManager.createKey(EntityFireBat.class, DataSerializers.BOOLEAN);
    }
}
