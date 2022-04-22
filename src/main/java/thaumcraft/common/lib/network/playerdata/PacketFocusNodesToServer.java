// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.playerdata;

import net.minecraft.util.IThreadListener;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import java.util.Iterator;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.tiles.crafting.FocusElementNode;
import java.util.HashMap;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketFocusNodesToServer implements IMessage, IMessageHandler<PacketFocusNodesToServer, IMessage>
{
    private long loc;
    private HashMap<Integer, FocusElementNode> data;
    private String name;
    
    public PacketFocusNodesToServer() {
        data = new HashMap<Integer, FocusElementNode>();
    }
    
    public PacketFocusNodesToServer(final BlockPos pos, final HashMap<Integer, FocusElementNode> data, final String name) {
        this.data = new HashMap<Integer, FocusElementNode>();
        loc = pos.toLong();
        this.data = data;
        this.name = name;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeLong(loc);
        buffer.writeByte(data.size());
        for (final FocusElementNode node : data.values()) {
            Utils.writeNBTTagCompoundToBuffer(buffer, node.serialize());
        }
        ByteBufUtils.writeUTF8String(buffer, name);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        loc = buffer.readLong();
        for (int m = buffer.readByte(), a = 0; a < m; ++a) {
            final FocusElementNode node = new FocusElementNode();
            node.deserialize(Utils.readNBTTagCompoundFromBuffer(buffer));
            data.put(node.id, node);
        }
        name = ByteBufUtils.readUTF8String(buffer);
    }
    
    public IMessage onMessage(final PacketFocusNodesToServer message, final MessageContext ctx) {
        final IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                if (ctx.getServerHandler().player == null) {
                    return;
                }
                final BlockPos pos = BlockPos.fromLong(message.loc);
                final TileEntity rt = ctx.getServerHandler().player.world.getTileEntity(pos);
                if (rt != null && rt instanceof TileFocalManipulator) {
                    ((TileFocalManipulator)rt).data.clear();
                    ((TileFocalManipulator)rt).data = message.data;
                    ((TileFocalManipulator)rt).focusName = message.name;
                    rt.markDirty();
                }
            }
        });
        return null;
    }
}
