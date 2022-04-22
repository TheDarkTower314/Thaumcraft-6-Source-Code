// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.misc;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketItemToClientContainer implements IMessage, IMessageHandler<PacketItemToClientContainer, IMessage>
{
    private int windowId;
    private int slot;
    private ItemStack item;
    
    public PacketItemToClientContainer() {
    }
    
    public PacketItemToClientContainer(final int windowIdIn, final int slotIn, final ItemStack itemIn) {
        windowId = windowIdIn;
        slot = slotIn;
        item = itemIn;
    }
    
    public void toBytes(final ByteBuf dos) {
        dos.writeInt(windowId);
        dos.writeInt(slot);
        Utils.writeItemStackToBuffer(dos, item);
    }
    
    public void fromBytes(final ByteBuf dat) {
        windowId = dat.readInt();
        slot = dat.readInt();
        item = Utils.readItemStackFromBuffer(dat);
    }
    
    public IMessage onMessage(final PacketItemToClientContainer message, final MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Minecraft.getMinecraft().player.openContainer != null && Minecraft.getMinecraft().player.openContainer.windowId == message.windowId) {
                        Minecraft.getMinecraft().player.openContainer.putStackInSlot(message.slot, message.item);
                    }
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return null;
    }
}
