// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.misc;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.utils.Utils;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.math.BlockPos;
import thaumcraft.Thaumcraft;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketBiomeChange implements IMessage, IMessageHandler<PacketBiomeChange, IMessage>
{
    private int x;
    private int z;
    private short biome;
    
    public PacketBiomeChange() {
    }
    
    public PacketBiomeChange(final int x, final int z, final short biome) {
        this.x = x;
        this.z = z;
        this.biome = biome;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.x);
        buffer.writeInt(this.z);
        buffer.writeShort(this.biome);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        this.x = buffer.readInt();
        this.z = buffer.readInt();
        this.biome = buffer.readShort();
    }
    
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(final PacketBiomeChange message, final MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                Utils.setBiomeAt(Thaumcraft.proxy.getClientWorld(), new BlockPos(message.x, 0, message.z), Biome.getBiome(message.biome));
            }
        });
        return null;
    }
}
