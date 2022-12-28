package thaumcraft.common.lib.network.fx;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.client.fx.FXDispatcher;


public class PacketFXPollute implements IMessage, IMessageHandler<PacketFXPollute, IMessage>
{
    private int x;
    private int y;
    private int z;
    private byte amount;
    
    public PacketFXPollute() {
    }
    
    public PacketFXPollute(BlockPos pos, float amt) {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
        if (amt < 1.0f && amt > 0.0f) {
            amt = 1.0f;
        }
        amount = (byte)amt;
    }
    
    public PacketFXPollute(BlockPos pos, float amt, boolean vary) {
        this(pos, amt);
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeByte(amount);
    }
    
    public void fromBytes(ByteBuf buffer) {
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        amount = buffer.readByte();
    }
    
    public IMessage onMessage(PacketFXPollute message, MessageContext ctx) {
        for (int a = 0; a < Math.min(40, message.amount); ++a) {
            FXDispatcher.INSTANCE.drawPollutionParticles(new BlockPos(message.x, message.y, message.z));
        }
        return null;
    }
}
