// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.particles;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.World;
import net.minecraft.client.particle.Particle;

public class FXFireMote extends Particle
{
    float baseScale;
    float baseAlpha;
    int glowlayer;
    
    public FXFireMote(final World worldIn, final double x, final double y, final double z, final double vx, final double vy, final double vz, final float r, final float g, final float b, final float scale, final int layer) {
        super(worldIn, x, y, z, 0.0, 0.0, 0.0);
        baseScale = 0.0f;
        baseAlpha = 1.0f;
        glowlayer = 0;
        float colorR = r;
        float colorG = g;
        float colorB = b;
        if (colorR > 1.0) {
            colorR /= 255.0f;
        }
        if (colorG > 1.0) {
            colorG /= 255.0f;
        }
        if (colorB > 1.0) {
            colorB /= 255.0f;
        }
        glowlayer = layer;
        setRBGColorF(colorR, colorG, colorB);
        particleMaxAge = 16;
        particleScale = scale;
        baseScale = scale;
        motionX = vx;
        motionY = vy;
        motionZ = vz;
        particleAngle = 6.2831855f;
        setParticleTextureIndex(7);
    }
    
    public void setParticleTextureIndex(final int particleTextureIndex) {
        particleTextureIndexX = particleTextureIndex % 64;
        particleTextureIndexY = particleTextureIndex / 64;
    }
    
    public void renderParticle(final BufferBuilder worldRendererIn, final Entity entityIn, final float partialTicks, final float rotationX, final float rotationZ, final float rotationYZ, final float rotationXY, final float rotationXZ) {
        float f = particleTextureIndexX / 64.0f;
        float f2 = f + 0.015625f;
        float f3 = particleTextureIndexY / 64.0f;
        float f4 = f3 + 0.015625f;
        final float f5 = 0.1f * particleScale;
        if (particleTexture != null) {
            f = particleTexture.getMinU();
            f2 = particleTexture.getMaxU();
            f3 = particleTexture.getMinV();
            f4 = particleTexture.getMaxV();
        }
        final float f6 = (float)(prevPosX + (posX - prevPosX) * partialTicks - FXFireMote.interpPosX);
        final float f7 = (float)(prevPosY + (posY - prevPosY) * partialTicks - FXFireMote.interpPosY);
        final float f8 = (float)(prevPosZ + (posZ - prevPosZ) * partialTicks - FXFireMote.interpPosZ);
        final int i = getBrightnessForRender(partialTicks);
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        final Vec3d[] avec3d = { new Vec3d(-rotationX * f5 - rotationXY * f5, -rotationZ * f5, -rotationYZ * f5 - rotationXZ * f5), new Vec3d(-rotationX * f5 + rotationXY * f5, rotationZ * f5, -rotationYZ * f5 + rotationXZ * f5), new Vec3d(rotationX * f5 + rotationXY * f5, rotationZ * f5, rotationYZ * f5 + rotationXZ * f5), new Vec3d(rotationX * f5 - rotationXY * f5, -rotationZ * f5, rotationYZ * f5 - rotationXZ * f5) };
        if (particleAngle != 0.0f) {
            final float f9 = particleAngle + (particleAngle - prevParticleAngle) * partialTicks;
            final float f10 = MathHelper.cos(f9 * 0.5f);
            final float f11 = MathHelper.sin(f9 * 0.5f) * (float)FXFireMote.cameraViewDir.x;
            final float f12 = MathHelper.sin(f9 * 0.5f) * (float)FXFireMote.cameraViewDir.y;
            final float f13 = MathHelper.sin(f9 * 0.5f) * (float)FXFireMote.cameraViewDir.z;
            final Vec3d vec3d = new Vec3d(f11, f12, f13);
            for (int l = 0; l < 4; ++l) {
                avec3d[l] = vec3d.scale(2.0 * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale(f10 * f10 - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale(2.0f * f10));
            }
        }
        worldRendererIn.pos(f6 + avec3d[0].x, f7 + avec3d[0].y, f8 + avec3d[0].z).tex(f2, f4).color(particleRed, particleGreen, particleBlue, particleAlpha * baseAlpha).lightmap(j, k).endVertex();
        worldRendererIn.pos(f6 + avec3d[1].x, f7 + avec3d[1].y, f8 + avec3d[1].z).tex(f2, f3).color(particleRed, particleGreen, particleBlue, particleAlpha * baseAlpha).lightmap(j, k).endVertex();
        worldRendererIn.pos(f6 + avec3d[2].x, f7 + avec3d[2].y, f8 + avec3d[2].z).tex(f, f3).color(particleRed, particleGreen, particleBlue, particleAlpha * baseAlpha).lightmap(j, k).endVertex();
        worldRendererIn.pos(f6 + avec3d[3].x, f7 + avec3d[3].y, f8 + avec3d[3].z).tex(f, f4).color(particleRed, particleGreen, particleBlue, particleAlpha * baseAlpha).lightmap(j, k).endVertex();
    }
    
    public int getBrightnessForRender(final float pTicks) {
        return 255;
    }
    
    public int getFXLayer() {
        return glowlayer;
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (world.rand.nextInt(6) == 0) {
            ++particleAge;
        }
        if (particleAge >= particleMaxAge) {
            setExpired();
        }
        final float lifespan = particleAge / (float) particleMaxAge;
        particleScale = baseScale - baseScale * lifespan;
        baseAlpha = 1.0f - lifespan;
        prevParticleAngle = particleAngle;
        ++particleAngle;
    }
}
