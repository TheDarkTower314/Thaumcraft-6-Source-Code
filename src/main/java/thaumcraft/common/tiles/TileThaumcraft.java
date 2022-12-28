package thaumcraft.common.tiles;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.tiles.PacketTileToClient;
import thaumcraft.common.lib.network.tiles.PacketTileToServer;


public class TileThaumcraft extends TileEntity
{
    public void sendMessageToClient(NBTTagCompound nbt, @Nullable EntityPlayerMP player) {
        if (player == null) {
            if (getWorld() != null) {
                PacketHandler.INSTANCE.sendToAllAround(new PacketTileToClient(getPos(), nbt), new NetworkRegistry.TargetPoint(getWorld().provider.getDimension(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 128.0));
            }
        }
        else {
            PacketHandler.INSTANCE.sendTo(new PacketTileToClient(getPos(), nbt), player);
        }
    }
    
    public void sendMessageToServer(NBTTagCompound nbt) {
        PacketHandler.INSTANCE.sendToServer(new PacketTileToServer(getPos(), nbt));
    }
    
    public void messageFromServer(NBTTagCompound nbt) {
    }
    
    public void messageFromClient(NBTTagCompound nbt, EntityPlayerMP player) {
    }
    
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        readSyncNBT(nbt);
    }
    
    public void readSyncNBT(NBTTagCompound nbt) {
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        return writeSyncNBT(super.writeToNBT(nbt));
    }
    
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
        return nbt;
    }
    
    public void syncTile(boolean rerender) {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 2 + (rerender ? 4 : 0));
    }
    
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, -9, getUpdateTag());
    }
    
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readSyncNBT(pkt.getNbtCompound());
    }
    
    public NBTTagCompound getUpdateTag() {
        return writeSyncNBT(setupNbt());
    }
    
    private NBTTagCompound setupNbt() {
        NBTTagCompound nbt = super.writeToNBT(new NBTTagCompound());
        nbt.removeTag("ForgeData");
        nbt.removeTag("ForgeCaps");
        return nbt;
    }
    
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
    
    public EnumFacing getFacing() {
        try {
            return EnumFacing.getFront(getBlockMetadata() & 0x7);
        }
        catch (Exception ex) {
            return EnumFacing.UP;
        }
    }
    
    public boolean gettingPower() {
        return world.isBlockPowered(getPos());
    }
}
