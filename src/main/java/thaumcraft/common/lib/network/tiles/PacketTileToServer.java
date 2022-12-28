package thaumcraft.common.lib.network.tiles;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.tiles.TileThaumcraft;


public class PacketTileToServer implements IMessage, IMessageHandler<PacketTileToServer, IMessage>
{
    private long pos;
    private NBTTagCompound nbt;
    
    public PacketTileToServer() {
    }
    
    public PacketTileToServer(BlockPos pos, NBTTagCompound nbt) {
        this.pos = pos.toLong();
        this.nbt = nbt;
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeLong(pos);
        Utils.writeNBTTagCompoundToBuffer(buffer, nbt);
    }
    
    public void fromBytes(ByteBuf buffer) {
        pos = buffer.readLong();
        nbt = Utils.readNBTTagCompoundFromBuffer(buffer);
    }
    
    public IMessage onMessage(PacketTileToServer message, MessageContext ctx) {
        IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                World world = ctx.getServerHandler().player.getServerWorld();
                BlockPos bp = BlockPos.fromLong(message.pos);
                if (world != null && bp != null) {
                    TileEntity te = world.getTileEntity(bp);
                    if (te != null && te instanceof TileThaumcraft) {
                        ((TileThaumcraft)te).messageFromClient((message.nbt == null) ? new NBTTagCompound() : message.nbt, ctx.getServerHandler().player);
                    }
                }
            }
        });
        return null;
    }
}
