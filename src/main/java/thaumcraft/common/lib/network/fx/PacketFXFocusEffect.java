// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.fx;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.casters.IFocusElement;
import net.minecraft.world.World;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketFXFocusEffect implements IMessage, IMessageHandler<PacketFXFocusEffect, IMessage>
{
    float x;
    float y;
    float z;
    float mx;
    float my;
    float mz;
    String parts;
    
    public PacketFXFocusEffect() {
    }
    
    public PacketFXFocusEffect(final float x, final float y, final float z, final float mx, final float my, final float mz, final String[] parts) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.mx = mx;
        this.my = my;
        this.mz = mz;
        this.parts = "";
        for (int a = 0; a < parts.length; ++a) {
            if (a > 0) {
                this.parts += "%";
            }
            this.parts += parts[a];
        }
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeFloat(x);
        buffer.writeFloat(y);
        buffer.writeFloat(z);
        buffer.writeFloat(mx);
        buffer.writeFloat(my);
        buffer.writeFloat(mz);
        ByteBufUtils.writeUTF8String(buffer, parts);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        x = buffer.readFloat();
        y = buffer.readFloat();
        z = buffer.readFloat();
        mx = buffer.readFloat();
        my = buffer.readFloat();
        mz = buffer.readFloat();
        parts = ByteBufUtils.readUTF8String(buffer);
    }
    
    public IMessage onMessage(final PacketFXFocusEffect message, final MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                processMessage(message);
            }
        });
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    void processMessage(final PacketFXFocusEffect message) {
        final String[] partKeys = message.parts.split("%");
        final int amt = Math.max(1, 10 / partKeys.length);
        for (final String k : partKeys) {
            final IFocusElement part = FocusEngine.getElement(k);
            if (part != null && part instanceof FocusEffect) {
                for (int a = 0; a < amt; ++a) {
                    ((FocusEffect)part).renderParticleFX(Minecraft.getMinecraft().world, message.x, message.y, message.z, message.mx + Minecraft.getMinecraft().world.rand.nextGaussian() / 20.0, message.my + Minecraft.getMinecraft().world.rand.nextGaussian() / 20.0, message.mz + Minecraft.getMinecraft().world.rand.nextGaussian() / 20.0);
                }
            }
        }
    }
}
