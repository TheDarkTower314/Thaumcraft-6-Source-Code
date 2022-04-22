// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.playerdata;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTBase;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSyncWarp implements IMessage, IMessageHandler<PacketSyncWarp, IMessage>
{
    protected NBTTagCompound data;
    
    public PacketSyncWarp() {
    }
    
    public PacketSyncWarp(EntityPlayer player) {
        IPlayerWarp pk = ThaumcraftCapabilities.getWarp(player);
        data = pk.serializeNBT();
    }
    
    public void toBytes(ByteBuf buffer) {
        Utils.writeNBTTagCompoundToBuffer(buffer, data);
    }
    
    public void fromBytes(ByteBuf buffer) {
        data = Utils.readNBTTagCompoundFromBuffer(buffer);
    }
    
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(PacketSyncWarp message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                EntityPlayer player = Minecraft.getMinecraft().player;
                IPlayerWarp pk = ThaumcraftCapabilities.getWarp(player);
                pk.deserializeNBT(message.data);
            }
        });
        return null;
    }
}
