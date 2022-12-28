package thaumcraft.common.lib.network.playerdata;
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.client.gui.ResearchToast;
import thaumcraft.common.lib.utils.Utils;


public class PacketSyncKnowledge implements IMessage, IMessageHandler<PacketSyncKnowledge, IMessage>
{
    protected NBTTagCompound data;
    
    public PacketSyncKnowledge() {
    }
    
    public PacketSyncKnowledge(EntityPlayer player) {
        IPlayerKnowledge pk = ThaumcraftCapabilities.getKnowledge(player);
        data = pk.serializeNBT();
        for (String key : pk.getResearchList()) {
            pk.clearResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP);
        }
    }
    
    public void toBytes(ByteBuf buffer) {
        Utils.writeNBTTagCompoundToBuffer(buffer, data);
    }
    
    public void fromBytes(ByteBuf buffer) {
        data = Utils.readNBTTagCompoundFromBuffer(buffer);
    }
    
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(PacketSyncKnowledge message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                EntityPlayer player = Minecraft.getMinecraft().player;
                IPlayerKnowledge pk = ThaumcraftCapabilities.getKnowledge(player);
                pk.deserializeNBT(message.data);
                for (String key : pk.getResearchList()) {
                    if (pk.hasResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP)) {
                        ResearchEntry ri = ResearchCategories.getResearch(key);
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
