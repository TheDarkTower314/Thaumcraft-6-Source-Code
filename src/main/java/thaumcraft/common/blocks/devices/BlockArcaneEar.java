package thaumcraft.common.blocks.devices;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.devices.TileArcaneEar;


public class BlockArcaneEar extends BlockTCDevice implements IBlockFacing, IBlockEnabled
{
    private static List<SoundEvent> INSTRUMENTS;
    
    public BlockArcaneEar(String name) {
        super(Material.WOOD, TileArcaneEar.class, name);
        setSoundType(SoundType.WOOD);
        setHardness(1.0f);
        IBlockState bs = blockState.getBaseState();
        bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)EnumFacing.UP);
        bs.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)false);
        setDefaultState(bs);
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
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
        bs = bs.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)false);
        return bs;
    }
    
    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        TileArcaneEar tile = (TileArcaneEar)worldIn.getTileEntity(pos);
        if (tile != null) {
            tile.updateTone();
        }
    }
    
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2) {
        TileArcaneEar tile = (TileArcaneEar)worldIn.getTileEntity(pos);
        if (tile != null) {
            tile.updateTone();
        }
        if (!worldIn.getBlockState(pos.offset(BlockStateUtils.getFacing(state).getOpposite())).isSideSolid(worldIn, pos.offset(BlockStateUtils.getFacing(state).getOpposite()), BlockStateUtils.getFacing(state))) {
            dropBlockAsItem(worldIn, pos, getDefaultState(), 0);
            worldIn.setBlockToAir(pos);
        }
    }
    
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileArcaneEar tile = (TileArcaneEar)world.getTileEntity(pos);
        if (tile != null) {
            tile.changePitch();
            tile.triggerNote(world, pos, true);
        }
        return true;
    }
    
    public boolean canProvidePower(IBlockState state) {
        return true;
    }
    
    public int getWeakPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return BlockStateUtils.isEnabled(state.getBlock().getMetaFromState(state)) ? 15 : 0;
    }
    
    public int getStrongPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return BlockStateUtils.isEnabled(state.getBlock().getMetaFromState(state)) ? 15 : 0;
    }
    
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return worldIn.getBlockState(pos.offset(side.getOpposite())).isSideSolid(worldIn, pos.offset(side.getOpposite()), side);
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing facing = BlockStateUtils.getFacing(getMetaFromState(state));
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
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int par5, int par6) {
        super.eventReceived(state, worldIn, pos, par5, par6);
        float var7 = (float)Math.pow(2.0, (par6 - 12) / 12.0);
        worldIn.playSound(null, pos, getInstrument(par5), SoundCategory.BLOCKS, 3.0f, var7);
        worldIn.spawnParticle(EnumParticleTypes.NOTE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, par6 / 24.0, 0.0, 0.0);
        return true;
    }
    
    static {
        INSTRUMENTS = Lists.newArrayList(SoundEvents.BLOCK_NOTE_HARP, SoundEvents.BLOCK_NOTE_BASEDRUM, SoundEvents.BLOCK_NOTE_SNARE, SoundEvents.BLOCK_NOTE_HAT, SoundEvents.BLOCK_NOTE_BASS, SoundEvents.BLOCK_NOTE_FLUTE, SoundEvents.BLOCK_NOTE_BELL, SoundEvents.BLOCK_NOTE_GUITAR, SoundEvents.BLOCK_NOTE_CHIME, SoundEvents.BLOCK_NOTE_XYLOPHONE);
    }
}
