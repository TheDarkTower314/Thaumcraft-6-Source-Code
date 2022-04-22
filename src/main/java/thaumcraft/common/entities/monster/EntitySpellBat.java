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
        owner = null;
        damBonus = 0;
        effects = null;
        color = 16777215;
        setSize(0.5f, 0.9f);
    }
    
    public EntitySpellBat(final FocusPackage pac, final boolean friendly) {
        super(pac.world);
        owner = null;
        damBonus = 0;
        effects = null;
        color = 16777215;
        setSize(0.5f, 0.9f);
        focusPackage = pac;
        setOwner(pac.getCaster());
        setIsFriendly(friendly);
    }
    
    public void entityInit() {
        super.entityInit();
        getDataManager().register(EntitySpellBat.FRIENDLY, false);
    }
    
    public boolean getIsFriendly() {
        return (boolean) getDataManager().get((DataParameter)EntitySpellBat.FRIENDLY);
    }
    
    public void setIsFriendly(final boolean par1) {
        getDataManager().set(EntitySpellBat.FRIENDLY, par1);
    }
    
    public void writeSpawnData(final ByteBuf data) {
        Utils.writeNBTTagCompoundToBuffer(data, focusPackage.serialize());
    }
    
    public void readSpawnData(final ByteBuf data) {
        try {
            (focusPackage = new FocusPackage()).deserialize(Utils.readNBTTagCompoundFromBuffer(data));
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setOwner(@Nullable final EntityLivingBase ownerIn) {
        owner = ownerIn;
        ownerUniqueId = ((ownerIn == null) ? null : ownerIn.getUniqueID());
    }
    
    @Nullable
    public EntityLivingBase getOwner() {
        if (owner == null && ownerUniqueId != null && world instanceof WorldServer) {
            final Entity entity = ((WorldServer) world).getEntityFromUuid(ownerUniqueId);
            if (entity instanceof EntityLivingBase) {
                owner = (EntityLivingBase)entity;
            }
        }
        return owner;
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
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
    }
    
    public Team getTeam() {
        final EntityLivingBase entitylivingbase = getOwner();
        if (entitylivingbase != null) {
            return entitylivingbase.getTeam();
        }
        return super.getTeam();
    }
    
    public boolean isOnSameTeam(final Entity otherEntity) {
        final EntityLivingBase owner = getOwner();
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
        if (!world.isRemote && (ticksExisted > 600 || getOwner() == null)) {
            setDead();
        }
        motionY *= 0.6000000238418579;
        if (isEntityAlive() && world.isRemote) {
            if (effects == null) {
                effects = focusPackage.getFocusEffects();
                int r = 0;
                int g = 0;
                int b = 0;
                for (final FocusEffect ef : effects) {
                    final Color c = new Color(FocusEngine.getElementColor(ef.getKey()));
                    r += c.getRed();
                    g += c.getGreen();
                    b += c.getBlue();
                }
                r /= effects.length;
                g /= effects.length;
                b /= effects.length;
                final Color c2 = new Color(r, g, b);
                color = c2.getRGB();
            }
            if (effects != null && effects.length > 0) {
                final FocusEffect eff = effects[rand.nextInt(effects.length)];
                eff.renderParticleFX(world, posX + world.rand.nextGaussian() * 0.125, posY + height / 2.0f + world.rand.nextGaussian() * 0.125, posZ + world.rand.nextGaussian() * 0.125, 0.0, 0.0, 0.0);
            }
        }
    }
    
    protected void updateAITasks() {
        super.updateAITasks();
        if (attackTime > 0) {
            --attackTime;
        }
        final BlockPos blockpos = new BlockPos(this);
        final BlockPos blockpos2 = blockpos.up();
        if (getAttackTarget() == null) {
            if (currentFlightTarget != null && (!world.isAirBlock(currentFlightTarget) || currentFlightTarget.getY() < 1)) {
                currentFlightTarget = null;
            }
            if (currentFlightTarget == null || rand.nextInt(30) == 0 || getDistanceSqToCenter(currentFlightTarget) < 4.0) {
                currentFlightTarget = new BlockPos((int) posX + rand.nextInt(7) - rand.nextInt(7), (int) posY + rand.nextInt(6) - 2, (int) posZ + rand.nextInt(7) - rand.nextInt(7));
            }
            final double var1 = currentFlightTarget.getX() + 0.5 - posX;
            final double var2 = currentFlightTarget.getY() + 0.1 - posY;
            final double var3 = currentFlightTarget.getZ() + 0.5 - posZ;
            motionX += (Math.signum(var1) * 0.5 - motionX) * 0.10000000149011612;
            motionY += (Math.signum(var2) * 0.699999988079071 - motionY) * 0.10000000149011612;
            motionZ += (Math.signum(var3) * 0.5 - motionZ) * 0.10000000149011612;
            final float var4 = (float)(Math.atan2(motionZ, motionX) * 180.0 / 3.141592653589793) - 90.0f;
            final float var5 = MathHelper.wrapDegrees(var4 - rotationYaw);
            moveForward = 0.5f;
            rotationYaw += var5;
        }
        else {
            final double var1 = getAttackTarget().posX - posX;
            final double var2 = getAttackTarget().posY + getAttackTarget().getEyeHeight() * 0.66f - posY;
            final double var3 = getAttackTarget().posZ - posZ;
            motionX += (Math.signum(var1) * 0.5 - motionX) * 0.10000000149011612;
            motionY += (Math.signum(var2) * 0.699999988079071 - motionY) * 0.10000000149011612;
            motionZ += (Math.signum(var3) * 0.5 - motionZ) * 0.10000000149011612;
            final float var4 = (float)(Math.atan2(motionZ, motionX) * 180.0 / 3.141592653589793) - 90.0f;
            final float var5 = MathHelper.wrapDegrees(var4 - rotationYaw);
            moveForward = 0.5f;
            rotationYaw += var5;
        }
        if (getAttackTarget() == null) {
            setAttackTarget(findTargetToAttack());
        }
        else if (getAttackTarget().isEntityAlive()) {
            final float f = getAttackTarget().getDistance(this);
            if (isEntityAlive() && canEntityBeSeen(getAttackTarget())) {
                attackEntity(getAttackTarget(), f);
            }
        }
        else {
            setAttackTarget(null);
        }
        if (!getIsFriendly() && getAttackTarget() instanceof EntityPlayer && ((EntityPlayer) getAttackTarget()).capabilities.disableDamage) {
            setAttackTarget(null);
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
        if (attackTime <= 0 && par2 < Math.max(2.5f, target.width * 1.1f) && target.getEntityBoundingBox().maxY > getEntityBoundingBox().minY && target.getEntityBoundingBox().minY < getEntityBoundingBox().maxY) {
            attackTime = 40;
            if (!world.isRemote) {
                final RayTraceResult ray = new RayTraceResult(target);
                ray.hitVec = target.getPositionVector().addVector(0.0, target.height / 2.0f, 0.0);
                final Trajectory tra = new Trajectory(getPositionVector(), getPositionVector().subtractReverse(ray.hitVec));
                FocusEngine.runFocusPackage(focusPackage.copy(getOwner()), new Trajectory[] { tra }, new RayTraceResult[] { ray });
                setHealth(getHealth() - 1.0f);
            }
            playSound(SoundEvents.ENTITY_BAT_HURT, 0.5f, 0.9f + world.rand.nextFloat() * 0.2f);
        }
    }
    
    protected void collideWithEntity(final Entity entityIn) {
        if (getIsFriendly()) {
            return;
        }
        super.collideWithEntity(entityIn);
    }
    
    protected EntityLivingBase findTargetToAttack() {
        final double var1 = 12.0;
        final List<EntityLivingBase> list = EntityUtils.getEntitiesInRange(world, posX, posY, posZ, this, EntityLivingBase.class, var1);
        double d = Double.MAX_VALUE;
        EntityLivingBase ret = null;
        for (final EntityLivingBase e : list) {
            if (e.isDead) {
                continue;
            }
            if (getIsFriendly()) {
                if (!EntityUtils.isFriendly(getOwner(), e)) {
                    continue;
                }
            }
            else {
                if (EntityUtils.isFriendly(getOwner(), e)) {
                    continue;
                }
                if (isOnSameTeam(e)) {
                    continue;
                }
            }
            final double ed = getDistanceSq(e);
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
        ownerUniqueId = nbt.getUniqueId("OwnerUUID");
        setIsFriendly(nbt.getBoolean("friendly"));
        try {
            (focusPackage = new FocusPackage()).deserialize(nbt.getCompoundTag("pack"));
        }
        catch (final Exception ex) {}
    }
    
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        if (ownerUniqueId != null) {
            nbt.setUniqueId("OwnerUUID", ownerUniqueId);
        }
        nbt.setTag("pack", focusPackage.serialize());
        nbt.setBoolean("friendly", getIsFriendly());
    }
    
    public boolean getCanSpawnHere() {
        final int i = MathHelper.floor(posX);
        final int j = MathHelper.floor(getEntityBoundingBox().minY);
        final int k = MathHelper.floor(posZ);
        final BlockPos blockpos = new BlockPos(i, j, k);
        final int var4 = world.getLight(blockpos);
        final byte var5 = 7;
        return var4 <= rand.nextInt(var5) && super.getCanSpawnHere();
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
