// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network;

import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.NetHandlerPlayServer;

public class FakeNetHandlerPlayServer extends NetHandlerPlayServer
{
    public FakeNetHandlerPlayServer(final MinecraftServer server, final NetworkManager networkManagerIn, final EntityPlayerMP playerIn) {
        super(server, networkManagerIn, playerIn);
    }
    
    public void update() {
    }
    
    public void processInput(final CPacketInput packetIn) {
    }
    
    public void processPlayer(final CPacketPlayer packetIn) {
    }
    
    public void sendPacket(final Packet packetIn) {
    }
    
    public void processUseEntity(final CPacketUseEntity packetIn) {
    }
    
    public void processClientStatus(final CPacketClientStatus packetIn) {
    }
    
    public void processCloseWindow(final CPacketCloseWindow packetIn) {
    }
    
    public void processClickWindow(final CPacketClickWindow packetIn) {
    }
    
    public void processEnchantItem(final CPacketEnchantItem packetIn) {
    }
    
    public void processCreativeInventoryAction(final CPacketCreativeInventoryAction packetIn) {
    }
    
    public void processConfirmTransaction(final CPacketConfirmTransaction packetIn) {
    }
    
    public void processUpdateSign(final CPacketUpdateSign packetIn) {
    }
    
    public void processKeepAlive(final CPacketKeepAlive packetIn) {
    }
    
    public void processTabComplete(final CPacketTabComplete packetIn) {
    }
    
    public void processClientSettings(final CPacketClientSettings packetIn) {
    }
    
    public void processCustomPayload(final CPacketCustomPayload packetIn) {
    }
}
