package thaumcraft.common.entities.monster.cult;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;


public class EntityCultistPortalLesser extends EntityMob
{
    private static DataParameter<Boolean> ACTIVE;
    int stagecounter;
    public int activeCounter;
    public int pulse;
    
    public EntityCultistPortalLesser(World par1World) {
        super(par1World);
        stagecounter = 100;
        activeCounter = 0;
        pulse = 0;
        isImmuneToFire = true;
        experienceValue = 10;
        setSize(1.5f, 3.0f);
    }
    
    public int getTotalArmorValue() {
        return 4;
    }
    
    protected void entityInit() {
        super.entityInit();
        getDataManager().register(EntityCultistPortalLesser.ACTIVE, false);
    }
    
    public boolean isActive() {
        return (boolean) getDataManager().get((DataParameter)EntityCultistPortalLesser.ACTIVE);
    }
    
    public void setActive(boolean active) {
        getDataManager().set(EntityCultistPortalLesser.ACTIVE, active);
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.0);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
    }
    
    protected boolean canDespawn() {
        return false;
    }
    
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("active", isActive());
    }
    
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setActive(nbt.getBoolean("active"));
    }
    
    public boolean canBeCollidedWith() {
        return true;
    }
    
    public boolean canBePushed() {
        return false;
    }
    
    public void move(MoverType mt, double par1, double par3, double par5) {
    }
    
    public void onLivingUpdate() {
    }
    
    public boolean isInRangeToRenderDist(double par1) {
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
        if (isActive()) {
            ++activeCounter;
        }
        if (!world.isRemote) {
            if (!isActive()) {
                if (ticksExisted % 10 == 0) {
                    EntityPlayer p = world.getClosestPlayerToEntity(this, 32.0);
                    if (p != null) {
                        setActive(true);
                        playSound(SoundsTC.craftstart, 1.0f, 1.0f);
                    }
                }
            }
            else if (stagecounter-- <= 0) {
                EntityPlayer p = world.getClosestPlayerToEntity(this, 32.0);
                if (p != null && canEntityBeSeen(p)) {
                    int count = (world.getDifficulty() == EnumDifficulty.HARD) ? 6 : ((world.getDifficulty() == EnumDifficulty.NORMAL) ? 4 : 2);
                    try {
                        List l = world.getEntitiesWithinAABB(EntityCultist.class, getEntityBoundingBox().grow(32.0, 32.0, 32.0));
                        if (l != null) {
                            count -= l.size();
                        }
                    }
                    catch (Exception ex) {}
                    if (count > 0) {
                        world.setEntityState(this, (byte)16);
                        spawnMinions();
                    }
                }
                stagecounter = 50 + rand.nextInt(50);
            }
        }
        if (pulse > 0) {
            --pulse;
        }
    }
    
    int getTiming() {
        List<Entity> l = EntityUtils.getEntitiesInRange(world, posX, posY, posZ, this, EntityCultist.class, 32.0);
        return l.size() * 20;
    }
    
    void spawnMinions() {
        EntityCultist cultist = null;
        if (rand.nextFloat() > 0.33) {
            cultist = new EntityCultistKnight(world);
        }
        else {
            cultist = new EntityCultistCleric(world);
        }
        cultist.setPosition(posX + rand.nextFloat() - rand.nextFloat(), posY + 0.25, posZ + rand.nextFloat() - rand.nextFloat());
        cultist.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(cultist.getPosition())), null);
        world.spawnEntity(cultist);
        cultist.spawnExplosionParticle();
        cultist.playSound(SoundsTC.wandfail, 1.0f, 1.0f);
        attackEntityFrom(DamageSource.OUT_OF_WORLD, (float)(5 + rand.nextInt(5)));
    }
    
    protected boolean isValidLightLevel() {
        return true;
    }
    
    public void onCollideWithPlayer(EntityPlayer p) {
        if (getDistanceSq(p) < 3.0 && p.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this), 4.0f)) {
            playSound(SoundsTC.zap, 1.0f, (rand.nextFloat() - rand.nextFloat()) * 0.1f + 1.0f);
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
    
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsTC.zap;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundsTC.shock;
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected void dropFewItems(boolean flag, int fortune) {
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte msg) {
        if (msg == 16) {
            pulse = 10;
        }
        else {
            super.handleStatusUpdate(msg);
        }
    }
    
    public void addPotionEffect(PotionEffect p_70690_1_) {
    }
    
    public void fall(float distance, float damageMultiplier) {
    }
    
    public void onDeath(DamageSource p_70645_1_) {
        if (!world.isRemote) {
            world.newExplosion(this, posX, posY, posZ, 1.5f, false, false);
        }
        super.onDeath(p_70645_1_);
    }
    
    static {
        ACTIVE = EntityDataManager.createKey(EntityCultistPortalLesser.class, DataSerializers.BOOLEAN);
    }
}
