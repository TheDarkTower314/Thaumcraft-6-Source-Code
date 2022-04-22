// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.devices;

import com.google.common.collect.Lists;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.devices.TileArcaneEar;
import net.minecraft.block.material.Material;
import net.minecraft.util.SoundEvent;
import java.util.List;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.blocks.BlockTCDevice;

public class BlockArcaneEar extends BlockTCDevice implements IBlockFacing, IBlockEnabled
{
    private static final List<SoundEvent> INSTRUMENTS;
    
    public BlockArcaneEar(final String name) {
        super(Material.WOOD, TileArcaneEar.class, name);
        setSoundType(SoundType.WOOD);
        setHardness(1.0f);
        final IBlockState bs = blockState.getBaseState();
        bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)EnumFacing.UP);
        bs.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)false);
        setDefaultState(bs);
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
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
        bs = bs.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)false);
        return bs;
    }
    
    @Override
    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
        final TileArcaneEar tile = (TileArcaneEar)worldIn.getTileEntity(pos);
        if (tile != null) {
            tile.updateTone();
        }
    }
    
    @Override
    public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos pos2) {
        final TileArcaneEar tile = (TileArcaneEar)worldIn.getTileEntity(pos);
        if (tile != null) {
            tile.updateTone();
        }
        if (!worldIn.getBlockState(pos.offset(BlockStateUtils.getFacing(state).getOpposite())).isSideSolid(worldIn, pos.offset(BlockStateUtils.getFacing(state).getOpposite()), BlockStateUtils.getFacing(state))) {
            dropBlockAsItem(worldIn, pos, getDefaultState(), 0);
            worldIn.setBlockToAir(pos);
        }
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (world.isRemote) {
            return true;
        }
        final TileArcaneEar tile = (TileArcaneEar)world.getTileEntity(pos);
        if (tile != null) {
            tile.changePitch();
            tile.triggerNote(world, pos, true);
        }
        return true;
    }
    
    public boolean canProvidePower(final IBlockState state) {
        return true;
    }
    
    public int getWeakPower(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos, final EnumFacing side) {
        return BlockStateUtils.isEnabled(state.getBlock().getMetaFromState(state)) ? 15 : 0;
    }
    
    public int getStrongPower(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos, final EnumFacing side) {
        return BlockStateUtils.isEnabled(state.getBlock().getMetaFromState(state)) ? 15 : 0;
    }
    
    public boolean canPlaceBlockOnSide(final World worldIn, final BlockPos pos, final EnumFacing side) {
        return worldIn.getBlockState(pos.offset(side.getOpposite())).isSideSolid(worldIn, pos.offset(side.getOpposite()), side);
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        final EnumFacing facing = BlockStateUtils.getFacing(getMetaFromState(state));
        switch (facing.ordinal()) {
            case 0: {
                return new AxisAlignedBB(0.125, 0.625, 0.125, 0.875, 1.0, 0.875);
            }
            case 1: {
                return new AxisAlignedBB(0.125, 0.0, 0.125, 0.875, 0.375, 0.875);
            }
            case 2: {
                return new AxisAlignedBB(0.125, 0.125, 0.625, 0.875, 0.875, 1.0);
            }
            case 3: {
                return new AxisAlignedBB(0.125, 0.125, 0.0, 0.875, 0.875, 0.375);
            }
            case 4: {
                return new AxisAlignedBB(0.625, 0.125, 0.125, 1.0, 0.875, 0.875);
            }
            default: {
                return new AxisAlignedBB(0.0, 0.125, 0.125, 0.375, 0.875, 0.875);
            }
        }
    }
    
    protected SoundEvent getInstrument(int type) {
        if (type < 0 || type >= BlockArcaneEar.INSTRUMENTS.size()) {
            type = 0;
        }
        return BlockArcaneEar.INSTRUMENTS.get(type);
    }
    
    @Override
    public boolean eventReceived(final IBlockState state, final World worldIn, final BlockPos pos, final int par5, final int par6) {
        super.eventReceived(state, worldIn, pos, par5, par6);
        final float var7 = (float)Math.pow(2.0, (par6 - 12) / 12.0);
        worldIn.playSound(null, pos, getInstrument(par5), SoundCategory.BLOCKS, 3.0f, var7);
        worldIn.spawnParticle(EnumParticleTypes.NOTE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, par6 / 24.0, 0.0, 0.0);
        return true;
    }
    
    static {
        INSTRUMENTS = Lists.newArrayList(SoundEvents.BLOCK_NOTE_HARP, SoundEvents.BLOCK_NOTE_BASEDRUM, SoundEvents.BLOCK_NOTE_SNARE, SoundEvents.BLOCK_NOTE_HAT, SoundEvents.BLOCK_NOTE_BASS, SoundEvents.BLOCK_NOTE_FLUTE, SoundEvents.BLOCK_NOTE_BELL, SoundEvents.BLOCK_NOTE_GUITAR, SoundEvents.BLOCK_NOTE_CHIME, SoundEvents.BLOCK_NOTE_XYLOPHONE);
    }
}
