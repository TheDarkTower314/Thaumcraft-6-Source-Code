// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.particles;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraft.util.math.MathHelper;
import java.awt.Color;
import net.minecraft.world.World;
import net.minecraft.client.particle.Particle;

public class FXVent2 extends Particle
{
    float grav;
    float psm;
    
    public FXVent2(final World par1World, final double par2, final double par4, final double par6, final double par8, final double par10, final double par12, final int color) {
        super(par1World, par2, par4, par6, par8, par10, par12);
        this.grav = 0.0f;
        this.psm = 1.0f;
        this.setSize(0.02f, 0.02f);
        this.particleScale = this.rand.nextFloat() * 0.1f + 0.05f;
        this.motionX = par8;
        this.motionY = par10;
        this.motionZ = par12;
        final Color c = new Color(color);
        this.particleRed = (float)MathHelper.clamp(c.getRed() / 255.0f + this.rand.nextGaussian() * 0.05, 0.0, 1.0);
        this.particleBlue = (float)MathHelper.clamp(c.getBlue() / 255.0f + this.rand.nextGaussian() * 0.05, 0.0, 1.0);
        this.particleGreen = (float)MathHelper.clamp(c.getGreen() / 255.0f + this.rand.nextGaussian() * 0.05, 0.0, 1.0);
        final Entity renderentity = FMLClientHandler.instance().getClient().getRenderViewEntity();
        int visibleDistance = 50;
        if (!FMLClientHandler.instance().getClient().gameSettings.fancyGraphics) {
            visibleDistance = 25;
        }
        if (renderentity.getDistance(this.posX, this.posY, this.posZ) > visibleDistance) {
            this.particleMaxAge = 0;
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.grav = (float)(this.rand.nextGaussian() * 0.0075);
    }
    
    public void setScale(final float f) {
        this.particleScale *= f;
        this.psm *= f;
    }
    
    public void setHeading(double par1, double par3, double par5, final float par7, final float par8) {
        final float f2 = MathHelper.sqrt(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= f2;
        par3 /= f2;
        par5 /= f2;
        par1 += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * par8;
        par3 += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * par8;
        par5 += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * par8;
        par1 *= par7;
        par3 *= par7;
        par5 *= par7;
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;
    }
    
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        ++this.particleAge;
        if (this.particleScale >= this.psm) {
            this.setExpired();
        }
        this.motionY += this.grav;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.8500000190734863;
        this.motionY *= 0.8500000190734863;
        this.motionZ *= 0.8500000190734863;
        if (this.particleScale < this.psm) {
            this.particleScale *= (float)1.2;
        }
        if (this.particleScale > this.psm) {
            this.particleScale = this.psm;
        }
        if (this.onGround) {
            this.motionX *= 0.699999988079071;
            this.motionZ *= 0.699999988079071;
        }
    }
    
    public void setRGB(final float r, final float g, final float b) {
        this.particleRed = r;
        this.particleGreen = g;
        this.particleBlue = b;
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.33f);
        final int part = (int)(1.0f + this.particleScale / this.psm * 4.0f);
        final float var8 = part % 16 / 64.0f;
        final float var9 = var8 + 0.015625f;
        final float var10 = part / 64 / 64.0f;
        final float var11 = var10 + 0.015625f;
        final float var12 = 0.3f * this.particleScale;
        final float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * f - FXVent2.interpPosX);
        final float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * f - FXVent2.interpPosY);
        final float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * f - FXVent2.interpPosZ);
        final float var16 = 1.0f;
        final int i = this.getBrightnessForRender(f);
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        final float alpha = this.particleAlpha * ((this.psm - this.particleScale) / this.psm);
        wr.pos(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12).tex(var9, var11).color(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16, alpha).lightmap(j, k).endVertex();
        wr.pos(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12).tex(var9, var10).color(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16, alpha).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12).tex(var8, var10).color(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16, alpha).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12).tex(var8, var11).color(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16, alpha).lightmap(j, k).endVertex();
    }
    
    public int getFXLayer() {
        return 1;
    }
}
