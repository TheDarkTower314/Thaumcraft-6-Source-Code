package thaumcraft.common.lib.network.playerdata;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.utils.Utils;


public class PacketSyncResearchFlagsToServer implements IMessage, IMessageHandler<PacketSyncResearchFlagsToServer, IMessage>
{
    String key;
    byte flags;
    
    public PacketSyncResearchFlagsToServer() {
    }
    
    public PacketSyncResearchFlagsToServer(EntityPlayer player, String key) {
        this.key = key;
        flags = Utils.pack(ThaumcraftCapabilities.getKnowledge(player).hasResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.PAGE), ThaumcraftCapabilities.getKnowledge(player).hasResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP), ThaumcraftCapabilities.getKnowledge(player).hasResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.RESEARCH));
    }
    
    public void toBytes(ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, key);
        buffer.writeByte(flags);
    }
    
    public void fromBytes(ByteBuf buffer) {
        key = ByteBufUtils.readUTF8String(buffer);
        flags = buffer.readByte();
    }
    
    public IMessage onMessage(PacketSyncResearchFlagsToServer message, MessageContext ctx) {
        IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                boolean[] b = Utils.unpack(message.flags);
                if (ctx.getServerHandler().player != null) {
                    EntityPlayer player = ctx.getServerHandler().player;
                    if (b[0]) {
                        ThaumcraftCapabilities.getKnowledge(player).setResearchFlag(message.key, IPlayerKnowledge.EnumResearchFlag.PAGE);
                    }
                    else {
                        ThaumcraftCapabilities.getKnowledge(player).clearResearchFlag(message.key, IPlayerKnowledge.EnumResearchFlag.PAGE);
                    }
                    if (b[1]) {
                        ThaumcraftCapabilities.getKnowledge(player).setResearchFlag(message.key, IPlayerKnowledge.EnumResearchFlag.POPUP);
                    }
                    else {
                        ThaumcraftCapabilities.getKnowledge(player).clearResearchFlag(message.key, IPlayerKnowledge.EnumResearchFlag.POPUP);
                    }
                    if (b[2]) {
                        ThaumcraftCapabilities.getKnowledge(player).setResearchFlag(message.key, IPlayerKnowledge.EnumResearchFlag.RESEARCH);
                    }
                    else {
                        ThaumcraftCapabilities.getKnowledge(player).clearResearchFlag(message.key, IPlayerKnowledge.EnumResearchFlag.RESEARCH);
                    }
                }
            }
        });
        return null;
    }
}
