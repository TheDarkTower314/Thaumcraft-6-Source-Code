package thaumcraft.common.blocks.world.taint;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
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
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.codechicken.lib.raytracer.ExtendedMOP;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.codechicken.lib.vec.BlockCoord;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.SoundsTC;


@Mod.EventBusSubscriber({ Side.CLIENT })
public class BlockTaintFibre extends Block implements ITaintBlock
{
    public static PropertyBool NORTH;
    public static PropertyBool EAST;
    public static PropertyBool SOUTH;
    public static PropertyBool WEST;
    public static PropertyBool UP;
    public static PropertyBool DOWN;
    public static PropertyBool GROWTH1;
    public static PropertyBool GROWTH2;
    public static PropertyBool GROWTH3;
    public static PropertyBool GROWTH4;
    private RayTracer rayTracer;
    protected static AxisAlignedBB AABB_EMPTY;
    protected static AxisAlignedBB AABB_UP;
    protected static AxisAlignedBB AABB_DOWN;
    protected static AxisAlignedBB AABB_EAST;
    protected static AxisAlignedBB AABB_WEST;
    protected static AxisAlignedBB AABB_SOUTH;
    protected static AxisAlignedBB AABB_NORTH;
    
    public BlockTaintFibre() {
        super(ThaumcraftMaterials.MATERIAL_TAINT);
        rayTracer = new RayTracer();
        setUnlocalizedName("taint_fibre");
        setRegistryName("thaumcraft", "taint_fibre");
        setHardness(1.0f);
        setSoundType(SoundsTC.GORE);
        setTickRandomly(true);
        setCreativeTab(ConfigItems.TABTC);
        setDefaultState(blockState.getBaseState().withProperty(BlockTaintFibre.NORTH, false).withProperty(BlockTaintFibre.EAST, false).withProperty(BlockTaintFibre.SOUTH, false).withProperty(BlockTaintFibre.WEST, false).withProperty(BlockTaintFibre.UP, false).withProperty(BlockTaintFibre.DOWN, false).withProperty(BlockTaintFibre.GROWTH1, false).withProperty(BlockTaintFibre.GROWTH2, false).withProperty(BlockTaintFibre.GROWTH3, false).withProperty(BlockTaintFibre.GROWTH4, false));
    }
    
    public SoundType getSoundType() {
        return SoundsTC.GORE;
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 3;
    }
    
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 3;
    }
    
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return MapColor.PURPLE;
    }
    
    public void die(World world, BlockPos pos, IBlockState blockState) {
        world.setBlockToAir(pos);
    }
    
    protected boolean canSilkHarvest() {
        return false;
    }
    
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemById(0);
    }
    
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        state = getActualState(state, worldIn, pos);
        if (state instanceof IBlockState && state.getValue(BlockTaintFibre.GROWTH3)) {
            if (worldIn.rand.nextInt(5) <= fortune) {
                spawnAsEntity(worldIn, pos, ConfigItems.FLUX_CRYSTAL.copy());
            }
            AuraHelper.polluteAura(worldIn, pos, 1.0f, true);
        }
    }
    
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (!world.isRemote) {
            state = getActualState(state, world, pos);
            if (state instanceof IBlockState) {
                if (!(boolean)state.getValue(BlockTaintFibre.GROWTH1) && !(boolean)state.getValue(BlockTaintFibre.GROWTH2) && !(boolean)state.getValue(BlockTaintFibre.GROWTH3) && !(boolean)state.getValue(BlockTaintFibre.GROWTH4) && isOnlyAdjacentToTaint(world, pos)) {
                    die(world, pos, state);
                }
                else if (!TaintHelper.isNearTaintSeed(world, pos)) {
                    die(world, pos, state);
                }
                else {
                    TaintHelper.spreadFibres(world, pos);
                }
            }
        }
    }
    
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2) {
        state = getActualState(state, worldIn, pos);
        if (state instanceof IBlockState && !(boolean)state.getValue(BlockTaintFibre.GROWTH1) && !(boolean)state.getValue(BlockTaintFibre.GROWTH2) && !(boolean)state.getValue(BlockTaintFibre.GROWTH3) && !(boolean)state.getValue(BlockTaintFibre.GROWTH4) && isOnlyAdjacentToTaint(worldIn, pos)) {
            worldIn.setBlockToAir(pos);
        }
    }
    
    public static int getAdjacentTaint(IBlockAccess world, BlockPos pos) {
        int count = 0;
        for (EnumFacing dir : EnumFacing.VALUES) {
            if (world.getBlockState(pos.offset(dir)).getMaterial() != ThaumcraftMaterials.MATERIAL_TAINT) {
                ++count;
            }
        }
        return count;
    }
    
    public static boolean isOnlyAdjacentToTaint(World world, BlockPos pos) {
        for (EnumFacing dir : EnumFacing.VALUES) {
            if (!world.isAirBlock(pos.offset(dir)) && world.getBlockState(pos.offset(dir)).getMaterial() != ThaumcraftMaterials.MATERIAL_TAINT && world.getBlockState(pos.offset(dir)).getBlock().isSideSolid(world.getBlockState(pos.offset(dir)), world, pos.offset(dir), dir.getOpposite())) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isHemmedByTaint(World world, BlockPos pos) {
        int c = 0;
        for (EnumFacing dir : EnumFacing.VALUES) {
            IBlockState block = world.getBlockState(pos.offset(dir));
            if (block.getMaterial() == ThaumcraftMaterials.MATERIAL_TAINT) {
                ++c;
            }
            else if (world.isAirBlock(pos.offset(dir))) {
                --c;
            }
            else if (!block.getMaterial().isLiquid() && !block.isSideSolid(world, pos.offset(dir), dir.getOpposite())) {
                --c;
            }
        }
        return c > 0;
    }
    
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        if (!world.isRemote && entity instanceof EntityLivingBase && !((EntityLivingBase)entity).isEntityUndead() && world.rand.nextInt(750) == 0) {
            ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(PotionFluxTaint.instance, 200, 0, false, true));
        }
    }
    
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int eventID, int eventParam) {
        if (eventID == 1) {
            if (worldIn.isRemote) {
                worldIn.playSound(null, pos, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 0.1f, 0.9f + worldIn.rand.nextFloat() * 0.2f);
            }
            return true;
        }
        return super.eventReceived(state, worldIn, pos, eventID, eventParam);
    }
    
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }
    
    private boolean drawAt(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        IBlockState b = worldIn.getBlockState(pos);
        return b.getBlock() != BlocksTC.taintFibre && b.getBlock() != BlocksTC.taintFeature && b.isSideSolid(worldIn, pos, side.getOpposite());
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onBlockHighlight(DrawBlockHighlightEvent event) {
        if (event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK && event.getPlayer().world.getBlockState(event.getTarget().getBlockPos()).getBlock() == this) {
            RayTracer.retraceBlock(event.getPlayer().world, event.getPlayer(), event.getTarget().getBlockPos());
        }
    }
    
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
        if (drawAt(world, pos.up(), EnumFacing.UP)) {
            cuboids.add(new IndexedCuboid6(0, new Cuboid6(BlockTaintFibre.AABB_UP.offset(pos))));
        }
        if (drawAt(world, pos.down(), EnumFacing.DOWN)) {
            cuboids.add(new IndexedCuboid6(1, new Cuboid6(BlockTaintFibre.AABB_DOWN.offset(pos))));
        }
        if (drawAt(world, pos.east(), EnumFacing.EAST)) {
            cuboids.add(new IndexedCuboid6(2, new Cuboid6(BlockTaintFibre.AABB_EAST.offset(pos))));
        }
        if (drawAt(world, pos.west(), EnumFacing.WEST)) {
            cuboids.add(new IndexedCuboid6(3, new Cuboid6(BlockTaintFibre.AABB_WEST.offset(pos))));
        }
        if (drawAt(world, pos.south(), EnumFacing.SOUTH)) {
            cuboids.add(new IndexedCuboid6(4, new Cuboid6(BlockTaintFibre.AABB_SOUTH.offset(pos))));
        }
        if (drawAt(world, pos.north(), EnumFacing.NORTH)) {
            cuboids.add(new IndexedCuboid6(5, new Cuboid6(BlockTaintFibre.AABB_NORTH.offset(pos))));
        }
        IBlockState ss = getActualState(world.getBlockState(pos), world, pos);
        if (ss.getBlock() == this && ss instanceof IBlockState) {
            if (ss.getValue(BlockTaintFibre.GROWTH1)) {
                cuboids.add(new IndexedCuboid6(6, new Cuboid6(new AxisAlignedBB(0.10000000149011612, 0.0, 0.10000000149011612, 0.8999999761581421, 0.4000000059604645, 0.8999999761581421).offset(pos))));
            }
            else if (ss.getValue(BlockTaintFibre.GROWTH2)) {
                cuboids.add(new IndexedCuboid6(6, new Cuboid6(new AxisAlignedBB(0.20000000298023224, 0.0, 0.20000000298023224, 0.800000011920929, 1.0, 0.800000011920929).offset(pos))));
            }
            else if (ss.getValue(BlockTaintFibre.GROWTH3)) {
                cuboids.add(new IndexedCuboid6(6, new Cuboid6(new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.3125, 0.75).offset(pos))));
            }
            else if (ss.getValue(BlockTaintFibre.GROWTH4)) {
                cuboids.add(new IndexedCuboid6(6, new Cuboid6(new AxisAlignedBB(0.10000000149011612, 0.30000001192092896, 0.10000000149011612, 0.8999999761581421, 1.0, 0.8999999761581421).offset(pos))));
            }
        }
        ArrayList<ExtendedMOP> list = new ArrayList<ExtendedMOP>();
        rayTracer.rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids, new BlockCoord(pos), this, list);
        return (list.size() > 0) ? list.get(0) : super.collisionRayTrace(state, world, pos, start, end);
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState s, IBlockAccess source, BlockPos pos) {
        return BlockTaintFibre.AABB_EMPTY;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState s, World world, BlockPos pos) {
        IBlockState state = getActualState(world.getBlockState(pos), world, pos);
        if (state.getBlock() == this && state instanceof IBlockState) {
            if (state.getValue(BlockTaintFibre.GROWTH1)) {
                return new AxisAlignedBB(0.10000000149011612, 0.0, 0.10000000149011612, 0.8999999761581421, 0.4000000059604645, 0.8999999761581421).offset(pos);
            }
            if (state.getValue(BlockTaintFibre.GROWTH2)) {
                return new AxisAlignedBB(0.20000000298023224, 0.0, 0.20000000298023224, 0.800000011920929, 1.0, 0.800000011920929).offset(pos);
            }
            if (state.getValue(BlockTaintFibre.GROWTH3)) {
                return new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.3125, 0.75).offset(pos);
            }
            if (state.getValue(BlockTaintFibre.GROWTH4)) {
                return new AxisAlignedBB(0.10000000149011612, 0.30000001192092896, 0.10000000149011612, 0.8999999761581421, 1.0, 0.8999999761581421).offset(pos);
            }
        }
        RayTraceResult hit = RayTracer.retraceBlock(world, Minecraft.getMinecraft().player, pos);
        if (hit != null) {
            switch (hit.subHit) {
                case 0: {
                    return BlockTaintFibre.AABB_UP.offset(pos);
                }
                case 1: {
                    return BlockTaintFibre.AABB_DOWN.offset(pos);
                }
                case 2: {
                    return BlockTaintFibre.AABB_EAST.offset(pos);
                }
                case 3: {
                    return BlockTaintFibre.AABB_WEST.offset(pos);
                }
                case 4: {
                    return BlockTaintFibre.AABB_SOUTH.offset(pos);
                }
                case 5: {
                    return BlockTaintFibre.AABB_NORTH.offset(pos);
                }
            }
        }
        return BlockTaintFibre.AABB_EMPTY;
    }
    
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        if (drawAt(worldIn, pos.up(), EnumFacing.UP)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockTaintFibre.AABB_UP);
        }
        if (drawAt(worldIn, pos.down(), EnumFacing.DOWN)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockTaintFibre.AABB_DOWN);
        }
        if (drawAt(worldIn, pos.east(), EnumFacing.EAST)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockTaintFibre.AABB_EAST);
        }
        if (drawAt(worldIn, pos.west(), EnumFacing.WEST)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockTaintFibre.AABB_WEST);
        }
        if (drawAt(worldIn, pos.south(), EnumFacing.SOUTH)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockTaintFibre.AABB_SOUTH);
        }
        if (drawAt(worldIn, pos.north(), EnumFacing.NORTH)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockTaintFibre.AABB_NORTH);
        }
    }
    
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }
    
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
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
    
    public int getLightValue(IBlockState state2, IBlockAccess world, BlockPos pos) {
        IBlockState state3 = getActualState(world.getBlockState(pos), world, pos);
        if (state3.getBlock() == this && state3 instanceof IBlockState) {
            return state3.getValue(BlockTaintFibre.GROWTH3) ? 12 : ((state3.getValue(BlockTaintFibre.GROWTH2) || state3.getValue(BlockTaintFibre.GROWTH4)) ? 6 : super.getLightValue(state2, world, pos));
        }
        return super.getLightValue(state2, world, pos);
    }
    
    private Boolean[] makeConnections(IBlockState state, IBlockAccess world, BlockPos pos) {
        Boolean[] cons = { false, false, false, false, false, false };
        int a = 0;
        for (EnumFacing face : EnumFacing.VALUES) {
            if (drawAt(world, pos.offset(face), face)) {
                cons[a] = true;
            }
            ++a;
        }
        return cons;
    }
    
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        Boolean[] cons = makeConnections(state, worldIn, pos);
        boolean d = drawAt(worldIn, pos.down(), EnumFacing.DOWN);
        boolean u = drawAt(worldIn, pos.up(), EnumFacing.UP);
        int growth = 0;
        Random rand = new Random(pos.toLong());
        int q = rand.nextInt(50);
        if (d) {
            if (q < 4) {
                growth = 1;
            }
            else if (q == 4 || q == 5) {
                growth = 2;
            }
            else if (q == 6) {
                growth = 3;
            }
        }
        if (u && q > 47) {
            growth = 4;
        }
        try {
            return state.withProperty(BlockTaintFibre.DOWN, cons[0]).withProperty(BlockTaintFibre.UP, cons[1]).withProperty(BlockTaintFibre.NORTH, cons[2]).withProperty(BlockTaintFibre.SOUTH, cons[3]).withProperty(BlockTaintFibre.WEST, cons[4]).withProperty(BlockTaintFibre.EAST, cons[5]).withProperty(BlockTaintFibre.GROWTH1, (growth == 1)).withProperty(BlockTaintFibre.GROWTH2, (growth == 2)).withProperty(BlockTaintFibre.GROWTH3, (growth == 3)).withProperty(BlockTaintFibre.GROWTH4, (growth == 4));
        }
        catch (Exception e) {
            return state;
        }
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockTaintFibre.NORTH, BlockTaintFibre.EAST, BlockTaintFibre.SOUTH, BlockTaintFibre.WEST, BlockTaintFibre.UP, BlockTaintFibre.DOWN, BlockTaintFibre.GROWTH1, BlockTaintFibre.GROWTH2, BlockTaintFibre.GROWTH3, BlockTaintFibre.GROWTH4);
    }
    
    static {
        NORTH = PropertyBool.create("north");
        EAST = PropertyBool.create("east");
        SOUTH = PropertyBool.create("south");
        WEST = PropertyBool.create("west");
        UP = PropertyBool.create("up");
        DOWN = PropertyBool.create("down");
        GROWTH1 = PropertyBool.create("growth1");
        GROWTH2 = PropertyBool.create("growth2");
        GROWTH3 = PropertyBool.create("growth3");
        GROWTH4 = PropertyBool.create("growth4");
        AABB_EMPTY = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        AABB_UP = new AxisAlignedBB(0.0, 0.949999988079071, 0.0, 1.0, 1.0, 1.0);
        AABB_DOWN = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.05000000074505806, 1.0);
        AABB_EAST = new AxisAlignedBB(0.949999988079071, 0.0, 0.0, 1.0, 1.0, 1.0);
        AABB_WEST = new AxisAlignedBB(0.0, 0.0, 0.0, 0.05000000074505806, 1.0, 1.0);
        AABB_SOUTH = new AxisAlignedBB(0.0, 0.0, 0.949999988079071, 1.0, 1.0, 1.0);
        AABB_NORTH = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.05000000074505806);
    }
}
