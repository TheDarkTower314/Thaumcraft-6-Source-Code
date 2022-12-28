package thaumcraft.common.tiles.devices;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileLampArcane extends TileThaumcraft implements ITickable
{
    public int rad;
    public int rad1;
    
    public TileLampArcane() {
        rad1 = 0;
    }
    
    public void update() {
        if (!world.isRemote && world.getTotalWorldTime() % 5L == 0L && !gettingPower()) {
            int x = world.rand.nextInt(16) - world.rand.nextInt(16);
            int y = world.rand.nextInt(16) - world.rand.nextInt(16);
            int z = world.rand.nextInt(16) - world.rand.nextInt(16);
            BlockPos bp = pos.add(x, y, z);
            if (bp.getY() > world.getPrecipitationHeight(bp).getY() + 4) {
                bp = world.getPrecipitationHeight(bp).up(4);
            }
            if (bp.getY() < 5) {
                bp = new BlockPos(bp.getX(), 5, bp.getZ());
            }
            if (world.isAirBlock(bp) && world.getBlockState(bp) != BlocksTC.effectGlimmer.getDefaultState() && world.getLightFor(EnumSkyBlock.BLOCK, bp) < 11 && BlockUtils.hasLOS(getWorld(), getPos(), bp)) {
                world.setBlockState(bp, BlocksTC.effectGlimmer.getDefaultState(), 3);
            }
        }
    }
    
    public void removeLights() {
        for (int x = -15; x <= 15; ++x) {
            for (int y = -15; y <= 15; ++y) {
                for (int z = -15; z <= 15; ++z) {
                    BlockPos bp = pos.add(x, y, z);
                    if (world.getBlockState(bp) == BlocksTC.effectGlimmer.getDefaultState()) {
                        world.setBlockToAir(bp);
                    }
                }
            }
        }
    }
}
