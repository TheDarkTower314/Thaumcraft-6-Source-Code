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
        ofx = 0.0;
        ofy = 0.0;
        rotation = 0.0f;
        runeIndex = 0;
        if (f1 == 0.0f) {
            f1 = 1.0f;
        }
        rotation = (float)(rand.nextInt(4) * 90);
        particleRed = f1;
        particleGreen = f2;
        particleBlue = f3;
        particleGravity = 0.0f;
        final double motionX = 0.0;
        motionZ = motionX;
        motionY = motionX;
        this.motionX = motionX;
        particleMaxAge = 3 * m;
        setSize(0.01f, 0.01f);
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        runeIndex = (int)(Math.random() * 16.0 + 224.0);
        ofx = rand.nextFloat() * 0.2;
        ofy = -0.3 + rand.nextFloat() * 0.6;
        particleScale = (float)(1.0 + rand.nextGaussian() * 0.10000000149011612);
        particleAlpha = 0.0f;
    }
    
    public void setScale(final float s) {
        particleScale = s;
    }
    
    public void setOffsetX(final double f) {
        ofx = f;
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity p_180434_2_, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, particleAlpha / 2.0f);
        final float var13 = (float)(prevPosX + (posX - prevPosX) * f - FXBlockRunes.interpPosX);
        final float var14 = (float)(prevPosY + (posY - prevPosY) * f - FXBlockRunes.interpPosY);
        final float var15 = (float)(prevPosZ + (posZ - prevPosZ) * f - FXBlockRunes.interpPosZ);
        GL11.glTranslated(var13, var14, var15);
        GL11.glRotatef(rotation, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
        GL11.glTranslated(ofx, ofy, -0.51);
        final float var16 = runeIndex % 16 / 64.0f;
        final float var17 = var16 + 0.015625f;
        final float var18 = 0.09375f;
        final float var19 = var18 + 0.015625f;
        final float var20 = 0.3f * particleScale;
        final float var21 = 1.0f;
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        final int i = 240;
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        wr.pos(-0.5 * var20, 0.5 * var20, 0.0).tex(var17, var19).color(particleRed * var21, particleGreen * var21, particleBlue * var21, particleAlpha / 2.0f).lightmap(j, k).endVertex();
        wr.pos(0.5 * var20, 0.5 * var20, 0.0).tex(var17, var18).color(particleRed * var21, particleGreen * var21, particleBlue * var21, particleAlpha / 2.0f).lightmap(j, k).endVertex();
        wr.pos(0.5 * var20, -0.5 * var20, 0.0).tex(var16, var18).color(particleRed * var21, particleGreen * var21, particleBlue * var21, particleAlpha / 2.0f).lightmap(j, k).endVertex();
        wr.pos(-0.5 * var20, -0.5 * var20, 0.0).tex(var16, var19).color(particleRed * var21, particleGreen * var21, particleBlue * var21, particleAlpha / 2.0f).lightmap(j, k).endVertex();
        Tessellator.getInstance().draw();
        GL11.glPopMatrix();
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }
    
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        final float threshold = particleMaxAge / 5.0f;
        if (particleAge <= threshold) {
            particleAlpha = particleAge / threshold;
        }
        else {
            particleAlpha = (particleMaxAge - particleAge) / (float) particleMaxAge;
        }
        if (particleAge++ >= particleMaxAge) {
            setExpired();
        }
        motionY -= 0.04 * particleGravity;
        posX += motionX;
        posY += motionY;
        posZ += motionZ;
    }
    
    public void setGravity(final float value) {
        particleGravity = value;
    }
}
