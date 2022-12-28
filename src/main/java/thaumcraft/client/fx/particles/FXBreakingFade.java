package thaumcraft.client.fx.particles;
import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class FXBreakingFade extends ParticleBreaking
{
    public FXBreakingFade(World worldIn, double p_i1197_2_, double p_i1197_4_, double p_i1197_6_, double p_i1197_8_, double p_i1197_10_, double p_i1197_12_, Item p_i1197_14_, int p_i1197_15_) {
        super(worldIn, p_i1197_2_, p_i1197_4_, p_i1197_6_, p_i1197_8_, p_i1197_10_, p_i1197_12_, p_i1197_14_, p_i1197_15_);
    }
    
    public FXBreakingFade(World worldIn, double p_i1196_2_, double p_i1196_4_, double p_i1196_6_, Item p_i1196_8_, int p_i1196_9_) {
        super(worldIn, p_i1196_2_, p_i1196_4_, p_i1196_6_, p_i1196_8_, p_i1196_9_);
    }
    
    public FXBreakingFade(World worldIn, double p_i1195_2_, double p_i1195_4_, double p_i1195_6_, Item p_i1195_8_) {
        super(worldIn, p_i1195_2_, p_i1195_4_, p_i1195_6_, p_i1195_8_);
    }
    
    public void setParticleMaxAge(int particleMaxAge) {
        this.particleMaxAge = particleMaxAge;
    }
    
    public void setParticleGravity(float f) {
        particleGravity = f;
    }
    
    public int getFXLayer() {
        return 1;
    }
    
    public void setSpeed(double x, double y, double z) {
        motionX = x;
        motionY = y;
        motionZ = z;
    }
    
    public void renderParticle(BufferBuilder p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
        GlStateManager.depthMask(false);
        float f6 = (particleTextureIndexX + particleTextureJitterX / 4.0f) / 16.0f;
        float f7 = f6 + 0.015609375f;
        float f8 = (particleTextureIndexY + particleTextureJitterY / 4.0f) / 16.0f;
        float f9 = f8 + 0.015609375f;
        float f10 = 0.1f * particleScale;
        float fade = 1.0f - particleAge / (float) particleMaxAge;
        if (particleTexture != null) {
            f6 = particleTexture.getInterpolatedU(particleTextureJitterX / 4.0f * 16.0f);
            f7 = particleTexture.getInterpolatedU((particleTextureJitterX + 1.0f) / 4.0f * 16.0f);
            f8 = particleTexture.getInterpolatedV(particleTextureJitterY / 4.0f * 16.0f);
            f9 = particleTexture.getInterpolatedV((particleTextureJitterY + 1.0f) / 4.0f * 16.0f);
        }
        int i = getBrightnessForRender(p_180434_3_);
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        float f11 = (float)(prevPosX + (posX - prevPosX) * p_180434_3_ - FXBreakingFade.interpPosX);
        float f12 = (float)(prevPosY + (posY - prevPosY) * p_180434_3_ - FXBreakingFade.interpPosY);
        float f13 = (float)(prevPosZ + (posZ - prevPosZ) * p_180434_3_ - FXBreakingFade.interpPosZ);
        p_180434_1_.pos(f11 - p_180434_4_ * f10 - p_180434_7_ * f10, f12 - p_180434_5_ * f10, f13 - p_180434_6_ * f10 - p_180434_8_ * f10).tex(f6, f9).color(particleRed, particleGreen, particleBlue, particleAlpha * fade).lightmap(j, k).endVertex();
        p_180434_1_.pos(f11 - p_180434_4_ * f10 + p_180434_7_ * f10, f12 + p_180434_5_ * f10, f13 - p_180434_6_ * f10 + p_180434_8_ * f10).tex(f6, f8).color(particleRed, particleGreen, particleBlue, particleAlpha * fade).lightmap(j, k).endVertex();
        p_180434_1_.pos(f11 + p_180434_4_ * f10 + p_180434_7_ * f10, f12 + p_180434_5_ * f10, f13 + p_180434_6_ * f10 + p_180434_8_ * f10).tex(f7, f8).color(particleRed, particleGreen, particleBlue, particleAlpha * fade).lightmap(j, k).endVertex();
        p_180434_1_.pos(f11 + p_180434_4_ * f10 - p_180434_7_ * f10, f12 - p_180434_5_ * f10, f13 + p_180434_6_ * f10 - p_180434_8_ * f10).tex(f7, f9).color(particleRed, particleGreen, particleBlue, particleAlpha * fade).lightmap(j, k).endVertex();
        GlStateManager.depthMask(true);
    }
    
    public void boom() {
        float f = (float)(Math.random() + Math.random() + 1.0) * 0.15f;
        float f2 = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX = motionX / f2 * f * 0.9640000000596046;
        motionY = motionY / f2 * f * 0.9640000000596046 + 0.10000000149011612;
        motionZ = motionZ / f2 * f * 0.9640000000596046;
    }
}
