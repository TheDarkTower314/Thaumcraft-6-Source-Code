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
        sizeMod = 0.0f;
        final float particleRed = 0.6f;
        particleBlue = particleRed;
        particleGreen = particleRed;
        this.particleRed = particleRed;
        particleScale = 0.0f;
        targetX = tx;
        targetY = ty;
        targetZ = tz;
        particleMaxAge = 1000;
        final float f3 = 0.01f;
        motionX = (float) rand.nextGaussian() * f3;
        motionY = (float) rand.nextGaussian() * f3;
        motionZ = (float) rand.nextGaussian() * f3;
        sizeMod = (float)(45 + rand.nextInt(15));
        this.particleRed = 0.2f;
        particleGreen = 0.6f + rand.nextFloat() * 0.3f;
        particleBlue = 0.2f;
        particleGravity = 0.2f;
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity p_180434_2_, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        final float bob = MathHelper.sin(particleAge / 3.0f) * 0.3f + 6.0f;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.75f);
        final int part = particleAge % 16;
        final float var8 = part / 64.0f;
        final float var9 = var8 + 0.015625f;
        final float var10 = 0.125f;
        final float var11 = var10 + 0.015625f;
        final float var12 = 0.1f * particleScale * bob;
        final float var13 = (float)(prevPosX + (posX - prevPosX) * f - FXVisSparkle.interpPosX);
        final float var14 = (float)(prevPosY + (posY - prevPosY) * f - FXVisSparkle.interpPosY);
        final float var15 = (float)(prevPosZ + (posZ - prevPosZ) * f - FXVisSparkle.interpPosZ);
        final float var16 = 1.0f;
        final int i = 240;
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        wr.pos(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12).tex(var9, var11).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 0.5f).lightmap(j, k).endVertex();
        wr.pos(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12).tex(var9, var10).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 0.5f).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12).tex(var8, var10).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 0.5f).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12).tex(var8, var11).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 0.5f).lightmap(j, k).endVertex();
    }
    
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        move(motionX, motionY, motionZ);
        motionX *= 0.985;
        motionY *= 0.985;
        motionZ *= 0.985;
        double dx = targetX - posX;
        double dy = targetY - posY;
        double dz = targetZ - posZ;
        final double d13 = 0.10000000149011612;
        final double d14 = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        if (d14 < 2.0) {
            particleScale *= 0.95f;
        }
        if (d14 < 0.2) {
            particleMaxAge = particleAge;
        }
        if (particleAge < 10) {
            particleScale = particleAge / sizeMod;
        }
        dx /= d14;
        dy /= d14;
        dz /= d14;
        motionX += dx * d13;
        motionY += dy * d13;
        motionZ += dz * d13;
        motionX = MathHelper.clamp((float) motionX, -0.1f, 0.1f);
        motionY = MathHelper.clamp((float) motionY, -0.1f, 0.1f);
        motionZ = MathHelper.clamp((float) motionZ, -0.1f, 0.1f);
        if (particleAge++ >= particleMaxAge) {
            setExpired();
        }
    }
    
    public void setGravity(final float value) {
        particleGravity = value;
    }
}
