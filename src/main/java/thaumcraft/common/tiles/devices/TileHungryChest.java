package thaumcraft.common.tiles.devices;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.blocks.devices.BlockHungryChest;


public class TileHungryChest extends TileEntityChest
{
    public void checkForAdjacentChests() {
        if (!adjacentChestChecked) {
            adjacentChestChecked = true;
        }
    }
    
    public boolean canRenderBreaking() {
        return true;
    }
    
    public void closeInventory(EntityPlayer player) {
        if (!player.isSpectator() && getBlockType() instanceof BlockHungryChest) {
            --numPlayersUsing;
            world.addBlockEvent(pos, getBlockType(), 1, numPlayersUsing);
            world.notifyNeighborsOfStateChange(pos, getBlockType(), true);
            world.notifyNeighborsOfStateChange(pos.down(), getBlockType(), true);
        }
    }
    
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
}
