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
    
    public PacketSyncWarp(final EntityPlayer player) {
        final IPlayerWarp pk = ThaumcraftCapabilities.getWarp(player);
        this.data = pk.serializeNBT();
    }
    
    public void toBytes(final ByteBuf buffer) {
        Utils.writeNBTTagCompoundToBuffer(buffer, this.data);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        this.data = Utils.readNBTTagCompoundFromBuffer(buffer);
    }
    
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(final PacketSyncWarp message, final MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                final EntityPlayer player = Minecraft.getMinecraft().player;
                final IPlayerWarp pk = ThaumcraftCapabilities.getWarp(player);
                pk.deserializeNBT(message.data);
            }
        });
        return null;
    }
}
