package thaumcraft.client.fx.particles;
import java.awt.Color;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;


public class FXVent extends Particle
{
    float psm;
    
    public FXVent(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, int color) {
        super(par1World, par2, par4, par6, par8, par10, par12);
        psm = 1.0f;
        setSize(0.02f, 0.02f);
        particleScale = rand.nextFloat() * 0.1f + 0.05f;
        motionX = par8;
        motionY = par10;
        motionZ = par12;
        Color c = new Color(color);
        particleRed = c.getRed() / 255.0f;
        particleBlue = c.getBlue() / 255.0f;
        particleGreen = c.getGreen() / 255.0f;
        setHeading(motionX, motionY, motionZ, 0.125f, 5.0f);
        Entity renderentity = FMLClientHandler.instance().getClient().getRenderViewEntity();
        int visibleDistance = 50;
        if (!FMLClientHandler.instance().getClient().gameSettings.fancyGraphics) {
            visibleDistance = 25;
        }
        if (renderentity.getDistance(posX, posY, posZ) > visibleDistance) {
            particleMaxAge = 0;
        }
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
    }
    
    public void setScale(float f) {
        particleScale *= f;
        psm *= f;
    }
    
    public void setHeading(double par1, double par3, double par5, float par7, float par8) {
        float f2 = MathHelper.sqrt(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= f2;
        par3 /= f2;
        par5 /= f2;
        par1 += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * par8;
        par3 += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * par8;
        par5 += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * par8;
        par1 *= par7;
        par3 *= par7;
        par5 *= par7;
        motionX = par1;
        motionY = par3;
        motionZ = par5;
    }
    
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        ++particleAge;
        if (particleScale >= psm) {
            setExpired();
        }
        motionY += 0.0025;
        move(motionX, motionY, motionZ);
        motionX *= 0.8500000190734863;
        motionY *= 0.8500000190734863;
        motionZ *= 0.8500000190734863;
        if (particleScale < psm) {
            particleScale *= (float)1.15;
        }
        if (particleScale > psm) {
            particleScale = psm;
        }
        if (onGround) {
            motionX *= 0.699999988079071;
            motionZ *= 0.699999988079071;
        }
    }
    
    public void setRGB(float r, float g, float b) {
        particleRed = r;
        particleGreen = g;
        particleBlue = b;
    }
    
    public void renderParticle(BufferBuilder wr, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.33f);
        int part = (int)(1.0f + particleScale / psm * 4.0f);
        float var8 = part % 16 / 64.0f;
        float var9 = var8 + 0.015625f;
        float var10 = part / 64 / 64.0f;
        float var11 = var10 + 0.015625f;
        float var12 = 0.3f * particleScale;
        float var13 = (float)(prevPosX + (posX - prevPosX) * f - FXVent.interpPosX);
        float var14 = (float)(prevPosY + (posY - prevPosY) * f - FXVent.interpPosY);
        float var15 = (float)(prevPosZ + (posZ - prevPosZ) * f - FXVent.interpPosZ);
        float var16 = 1.0f;
        int i = getBrightnessForRender(f);
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        float alpha = particleAlpha * ((psm - particleScale) / psm);
        wr.pos(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12).tex(var9, var11).color(particleRed * var16, particleGreen * var16, particleBlue * var16, alpha).lightmap(j, k).endVertex();
        wr.pos(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12).tex(var9, var10).color(particleRed * var16, particleGreen * var16, particleBlue * var16, alpha).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12).tex(var8, var10).color(particleRed * var16, particleGreen * var16, particleBlue * var16, alpha).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12).tex(var8, var11).color(particleRed * var16, particleGreen * var16, particleBlue * var16, alpha).lightmap(j, k).endVertex();
    }
    
    public int getFXLayer() {
        return 1;
    }
}
