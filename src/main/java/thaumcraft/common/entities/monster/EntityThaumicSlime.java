// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster;

import thaumcraft.common.config.ConfigItems;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.item.Item;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.entity.Entity;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import thaumcraft.api.entities.ITaintedMob;
import net.minecraft.entity.monster.EntitySlime;

public class EntityThaumicSlime extends EntitySlime implements ITaintedMob
{
    int launched;
    int spitCounter;
    
    public EntityThaumicSlime(final World par1World) {
        super(par1World);
        this.launched = 10;
        this.spitCounter = 100;
        final int i = 1 << 1 + this.rand.nextInt(3);
        this.setSlimeSize(i, true);
    }
    
    public EntityThaumicSlime(final World par1World, final EntityLivingBase par2EntityLiving, final EntityLivingBase par3EntityLiving) {
        super(par1World);
        this.launched = 10;
        this.spitCounter = 100;
        this.setSlimeSize(1, true);
        this.posY = (par2EntityLiving.getEntityBoundingBox().minY + par2EntityLiving.getEntityBoundingBox().maxY) / 2.0;
        final double var6 = par3EntityLiving.posX - par2EntityLiving.posX;
        final double var7 = par3EntityLiving.getEntityBoundingBox().minY + par3EntityLiving.height / 3.0f - this.posY;
        final double var8 = par3EntityLiving.posZ - par2EntityLiving.posZ;
        final double var9 = MathHelper.sqrt(var6 * var6 + var8 * var8);
        if (var9 >= 1.0E-7) {
            final float var10 = (float)(Math.atan2(var8, var6) * 180.0 / 3.141592653589793) - 90.0f;
            final float var11 = (float)(-(Math.atan2(var7, var9) * 180.0 / 3.141592653589793));
            final double var12 = var6 / var9;
            final double var13 = var8 / var9;
            this.setLocationAndAngles(par2EntityLiving.posX + var12, this.posY, par2EntityLiving.posZ + var13, var10, var11);
            final float var14 = (float)var9 * 0.2f;
            this.shoot(var6, var7 + var14, var8, 1.5f, 1.0f);
        }
    }
    
    public void shoot(double par1, double par3, double par5, final float par7, final float par8) {
        final float var9 = MathHelper.sqrt(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= var9;
        par3 /= var9;
        par5 /= var9;
        par1 += this.rand.nextGaussian() * 0.007499999832361937 * par8;
        par3 += this.rand.nextGaussian() * 0.007499999832361937 * par8;
        par5 += this.rand.nextGaussian() * 0.007499999832361937 * par8;
        par1 *= par7;
        par3 *= par7;
        par5 *= par7;
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;
        final float var10 = MathHelper.sqrt(par1 * par1 + par5 * par5);
        final float n = (float)(Math.atan2(par1, par5) * 180.0 / 3.141592653589793);
        this.rotationYaw = n;
        this.prevRotationYaw = n;
        final float n2 = (float)(Math.atan2(par3, var10) * 180.0 / 3.141592653589793);
        this.rotationPitch = n2;
        this.prevRotationPitch = n2;
    }
    
    public IEntityLivingData onInitialSpawn(final DifficultyInstance p_180482_1_, final IEntityLivingData p_180482_2_) {
        int i = this.rand.nextInt(3);
        if (i < 2 && this.rand.nextFloat() < 0.5f * p_180482_1_.getClampedAdditionalDifficulty()) {
            ++i;
        }
        final int j = 1 << i;
        this.setSlimeSize(j, true);
        return super.onInitialSpawn(p_180482_1_, p_180482_2_);
    }
    
    public void setSlimeSize(final int par1, final boolean t) {
        super.setSlimeSize(par1, t);
        this.experienceValue = par1 + 2;
    }
    
    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
    }
    
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
    }
    
    public void onUpdate() {
        final int i = this.getSlimeSize();
        if (this.onGround && !this.wasOnGround) {
            this.wasOnGround = true;
            final float sa = this.squishAmount;
            super.onUpdate();
            this.squishAmount = sa;
            if (this.world.isRemote) {
                for (int j = 0; j < i * 2; ++j) {
                    FXDispatcher.INSTANCE.slimeJumpFX(this, i);
                }
            }
            if (this.makesSoundOnJump()) {
                this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.getRNG().nextFloat() - this.getRNG().nextFloat()) * 0.2f + 1.0f) * 0.8f);
            }
            this.squishAmount = -0.5f;
            this.wasOnGround = this.onGround;
            this.alterSquishAmount();
        }
        else {
            super.onUpdate();
        }
        if (this.world.isRemote) {
            if (this.launched > 0) {
                --this.launched;
                for (int k = 0; k < i * (this.launched + 1); ++k) {
                    FXDispatcher.INSTANCE.slimeJumpFX(this, i);
                }
            }
            final float ff = (float)this.getSlimeSize();
            this.setSize(0.6f * ff, 0.6f * ff);
            this.setSize(0.51000005f * ff, 0.51000005f * ff);
        }
        else if (!this.isDead) {
            final EntityPlayer entityplayer = this.world.getClosestPlayerToEntity(this, 16.0);
            if (entityplayer != null) {
                if (this.spitCounter > 0) {
                    --this.spitCounter;
                }
                this.faceEntity(entityplayer, 10.0f, 20.0f);
                if (this.getDistance(entityplayer) > 4.0f && this.spitCounter <= 0 && this.getSlimeSize() > 2) {
                    this.spitCounter = 101;
                    if (!this.world.isRemote) {
                        final EntityThaumicSlime flyslime = new EntityThaumicSlime(this.world, this, entityplayer);
                        this.world.spawnEntity(flyslime);
                    }
                    this.playSound(SoundsTC.gore, 1.0f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f) * 0.8f);
                    this.setSlimeSize(this.getSlimeSize() - 1, true);
                }
            }
        }
    }
    
    protected EntityThaumicSlime createInstance() {
        return new EntityThaumicSlime(this.world);
    }
    
    public void setDead() {
        final int i = this.getSlimeSize();
        if (!this.world.isRemote && i > 1 && this.getHealth() <= 0.0f) {
            for (int k = 0; k < i; ++k) {
                final float f = (k % 2 - 0.5f) * i / 4.0f;
                final float f2 = (k / 2 - 0.5f) * i / 4.0f;
                final EntityThaumicSlime entityslime = this.createInstance();
                entityslime.setSlimeSize(1, true);
                entityslime.setLocationAndAngles(this.posX + f, this.posY + 0.5, this.posZ + f2, this.rand.nextFloat() * 360.0f, 0.0f);
                this.world.spawnEntity(entityslime);
            }
        }
        this.isDead = true;
    }
    
    public boolean getCanSpawnHere() {
        return false;
    }
    
    protected int getAttackStrength() {
        return this.getSlimeSize() + 1;
    }
    
    protected boolean canDamagePlayer() {
        return true;
    }
    
    protected void dealDamage(final EntityLivingBase p_175451_1_) {
        int i = this.getSlimeSize();
        if (this.launched > 0) {
            i += 2;
        }
        if (this.isEntityAlive() && this.canEntityBeSeen(p_175451_1_) && this.getDistanceSq(p_175451_1_) < 0.6 * i * 0.6 * i && p_175451_1_.attackEntityFrom(DamageSource.causeMobDamage(this), (float)this.getAttackStrength())) {
            this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
            this.applyEnchantments(this, p_175451_1_);
        }
    }
    
    protected Item getDropItem() {
        return (this.getSlimeSize() > 1) ? ItemsTC.crystalEssence : Item.getItemById(0);
    }
    
    protected void dropFewItems(final boolean flag, final int i) {
        if (this.getSlimeSize() > 1) {
            this.entityDropItem(ConfigItems.FLUX_CRYSTAL.copy(), this.height / 2.0f);
        }
    }
}
