package thaumcraft.common.lib.network.playerdata;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class PacketPlayerFlagToServer implements IMessage, IMessageHandler<PacketPlayerFlagToServer, IMessage>
{
    byte flag;
    
    public PacketPlayerFlagToServer() {
    }
    
    public PacketPlayerFlagToServer(EntityLivingBase player, int i) {
        flag = (byte)i;
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeByte(flag);
    }
    
    public void fromBytes(ByteBuf buffer) {
        flag = buffer.readByte();
    }
    
    public IMessage onMessage(PacketPlayerFlagToServer message, MessageContext ctx) {
        IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                if (ctx.getServerHandler().player != null) {
                    EntityPlayer player = ctx.getServerHandler().player;
                    switch (message.flag) {
                        case 1: {
                            player.fallDistance = 0.0f;
                            break;
                        }
                    }
                }
            }
        });
        return null;
    }
}
