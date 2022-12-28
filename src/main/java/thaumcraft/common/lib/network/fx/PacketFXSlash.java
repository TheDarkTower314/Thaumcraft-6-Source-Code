package thaumcraft.common.lib.network.fx;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;


public class PacketFXSlash implements IMessage, IMessageHandler<PacketFXSlash, IMessage>
{
    private int source;
    private int target;
    
    public PacketFXSlash() {
    }
    
    public PacketFXSlash(int source, int target) {
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
    public IMessage onMessage(PacketFXSlash message, MessageContext ctx) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        WorldClient world = mc.world;
        Entity var2 = getEntityByID(message.source, mc, world);
        Entity var3 = getEntityByID(message.target, mc, world);
        if (var2 != null && var3 != null) {
            FXDispatcher.INSTANCE.drawSlash(var2.posX, var2.getEntityBoundingBox().minY + var2.height / 2.0f, var2.posZ, var3.posX, var3.getEntityBoundingBox().minY + var3.height / 2.0f, var3.posZ, 8);
        }
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    private Entity getEntityByID(int par1, Minecraft mc, WorldClient world) {
        return (par1 == mc.player.getEntityId()) ? mc.player : world.getEntityByID(par1);
    }
}
