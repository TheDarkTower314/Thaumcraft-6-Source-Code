package thaumcraft.common.lib.network.misc;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.common.lib.utils.Utils;


public class PacketLogisticsRequestToServer implements IMessage, IMessageHandler<PacketLogisticsRequestToServer, IMessage>
{
    private BlockPos pos;
    private ItemStack stack;
    private EnumFacing side;
    private int stacksize;
    
    public PacketLogisticsRequestToServer() {
    }
    
    public PacketLogisticsRequestToServer(BlockPos pos, EnumFacing side, ItemStack stack, int size) {
        this.pos = pos;
        this.stack = stack;
        this.side = side;
        stacksize = size;
    }
    
    public void toBytes(ByteBuf buffer) {
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
    
    public void fromBytes(ByteBuf buffer) {
        if (buffer.readBoolean()) {
            pos = BlockPos.fromLong(buffer.readLong());
            side = EnumFacing.values()[buffer.readByte()];
        }
        stack = Utils.readItemStackFromBuffer(buffer);
        stacksize = buffer.readInt();
    }
    
    public IMessage onMessage(PacketLogisticsRequestToServer message, MessageContext ctx) {
        IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                World world = ctx.getServerHandler().player.getServerWorld();
                Entity player = ctx.getServerHandler().player;
                int ui = 0;
                while (message.stacksize > 0) {
                    ItemStack s = message.stack.copy();
                    s.setCount(Math.min(message.stacksize, s.getMaxStackSize()));
                    PacketLogisticsRequestToServer val$message = message;
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
