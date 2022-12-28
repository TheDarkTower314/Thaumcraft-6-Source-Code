package thaumcraft.common.lib.network.tiles;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.Thaumcraft;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.tiles.TileThaumcraft;


public class PacketTileToClient implements IMessage, IMessageHandler<PacketTileToClient, IMessage>
{
    private long pos;
    private NBTTagCompound nbt;
    
    public PacketTileToClient() {
    }
    
    public PacketTileToClient(BlockPos pos, NBTTagCompound nbt) {
        this.pos = pos.toLong();
        this.nbt = nbt;
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeLong(pos);
        Utils.writeNBTTagCompoundToBuffer(buffer, nbt);
    }
    
    public void fromBytes(ByteBuf buffer) {
        pos = buffer.readLong();
        nbt = Utils.readNBTTagCompoundFromBuffer(buffer);
    }
    
    public IMessage onMessage(PacketTileToClient message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                World world = Thaumcraft.proxy.getClientWorld();
                BlockPos bp = BlockPos.fromLong(message.pos);
                if (world != null && bp != null) {
                    TileEntity te = world.getTileEntity(bp);
                    if (te != null && te instanceof TileThaumcraft) {
                        ((TileThaumcraft)te).messageFromServer((message.nbt == null) ? new NBTTagCompound() : message.nbt);
                    }
                }
            }
        });
        return null;
    }
}
