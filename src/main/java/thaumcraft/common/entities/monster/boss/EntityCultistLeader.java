package thaumcraft.common.entities.monster.boss;
import java.util.Iterator;
import java.util.List;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.ai.combat.AICultistHurtByTarget;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;
import thaumcraft.common.entities.monster.cult.EntityCultistKnight;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.common.entities.projectile.EntityGolemOrb;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;


public class EntityCultistLeader extends EntityThaumcraftBoss implements IRangedAttackMob
{
    private static DataParameter<Byte> NAME;
    String[] titles;
    
    public EntityCultistLeader(World p_i1745_1_) {
        super(p_i1745_1_);
        titles = new String[] { "Alberic", "Anselm", "Bastian", "Beturian", "Chabier", "Chorache", "Chuse", "Dodorol", "Ebardo", "Ferrando", "Fertus", "Guillen", "Larpe", "Obano", "Zelipe" };
        setSize(0.75f, 2.25f);
        experienceValue = 40;
    }
    
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(2, new AILongRangeAttack(this, 16.0, 1.0, 30, 40, 24.0f));
        tasks.addTask(3, new EntityAIAttackMelee(this, 1.1, false));
        tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.8));
        tasks.addTask(7, new EntityAIWander(this, 0.8));
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        tasks.addTask(8, new EntityAILookIdle(this));
        targetTasks.addTask(1, new AICultistHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.32);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(150.0);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
    }
    
    @Override
    protected void entityInit() {
        super.entityInit();
        getDataManager().register((DataParameter)EntityCultistLeader.NAME, 0);
    }
    
    @Override
    public void generateName() {
        int t = (int) getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue();
        if (t >= 0) {
            setCustomNameTag(String.format(I18n.translateToLocal("entity.Thaumcraft.CultistLeader.name.custom"), getTitle(), ChampionModifier.mods[t].getModNameLocalized()));
        }
    }
    
    private String getTitle() {
        return titles[(byte) getDataManager().get((DataParameter)EntityCultistLeader.NAME)];
    }
    
    private void setTitle(int title) {
        getDataManager().set(EntityCultistLeader.NAME, (byte)title);
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("title", (byte) getDataManager().get((DataParameter)EntityCultistLeader.NAME));
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setTitle(nbt.getByte("title"));
    }
    
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ItemsTC.crimsonPraetorHelm));
        setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemsTC.crimsonPraetorChest));
        setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ItemsTC.crimsonPraetorLegs));
        setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(ItemsTC.crimsonBoots));
        if (world.getDifficulty() == EnumDifficulty.EASY) {
            setHeldItem(getActiveHand(), new ItemStack(ItemsTC.voidSword));
        }
        else {
            setHeldItem(getActiveHand(), new ItemStack(ItemsTC.crimsonBlade));
        }
    }
    
    @Override
    protected void setEnchantmentBasedOnDifficulty(DifficultyInstance diff) {
        float f = diff.getClampedAdditionalDifficulty();
        if (getHeldItemMainhand() != null && rand.nextFloat() < 0.5f * f) {
            EnchantmentHelper.addRandomEnchantment(rand, getHeldItemMainhand(), (int)(7.0f + f * rand.nextInt(22)), false);
        }
    }
    
    @Override
    public boolean isOnSameTeam(Entity el) {
        return el instanceof EntityCultist || el instanceof EntityCultistLeader;
    }
    
    public boolean canAttackClass(Class clazz) {
        return clazz != EntityCultistCleric.class && clazz != EntityCultistLeader.class && clazz != EntityCultistKnight.class && super.canAttackClass(clazz);
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    @Override
    protected void dropFewItems(boolean flag, int i) {
        entityDropItem(new ItemStack(ItemsTC.lootBag, 1, 2), 1.5f);
    }
    
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData data) {
        setEquipmentBasedOnDifficulty(diff);
        setEnchantmentBasedOnDifficulty(diff);
        setTitle(rand.nextInt(titles.length));
        return super.onInitialSpawn(diff, data);
    }
    
    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        List<Entity> list = EntityUtils.getEntitiesInRange(world, posX, posY, posZ, this, EntityCultist.class, 8.0);
        for (Entity e : list) {
            try {
                if (!(e instanceof EntityCultist) || ((EntityCultist)e).isPotionActive(MobEffects.REGENERATION)) {
                    continue;
                }
                ((EntityCultist)e).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 60, 1));
            }
            catch (Exception ex) {}
        }
    }
    
    public void attackEntityWithRangedAttack(EntityLivingBase entitylivingbase, float f) {
        if (canEntityBeSeen(entitylivingbase)) {
            swingArm(getActiveHand());
            getLookHelper().setLookPosition(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY + entitylivingbase.height / 2.0f, entitylivingbase.posZ, 30.0f, 30.0f);
            EntityGolemOrb entityGolemOrb;
            EntityGolemOrb blast = entityGolemOrb = new EntityGolemOrb(world, this, entitylivingbase, true);
            entityGolemOrb.posX += blast.motionX / 2.0;
            EntityGolemOrb entityGolemOrb2 = blast;
            entityGolemOrb2.posZ += blast.motionZ / 2.0;
            blast.setPosition(blast.posX, blast.posY, blast.posZ);
            double d0 = entitylivingbase.posX - posX;
            double d2 = entitylivingbase.getEntityBoundingBox().minY + entitylivingbase.height / 2.0f - (posY + height / 2.0f);
            double d3 = entitylivingbase.posZ - posZ;
            blast.shoot(d0, d2 + 2.0, d3, 0.66f, 3.0f);
            playSound(SoundsTC.egattack, 1.0f, 1.0f + rand.nextFloat() * 0.1f);
            world.spawnEntity(blast);
        }
    }
    
    public void spawnExplosionParticle() {
        if (world.isRemote) {
            for (int i = 0; i < 20; ++i) {
                double d0 = rand.nextGaussian() * 0.05;
                double d2 = rand.nextGaussian() * 0.05;
                double d3 = rand.nextGaussian() * 0.05;
                double d4 = 2.0;
                FXDispatcher.INSTANCE.cultistSpawn(posX + rand.nextFloat() * width * 2.0f - width + d0 * d4, posY + rand.nextFloat() * height + d2 * d4, posZ + rand.nextFloat() * width * 2.0f - width + d3 * d4, d0, d2, d3);
            }
        }
        else {
            world.setEntityState(this, (byte)20);
        }
    }
    
    public void setSwingingArms(boolean swingingArms) {
    }
    
    static {
        NAME = EntityDataManager.createKey(EntityCultistLeader.class, DataSerializers.BYTE);
    }
}
