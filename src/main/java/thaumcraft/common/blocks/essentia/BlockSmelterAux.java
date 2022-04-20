// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.essentia;

import net.minecraft.block.Block;
import java.util.ArrayList;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.blocks.BlockTC;

public class BlockSmelterAux extends BlockTC implements IBlockFacingHorizontal
{
    public BlockSmelterAux() {
        super(Material.IRON, "smelter_aux");
        this.setSoundType(SoundType.METAL);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)EnumFacing.NORTH));
        this.setHardness(1.0f);
        this.setResistance(10.0f);
    }
    
    public boolean rotateBlock(final World world, final BlockPos pos, final EnumFacing axis) {
        return false;
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
    
    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
    }
    
    public boolean canPlaceBlockOnSide(final World worldIn, final BlockPos pos, final EnumFacing side) {
        return super.canPlaceBlockOnSide(worldIn, pos, side) && side.getAxis().isHorizontal() && worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock() instanceof BlockSmelter && BlockStateUtils.getFacing(worldIn.getBlockState(pos.offset(side.getOpposite()))) != side;
    }
    
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        IBlockState bs = this.getDefaultState();
        if (!facing.getAxis().isHorizontal()) {
            facing = EnumFacing.NORTH;
        }
        bs = bs.withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)facing.getOpposite());
        return bs;
    }
    
    public IBlockState getStateFromMeta(final int meta) {
        IBlockState bs = this.getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)EnumFacing.getHorizontal(BlockStateUtils.getFacing(meta).getHorizontalIndex()));
        return bs;
    }
    
    public int getMetaFromState(final IBlockState state) {
        return 0x0 | ((EnumFacing)state.getValue((IProperty)IBlockFacingHorizontal.FACING)).getIndex();
    }
    
    protected BlockStateContainer createBlockState() {
        final ArrayList<IProperty> ip = new ArrayList<IProperty>();
        ip.add(IBlockFacingHorizontal.FACING);
        return (ip.size() == 0) ? super.createBlockState() : new BlockStateContainer(this, ip.toArray(new IProperty[ip.size()]));
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
}
