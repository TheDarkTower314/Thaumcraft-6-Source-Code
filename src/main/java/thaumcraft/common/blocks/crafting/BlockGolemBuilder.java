package thaumcraft.common.blocks.crafting;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.Thaumcraft;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.tiles.crafting.TileGolemBuilder;


public class BlockGolemBuilder extends BlockTCDevice implements IBlockFacingHorizontal
{
    public static boolean ignore;
    
    public BlockGolemBuilder() {
        super(Material.ROCK, TileGolemBuilder.class, "golem_builder");
        setSoundType(SoundType.STONE);
        setCreativeTab(null);
    }
    
    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        return false;
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
    
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(Blocks.PISTON);
    }
    
    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }
    
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        destroy(worldIn, pos, state, pos);
        super.breakBlock(worldIn, pos, state);
    }
    
    public static void destroy(World worldIn, BlockPos pos, IBlockState state, BlockPos startpos) {
        if (BlockGolemBuilder.ignore || worldIn.isRemote) {
            return;
        }
        BlockGolemBuilder.ignore = true;
        for (int a = -1; a <= 1; ++a) {
            for (int b = 0; b <= 1; ++b) {
                for (int c = -1; c <= 1; ++c) {
                    if (pos.add(a, b, c) != startpos) {
                        IBlockState bs = worldIn.getBlockState(pos.add(a, b, c));
                        if (bs.getBlock() == BlocksTC.placeholderBars) {
                            worldIn.setBlockState(pos.add(a, b, c), Blocks.IRON_BARS.getDefaultState());
                        }
                        if (bs.getBlock() == BlocksTC.placeholderAnvil) {
                            worldIn.setBlockState(pos.add(a, b, c), Blocks.ANVIL.getDefaultState());
                        }
                        if (bs.getBlock() == BlocksTC.placeholderCauldron) {
                            worldIn.setBlockState(pos.add(a, b, c), Blocks.CAULDRON.getDefaultState());
                        }
                        if (bs.getBlock() == BlocksTC.placeholderTable) {
                            worldIn.setBlockState(pos.add(a, b, c), BlocksTC.tableStone.getDefaultState());
                        }
                    }
                }
            }
        }
        if (pos != startpos) {
            worldIn.setBlockState(pos, Blocks.PISTON.getDefaultState().withProperty((IProperty)BlockPistonBase.FACING, (Comparable)EnumFacing.UP));
        }
        BlockGolemBuilder.ignore = false;
    }
    
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        player.openGui(Thaumcraft.instance, 19, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
    
    static {
        BlockGolemBuilder.ignore = false;
    }
}
