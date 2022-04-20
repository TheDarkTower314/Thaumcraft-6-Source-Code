// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumSkyBlock;
import java.util.List;
import thaumcraft.api.ThaumcraftApiHelper;
import net.minecraft.item.Item;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import java.util.ArrayList;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.fx.PacketFXWispZap;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.world.EnumDifficulty;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.EntityFlying;

public class EntityWisp extends EntityFlying implements IMob
{
    public int courseChangeCooldown;
    public double waypointX;
    public double waypointY;
    public double waypointZ;
    private int aggroCooldown;
    public int prevAttackCounter;
    public int attackCounter;
    private BlockPos currentFlightTarget;
    private static final DataParameter<String> TYPE;
    
    public EntityWisp(final World world) {
        super(world);
        this.courseChangeCooldown = 0;
        this.aggroCooldown = 0;
        this.prevAttackCounter = 0;
        this.attackCounter = 0;
        this.setSize(0.9f, 0.9f);
        this.experienceValue = 5;
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(22.0);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
    }
    
    protected boolean canTriggerWalking() {
        return false;
    }
    
    public int decreaseAirSupply(final int par1) {
        return par1;
    }
    
    public boolean attackEntityFrom(final DamageSource damagesource, final float i) {
        if (damagesource.getTrueSource() instanceof EntityLivingBase) {
            this.setAttackTarget((EntityLivingBase)damagesource.getTrueSource());
            this.aggroCooldown = 200;
        }
        return super.attackEntityFrom(damagesource, i);
    }
    
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(EntityWisp.TYPE, String.valueOf(""));
    }
    
    public void onDeath(final DamageSource par1DamageSource) {
        super.onDeath(par1DamageSource);
        if (this.world.isRemote) {
            FXDispatcher.INSTANCE.burst(this.posX, this.posY + 0.44999998807907104, this.posZ, 1.0f);
        }
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (this.world.isRemote && this.ticksExisted <= 1) {
            FXDispatcher.INSTANCE.burst(this.posX, this.posY, this.posZ, 10.0f);
        }
        if (this.world.isRemote && this.world.rand.nextBoolean() && Aspect.getAspect(this.getType()) != null) {
            FXDispatcher.INSTANCE.drawWispParticles(this.posX + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.7f, this.posY + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.7f, this.posZ + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.7f, 0.0, 0.0, 0.0, Aspect.getAspect(this.getType()).getColor(), 0);
        }
        this.motionY *= 0.6000000238418579;
    }
    
    public String getType() {
        return (String)this.getDataManager().get((DataParameter)EntityWisp.TYPE);
    }
    
    public void setType(final String t) {
        this.getDataManager().set(EntityWisp.TYPE, String.valueOf(t));
    }
    
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.isServerWorld()) {
            if (!this.world.isRemote && Aspect.getAspect(this.getType()) == null) {
                if (this.world.rand.nextInt(10) != 0) {
                    final ArrayList<Aspect> as = Aspect.getPrimalAspects();
                    this.setType(as.get(this.world.rand.nextInt(as.size())).getTag());
                }
                else {
                    final ArrayList<Aspect> as = Aspect.getCompoundAspects();
                    this.setType(as.get(this.world.rand.nextInt(as.size())).getTag());
                }
            }
            if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
                this.setDead();
            }
            this.prevAttackCounter = this.attackCounter;
            final double attackrange = 16.0;
            if (this.getAttackTarget() == null || !this.canEntityBeSeen(this.getAttackTarget())) {
                if (this.currentFlightTarget != null && (!this.world.isAirBlock(this.currentFlightTarget) || this.currentFlightTarget.getY() < 1 || this.currentFlightTarget.getY() > this.world.getPrecipitationHeight(this.currentFlightTarget).up(8).getY())) {
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
                this.moveForward = 0.15f;
                this.rotationYaw += var5;
            }
            else if (this.getDistanceSq(this.getAttackTarget()) > attackrange * attackrange / 2.0 && this.canEntityBeSeen(this.getAttackTarget())) {
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
            if (this.getAttackTarget() instanceof EntityPlayer && ((EntityPlayer)this.getAttackTarget()).capabilities.disableDamage) {
                this.setAttackTarget(null);
            }
            if (this.getAttackTarget() != null && this.getAttackTarget().isDead) {
                this.setAttackTarget(null);
            }
            --this.aggroCooldown;
            if (this.world.rand.nextInt(1000) == 0 && (this.getAttackTarget() == null || this.aggroCooldown-- <= 0)) {
                this.setAttackTarget(this.world.getClosestPlayerToEntity(this, 16.0));
                if (this.getAttackTarget() != null) {
                    this.aggroCooldown = 50;
                }
            }
            if (this.isEntityAlive() && this.getAttackTarget() != null && this.getAttackTarget().getDistanceSq(this) < attackrange * attackrange) {
                final double d5 = this.getAttackTarget().posX - this.posX;
                final double d6 = this.getAttackTarget().getEntityBoundingBox().minY + this.getAttackTarget().height / 2.0f - (this.posY + this.height / 2.0f);
                final double d7 = this.getAttackTarget().posZ - this.posZ;
                final float n = -(float)Math.atan2(d5, d7) * 180.0f / 3.141593f;
                this.rotationYaw = n;
                this.renderYawOffset = n;
                if (this.canEntityBeSeen(this.getAttackTarget())) {
                    ++this.attackCounter;
                    if (this.attackCounter == 20) {
                        this.playSound(SoundsTC.zap, 1.0f, 1.1f);
                        PacketHandler.INSTANCE.sendToAllAround(new PacketFXWispZap(this.getEntityId(), this.getAttackTarget().getEntityId()), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.posX, this.posY, this.posZ, 32.0));
                        final float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                        if (Math.abs(this.getAttackTarget().motionX) > 0.10000000149011612 || Math.abs(this.getAttackTarget().motionY) > 0.10000000149011612 || Math.abs(this.getAttackTarget().motionZ) > 0.10000000149011612) {
                            if (this.world.rand.nextFloat() < 0.4f) {
                                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), damage);
                            }
                        }
                        else if (this.world.rand.nextFloat() < 0.66f) {
                            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), damage + 1.0f);
                        }
                        this.attackCounter = -20 + this.world.rand.nextInt(20);
                    }
                }
                else if (this.attackCounter > 0) {
                    --this.attackCounter;
                }
            }
        }
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundsTC.wisplive;
    }
    
    protected SoundEvent getHurtSound(final DamageSource damageSourceIn) {
        return SoundEvents.BLOCK_LAVA_EXTINGUISH;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundsTC.wispdead;
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected void dropFewItems(final boolean flag, final int i) {
        if (Aspect.getAspect(this.getType()) != null) {
            this.entityDropItem(ThaumcraftApiHelper.makeCrystal(Aspect.getAspect(this.getType())), 0.0f);
        }
    }
    
    protected float getSoundVolume() {
        return 0.25f;
    }
    
    protected boolean canDespawn() {
        return true;
    }
    
    public boolean getCanSpawnHere() {
        int count = 0;
        try {
            final List l = this.world.getEntitiesWithinAABB((Class)EntityWisp.class, this.getEntityBoundingBox().grow(16.0, 16.0, 16.0));
            if (l != null) {
                count = l.size();
            }
        }
        catch (final Exception ex) {}
        return count < 8 && this.world.getDifficulty() != EnumDifficulty.PEACEFUL && this.isValidLightLevel() && super.getCanSpawnHere();
    }
    
    protected boolean isValidLightLevel() {
        final BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
        if (this.world.getLightFor(EnumSkyBlock.SKY, blockpos) > this.rand.nextInt(32)) {
            return false;
        }
        int i = this.world.getLightFromNeighbors(blockpos);
        if (this.world.isThundering()) {
            final int j = this.world.getSkylightSubtracted();
            this.world.setSkylightSubtracted(10);
            i = this.world.getLightFromNeighbors(blockpos);
            this.world.setSkylightSubtracted(j);
        }
        return i <= this.rand.nextInt(8);
    }
    
    public void writeEntityToNBT(final NBTTagCompound nbttagcompound) {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setString("Type", this.getType());
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbttagcompound) {
        super.readEntityFromNBT(nbttagcompound);
        this.setType(nbttagcompound.getString("Type"));
    }
    
    public int getMaxSpawnedInChunk() {
        return 2;
    }
    
    static {
        TYPE = EntityDataManager.createKey(EntityWisp.class, DataSerializers.STRING);
    }
}
