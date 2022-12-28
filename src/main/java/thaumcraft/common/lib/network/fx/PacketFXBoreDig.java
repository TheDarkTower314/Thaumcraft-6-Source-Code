package thaumcraft.common.lib.network.fx;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.events.ServerEvents;


public class PacketFXBoreDig implements IMessage, IMessageHandler<PacketFXBoreDig, IMessage>
{
    private int x;
    private int y;
    private int z;
    private int bore;
    private int delay;
    
    public PacketFXBoreDig() {
    }
    
    public PacketFXBoreDig(BlockPos pos, Entity bore, int delay) {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
        this.bore = bore.getEntityId();
        this.delay = delay;
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeInt(bore);
        buffer.writeInt(delay);
    }
    
    public void fromBytes(ByteBuf buffer) {
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        bore = buffer.readInt();
        delay = buffer.readInt();
    }
    
    public IMessage onMessage(PacketFXBoreDig message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                processMessage(message);
            }
        });
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    void processMessage(PacketFXBoreDig message) {
        try {
            World world = Minecraft.getMinecraft().world;
            BlockPos pos = new BlockPos(message.x, message.y, message.z);
            Entity entity = world.getEntityByID(message.bore);
            if (entity == null) {
                return;
            }
            IBlockState ts = world.getBlockState(pos);
            if (ts.getBlock() == Blocks.AIR) {
                return;
            }
            for (int a = 0; a < message.delay; ++a) {
                ServerEvents.addRunnableClient(world, new Runnable() {
                    @Override
                    public void run() {
                        FXDispatcher.INSTANCE.boreDigFx(pos.getX(), pos.getY(), pos.getZ(), entity, ts, ts.getBlock().getMetaFromState(ts) >> 12 & 0xFF, message.delay);
                    }
                }, a);
            }
        }
        catch (Exception ex) {}
    }
}
