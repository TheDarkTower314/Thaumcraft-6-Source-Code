package thaumcraft.common.lib.network.misc;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.client.lib.events.HudHandler;
import thaumcraft.client.lib.events.RenderEventHandler;
import thaumcraft.common.lib.SoundsTC;


public class PacketKnowledgeGain implements IMessage, IMessageHandler<PacketKnowledgeGain, IMessage>
{
    private byte type;
    private String cat;
    
    public PacketKnowledgeGain() {
    }
    
    public PacketKnowledgeGain(byte type, String value) {
        this.type = type;
        cat = ((value == null) ? "" : value);
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeByte(type);
        ByteBufUtils.writeUTF8String(buffer, cat);
    }
    
    public void fromBytes(ByteBuf buffer) {
        type = buffer.readByte();
        cat = ByteBufUtils.readUTF8String(buffer);
    }
    
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(PacketKnowledgeGain message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                processMessage(message);
            }
        });
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    void processMessage(PacketKnowledgeGain message) {
        EntityPlayer p = Minecraft.getMinecraft().player;
        IPlayerKnowledge.EnumKnowledgeType type = IPlayerKnowledge.EnumKnowledgeType.values()[message.type];
        ResearchCategory cat = (message.cat.length() > 0) ? ResearchCategories.getResearchCategory(message.cat) : null;
        RenderEventHandler instance = RenderEventHandler.INSTANCE;
        RenderEventHandler.hudHandler.knowledgeGainTrackers.add(new HudHandler.KnowledgeGainTracker(type, cat, 40 + p.world.rand.nextInt(20), p.world.rand.nextLong()));
        p.world.playSound(p.posX, p.posY, p.posZ, SoundsTC.learn, SoundCategory.AMBIENT, 1.0f, 1.0f, false);
    }
}
