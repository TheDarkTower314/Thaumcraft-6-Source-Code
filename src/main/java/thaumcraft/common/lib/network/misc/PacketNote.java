package thaumcraft.common.lib.network.misc;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import thaumcraft.Thaumcraft;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.tiles.devices.TileArcaneEar;


public class PacketNote implements IMessage, IMessageHandler<PacketNote, IMessage>
{
    private int x;
    private int y;
    private int z;
    private int dim;
    private byte note;
    
    public PacketNote() {
    }
    
    public PacketNote(int x, int y, int z, int dim) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
        note = -1;
    }
    
    public PacketNote(int x, int y, int z, int dim, byte note) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
        this.note = note;
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeInt(dim);
        buffer.writeByte(note);
    }
    
    public void fromBytes(ByteBuf buffer) {
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        dim = buffer.readInt();
        note = buffer.readByte();
    }
    
    public IMessage onMessage(PacketNote message, MessageContext ctx) {
        if (ctx.side == Side.CLIENT) {
            if (message.note >= 0) {
                TileEntity tile = Thaumcraft.proxy.getClientWorld().getTileEntity(new BlockPos(message.x, message.y, message.z));
                if (tile != null && tile instanceof TileEntityNote) {
                    ((TileEntityNote)tile).note = message.note;
                }
                else if (tile != null && tile instanceof TileArcaneEar) {
                    ((TileArcaneEar)tile).note = message.note;
                }
            }
        }
        else if (message.note == -1) {
            World world = DimensionManager.getWorld(message.dim);
            if (world == null) {
                return null;
            }
            TileEntity tile2 = world.getTileEntity(new BlockPos(message.x, message.y, message.z));
            byte note = -1;
            if (tile2 != null && tile2 instanceof TileEntityNote) {
                note = ((TileEntityNote)tile2).note;
            }
            else if (tile2 != null && tile2 instanceof TileArcaneEar) {
                note = ((TileArcaneEar)tile2).note;
            }
            if (note >= 0) {
                PacketHandler.INSTANCE.sendToAllAround(new PacketNote(message.x, message.y, message.z, message.dim, note), new NetworkRegistry.TargetPoint(message.dim, message.x, message.y, message.z, 8.0));
            }
        }
        return null;
    }
}
