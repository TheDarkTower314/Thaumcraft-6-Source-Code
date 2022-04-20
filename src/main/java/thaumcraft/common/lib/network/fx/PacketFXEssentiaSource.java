// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.fx;

import thaumcraft.common.lib.events.EssentiaHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketFXEssentiaSource implements IMessage, IMessageHandler<PacketFXEssentiaSource, IMessage>
{
    private int x;
    private int y;
    private int z;
    private byte dx;
    private byte dy;
    private byte dz;
    private int color;
    private int ext;
    
    public PacketFXEssentiaSource() {
    }
    
    public PacketFXEssentiaSource(final BlockPos p1, final byte dx, final byte dy, final byte dz, final int color, final int e) {
        this.x = p1.getX();
        this.y = p1.getY();
        this.z = p1.getZ();
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.color = color;
        this.ext = e;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.x);
        buffer.writeInt(this.y);
        buffer.writeInt(this.z);
        buffer.writeInt(this.color);
        buffer.writeByte(this.dx);
        buffer.writeByte(this.dy);
        buffer.writeByte(this.dz);
        buffer.writeShort(this.ext);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
        this.color = buffer.readInt();
        this.dx = buffer.readByte();
        this.dy = buffer.readByte();
        this.dz = buffer.readByte();
        this.ext = buffer.readShort();
    }
    
    public IMessage onMessage(final PacketFXEssentiaSource message, final MessageContext ctx) {
        final int tx = message.x - message.dx;
        final int ty = message.y - message.dy;
        final int tz = message.z - message.dz;
        final String key = message.x + ":" + message.y + ":" + message.z + ":" + tx + ":" + ty + ":" + tz + ":" + message.color;
        if (EssentiaHandler.sourceFX.containsKey(key)) {
            final EssentiaHandler.EssentiaSourceFX sf = EssentiaHandler.sourceFX.get(key);
            EssentiaHandler.sourceFX.remove(key);
            EssentiaHandler.sourceFX.put(key, sf);
        }
        else {
            EssentiaHandler.sourceFX.put(key, new EssentiaHandler.EssentiaSourceFX(new BlockPos(message.x, message.y, message.z), new BlockPos(tx, ty, tz), message.color, message.ext));
        }
        return null;
    }
}
