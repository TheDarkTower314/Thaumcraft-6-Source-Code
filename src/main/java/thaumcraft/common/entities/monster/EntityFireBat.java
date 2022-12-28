package thaumcraft.common.entities.monster;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class EntityFireBat extends EntityMob
{
    private BlockPos currentFlightTarget;
    public EntityLivingBase owner;
    private static DataParameter<Boolean> HANGING;
    public int damBonus;
    private int attackTime;
    
    public EntityFireBat(World par1World) {
        super(par1World);
        owner = null;
        damBonus = 0;
        setSize(0.5f, 0.9f);
        setIsBatHanging(true);
        isImmuneToFire = true;
    }
    
    public void entityInit() {
        super.entityInit();
        getDataManager().register(EntityFireBat.HANGING, false);
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
        return (getIsBatHanging() && rand.nextInt(4) != 0) ? null : SoundEvents.ENTITY_BAT_AMBIENT;
    }
    
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
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
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
    }
    
    public boolean getIsBatHanging() {
        return (boolean) getDataManager().get((DataParameter)EntityFireBat.HANGING);
    }
    
    public void setIsBatHanging(boolean par1) {
        getDataManager().set(EntityFireBat.HANGING, par1);
    }
    
    public void onLivingUpdate() {
        if (isWet()) {
            attackEntityFrom(DamageSource.DROWN, 1.0f);
        }
        super.onLivingUpdate();
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (getIsBatHanging()) {
            double motionX = 0.0;
            motionZ = motionX;
            motionY = motionX;
            this.motionX = motionX;
            posY = MathHelper.floor(posY) + 1.0 - height;
        }
        else {
            motionY *= 0.6000000238418579;
        }
    }
    
    protected void updateAITasks() {
        super.updateAITasks();
        if (attackTime > 0) {
            --attackTime;
        }
        BlockPos blockpos = new BlockPos(this);
        BlockPos blockpos2 = blockpos.up();
        if (getIsBatHanging()) {
            if (!world.getBlockState(blockpos2).isNormalCube()) {
                setIsBatHanging(false);
                world.playEvent(null, 1025, blockpos, 0);
            }
            else {
                if (rand.nextInt(200) == 0) {
                    rotationYawHead = (float) rand.nextInt(360);
                }
                if (world.getClosestPlayerToEntity(this, 4.0) != null) {
                    setIsBatHanging(false);
                    world.playEvent(null, 1025, blockpos, 0);
                }
            }
        }
        else if (getAttackTarget() == null) {
            if (currentFlightTarget != null && (!world.isAirBlock(currentFlightTarget) || currentFlightTarget.getY() < 1)) {
                currentFlightTarget = null;
            }
            if (currentFlightTarget == null || rand.nextInt(30) == 0 || getDistanceSqToCenter(currentFlightTarget) < 4.0) {
                currentFlightTarget = new BlockPos((int) posX + rand.nextInt(7) - rand.nextInt(7), (int) posY + rand.nextInt(6) - 2, (int) posZ + rand.nextInt(7) - rand.nextInt(7));
            }
            double var1 = currentFlightTarget.getX() + 0.5 - posX;
            double var2 = currentFlightTarget.getY() + 0.1 - posY;
            double var3 = currentFlightTarget.getZ() + 0.5 - posZ;
            motionX += (Math.signum(var1) * 0.5 - motionX) * 0.10000000149011612;
            motionY += (Math.signum(var2) * 0.699999988079071 - motionY) * 0.10000000149011612;
            motionZ += (Math.signum(var3) * 0.5 - motionZ) * 0.10000000149011612;
            float var4 = (float)(Math.atan2(motionZ, motionX) * 180.0 / 3.141592653589793) - 90.0f;
            float var5 = MathHelper.wrapDegrees(var4 - rotationYaw);
            moveForward = 0.5f;
            rotationYaw += var5;
            if (rand.nextInt(100) == 0 && world.getBlockState(blockpos2).isNormalCube()) {
                setIsBatHanging(true);
            }
        }
        else {
            double var1 = getAttackTarget().posX - posX;
            double var2 = getAttackTarget().posY + getAttackTarget().getEyeHeight() * 0.66f - posY;
            double var3 = getAttackTarget().posZ - posZ;
            motionX += (Math.signum(var1) * 0.5 - motionX) * 0.10000000149011612;
            motionY += (Math.signum(var2) * 0.699999988079071 - motionY) * 0.10000000149011612;
            motionZ += (Math.signum(var3) * 0.5 - motionZ) * 0.10000000149011612;
            float var4 = (float)(Math.atan2(motionZ, motionX) * 180.0 / 3.141592653589793) - 90.0f;
            float var5 = MathHelper.wrapDegrees(var4 - rotationYaw);
            moveForward = 0.5f;
            rotationYaw += var5;
        }
        if (getAttackTarget() == null) {
            setAttackTarget(findPlayerToAttack());
        }
        else if (getAttackTarget().isEntityAlive()) {
            float f = getAttackTarget().getDistance(this);
            if (isEntityAlive() && canEntityBeSeen(getAttackTarget())) {
                attackEntity(getAttackTarget(), f);
            }
        }
        else {
            setAttackTarget(null);
        }
        if (getAttackTarget() instanceof EntityPlayer && ((EntityPlayer) getAttackTarget()).capabilities.disableDamage) {
            setAttackTarget(null);
        }
    }
    
    protected boolean canTriggerWalking() {
        return false;
    }
    
    public void fall(float par1, float damageMultiplier) {
    }
    
    protected void updateFallState(double p_180433_1_, boolean p_180433_3_, IBlockState state, BlockPos pos) {
    }
    
    public boolean doesEntityNotTriggerPressurePlate() {
        return true;
    }
    
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if (isEntityInvulnerable(par1DamageSource) || par1DamageSource.isFireDamage() || par1DamageSource.isExplosion()) {
            return false;
        }
        if (!world.isRemote && getIsBatHanging()) {
            setIsBatHanging(false);
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }
    
    protected void attackEntity(Entity entity, float par2) {
        if (attackTime <= 0 && par2 < Math.max(2.5f, entity.width * 1.1f) && entity.getEntityBoundingBox().maxY > getEntityBoundingBox().minY && entity.getEntityBoundingBox().minY < getEntityBoundingBox().maxY) {
            attackTime = 20 + world.rand.nextInt(20);
            if (world.rand.nextInt(10) == 0 && !world.isRemote) {
                entity.hurtResistantTime = 0;
                world.newExplosion(this, posX, posY, posZ, 1.5f, false, false);
                setDead();
            }
            playSound(SoundEvents.ENTITY_BAT_HURT, 0.5f, 0.9f + world.rand.nextFloat() * 0.2f);
            attackEntityAsMob(entity);
        }
    }
    
    protected EntityLivingBase findPlayerToAttack() {
        double var1 = 12.0;
        return world.getClosestPlayerToEntity(this, var1);
    }
    
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setIsBatHanging(nbt.getBoolean("hang"));
        damBonus = nbt.getByte("damBonus");
    }
    
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("hang", getIsBatHanging());
        nbt.setByte("damBonus", (byte) damBonus);
    }
    
    public boolean getCanSpawnHere() {
        int i = MathHelper.floor(posX);
        int j = MathHelper.floor(getEntityBoundingBox().minY);
        int k = MathHelper.floor(posZ);
        BlockPos blockpos = new BlockPos(i, j, k);
        int var4 = world.getLight(blockpos);
        byte var5 = 7;
        return var4 <= rand.nextInt(var5) && super.getCanSpawnHere();
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
