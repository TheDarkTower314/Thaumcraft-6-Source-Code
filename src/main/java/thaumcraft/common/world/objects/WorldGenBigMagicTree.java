package thaumcraft.common.world.objects;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.IPlantable;


public class WorldGenBigMagicTree extends WorldGenAbstractTree
{
    private Random rand;
    private World world;
    private BlockPos basePos;
    int heightLimit;
    int height;
    double heightAttenuation;
    double branchSlope;
    double scaleWidth;
    double leafDensity;
    int trunkSize;
    int heightLimitLimit;
    int leafDistanceLimit;
    List<FoliageCoordinates> foliageCoords;
    private static String __OBFID = "CL_00000400";
    
    public WorldGenBigMagicTree(boolean p_i2008_1_) {
        super(p_i2008_1_);
        basePos = BlockPos.ORIGIN;
        heightAttenuation = 0.6618;
        branchSlope = 0.381;
        scaleWidth = 1.25;
        leafDensity = 0.9;
        trunkSize = 1;
        heightLimitLimit = 11;
        leafDistanceLimit = 4;
    }
    
    void generateLeafNodeList() {
        height = (int)(heightLimit * heightAttenuation);
        if (height >= heightLimit) {
            height = heightLimit - 1;
        }
        int i = (int)(1.382 + Math.pow(leafDensity * heightLimit / 13.0, 2.0));
        if (i < 1) {
            i = 1;
        }
        int j = basePos.getY() + height;
        int k = heightLimit - leafDistanceLimit;
        (foliageCoords = Lists.newArrayList()).add(new FoliageCoordinates(basePos.up(k), j));
        while (k >= 0) {
            float f = layerSize(k);
            if (f >= 0.0f) {
                for (int l = 0; l < i; ++l) {
                    double d0 = scaleWidth * f * (rand.nextFloat() + 0.328);
                    double d2 = rand.nextFloat() * 2.0f * 3.141592653589793;
                    double d3 = d0 * Math.sin(d2) + 0.5;
                    double d4 = d0 * Math.cos(d2) + 0.5;
                    BlockPos blockpos = basePos.add(d3, k - 1, d4);
                    BlockPos blockpos2 = blockpos.up(leafDistanceLimit);
                    if (checkBlockLine(blockpos, blockpos2) == -1) {
                        int i2 = basePos.getX() - blockpos.getX();
                        int j2 = basePos.getZ() - blockpos.getZ();
                        double d5 = blockpos.getY() - Math.sqrt(i2 * i2 + j2 * j2) * branchSlope;
                        int k2 = (d5 > j) ? j : ((int)d5);
                        BlockPos blockpos3 = new BlockPos(basePos.getX(), k2, basePos.getZ());
                        if (checkBlockLine(blockpos3, blockpos) == -1) {
                            foliageCoords.add(new FoliageCoordinates(blockpos, blockpos3.getY()));
                        }
                    }
                }
            }
            --k;
        }
    }
    
    void crosSection(BlockPos p_181631_1_, float p_181631_2_, IBlockState p_181631_3_) {
        for (int i = (int)(p_181631_2_ + 0.618), j = -i; j <= i; ++j) {
            for (int k = -i; k <= i; ++k) {
                if (Math.pow(Math.abs(j) + 0.5, 2.0) + Math.pow(Math.abs(k) + 0.5, 2.0) <= p_181631_2_ * p_181631_2_) {
                    BlockPos blockpos = p_181631_1_.add(j, 0, k);
                    IBlockState state = world.getBlockState(blockpos);
                    if (state.getBlock().isAir(state, world, blockpos) || state.getBlock().isLeaves(state, world, blockpos)) {
                        setBlockAndNotifyAdequately(world, blockpos, p_181631_3_);
                    }
                }
            }
        }
    }
    
    float layerSize(int p_76490_1_) {
        if (p_76490_1_ < heightLimit * 0.3f) {
            return -1.0f;
        }
        float f = heightLimit / 2.0f;
        float f2 = f - p_76490_1_;
        float f3 = MathHelper.sqrt(f * f - f2 * f2);
        if (f2 == 0.0f) {
            f3 = f;
        }
        else if (Math.abs(f2) >= f) {
            return 0.0f;
        }
        return f3 * 0.5f;
    }
    
    float leafSize(int p_76495_1_) {
        return (p_76495_1_ >= 0 && p_76495_1_ < leafDistanceLimit) ? ((p_76495_1_ != 0 && p_76495_1_ != leafDistanceLimit - 1) ? 3.0f : 2.0f) : -1.0f;
    }
    
    void generateLeafNode(BlockPos pos) {
        for (int i = 0; i < leafDistanceLimit; ++i) {
            crosSection(pos.up(i), leafSize(i), Blocks.LEAVES.getDefaultState().withProperty((IProperty)BlockLeaves.CHECK_DECAY, (Comparable)false));
        }
    }
    
    void limb(BlockPos p_175937_1_, BlockPos p_175937_2_, Block p_175937_3_) {
        BlockPos blockpos2 = p_175937_2_.add(-p_175937_1_.getX(), -p_175937_1_.getY(), -p_175937_1_.getZ());
        int i = getGreatestDistance(blockpos2);
        float f = blockpos2.getX() / (float)i;
        float f2 = blockpos2.getY() / (float)i;
        float f3 = blockpos2.getZ() / (float)i;
        for (int j = 0; j <= i; ++j) {
            BlockPos blockpos3 = p_175937_1_.add(0.5f + j * f, 0.5f + j * f2, 0.5f + j * f3);
            BlockLog.EnumAxis enumaxis = getLogAxis(p_175937_1_, blockpos3);
            setBlockAndNotifyAdequately(world, blockpos3, p_175937_3_.getDefaultState().withProperty((IProperty)BlockLog.LOG_AXIS, (Comparable)enumaxis));
        }
    }
    
    private int getGreatestDistance(BlockPos p_175935_1_) {
        int i = MathHelper.abs(p_175935_1_.getX());
        int j = MathHelper.abs(p_175935_1_.getY());
        int k = MathHelper.abs(p_175935_1_.getZ());
        return (k > i && k > j) ? k : ((j > i) ? j : i);
    }
    
    private BlockLog.EnumAxis getLogAxis(BlockPos p_175938_1_, BlockPos p_175938_2_) {
        BlockLog.EnumAxis enumaxis = BlockLog.EnumAxis.Y;
        int i = Math.abs(p_175938_2_.getX() - p_175938_1_.getX());
        int j = Math.abs(p_175938_2_.getZ() - p_175938_1_.getZ());
        int k = Math.max(i, j);
        if (k > 0) {
            if (i == k) {
                enumaxis = BlockLog.EnumAxis.X;
            }
            else if (j == k) {
                enumaxis = BlockLog.EnumAxis.Z;
            }
        }
        return enumaxis;
    }
    
    void generateLeaves() {
        for (FoliageCoordinates foliagecoordinates : foliageCoords) {
            generateLeafNode(foliagecoordinates);
        }
    }
    
    boolean leafNodeNeedsBase(int p_76493_1_) {
        return p_76493_1_ >= heightLimit * 0.2;
    }
    
    void generateTrunk() {
        BlockPos blockpos = basePos;
        BlockPos blockpos2 = basePos.up(height);
        Block block = Blocks.LOG;
        limb(blockpos, blockpos2, block);
        if (trunkSize == 2) {
            limb(blockpos.east(), blockpos2.east(), block);
            limb(blockpos.east().south(), blockpos2.east().south(), block);
            limb(blockpos.south(), blockpos2.south(), block);
        }
    }
    
    void generateLeafNodeBases() {
        for (FoliageCoordinates foliagecoordinates : foliageCoords) {
            int i = foliagecoordinates.getBranchBase();
            BlockPos blockpos = new BlockPos(basePos.getX(), i, basePos.getZ());
            if (leafNodeNeedsBase(i - basePos.getY())) {
                limb(blockpos, foliagecoordinates, Blocks.LOG);
            }
        }
    }
    
    int checkBlockLine(BlockPos p_175936_1_, BlockPos p_175936_2_) {
        BlockPos blockpos2 = p_175936_2_.add(-p_175936_1_.getX(), -p_175936_1_.getY(), -p_175936_1_.getZ());
        int i = getGreatestDistance(blockpos2);
        float f = blockpos2.getX() / (float)i;
        float f2 = blockpos2.getY() / (float)i;
        float f3 = blockpos2.getZ() / (float)i;
        if (i == 0) {
            return -1;
        }
        for (int j = 0; j <= i; ++j) {
            BlockPos blockpos3 = p_175936_1_.add(0.5f + j * f, 0.5f + j * f2, 0.5f + j * f3);
            if (!isReplaceable(world, blockpos3)) {
                return j;
            }
        }
        return -1;
    }
    
    public void setDecorationDefaults() {
        leafDistanceLimit = 4;
    }
    
    public boolean generate(World worldIn, Random p_180709_2_, BlockPos p_180709_3_) {
        world = worldIn;
        basePos = p_180709_3_;
        rand = new Random(p_180709_2_.nextLong());
        if (heightLimit == 0) {
            heightLimit = 11 + rand.nextInt(heightLimitLimit);
        }
        if (!validTreeLocation()) {
            world = null;
            return false;
        }
        generateLeafNodeList();
        generateLeaves();
        generateTrunk();
        generateLeafNodeBases();
        world = null;
        return true;
    }
    
    private boolean validTreeLocation() {
        BlockPos down = basePos.down();
        IBlockState state = world.getBlockState(down);
        boolean isSoil = state.getBlock().canSustainPlant(state, world, down, EnumFacing.UP, (IPlantable)Blocks.SAPLING);
        if (!isSoil) {
            return false;
        }
        int i = checkBlockLine(basePos, basePos.up(heightLimit - 1));
        if (i == -1) {
            return true;
        }
        if (i < 6) {
            return false;
        }
        heightLimit = i;
        return true;
    }
    
    static class FoliageCoordinates extends BlockPos
    {
        private int branchBase;
        private static String __OBFID = "CL_00002001";
        
        public FoliageCoordinates(BlockPos p_i45635_1_, int p_i45635_2_) {
            super(p_i45635_1_.getX(), p_i45635_1_.getY(), p_i45635_1_.getZ());
            branchBase = p_i45635_2_;
        }
        
        public int getBranchBase() {
            return branchBase;
        }
    }
}
