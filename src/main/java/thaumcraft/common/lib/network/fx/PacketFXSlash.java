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
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketFXSlash implements IMessage, IMessageHandler<PacketFXSlash, IMessage>
{
    private int source;
    private int target;
    
    public PacketFXSlash() {
    }
    
    public PacketFXSlash(final int source, final int target) {
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
    
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(final PacketFXSlash message, final MessageContext ctx) {
        final Minecraft mc = FMLClientHandler.instance().getClient();
        final WorldClient world = mc.world;
        final Entity var2 = this.getEntityByID(message.source, mc, world);
        final Entity var3 = this.getEntityByID(message.target, mc, world);
        if (var2 != null && var3 != null) {
            FXDispatcher.INSTANCE.drawSlash(var2.posX, var2.getEntityBoundingBox().minY + var2.height / 2.0f, var2.posZ, var3.posX, var3.getEntityBoundingBox().minY + var3.height / 2.0f, var3.posZ, 8);
        }
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    private Entity getEntityByID(final int par1, final Minecraft mc, final WorldClient world) {
        return (par1 == mc.player.getEntityId()) ? mc.player : world.getEntityByID(par1);
    }
}
