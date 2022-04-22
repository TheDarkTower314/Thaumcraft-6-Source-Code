// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.essentia;

import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.blocks.BlockTCDevice;

public class BlockEssentiaTransport extends BlockTCDevice implements IBlockFacing
{
    public BlockEssentiaTransport(final Class te, final String name) {
        super(Material.IRON, te, name);
        setSoundType(SoundType.METAL);
        setHardness(1.0f);
        setResistance(10.0f);
        final IBlockState bs = blockState.getBaseState();
        bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)EnumFacing.UP);
        setDefaultState(bs);
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    @Override
    public boolean canHarvestBlock(final IBlockAccess world, final BlockPos pos, final EntityPlayer player) {
        return true;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return 0;
    }
    
    @Override
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        IBlockState bs = getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)facing);
        return bs;
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        final EnumFacing facing = BlockStateUtils.getFacing(state);
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
