package thaumcraft.common.blocks.devices;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.tiles.devices.TileStabilizer;


public class BlockInlay extends BlockTC implements IInfusionStabiliserExt
{
    public static PropertyEnum<EnumAttachPosition> NORTH;
    public static PropertyEnum<EnumAttachPosition> EAST;
    public static PropertyEnum<EnumAttachPosition> SOUTH;
    public static PropertyEnum<EnumAttachPosition> WEST;
    public static PropertyInteger CHARGE;
    protected static AxisAlignedBB[] REDSTONE_WIRE_AABB;
    
    public BlockInlay() {
        super(Material.IRON, "inlay");
        setSoundType(SoundType.METAL);
        setHardness(0.5f);
        setDefaultState(blockState.getBaseState().withProperty((IProperty)BlockInlay.NORTH, (Comparable)EnumAttachPosition.NONE).withProperty((IProperty)BlockInlay.EAST, (Comparable)EnumAttachPosition.NONE).withProperty((IProperty)BlockInlay.SOUTH, (Comparable)EnumAttachPosition.NONE).withProperty((IProperty)BlockInlay.WEST, (Comparable)EnumAttachPosition.NONE).withProperty((IProperty)BlockInlay.CHARGE, (Comparable)0));
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BlockInlay.REDSTONE_WIRE_AABB[getAABBIndex(state.getActualState(source, pos))];
    }
    
    private static int getAABBIndex(IBlockState state) {
        int i = 0;
        boolean flag = state.getValue((IProperty)BlockInlay.NORTH) != EnumAttachPosition.NONE;
        boolean flag2 = state.getValue((IProperty)BlockInlay.EAST) != EnumAttachPosition.NONE;
        boolean flag3 = state.getValue((IProperty)BlockInlay.SOUTH) != EnumAttachPosition.NONE;
        boolean flag4 = state.getValue((IProperty)BlockInlay.WEST) != EnumAttachPosition.NONE;
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
    
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        state = state.withProperty((IProperty)BlockInlay.WEST, (Comparable) getAttachPosition(worldIn, pos, EnumFacing.WEST));
        state = state.withProperty((IProperty)BlockInlay.EAST, (Comparable) getAttachPosition(worldIn, pos, EnumFacing.EAST));
        state = state.withProperty((IProperty)BlockInlay.NORTH, (Comparable) getAttachPosition(worldIn, pos, EnumFacing.NORTH));
        state = state.withProperty((IProperty)BlockInlay.SOUTH, (Comparable) getAttachPosition(worldIn, pos, EnumFacing.SOUTH));
        return state;
    }
    
    private EnumAttachPosition getAttachPosition(IBlockAccess worldIn, BlockPos pos, EnumFacing direction) {
        BlockPos blockpos = pos.offset(direction);
        IBlockState iblockstate = worldIn.getBlockState(pos.offset(direction));
        if (canConnectTo(worldIn.getBlockState(blockpos), direction, worldIn, blockpos)) {
            return EnumAttachPosition.SIDE;
        }
        Block b = worldIn.getBlockState(blockpos).getBlock();
        if (isSourceBlock(worldIn, blockpos)) {
            return EnumAttachPosition.EXT;
        }
        return EnumAttachPosition.NONE;
    }
    
    protected static boolean canConnectTo(IBlockState blockState, @Nullable EnumFacing side, IBlockAccess world, BlockPos pos) {
        Block block = blockState.getBlock();
        return block == BlocksTC.inlay || block instanceof BlockPedestal;
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).isTopSolid();
    }
    
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return BlockInlay.NULL_AABB;
    }
    
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return super.getItem(worldIn, pos, state);
    }
    
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return super.getItemDropped(state, rand, fortune);
    }
    
    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }
    
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty((IProperty)BlockInlay.CHARGE, (Comparable)meta);
    }
    
    public int getMetaFromState(IBlockState state) {
        return (int)state.getValue((IProperty)BlockInlay.CHARGE);
    }
    
    public IBlockState withRotation(IBlockState state, Rotation rot) {
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
    
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
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
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        int charge = (int)stateIn.getValue((IProperty)BlockInlay.CHARGE);
        if (charge > 0 && rand.nextInt(20 - charge) == 0) {
            EnumFacing face = EnumFacing.HORIZONTALS[rand.nextInt(EnumFacing.HORIZONTALS.length)];
            if (getAttachPosition(worldIn, pos, face) != EnumAttachPosition.NONE) {
                double d0 = pos.getX() + 0.5 + rand.nextGaussian() * 0.08;
                double d2 = pos.getY() + 0.025f;
                double d3 = pos.getZ() + 0.5 + rand.nextGaussian() * 0.08;
                double f0 = face.getFrontOffsetX() / 70.0 * (1.0 - rand.nextFloat() * 0.1);
                double f2 = face.getFrontOffsetZ() / 70.0 * (1.0 - rand.nextFloat() * 0.1);
                float r = MathHelper.getInt(rand, 150, 200) / 255.0f;
                float g = MathHelper.getInt(rand, 0, 200) / 255.0f;
                FXDispatcher.INSTANCE.drawLineSparkle(rand, d0, d2, d3, f0, 0.0, f2, 0.33f, r, g, g / 2.0f, 0, 1.0f, 0.0f, 16);
            }
        }
    }
    
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
    }
    
    @SideOnly(Side.CLIENT)
    public static int colorMultiplier(int meta) {
        float f = meta / 15.0f;
        float f2 = f * 0.5f + 0.5f;
        if (meta == 0) {
            f2 = 0.3f;
        }
        int i = MathHelper.clamp((int)(f2 * 255.0f), 0, 255);
        int j = MathHelper.clamp((int)(f2 * 255.0f), 0, 255);
        int k = MathHelper.clamp((int)(f2 * 255.0f), 0, 255);
        return 0xFF000000 | i << 16 | j << 8 | k;
    }
    
    public int getLightValue(IBlockState state) {
        return 1;
    }
    
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int par5, int par6) {
        return super.eventReceived(state, worldIn, pos, par5, par6);
    }
    
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            updateSurroundingInlay(worldIn, pos, state);
            for (EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
                notifyInlayNeighborsOfStateChange(worldIn, pos.offset(enumfacing1));
            }
        }
    }
    
    public static void notifyInlayNeighborsOfStateChange(World worldIn, BlockPos pos) {
        IBlockState bs = worldIn.getBlockState(pos);
        if (bs.getBlock() == BlocksTC.inlay || bs.getBlock() instanceof BlockPedestal) {
            worldIn.notifyNeighborsOfStateChange(pos, bs.getBlock(), false);
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), bs.getBlock(), false);
            }
        }
    }
    
    public static IBlockState updateSurroundingInlay(World worldIn, BlockPos pos, IBlockState state) {
        Set<BlockPos> blocksNeedingUpdate = Sets.newHashSet();
        state = calculateChanges(worldIn, pos, pos, state, blocksNeedingUpdate);
        List<BlockPos> list = Lists.newArrayList((Iterable)blocksNeedingUpdate);
        for (BlockPos blockpos : list) {
            worldIn.notifyNeighborsOfStateChange(blockpos, worldIn.getBlockState(pos).getBlock(), false);
        }
        return state;
    }
    
    public static int getMaxStrength(World worldIn, BlockPos pos, int strength) {
        IBlockState bs = worldIn.getBlockState(pos);
        if (bs.getBlock() != BlocksTC.inlay && !(bs.getBlock() instanceof BlockPedestal)) {
            return strength;
        }
        int i = (int)bs.getValue((IProperty)BlockInlay.CHARGE);
        return (i > strength) ? i : strength;
    }
    
    public static int getSourceStrength(IBlockAccess world, BlockPos pos) {
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            int e = getSourceStrengthAt(world, pos.offset(enumfacing));
            if (e > 0) {
                return e;
            }
        }
        return 0;
    }
    
    public static int getSourceStrengthAt(IBlockAccess world, BlockPos pos) {
        if (isSourceBlock(world, pos)) {
            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileStabilizer) {
                return ((TileStabilizer)te).getEnergy();
            }
        }
        return 0;
    }
    
    public static boolean isSourceBlock(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == BlocksTC.stabilizer;
    }
    
    public static IBlockState calculateChanges(World worldIn, BlockPos pos1, BlockPos pos2, IBlockState state, Set<BlockPos> blocksNeedingUpdate) {
        IBlockState iblockstate = state;
        int current = (int)state.getValue((IProperty)BlockInlay.CHARGE);
        int max = 0;
        max = getMaxStrength(worldIn, pos2, max);
        int source = getSourceStrength(worldIn, pos1);
        if (source > 0 && source > max - 1) {
            max = source;
        }
        int neighbour = 0;
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockpos = pos1.offset(enumfacing);
            boolean flag = blockpos.getX() != pos2.getX() || blockpos.getZ() != pos2.getZ();
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
            for (EnumFacing enumfacing2 : EnumFacing.values()) {
                blocksNeedingUpdate.add(pos1.offset(enumfacing2));
            }
        }
        return state;
    }
    
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        if (!worldIn.isRemote) {
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
            }
            updateSurroundingInlay(worldIn, pos, state);
            for (EnumFacing enumfacing2 : EnumFacing.Plane.HORIZONTAL) {
                notifyInlayNeighborsOfStateChange(worldIn, pos.offset(enumfacing2));
            }
        }
    }
    
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
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
    
    public boolean canStabaliseInfusion(World world, BlockPos pos) {
        return true;
    }
    
    @Override
    public float getStabilizationAmount(World world, BlockPos pos) {
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
        
        private String name;
        
        private EnumAttachPosition(String name) {
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
