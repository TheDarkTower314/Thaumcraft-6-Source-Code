// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.particles;

import org.lwjgl.opengl.GL11;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.client.particle.Particle;

public class FXWispEG extends Particle
{
    Entity target;
    double rx;
    double ry;
    double rz;
    public int blendmode;
    
    public FXWispEG(final World world, final double posX, final double posY, final double posZ, final Entity target2) {
        super(world, posX, posY, posZ, 0.0, 0.0, 0.0);
        this.target = null;
        this.rx = 0.0;
        this.ry = 0.0;
        this.rz = 0.0;
        this.blendmode = 1;
        this.target = target2;
        this.motionX = this.rand.nextGaussian() * 0.03;
        this.motionY = -0.05;
        this.motionZ = this.rand.nextGaussian() * 0.03;
        this.particleScale *= 0.4f;
        this.particleMaxAge = (int)(40.0 / (Math.random() * 0.3 + 0.7));
        this.setSize(0.01f, 0.01f);
        this.prevPosX = posX;
        this.prevPosY = posY;
        this.prevPosZ = posZ;
        this.blendmode = 771;
        this.particleRed = this.rand.nextFloat() * 0.05f;
        this.particleGreen = this.rand.nextFloat() * 0.05f;
        this.particleBlue = this.rand.nextFloat() * 0.05f;
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        final Entity e = Minecraft.getMinecraft().getRenderViewEntity();
        final float agescale = 1.0f - this.particleAge / (float)this.particleMaxAge;
        final float d6 = 1024.0f;
        final double dist = new Vec3d(e.posX, e.posY, e.posZ).squareDistanceTo(new Vec3d(this.posX, this.posY, this.posZ));
        final float base = (float)(1.0 - Math.min(d6, dist) / d6);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.75f * base);
        final float f6 = 0.5f * this.particleScale;
        final float f7 = (float)(this.prevPosX + (this.posX - this.prevPosX) * f - FXWispEG.interpPosX);
        final float f8 = (float)(this.prevPosY + (this.posY - this.prevPosY) * f - FXWispEG.interpPosY);
        final float f9 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * f - FXWispEG.interpPosZ);
        final float var8 = this.particleAge % 13 / 64.0f;
        final float var9 = var8 + 0.015625f;
        final float var10 = 0.046875f;
        final float var11 = var10 + 0.015625f;
        final int i = 240;
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        wr.pos(f7 - f1 * f6 - f4 * f6, f8 - f2 * f6, f9 - f3 * f6 - f5 * f6).tex(var9, var11).color(this.particleRed, this.particleGreen, this.particleBlue, 0.2f * agescale * base).lightmap(j, k).endVertex();
        wr.pos(f7 - f1 * f6 + f4 * f6, f8 + f2 * f6, f9 - f3 * f6 + f5 * f6).tex(var9, var10).color(this.particleRed, this.particleGreen, this.particleBlue, 0.2f * agescale * base).lightmap(j, k).endVertex();
        wr.pos(f7 + f1 * f6 + f4 * f6, f8 + f2 * f6, f9 + f3 * f6 + f5 * f6).tex(var8, var10).color(this.particleRed, this.particleGreen, this.particleBlue, 0.2f * agescale * base).lightmap(j, k).endVertex();
        wr.pos(f7 + f1 * f6 - f4 * f6, f8 - f2 * f6, f9 + f3 * f6 - f5 * f6).tex(var8, var11).color(this.particleRed, this.particleGreen, this.particleBlue, 0.2f * agescale * base).lightmap(j, k).endVertex();
    }
    
    public int getFXLayer() {
        return (this.blendmode != 1) ? 1 : 0;
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (this.target != null && !this.onGround) {
            this.posX += this.target.motionX;
            this.posZ += this.target.motionZ;
        }
    }
}
