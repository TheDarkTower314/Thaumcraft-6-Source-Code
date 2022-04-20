// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.particles;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.World;
import net.minecraft.client.particle.Particle;

public class FXBlockRunes extends Particle
{
    double ofx;
    double ofy;
    float rotation;
    int runeIndex;
    
    public FXBlockRunes(final World world, final double d, final double d1, final double d2, float f1, final float f2, final float f3, final int m) {
        super(world, d, d1, d2, 0.0, 0.0, 0.0);
        this.ofx = 0.0;
        this.ofy = 0.0;
        this.rotation = 0.0f;
        this.runeIndex = 0;
        if (f1 == 0.0f) {
            f1 = 1.0f;
        }
        this.rotation = (float)(this.rand.nextInt(4) * 90);
        this.particleRed = f1;
        this.particleGreen = f2;
        this.particleBlue = f3;
        this.particleGravity = 0.0f;
        final double motionX = 0.0;
        this.motionZ = motionX;
        this.motionY = motionX;
        this.motionX = motionX;
        this.particleMaxAge = 3 * m;
        this.setSize(0.01f, 0.01f);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.runeIndex = (int)(Math.random() * 16.0 + 224.0);
        this.ofx = this.rand.nextFloat() * 0.2;
        this.ofy = -0.3 + this.rand.nextFloat() * 0.6;
        this.particleScale = (float)(1.0 + this.rand.nextGaussian() * 0.10000000149011612);
        this.particleAlpha = 0.0f;
    }
    
    public void setScale(final float s) {
        this.particleScale = s;
    }
    
    public void setOffsetX(final double f) {
        this.ofx = f;
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity p_180434_2_, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, this.particleAlpha / 2.0f);
        final float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * f - FXBlockRunes.interpPosX);
        final float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * f - FXBlockRunes.interpPosY);
        final float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * f - FXBlockRunes.interpPosZ);
        GL11.glTranslated(var13, var14, var15);
        GL11.glRotatef(this.rotation, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
        GL11.glTranslated(this.ofx, this.ofy, -0.51);
        final float var16 = this.runeIndex % 16 / 64.0f;
        final float var17 = var16 + 0.015625f;
        final float var18 = 0.09375f;
        final float var19 = var18 + 0.015625f;
        final float var20 = 0.3f * this.particleScale;
        final float var21 = 1.0f;
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        final int i = 240;
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        wr.pos(-0.5 * var20, 0.5 * var20, 0.0).tex(var17, var19).color(this.particleRed * var21, this.particleGreen * var21, this.particleBlue * var21, this.particleAlpha / 2.0f).lightmap(j, k).endVertex();
        wr.pos(0.5 * var20, 0.5 * var20, 0.0).tex(var17, var18).color(this.particleRed * var21, this.particleGreen * var21, this.particleBlue * var21, this.particleAlpha / 2.0f).lightmap(j, k).endVertex();
        wr.pos(0.5 * var20, -0.5 * var20, 0.0).tex(var16, var18).color(this.particleRed * var21, this.particleGreen * var21, this.particleBlue * var21, this.particleAlpha / 2.0f).lightmap(j, k).endVertex();
        wr.pos(-0.5 * var20, -0.5 * var20, 0.0).tex(var16, var19).color(this.particleRed * var21, this.particleGreen * var21, this.particleBlue * var21, this.particleAlpha / 2.0f).lightmap(j, k).endVertex();
        Tessellator.getInstance().draw();
        GL11.glPopMatrix();
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }
    
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        final float threshold = this.particleMaxAge / 5.0f;
        if (this.particleAge <= threshold) {
            this.particleAlpha = this.particleAge / threshold;
        }
        else {
            this.particleAlpha = (this.particleMaxAge - this.particleAge) / (float)this.particleMaxAge;
        }
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
        this.motionY -= 0.04 * this.particleGravity;
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
    }
    
    public void setGravity(final float value) {
        this.particleGravity = value;
    }
}
