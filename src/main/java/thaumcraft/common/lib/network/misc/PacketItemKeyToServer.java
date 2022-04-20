// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.misc;

import net.minecraft.util.IThreadListener;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import thaumcraft.common.items.tools.ItemElementalShovel;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.api.casters.ICaster;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketItemKeyToServer implements IMessage, IMessageHandler<PacketItemKeyToServer, IMessage>
{
    private byte key;
    private byte mod;
    
    public PacketItemKeyToServer() {
    }
    
    public PacketItemKeyToServer(final int key) {
        this.key = (byte)key;
        this.mod = 0;
    }
    
    public PacketItemKeyToServer(final int key, final int mod) {
        this.key = (byte)key;
        this.mod = (byte)mod;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeByte(this.key);
        buffer.writeByte(this.mod);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        this.key = buffer.readByte();
        this.mod = buffer.readByte();
    }
    
    public IMessage onMessage(final PacketItemKeyToServer message, final MessageContext ctx) {
        final IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                final World world = ctx.getServerHandler().player.getServerWorld();
                if (world == null) {
                    return;
                }
                final Entity player = ctx.getServerHandler().player;
                if (player != null && player instanceof EntityPlayer) {
                    boolean flag = false;
                    if (((EntityPlayer)player).getHeldItemMainhand() != null) {
                        if (message.key == 1 && ((EntityPlayer)player).getHeldItemMainhand().getItem() instanceof ICaster) {
                            CasterManager.toggleMisc(((EntityPlayer)player).getHeldItemMainhand(), world, (EntityPlayer)player, message.mod);
                            flag = true;
                        }
                        if (!flag && message.key == 1 && ((EntityPlayer)player).getHeldItemOffhand().getItem() instanceof ICaster) {
                            CasterManager.toggleMisc(((EntityPlayer)player).getHeldItemOffhand(), world, (EntityPlayer)player, message.mod);
                        }
                        if (message.key == 1 && ((EntityPlayer)player).getHeldItemMainhand().getItem() instanceof ItemElementalShovel) {
                            final ItemElementalShovel itemElementalShovel = (ItemElementalShovel)((EntityPlayer)player).getHeldItemMainhand().getItem();
                            final byte b = ItemElementalShovel.getOrientation(((EntityPlayer)player).getHeldItemMainhand());
                            final ItemElementalShovel itemElementalShovel2 = (ItemElementalShovel)((EntityPlayer)player).getHeldItemMainhand().getItem();
                            ItemElementalShovel.setOrientation(((EntityPlayer)player).getHeldItemMainhand(), (byte)(b + 1));
                            flag = true;
                        }
                    }
                }
            }
        });
        return null;
    }
}
