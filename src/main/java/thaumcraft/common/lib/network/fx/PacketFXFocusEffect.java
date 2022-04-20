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
        buffer.writeFloat(this.x);
        buffer.writeFloat(this.y);
        buffer.writeFloat(this.z);
        buffer.writeFloat(this.mx);
        buffer.writeFloat(this.my);
        buffer.writeFloat(this.mz);
        ByteBufUtils.writeUTF8String(buffer, this.parts);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        this.x = buffer.readFloat();
        this.y = buffer.readFloat();
        this.z = buffer.readFloat();
        this.mx = buffer.readFloat();
        this.my = buffer.readFloat();
        this.mz = buffer.readFloat();
        this.parts = ByteBufUtils.readUTF8String(buffer);
    }
    
    public IMessage onMessage(final PacketFXFocusEffect message, final MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                PacketFXFocusEffect.this.processMessage(message);
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
