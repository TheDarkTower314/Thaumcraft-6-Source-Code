// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.misc;

import thaumcraft.client.lib.events.HudHandler;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import thaumcraft.common.world.aura.AuraChunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketAuraToClient implements IMessage, IMessageHandler<PacketAuraToClient, IMessage>
{
    short base;
    float vis;
    float flux;
    
    public PacketAuraToClient() {
    }
    
    public PacketAuraToClient(final AuraChunk ac) {
        this.base = ac.getBase();
        this.vis = ac.getVis();
        this.flux = ac.getFlux();
    }
    
    public void toBytes(final ByteBuf dos) {
        dos.writeShort(this.base);
        dos.writeFloat(this.vis);
        dos.writeFloat(this.flux);
    }
    
    public void fromBytes(final ByteBuf dat) {
        this.base = dat.readShort();
        this.vis = dat.readFloat();
        this.flux = dat.readFloat();
    }
    
    public IMessage onMessage(final PacketAuraToClient message, final MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                HudHandler.currentAura = new AuraChunk(null, message.base, message.vis, message.flux);
            }
        });
        return null;
    }
}
