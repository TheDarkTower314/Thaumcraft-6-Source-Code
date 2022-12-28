package thaumcraft.common.entities.monster;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusMediumRoot;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.ai.pech.AINearestAttackableTargetPech;
import thaumcraft.common.entities.ai.pech.AIPechItemEntityGoto;
import thaumcraft.common.entities.ai.pech.AIPechTradePlayer;
import thaumcraft.common.items.casters.foci.FocusEffectAir;
import thaumcraft.common.items.casters.foci.FocusEffectCurse;
import thaumcraft.common.items.casters.foci.FocusEffectEarth;
import thaumcraft.common.items.casters.foci.FocusEffectFire;
import thaumcraft.common.items.casters.foci.FocusEffectFlux;
import thaumcraft.common.items.casters.foci.FocusMediumProjectile;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.world.biomes.BiomeHandler;


public class EntityPech extends EntityMob implements IRangedAttackMob
{
    public NonNullList<ItemStack> loot;
    public boolean trading;
    private EntityAIAttackRanged aiArrowAttack;
    private EntityAIAttackRanged aiBlastAttack;
    private EntityAIAttackMelee aiMeleeAttack;
    private EntityAIAvoidEntity aiAvoidPlayer;
    private static DataParameter<Byte> TYPE;
    private static DataParameter<Integer> ANGER;
    private static DataParameter<Boolean> TAMED;
    public static ResourceLocation LOOT;
    public float mumble;
    int chargecount;
    public static HashMap<Integer, Integer> valuedItems;
    public static HashMap<Integer, ArrayList<List>> tradeInventory;
    
    public EntityPech(World world) {
        super(world);
        loot = NonNullList.withSize(9, ItemStack.EMPTY);
        trading = false;
        aiArrowAttack = new EntityAIAttackRanged(this, 0.6, 20, 50, 15.0f);
        aiBlastAttack = new EntityAIAttackRanged(this, 0.6, 20, 50, 15.0f);
        aiMeleeAttack = new EntityAIAttackMelee(this, 0.6, false);
        aiAvoidPlayer = new EntityAIAvoidEntity(this, EntityPlayer.class, 8.0f, 0.5, 0.6);
        mumble = 0.0f;
        chargecount = 0;
        setSize(0.6f, 1.8f);
        experienceValue = 8;
    }
    
    public String getName() {
        if (hasCustomName()) {
            return getCustomNameTag();
        }
        switch (getPechType()) {
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
        ((PathNavigateGround) getNavigator()).setCanSwim(false);
        setDropChance(EntityEquipmentSlot.MAINHAND, 0.2f);
        setDropChance(EntityEquipmentSlot.OFFHAND, 0.2f);
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new AIPechTradePlayer(this));
        tasks.addTask(3, new AIPechItemEntityGoto(this));
        tasks.addTask(5, new EntityAIOpenDoor(this, true));
        tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.5));
        tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0, false));
        tasks.addTask(9, new EntityAIWander(this, 0.6));
        tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0f, 1.0f));
        tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0f));
        tasks.addTask(11, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(2, new AINearestAttackableTargetPech(this, EntityPlayer.class, true));
    }
    
    public void setCombatTask() {
        if (world != null && !world.isRemote) {
            tasks.removeTask(aiMeleeAttack);
            tasks.removeTask(aiArrowAttack);
            tasks.removeTask(aiBlastAttack);
            ItemStack itemstack = getHeldItemMainhand();
            if (itemstack.getItem() == Items.BOW) {
                tasks.addTask(2, aiArrowAttack);
            }
            else if (itemstack.getItem() == ItemsTC.pechWand) {
                tasks.addTask(2, aiBlastAttack);
            }
            else {
                tasks.addTask(2, aiMeleeAttack);
            }
            if (isTamed()) {
                tasks.removeTask(aiAvoidPlayer);
            }
            else {
                tasks.addTask(4, aiAvoidPlayer);
            }
        }
    }
    
    public void attackEntityWithRangedAttack(EntityLivingBase target, float f) {
        if (getPechType() == 2) {
            EntityTippedArrow entityarrow = new EntityTippedArrow(world, this);
            if (world.rand.nextFloat() < 0.2) {
                ItemStack itemstack = new ItemStack(Items.TIPPED_ARROW);
                PotionUtils.appendEffects(itemstack, Collections.singletonList(new PotionEffect(MobEffects.POISON, 40)));
                entityarrow.setPotionEffect(itemstack);
            }
            double d0 = target.posX - posX;
            double d2 = target.getEntityBoundingBox().minY + target.height / 3.0f - entityarrow.posY;
            double d3 = target.posZ - posZ;
            double d4 = MathHelper.sqrt(d0 * d0 + d3 * d3);
            entityarrow.shoot(d0, d2 + d4 * 0.20000000298023224, d3, 1.6f, (float)(14 - world.getDifficulty().getDifficultyId() * 4));
            int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, this);
            int j = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, this);
            entityarrow.setDamage(f * 2.0f + rand.nextGaussian() * 0.25 + world.getDifficulty().getDifficultyId() * 0.11f);
            if (i > 0) {
                entityarrow.setDamage(entityarrow.getDamage() + i * 0.5 + 0.5);
            }
            if (j > 0) {
                entityarrow.setKnockbackStrength(j);
            }
            if (EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FLAME, this) > 0) {
                entityarrow.setFire(100);
            }
            playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, 1.0f / (getRNG().nextFloat() * 0.4f + 0.8f));
            world.spawnEntity(entityarrow);
        }
        else if (getPechType() == 1) {
            FocusPackage p = new FocusPackage(this);
            FocusMediumRoot root = new FocusMediumRoot();
            double off = getDistance(target) / 6.0f;
            root.setupFromCasterToTarget(this, target, off);
            p.addNode(root);
            FocusMediumProjectile fp = new FocusMediumProjectile();
            fp.initialize();
            fp.getSetting("speed").setValue(2);
            p.addNode(fp);
            p.addNode(rand.nextBoolean() ? new FocusEffectCurse() : (rand.nextBoolean() ? new FocusEffectFlux() : (rand.nextBoolean() ? new FocusEffectEarth() : (rand.nextBoolean() ? new FocusEffectAir() : new FocusEffectFire()))));
            FocusEngine.castFocusPackage(this, p, true);
            swingArm(getActiveHand());
        }
    }
    
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, @Nullable ItemStack stack) {
        super.setItemStackToSlot(slotIn, stack);
        if (!world.isRemote && slotIn == EntityEquipmentSlot.MAINHAND) {
            setCombatTask();
        }
    }
    
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        super.setEquipmentBasedOnDifficulty(difficulty);
        switch (rand.nextInt(20)) {
            case 0:
            case 12: {
                setHeldItem(getActiveHand(), new ItemStack(ItemsTC.pechWand));
                break;
            }
            case 1: {
                setHeldItem(getActiveHand(), new ItemStack(Items.STONE_SWORD));
                break;
            }
            case 3: {
                setHeldItem(getActiveHand(), new ItemStack(Items.STONE_AXE));
                break;
            }
            case 5: {
                setHeldItem(getActiveHand(), new ItemStack(Items.IRON_SWORD));
                break;
            }
            case 6: {
                setHeldItem(getActiveHand(), new ItemStack(Items.IRON_AXE));
                break;
            }
            case 7: {
                setHeldItem(getActiveHand(), new ItemStack(Items.FISHING_ROD));
                break;
            }
            case 8: {
                setHeldItem(getActiveHand(), new ItemStack(Items.STONE_PICKAXE));
                break;
            }
            case 9: {
                setHeldItem(getActiveHand(), new ItemStack(Items.IRON_PICKAXE));
                break;
            }
            case 2:
            case 4:
            case 10:
            case 11:
            case 13: {
                setHeldItem(getActiveHand(), new ItemStack(Items.BOW));
                break;
            }
        }
    }
    
    public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData data) {
        setEquipmentBasedOnDifficulty(diff);
        ItemStack itemstack = getHeldItem(getActiveHand());
        if (itemstack.getItem() == ItemsTC.pechWand) {
            setPechType(1);
            setDropChance((getActiveHand() == EnumHand.MAIN_HAND) ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, 0.1f);
        }
        else if (!itemstack.isEmpty()) {
            if (itemstack.getItem() == Items.BOW) {
                setPechType(2);
            }
            setEnchantmentBasedOnDifficulty(diff);
        }
        float f = diff.getClampedAdditionalDifficulty();
        setCanPickUpLoot(rand.nextFloat() < 0.75f * f);
        setCombatTask();
        return super.onInitialSpawn(diff, data);
    }
    
    public boolean getCanSpawnHere() {
        Biome biome = world.getBiome(new BlockPos(this));
        boolean magicBiome = false;
        if (biome != null) {
            magicBiome = BiomeDictionary.hasType(biome, BiomeDictionary.Type.MAGICAL);
        }
        int count = 0;
        try {
            List l = world.getEntitiesWithinAABB(EntityPech.class, getEntityBoundingBox().grow(16.0, 16.0, 16.0));
            if (l != null) {
                count = l.size();
            }
        }
        catch (Exception ex) {}
        if (world.provider.getDimension() != ModConfig.CONFIG_WORLD.overworldDim && biome != BiomeHandler.MAGICAL_FOREST && biome != BiomeHandler.EERIE) {
            magicBiome = false;
        }
        return count < 4 && magicBiome && super.getCanSpawnHere();
    }
    
    public float getEyeHeight() {
        return height * 0.66f;
    }
    
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }
    
    protected void entityInit() {
        super.entityInit();
        getDataManager().register(EntityPech.TYPE, (byte)0);
        getDataManager().register(EntityPech.ANGER, 0);
        getDataManager().register(EntityPech.TAMED, false);
    }
    
    public int getPechType() {
        return getDataManager().get(EntityPech.TYPE);
    }
    
    public int getAnger() {
        return getDataManager().get(EntityPech.ANGER);
    }
    
    public boolean isTamed() {
        return getDataManager().get(EntityPech.TAMED);
    }
    
    public void setPechType(int par1) {
        getDataManager().set(EntityPech.TYPE, (byte)par1);
    }
    
    public void setAnger(int par1) {
        getDataManager().set(EntityPech.ANGER, par1);
    }
    
    public void setTamed(boolean par1) {
        getDataManager().set(EntityPech.TAMED, par1);
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
    }
    
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("PechType", (byte) getPechType());
        nbt.setShort("Anger", (short) getAnger());
        nbt.setBoolean("Tamed", isTamed());
        ItemStackHelper.saveAllItems(nbt, loot);
    }
    
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("PechType")) {
            byte b0 = nbt.getByte("PechType");
            setPechType(b0);
        }
        setAnger(nbt.getShort("Anger"));
        setTamed(nbt.getBoolean("Tamed"));
        ItemStackHelper.loadAllItems(nbt, loot = NonNullList.withSize(9, ItemStack.EMPTY));
        setCombatTask();
    }
    
    protected boolean canDespawn() {
        try {
            if (loot == null) {
                return true;
            }
            int q = 0;
            for (ItemStack is : loot) {
                if (is != null && is.getCount() > 0) {
                    ++q;
                }
            }
            return q < 5;
        }
        catch (Exception e) {
            return true;
        }
    }
    
    public boolean canBeLeashedTo(EntityPlayer player) {
        return false;
    }
    
    protected ResourceLocation getLootTable() {
        return EntityPech.LOOT;
    }
    
    protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
        for (int a = 0; a < loot.size(); ++a) {
            if (!loot.get(a).isEmpty() && world.rand.nextFloat() < 0.33f) {
                entityDropItem(loot.get(a).copy(), 1.5f);
            }
        }
        super.dropLoot(wasRecentlyHit, lootingModifier, source);
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte par1) {
        if (par1 == 16) {
            mumble = 3.1415927f;
        }
        else if (par1 == 17) {
            mumble = 6.2831855f;
        }
        else if (par1 == 18) {
            for (int i = 0; i < 5; ++i) {
                double d0 = rand.nextGaussian() * 0.02;
                double d2 = rand.nextGaussian() * 0.02;
                double d3 = rand.nextGaussian() * 0.02;
                world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, posX + rand.nextFloat() * width * 2.0f - width, posY + 0.5 + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0f - width, d0, d2, d3);
            }
        }
        if (par1 == 19) {
            for (int i = 0; i < 5; ++i) {
                double d0 = rand.nextGaussian() * 0.02;
                double d2 = rand.nextGaussian() * 0.02;
                double d3 = rand.nextGaussian() * 0.02;
                world.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, posX + rand.nextFloat() * width * 2.0f - width, posY + 0.5 + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0f - width, d0, d2, d3);
            }
            mumble = 6.2831855f;
        }
        else {
            super.handleStatusUpdate(par1);
        }
    }
    
    public void playLivingSound() {
        if (!world.isRemote) {
            if (rand.nextInt(3) == 0) {
                List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(4.0, 2.0, 4.0));
                for (int i = 0; i < list.size(); ++i) {
                    Entity entity1 = list.get(i);
                    if (entity1 instanceof EntityPech) {
                        world.setEntityState(this, (byte)17);
                        playSound(SoundsTC.pech_trade, getSoundVolume(), getSoundPitch());
                        return;
                    }
                }
            }
            world.setEntityState(this, (byte)16);
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
    
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsTC.pech_hit;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundsTC.pech_death;
    }
    
    private void becomeAngryAt(Entity entity) {
        if (entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative()) {
            return;
        }
        if (getAnger() <= 0) {
            world.setEntityState(this, (byte)19);
            playSound(SoundsTC.pech_charge, getSoundVolume(), getSoundPitch());
        }
        setAttackTarget((EntityLivingBase)entity);
        setAnger(400 + rand.nextInt(400));
        setTamed(false);
        setCombatTask();
    }
    
    public int getTotalArmorValue() {
        int i = super.getTotalArmorValue() + 2;
        if (i > 20) {
            i = 20;
        }
        return i;
    }
    
    public boolean attackEntityFrom(DamageSource damSource, float par2) {
        if (isEntityInvulnerable(damSource)) {
            return false;
        }
        Entity entity = damSource.getTrueSource();
        if (entity instanceof EntityPlayer) {
            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(32.0, 16.0, 32.0));
            for (int i = 0; i < list.size(); ++i) {
                Entity entity2 = list.get(i);
                if (entity2 instanceof EntityPech) {
                    EntityPech entitypech = (EntityPech)entity2;
                    entitypech.becomeAngryAt(entity);
                }
            }
            becomeAngryAt(entity);
        }
        return super.attackEntityFrom(damSource, par2);
    }
    
    public void onUpdate() {
        if (mumble > 0.0f) {
            mumble *= 0.75f;
        }
        if (getAnger() > 0) {
            setAnger(getAnger() - 1);
        }
        if (getAnger() > 0 && getAttackTarget() != null) {
            if (chargecount > 0) {
                --chargecount;
            }
            if (chargecount == 0) {
                chargecount = 100;
                playSound(SoundsTC.pech_charge, getSoundVolume(), getSoundPitch());
            }
            world.setEntityState(this, (byte)17);
        }
        if (world.isRemote && rand.nextInt(15) == 0 && getAnger() > 0) {
            double d0 = rand.nextGaussian() * 0.02;
            double d2 = rand.nextGaussian() * 0.02;
            double d3 = rand.nextGaussian() * 0.02;
            world.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, posX + rand.nextFloat() * width * 2.0f - width, posY + 0.5 + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0f - width, d0, d2, d3);
        }
        if (world.isRemote && rand.nextInt(25) == 0 && isTamed()) {
            double d0 = rand.nextGaussian() * 0.02;
            double d2 = rand.nextGaussian() * 0.02;
            double d3 = rand.nextGaussian() * 0.02;
            world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, posX + rand.nextFloat() * width * 2.0f - width, posY + 0.5 + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0f - width, d0, d2, d3);
        }
        super.onUpdate();
    }
    
    public void updateAITasks() {
        super.updateAITasks();
        if (ticksExisted % 40 == 0) {
            heal(1.0f);
        }
    }
    
    public boolean canPickup(ItemStack entityItem) {
        if (entityItem == null) {
            return false;
        }
        if (!isTamed() && EntityPech.valuedItems.containsKey(Item.getIdFromItem(entityItem.getItem()))) {
            return true;
        }
        for (int a = 0; a < loot.size(); ++a) {
            if (!loot.get(a).isEmpty() && loot.get(a).getCount() <= 0) {
                loot.set(a, ItemStack.EMPTY);
            }
            if (loot.get(a).isEmpty()) {
                return true;
            }
            if (InventoryUtils.areItemStacksEqualStrict(entityItem, loot.get(a)) && entityItem.getCount() + loot.get(a).getCount() <= loot.get(a).getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }
    
    public ItemStack pickupItem(ItemStack entityItem) {
        if (entityItem == null || entityItem.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (isTamed() || !isValued(entityItem)) {
            for (int a = 0; a < loot.size(); ++a) {
                if (loot.get(a) != null && loot.get(a).getCount() <= 0) {
                    loot.set(a, ItemStack.EMPTY);
                }
                if (entityItem != null && !entityItem.isEmpty() && entityItem.getCount() > 0 && !loot.get(a).isEmpty() && loot.get(a).getCount() < loot.get(a).getMaxStackSize() && InventoryUtils.areItemStacksEqualStrict(entityItem, loot.get(a))) {
                    if (entityItem.getCount() + loot.get(a).getCount() <= loot.get(a).getMaxStackSize()) {
                        loot.get(a).grow(entityItem.getCount());
                        return ItemStack.EMPTY;
                    }
                    int sz = Math.min(entityItem.getCount(), loot.get(a).getMaxStackSize() - loot.get(a).getCount());
                    loot.get(a).grow(sz);
                    entityItem.shrink(sz);
                }
                if (entityItem != null && !entityItem.isEmpty() && entityItem.getCount() <= 0) {
                    entityItem = ItemStack.EMPTY;
                }
            }
            for (int a = 0; a < loot.size(); ++a) {
                if (!loot.get(a).isEmpty() && loot.get(a).getCount() <= 0) {
                    loot.set(a, ItemStack.EMPTY);
                }
                if (entityItem != null && entityItem.getCount() > 0 && loot.get(a).isEmpty()) {
                    loot.set(a, entityItem.copy());
                    return null;
                }
            }
            if (entityItem != null && !entityItem.isEmpty() && entityItem.getCount() <= 0) {
                entityItem = ItemStack.EMPTY;
            }
            return entityItem;
        }
        if (rand.nextInt(10) < getValue(entityItem)) {
            setTamed(true);
            setCombatTask();
            world.setEntityState(this, (byte)18);
        }
        entityItem.shrink(1);
        if (entityItem.getCount() <= 0) {
            return ItemStack.EMPTY;
        }
        return entityItem;
    }
    
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (player.isSneaking() || (player.getHeldItem(hand) != null && player.getHeldItem(hand).getItem() instanceof ItemNameTag)) {
            return false;
        }
        if (isTamed()) {
            if (!world.isRemote) {
                player.openGui(Thaumcraft.instance, 1, world, getEntityId(), 0, 0);
            }
            return true;
        }
        return super.processInteract(player, hand);
    }
    
    public boolean isValued(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return false;
        }
        boolean value = EntityPech.valuedItems.containsKey(Item.getIdFromItem(item.getItem()));
        if (!value) {
            AspectList al = ThaumcraftCraftingManager.getObjectTags(item);
            if (al.getAmount(Aspect.DESIRE) > 1) {
                value = true;
            }
        }
        return value;
    }
    
    public int getValue(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return 0;
        }
        int value = EntityPech.valuedItems.containsKey(Item.getIdFromItem(item.getItem())) ? EntityPech.valuedItems.get(Item.getIdFromItem(item.getItem())) : 0;
        if (value == 0) {
            AspectList al = ThaumcraftCraftingManager.getObjectTags(item);
            value = Math.min(32, al.getAmount(Aspect.DESIRE) / 2);
        }
        return value;
    }
    
    public void setSwingingArms(boolean swingingArms) {
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
