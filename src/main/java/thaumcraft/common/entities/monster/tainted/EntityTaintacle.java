package thaumcraft.common.entities.monster.tainted;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.world.biomes.BiomeHandler;


public class EntityTaintacle extends EntityMob implements ITaintedMob
{
    public float flailIntensity;
    
    public EntityTaintacle(World par1World) {
        super(par1World);
        flailIntensity = 1.0f;
        setSize(0.8f, 3.0f);
        experienceValue = 8;
    }
    
    protected void initEntityAI() {
        tasks.addTask(1, new EntityAIAttackMelee(this, 1.0, false));
        tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0f));
        tasks.addTask(3, new EntityAILookIdle(this));
        targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }
    
    public boolean canAttackClass(Class clazz) {
        return !ITaintedMob.class.isAssignableFrom(clazz);
    }
    
    public boolean isOnSameTeam(Entity otherEntity) {
        return otherEntity instanceof ITaintedMob || super.isOnSameTeam(otherEntity);
    }
    
    public boolean getCanSpawnHere() {
        boolean onTaint = world.getBlockState(getPosition()).getMaterial() == ThaumcraftMaterials.MATERIAL_TAINT || world.getBlockState(getPosition().down()).getMaterial() == ThaumcraftMaterials.MATERIAL_TAINT;
        return onTaint && world.getDifficulty() != EnumDifficulty.PEACEFUL;
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0);
    }
    
    public void move(MoverType mt, double par1, double par3, double par5) {
        par1 = 0.0;
        par5 = 0.0;
        if (par3 > 0.0) {
            par3 = 0.0;
        }
        super.move(mt, par1, par3, par5);
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote && ticksExisted % 20 == 0) {
            boolean onTaint = world.getBlockState(getPosition()).getMaterial() == ThaumcraftMaterials.MATERIAL_TAINT || world.getBlockState(getPosition().down()).getMaterial() == ThaumcraftMaterials.MATERIAL_TAINT;
            if (!onTaint) {
                damageEntity(DamageSource.STARVE, 1.0f);
            }
            if (!(this instanceof EntityTaintacleSmall) && ticksExisted % 40 == 0 && getAttackTarget() != null && getDistanceSq(getAttackTarget()) > 16.0 && getDistanceSq(getAttackTarget()) < 256.0 && getEntitySenses().canSee(getAttackTarget())) {
                spawnTentacles(getAttackTarget());
            }
        }
        if (world.isRemote) {
            if (flailIntensity > 1.0f) {
                flailIntensity -= 0.01f;
            }
            if (ticksExisted < height * 10.0f && onGround) {
                FXDispatcher.INSTANCE.tentacleAriseFX(this);
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
    
    public void faceEntity(Entity par1Entity, float par2) {
        double d0 = par1Entity.posX - posX;
        double d2 = par1Entity.posZ - posZ;
        float f2 = (float)(Math.atan2(d2, d0) * 180.0 / 3.141592653589793) - 90.0f;
        rotationYaw = updateRotation(rotationYaw, f2, par2);
    }
    
    protected float updateRotation(float par1, float par2, float par3) {
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
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte par1) {
        if (par1 == 16) {
            flailIntensity = 3.0f;
        }
        else {
            super.handleStatusUpdate(par1);
        }
    }
    
    public boolean attackEntityAsMob(Entity p_70652_1_) {
        world.setEntityState(this, (byte)16);
        playSound(SoundsTC.tentacle, getSoundVolume(), getSoundPitch());
        return super.attackEntityAsMob(p_70652_1_);
    }
}
