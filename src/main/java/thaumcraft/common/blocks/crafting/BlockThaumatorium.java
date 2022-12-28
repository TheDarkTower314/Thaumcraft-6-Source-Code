package thaumcraft.common.blocks.crafting;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.Thaumcraft;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.tiles.crafting.TileThaumatorium;
import thaumcraft.common.tiles.crafting.TileThaumatoriumTop;


public class BlockThaumatorium extends BlockTCDevice implements IBlockFacingHorizontal
{
    boolean top;
    
    public BlockThaumatorium(boolean top) {
        super(Material.IRON, null, top ? "thaumatorium_top" : "thaumatorium");
        setSoundType(SoundType.METAL);
        setCreativeTab(null);
        this.top = top;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        if (!top) {
            return new TileThaumatorium();
        }
        if (top) {
            return new TileThaumatoriumTop();
        }
        return null;
    }
    
    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return top ? EnumBlockRenderType.INVISIBLE : EnumBlockRenderType.MODEL;
    }
    
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && !player.isSneaking()) {
            if (!top) {
                player.openGui(Thaumcraft.instance, 3, world, pos.getX(), pos.getY(), pos.getZ());
            }
            else {
                player.openGui(Thaumcraft.instance, 3, world, pos.down().getX(), pos.down().getY(), pos.down().getZ());
            }
        }
        return true;
    }
    
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(BlocksTC.metalAlchemical);
    }
    
    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }
    
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (top && worldIn.getBlockState(pos.down()).getBlock() == BlocksTC.thaumatorium) {
            worldIn.setBlockState(pos.down(), BlocksTC.metalAlchemical.getDefaultState());
        }
        if (!top && worldIn.getBlockState(pos.up()).getBlock() == BlocksTC.thaumatoriumTop) {
            worldIn.setBlockState(pos.up(), BlocksTC.metalAlchemical.getDefaultState());
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2) {
        if (!top && worldIn.getBlockState(pos.down()).getBlock() != BlocksTC.crucible) {
            worldIn.setBlockState(pos, BlocksTC.metalAlchemical.getDefaultState());
            if (worldIn.getBlockState(pos.up()).getBlock() == BlocksTC.thaumatoriumTop) {
                worldIn.setBlockState(pos.up(), BlocksTC.metalAlchemical.getDefaultState());
            }
        }
    }
    
    public boolean hasComparatorInputOverride(IBlockState state) {
        return !top;
    }
    
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileThaumatorium) {
            return Container.calcRedstoneFromInventory((IInventory)tile);
        }
        return super.getComparatorInputOverride(state, world, pos);
    }
}
