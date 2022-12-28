package thaumcraft.common.lib.network.fx;
import io.netty.buffer.ByteBuf;
import java.awt.Color;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;


public class PacketFXZap implements IMessage, IMessageHandler<PacketFXZap, IMessage>
{
    private Vec3d source;
    private Vec3d target;
    private int color;
    private float width;
    
    public PacketFXZap() {
    }
    
    public PacketFXZap(Vec3d source, Vec3d target, int color, float width) {
        this.source = source;
        this.target = target;
        this.color = color;
        this.width = width;
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeDouble(source.x);
        buffer.writeDouble(source.y);
        buffer.writeDouble(source.z);
        buffer.writeDouble(target.x);
        buffer.writeDouble(target.y);
        buffer.writeDouble(target.z);
        buffer.writeInt(color);
        buffer.writeFloat(width);
    }
    
    public void fromBytes(ByteBuf buffer) {
        source = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        target = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        color = buffer.readInt();
        width = buffer.readFloat();
    }
    
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(PacketFXZap message, MessageContext ctx) {
        Color c = new Color(message.color);
        FXDispatcher.INSTANCE.arcBolt(message.source.x, message.source.y, message.source.z, message.target.x, message.target.y, message.target.z, c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, message.width);
        return null;
    }
}
