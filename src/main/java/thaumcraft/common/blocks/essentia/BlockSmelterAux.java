package thaumcraft.common.blocks.essentia;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.lib.utils.BlockStateUtils;


public class BlockSmelterAux extends BlockTC implements IBlockFacingHorizontal
{
    public BlockSmelterAux() {
        super(Material.IRON, "smelter_aux");
        setSoundType(SoundType.METAL);
        setDefaultState(blockState.getBaseState().withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)EnumFacing.NORTH));
        setHardness(1.0f);
        setResistance(10.0f);
    }
    
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        return false;
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }
    
    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }
    
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
    }
    
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return super.canPlaceBlockOnSide(worldIn, pos, side) && side.getAxis().isHorizontal() && worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock() instanceof BlockSmelter && BlockStateUtils.getFacing(worldIn.getBlockState(pos.offset(side.getOpposite()))) != side;
    }
    
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState bs = getDefaultState();
        if (!facing.getAxis().isHorizontal()) {
            facing = EnumFacing.NORTH;
        }
        bs = bs.withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)facing.getOpposite());
        return bs;
    }
    
    public IBlockState getStateFromMeta(int meta) {
        IBlockState bs = getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)EnumFacing.getHorizontal(BlockStateUtils.getFacing(meta).getHorizontalIndex()));
        return bs;
    }
    
    public int getMetaFromState(IBlockState state) {
        return 0x0 | ((EnumFacing)state.getValue((IProperty)IBlockFacingHorizontal.FACING)).getIndex();
    }
    
    protected BlockStateContainer createBlockState() {
        ArrayList<IProperty> ip = new ArrayList<IProperty>();
        ip.add(IBlockFacingHorizontal.FACING);
        return (ip.size() == 0) ? super.createBlockState() : new BlockStateContainer(this, ip.toArray(new IProperty[ip.size()]));
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}
