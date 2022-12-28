package thaumcraft.common.lib.network;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import thaumcraft.common.lib.network.fx.PacketFXBlockArc;
import thaumcraft.common.lib.network.fx.PacketFXBlockBamf;
import thaumcraft.common.lib.network.fx.PacketFXBlockMist;
import thaumcraft.common.lib.network.fx.PacketFXBoreDig;
import thaumcraft.common.lib.network.fx.PacketFXEssentiaSource;
import thaumcraft.common.lib.network.fx.PacketFXFocusEffect;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpactBurst;
import thaumcraft.common.lib.network.fx.PacketFXInfusionSource;
import thaumcraft.common.lib.network.fx.PacketFXPollute;
import thaumcraft.common.lib.network.fx.PacketFXScanSource;
import thaumcraft.common.lib.network.fx.PacketFXShield;
import thaumcraft.common.lib.network.fx.PacketFXSlash;
import thaumcraft.common.lib.network.fx.PacketFXSonic;
import thaumcraft.common.lib.network.fx.PacketFXWispZap;
import thaumcraft.common.lib.network.fx.PacketFXZap;
import thaumcraft.common.lib.network.misc.PacketAuraToClient;
import thaumcraft.common.lib.network.misc.PacketBiomeChange;
import thaumcraft.common.lib.network.misc.PacketFocusChangeToServer;
import thaumcraft.common.lib.network.misc.PacketItemKeyToServer;
import thaumcraft.common.lib.network.misc.PacketItemToClientContainer;
import thaumcraft.common.lib.network.misc.PacketKnowledgeGain;
import thaumcraft.common.lib.network.misc.PacketLogisticsRequestToServer;
import thaumcraft.common.lib.network.misc.PacketMiscEvent;
import thaumcraft.common.lib.network.misc.PacketMiscStringToServer;
import thaumcraft.common.lib.network.misc.PacketNote;
import thaumcraft.common.lib.network.misc.PacketSealFilterToClient;
import thaumcraft.common.lib.network.misc.PacketSealToClient;
import thaumcraft.common.lib.network.misc.PacketSelectThaumotoriumRecipeToServer;
import thaumcraft.common.lib.network.misc.PacketStartTheoryToServer;
import thaumcraft.common.lib.network.playerdata.PacketFocusNameToServer;
import thaumcraft.common.lib.network.playerdata.PacketFocusNodesToServer;
import thaumcraft.common.lib.network.playerdata.PacketPlayerFlagToServer;
import thaumcraft.common.lib.network.playerdata.PacketSyncKnowledge;
import thaumcraft.common.lib.network.playerdata.PacketSyncProgressToServer;
import thaumcraft.common.lib.network.playerdata.PacketSyncResearchFlagsToServer;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;
import thaumcraft.common.lib.network.playerdata.PacketWarpMessage;
import thaumcraft.common.lib.network.tiles.PacketTileToClient;
import thaumcraft.common.lib.network.tiles.PacketTileToServer;


public class PacketHandler
{
    public static SimpleNetworkWrapper INSTANCE;
    
    public static void preInit() {
        int idx = 0;
        PacketHandler.INSTANCE.registerMessage(PacketBiomeChange.class, PacketBiomeChange.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketMiscEvent.class, PacketMiscEvent.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketKnowledgeGain.class, PacketKnowledgeGain.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketStartTheoryToServer.class, PacketStartTheoryToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(PacketLogisticsRequestToServer.class, PacketLogisticsRequestToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(PacketMiscStringToServer.class, PacketMiscStringToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(PacketSelectThaumotoriumRecipeToServer.class, PacketSelectThaumotoriumRecipeToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(PacketItemToClientContainer.class, PacketItemToClientContainer.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketTileToServer.class, PacketTileToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(PacketTileToClient.class, PacketTileToClient.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketFocusNodesToServer.class, PacketFocusNodesToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(PacketFocusNameToServer.class, PacketFocusNameToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(PacketAuraToClient.class, PacketAuraToClient.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketSealToClient.class, PacketSealToClient.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketSealFilterToClient.class, PacketSealFilterToClient.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketNote.class, PacketNote.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketSyncWarp.class, PacketSyncWarp.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketSyncKnowledge.class, PacketSyncKnowledge.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketWarpMessage.class, PacketWarpMessage.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketNote.class, PacketNote.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(PacketItemKeyToServer.class, PacketItemKeyToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(PacketFocusChangeToServer.class, PacketFocusChangeToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(PacketSyncProgressToServer.class, PacketSyncProgressToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(PacketSyncResearchFlagsToServer.class, PacketSyncResearchFlagsToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(PacketPlayerFlagToServer.class, PacketPlayerFlagToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(PacketFXPollute.class, PacketFXPollute.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketFXBlockBamf.class, PacketFXBlockBamf.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketFXFocusEffect.class, PacketFXFocusEffect.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketFXFocusPartImpact.class, PacketFXFocusPartImpact.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketFXFocusPartImpactBurst.class, PacketFXFocusPartImpactBurst.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketFXBlockMist.class, PacketFXBlockMist.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketFXBlockArc.class, PacketFXBlockArc.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketFXEssentiaSource.class, PacketFXEssentiaSource.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketFXInfusionSource.class, PacketFXInfusionSource.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketFXShield.class, PacketFXShield.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketFXSonic.class, PacketFXSonic.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketFXWispZap.class, PacketFXWispZap.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketFXZap.class, PacketFXZap.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketFXSlash.class, PacketFXSlash.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketFXScanSource.class, PacketFXScanSource.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketFXBoreDig.class, PacketFXBoreDig.class, idx++, Side.CLIENT);
    }
    
    static {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("thaumcraft".toLowerCase());
    }
}
