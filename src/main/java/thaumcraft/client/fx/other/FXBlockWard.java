// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.other;

import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.particle.Particle;

public class FXBlockWard extends Particle
{
    ResourceLocation[] tex1;
    EnumFacing side;
    int rotation;
    float sx;
    float sy;
    float sz;
    
    public FXBlockWard(final World world, final double d, final double d1, final double d2, final EnumFacing side, final float f, final float f1, final float f2) {
        super(world, d, d1, d2, 0.0, 0.0, 0.0);
        this.tex1 = new ResourceLocation[15];
        this.rotation = 0;
        this.sx = 0.0f;
        this.sy = 0.0f;
        this.sz = 0.0f;
        this.side = side;
        this.particleGravity = 0.0f;
        final double motionX = 0.0;
        this.motionZ = motionX;
        this.motionY = motionX;
        this.motionX = motionX;
        this.particleMaxAge = 12 + this.rand.nextInt(5);
        this.setSize(0.01f, 0.01f);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.particleScale = (float)(1.4 + this.rand.nextGaussian() * 0.30000001192092896);
        this.rotation = this.rand.nextInt(360);
        this.sx = MathHelper.clamp(f - 0.6f + this.rand.nextFloat() * 0.2f, -0.4f, 0.4f);
        this.sy = MathHelper.clamp(f1 - 0.6f + this.rand.nextFloat() * 0.2f, -0.4f, 0.4f);
        this.sz = MathHelper.clamp(f2 - 0.6f + this.rand.nextFloat() * 0.2f, -0.4f, 0.4f);
        if (side.getFrontOffsetX() != 0) {
            this.sx = 0.0f;
        }
        if (side.getFrontOffsetY() != 0) {
            this.sy = 0.0f;
        }
        if (side.getFrontOffsetZ() != 0) {
            this.sz = 0.0f;
        }
        for (int a = 0; a < 15; ++a) {
            this.tex1[a] = new ResourceLocation("thaumcraft", "textures/models/hemis" + (a + 1) + ".png");
        }
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity p_180434_2_, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        final float fade = (this.particleAge + f) / this.particleMaxAge;
        final int frame = Math.min(15, (int)(15.0f * fade));
        Minecraft.getMinecraft().renderEngine.bindTexture(this.tex1[frame - 1]);
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, this.particleAlpha / 2.0f);
        final float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * f - FXBlockWard.interpPosX);
        final float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * f - FXBlockWard.interpPosY);
        final float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * f - FXBlockWard.interpPosZ);
        GL11.glTranslated(var13 + this.sx, var14 + this.sy, var15 + this.sz);
        GL11.glRotatef(90.0f, (float)this.side.getFrontOffsetY(), (float)(-this.side.getFrontOffsetX()), (float)this.side.getFrontOffsetZ());
        GL11.glRotatef((float)this.rotation, 0.0f, 0.0f, 1.0f);
        if (this.side.getFrontOffsetZ() > 0) {
            GL11.glTranslated(0.0, 0.0, 0.5049999952316284);
            GL11.glRotatef(180.0f, 0.0f, -1.0f, 0.0f);
        }
        else {
            GL11.glTranslated(0.0, 0.0, -0.5049999952316284);
        }
        final float var16 = this.particleScale;
        final float var17 = 1.0f;
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        final int i = 240;
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        wr.pos(-0.5 * var16, 0.5 * var16, 0.0).tex(0.0, 1.0).color(this.particleRed * var17, this.particleGreen * var17, this.particleBlue * var17, this.particleAlpha / 2.0f).lightmap(j, k).endVertex();
        wr.pos(0.5 * var16, 0.5 * var16, 0.0).tex(1.0, 1.0).color(this.particleRed * var17, this.particleGreen * var17, this.particleBlue * var17, this.particleAlpha / 2.0f).lightmap(j, k).endVertex();
        wr.pos(0.5 * var16, -0.5 * var16, 0.0).tex(1.0, 0.0).color(this.particleRed * var17, this.particleGreen * var17, this.particleBlue * var17, this.particleAlpha / 2.0f).lightmap(j, k).endVertex();
        wr.pos(-0.5 * var16, -0.5 * var16, 0.0).tex(0.0, 0.0).color(this.particleRed * var17, this.particleGreen * var17, this.particleBlue * var17, this.particleAlpha / 2.0f).lightmap(j, k).endVertex();
        Tessellator.getInstance().draw();
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(ParticleManager.PARTICLE_TEXTURES);
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }
    
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        final float threshold = this.particleMaxAge / 5.0f;
        if (this.particleAge <= threshold) {
            this.particleAlpha = this.particleAge / threshold;
        }
        else {
            this.particleAlpha = (this.particleMaxAge - this.particleAge) / (float)this.particleMaxAge;
        }
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
        this.motionY -= 0.04 * this.particleGravity;
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
    }
    
    public void setGravity(final float value) {
        this.particleGravity = value;
    }
}
