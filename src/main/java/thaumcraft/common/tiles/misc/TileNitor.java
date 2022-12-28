package thaumcraft.common.tiles.misc;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.client.fx.FXDispatcher;


public class TileNitor extends TileEntity implements ITickable
{
    int count;
    
    public TileNitor() {
        count = 0;
    }
    
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
    
    public void update() {
        if (world.isRemote) {
            IBlockState state = world.getBlockState(getPos());
            FXDispatcher.INSTANCE.drawNitorFlames(pos.getX() + 0.5f + world.rand.nextGaussian() * 0.025, pos.getY() + 0.45f + world.rand.nextGaussian() * 0.025, pos.getZ() + 0.5f + world.rand.nextGaussian() * 0.025, world.rand.nextGaussian() * 0.0025, world.rand.nextFloat() * 0.06, world.rand.nextGaussian() * 0.0025, state.getBlock().getMapColor(state, world, getPos()).colorValue, 0);
            if (count++ % 10 == 0) {
                FXDispatcher.INSTANCE.drawNitorCore(pos.getX() + 0.5f, pos.getY() + 0.49f, pos.getZ() + 0.5f, 0.0, 0.0, 0.0);
            }
        }
    }
}
