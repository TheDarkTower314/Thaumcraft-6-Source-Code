package thaumcraft.common.blocks;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.lib.utils.BlockStateUtils;


public class BlockTCDevice extends BlockTCTile
{
    public BlockTCDevice(Material mat, Class tc, String name) {
        super(mat, tc, name);
        IBlockState bs = blockState.getBaseState();
        if (this instanceof IBlockFacingHorizontal) {
            bs.withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)EnumFacing.NORTH);
        }
        else if (this instanceof IBlockFacing) {
            bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)EnumFacing.UP);
        }
        if (this instanceof IBlockEnabled) {
            bs.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)true);
        }
        setDefaultState(bs);
    }
    
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        IBlockState state = world.getBlockState(pos);
        for (IProperty<?> prop : state.getProperties().keySet()) {
            if (prop.getName().equals("facing")) {
                world.setBlockState(pos, state.cycleProperty((IProperty)prop));
                return true;
            }
        }
        return false;
    }
    
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        updateState(worldIn, pos, state);
    }
    
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos frompos) {
        updateState(worldIn, pos, state);
        super.neighborChanged(state, worldIn, pos, blockIn, frompos);
    }
    
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState bs = getDefaultState();
        if (this instanceof IBlockFacingHorizontal) {
            bs = bs.withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)(placer.isSneaking() ? placer.getHorizontalFacing() : placer.getHorizontalFacing().getOpposite()));
        }
        if (this instanceof IBlockFacing) {
            bs = bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)(placer.isSneaking() ? EnumFacing.getDirectionFromEntityLiving(pos, placer).getOpposite() : EnumFacing.getDirectionFromEntityLiving(pos, placer)));
        }
        if (this instanceof IBlockEnabled) {
            bs = bs.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)true);
        }
        return bs;
    }
    
    protected void updateState(World worldIn, BlockPos pos, IBlockState state) {
        if (this instanceof IBlockEnabled) {
            boolean flag = !worldIn.isBlockPowered(pos);
            if (flag != (boolean)state.getValue((IProperty)IBlockEnabled.ENABLED)) {
                worldIn.setBlockState(pos, state.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)flag), 3);
            }
        }
    }
    
    public void updateFacing(World world, BlockPos pos, EnumFacing face) {
        if (this instanceof IBlockFacing || this instanceof IBlockFacingHorizontal) {
            if (face == BlockStateUtils.getFacing(world.getBlockState(pos))) {
                return;
            }
            if (this instanceof IBlockFacingHorizontal && face.getHorizontalIndex() >= 0) {
                world.setBlockState(pos, world.getBlockState(pos).withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)face), 3);
            }
            if (this instanceof IBlockFacing) {
                world.setBlockState(pos, world.getBlockState(pos).withProperty((IProperty)IBlockFacing.FACING, (Comparable)face), 3);
            }
        }
    }
    
    public IBlockState getStateFromMeta(int meta) {
        IBlockState bs = getDefaultState();
        try {
            if (this instanceof IBlockFacingHorizontal) {
                bs = bs.withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)BlockStateUtils.getFacing(meta));
            }
            if (this instanceof IBlockFacing) {
                bs = bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)BlockStateUtils.getFacing(meta));
            }
            if (this instanceof IBlockEnabled) {
                bs = bs.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)BlockStateUtils.isEnabled(meta));
            }
        }
        catch (Exception ex) {}
        return bs;
    }
    
    public int getMetaFromState(IBlockState state) {
        byte b0 = 0;
        int i = (this instanceof IBlockFacingHorizontal) ? (b0 | ((EnumFacing)state.getValue((IProperty)IBlockFacingHorizontal.FACING)).getIndex()) : ((this instanceof IBlockFacing) ? (b0 | ((EnumFacing)state.getValue((IProperty)IBlockFacing.FACING)).getIndex()) : b0);
        if (this instanceof IBlockEnabled && !(boolean)state.getValue((IProperty)IBlockEnabled.ENABLED)) {
            i |= 0x8;
        }
        return i;
    }
    
    protected BlockStateContainer createBlockState() {
        ArrayList<IProperty> ip = new ArrayList<IProperty>();
        if (this instanceof IBlockFacingHorizontal) {
            ip.add(IBlockFacingHorizontal.FACING);
        }
        if (this instanceof IBlockFacing) {
            ip.add(IBlockFacing.FACING);
        }
        if (this instanceof IBlockEnabled) {
            ip.add(IBlockEnabled.ENABLED);
        }
        return (ip.size() == 0) ? super.createBlockState() : new BlockStateContainer(this, ip.toArray(new IProperty[ip.size()]));
    }
}
