package thaumcraft.common.entities.monster.tainted;
import java.util.ArrayList;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.client.fx.particles.FXSwarm;
import thaumcraft.common.blocks.world.taint.TaintHelper;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.SoundsTC;


public class EntityTaintSwarm extends EntityMob implements ITaintedMob
{
    private BlockPos currentFlightTarget;
    private static DataParameter<Boolean> SUMMONED;
    public int damBonus;
    public ArrayList<FXSwarm> swarm;
    private int attackTime;
    
    public EntityTaintSwarm(World par1World) {
        super(par1World);
        damBonus = 0;
        swarm = new ArrayList();
        setSize(2.0f, 2.0f);
    }
    
    public boolean canAttackClass(Class clazz) {
        return !ITaintedMob.class.isAssignableFrom(clazz);
    }
    
    public boolean isOnSameTeam(Entity otherEntity) {
        return otherEntity instanceof ITaintedMob || super.isOnSameTeam(otherEntity);
    }
    
    protected void entityInit() {
        super.entityInit();
        getDataManager().register(EntityTaintSwarm.SUMMONED, false);
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
    
    protected boolean canDespawn() {
        return true;
    }
    
    public float getBrightness() {
        return 1.0f;
    }
    
    protected float getSoundVolume() {
        return 0.1f;
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundsTC.swarm;
    }
    
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsTC.swarmattack;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundsTC.swarmattack;
    }
    
    public boolean canBePushed() {
        return false;
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2 + damBonus);
    }
    
    public boolean getIsSummoned() {
        return (boolean) getDataManager().get((DataParameter)EntityTaintSwarm.SUMMONED);
    }
    
    public void setIsSummoned(boolean par1) {
        getDataManager().set(EntityTaintSwarm.SUMMONED, par1);
    }
    
    public void onUpdate() {
        super.onUpdate();
        motionY *= 0.6000000238418579;
        if (world.isRemote) {
            for (int a = 0; a < swarm.size(); ++a) {
                if (swarm.get(a) == null || !swarm.get(a).isAlive()) {
                    swarm.remove(a);
                    break;
                }
            }
            if (swarm.size() < 30) {
                swarm.add(FXDispatcher.INSTANCE.swarmParticleFX(this, 0.22f, 15.0f, 0.08f));
            }
        }
    }
    
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (attackTime > 0) {
            --attackTime;
        }
        if (getAttackTarget() == null) {
            if (getIsSummoned()) {
                attackEntityFrom(DamageSource.GENERIC, 5.0f);
            }
            if (currentFlightTarget != null && (!world.isAirBlock(currentFlightTarget) || currentFlightTarget.getY() < 1 || currentFlightTarget.getY() > world.getPrecipitationHeight(currentFlightTarget).up(2).getY() || !TaintHelper.isNearTaintSeed(world, currentFlightTarget))) {
                currentFlightTarget = null;
            }
            if (currentFlightTarget == null || rand.nextInt(30) == 0 || getDistanceSqToCenter(currentFlightTarget) < 4.0) {
                currentFlightTarget = new BlockPos((int) posX + rand.nextInt(7) - rand.nextInt(7), (int) posY + rand.nextInt(6) - 2, (int) posZ + rand.nextInt(7) - rand.nextInt(7));
            }
            double var1 = currentFlightTarget.getX() + 0.5 - posX;
            double var2 = currentFlightTarget.getY() + 0.1 - posY;
            double var3 = currentFlightTarget.getZ() + 0.5 - posZ;
            motionX += (Math.signum(var1) * 0.5 - motionX) * 0.015000000014901161;
            motionY += (Math.signum(var2) * 0.699999988079071 - motionY) * 0.10000000149011612;
            motionZ += (Math.signum(var3) * 0.5 - motionZ) * 0.015000000014901161;
            float var4 = (float)(Math.atan2(motionZ, motionX) * 180.0 / 3.141592653589793) - 90.0f;
            float var5 = MathHelper.wrapDegrees(var4 - rotationYaw);
            moveForward = 0.1f;
            rotationYaw += var5;
        }
        else if (getAttackTarget() != null) {
            double var1 = getAttackTarget().posX - posX;
            double var2 = getAttackTarget().posY + getAttackTarget().getEyeHeight() - posY;
            double var3 = getAttackTarget().posZ - posZ;
            motionX += (Math.signum(var1) * 0.5 - motionX) * 0.025000000149011613;
            motionY += (Math.signum(var2) * 0.699999988079071 - motionY) * 0.10000000149011612;
            motionZ += (Math.signum(var3) * 0.5 - motionZ) * 0.02500000001490116;
            float var4 = (float)(Math.atan2(motionZ, motionX) * 180.0 / 3.141592653589793) - 90.0f;
            float var5 = MathHelper.wrapDegrees(var4 - rotationYaw);
            moveForward = 0.1f;
            rotationYaw += var5;
        }
        if (getAttackTarget() == null) {
            setAttackTarget((EntityLivingBase) findPlayerToAttack());
        }
        else if (isEntityAlive() && getAttackTarget().isEntityAlive()) {
            float f = getAttackTarget().getDistance(this);
            if (canEntityBeSeen(getAttackTarget())) {
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
    
    protected void updateAITasks() {
        super.updateAITasks();
    }
    
    protected boolean canTriggerWalking() {
        return false;
    }
    
    public void fall(float distance, float damageMultiplier) {
    }
    
    public boolean doesEntityNotTriggerPressurePlate() {
        return true;
    }
    
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        return !isEntityInvulnerable(par1DamageSource) && super.attackEntityFrom(par1DamageSource, par2);
    }
    
    protected void attackEntity(Entity par1Entity, float par2) {
        if (attackTime <= 0 && par2 < 3.0f && par1Entity.getEntityBoundingBox().maxY > getEntityBoundingBox().minY && par1Entity.getEntityBoundingBox().minY < getEntityBoundingBox().maxY) {
            if (getIsSummoned()) {
                ((EntityLivingBase)par1Entity).recentlyHit = 100;
            }
            attackTime = 15 + rand.nextInt(10);
            double mx = par1Entity.motionX;
            double my = par1Entity.motionY;
            double mz = par1Entity.motionZ;
            if (attackEntityAsMob(par1Entity) && !world.isRemote && par1Entity instanceof EntityLivingBase) {
                ((EntityLivingBase)par1Entity).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100, 0));
            }
            par1Entity.isAirBorne = false;
            par1Entity.motionX = mx;
            par1Entity.motionY = my;
            par1Entity.motionZ = mz;
            playSound(SoundsTC.swarmattack, 0.3f, 0.9f + world.rand.nextFloat() * 0.2f);
        }
    }
    
    protected Entity findPlayerToAttack() {
        double var1 = 8.0;
        return getIsSummoned() ? null : world.getClosestPlayerToEntity(this, var1);
    }
    
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        setIsSummoned(par1NBTTagCompound.getBoolean("summoned"));
        damBonus = par1NBTTagCompound.getByte("damBonus");
    }
    
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("summoned", getIsSummoned());
        par1NBTTagCompound.setByte("damBonus", (byte) damBonus);
    }
    
    public boolean getCanSpawnHere() {
        int var1 = MathHelper.floor(getEntityBoundingBox().minY);
        int var2 = MathHelper.floor(posX);
        int var3 = MathHelper.floor(posZ);
        int var4 = world.getLight(new BlockPos(var2, var1, var3));
        byte var5 = 7;
        return var4 <= rand.nextInt(var5) && super.getCanSpawnHere();
    }
    
    protected boolean isValidLightLevel() {
        return true;
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected void dropFewItems(boolean flag, int i) {
        if (world.rand.nextBoolean()) {
            entityDropItem(ConfigItems.FLUX_CRYSTAL.copy(), height / 2.0f);
        }
    }
    
    static {
        SUMMONED = EntityDataManager.createKey(EntityTaintSwarm.class, DataSerializers.BOOLEAN);
    }
}
