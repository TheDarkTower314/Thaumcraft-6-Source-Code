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

public class PacketFXBlockMist implements IMessage, IMessageHandler<PacketFXBlockMist, IMessage>
{
    private long loc;
    private int color;
    
    public PacketFXBlockMist() {
    }
    
    public PacketFXBlockMist(final BlockPos pos, final int color) {
        this.loc = pos.toLong();
        this.color = color;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeLong(this.loc);
        buffer.writeInt(this.color);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        this.loc = buffer.readLong();
        this.color = buffer.readInt();
    }
    
    public IMessage onMessage(final PacketFXBlockMist message, final MessageContext ctx) {
        FXDispatcher.INSTANCE.drawBlockMistParticles(BlockPos.fromLong(message.loc), message.color);
        return null;
    }
}
