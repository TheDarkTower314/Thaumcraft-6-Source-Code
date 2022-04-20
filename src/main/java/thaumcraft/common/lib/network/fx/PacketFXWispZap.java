// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.fx;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.Minecraft;
import thaumcraft.client.fx.FXDispatcher;
import java.awt.Color;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.entities.monster.EntityWisp;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketFXWispZap implements IMessage, IMessageHandler<PacketFXWispZap, IMessage>
{
    private int source;
    private int target;
    
    public PacketFXWispZap() {
    }
    
    public PacketFXWispZap(final int source, final int target) {
        this.source = source;
        this.target = target;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.source);
        buffer.writeInt(this.target);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        this.source = buffer.readInt();
        this.target = buffer.readInt();
    }
    
    public IMessage onMessage(final PacketFXWispZap message, final MessageContext ctx) {
        final Minecraft mc = FMLClientHandler.instance().getClient();
        final WorldClient world = mc.world;
        final Entity var2 = this.getEntityByID(message.source, mc, world);
        final Entity var3 = this.getEntityByID(message.target, mc, world);
        if (var2 != null && var3 != null) {
            float r = 1.0f;
            float g = 1.0f;
            float b = 1.0f;
            if (var2 instanceof EntityWisp) {
                final Color c = new Color(Aspect.getAspect(((EntityWisp)var2).getType()).getColor());
                r = c.getRed() / 255.0f;
                g = c.getGreen() / 255.0f;
                b = c.getBlue() / 255.0f;
            }
            FXDispatcher.INSTANCE.arcBolt(var2.posX, var2.posY, var2.posZ, var3.posX, var3.posY, var3.posZ, r, g, b, 0.6f);
        }
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    private Entity getEntityByID(final int par1, final Minecraft mc, final WorldClient world) {
        return (par1 == mc.player.getEntityId()) ? mc.player : world.getEntityByID(par1);
    }
}
