package thaumcraft.common.lib.network.fx;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.Thaumcraft;
import thaumcraft.client.fx.other.FXShieldRunes;


public class PacketFXShield implements IMessage, IMessageHandler<PacketFXShield, IMessage>
{
    private int source;
    private int target;
    
    public PacketFXShield() {
    }
    
    public PacketFXShield(int source, int target) {
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
    
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(PacketFXShield message, MessageContext ctx) {
        Entity p = Thaumcraft.proxy.getClientWorld().getEntityByID(message.source);
        if (p == null) {
            return null;
        }
        float pitch = 0.0f;
        float yaw = 0.0f;
        if (message.target >= 0) {
            Entity t = Thaumcraft.proxy.getClientWorld().getEntityByID(message.target);
            if (t != null) {
                double d0 = p.posX - t.posX;
                double d2 = (p.getEntityBoundingBox().minY + p.getEntityBoundingBox().maxY) / 2.0 - (t.getEntityBoundingBox().minY + t.getEntityBoundingBox().maxY) / 2.0;
                double d3 = p.posZ - t.posZ;
                double d4 = MathHelper.sqrt(d0 * d0 + d3 * d3);
                float f = (float)(Math.atan2(d3, d0) * 180.0 / 3.141592653589793) - 90.0f;
                float f2 = pitch = (float)(-(Math.atan2(d2, d4) * 180.0 / 3.141592653589793));
                yaw = f;
            }
            else {
                pitch = 90.0f;
                yaw = 0.0f;
            }
            FXShieldRunes fb = new FXShieldRunes(Thaumcraft.proxy.getClientWorld(), p.posX, p.posY, p.posZ, p, 8, yaw, pitch);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fb);
        }
        else if (message.target == -1) {
            FXShieldRunes fb2 = new FXShieldRunes(Thaumcraft.proxy.getClientWorld(), p.posX, p.posY, p.posZ, p, 8, 0.0f, 90.0f);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fb2);
            fb2 = new FXShieldRunes(Thaumcraft.proxy.getClientWorld(), p.posX, p.posY, p.posZ, p, 8, 0.0f, 270.0f);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fb2);
        }
        else if (message.target == -2) {
            FXShieldRunes fb2 = new FXShieldRunes(Thaumcraft.proxy.getClientWorld(), p.posX, p.posY, p.posZ, p, 8, 0.0f, 270.0f);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fb2);
        }
        else if (message.target == -3) {
            FXShieldRunes fb2 = new FXShieldRunes(Thaumcraft.proxy.getClientWorld(), p.posX, p.posY, p.posZ, p, 8, 0.0f, 90.0f);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fb2);
        }
        return null;
    }
}
