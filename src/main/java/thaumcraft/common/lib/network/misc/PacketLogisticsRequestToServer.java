// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.misc;

import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import thaumcraft.api.golems.GolemHelper;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketLogisticsRequestToServer implements IMessage, IMessageHandler<PacketLogisticsRequestToServer, IMessage>
{
    private BlockPos pos;
    private ItemStack stack;
    private EnumFacing side;
    private int stacksize;
    
    public PacketLogisticsRequestToServer() {
    }
    
    public PacketLogisticsRequestToServer(final BlockPos pos, final EnumFacing side, final ItemStack stack, final int size) {
        this.pos = pos;
        this.stack = stack;
        this.side = side;
        stacksize = size;
    }
    
    public void toBytes(final ByteBuf buffer) {
        if (pos == null || side == null) {
            buffer.writeBoolean(false);
        }
        else {
            buffer.writeBoolean(true);
            buffer.writeLong(pos.toLong());
            buffer.writeByte(side.getIndex());
        }
        Utils.writeItemStackToBuffer(buffer, stack);
        buffer.writeInt(stacksize);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        if (buffer.readBoolean()) {
            pos = BlockPos.fromLong(buffer.readLong());
            side = EnumFacing.values()[buffer.readByte()];
        }
        stack = Utils.readItemStackFromBuffer(buffer);
        stacksize = buffer.readInt();
    }
    
    public IMessage onMessage(final PacketLogisticsRequestToServer message, final MessageContext ctx) {
        final IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                final World world = ctx.getServerHandler().player.getServerWorld();
                final Entity player = ctx.getServerHandler().player;
                int ui = 0;
                while (message.stacksize > 0) {
                    final ItemStack s = message.stack.copy();
                    s.setCount(Math.min(message.stacksize, s.getMaxStackSize()));
                    final PacketLogisticsRequestToServer val$message = message;
                    val$message.stacksize -= s.getCount();
                    if (message.pos != null) {
                        GolemHelper.requestProvisioning(world, message.pos, message.side, s, ui);
                    }
                    else {
                        GolemHelper.requestProvisioning(world, ctx.getServerHandler().player, s, ui);
                    }
                    ++ui;
                }
            }
        });
        return null;
    }
}
