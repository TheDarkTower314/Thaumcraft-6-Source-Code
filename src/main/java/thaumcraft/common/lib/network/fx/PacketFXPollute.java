// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.fx;

import thaumcraft.client.fx.FXDispatcher;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketFXPollute implements IMessage, IMessageHandler<PacketFXPollute, IMessage>
{
    private int x;
    private int y;
    private int z;
    private byte amount;
    
    public PacketFXPollute() {
    }
    
    public PacketFXPollute(final BlockPos pos, float amt) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        if (amt < 1.0f && amt > 0.0f) {
            amt = 1.0f;
        }
        this.amount = (byte)amt;
    }
    
    public PacketFXPollute(final BlockPos pos, final float amt, final boolean vary) {
        this(pos, amt);
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.x);
        buffer.writeInt(this.y);
        buffer.writeInt(this.z);
        buffer.writeByte(this.amount);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
        this.amount = buffer.readByte();
    }
    
    public IMessage onMessage(final PacketFXPollute message, final MessageContext ctx) {
        for (int a = 0; a < Math.min(40, message.amount); ++a) {
            FXDispatcher.INSTANCE.drawPollutionParticles(new BlockPos(message.x, message.y, message.z));
        }
        return null;
    }
}
