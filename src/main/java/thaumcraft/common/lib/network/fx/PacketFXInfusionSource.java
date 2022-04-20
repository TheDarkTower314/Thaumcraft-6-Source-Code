// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.fx;

import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.crafting.TilePedestal;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;
import thaumcraft.Thaumcraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketFXInfusionSource implements IMessage, IMessageHandler<PacketFXInfusionSource, IMessage>
{
    private long p1;
    private long p2;
    private int color;
    
    public PacketFXInfusionSource() {
    }
    
    public PacketFXInfusionSource(final BlockPos pos, final BlockPos pos2, final int color) {
        this.p1 = pos.toLong();
        this.p2 = pos2.toLong();
        this.color = color;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeLong(this.p1);
        buffer.writeLong(this.p2);
        buffer.writeInt(this.color);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        this.p1 = buffer.readLong();
        this.p2 = buffer.readLong();
        this.color = buffer.readInt();
    }
    
    public IMessage onMessage(final PacketFXInfusionSource message, final MessageContext ctx) {
        final BlockPos p1 = BlockPos.fromLong(message.p1);
        final BlockPos p2 = BlockPos.fromLong(message.p2);
        final String key = p2.getX() + ":" + p2.getY() + ":" + p2.getZ() + ":" + message.color;
        final TileEntity tile = Thaumcraft.proxy.getClientWorld().getTileEntity(p1);
        if (tile != null && tile instanceof TileInfusionMatrix) {
            int count = 15;
            if (Thaumcraft.proxy.getClientWorld().getTileEntity(p2) != null && Thaumcraft.proxy.getClientWorld().getTileEntity(p2) instanceof TilePedestal) {
                count = 60;
            }
            final TileInfusionMatrix is = (TileInfusionMatrix)tile;
            if (is.sourceFX.containsKey(key)) {
                final TileInfusionMatrix.SourceFX sf = is.sourceFX.get(key);
                sf.ticks = count;
                is.sourceFX.put(key, sf);
            }
            else {
                is.sourceFX.put(key, is.new SourceFX(p2, count, message.color));
            }
        }
        return null;
    }
}
