package thaumcraft.common.config;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.EntityFallingTaint;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.entities.EntityFollowingItem;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.entities.construct.EntityArcaneBore;
import thaumcraft.common.entities.construct.EntityTurretCrossbow;
import thaumcraft.common.entities.construct.EntityTurretCrossbowAdvanced;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.EntityEldritchCrab;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityFireBat;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;
import thaumcraft.common.entities.monster.EntityInhabitedZombie;
import thaumcraft.common.entities.monster.EntityMindSpider;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.entities.monster.EntitySpellBat;
import thaumcraft.common.entities.monster.EntityThaumicSlime;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.entities.monster.boss.EntityCultistLeader;
import thaumcraft.common.entities.monster.boss.EntityCultistPortalGreater;
import thaumcraft.common.entities.monster.boss.EntityEldritchGolem;
import thaumcraft.common.entities.monster.boss.EntityEldritchWarden;
import thaumcraft.common.entities.monster.boss.EntityTaintacleGiant;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;
import thaumcraft.common.entities.monster.cult.EntityCultistKnight;
import thaumcraft.common.entities.monster.cult.EntityCultistPortalLesser;
import thaumcraft.common.entities.monster.tainted.EntityTaintCrawler;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeedPrime;
import thaumcraft.common.entities.monster.tainted.EntityTaintSwarm;
import thaumcraft.common.entities.monster.tainted.EntityTaintacle;
import thaumcraft.common.entities.monster.tainted.EntityTaintacleSmall;
import thaumcraft.common.entities.projectile.EntityAlumentum;
import thaumcraft.common.entities.projectile.EntityBottleTaint;
import thaumcraft.common.entities.projectile.EntityCausalityCollapser;
import thaumcraft.common.entities.projectile.EntityEldritchOrb;
import thaumcraft.common.entities.projectile.EntityFocusCloud;
import thaumcraft.common.entities.projectile.EntityFocusMine;
import thaumcraft.common.entities.projectile.EntityFocusProjectile;
import thaumcraft.common.entities.projectile.EntityGolemDart;
import thaumcraft.common.entities.projectile.EntityGolemOrb;
import thaumcraft.common.entities.projectile.EntityGrapple;
import thaumcraft.common.golems.EntityThaumcraftGolem;


public class ConfigEntities
{
    public static HashMap<Class, Integer> championModWhitelist;
    
    public static void initEntities(IForgeRegistry<EntityEntry> iForgeRegistry) {
        int id = 0;
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "CultistPortalGreater"), EntityCultistPortalGreater.class, "CultistPortalGreater", id++, Thaumcraft.instance, 64, 20, false, 6842578, 32896);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "CultistPortalLesser"), EntityCultistPortalLesser.class, "CultistPortalLesser", id++, Thaumcraft.instance, 64, 20, false, 9438728, 6316242);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "FluxRift"), EntityFluxRift.class, "FluxRift", id++, Thaumcraft.instance, 64, 20, false);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "SpecialItem"), EntitySpecialItem.class, "SpecialItem", id++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "FollowItem"), EntityFollowingItem.class, "FollowItem", id++, Thaumcraft.instance, 64, 20, false);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "FallingTaint"), EntityFallingTaint.class, "FallingTaint", id++, Thaumcraft.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Alumentum"), EntityAlumentum.class, "Alumentum", id++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "GolemDart"), EntityGolemDart.class, "GolemDart", id++, Thaumcraft.instance, 64, 20, false);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "EldritchOrb"), EntityEldritchOrb.class, "EldritchOrb", id++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "BottleTaint"), EntityBottleTaint.class, "BottleTaint", id++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "GolemOrb"), EntityGolemOrb.class, "GolemOrb", id++, Thaumcraft.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Grapple"), EntityGrapple.class, "Grapple", id++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "CausalityCollapser"), EntityCausalityCollapser.class, "CausalityCollapser", id++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "FocusProjectile"), EntityFocusProjectile.class, "FocusProjectile", id++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "FocusCloud"), EntityFocusCloud.class, "FocusCloud", id++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Focusmine"), EntityFocusMine.class, "Focusmine", id++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TurretBasic"), EntityTurretCrossbow.class, "TurretBasic", id++, Thaumcraft.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TurretAdvanced"), EntityTurretCrossbowAdvanced.class, "TurretAdvanced", id++, Thaumcraft.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "ArcaneBore"), EntityArcaneBore.class, "ArcaneBore", id++, Thaumcraft.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Golem"), EntityThaumcraftGolem.class, "Golem", id++, Thaumcraft.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "EldritchWarden"), EntityEldritchWarden.class, "EldritchWarden", id++, Thaumcraft.instance, 64, 3, true, 6842578, 8421504);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "EldritchGolem"), EntityEldritchGolem.class, "EldritchGolem", id++, Thaumcraft.instance, 64, 3, true, 6842578, 8947848);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "CultistLeader"), EntityCultistLeader.class, "CultistLeader", id++, Thaumcraft.instance, 64, 3, true, 6842578, 9438728);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TaintacleGiant"), EntityTaintacleGiant.class, "TaintacleGiant", id++, Thaumcraft.instance, 96, 3, false, 6842578, 10618530);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "BrainyZombie"), EntityBrainyZombie.class, "BrainyZombie", id++, Thaumcraft.instance, 64, 3, true, -16129, -16744448);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "GiantBrainyZombie"), EntityGiantBrainyZombie.class, "GiantBrainyZombie", id++, Thaumcraft.instance, 64, 3, true, -16129, -16760832);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Wisp"), EntityWisp.class, "Wisp", id++, Thaumcraft.instance, 64, 3, false, -16129, -1);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Firebat"), EntityFireBat.class, "Firebat", id++, Thaumcraft.instance, 64, 3, false, -16129, -806354944);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Spellbat"), EntitySpellBat.class, "Spellbat", id++, Thaumcraft.instance, 64, 3, false, -16129, -806354944);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Pech"), EntityPech.class, "Pech", id++, Thaumcraft.instance, 64, 3, true, -16129, -12582848);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "MindSpider"), EntityMindSpider.class, "MindSpider", id++, Thaumcraft.instance, 64, 3, true, 4996656, 4473924);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "EldritchGuardian"), EntityEldritchGuardian.class, "EldritchGuardian", id++, Thaumcraft.instance, 64, 3, true, 8421504, 0);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "CultistKnight"), EntityCultistKnight.class, "CultistKnight", id++, Thaumcraft.instance, 64, 3, true, 9438728, 128);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "CultistCleric"), EntityCultistCleric.class, "CultistCleric", id++, Thaumcraft.instance, 64, 3, true, 9438728, 8388608);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "EldritchCrab"), EntityEldritchCrab.class, "EldritchCrab", id++, Thaumcraft.instance, 64, 3, true, 8421504, 5570560);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "InhabitedZombie"), EntityInhabitedZombie.class, "InhabitedZombie", id++, Thaumcraft.instance, 64, 3, true, 8421504, 5570560);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "ThaumSlime"), EntityThaumicSlime.class, "ThaumSlime", id++, Thaumcraft.instance, 64, 3, true, 10618530, -32513);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TaintCrawler"), EntityTaintCrawler.class, "TaintCrawler", id++, Thaumcraft.instance, 64, 3, true, 10618530, 3158064);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Taintacle"), EntityTaintacle.class, "Taintacle", id++, Thaumcraft.instance, 64, 3, false, 10618530, 4469572);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TaintacleTiny"), EntityTaintacleSmall.class, "TaintacleTiny", id++, Thaumcraft.instance, 64, 3, false);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TaintSwarm"), EntityTaintSwarm.class, "TaintSwarm", id++, Thaumcraft.instance, 64, 3, false, 10618530, 16744576);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TaintSeed"), EntityTaintSeed.class, "TaintSeed", id++, Thaumcraft.instance, 64, 20, false, 10618530, 4465237);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TaintSeedPrime"), EntityTaintSeedPrime.class, "TaintSeedPrime", id++, Thaumcraft.instance, 64, 20, false, 10618530, 5583718);
        EntityPech.valuedItems.put(Item.getIdFromItem(Items.ENDER_PEARL), 15);
        ArrayList<List> forInv = new ArrayList<List>();
        forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 0)));
        forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 1)));
        forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 6)));
        forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 7)));
        if (ModConfig.foundCopperIngot) {
            forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 2)));
        }
        if (ModConfig.foundTinIngot) {
            forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 3)));
        }
        if (ModConfig.foundSilverIngot) {
            forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 4)));
        }
        if (ModConfig.foundLeadIngot) {
            forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 5)));
        }
        forInv.add(Arrays.asList(2, new ItemStack(Items.BLAZE_ROD)));
        forInv.add(Arrays.asList(2, new ItemStack(BlocksTC.saplingGreatwood)));
        forInv.add(Arrays.asList(2, new ItemStack(Items.DRAGON_BREATH)));
        forInv.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forInv.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forInv.add(Arrays.asList(3, new ItemStack(Items.GOLDEN_APPLE, 1, 0)));
        forInv.add(Arrays.asList(4, new ItemStack(ItemsTC.thaumiumPick)));
        forInv.add(Arrays.asList(4, new ItemStack(ItemsTC.thaumiumAxe)));
        forInv.add(Arrays.asList(4, new ItemStack(ItemsTC.thaumiumHoe)));
        forInv.add(Arrays.asList(5, new ItemStack(Items.GOLDEN_APPLE, 1, 1)));
        forInv.add(Arrays.asList(5, new ItemStack(BlocksTC.saplingSilverwood)));
        forInv.add(Arrays.asList(5, new ItemStack(ItemsTC.curio, 1, 4)));
        EntityPech.tradeInventory.put(0, forInv);
        ArrayList<List> forMag = new ArrayList<List>();
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.AIR)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.EARTH)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.FIRE)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.WATER)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.ORDER)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.ENTROPY)));
        forMag.add(Arrays.asList(2, new ItemStack(Items.POTIONITEM, 1, 8193)));
        forMag.add(Arrays.asList(2, new ItemStack(Items.POTIONITEM, 1, 8261)));
        forMag.add(Arrays.asList(2, ThaumcraftApiHelper.makeCrystal(Aspect.FLUX)));
        forMag.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forMag.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forMag.add(Arrays.asList(3, ThaumcraftApiHelper.makeCrystal(Aspect.AURA)));
        forMag.add(Arrays.asList(3, new ItemStack(Items.GOLDEN_APPLE, 1, 0)));
        forMag.add(Arrays.asList(4, new ItemStack(ItemsTC.clothBoots)));
        forMag.add(Arrays.asList(4, new ItemStack(ItemsTC.clothChest)));
        forMag.add(Arrays.asList(4, new ItemStack(ItemsTC.clothLegs)));
        forMag.add(Arrays.asList(5, new ItemStack(Items.GOLDEN_APPLE, 1, 1)));
        forMag.add(Arrays.asList(5, new ItemStack(ItemsTC.pechWand)));
        forMag.add(Arrays.asList(5, new ItemStack(ItemsTC.curio, 1, 4)));
        forMag.add(Arrays.asList(5, new ItemStack(ItemsTC.amuletVis, 1, 0)));
        forInv.add(Arrays.asList(5, new ItemStack(Items.TOTEM_OF_UNDYING)));
        EntityPech.tradeInventory.put(1, forMag);
        ArrayList<List> forArc = new ArrayList<List>();
        for (int a = 0; a < 15; ++a) {
            forArc.add(Arrays.asList(1, new ItemStack(BlocksTC.candles.get(EnumDyeColor.byDyeDamage(a)))));
        }
        forArc.add(Arrays.asList(2, new ItemStack(Items.GHAST_TEAR)));
        forInv.add(Arrays.asList(2, new ItemStack(Items.COMPASS)));
        forArc.add(Arrays.asList(2, ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(Enchantments.POWER, 1))));
        forArc.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forArc.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forArc.add(Arrays.asList(3, new ItemStack(Items.GOLDEN_APPLE, 1, 0)));
        forArc.add(Arrays.asList(4, new ItemStack(ItemsTC.eldritchEye)));
        forArc.add(Arrays.asList(4, new ItemStack(Items.GOLDEN_APPLE, 1, 1)));
        forInv.add(Arrays.asList(4, new ItemStack(Items.SPECTRAL_ARROW)));
        forArc.add(Arrays.asList(5, new ItemStack(ItemsTC.baubles, 1, 3)));
        forArc.add(Arrays.asList(5, ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(Enchantments.FLAME, 1))));
        forArc.add(Arrays.asList(5, ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(Enchantments.INFINITY, 1))));
        forArc.add(Arrays.asList(5, new ItemStack(ItemsTC.curio, 1, 4)));
        EntityPech.tradeInventory.put(2, forArc);
    }
    
    public static void postInitEntitySpawns() {
        ArrayList<Biome> biomes = new ArrayList<Biome>();
        for (BiomeManager.BiomeEntry be : BiomeManager.getBiomes(BiomeManager.BiomeType.WARM)) {
            biomes.add(be.biome);
        }
        for (BiomeManager.BiomeEntry be : BiomeManager.getBiomes(BiomeManager.BiomeType.COOL)) {
            biomes.add(be.biome);
        }
        for (BiomeManager.BiomeEntry be : BiomeManager.getBiomes(BiomeManager.BiomeType.ICY)) {
            biomes.add(be.biome);
        }
        for (BiomeManager.BiomeEntry be : BiomeManager.getBiomes(BiomeManager.BiomeType.DESERT)) {
            biomes.add(be.biome);
        }
        Biome[] allBiomes = biomes.toArray(new Biome[] { null });
        if (ModConfig.CONFIG_WORLD.allowSpawnAngryZombie) {
            for (Biome bgb : biomes) {
                if (bgb != null && (bgb.getSpawnableList(EnumCreatureType.MONSTER) != null & bgb.getSpawnableList(EnumCreatureType.MONSTER).size() > 0)) {
                    EntityRegistry.addSpawn(EntityBrainyZombie.class, 10, 1, 1, EnumCreatureType.MONSTER, bgb);
                }
            }
        }
        if (ModConfig.CONFIG_WORLD.allowSpawnPech) {
            for (Biome bgb : BiomeDictionary.getBiomes(BiomeDictionary.Type.MAGICAL)) {
                if (bgb != null && (bgb.getSpawnableList(EnumCreatureType.MONSTER) != null & bgb.getSpawnableList(EnumCreatureType.MONSTER).size() > 0)) {
                    EntityRegistry.addSpawn(EntityPech.class, 10, 1, 1, EnumCreatureType.MONSTER, bgb);
                }
            }
        }
        if (ModConfig.CONFIG_WORLD.allowSpawnFireBat) {
            EntityRegistry.addSpawn(EntityFireBat.class, 10, 1, 2, EnumCreatureType.MONSTER, BiomeDictionary.getBiomes(BiomeDictionary.Type.NETHER).toArray(new Biome[0]));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
                EntityRegistry.addSpawn(EntityFireBat.class, 5, 1, 2, EnumCreatureType.MONSTER, biomes.toArray(allBiomes));
            }
        }
        if (ModConfig.CONFIG_WORLD.allowSpawnWisp) {
            EntityRegistry.addSpawn(EntityWisp.class, 5, 1, 1, EnumCreatureType.MONSTER, BiomeDictionary.getBiomes(BiomeDictionary.Type.NETHER).toArray(new Biome[0]));
        }
        FMLInterModComms.sendMessage("Thaumcraft", "championWhiteList", "Zombie:0");
        FMLInterModComms.sendMessage("Thaumcraft", "championWhiteList", "Spider:0");
        FMLInterModComms.sendMessage("Thaumcraft", "championWhiteList", "Blaze:0");
        FMLInterModComms.sendMessage("Thaumcraft", "championWhiteList", "Enderman:0");
        FMLInterModComms.sendMessage("Thaumcraft", "championWhiteList", "Skeleton:0");
        FMLInterModComms.sendMessage("Thaumcraft", "championWhiteList", "Witch:1");
        FMLInterModComms.sendMessage("Thaumcraft", "championWhiteList", "Thaumcraft.EldritchCrab:0");
        FMLInterModComms.sendMessage("Thaumcraft", "championWhiteList", "Thaumcraft.Taintacle:2");
        FMLInterModComms.sendMessage("Thaumcraft", "championWhiteList", "Thaumcraft.InhabitedZombie:3");
    }
    
    static {
        ConfigEntities.championModWhitelist = new HashMap<Class, Integer>();
    }
}
