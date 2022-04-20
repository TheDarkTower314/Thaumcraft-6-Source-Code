// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster;

import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.Thaumcraft;
import net.minecraft.item.ItemNameTag;
import thaumcraft.common.lib.utils.InventoryUtils;
import net.minecraft.util.SoundEvent;
import thaumcraft.common.lib.SoundsTC;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.DamageSource;
import java.util.Iterator;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.biome.Biome;
import thaumcraft.common.world.biomes.BiomeHandler;
import thaumcraft.common.config.ModConfig;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.item.Item;
import net.minecraft.world.DifficultyInstance;
import javax.annotation.Nullable;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.common.items.casters.foci.FocusEffectFire;
import thaumcraft.common.items.casters.foci.FocusEffectAir;
import thaumcraft.common.items.casters.foci.FocusEffectEarth;
import thaumcraft.common.items.casters.foci.FocusEffectFlux;
import thaumcraft.common.items.casters.foci.FocusEffectCurse;
import thaumcraft.common.items.casters.foci.FocusMediumProjectile;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.FocusMediumRoot;
import thaumcraft.api.casters.FocusPackage;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.util.math.MathHelper;
import java.util.Collection;
import net.minecraft.potion.PotionUtils;
import java.util.Collections;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.init.Items;
import thaumcraft.common.entities.ai.pech.AINearestAttackableTargetPech;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import thaumcraft.common.entities.ai.pech.AIPechItemEntityGoto;
import thaumcraft.common.entities.ai.pech.AIPechTradePlayer;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityCreature;
import net.minecraft.world.World;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.monster.EntityMob;

public class EntityPech extends EntityMob implements IRangedAttackMob
{
    public NonNullList<ItemStack> loot;
    public boolean trading;
    private final EntityAIAttackRanged aiArrowAttack;
    private final EntityAIAttackRanged aiBlastAttack;
    private final EntityAIAttackMelee aiMeleeAttack;
    private final EntityAIAvoidEntity aiAvoidPlayer;
    private static final DataParameter<Byte> TYPE;
    private static final DataParameter<Integer> ANGER;
    private static final DataParameter<Boolean> TAMED;
    public static final ResourceLocation LOOT;
    public float mumble;
    int chargecount;
    public static HashMap<Integer, Integer> valuedItems;
    public static HashMap<Integer, ArrayList<List>> tradeInventory;
    
    public EntityPech(final World world) {
        super(world);
        this.loot = NonNullList.withSize(9, ItemStack.EMPTY);
        this.trading = false;
        this.aiArrowAttack = new EntityAIAttackRanged(this, 0.6, 20, 50, 15.0f);
        this.aiBlastAttack = new EntityAIAttackRanged(this, 0.6, 20, 50, 15.0f);
        this.aiMeleeAttack = new EntityAIAttackMelee(this, 0.6, false);
        this.aiAvoidPlayer = new EntityAIAvoidEntity(this, EntityPlayer.class, 8.0f, 0.5, 0.6);
        this.mumble = 0.0f;
        this.chargecount = 0;
        this.setSize(0.6f, 1.8f);
        this.experienceValue = 8;
    }
    
    public String getName() {
        if (this.hasCustomName()) {
            return this.getCustomNameTag();
        }
        switch (this.getPechType()) {
            case 0: {
                return I18n.translateToLocal("entity.Pech.name");
            }
            case 1: {
                return I18n.translateToLocal("entity.Pech.1.name");
            }
            case 2: {
                return I18n.translateToLocal("entity.Pech.2.name");
            }
            default: {
                return I18n.translateToLocal("entity.Pech.name");
            }
        }
    }
    
    protected void initEntityAI() {
        ((PathNavigateGround)this.getNavigator()).setCanSwim(false);
        this.setDropChance(EntityEquipmentSlot.MAINHAND, 0.2f);
        this.setDropChance(EntityEquipmentSlot.OFFHAND, 0.2f);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new AIPechTradePlayer(this));
        this.tasks.addTask(3, new AIPechItemEntityGoto(this));
        this.tasks.addTask(5, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.5));
        this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0, false));
        this.tasks.addTask(9, new EntityAIWander(this, 0.6));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0f, 1.0f));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0f));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
        this.targetTasks.addTask(2, new AINearestAttackableTargetPech(this, EntityPlayer.class, true));
    }
    
    public void setCombatTask() {
        if (this.world != null && !this.world.isRemote) {
            this.tasks.removeTask(this.aiMeleeAttack);
            this.tasks.removeTask(this.aiArrowAttack);
            this.tasks.removeTask(this.aiBlastAttack);
            final ItemStack itemstack = this.getHeldItemMainhand();
            if (itemstack.getItem() == Items.BOW) {
                this.tasks.addTask(2, this.aiArrowAttack);
            }
            else if (itemstack.getItem() == ItemsTC.pechWand) {
                this.tasks.addTask(2, this.aiBlastAttack);
            }
            else {
                this.tasks.addTask(2, this.aiMeleeAttack);
            }
            if (this.isTamed()) {
                this.tasks.removeTask(this.aiAvoidPlayer);
            }
            else {
                this.tasks.addTask(4, this.aiAvoidPlayer);
            }
        }
    }
    
    public void attackEntityWithRangedAttack(final EntityLivingBase target, final float f) {
        if (this.getPechType() == 2) {
            final EntityTippedArrow entityarrow = new EntityTippedArrow(this.world, this);
            if (this.world.rand.nextFloat() < 0.2) {
                final ItemStack itemstack = new ItemStack(Items.TIPPED_ARROW);
                PotionUtils.appendEffects(itemstack, Collections.singletonList(new PotionEffect(MobEffects.POISON, 40)));
                entityarrow.setPotionEffect(itemstack);
            }
            final double d0 = target.posX - this.posX;
            final double d2 = target.getEntityBoundingBox().minY + target.height / 3.0f - entityarrow.posY;
            final double d3 = target.posZ - this.posZ;
            final double d4 = MathHelper.sqrt(d0 * d0 + d3 * d3);
            entityarrow.shoot(d0, d2 + d4 * 0.20000000298023224, d3, 1.6f, (float)(14 - this.world.getDifficulty().getDifficultyId() * 4));
            final int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, this);
            final int j = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, this);
            entityarrow.setDamage(f * 2.0f + this.rand.nextGaussian() * 0.25 + this.world.getDifficulty().getDifficultyId() * 0.11f);
            if (i > 0) {
                entityarrow.setDamage(entityarrow.getDamage() + i * 0.5 + 0.5);
            }
            if (j > 0) {
                entityarrow.setKnockbackStrength(j);
            }
            if (EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FLAME, this) > 0) {
                entityarrow.setFire(100);
            }
            this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, 1.0f / (this.getRNG().nextFloat() * 0.4f + 0.8f));
            this.world.spawnEntity(entityarrow);
        }
        else if (this.getPechType() == 1) {
            final FocusPackage p = new FocusPackage(this);
            final FocusMediumRoot root = new FocusMediumRoot();
            final double off = this.getDistance(target) / 6.0f;
            root.setupFromCasterToTarget(this, target, off);
            p.addNode(root);
            final FocusMediumProjectile fp = new FocusMediumProjectile();
            fp.initialize();
            fp.getSetting("speed").setValue(2);
            p.addNode(fp);
            p.addNode(this.rand.nextBoolean() ? new FocusEffectCurse() : (this.rand.nextBoolean() ? new FocusEffectFlux() : (this.rand.nextBoolean() ? new FocusEffectEarth() : (this.rand.nextBoolean() ? new FocusEffectAir() : new FocusEffectFire()))));
            FocusEngine.castFocusPackage(this, p, true);
            this.swingArm(this.getActiveHand());
        }
    }
    
    public void setItemStackToSlot(final EntityEquipmentSlot slotIn, @Nullable final ItemStack stack) {
        super.setItemStackToSlot(slotIn, stack);
        if (!this.world.isRemote && slotIn == EntityEquipmentSlot.MAINHAND) {
            this.setCombatTask();
        }
    }
    
    protected void setEquipmentBasedOnDifficulty(final DifficultyInstance difficulty) {
        super.setEquipmentBasedOnDifficulty(difficulty);
        switch (this.rand.nextInt(20)) {
            case 0:
            case 12: {
                this.setHeldItem(this.getActiveHand(), new ItemStack(ItemsTC.pechWand));
                break;
            }
            case 1: {
                this.setHeldItem(this.getActiveHand(), new ItemStack(Items.STONE_SWORD));
                break;
            }
            case 3: {
                this.setHeldItem(this.getActiveHand(), new ItemStack(Items.STONE_AXE));
                break;
            }
            case 5: {
                this.setHeldItem(this.getActiveHand(), new ItemStack(Items.IRON_SWORD));
                break;
            }
            case 6: {
                this.setHeldItem(this.getActiveHand(), new ItemStack(Items.IRON_AXE));
                break;
            }
            case 7: {
                this.setHeldItem(this.getActiveHand(), new ItemStack(Items.FISHING_ROD));
                break;
            }
            case 8: {
                this.setHeldItem(this.getActiveHand(), new ItemStack(Items.STONE_PICKAXE));
                break;
            }
            case 9: {
                this.setHeldItem(this.getActiveHand(), new ItemStack(Items.IRON_PICKAXE));
                break;
            }
            case 2:
            case 4:
            case 10:
            case 11:
            case 13: {
                this.setHeldItem(this.getActiveHand(), new ItemStack(Items.BOW));
                break;
            }
        }
    }
    
    public IEntityLivingData onInitialSpawn(final DifficultyInstance diff, final IEntityLivingData data) {
        this.setEquipmentBasedOnDifficulty(diff);
        final ItemStack itemstack = this.getHeldItem(this.getActiveHand());
        if (itemstack.getItem() == ItemsTC.pechWand) {
            this.setPechType(1);
            this.setDropChance((this.getActiveHand() == EnumHand.MAIN_HAND) ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, 0.1f);
        }
        else if (!itemstack.isEmpty()) {
            if (itemstack.getItem() == Items.BOW) {
                this.setPechType(2);
            }
            this.setEnchantmentBasedOnDifficulty(diff);
        }
        final float f = diff.getClampedAdditionalDifficulty();
        this.setCanPickUpLoot(this.rand.nextFloat() < 0.75f * f);
        this.setCombatTask();
        return super.onInitialSpawn(diff, data);
    }
    
    public boolean getCanSpawnHere() {
        final Biome biome = this.world.getBiome(new BlockPos(this));
        boolean magicBiome = false;
        if (biome != null) {
            magicBiome = BiomeDictionary.hasType(biome, BiomeDictionary.Type.MAGICAL);
        }
        int count = 0;
        try {
            final List l = this.world.getEntitiesWithinAABB((Class)EntityPech.class, this.getEntityBoundingBox().grow(16.0, 16.0, 16.0));
            if (l != null) {
                count = l.size();
            }
        }
        catch (final Exception ex) {}
        if (this.world.provider.getDimension() != ModConfig.CONFIG_WORLD.overworldDim && biome != BiomeHandler.MAGICAL_FOREST && biome != BiomeHandler.EERIE) {
            magicBiome = false;
        }
        return count < 4 && magicBiome && super.getCanSpawnHere();
    }
    
    public float getEyeHeight() {
        return this.height * 0.66f;
    }
    
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }
    
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(EntityPech.TYPE, (byte)0);
        this.getDataManager().register(EntityPech.ANGER, 0);
        this.getDataManager().register(EntityPech.TAMED, false);
    }
    
    public int getPechType() {
        return this.getDataManager().get(EntityPech.TYPE);
    }
    
    public int getAnger() {
        return this.getDataManager().get(EntityPech.ANGER);
    }
    
    public boolean isTamed() {
        return this.getDataManager().get(EntityPech.TAMED);
    }
    
    public void setPechType(final int par1) {
        this.getDataManager().set(EntityPech.TYPE, (byte)par1);
    }
    
    public void setAnger(final int par1) {
        this.getDataManager().set(EntityPech.ANGER, par1);
    }
    
    public void setTamed(final boolean par1) {
        this.getDataManager().set(EntityPech.TAMED, par1);
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
    }
    
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("PechType", (byte)this.getPechType());
        nbt.setShort("Anger", (short)this.getAnger());
        nbt.setBoolean("Tamed", this.isTamed());
        ItemStackHelper.saveAllItems(nbt, this.loot);
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("PechType")) {
            final byte b0 = nbt.getByte("PechType");
            this.setPechType(b0);
        }
        this.setAnger(nbt.getShort("Anger"));
        this.setTamed(nbt.getBoolean("Tamed"));
        ItemStackHelper.loadAllItems(nbt, this.loot = NonNullList.withSize(9, ItemStack.EMPTY));
        this.setCombatTask();
    }
    
    protected boolean canDespawn() {
        try {
            if (this.loot == null) {
                return true;
            }
            int q = 0;
            for (final ItemStack is : this.loot) {
                if (is != null && is.getCount() > 0) {
                    ++q;
                }
            }
            return q < 5;
        }
        catch (final Exception e) {
            return true;
        }
    }
    
    public boolean canBeLeashedTo(final EntityPlayer player) {
        return false;
    }
    
    protected ResourceLocation getLootTable() {
        return EntityPech.LOOT;
    }
    
    protected void dropLoot(final boolean wasRecentlyHit, final int lootingModifier, final DamageSource source) {
        for (int a = 0; a < this.loot.size(); ++a) {
            if (!this.loot.get(a).isEmpty() && this.world.rand.nextFloat() < 0.33f) {
                this.entityDropItem(this.loot.get(a).copy(), 1.5f);
            }
        }
        super.dropLoot(wasRecentlyHit, lootingModifier, source);
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(final byte par1) {
        if (par1 == 16) {
            this.mumble = 3.1415927f;
        }
        else if (par1 == 17) {
            this.mumble = 6.2831855f;
        }
        else if (par1 == 18) {
            for (int i = 0; i < 5; ++i) {
                final double d0 = this.rand.nextGaussian() * 0.02;
                final double d2 = this.rand.nextGaussian() * 0.02;
                final double d3 = this.rand.nextGaussian() * 0.02;
                this.world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, this.posX + this.rand.nextFloat() * this.width * 2.0f - this.width, this.posY + 0.5 + this.rand.nextFloat() * this.height, this.posZ + this.rand.nextFloat() * this.width * 2.0f - this.width, d0, d2, d3);
            }
        }
        if (par1 == 19) {
            for (int i = 0; i < 5; ++i) {
                final double d0 = this.rand.nextGaussian() * 0.02;
                final double d2 = this.rand.nextGaussian() * 0.02;
                final double d3 = this.rand.nextGaussian() * 0.02;
                this.world.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, this.posX + this.rand.nextFloat() * this.width * 2.0f - this.width, this.posY + 0.5 + this.rand.nextFloat() * this.height, this.posZ + this.rand.nextFloat() * this.width * 2.0f - this.width, d0, d2, d3);
            }
            this.mumble = 6.2831855f;
        }
        else {
            super.handleStatusUpdate(par1);
        }
    }
    
    public void playLivingSound() {
        if (!this.world.isRemote) {
            if (this.rand.nextInt(3) == 0) {
                final List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(4.0, 2.0, 4.0));
                for (int i = 0; i < list.size(); ++i) {
                    final Entity entity1 = list.get(i);
                    if (entity1 instanceof EntityPech) {
                        this.world.setEntityState(this, (byte)17);
                        this.playSound(SoundsTC.pech_trade, this.getSoundVolume(), this.getSoundPitch());
                        return;
                    }
                }
            }
            this.world.setEntityState(this, (byte)16);
        }
        super.playLivingSound();
    }
    
    public int getTalkInterval() {
        return 120;
    }
    
    protected float getSoundVolume() {
        return 0.4f;
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundsTC.pech_idle;
    }
    
    protected SoundEvent getHurtSound(final DamageSource damageSourceIn) {
        return SoundsTC.pech_hit;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundsTC.pech_death;
    }
    
    private void becomeAngryAt(final Entity entity) {
        if (entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative()) {
            return;
        }
        if (this.getAnger() <= 0) {
            this.world.setEntityState(this, (byte)19);
            this.playSound(SoundsTC.pech_charge, this.getSoundVolume(), this.getSoundPitch());
        }
        this.setAttackTarget((EntityLivingBase)entity);
        this.setAnger(400 + this.rand.nextInt(400));
        this.setTamed(false);
        this.setCombatTask();
    }
    
    public int getTotalArmorValue() {
        int i = super.getTotalArmorValue() + 2;
        if (i > 20) {
            i = 20;
        }
        return i;
    }
    
    public boolean attackEntityFrom(final DamageSource damSource, final float par2) {
        if (this.isEntityInvulnerable(damSource)) {
            return false;
        }
        final Entity entity = damSource.getTrueSource();
        if (entity instanceof EntityPlayer) {
            final List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(32.0, 16.0, 32.0));
            for (int i = 0; i < list.size(); ++i) {
                final Entity entity2 = list.get(i);
                if (entity2 instanceof EntityPech) {
                    final EntityPech entitypech = (EntityPech)entity2;
                    entitypech.becomeAngryAt(entity);
                }
            }
            this.becomeAngryAt(entity);
        }
        return super.attackEntityFrom(damSource, par2);
    }
    
    public void onUpdate() {
        if (this.mumble > 0.0f) {
            this.mumble *= 0.75f;
        }
        if (this.getAnger() > 0) {
            this.setAnger(this.getAnger() - 1);
        }
        if (this.getAnger() > 0 && this.getAttackTarget() != null) {
            if (this.chargecount > 0) {
                --this.chargecount;
            }
            if (this.chargecount == 0) {
                this.chargecount = 100;
                this.playSound(SoundsTC.pech_charge, this.getSoundVolume(), this.getSoundPitch());
            }
            this.world.setEntityState(this, (byte)17);
        }
        if (this.world.isRemote && this.rand.nextInt(15) == 0 && this.getAnger() > 0) {
            final double d0 = this.rand.nextGaussian() * 0.02;
            final double d2 = this.rand.nextGaussian() * 0.02;
            final double d3 = this.rand.nextGaussian() * 0.02;
            this.world.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, this.posX + this.rand.nextFloat() * this.width * 2.0f - this.width, this.posY + 0.5 + this.rand.nextFloat() * this.height, this.posZ + this.rand.nextFloat() * this.width * 2.0f - this.width, d0, d2, d3);
        }
        if (this.world.isRemote && this.rand.nextInt(25) == 0 && this.isTamed()) {
            final double d0 = this.rand.nextGaussian() * 0.02;
            final double d2 = this.rand.nextGaussian() * 0.02;
            final double d3 = this.rand.nextGaussian() * 0.02;
            this.world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, this.posX + this.rand.nextFloat() * this.width * 2.0f - this.width, this.posY + 0.5 + this.rand.nextFloat() * this.height, this.posZ + this.rand.nextFloat() * this.width * 2.0f - this.width, d0, d2, d3);
        }
        super.onUpdate();
    }
    
    public void updateAITasks() {
        super.updateAITasks();
        if (this.ticksExisted % 40 == 0) {
            this.heal(1.0f);
        }
    }
    
    public boolean canPickup(final ItemStack entityItem) {
        if (entityItem == null) {
            return false;
        }
        if (!this.isTamed() && EntityPech.valuedItems.containsKey(Item.getIdFromItem(entityItem.getItem()))) {
            return true;
        }
        for (int a = 0; a < this.loot.size(); ++a) {
            if (!this.loot.get(a).isEmpty() && this.loot.get(a).getCount() <= 0) {
                this.loot.set(a, ItemStack.EMPTY);
            }
            if (this.loot.get(a).isEmpty()) {
                return true;
            }
            if (InventoryUtils.areItemStacksEqualStrict(entityItem, this.loot.get(a)) && entityItem.getCount() + this.loot.get(a).getCount() <= this.loot.get(a).getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }
    
    public ItemStack pickupItem(ItemStack entityItem) {
        if (entityItem == null || entityItem.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (this.isTamed() || !this.isValued(entityItem)) {
            for (int a = 0; a < this.loot.size(); ++a) {
                if (this.loot.get(a) != null && this.loot.get(a).getCount() <= 0) {
                    this.loot.set(a, ItemStack.EMPTY);
                }
                if (entityItem != null && !entityItem.isEmpty() && entityItem.getCount() > 0 && !this.loot.get(a).isEmpty() && this.loot.get(a).getCount() < this.loot.get(a).getMaxStackSize() && InventoryUtils.areItemStacksEqualStrict(entityItem, this.loot.get(a))) {
                    if (entityItem.getCount() + this.loot.get(a).getCount() <= this.loot.get(a).getMaxStackSize()) {
                        this.loot.get(a).grow(entityItem.getCount());
                        return ItemStack.EMPTY;
                    }
                    final int sz = Math.min(entityItem.getCount(), this.loot.get(a).getMaxStackSize() - this.loot.get(a).getCount());
                    this.loot.get(a).grow(sz);
                    entityItem.shrink(sz);
                }
                if (entityItem != null && !entityItem.isEmpty() && entityItem.getCount() <= 0) {
                    entityItem = ItemStack.EMPTY;
                }
            }
            for (int a = 0; a < this.loot.size(); ++a) {
                if (!this.loot.get(a).isEmpty() && this.loot.get(a).getCount() <= 0) {
                    this.loot.set(a, ItemStack.EMPTY);
                }
                if (entityItem != null && entityItem.getCount() > 0 && this.loot.get(a).isEmpty()) {
                    this.loot.set(a, entityItem.copy());
                    return null;
                }
            }
            if (entityItem != null && !entityItem.isEmpty() && entityItem.getCount() <= 0) {
                entityItem = ItemStack.EMPTY;
            }
            return entityItem;
        }
        if (this.rand.nextInt(10) < this.getValue(entityItem)) {
            this.setTamed(true);
            this.setCombatTask();
            this.world.setEntityState(this, (byte)18);
        }
        entityItem.shrink(1);
        if (entityItem.getCount() <= 0) {
            return ItemStack.EMPTY;
        }
        return entityItem;
    }
    
    protected boolean processInteract(final EntityPlayer player, final EnumHand hand) {
        if (player.isSneaking() || (player.getHeldItem(hand) != null && player.getHeldItem(hand).getItem() instanceof ItemNameTag)) {
            return false;
        }
        if (this.isTamed()) {
            if (!this.world.isRemote) {
                player.openGui(Thaumcraft.instance, 1, this.world, this.getEntityId(), 0, 0);
            }
            return true;
        }
        return super.processInteract(player, hand);
    }
    
    public boolean isValued(final ItemStack item) {
        if (item == null || item.isEmpty()) {
            return false;
        }
        boolean value = EntityPech.valuedItems.containsKey(Item.getIdFromItem(item.getItem()));
        if (!value) {
            final AspectList al = ThaumcraftCraftingManager.getObjectTags(item);
            if (al.getAmount(Aspect.DESIRE) > 1) {
                value = true;
            }
        }
        return value;
    }
    
    public int getValue(final ItemStack item) {
        if (item == null || item.isEmpty()) {
            return 0;
        }
        int value = EntityPech.valuedItems.containsKey(Item.getIdFromItem(item.getItem())) ? EntityPech.valuedItems.get(Item.getIdFromItem(item.getItem())) : 0;
        if (value == 0) {
            final AspectList al = ThaumcraftCraftingManager.getObjectTags(item);
            value = Math.min(32, al.getAmount(Aspect.DESIRE) / 2);
        }
        return value;
    }
    
    public void setSwingingArms(final boolean swingingArms) {
    }
    
    static {
        TYPE = EntityDataManager.createKey(EntityPech.class, DataSerializers.BYTE);
        ANGER = EntityDataManager.createKey(EntityPech.class, DataSerializers.VARINT);
        TAMED = EntityDataManager.createKey(EntityPech.class, DataSerializers.BOOLEAN);
        LOOT = LootTableList.register(new ResourceLocation("thaumcraft", "pech"));
        EntityPech.valuedItems = new HashMap<Integer, Integer>();
        EntityPech.tradeInventory = new HashMap<Integer, ArrayList<List>>();
    }
}
