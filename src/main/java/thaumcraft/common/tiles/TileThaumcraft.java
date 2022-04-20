// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.block.state.IBlockState;
import thaumcraft.common.lib.network.tiles.PacketTileToServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.tiles.PacketTileToClient;
import thaumcraft.common.lib.network.PacketHandler;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileThaumcraft extends TileEntity
{
    public final void sendMessageToClient(final NBTTagCompound nbt, @Nullable final EntityPlayerMP player) {
        if (player == null) {
            if (this.getWorld() != null) {
                PacketHandler.INSTANCE.sendToAllAround(new PacketTileToClient(this.getPos(), nbt), new NetworkRegistry.TargetPoint(this.getWorld().provider.getDimension(), this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, 128.0));
            }
        }
        else {
            PacketHandler.INSTANCE.sendTo(new PacketTileToClient(this.getPos(), nbt), player);
        }
    }
    
    public final void sendMessageToServer(final NBTTagCompound nbt) {
        PacketHandler.INSTANCE.sendToServer(new PacketTileToServer(this.getPos(), nbt));
    }
    
    public void messageFromServer(final NBTTagCompound nbt) {
    }
    
    public void messageFromClient(final NBTTagCompound nbt, final EntityPlayerMP player) {
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.readSyncNBT(nbt);
    }
    
    public void readSyncNBT(final NBTTagCompound nbt) {
    }
    
    public NBTTagCompound writeToNBT(final NBTTagCompound nbt) {
        return this.writeSyncNBT(super.writeToNBT(nbt));
    }
    
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbt) {
        return nbt;
    }
    
    public void syncTile(final boolean rerender) {
        final IBlockState state = this.world.getBlockState(this.pos);
        this.world.notifyBlockUpdate(this.pos, state, state, 2 + (rerender ? 4 : 0));
    }
    
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, -9, this.getUpdateTag());
    }
    
    public void onDataPacket(final NetworkManager net, final SPacketUpdateTileEntity pkt) {
        this.readSyncNBT(pkt.getNbtCompound());
    }
    
    public NBTTagCompound getUpdateTag() {
        return this.writeSyncNBT(this.setupNbt());
    }
    
    private NBTTagCompound setupNbt() {
        final NBTTagCompound nbt = super.writeToNBT(new NBTTagCompound());
        nbt.removeTag("ForgeData");
        nbt.removeTag("ForgeCaps");
        return nbt;
    }
    
    public boolean shouldRefresh(final World world, final BlockPos pos, final IBlockState oldState, final IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
    
    public EnumFacing getFacing() {
        try {
            return EnumFacing.getFront(this.getBlockMetadata() & 0x7);
        }
        catch (final Exception ex) {
            return EnumFacing.UP;
        }
    }
    
    public boolean gettingPower() {
        return this.world.isBlockPowered(this.getPos());
    }
}
