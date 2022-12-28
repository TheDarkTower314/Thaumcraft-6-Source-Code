package thaumcraft.common.lib.network.fx;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.utils.Utils;


public class PacketFXBlockBamf implements IMessage, IMessageHandler<PacketFXBlockBamf, IMessage>
{
    private double x;
    private double y;
    private double z;
    private int color;
    private byte flags;
    private byte face;
    
    public PacketFXBlockBamf() {
    }
    
    public PacketFXBlockBamf(double x, double y, double z, int color, boolean sound, boolean flair, EnumFacing side) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        int f = 0;
        if (sound) {
            f = Utils.setBit(f, 0);
        }
        if (flair) {
            f = Utils.setBit(f, 1);
        }
        if (side != null) {
            face = (byte)side.ordinal();
        }
        else {
            face = -1;
        }
        flags = (byte)f;
    }
    
    public PacketFXBlockBamf(BlockPos pos, int color, boolean sound, boolean flair, EnumFacing side) {
        x = pos.getX() + 0.5;
        y = pos.getY() + 0.5;
        z = pos.getZ() + 0.5;
        this.color = color;
        int f = 0;
        if (sound) {
            f = Utils.setBit(f, 0);
        }
        if (flair) {
            f = Utils.setBit(f, 1);
        }
        if (side != null) {
            face = (byte)side.ordinal();
        }
        else {
            face = -1;
        }
        flags = (byte)f;
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeDouble(x);
        buffer.writeDouble(y);
        buffer.writeDouble(z);
        buffer.writeInt(color);
        buffer.writeByte(flags);
        buffer.writeByte(face);
    }
    
    public void fromBytes(ByteBuf buffer) {
        x = buffer.readDouble();
        y = buffer.readDouble();
        z = buffer.readDouble();
        color = buffer.readInt();
        flags = buffer.readByte();
        face = buffer.readByte();
    }
    
    public IMessage onMessage(PacketFXBlockBamf message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                processMessage(message);
            }
        });
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    void processMessage(PacketFXBlockBamf message) {
        EnumFacing side = null;
        if (message.face >= 0) {
            side = EnumFacing.getFront(message.face);
        }
        if (message.color != -9999) {
            FXDispatcher.INSTANCE.drawBamf(message.x, message.y, message.z, message.color, Utils.getBit(message.flags, 0), Utils.getBit(message.flags, 1), side);
        }
        else {
            FXDispatcher.INSTANCE.drawBamf(message.x, message.y, message.z, Utils.getBit(message.flags, 0), Utils.getBit(message.flags, 1), side);
        }
    }
}
