package thaumcraft.common.blocks.devices;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.codechicken.lib.raytracer.ExtendedMOP;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.codechicken.lib.vec.BlockCoord;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.devices.TileRedstoneRelay;


@Mod.EventBusSubscriber({ Side.CLIENT })
public class BlockRedstoneRelay extends BlockTCDevice implements IBlockFacingHorizontal, IBlockEnabled
{
    private RayTracer rayTracer;
    
    public BlockRedstoneRelay() {
        super(Material.CIRCUITS, TileRedstoneRelay.class, "redstone_relay");
        rayTracer = new RayTracer();
        setHardness(0.0f);
        setResistance(0.0f);
        setSoundType(SoundType.WOOD);
        disableStats();
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }
    
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).isTopSolid() && super.canPlaceBlockAt(worldIn, pos);
    }
    
    public boolean canBlockStay(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).isTopSolid();
    }
    
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
    }
    
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!player.capabilities.allowEdit) {
            return false;
        }
        RayTraceResult hit = RayTracer.retraceBlock(world, player, pos);
        if (hit == null) {
            return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileRedstoneRelay) {
            if (hit.subHit == 0) {
                ((TileRedstoneRelay)tile).increaseOut();
                world.playSound(null, pos, SoundsTC.key, SoundCategory.BLOCKS, 0.5f, 1.0f);
                updateState(world, pos, state);
                notifyNeighbors(world, pos, state);
            }
            if (hit.subHit == 1) {
                ((TileRedstoneRelay)tile).increaseIn();
                world.playSound(null, pos, SoundsTC.key, SoundCategory.BLOCKS, 0.5f, 1.0f);
                updateState(world, pos, state);
                notifyNeighbors(world, pos, state);
            }
            return true;
        }
        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }
    
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        boolean flag = shouldBePowered(worldIn, pos, state);
        if (isPowered(state) && !flag) {
            worldIn.setBlockState(pos, getUnpoweredState(state), 2);
            notifyNeighbors(worldIn, pos, state);
        }
        else if (!isPowered(state)) {
            worldIn.setBlockState(pos, getPoweredState(state), 2);
            notifyNeighbors(worldIn, pos, state);
            if (!flag) {
                worldIn.updateBlockTick(pos, getPoweredState(state).getBlock(), getTickDelay(state), -1);
            }
        }
    }
    
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        notifyNeighbors(worldIn, pos, state);
    }
    
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return side.getAxis() != EnumFacing.Axis.Y;
    }
    
    protected boolean isPowered(IBlockState state) {
        return BlockStateUtils.isEnabled(state);
    }
    
    public int getStrongPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return getWeakPower(state, worldIn, pos, side);
    }
    
    public int getWeakPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return isPowered(state) ? ((state.getValue(BlockRedstoneRelay.FACING) == side) ? getActiveSignal(worldIn, pos, state) : 0) : 0;
    }
    
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2) {
        if (canBlockStay(worldIn, pos)) {
            updateState(worldIn, pos, state);
        }
        else {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            for (EnumFacing enumfacing : EnumFacing.values()) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
            }
        }
    }
    
    @Override
    protected void updateState(World worldIn, BlockPos pos, IBlockState state) {
        boolean flag = shouldBePowered(worldIn, pos, state);
        if (((isPowered(state) && !flag) || (!isPowered(state) && flag)) && !worldIn.isBlockTickPending(pos, this)) {
            byte b0 = -1;
            if (isFacingTowardsRepeater(worldIn, pos, state)) {
                b0 = -3;
            }
            else if (isPowered(state)) {
                b0 = -2;
            }
            worldIn.updateBlockTick(pos, this, getTickDelay(state), b0);
        }
    }
    
    protected boolean shouldBePowered(World worldIn, BlockPos pos, IBlockState state) {
        int pr = 1;
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null && tile instanceof TileRedstoneRelay) {
            pr = ((TileRedstoneRelay)tile).getIn();
        }
        return calculateInputStrength(worldIn, pos, state) >= pr;
    }
    
    protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state) {
        EnumFacing enumfacing = state.getValue(BlockRedstoneRelay.FACING);
        BlockPos blockpos1 = pos.offset(enumfacing);
        int i = worldIn.getRedstonePower(blockpos1, enumfacing);
        if (i >= 15) {
            return i;
        }
        IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);
        return Math.max(i, (iblockstate1.getBlock() == Blocks.REDSTONE_WIRE) ? ((int)iblockstate1.getValue(BlockRedstoneWire.POWER)) : 0);
    }
    
    protected int getPowerOnSides(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
        EnumFacing enumfacing = state.getValue(BlockRedstoneRelay.FACING);
        EnumFacing enumfacing2 = enumfacing.rotateY();
        EnumFacing enumfacing3 = enumfacing.rotateYCCW();
        return Math.max(getPowerOnSide(worldIn, pos.offset(enumfacing2), enumfacing2), getPowerOnSide(worldIn, pos.offset(enumfacing3), enumfacing3));
    }
    
    protected int getPowerOnSide(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();
        return canPowerSide(block, iblockstate) ? ((block == Blocks.REDSTONE_WIRE) ? iblockstate.getValue(BlockRedstoneWire.POWER) : worldIn.getStrongPower(pos, side)) : 0;
    }
    
    public boolean canProvidePower(IBlockState state) {
        return true;
    }
    
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (shouldBePowered(worldIn, pos, state)) {
            worldIn.scheduleUpdate(pos, this, 1);
        }
    }
    
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState bs = getDefaultState();
        bs = bs.withProperty(BlockRedstoneRelay.FACING, (placer.isSneaking() ? placer.getHorizontalFacing() : placer.getHorizontalFacing().getOpposite()));
        bs = bs.withProperty(BlockRedstoneRelay.ENABLED, false);
        return bs;
    }
    
    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        notifyNeighbors(worldIn, pos, state);
    }
    
    protected void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state) {
        EnumFacing enumfacing = state.getValue(BlockRedstoneRelay.FACING);
        BlockPos blockpos1 = pos.offset(enumfacing.getOpposite());
        if (ForgeEventFactory.onNeighborNotify(worldIn, pos, worldIn.getBlockState(pos), EnumSet.of(enumfacing.getOpposite()), false).isCanceled()) {
            return;
        }
        worldIn.neighborChanged(blockpos1, this, pos);
        worldIn.notifyNeighborsOfStateExcept(blockpos1, this, enumfacing);
    }
    
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
        if (isPowered(state)) {
            for (EnumFacing enumfacing : EnumFacing.values()) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
            }
        }
        super.onBlockDestroyedByPlayer(worldIn, pos, state);
    }
    
    protected boolean canPowerSide(Block blockIn, IBlockState iblockstate) {
        return blockIn.canProvidePower(iblockstate);
    }
    
    protected int getActiveSignal(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null && tile instanceof TileRedstoneRelay) {
            return ((TileRedstoneRelay)tile).getOut();
        }
        return 0;
    }
    
    public static boolean isRedstoneRepeaterBlockID(Block blockIn) {
        return Blocks.UNPOWERED_REPEATER.isAssociatedBlock(blockIn) || Blocks.UNPOWERED_COMPARATOR.isAssociatedBlock(blockIn);
    }
    
    public boolean isAssociated(Block other) {
        return other == getPoweredState(getDefaultState()).getBlock() || other == getUnpoweredState(getDefaultState()).getBlock();
    }
    
    public boolean isFacingTowardsRepeater(World worldIn, BlockPos pos, IBlockState state) {
        EnumFacing enumfacing = (state.getValue(BlockRedstoneRelay.FACING)).getOpposite();
        BlockPos blockpos1 = pos.offset(enumfacing);
        return isRedstoneRepeaterBlockID(worldIn.getBlockState(blockpos1).getBlock()) && worldIn.getBlockState(blockpos1).getValue(BlockRedstoneRelay.FACING) != enumfacing;
    }
    
    protected int getTickDelay(IBlockState state) {
        return 2;
    }
    
    protected IBlockState getPoweredState(IBlockState unpoweredState) {
        EnumFacing enumfacing = unpoweredState.getValue(BlockRedstoneRelay.FACING);
        return getDefaultState().withProperty(BlockRedstoneRelay.FACING, enumfacing).withProperty(BlockRedstoneRelay.ENABLED, true);
    }
    
    protected IBlockState getUnpoweredState(IBlockState poweredState) {
        EnumFacing enumfacing = poweredState.getValue(BlockRedstoneRelay.FACING);
        return getDefaultState().withProperty(BlockRedstoneRelay.FACING, enumfacing).withProperty(BlockRedstoneRelay.ENABLED, false);
    }
    
    public boolean isAssociatedBlock(Block other) {
        return isAssociated(other);
    }
    
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileRedstoneRelay) {
            RayTraceResult hit = RayTracer.retraceBlock(world, Minecraft.getMinecraft().player, pos);
            if (hit != null && hit.subHit == 0) {
                Cuboid6 cubeoid = ((TileRedstoneRelay)tile).getCuboid0(BlockStateUtils.getFacing(tile.getBlockMetadata()));
                Vector3 v = new Vector3(pos);
                Cuboid6 c = cubeoid.sub(v);
                return new AxisAlignedBB((float)c.min.x, (float)c.min.y, (float)c.min.z, (float)c.max.x, (float)c.max.y, (float)c.max.z).offset(pos);
            }
            if (hit != null && hit.subHit == 1) {
                Cuboid6 cubeoid = ((TileRedstoneRelay)tile).getCuboid1(BlockStateUtils.getFacing(tile.getBlockMetadata()));
                Vector3 v = new Vector3(pos);
                Cuboid6 c = cubeoid.sub(v);
                return new AxisAlignedBB((float)c.min.x, (float)c.min.y, (float)c.min.z, (float)c.max.x, (float)c.max.y, (float)c.max.z).offset(pos);
            }
        }
        return super.getSelectedBoundingBox(state, world, pos);
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onBlockHighlight(DrawBlockHighlightEvent event) {
        if (event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK && event.getPlayer().world.getBlockState(event.getTarget().getBlockPos()).getBlock() == this) {
            RayTracer.retraceBlock(event.getPlayer().world, event.getPlayer(), event.getTarget().getBlockPos());
        }
    }
    
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null || !(tile instanceof TileRedstoneRelay)) {
            return super.collisionRayTrace(state, world, pos, start, end);
        }
        List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
        if (tile instanceof TileRedstoneRelay) {
            ((TileRedstoneRelay)tile).addTraceableCuboids(cuboids);
        }
        ArrayList<ExtendedMOP> list = new ArrayList<ExtendedMOP>();
        rayTracer.rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids, new BlockCoord(pos), this, list);
        return (list.size() > 0) ? list.get(0) : super.collisionRayTrace(state, world, pos, start, end);
    }
}
