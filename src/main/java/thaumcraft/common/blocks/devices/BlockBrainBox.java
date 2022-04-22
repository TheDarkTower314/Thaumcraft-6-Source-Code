// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.devices;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import thaumcraft.common.blocks.IBlockFacing;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.blocks.BlockTC;

public class BlockBrainBox extends BlockTC implements IBlockFacingHorizontal, IBlockEnabled
{
    public BlockBrainBox() {
        super(Material.IRON, "brain_box");
        this.setSoundType(SoundType.METAL);
        final IBlockState bs = this.blockState.getBaseState();
        bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)EnumFacing.UP);
        this.setDefaultState(bs);
        this.setHardness(1.0f);
        this.setResistance(10.0f);
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean canHarvestBlock(final IBlockAccess world, final BlockPos pos, final EntityPlayer player) {
        return true;
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return 0;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos pos2) {
        if (worldIn.getBlockState(pos.offset(BlockStateUtils.getFacing(state))).getBlock() != BlocksTC.thaumatorium && worldIn.getBlockState(pos.offset(BlockStateUtils.getFacing(state))).getBlock() != BlocksTC.thaumatoriumTop) {
            this.dropBlockAsItem(worldIn, pos, BlocksTC.brainBox.getDefaultState(), 0);
            worldIn.setBlockToAir(pos);
        }
    }
    
    public boolean canPlaceBlockOnSide(final World worldIn, final BlockPos pos, final EnumFacing side) {
        return (worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock() == BlocksTC.thaumatorium || worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock() == BlocksTC.thaumatoriumTop) && worldIn.getBlockState(pos.offset(side.getOpposite())).getValue((IProperty)BlockBrainBox.FACING) != side && super.canPlaceBlockOnSide(worldIn, pos, side);
    }
    
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        IBlockState bs = this.getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)facing.getOpposite());
        return bs;
    }
    
    public IBlockState getStateFromMeta(final int meta) {
        IBlockState bs = this.getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)BlockStateUtils.getFacing(meta));
        return bs;
    }
    
    public int getMetaFromState(final IBlockState state) {
        final byte b0 = 0;
        final int i = b0 | ((EnumFacing)state.getValue((IProperty)IBlockFacing.FACING)).getIndex();
        return i;
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, IBlockFacing.FACING);
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return new AxisAlignedBB(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125);
    }
}
