// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.particles;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.client.particle.Particle;

public class FXBoreSparkle extends Particle
{
    private Entity target;
    private double targetX;
    private double targetY;
    private double targetZ;
    public int particle;
    
    public FXBoreSparkle(final World par1World, final double par2, final double par4, final double par6, final double tx, final double ty, final double tz) {
        super(par1World, par2, par4, par6, 0.0, 0.0, 0.0);
        this.particle = 24;
        final float particleRed = 0.6f;
        this.particleBlue = particleRed;
        this.particleGreen = particleRed;
        this.particleRed = particleRed;
        this.particleScale = this.rand.nextFloat() * 0.5f + 0.5f;
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
        final double dx = tx - this.posX;
        final double dy = ty - this.posY;
        final double dz = tz - this.posZ;
        int base = (int)(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 10.0f);
        if (base < 1) {
            base = 1;
        }
        this.particleMaxAge = base / 2 + this.rand.nextInt(base);
        final float f3 = 0.01f;
        this.motionX = (float)this.rand.nextGaussian() * f3;
        this.motionY = (float)this.rand.nextGaussian() * f3;
        this.motionZ = (float)this.rand.nextGaussian() * f3;
        this.particleRed = 0.2f;
        this.particleGreen = 0.6f + this.rand.nextFloat() * 0.3f;
        this.particleBlue = 0.2f;
        this.particleGravity = 0.2f;
        final Entity renderentity = FMLClientHandler.instance().getClient().getRenderViewEntity();
        int visibleDistance = 64;
        if (!FMLClientHandler.instance().getClient().gameSettings.fancyGraphics) {
            visibleDistance = 32;
        }
        if (renderentity.getDistance(this.posX, this.posY, this.posZ) > visibleDistance) {
            this.particleMaxAge = 0;
        }
    }
    
    public FXBoreSparkle(final World par1World, final double par2, final double par4, final double par6, final Entity t) {
        this(par1World, par2, par4, par6, t.posX, t.posY + t.getEyeHeight(), t.posZ);
        this.target = t;
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        final float bob = MathHelper.sin(this.particleAge / 3.0f) * 0.5f + 1.0f;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.75f);
        final int part = this.particleAge % 4;
        final float var8 = part / 64.0f;
        final float var9 = var8 + 0.015625f;
        final float var10 = 0.0625f;
        final float var11 = var10 + 0.015625f;
        final float var12 = 0.1f * this.particleScale * bob;
        final float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * f - FXBoreSparkle.interpPosX);
        final float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * f - FXBoreSparkle.interpPosY);
        final float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * f - FXBoreSparkle.interpPosZ);
        final float var16 = 1.0f;
        final int i = 240;
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        wr.pos(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12).tex(var9, var11).color(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16, 1.0f).lightmap(j, k).endVertex();
        wr.pos(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12).tex(var9, var10).color(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16, 1.0f).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12).tex(var8, var10).color(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16, 1.0f).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12).tex(var8, var11).color(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16, 1.0f).lightmap(j, k).endVertex();
    }
    
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.target != null) {
            this.targetX = this.target.posX;
            this.targetY = this.target.posY + this.target.getEyeHeight();
            this.targetZ = this.target.posZ;
        }
        if (this.particleAge++ >= this.particleMaxAge || (MathHelper.floor(this.posX) == MathHelper.floor(this.targetX) && MathHelper.floor(this.posY) == MathHelper.floor(this.targetY) && MathHelper.floor(this.posZ) == MathHelper.floor(this.targetZ))) {
            this.setExpired();
            return;
        }
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.985;
        this.motionY *= 0.95;
        this.motionZ *= 0.985;
        double dx = this.targetX - this.posX;
        double dy = this.targetY - this.posY;
        double dz = this.targetZ - this.posZ;
        final double d11 = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        final double clamp = Math.min(0.25, d11 / 15.0);
        if (d11 < 2.0) {
            this.particleScale *= 0.9f;
        }
        dx /= d11;
        dy /= d11;
        dz /= d11;
        this.motionX += dx * clamp;
        this.motionY += dy * clamp;
        this.motionZ += dz * clamp;
        this.motionX = MathHelper.clamp((float)this.motionX, -clamp, clamp);
        this.motionY = MathHelper.clamp((float)this.motionY, -clamp, clamp);
        this.motionZ = MathHelper.clamp((float)this.motionZ, -clamp, clamp);
        this.motionX += this.rand.nextGaussian() * 0.01;
        this.motionY += this.rand.nextGaussian() * 0.01;
        this.motionZ += this.rand.nextGaussian() * 0.01;
    }
    
    public void setGravity(final float value) {
        this.particleGravity = value;
    }
}
