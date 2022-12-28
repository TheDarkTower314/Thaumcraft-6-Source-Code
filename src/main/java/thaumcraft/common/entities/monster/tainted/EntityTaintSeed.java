package thaumcraft.common.entities.monster.tainted;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.world.taint.TaintHelper;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.world.aura.AuraHandler;
import thaumcraft.common.world.biomes.BiomeHandler;


public class EntityTaintSeed extends EntityMob implements ITaintedMob
{
    public int boost;
    boolean firstRun;
    public float attackAnim;
    
    public EntityTaintSeed(World par1World) {
        super(par1World);
        boost = 0;
        firstRun = false;
        attackAnim = 0.0f;
        setSize(1.5f, 1.25f);
        experienceValue = 8;
    }
    
    protected int getArea() {
        return 1;
    }
    
    protected void initEntityAI() {
        tasks.addTask(1, new EntityAIAttackMelee(this, 1.0, false));
        targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }
    
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        boost = nbt.getInteger("boost");
    }
    
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("boost", boost);
    }
    
    public boolean attackEntityAsMob(Entity p_70652_1_) {
        world.setEntityState(this, (byte)16);
        playSound(SoundsTC.tentacle, getSoundVolume(), getSoundPitch());
        return super.attackEntityAsMob(p_70652_1_);
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte par1) {
        if (par1 == 16) {
            attackAnim = 0.5f;
        }
        else {
            super.handleStatusUpdate(par1);
        }
    }
    
    public boolean canAttackClass(Class clazz) {
        return !ITaintedMob.class.isAssignableFrom(clazz);
    }
    
    public boolean isOnSameTeam(Entity otherEntity) {
        return otherEntity instanceof ITaintedMob || super.isOnSameTeam(otherEntity);
    }
    
    public boolean getCanSpawnHere() {
        return world.getDifficulty() != EnumDifficulty.PEACEFUL && isNotColliding() && EntityUtils.getEntitiesInRange(getEntityWorld(), getPosition(), null, (Class<? extends Entity>)EntityTaintSeed.class, ModConfig.CONFIG_WORLD.taintSpreadArea * 0.8).size() <= 0;
    }
    
    public boolean isNotColliding() {
        return !world.containsAnyLiquid(getEntityBoundingBox()) && world.checkNoEntityCollision(getEntityBoundingBox(), this);
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(75.0);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0);
    }
    
    public void onDeath(DamageSource cause) {
        TaintHelper.removeTaintSeed(getEntityWorld(), getPosition());
        super.onDeath(cause);
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote) {
            if (!firstRun || ticksExisted % 1200 == 0) {
                TaintHelper.removeTaintSeed(getEntityWorld(), getPosition());
                TaintHelper.addTaintSeed(getEntityWorld(), getPosition());
                firstRun = true;
            }
            if (isEntityAlive()) {
                boolean tickFlag = ticksExisted % 20 == 0;
                if (boost > 0 || tickFlag) {
                    float mod = (boost > 0) ? 1.0f : AuraHandler.getFluxSaturation(world, getPosition());
                    if (boost > 0) {
                        --boost;
                    }
                    if (mod <= 0.0f) {
                        attackEntityFrom(DamageSource.STARVE, 0.5f);
                        AuraHelper.polluteAura(getEntityWorld(), getPosition(), 0.1f, false);
                    }
                    else {
                        TaintHelper.spreadFibres(world, getPosition().add(MathHelper.getInt(getRNG(), -getArea() * 3, getArea() * 3), MathHelper.getInt(getRNG(), -getArea(), getArea()), MathHelper.getInt(getRNG(), -getArea() * 3, getArea() * 3)), true);
                    }
                }
                if (tickFlag) {
                    if (getAttackTarget() != null && getDistanceSq(getAttackTarget()) < getArea() * 256 && getEntitySenses().canSee(getAttackTarget())) {
                        spawnTentacles(getAttackTarget());
                    }
                    List<EntityLivingBase> list = EntityUtils.getEntitiesInRange(getEntityWorld(), getPosition(), this, EntityLivingBase.class, getArea() * 4);
                    for (EntityLivingBase elb : list) {
                        elb.addPotionEffect(new PotionEffect(PotionFluxTaint.instance, 100, getArea() - 1, false, true));
                    }
                }
            }
        }
        else {
            if (attackAnim > 0.0f) {
                attackAnim *= 0.75f;
            }
            if (attackAnim < 0.001) {
                attackAnim = 0.0f;
            }
            float xx = 1.0f * MathHelper.sin(ticksExisted * 0.05f - 0.5f) / 5.0f;
            float zz = 1.0f * MathHelper.sin(ticksExisted * 0.06f - 0.5f) / 5.0f + hurtTime / 200.0f + attackAnim;
            if (rand.nextFloat() < 0.033) {
                FXDispatcher.INSTANCE.drawLightningFlash((float) posX + xx, (float) posY + height + 0.25f, (float) posZ + zz, 0.7f, 0.1f, 0.9f, 0.5f, 1.5f + rand.nextFloat());
            }
            else {
                FXDispatcher.INSTANCE.drawTaintParticles((float) posX + xx, (float) posY + height + 0.25f, (float) posZ + zz, (float) rand.nextGaussian() * 0.05f, 0.1f + 0.01f * rand.nextFloat(), (float) rand.nextGaussian() * 0.05f, 2.0f);
            }
        }
    }
    
    protected void spawnTentacles(Entity entity) {
        if (world.getBiome(entity.getPosition()) == BiomeHandler.ELDRITCH || world.getBlockState(entity.getPosition()).getMaterial() == ThaumcraftMaterials.MATERIAL_TAINT || world.getBlockState(entity.getPosition().down()).getMaterial() == ThaumcraftMaterials.MATERIAL_TAINT) {
            EntityTaintacleSmall taintlet = new EntityTaintacleSmall(world);
            taintlet.setLocationAndAngles(entity.posX + world.rand.nextFloat() - world.rand.nextFloat(), entity.posY, entity.posZ + world.rand.nextFloat() - world.rand.nextFloat(), 0.0f, 0.0f);
            world.spawnEntity(taintlet);
            playSound(SoundsTC.tentacle, getSoundVolume(), getSoundPitch());
            if (world.getBiome(entity.getPosition()) == BiomeHandler.ELDRITCH && world.isAirBlock(entity.getPosition()) && BlockUtils.isAdjacentToSolidBlock(world, entity.getPosition())) {
                world.setBlockState(entity.getPosition(), BlocksTC.taintFibre.getDefaultState());
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
        return 1.3f - height / 10.0f;
    }
    
    protected float getSoundVolume() {
        return height / 8.0f;
    }
    
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsTC.tentacle;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundsTC.tentacle;
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected void dropFewItems(boolean flag, int i) {
        entityDropItem(ConfigItems.FLUX_CRYSTAL.copy(), height / 2.0f);
    }
    
    public boolean canBePushed() {
        return false;
    }
    
    public boolean canBeCollidedWith() {
        return true;
    }
    
    public void moveRelative(float strafe, float forward, float friction, float g) {
    }
    
    public void move(MoverType mt, double par1, double par3, double par5) {
        par1 = 0.0;
        par5 = 0.0;
        if (par3 > 0.0) {
            par3 = 0.0;
        }
        super.move(mt, par1, par3, par5);
    }
    
    protected int decreaseAirSupply(int air) {
        return air;
    }
    
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    protected boolean canDespawn() {
        return false;
    }
}
