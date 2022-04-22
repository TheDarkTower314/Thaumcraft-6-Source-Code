// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.world.taint;

import net.minecraft.block.state.BlockStateContainer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import java.util.List;
import thaumcraft.codechicken.lib.vec.BlockCoord;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.codechicken.lib.raytracer.ExtendedMOP;
import java.util.ArrayList;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import java.util.LinkedList;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import thaumcraft.api.potions.PotionFluxTaint;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import thaumcraft.api.aura.AuraHelper;
import net.minecraft.item.Item;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.api.ThaumcraftMaterials;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import net.minecraft.block.properties.PropertyBool;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.block.Block;

@Mod.EventBusSubscriber({ Side.CLIENT })
public class BlockTaintFibre extends Block implements ITaintBlock
{
    public static final PropertyBool NORTH;
    public static final PropertyBool EAST;
    public static final PropertyBool SOUTH;
    public static final PropertyBool WEST;
    public static final PropertyBool UP;
    public static final PropertyBool DOWN;
    public static final PropertyBool GROWTH1;
    public static final PropertyBool GROWTH2;
    public static final PropertyBool GROWTH3;
    public static final PropertyBool GROWTH4;
    private RayTracer rayTracer;
    protected static final AxisAlignedBB AABB_EMPTY;
    protected static final AxisAlignedBB AABB_UP;
    protected static final AxisAlignedBB AABB_DOWN;
    protected static final AxisAlignedBB AABB_EAST;
    protected static final AxisAlignedBB AABB_WEST;
    protected static final AxisAlignedBB AABB_SOUTH;
    protected static final AxisAlignedBB AABB_NORTH;
    
    public BlockTaintFibre() {
        super(ThaumcraftMaterials.MATERIAL_TAINT);
        this.rayTracer = new RayTracer();
        this.setUnlocalizedName("taint_fibre");
        this.setRegistryName("thaumcraft", "taint_fibre");
        this.setHardness(1.0f);
        this.setSoundType(SoundsTC.GORE);
        this.setTickRandomly(true);
        this.setCreativeTab(ConfigItems.TABTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockTaintFibre.NORTH, false).withProperty(BlockTaintFibre.EAST, false).withProperty(BlockTaintFibre.SOUTH, false).withProperty(BlockTaintFibre.WEST, false).withProperty(BlockTaintFibre.UP, false).withProperty(BlockTaintFibre.DOWN, false).withProperty(BlockTaintFibre.GROWTH1, false).withProperty(BlockTaintFibre.GROWTH2, false).withProperty(BlockTaintFibre.GROWTH3, false).withProperty(BlockTaintFibre.GROWTH4, false));
    }
    
    public SoundType getSoundType() {
        return SoundsTC.GORE;
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public int getFlammability(final IBlockAccess world, final BlockPos pos, final EnumFacing face) {
        return 3;
    }
    
    public int getFireSpreadSpeed(final IBlockAccess world, final BlockPos pos, final EnumFacing face) {
        return 3;
    }
    
    public MapColor getMapColor(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        return MapColor.PURPLE;
    }
    
    public void die(final World world, final BlockPos pos, final IBlockState blockState) {
        world.setBlockToAir(pos);
    }
    
    protected boolean canSilkHarvest() {
        return false;
    }
    
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Item.getItemById(0);
    }
    
    public void dropBlockAsItemWithChance(final World worldIn, final BlockPos pos, IBlockState state, final float chance, final int fortune) {
        state = this.getActualState(state, worldIn, pos);
        if (state instanceof IBlockState && state.getValue(BlockTaintFibre.GROWTH3)) {
            if (worldIn.rand.nextInt(5) <= fortune) {
                spawnAsEntity(worldIn, pos, ConfigItems.FLUX_CRYSTAL.copy());
            }
            AuraHelper.polluteAura(worldIn, pos, 1.0f, true);
        }
    }
    
    public void updateTick(final World world, final BlockPos pos, IBlockState state, final Random random) {
        if (!world.isRemote) {
            state = this.getActualState(state, world, pos);
            if (state instanceof IBlockState) {
                if (!(boolean)state.getValue(BlockTaintFibre.GROWTH1) && !(boolean)state.getValue(BlockTaintFibre.GROWTH2) && !(boolean)state.getValue(BlockTaintFibre.GROWTH3) && !(boolean)state.getValue(BlockTaintFibre.GROWTH4) && isOnlyAdjacentToTaint(world, pos)) {
                    this.die(world, pos, state);
                }
                else if (!TaintHelper.isNearTaintSeed(world, pos)) {
                    this.die(world, pos, state);
                }
                else {
                    TaintHelper.spreadFibres(world, pos);
                }
            }
        }
    }
    
    public void neighborChanged(IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos pos2) {
        state = this.getActualState(state, worldIn, pos);
        if (state instanceof IBlockState && !(boolean)state.getValue(BlockTaintFibre.GROWTH1) && !(boolean)state.getValue(BlockTaintFibre.GROWTH2) && !(boolean)state.getValue(BlockTaintFibre.GROWTH3) && !(boolean)state.getValue(BlockTaintFibre.GROWTH4) && isOnlyAdjacentToTaint(worldIn, pos)) {
            worldIn.setBlockToAir(pos);
        }
    }
    
    public static int getAdjacentTaint(final IBlockAccess world, final BlockPos pos) {
        int count = 0;
        for (final EnumFacing dir : EnumFacing.VALUES) {
            if (world.getBlockState(pos.offset(dir)).getMaterial() != ThaumcraftMaterials.MATERIAL_TAINT) {
                ++count;
            }
        }
        return count;
    }
    
    public static boolean isOnlyAdjacentToTaint(final World world, final BlockPos pos) {
        for (final EnumFacing dir : EnumFacing.VALUES) {
            if (!world.isAirBlock(pos.offset(dir)) && world.getBlockState(pos.offset(dir)).getMaterial() != ThaumcraftMaterials.MATERIAL_TAINT && world.getBlockState(pos.offset(dir)).getBlock().isSideSolid(world.getBlockState(pos.offset(dir)), world, pos.offset(dir), dir.getOpposite())) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isHemmedByTaint(final World world, final BlockPos pos) {
        int c = 0;
        for (final EnumFacing dir : EnumFacing.VALUES) {
            final IBlockState block = world.getBlockState(pos.offset(dir));
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
    
    public void onEntityWalk(final World world, final BlockPos pos, final Entity entity) {
        if (!world.isRemote && entity instanceof EntityLivingBase && !((EntityLivingBase)entity).isEntityUndead() && world.rand.nextInt(750) == 0) {
            ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(PotionFluxTaint.instance, 200, 0, false, true));
        }
    }
    
    public boolean eventReceived(final IBlockState state, final World worldIn, final BlockPos pos, final int eventID, final int eventParam) {
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
    
    public boolean isSideSolid(final IBlockState base_state, final IBlockAccess world, final BlockPos pos, final EnumFacing side) {
        return false;
    }
    
    private boolean drawAt(final IBlockAccess worldIn, final BlockPos pos, final EnumFacing side) {
        final IBlockState b = worldIn.getBlockState(pos);
        return b.getBlock() != BlocksTC.taintFibre && b.getBlock() != BlocksTC.taintFeature && b.isSideSolid(worldIn, pos, side.getOpposite());
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onBlockHighlight(final DrawBlockHighlightEvent event) {
        if (event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK && event.getPlayer().world.getBlockState(event.getTarget().getBlockPos()).getBlock() == this) {
            RayTracer.retraceBlock(event.getPlayer().world, event.getPlayer(), event.getTarget().getBlockPos());
        }
    }
    
    public RayTraceResult collisionRayTrace(final IBlockState state, final World world, final BlockPos pos, final Vec3d start, final Vec3d end) {
        final List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
        if (this.drawAt(world, pos.up(), EnumFacing.UP)) {
            cuboids.add(new IndexedCuboid6(0, new Cuboid6(BlockTaintFibre.AABB_UP.offset(pos))));
        }
        if (this.drawAt(world, pos.down(), EnumFacing.DOWN)) {
            cuboids.add(new IndexedCuboid6(1, new Cuboid6(BlockTaintFibre.AABB_DOWN.offset(pos))));
        }
        if (this.drawAt(world, pos.east(), EnumFacing.EAST)) {
            cuboids.add(new IndexedCuboid6(2, new Cuboid6(BlockTaintFibre.AABB_EAST.offset(pos))));
        }
        if (this.drawAt(world, pos.west(), EnumFacing.WEST)) {
            cuboids.add(new IndexedCuboid6(3, new Cuboid6(BlockTaintFibre.AABB_WEST.offset(pos))));
        }
        if (this.drawAt(world, pos.south(), EnumFacing.SOUTH)) {
            cuboids.add(new IndexedCuboid6(4, new Cuboid6(BlockTaintFibre.AABB_SOUTH.offset(pos))));
        }
        if (this.drawAt(world, pos.north(), EnumFacing.NORTH)) {
            cuboids.add(new IndexedCuboid6(5, new Cuboid6(BlockTaintFibre.AABB_NORTH.offset(pos))));
        }
        final IBlockState ss = this.getActualState(world.getBlockState(pos), world, pos);
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
        final ArrayList<ExtendedMOP> list = new ArrayList<ExtendedMOP>();
        this.rayTracer.rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids, new BlockCoord(pos), this, list);
        return (list.size() > 0) ? list.get(0) : super.collisionRayTrace(state, world, pos, start, end);
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState s, final IBlockAccess source, final BlockPos pos) {
        return BlockTaintFibre.AABB_EMPTY;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(final IBlockState s, final World world, final BlockPos pos) {
        final IBlockState state = this.getActualState(world.getBlockState(pos), world, pos);
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
        final RayTraceResult hit = RayTracer.retraceBlock(world, Minecraft.getMinecraft().player, pos);
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
    
    public void addCollisionBoxToList(final IBlockState state, final World worldIn, final BlockPos pos, final AxisAlignedBB entityBox, final List<AxisAlignedBB> collidingBoxes, @Nullable final Entity entityIn, final boolean isActualState) {
        if (this.drawAt(worldIn, pos.up(), EnumFacing.UP)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockTaintFibre.AABB_UP);
        }
        if (this.drawAt(worldIn, pos.down(), EnumFacing.DOWN)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockTaintFibre.AABB_DOWN);
        }
        if (this.drawAt(worldIn, pos.east(), EnumFacing.EAST)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockTaintFibre.AABB_EAST);
        }
        if (this.drawAt(worldIn, pos.west(), EnumFacing.WEST)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockTaintFibre.AABB_WEST);
        }
        if (this.drawAt(worldIn, pos.south(), EnumFacing.SOUTH)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockTaintFibre.AABB_SOUTH);
        }
        if (this.drawAt(worldIn, pos.north(), EnumFacing.NORTH)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockTaintFibre.AABB_NORTH);
        }
    }
    
    public boolean isReplaceable(final IBlockAccess worldIn, final BlockPos pos) {
        return true;
    }
    
    public boolean isPassable(final IBlockAccess worldIn, final BlockPos pos) {
        return true;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public int getMetaFromState(final IBlockState state) {
        return 0;
    }
    
    public int getLightValue(final IBlockState state2, final IBlockAccess world, final BlockPos pos) {
        final IBlockState state3 = this.getActualState(world.getBlockState(pos), world, pos);
        if (state3.getBlock() == this && state3 instanceof IBlockState) {
            return state3.getValue(BlockTaintFibre.GROWTH3) ? 12 : ((state3.getValue(BlockTaintFibre.GROWTH2) || state3.getValue(BlockTaintFibre.GROWTH4)) ? 6 : super.getLightValue(state2, world, pos));
        }
        return super.getLightValue(state2, world, pos);
    }
    
    private Boolean[] makeConnections(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        final Boolean[] cons = { false, false, false, false, false, false };
        int a = 0;
        for (final EnumFacing face : EnumFacing.VALUES) {
            if (this.drawAt(world, pos.offset(face), face)) {
                cons[a] = true;
            }
            ++a;
        }
        return cons;
    }
    
    public IBlockState getActualState(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        final Boolean[] cons = this.makeConnections(state, worldIn, pos);
        final boolean d = this.drawAt(worldIn, pos.down(), EnumFacing.DOWN);
        final boolean u = this.drawAt(worldIn, pos.up(), EnumFacing.UP);
        int growth = 0;
        final Random rand = new Random(pos.toLong());
        final int q = rand.nextInt(50);
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
        catch (final Exception e) {
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
