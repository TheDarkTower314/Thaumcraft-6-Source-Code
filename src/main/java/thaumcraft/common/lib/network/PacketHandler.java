// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.fx.PacketFXBoreDig;
import thaumcraft.common.lib.network.fx.PacketFXScanSource;
import thaumcraft.common.lib.network.fx.PacketFXSlash;
import thaumcraft.common.lib.network.fx.PacketFXZap;
import thaumcraft.common.lib.network.fx.PacketFXWispZap;
import thaumcraft.common.lib.network.fx.PacketFXSonic;
import thaumcraft.common.lib.network.fx.PacketFXShield;
import thaumcraft.common.lib.network.fx.PacketFXInfusionSource;
import thaumcraft.common.lib.network.fx.PacketFXEssentiaSource;
import thaumcraft.common.lib.network.fx.PacketFXBlockArc;
import thaumcraft.common.lib.network.fx.PacketFXBlockMist;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpactBurst;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;
import thaumcraft.common.lib.network.fx.PacketFXFocusEffect;
import thaumcraft.common.lib.network.fx.PacketFXBlockBamf;
import thaumcraft.common.lib.network.fx.PacketFXPollute;
import thaumcraft.common.lib.network.playerdata.PacketPlayerFlagToServer;
import thaumcraft.common.lib.network.playerdata.PacketSyncResearchFlagsToServer;
import thaumcraft.common.lib.network.playerdata.PacketSyncProgressToServer;
import thaumcraft.common.lib.network.misc.PacketFocusChangeToServer;
import thaumcraft.common.lib.network.misc.PacketItemKeyToServer;
import thaumcraft.common.lib.network.playerdata.PacketWarpMessage;
import thaumcraft.common.lib.network.playerdata.PacketSyncKnowledge;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;
import thaumcraft.common.lib.network.misc.PacketNote;
import thaumcraft.common.lib.network.misc.PacketSealFilterToClient;
import thaumcraft.common.lib.network.misc.PacketSealToClient;
import thaumcraft.common.lib.network.misc.PacketAuraToClient;
import thaumcraft.common.lib.network.playerdata.PacketFocusNameToServer;
import thaumcraft.common.lib.network.playerdata.PacketFocusNodesToServer;
import thaumcraft.common.lib.network.tiles.PacketTileToClient;
import thaumcraft.common.lib.network.tiles.PacketTileToServer;
import thaumcraft.common.lib.network.misc.PacketItemToClientContainer;
import thaumcraft.common.lib.network.misc.PacketSelectThaumotoriumRecipeToServer;
import thaumcraft.common.lib.network.misc.PacketMiscStringToServer;
import thaumcraft.common.lib.network.misc.PacketLogisticsRequestToServer;
import thaumcraft.common.lib.network.misc.PacketStartTheoryToServer;
import thaumcraft.common.lib.network.misc.PacketKnowledgeGain;
import thaumcraft.common.lib.network.misc.PacketMiscEvent;
import net.minecraftforge.fml.relauncher.Side;
import thaumcraft.common.lib.network.misc.PacketBiomeChange;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class PacketHandler
{
    public static final SimpleNetworkWrapper INSTANCE;
    
    public static void preInit() {
        int idx = 0;
        PacketHandler.INSTANCE.registerMessage((Class)PacketBiomeChange.class, (Class)PacketBiomeChange.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketMiscEvent.class, (Class)PacketMiscEvent.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketKnowledgeGain.class, (Class)PacketKnowledgeGain.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketStartTheoryToServer.class, (Class)PacketStartTheoryToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage((Class)PacketLogisticsRequestToServer.class, (Class)PacketLogisticsRequestToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage((Class)PacketMiscStringToServer.class, (Class)PacketMiscStringToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage((Class)PacketSelectThaumotoriumRecipeToServer.class, (Class)PacketSelectThaumotoriumRecipeToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage((Class)PacketItemToClientContainer.class, (Class)PacketItemToClientContainer.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketTileToServer.class, (Class)PacketTileToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage((Class)PacketTileToClient.class, (Class)PacketTileToClient.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFocusNodesToServer.class, (Class)PacketFocusNodesToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFocusNameToServer.class, (Class)PacketFocusNameToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage((Class)PacketAuraToClient.class, (Class)PacketAuraToClient.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketSealToClient.class, (Class)PacketSealToClient.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketSealFilterToClient.class, (Class)PacketSealFilterToClient.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketNote.class, (Class)PacketNote.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketSyncWarp.class, (Class)PacketSyncWarp.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketSyncKnowledge.class, (Class)PacketSyncKnowledge.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketWarpMessage.class, (Class)PacketWarpMessage.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketNote.class, (Class)PacketNote.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage((Class)PacketItemKeyToServer.class, (Class)PacketItemKeyToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFocusChangeToServer.class, (Class)PacketFocusChangeToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage((Class)PacketSyncProgressToServer.class, (Class)PacketSyncProgressToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage((Class)PacketSyncResearchFlagsToServer.class, (Class)PacketSyncResearchFlagsToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage((Class)PacketPlayerFlagToServer.class, (Class)PacketPlayerFlagToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFXPollute.class, (Class)PacketFXPollute.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFXBlockBamf.class, (Class)PacketFXBlockBamf.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFXFocusEffect.class, (Class)PacketFXFocusEffect.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFXFocusPartImpact.class, (Class)PacketFXFocusPartImpact.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFXFocusPartImpactBurst.class, (Class)PacketFXFocusPartImpactBurst.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFXBlockMist.class, (Class)PacketFXBlockMist.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFXBlockArc.class, (Class)PacketFXBlockArc.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFXEssentiaSource.class, (Class)PacketFXEssentiaSource.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFXInfusionSource.class, (Class)PacketFXInfusionSource.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFXShield.class, (Class)PacketFXShield.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFXSonic.class, (Class)PacketFXSonic.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFXWispZap.class, (Class)PacketFXWispZap.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFXZap.class, (Class)PacketFXZap.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFXSlash.class, (Class)PacketFXSlash.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFXScanSource.class, (Class)PacketFXScanSource.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage((Class)PacketFXBoreDig.class, (Class)PacketFXBoreDig.class, idx++, Side.CLIENT);
    }
    
    static {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("thaumcraft".toLowerCase());
    }
}
