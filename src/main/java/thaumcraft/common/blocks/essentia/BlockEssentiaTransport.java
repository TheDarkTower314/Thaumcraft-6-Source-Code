package thaumcraft.common.blocks.essentia;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.lib.utils.BlockStateUtils;


public class BlockEssentiaTransport extends BlockTCDevice implements IBlockFacing
{
    public BlockEssentiaTransport(Class te, String name) {
        super(Material.IRON, te, name);
        setSoundType(SoundType.METAL);
        setHardness(1.0f);
        setResistance(10.0f);
        IBlockState bs = blockState.getBaseState();
        bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)EnumFacing.UP);
        setDefaultState(bs);
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }
    
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState bs = getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)facing);
        return bs;
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing facing = BlockStateUtils.getFacing(state);
        switch (facing.ordinal()) {
            default: {
                return new AxisAlignedBB(0.25, 0.5, 0.25, 0.75, 1.0, 0.75);
            }
            case 1: {
                return new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.5, 0.75);
            }
            case 2: {
                return new AxisAlignedBB(0.25, 0.25, 0.5, 0.75, 0.75, 1.0);
            }
            case 3: {
                return new AxisAlignedBB(0.25, 0.25, 0.0, 0.75, 0.75, 0.5);
            }
            case 4: {
                return new AxisAlignedBB(0.5, 0.25, 0.25, 1.0, 0.75, 0.75);
            }
            case 5: {
                return new AxisAlignedBB(0.0, 0.25, 0.25, 0.5, 0.75, 0.75);
            }
        }
    }
}
