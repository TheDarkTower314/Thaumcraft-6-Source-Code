// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.tainted;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.item.Item;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.world.biomes.BiomeHandler;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.EnumDifficulty;
import thaumcraft.api.ThaumcraftMaterials;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.world.World;
import thaumcraft.api.entities.ITaintedMob;
import net.minecraft.entity.monster.EntityMob;

public class EntityTaintacle extends EntityMob implements ITaintedMob
{
    public float flailIntensity;
    
    public EntityTaintacle(final World par1World) {
        super(par1World);
        this.flailIntensity = 1.0f;
        this.setSize(0.8f, 3.0f);
        this.experienceValue = 8;
    }
    
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0, false));
        this.tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0f));
        this.tasks.addTask(3, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }
    
    public boolean canAttackClass(final Class clazz) {
        return !ITaintedMob.class.isAssignableFrom(clazz);
    }
    
    public boolean isOnSameTeam(final Entity otherEntity) {
        return otherEntity instanceof ITaintedMob || super.isOnSameTeam(otherEntity);
    }
    
    public boolean getCanSpawnHere() {
        final boolean onTaint = this.world.getBlockState(this.getPosition()).getMaterial() == ThaumcraftMaterials.MATERIAL_TAINT || this.world.getBlockState(this.getPosition().down()).getMaterial() == ThaumcraftMaterials.MATERIAL_TAINT;
        return onTaint && this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0);
    }
    
    public void move(final MoverType mt, double par1, double par3, double par5) {
        par1 = 0.0;
        par5 = 0.0;
        if (par3 > 0.0) {
            par3 = 0.0;
        }
        super.move(mt, par1, par3, par5);
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (!this.world.isRemote && this.ticksExisted % 20 == 0) {
            final boolean onTaint = this.world.getBlockState(this.getPosition()).getMaterial() == ThaumcraftMaterials.MATERIAL_TAINT || this.world.getBlockState(this.getPosition().down()).getMaterial() == ThaumcraftMaterials.MATERIAL_TAINT;
            if (!onTaint) {
                this.damageEntity(DamageSource.STARVE, 1.0f);
            }
            if (!(this instanceof EntityTaintacleSmall) && this.ticksExisted % 40 == 0 && this.getAttackTarget() != null && this.getDistanceSq(this.getAttackTarget()) > 16.0 && this.getDistanceSq(this.getAttackTarget()) < 256.0 && this.getEntitySenses().canSee(this.getAttackTarget())) {
                this.spawnTentacles(this.getAttackTarget());
            }
        }
        if (this.world.isRemote) {
            if (this.flailIntensity > 1.0f) {
                this.flailIntensity -= 0.01f;
            }
            if (this.ticksExisted < this.height * 10.0f && this.onGround) {
                FXDispatcher.INSTANCE.tentacleAriseFX(this);
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
    
    public void faceEntity(final Entity par1Entity, final float par2) {
        final double d0 = par1Entity.posX - this.posX;
        final double d2 = par1Entity.posZ - this.posZ;
        final float f2 = (float)(Math.atan2(d2, d0) * 180.0 / 3.141592653589793) - 90.0f;
        this.rotationYaw = this.updateRotation(this.rotationYaw, f2, par2);
    }
    
    protected float updateRotation(final float par1, final float par2, final float par3) {
        float f3 = MathHelper.wrapDegrees(par2 - par1);
        if (f3 > par3) {
            f3 = par3;
        }
        if (f3 < -par3) {
            f3 = -par3;
        }
        return par1 + f3;
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
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(final byte par1) {
        if (par1 == 16) {
            this.flailIntensity = 3.0f;
        }
        else {
            super.handleStatusUpdate(par1);
        }
    }
    
    public boolean attackEntityAsMob(final Entity p_70652_1_) {
        this.world.setEntityState(this, (byte)16);
        this.playSound(SoundsTC.tentacle, this.getSoundVolume(), this.getSoundPitch());
        return super.attackEntityAsMob(p_70652_1_);
    }
}
