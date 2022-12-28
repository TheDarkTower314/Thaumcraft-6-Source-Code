package thaumcraft.common.blocks.world.taint;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.world.aura.AuraHandler;


public class TaintHelper
{
    private static ConcurrentHashMap<Integer, ArrayList<BlockPos>> taintSeeds;
    
    public static void addTaintSeed(World world, BlockPos pos) {
        ArrayList<BlockPos> locs = TaintHelper.taintSeeds.get(world.provider.getDimension());
        if (locs == null) {
            locs = new ArrayList<BlockPos>();
        }
        locs.add(pos);
        TaintHelper.taintSeeds.put(world.provider.getDimension(), locs);
    }
    
    public static void removeTaintSeed(World world, BlockPos pos) {
        ArrayList<BlockPos> locs = TaintHelper.taintSeeds.get(world.provider.getDimension());
        if (locs != null && !locs.isEmpty()) {
            locs.remove(pos);
        }
    }
    
    public static boolean isNearTaintSeed(World world, BlockPos pos) {
        double area = ModConfig.CONFIG_WORLD.taintSpreadArea * ModConfig.CONFIG_WORLD.taintSpreadArea;
        ArrayList<BlockPos> locs = TaintHelper.taintSeeds.get(world.provider.getDimension());
        if (locs != null && !locs.isEmpty()) {
            for (BlockPos p : locs) {
                if (p.distanceSq(pos) <= area) {
                    if (EntityUtils.getEntitiesInRange(world, p, null, (Class<? extends Entity>)EntityTaintSeed.class, 1.0).size() <= 0) {
                        removeTaintSeed(world, p);
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean isAtTaintSeedEdge(World world, BlockPos pos) {
        double area = ModConfig.CONFIG_WORLD.taintSpreadArea * ModConfig.CONFIG_WORLD.taintSpreadArea;
        double fringe = ModConfig.CONFIG_WORLD.taintSpreadArea * 0.8 * (ModConfig.CONFIG_WORLD.taintSpreadArea * 0.8);
        ArrayList<BlockPos> locs = TaintHelper.taintSeeds.get(world.provider.getDimension());
        if (locs != null && !locs.isEmpty()) {
            for (BlockPos p : locs) {
                double d = p.distanceSq(pos);
                if (d < area && d > fringe) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void spreadFibres(World world, BlockPos pos) {
        spreadFibres(world, pos, false);
    }
    
    public static void spreadFibres(World world, BlockPos pos, boolean ignore) {
        if (!ignore && ModConfig.CONFIG_MISC.wussMode) {
            return;
        }
        float mod = 0.001f + AuraHandler.getFluxSaturation(world, pos) * 2.0f;
        if (!ignore && world.rand.nextFloat() > ModConfig.CONFIG_WORLD.taintSpreadRate / 100.0f * mod) {
            return;
        }
        if (isNearTaintSeed(world, pos)) {
            int xx = pos.getX() + world.rand.nextInt(3) - 1;
            int yy = pos.getY() + world.rand.nextInt(3) - 1;
            int zz = pos.getZ() + world.rand.nextInt(3) - 1;
            BlockPos t = new BlockPos(xx, yy, zz);
            if (t.equals(pos)) {
                return;
            }
            IBlockState bs = world.getBlockState(t);
            Material bm = bs.getBlock().getMaterial(bs);
            float bh = bs.getBlock().getBlockHardness(bs, world, t);
            if (bh < 0.0f || bh > 10.0f) {
                return;
            }
            if (!bs.getBlock().isLeaves(bs, world, t) && !bm.isLiquid() && (world.isAirBlock(t) || bs.getBlock().isReplaceable(world, t) || bs.getBlock() instanceof BlockFlower || bs.getBlock() instanceof IPlantable) && BlockUtils.isAdjacentToSolidBlock(world, t) && !BlockTaintFibre.isOnlyAdjacentToTaint(world, t)) {
                world.setBlockState(t, BlocksTC.taintFibre.getDefaultState());
                world.addBlockEvent(t, BlocksTC.taintFibre, 1, 0);
                AuraHelper.drainFlux(world, t, 0.01f, false);
                return;
            }
            if (bs.getBlock().isLeaves(bs, world, t)) {
                EnumFacing face = null;
                if (world.rand.nextFloat() < 0.6 && (face = BlockUtils.getFaceBlockTouching(world, t, BlocksTC.taintLog)) != null) {
                    world.setBlockState(t, BlocksTC.taintFeature.getDefaultState().withProperty((IProperty)IBlockFacing.FACING, (Comparable)face.getOpposite()));
                }
                else {
                    world.setBlockState(t, BlocksTC.taintFibre.getDefaultState());
                    world.addBlockEvent(t, BlocksTC.taintFibre, 1, 0);
                    AuraHelper.drainFlux(world, t, 0.01f, false);
                }
                return;
            }
            if (BlockTaintFibre.isHemmedByTaint(world, t) && bs.getBlockHardness(world, t) < 5.0f) {
                if (Utils.isWoodLog(world, t) && bs.getMaterial() != ThaumcraftMaterials.MATERIAL_TAINT) {
                    world.setBlockState(t, BlocksTC.taintLog.getDefaultState().withProperty(BlockTaintLog.AXIS, (Comparable)BlockUtils.getBlockAxis(world, t)));
                    return;
                }
                if (bs.getBlock() == Blocks.RED_MUSHROOM_BLOCK || bs.getBlock() == Blocks.BROWN_MUSHROOM_BLOCK || bm == Material.GOURD || bm == Material.CACTUS || bm == Material.CORAL || bm == Material.SPONGE || bm == Material.WOOD) {
                    world.setBlockState(t, BlocksTC.taintCrust.getDefaultState());
                    world.addBlockEvent(t, BlocksTC.taintCrust, 1, 0);
                    AuraHelper.drainFlux(world, t, 0.01f, false);
                    return;
                }
                if (bm == Material.SAND || bm == Material.GROUND || bm == Material.GRASS || bm == Material.CLAY) {
                    world.setBlockState(t, BlocksTC.taintSoil.getDefaultState());
                    world.addBlockEvent(t, BlocksTC.taintSoil, 1, 0);
                    AuraHelper.drainFlux(world, t, 0.01f, false);
                    return;
                }
                if (bm == Material.ROCK) {
                    world.setBlockState(t, BlocksTC.taintRock.getDefaultState());
                    world.addBlockEvent(t, BlocksTC.taintRock, 1, 0);
                    AuraHelper.drainFlux(world, t, 0.01f, false);
                    return;
                }
            }
            if ((bs.getBlock() == BlocksTC.taintSoil || bs.getBlock() == BlocksTC.taintRock) && world.isAirBlock(t.up()) && AuraHelper.getFlux(world, t) >= 5.0f && world.rand.nextFloat() < ModConfig.CONFIG_WORLD.taintSpreadRate / 100.0f * 0.33 && isAtTaintSeedEdge(world, t)) {
                EntityTaintSeed e = new EntityTaintSeed(world);
                e.setLocationAndAngles(t.getX() + 0.5f, t.up().getY(), t.getZ() + 0.5f, (float)world.rand.nextInt(360), 0.0f);
                if (e.getCanSpawnHere()) {
                    AuraHelper.drainFlux(world, t, 5.0f, false);
                    world.spawnEntity(e);
                }
            }
        }
    }
    
    static {
        TaintHelper.taintSeeds = new ConcurrentHashMap<Integer, ArrayList<BlockPos>>();
    }
}
