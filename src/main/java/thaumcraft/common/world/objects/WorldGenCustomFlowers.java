package thaumcraft.common.world.objects;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;


public class WorldGenCustomFlowers extends WorldGenerator
{
    private Block plantBlock;
    private int plantBlockMeta;
    
    public WorldGenCustomFlowers(Block bi, int md) {
        plantBlock = bi;
        plantBlockMeta = md;
    }
    
    public boolean generate(World world, Random par2Random, BlockPos pos) {
        for (int var6 = 0; var6 < 18; ++var6) {
            int var7 = pos.getX() + par2Random.nextInt(8) - par2Random.nextInt(8);
            int var8 = pos.getY() + par2Random.nextInt(4) - par2Random.nextInt(4);
            int var9 = pos.getZ() + par2Random.nextInt(8) - par2Random.nextInt(8);
            BlockPos bp = new BlockPos(var7, var8, var9);
            if (world.isAirBlock(bp) && (world.getBlockState(bp.down()).getBlock() == Blocks.GRASS || world.getBlockState(bp.down()).getBlock() == Blocks.SAND)) {
                world.setBlockState(bp, plantBlock.getStateFromMeta(plantBlockMeta), 3);
            }
        }
        return true;
    }
}
