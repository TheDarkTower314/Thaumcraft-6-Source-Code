package thaumcraft.client.renderers.entity.projectile;
import java.util.Random;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.fx.ParticleEngine;


public class RenderEldritchOrb extends Render
{
    private Random random;
    
    public RenderEldritchOrb(RenderManager renderManager) {
        super(renderManager);
        random = new Random();
        shadowSize = 0.0f;
    }
    
    public void renderEntityAt(Entity entity, double x, double y, double z, float fq, float pticks) {
        Tessellator tessellator = Tessellator.getInstance();
        random.setSeed(187L);
        GL11.glPushMatrix();
        RenderHelper.disableStandardItemLighting();
        float f1 = entity.ticksExisted / 80.0f;
        float f2 = 0.9f;
        float f3 = 0.0f;
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glDisable(3553);
        GL11.glShadeModel(7425);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        GL11.glDisable(3008);
        GL11.glEnable(2884);
        GL11.glDepthMask(false);
        GL11.glPushMatrix();
        for (int i = 0; i < 12; ++i) {
            GL11.glRotatef(random.nextFloat() * 360.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f + f1 * 360.0f, 0.0f, 0.0f, 1.0f);
            tessellator.getBuffer().begin(6, DefaultVertexFormats.POSITION_COLOR);
            float fa = random.nextFloat() * 20.0f + 5.0f + f3 * 10.0f;
            float f4 = random.nextFloat() * 2.0f + 1.0f + f3 * 2.0f;
            fa /= 30.0f / (Math.min(entity.ticksExisted, 10) / 10.0f);
            f4 /= 30.0f / (Math.min(entity.ticksExisted, 10) / 10.0f);
            tessellator.getBuffer().pos(0.0, 0.0, 0.0).color(1.0f, 1.0f, 1.0f, 1.0f - f3).endVertex();
            tessellator.getBuffer().pos(-0.866 * f4, fa, -0.5f * f4).color(64.0f, 64.0f, 64.0f, 255.0f * (1.0f - f3)).endVertex();
            tessellator.getBuffer().pos(0.866 * f4, fa, -0.5f * f4).color(64.0f, 64.0f, 64.0f, 255.0f * (1.0f - f3)).endVertex();
            tessellator.getBuffer().pos(0.0, fa, 1.0f * f4).color(64.0f, 64.0f, 64.0f, 255.0f * (1.0f - f3)).endVertex();
            tessellator.getBuffer().pos(-0.866 * f4, fa, -0.5f * f4).color(64.0f, 64.0f, 64.0f, 255.0f * (1.0f - f3)).endVertex();
            tessellator.draw();
        }
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glDisable(2884);
        GL11.glDisable(3042);
        GL11.glShadeModel(7424);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
        GL11.glEnable(3008);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(false);
        bindTexture(ParticleEngine.particleTexture);
        f3 = entity.ticksExisted % 13 / 64.0f;
        f2 = f3 + 0.015625f;
        float f5 = 0.046875f;
        float f6 = f5 + 0.015625f;
        float f7 = 1.0f;
        float f8 = 0.5f;
        float f9 = 0.5f;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glRotatef(180.0f - renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
        GL11.glScaled(0.75, 0.75, 0.75);
        tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        tessellator.getBuffer();
        tessellator.getBuffer().pos(0.0f - f8, 0.0f - f9, 0.0).tex(f3, f6).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(f7 - f8, 0.0f - f9, 0.0).tex(f2, f6).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(f7 - f8, 1.0f - f9, 0.0).tex(f2, f5).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(0.0f - f8, 1.0f - f9, 0.0).tex(f3, f5).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glDisable(32826);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
        renderEntityAt(entity, d, d1, d2, f, f1);
    }
    
    protected ResourceLocation getEntityTexture(Entity entity) {
        return ParticleEngine.particleTexture;
    }
}
