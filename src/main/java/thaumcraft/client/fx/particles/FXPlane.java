// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.particles;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.client.particle.Particle;

public class FXPlane extends Particle
{
    float angle;
    float angleYaw;
    float anglePitch;
    
    public FXPlane(final World world, final double d, final double d1, final double d2, final double m, final double m1, final double m2, final int life) {
        super(world, d, d1, d2, 0.0, 0.0, 0.0);
        this.particleRed = 1.0f;
        this.particleGreen = 1.0f;
        this.particleBlue = 1.0f;
        this.particleGravity = 0.0f;
        final double motionX = 0.0;
        this.motionZ = motionX;
        this.motionY = motionX;
        this.motionX = motionX;
        this.particleMaxAge = life;
        this.setSize(0.01f, 0.01f);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.particleScale = 1.0f;
        this.particleAlpha = 0.0f;
        final double dx = m - this.posX;
        final double dy = m1 - this.posY;
        final double dz = m2 - this.posZ;
        this.motionX = dx / this.particleMaxAge;
        this.motionY = dy / this.particleMaxAge;
        this.motionZ = dz / this.particleMaxAge;
        this.particleTextureIndexX = 22;
        this.particleTextureIndexY = 10;
        final double d3 = MathHelper.sqrt(dx * dx + dz * dz);
        this.angleYaw = 0.0f;
        this.anglePitch = 0.0f;
        if (d3 >= 1.0E-7) {
            this.angleYaw = (float)(MathHelper.atan2(dz, dx) * 180.0 / 3.141592653589793) - 90.0f;
            this.anglePitch = (float)(-(MathHelper.atan2(dy, d3) * 180.0 / 3.141592653589793));
        }
        this.angle = (float)(this.rand.nextGaussian() * 20.0);
    }
    
    public int getFXLayer() {
        return 0;
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity p_180434_2_, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, this.particleAlpha / 2.0f);
        final float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * f - FXPlane.interpPosX);
        final float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * f - FXPlane.interpPosY);
        final float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * f - FXPlane.interpPosZ);
        GL11.glTranslated(var13, var14, var15);
        GL11.glRotatef(-this.angleYaw + 90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(this.anglePitch + 90.0f, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(this.angle, 0.0f, 1.0f, 0.0f);
        this.particleTextureIndexX = 22 + Math.round((this.particleAge + f) / this.particleMaxAge * 8.0f);
        final float var16 = this.particleTextureIndexX / 32.0f;
        final float var17 = var16 + 0.03125f;
        final float var18 = this.particleTextureIndexY / 32.0f;
        final float var19 = var18 + 0.03125f;
        final float var20 = this.particleScale * (0.5f + (this.particleAge + f) / this.particleMaxAge);
        final float var21 = 1.0f;
        final int i = 240;
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        GL11.glDisable(2884);
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        wr.pos(-0.5 * var20, 0.5 * var20, 0.0).tex(var17, var19).color(this.particleRed * var21, this.particleGreen * var21, this.particleBlue * var21, this.particleAlpha / 2.0f).lightmap(j, k).endVertex();
        wr.pos(0.5 * var20, 0.5 * var20, 0.0).tex(var17, var18).color(this.particleRed * var21, this.particleGreen * var21, this.particleBlue * var21, this.particleAlpha / 2.0f).lightmap(j, k).endVertex();
        wr.pos(0.5 * var20, -0.5 * var20, 0.0).tex(var16, var18).color(this.particleRed * var21, this.particleGreen * var21, this.particleBlue * var21, this.particleAlpha / 2.0f).lightmap(j, k).endVertex();
        wr.pos(-0.5 * var20, -0.5 * var20, 0.0).tex(var16, var19).color(this.particleRed * var21, this.particleGreen * var21, this.particleBlue * var21, this.particleAlpha / 2.0f).lightmap(j, k).endVertex();
        Tessellator.getInstance().draw();
        GL11.glEnable(2884);
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
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
    }
    
    public void setGravity(final float value) {
        this.particleGravity = value;
    }
}
