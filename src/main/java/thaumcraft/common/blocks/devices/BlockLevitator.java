// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.devices;

import java.util.List;
import net.minecraft.block.Block;
import thaumcraft.codechicken.lib.vec.BlockCoord;
import thaumcraft.codechicken.lib.raytracer.ExtendedMOP;
import java.util.ArrayList;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import java.util.LinkedList;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.devices.TileLevitator;
import net.minecraft.block.material.Material;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.Mod;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.blocks.BlockTCDevice;

@Mod.EventBusSubscriber({ Side.CLIENT })
public class BlockLevitator extends BlockTCDevice implements IBlockFacing, IBlockEnabled
{
    private RayTracer rayTracer;
    
    public BlockLevitator() {
        super(Material.WOOD, TileLevitator.class, "levitator");
        this.rayTracer = new RayTracer();
        this.setSoundType(SoundType.WOOD);
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
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        final RayTraceResult hit = RayTracer.retraceBlock(world, player, pos);
        if (hit == null) {
            return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
        }
        final TileEntity tile = world.getTileEntity(pos);
        if (hit.subHit == 0 && tile instanceof TileLevitator) {
            ((TileLevitator)tile).increaseRange(player);
            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.key, SoundCategory.BLOCKS, 0.5f, 1.0f);
            return true;
        }
        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(final IBlockState state, final World world, final BlockPos pos) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileLevitator) {
            final RayTraceResult hit = RayTracer.retraceBlock(world, Minecraft.getMinecraft().player, pos);
            if (hit != null && hit.subHit == 0) {
                final Cuboid6 cubeoid = ((TileLevitator)tile).getCuboidByFacing(BlockStateUtils.getFacing(tile.getBlockMetadata()));
                final Vector3 v = new Vector3(pos);
                final Cuboid6 c = cubeoid.add(v);
                return c.aabb();
            }
        }
        return super.getSelectedBoundingBox(state, world, pos);
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        final EnumFacing facing = BlockStateUtils.getFacing(state);
        final float f = 0.125f;
        final float minx = 0.0f + ((facing.getFrontOffsetX() > 0) ? f : 0.0f);
        final float maxx = 1.0f - ((facing.getFrontOffsetX() < 0) ? f : 0.0f);
        final float miny = 0.0f + ((facing.getFrontOffsetY() > 0) ? f : 0.0f);
        final float maxy = 1.0f - ((facing.getFrontOffsetY() < 0) ? f : 0.0f);
        final float minz = 0.0f + ((facing.getFrontOffsetZ() > 0) ? f : 0.0f);
        final float maxz = 1.0f - ((facing.getFrontOffsetZ() < 0) ? f : 0.0f);
        return new AxisAlignedBB(minx, miny, minz, maxx, maxy, maxz);
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
        if (tile == null || !(tile instanceof TileLevitator)) {
            return super.collisionRayTrace(state, world, pos, start, end);
        }
        final List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
        if (tile instanceof TileLevitator) {
            ((TileLevitator)tile).addTraceableCuboids(cuboids);
        }
        final ArrayList<ExtendedMOP> list = new ArrayList<ExtendedMOP>();
        this.rayTracer.rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids, new BlockCoord(pos), this, list);
        return (list.size() > 0) ? list.get(0) : super.collisionRayTrace(state, world, pos, start, end);
    }
}
