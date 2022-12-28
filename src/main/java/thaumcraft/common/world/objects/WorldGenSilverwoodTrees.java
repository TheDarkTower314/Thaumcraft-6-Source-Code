package thaumcraft.common.world.objects;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.IPlantable;
import thaumcraft.api.blocks.BlocksTC;


public class WorldGenSilverwoodTrees extends WorldGenAbstractTree
{
    private int minTreeHeight;
    private int randomTreeHeight;
    boolean worldgen;
    
    public WorldGenSilverwoodTrees(boolean doBlockNotify, int minTreeHeight, int randomTreeHeight) {
        super(doBlockNotify);
        worldgen = false;
        worldgen = !doBlockNotify;
        this.minTreeHeight = minTreeHeight;
        this.randomTreeHeight = randomTreeHeight;
    }
    
    public boolean generate(World world, Random random, BlockPos pos) {
        int height = random.nextInt(randomTreeHeight) + minTreeHeight;
        boolean flag = true;
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (y < 1 || y + height + 1 > 256) {
            return false;
        }
        for (int i1 = y; i1 <= y + 1 + height; ++i1) {
            byte spread = 1;
            if (i1 == y) {
                spread = 0;
            }
            if (i1 >= y + 1 + height - 2) {
                spread = 3;
            }
            for (int j1 = x - spread; j1 <= x + spread && flag; ++j1) {
                for (int k1 = z - spread; k1 <= z + spread && flag; ++k1) {
                    if (i1 >= 0 && i1 < 256) {
                        IBlockState state = world.getBlockState(new BlockPos(j1, i1, k1));
                        Block block = state.getBlock();
                        if (!block.isAir(state, world, new BlockPos(j1, i1, k1)) && !block.isLeaves(state, world, new BlockPos(j1, i1, k1)) && !block.isReplaceable(world, new BlockPos(j1, i1, k1)) && i1 > y) {
                            flag = false;
                        }
                    }
                    else {
                        flag = false;
                    }
                }
            }
        }
        if (!flag) {
            return false;
        }
        IBlockState state2 = world.getBlockState(new BlockPos(x, y - 1, z));
        Block block2 = state2.getBlock();
        boolean isSoil = block2.canSustainPlant(state2, world, new BlockPos(x, y - 1, z), EnumFacing.UP, (IPlantable)Blocks.SAPLING);
        if (isSoil && y < 256 - height - 1) {
            block2.onPlantGrow(state2, world, new BlockPos(x, y - 1, z), new BlockPos(x, y, z));
            int start = y + height - 5;
            for (int end = y + height + 3 + random.nextInt(3), k2 = start; k2 <= end; ++k2) {
                int cty = MathHelper.clamp(k2, y + height - 3, y + height);
                for (int xx = x - 5; xx <= x + 5; ++xx) {
                    for (int zz = z - 5; zz <= z + 5; ++zz) {
                        double d3 = xx - x;
                        double d4 = k2 - cty;
                        double d5 = zz - z;
                        double dist = d3 * d3 + d4 * d4 + d5 * d5;
                        IBlockState s2 = world.getBlockState(new BlockPos(xx, k2, zz));
                        if (dist < 10 + random.nextInt(8) && s2.getBlock().canBeReplacedByLeaves(s2, world, new BlockPos(xx, k2, zz))) {
                            setBlockAndNotifyAdequately(world, new BlockPos(xx, k2, zz), BlocksTC.leafSilverwood.getStateFromMeta(1));
                        }
                    }
                }
            }
            int k2;
            for (k2 = 0; k2 < height; ++k2) {
                IBlockState s3 = world.getBlockState(new BlockPos(x, y + k2, z));
                Block block3 = s3.getBlock();
                if (block3.isAir(s3, world, new BlockPos(x, y + k2, z)) || block3.isLeaves(s3, world, new BlockPos(x, y + k2, z)) || block3.isReplaceable(world, new BlockPos(x, y + k2, z))) {
                    setBlockAndNotifyAdequately(world, new BlockPos(x, y + k2, z), BlocksTC.logSilverwood.getStateFromMeta(1));
                    setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y + k2, z), BlocksTC.logSilverwood.getStateFromMeta(1));
                    setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y + k2, z), BlocksTC.logSilverwood.getStateFromMeta(1));
                    setBlockAndNotifyAdequately(world, new BlockPos(x, y + k2, z - 1), BlocksTC.logSilverwood.getStateFromMeta(1));
                    setBlockAndNotifyAdequately(world, new BlockPos(x, y + k2, z + 1), BlocksTC.logSilverwood.getStateFromMeta(1));
                }
            }
            setBlockAndNotifyAdequately(world, new BlockPos(x, y + k2, z), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y, z - 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y, z + 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y, z + 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y, z - 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            if (random.nextInt(3) != 0) {
                setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y + 1, z - 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            }
            if (random.nextInt(3) != 0) {
                setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y + 1, z + 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            }
            if (random.nextInt(3) != 0) {
                setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y + 1, z + 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            }
            if (random.nextInt(3) != 0) {
                setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y + 1, z - 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            }
            setBlockAndNotifyAdequately(world, new BlockPos(x - 2, y, z), BlocksTC.logSilverwood.getStateFromMeta(0));
            setBlockAndNotifyAdequately(world, new BlockPos(x + 2, y, z), BlocksTC.logSilverwood.getStateFromMeta(0));
            setBlockAndNotifyAdequately(world, new BlockPos(x, y, z - 2), BlocksTC.logSilverwood.getStateFromMeta(2));
            setBlockAndNotifyAdequately(world, new BlockPos(x, y, z + 2), BlocksTC.logSilverwood.getStateFromMeta(2));
            setBlockAndNotifyAdequately(world, new BlockPos(x - 2, y - 1, z), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x + 2, y - 1, z), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x, y - 1, z - 2), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x, y - 1, z + 2), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y + (height - 4), z - 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y + (height - 4), z + 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y + (height - 4), z + 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y + (height - 4), z - 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            if (random.nextInt(3) == 0) {
                setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y + (height - 5), z - 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            }
            if (random.nextInt(3) == 0) {
                setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y + (height - 5), z + 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            }
            if (random.nextInt(3) == 0) {
                setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y + (height - 5), z + 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            }
            if (random.nextInt(3) == 0) {
                setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y + (height - 5), z - 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            }
            setBlockAndNotifyAdequately(world, new BlockPos(x - 2, y + (height - 4), z), BlocksTC.logSilverwood.getStateFromMeta(0));
            setBlockAndNotifyAdequately(world, new BlockPos(x + 2, y + (height - 4), z), BlocksTC.logSilverwood.getStateFromMeta(0));
            setBlockAndNotifyAdequately(world, new BlockPos(x, y + (height - 4), z - 2), BlocksTC.logSilverwood.getStateFromMeta(2));
            setBlockAndNotifyAdequately(world, new BlockPos(x, y + (height - 4), z + 2), BlocksTC.logSilverwood.getStateFromMeta(2));
            if (worldgen) {
                WorldGenerator flowers = new WorldGenCustomFlowers(BlocksTC.shimmerleaf, 0);
                flowers.generate(world, random, new BlockPos(x, y, z));
            }
            return true;
        }
        return false;
    }
    
    protected void setBlockAndNotifyAdequately(World worldIn, BlockPos pos, IBlockState state) {
        IBlockState bs = worldIn.getBlockState(pos);
        if (worldIn.isAirBlock(pos) || bs.getBlock().isLeaves(bs, worldIn, pos) || bs.getBlock().isReplaceable(worldIn, pos)) {
            super.setBlockAndNotifyAdequately(worldIn, pos, state);
        }
    }
}
