// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.tainted;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import thaumcraft.client.fx.particles.FXSwarm;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.blocks.world.taint.TaintHelper;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.DamageSource;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import java.util.ArrayList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.entities.ITaintedMob;
import net.minecraft.entity.monster.EntityMob;

public class EntityTaintSwarm extends EntityMob implements ITaintedMob
{
    private BlockPos currentFlightTarget;
    private static final DataParameter<Boolean> SUMMONED;
    public int damBonus;
    public ArrayList<FXSwarm> swarm;
    private int attackTime;
    
    public EntityTaintSwarm(final World par1World) {
        super(par1World);
        this.damBonus = 0;
        this.swarm = new ArrayList();
        this.setSize(2.0f, 2.0f);
    }
    
    public boolean canAttackClass(final Class clazz) {
        return !ITaintedMob.class.isAssignableFrom(clazz);
    }
    
    public boolean isOnSameTeam(final Entity otherEntity) {
        return otherEntity instanceof ITaintedMob || super.isOnSameTeam(otherEntity);
    }
    
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(EntityTaintSwarm.SUMMONED, false);
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
    
    protected SoundEvent getHurtSound(final DamageSource damageSourceIn) {
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
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2 + this.damBonus);
    }
    
    public boolean getIsSummoned() {
        return (boolean)this.getDataManager().get((DataParameter)EntityTaintSwarm.SUMMONED);
    }
    
    public void setIsSummoned(final boolean par1) {
        this.getDataManager().set(EntityTaintSwarm.SUMMONED, par1);
    }
    
    public void onUpdate() {
        super.onUpdate();
        this.motionY *= 0.6000000238418579;
        if (this.world.isRemote) {
            for (int a = 0; a < this.swarm.size(); ++a) {
                if (this.swarm.get(a) == null || !this.swarm.get(a).isAlive()) {
                    this.swarm.remove(a);
                    break;
                }
            }
            if (this.swarm.size() < 30) {
                this.swarm.add(FXDispatcher.INSTANCE.swarmParticleFX(this, 0.22f, 15.0f, 0.08f));
            }
        }
    }
    
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.attackTime > 0) {
            --this.attackTime;
        }
        if (this.getAttackTarget() == null) {
            if (this.getIsSummoned()) {
                this.attackEntityFrom(DamageSource.GENERIC, 5.0f);
            }
            if (this.currentFlightTarget != null && (!this.world.isAirBlock(this.currentFlightTarget) || this.currentFlightTarget.getY() < 1 || this.currentFlightTarget.getY() > this.world.getPrecipitationHeight(this.currentFlightTarget).up(2).getY() || !TaintHelper.isNearTaintSeed(this.world, this.currentFlightTarget))) {
                this.currentFlightTarget = null;
            }
            if (this.currentFlightTarget == null || this.rand.nextInt(30) == 0 || this.getDistanceSqToCenter(this.currentFlightTarget) < 4.0) {
                this.currentFlightTarget = new BlockPos((int)this.posX + this.rand.nextInt(7) - this.rand.nextInt(7), (int)this.posY + this.rand.nextInt(6) - 2, (int)this.posZ + this.rand.nextInt(7) - this.rand.nextInt(7));
            }
            final double var1 = this.currentFlightTarget.getX() + 0.5 - this.posX;
            final double var2 = this.currentFlightTarget.getY() + 0.1 - this.posY;
            final double var3 = this.currentFlightTarget.getZ() + 0.5 - this.posZ;
            this.motionX += (Math.signum(var1) * 0.5 - this.motionX) * 0.015000000014901161;
            this.motionY += (Math.signum(var2) * 0.699999988079071 - this.motionY) * 0.10000000149011612;
            this.motionZ += (Math.signum(var3) * 0.5 - this.motionZ) * 0.015000000014901161;
            final float var4 = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0 / 3.141592653589793) - 90.0f;
            final float var5 = MathHelper.wrapDegrees(var4 - this.rotationYaw);
            this.moveForward = 0.1f;
            this.rotationYaw += var5;
        }
        else if (this.getAttackTarget() != null) {
            final double var1 = this.getAttackTarget().posX - this.posX;
            final double var2 = this.getAttackTarget().posY + this.getAttackTarget().getEyeHeight() - this.posY;
            final double var3 = this.getAttackTarget().posZ - this.posZ;
            this.motionX += (Math.signum(var1) * 0.5 - this.motionX) * 0.025000000149011613;
            this.motionY += (Math.signum(var2) * 0.699999988079071 - this.motionY) * 0.10000000149011612;
            this.motionZ += (Math.signum(var3) * 0.5 - this.motionZ) * 0.02500000001490116;
            final float var4 = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0 / 3.141592653589793) - 90.0f;
            final float var5 = MathHelper.wrapDegrees(var4 - this.rotationYaw);
            this.moveForward = 0.1f;
            this.rotationYaw += var5;
        }
        if (this.getAttackTarget() == null) {
            this.setAttackTarget((EntityLivingBase)this.findPlayerToAttack());
        }
        else if (this.isEntityAlive() && this.getAttackTarget().isEntityAlive()) {
            final float f = this.getAttackTarget().getDistance(this);
            if (this.canEntityBeSeen(this.getAttackTarget())) {
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
    
    protected void updateAITasks() {
        super.updateAITasks();
    }
    
    protected boolean canTriggerWalking() {
        return false;
    }
    
    public void fall(final float distance, final float damageMultiplier) {
    }
    
    public boolean doesEntityNotTriggerPressurePlate() {
        return true;
    }
    
    public boolean attackEntityFrom(final DamageSource par1DamageSource, final float par2) {
        return !this.isEntityInvulnerable(par1DamageSource) && super.attackEntityFrom(par1DamageSource, par2);
    }
    
    protected void attackEntity(final Entity par1Entity, final float par2) {
        if (this.attackTime <= 0 && par2 < 3.0f && par1Entity.getEntityBoundingBox().maxY > this.getEntityBoundingBox().minY && par1Entity.getEntityBoundingBox().minY < this.getEntityBoundingBox().maxY) {
            if (this.getIsSummoned()) {
                ((EntityLivingBase)par1Entity).recentlyHit = 100;
            }
            this.attackTime = 15 + this.rand.nextInt(10);
            final double mx = par1Entity.motionX;
            final double my = par1Entity.motionY;
            final double mz = par1Entity.motionZ;
            if (this.attackEntityAsMob(par1Entity) && !this.world.isRemote && par1Entity instanceof EntityLivingBase) {
                ((EntityLivingBase)par1Entity).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100, 0));
            }
            par1Entity.isAirBorne = false;
            par1Entity.motionX = mx;
            par1Entity.motionY = my;
            par1Entity.motionZ = mz;
            this.playSound(SoundsTC.swarmattack, 0.3f, 0.9f + this.world.rand.nextFloat() * 0.2f);
        }
    }
    
    protected Entity findPlayerToAttack() {
        final double var1 = 8.0;
        return this.getIsSummoned() ? null : this.world.getClosestPlayerToEntity(this, var1);
    }
    
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.setIsSummoned(par1NBTTagCompound.getBoolean("summoned"));
        this.damBonus = par1NBTTagCompound.getByte("damBonus");
    }
    
    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("summoned", this.getIsSummoned());
        par1NBTTagCompound.setByte("damBonus", (byte)this.damBonus);
    }
    
    public boolean getCanSpawnHere() {
        final int var1 = MathHelper.floor(this.getEntityBoundingBox().minY);
        final int var2 = MathHelper.floor(this.posX);
        final int var3 = MathHelper.floor(this.posZ);
        final int var4 = this.world.getLight(new BlockPos(var2, var1, var3));
        final byte var5 = 7;
        return var4 <= this.rand.nextInt(var5) && super.getCanSpawnHere();
    }
    
    protected boolean isValidLightLevel() {
        return true;
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected void dropFewItems(final boolean flag, final int i) {
        if (this.world.rand.nextBoolean()) {
            this.entityDropItem(ConfigItems.FLUX_CRYSTAL.copy(), this.height / 2.0f);
        }
    }
    
    static {
        SUMMONED = EntityDataManager.createKey(EntityTaintSwarm.class, DataSerializers.BOOLEAN);
    }
}
