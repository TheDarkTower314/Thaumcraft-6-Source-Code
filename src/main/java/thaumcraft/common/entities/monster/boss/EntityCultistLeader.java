// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.boss;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.entities.projectile.EntityGolemOrb;
import net.minecraft.entity.EntityLivingBase;
import java.util.Iterator;
import java.util.List;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import thaumcraft.common.lib.utils.EntityUtils;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.item.Item;
import thaumcraft.common.entities.monster.cult.EntityCultistKnight;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import net.minecraft.entity.Entity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.ThaumcraftApiHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import thaumcraft.common.entities.ai.combat.AICultistHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.world.World;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.entity.IRangedAttackMob;

public class EntityCultistLeader extends EntityThaumcraftBoss implements IRangedAttackMob
{
    private static final DataParameter<Byte> NAME;
    String[] titles;
    
    public EntityCultistLeader(final World p_i1745_1_) {
        super(p_i1745_1_);
        this.titles = new String[] { "Alberic", "Anselm", "Bastian", "Beturian", "Chabier", "Chorache", "Chuse", "Dodorol", "Ebardo", "Ferrando", "Fertus", "Guillen", "Larpe", "Obano", "Zelipe" };
        this.setSize(0.75f, 2.25f);
        this.experienceValue = 40;
    }
    
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new AILongRangeAttack(this, 16.0, 1.0, 30, 40, 24.0f));
        this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.1, false));
        this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.8));
        this.tasks.addTask(7, new EntityAIWander(this, 0.8));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new AICultistHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.32);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(150.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
    }
    
    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register((DataParameter)EntityCultistLeader.NAME, 0);
    }
    
    @Override
    public void generateName() {
        final int t = (int)this.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue();
        if (t >= 0) {
            this.setCustomNameTag(String.format(I18n.translateToLocal("entity.Thaumcraft.CultistLeader.name.custom"), this.getTitle(), ChampionModifier.mods[t].getModNameLocalized()));
        }
    }
    
    private String getTitle() {
        return this.titles[(byte)this.getDataManager().get((DataParameter)EntityCultistLeader.NAME)];
    }
    
    private void setTitle(final int title) {
        this.getDataManager().set(EntityCultistLeader.NAME, (byte)title);
    }
    
    @Override
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("title", (byte)this.getDataManager().get((DataParameter)EntityCultistLeader.NAME));
    }
    
    @Override
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setTitle(nbt.getByte("title"));
    }
    
    protected void setEquipmentBasedOnDifficulty(final DifficultyInstance difficulty) {
        this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ItemsTC.crimsonPraetorHelm));
        this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemsTC.crimsonPraetorChest));
        this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ItemsTC.crimsonPraetorLegs));
        this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(ItemsTC.crimsonBoots));
        if (this.world.getDifficulty() == EnumDifficulty.EASY) {
            this.setHeldItem(this.getActiveHand(), new ItemStack(ItemsTC.voidSword));
        }
        else {
            this.setHeldItem(this.getActiveHand(), new ItemStack(ItemsTC.crimsonBlade));
        }
    }
    
    @Override
    protected void setEnchantmentBasedOnDifficulty(final DifficultyInstance diff) {
        final float f = diff.getClampedAdditionalDifficulty();
        if (this.getHeldItemMainhand() != null && this.rand.nextFloat() < 0.5f * f) {
            EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItemMainhand(), (int)(7.0f + f * this.rand.nextInt(22)), false);
        }
    }
    
    @Override
    public boolean isOnSameTeam(final Entity el) {
        return el instanceof EntityCultist || el instanceof EntityCultistLeader;
    }
    
    public boolean canAttackClass(final Class clazz) {
        return clazz != EntityCultistCleric.class && clazz != EntityCultistLeader.class && clazz != EntityCultistKnight.class && super.canAttackClass(clazz);
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    @Override
    protected void dropFewItems(final boolean flag, final int i) {
        this.entityDropItem(new ItemStack(ItemsTC.lootBag, 1, 2), 1.5f);
    }
    
    @Override
    public IEntityLivingData onInitialSpawn(final DifficultyInstance diff, final IEntityLivingData data) {
        this.setEquipmentBasedOnDifficulty(diff);
        this.setEnchantmentBasedOnDifficulty(diff);
        this.setTitle(this.rand.nextInt(this.titles.length));
        return super.onInitialSpawn(diff, data);
    }
    
    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        final List<Entity> list = EntityUtils.getEntitiesInRange(this.world, this.posX, this.posY, this.posZ, this, EntityCultist.class, 8.0);
        for (final Entity e : list) {
            try {
                if (!(e instanceof EntityCultist) || ((EntityCultist)e).isPotionActive(MobEffects.REGENERATION)) {
                    continue;
                }
                ((EntityCultist)e).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 60, 1));
            }
            catch (final Exception ex) {}
        }
    }
    
    public void attackEntityWithRangedAttack(final EntityLivingBase entitylivingbase, final float f) {
        if (this.canEntityBeSeen(entitylivingbase)) {
            this.swingArm(this.getActiveHand());
            this.getLookHelper().setLookPosition(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY + entitylivingbase.height / 2.0f, entitylivingbase.posZ, 30.0f, 30.0f);
            final EntityGolemOrb entityGolemOrb;
            final EntityGolemOrb blast = entityGolemOrb = new EntityGolemOrb(this.world, this, entitylivingbase, true);
            entityGolemOrb.posX += blast.motionX / 2.0;
            final EntityGolemOrb entityGolemOrb2 = blast;
            entityGolemOrb2.posZ += blast.motionZ / 2.0;
            blast.setPosition(blast.posX, blast.posY, blast.posZ);
            final double d0 = entitylivingbase.posX - this.posX;
            final double d2 = entitylivingbase.getEntityBoundingBox().minY + entitylivingbase.height / 2.0f - (this.posY + this.height / 2.0f);
            final double d3 = entitylivingbase.posZ - this.posZ;
            blast.shoot(d0, d2 + 2.0, d3, 0.66f, 3.0f);
            this.playSound(SoundsTC.egattack, 1.0f, 1.0f + this.rand.nextFloat() * 0.1f);
            this.world.spawnEntity(blast);
        }
    }
    
    public void spawnExplosionParticle() {
        if (this.world.isRemote) {
            for (int i = 0; i < 20; ++i) {
                final double d0 = this.rand.nextGaussian() * 0.05;
                final double d2 = this.rand.nextGaussian() * 0.05;
                final double d3 = this.rand.nextGaussian() * 0.05;
                final double d4 = 2.0;
                FXDispatcher.INSTANCE.cultistSpawn(this.posX + this.rand.nextFloat() * this.width * 2.0f - this.width + d0 * d4, this.posY + this.rand.nextFloat() * this.height + d2 * d4, this.posZ + this.rand.nextFloat() * this.width * 2.0f - this.width + d3 * d4, d0, d2, d3);
            }
        }
        else {
            this.world.setEntityState(this, (byte)20);
        }
    }
    
    public void setSwingingArms(final boolean swingingArms) {
    }
    
    static {
        NAME = EntityDataManager.createKey(EntityCultistLeader.class, DataSerializers.BYTE);
    }
}
