// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.playerdata;

import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSyncResearchFlagsToServer implements IMessage, IMessageHandler<PacketSyncResearchFlagsToServer, IMessage>
{
    String key;
    byte flags;
    
    public PacketSyncResearchFlagsToServer() {
    }
    
    public PacketSyncResearchFlagsToServer(final EntityPlayer player, final String key) {
        this.key = key;
        this.flags = Utils.pack(ThaumcraftCapabilities.getKnowledge(player).hasResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.PAGE), ThaumcraftCapabilities.getKnowledge(player).hasResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP), ThaumcraftCapabilities.getKnowledge(player).hasResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.RESEARCH));
    }
    
    public void toBytes(final ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, this.key);
        buffer.writeByte(this.flags);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        this.key = ByteBufUtils.readUTF8String(buffer);
        this.flags = buffer.readByte();
    }
    
    public IMessage onMessage(final PacketSyncResearchFlagsToServer message, final MessageContext ctx) {
        final IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                final boolean[] b = Utils.unpack(message.flags);
                if (ctx.getServerHandler().player != null) {
                    final EntityPlayer player = ctx.getServerHandler().player;
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
