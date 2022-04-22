// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.misc;

import net.minecraft.util.IThreadListener;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import thaumcraft.common.tiles.crafting.TileResearchTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import java.util.Iterator;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import java.util.HashSet;
import java.util.Set;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketStartTheoryToServer implements IMessage, IMessageHandler<PacketStartTheoryToServer, IMessage>
{
    private long pos;
    private Set<String> aids;
    
    public PacketStartTheoryToServer() {
        aids = new HashSet<String>();
    }
    
    public PacketStartTheoryToServer(BlockPos pos, Set<String> aids) {
        this.aids = new HashSet<String>();
        this.pos = pos.toLong();
        this.aids = aids;
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeLong(pos);
        buffer.writeByte(aids.size());
        for (String aid : aids) {
            ByteBufUtils.writeUTF8String(buffer, aid);
        }
    }
    
    public void fromBytes(ByteBuf buffer) {
        pos = buffer.readLong();
        for (int s = buffer.readByte(), a = 0; a < s; ++a) {
            aids.add(ByteBufUtils.readUTF8String(buffer));
        }
    }
    
    public IMessage onMessage(PacketStartTheoryToServer message, MessageContext ctx) {
        IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                World world = ctx.getServerHandler().player.getServerWorld();
                Entity player = ctx.getServerHandler().player;
                BlockPos bp = BlockPos.fromLong(message.pos);
                if (world != null && player != null && player instanceof EntityPlayer && bp != null) {
                    TileEntity te = world.getTileEntity(bp);
                    if (te != null && te instanceof TileResearchTable) {
                        ((TileResearchTable)te).startNewTheory((EntityPlayer)player, message.aids);
                    }
                }
            }
        });
        return null;
    }
}
