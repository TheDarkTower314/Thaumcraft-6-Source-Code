// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.fx;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.client.particle.Particle;
import net.minecraftforge.fml.client.FMLClientHandler;
import thaumcraft.client.fx.other.FXShieldRunes;
import net.minecraft.util.math.MathHelper;
import thaumcraft.Thaumcraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketFXShield implements IMessage, IMessageHandler<PacketFXShield, IMessage>
{
    private int source;
    private int target;
    
    public PacketFXShield() {
    }
    
    public PacketFXShield(final int source, final int target) {
        this.source = source;
        this.target = target;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(source);
        buffer.writeInt(target);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        source = buffer.readInt();
        target = buffer.readInt();
    }
    
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(final PacketFXShield message, final MessageContext ctx) {
        final Entity p = Thaumcraft.proxy.getClientWorld().getEntityByID(message.source);
        if (p == null) {
            return null;
        }
        float pitch = 0.0f;
        float yaw = 0.0f;
        if (message.target >= 0) {
            final Entity t = Thaumcraft.proxy.getClientWorld().getEntityByID(message.target);
            if (t != null) {
                final double d0 = p.posX - t.posX;
                final double d2 = (p.getEntityBoundingBox().minY + p.getEntityBoundingBox().maxY) / 2.0 - (t.getEntityBoundingBox().minY + t.getEntityBoundingBox().maxY) / 2.0;
                final double d3 = p.posZ - t.posZ;
                final double d4 = MathHelper.sqrt(d0 * d0 + d3 * d3);
                final float f = (float)(Math.atan2(d3, d0) * 180.0 / 3.141592653589793) - 90.0f;
                final float f2 = pitch = (float)(-(Math.atan2(d2, d4) * 180.0 / 3.141592653589793));
                yaw = f;
            }
            else {
                pitch = 90.0f;
                yaw = 0.0f;
            }
            final FXShieldRunes fb = new FXShieldRunes(Thaumcraft.proxy.getClientWorld(), p.posX, p.posY, p.posZ, p, 8, yaw, pitch);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fb);
        }
        else if (message.target == -1) {
            FXShieldRunes fb2 = new FXShieldRunes(Thaumcraft.proxy.getClientWorld(), p.posX, p.posY, p.posZ, p, 8, 0.0f, 90.0f);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fb2);
            fb2 = new FXShieldRunes(Thaumcraft.proxy.getClientWorld(), p.posX, p.posY, p.posZ, p, 8, 0.0f, 270.0f);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fb2);
        }
        else if (message.target == -2) {
            final FXShieldRunes fb2 = new FXShieldRunes(Thaumcraft.proxy.getClientWorld(), p.posX, p.posY, p.posZ, p, 8, 0.0f, 270.0f);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fb2);
        }
        else if (message.target == -3) {
            final FXShieldRunes fb2 = new FXShieldRunes(Thaumcraft.proxy.getClientWorld(), p.posX, p.posY, p.posZ, p, 8, 0.0f, 90.0f);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fb2);
        }
        return null;
    }
}
