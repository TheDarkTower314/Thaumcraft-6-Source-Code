// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.world;

import thaumcraft.common.world.objects.WorldGenSilverwoodTrees;
import net.minecraftforge.common.BiomeDictionary;
import thaumcraft.common.world.objects.WorldGenGreatwoodTrees;
import thaumcraft.common.world.objects.WorldGenCustomFlowers;
import net.minecraft.block.Block;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.blocks.world.ore.ShardType;
import net.minecraft.world.IBlockAccess;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.Entity;
import thaumcraft.common.entities.monster.cult.EntityCultistPortalLesser;
import net.minecraft.block.material.Material;
import thaumcraft.common.world.objects.WorldGenMound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.world.biomes.BiomeHandler;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.world.aura.AuraHandler;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.World;
import java.util.Random;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import com.google.common.base.Predicate;
import net.minecraftforge.fml.common.IWorldGenerator;

public class ThaumcraftWorldGenerator implements IWorldGenerator
{
    public static ThaumcraftWorldGenerator INSTANCE;
    private final Predicate<IBlockState> predicate;
    
    public ThaumcraftWorldGenerator() {
        this.predicate = BlockMatcher.forBlock(Blocks.STONE);
    }
    
    public void initialize() {
    }
    
    public void generate(final Random random, final int chunkX, final int chunkZ, final World world, final IChunkGenerator chunkGenerator, final IChunkProvider chunkProvider) {
        this.worldGeneration(random, chunkX, chunkZ, world, true);
        AuraHandler.generateAura(chunkProvider.provideChunk(chunkX, chunkZ), random);
    }
    
    public void worldGeneration(final Random random, final int chunkX, final int chunkZ, final World world, final boolean newGen) {
        if (world.provider.getDimension() == ModConfig.CONFIG_WORLD.dimensionOuterId) {
            world.getChunkFromChunkCoords(chunkX, chunkZ).markDirty();
        }
        else {
            this.generateAll(world, random, chunkX, chunkZ, newGen);
            if (world.provider.getDimension() == -1) {
                this.generateNether(world, random, chunkX, chunkZ, newGen);
            }
            else if (world.provider.getDimension() == ModConfig.CONFIG_WORLD.overworldDim) {
                this.generateSurface(world, random, chunkX, chunkZ, newGen);
            }
            if (!newGen) {
                world.getChunkFromChunkCoords(chunkX, chunkZ).markDirty();
            }
        }
    }
    
    private void generateSurface(final World world, final Random random, final int chunkX, final int chunkZ, final boolean newGen) {
        final int blacklist = BiomeHandler.getDimBlacklist(world.provider.getDimension());
        if (blacklist == -1 && ModConfig.CONFIG_WORLD.generateStructure && world.provider.getDimension() == ModConfig.CONFIG_WORLD.overworldDim && !world.getWorldInfo().getTerrainType().getName().startsWith("flat") && (newGen || ModConfig.CONFIG_WORLD.regenStructure)) {
            final int randPosX = chunkX * 16 + 8 + MathHelper.getInt(random, -4, 4);
            final int randPosZ = chunkZ * 16 + 8 + MathHelper.getInt(random, -4, 4);
            final BlockPos p = world.getPrecipitationHeight(new BlockPos(randPosX, 0, randPosZ)).down(9);
            if (p.getY() < world.getActualHeight()) {
                if (random.nextInt(100) == 0) {
                    final WorldGenerator mound = new WorldGenMound();
                    mound.generate(world, random, p);
                }
                else if (random.nextInt(500) == 0) {
                    final BlockPos p2 = p.up(8);
                    final IBlockState bs = world.getBlockState(p2);
                    if (bs.getMaterial() == Material.GROUND || bs.getMaterial() == Material.ROCK || bs.getMaterial() == Material.SAND || bs.getMaterial() == Material.SNOW) {
                        final EntityCultistPortalLesser eg = new EntityCultistPortalLesser(world);
                        eg.setPosition(p2.getX() + 0.5, p2.getY() + 1, p2.getZ() + 0.5);
                        eg.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(eg)), null);
                        world.spawnEntity(eg);
                    }
                }
            }
        }
    }
    
    private void generateNodes(final World world, final Random random, final int chunkX, final int chunkZ, final boolean newGen, final int blacklist) {
        if (blacklist != 0 && blacklist != 2 && ModConfig.CONFIG_WORLD.generateAura && (newGen || ModConfig.CONFIG_WORLD.regenAura)) {
            BlockPos var7 = null;
            try {
                var7 = new MapGenScatteredFeature().getNearestStructurePos(world, world.getHeight(new BlockPos(chunkX * 16 + 8, 64, chunkZ * 16 + 8)), true);
            }
            catch (final Exception ex) {}
        }
    }
    
    private void generateVegetation(final World world, final Random random, final int chunkX, final int chunkZ, final boolean newGen) {
        final Biome bgb = world.getBiome(new BlockPos(chunkX * 16 + 8, 50, chunkZ * 16 + 8));
        if (BiomeHandler.getBiomeBlacklist(Biome.getIdForBiome(bgb)) != -1) {
            return;
        }
        if (random.nextInt(80) == 3) {
            generateSilverwood(world, random, chunkX, chunkZ);
        }
        if (random.nextInt(25) == 7) {
            generateGreatwood(world, random, chunkX, chunkZ);
        }
        final int randPosX = chunkX * 16 + 8;
        final int randPosZ = chunkZ * 16 + 8;
        final BlockPos bp = world.getHeight(new BlockPos(randPosX, 0, randPosZ));
        if (world.getBiome(bp).topBlock.getBlock() == Blocks.SAND && world.getBiome(bp).getTemperature(bp) > 1.0f && random.nextInt(30) == 0) {
            generateFlowers(world, random, bp, BlocksTC.cinderpearl, 0);
        }
    }
    
    private void generateOres(final World world, final Random random, final int chunkX, final int chunkZ, final boolean newGen) {
        final Biome bgb = world.getBiome(new BlockPos(chunkX * 16 + 8, 50, chunkZ * 16 + 8));
        if (BiomeHandler.getBiomeBlacklist(Biome.getIdForBiome(bgb)) == 0 || BiomeHandler.getBiomeBlacklist(Biome.getIdForBiome(bgb)) == 2) {
            return;
        }
        final float density = ModConfig.CONFIG_WORLD.oreDensity / 100.0f;
        if (world.provider.getDimension() == -1) {
            return;
        }
        if (ModConfig.CONFIG_WORLD.generateCinnabar && (newGen || ModConfig.CONFIG_WORLD.regenCinnabar)) {
            for (int i = 0; i < Math.round(18.0f * density); ++i) {
                final int randPosX = chunkX * 16 + 8 + MathHelper.getInt(random, -6, 6);
                final int randPosY = random.nextInt(world.getHeight() / 5);
                final int randPosZ = chunkZ * 16 + 8 + MathHelper.getInt(random, -6, 6);
                final BlockPos pos = new BlockPos(randPosX, randPosY, randPosZ);
                final IBlockState block = world.getBlockState(pos);
                if (block.getBlock().isReplaceableOreGen(block, world, pos, this.predicate)) {
                    world.setBlockState(pos, BlocksTC.oreCinnabar.getDefaultState(), 2);
                }
            }
        }
        if (ModConfig.CONFIG_WORLD.generateQuartz && (newGen || ModConfig.CONFIG_WORLD.regenQuartz)) {
            for (int i = 0; i < Math.round(18.0f * density); ++i) {
                final int randPosX = chunkX * 16 + 8 + MathHelper.getInt(random, -6, 6);
                final int randPosY = random.nextInt(world.getHeight() / 4);
                final int randPosZ = chunkZ * 16 + 8 + MathHelper.getInt(random, -6, 6);
                final BlockPos pos = new BlockPos(randPosX, randPosY, randPosZ);
                final IBlockState block = world.getBlockState(pos);
                if (block.getBlock().isReplaceableOreGen(block, world, pos, this.predicate)) {
                    world.setBlockState(pos, BlocksTC.oreQuartz.getDefaultState(), 2);
                }
            }
        }
        if (ModConfig.CONFIG_WORLD.generateAmber && (newGen || ModConfig.CONFIG_WORLD.regenAmber)) {
            for (int i = 0; i < Math.round(20.0f * density); ++i) {
                final int randPosX = chunkX * 16 + 8 + MathHelper.getInt(random, -6, 6);
                final int randPosZ2 = chunkZ * 16 + 8 + MathHelper.getInt(random, -6, 6);
                final int randPosY2 = world.getHeight(new BlockPos(randPosX, 0, randPosZ2)).getY() - random.nextInt(25);
                final BlockPos pos = new BlockPos(randPosX, randPosY2, randPosZ2);
                final IBlockState block = world.getBlockState(pos);
                if (block.getBlock().isReplaceableOreGen(block, world, pos, this.predicate)) {
                    world.setBlockState(pos, BlocksTC.oreAmber.getDefaultState(), 2);
                }
            }
        }
        if (ModConfig.CONFIG_WORLD.generateCrystals && (newGen || ModConfig.CONFIG_WORLD.regenCrystals)) {
            int t = 8;
            final int maxCrystals = Math.round(64.0f * density);
            int cc = 0;
            if (world.provider.getDimension() == -1) {
                t = 1;
            }
            for (int j = 0; j < Math.round(t * density); ++j) {
                final int randPosX2 = chunkX * 16 + 8 + MathHelper.getInt(random, -6, 6);
                final int randPosZ3 = chunkZ * 16 + 8 + MathHelper.getInt(random, -6, 6);
                final int randPosY3 = random.nextInt(Math.max(5, world.getHeight(new BlockPos(randPosX2, 0, randPosZ3)).getY() - 5));
                final BlockPos bp = new BlockPos(randPosX2, randPosY3, randPosZ3);
                int md = random.nextInt(6);
                if (random.nextInt(3) == 0) {
                    final Aspect tag = BiomeHandler.getRandomBiomeTag(Biome.getIdForBiome(world.getBiome(bp)), random);
                    if (tag == null) {
                        md = random.nextInt(6);
                    }
                    else {
                        md = ShardType.getMetaByAspect(tag);
                    }
                }
                final Block oreBlock = ShardType.byMetadata(md).getOre();
                for (int xx = -1; xx <= 1; ++xx) {
                    for (int yy = -1; yy <= 1; ++yy) {
                        for (int zz = -1; zz <= 1; ++zz) {
                            if (random.nextInt(3) != 0) {
                                final IBlockState bs = world.getBlockState(bp.add(xx, yy, zz));
                                final Material bm = bs.getMaterial();
                                if (!bm.isLiquid() && (world.isAirBlock(bp.add(xx, yy, zz)) || bs.getBlock().isReplaceable(world, bp.add(xx, yy, zz))) && BlockUtils.isBlockTouching(world, bp.add(xx, yy, zz), Material.ROCK, true)) {
                                    final int amt = 1 + random.nextInt(3);
                                    world.setBlockState(bp.add(xx, yy, zz), oreBlock.getStateFromMeta(amt), 0);
                                    cc += amt;
                                }
                            }
                        }
                    }
                }
                if (cc > maxCrystals) {
                    break;
                }
            }
        }
    }
    
    private void generateAll(final World world, final Random random, final int chunkX, final int chunkZ, final boolean newGen) {
        final boolean auraGen = false;
        final int blacklist = BiomeHandler.getDimBlacklist(world.provider.getDimension());
        if (blacklist == -1 && ModConfig.CONFIG_WORLD.generateTrees && !world.getWorldInfo().getTerrainType().getName().startsWith("flat") && (newGen || ModConfig.CONFIG_WORLD.regenTrees)) {
            this.generateVegetation(world, random, chunkX, chunkZ, newGen);
        }
        if (blacklist != 0 && blacklist != 2) {
            this.generateOres(world, random, chunkX, chunkZ, newGen);
        }
    }
    
    private void generateNether(final World world, final Random random, final int chunkX, final int chunkZ, final boolean newGen) {
        final boolean auraGen = false;
    }
    
    public static boolean generateFlowers(final World world, final Random random, final BlockPos pos, final Block block, final int md) {
        final WorldGenerator flowers = new WorldGenCustomFlowers(block, md);
        return flowers.generate(world, random, pos);
    }
    
    public static boolean generateGreatwood(final World world, final Random random, final int chunkX, final int chunkZ) {
        final int x = chunkX * 16 + 8 + MathHelper.getInt(random, -4, 4);
        final int z = chunkZ * 16 + 8 + MathHelper.getInt(random, -4, 4);
        final BlockPos bp = world.getPrecipitationHeight(new BlockPos(x, 0, z));
        final int bio = Biome.getIdForBiome(world.getBiome(bp));
        if (BiomeHandler.getBiomeSupportsGreatwood(bio) > random.nextFloat()) {
            final boolean t = new WorldGenGreatwoodTrees(false, random.nextInt(8) == 0).generate(world, random, bp);
            return t;
        }
        return false;
    }
    
    public static boolean generateSilverwood(final World world, final Random random, final int chunkX, final int chunkZ) {
        final int x = chunkX * 16 + 8 + MathHelper.getInt(random, -4, 4);
        final int z = chunkZ * 16 + 8 + MathHelper.getInt(random, -4, 4);
        final BlockPos bp = world.getPrecipitationHeight(new BlockPos(x, 0, z));
        final Biome bio = world.getBiome(bp);
        final int bi = Biome.getIdForBiome(world.getBiome(bp));
        if (BiomeHandler.getBiomeSupportsGreatwood(bi) / 2.0f > random.nextFloat() || (!bio.equals(BiomeHandler.MAGICAL_FOREST) && BiomeDictionary.hasType(bio, BiomeDictionary.Type.MAGICAL)) || bio == Biome.getBiome(18) || bio == Biome.getBiome(28)) {
            final boolean t = new WorldGenSilverwoodTrees(false, 7, 4).generate(world, random, bp);
            return t;
        }
        return false;
    }
    
    static {
        ThaumcraftWorldGenerator.INSTANCE = new ThaumcraftWorldGenerator();
    }
}
