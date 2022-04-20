// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.tainted;

import net.minecraft.entity.MoverType;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.item.Item;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.common.world.biomes.BiomeHandler;
import java.util.Iterator;
import java.util.List;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.potion.PotionEffect;
import thaumcraft.api.potions.PotionFluxTaint;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.world.aura.AuraHandler;
import thaumcraft.common.blocks.world.taint.TaintHelper;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.SharedMonsterAttributes;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.config.ModConfig;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.world.World;
import thaumcraft.api.entities.ITaintedMob;
import net.minecraft.entity.monster.EntityMob;

public class EntityTaintSeed extends EntityMob implements ITaintedMob
{
    public int boost;
    boolean firstRun;
    public float attackAnim;
    
    public EntityTaintSeed(final World par1World) {
        super(par1World);
        this.boost = 0;
        this.firstRun = false;
        this.attackAnim = 0.0f;
        this.setSize(1.5f, 1.25f);
        this.experienceValue = 8;
    }
    
    protected int getArea() {
        return 1;
    }
    
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0, false));
        this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false, new Class[0]));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.boost = nbt.getInteger("boost");
    }
    
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("boost", this.boost);
    }
    
    public boolean attackEntityAsMob(final Entity p_70652_1_) {
        this.world.setEntityState(this, (byte)16);
        this.playSound(SoundsTC.tentacle, this.getSoundVolume(), this.getSoundPitch());
        return super.attackEntityAsMob(p_70652_1_);
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(final byte par1) {
        if (par1 == 16) {
            this.attackAnim = 0.5f;
        }
        else {
            super.handleStatusUpdate(par1);
        }
    }
    
    public boolean canAttackClass(final Class clazz) {
        return !ITaintedMob.class.isAssignableFrom(clazz);
    }
    
    public boolean isOnSameTeam(final Entity otherEntity) {
        return otherEntity instanceof ITaintedMob || super.isOnSameTeam(otherEntity);
    }
    
    public boolean getCanSpawnHere() {
        return this.world.getDifficulty() != EnumDifficulty.PEACEFUL && this.isNotColliding() && EntityUtils.getEntitiesInRange(this.getEntityWorld(), this.getPosition(), null, (Class<? extends Entity>)EntityTaintSeed.class, ModConfig.CONFIG_WORLD.taintSpreadArea * 0.8).size() <= 0;
    }
    
    public boolean isNotColliding() {
        return !this.world.containsAnyLiquid(this.getEntityBoundingBox()) && this.world.checkNoEntityCollision(this.getEntityBoundingBox(), this);
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(75.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0);
    }
    
    public void onDeath(final DamageSource cause) {
        TaintHelper.removeTaintSeed(this.getEntityWorld(), this.getPosition());
        super.onDeath(cause);
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (!this.world.isRemote) {
            if (!this.firstRun || this.ticksExisted % 1200 == 0) {
                TaintHelper.removeTaintSeed(this.getEntityWorld(), this.getPosition());
                TaintHelper.addTaintSeed(this.getEntityWorld(), this.getPosition());
                this.firstRun = true;
            }
            if (this.isEntityAlive()) {
                final boolean tickFlag = this.ticksExisted % 20 == 0;
                if (this.boost > 0 || tickFlag) {
                    final float mod = (this.boost > 0) ? 1.0f : AuraHandler.getFluxSaturation(this.world, this.getPosition());
                    if (this.boost > 0) {
                        --this.boost;
                    }
                    if (mod <= 0.0f) {
                        this.attackEntityFrom(DamageSource.STARVE, 0.5f);
                        AuraHelper.polluteAura(this.getEntityWorld(), this.getPosition(), 0.1f, false);
                    }
                    else {
                        TaintHelper.spreadFibres(this.world, this.getPosition().add(MathHelper.getInt(this.getRNG(), -this.getArea() * 3, this.getArea() * 3), MathHelper.getInt(this.getRNG(), -this.getArea(), this.getArea()), MathHelper.getInt(this.getRNG(), -this.getArea() * 3, this.getArea() * 3)), true);
                    }
                }
                if (tickFlag) {
                    if (this.getAttackTarget() != null && this.getDistanceSq(this.getAttackTarget()) < this.getArea() * 256 && this.getEntitySenses().canSee(this.getAttackTarget())) {
                        this.spawnTentacles(this.getAttackTarget());
                    }
                    final List<EntityLivingBase> list = EntityUtils.getEntitiesInRange(this.getEntityWorld(), this.getPosition(), this, EntityLivingBase.class, this.getArea() * 4);
                    for (final EntityLivingBase elb : list) {
                        elb.addPotionEffect(new PotionEffect(PotionFluxTaint.instance, 100, this.getArea() - 1, false, true));
                    }
                }
            }
        }
        else {
            if (this.attackAnim > 0.0f) {
                this.attackAnim *= 0.75f;
            }
            if (this.attackAnim < 0.001) {
                this.attackAnim = 0.0f;
            }
            final float xx = 1.0f * MathHelper.sin(this.ticksExisted * 0.05f - 0.5f) / 5.0f;
            final float zz = 1.0f * MathHelper.sin(this.ticksExisted * 0.06f - 0.5f) / 5.0f + this.hurtTime / 200.0f + this.attackAnim;
            if (this.rand.nextFloat() < 0.033) {
                FXDispatcher.INSTANCE.drawLightningFlash((float)this.posX + xx, (float)this.posY + this.height + 0.25f, (float)this.posZ + zz, 0.7f, 0.1f, 0.9f, 0.5f, 1.5f + this.rand.nextFloat());
            }
            else {
                FXDispatcher.INSTANCE.drawTaintParticles((float)this.posX + xx, (float)this.posY + this.height + 0.25f, (float)this.posZ + zz, (float)this.rand.nextGaussian() * 0.05f, 0.1f + 0.01f * this.rand.nextFloat(), (float)this.rand.nextGaussian() * 0.05f, 2.0f);
            }
        }
    }
    
    protected void spawnTentacles(final Entity entity) {
        if (this.world.getBiome(entity.getPosition()) == BiomeHandler.ELDRITCH || this.world.getBlockState(entity.getPosition()).getMaterial() == ThaumcraftMaterials.MATERIAL_TAINT || this.world.getBlockState(entity.getPosition().down()).getMaterial() == ThaumcraftMaterials.MATERIAL_TAINT) {
            final EntityTaintacleSmall taintlet = new EntityTaintacleSmall(this.world);
            taintlet.setLocationAndAngles(entity.posX + this.world.rand.nextFloat() - this.world.rand.nextFloat(), entity.posY, entity.posZ + this.world.rand.nextFloat() - this.world.rand.nextFloat(), 0.0f, 0.0f);
            this.world.spawnEntity(taintlet);
            this.playSound(SoundsTC.tentacle, this.getSoundVolume(), this.getSoundPitch());
            if (this.world.getBiome(entity.getPosition()) == BiomeHandler.ELDRITCH && this.world.isAirBlock(entity.getPosition()) && BlockUtils.isAdjacentToSolidBlock(this.world, entity.getPosition())) {
                this.world.setBlockState(entity.getPosition(), BlocksTC.taintFibre.getDefaultState());
            }
        }
    }
    
    public int getTalkInterval() {
        return 200;
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLOCK_CHORUS_FLOWER_DEATH;
    }
    
    protected float getSoundPitch() {
        return 1.3f - this.height / 10.0f;
    }
    
    protected float getSoundVolume() {
        return this.height / 8.0f;
    }
    
    protected SoundEvent getHurtSound(final DamageSource damageSourceIn) {
        return SoundsTC.tentacle;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundsTC.tentacle;
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected void dropFewItems(final boolean flag, final int i) {
        this.entityDropItem(ConfigItems.FLUX_CRYSTAL.copy(), this.height / 2.0f);
    }
    
    public boolean canBePushed() {
        return false;
    }
    
    public boolean canBeCollidedWith() {
        return true;
    }
    
    public void moveRelative(final float strafe, final float forward, final float friction, final float g) {
    }
    
    public void move(final MoverType mt, double par1, double par3, double par5) {
        par1 = 0.0;
        par5 = 0.0;
        if (par3 > 0.0) {
            par3 = 0.0;
        }
        super.move(mt, par1, par3, par5);
    }
    
    protected int decreaseAirSupply(final int air) {
        return air;
    }
    
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    protected boolean canDespawn() {
        return false;
    }
}
