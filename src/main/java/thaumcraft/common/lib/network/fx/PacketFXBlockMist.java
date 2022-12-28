package thaumcraft.common.lib.network.fx;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.client.fx.FXDispatcher;


public class PacketFXBlockMist implements IMessage, IMessageHandler<PacketFXBlockMist, IMessage>
{
    private long loc;
    private int color;
    
    public PacketFXBlockMist() {
    }
    
    public PacketFXBlockMist(BlockPos pos, int color) {
        loc = pos.toLong();
        this.color = color;
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeLong(loc);
        buffer.writeInt(color);
    }
    
    public void fromBytes(ByteBuf buffer) {
        loc = buffer.readLong();
        color = buffer.readInt();
    }
    
    public IMessage onMessage(PacketFXBlockMist message, MessageContext ctx) {
        FXDispatcher.INSTANCE.drawBlockMistParticles(BlockPos.fromLong(message.loc), message.color);
        return null;
    }
}
