package thaumcraft.common.lib.network.misc;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.client.lib.events.HudHandler;
import thaumcraft.common.world.aura.AuraChunk;


public class PacketAuraToClient implements IMessage, IMessageHandler<PacketAuraToClient, IMessage>
{
    short base;
    float vis;
    float flux;
    
    public PacketAuraToClient() {
    }
    
    public PacketAuraToClient(AuraChunk ac) {
        base = ac.getBase();
        vis = ac.getVis();
        flux = ac.getFlux();
    }
    
    public void toBytes(ByteBuf dos) {
        dos.writeShort(base);
        dos.writeFloat(vis);
        dos.writeFloat(flux);
    }
    
    public void fromBytes(ByteBuf dat) {
        base = dat.readShort();
        vis = dat.readFloat();
        flux = dat.readFloat();
    }
    
    public IMessage onMessage(PacketAuraToClient message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                HudHandler.currentAura = new AuraChunk(null, message.base, message.vis, message.flux);
            }
        });
        return null;
    }
}
