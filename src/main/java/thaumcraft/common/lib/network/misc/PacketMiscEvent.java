package thaumcraft.common.lib.network.misc;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.lib.events.RenderEventHandler;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.lib.SoundsTC;


public class PacketMiscEvent implements IMessage, IMessageHandler<PacketMiscEvent, IMessage>
{
    private byte type;
    private int value;
    public static byte WARP_EVENT = 0;
    public static byte MIST_EVENT = 1;
    public static byte MIST_EVENT_SHORT = 2;
    
    public PacketMiscEvent() {
        value = 0;
    }
    
    public PacketMiscEvent(byte type) {
        value = 0;
        this.type = type;
    }
    
    public PacketMiscEvent(byte type, int value) {
        this.value = 0;
        this.type = type;
        this.value = value;
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeByte(type);
        if (value != 0) {
            buffer.writeInt(value);
        }
    }
    
    public void fromBytes(ByteBuf buffer) {
        type = buffer.readByte();
        if (buffer.isReadable()) {
            value = buffer.readInt();
        }
    }
    
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(PacketMiscEvent message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                processMessage(message);
            }
        });
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    void processMessage(PacketMiscEvent message) {
        EntityPlayer p = Minecraft.getMinecraft().player;
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
