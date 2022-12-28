package thaumcraft.common.entities.monster.tainted;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.common.blocks.world.taint.BlockTaintFibre;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.utils.BlockUtils;


public class EntityTaintCrawler extends EntityMob implements ITaintedMob
{
    BlockPos lastPos;
    
    public EntityTaintCrawler(World par1World) {
        super(par1World);
        lastPos = new BlockPos(0, 0, 0);
        setSize(0.5f, 0.4f);
        experienceValue = 3;
    }
    
    protected void initEntityAI() {
        tasks.addTask(1, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAIAttackMelee(this, 1.0, false));
        tasks.addTask(3, new EntityAIWander(this, 1.0));
        tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        tasks.addTask(8, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }
    
    public boolean canAttackClass(Class clazz) {
        return !ITaintedMob.class.isAssignableFrom(clazz);
    }
    
    public boolean isOnSameTeam(Entity otherEntity) {
        return otherEntity instanceof ITaintedMob || super.isOnSameTeam(otherEntity);
    }
    
    public float getEyeHeight() {
        return 0.1f;
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.275);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
    }
    
    protected float getSoundPitch() {
        return 0.7f;
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SILVERFISH_AMBIENT;
    }
    
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_SILVERFISH_HURT;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SILVERFISH_DEATH;
    }
    
    protected void playStepSound(BlockPos p_180429_1_, Block p_180429_2_) {
        playSound(SoundEvents.ENTITY_SILVERFISH_STEP, 0.15f, 1.0f);
    }
    
    protected boolean canTriggerWalking() {
        return false;
    }
    
    public void onUpdate() {
        if (!world.isRemote && isEntityAlive() && ticksExisted % 40 == 0 && lastPos != getPosition()) {
            lastPos = getPosition();
            IBlockState bs = world.getBlockState(getPosition());
            Material bm = bs.getMaterial();
            if (!bs.getBlock().isLeaves(bs, world, getPosition()) && !bm.isLiquid() && bm != ThaumcraftMaterials.MATERIAL_TAINT && (world.isAirBlock(getPosition()) || bs.getBlock().isReplaceable(world, getPosition()) || bs.getBlock() instanceof BlockFlower || bs.getBlock() instanceof IPlantable) && BlockUtils.isAdjacentToSolidBlock(world, getPosition()) && !BlockTaintFibre.isOnlyAdjacentToTaint(world, getPosition())) {
                world.setBlockState(getPosition(), BlocksTC.taintFibre.getDefaultState());
            }
        }
        super.onUpdate();
    }
    
    protected boolean isValidLightLevel() {
        return true;
    }
    
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected void dropFewItems(boolean flag, int i) {
        if (world.rand.nextInt(8) == 0) {
            entityDropItem(ConfigItems.FLUX_CRYSTAL.copy(), height / 2.0f);
        }
    }
    
    public IEntityLivingData onInitialSpawn(DifficultyInstance p_180482_1_, IEntityLivingData p_180482_2_) {
        return p_180482_2_;
    }
    
    public boolean attackEntityAsMob(Entity victim) {
        if (super.attackEntityAsMob(victim)) {
            if (victim instanceof EntityLivingBase) {
                byte b0 = 0;
                if (world.getDifficulty() == EnumDifficulty.NORMAL) {
                    b0 = 3;
                }
                else if (world.getDifficulty() == EnumDifficulty.HARD) {
                    b0 = 6;
                }
                if (b0 > 0 && rand.nextInt(b0 + 1) > 2) {
                    ((EntityLivingBase)victim).addPotionEffect(new PotionEffect(PotionFluxTaint.instance, b0 * 20, 0));
                }
            }
            return true;
        }
        return false;
    }
}
