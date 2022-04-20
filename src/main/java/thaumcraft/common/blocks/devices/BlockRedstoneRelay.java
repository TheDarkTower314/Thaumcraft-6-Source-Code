// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.devices;

import java.util.List;
import thaumcraft.codechicken.lib.vec.BlockCoord;
import thaumcraft.codechicken.lib.raytracer.ExtendedMOP;
import java.util.ArrayList;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import java.util.LinkedList;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.event.ForgeEventFactory;
import java.util.EnumSet;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.devices.TileRedstoneRelay;
import net.minecraft.block.material.Material;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.Mod;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.blocks.BlockTCDevice;

@Mod.EventBusSubscriber({ Side.CLIENT })
public class BlockRedstoneRelay extends BlockTCDevice implements IBlockFacingHorizontal, IBlockEnabled
{
    private RayTracer rayTracer;
    
    public BlockRedstoneRelay() {
        super(Material.CIRCUITS, TileRedstoneRelay.class, "redstone_relay");
        this.rayTracer = new RayTracer();
        this.setHardness(0.0f);
        this.setResistance(0.0f);
        this.setSoundType(SoundType.WOOD);
        this.disableStats();
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return 0;
    }
    
    public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
        return worldIn.getBlockState(pos.down()).isTopSolid() && super.canPlaceBlockAt(worldIn, pos);
    }
    
    public boolean canBlockStay(final World worldIn, final BlockPos pos) {
        return worldIn.getBlockState(pos.down()).isTopSolid();
    }
    
    public void randomTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random random) {
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (!player.capabilities.allowEdit) {
            return false;
        }
        final RayTraceResult hit = RayTracer.retraceBlock(world, player, pos);
        if (hit == null) {
            return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
        }
        final TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileRedstoneRelay) {
            if (hit.subHit == 0) {
                ((TileRedstoneRelay)tile).increaseOut();
                world.playSound(null, pos, SoundsTC.key, SoundCategory.BLOCKS, 0.5f, 1.0f);
                this.updateState(world, pos, state);
                this.notifyNeighbors(world, pos, state);
            }
            if (hit.subHit == 1) {
                ((TileRedstoneRelay)tile).increaseIn();
                world.playSound(null, pos, SoundsTC.key, SoundCategory.BLOCKS, 0.5f, 1.0f);
                this.updateState(world, pos, state);
                this.notifyNeighbors(world, pos, state);
            }
            return true;
        }
        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }
    
    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        final boolean flag = this.shouldBePowered(worldIn, pos, state);
        if (this.isPowered(state) && !flag) {
            worldIn.setBlockState(pos, this.getUnpoweredState(state), 2);
            this.notifyNeighbors(worldIn, pos, state);
        }
        else if (!this.isPowered(state)) {
            worldIn.setBlockState(pos, this.getPoweredState(state), 2);
            this.notifyNeighbors(worldIn, pos, state);
            if (!flag) {
                worldIn.updateBlockTick(pos, this.getPoweredState(state).getBlock(), this.getTickDelay(state), -1);
            }
        }
    }
    
    @Override
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        this.notifyNeighbors(worldIn, pos, state);
    }
    
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos, final EnumFacing side) {
        return side.getAxis() != EnumFacing.Axis.Y;
    }
    
    protected boolean isPowered(final IBlockState state) {
        return BlockStateUtils.isEnabled(state);
    }
    
    public int getStrongPower(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos, final EnumFacing side) {
        return this.getWeakPower(state, worldIn, pos, side);
    }
    
    public int getWeakPower(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos, final EnumFacing side) {
        return this.isPowered(state) ? ((state.getValue(BlockRedstoneRelay.FACING) == side) ? this.getActiveSignal(worldIn, pos, state) : 0) : 0;
    }
    
    @Override
    public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos pos2) {
        if (this.canBlockStay(worldIn, pos)) {
            this.updateState(worldIn, pos, state);
        }
        else {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            for (final EnumFacing enumfacing : EnumFacing.values()) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
            }
        }
    }
    
    @Override
    protected void updateState(final World worldIn, final BlockPos pos, final IBlockState state) {
        final boolean flag = this.shouldBePowered(worldIn, pos, state);
        if (((this.isPowered(state) && !flag) || (!this.isPowered(state) && flag)) && !worldIn.isBlockTickPending(pos, this)) {
            byte b0 = -1;
            if (this.isFacingTowardsRepeater(worldIn, pos, state)) {
                b0 = -3;
            }
            else if (this.isPowered(state)) {
                b0 = -2;
            }
            worldIn.updateBlockTick(pos, this, this.getTickDelay(state), b0);
        }
    }
    
    protected boolean shouldBePowered(final World worldIn, final BlockPos pos, final IBlockState state) {
        int pr = 1;
        final TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null && tile instanceof TileRedstoneRelay) {
            pr = ((TileRedstoneRelay)tile).getIn();
        }
        return this.calculateInputStrength(worldIn, pos, state) >= pr;
    }
    
    protected int calculateInputStrength(final World worldIn, final BlockPos pos, final IBlockState state) {
        final EnumFacing enumfacing = state.getValue(BlockRedstoneRelay.FACING);
        final BlockPos blockpos1 = pos.offset(enumfacing);
        final int i = worldIn.getRedstonePower(blockpos1, enumfacing);
        if (i >= 15) {
            return i;
        }
        final IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);
        return Math.max(i, (iblockstate1.getBlock() == Blocks.REDSTONE_WIRE) ? ((int)iblockstate1.getValue(BlockRedstoneWire.POWER)) : 0);
    }
    
    protected int getPowerOnSides(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state) {
        final EnumFacing enumfacing = state.getValue(BlockRedstoneRelay.FACING);
        final EnumFacing enumfacing2 = enumfacing.rotateY();
        final EnumFacing enumfacing3 = enumfacing.rotateYCCW();
        return Math.max(this.getPowerOnSide(worldIn, pos.offset(enumfacing2), enumfacing2), this.getPowerOnSide(worldIn, pos.offset(enumfacing3), enumfacing3));
    }
    
    protected int getPowerOnSide(final IBlockAccess worldIn, final BlockPos pos, final EnumFacing side) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);
        final Block block = iblockstate.getBlock();
        return this.canPowerSide(block, iblockstate) ? ((block == Blocks.REDSTONE_WIRE) ? iblockstate.getValue(BlockRedstoneWire.POWER) : worldIn.getStrongPower(pos, side)) : 0;
    }
    
    public boolean canProvidePower(final IBlockState state) {
        return true;
    }
    
    public void onBlockPlacedBy(final World worldIn, final BlockPos pos, final IBlockState state, final EntityLivingBase placer, final ItemStack stack) {
        if (this.shouldBePowered(worldIn, pos, state)) {
            worldIn.scheduleUpdate(pos, this, 1);
        }
    }
    
    @Override
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        IBlockState bs = this.getDefaultState();
        bs = bs.withProperty(BlockRedstoneRelay.FACING, (placer.isSneaking() ? placer.getHorizontalFacing() : placer.getHorizontalFacing().getOpposite()));
        bs = bs.withProperty(BlockRedstoneRelay.ENABLED, false);
        return bs;
    }
    
    @Override
    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
        this.notifyNeighbors(worldIn, pos, state);
    }
    
    protected void notifyNeighbors(final World worldIn, final BlockPos pos, final IBlockState state) {
        final EnumFacing enumfacing = state.getValue(BlockRedstoneRelay.FACING);
        final BlockPos blockpos1 = pos.offset(enumfacing.getOpposite());
        if (ForgeEventFactory.onNeighborNotify(worldIn, pos, worldIn.getBlockState(pos), EnumSet.of(enumfacing.getOpposite()), false).isCanceled()) {
            return;
        }
        worldIn.neighborChanged(blockpos1, this, pos);
        worldIn.notifyNeighborsOfStateExcept(blockpos1, this, enumfacing);
    }
    
    public void onBlockDestroyedByPlayer(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (this.isPowered(state)) {
            for (final EnumFacing enumfacing : EnumFacing.values()) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
            }
        }
        super.onBlockDestroyedByPlayer(worldIn, pos, state);
    }
    
    protected boolean canPowerSide(final Block blockIn, final IBlockState iblockstate) {
        return blockIn.canProvidePower(iblockstate);
    }
    
    protected int getActiveSignal(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state) {
        final TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null && tile instanceof TileRedstoneRelay) {
            return ((TileRedstoneRelay)tile).getOut();
        }
        return 0;
    }
    
    public static boolean isRedstoneRepeaterBlockID(final Block blockIn) {
        return Blocks.UNPOWERED_REPEATER.isAssociatedBlock(blockIn) || Blocks.UNPOWERED_COMPARATOR.isAssociatedBlock(blockIn);
    }
    
    public boolean isAssociated(final Block other) {
        return other == this.getPoweredState(this.getDefaultState()).getBlock() || other == this.getUnpoweredState(this.getDefaultState()).getBlock();
    }
    
    public boolean isFacingTowardsRepeater(final World worldIn, final BlockPos pos, final IBlockState state) {
        final EnumFacing enumfacing = (state.getValue(BlockRedstoneRelay.FACING)).getOpposite();
        final BlockPos blockpos1 = pos.offset(enumfacing);
        return isRedstoneRepeaterBlockID(worldIn.getBlockState(blockpos1).getBlock()) && worldIn.getBlockState(blockpos1).getValue(BlockRedstoneRelay.FACING) != enumfacing;
    }
    
    protected int getTickDelay(final IBlockState state) {
        return 2;
    }
    
    protected IBlockState getPoweredState(final IBlockState unpoweredState) {
        final EnumFacing enumfacing = unpoweredState.getValue(BlockRedstoneRelay.FACING);
        return this.getDefaultState().withProperty(BlockRedstoneRelay.FACING, enumfacing).withProperty(BlockRedstoneRelay.ENABLED, true);
    }
    
    protected IBlockState getUnpoweredState(final IBlockState poweredState) {
        final EnumFacing enumfacing = poweredState.getValue(BlockRedstoneRelay.FACING);
        return this.getDefaultState().withProperty(BlockRedstoneRelay.FACING, enumfacing).withProperty(BlockRedstoneRelay.ENABLED, false);
    }
    
    public boolean isAssociatedBlock(final Block other) {
        return this.isAssociated(other);
    }
    
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(final IBlockState state, final World world, final BlockPos pos) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileRedstoneRelay) {
            final RayTraceResult hit = RayTracer.retraceBlock(world, Minecraft.getMinecraft().player, pos);
            if (hit != null && hit.subHit == 0) {
                final Cuboid6 cubeoid = ((TileRedstoneRelay)tile).getCuboid0(BlockStateUtils.getFacing(tile.getBlockMetadata()));
                final Vector3 v = new Vector3(pos);
                final Cuboid6 c = cubeoid.sub(v);
                return new AxisAlignedBB((float)c.min.x, (float)c.min.y, (float)c.min.z, (float)c.max.x, (float)c.max.y, (float)c.max.z).offset(pos);
            }
            if (hit != null && hit.subHit == 1) {
                final Cuboid6 cubeoid = ((TileRedstoneRelay)tile).getCuboid1(BlockStateUtils.getFacing(tile.getBlockMetadata()));
                final Vector3 v = new Vector3(pos);
                final Cuboid6 c = cubeoid.sub(v);
                return new AxisAlignedBB((float)c.min.x, (float)c.min.y, (float)c.min.z, (float)c.max.x, (float)c.max.y, (float)c.max.z).offset(pos);
            }
        }
        return super.getSelectedBoundingBox(state, world, pos);
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onBlockHighlight(final DrawBlockHighlightEvent event) {
        if (event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK && event.getPlayer().world.getBlockState(event.getTarget().getBlockPos()).getBlock() == this) {
            RayTracer.retraceBlock(event.getPlayer().world, event.getPlayer(), event.getTarget().getBlockPos());
        }
    }
    
    public RayTraceResult collisionRayTrace(final IBlockState state, final World world, final BlockPos pos, final Vec3d start, final Vec3d end) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile == null || !(tile instanceof TileRedstoneRelay)) {
            return super.collisionRayTrace(state, world, pos, start, end);
        }
        final List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
        if (tile instanceof TileRedstoneRelay) {
            ((TileRedstoneRelay)tile).addTraceableCuboids(cuboids);
        }
        final ArrayList<ExtendedMOP> list = new ArrayList<ExtendedMOP>();
        this.rayTracer.rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids, new BlockCoord(pos), this, list);
        return (list.size() > 0) ? list.get(0) : super.collisionRayTrace(state, world, pos, start, end);
    }
}
