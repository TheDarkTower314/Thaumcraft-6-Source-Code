// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.fx;

import thaumcraft.client.fx.FXDispatcher;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketFXBlockArc implements IMessage, IMessageHandler<PacketFXBlockArc, IMessage>
{
    private int x;
    private int y;
    private int z;
    private float tx;
    private float ty;
    private float tz;
    private float r;
    private float g;
    private float b;
    
    public PacketFXBlockArc() {
    }
    
    public PacketFXBlockArc(final BlockPos pos, final Entity source, final float r, final float g, final float b) {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
        tx = (float)source.posX;
        ty = (float)(source.getEntityBoundingBox().minY + source.height / 2.0f);
        tz = (float)source.posZ;
        this.r = r;
        this.g = g;
        this.b = b;
    }
    
    public PacketFXBlockArc(final BlockPos pos, final BlockPos pos2, final float r, final float g, final float b) {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
        tx = pos2.getX() + 0.5f;
        ty = pos2.getY() + 0.5f;
        tz = pos2.getZ() + 0.5f;
        this.r = r;
        this.g = g;
        this.b = b;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeFloat(tx);
        buffer.writeFloat(ty);
        buffer.writeFloat(tz);
        buffer.writeFloat(r);
        buffer.writeFloat(g);
        buffer.writeFloat(b);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        tx = buffer.readFloat();
        ty = buffer.readFloat();
        tz = buffer.readFloat();
        r = buffer.readFloat();
        g = buffer.readFloat();
        b = buffer.readFloat();
    }
    
    public IMessage onMessage(final PacketFXBlockArc message, final MessageContext ctx) {
        FXDispatcher.INSTANCE.arcLightning(message.tx, message.ty, message.tz, message.x + 0.5, message.y + 0.5, message.z + 0.5, message.r, message.g, message.b, 0.5f);
        return null;
    }
}
