// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.particles;

import org.lwjgl.opengl.GL11;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.World;
import net.minecraft.client.particle.Particle;

public class FXVisSparkle extends Particle
{
    private double targetX;
    private double targetY;
    private double targetZ;
    float sizeMod;
    
    public FXVisSparkle(final World par1World, final double par2, final double par4, final double par6, final double tx, final double ty, final double tz) {
        super(par1World, par2, par4, par6, 0.0, 0.0, 0.0);
        this.sizeMod = 0.0f;
        final float particleRed = 0.6f;
        this.particleBlue = particleRed;
        this.particleGreen = particleRed;
        this.particleRed = particleRed;
        this.particleScale = 0.0f;
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
        this.particleMaxAge = 1000;
        final float f3 = 0.01f;
        this.motionX = (float)this.rand.nextGaussian() * f3;
        this.motionY = (float)this.rand.nextGaussian() * f3;
        this.motionZ = (float)this.rand.nextGaussian() * f3;
        this.sizeMod = (float)(45 + this.rand.nextInt(15));
        this.particleRed = 0.2f;
        this.particleGreen = 0.6f + this.rand.nextFloat() * 0.3f;
        this.particleBlue = 0.2f;
        this.particleGravity = 0.2f;
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity p_180434_2_, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        final float bob = MathHelper.sin(this.particleAge / 3.0f) * 0.3f + 6.0f;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.75f);
        final int part = this.particleAge % 16;
        final float var8 = part / 64.0f;
        final float var9 = var8 + 0.015625f;
        final float var10 = 0.125f;
        final float var11 = var10 + 0.015625f;
        final float var12 = 0.1f * this.particleScale * bob;
        final float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * f - FXVisSparkle.interpPosX);
        final float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * f - FXVisSparkle.interpPosY);
        final float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * f - FXVisSparkle.interpPosZ);
        final float var16 = 1.0f;
        final int i = 240;
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        wr.pos(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12).tex(var9, var11).color(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16, 0.5f).lightmap(j, k).endVertex();
        wr.pos(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12).tex(var9, var10).color(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16, 0.5f).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12).tex(var8, var10).color(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16, 0.5f).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12).tex(var8, var11).color(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16, 0.5f).lightmap(j, k).endVertex();
    }
    
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.985;
        this.motionY *= 0.985;
        this.motionZ *= 0.985;
        double dx = this.targetX - this.posX;
        double dy = this.targetY - this.posY;
        double dz = this.targetZ - this.posZ;
        final double d13 = 0.10000000149011612;
        final double d14 = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        if (d14 < 2.0) {
            this.particleScale *= 0.95f;
        }
        if (d14 < 0.2) {
            this.particleMaxAge = this.particleAge;
        }
        if (this.particleAge < 10) {
            this.particleScale = this.particleAge / this.sizeMod;
        }
        dx /= d14;
        dy /= d14;
        dz /= d14;
        this.motionX += dx * d13;
        this.motionY += dy * d13;
        this.motionZ += dz * d13;
        this.motionX = MathHelper.clamp((float)this.motionX, -0.1f, 0.1f);
        this.motionY = MathHelper.clamp((float)this.motionY, -0.1f, 0.1f);
        this.motionZ = MathHelper.clamp((float)this.motionZ, -0.1f, 0.1f);
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
    }
    
    public void setGravity(final float value) {
        this.particleGravity = value;
    }
}
