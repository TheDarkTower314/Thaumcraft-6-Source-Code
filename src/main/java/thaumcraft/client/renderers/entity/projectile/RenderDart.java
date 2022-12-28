package thaumcraft.client.renderers.entity.projectile;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.fx.ParticleEngine;


@SideOnly(Side.CLIENT)
public class RenderDart extends Render
{
    private static ResourceLocation arrowTextures;
    int size1;
    int size2;
    
    public RenderDart(RenderManager renderManager) {
        super(renderManager);
        size1 = 0;
        size2 = 0;
    }
    
    public void renderArrow(EntityArrow arrow, double x, double y, double z, float ns, float prt) {
        bindEntityTexture(arrow);
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(arrow.prevRotationYaw + (arrow.rotationYaw - arrow.prevRotationYaw) * prt - 90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(arrow.prevRotationPitch + (arrow.rotationPitch - arrow.prevRotationPitch) * prt, 0.0f, 0.0f, 1.0f);
        Tessellator tessellator = Tessellator.getInstance();
        byte b0 = 0;
        float f2 = 0.0f;
        float f3 = 0.5f;
        float f4 = (0 + b0 * 10) / 32.0f;
        float f5 = (5 + b0 * 10) / 32.0f;
        float f6 = 0.0f;
        float f7 = 0.15625f;
        float f8 = (5 + b0 * 10) / 32.0f;
        float f9 = (10 + b0 * 10) / 32.0f;
        float f10 = 0.033f;
        GL11.glEnable(32826);
        float f11 = arrow.arrowShake - prt;
        if (f11 > 0.0f) {
            float f12 = -MathHelper.sin(f11 * 3.0f) * f11;
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
        bindTexture(ParticleEngine.particleTexture);
        GL11.glPopMatrix();
    }
    
    protected ResourceLocation getArrowTextures(EntityArrow par1EntityArrow) {
        return RenderDart.arrowTextures;
    }
    
    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return getArrowTextures((EntityArrow)par1Entity);
    }
    
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        renderArrow((EntityArrow)par1Entity, par2, par4, par6, par8, par9);
    }
    
    static {
        arrowTextures = new ResourceLocation("textures/entity/arrow.png");
    }
}
