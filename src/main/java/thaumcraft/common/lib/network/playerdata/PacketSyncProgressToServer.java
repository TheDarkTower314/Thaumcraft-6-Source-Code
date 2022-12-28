package thaumcraft.common.lib.network.playerdata;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.InventoryUtils;


public class PacketSyncProgressToServer implements IMessage, IMessageHandler<PacketSyncProgressToServer, IMessage>
{
    private String key;
    private boolean first;
    private boolean checks;
    private boolean noFlags;
    
    public PacketSyncProgressToServer() {
    }
    
    public PacketSyncProgressToServer(String key, boolean first, boolean checks, boolean noFlags) {
        this.key = key;
        this.first = first;
        this.checks = checks;
        this.noFlags = noFlags;
    }
    
    public PacketSyncProgressToServer(String key, boolean first) {
        this(key, first, false, true);
    }
    
    public void toBytes(ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, key);
        buffer.writeBoolean(first);
        buffer.writeBoolean(checks);
        buffer.writeBoolean(noFlags);
    }
    
    public void fromBytes(ByteBuf buffer) {
        key = ByteBufUtils.readUTF8String(buffer);
        first = buffer.readBoolean();
        checks = buffer.readBoolean();
        noFlags = buffer.readBoolean();
    }
    
    public IMessage onMessage(PacketSyncProgressToServer message, MessageContext ctx) {
        IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                EntityPlayer player = ctx.getServerHandler().player;
                if (player != null && message.first != ThaumcraftCapabilities.knowsResearch(player, message.key)) {
                    if (message.checks && !checkRequisites(player, message.key)) {
                        return;
                    }
                    if (message.noFlags) {
                        ResearchManager.noFlags = true;
                    }
                    ResearchManager.progressResearch(player, message.key);
                }
            }
        });
        return null;
    }
    
    private boolean checkRequisites(EntityPlayer player, String key) {
        ResearchEntry research = ResearchCategories.getResearch(key);
        if (research.getStages() != null) {
            int currentStage = ThaumcraftCapabilities.getKnowledge(player).getResearchStage(key) - 1;
            if (currentStage < 0) {
                return false;
            }
            if (currentStage >= research.getStages().length) {
                return true;
            }
            ResearchStage stage = research.getStages()[currentStage];
            Object[] o = stage.getObtain();
            if (o != null) {
                for (int a = 0; a < o.length; ++a) {
                    ItemStack ts = ItemStack.EMPTY;
                    boolean ore = false;
                    if (o[a] instanceof ItemStack) {
                        ts = (ItemStack)o[a];
                    }
                    else {
                        NonNullList<ItemStack> nnl = OreDictionary.getOres((String)o[a]);
                        ts = nnl.get(0);
                        ore = true;
                    }
                    if (!InventoryUtils.isPlayerCarryingAmount(player, ts, ore)) {
                        return false;
                    }
                }
                for (int a = 0; a < o.length; ++a) {
                    boolean ore2 = false;
                    ItemStack ts2 = ItemStack.EMPTY;
                    if (o[a] instanceof ItemStack) {
                        ts2 = (ItemStack)o[a];
                    }
                    else {
                        NonNullList<ItemStack> nnl = OreDictionary.getOres((String)o[a]);
                        ts2 = nnl.get(0);
                        ore2 = true;
                    }
                    InventoryUtils.consumePlayerItem(player, ts2, true, ore2);
                }
            }
            Object[] c = stage.getCraft();
            if (c != null) {
                for (int a2 = 0; a2 < c.length; ++a2) {
                    if (!ThaumcraftCapabilities.getKnowledge(player).isResearchKnown("[#]" + stage.getCraftReference()[a2])) {
                        return false;
                    }
                }
            }
            String[] r = stage.getResearch();
            if (r != null) {
                for (int a3 = 0; a3 < r.length; ++a3) {
                    if (!ThaumcraftCapabilities.knowsResearchStrict(player, r[a3])) {
                        return false;
                    }
                }
            }
            ResearchStage.Knowledge[] k = stage.getKnow();
            if (k != null) {
                for (int a4 = 0; a4 < k.length; ++a4) {
                    int pk = ThaumcraftCapabilities.getKnowledge(player).getKnowledge(k[a4].type, k[a4].category);
                    if (pk < k[a4].amount) {
                        return false;
                    }
                }
                for (int a4 = 0; a4 < k.length; ++a4) {
                    ResearchManager.addKnowledge(player, k[a4].type, k[a4].category, -k[a4].amount * k[a4].type.getProgression());
                }
            }
        }
        return true;
    }
}
