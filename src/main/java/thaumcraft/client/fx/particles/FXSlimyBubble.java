// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.particles;

import org.lwjgl.opengl.GL11;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.World;
import net.minecraft.client.particle.Particle;

public class FXSlimyBubble extends Particle
{
    int particle;
    
    public FXSlimyBubble(final World world, final double d, final double d1, final double d2, final float f) {
        super(world, d, d1, d2, 0.0, 0.0, 0.0);
        this.particle = 144;
        this.particleRed = 1.0f;
        this.particleGreen = 1.0f;
        this.particleBlue = 1.0f;
        this.particleGravity = 0.0f;
        final double motionX = 0.0;
        this.motionZ = motionX;
        this.motionY = motionX;
        this.motionX = motionX;
        this.particleScale = f;
        this.particleMaxAge = 15 + world.rand.nextInt(5);
        this.setSize(0.01f, 0.01f);
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, this.particleAlpha);
        final float var8 = this.particle % 16 / 64.0f;
        final float var9 = var8 + 0.015625f;
        final float var10 = this.particle / 16 / 64.0f;
        final float var11 = var10 + 0.015625f;
        final float var12 = this.particleScale;
        final float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * f - FXSlimyBubble.interpPosX);
        final float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * f - FXSlimyBubble.interpPosY);
        final float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * f - FXSlimyBubble.interpPosZ);
        final int i = this.getBrightnessForRender(f);
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        wr.pos(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12).tex(var9, var11).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        wr.pos(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12).tex(var9, var10).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12).tex(var8, var10).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12).tex(var8, var11).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
    }
    
    public int getFXLayer() {
        return 1;
    }
    
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
        if (this.particleAge - 1 < 6) {
            this.particle = 144 + this.particleAge / 2;
            if (this.particleAge == 5) {
                this.posY += 0.1;
            }
        }
        else if (this.particleAge < this.particleMaxAge - 4) {
            this.motionY += 0.005;
            this.particle = 147 + this.particleAge % 4 / 2;
        }
        else {
            this.motionY /= 2.0;
            this.particle = 150 - (this.particleMaxAge - this.particleAge) / 2;
        }
        this.posY += this.motionY;
    }
}
