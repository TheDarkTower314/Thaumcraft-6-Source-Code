package thaumcraft.common.world.objects;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.IPlantable;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.lib.utils.BlockUtils;


public class WorldGenGreatwoodTrees extends WorldGenAbstractTree
{
    static byte[] otherCoordPairs;
    Random rand;
    World world;
    int[] basePos;
    int heightLimit;
    int height;
    double heightAttenuation;
    double branchDensity;
    double branchSlope;
    double scaleWidth;
    double leafDensity;
    int trunkSize;
    int heightLimitLimit;
    int leafDistanceLimit;
    int[][] leafNodes;
    boolean spiders;
    
    public WorldGenGreatwoodTrees(boolean par1, boolean spiders) {
        super(par1);
        rand = new Random();
        basePos = new int[] { 0, 0, 0 };
        heightLimit = 0;
        heightAttenuation = 0.618;
        branchDensity = 1.0;
        branchSlope = 0.38;
        scaleWidth = 1.2;
        leafDensity = 0.9;
        trunkSize = 2;
        heightLimitLimit = 11;
        leafDistanceLimit = 4;
        this.spiders = false;
        this.spiders = spiders;
    }
    
    void generateLeafNodeList() {
        height = (int)(heightLimit * heightAttenuation);
        if (height >= heightLimit) {
            height = heightLimit - 1;
        }
        int var1 = (int)(1.382 + Math.pow(leafDensity * heightLimit / 13.0, 2.0));
        if (var1 < 1) {
            var1 = 1;
        }
        int[][] var2 = new int[var1 * heightLimit][4];
        int var3 = basePos[1] + heightLimit - leafDistanceLimit;
        int var4 = 1;
        int var5 = basePos[1] + height;
        int var6 = var3 - basePos[1];
        var2[0][0] = basePos[0];
        var2[0][1] = var3;
        var2[0][2] = basePos[2];
        var2[0][3] = var5;
        --var3;
        while (var6 >= 0) {
            int var7 = 0;
            float var8 = layerSize(var6);
            if (var8 < 0.0f) {
                --var3;
                --var6;
            }
            else {
                double var9 = 0.5;
                while (var7 < var1) {
                    double var10 = scaleWidth * var8 * (rand.nextFloat() + 0.328);
                    double var11 = rand.nextFloat() * 2.0 * 3.141592653589793;
                    int var12 = MathHelper.floor(var10 * Math.sin(var11) + basePos[0] + var9);
                    int var13 = MathHelper.floor(var10 * Math.cos(var11) + basePos[2] + var9);
                    int[] var14 = { var12, var3, var13 };
                    int[] var15 = { var12, var3 + leafDistanceLimit, var13 };
                    if (checkBlockLine(var14, var15) == -1) {
                        int[] var16 = { basePos[0], basePos[1], basePos[2] };
                        double var17 = Math.sqrt(Math.pow(Math.abs(basePos[0] - var14[0]), 2.0) + Math.pow(Math.abs(basePos[2] - var14[2]), 2.0));
                        double var18 = var17 * branchSlope;
                        if (var14[1] - var18 > var5) {
                            var16[1] = var5;
                        }
                        else {
                            var16[1] = (int)(var14[1] - var18);
                        }
                        if (checkBlockLine(var16, var14) == -1) {
                            var2[var4][0] = var12;
                            var2[var4][1] = var3;
                            var2[var4][2] = var13;
                            var2[var4][3] = var16[1];
                            ++var4;
                        }
                    }
                    ++var7;
                }
                --var3;
                --var6;
            }
        }
        System.arraycopy(var2, 0, leafNodes = new int[var4][4], 0, var4);
    }
    
    void genTreeLayer(int par1, int par2, int par3, float par4, byte par5, Block par6) {
        int var7 = (int)(par4 + 0.618);
        byte var8 = WorldGenGreatwoodTrees.otherCoordPairs[par5];
        byte var9 = WorldGenGreatwoodTrees.otherCoordPairs[par5 + 3];
        int[] var10 = { par1, par2, par3 };
        int[] var11 = { 0, 0, 0 };
        int var12 = -var7;
        int var13 = -var7;
        var11[par5] = var10[par5];
        while (var12 <= var7) {
            var11[var8] = var10[var8] + var12;
            for (var13 = -var7; var13 <= var7; ++var13) {
                double var14 = Math.pow(Math.abs(var12) + 0.5, 2.0) + Math.pow(Math.abs(var13) + 0.5, 2.0);
                if (var14 <= par4 * par4) {
                    try {
                        var11[var9] = var10[var9] + var13;
                        IBlockState state = world.getBlockState(new BlockPos(var11[0], var11[1], var11[2]));
                        Block block = state.getBlock();
                        if (block == Blocks.AIR || block == BlocksTC.leafGreatwood) {
                            if (block == null || block.canBeReplacedByLeaves(state, world, new BlockPos(var11[0], var11[1], var11[2]))) {
                                setBlockAndNotifyAdequately(world, new BlockPos(var11[0], var11[1], var11[2]), par6.getDefaultState());
                            }
                        }
                    }
                    catch (Exception ex) {}
                }
            }
            ++var12;
        }
    }
    
    float layerSize(int par1) {
        if (par1 < (float) heightLimit * 0.3) {
            return -1.618f;
        }
        float var2 = heightLimit / 2.0f;
        float var3 = heightLimit / 2.0f - par1;
        float var4;
        if (var3 == 0.0f) {
            var4 = var2;
        }
        else if (Math.abs(var3) >= var2) {
            var4 = 0.0f;
        }
        else {
            var4 = (float)Math.sqrt(Math.pow(Math.abs(var2), 2.0) - Math.pow(Math.abs(var3), 2.0));
        }
        var4 *= 0.5f;
        return var4;
    }
    
    float leafSize(int par1) {
        return (par1 >= 0 && par1 < leafDistanceLimit) ? ((par1 != 0 && par1 != leafDistanceLimit - 1) ? 3.0f : 2.0f) : -1.0f;
    }
    
    void generateLeafNode(int par1, int par2, int par3) {
        for (int var4 = par2, var5 = par2 + leafDistanceLimit; var4 < var5; ++var4) {
            float var6 = leafSize(var4 - par2);
            genTreeLayer(par1, var4, par3, var6, (byte)1, BlocksTC.leafGreatwood);
        }
    }
    
    void placeBlockLine(int[] par1ArrayOfInteger, int[] par2ArrayOfInteger, Block par3) {
        int[] var4 = { 0, 0, 0 };
        byte var5 = 0;
        byte var6 = 0;
        while (var5 < 3) {
            var4[var5] = par2ArrayOfInteger[var5] - par1ArrayOfInteger[var5];
            if (Math.abs(var4[var5]) > Math.abs(var4[var6])) {
                var6 = var5;
            }
            ++var5;
        }
        if (var4[var6] != 0) {
            byte var7 = WorldGenGreatwoodTrees.otherCoordPairs[var6];
            byte var8 = WorldGenGreatwoodTrees.otherCoordPairs[var6 + 3];
            byte var9;
            if (var4[var6] > 0) {
                var9 = 1;
            }
            else {
                var9 = -1;
            }
            double var10 = var4[var7] / (double)var4[var6];
            double var11 = var4[var8] / (double)var4[var6];
            int[] var12 = { 0, 0, 0 };
            for (int var13 = 0, var14 = var4[var6] + var9; var13 != var14; var13 += var9) {
                var12[var6] = MathHelper.floor(par1ArrayOfInteger[var6] + var13 + 0.5);
                var12[var7] = MathHelper.floor(par1ArrayOfInteger[var7] + var13 * var10 + 0.5);
                var12[var8] = MathHelper.floor(par1ArrayOfInteger[var8] + var13 * var11 + 0.5);
                byte var15 = 1;
                int var16 = Math.abs(var12[0] - par1ArrayOfInteger[0]);
                int var17 = Math.abs(var12[2] - par1ArrayOfInteger[2]);
                int var18 = Math.max(var16, var17);
                if (var18 > 0) {
                    if (var16 == var18) {
                        var15 = 0;
                    }
                    else if (var17 == var18) {
                        var15 = 2;
                    }
                }
                if (isReplaceable(world, new BlockPos(var12[0], var12[1], var12[2]))) {
                    setBlockAndNotifyAdequately(world, new BlockPos(var12[0], var12[1], var12[2]), par3.getStateFromMeta(var15));
                }
            }
        }
    }
    
    void generateLeaves() {
        for (int var1 = 0, var2 = leafNodes.length; var1 < var2; ++var1) {
            int var3 = leafNodes[var1][0];
            int var4 = leafNodes[var1][1];
            int var5 = leafNodes[var1][2];
            generateLeafNode(var3, var4, var5);
        }
    }
    
    boolean leafNodeNeedsBase(int par1) {
        return par1 >= heightLimit * 0.2;
    }
    
    void generateTrunk() {
        int var1 = basePos[0];
        int var2 = basePos[1];
        int var3 = basePos[1] + height;
        int var4 = basePos[2];
        int[] var5 = { var1, var2, var4 };
        int[] var6 = { var1, var3, var4 };
        placeBlockLine(var5, var6, BlocksTC.logGreatwood);
        if (trunkSize == 2) {
            int[] array = var5;
            int n = 0;
            ++array[n];
            int[] array2 = var6;
            int n2 = 0;
            ++array2[n2];
            placeBlockLine(var5, var6, BlocksTC.logGreatwood);
            int[] array3 = var5;
            int n3 = 2;
            ++array3[n3];
            int[] array4 = var6;
            int n4 = 2;
            ++array4[n4];
            placeBlockLine(var5, var6, BlocksTC.logGreatwood);
            int[] array5 = var5;
            int n5 = 0;
            --array5[n5];
            int[] array6 = var6;
            int n6 = 0;
            --array6[n6];
            placeBlockLine(var5, var6, BlocksTC.logGreatwood);
        }
    }
    
    void generateLeafNodeBases() {
        int var1 = 0;
        int var2 = leafNodes.length;
        int[] var3 = { basePos[0], basePos[1], basePos[2] };
        while (var1 < var2) {
            int[] var4 = leafNodes[var1];
            int[] var5 = { var4[0], var4[1], var4[2] };
            var3[1] = var4[3];
            int var6 = var3[1] - basePos[1];
            if (leafNodeNeedsBase(var6)) {
                placeBlockLine(var3, var5, BlocksTC.logGreatwood);
            }
            ++var1;
        }
    }
    
    int checkBlockLine(int[] par1ArrayOfInteger, int[] par2ArrayOfInteger) {
        int[] var3 = { 0, 0, 0 };
        byte var4 = 0;
        byte var5 = 0;
        while (var4 < 3) {
            var3[var4] = par2ArrayOfInteger[var4] - par1ArrayOfInteger[var4];
            if (Math.abs(var3[var4]) > Math.abs(var3[var5])) {
                var5 = var4;
            }
            ++var4;
        }
        if (var3[var5] == 0) {
            return -1;
        }
        byte var6 = WorldGenGreatwoodTrees.otherCoordPairs[var5];
        byte var7 = WorldGenGreatwoodTrees.otherCoordPairs[var5 + 3];
        byte var8;
        if (var3[var5] > 0) {
            var8 = 1;
        }
        else {
            var8 = -1;
        }
        double var9 = var3[var6] / (double)var3[var5];
        double var10 = var3[var7] / (double)var3[var5];
        int[] var11 = { 0, 0, 0 };
        int var12;
        int var13;
        for (var12 = 0, var13 = var3[var5] + var8; var12 != var13; var12 += var8) {
            var11[var5] = par1ArrayOfInteger[var5] + var12;
            var11[var6] = MathHelper.floor(par1ArrayOfInteger[var6] + var12 * var9);
            var11[var7] = MathHelper.floor(par1ArrayOfInteger[var7] + var12 * var10);
            try {
                Block var14 = world.getBlockState(new BlockPos(var11[0], var11[1], var11[2])).getBlock();
                if (var14 != Blocks.AIR && var14 != BlocksTC.leafGreatwood) {
                    break;
                }
            }
            catch (Exception ex) {}
        }
        return (var12 == var13) ? -1 : Math.abs(var12);
    }
    
    boolean validTreeLocation(int x, int z) {
        int[] var1 = { basePos[0] + x, basePos[1], basePos[2] + z };
        int[] var2 = { basePos[0] + x, basePos[1] + heightLimit - 1, basePos[2] + z };
        try {
            IBlockState state = world.getBlockState(new BlockPos(basePos[0] + x, basePos[1] - 1, basePos[2] + z));
            Block var3 = state.getBlock();
            boolean isSoil = var3.canSustainPlant(state, world, new BlockPos(basePos[0] + x, basePos[1] - 1, basePos[2] + z), EnumFacing.UP, (IPlantable)Blocks.SAPLING);
            if (!isSoil) {
                return false;
            }
            int var4 = checkBlockLine(var1, var2);
            if (var4 == -1) {
                return true;
            }
            if (var4 < 6) {
                return false;
            }
            heightLimit = var4;
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    public void setScale(double par1, double par3, double par5) {
    }
    
    public boolean generate(World par1World, Random par2Random, BlockPos pos) {
        world = par1World;
        long var6 = par2Random.nextLong();
        rand.setSeed(var6);
        basePos[0] = pos.getX();
        basePos[1] = pos.getY();
        basePos[2] = pos.getZ();
        if (heightLimit == 0) {
            heightLimit = heightLimitLimit + rand.nextInt(heightLimitLimit);
        }
        int x = 0;
        int z = 0;
        for (x = 0; x < trunkSize; ++x) {
            for (z = 0; z < trunkSize; ++z) {
                if (!validTreeLocation(x, z)) {
                    world = null;
                    return false;
                }
            }
        }
        world.setBlockToAir(pos);
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        generateTrunk();
        scaleWidth = 1.66;
        basePos[0] = pos.getX();
        basePos[1] = pos.getY() + height;
        basePos[2] = pos.getZ();
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        generateTrunk();
        if (spiders) {
            world.setBlockState(pos.down(), Blocks.MOB_SPAWNER.getDefaultState());
            TileEntityMobSpawner var7 = (TileEntityMobSpawner)par1World.getTileEntity(pos.down());
            if (var7 != null) {
                var7.getSpawnerBaseLogic().setEntityId(EntityList.getKey(EntityCaveSpider.class));
                for (int a = 0; a < 50; ++a) {
                    int xx = pos.getX() - 7 + par2Random.nextInt(14);
                    int yy = pos.getY() + par2Random.nextInt(10);
                    int zz = pos.getZ() - 7 + par2Random.nextInt(14);
                    if (par1World.isAirBlock(new BlockPos(xx, yy, zz)) && (BlockUtils.isBlockTouching(par1World, new BlockPos(xx, yy, zz), BlocksTC.leafGreatwood) || BlockUtils.isBlockTouching(par1World, new BlockPos(xx, yy, zz), BlocksTC.logGreatwood))) {
                        world.setBlockState(new BlockPos(xx, yy, zz), Blocks.WEB.getDefaultState());
                    }
                }
                par1World.setBlockState(pos.down(2), Blocks.CHEST.getDefaultState());
                TileEntityChest var8 = (TileEntityChest)par1World.getTileEntity(pos.down(2));
                if (var8 != null) {
                    var8.setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, rand.nextLong());
                }
            }
        }
        world = null;
        return true;
    }
    
    static {
        otherCoordPairs = new byte[] { 2, 0, 0, 1, 2, 1 };
    }
}
