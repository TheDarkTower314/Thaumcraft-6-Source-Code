// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.essentia;

import thaumcraft.codechicken.lib.vec.BlockCoord;
import thaumcraft.codechicken.lib.raytracer.ExtendedMOP;
import java.util.ArrayList;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.tiles.essentia.TileTubeFilter;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.essentia.TileTubeValve;
import net.minecraft.item.Item;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.util.EnumHand;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import thaumcraft.api.aura.AuraHelper;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.tiles.essentia.TileTubeBuffer;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.math.RayTraceResult;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import java.util.LinkedList;
import thaumcraft.common.items.tools.ItemResonator;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.api.casters.ICaster;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.IEssentiaTransport;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.essentia.TileTube;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import net.minecraft.block.properties.PropertyBool;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.Mod;
import thaumcraft.common.blocks.BlockTCDevice;

@Mod.EventBusSubscriber({ Side.CLIENT })
public class BlockTube extends BlockTCDevice
{
    public static final PropertyBool NORTH;
    public static final PropertyBool EAST;
    public static final PropertyBool SOUTH;
    public static final PropertyBool WEST;
    public static final PropertyBool UP;
    public static final PropertyBool DOWN;
    private RayTracer rayTracer;
    
    public BlockTube(final Class tile, final String name) {
        super(Material.IRON, tile, name);
        this.rayTracer = new RayTracer();
        this.setHardness(0.5f);
        this.setResistance(5.0f);
        this.setSoundType(SoundType.METAL);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)BlockTube.NORTH, (Comparable)false).withProperty((IProperty)BlockTube.EAST, (Comparable)false).withProperty((IProperty)BlockTube.SOUTH, (Comparable)false).withProperty((IProperty)BlockTube.WEST, (Comparable)false).withProperty((IProperty)BlockTube.UP, (Comparable)false).withProperty((IProperty)BlockTube.DOWN, (Comparable)false));
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockTube.NORTH, BlockTube.EAST, BlockTube.SOUTH, BlockTube.WEST, BlockTube.UP, BlockTube.DOWN);
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        return 0;
    }
    
    public void onBlockPlacedBy(final World worldIn, final BlockPos pos, final IBlockState state, final EntityLivingBase placer, final ItemStack stack) {
        final TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null && tile instanceof TileTube) {
            ((TileTube)tile).facing = EnumFacing.getDirectionFromEntityLiving(pos, placer);
            tile.markDirty();
        }
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }
    
    public IBlockState getActualState(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        final Boolean[] cons = this.makeConnections(state, worldIn, pos);
        return state.withProperty((IProperty)BlockTube.DOWN, (Comparable)cons[0]).withProperty((IProperty)BlockTube.UP, (Comparable)cons[1]).withProperty((IProperty)BlockTube.NORTH, (Comparable)cons[2]).withProperty((IProperty)BlockTube.SOUTH, (Comparable)cons[3]).withProperty((IProperty)BlockTube.WEST, (Comparable)cons[4]).withProperty((IProperty)BlockTube.EAST, (Comparable)cons[5]);
    }
    
    private Boolean[] makeConnections(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        final Boolean[] cons = { false, false, false, false, false, false };
        final TileEntity t = world.getTileEntity(pos);
        if (t != null && t instanceof IEssentiaTransport) {
            final IEssentiaTransport tube = (IEssentiaTransport)t;
            int a = 0;
            for (final EnumFacing face : EnumFacing.VALUES) {
                if (tube.isConnectable(face) && ThaumcraftApiHelper.getConnectableTile(world, pos, face) != null) {
                    cons[a] = true;
                }
                ++a;
            }
        }
        return cons;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(final IBlockState state, final World world, final BlockPos pos) {
        final boolean noDoodads = InventoryUtils.isHoldingItem(Minecraft.getMinecraft().player, ICaster.class) == null && InventoryUtils.isHoldingItem(Minecraft.getMinecraft().player, ItemResonator.class) == null;
        final TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileTube) {
            final RayTraceResult hit = RayTracer.retraceBlock(world, Minecraft.getMinecraft().player, pos);
            final List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
            ((TileTube)tile).addTraceableCuboids(cuboids);
            if (hit != null && hit.subHit >= 0 && hit.subHit <= 6 && !noDoodads) {
                for (final IndexedCuboid6 cc : cuboids) {
                    if ((int)cc.data == hit.subHit) {
                        final Vector3 v = new Vector3(pos);
                        final Cuboid6 c = cc.sub(v);
                        return new AxisAlignedBB((float)c.min.x, (float)c.min.y, (float)c.min.z, (float)c.max.x, (float)c.max.y, (float)c.max.z).offset(pos);
                    }
                }
            }
        }
        return super.getSelectedBoundingBox(state, world, pos);
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        float minx = 0.3125f;
        float maxx = 0.6875f;
        float miny = 0.3125f;
        float maxy = 0.6875f;
        float minz = 0.3125f;
        float maxz = 0.6875f;
        EnumFacing fd = null;
        for (int side = 0; side < 6; ++side) {
            fd = EnumFacing.VALUES[side];
            final TileEntity te = ThaumcraftApiHelper.getConnectableTile(source, pos, fd);
            if (te != null) {
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
    
    public boolean hasComparatorInputOverride(final IBlockState state) {
        return true;
    }
    
    public int getComparatorInputOverride(final IBlockState state, final World world, final BlockPos pos) {
        final TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileTubeBuffer) {
            final float n = (float)((TileTubeBuffer)te).aspects.visSize();
            te.getClass();
            final float r = n / 10.0f;
            return MathHelper.floor(r * 14.0f) + ((((TileTubeBuffer)te).aspects.visSize() > 0) ? 1 : 0);
        }
        return 0;
    }
    
    @Override
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        final TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te instanceof TileTube && ((TileTube)te).getEssentiaAmount(EnumFacing.UP) > 0) {
            if (!worldIn.isRemote) {
                AuraHelper.polluteAura(worldIn, pos, (float)((TileTube)te).getEssentiaAmount(EnumFacing.UP), true);
            }
            else {
                worldIn.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.1f, 1.0f + worldIn.rand.nextFloat() * 0.1f, false);
                for (int a = 0; a < 5; ++a) {
                    FXDispatcher.INSTANCE.drawVentParticles(pos.getX() + 0.33 + worldIn.rand.nextFloat() * 0.33, pos.getY() + 0.33 + worldIn.rand.nextFloat() * 0.33, pos.getZ() + 0.33 + worldIn.rand.nextFloat() * 0.33, 0.0, 0.0, 0.0, Aspect.FLUX.getColor());
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (state.getBlock() == BlocksTC.tubeValve) {
            if (player.getHeldItem(hand).getItem() instanceof ICaster || player.getHeldItem(hand).getItem() instanceof ItemResonator || player.getHeldItem(hand).getItem() == Item.getItemFromBlock(this)) {
                return false;
            }
            final TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileTubeValve) {
                ((TileTubeValve)te).allowFlow = !((TileTubeValve)te).allowFlow;
                world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), state, state, 3);
                te.markDirty();
                if (!world.isRemote) {
                    world.playSound(null, pos, SoundsTC.squeek, SoundCategory.BLOCKS, 0.7f, 0.9f + world.rand.nextFloat() * 0.2f);
                }
                return true;
            }
        }
        if (state.getBlock() == BlocksTC.tubeFilter) {
            final TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileTubeFilter && player.isSneaking() && ((TileTubeFilter)te).aspectFilter != null) {
                ((TileTubeFilter)te).aspectFilter = null;
                world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), state, state, 3);
                te.markDirty();
                if (world.isRemote) {
                    world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundsTC.key, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                }
                return true;
            }
            if (te != null && te instanceof TileTubeFilter && ((TileTubeFilter)te).aspectFilter == null && player.getHeldItem(hand).getItem() instanceof IEssentiaContainerItem) {
                if (((IEssentiaContainerItem)player.getHeldItem(hand).getItem()).getAspects(player.getHeldItem(hand)) != null) {
                    ((TileTubeFilter)te).aspectFilter = ((IEssentiaContainerItem)player.getHeldItem(hand).getItem()).getAspects(player.getHeldItem(hand)).getAspects()[0];
                    world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), state, state, 3);
                    te.markDirty();
                    if (world.isRemote) {
                        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundsTC.key, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                    }
                }
                return true;
            }
        }
        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onBlockHighlight(final DrawBlockHighlightEvent event) {
        if (event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK && event.getPlayer().world.getBlockState(event.getTarget().getBlockPos()).getBlock() == this && (InventoryUtils.isHoldingItem(event.getPlayer(), ICaster.class) != null || InventoryUtils.isHoldingItem(event.getPlayer(), ItemResonator.class) != null)) {
            RayTracer.retraceBlock(event.getPlayer().world, event.getPlayer(), event.getTarget().getBlockPos());
        }
    }
    
    public RayTraceResult collisionRayTrace(final IBlockState state, final World world, final BlockPos pos, final Vec3d start, final Vec3d end) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile == null || (!(tile instanceof TileTube) && !(tile instanceof TileTubeBuffer))) {
            return super.collisionRayTrace(state, world, pos, start, end);
        }
        final List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
        if (tile instanceof TileTube) {
            ((TileTube)tile).addTraceableCuboids(cuboids);
        }
        else if (tile instanceof TileTubeBuffer) {
            ((TileTubeBuffer)tile).addTraceableCuboids(cuboids);
        }
        final ArrayList<ExtendedMOP> list = new ArrayList<ExtendedMOP>();
        this.rayTracer.rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids, new BlockCoord(pos), this, list);
        return (list.size() > 0) ? list.get(0) : super.collisionRayTrace(state, world, pos, start, end);
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
