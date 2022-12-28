package thaumcraft.common.lib.network.misc;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.casters.ICaster;
import thaumcraft.common.items.casters.CasterManager;


public class PacketFocusChangeToServer implements IMessage, IMessageHandler<PacketFocusChangeToServer, IMessage>
{
    private String focus;
    
    public PacketFocusChangeToServer() {
    }
    
    public PacketFocusChangeToServer(String focus) {
        this.focus = focus;
    }
    
    public void toBytes(ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, focus);
    }
    
    public void fromBytes(ByteBuf buffer) {
        focus = ByteBufUtils.readUTF8String(buffer);
    }
    
    public IMessage onMessage(PacketFocusChangeToServer message, MessageContext ctx) {
        IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                World world = ctx.getServerHandler().player.getServerWorld();
                if (world == null) {
                    return;
                }
                Entity player = ctx.getServerHandler().player;
                if (player != null && player instanceof EntityPlayer && ((EntityPlayer)player).getHeldItemMainhand().getItem() instanceof ICaster) {
                    CasterManager.changeFocus(((EntityPlayer)player).getHeldItemMainhand(), world, (EntityPlayer)player, message.focus);
                }
                else if (player != null && player instanceof EntityPlayer && ((EntityPlayer)player).getHeldItemOffhand().getItem() instanceof ICaster) {
                    CasterManager.changeFocus(((EntityPlayer)player).getHeldItemOffhand(), world, (EntityPlayer)player, message.focus);
                }
            }
        });
        return null;
    }
}
