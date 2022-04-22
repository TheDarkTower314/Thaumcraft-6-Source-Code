// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.particles;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.particle.ParticleBreaking;

@SideOnly(Side.CLIENT)
public class FXBreakingFade extends ParticleBreaking
{
    public FXBreakingFade(final World worldIn, final double p_i1197_2_, final double p_i1197_4_, final double p_i1197_6_, final double p_i1197_8_, final double p_i1197_10_, final double p_i1197_12_, final Item p_i1197_14_, final int p_i1197_15_) {
        super(worldIn, p_i1197_2_, p_i1197_4_, p_i1197_6_, p_i1197_8_, p_i1197_10_, p_i1197_12_, p_i1197_14_, p_i1197_15_);
    }
    
    public FXBreakingFade(final World worldIn, final double p_i1196_2_, final double p_i1196_4_, final double p_i1196_6_, final Item p_i1196_8_, final int p_i1196_9_) {
        super(worldIn, p_i1196_2_, p_i1196_4_, p_i1196_6_, p_i1196_8_, p_i1196_9_);
    }
    
    public FXBreakingFade(final World worldIn, final double p_i1195_2_, final double p_i1195_4_, final double p_i1195_6_, final Item p_i1195_8_) {
        super(worldIn, p_i1195_2_, p_i1195_4_, p_i1195_6_, p_i1195_8_);
    }
    
    public void setParticleMaxAge(final int particleMaxAge) {
        this.particleMaxAge = particleMaxAge;
    }
    
    public void setParticleGravity(final float f) {
        particleGravity = f;
    }
    
    public int getFXLayer() {
        return 1;
    }
    
    public void setSpeed(final double x, final double y, final double z) {
        motionX = x;
        motionY = y;
        motionZ = z;
    }
    
    public void renderParticle(final BufferBuilder p_180434_1_, final Entity p_180434_2_, final float p_180434_3_, final float p_180434_4_, final float p_180434_5_, final float p_180434_6_, final float p_180434_7_, final float p_180434_8_) {
        GlStateManager.depthMask(false);
        float f6 = (particleTextureIndexX + particleTextureJitterX / 4.0f) / 16.0f;
        float f7 = f6 + 0.015609375f;
        float f8 = (particleTextureIndexY + particleTextureJitterY / 4.0f) / 16.0f;
        float f9 = f8 + 0.015609375f;
        final float f10 = 0.1f * particleScale;
        final float fade = 1.0f - particleAge / (float) particleMaxAge;
        if (particleTexture != null) {
            f6 = particleTexture.getInterpolatedU(particleTextureJitterX / 4.0f * 16.0f);
            f7 = particleTexture.getInterpolatedU((particleTextureJitterX + 1.0f) / 4.0f * 16.0f);
            f8 = particleTexture.getInterpolatedV(particleTextureJitterY / 4.0f * 16.0f);
            f9 = particleTexture.getInterpolatedV((particleTextureJitterY + 1.0f) / 4.0f * 16.0f);
        }
        final int i = getBrightnessForRender(p_180434_3_);
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        final float f11 = (float)(prevPosX + (posX - prevPosX) * p_180434_3_ - FXBreakingFade.interpPosX);
        final float f12 = (float)(prevPosY + (posY - prevPosY) * p_180434_3_ - FXBreakingFade.interpPosY);
        final float f13 = (float)(prevPosZ + (posZ - prevPosZ) * p_180434_3_ - FXBreakingFade.interpPosZ);
        p_180434_1_.pos(f11 - p_180434_4_ * f10 - p_180434_7_ * f10, f12 - p_180434_5_ * f10, f13 - p_180434_6_ * f10 - p_180434_8_ * f10).tex(f6, f9).color(particleRed, particleGreen, particleBlue, particleAlpha * fade).lightmap(j, k).endVertex();
        p_180434_1_.pos(f11 - p_180434_4_ * f10 + p_180434_7_ * f10, f12 + p_180434_5_ * f10, f13 - p_180434_6_ * f10 + p_180434_8_ * f10).tex(f6, f8).color(particleRed, particleGreen, particleBlue, particleAlpha * fade).lightmap(j, k).endVertex();
        p_180434_1_.pos(f11 + p_180434_4_ * f10 + p_180434_7_ * f10, f12 + p_180434_5_ * f10, f13 + p_180434_6_ * f10 + p_180434_8_ * f10).tex(f7, f8).color(particleRed, particleGreen, particleBlue, particleAlpha * fade).lightmap(j, k).endVertex();
        p_180434_1_.pos(f11 + p_180434_4_ * f10 - p_180434_7_ * f10, f12 - p_180434_5_ * f10, f13 + p_180434_6_ * f10 - p_180434_8_ * f10).tex(f7, f9).color(particleRed, particleGreen, particleBlue, particleAlpha * fade).lightmap(j, k).endVertex();
        GlStateManager.depthMask(true);
    }
    
    public void boom() {
        final float f = (float)(Math.random() + Math.random() + 1.0) * 0.15f;
        final float f2 = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX = motionX / f2 * f * 0.9640000000596046;
        motionY = motionY / f2 * f * 0.9640000000596046 + 0.10000000149011612;
        motionZ = motionZ / f2 * f * 0.9640000000596046;
    }
}
