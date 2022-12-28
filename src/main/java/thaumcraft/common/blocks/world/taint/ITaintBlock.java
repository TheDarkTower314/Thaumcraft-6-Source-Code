package thaumcraft.common.blocks.world.taint;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public interface ITaintBlock
{
    void die(World p0, BlockPos p1, IBlockState p2);
}
