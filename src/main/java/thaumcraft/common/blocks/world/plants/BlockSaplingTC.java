// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.world.plants;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.Block;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.init.Blocks;
import thaumcraft.common.world.objects.WorldGenSilverwoodTrees;
import thaumcraft.common.world.objects.WorldGenGreatwoodTrees;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraftforge.event.terraingen.TerrainGen;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.IGrowable;
import net.minecraft.block.BlockBush;

public class BlockSaplingTC extends BlockBush implements IGrowable
{
    public static final PropertyInteger STAGE;
    protected static final AxisAlignedBB SAPLING_AABB;
    
    public BlockSaplingTC(final String name) {
        setUnlocalizedName(name);
        setRegistryName("thaumcraft", name);
        setDefaultState(blockState.getBaseState().withProperty((IProperty)BlockSaplingTC.STAGE, (Comparable)0));
        setCreativeTab(ConfigItems.TABTC);
        setSoundType(SoundType.PLANT);
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return BlockSaplingTC.SAPLING_AABB;
    }
    
    public int getFlammability(final IBlockAccess world, final BlockPos pos, final EnumFacing face) {
        return 60;
    }
    
    public int getFireSpreadSpeed(final IBlockAccess world, final BlockPos pos, final EnumFacing face) {
        return 30;
    }
    
    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        if (!worldIn.isRemote) {
            super.updateTick(worldIn, pos, state, rand);
            if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
                grow(worldIn, pos, state, rand);
            }
        }
    }
    
    public void grow(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        if ((int)state.getValue((IProperty)BlockSaplingTC.STAGE) == 0) {
            worldIn.setBlockState(pos, state.cycleProperty((IProperty)BlockSaplingTC.STAGE), 4);
        }
        else {
            generateTree(worldIn, pos, state, rand);
        }
    }
    
    public void generateTree(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        if (!TerrainGen.saplingGrowTree(worldIn, rand, pos)) {
            return;
        }
        Object object = null;
        int i = 0;
        int j = 0;
        boolean flag = false;
        Label_0111: {
            if (state.getBlock() == BlocksTC.saplingGreatwood) {
                for (i = 0; i >= -1; --i) {
                    for (j = 0; j >= -1; --j) {
                        if (isTwoByTwoOfType(worldIn, pos, i, j, BlocksTC.saplingGreatwood)) {
                            object = new WorldGenGreatwoodTrees(true, false);
                            flag = true;
                            break Label_0111;
                        }
                    }
                }
            }
            else {
                object = new WorldGenSilverwoodTrees(true, 7, 4);
            }
        }
        if (object == null) {
            return;
        }
        final IBlockState iblockstate1 = Blocks.AIR.getDefaultState();
        if (flag) {
            worldIn.setBlockState(pos.add(i, 0, j), iblockstate1, 4);
            worldIn.setBlockState(pos.add(i + 1, 0, j), iblockstate1, 4);
            worldIn.setBlockState(pos.add(i, 0, j + 1), iblockstate1, 4);
            worldIn.setBlockState(pos.add(i + 1, 0, j + 1), iblockstate1, 4);
        }
        else {
            worldIn.setBlockState(pos, iblockstate1, 4);
        }
        if (!((WorldGenerator)object).generate(worldIn, rand, pos.add(i, 0, j))) {
            if (flag) {
                worldIn.setBlockState(pos.add(i, 0, j), state, 4);
                worldIn.setBlockState(pos.add(i + 1, 0, j), state, 4);
                worldIn.setBlockState(pos.add(i, 0, j + 1), state, 4);
                worldIn.setBlockState(pos.add(i + 1, 0, j + 1), state, 4);
            }
            else {
                worldIn.setBlockState(pos.add(i, 0, j), state, 4);
            }
        }
    }
    
    private boolean isTwoByTwoOfType(final World worldIn, final BlockPos pos, final int p_181624_3_, final int p_181624_4_, final Block type) {
        return isTypeAt(worldIn, pos.add(p_181624_3_, 0, p_181624_4_), type) && isTypeAt(worldIn, pos.add(p_181624_3_ + 1, 0, p_181624_4_), type) && isTypeAt(worldIn, pos.add(p_181624_3_, 0, p_181624_4_ + 1), type) && isTypeAt(worldIn, pos.add(p_181624_3_ + 1, 0, p_181624_4_ + 1), type);
    }
    
    public boolean isTypeAt(final World worldIn, final BlockPos pos, final Block type) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);
        return iblockstate.getBlock() == type;
    }
    
    public int damageDropped(final IBlockState state) {
        return 0;
    }
    
    public boolean canGrow(final World worldIn, final BlockPos pos, final IBlockState state, final boolean isClient) {
        return true;
    }
    
    public boolean canUseBonemeal(final World worldIn, final Random rand, final BlockPos pos, final IBlockState state) {
        return worldIn.rand.nextFloat() < 0.25;
    }
    
    public void grow(final World worldIn, final Random rand, final BlockPos pos, final IBlockState state) {
        grow(worldIn, pos, state, rand);
    }
    
    public IBlockState getStateFromMeta(final int meta) {
        return getDefaultState().withProperty((IProperty)BlockSaplingTC.STAGE, (Comparable)((meta & 0x8) >> 3));
    }
    
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i |= (int)state.getValue((IProperty)BlockSaplingTC.STAGE) << 3;
        return i;
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockSaplingTC.STAGE);
    }
    
    static {
        STAGE = PropertyInteger.create("stage", 0, 1);
        SAPLING_AABB = new AxisAlignedBB(0.09999999403953552, 0.0, 0.09999999403953552, 0.8999999761581421, 0.800000011920929, 0.8999999761581421);
    }
}
