package thaumcraft.common.blocks.devices;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.devices.TileLampArcane;
import thaumcraft.common.tiles.devices.TileLampFertility;
import thaumcraft.common.tiles.devices.TileLampGrowth;


public class BlockLamp extends BlockTCDevice implements IBlockFacing, IBlockEnabled
{
    public BlockLamp(Class tc, String name) {
        super(Material.IRON, tc, name);
        setSoundType(SoundType.METAL);
        setHardness(1.0f);
        IBlockState bs = blockState.getBaseState();
        bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)EnumFacing.DOWN);
        bs.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)true);
        setDefaultState(bs);
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
    
    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }
    
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return BlockStateUtils.isEnabled(world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos))) ? 15 : super.getLightValue(state, world, pos);
    }
    
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState bs = getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)facing.getOpposite());
        bs = bs.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)false);
        return bs;
    }
    
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te instanceof TileLampArcane) {
            ((TileLampArcane)te).removeLights();
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2) {
        if (worldIn.isAirBlock(pos.offset(BlockStateUtils.getFacing(state)))) {
            dropBlockAsItem(worldIn, pos, getDefaultState(), 0);
            worldIn.setBlockToAir(pos);
            return;
        }
        TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te instanceof TileLampArcane && BlockStateUtils.isEnabled(state) && worldIn.isBlockPowered(pos)) {
            ((TileLampArcane)te).removeLights();
        }
        boolean checkUpdate = true;
        if (te != null && te instanceof TileLampGrowth && ((TileLampGrowth)te).charges <= 0) {
            checkUpdate = false;
        }
        if (te != null && te instanceof TileLampFertility && ((TileLampFertility)te).charges <= 0) {
            checkUpdate = false;
        }
        if (checkUpdate) {
            super.neighborChanged(state, worldIn, pos, blockIn, pos2);
        }
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.25, 0.125, 0.25, 0.75, 0.875, 0.75);
    }
}
