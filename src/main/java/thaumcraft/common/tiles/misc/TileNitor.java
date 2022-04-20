// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.misc;

import net.minecraft.world.IBlockAccess;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.ITickable;
import net.minecraft.tileentity.TileEntity;

public class TileNitor extends TileEntity implements ITickable
{
    int count;
    
    public TileNitor() {
        this.count = 0;
    }
    
    public boolean shouldRefresh(final World world, final BlockPos pos, final IBlockState oldState, final IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
    
    public void update() {
        if (this.world.isRemote) {
            final IBlockState state = this.world.getBlockState(this.getPos());
            FXDispatcher.INSTANCE.drawNitorFlames(this.pos.getX() + 0.5f + this.world.rand.nextGaussian() * 0.025, this.pos.getY() + 0.45f + this.world.rand.nextGaussian() * 0.025, this.pos.getZ() + 0.5f + this.world.rand.nextGaussian() * 0.025, this.world.rand.nextGaussian() * 0.0025, this.world.rand.nextFloat() * 0.06, this.world.rand.nextGaussian() * 0.0025, state.getBlock().getMapColor(state, this.world, this.getPos()).colorValue, 0);
            if (this.count++ % 10 == 0) {
                FXDispatcher.INSTANCE.drawNitorCore(this.pos.getX() + 0.5f, this.pos.getY() + 0.49f, this.pos.getZ() + 0.5f, 0.0, 0.0, 0.0);
            }
        }
    }
}
