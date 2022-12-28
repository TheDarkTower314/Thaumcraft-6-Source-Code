package thaumcraft.common.lib.network;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.server.MinecraftServer;


public class FakeNetHandlerPlayServer extends NetHandlerPlayServer
{
    public FakeNetHandlerPlayServer(MinecraftServer server, NetworkManager networkManagerIn, EntityPlayerMP playerIn) {
        super(server, networkManagerIn, playerIn);
    }
    
    public void update() {
    }
    
    public void processInput(CPacketInput packetIn) {
    }
    
    public void processPlayer(CPacketPlayer packetIn) {
    }
    
    public void sendPacket(Packet packetIn) {
    }
    
    public void processUseEntity(CPacketUseEntity packetIn) {
    }
    
    public void processClientStatus(CPacketClientStatus packetIn) {
    }
    
    public void processCloseWindow(CPacketCloseWindow packetIn) {
    }
    
    public void processClickWindow(CPacketClickWindow packetIn) {
    }
    
    public void processEnchantItem(CPacketEnchantItem packetIn) {
    }
    
    public void processCreativeInventoryAction(CPacketCreativeInventoryAction packetIn) {
    }
    
    public void processConfirmTransaction(CPacketConfirmTransaction packetIn) {
    }
    
    public void processUpdateSign(CPacketUpdateSign packetIn) {
    }
    
    public void processKeepAlive(CPacketKeepAlive packetIn) {
    }
    
    public void processTabComplete(CPacketTabComplete packetIn) {
    }
    
    public void processClientSettings(CPacketClientSettings packetIn) {
    }
    
    public void processCustomPayload(CPacketCustomPayload packetIn) {
    }
}
