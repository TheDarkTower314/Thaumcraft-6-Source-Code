// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.cult;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.potion.PotionEffect;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.lib.utils.EntityUtils;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumDifficulty;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.entity.monster.EntityMob;

public class EntityCultistPortalLesser extends EntityMob
{
    private static final DataParameter<Boolean> ACTIVE;
    int stagecounter;
    public int activeCounter;
    public int pulse;
    
    public EntityCultistPortalLesser(final World par1World) {
        super(par1World);
        this.stagecounter = 100;
        this.activeCounter = 0;
        this.pulse = 0;
        this.isImmuneToFire = true;
        this.experienceValue = 10;
        this.setSize(1.5f, 3.0f);
    }
    
    public int getTotalArmorValue() {
        return 4;
    }
    
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(EntityCultistPortalLesser.ACTIVE, false);
    }
    
    public boolean isActive() {
        return (boolean)this.getDataManager().get((DataParameter)EntityCultistPortalLesser.ACTIVE);
    }
    
    public void setActive(final boolean active) {
        this.getDataManager().set(EntityCultistPortalLesser.ACTIVE, active);
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.0);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
    }
    
    protected boolean canDespawn() {
        return false;
    }
    
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("active", this.isActive());
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setActive(nbt.getBoolean("active"));
    }
    
    public boolean canBeCollidedWith() {
        return true;
    }
    
    public boolean canBePushed() {
        return false;
    }
    
    public void move(final MoverType mt, final double par1, final double par3, final double par5) {
    }
    
    public void onLivingUpdate() {
    }
    
    public boolean isInRangeToRenderDist(final double par1) {
        return par1 < 4096.0;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
    
    public float getBrightness() {
        return 1.0f;
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (this.isActive()) {
            ++this.activeCounter;
        }
        if (!this.world.isRemote) {
            if (!this.isActive()) {
                if (this.ticksExisted % 10 == 0) {
                    final EntityPlayer p = this.world.getClosestPlayerToEntity(this, 32.0);
                    if (p != null) {
                        this.setActive(true);
                        this.playSound(SoundsTC.craftstart, 1.0f, 1.0f);
                    }
                }
            }
            else if (this.stagecounter-- <= 0) {
                final EntityPlayer p = this.world.getClosestPlayerToEntity(this, 32.0);
                if (p != null && this.canEntityBeSeen(p)) {
                    int count = (this.world.getDifficulty() == EnumDifficulty.HARD) ? 6 : ((this.world.getDifficulty() == EnumDifficulty.NORMAL) ? 4 : 2);
                    try {
                        final List l = this.world.getEntitiesWithinAABB(EntityCultist.class, this.getEntityBoundingBox().grow(32.0, 32.0, 32.0));
                        if (l != null) {
                            count -= l.size();
                        }
                    }
                    catch (final Exception ex) {}
                    if (count > 0) {
                        this.world.setEntityState(this, (byte)16);
                        this.spawnMinions();
                    }
                }
                this.stagecounter = 50 + this.rand.nextInt(50);
            }
        }
        if (this.pulse > 0) {
            --this.pulse;
        }
    }
    
    int getTiming() {
        final List<Entity> l = EntityUtils.getEntitiesInRange(this.world, this.posX, this.posY, this.posZ, this, EntityCultist.class, 32.0);
        return l.size() * 20;
    }
    
    void spawnMinions() {
        EntityCultist cultist = null;
        if (this.rand.nextFloat() > 0.33) {
            cultist = new EntityCultistKnight(this.world);
        }
        else {
            cultist = new EntityCultistCleric(this.world);
        }
        cultist.setPosition(this.posX + this.rand.nextFloat() - this.rand.nextFloat(), this.posY + 0.25, this.posZ + this.rand.nextFloat() - this.rand.nextFloat());
        cultist.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(cultist.getPosition())), null);
        this.world.spawnEntity(cultist);
        cultist.spawnExplosionParticle();
        cultist.playSound(SoundsTC.wandfail, 1.0f, 1.0f);
        this.attackEntityFrom(DamageSource.OUT_OF_WORLD, (float)(5 + this.rand.nextInt(5)));
    }
    
    protected boolean isValidLightLevel() {
        return true;
    }
    
    public void onCollideWithPlayer(final EntityPlayer p) {
        if (this.getDistanceSq(p) < 3.0 && p.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this), 4.0f)) {
            this.playSound(SoundsTC.zap, 1.0f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1f + 1.0f);
        }
    }
    
    protected float getSoundVolume() {
        return 0.75f;
    }
    
    public int getTalkInterval() {
        return 540;
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundsTC.monolith;
    }
    
    protected SoundEvent getHurtSound(final DamageSource damageSourceIn) {
        return SoundsTC.zap;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundsTC.shock;
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected void dropFewItems(final boolean flag, final int fortune) {
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(final byte msg) {
        if (msg == 16) {
            this.pulse = 10;
        }
        else {
            super.handleStatusUpdate(msg);
        }
    }
    
    public void addPotionEffect(final PotionEffect p_70690_1_) {
    }
    
    public void fall(final float distance, final float damageMultiplier) {
    }
    
    public void onDeath(final DamageSource p_70645_1_) {
        if (!this.world.isRemote) {
            this.world.newExplosion(this, this.posX, this.posY, this.posZ, 1.5f, false, false);
        }
        super.onDeath(p_70645_1_);
    }
    
    static {
        ACTIVE = EntityDataManager.createKey(EntityCultistPortalLesser.class, DataSerializers.BOOLEAN);
    }
}
