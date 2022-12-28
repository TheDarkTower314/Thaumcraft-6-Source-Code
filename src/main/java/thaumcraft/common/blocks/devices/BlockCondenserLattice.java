package thaumcraft.common.blocks.devices;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.devices.TileCondenser;


public class BlockCondenserLattice extends BlockTC
{
    public static PropertyBool NORTH;
    public static PropertyBool EAST;
    public static PropertyBool SOUTH;
    public static PropertyBool WEST;
    public static PropertyBool UP;
    public static PropertyBool DOWN;
    private ArrayList<Long> history;
    
    public BlockCondenserLattice(boolean dirty) {
        super(Material.IRON, dirty ? "condenser_lattice_dirty" : "condenser_lattice");
        history = new ArrayList<Long>();
        setHardness(0.5f);
        setResistance(5.0f);
        setSoundType(SoundType.METAL);
        setLightLevel(dirty ? 0.0f : 0.33f);
        setDefaultState(blockState.getBaseState().withProperty((IProperty)BlockCondenserLattice.NORTH, (Comparable)false).withProperty((IProperty)BlockCondenserLattice.EAST, (Comparable)false).withProperty((IProperty)BlockCondenserLattice.SOUTH, (Comparable)false).withProperty((IProperty)BlockCondenserLattice.WEST, (Comparable)false).withProperty((IProperty)BlockCondenserLattice.UP, (Comparable)false).withProperty((IProperty)BlockCondenserLattice.DOWN, (Comparable)false));
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockCondenserLattice.NORTH, BlockCondenserLattice.EAST, BlockCondenserLattice.SOUTH, BlockCondenserLattice.WEST, BlockCondenserLattice.UP, BlockCondenserLattice.DOWN);
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public int getMetaFromState(IBlockState state) {
        return 0;
    }
    
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        Boolean[] cons = makeConnections(state, worldIn, pos);
        return state.withProperty((IProperty)BlockCondenserLattice.DOWN, (Comparable)cons[0]).withProperty((IProperty)BlockCondenserLattice.UP, (Comparable)cons[1]).withProperty((IProperty)BlockCondenserLattice.NORTH, (Comparable)cons[2]).withProperty((IProperty)BlockCondenserLattice.SOUTH, (Comparable)cons[3]).withProperty((IProperty)BlockCondenserLattice.WEST, (Comparable)cons[4]).withProperty((IProperty)BlockCondenserLattice.EAST, (Comparable)cons[5]);
    }
    
    private Boolean[] makeConnections(IBlockState state, IBlockAccess world, BlockPos pos) {
        Boolean[] cons = { false, false, false, false, false, false };
        int a = 0;
        for (EnumFacing face : EnumFacing.VALUES) {
            Block b = world.getBlockState(pos.offset(face)).getBlock();
            if (b instanceof BlockCondenserLattice || (face == EnumFacing.DOWN && b == BlocksTC.condenser)) {
                cons[a] = true;
            }
            ++a;
        }
        return cons;
    }
    
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        triggerUpdate(worldIn, pos);
    }
    
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (blockIn == BlocksTC.condenserlattice || blockIn == BlocksTC.condenserlatticeDirty || blockIn == BlocksTC.condenser) {
            triggerUpdate(worldIn, pos);
        }
    }
    
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
        if (state.getBlock() == BlocksTC.condenserlatticeDirty && playerIn.getHeldItem(hand).getItem() == ItemsTC.filter) {
            playerIn.getHeldItem(hand).shrink(1);
            if (worldIn.rand.nextBoolean()) {
                worldIn.spawnEntity(new EntityItem(worldIn, pos.getX() + 0.5f + facing.getFrontOffsetX() / 3.0f, pos.getY() + 0.5f, pos.getZ() + 0.5f + facing.getFrontOffsetZ() / 3.0f, ConfigItems.FLUX_CRYSTAL.copy()));
            }
            worldIn.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2f, ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.7f + 1.0f) * 1.6f);
            worldIn.setBlockState(pos, BlocksTC.condenserlattice.getDefaultState(), 3);
            IBlockState state2 = worldIn.getBlockState(pos);
            if (state2.getBlock() instanceof BlockCondenserLattice) {
                ((BlockCondenserLattice)state2.getBlock()).triggerUpdate(worldIn, pos);
            }
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }
    
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        super.onBlockHarvested(worldIn, pos, state, player);
        triggerUpdate(worldIn, pos);
    }
    
    public void triggerUpdate(World world, BlockPos pos) {
        history.clear();
        BlockPos p = processUpdate(world, pos);
        if (p == null || p.distanceSq(pos) > 74.0) {
            dropBlockAsItem(world, pos, getDefaultState(), 0);
            world.setBlockToAir(pos);
        }
        history.clear();
    }
    
    private BlockPos processUpdate(World world, BlockPos pos) {
        history.add(pos.toLong());
        for (EnumFacing face : EnumFacing.VALUES) {
            BlockPos p2 = pos.offset(face);
            if (!history.contains(p2.toLong())) {
                Block b = world.getBlockState(p2).getBlock();
                if (b instanceof BlockCondenserLattice) {
                    BlockPos pp = processUpdate(world, p2);
                    if (pp != null) {
                        return pp;
                    }
                }
                if (face == EnumFacing.DOWN && b == BlocksTC.condenser) {
                    TileEntity te = world.getTileEntity(p2);
                    if (te != null && te instanceof TileCondenser) {
                        ((TileCondenser)te).latticeCount = -1.0f;
                    }
                    return p2;
                }
            }
        }
        return null;
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        float minx = 0.3125f;
        float maxx = 0.6875f;
        float miny = 0.3125f;
        float maxy = 0.6875f;
        float minz = 0.3125f;
        float maxz = 0.6875f;
        EnumFacing fd = null;
        for (int side = 0; side < 6; ++side) {
            fd = EnumFacing.VALUES[side];
            Block b = source.getBlockState(pos.offset(fd)).getBlock();
            if (b instanceof BlockCondenserLattice || (fd == EnumFacing.DOWN && b == BlocksTC.condenser)) {
                switch (side) {
                    case 0: {
                        miny = 0.0f;
                        break;
                    }
                    case 1: {
                        maxy = 1.0f;
                        break;
                    }
                    case 2: {
                        minz = 0.0f;
                        break;
                    }
                    case 3: {
                        maxz = 1.0f;
                        break;
                    }
                    case 4: {
                        minx = 0.0f;
                        break;
                    }
                    case 5: {
                        maxx = 1.0f;
                        break;
                    }
                }
            }
        }
        return new AxisAlignedBB(minx, miny, minz, maxx, maxy, maxz);
    }
    
    static {
        NORTH = PropertyBool.create("north");
        EAST = PropertyBool.create("east");
        SOUTH = PropertyBool.create("south");
        WEST = PropertyBool.create("west");
        UP = PropertyBool.create("up");
        DOWN = PropertyBool.create("down");
    }
}
