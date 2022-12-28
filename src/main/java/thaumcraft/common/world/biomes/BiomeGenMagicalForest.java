package thaumcraft.common.world.biomes;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.world.objects.WorldGenBigMagicTree;
import thaumcraft.common.world.objects.WorldGenGreatwoodTrees;
import thaumcraft.common.world.objects.WorldGenSilverwoodTrees;


public class BiomeGenMagicalForest extends Biome
{
    protected WorldGenBigMagicTree bigTree;
    private static WorldGenBlockBlob blobs;
    
    public BiomeGenMagicalForest(Biome.BiomeProperties par1) {
        super(par1);
        setRegistryName("thaumcraft", "magical_forest");
        bigTree = new WorldGenBigMagicTree(false);
        spawnableCreatureList.add(new Biome.SpawnListEntry(EntityWolf.class, 2, 1, 3));
        spawnableCreatureList.add(new Biome.SpawnListEntry(EntityHorse.class, 2, 1, 3));
        spawnableMonsterList.add(new Biome.SpawnListEntry(EntityWitch.class, 3, 1, 1));
        spawnableMonsterList.add(new Biome.SpawnListEntry(EntityEnderman.class, 3, 1, 1));
        spawnableMonsterList.add(new Biome.SpawnListEntry(EntityVex.class, 1, 1, 1));
        if (ModConfig.CONFIG_WORLD.allowSpawnPech) {
            spawnableMonsterList.add(new Biome.SpawnListEntry(EntityPech.class, 20, 1, 2));
        }
        if (ModConfig.CONFIG_WORLD.allowSpawnWisp) {
            spawnableMonsterList.add(new Biome.SpawnListEntry(EntityWisp.class, 20, 1, 2));
        }
        decorator.treesPerChunk = 2;
        decorator.flowersPerChunk = 10;
        decorator.grassPerChunk = 12;
        decorator.waterlilyPerChunk = 6;
        decorator.mushroomsPerChunk = 6;
    }
    
    public WorldGenAbstractTree getRandomTreeFeature(Random par1Random) {
        return (par1Random.nextInt(18) == 0) ? new WorldGenSilverwoodTrees(false, 8, 5) : ((par1Random.nextInt(12) == 0) ? new WorldGenGreatwoodTrees(false, par1Random.nextInt(8) == 0) : bigTree);
    }
    
    public WorldGenerator getRandomWorldGenForGrass(Random par1Random) {
        return (par1Random.nextInt(4) == 0) ? new WorldGenTallGrass(BlockTallGrass.EnumType.FERN) : new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
    }
    
    @SideOnly(Side.CLIENT)
    public int getGrassColorAtPos(BlockPos p_180627_1_) {
        return ModConfig.CONFIG_GRAPHICS.blueBiome ? 6728396 : 5635969;
    }
    
    @SideOnly(Side.CLIENT)
    public int getFoliageColorAtPos(BlockPos p_180625_1_) {
        return ModConfig.CONFIG_GRAPHICS.blueBiome ? 7851246 : 6750149;
    }
    
    public int getWaterColorMultiplier() {
        return 30702;
    }
    
    public void decorate(World world, Random random, BlockPos pos) {
        for (int a = 0; a < 3; ++a) {
            BlockPos pp;
            for (pp = new BlockPos(pos), pp = pp.add(4 + random.nextInt(8), 0, 4 + random.nextInt(8)), pp = world.getHeight(pp); pp.getY() > 30 && world.getBlockState(pp).getBlock() != Blocks.GRASS; pp = pp.down()) {}
            Block l1 = world.getBlockState(pp).getBlock();
            if (l1 == Blocks.GRASS) {
                world.setBlockState(pp, BlocksTC.grassAmbient.getDefaultState(), 2);
                break;
            }
        }
        for (int k = random.nextInt(3), i = 0; i < k; ++i) {
            BlockPos p2 = new BlockPos(pos);
            p2 = p2.add(random.nextInt(16) + 8, 0, random.nextInt(16) + 8);
            p2 = world.getHeight(p2);
            BiomeGenMagicalForest.blobs.generate(world, random, p2);
        }
        for (int k = 0; k < 4; ++k) {
            for (int i = 0; i < 4; ++i) {
                if (random.nextInt(40) == 0) {
                    BlockPos p2 = new BlockPos(pos);
                    p2 = p2.add(k * 4 + 1 + 8 + random.nextInt(3), 0, i * 4 + 1 + 8 + random.nextInt(3));
                    p2 = world.getHeight(p2);
                    WorldGenBigMushroom worldgenbigmushroom = new WorldGenBigMushroom();
                    worldgenbigmushroom.generate(world, random, p2);
                }
            }
        }
        try {
            super.decorate(world, random, pos);
        }
        catch (Exception ex) {}
        for (int a = 0; a < 8; ++a) {
            BlockPos p3;
            for (p3 = new BlockPos(pos), p3 = p3.add(random.nextInt(16), 0, random.nextInt(16)), p3 = world.getHeight(p3); p3.getY() > 50 && world.getBlockState(p3).getBlock() != Blocks.GRASS; p3 = p3.down()) {}
            Block l2 = world.getBlockState(p3).getBlock();
            if (l2 == Blocks.GRASS && world.getBlockState(p3.up()).getBlock().isReplaceable(world, p3.up()) && isBlockAdjacentToWood(world, p3.up())) {
                world.setBlockState(p3.up(), BlocksTC.vishroom.getDefaultState(), 2);
            }
        }
    }
    
    private boolean isBlockAdjacentToWood(IBlockAccess world, BlockPos pos) {
        int count = 0;
        for (int xx = -1; xx <= 1; ++xx) {
            for (int yy = -1; yy <= 1; ++yy) {
                for (int zz = -1; zz <= 1; ++zz) {
                    if (xx != 0 || yy != 0 || zz != 0) {
                        if (Utils.isWoodLog(world, pos.add(xx, yy, zz))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
        double d0 = MathHelper.clamp((1.0 + BiomeGenMagicalForest.GRASS_COLOR_NOISE.getValue(pos.getX() / 48.0, pos.getZ() / 48.0)) / 2.0, 0.0, 0.9999);
        return BlockFlower.EnumFlowerType.values()[(int)(d0 * BlockFlower.EnumFlowerType.values().length)];
    }
    
    static {
        blobs = new WorldGenBlockBlob(Blocks.MOSSY_COBBLESTONE, 0);
    }
}
