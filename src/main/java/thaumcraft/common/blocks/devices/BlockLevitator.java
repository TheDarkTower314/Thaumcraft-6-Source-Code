package thaumcraft.common.blocks.devices;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
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
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.devices.TileLevitator;


@Mod.EventBusSubscriber({ Side.CLIENT })
public class BlockLevitator extends BlockTCDevice implements IBlockFacing, IBlockEnabled
{
    private RayTracer rayTracer;
    
    public BlockLevitator() {
        super(Material.WOOD, TileLevitator.class, "levitator");
        rayTracer = new RayTracer();
        setSoundType(SoundType.WOOD);
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
    
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        RayTraceResult hit = RayTracer.retraceBlock(world, player, pos);
        if (hit == null) {
            return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
        }
        TileEntity tile = world.getTileEntity(pos);
        if (hit.subHit == 0 && tile instanceof TileLevitator) {
            ((TileLevitator)tile).increaseRange(player);
            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.key, SoundCategory.BLOCKS, 0.5f, 1.0f);
            return true;
        }
        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileLevitator) {
            RayTraceResult hit = RayTracer.retraceBlock(world, Minecraft.getMinecraft().player, pos);
            if (hit != null && hit.subHit == 0) {
                Cuboid6 cubeoid = ((TileLevitator)tile).getCuboidByFacing(BlockStateUtils.getFacing(tile.getBlockMetadata()));
                Vector3 v = new Vector3(pos);
                Cuboid6 c = cubeoid.add(v);
                return c.aabb();
            }
        }
        return super.getSelectedBoundingBox(state, world, pos);
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing facing = BlockStateUtils.getFacing(state);
        float f = 0.125f;
        float minx = 0.0f + ((facing.getFrontOffsetX() > 0) ? f : 0.0f);
        float maxx = 1.0f - ((facing.getFrontOffsetX() < 0) ? f : 0.0f);
        float miny = 0.0f + ((facing.getFrontOffsetY() > 0) ? f : 0.0f);
        float maxy = 1.0f - ((facing.getFrontOffsetY() < 0) ? f : 0.0f);
        float minz = 0.0f + ((facing.getFrontOffsetZ() > 0) ? f : 0.0f);
        float maxz = 1.0f - ((facing.getFrontOffsetZ() < 0) ? f : 0.0f);
        return new AxisAlignedBB(minx, miny, minz, maxx, maxy, maxz);
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
        if (tile == null || !(tile instanceof TileLevitator)) {
            return super.collisionRayTrace(state, world, pos, start, end);
        }
        List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
        if (tile instanceof TileLevitator) {
            ((TileLevitator)tile).addTraceableCuboids(cuboids);
        }
        ArrayList<ExtendedMOP> list = new ArrayList<ExtendedMOP>();
        rayTracer.rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids, new BlockCoord(pos), this, list);
        return (list.size() > 0) ? list.get(0) : super.collisionRayTrace(state, world, pos, start, end);
    }
}
