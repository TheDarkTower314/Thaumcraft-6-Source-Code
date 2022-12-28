package thaumcraft.common.lib.network.fx;
import io.netty.buffer.ByteBuf;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.monster.EntityWisp;


public class PacketFXWispZap implements IMessage, IMessageHandler<PacketFXWispZap, IMessage>
{
    private int source;
    private int target;
    
    public PacketFXWispZap() {
    }
    
    public PacketFXWispZap(int source, int target) {
        this.source = source;
        this.target = target;
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(source);
        buffer.writeInt(target);
    }
    
    public void fromBytes(ByteBuf buffer) {
        source = buffer.readInt();
        target = buffer.readInt();
    }
    
    public IMessage onMessage(PacketFXWispZap message, MessageContext ctx) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        WorldClient world = mc.world;
        Entity var2 = getEntityByID(message.source, mc, world);
        Entity var3 = getEntityByID(message.target, mc, world);
        if (var2 != null && var3 != null) {
            float r = 1.0f;
            float g = 1.0f;
            float b = 1.0f;
            if (var2 instanceof EntityWisp) {
                Color c = new Color(Aspect.getAspect(((EntityWisp)var2).getType()).getColor());
                r = c.getRed() / 255.0f;
                g = c.getGreen() / 255.0f;
                b = c.getBlue() / 255.0f;
            }
            FXDispatcher.INSTANCE.arcBolt(var2.posX, var2.posY, var2.posZ, var3.posX, var3.posY, var3.posZ, r, g, b, 0.6f);
        }
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    private Entity getEntityByID(int par1, Minecraft mc, WorldClient world) {
        return (par1 == mc.player.getEntityId()) ? mc.player : world.getEntityByID(par1);
    }
}
