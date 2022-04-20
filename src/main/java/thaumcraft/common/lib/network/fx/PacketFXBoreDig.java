// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.fx;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.world.World;
import thaumcraft.common.lib.events.ServerEvents;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketFXBoreDig implements IMessage, IMessageHandler<PacketFXBoreDig, IMessage>
{
    private int x;
    private int y;
    private int z;
    private int bore;
    private int delay;
    
    public PacketFXBoreDig() {
    }
    
    public PacketFXBoreDig(final BlockPos pos, final Entity bore, final int delay) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.bore = bore.getEntityId();
        this.delay = delay;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.x);
        buffer.writeInt(this.y);
        buffer.writeInt(this.z);
        buffer.writeInt(this.bore);
        buffer.writeInt(this.delay);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
        this.bore = buffer.readInt();
        this.delay = buffer.readInt();
    }
    
    public IMessage onMessage(final PacketFXBoreDig message, final MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                PacketFXBoreDig.this.processMessage(message);
            }
        });
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    void processMessage(final PacketFXBoreDig message) {
        try {
            final World world = Minecraft.getMinecraft().world;
            final BlockPos pos = new BlockPos(message.x, message.y, message.z);
            final Entity entity = world.getEntityByID(message.bore);
            if (entity == null) {
                return;
            }
            final IBlockState ts = world.getBlockState(pos);
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
        catch (final Exception ex) {}
    }
}
