package thaumcraft.common.lib.network.misc;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.container.ContainerLogistics;


public class PacketMiscStringToServer implements IMessage, IMessageHandler<PacketMiscStringToServer, IMessage>
{
    private int id;
    private String text;
    
    public PacketMiscStringToServer() {
    }
    
    public PacketMiscStringToServer(int id, String text) {
        this.id = id;
        this.text = text;
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(id);
        ByteBufUtils.writeUTF8String(buffer, text);
    }
    
    public void fromBytes(ByteBuf buffer) {
        id = buffer.readInt();
        text = ByteBufUtils.readUTF8String(buffer);
    }
    
    public IMessage onMessage(PacketMiscStringToServer message, MessageContext ctx) {
        IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                EntityPlayerMP player = ctx.getServerHandler().player;
                if (id == 0 && player.openContainer instanceof ContainerLogistics) {
                    ContainerLogistics container = (ContainerLogistics)player.openContainer;
                    container.searchText = message.text;
                    container.refreshItemList(true);
                }
            }
        });
        return null;
    }
}
