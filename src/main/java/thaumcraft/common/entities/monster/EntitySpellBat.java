// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import java.util.Iterator;
import java.util.List;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.api.casters.Trajectory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import java.awt.Color;
import thaumcraft.api.casters.FocusEngine;
import net.minecraft.scoreboard.Team;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.DamageSource;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldServer;
import javax.annotation.Nullable;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import thaumcraft.api.casters.FocusEffect;
import net.minecraft.network.datasync.DataParameter;
import java.util.UUID;
import thaumcraft.api.casters.FocusPackage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.monster.EntityMob;

public class EntitySpellBat extends EntityMob implements IEntityAdditionalSpawnData
{
    private BlockPos currentFlightTarget;
    public EntityLivingBase owner;
    FocusPackage focusPackage;
    private UUID ownerUniqueId;
    private static final DataParameter<Boolean> FRIENDLY;
    public int damBonus;
    private int attackTime;
    FocusEffect[] effects;
    public int color;
    
    public EntitySpellBat(final World world) {
        super(world);
        this.owner = null;
        this.damBonus = 0;
        this.effects = null;
        this.color = 16777215;
        this.setSize(0.5f, 0.9f);
    }
    
    public EntitySpellBat(final FocusPackage pac, final boolean friendly) {
        super(pac.world);
        this.owner = null;
        this.damBonus = 0;
        this.effects = null;
        this.color = 16777215;
        this.setSize(0.5f, 0.9f);
        this.focusPackage = pac;
        this.setOwner(pac.getCaster());
        this.setIsFriendly(friendly);
    }
    
    public void entityInit() {
        super.entityInit();
        this.getDataManager().register(EntitySpellBat.FRIENDLY, false);
    }
    
    public boolean getIsFriendly() {
        return (boolean)this.getDataManager().get((DataParameter)EntitySpellBat.FRIENDLY);
    }
    
    public void setIsFriendly(final boolean par1) {
        this.getDataManager().set(EntitySpellBat.FRIENDLY, par1);
    }
    
    public void writeSpawnData(final ByteBuf data) {
        Utils.writeNBTTagCompoundToBuffer(data, this.focusPackage.serialize());
    }
    
    public void readSpawnData(final ByteBuf data) {
        try {
            (this.focusPackage = new FocusPackage()).deserialize(Utils.readNBTTagCompoundFromBuffer(data));
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setOwner(@Nullable final EntityLivingBase ownerIn) {
        this.owner = ownerIn;
        this.ownerUniqueId = ((ownerIn == null) ? null : ownerIn.getUniqueID());
    }
    
    @Nullable
    public EntityLivingBase getOwner() {
        if (this.owner == null && this.ownerUniqueId != null && this.world instanceof WorldServer) {
            final Entity entity = ((WorldServer)this.world).getEntityFromUuid(this.ownerUniqueId);
            if (entity instanceof EntityLivingBase) {
                this.owner = (EntityLivingBase)entity;
            }
        }
        return this.owner;
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
        return SoundEvents.ENTITY_BAT_AMBIENT;
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
    
    public Team getTeam() {
        final EntityLivingBase entitylivingbase = this.getOwner();
        if (entitylivingbase != null) {
            return entitylivingbase.getTeam();
        }
        return super.getTeam();
    }
    
    public boolean isOnSameTeam(final Entity otherEntity) {
        final EntityLivingBase owner = this.getOwner();
        if (otherEntity == owner) {
            return true;
        }
        if (owner != null) {
            return owner.isOnSameTeam(otherEntity) || otherEntity.isOnSameTeam(owner);
        }
        return super.isOnSameTeam(otherEntity);
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (!this.world.isRemote && (this.ticksExisted > 600 || this.getOwner() == null)) {
            this.setDead();
        }
        this.motionY *= 0.6000000238418579;
        if (this.isEntityAlive() && this.world.isRemote) {
            if (this.effects == null) {
                this.effects = this.focusPackage.getFocusEffects();
                int r = 0;
                int g = 0;
                int b = 0;
                for (final FocusEffect ef : this.effects) {
                    final Color c = new Color(FocusEngine.getElementColor(ef.getKey()));
                    r += c.getRed();
                    g += c.getGreen();
                    b += c.getBlue();
                }
                r /= this.effects.length;
                g /= this.effects.length;
                b /= this.effects.length;
                final Color c2 = new Color(r, g, b);
                this.color = c2.getRGB();
            }
            if (this.effects != null && this.effects.length > 0) {
                final FocusEffect eff = this.effects[this.rand.nextInt(this.effects.length)];
                eff.renderParticleFX(this.world, this.posX + this.world.rand.nextGaussian() * 0.125, this.posY + this.height / 2.0f + this.world.rand.nextGaussian() * 0.125, this.posZ + this.world.rand.nextGaussian() * 0.125, 0.0, 0.0, 0.0);
            }
        }
    }
    
    protected void updateAITasks() {
        super.updateAITasks();
        if (this.attackTime > 0) {
            --this.attackTime;
        }
        final BlockPos blockpos = new BlockPos(this);
        final BlockPos blockpos2 = blockpos.up();
        if (this.getAttackTarget() == null) {
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
            this.setAttackTarget(this.findTargetToAttack());
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
        if (!this.getIsFriendly() && this.getAttackTarget() instanceof EntityPlayer && ((EntityPlayer)this.getAttackTarget()).capabilities.disableDamage) {
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
        return super.attackEntityFrom(par1DamageSource, par2);
    }
    
    protected void attackEntity(final Entity target, final float par2) {
        if (this.attackTime <= 0 && par2 < Math.max(2.5f, target.width * 1.1f) && target.getEntityBoundingBox().maxY > this.getEntityBoundingBox().minY && target.getEntityBoundingBox().minY < this.getEntityBoundingBox().maxY) {
            this.attackTime = 40;
            if (!this.world.isRemote) {
                final RayTraceResult ray = new RayTraceResult(target);
                ray.hitVec = target.getPositionVector().addVector(0.0, target.height / 2.0f, 0.0);
                final Trajectory tra = new Trajectory(this.getPositionVector(), this.getPositionVector().subtractReverse(ray.hitVec));
                FocusEngine.runFocusPackage(this.focusPackage.copy(this.getOwner()), new Trajectory[] { tra }, new RayTraceResult[] { ray });
                this.setHealth(this.getHealth() - 1.0f);
            }
            this.playSound(SoundEvents.ENTITY_BAT_HURT, 0.5f, 0.9f + this.world.rand.nextFloat() * 0.2f);
        }
    }
    
    protected void collideWithEntity(final Entity entityIn) {
        if (this.getIsFriendly()) {
            return;
        }
        super.collideWithEntity(entityIn);
    }
    
    protected EntityLivingBase findTargetToAttack() {
        final double var1 = 12.0;
        final List<EntityLivingBase> list = EntityUtils.getEntitiesInRange(this.world, this.posX, this.posY, this.posZ, this, EntityLivingBase.class, var1);
        double d = Double.MAX_VALUE;
        EntityLivingBase ret = null;
        for (final EntityLivingBase e : list) {
            if (e.isDead) {
                continue;
            }
            if (this.getIsFriendly()) {
                if (!EntityUtils.isFriendly(this.getOwner(), e)) {
                    continue;
                }
            }
            else {
                if (EntityUtils.isFriendly(this.getOwner(), e)) {
                    continue;
                }
                if (this.isOnSameTeam(e)) {
                    continue;
                }
            }
            final double ed = this.getDistanceSq(e);
            if (ed >= d) {
                continue;
            }
            d = ed;
            ret = e;
        }
        return ret;
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.ownerUniqueId = nbt.getUniqueId("OwnerUUID");
        this.setIsFriendly(nbt.getBoolean("friendly"));
        try {
            (this.focusPackage = new FocusPackage()).deserialize(nbt.getCompoundTag("pack"));
        }
        catch (final Exception ex) {}
    }
    
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        if (this.ownerUniqueId != null) {
            nbt.setUniqueId("OwnerUUID", this.ownerUniqueId);
        }
        nbt.setTag("pack", this.focusPackage.serialize());
        nbt.setBoolean("friendly", this.getIsFriendly());
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
    
    protected boolean canDropLoot() {
        return false;
    }
    
    protected boolean isValidLightLevel() {
        return true;
    }
    
    static {
        FRIENDLY = EntityDataManager.createKey(EntitySpellBat.class, DataSerializers.BOOLEAN);
    }
}
