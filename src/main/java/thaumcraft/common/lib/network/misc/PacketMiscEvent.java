// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.misc;

import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.client.lib.events.RenderEventHandler;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.config.ModConfig;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketMiscEvent implements IMessage, IMessageHandler<PacketMiscEvent, IMessage>
{
    private byte type;
    private int value;
    public static final byte WARP_EVENT = 0;
    public static final byte MIST_EVENT = 1;
    public static final byte MIST_EVENT_SHORT = 2;
    
    public PacketMiscEvent() {
        value = 0;
    }
    
    public PacketMiscEvent(final byte type) {
        value = 0;
        this.type = type;
    }
    
    public PacketMiscEvent(final byte type, final int value) {
        this.value = 0;
        this.type = type;
        this.value = value;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeByte(type);
        if (value != 0) {
            buffer.writeInt(value);
        }
    }
    
    public void fromBytes(final ByteBuf buffer) {
        type = buffer.readByte();
        if (buffer.isReadable()) {
            value = buffer.readInt();
        }
    }
    
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(final PacketMiscEvent message, final MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                processMessage(message);
            }
        });
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    void processMessage(final PacketMiscEvent message) {
        final EntityPlayer p = Minecraft.getMinecraft().player;
        switch (message.type) {
            case 0: {
                if (!ModConfig.CONFIG_GRAPHICS.nostress) {
                    p.world.playSound(p.posX, p.posY, p.posZ, SoundsTC.heartbeat, SoundCategory.AMBIENT, 1.0f, 1.0f, false);
                    break;
                }
                break;
            }
            case 1: {
                RenderEventHandler.fogFiddled = true;
                RenderEventHandler.fogDuration = 2400;
                break;
            }
            case 2: {
                RenderEventHandler.fogFiddled = true;
                if (RenderEventHandler.fogDuration < 200) {
                    RenderEventHandler.fogDuration = 200;
                    break;
                }
                break;
            }
        }
    }
}
