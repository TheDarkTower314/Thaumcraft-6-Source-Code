package thaumcraft.common.lib.network.fx;
import io.netty.buffer.ByteBuf;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.IFocusElement;


public class PacketFXFocusPartImpact implements IMessage, IMessageHandler<PacketFXFocusPartImpact, IMessage>
{
    double x;
    double y;
    double z;
    String parts;
    
    public PacketFXFocusPartImpact() {
    }
    
    public PacketFXFocusPartImpact(double x, double y, double z, String[] parts) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.parts = "";
        for (int a = 0; a < parts.length; ++a) {
            if (a > 0) {
                this.parts += "%";
            }
            this.parts += parts[a];
        }
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeFloat((float) x);
        buffer.writeFloat((float) y);
        buffer.writeFloat((float) z);
        ByteBufUtils.writeUTF8String(buffer, parts);
    }
    
    public void fromBytes(ByteBuf buffer) {
        x = buffer.readFloat();
        y = buffer.readFloat();
        z = buffer.readFloat();
        parts = ByteBufUtils.readUTF8String(buffer);
    }
    
    public IMessage onMessage(PacketFXFocusPartImpact message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                processMessage(message);
            }
        });
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    void processMessage(PacketFXFocusPartImpact message) {
        String[] partKeys = message.parts.split("%");
        int amt = Math.max(1, 15 / partKeys.length);
        Random r = Minecraft.getMinecraft().world.rand;
        for (String k : partKeys) {
            IFocusElement part = FocusEngine.getElement(k);
            if (part != null && part instanceof FocusEffect) {
                for (int a = 0; a < amt; ++a) {
                    ((FocusEffect)part).renderParticleFX(Minecraft.getMinecraft().world, message.x, message.y, message.z, r.nextGaussian() * 0.15, r.nextGaussian() * 0.15, r.nextGaussian() * 0.15);
                }
            }
        }
    }
}
