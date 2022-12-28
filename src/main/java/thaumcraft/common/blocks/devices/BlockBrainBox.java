package thaumcraft.common.blocks.devices;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.lib.utils.BlockStateUtils;


public class BlockBrainBox extends BlockTC implements IBlockFacingHorizontal, IBlockEnabled
{
    public BlockBrainBox() {
        super(Material.IRON, "brain_box");
        setSoundType(SoundType.METAL);
        IBlockState bs = blockState.getBaseState();
        bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)EnumFacing.UP);
        setDefaultState(bs);
        setHardness(1.0f);
        setResistance(10.0f);
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
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2) {
        if (worldIn.getBlockState(pos.offset(BlockStateUtils.getFacing(state))).getBlock() != BlocksTC.thaumatorium && worldIn.getBlockState(pos.offset(BlockStateUtils.getFacing(state))).getBlock() != BlocksTC.thaumatoriumTop) {
            dropBlockAsItem(worldIn, pos, BlocksTC.brainBox.getDefaultState(), 0);
            worldIn.setBlockToAir(pos);
        }
    }
    
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return (worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock() == BlocksTC.thaumatorium || worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock() == BlocksTC.thaumatoriumTop) && worldIn.getBlockState(pos.offset(side.getOpposite())).getValue((IProperty)BlockBrainBox.FACING) != side && super.canPlaceBlockOnSide(worldIn, pos, side);
    }
    
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState bs = getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)facing.getOpposite());
        return bs;
    }
    
    public IBlockState getStateFromMeta(int meta) {
        IBlockState bs = getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)BlockStateUtils.getFacing(meta));
        return bs;
    }
    
    public int getMetaFromState(IBlockState state) {
        byte b0 = 0;
        int i = b0 | ((EnumFacing)state.getValue((IProperty)IBlockFacing.FACING)).getIndex();
        return i;
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, IBlockFacing.FACING);
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125);
    }
}
