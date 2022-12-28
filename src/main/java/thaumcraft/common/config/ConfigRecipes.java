package thaumcraft.common.config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.IngredientNBTTC;
import thaumcraft.api.crafting.Part;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.basic.BlockPillar;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.lib.crafting.DustTriggerMultiblock;
import thaumcraft.common.lib.crafting.DustTriggerOre;
import thaumcraft.common.lib.crafting.DustTriggerSimple;
import thaumcraft.common.lib.crafting.InfusionEnchantmentRecipe;
import thaumcraft.common.lib.crafting.InfusionRunicAugmentRecipe;
import thaumcraft.common.lib.crafting.RecipeMagicDust;
import thaumcraft.common.lib.crafting.RecipeTripleMeatTreat;
import thaumcraft.common.lib.crafting.RecipesRobeArmorDyes;
import thaumcraft.common.lib.crafting.RecipesVoidRobeArmorDyes;
import thaumcraft.common.lib.crafting.ShapedArcaneVoidJar;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;


public class ConfigRecipes
{
    static ResourceLocation defaultGroup;
    public static HashMap<String, ArrayList<ResourceLocation>> recipeGroups;
    
    public static void initializeCompoundRecipes() {
        IDustTrigger.registerDustTrigger(new DustTriggerSimple("!gotdream", Blocks.BOOKSHELF, new ItemStack(ItemsTC.thaumonomicon)));
        IDustTrigger.registerDustTrigger(new DustTriggerOre("!gotdream", "bookshelf", new ItemStack(ItemsTC.thaumonomicon)));
        IDustTrigger.registerDustTrigger(new DustTriggerSimple("FIRSTSTEPS@1", Blocks.CRAFTING_TABLE, new ItemStack(BlocksTC.arcaneWorkbench)));
        IDustTrigger.registerDustTrigger(new DustTriggerOre("FIRSTSTEPS@1", "workbench", new ItemStack(BlocksTC.arcaneWorkbench)));
        IDustTrigger.registerDustTrigger(new DustTriggerSimple("UNLOCKALCHEMY@1", Blocks.CAULDRON, new ItemStack(BlocksTC.crucible)));
        Part NB = new Part(Blocks.NETHER_BRICK, new ItemStack(BlocksTC.placeholderNetherbrick));
        Part OB = new Part(Blocks.OBSIDIAN, new ItemStack(BlocksTC.placeholderObsidian));
        Part IB = new Part(Blocks.IRON_BARS, "AIR");
        Part LA = new Part(Material.LAVA, BlocksTC.infernalFurnace, true);
        Part[][][] infernalFurnaceBlueprint = { { { NB, OB, NB }, { OB, null, OB }, { NB, OB, NB } }, { { NB, OB, NB }, { OB, LA, OB }, { NB, IB, NB } }, { { NB, OB, NB }, { OB, OB, OB }, { NB, OB, NB } } };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("INFERNALFURNACE", infernalFurnaceBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(new ResourceLocation("thaumcraft:infernalfurnace"), new ThaumcraftApi.BluePrint("INFERNALFURNACE", infernalFurnaceBlueprint, new ItemStack(Blocks.NETHER_BRICK, 12), new ItemStack(Blocks.OBSIDIAN, 12), new ItemStack(Blocks.IRON_BARS), new ItemStack(Items.LAVA_BUCKET)));
        Part IM = new Part(BlocksTC.infusionMatrix, null);
        Part SNT = new Part(BlocksTC.stoneArcane, "AIR");
        Part SNB1 = new Part(BlocksTC.stoneArcane, new ItemStack(BlocksTC.pillarArcane, 1, BlockPillar.calcMeta(EnumFacing.EAST)));
        Part SNB2 = new Part(BlocksTC.stoneArcane, new ItemStack(BlocksTC.pillarArcane, 1, BlockPillar.calcMeta(EnumFacing.NORTH)));
        Part SNB3 = new Part(BlocksTC.stoneArcane, new ItemStack(BlocksTC.pillarArcane, 1, BlockPillar.calcMeta(EnumFacing.SOUTH)));
        Part SNB4 = new Part(BlocksTC.stoneArcane, new ItemStack(BlocksTC.pillarArcane, 1, BlockPillar.calcMeta(EnumFacing.WEST)));
        Part PN = new Part(BlocksTC.pedestalArcane.getDefaultState(), null);
        Part[][][] infusionAltarNormalBlueprint = { { { null, null, null }, { null, IM, null }, { null, null, null } }, { { SNT, null, SNT }, { null, null, null }, { SNT, null, SNT } }, { { SNB1, null, SNB2 }, { null, PN, null }, { SNB3, null, SNB4 } } };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("INFUSION", infusionAltarNormalBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(new ResourceLocation("thaumcraft:infusionaltar"), new ThaumcraftApi.BluePrint("INFUSION", infusionAltarNormalBlueprint, new ItemStack(BlocksTC.stoneArcane, 8), new ItemStack(BlocksTC.pedestalArcane), new ItemStack(BlocksTC.infusionMatrix)));
        Part SAT = new Part(BlocksTC.stoneAncient, "AIR");
        Part SAB1 = new Part(BlocksTC.stoneAncient, new ItemStack(BlocksTC.pillarAncient, 1, BlockPillar.calcMeta(EnumFacing.EAST)));
        Part SAB2 = new Part(BlocksTC.stoneAncient, new ItemStack(BlocksTC.pillarAncient, 1, BlockPillar.calcMeta(EnumFacing.NORTH)));
        Part SAB3 = new Part(BlocksTC.stoneAncient, new ItemStack(BlocksTC.pillarAncient, 1, BlockPillar.calcMeta(EnumFacing.SOUTH)));
        Part SAB4 = new Part(BlocksTC.stoneAncient, new ItemStack(BlocksTC.pillarAncient, 1, BlockPillar.calcMeta(EnumFacing.WEST)));
        Part PA = new Part(BlocksTC.pedestalAncient.getStateFromMeta(2), null);
        Part[][][] infusionAltarAncientBlueprint = { { { null, null, null }, { null, IM, null }, { null, null, null } }, { { SAT, null, SAT }, { null, null, null }, { SAT, null, SAT } }, { { SAB1, null, SAB2 }, { null, PA, null }, { SAB3, null, SAB4 } } };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("INFUSIONANCIENT", infusionAltarAncientBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(new ResourceLocation("thaumcraft:infusionaltarancient"), new ThaumcraftApi.BluePrint("INFUSIONANCIENT", infusionAltarAncientBlueprint, new ItemStack(BlocksTC.stoneAncient, 8), new ItemStack(BlocksTC.pedestalAncient), new ItemStack(BlocksTC.infusionMatrix)));
        Part SET = new Part(BlocksTC.stoneEldritchTile, "AIR");
        Part SEB1 = new Part(BlocksTC.stoneEldritchTile, new ItemStack(BlocksTC.pillarEldritch, 1, BlockPillar.calcMeta(EnumFacing.EAST)));
        Part SEB2 = new Part(BlocksTC.stoneEldritchTile, new ItemStack(BlocksTC.pillarEldritch, 1, BlockPillar.calcMeta(EnumFacing.NORTH)));
        Part SEB3 = new Part(BlocksTC.stoneEldritchTile, new ItemStack(BlocksTC.pillarEldritch, 1, BlockPillar.calcMeta(EnumFacing.SOUTH)));
        Part SEB4 = new Part(BlocksTC.stoneEldritchTile, new ItemStack(BlocksTC.pillarEldritch, 1, BlockPillar.calcMeta(EnumFacing.WEST)));
        Part PE = new Part(BlocksTC.pedestalEldritch.getStateFromMeta(1), null);
        Part[][][] infusionAltarEldritchBlueprint = { { { null, null, null }, { null, IM, null }, { null, null, null } }, { { SET, null, SET }, { null, null, null }, { SET, null, SET } }, { { SEB1, null, SEB2 }, { null, PE, null }, { SEB3, null, SEB4 } } };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("INFUSIONELDRITCH", infusionAltarEldritchBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(new ResourceLocation("thaumcraft:infusionaltareldritch"), new ThaumcraftApi.BluePrint("INFUSIONELDRITCH", infusionAltarEldritchBlueprint, new ItemStack(BlocksTC.stoneEldritchTile, 8), new ItemStack(BlocksTC.pedestalEldritch), new ItemStack(BlocksTC.infusionMatrix)));
        Part TH1 = new Part(BlocksTC.metalAlchemical.getDefaultState(), BlocksTC.thaumatoriumTop).setApplyPlayerFacing(true);
        Part TH2 = new Part(BlocksTC.metalAlchemical.getDefaultState(), BlocksTC.thaumatorium).setApplyPlayerFacing(true);
        Part TH3 = new Part(BlocksTC.crucible, null);
        Part[][][] thaumotoriumBlueprint = { { { TH1 } }, { { TH2 } }, { { TH3 } } };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("THAUMATORIUM", thaumotoriumBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(new ResourceLocation("thaumcraft:Thaumatorium"), new ThaumcraftApi.BluePrint("THAUMATORIUM", thaumotoriumBlueprint, new ItemStack(BlocksTC.metalAlchemical, 2), new ItemStack(BlocksTC.crucible)));
        Part GP1 = new Part(Blocks.IRON_BARS, new ItemStack(BlocksTC.placeholderBars));
        Part GP2 = new Part(Blocks.CAULDRON, new ItemStack(BlocksTC.placeholderCauldron));
        Part GP3 = new Part(Blocks.PISTON.getDefaultState().withProperty((IProperty)BlockPistonBase.FACING, (Comparable)EnumFacing.UP), BlocksTC.golemBuilder);
        Part GP4 = new Part(Blocks.ANVIL, new ItemStack(BlocksTC.placeholderAnvil));
        Part GP5 = new Part(BlocksTC.tableStone, new ItemStack(BlocksTC.placeholderTable));
        Part[][][] golempressBlueprint = { { { null, null }, { GP1, null } }, { { GP2, GP4 }, { GP3, GP5 } } };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("MINDCLOCKWORK", golempressBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(new ResourceLocation("thaumcraft:GolemPress"), new ThaumcraftApi.BluePrint("MINDCLOCKWORK", new ItemStack(BlocksTC.golemBuilder), golempressBlueprint, new ItemStack(Blocks.IRON_BARS), new ItemStack(Items.CAULDRON), new ItemStack(Blocks.PISTON), new ItemStack(Blocks.ANVIL), new ItemStack(BlocksTC.tableStone)));
    }
    
    public static void initializeAlchemyRecipes() {
        ResourceLocation visCrystalGroup = new ResourceLocation("thaumcraft:viscrystalgroup");
        CrucibleRecipe[] cre = new CrucibleRecipe[Aspect.aspects.size()];
        for (Aspect aspect : Aspect.aspects.values()) {
            ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:vis_crystal_" + aspect.getTag()), new CrucibleRecipe("BASEALCHEMY", ThaumcraftApiHelper.makeCrystal(aspect), "nuggetQuartz", new AspectList().add(aspect, 2)).setGroup(visCrystalGroup));
        }
        ResourceLocation nitorGroup = new ResourceLocation("thaumcraft", "nitorgroup");
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:nitor"), new CrucibleRecipe("UNLOCKALCHEMY@3", new ItemStack(BlocksTC.nitor.get(EnumDyeColor.YELLOW)), "dustGlowstone", new AspectList().merge(Aspect.ENERGY, 10).merge(Aspect.FIRE, 10).merge(Aspect.LIGHT, 10)));
        int a = 0;
        for (EnumDyeColor d : EnumDyeColor.values()) {
            shapelessOreDictRecipe("NitorDye" + d.getUnlocalizedName().toLowerCase(), nitorGroup, new ItemStack(BlocksTC.nitor.get(d)), new Object[] { ConfigAspects.dyes[15 - a], "nitor" });
            ++a;
        }
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:alumentum"), new CrucibleRecipe("ALUMENTUM", new ItemStack(ItemsTC.alumentum), new ItemStack(Items.COAL, 1, 32767), new AspectList().merge(Aspect.ENERGY, 10).merge(Aspect.FIRE, 10).merge(Aspect.ENTROPY, 5)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:brassingot"), new CrucibleRecipe("METALLURGY@1", new ItemStack(ItemsTC.ingots, 1, 2), "ingotIron", new AspectList().merge(Aspect.TOOL, 5)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:thaumiumingot"), new CrucibleRecipe("METALLURGY@2", new ItemStack(ItemsTC.ingots, 1, 0), "ingotIron", new AspectList().merge(Aspect.MAGIC, 5).merge(Aspect.EARTH, 5)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:voidingot"), new CrucibleRecipe("BASEELDRITCH", new ItemStack(ItemsTC.ingots, 1, 1), new ItemStack(ItemsTC.voidSeed), new AspectList().merge(Aspect.METAL, 10).merge(Aspect.FLUX, 5)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_tallow"), new CrucibleRecipe("HEDGEALCHEMY@1", new ItemStack(ItemsTC.tallow), new ItemStack(Items.ROTTEN_FLESH), new AspectList().merge(Aspect.FIRE, 1)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_leather"), new CrucibleRecipe("HEDGEALCHEMY@1", new ItemStack(Items.LEATHER), new ItemStack(Items.ROTTEN_FLESH), new AspectList().merge(Aspect.AIR, 3).merge(Aspect.BEAST, 3)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:focus_1"), new CrucibleRecipe("UNLOCKAUROMANCY", new ItemStack(ItemsTC.focus1), ConfigItems.ORDER_CRYSTAL, new AspectList().merge(Aspect.CRYSTAL, 20).merge(Aspect.MAGIC, 10).merge(Aspect.AURA, 5)));
        ArrayList<CrucibleRecipe> rl = new ArrayList<CrucibleRecipe>();
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:metal_purification_iron"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters, 1, 0), "oreIron", new AspectList().merge(Aspect.METAL, 5).merge(Aspect.ORDER, 5)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:metal_purification_gold"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters, 1, 1), "oreGold", new AspectList().merge(Aspect.METAL, 5).merge(Aspect.ORDER, 5)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:metal_purification_cinnabar"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters, 1, 6), "oreCinnabar", new AspectList().merge(Aspect.METAL, 5).merge(Aspect.ORDER, 5)));
        if (ModConfig.foundCopperOre) {
            ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:metal_purification_copper"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters, 1, 2), "oreCopper", new AspectList().merge(Aspect.METAL, 5).merge(Aspect.ORDER, 5)));
        }
        if (ModConfig.foundTinOre) {
            ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:metal_purification_tin"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters, 1, 3), "oreTin", new AspectList().merge(Aspect.METAL, 5).merge(Aspect.ORDER, 5)));
        }
        if (ModConfig.foundSilverOre) {
            ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:metal_purification_silver"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters, 1, 4), "oreSilver", new AspectList().merge(Aspect.METAL, 5).merge(Aspect.ORDER, 5)));
        }
        if (ModConfig.foundLeadOre) {
            ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:metal_purification_lead"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters, 1, 5), "oreLead", new AspectList().merge(Aspect.METAL, 5).merge(Aspect.ORDER, 5)));
        }
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:LiquidDeath"), new CrucibleRecipe("LIQUIDDEATH", FluidUtil.getFilledBucket(new FluidStack(ConfigBlocks.FluidDeath.instance, 1000)), new ItemStack(Items.BUCKET), new AspectList().add(Aspect.DEATH, 100).add(Aspect.ALCHEMY, 20).add(Aspect.ENTROPY, 50)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:BottleTaint"), new CrucibleRecipe("BOTTLETAINT", new ItemStack(ItemsTC.bottleTaint), ItemPhial.makeFilledPhial(Aspect.FLUX), new AspectList().add(Aspect.FLUX, 30).add(Aspect.WATER, 30)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:BathSalts"), new CrucibleRecipe("BATHSALTS", new ItemStack(ItemsTC.bathSalts), new ItemStack(ItemsTC.salisMundus), new AspectList().add(Aspect.MIND, 40).add(Aspect.AIR, 40).add(Aspect.ORDER, 40).add(Aspect.LIFE, 40)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SaneSoap"), new CrucibleRecipe("SANESOAP", new ItemStack(ItemsTC.sanitySoap), new ItemStack(BlocksTC.fleshBlock), new AspectList().add(Aspect.MIND, 75).add(Aspect.ELDRITCH, 50).add(Aspect.ORDER, 75).add(Aspect.LIFE, 50)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealCollect"), new CrucibleRecipe("SEALCOLLECT", GolemHelper.getSealStack("thaumcraft:pickup"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.DESIRE, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealCollectAdv"), new CrucibleRecipe("SEALCOLLECT&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:pickup_advanced"), GolemHelper.getSealStack("thaumcraft:pickup"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStore"), new CrucibleRecipe("SEALSTORE", GolemHelper.getSealStack("thaumcraft:fill"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.AVERSION, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStoreAdv"), new CrucibleRecipe("SEALSTORE&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:fill_advanced"), GolemHelper.getSealStack("thaumcraft:fill"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealEmpty"), new CrucibleRecipe("SEALEMPTY", GolemHelper.getSealStack("thaumcraft:empty"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.VOID, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealEmptyAdv"), new CrucibleRecipe("SEALEMPTY&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:empty_advanced"), GolemHelper.getSealStack("thaumcraft:empty"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealProvide"), new CrucibleRecipe("SEALPROVIDE", GolemHelper.getSealStack("thaumcraft:provider"), GolemHelper.getSealStack("thaumcraft:empty_advanced"), new AspectList().add(Aspect.EXCHANGE, 10).add(Aspect.DESIRE, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStock"), new CrucibleRecipe("SEALSTOCK", GolemHelper.getSealStack("thaumcraft:stock"), GolemHelper.getSealStack("thaumcraft:fill"), new AspectList().add(Aspect.MIND, 10).add(Aspect.DESIRE, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealGuard"), new CrucibleRecipe("SEALGUARD", GolemHelper.getSealStack("thaumcraft:guard"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.AVERSION, 20).add(Aspect.PROTECT, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealGuardAdv"), new CrucibleRecipe("SEALGUARD&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:guard_advanced"), GolemHelper.getSealStack("thaumcraft:guard"), new AspectList().add(Aspect.SENSES, 20).add(Aspect.MIND, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealLumber"), new CrucibleRecipe("SEALLUMBER", GolemHelper.getSealStack("thaumcraft:lumber"), GolemHelper.getSealStack("thaumcraft:breaker"), new AspectList().add(Aspect.PLANT, 40).add(Aspect.SENSES, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealUse"), new CrucibleRecipe("SEALUSE", GolemHelper.getSealStack("thaumcraft:use"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.CRAFT, 20).add(Aspect.SENSES, 10).add(Aspect.MIND, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealBreakAdv"), new CrucibleRecipe("SEALBREAK&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:breaker_advanced"), GolemHelper.getSealStack("thaumcraft:breaker"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10).add(Aspect.TOOL, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:EverfullUrn"), new CrucibleRecipe("EVERFULLURN", new ItemStack(BlocksTC.everfullUrn), new ItemStack(Items.FLOWER_POT), new AspectList().add(Aspect.WATER, 30).add(Aspect.CRAFT, 10).add(Aspect.EARTH, 10)));
    }
    
    public static void initializeArcaneRecipes(IForgeRegistry<IRecipe> iForgeRegistry) {
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:mechanism_simple"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BASEARTIFICE", 10, new AspectList().add(Aspect.FIRE, 1).add(Aspect.WATER, 1), ItemsTC.mechanismSimple, " B ", "ISI", " B ", 'B', "plateBrass", 'I', "plateIron", 'S', "stickWood"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:mechanism_complex"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BASEARTIFICE", 50, new AspectList().add(Aspect.FIRE, 1).add(Aspect.WATER, 1), ItemsTC.mechanismComplex, " M ", "TQT", " M ", 'T', "plateThaumium", 'Q', Blocks.PISTON, 'M', new ItemStack(ItemsTC.mechanismSimple)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:vis_resonator"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKAUROMANCY@2", 50, new AspectList().add(Aspect.AIR, 1).add(Aspect.WATER, 1), ItemsTC.visResonator, "plateIron", "gemQuartz"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:activatorrail"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "FIRSTSTEPS", 10, null, BlocksTC.activatorRail, new ItemStack(Blocks.ACTIVATOR_RAIL)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:thaumometer"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "FIRSTSTEPS@2", 20, new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.FIRE, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), ItemsTC.thaumometer, " I ", "IGI", " I ", 'I', "ingotGold", 'G', new ItemStack(Blocks.GLASS_PANE)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:sanitychecker"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "WARP", 20, new AspectList().add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), ItemsTC.sanityChecker, "BN ", "M N", "BN ", 'N', "nuggetBrass", 'B', new ItemStack(ItemsTC.brain), 'M', new ItemStack(ItemsTC.mirroredGlass)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:rechargepedestal"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "RECHARGEPEDESTAL", 100, new AspectList().add(Aspect.AIR, 1).add(Aspect.ORDER, 1), BlocksTC.rechargePedestal, " R ", "DID", "SSS", 'I', "ingotGold", 'D', "gemDiamond", 'R', new ItemStack(ItemsTC.visResonator), 'S', "stone"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:workbenchcharger"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "WORKBENCHCHARGER", 200, new AspectList().add(Aspect.AIR, 2).add(Aspect.ORDER, 2), new ItemStack(BlocksTC.arcaneWorkbenchCharger), " R ", "W W", "I I", 'I', "ingotIron", 'R', new ItemStack(ItemsTC.visResonator), 'W', new ItemStack(BlocksTC.plankGreatwood)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:wand_workbench"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BASEAUROMANCY@2", 100, new AspectList().add(Aspect.EARTH, 1).add(Aspect.WATER, 1), new ItemStack(BlocksTC.wandWorkbench), "ISI", "BRB", "GTG", 'S', new ItemStack(BlocksTC.slabArcaneStone), 'T', new ItemStack(BlocksTC.tableStone), 'R', new ItemStack(ItemsTC.visResonator), 'B', new ItemStack(BlocksTC.stoneArcane), 'G', "ingotGold", 'I', "plateIron"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:caster_basic"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKAUROMANCY@2", 100, new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.FIRE, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), new ItemStack(ItemsTC.casterBasic), "III", "LRL", "LTL", 'T', new ItemStack(ItemsTC.thaumometer), 'R', new ItemStack(ItemsTC.visResonator), 'L', "leather", 'I', "ingotIron"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:EnchantedFabric"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKINFUSION", 5, null, new ItemStack(ItemsTC.fabric), " S ", "SCS", " S ", 'S', "string", 'C', new ItemStack(Blocks.WOOL, 1, 32767)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:RobeChest"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKINFUSION", 100, null, new ItemStack(ItemsTC.clothChest, 1), "I I", "III", "III", 'I', new ItemStack(ItemsTC.fabric)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:RobeLegs"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKINFUSION", 100, null, new ItemStack(ItemsTC.clothLegs, 1), "III", "I I", "I I", 'I', new ItemStack(ItemsTC.fabric)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:RobeBoots"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKINFUSION", 100, null, new ItemStack(ItemsTC.clothBoots, 1), "I I", "I I", 'I', new ItemStack(ItemsTC.fabric)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Goggles"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKARTIFICE", 50, null, new ItemStack(ItemsTC.goggles), "LGL", "L L", "TGT", 'T', new ItemStack(ItemsTC.thaumometer), 'G', "ingotBrass", 'L', "leather"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:SealBlank"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "CONTROLSEALS", 20, new AspectList().add(Aspect.AIR, 1), new ItemStack(ItemsTC.seals, 3), new Object[] { new ItemStack(Items.CLAY_BALL), new ItemStack(ItemsTC.tallow), "dyeRed", "nitor" }));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:modvision"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "GOLEMVISION", 50, new AspectList().add(Aspect.WATER, 1), new ItemStack(ItemsTC.modules, 1, 0), "B B", "E E", "PGP", 'B', new ItemStack(Items.GLASS_BOTTLE), 'E', new ItemStack(Items.FERMENTED_SPIDER_EYE), 'P', "plateBrass", 'G', new ItemStack(ItemsTC.mechanismSimple)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:modaggression"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "SEALGUARD", 50, new AspectList().add(Aspect.FIRE, 1), new ItemStack(ItemsTC.modules, 1, 1), " R ", "RTR", "PGP", 'R', "paneGlass", 'T', new ItemStack(Items.BLAZE_POWDER), 'P', "plateBrass", 'G', new ItemStack(ItemsTC.mechanismSimple)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:mirrorglass"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "BASEARTIFICE", 50, new AspectList().add(Aspect.WATER, 1).add(Aspect.ORDER, 1), new ItemStack(ItemsTC.mirroredGlass), new Object[] { new ItemStack(ItemsTC.quicksilver), "paneGlass" }));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:ArcaneSpa"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ARCANESPA", 50, new AspectList().add(Aspect.WATER, 1), new ItemStack(BlocksTC.spa), "QIQ", "SJS", "SPS", 'P', new ItemStack(ItemsTC.mechanismSimple), 'J', new ItemStack(BlocksTC.jarNormal), 'S', new ItemStack(BlocksTC.stoneArcane), 'Q', new ItemStack(Blocks.QUARTZ_BLOCK), 'I', new ItemStack(Blocks.IRON_BARS)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Tube"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 10, null, new ItemStack(BlocksTC.tube, 8, 0), " Q ", "IGI", " B ", 'I', "plateIron", 'B', "nuggetBrass", 'G', "blockGlass", 'Q', "nuggetQuicksilver"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Resonator"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 50, null, new ItemStack(ItemsTC.resonator), "I I", "INI", " S ", 'I', "plateIron", 'N', Items.QUARTZ, 'S', "stickWood"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:TubeValve"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 10, null, new ItemStack(BlocksTC.tubeValve), new Object[] { new ItemStack(BlocksTC.tube), new ItemStack(Blocks.LEVER) }));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:TubeFilter"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 10, null, new ItemStack(BlocksTC.tubeFilter), new Object[] { new ItemStack(BlocksTC.tube, 1, 0), new ItemStack(ItemsTC.filter) }));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:TubeRestrict"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 10, new AspectList().add(Aspect.EARTH, 1), new ItemStack(BlocksTC.tubeRestrict), new Object[] { new ItemStack(BlocksTC.tube) }));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:TubeOneway"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 10, new AspectList().add(Aspect.WATER, 1), new ItemStack(BlocksTC.tubeOneway), new Object[] { new ItemStack(BlocksTC.tube) }));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:TubeBuffer"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 25, null, new ItemStack(BlocksTC.tubeBuffer), "PVP", "TWT", "PRP", 'T', new ItemStack(BlocksTC.tube), 'V', new ItemStack(BlocksTC.tubeValve), 'W', "plateIron", 'R', new ItemStack(BlocksTC.tubeRestrict), 'P', new ItemStack(ItemsTC.phial)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:WardedJar"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "WARDEDJARS", 5, null, new ItemStack(BlocksTC.jarNormal), "GWG", "G G", "GGG", 'W', "slabWood", 'G', "paneGlass"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:JarVoid"), new ShapedArcaneVoidJar(ConfigRecipes.defaultGroup, "WARDEDJARS", 50, new AspectList().add(Aspect.ENTROPY, 1), new ItemStack(BlocksTC.jarVoid), "J", 'J', new ItemStack(BlocksTC.jarNormal)));
        ResourceLocation bannerGroup = new ResourceLocation("thaumcraft", "banners");
        int a = 0;
        for (EnumDyeColor d : EnumDyeColor.values()) {
            ItemStack banner = new ItemStack(BlocksTC.banners.get(d));
            ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Banner" + d.getUnlocalizedName().toLowerCase()), new ShapedArcaneRecipe(bannerGroup, "BASEINFUSION", 10, null, banner, "WS", "WS", "WB", 'W', new ItemStack(Blocks.WOOL, 1, a), 'S', "stickWood", 'B', "slabWood"));
            ++a;
        }
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:PaveBarrier"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "PAVINGSTONES", 50, new AspectList().add(Aspect.FIRE, 1).add(Aspect.ORDER, 1), new ItemStack(BlocksTC.pavingStoneBarrier, 4), "SS", "SS", 'S', new ItemStack(BlocksTC.stoneArcaneBrick)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:PaveTravel"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "PAVINGSTONES", 50, new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1), new ItemStack(BlocksTC.pavingStoneTravel, 4), "SS", "SS", 'S', new ItemStack(BlocksTC.stoneArcaneBrick)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:ArcaneLamp"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ARCANELAMP", 50, new AspectList().add(Aspect.AIR, 1).add(Aspect.FIRE, 1), new ItemStack(BlocksTC.lampArcane), " I ", "IAI", " I ", 'A', new ItemStack(BlocksTC.amberBlock), 'I', "plateIron"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Levitator"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "LEVITATOR", 35, new AspectList().add(Aspect.AIR, 1), new ItemStack(BlocksTC.levitator), "WIW", "BNB", "WGW", 'I', "plateThaumium", 'N', "nitor", 'W', "plankWood", 'B', "plateIron", 'G', new ItemStack(ItemsTC.mechanismSimple)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:RedstoneRelay"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "REDSTONERELAY", 10, new AspectList().add(Aspect.ORDER, 1), new ItemStack(BlocksTC.redstoneRelay), "   ", "TGT", "SSS", 'T', new ItemStack(Blocks.REDSTONE_TORCH), 'G', new ItemStack(ItemsTC.mechanismSimple), 'S', new ItemStack(Blocks.STONE_SLAB)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:ArcaneEar"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ARCANEEAR", 15, new AspectList().add(Aspect.AIR, 1), new ItemStack(BlocksTC.arcaneEar), "P P", " G ", "WRW", 'W', "slabWood", 'R', Items.REDSTONE, 'G', new ItemStack(ItemsTC.mechanismSimple), 'P', "plateBrass"));
        shapelessOreDictRecipe("ArcaneEarToggle", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.arcaneEarToggle), new Object[] { new ItemStack(BlocksTC.arcaneEar), new ItemStack(Blocks.LEVER) });
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:InfusionMatrix"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSION@2", 150, new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.FIRE, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), new ItemStack(BlocksTC.infusionMatrix), "S S", " N ", "S S", 'S', new ItemStack(BlocksTC.stoneArcaneBrick), 'N', "nitor"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:MatrixMotion"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONBOOST", 500, new AspectList().add(Aspect.AIR, 1).add(Aspect.ORDER, 1), new ItemStack(BlocksTC.matrixSpeed), "SNS", "NGN", "SNS", 'S', new ItemStack(BlocksTC.stoneArcane), 'N', "nitor", 'G', new ItemStack(Blocks.DIAMOND_BLOCK)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:MatrixCost"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONBOOST", 500, new AspectList().add(Aspect.AIR, 1).add(Aspect.WATER, 1).add(Aspect.ENTROPY, 1), new ItemStack(BlocksTC.matrixCost), "SAS", "AGA", "SAS", 'S', new ItemStack(BlocksTC.stoneArcane), 'A', new ItemStack(ItemsTC.alumentum), 'G', new ItemStack(Blocks.DIAMOND_BLOCK)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:ArcanePedestal"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSION", 10, null, new ItemStack(BlocksTC.pedestalArcane), "SSS", " B ", "SSS", 'S', new ItemStack(BlocksTC.slabArcaneStone), 'B', new ItemStack(BlocksTC.stoneArcane)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:AncientPedestal"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONANCIENT", 150, null, new ItemStack(BlocksTC.pedestalAncient), "SSS", " B ", "SSS", 'S', new ItemStack(BlocksTC.slabAncient), 'B', new ItemStack(BlocksTC.stoneAncient)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:EldritchPedestal"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONELDRITCH", 150, null, new ItemStack(BlocksTC.pedestalEldritch), "SSS", " B ", "SSS", 'S', new ItemStack(BlocksTC.slabEldritch), 'B', new ItemStack(BlocksTC.stoneEldritchTile)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:FocusPouch"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "FOCUSPOUCH", 25, null, new ItemStack(ItemsTC.focusPouch), "LGL", "LBL", "LLL", 'B', new ItemStack(ItemsTC.baubles, 1, 2), 'L', "leather", 'G', Items.GOLD_INGOT));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:dioptra"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "DIOPTRA", 50, new AspectList().add(Aspect.AIR, 1).add(Aspect.WATER, 1), new ItemStack(BlocksTC.dioptra), "APA", "IGI", "AAA", 'A', new ItemStack(BlocksTC.stoneArcane), 'G', new ItemStack(ItemsTC.thaumometer), 'P', new ItemStack(ItemsTC.visResonator), 'I', "plateIron"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:HungryChest"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "HUNGRYCHEST", 15, new AspectList().add(Aspect.EARTH, 1).add(Aspect.WATER, 1), new ItemStack(BlocksTC.hungryChest), "WTW", "W W", "WWW", 'W', new ItemStack(BlocksTC.plankGreatwood), 'T', "trapdoorWood"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Filter"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BASEALCHEMY", 15, new AspectList().add(Aspect.WATER, 1), new ItemStack(ItemsTC.filter, 2, 0), "GWG", 'G', Items.GOLD_INGOT, 'W', new ItemStack(BlocksTC.plankSilverwood)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:MorphicResonator"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BASEALCHEMY", 50, new AspectList().add(Aspect.AIR, 1).add(Aspect.FIRE, 1), new ItemStack(ItemsTC.morphicResonator), " G ", "BSB", " G ", 'G', "paneGlass", 'B', "plateBrass", 'S', new ItemStack(ItemsTC.nuggets, 1, 10)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Alembic"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIASMELTER", 50, new AspectList().add(Aspect.WATER, 1), new ItemStack(BlocksTC.alembic), "WFW", "SBS", "WFW", 'W', new ItemStack(BlocksTC.plankGreatwood), 'B', Items.BUCKET, 'F', new ItemStack(ItemsTC.filter), 'S', "plateBrass"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:EssentiaSmelter"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIASMELTER@2", 50, new AspectList().add(Aspect.FIRE, 1), new ItemStack(BlocksTC.smelterBasic), "BCB", "SFS", "SSS", 'C', new ItemStack(BlocksTC.crucible), 'F', new ItemStack(Blocks.FURNACE), 'S', "cobblestone", 'B', "plateBrass"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:EssentiaSmelterThaumium"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIASMELTERTHAUMIUM", 250, new AspectList().add(Aspect.FIRE, 2), new ItemStack(BlocksTC.smelterThaumium), "BFB", "IGI", "III", 'F', new ItemStack(BlocksTC.smelterBasic), 'G', new ItemStack(BlocksTC.metalAlchemical), 'I', "plateThaumium", 'B', "plateBrass"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:EssentiaSmelterVoid"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIASMELTERVOID", 750, new AspectList().add(Aspect.FIRE, 3), new ItemStack(BlocksTC.smelterVoid), "BFB", "IGI", "III", 'F', new ItemStack(BlocksTC.smelterBasic), 'G', new ItemStack(BlocksTC.metalAlchemicalAdvanced), 'I', "plateVoid", 'B', "plateBrass"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:AlchemicalConstruct"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 75, new AspectList().add(Aspect.WATER, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), new ItemStack(BlocksTC.metalAlchemical, 2), "IVI", "TWT", "IVI", 'W', new ItemStack(BlocksTC.plankGreatwood), 'V', new ItemStack(BlocksTC.tubeValve), 'T', new ItemStack(BlocksTC.tube), 'I', "plateIron"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:AdvAlchemyConstruct"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIASMELTERVOID@1", 200, new AspectList().add(Aspect.EARTH, 1).add(Aspect.FIRE, 1), new ItemStack(BlocksTC.metalAlchemicalAdvanced), " A ", "VPV", " A ", 'A', new ItemStack(BlocksTC.metalAlchemical), 'V', "plateVoid", 'P', Ingredient.fromItem(ItemsTC.primordialPearl)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:PotionSprayer"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "POTIONSPRAYER", 75, new AspectList().add(Aspect.WATER, 1).add(Aspect.FIRE, 1), new ItemStack(BlocksTC.potionSprayer), "BDB", "IAI", "ICI", 'B', "plateBrass", 'I', "plateIron", 'A', new ItemStack(Items.BREWING_STAND), 'D', new ItemStack(Blocks.DISPENSER), 'C', new ItemStack(BlocksTC.metalAlchemical)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:SmelterAux"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "IMPROVEDSMELTING", 100, new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1), new ItemStack(BlocksTC.smelterAux), "WTW", "RGR", "IBI", 'W', new ItemStack(BlocksTC.plankGreatwood), 'B', new ItemStack(BlocksTC.bellows), 'R', "plateBrass", 'T', new ItemStack(BlocksTC.tubeFilter), 'I', "plateIron", 'G', new ItemStack(BlocksTC.metalAlchemical)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:SmelterVent"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "IMPROVEDSMELTING2", 150, new AspectList().add(Aspect.AIR, 1), new ItemStack(BlocksTC.smelterVent), "IBI", "MGF", "IBI", 'I', "plateIron", 'B', "plateBrass", 'F', new ItemStack(ItemsTC.filter), 'M', new ItemStack(ItemsTC.filter), 'G', new ItemStack(BlocksTC.metalAlchemical)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:EssentiaTransportIn"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIATRANSPORT", 100, new AspectList().add(Aspect.AIR, 1).add(Aspect.WATER, 1), new ItemStack(BlocksTC.essentiaTransportInput), "   ", "BQB", "IGI", 'I', "plateIron", 'B', "plateBrass", 'Q', new ItemStack(Blocks.DISPENSER), 'G', new ItemStack(BlocksTC.metalAlchemical)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:EssentiaTransportOut"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIATRANSPORT", 100, new AspectList().add(Aspect.AIR, 1).add(Aspect.WATER, 1), new ItemStack(BlocksTC.essentiaTransportOutput), "   ", "BQB", "IGI", 'I', "plateIron", 'B', "plateBrass", 'Q', new ItemStack(Blocks.HOPPER), 'G', new ItemStack(BlocksTC.metalAlchemical)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Bellows"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BELLOWS", 25, new AspectList().add(Aspect.AIR, 1), new ItemStack(BlocksTC.bellows), "WW ", "LLI", "WW ", 'W', "plankWood", 'I', "ingotIron", 'L', "leather"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Centrifuge"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "CENTRIFUGE", 100, new AspectList().add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), new ItemStack(BlocksTC.centrifuge), " T ", "RCP", " T ", 'T', new ItemStack(BlocksTC.tube), 'P', new ItemStack(ItemsTC.mechanismSimple), 'R', new ItemStack(ItemsTC.morphicResonator), 'C', new ItemStack(BlocksTC.metalAlchemical)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:MnemonicMatrix"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "THAUMATORIUM", 50, new AspectList().add(Aspect.EARTH, 1).add(Aspect.ORDER, 1), new ItemStack(BlocksTC.brainBox), "IAI", "ABA", "IAI", 'B', new ItemStack(ItemsTC.mind, 1, 0), 'A', "gemAmber", 'I', "plateIron"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:MindClockwork"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "MINDCLOCKWORK@2", 25, new AspectList().add(Aspect.FIRE, 1).add(Aspect.ORDER, 1), new ItemStack(ItemsTC.mind, 1, 0), " P ", "PGP", "BCB", 'G', new ItemStack(ItemsTC.mechanismSimple), 'B', "plateBrass", 'P', "paneGlass", 'C', new ItemStack(Items.COMPARATOR)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:AutomatedCrossbow"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BASICTURRET", 100, new AspectList().add(Aspect.AIR, 1), new ItemStack(ItemsTC.turretPlacer, 1, 0), "BGI", "WMW", "S S", 'G', new ItemStack(ItemsTC.mechanismSimple), 'I', "plateIron", 'S', "stickWood", 'M', new ItemStack(ItemsTC.mind), 'B', Ingredient.fromItem(Items.BOW), 'W', new ItemStack(BlocksTC.plankGreatwood)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:AdvancedCrossbow"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ADVANCEDTURRET", 150, new AspectList().add(Aspect.AIR, 2), new ItemStack(ItemsTC.turretPlacer, 1, 1), "PMP", "PTP", "   ", 'T', new ItemStack(ItemsTC.turretPlacer, 1, 0), 'P', "plateIron", 'M', new ItemStack(ItemsTC.mind, 1, 1)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:patterncrafter"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ARCANEPATTERNCRAFTER", 50, new AspectList().add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.ORDER, 1), new ItemStack(BlocksTC.patternCrafter), "VH ", "GCG", " W ", 'H', new ItemStack(Blocks.HOPPER), 'W', new ItemStack(BlocksTC.plankGreatwood), 'G', new ItemStack(ItemsTC.mechanismSimple), 'V', new ItemStack(ItemsTC.visResonator), 'C', "workbench"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:GrappleGunTip"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "GRAPPLEGUN", 25, new AspectList().add(Aspect.EARTH, 1), new ItemStack(ItemsTC.grappleGunTip), "BRB", "RHR", "BRB", 'B', "plateBrass", 'R', new ItemStack(ItemsTC.nuggets, 1, 10), 'H', new ItemStack(Blocks.TRIPWIRE_HOOK)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:GrappleGunSpool"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "GRAPPLEGUN", 25, new AspectList().add(Aspect.WATER, 1), new ItemStack(ItemsTC.grappleGunSpool), "SHS", "SGS", "SSS", 'G', new ItemStack(ItemsTC.mechanismSimple), 'S', "string", 'H', new ItemStack(Blocks.TRIPWIRE_HOOK)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:GrappleGun"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "GRAPPLEGUN", 75, new AspectList().add(Aspect.AIR, 1).add(Aspect.FIRE, 1), new ItemStack(ItemsTC.grappleGun), "  S", "TII", " BW", 'B', "plateBrass", 'I', "plateIron", 'T', new ItemStack(ItemsTC.grappleGunTip), 'W', "plankWood", 'S', new ItemStack(ItemsTC.grappleGunSpool)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:VisBattery"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "VISBATTERY", 50, new AspectList().add(Aspect.AIR, 2).add(Aspect.EARTH, 2).add(Aspect.WATER, 2).add(Aspect.FIRE, 2).add(Aspect.ORDER, 2).add(Aspect.ENTROPY, 2), new ItemStack(BlocksTC.visBattery), "SSS", "SRS", "SSS", 'R', new ItemStack(ItemsTC.visResonator), 'S', new ItemStack(BlocksTC.slabArcaneStone)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:VisGenerator"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "VISGENERATOR", 25, new AspectList().add(Aspect.FIRE, 1).add(Aspect.ORDER, 1), new ItemStack(BlocksTC.visGenerator), "WSW", "EPE", "WRW", 'R', new ItemStack(ItemsTC.visResonator), 'E', new ItemStack(ItemsTC.nuggets, 1, 10), 'S', "dustRedstone", 'P', new ItemStack(Blocks.PISTON), 'W', "plankWood"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Condenser"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "FLUXCLEANUP", 500, new AspectList().add(Aspect.AIR, 5).add(Aspect.WATER, 5).add(Aspect.ENTROPY, 5), new ItemStack(BlocksTC.condenser), "BCB", "WMW", "BTB", 'T', new ItemStack(BlocksTC.tube), 'C', new ItemStack(ItemsTC.morphicResonator), 'W', "plankWood", 'M', new ItemStack(ItemsTC.mechanismComplex), 'B', "plateBrass"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:CondenserLattice"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "FLUXCLEANUP", 100, new AspectList().add(Aspect.EARTH, 3).add(Aspect.AIR, 3), new ItemStack(BlocksTC.condenserlattice), "QTQ", "QFQ", "QTQ", 'T', "plateThaumium", 'F', new ItemStack(ItemsTC.filter), 'Q', "gemQuartz"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Stabilizer"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONSTABLE", 250, new AspectList().add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.ENTROPY, 1), new ItemStack(BlocksTC.stabilizer), "SRS", "BVB", "IMI", 'R', "blockRedstone", 'S', BlocksTC.slabArcaneStone, 'B', BlocksTC.stoneArcane, 'M', new ItemStack(ItemsTC.mechanismComplex), 'V', new ItemStack(ItemsTC.visResonator), 'I', new ItemStack(BlocksTC.inlay)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:RedstoneInlay"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONSTABLE", 25, new AspectList().add(Aspect.WATER, 1), new ItemStack(BlocksTC.inlay, 2), new Object[] { "dustRedstone", "ingotGold" }));
    }
    
    public static void initializeInfusionRecipes() {
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealHarvest"), new InfusionRecipe("SEALHARVEST", GolemHelper.getSealStack("thaumcraft:harvest"), 0, new AspectList().add(Aspect.PLANT, 10).add(Aspect.SENSES, 10).add(Aspect.MAN, 10), new ItemStack(ItemsTC.seals), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.PUMPKIN_SEEDS), new ItemStack(Items.MELON_SEEDS), new ItemStack(Items.BEETROOT_SEEDS), new ItemStack(Items.REEDS), new ItemStack(Blocks.CACTUS)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealButcher"), new InfusionRecipe("SEALBUTCHER", GolemHelper.getSealStack("thaumcraft:butcher"), 0, new AspectList().add(Aspect.BEAST, 10).add(Aspect.SENSES, 10).add(Aspect.MAN, 10), GolemHelper.getSealStack("thaumcraft:guard"), "leather", new ItemStack(Blocks.WOOL, 1, 32767), new ItemStack(Items.RABBIT_HIDE), new ItemStack(Items.PORKCHOP), new ItemStack(Items.MUTTON), new ItemStack(Items.BEEF)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealBreak"), new InfusionRecipe("SEALBREAK", GolemHelper.getSealStack("thaumcraft:breaker"), 1, new AspectList().add(Aspect.TOOL, 10).add(Aspect.ENTROPY, 10).add(Aspect.MAN, 10), new ItemStack(ItemsTC.seals), Ingredient.fromItem(Items.GOLDEN_AXE), Ingredient.fromItem(Items.GOLDEN_PICKAXE), Ingredient.fromItem(Items.GOLDEN_SHOVEL)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterAir"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalAir), 0, new AspectList().add(Aspect.AIR, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.AIR), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterFire"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalFire), 0, new AspectList().add(Aspect.FIRE, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.FIRE), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterWater"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalWater), 0, new AspectList().add(Aspect.WATER, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.WATER), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterEarth"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalEarth), 0, new AspectList().add(Aspect.EARTH, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.EARTH), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterOrder"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalOrder), 0, new AspectList().add(Aspect.ORDER, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.ORDER), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterEntropy"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalEntropy), 0, new AspectList().add(Aspect.ENTROPY, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.ENTROPY), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterFlux"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalTaint), 4, new AspectList().add(Aspect.FLUX, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.FLUX), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:focus_2"), new InfusionRecipe("FOCUSADVANCED@1", new ItemStack(ItemsTC.focus2), 3, new AspectList().add(Aspect.MAGIC, 25).add(Aspect.ORDER, 50), new ItemStack(ItemsTC.focus1), new ItemStack(ItemsTC.quicksilver), "gemDiamond", new ItemStack(ItemsTC.quicksilver), new ItemStack(Items.ENDER_PEARL)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:focus_3"), new InfusionRecipe("FOCUSGREATER@1", new ItemStack(ItemsTC.focus3), 5, new AspectList().add(Aspect.MAGIC, 25).add(Aspect.ORDER, 50).add(Aspect.VOID, 100), new ItemStack(ItemsTC.focus2), new ItemStack(ItemsTC.quicksilver), Ingredient.fromItem(ItemsTC.primordialPearl), new ItemStack(ItemsTC.quicksilver), new ItemStack(Items.NETHER_STAR)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:JarBrain"), new InfusionRecipe("JARBRAIN", new ItemStack(BlocksTC.jarBrain), 4, new AspectList().add(Aspect.MIND, 25).add(Aspect.SENSES, 25).add(Aspect.UNDEAD, 25), new ItemStack(BlocksTC.jarNormal), new ItemStack(ItemsTC.brain), new ItemStack(Items.SPIDER_EYE), new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.SPIDER_EYE)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VisAmulet"), new InfusionRecipe("VISAMULET", new ItemStack(ItemsTC.amuletVis, 1, 1), 6, new AspectList().add(Aspect.AURA, 50).add(Aspect.ENERGY, 100).add(Aspect.VOID, 50), new ItemStack(ItemsTC.baubles, 1, 0), new ItemStack(ItemsTC.visResonator), ThaumcraftApiHelper.makeCrystal(Aspect.AIR), ThaumcraftApiHelper.makeCrystal(Aspect.FIRE), ThaumcraftApiHelper.makeCrystal(Aspect.WATER), ThaumcraftApiHelper.makeCrystal(Aspect.EARTH), ThaumcraftApiHelper.makeCrystal(Aspect.ORDER)));
        InfusionRunicAugmentRecipe ra = new InfusionRunicAugmentRecipe();
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:RunicArmor"), ra);
        for (int a = 0; a < 3; ++a) {
            ItemStack in = new ItemStack(ItemsTC.baubles, 1, 1);
            if (a > 0) {
                in.setTagInfo("TC.RUNIC", new NBTTagByte((byte)a));
            }
            ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:RunicArmorFake" + a), new InfusionRunicAugmentRecipe(in));
        }
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:Mirror"), new InfusionRecipe("MIRROR", new ItemStack(BlocksTC.mirror), 1, new AspectList().add(Aspect.MOTION, 25).add(Aspect.DARKNESS, 25).add(Aspect.EXCHANGE, 25), new ItemStack(ItemsTC.mirroredGlass), "ingotGold", "ingotGold", "ingotGold", new ItemStack(Items.ENDER_PEARL)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MirrorHand"), new InfusionRecipe("MIRRORHAND", new ItemStack(ItemsTC.handMirror), 5, new AspectList().add(Aspect.TOOL, 50).add(Aspect.MOTION, 50), new ItemStack(BlocksTC.mirror), "stickWood", new ItemStack(Items.COMPASS), new ItemStack(Items.MAP)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MirrorEssentia"), new InfusionRecipe("MIRRORESSENTIA", new ItemStack(BlocksTC.mirrorEssentia), 2, new AspectList().add(Aspect.MOTION, 25).add(Aspect.WATER, 25).add(Aspect.EXCHANGE, 25), new ItemStack(ItemsTC.mirroredGlass), "ingotIron", "ingotIron", "ingotIron", new ItemStack(Items.ENDER_PEARL)));
        ItemStack isEA = new ItemStack(ItemsTC.elementalAxe);
        EnumInfusionEnchantment.addInfusionEnchantment(isEA, EnumInfusionEnchantment.COLLECTOR, 1);
        EnumInfusionEnchantment.addInfusionEnchantment(isEA, EnumInfusionEnchantment.BURROWING, 1);
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalAxe"), new InfusionRecipe("ELEMENTALTOOLS", isEA, 1, new AspectList().add(Aspect.WATER, 60).add(Aspect.PLANT, 30), new ItemStack(ItemsTC.thaumiumAxe, 1, 32767), ConfigItems.WATER_CRYSTAL, ConfigItems.WATER_CRYSTAL, new ItemStack(ItemsTC.nuggets, 1, 10), new ItemStack(BlocksTC.plankGreatwood)));
        ItemStack isEP = new ItemStack(ItemsTC.elementalPick);
        EnumInfusionEnchantment.addInfusionEnchantment(isEP, EnumInfusionEnchantment.REFINING, 1);
        EnumInfusionEnchantment.addInfusionEnchantment(isEP, EnumInfusionEnchantment.SOUNDING, 2);
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalPick"), new InfusionRecipe("ELEMENTALTOOLS", isEP, 1, new AspectList().add(Aspect.FIRE, 30).add(Aspect.METAL, 30).add(Aspect.SENSES, 30), new ItemStack(ItemsTC.thaumiumPick, 1, 32767), ConfigItems.FIRE_CRYSTAL, ConfigItems.FIRE_CRYSTAL, new ItemStack(ItemsTC.nuggets, 1, 10), new ItemStack(BlocksTC.plankGreatwood)));
        ItemStack isESW = new ItemStack(ItemsTC.elementalSword);
        EnumInfusionEnchantment.addInfusionEnchantment(isESW, EnumInfusionEnchantment.ARCING, 2);
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalSword"), new InfusionRecipe("ELEMENTALTOOLS", isESW, 1, new AspectList().add(Aspect.AIR, 30).add(Aspect.MOTION, 30).add(Aspect.AVERSION, 30), new ItemStack(ItemsTC.thaumiumSword, 1, 32767), ConfigItems.AIR_CRYSTAL, ConfigItems.AIR_CRYSTAL, new ItemStack(ItemsTC.nuggets, 1, 10), new ItemStack(BlocksTC.plankGreatwood)));
        ItemStack isES = new ItemStack(ItemsTC.elementalShovel);
        EnumInfusionEnchantment.addInfusionEnchantment(isES, EnumInfusionEnchantment.DESTRUCTIVE, 1);
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalShovel"), new InfusionRecipe("ELEMENTALTOOLS", isES, 1, new AspectList().add(Aspect.EARTH, 60).add(Aspect.CRAFT, 30), new ItemStack(ItemsTC.thaumiumShovel, 1, 32767), ConfigItems.EARTH_CRYSTAL, ConfigItems.EARTH_CRYSTAL, new ItemStack(ItemsTC.nuggets, 1, 10), new ItemStack(BlocksTC.plankGreatwood)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalHoe"), new InfusionRecipe("ELEMENTALTOOLS", new ItemStack(ItemsTC.elementalHoe), 1, new AspectList().add(Aspect.ORDER, 30).add(Aspect.PLANT, 30).add(Aspect.ENTROPY, 30), new ItemStack(ItemsTC.thaumiumHoe, 1, 32767), ConfigItems.ORDER_CRYSTAL, ConfigItems.ENTROPY_CRYSTAL, new ItemStack(ItemsTC.nuggets, 1, 10), new ItemStack(BlocksTC.plankGreatwood)));
        InfusionEnchantmentRecipe IEBURROWING = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.BURROWING, new AspectList().add(Aspect.SENSES, 80).add(Aspect.EARTH, 150), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(Items.RABBIT_FOOT));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IEBURROWING"), IEBURROWING);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:IEBURROWINGFAKE"), new InfusionEnchantmentRecipe(IEBURROWING, new ItemStack(Items.WOODEN_PICKAXE)));
        InfusionEnchantmentRecipe IECOLLECTOR = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.COLLECTOR, new AspectList().add(Aspect.DESIRE, 80).add(Aspect.WATER, 100), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(Items.LEAD));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IECOLLECTOR"), IECOLLECTOR);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:IECOLLECTORFAKE"), new InfusionEnchantmentRecipe(IECOLLECTOR, new ItemStack(Items.STONE_AXE)));
        InfusionEnchantmentRecipe IEDESTRUCTIVE = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.DESTRUCTIVE, new AspectList().add(Aspect.AVERSION, 200).add(Aspect.ENTROPY, 250), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(Blocks.TNT));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IEDESTRUCTIVE"), IEDESTRUCTIVE);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:IEDESTRUCTIVEFAKE"), new InfusionEnchantmentRecipe(IEDESTRUCTIVE, new ItemStack(Items.STONE_PICKAXE)));
        InfusionEnchantmentRecipe IEREFINING = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.REFINING, new AspectList().add(Aspect.ORDER, 80).add(Aspect.EXCHANGE, 60), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(ItemsTC.salisMundus));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IEREFINING"), IEREFINING);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:IEREFININGFAKE"), new InfusionEnchantmentRecipe(IEREFINING, new ItemStack(Items.IRON_PICKAXE)));
        InfusionEnchantmentRecipe IESOUNDING = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.SOUNDING, new AspectList().add(Aspect.SENSES, 40).add(Aspect.FIRE, 60), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(Items.MAP));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IESOUNDING"), IESOUNDING);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:IESOUNDINGFAKE"), new InfusionEnchantmentRecipe(IESOUNDING, new ItemStack(Items.GOLDEN_PICKAXE)));
        InfusionEnchantmentRecipe IEARCING = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.ARCING, new AspectList().add(Aspect.ENERGY, 40).add(Aspect.AIR, 60), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(Blocks.REDSTONE_BLOCK));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IEARCING"), IEARCING);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:IEARCINGFAKE"), new InfusionEnchantmentRecipe(IEARCING, new ItemStack(Items.WOODEN_SWORD)));
        InfusionEnchantmentRecipe IEESSENCE = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.ESSENCE, new AspectList().add(Aspect.BEAST, 40).add(Aspect.FLUX, 60), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(ItemsTC.crystalEssence));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IEESSENCE"), IEESSENCE);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:IEESSENCEFAKE"), new InfusionEnchantmentRecipe(IEESSENCE, new ItemStack(Items.STONE_SWORD)));
        InfusionEnchantmentRecipe IELAMPLIGHT = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.LAMPLIGHT, new AspectList().add(Aspect.LIGHT, 80).add(Aspect.AIR, 20), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), "nitor");
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IELAMPLIGHT"), IELAMPLIGHT);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:IELAMPLIGHTFAKE"), new InfusionEnchantmentRecipe(IELAMPLIGHT, new ItemStack(Items.GOLDEN_PICKAXE)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:BootsTraveller"), new InfusionRecipe("BOOTSTRAVELLER", new ItemStack(ItemsTC.travellerBoots), 1, new AspectList().add(Aspect.FLIGHT, 100).add(Aspect.MOTION, 100), new ItemStack(Items.LEATHER_BOOTS, 1, 32767), ConfigItems.AIR_CRYSTAL, ConfigItems.AIR_CRYSTAL, new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric), new ItemStack(Items.FEATHER), new ItemStack(Items.FISH, 1, 32767)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MindBiothaumic"), new InfusionRecipe("MINDBIOTHAUMIC", new ItemStack(ItemsTC.mind, 1, 1), 4, new AspectList().add(Aspect.MIND, 50).add(Aspect.MECHANISM, 25), new ItemStack(ItemsTC.mind, 1, 0), new ItemStack(ItemsTC.brain), new ItemStack(ItemsTC.mechanismComplex)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ArcaneBore"), new InfusionRecipe("ARCANEBORE", new ItemStack(ItemsTC.turretPlacer, 1, 2), 4, new AspectList().add(Aspect.ENERGY, 25).add(Aspect.EARTH, 25).add(Aspect.MECHANISM, 100).add(Aspect.VOID, 25).add(Aspect.MOTION, 25), new ItemStack(ItemsTC.turretPlacer), new ItemStack(BlocksTC.plankGreatwood), new ItemStack(BlocksTC.plankGreatwood), new ItemStack(ItemsTC.mechanismComplex), "plateBrass", Ingredient.fromItem(Items.DIAMOND_PICKAXE), Ingredient.fromItem(Items.DIAMOND_SHOVEL), new ItemStack(ItemsTC.morphicResonator), new ItemStack(ItemsTC.nuggets, 1, 10)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:LampGrowth"), new InfusionRecipe("LAMPGROWTH", new ItemStack(BlocksTC.lampGrowth), 4, new AspectList().add(Aspect.PLANT, 20).add(Aspect.LIGHT, 15).add(Aspect.LIFE, 15).add(Aspect.TOOL, 15), new ItemStack(BlocksTC.lampArcane), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.DYE, 1, 15), ConfigItems.EARTH_CRYSTAL, new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.DYE, 1, 15), ConfigItems.EARTH_CRYSTAL));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:LampFertility"), new InfusionRecipe("LAMPFERTILITY", new ItemStack(BlocksTC.lampFertility), 4, new AspectList().add(Aspect.BEAST, 20).add(Aspect.LIGHT, 15).add(Aspect.LIFE, 15).add(Aspect.DESIRE, 15), new ItemStack(BlocksTC.lampArcane), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.WHEAT), ConfigItems.FIRE_CRYSTAL, new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.CARROT), ConfigItems.FIRE_CRYSTAL));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ThaumiumFortressHelm"), new InfusionRecipe("ARMORFORTRESS", new ItemStack(ItemsTC.fortressHelm), 3, new AspectList().add(Aspect.METAL, 50).add(Aspect.PROTECT, 20).add(Aspect.ENERGY, 25), new ItemStack(ItemsTC.thaumiumHelm, 1, 32767), "plateThaumium", "plateThaumium", new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.EMERALD)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ThaumiumFortressChest"), new InfusionRecipe("ARMORFORTRESS", new ItemStack(ItemsTC.fortressChest), 3, new AspectList().add(Aspect.METAL, 50).add(Aspect.PROTECT, 30).add(Aspect.ENERGY, 25), new ItemStack(ItemsTC.thaumiumChest, 1, 32767), "plateThaumium", "plateThaumium", "plateThaumium", "plateThaumium", new ItemStack(Items.GOLD_INGOT), "leather"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ThaumiumFortressLegs"), new InfusionRecipe("ARMORFORTRESS", new ItemStack(ItemsTC.fortressLegs), 3, new AspectList().add(Aspect.METAL, 50).add(Aspect.PROTECT, 25).add(Aspect.ENERGY, 25), new ItemStack(ItemsTC.thaumiumLegs, 1, 32767), "plateThaumium", "plateThaumium", "plateThaumium", new ItemStack(Items.GOLD_INGOT), "leather"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidRobeHelm"), new InfusionRecipe("VOIDROBEARMOR", new ItemStack(ItemsTC.voidRobeHelm), 6, new AspectList().add(Aspect.METAL, 25).add(Aspect.SENSES, 25).add(Aspect.PROTECT, 25).add(Aspect.ENERGY, 25).add(Aspect.ELDRITCH, 25).add(Aspect.VOID, 25), new ItemStack(ItemsTC.voidHelm), new ItemStack(ItemsTC.goggles, 1, 32767), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidRobeChest"), new InfusionRecipe("VOIDROBEARMOR", new ItemStack(ItemsTC.voidRobeChest), 6, new AspectList().add(Aspect.METAL, 35).add(Aspect.PROTECT, 35).add(Aspect.ENERGY, 25).add(Aspect.ELDRITCH, 25).add(Aspect.VOID, 35), new ItemStack(ItemsTC.voidChest), new ItemStack(ItemsTC.clothChest), "plateVoid", "plateVoid", new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.fabric), "leather"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidRobeLegs"), new InfusionRecipe("VOIDROBEARMOR", new ItemStack(ItemsTC.voidRobeLegs), 6, new AspectList().add(Aspect.METAL, 30).add(Aspect.PROTECT, 30).add(Aspect.ENERGY, 25).add(Aspect.ELDRITCH, 25).add(Aspect.VOID, 30), new ItemStack(ItemsTC.voidLegs), new ItemStack(ItemsTC.clothLegs), "plateVoid", "plateVoid", new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.fabric), "leather"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:HelmGoggles"), new InfusionRecipe("FORTRESSMASK", new Object[] { "goggles", new NBTTagByte((byte)1) }, 5, new AspectList().add(Aspect.SENSES, 40).add(Aspect.AURA, 20).add(Aspect.PROTECT, 20), new ItemStack(ItemsTC.fortressHelm, 1, 32767), new ItemStack(Items.SLIME_BALL), new ItemStack(ItemsTC.goggles, 1, 32767)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MaskGrinningDevil"), new InfusionRecipe("FORTRESSMASK", new Object[] { "mask", new NBTTagInt(0) }, 8, new AspectList().add(Aspect.MIND, 80).add(Aspect.LIFE, 80).add(Aspect.PROTECT, 20), new ItemStack(ItemsTC.fortressHelm, 1, 32767), new ItemStack(Items.DYE, 1, 0), "plateIron", "leather", new ItemStack(BlocksTC.shimmerleaf), new ItemStack(ItemsTC.brain), "plateIron"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MaskAngryGhost"), new InfusionRecipe("FORTRESSMASK", new Object[] { "mask", new NBTTagInt(1) }, 8, new AspectList().add(Aspect.ENTROPY, 80).add(Aspect.DEATH, 80).add(Aspect.PROTECT, 20), new ItemStack(ItemsTC.fortressHelm, 1, 32767), new ItemStack(Items.DYE, 1, 15), "plateIron", "leather", new ItemStack(Items.POISONOUS_POTATO), new ItemStack(Items.SKULL, 1, 1), "plateIron"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MaskSippingFiend"), new InfusionRecipe("FORTRESSMASK", new Object[] { "mask", new NBTTagInt(2) }, 8, new AspectList().add(Aspect.UNDEAD, 80).add(Aspect.LIFE, 80).add(Aspect.PROTECT, 20), new ItemStack(ItemsTC.fortressHelm, 1, 32767), new ItemStack(Items.DYE, 1, 1), "plateIron", "leather", new ItemStack(Items.GHAST_TEAR), new ItemStack(Items.MILK_BUCKET), "plateIron"));
        ItemStack isPC = new ItemStack(ItemsTC.primalCrusher);
        EnumInfusionEnchantment.addInfusionEnchantment(isPC, EnumInfusionEnchantment.DESTRUCTIVE, 1);
        EnumInfusionEnchantment.addInfusionEnchantment(isPC, EnumInfusionEnchantment.REFINING, 1);
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:PrimalCrusher"), new InfusionRecipe("PRIMALCRUSHER", isPC, 6, new AspectList().add(Aspect.EARTH, 75).add(Aspect.TOOL, 75).add(Aspect.ENTROPY, 50).add(Aspect.VOID, 50).add(Aspect.AVERSION, 50).add(Aspect.ELDRITCH, 50).add(Aspect.DESIRE, 50), Ingredient.fromItem(ItemsTC.primordialPearl), Ingredient.fromItem(ItemsTC.voidPick), Ingredient.fromItem(ItemsTC.voidShovel), Ingredient.fromItem(ItemsTC.elementalPick), Ingredient.fromItem(ItemsTC.elementalShovel)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VerdantHeart"), new InfusionRecipe("VERDANTCHARMS", new ItemStack(ItemsTC.charmVerdant), 5, new AspectList().add(Aspect.LIFE, 60).add(Aspect.ORDER, 30).add(Aspect.PLANT, 60), new ItemStack(ItemsTC.baubles, 1, 4), new ItemStack(ItemsTC.nuggets, 1, 10), ThaumcraftApiHelper.makeCrystal(Aspect.LIFE), new ItemStack(Items.MILK_BUCKET), ThaumcraftApiHelper.makeCrystal(Aspect.PLANT)));
        ItemStack pis1 = new ItemStack(Items.POTIONITEM);
        pis1.setTagInfo("Potion", new NBTTagString("minecraft:strong_healing"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VerdantHeartLife"), new InfusionRecipe("VERDANTCHARMS", new Object[] { "type", new NBTTagByte((byte)1) }, 5, new AspectList().add(Aspect.LIFE, 80).add(Aspect.MAN, 80), new ItemStack(ItemsTC.charmVerdant), new ItemStack(Items.GOLDEN_APPLE), ThaumcraftApiHelper.makeCrystal(Aspect.LIFE), pis1, ThaumcraftApiHelper.makeCrystal(Aspect.MAN)));
        ItemStack pis2 = new ItemStack(Items.POTIONITEM);
        pis2.setTagInfo("Potion", new NBTTagString("minecraft:strong_regeneration"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VerdantHeartSustain"), new InfusionRecipe("VERDANTCHARMS", new Object[] { "type", new NBTTagByte((byte)2) }, 5, new AspectList().add(Aspect.DESIRE, 80).add(Aspect.AIR, 80), new ItemStack(ItemsTC.charmVerdant), new ItemStack(ItemsTC.tripleMeatTreat), ThaumcraftApiHelper.makeCrystal(Aspect.DESIRE), pis2, ThaumcraftApiHelper.makeCrystal(Aspect.AIR)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CLOUDRING"), new InfusionRecipe("CLOUDRING", new ItemStack(ItemsTC.ringCloud), 1, new AspectList().add(Aspect.AIR, 50), new ItemStack(ItemsTC.baubles, 1, 1), ConfigItems.AIR_CRYSTAL, new ItemStack(Items.FEATHER)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CuriosityBand"), new InfusionRecipe("CURIOSITYBAND", new ItemStack(ItemsTC.bandCuriosity), 5, new AspectList().add(Aspect.MIND, 150).add(Aspect.VOID, 50).add(Aspect.TRAP, 100), new ItemStack(ItemsTC.baubles, 1, 6), new ItemStack(Items.EMERALD), new ItemStack(Items.WRITABLE_BOOK), new ItemStack(Items.EMERALD), new ItemStack(Items.WRITABLE_BOOK), new ItemStack(Items.EMERALD), new ItemStack(Items.WRITABLE_BOOK), new ItemStack(Items.EMERALD), new ItemStack(Items.WRITABLE_BOOK)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CHARMUNDYING"), new InfusionRecipe("CHARMUNDYING", new ItemStack(ItemsTC.charmUndying), 2, new AspectList().add(Aspect.LIFE, 25), new ItemStack(Items.TOTEM_OF_UNDYING), "plateBrass"));
        int a2 = 0;
        ItemStack[] nitorStacks = new ItemStack[16];
        for (EnumDyeColor d : EnumDyeColor.values()) {
            nitorStacks[a2] = new ItemStack(BlocksTC.nitor.get(d));
            ++a2;
        }
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CausalityCollapser"), new InfusionRecipe("RIFTCLOSER", new ItemStack(ItemsTC.causalityCollapser), 8, new AspectList().add(Aspect.ELDRITCH, 50).add(Aspect.FLUX, 50), new ItemStack(Blocks.TNT), new ItemStack(ItemsTC.morphicResonator), new ItemStack(Blocks.REDSTONE_BLOCK), new ItemStack(ItemsTC.alumentum), Ingredient.fromStacks(nitorStacks), new ItemStack(ItemsTC.visResonator), new ItemStack(Blocks.REDSTONE_BLOCK), new ItemStack(ItemsTC.alumentum), Ingredient.fromStacks(nitorStacks)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidSiphon"), new InfusionRecipe("VOIDSIPHON", new ItemStack(BlocksTC.voidSiphon), 7, new AspectList().add(Aspect.ELDRITCH, 50).add(Aspect.ENTROPY, 50).add(Aspect.VOID, 100).add(Aspect.CRAFT, 50), new ItemStack(BlocksTC.metalBlockVoid), new ItemStack(BlocksTC.stoneArcane), new ItemStack(BlocksTC.stoneArcane), new ItemStack(ItemsTC.mechanismComplex), "plateBrass", "plateBrass", new ItemStack(Items.NETHER_STAR)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidseerPearl"), new InfusionRecipe("VOIDSEERPEARL", new ItemStack(ItemsTC.charmVoidseer), 8, new AspectList().add(Aspect.MIND, 150).add(Aspect.VOID, 150).add(Aspect.MAGIC, 100), new ItemStack(ItemsTC.baubles, 1, 4), new ItemStack(ItemsTC.brain), new ItemStack(ItemsTC.voidSeed), new ItemStack(ItemsTC.brain), Ingredient.fromItem(ItemsTC.primordialPearl)));
    }
    
    public static void initializeNormalRecipes(IForgeRegistry<IRecipe> iForgeRegistry) {
        ResourceLocation brassGroup = new ResourceLocation("thaumcraft", "brass_stuff");
        ResourceLocation thaumiumGroup = new ResourceLocation("thaumcraft", "thaumium_stuff");
        ResourceLocation voidGroup = new ResourceLocation("thaumcraft", "void_stuff");
        ResourceLocation baublesGroup = new ResourceLocation("thaumcraft", "baubles_stuff");
        iForgeRegistry.register(new RecipesRobeArmorDyes().setRegistryName("robedye"));
        iForgeRegistry.register(new RecipesVoidRobeArmorDyes().setRegistryName("voidarmordye"));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "ironnuggetconvert"), ConfigRecipes.defaultGroup, new ItemStack(Items.IRON_NUGGET), "#", '#', new ItemStack(ItemsTC.nuggets, 1, 0));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "thaumiumtonuggets"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 6), "#", '#', new ItemStack(ItemsTC.ingots, 1, 0));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "voidtonuggets"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 7), "#", '#', new ItemStack(ItemsTC.ingots, 1, 1));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "brasstonuggets"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 8), "#", '#', new ItemStack(ItemsTC.ingots, 1, 2));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "quartztonuggets"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 9), "#", '#', new ItemStack(Items.QUARTZ));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "quicksilvertonuggets"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 5), "#", '#', new ItemStack(ItemsTC.quicksilver));
        oreDictRecipe("nuggetstothaumium", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.ingots, 1, 0), new Object[] { "###", "###", "###", '#', "nuggetThaumium" });
        oreDictRecipe("nuggetstovoid", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.ingots, 1, 1), new Object[] { "###", "###", "###", '#', "nuggetVoid" });
        oreDictRecipe("nuggetstobrass", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.ingots, 1, 2), new Object[] { "###", "###", "###", '#', "nuggetBrass" });
        oreDictRecipe("nuggetstoquicksilver", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.quicksilver), new Object[] { "###", "###", "###", '#', "nuggetQuicksilver" });
        oreDictRecipe("thaumiumingotstoblock", thaumiumGroup, new ItemStack(BlocksTC.metalBlockThaumium), new Object[] { "###", "###", "###", '#', new ItemStack(ItemsTC.ingots, 1, 0) });
        oreDictRecipe("thaumiumblocktoingots", thaumiumGroup, new ItemStack(ItemsTC.ingots, 9, 0), new Object[] { "#", '#', new ItemStack(BlocksTC.metalBlockThaumium) });
        oreDictRecipe("voidingotstoblock", voidGroup, new ItemStack(BlocksTC.metalBlockVoid), new Object[] { "###", "###", "###", '#', new ItemStack(ItemsTC.ingots, 1, 1) });
        oreDictRecipe("voidblocktoingots", voidGroup, new ItemStack(ItemsTC.ingots, 9, 1), new Object[] { "#", '#', new ItemStack(BlocksTC.metalBlockVoid) });
        oreDictRecipe("brassingotstoblock", brassGroup, new ItemStack(BlocksTC.metalBlockBrass), new Object[] { "###", "###", "###", '#', new ItemStack(ItemsTC.ingots, 1, 2) });
        oreDictRecipe("brassblocktoingots", brassGroup, new ItemStack(ItemsTC.ingots, 9, 2), new Object[] { "#", '#', new ItemStack(BlocksTC.metalBlockBrass) });
        oreDictRecipe("fleshtoblock", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.fleshBlock), new Object[] { "###", "###", "###", '#', Items.ROTTEN_FLESH });
        oreDictRecipe("blocktoflesh", ConfigRecipes.defaultGroup, new ItemStack(Items.ROTTEN_FLESH, 9, 0), new Object[] { "#", '#', BlocksTC.fleshBlock });
        oreDictRecipe("ambertoblock", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.amberBlock), new Object[] { "##", "##", '#', "gemAmber" });
        oreDictRecipe("amberblocktobrick", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.amberBrick, 4), new Object[] { "##", "##", '#', new ItemStack(BlocksTC.amberBlock) });
        oreDictRecipe("amberbricktoblock", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.amberBlock, 4), new Object[] { "##", "##", '#', new ItemStack(BlocksTC.amberBrick) });
        oreDictRecipe("amberblocktoamber", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.amber, 4), new Object[] { "#", '#', new ItemStack(BlocksTC.amberBlock) });
        oreDictRecipe("ironplate", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.plate, 3, 1), new Object[] { "BBB", 'B', "ingotIron" });
        oreDictRecipe("brassplate", brassGroup, new ItemStack(ItemsTC.plate, 3, 0), new Object[] { "BBB", 'B', "ingotBrass" });
        oreDictRecipe("thaumiumplate", thaumiumGroup, new ItemStack(ItemsTC.plate, 3, 2), new Object[] { "BBB", 'B', "ingotThaumium" });
        oreDictRecipe("thaumiumhelm", thaumiumGroup, new ItemStack(ItemsTC.thaumiumHelm, 1), new Object[] { "III", "I I", 'I', "ingotThaumium" });
        oreDictRecipe("thaumiumchest", thaumiumGroup, new ItemStack(ItemsTC.thaumiumChest, 1), new Object[] { "I I", "III", "III", 'I', "ingotThaumium" });
        oreDictRecipe("thaumiumlegs", thaumiumGroup, new ItemStack(ItemsTC.thaumiumLegs, 1), new Object[] { "III", "I I", "I I", 'I', "ingotThaumium" });
        oreDictRecipe("thaumiumboots", thaumiumGroup, new ItemStack(ItemsTC.thaumiumBoots, 1), new Object[] { "I I", "I I", 'I', "ingotThaumium" });
        oreDictRecipe("thaumiumshovel", thaumiumGroup, new ItemStack(ItemsTC.thaumiumShovel, 1), new Object[] { "I", "S", "S", 'I', "ingotThaumium", 'S', "stickWood" });
        oreDictRecipe("thaumiumpick", thaumiumGroup, new ItemStack(ItemsTC.thaumiumPick, 1), new Object[] { "III", " S ", " S ", 'I', "ingotThaumium", 'S', "stickWood" });
        oreDictRecipe("thaumiumaxe", thaumiumGroup, new ItemStack(ItemsTC.thaumiumAxe, 1), new Object[] { "II", "SI", "S ", 'I', "ingotThaumium", 'S', "stickWood" });
        oreDictRecipe("thaumiumhoe", thaumiumGroup, new ItemStack(ItemsTC.thaumiumHoe, 1), new Object[] { "II", "S ", "S ", 'I', "ingotThaumium", 'S', "stickWood" });
        oreDictRecipe("thaumiumsword", thaumiumGroup, new ItemStack(ItemsTC.thaumiumSword, 1), new Object[] { "I", "I", "S", 'I', "ingotThaumium", 'S', "stickWood" });
        oreDictRecipe("voidplate", voidGroup, new ItemStack(ItemsTC.plate, 3, 3), new Object[] { "BBB", 'B', "ingotVoid" });
        oreDictRecipe("voidhelm", voidGroup, new ItemStack(ItemsTC.voidHelm, 1), new Object[] { "III", "I I", 'I', "ingotVoid" });
        oreDictRecipe("voidchest", voidGroup, new ItemStack(ItemsTC.voidChest, 1), new Object[] { "I I", "III", "III", 'I', "ingotVoid" });
        oreDictRecipe("voidlegs", voidGroup, new ItemStack(ItemsTC.voidLegs, 1), new Object[] { "III", "I I", "I I", 'I', "ingotVoid" });
        oreDictRecipe("voidboots", voidGroup, new ItemStack(ItemsTC.voidBoots, 1), new Object[] { "I I", "I I", 'I', "ingotVoid" });
        oreDictRecipe("voidshovel", voidGroup, new ItemStack(ItemsTC.voidShovel, 1), new Object[] { "I", "S", "S", 'I', "ingotVoid", 'S', "stickWood" });
        oreDictRecipe("voidpick", voidGroup, new ItemStack(ItemsTC.voidPick, 1), new Object[] { "III", " S ", " S ", 'I', "ingotVoid", 'S', "stickWood" });
        oreDictRecipe("voidaxe", voidGroup, new ItemStack(ItemsTC.voidAxe, 1), new Object[] { "II", "SI", "S ", 'I', "ingotVoid", 'S', "stickWood" });
        oreDictRecipe("voidhoe", voidGroup, new ItemStack(ItemsTC.voidHoe, 1), new Object[] { "II", "S ", "S ", 'I', "ingotVoid", 'S', "stickWood" });
        oreDictRecipe("voidsword", voidGroup, new ItemStack(ItemsTC.voidSword, 1), new Object[] { "I", "I", "S", 'I', "ingotVoid", 'S', "stickWood" });
        oreDictRecipe("babuleamulet", baublesGroup, new ItemStack(ItemsTC.baubles, 1, 0), new Object[] { " S ", "S S", " I ", 'S', "string", 'I', "ingotBrass" });
        oreDictRecipe("babulering", baublesGroup, new ItemStack(ItemsTC.baubles, 1, 1), new Object[] { "NNN", "N N", "NNN", 'N', "nuggetBrass" });
        oreDictRecipe("babulegirdle", baublesGroup, new ItemStack(ItemsTC.baubles, 1, 2), new Object[] { " L ", "L L", " I ", 'L', "leather", 'I', "ingotBrass" });
        oreDictRecipe("babuleamuletfancy", baublesGroup, new ItemStack(ItemsTC.baubles, 1, 4), new Object[] { " S ", "SGS", " I ", 'S', "string", 'G', "gemDiamond", 'I', "ingotGold" });
        oreDictRecipe("babuleringfancy", baublesGroup, new ItemStack(ItemsTC.baubles, 1, 5), new Object[] { "NGN", "N N", "NNN", 'G', "gemDiamond", 'N', "nuggetGold" });
        oreDictRecipe("babulegirdlefancy", baublesGroup, new ItemStack(ItemsTC.baubles, 1, 6), new Object[] { " L ", "LGL", " I ", 'L', "leather", 'G', "gemDiamond", 'I', "ingotGold" });
        iForgeRegistry.register(new RecipeTripleMeatTreat().setRegistryName("triplemeattreat"));
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:triplemeattreatfake"), new ShapelessOreRecipe(ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.tripleMeatTreat), "nuggetMeat", "nuggetMeat", "nuggetMeat", new ItemStack(Items.SUGAR)));
        iForgeRegistry.register(new RecipeMagicDust().setRegistryName("salismundus"));
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:salismundusfake"), new ShapelessOreRecipe(ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.salisMundus), Items.FLINT, Items.BOWL, Items.REDSTONE, new ItemStack(ItemsTC.crystalEssence, 1, 32767), new ItemStack(ItemsTC.crystalEssence, 1, 32767), new ItemStack(ItemsTC.crystalEssence, 1, 32767)));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "shimmerleaftoquicksilver"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.quicksilver), "#", '#', BlocksTC.shimmerleaf);
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "cinderpearltoblazepowder"), ConfigRecipes.defaultGroup, new ItemStack(Items.BLAZE_POWDER), "#", '#', BlocksTC.cinderpearl);
        ResourceLocation labelsGroup = new ResourceLocation("thaumcraft", "jarlabels");
        shapelessOreDictRecipe("JarLabel", labelsGroup, new ItemStack(ItemsTC.label, 4, 0), new Object[] { "dyeBlack", "slimeball", Items.PAPER, Items.PAPER, Items.PAPER, Items.PAPER });
        int count = 0;
        for (Aspect aspect : Aspect.aspects.values()) {
            ItemStack output = new ItemStack(ItemsTC.label, 1, 1);
            ((IEssentiaContainerItem)output.getItem()).setAspects(output, new AspectList().add(aspect, 1));
            shapelessOreDictRecipe("label_" + aspect.getTag(), labelsGroup, output, new Object[] { new ItemStack(ItemsTC.label), new IngredientNBTTC(ItemPhial.makeFilledPhial(aspect)) });
        }
        shapelessOreDictRecipe("JarLabelNull", labelsGroup, new ItemStack(ItemsTC.label), new Object[] { new ItemStack(ItemsTC.label, 1, 1) });
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "PlankGreatwood"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.plankGreatwood, 4), "W", 'W', new ItemStack(BlocksTC.logGreatwood));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "PlankSilverwood"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.plankSilverwood, 4), "W", 'W', new ItemStack(BlocksTC.logSilverwood));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "StairsGreatwood"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stairsGreatwood, 4, 0), "K  ", "KK ", "KKK", 'K', new ItemStack(BlocksTC.plankGreatwood));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "StairsSilverwood"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stairsSilverwood, 4, 0), "K  ", "KK ", "KKK", 'K', new ItemStack(BlocksTC.plankSilverwood));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "StairsArcane"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stairsArcane, 4, 0), "K  ", "KK ", "KKK", 'K', new ItemStack(BlocksTC.stoneArcane));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "StairsArcaneBrick"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stairsArcaneBrick, 4, 0), "K  ", "KK ", "KKK", 'K', new ItemStack(BlocksTC.stoneArcaneBrick));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "StairsAncient"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stairsAncient, 4, 0), "K  ", "KK ", "KKK", 'K', new ItemStack(BlocksTC.stoneAncient));
        oreDictRecipe("StoneArcane", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stoneArcane, 9), new Object[] { "KKK", "KCK", "KKK", 'K', "stone", 'C', new ItemStack(ItemsTC.crystalEssence) });
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "BrickArcane"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stoneArcaneBrick, 4), "KK", "KK", 'K', new ItemStack(BlocksTC.stoneArcane));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "SlabGreatwood"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.slabGreatwood, 6), "KKK", 'K', new ItemStack(BlocksTC.plankGreatwood));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "SlabSilverwood"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.slabSilverwood, 6), "KKK", 'K', new ItemStack(BlocksTC.plankSilverwood));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "SlabArcaneStone"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.slabArcaneStone, 6), "KKK", 'K', new ItemStack(BlocksTC.stoneArcane));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "SlabArcaneBrick"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.slabArcaneBrick, 6), "KKK", 'K', new ItemStack(BlocksTC.stoneArcaneBrick));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "SlabAncient"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.slabAncient, 6), "KKK", 'K', new ItemStack(BlocksTC.stoneAncient));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "SlabEldritch"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.slabEldritch, 6), "KKK", 'K', new ItemStack(BlocksTC.stoneEldritchTile));
        oreDictRecipe("phial", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.phial, 8, 0), new Object[] { " C ", "G G", " G ", 'G', "blockGlass", 'C', Items.CLAY_BALL });
        oreDictRecipe("tablewood", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.tableWood), new Object[] { "SSS", "W W", 'S', "slabWood", 'W', "plankWood" });
        oreDictRecipe("tablestone", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.tableStone), new Object[] { "SSS", "W W", 'S', new ItemStack(Blocks.STONE_SLAB), 'W', "stone" });
        ResourceLocation inkwellGroup = new ResourceLocation("thaumcraft", "inkwell");
        shapelessOreDictRecipe("scribingtoolscraft1", inkwellGroup, new ItemStack(ItemsTC.scribingTools), new Object[] { new ItemStack(ItemsTC.phial, 1, 0), Items.FEATHER, "dyeBlack" });
        shapelessOreDictRecipe("scribingtoolscraft2", inkwellGroup, new ItemStack(ItemsTC.scribingTools), new Object[] { Items.GLASS_BOTTLE, Items.FEATHER, "dyeBlack" });
        shapelessOreDictRecipe("scribingtoolsrefill", inkwellGroup, new ItemStack(ItemsTC.scribingTools), new Object[] { new ItemStack(ItemsTC.scribingTools, 1, 32767), "dyeBlack" });
        oreDictRecipe("GolemBell", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.golemBell), new Object[] { " QQ", " QQ", "S  ", 'S', "stickWood", 'Q', "gemQuartz" });
        ResourceLocation candlesGroup = new ResourceLocation("thaumcraft", "tallowcandles");
        oreDictRecipe("TallowCandle", candlesGroup, new ItemStack(BlocksTC.candles.get(EnumDyeColor.WHITE), 3), new Object[] { " S ", " T ", " T ", 'S', "string", 'T', new ItemStack(ItemsTC.tallow) });
        IRecipe[] trs = new IRecipe[16];
        int a = 0;
        for (EnumDyeColor d : EnumDyeColor.values()) {
            trs[a] = shapelessOreDictRecipe("TallowCandle" + d.getUnlocalizedName().toLowerCase(), candlesGroup, new ItemStack(BlocksTC.candles.get(d)), new Object[] { ConfigAspects.dyes[15 - a], ingredientsFromBlocks(BlocksTC.candles.values().toArray(new Block[0])) });
            ++a;
        }
        oreDictRecipe("BrassBrace", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.jarBrace, 2), new Object[] { "NSN", "S S", "NSN", 'N', "nuggetBrass", 'S', "stickWood" });
    }
    
    public static Ingredient ingredientsFromBlocks(Block... blocks) {
        ItemStack[] aitemstack = new ItemStack[blocks.length];
        for (int i = 0; i < blocks.length; ++i) {
            aitemstack[i] = new ItemStack(blocks[i]);
        }
        return Ingredient.fromStacks(aitemstack);
    }
    
    public static void initializeSmelting() {
        GameRegistry.addSmelting(BlocksTC.oreCinnabar, new ItemStack(ItemsTC.quicksilver), 1.0f);
        GameRegistry.addSmelting(BlocksTC.oreAmber, new ItemStack(ItemsTC.amber), 1.0f);
        GameRegistry.addSmelting(BlocksTC.oreQuartz, new ItemStack(Items.QUARTZ), 1.0f);
        GameRegistry.addSmelting(BlocksTC.logGreatwood, new ItemStack(Items.COAL, 1, 1), 0.5f);
        GameRegistry.addSmelting(BlocksTC.logSilverwood, new ItemStack(Items.COAL, 1, 1), 0.5f);
        GameRegistry.addSmelting(new ItemStack(ItemsTC.clusters, 1, 0), new ItemStack(Items.IRON_INGOT, 2, 0), 1.0f);
        GameRegistry.addSmelting(new ItemStack(ItemsTC.clusters, 1, 1), new ItemStack(Items.GOLD_INGOT, 2, 0), 1.0f);
        GameRegistry.addSmelting(new ItemStack(ItemsTC.clusters, 1, 6), new ItemStack(ItemsTC.quicksilver, 2, 0), 1.0f);
        GameRegistry.addSmelting(new ItemStack(ItemsTC.clusters, 1, 7), new ItemStack(Items.QUARTZ, 2, 0), 1.0f);
        ThaumcraftApi.addSmeltingBonus("oreGold", new ItemStack(Items.GOLD_NUGGET));
        ThaumcraftApi.addSmeltingBonus("oreIron", new ItemStack(Items.IRON_NUGGET));
        ThaumcraftApi.addSmeltingBonus("oreCinnabar", new ItemStack(ItemsTC.nuggets, 1, 5));
        ThaumcraftApi.addSmeltingBonus("oreCopper", new ItemStack(ItemsTC.nuggets, 1, 1));
        ThaumcraftApi.addSmeltingBonus("oreTin", new ItemStack(ItemsTC.nuggets, 1, 2));
        ThaumcraftApi.addSmeltingBonus("oreSilver", new ItemStack(ItemsTC.nuggets, 1, 3));
        ThaumcraftApi.addSmeltingBonus("oreLead", new ItemStack(ItemsTC.nuggets, 1, 4));
        ThaumcraftApi.addSmeltingBonus("oreQuartz", new ItemStack(ItemsTC.nuggets, 1, 9));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTC.clusters, 1, 0), new ItemStack(Items.IRON_NUGGET));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTC.clusters, 1, 1), new ItemStack(Items.GOLD_NUGGET));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTC.clusters, 1, 6), new ItemStack(ItemsTC.nuggets, 1, 5));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTC.clusters, 1, 2), new ItemStack(ItemsTC.nuggets, 1, 1));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTC.clusters, 1, 3), new ItemStack(ItemsTC.nuggets, 1, 2));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTC.clusters, 1, 4), new ItemStack(ItemsTC.nuggets, 1, 3));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTC.clusters, 1, 5), new ItemStack(ItemsTC.nuggets, 1, 4));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTC.clusters, 1, 7), new ItemStack(ItemsTC.nuggets, 1, 9));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(Items.BEEF), new ItemStack(ItemsTC.chunks, 1, 0));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(Items.CHICKEN), new ItemStack(ItemsTC.chunks, 1, 1));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(Items.PORKCHOP), new ItemStack(ItemsTC.chunks, 1, 2));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(Items.FISH, 1, 32767), new ItemStack(ItemsTC.chunks, 1, 3));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(Items.RABBIT), new ItemStack(ItemsTC.chunks, 1, 4));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(Items.MUTTON), new ItemStack(ItemsTC.chunks, 1, 5));
        ThaumcraftApi.addSmeltingBonus("oreDiamond", new ItemStack(ItemsTC.nuggets, 1, 10), 0.025f);
        ThaumcraftApi.addSmeltingBonus("oreRedstone", new ItemStack(ItemsTC.nuggets, 1, 10), 0.01f);
        ThaumcraftApi.addSmeltingBonus("oreLapis", new ItemStack(ItemsTC.nuggets, 1, 10), 0.01f);
        ThaumcraftApi.addSmeltingBonus("oreEmerald", new ItemStack(ItemsTC.nuggets, 1, 10), 0.025f);
        ThaumcraftApi.addSmeltingBonus("oreGold", new ItemStack(ItemsTC.nuggets, 1, 10), 0.02f);
        ThaumcraftApi.addSmeltingBonus("oreIron", new ItemStack(ItemsTC.nuggets, 1, 10), 0.01f);
        ThaumcraftApi.addSmeltingBonus("oreCinnabar", new ItemStack(ItemsTC.nuggets, 1, 10), 0.025f);
        ThaumcraftApi.addSmeltingBonus("oreCopper", new ItemStack(ItemsTC.nuggets, 1, 10), 0.01f);
        ThaumcraftApi.addSmeltingBonus("oreTin", new ItemStack(ItemsTC.nuggets, 1, 10), 0.01f);
        ThaumcraftApi.addSmeltingBonus("oreSilver", new ItemStack(ItemsTC.nuggets, 1, 10), 0.02f);
        ThaumcraftApi.addSmeltingBonus("oreLead", new ItemStack(ItemsTC.nuggets, 1, 10), 0.01f);
        ThaumcraftApi.addSmeltingBonus("oreQuartz", new ItemStack(ItemsTC.nuggets, 1, 10), 0.01f);
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTC.clusters, 1, 32767), new ItemStack(ItemsTC.nuggets, 1, 10), 0.02f);
    }
    
    static IRecipe oreDictRecipe(String name, ResourceLocation optionalGroup, ItemStack res, Object[] params) {
        IRecipe rec = new ShapedOreRecipe(optionalGroup, res, params);
        rec.setRegistryName(new ResourceLocation("thaumcraft", name));
        GameData.register_impl((IForgeRegistryEntry)rec);
        return rec;
    }
    
    static IRecipe shapelessOreDictRecipe(String name, ResourceLocation optionalGroup, ItemStack res, Object[] params) {
        IRecipe rec = new ShapelessOreRecipe(optionalGroup, res, params);
        rec.setRegistryName(new ResourceLocation("thaumcraft", name));
        GameData.register_impl((IForgeRegistryEntry)rec);
        return rec;
    }
    
    public static void postAspects() {
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_gunpowder"), new CrucibleRecipe("HEDGEALCHEMY@2", new ItemStack(Items.GUNPOWDER, 2, 0), new ItemStack(Items.GUNPOWDER), new AspectList(new ItemStack(Items.GUNPOWDER))));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_slime"), new CrucibleRecipe("HEDGEALCHEMY@2", new ItemStack(Items.SLIME_BALL, 2, 0), new ItemStack(Items.SLIME_BALL), new AspectList(new ItemStack(Items.SLIME_BALL))));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_glowstone"), new CrucibleRecipe("HEDGEALCHEMY@2", new ItemStack(Items.GLOWSTONE_DUST, 2, 0), "dustGlowstone", new AspectList(new ItemStack(Items.GLOWSTONE_DUST))));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_dye"), new CrucibleRecipe("HEDGEALCHEMY@2", new ItemStack(Items.DYE, 2, 0), new ItemStack(Items.DYE, 1, 0), new AspectList(new ItemStack(Items.DYE))));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_clay"), new CrucibleRecipe("HEDGEALCHEMY@3", new ItemStack(Items.CLAY_BALL, 1, 0), new ItemStack(Blocks.DIRT), new AspectList(new ItemStack(Items.CLAY_BALL, 1, 0)).remove(new AspectList(new ItemStack(Blocks.DIRT)))));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_string"), new CrucibleRecipe("HEDGEALCHEMY@3", new ItemStack(Items.STRING), new ItemStack(Items.WHEAT), new AspectList(new ItemStack(Items.STRING)).remove(new AspectList(new ItemStack(Items.WHEAT)))));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_web"), new CrucibleRecipe("HEDGEALCHEMY@3", new ItemStack(Blocks.WEB), new ItemStack(Items.STRING), new AspectList(new ItemStack(Blocks.WEB)).remove(new AspectList(new ItemStack(Items.STRING)))));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_lava"), new CrucibleRecipe("HEDGEALCHEMY@3", new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.BUCKET), new AspectList().add(Aspect.FIRE, 15).add(Aspect.EARTH, 5)));
    }
    
    public static void compileGroups() {
        for (ResourceLocation reg : CraftingManager.REGISTRY.getKeys()) {
            IRecipe recipe = CraftingManager.getRecipe(reg);
            if (recipe != null) {
                String group = recipe.getGroup();
                if (group.trim().isEmpty()) {
                    continue;
                }
                if (group.startsWith("minecraft")) {
                    continue;
                }
                if (!ConfigRecipes.recipeGroups.containsKey(group)) {
                    ConfigRecipes.recipeGroups.put(group, new ArrayList<ResourceLocation>());
                }
                ArrayList list = ConfigRecipes.recipeGroups.get(group);
                list.add(reg);
            }
        }
        for (ResourceLocation reg : CommonInternals.craftingRecipeCatalog.keySet()) {
            IThaumcraftRecipe recipe2 = CommonInternals.craftingRecipeCatalog.get(reg);
            if (recipe2 != null) {
                String group = recipe2.getGroup();
                if (group == null) {
                    continue;
                }
                if (group.trim().isEmpty()) {
                    continue;
                }
                if (!ConfigRecipes.recipeGroups.containsKey(group)) {
                    ConfigRecipes.recipeGroups.put(group, new ArrayList<ResourceLocation>());
                }
                ArrayList list = ConfigRecipes.recipeGroups.get(group);
                list.add(reg);
            }
        }
        for (ResourceLocation reg : CommonInternals.craftingRecipeCatalogFake.keySet()) {
            Object recipe3 = CommonInternals.craftingRecipeCatalogFake.get(reg);
            if (recipe3 != null) {
                String group = "";
                if (recipe3 instanceof IRecipe) {
                    recipe3 = ((IRecipe)recipe3).getGroup();
                }
                else if (recipe3 instanceof IThaumcraftRecipe) {
                    recipe3 = ((IThaumcraftRecipe)recipe3).getGroup();
                }
                if (group == null) {
                    continue;
                }
                if (group.trim().isEmpty()) {
                    continue;
                }
                if (!ConfigRecipes.recipeGroups.containsKey(group)) {
                    ConfigRecipes.recipeGroups.put(group, new ArrayList<ResourceLocation>());
                }
                ArrayList list = ConfigRecipes.recipeGroups.get(group);
                list.add(reg);
            }
        }
    }
    
    static {
        ConfigRecipes.defaultGroup = new ResourceLocation("");
        ConfigRecipes.recipeGroups = new HashMap<String, ArrayList<ResourceLocation>>();
    }
}
