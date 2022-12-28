package thaumcraft.common.lib.network.fx;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.Thaumcraft;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;
import thaumcraft.common.tiles.crafting.TilePedestal;


public class PacketFXInfusionSource implements IMessage, IMessageHandler<PacketFXInfusionSource, IMessage>
{
    private long p1;
    private long p2;
    private int color;
    
    public PacketFXInfusionSource() {
    }
    
    public PacketFXInfusionSource(BlockPos pos, BlockPos pos2, int color) {
        p1 = pos.toLong();
        p2 = pos2.toLong();
        this.color = color;
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeLong(p1);
        buffer.writeLong(p2);
        buffer.writeInt(color);
    }
    
    public void fromBytes(ByteBuf buffer) {
        p1 = buffer.readLong();
        p2 = buffer.readLong();
        color = buffer.readInt();
    }
    
    public IMessage onMessage(PacketFXInfusionSource message, MessageContext ctx) {
        BlockPos p1 = BlockPos.fromLong(message.p1);
        BlockPos p2 = BlockPos.fromLong(message.p2);
        String key = p2.getX() + ":" + p2.getY() + ":" + p2.getZ() + ":" + message.color;
        TileEntity tile = Thaumcraft.proxy.getClientWorld().getTileEntity(p1);
        if (tile != null && tile instanceof TileInfusionMatrix) {
            int count = 15;
            if (Thaumcraft.proxy.getClientWorld().getTileEntity(p2) != null && Thaumcraft.proxy.getClientWorld().getTileEntity(p2) instanceof TilePedestal) {
                count = 60;
            }
            TileInfusionMatrix is = (TileInfusionMatrix)tile;
            if (is.sourceFX.containsKey(key)) {
                TileInfusionMatrix.SourceFX sf = is.sourceFX.get(key);
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
