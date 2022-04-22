// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.devices;

import net.minecraft.util.IStringSerializable;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.devices.TileStabilizer;
import java.util.List;
import java.util.Set;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.item.Item;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.blocks.BlocksTC;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.properties.PropertyEnum;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.common.blocks.BlockTC;

public class BlockInlay extends BlockTC implements IInfusionStabiliserExt
{
    public static final PropertyEnum<EnumAttachPosition> NORTH;
    public static final PropertyEnum<EnumAttachPosition> EAST;
    public static final PropertyEnum<EnumAttachPosition> SOUTH;
    public static final PropertyEnum<EnumAttachPosition> WEST;
    public static final PropertyInteger CHARGE;
    protected static final AxisAlignedBB[] REDSTONE_WIRE_AABB;
    
    public BlockInlay() {
        super(Material.IRON, "inlay");
        setSoundType(SoundType.METAL);
        setHardness(0.5f);
        setDefaultState(blockState.getBaseState().withProperty((IProperty)BlockInlay.NORTH, (Comparable)EnumAttachPosition.NONE).withProperty((IProperty)BlockInlay.EAST, (Comparable)EnumAttachPosition.NONE).withProperty((IProperty)BlockInlay.SOUTH, (Comparable)EnumAttachPosition.NONE).withProperty((IProperty)BlockInlay.WEST, (Comparable)EnumAttachPosition.NONE).withProperty((IProperty)BlockInlay.CHARGE, (Comparable)0));
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return BlockInlay.REDSTONE_WIRE_AABB[getAABBIndex(state.getActualState(source, pos))];
    }
    
    private static int getAABBIndex(final IBlockState state) {
        int i = 0;
        final boolean flag = state.getValue((IProperty)BlockInlay.NORTH) != EnumAttachPosition.NONE;
        final boolean flag2 = state.getValue((IProperty)BlockInlay.EAST) != EnumAttachPosition.NONE;
        final boolean flag3 = state.getValue((IProperty)BlockInlay.SOUTH) != EnumAttachPosition.NONE;
        final boolean flag4 = state.getValue((IProperty)BlockInlay.WEST) != EnumAttachPosition.NONE;
        if (flag || (flag3 && !flag && !flag2 && !flag4)) {
            i |= 1 << EnumFacing.NORTH.getHorizontalIndex();
        }
        if (flag2 || (flag4 && !flag && !flag2 && !flag3)) {
            i |= 1 << EnumFacing.EAST.getHorizontalIndex();
        }
        if (flag3 || (flag && !flag2 && !flag3 && !flag4)) {
            i |= 1 << EnumFacing.SOUTH.getHorizontalIndex();
        }
        if (flag4 || (flag2 && !flag && !flag3 && !flag4)) {
            i |= 1 << EnumFacing.WEST.getHorizontalIndex();
        }
        return i;
    }
    
    public IBlockState getActualState(IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        state = state.withProperty((IProperty)BlockInlay.WEST, (Comparable) getAttachPosition(worldIn, pos, EnumFacing.WEST));
        state = state.withProperty((IProperty)BlockInlay.EAST, (Comparable) getAttachPosition(worldIn, pos, EnumFacing.EAST));
        state = state.withProperty((IProperty)BlockInlay.NORTH, (Comparable) getAttachPosition(worldIn, pos, EnumFacing.NORTH));
        state = state.withProperty((IProperty)BlockInlay.SOUTH, (Comparable) getAttachPosition(worldIn, pos, EnumFacing.SOUTH));
        return state;
    }
    
    private EnumAttachPosition getAttachPosition(final IBlockAccess worldIn, final BlockPos pos, final EnumFacing direction) {
        final BlockPos blockpos = pos.offset(direction);
        final IBlockState iblockstate = worldIn.getBlockState(pos.offset(direction));
        if (canConnectTo(worldIn.getBlockState(blockpos), direction, worldIn, blockpos)) {
            return EnumAttachPosition.SIDE;
        }
        final Block b = worldIn.getBlockState(blockpos).getBlock();
        if (isSourceBlock(worldIn, blockpos)) {
            return EnumAttachPosition.EXT;
        }
        return EnumAttachPosition.NONE;
    }
    
    protected static boolean canConnectTo(final IBlockState blockState, @Nullable final EnumFacing side, final IBlockAccess world, final BlockPos pos) {
        final Block block = blockState.getBlock();
        return block == BlocksTC.inlay || block instanceof BlockPedestal;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
        return worldIn.getBlockState(pos.down()).isTopSolid();
    }
    
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(final IBlockState blockState, final IBlockAccess worldIn, final BlockPos pos) {
        return BlockInlay.NULL_AABB;
    }
    
    public ItemStack getItem(final World worldIn, final BlockPos pos, final IBlockState state) {
        return super.getItem(worldIn, pos, state);
    }
    
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return super.getItemDropped(state, rand, fortune);
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return 0;
    }
    
    public IBlockState getStateFromMeta(final int meta) {
        return getDefaultState().withProperty((IProperty)BlockInlay.CHARGE, (Comparable)meta);
    }
    
    public int getMetaFromState(final IBlockState state) {
        return (int)state.getValue((IProperty)BlockInlay.CHARGE);
    }
    
    public IBlockState withRotation(final IBlockState state, final Rotation rot) {
        switch (rot) {
            case CLOCKWISE_180: {
                return state.withProperty((IProperty)BlockInlay.NORTH, state.getValue((IProperty)BlockInlay.SOUTH)).withProperty((IProperty)BlockInlay.EAST, state.getValue((IProperty)BlockInlay.WEST)).withProperty((IProperty)BlockInlay.SOUTH, state.getValue((IProperty)BlockInlay.NORTH)).withProperty((IProperty)BlockInlay.WEST, state.getValue((IProperty)BlockInlay.EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return state.withProperty((IProperty)BlockInlay.NORTH, state.getValue((IProperty)BlockInlay.EAST)).withProperty((IProperty)BlockInlay.EAST, state.getValue((IProperty)BlockInlay.SOUTH)).withProperty((IProperty)BlockInlay.SOUTH, state.getValue((IProperty)BlockInlay.WEST)).withProperty((IProperty)BlockInlay.WEST, state.getValue((IProperty)BlockInlay.NORTH));
            }
            case CLOCKWISE_90: {
                return state.withProperty((IProperty)BlockInlay.NORTH, state.getValue((IProperty)BlockInlay.WEST)).withProperty((IProperty)BlockInlay.EAST, state.getValue((IProperty)BlockInlay.NORTH)).withProperty((IProperty)BlockInlay.SOUTH, state.getValue((IProperty)BlockInlay.EAST)).withProperty((IProperty)BlockInlay.WEST, state.getValue((IProperty)BlockInlay.SOUTH));
            }
            default: {
                return state;
            }
        }
    }
    
    public IBlockState withMirror(final IBlockState state, final Mirror mirrorIn) {
        switch (mirrorIn) {
            case LEFT_RIGHT: {
                return state.withProperty((IProperty)BlockInlay.NORTH, state.getValue((IProperty)BlockInlay.SOUTH)).withProperty((IProperty)BlockInlay.SOUTH, state.getValue((IProperty)BlockInlay.NORTH));
            }
            case FRONT_BACK: {
                return state.withProperty((IProperty)BlockInlay.EAST, state.getValue((IProperty)BlockInlay.WEST)).withProperty((IProperty)BlockInlay.WEST, state.getValue((IProperty)BlockInlay.EAST));
            }
            default: {
                return super.withMirror(state, mirrorIn);
            }
        }
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockInlay.NORTH, BlockInlay.EAST, BlockInlay.SOUTH, BlockInlay.WEST, BlockInlay.CHARGE);
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final IBlockState stateIn, final World worldIn, final BlockPos pos, final Random rand) {
        final int charge = (int)stateIn.getValue((IProperty)BlockInlay.CHARGE);
        if (charge > 0 && rand.nextInt(20 - charge) == 0) {
            final EnumFacing face = EnumFacing.HORIZONTALS[rand.nextInt(EnumFacing.HORIZONTALS.length)];
            if (getAttachPosition(worldIn, pos, face) != EnumAttachPosition.NONE) {
                final double d0 = pos.getX() + 0.5 + rand.nextGaussian() * 0.08;
                final double d2 = pos.getY() + 0.025f;
                final double d3 = pos.getZ() + 0.5 + rand.nextGaussian() * 0.08;
                final double f0 = face.getFrontOffsetX() / 70.0 * (1.0 - rand.nextFloat() * 0.1);
                final double f2 = face.getFrontOffsetZ() / 70.0 * (1.0 - rand.nextFloat() * 0.1);
                final float r = MathHelper.getInt(rand, 150, 200) / 255.0f;
                final float g = MathHelper.getInt(rand, 0, 200) / 255.0f;
                FXDispatcher.INSTANCE.drawLineSparkle(rand, d0, d2, d3, f0, 0.0, f2, 0.33f, r, g, g / 2.0f, 0, 1.0f, 0.0f, 16);
            }
        }
    }
    
    public boolean canRenderInLayer(final IBlockState state, final BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
    }
    
    @SideOnly(Side.CLIENT)
    public static int colorMultiplier(final int meta) {
        final float f = meta / 15.0f;
        float f2 = f * 0.5f + 0.5f;
        if (meta == 0) {
            f2 = 0.3f;
        }
        final int i = MathHelper.clamp((int)(f2 * 255.0f), 0, 255);
        final int j = MathHelper.clamp((int)(f2 * 255.0f), 0, 255);
        final int k = MathHelper.clamp((int)(f2 * 255.0f), 0, 255);
        return 0xFF000000 | i << 16 | j << 8 | k;
    }
    
    public int getLightValue(final IBlockState state) {
        return 1;
    }
    
    public boolean eventReceived(final IBlockState state, final World worldIn, final BlockPos pos, final int par5, final int par6) {
        return super.eventReceived(state, worldIn, pos, par5, par6);
    }
    
    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (!worldIn.isRemote) {
            updateSurroundingInlay(worldIn, pos, state);
            for (final EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
                notifyInlayNeighborsOfStateChange(worldIn, pos.offset(enumfacing1));
            }
        }
    }
    
    public static void notifyInlayNeighborsOfStateChange(final World worldIn, final BlockPos pos) {
        final IBlockState bs = worldIn.getBlockState(pos);
        if (bs.getBlock() == BlocksTC.inlay || bs.getBlock() instanceof BlockPedestal) {
            worldIn.notifyNeighborsOfStateChange(pos, bs.getBlock(), false);
            for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), bs.getBlock(), false);
            }
        }
    }
    
    public static IBlockState updateSurroundingInlay(final World worldIn, final BlockPos pos, IBlockState state) {
        final Set<BlockPos> blocksNeedingUpdate = Sets.newHashSet();
        state = calculateChanges(worldIn, pos, pos, state, blocksNeedingUpdate);
        final List<BlockPos> list = Lists.newArrayList((Iterable)blocksNeedingUpdate);
        for (final BlockPos blockpos : list) {
            worldIn.notifyNeighborsOfStateChange(blockpos, worldIn.getBlockState(pos).getBlock(), false);
        }
        return state;
    }
    
    public static int getMaxStrength(final World worldIn, final BlockPos pos, final int strength) {
        final IBlockState bs = worldIn.getBlockState(pos);
        if (bs.getBlock() != BlocksTC.inlay && !(bs.getBlock() instanceof BlockPedestal)) {
            return strength;
        }
        final int i = (int)bs.getValue((IProperty)BlockInlay.CHARGE);
        return (i > strength) ? i : strength;
    }
    
    public static int getSourceStrength(final IBlockAccess world, final BlockPos pos) {
        for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            final int e = getSourceStrengthAt(world, pos.offset(enumfacing));
            if (e > 0) {
                return e;
            }
        }
        return 0;
    }
    
    public static int getSourceStrengthAt(final IBlockAccess world, final BlockPos pos) {
        if (isSourceBlock(world, pos)) {
            final TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileStabilizer) {
                return ((TileStabilizer)te).getEnergy();
            }
        }
        return 0;
    }
    
    public static boolean isSourceBlock(final IBlockAccess world, final BlockPos pos) {
        return world.getBlockState(pos).getBlock() == BlocksTC.stabilizer;
    }
    
    public static IBlockState calculateChanges(final World worldIn, final BlockPos pos1, final BlockPos pos2, IBlockState state, final Set<BlockPos> blocksNeedingUpdate) {
        final IBlockState iblockstate = state;
        final int current = (int)state.getValue((IProperty)BlockInlay.CHARGE);
        int max = 0;
        max = getMaxStrength(worldIn, pos2, max);
        final int source = getSourceStrength(worldIn, pos1);
        if (source > 0 && source > max - 1) {
            max = source;
        }
        int neighbour = 0;
        for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            final BlockPos blockpos = pos1.offset(enumfacing);
            final boolean flag = blockpos.getX() != pos2.getX() || blockpos.getZ() != pos2.getZ();
            if (flag) {
                neighbour = getMaxStrength(worldIn, blockpos, neighbour);
            }
        }
        if (neighbour > max) {
            max = neighbour - 1;
        }
        else if (max > 0) {
            --max;
        }
        else {
            max = 0;
        }
        if (source > max - 1) {
            max = source;
        }
        if (current != max) {
            state = state.withProperty((IProperty)BlockInlay.CHARGE, (Comparable)max);
            if (worldIn.getBlockState(pos1) == iblockstate) {
                worldIn.setBlockState(pos1, state, 2);
            }
            blocksNeedingUpdate.add(pos1);
            for (final EnumFacing enumfacing2 : EnumFacing.values()) {
                blocksNeedingUpdate.add(pos1.offset(enumfacing2));
            }
        }
        return state;
    }
    
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        if (!worldIn.isRemote) {
            for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
            }
            updateSurroundingInlay(worldIn, pos, state);
            for (final EnumFacing enumfacing2 : EnumFacing.Plane.HORIZONTAL) {
                notifyInlayNeighborsOfStateChange(worldIn, pos.offset(enumfacing2));
            }
        }
    }
    
    public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos fromPos) {
        if (!worldIn.isRemote) {
            if (canPlaceBlockAt(worldIn, pos)) {
                updateSurroundingInlay(worldIn, pos, state);
            }
            else {
                dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
        }
    }
    
    public boolean canStabaliseInfusion(final World world, final BlockPos pos) {
        return true;
    }
    
    @Override
    public float getStabilizationAmount(final World world, final BlockPos pos) {
        return 0.025f;
    }
    
    static {
        NORTH = PropertyEnum.create("north", EnumAttachPosition.class);
        EAST = PropertyEnum.create("east", EnumAttachPosition.class);
        SOUTH = PropertyEnum.create("south", EnumAttachPosition.class);
        WEST = PropertyEnum.create("west", EnumAttachPosition.class);
        CHARGE = PropertyInteger.create("charge", 0, 15);
        REDSTONE_WIRE_AABB = new AxisAlignedBB[] { new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.0625, 0.8125), new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.0625, 1.0), new AxisAlignedBB(0.0, 0.0, 0.1875, 0.8125, 0.0625, 0.8125), new AxisAlignedBB(0.0, 0.0, 0.1875, 0.8125, 0.0625, 1.0), new AxisAlignedBB(0.1875, 0.0, 0.0, 0.8125, 0.0625, 0.8125), new AxisAlignedBB(0.1875, 0.0, 0.0, 0.8125, 0.0625, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 0.8125, 0.0625, 0.8125), new AxisAlignedBB(0.0, 0.0, 0.0, 0.8125, 0.0625, 1.0), new AxisAlignedBB(0.1875, 0.0, 0.1875, 1.0, 0.0625, 0.8125), new AxisAlignedBB(0.1875, 0.0, 0.1875, 1.0, 0.0625, 1.0), new AxisAlignedBB(0.0, 0.0, 0.1875, 1.0, 0.0625, 0.8125), new AxisAlignedBB(0.0, 0.0, 0.1875, 1.0, 0.0625, 1.0), new AxisAlignedBB(0.1875, 0.0, 0.0, 1.0, 0.0625, 0.8125), new AxisAlignedBB(0.1875, 0.0, 0.0, 1.0, 0.0625, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 0.8125), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0) };
    }
    
    enum EnumAttachPosition implements IStringSerializable
    {
        SIDE("side"), 
        NONE("none"), 
        EXT("ext");
        
        private final String name;
        
        private EnumAttachPosition(final String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return getName();
        }
        
        public String getName() {
            return name;
        }
    }
}
