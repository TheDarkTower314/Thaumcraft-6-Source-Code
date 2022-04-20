// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.fx;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.lib.utils.Utils;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketFXBlockBamf implements IMessage, IMessageHandler<PacketFXBlockBamf, IMessage>
{
    private double x;
    private double y;
    private double z;
    private int color;
    private byte flags;
    private byte face;
    
    public PacketFXBlockBamf() {
    }
    
    public PacketFXBlockBamf(final double x, final double y, final double z, final int color, final boolean sound, final boolean flair, final EnumFacing side) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        int f = 0;
        if (sound) {
            f = Utils.setBit(f, 0);
        }
        if (flair) {
            f = Utils.setBit(f, 1);
        }
        if (side != null) {
            this.face = (byte)side.ordinal();
        }
        else {
            this.face = -1;
        }
        this.flags = (byte)f;
    }
    
    public PacketFXBlockBamf(final BlockPos pos, final int color, final boolean sound, final boolean flair, final EnumFacing side) {
        this.x = pos.getX() + 0.5;
        this.y = pos.getY() + 0.5;
        this.z = pos.getZ() + 0.5;
        this.color = color;
        int f = 0;
        if (sound) {
            f = Utils.setBit(f, 0);
        }
        if (flair) {
            f = Utils.setBit(f, 1);
        }
        if (side != null) {
            this.face = (byte)side.ordinal();
        }
        else {
            this.face = -1;
        }
        this.flags = (byte)f;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeDouble(this.x);
        buffer.writeDouble(this.y);
        buffer.writeDouble(this.z);
        buffer.writeInt(this.color);
        buffer.writeByte(this.flags);
        buffer.writeByte(this.face);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
        this.color = buffer.readInt();
        this.flags = buffer.readByte();
        this.face = buffer.readByte();
    }
    
    public IMessage onMessage(final PacketFXBlockBamf message, final MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                PacketFXBlockBamf.this.processMessage(message);
            }
        });
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    void processMessage(final PacketFXBlockBamf message) {
        EnumFacing side = null;
        if (message.face >= 0) {
            side = EnumFacing.getFront(message.face);
        }
        if (message.color != -9999) {
            FXDispatcher.INSTANCE.drawBamf(message.x, message.y, message.z, message.color, Utils.getBit(message.flags, 0), Utils.getBit(message.flags, 1), side);
        }
        else {
            FXDispatcher.INSTANCE.drawBamf(message.x, message.y, message.z, Utils.getBit(message.flags, 0), Utils.getBit(message.flags, 1), side);
        }
    }
}
