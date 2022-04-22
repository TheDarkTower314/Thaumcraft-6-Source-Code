// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.misc;

import net.minecraft.util.IThreadListener;
import net.minecraft.entity.player.EntityPlayerMP;
import thaumcraft.common.container.ContainerLogistics;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketMiscStringToServer implements IMessage, IMessageHandler<PacketMiscStringToServer, IMessage>
{
    private int id;
    private String text;
    
    public PacketMiscStringToServer() {
    }
    
    public PacketMiscStringToServer(final int id, final String text) {
        this.id = id;
        this.text = text;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(id);
        ByteBufUtils.writeUTF8String(buffer, text);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        id = buffer.readInt();
        text = ByteBufUtils.readUTF8String(buffer);
    }
    
    public IMessage onMessage(final PacketMiscStringToServer message, final MessageContext ctx) {
        final IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                final EntityPlayerMP player = ctx.getServerHandler().player;
                if (id == 0 && player.openContainer instanceof ContainerLogistics) {
                    final ContainerLogistics container = (ContainerLogistics)player.openContainer;
                    container.searchText = message.text;
                    container.refreshItemList(true);
                }
            }
        });
        return null;
    }
}
