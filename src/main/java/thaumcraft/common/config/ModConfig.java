package thaumcraft.common.config;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.EntityFallingTaint;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import thaumcraft.common.entities.monster.EntityEldritchCrab;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityInhabitedZombie;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.entities.monster.boss.EntityCultistLeader;
import thaumcraft.common.entities.monster.boss.EntityCultistPortalGreater;
import thaumcraft.common.entities.monster.boss.EntityEldritchGolem;
import thaumcraft.common.entities.monster.boss.EntityEldritchWarden;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;
import thaumcraft.common.entities.monster.cult.EntityCultistKnight;
import thaumcraft.common.entities.monster.cult.EntityCultistPortalLesser;
import thaumcraft.common.lib.utils.CropUtils;
import thaumcraft.common.lib.utils.Utils;


public class ModConfig
{
    public static float auraSize = 4.0f;
    public static ArrayList<Aspect> aspectOrder;
    public static boolean foundCopperIngot;
    public static boolean foundTinIngot;
    public static boolean foundSilverIngot;
    public static boolean foundLeadIngot;
    public static boolean foundCopperOre;
    public static boolean foundTinOre;
    public static boolean foundSilverOre;
    public static boolean foundLeadOre;
    public static boolean isHalloween;
    
    public static void postInitLoot() {
        int COMMON = 0;
        int UNCOMMON = 1;
        int RARE = 2;
        Random rand = new Random(System.currentTimeMillis());
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.GOLD_NUGGET, 1), 2500, 0);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.GOLD_NUGGET, 2), 2250, 1);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.GOLD_NUGGET, 3), 2000, 2);
        ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.salisMundus), 3, 0);
        ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.salisMundus), 6, 1);
        ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.salisMundus), 9, 2);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.CHORUS_FRUIT), 5, 0, 1, 2);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.COMPASS), 5, 0, 1, 2);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.COOKIE), 5, 0, 1, 2);
        ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.primordialPearl, 1, 7), 1, 0);
        ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.primordialPearl, 1, 7), 3, 1);
        ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.primordialPearl, 1, 6), 1, 1);
        ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.primordialPearl, 1, 5), 9, 2);
        ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.primordialPearl, 1, 3), 3, 2);
        ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.primordialPearl, 1), 1, 2);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.NETHER_STAR), 1, 2);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.DIAMOND), 10, 0);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.DIAMOND), 50, 1, 2);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.EMERALD), 15, 0);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.EMERALD), 75, 1, 2);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.GOLD_INGOT), 100, 0, 1, 2);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.ENDER_PEARL), 100, 0, 1, 2);
        ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.amuletVis, 1, 0), 6, 1, 2);
        ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.baubles, 1, 0), 10, 0);
        ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.baubles, 1, 1), 10, 0);
        ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.baubles, 1, 2), 10, 0);
        ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.baubles, 1, 3), 5, 2);
        ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.baubles, 1, 4), 5, 1);
        ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.baubles, 1, 5), 5, 1);
        ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.baubles, 1, 6), 5, 1);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.EXPERIENCE_BOTTLE), 5, 0);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.EXPERIENCE_BOTTLE), 10, 1);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.EXPERIENCE_BOTTLE), 20, 2);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.GOLDEN_APPLE, 1, 1), 1, 0);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.GOLDEN_APPLE, 1, 1), 2, 1);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.GOLDEN_APPLE, 1, 1), 3, 2);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.GOLDEN_APPLE, 1, 0), 3, 0);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.GOLDEN_APPLE, 1, 0), 6, 1);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.GOLDEN_APPLE, 1, 0), 9, 2);
        ThaumcraftApi.addLootBagItem(new ItemStack(Items.BOOK), 10, 0, 1, 2);
        for (PotionType pt : PotionType.REGISTRY) {
            ThaumcraftApi.addLootBagItem(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), pt), 2, 0, 1, 2);
            ThaumcraftApi.addLootBagItem(PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), pt), 2, 0, 1, 2);
            ThaumcraftApi.addLootBagItem(PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION), pt), 2, 1, 2);
        }
        ItemStack[] commonLoot = { new ItemStack(ItemsTC.lootBag, 1, 0), new ItemStack(ItemsTC.ingots), new ItemStack(ItemsTC.amber) };
        ItemStack[] uncommonLoot = { new ItemStack(ItemsTC.lootBag, 1, 1), new ItemStack(ItemsTC.baubles, 1, 0), new ItemStack(ItemsTC.baubles, 1, 1), new ItemStack(ItemsTC.baubles, 1, 2) };
        ItemStack[] rareLoot = { new ItemStack(ItemsTC.lootBag, 1, 2), new ItemStack(ItemsTC.thaumonomicon), new ItemStack(ItemsTC.thaumiumSword), new ItemStack(ItemsTC.thaumiumAxe), new ItemStack(ItemsTC.thaumiumHoe), new ItemStack(ItemsTC.thaumiumPick), new ItemStack(ItemsTC.baubles, 1, 3), new ItemStack(ItemsTC.baubles, 1, 4), new ItemStack(ItemsTC.baubles, 1, 5), new ItemStack(ItemsTC.baubles, 1, 6), new ItemStack(ItemsTC.amuletVis, 1, 0) };
    }
    
    public static void modCompatibility() {
        Thaumcraft.log.info("Checking for mod & oredict compatibilities");
        ResourceLocation defaultGroup = new ResourceLocation("");
        try {
            if (OreDictionary.doesOreNameExist("oreIron") && OreDictionary.getOres("oreIron", false).size() > 1) {
                for (ItemStack is : OreDictionary.getOres("oreIron", false)) {
                    if (is.getItem() != Item.getItemFromBlock(Blocks.IRON_ORE)) {
                        Utils.addSpecialMiningResult(is, new ItemStack(ItemsTC.clusters, 1, 0), 1.0f);
                    }
                }
            }
            if (OreDictionary.doesOreNameExist("oreGold") && OreDictionary.getOres("oreGold", false).size() > 1) {
                for (ItemStack is : OreDictionary.getOres("oreGold", false)) {
                    if (is.getItem() != Item.getItemFromBlock(Blocks.GOLD_ORE)) {
                        Utils.addSpecialMiningResult(is, new ItemStack(ItemsTC.clusters, 1, 1), 1.0f);
                    }
                }
            }
        }
        catch (Exception ex) {}
        if (OreDictionary.doesOreNameExist("oreCopper")) {
            for (ItemStack is : OreDictionary.getOres("oreCopper", false)) {
                Utils.addSpecialMiningResult(is, new ItemStack(ItemsTC.clusters, 1, 2), 1.0f);
                ModConfig.foundCopperOre = true;
            }
        }
        if (OreDictionary.doesOreNameExist("ingotCopper")) {
            boolean first = true;
            for (ItemStack is2 : OreDictionary.getOres("ingotCopper", false)) {
                if (is2.getCount() > 1) {
                    is2.setCount(1);
                }
                ModConfig.foundCopperIngot = true;
                GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "coppertonuggets"), defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 1), "#", '#', is2);
                if (first) {
                    first = false;
                    FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemsTC.clusters, 1, 2), new ItemStack(is2.getItem(), 2, is2.getItemDamage()), 1.0f);
                    ConfigRecipes.oreDictRecipe("coppernuggetstoingot", defaultGroup, is2, new Object[] { "###", "###", "###", '#', new ItemStack(ItemsTC.nuggets, 1, 1) });
                }
            }
        }
        if (OreDictionary.doesOreNameExist("oreTin")) {
            for (ItemStack is : OreDictionary.getOres("oreTin", false)) {
                Utils.addSpecialMiningResult(is, new ItemStack(ItemsTC.clusters, 1, 3), 1.0f);
                ModConfig.foundTinOre = true;
            }
        }
        if (OreDictionary.doesOreNameExist("ingotTin")) {
            boolean first = true;
            for (ItemStack is2 : OreDictionary.getOres("ingotTin", false)) {
                if (is2.getCount() > 1) {
                    is2.setCount(1);
                }
                ModConfig.foundTinIngot = true;
                GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "tintonuggets"), defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 2), "#", '#', is2);
                if (first) {
                    first = false;
                    FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemsTC.clusters, 1, 3), new ItemStack(is2.getItem(), 2, is2.getItemDamage()), 1.0f);
                    ConfigRecipes.oreDictRecipe("tinnuggetstoingot", defaultGroup, is2, new Object[] { "###", "###", "###", '#', new ItemStack(ItemsTC.nuggets, 1, 2) });
                }
            }
        }
        if (OreDictionary.doesOreNameExist("oreSilver")) {
            for (ItemStack is : OreDictionary.getOres("oreSilver", false)) {
                Utils.addSpecialMiningResult(is, new ItemStack(ItemsTC.clusters, 1, 4), 1.0f);
                ModConfig.foundSilverOre = true;
            }
        }
        if (OreDictionary.doesOreNameExist("ingotSilver")) {
            boolean first = true;
            for (ItemStack is2 : OreDictionary.getOres("ingotSilver", false)) {
                if (is2.getCount() > 1) {
                    is2.setCount(1);
                }
                ModConfig.foundSilverIngot = true;
                GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "silvertonuggets"), defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 3), "#", '#', is2);
                if (first) {
                    first = false;
                    FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemsTC.clusters, 1, 4), new ItemStack(is2.getItem(), 2, is2.getItemDamage()), 1.0f);
                    ConfigRecipes.oreDictRecipe("silvernuggetstoingot", defaultGroup, is2, new Object[] { "###", "###", "###", '#', new ItemStack(ItemsTC.nuggets, 1, 3) });
                }
            }
        }
        if (OreDictionary.doesOreNameExist("oreLead")) {
            for (ItemStack is : OreDictionary.getOres("oreLead", false)) {
                Utils.addSpecialMiningResult(is, new ItemStack(ItemsTC.clusters, 1, 5), 1.0f);
                ModConfig.foundLeadOre = true;
            }
        }
        if (OreDictionary.doesOreNameExist("ingotLead")) {
            boolean first = true;
            for (ItemStack is2 : OreDictionary.getOres("ingotLead", false)) {
                if (is2.getCount() > 1) {
                    is2.setCount(1);
                }
                ModConfig.foundLeadIngot = true;
                GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "leadtonuggets"), defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 4), "#", '#', is2);
                if (first) {
                    first = false;
                    FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemsTC.clusters, 1, 5), new ItemStack(is2.getItem(), 2, is2.getItemDamage()), 1.0f);
                    ConfigRecipes.oreDictRecipe("leadnuggetstoingot", defaultGroup, is2, new Object[] { "###", "###", "###", '#', new ItemStack(ItemsTC.nuggets, 1, 4) });
                }
            }
        }
        Thaumcraft.log.info("Adding entities to MFR safari net blacklist.");
        registerSafariNetBlacklist(EntityOwnedConstruct.class);
        registerSafariNetBlacklist(EntityFallingTaint.class);
        registerSafariNetBlacklist(EntityWisp.class);
        registerSafariNetBlacklist(EntityPech.class);
        registerSafariNetBlacklist(EntityEldritchGuardian.class);
        registerSafariNetBlacklist(EntityEldritchWarden.class);
        registerSafariNetBlacklist(EntityEldritchGolem.class);
        registerSafariNetBlacklist(EntityCultistCleric.class);
        registerSafariNetBlacklist(EntityCultistKnight.class);
        registerSafariNetBlacklist(EntityCultistLeader.class);
        registerSafariNetBlacklist(EntityCultistPortalGreater.class);
        registerSafariNetBlacklist(EntityCultistPortalLesser.class);
        registerSafariNetBlacklist(EntityEldritchCrab.class);
        registerSafariNetBlacklist(EntityInhabitedZombie.class);
    }
    
    public static void registerSafariNetBlacklist(Class<?> blacklistedEntity) {
        try {
            Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
            if (registry != null) {
                Method reg = registry.getMethod("registerSafariNetBlacklist", Class.class);
                reg.invoke(registry, blacklistedEntity);
            }
        }
        catch (Exception ex) {}
    }
    
    public static void postInitMisc() {
        for (Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
            if (item != null && item instanceof IPlantable) {
                try {
                    IBlockState bs = ((IPlantable)item).getPlant(null, null);
                    if (bs == null) {
                        continue;
                    }
                    ThaumcraftApi.registerSeed(bs.getBlock(), new ItemStack(item));
                }
                catch (Exception ex) {}
            }
        }
        CropUtils.addStandardCrop(Blocks.MELON_BLOCK, 32767);
        CropUtils.addStandardCrop(Blocks.PUMPKIN, 32767);
        CropUtils.addStackedCrop(Blocks.REEDS, 32767);
        CropUtils.addStackedCrop(Blocks.CACTUS, 32767);
        CropUtils.addStandardCrop(Blocks.NETHER_WART, 3);
        ThaumcraftApi.registerSeed(Blocks.COCOA, new ItemStack(Items.DYE, 1, EnumDyeColor.BROWN.getDyeDamage()));
        Utils.addSpecialMiningResult(new ItemStack(Blocks.IRON_ORE), new ItemStack(ItemsTC.clusters, 1, 0), 1.0f);
        Utils.addSpecialMiningResult(new ItemStack(Blocks.GOLD_ORE), new ItemStack(ItemsTC.clusters, 1, 1), 1.0f);
        Utils.addSpecialMiningResult(new ItemStack(BlocksTC.oreCinnabar), new ItemStack(ItemsTC.clusters, 1, 6), 1.0f);
        Utils.addSpecialMiningResult(new ItemStack(Items.QUARTZ), new ItemStack(ItemsTC.clusters, 1, 7), 1.0f);
        Collection<Aspect> pa = Aspect.aspects.values();
        for (Aspect aspect : pa) {
            ModConfig.aspectOrder.add(aspect);
        }
    }
    
    static {
        ModConfig.aspectOrder = new ArrayList<Aspect>();
        ModConfig.foundCopperIngot = false;
        ModConfig.foundTinIngot = false;
        ModConfig.foundSilverIngot = false;
        ModConfig.foundLeadIngot = false;
        ModConfig.foundCopperOre = false;
        ModConfig.foundTinOre = false;
        ModConfig.foundSilverOre = false;
        ModConfig.foundLeadOre = false;
    }
    
    @Config.LangKey("thaumcraft.config.graphics")
    @Config(modid = "thaumcraft", type = Config.Type.INSTANCE, name = "thaumcraft_graphics")
    public static class CONFIG_GRAPHICS
    {
        @Config.Comment({ "Setting this to true will make the amount text in aspect tags twice as large. Useful for certain resolutions and custom fonts." })
        public static boolean largeTagText;
        @Config.Comment({ "This setting will disable certain thaumcraft shaders for those who experience FPS drops." })
        public static boolean disableShaders;
        @Config.Comment({ "Set to true to disable anxiety triggers like the heartbeat sound." })
        public static boolean nostress;
        @Config.Comment({ "Hate crooked labels, kittens, puppies and all things awesome? If yes, set this to false." })
        public static boolean crooked;
        @Config.Comment({ "Set to true to have the wand dial display in the bottom left instead of the top left." })
        public static boolean dialBottom;
        @Config.Comment({ "Item aspects are hidden by default and pressing shift reveals them.", "Changing this setting to 'true' will reverse this behaviour and always", "display aspects unless shift is pressed." })
        public static boolean showTags;
        @Config.Comment({ "Set this to true to get the old blue magical forest back." })
        public static boolean blueBiome;
        @Config.Comment({ "Will golems display emote particles if they recieve orders or encounter problems" })
        public static boolean showGolemEmotes;
        
        static {
            CONFIG_GRAPHICS.largeTagText = false;
            CONFIG_GRAPHICS.disableShaders = false;
            CONFIG_GRAPHICS.nostress = false;
            CONFIG_GRAPHICS.crooked = true;
            CONFIG_GRAPHICS.dialBottom = false;
            CONFIG_GRAPHICS.showTags = false;
            CONFIG_GRAPHICS.blueBiome = false;
            CONFIG_GRAPHICS.showGolemEmotes = true;
        }
    }
    
    @Config.LangKey("thaumcraft.config.world")
    @Config(modid = "thaumcraft", type = Config.Type.INSTANCE, name = "thaumcraft_world")
    public static class CONFIG_WORLD
    {
        @Config.Comment({ "The dimension considered to be your 'overworld'. Certain TC structures will only spawn in this dim." })
        @Config.RequiresMcRestart
        public static int overworldDim;
        @Config.Comment({ "Outer lands dimension id" })
        @Config.RequiresMcRestart
        public static int dimensionOuterId;
        @Config.Comment({ "The % of normal ore amounts that will be spawned. For example 50 will spawn half", "the ores while 200 will spawn double. Default 100" })
        @Config.RangeInt(min = 1, max = 500)
        @Config.RequiresMcRestart
        public static int oreDensity;
        public static boolean generateMagicForest;
        @Config.Comment({ "Higher values increases number of magical forest biomes. If you are using biome", "addon mods you probably want to increase this weight quite a bit" })
        @Config.RangeInt(min = 0, max = 100)
        @Config.RequiresMcRestart
        public static int biomeMagicalForestWeight;
        @Config.Comment({ "The % chance of taint fibres spreading on a block tick.", "Setting this to 0 will effectively stop taint fibre spread." })
        @Config.RangeInt(min = 0, max = 500)
        public static float taintSpreadRate;
        @Config.Comment({ "The range at which taint can spread from a taint seed.", "This value is only a base and will be modified by flux levels." })
        @Config.RangeInt(min = 8, max = 256)
        public static int taintSpreadArea;
        public static boolean generateAura;
        public static boolean generateStructure;
        public static boolean generateCinnabar;
        public static boolean generateAmber;
        public static boolean generateQuartz;
        public static boolean generateCrystals;
        public static boolean generateTrees;
        @Config.Comment({ "This key is used to keep track of which chunk have been generated/regenerated.", "Changing it will cause the regeneration code to run again, so only change it if you want it to happen.", "Useful to regen only one world feature at a time." })
        public static String regenKey;
        public static boolean regenAura;
        public static boolean regenStructure;
        public static boolean regenCinnabar;
        public static boolean regenAmber;
        public static boolean regenQuartz;
        public static boolean regenCrystals;
        public static boolean regenTrees;
        public static boolean allowSpawnAngryZombie;
        public static boolean allowSpawnFireBat;
        public static boolean allowSpawnWisp;
        public static boolean allowSpawnTaintacle;
        public static boolean allowSpawnPech;
        public static boolean allowSpawnElder;
        @Config.Comment({ "Setting this to false will disable spawning champion mobs. Even when false they will still", "have a greatly reduced chance of spawning in certain dangerous places." })
        public static boolean allowChampionMobs;
        
        static {
            CONFIG_WORLD.overworldDim = 0;
            CONFIG_WORLD.dimensionOuterId = -42;
            CONFIG_WORLD.oreDensity = 100;
            CONFIG_WORLD.generateMagicForest = true;
            CONFIG_WORLD.biomeMagicalForestWeight = 5;
            CONFIG_WORLD.taintSpreadRate = 100.0f;
            CONFIG_WORLD.taintSpreadArea = 32;
            CONFIG_WORLD.generateAura = true;
            CONFIG_WORLD.generateStructure = true;
            CONFIG_WORLD.generateCinnabar = true;
            CONFIG_WORLD.generateAmber = true;
            CONFIG_WORLD.generateQuartz = true;
            CONFIG_WORLD.generateCrystals = true;
            CONFIG_WORLD.generateTrees = true;
            CONFIG_WORLD.regenKey = "DEFAULT";
            CONFIG_WORLD.regenAura = false;
            CONFIG_WORLD.regenStructure = false;
            CONFIG_WORLD.regenCinnabar = false;
            CONFIG_WORLD.regenAmber = false;
            CONFIG_WORLD.regenQuartz = false;
            CONFIG_WORLD.regenCrystals = false;
            CONFIG_WORLD.regenTrees = false;
            CONFIG_WORLD.allowSpawnAngryZombie = true;
            CONFIG_WORLD.allowSpawnFireBat = true;
            CONFIG_WORLD.allowSpawnWisp = true;
            CONFIG_WORLD.allowSpawnTaintacle = true;
            CONFIG_WORLD.allowSpawnPech = true;
            CONFIG_WORLD.allowSpawnElder = true;
            CONFIG_WORLD.allowChampionMobs = true;
        }
    }
    
    @Config.LangKey("thaumcraft.config.misc")
    @Config(modid = "thaumcraft", type = Config.Type.INSTANCE, name = "thaumcraft_misc")
    public static class CONFIG_MISC
    {
        @Config.Comment({ "Setting this to true will make you get the recipe book for salis mundus without having to sleep first." })
        public static boolean noSleep;
        @Config.Comment({ "Setting this to true disables Warp, Taint spread and similar mechanics. You wuss." })
        public static boolean wussMode;
        @Config.Comment({ "Enables a version of the Thauminomicon in creative mode that grants you all the research when you first use it." })
        public static boolean allowCheatSheet;
        @Config.Comment({ "How many milliseconds passes between runic shielding recharge ticks.", "Lower values equals faster recharge. Minimum of 500." })
        @Config.RangeInt(min = 500, max = 500000)
        public static int shieldRecharge;
        @Config.Comment({ "How many milliseconds passes after a shield has been reduced to zero", "before it can start recharging again. Minimum of 0." })
        @Config.RangeInt(min = 0, max = 50000)
        public static int shieldWait;
        @Config.Comment({ "How much vis it costs to reacharge a single unit of shielding. Minimum of 0." })
        @Config.RangeInt(min = 0, max = 500)
        public static int shieldCost;
        
        static {
            CONFIG_MISC.noSleep = false;
            CONFIG_MISC.wussMode = false;
            CONFIG_MISC.allowCheatSheet = false;
            CONFIG_MISC.shieldRecharge = 2000;
            CONFIG_MISC.shieldWait = 4000;
            CONFIG_MISC.shieldCost = 1;
        }
    }
}
