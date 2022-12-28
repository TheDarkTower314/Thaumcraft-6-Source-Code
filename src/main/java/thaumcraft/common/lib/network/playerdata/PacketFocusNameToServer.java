package thaumcraft.common.lib.network.playerdata;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;


public class PacketFocusNameToServer implements IMessage, IMessageHandler<PacketFocusNameToServer, IMessage>
{
    private long loc;
    private String name;
    
    public PacketFocusNameToServer() {
    }
    
    public PacketFocusNameToServer(BlockPos pos, String name) {
        loc = pos.toLong();
        this.name = name;
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeLong(loc);
        ByteBufUtils.writeUTF8String(buffer, name);
    }
    
    public void fromBytes(ByteBuf buffer) {
        loc = buffer.readLong();
        name = ByteBufUtils.readUTF8String(buffer);
    }
    
    public IMessage onMessage(PacketFocusNameToServer message, MessageContext ctx) {
        IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                if (ctx.getServerHandler().player == null) {
                    return;
                }
                BlockPos pos = BlockPos.fromLong(message.loc);
                TileEntity rt = ctx.getServerHandler().player.world.getTileEntity(pos);
                if (rt != null && rt instanceof TileFocalManipulator) {
                    ((TileFocalManipulator)rt).focusName = message.name;
                    rt.markDirty();
                }
            }
        });
        return null;
    }
}
