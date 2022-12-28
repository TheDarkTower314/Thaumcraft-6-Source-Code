package thaumcraft.common.config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSlab;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.basic.BlockBannerTC;
import thaumcraft.common.blocks.basic.BlockBannerTCItem;
import thaumcraft.common.blocks.basic.BlockCandle;
import thaumcraft.common.blocks.basic.BlockMetalTC;
import thaumcraft.common.blocks.basic.BlockPavingStone;
import thaumcraft.common.blocks.basic.BlockPillar;
import thaumcraft.common.blocks.basic.BlockPlanksTC;
import thaumcraft.common.blocks.basic.BlockSlabTC;
import thaumcraft.common.blocks.basic.BlockStairsTC;
import thaumcraft.common.blocks.basic.BlockStonePorous;
import thaumcraft.common.blocks.basic.BlockStoneTC;
import thaumcraft.common.blocks.basic.BlockTable;
import thaumcraft.common.blocks.basic.BlockTranslucent;
import thaumcraft.common.blocks.crafting.BlockArcaneWorkbench;
import thaumcraft.common.blocks.crafting.BlockArcaneWorkbenchCharger;
import thaumcraft.common.blocks.crafting.BlockCrucible;
import thaumcraft.common.blocks.crafting.BlockFocalManipulator;
import thaumcraft.common.blocks.crafting.BlockGolemBuilder;
import thaumcraft.common.blocks.crafting.BlockInfusionMatrix;
import thaumcraft.common.blocks.crafting.BlockPatternCrafter;
import thaumcraft.common.blocks.crafting.BlockResearchTable;
import thaumcraft.common.blocks.crafting.BlockThaumatorium;
import thaumcraft.common.blocks.crafting.BlockVoidSiphon;
import thaumcraft.common.blocks.devices.BlockArcaneEar;
import thaumcraft.common.blocks.devices.BlockArcaneEarToggle;
import thaumcraft.common.blocks.devices.BlockBellows;
import thaumcraft.common.blocks.devices.BlockBrainBox;
import thaumcraft.common.blocks.devices.BlockCondenser;
import thaumcraft.common.blocks.devices.BlockCondenserLattice;
import thaumcraft.common.blocks.devices.BlockDioptra;
import thaumcraft.common.blocks.devices.BlockHungryChest;
import thaumcraft.common.blocks.devices.BlockInfernalFurnace;
import thaumcraft.common.blocks.devices.BlockInlay;
import thaumcraft.common.blocks.devices.BlockLamp;
import thaumcraft.common.blocks.devices.BlockLevitator;
import thaumcraft.common.blocks.devices.BlockMirror;
import thaumcraft.common.blocks.devices.BlockMirrorItem;
import thaumcraft.common.blocks.devices.BlockPedestal;
import thaumcraft.common.blocks.devices.BlockPotionSprayer;
import thaumcraft.common.blocks.devices.BlockRechargePedestal;
import thaumcraft.common.blocks.devices.BlockRedstoneRelay;
import thaumcraft.common.blocks.devices.BlockSpa;
import thaumcraft.common.blocks.devices.BlockStabilizer;
import thaumcraft.common.blocks.devices.BlockVisBattery;
import thaumcraft.common.blocks.devices.BlockVisGenerator;
import thaumcraft.common.blocks.devices.BlockWaterJug;
import thaumcraft.common.blocks.essentia.BlockAlembic;
import thaumcraft.common.blocks.essentia.BlockCentrifuge;
import thaumcraft.common.blocks.essentia.BlockEssentiaTransport;
import thaumcraft.common.blocks.essentia.BlockJar;
import thaumcraft.common.blocks.essentia.BlockJarBrainItem;
import thaumcraft.common.blocks.essentia.BlockJarItem;
import thaumcraft.common.blocks.essentia.BlockSmelter;
import thaumcraft.common.blocks.essentia.BlockSmelterAux;
import thaumcraft.common.blocks.essentia.BlockSmelterVent;
import thaumcraft.common.blocks.essentia.BlockTube;
import thaumcraft.common.blocks.misc.BlockBarrier;
import thaumcraft.common.blocks.misc.BlockEffect;
import thaumcraft.common.blocks.misc.BlockFlesh;
import thaumcraft.common.blocks.misc.BlockFluidDeath;
import thaumcraft.common.blocks.misc.BlockFluidPure;
import thaumcraft.common.blocks.misc.BlockHole;
import thaumcraft.common.blocks.misc.BlockNitor;
import thaumcraft.common.blocks.misc.BlockPlaceholder;
import thaumcraft.common.blocks.world.BlockGrassAmbient;
import thaumcraft.common.blocks.world.BlockLoot;
import thaumcraft.common.blocks.world.ore.BlockCrystal;
import thaumcraft.common.blocks.world.ore.BlockOreTC;
import thaumcraft.common.blocks.world.ore.ShardType;
import thaumcraft.common.blocks.world.plants.BlockLeavesTC;
import thaumcraft.common.blocks.world.plants.BlockLogsTC;
import thaumcraft.common.blocks.world.plants.BlockPlantCinderpearl;
import thaumcraft.common.blocks.world.plants.BlockPlantShimmerleaf;
import thaumcraft.common.blocks.world.plants.BlockPlantVishroom;
import thaumcraft.common.blocks.world.plants.BlockSaplingTC;
import thaumcraft.common.blocks.world.taint.BlockFluxGoo;
import thaumcraft.common.blocks.world.taint.BlockTaint;
import thaumcraft.common.blocks.world.taint.BlockTaintFeature;
import thaumcraft.common.blocks.world.taint.BlockTaintFibre;
import thaumcraft.common.blocks.world.taint.BlockTaintLog;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;
import thaumcraft.common.tiles.crafting.TileCrucible;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;
import thaumcraft.common.tiles.crafting.TileGolemBuilder;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;
import thaumcraft.common.tiles.crafting.TilePatternCrafter;
import thaumcraft.common.tiles.crafting.TilePedestal;
import thaumcraft.common.tiles.crafting.TileResearchTable;
import thaumcraft.common.tiles.crafting.TileThaumatorium;
import thaumcraft.common.tiles.crafting.TileThaumatoriumTop;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;
import thaumcraft.common.tiles.devices.TileArcaneEar;
import thaumcraft.common.tiles.devices.TileBellows;
import thaumcraft.common.tiles.devices.TileCondenser;
import thaumcraft.common.tiles.devices.TileDioptra;
import thaumcraft.common.tiles.devices.TileHungryChest;
import thaumcraft.common.tiles.devices.TileInfernalFurnace;
import thaumcraft.common.tiles.devices.TileJarBrain;
import thaumcraft.common.tiles.devices.TileLampArcane;
import thaumcraft.common.tiles.devices.TileLampFertility;
import thaumcraft.common.tiles.devices.TileLampGrowth;
import thaumcraft.common.tiles.devices.TileLevitator;
import thaumcraft.common.tiles.devices.TileMirror;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;
import thaumcraft.common.tiles.devices.TilePotionSprayer;
import thaumcraft.common.tiles.devices.TileRechargePedestal;
import thaumcraft.common.tiles.devices.TileRedstoneRelay;
import thaumcraft.common.tiles.devices.TileSpa;
import thaumcraft.common.tiles.devices.TileStabilizer;
import thaumcraft.common.tiles.devices.TileVisGenerator;
import thaumcraft.common.tiles.devices.TileWaterJug;
import thaumcraft.common.tiles.essentia.TileAlembic;
import thaumcraft.common.tiles.essentia.TileCentrifuge;
import thaumcraft.common.tiles.essentia.TileEssentiaInput;
import thaumcraft.common.tiles.essentia.TileEssentiaOutput;
import thaumcraft.common.tiles.essentia.TileJarFillable;
import thaumcraft.common.tiles.essentia.TileJarFillableVoid;
import thaumcraft.common.tiles.essentia.TileSmelter;
import thaumcraft.common.tiles.essentia.TileTube;
import thaumcraft.common.tiles.essentia.TileTubeBuffer;
import thaumcraft.common.tiles.essentia.TileTubeFilter;
import thaumcraft.common.tiles.essentia.TileTubeOneway;
import thaumcraft.common.tiles.essentia.TileTubeRestrict;
import thaumcraft.common.tiles.essentia.TileTubeValve;
import thaumcraft.common.tiles.misc.TileBanner;
import thaumcraft.common.tiles.misc.TileBarrierStone;
import thaumcraft.common.tiles.misc.TileHole;
import thaumcraft.common.tiles.misc.TileNitor;


public class ConfigBlocks
{
    public static void initMisc() {
        BlocksTC.oreAmber.setHarvestLevel("pickaxe", 1);
        BlocksTC.oreCinnabar.setHarvestLevel("pickaxe", 2);
        BlockUtils.portableHoleBlackList.add("minecraft:bed");
        BlockUtils.portableHoleBlackList.add("minecraft:piston");
        BlockUtils.portableHoleBlackList.add("minecraft:piston_head");
        BlockUtils.portableHoleBlackList.add("minecraft:sticky_piston");
        BlockUtils.portableHoleBlackList.add("minecraft:piston_extension");
        BlockUtils.portableHoleBlackList.add("minecraft:wooden_door");
        BlockUtils.portableHoleBlackList.add("minecraft:spruce_door");
        BlockUtils.portableHoleBlackList.add("minecraft:birch_door");
        BlockUtils.portableHoleBlackList.add("minecraft:jungle_door");
        BlockUtils.portableHoleBlackList.add("minecraft:acacia_door");
        BlockUtils.portableHoleBlackList.add("minecraft:dark_oak_door");
        BlockUtils.portableHoleBlackList.add("minecraft:iron_door");
        BlockUtils.portableHoleBlackList.add("thaumcraft:infernal_furnace");
    }
    
    public static void initBlocks(IForgeRegistry<Block> iForgeRegistry) {
        BlocksTC.oreAmber = registerBlock(new BlockOreTC("ore_amber").setHardness(1.5f));
        BlocksTC.oreCinnabar = registerBlock(new BlockOreTC("ore_cinnabar").setHardness(2.0f));
        BlocksTC.oreQuartz = registerBlock(new BlockOreTC("ore_quartz").setHardness(3.0f));
        BlocksTC.crystalAir = registerBlock(new BlockCrystal("crystal_aer", Aspect.AIR));
        BlocksTC.crystalFire = registerBlock(new BlockCrystal("crystal_ignis", Aspect.FIRE));
        BlocksTC.crystalWater = registerBlock(new BlockCrystal("crystal_aqua", Aspect.WATER));
        BlocksTC.crystalEarth = registerBlock(new BlockCrystal("crystal_terra", Aspect.EARTH));
        BlocksTC.crystalOrder = registerBlock(new BlockCrystal("crystal_ordo", Aspect.ORDER));
        BlocksTC.crystalEntropy = registerBlock(new BlockCrystal("crystal_perditio", Aspect.ENTROPY));
        BlocksTC.crystalTaint = registerBlock(new BlockCrystal("crystal_vitium", Aspect.FLUX));
        ShardType.AIR.setOre(BlocksTC.crystalAir);
        ShardType.FIRE.setOre(BlocksTC.crystalFire);
        ShardType.WATER.setOre(BlocksTC.crystalWater);
        ShardType.EARTH.setOre(BlocksTC.crystalEarth);
        ShardType.ORDER.setOre(BlocksTC.crystalOrder);
        ShardType.ENTROPY.setOre(BlocksTC.crystalEntropy);
        ShardType.FLUX.setOre(BlocksTC.crystalTaint);
        BlocksTC.stoneArcane = registerBlock(new BlockStoneTC("stone_arcane", true));
        BlocksTC.stoneArcaneBrick = registerBlock(new BlockStoneTC("stone_arcane_brick", true));
        BlocksTC.stoneAncient = registerBlock(new BlockStoneTC("stone_ancient", true));
        BlocksTC.stoneAncientTile = registerBlock(new BlockStoneTC("stone_ancient_tile", false));
        BlocksTC.stoneAncientRock = registerBlock(new BlockStoneTC("stone_ancient_rock", false).setHardness(-1.0f));
        BlocksTC.stoneAncientGlyphed = registerBlock(new BlockStoneTC("stone_ancient_glyphed", false));
        BlocksTC.stoneAncientDoorway = registerBlock(new BlockStoneTC("stone_ancient_doorway", false).setHardness(-1.0f));
        BlocksTC.stoneEldritchTile = registerBlock(new BlockStoneTC("stone_eldritch_tile", true).setHardness(15.0f).setResistance(1000.0f));
        BlocksTC.stonePorous = registerBlock(new BlockStonePorous());
        BlocksTC.stairsArcane = registerBlock(new BlockStairsTC("stairs_arcane", BlocksTC.stoneArcane.getDefaultState()));
        BlocksTC.stairsArcaneBrick = registerBlock(new BlockStairsTC("stairs_arcane_brick", BlocksTC.stoneArcaneBrick.getDefaultState()));
        BlocksTC.stairsAncient = registerBlock(new BlockStairsTC("stairs_ancient", BlocksTC.stoneAncient.getDefaultState()));
        BlocksTC.slabArcaneStone = (BlockSlab)new BlockSlabTC.Half("slab_arcane_stone", null, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.doubleSlabArcaneStone = (BlockSlab)new BlockSlabTC.Double("slab_double_arcane_stone", BlocksTC.slabArcaneStone, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.slabArcaneBrick = (BlockSlab)new BlockSlabTC.Half("slab_arcane_brick", null, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.doubleSlabArcaneBrick = (BlockSlab)new BlockSlabTC.Double("slab_double_arcane_brick", BlocksTC.slabArcaneBrick, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.slabAncient = (BlockSlab)new BlockSlabTC.Half("slab_ancient", null, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.doubleSlabAncient = (BlockSlab)new BlockSlabTC.Double("slab_double_ancient", BlocksTC.slabAncient, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.slabEldritch = (BlockSlab)new BlockSlabTC.Half("slab_eldritch", null, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.doubleSlabEldritch = (BlockSlab)new BlockSlabTC.Double("slab_double_eldritch", BlocksTC.slabEldritch, false).setHardness(2.0f).setResistance(10.0f);
        ForgeRegistries.BLOCKS.register(BlocksTC.slabArcaneStone);
        ForgeRegistries.BLOCKS.register(BlocksTC.doubleSlabArcaneStone);
        ForgeRegistries.BLOCKS.register(BlocksTC.slabArcaneBrick);
        ForgeRegistries.BLOCKS.register(BlocksTC.doubleSlabArcaneBrick);
        ForgeRegistries.BLOCKS.register(BlocksTC.slabAncient);
        ForgeRegistries.BLOCKS.register(BlocksTC.doubleSlabAncient);
        ForgeRegistries.BLOCKS.register(BlocksTC.slabEldritch);
        ForgeRegistries.BLOCKS.register(BlocksTC.doubleSlabEldritch);
        ForgeRegistries.ITEMS.register(new ItemSlab(BlocksTC.slabArcaneStone, BlocksTC.slabArcaneStone, BlocksTC.doubleSlabArcaneStone).setRegistryName(BlocksTC.slabArcaneStone.getRegistryName()));
        ForgeRegistries.ITEMS.register(new ItemSlab(BlocksTC.slabArcaneBrick, BlocksTC.slabArcaneBrick, BlocksTC.doubleSlabArcaneBrick).setRegistryName(BlocksTC.slabArcaneBrick.getRegistryName()));
        ForgeRegistries.ITEMS.register(new ItemSlab(BlocksTC.slabAncient, BlocksTC.slabAncient, BlocksTC.doubleSlabAncient).setRegistryName(BlocksTC.slabAncient.getRegistryName()));
        ForgeRegistries.ITEMS.register(new ItemSlab(BlocksTC.slabEldritch, BlocksTC.slabEldritch, BlocksTC.doubleSlabEldritch).setRegistryName(BlocksTC.slabEldritch.getRegistryName()));
        BlocksTC.saplingGreatwood = registerBlock(new BlockSaplingTC("sapling_greatwood"));
        BlocksTC.saplingSilverwood = registerBlock(new BlockSaplingTC("sapling_silverwood"));
        BlocksTC.logGreatwood = registerBlock(new BlockLogsTC("log_greatwood"));
        BlocksTC.logSilverwood = registerBlock(new BlockLogsTC("log_silverwood"));
        BlocksTC.leafGreatwood = registerBlock(new BlockLeavesTC("leaves_greatwood"));
        BlocksTC.leafSilverwood = registerBlock(new BlockLeavesTC("leaves_silverwood"));
        BlocksTC.shimmerleaf = registerBlock(new BlockPlantShimmerleaf());
        BlocksTC.cinderpearl = registerBlock(new BlockPlantCinderpearl());
        BlocksTC.vishroom = registerBlock(new BlockPlantVishroom());
        BlocksTC.plankGreatwood = registerBlock(new BlockPlanksTC("plank_greatwood"));
        BlocksTC.plankSilverwood = registerBlock(new BlockPlanksTC("plank_silverwood"));
        BlocksTC.stairsGreatwood = registerBlock(new BlockStairsTC("stairs_greatwood", BlocksTC.plankGreatwood.getDefaultState()));
        BlocksTC.stairsSilverwood = registerBlock(new BlockStairsTC("stairs_silverwood", BlocksTC.plankSilverwood.getDefaultState()));
        BlocksTC.slabGreatwood = (BlockSlab)new BlockSlabTC.Half("slab_greatwood", null, true).setHardness(1.2f).setResistance(2.0f);
        BlocksTC.doubleSlabGreatwood = (BlockSlab)new BlockSlabTC.Double("slab_double_greatwood", BlocksTC.slabGreatwood, true).setHardness(1.2f).setResistance(2.0f);
        BlocksTC.slabSilverwood = (BlockSlab)new BlockSlabTC.Half("slab_silverwood", null, true).setHardness(1.0f).setResistance(2.0f);
        BlocksTC.doubleSlabSilverwood = (BlockSlab)new BlockSlabTC.Double("slab_double_silverwood", BlocksTC.slabSilverwood, true).setHardness(1.0f).setResistance(2.0f);
        ForgeRegistries.BLOCKS.register(BlocksTC.slabGreatwood);
        ForgeRegistries.BLOCKS.register(BlocksTC.doubleSlabGreatwood);
        ForgeRegistries.BLOCKS.register(BlocksTC.slabSilverwood);
        ForgeRegistries.BLOCKS.register(BlocksTC.doubleSlabSilverwood);
        ForgeRegistries.ITEMS.register(new ItemSlab(BlocksTC.slabGreatwood, BlocksTC.slabGreatwood, BlocksTC.doubleSlabGreatwood).setRegistryName(BlocksTC.slabGreatwood.getRegistryName()));
        ForgeRegistries.ITEMS.register(new ItemSlab(BlocksTC.slabSilverwood, BlocksTC.slabSilverwood, BlocksTC.doubleSlabSilverwood).setRegistryName(BlocksTC.slabSilverwood.getRegistryName()));
        BlocksTC.amberBlock = registerBlock(new BlockTranslucent("amber_block"));
        BlocksTC.amberBrick = registerBlock(new BlockTranslucent("amber_brick"));
        BlocksTC.fleshBlock = registerBlock(new BlockFlesh());
        BlocksTC.lootCrateCommon = registerBlock(new BlockLoot(Material.WOOD, "loot_crate_common", BlockLoot.LootType.COMMON));
        BlocksTC.lootCrateUncommon = registerBlock(new BlockLoot(Material.WOOD, "loot_crate_uncommon", BlockLoot.LootType.UNCOMMON));
        BlocksTC.lootCrateRare = registerBlock(new BlockLoot(Material.WOOD, "loot_crate_rare", BlockLoot.LootType.RARE));
        BlocksTC.lootUrnCommon = registerBlock(new BlockLoot(Material.ROCK, "loot_urn_common", BlockLoot.LootType.COMMON));
        BlocksTC.lootUrnUncommon = registerBlock(new BlockLoot(Material.ROCK, "loot_urn_uncommon", BlockLoot.LootType.UNCOMMON));
        BlocksTC.lootUrnRare = registerBlock(new BlockLoot(Material.ROCK, "loot_urn_rare", BlockLoot.LootType.RARE));
        BlocksTC.taintFibre = registerBlock(new BlockTaintFibre());
        BlocksTC.taintCrust = registerBlock(new BlockTaint("taint_crust"));
        BlocksTC.taintSoil = registerBlock(new BlockTaint("taint_soil"));
        BlocksTC.taintRock = registerBlock(new BlockTaint("taint_rock"));
        BlocksTC.taintGeyser = registerBlock(new BlockTaint("taint_geyser"));
        BlocksTC.taintFeature = registerBlock(new BlockTaintFeature());
        BlocksTC.taintLog = registerBlock(new BlockTaintLog());
        BlocksTC.grassAmbient = registerBlock(new BlockGrassAmbient());
        BlocksTC.tableWood = registerBlock(new BlockTable(Material.WOOD, "table_wood", SoundType.WOOD).setHardness(2.0f));
        BlocksTC.tableStone = registerBlock(new BlockTable(Material.ROCK, "table_stone", SoundType.STONE).setHardness(2.5f));
        BlocksTC.pedestalArcane = registerBlock(new BlockPedestal("pedestal_arcane"));
        BlocksTC.pedestalAncient = registerBlock(new BlockPedestal("pedestal_ancient"));
        BlocksTC.pedestalEldritch = registerBlock(new BlockPedestal("pedestal_eldritch"));
        BlocksTC.metalBlockBrass = registerBlock(new BlockMetalTC("metal_brass"));
        BlocksTC.metalBlockThaumium = registerBlock(new BlockMetalTC("metal_thaumium"));
        BlocksTC.metalBlockVoid = registerBlock(new BlockMetalTC("metal_void"));
        BlocksTC.metalAlchemical = registerBlock(new BlockMetalTC("metal_alchemical"));
        BlocksTC.metalAlchemicalAdvanced = registerBlock(new BlockMetalTC("metal_alchemical_advanced"));
        BlocksTC.pavingStoneTravel = registerBlock(new BlockPavingStone("paving_stone_travel"));
        BlocksTC.pavingStoneBarrier = registerBlock(new BlockPavingStone("paving_stone_barrier"));
        BlocksTC.pillarArcane = registerBlock(new BlockPillar("pillar_arcane"));
        BlocksTC.pillarAncient = registerBlock(new BlockPillar("pillar_ancient"));
        BlocksTC.pillarEldritch = registerBlock(new BlockPillar("pillar_eldritch"));
        BlocksTC.matrixSpeed = registerBlock(new BlockStoneTC("matrix_speed", false));
        BlocksTC.matrixCost = registerBlock(new BlockStoneTC("matrix_cost", false));
        for (EnumDyeColor dye : EnumDyeColor.values()) {
            BlocksTC.candles.put(dye, registerBlock(new BlockCandle("candle_" + dye.getUnlocalizedName().toLowerCase(), dye)));
        }
        for (EnumDyeColor dye : EnumDyeColor.values()) {
            BlockBannerTC block = new BlockBannerTC("banner_" + dye.getUnlocalizedName().toLowerCase(), dye);
            ForgeRegistries.BLOCKS.register(block);
            ForgeRegistries.ITEMS.register(new BlockBannerTCItem(block).setRegistryName(block.getRegistryName()));
            BlocksTC.banners.put(dye, block);
        }
        BlocksTC.bannerCrimsonCult = new BlockBannerTC("banner_crimson_cult", null);
        ForgeRegistries.BLOCKS.register(BlocksTC.bannerCrimsonCult);
        ForgeRegistries.ITEMS.register(new BlockBannerTCItem((BlockBannerTC)BlocksTC.bannerCrimsonCult).setRegistryName(BlocksTC.bannerCrimsonCult.getRegistryName()));
        for (EnumDyeColor dye : EnumDyeColor.values()) {
            BlocksTC.nitor.put(dye, registerBlock(new BlockNitor("nitor_" + dye.getUnlocalizedName().toLowerCase(), dye)));
        }
        BlocksTC.visBattery = registerBlock(new BlockVisBattery());
        BlocksTC.inlay = registerBlock(new BlockInlay());
        BlocksTC.arcaneWorkbench = registerBlock(new BlockArcaneWorkbench());
        BlocksTC.arcaneWorkbenchCharger = registerBlock(new BlockArcaneWorkbenchCharger());
        BlocksTC.dioptra = registerBlock(new BlockDioptra());
        BlocksTC.researchTable = registerBlock(new BlockResearchTable());
        BlocksTC.crucible = registerBlock(new BlockCrucible());
        BlocksTC.arcaneEar = registerBlock(new BlockArcaneEar("arcane_ear"));
        BlocksTC.arcaneEarToggle = registerBlock(new BlockArcaneEarToggle());
        BlocksTC.lampArcane = registerBlock(new BlockLamp(TileLampArcane.class, "lamp_arcane"));
        BlocksTC.lampFertility = registerBlock(new BlockLamp(TileLampFertility.class, "lamp_fertility"));
        BlocksTC.lampGrowth = registerBlock(new BlockLamp(TileLampGrowth.class, "lamp_growth"));
        BlocksTC.levitator = registerBlock(new BlockLevitator());
        BlocksTC.centrifuge = registerBlock(new BlockCentrifuge());
        BlocksTC.bellows = registerBlock(new BlockBellows());
        BlocksTC.smelterBasic = registerBlock(new BlockSmelter("smelter_basic"));
        BlocksTC.smelterThaumium = registerBlock(new BlockSmelter("smelter_thaumium"));
        BlocksTC.smelterVoid = registerBlock(new BlockSmelter("smelter_void"));
        BlocksTC.smelterAux = registerBlock(new BlockSmelterAux());
        BlocksTC.smelterVent = registerBlock(new BlockSmelterVent());
        BlocksTC.alembic = registerBlock(new BlockAlembic());
        BlocksTC.rechargePedestal = registerBlock(new BlockRechargePedestal());
        BlocksTC.wandWorkbench = registerBlock(new BlockFocalManipulator());
        BlocksTC.hungryChest = registerBlock(new BlockHungryChest());
        BlocksTC.tube = registerBlock(new BlockTube(TileTube.class, "tube"));
        BlocksTC.tubeValve = registerBlock(new BlockTube(TileTubeValve.class, "tube_valve"));
        BlocksTC.tubeRestrict = registerBlock(new BlockTube(TileTubeRestrict.class, "tube_restrict"));
        BlocksTC.tubeOneway = registerBlock(new BlockTube(TileTubeOneway.class, "tube_oneway"));
        BlocksTC.tubeFilter = registerBlock(new BlockTube(TileTubeFilter.class, "tube_filter"));
        BlocksTC.tubeBuffer = registerBlock(new BlockTube(TileTubeBuffer.class, "tube_buffer"));
        BlocksTC.jarNormal = registerBlock(new BlockJar(TileJarFillable.class, "jar_normal"), BlockJarItem.class);
        BlocksTC.jarVoid = registerBlock(new BlockJar(TileJarFillableVoid.class, "jar_void"), BlockJarItem.class);
        BlocksTC.jarBrain = registerBlock(new BlockJar(TileJarBrain.class, "jar_brain"), BlockJarBrainItem.class);
        BlocksTC.infusionMatrix = registerBlock(new BlockInfusionMatrix());
        BlocksTC.infernalFurnace = registerBlock(new BlockInfernalFurnace());
        BlocksTC.everfullUrn = registerBlock(new BlockWaterJug());
        BlocksTC.thaumatorium = registerBlock(new BlockThaumatorium(false));
        BlocksTC.thaumatoriumTop = registerBlock(new BlockThaumatorium(true));
        BlocksTC.brainBox = registerBlock(new BlockBrainBox());
        BlocksTC.spa = registerBlock(new BlockSpa());
        BlocksTC.golemBuilder = registerBlock(new BlockGolemBuilder());
        BlocksTC.mirror = registerBlock(new BlockMirror(TileMirror.class, "mirror"), BlockMirrorItem.class);
        BlocksTC.mirrorEssentia = registerBlock(new BlockMirror(TileMirrorEssentia.class, "mirror_essentia"), BlockMirrorItem.class);
        BlocksTC.essentiaTransportInput = registerBlock(new BlockEssentiaTransport(TileEssentiaInput.class, "essentia_input"));
        BlocksTC.essentiaTransportOutput = registerBlock(new BlockEssentiaTransport(TileEssentiaOutput.class, "essentia_output"));
        BlocksTC.redstoneRelay = registerBlock(new BlockRedstoneRelay());
        BlocksTC.patternCrafter = registerBlock(new BlockPatternCrafter());
        BlocksTC.potionSprayer = registerBlock(new BlockPotionSprayer());
        BlocksTC.activatorRail = registerBlock(new BlockRailPowered().setHardness(0.7f).setCreativeTab(ConfigItems.TABTC).setRegistryName("thaumcraft", "activator_rail").setUnlocalizedName("activator_rail"));
        BlocksTC.stabilizer = registerBlock(new BlockStabilizer());
        BlocksTC.visGenerator = registerBlock(new BlockVisGenerator());
        BlocksTC.condenser = registerBlock(new BlockCondenser());
        BlocksTC.condenserlattice = registerBlock(new BlockCondenserLattice(false));
        BlocksTC.condenserlatticeDirty = registerBlock(new BlockCondenserLattice(true));
        BlocksTC.voidSiphon = registerBlock(new BlockVoidSiphon());
        FluidRegistry.registerFluid(FluidFluxGoo.instance);
        iForgeRegistry.register((BlocksTC.fluxGoo = new BlockFluxGoo()));
        FluidRegistry.registerFluid(FluidDeath.instance);
        FluidRegistry.addBucketForFluid(FluidDeath.instance);
        iForgeRegistry.register((BlocksTC.liquidDeath = new BlockFluidDeath()));
        FluidRegistry.registerFluid(FluidPure.instance);
        FluidRegistry.addBucketForFluid(FluidPure.instance);
        iForgeRegistry.register((BlocksTC.purifyingFluid = new BlockFluidPure()));
        BlocksTC.hole = registerBlock(new BlockHole());
        BlocksTC.effectShock = registerBlock(new BlockEffect("effect_shock"));
        BlocksTC.effectSap = registerBlock(new BlockEffect("effect_sap"));
        BlocksTC.effectGlimmer = registerBlock(new BlockEffect("effect_glimmer"));
        BlocksTC.placeholderNetherbrick = registerBlock(new BlockPlaceholder("placeholder_brick"));
        BlocksTC.placeholderObsidian = registerBlock(new BlockPlaceholder("placeholder_obsidian"));
        BlocksTC.placeholderBars = registerBlock(new BlockPlaceholder("placeholder_bars"));
        BlocksTC.placeholderAnvil = registerBlock(new BlockPlaceholder("placeholder_anvil"));
        BlocksTC.placeholderCauldron = registerBlock(new BlockPlaceholder("placeholder_cauldron"));
        BlocksTC.placeholderTable = registerBlock(new BlockPlaceholder("placeholder_table"));
        BlocksTC.empty = registerBlock(new BlockTranslucent("empty"));
        BlocksTC.barrier = registerBlock(new BlockBarrier());
    }
    
    public static void initTileEntities() {
        GameRegistry.registerTileEntity(TileArcaneWorkbench.class, "thaumcraft:TileArcaneWorkbench");
        GameRegistry.registerTileEntity(TileDioptra.class, "thaumcraft:TileDioptra");
        GameRegistry.registerTileEntity(TileArcaneEar.class, "thaumcraft:TileArcaneEar");
        GameRegistry.registerTileEntity(TileLevitator.class, "thaumcraft:TileLevitator");
        GameRegistry.registerTileEntity(TileCrucible.class, "thaumcraft:TileCrucible");
        GameRegistry.registerTileEntity(TileNitor.class, "thaumcraft:TileNitor");
        GameRegistry.registerTileEntity(TileFocalManipulator.class, "thaumcraft:TileFocalManipulator");
        GameRegistry.registerTileEntity(TilePedestal.class, "thaumcraft:TilePedestal");
        GameRegistry.registerTileEntity(TileRechargePedestal.class, "thaumcraft:TileRechargePedestal");
        GameRegistry.registerTileEntity(TileResearchTable.class, "thaumcraft:TileResearchTable");
        GameRegistry.registerTileEntity(TileTube.class, "thaumcraft:TileTube");
        GameRegistry.registerTileEntity(TileTubeValve.class, "thaumcraft:TileTubeValve");
        GameRegistry.registerTileEntity(TileTubeFilter.class, "thaumcraft:TileTubeFilter");
        GameRegistry.registerTileEntity(TileTubeRestrict.class, "thaumcraft:TileTubeRestrict");
        GameRegistry.registerTileEntity(TileTubeOneway.class, "thaumcraft:TileTubeOneway");
        GameRegistry.registerTileEntity(TileTubeBuffer.class, "thaumcraft:TileTubeBuffer");
        GameRegistry.registerTileEntity(TileHungryChest.class, "thaumcraft:TileChestHungry");
        GameRegistry.registerTileEntity(TileCentrifuge.class, "thaumcraft:TileCentrifuge");
        GameRegistry.registerTileEntity(TileJarFillable.class, "thaumcraft:TileJar");
        GameRegistry.registerTileEntity(TileJarFillableVoid.class, "thaumcraft:TileJarVoid");
        GameRegistry.registerTileEntity(TileJarBrain.class, "thaumcraft:TileJarBrain");
        GameRegistry.registerTileEntity(TileBellows.class, "thaumcraft:TileBellows");
        GameRegistry.registerTileEntity(TileSmelter.class, "thaumcraft:TileSmelter");
        GameRegistry.registerTileEntity(TileAlembic.class, "thaumcraft:TileAlembic");
        GameRegistry.registerTileEntity(TileInfusionMatrix.class, "thaumcraft:TileInfusionMatrix");
        GameRegistry.registerTileEntity(TileWaterJug.class, "thaumcraft:TileWaterJug");
        GameRegistry.registerTileEntity(TileInfernalFurnace.class, "thaumcraft:TileInfernalFurnace");
        GameRegistry.registerTileEntity(TileThaumatorium.class, "thaumcraft:TileThaumatorium");
        GameRegistry.registerTileEntity(TileThaumatoriumTop.class, "thaumcraft:TileThaumatoriumTop");
        GameRegistry.registerTileEntity(TileSpa.class, "thaumcraft:TileSpa");
        GameRegistry.registerTileEntity(TileLampGrowth.class, "thaumcraft:TileLampGrowth");
        GameRegistry.registerTileEntity(TileLampArcane.class, "thaumcraft:TileLampArcane");
        GameRegistry.registerTileEntity(TileLampFertility.class, "thaumcraft:TileLampFertility");
        GameRegistry.registerTileEntity(TileMirror.class, "thaumcraft:TileMirror");
        GameRegistry.registerTileEntity(TileMirrorEssentia.class, "thaumcraft:TileMirrorEssentia");
        GameRegistry.registerTileEntity(TileRedstoneRelay.class, "thaumcraft:TileRedstoneRelay");
        GameRegistry.registerTileEntity(TileGolemBuilder.class, "thaumcraft:TileGolemBuilder");
        GameRegistry.registerTileEntity(TileEssentiaInput.class, "thaumcraft:TileEssentiaInput");
        GameRegistry.registerTileEntity(TileEssentiaOutput.class, "thaumcraft:TileEssentiaOutput");
        GameRegistry.registerTileEntity(TilePatternCrafter.class, "thaumcraft:TilePatternCrafter");
        GameRegistry.registerTileEntity(TilePotionSprayer.class, "thaumcraft:TilePotionSprayer");
        GameRegistry.registerTileEntity(TileVisGenerator.class, "thaumcraft:TileVisGenerator");
        GameRegistry.registerTileEntity(TileStabilizer.class, "thaumcraft:TileStabilizer");
        GameRegistry.registerTileEntity(TileCondenser.class, "thaumcraft:TileCondenser");
        GameRegistry.registerTileEntity(TileVoidSiphon.class, "thaumcraft:TileVoidSiphon");
        GameRegistry.registerTileEntity(TileBanner.class, "thaumcraft:TileBanner");
        GameRegistry.registerTileEntity(TileHole.class, "thaumcraft:TileHole");
        GameRegistry.registerTileEntity(TileBarrierStone.class, "thaumcraft:TileBarrierStone");
    }
    
    private static Block registerBlock(Block block) {
        return registerBlock(block, new ItemBlock(block));
    }
    
    private static Block registerBlock(Block block, ItemBlock itemBlock) {
        ForgeRegistries.BLOCKS.register(block);
        itemBlock.setRegistryName(block.getRegistryName());
        ForgeRegistries.ITEMS.register(itemBlock);
        Thaumcraft.proxy.registerModel(itemBlock);
        return block;
    }
    
    private static Block registerBlock(Block block, Class clazz) {
        ForgeRegistries.BLOCKS.register(block);
        try {
            ItemBlock itemBlock = (ItemBlock)clazz.getConstructors()[0].newInstance(block);
            itemBlock.setRegistryName(block.getRegistryName());
            ForgeRegistries.ITEMS.register(itemBlock);
            Thaumcraft.proxy.registerModel(itemBlock);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return block;
    }
    
    public static class FluidPure extends Fluid
    {
        public static String name = "purifying_fluid";
        public static FluidPure instance;
        
        private FluidPure() {
            super("purifying_fluid", new ResourceLocation("blocks/water_still"), new ResourceLocation("blocks/water_flow"));
            setLuminosity(5);
            setRarity(EnumRarity.RARE);
        }
        
        public int getColor() {
            return 2013252778;
        }
        
        static {
            instance = new FluidPure();
        }
    }
    
    public static class FluidDeath extends Fluid
    {
        public static String name = "liquid_death";
        public static FluidDeath instance;
        
        private FluidDeath() {
            super("liquid_death", new ResourceLocation("thaumcraft:blocks/animatedglow"), new ResourceLocation("thaumcraft:blocks/animatedglow"));
            setViscosity(1500);
            setRarity(EnumRarity.RARE);
        }
        
        public int getColor() {
            return -263978855;
        }
        
        static {
            instance = new FluidDeath();
        }
    }
    
    public static class FluidFluxGoo extends Fluid
    {
        public static String name = "flux_goo";
        public static FluidFluxGoo instance;
        
        private FluidFluxGoo() {
            super("flux_goo", new ResourceLocation("thaumcraft:blocks/flux_goo"), new ResourceLocation("thaumcraft:blocks/flux_goo"));
            setViscosity(6000);
            setDensity(8);
        }
        
        static {
            instance = new FluidFluxGoo();
        }
    }
}
