// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.playerdata;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.research.ResearchEntry;
import net.minecraft.client.gui.toasts.IToast;
import thaumcraft.client.gui.ResearchToast;
import thaumcraft.api.research.ResearchCategories;
import net.minecraft.nbt.NBTBase;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSyncKnowledge implements IMessage, IMessageHandler<PacketSyncKnowledge, IMessage>
{
    protected NBTTagCompound data;
    
    public PacketSyncKnowledge() {
    }
    
    public PacketSyncKnowledge(final EntityPlayer player) {
        final IPlayerKnowledge pk = ThaumcraftCapabilities.getKnowledge(player);
        this.data = pk.serializeNBT();
        for (final String key : pk.getResearchList()) {
            pk.clearResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP);
        }
    }
    
    public void toBytes(final ByteBuf buffer) {
        Utils.writeNBTTagCompoundToBuffer(buffer, this.data);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        this.data = Utils.readNBTTagCompoundFromBuffer(buffer);
    }
    
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(final PacketSyncKnowledge message, final MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                final EntityPlayer player = Minecraft.getMinecraft().player;
                final IPlayerKnowledge pk = ThaumcraftCapabilities.getKnowledge(player);
                pk.deserializeNBT(message.data);
                for (final String key : pk.getResearchList()) {
                    if (pk.hasResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP)) {
                        final ResearchEntry ri = ResearchCategories.getResearch(key);
                        if (ri != null) {
                            Minecraft.getMinecraft().getToastGui().add(new ResearchToast(ri));
                        }
                    }
                    pk.clearResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP);
                }
            }
        });
        return null;
    }
}
