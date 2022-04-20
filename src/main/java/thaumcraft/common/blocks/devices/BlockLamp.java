// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.devices;

import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.common.tiles.devices.TileLampFertility;
import thaumcraft.common.tiles.devices.TileLampGrowth;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.devices.TileLampArcane;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.blocks.BlockTCDevice;

public class BlockLamp extends BlockTCDevice implements IBlockFacing, IBlockEnabled
{
    public BlockLamp(final Class tc, final String name) {
        super(Material.IRON, tc, name);
        this.setSoundType(SoundType.METAL);
        this.setHardness(1.0f);
        final IBlockState bs = this.blockState.getBaseState();
        bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)EnumFacing.DOWN);
        bs.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)true);
        this.setDefaultState(bs);
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return 0;
    }
    
    public int getLightValue(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        return BlockStateUtils.isEnabled(world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos))) ? 15 : super.getLightValue(state, world, pos);
    }
    
    @Override
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        IBlockState bs = this.getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)facing.getOpposite());
        bs = bs.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)false);
        return bs;
    }
    
    @Override
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        final TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te instanceof TileLampArcane) {
            ((TileLampArcane)te).removeLights();
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    @Override
    public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos pos2) {
        if (worldIn.isAirBlock(pos.offset(BlockStateUtils.getFacing(state)))) {
            this.dropBlockAsItem(worldIn, pos, this.getDefaultState(), 0);
            worldIn.setBlockToAir(pos);
            return;
        }
        final TileEntity te = worldIn.getTileEntity(pos);
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
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return new AxisAlignedBB(0.25, 0.125, 0.25, 0.75, 0.875, 0.75);
    }
}
