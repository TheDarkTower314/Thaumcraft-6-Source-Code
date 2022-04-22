// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.particles;

import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.World;
import net.minecraft.client.particle.Particle;

public class FXSmokeSpiral extends Particle
{
    private float radius;
    private int start;
    private int miny;
    
    public FXSmokeSpiral(final World world, final double d, final double d1, final double d2, final float radius, final int start, final int miny) {
        super(world, d, d1, d2, 0.0, 0.0, 0.0);
        this.radius = 1.0f;
        this.start = 0;
        this.miny = 0;
        particleGravity = -0.01f;
        final double motionX = 0.0;
        motionZ = motionX;
        motionY = motionX;
        this.motionX = motionX;
        particleScale *= 1.0f;
        particleMaxAge = 20 + world.rand.nextInt(10);
        setSize(0.01f, 0.01f);
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        this.radius = radius;
        this.start = start;
        this.miny = miny;
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.66f * particleAlpha);
        final int particle = (int)(1.0f + particleAge / (float) particleMaxAge * 4.0f);
        final float r1 = start + 720.0f * ((particleAge + f) / particleMaxAge);
        final float r2 = 90.0f - 180.0f * ((particleAge + f) / particleMaxAge);
        float mX = -MathHelper.sin(r1 / 180.0f * 3.1415927f) * MathHelper.cos(r2 / 180.0f * 3.1415927f);
        float mZ = MathHelper.cos(r1 / 180.0f * 3.1415927f) * MathHelper.cos(r2 / 180.0f * 3.1415927f);
        float mY = -MathHelper.sin(r2 / 180.0f * 3.1415927f);
        mX *= radius;
        mY *= radius;
        mZ *= radius;
        final float var8 = particle % 16 / 64.0f;
        final float var9 = var8 + 0.015625f;
        final float var10 = particle / 16 / 64.0f;
        final float var11 = var10 + 0.015625f;
        final float var12 = 0.15f * particleScale;
        final float var13 = (float)(posX + mX - FXSmokeSpiral.interpPosX);
        final float var14 = (float)(Math.max(posY + mY, miny + 0.1f) - FXSmokeSpiral.interpPosY);
        final float var15 = (float)(posZ + mZ - FXSmokeSpiral.interpPosZ);
        final float var16 = 1.0f;
        final int i = getBrightnessForRender(f);
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        wr.pos(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12).tex(var9, var11).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 0.66f * particleAlpha).lightmap(j, k).endVertex();
        wr.pos(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12).tex(var9, var10).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 0.66f * particleAlpha).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12).tex(var8, var10).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 0.66f * particleAlpha).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12).tex(var8, var11).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 0.66f * particleAlpha).lightmap(j, k).endVertex();
    }
    
    public int getFXLayer() {
        return 1;
    }
    
    public void onUpdate() {
        setAlphaF((particleMaxAge - particleAge) / (float) particleMaxAge);
        if (particleAge++ >= particleMaxAge) {
            setExpired();
        }
    }
}
