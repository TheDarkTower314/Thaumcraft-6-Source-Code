// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.lib.network.playerdata.PacketSyncKnowledge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EventHandlerNetwork
{
    @SubscribeEvent
    public void playerLoggedInEvent(final PlayerEvent.PlayerLoggedInEvent event) {
        final Side side = FMLCommonHandler.instance().getEffectiveSide();
        if (side == Side.SERVER) {
            final EntityPlayer p = event.player;
            PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(p), (EntityPlayerMP)p);
            PacketHandler.INSTANCE.sendTo(new PacketSyncKnowledge(p), (EntityPlayerMP)p);
        }
    }
}
