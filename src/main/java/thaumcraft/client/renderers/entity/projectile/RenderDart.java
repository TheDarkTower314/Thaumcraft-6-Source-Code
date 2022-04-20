// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.projectile;

import thaumcraft.client.fx.ParticleEngine;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;

@SideOnly(Side.CLIENT)
public class RenderDart extends Render
{
    private static final ResourceLocation arrowTextures;
    int size1;
    int size2;
    
    public RenderDart(final RenderManager renderManager) {
        super(renderManager);
        this.size1 = 0;
        this.size2 = 0;
    }
    
    public void renderArrow(final EntityArrow arrow, final double x, final double y, final double z, final float ns, final float prt) {
        this.bindEntityTexture(arrow);
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(arrow.prevRotationYaw + (arrow.rotationYaw - arrow.prevRotationYaw) * prt - 90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(arrow.prevRotationPitch + (arrow.rotationPitch - arrow.prevRotationPitch) * prt, 0.0f, 0.0f, 1.0f);
        final Tessellator tessellator = Tessellator.getInstance();
        final byte b0 = 0;
        final float f2 = 0.0f;
        final float f3 = 0.5f;
        final float f4 = (0 + b0 * 10) / 32.0f;
        final float f5 = (5 + b0 * 10) / 32.0f;
        final float f6 = 0.0f;
        final float f7 = 0.15625f;
        final float f8 = (5 + b0 * 10) / 32.0f;
        final float f9 = (10 + b0 * 10) / 32.0f;
        final float f10 = 0.033f;
        GL11.glEnable(32826);
        final float f11 = arrow.arrowShake - prt;
        if (f11 > 0.0f) {
            final float f12 = -MathHelper.sin(f11 * 3.0f) * f11;
            GL11.glRotatef(f12, 0.0f, 0.0f, 1.0f);
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glRotatef(45.0f, 1.0f, 0.0f, 0.0f);
        GL11.glScalef(f10, f10, f10);
        GL11.glTranslatef(-4.0f, 0.0f, 0.0f);
        GL11.glNormal3f(f10, 0.0f, 0.0f);
        tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
        tessellator.getBuffer().pos(-7.0, -2.0, -2.0).tex(f6, f8).endVertex();
        tessellator.getBuffer().pos(-7.0, -2.0, 2.0).tex(f7, f8).endVertex();
        tessellator.getBuffer().pos(-7.0, 2.0, 2.0).tex(f7, f9).endVertex();
        tessellator.getBuffer().pos(-7.0, 2.0, -2.0).tex(f6, f9).endVertex();
        tessellator.draw();
        GL11.glNormal3f(-f10, 0.0f, 0.0f);
        tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
        tessellator.getBuffer().pos(-7.0, 2.0, -2.0).tex(f6, f8).endVertex();
        tessellator.getBuffer().pos(-7.0, 2.0, 2.0).tex(f7, f8).endVertex();
        tessellator.getBuffer().pos(-7.0, -2.0, 2.0).tex(f7, f9).endVertex();
        tessellator.getBuffer().pos(-7.0, -2.0, -2.0).tex(f6, f9).endVertex();
        tessellator.draw();
        for (int i = 0; i < 4; ++i) {
            GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            GL11.glNormal3f(0.0f, 0.0f, f10);
            tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
            tessellator.getBuffer().pos(-8.0, -2.0, 0.0).tex(f2, f4).endVertex();
            tessellator.getBuffer().pos(8.0, -2.0, 0.0).tex(f3, f4).endVertex();
            tessellator.getBuffer().pos(8.0, 2.0, 0.0).tex(f3, f5).endVertex();
            tessellator.getBuffer().pos(-8.0, 2.0, 0.0).tex(f2, f5).endVertex();
            tessellator.draw();
        }
        GL11.glDisable(32826);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.bindTexture(ParticleEngine.particleTexture);
        GL11.glPopMatrix();
    }
    
    protected ResourceLocation getArrowTextures(final EntityArrow par1EntityArrow) {
        return RenderDart.arrowTextures;
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return this.getArrowTextures((EntityArrow)par1Entity);
    }
    
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderArrow((EntityArrow)par1Entity, par2, par4, par6, par8, par9);
    }
    
    static {
        arrowTextures = new ResourceLocation("textures/entity/arrow.png");
    }
}
