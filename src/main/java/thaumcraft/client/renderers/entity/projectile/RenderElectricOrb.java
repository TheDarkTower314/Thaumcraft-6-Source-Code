// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.projectile;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.lib.UtilsFX;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.projectile.EntityGolemOrb;
import thaumcraft.client.fx.ParticleEngine;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import java.util.Random;
import net.minecraft.client.renderer.entity.Render;

public class RenderElectricOrb extends Render
{
    private Random random;
    
    public RenderElectricOrb(final RenderManager rm) {
        super(rm);
        this.random = new Random();
        this.shadowSize = 0.0f;
    }
    
    public void renderEntityAt(final Entity entity, final double x, final double y, final double z, final float fq, final float pticks) {
        final Tessellator tessellator = Tessellator.getInstance();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        GL11.glDepthMask(false);
        this.bindTexture(ParticleEngine.particleTexture);
        final float f2 = (1 + entity.ticksExisted % 6) / 32.0f;
        final float f3 = f2 + 0.03125f;
        float f4 = 0.21875f;
        if (entity instanceof EntityGolemOrb && ((EntityGolemOrb)entity).red) {
            f4 = 0.1875f;
        }
        final float f5 = f4 + 0.03125f;
        final float f6 = 1.0f;
        final float f7 = 0.5f;
        final float f8 = 0.5f;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.8f);
        GL11.glRotatef(180.0f - this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
        final float bob = MathHelper.sin(entity.ticksExisted / 5.0f) * 0.2f + 0.2f;
        GL11.glScalef(1.0f + bob, 1.0f + bob, 1.0f + bob);
        tessellator.getBuffer().begin(7, UtilsFX.VERTEXFORMAT_POS_TEX_CO_LM_NO);
        final int i = 220;
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        tessellator.getBuffer().pos(-f7, -f8, 0.0).tex(f2, f5).color(1.0f, 1.0f, 1.0f, 1.0f).lightmap(j, k).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(f6 - f7, -f8, 0.0).tex(f3, f5).color(1.0f, 1.0f, 1.0f, 1.0f).lightmap(j, k).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(f6 - f7, 1.0f - f8, 0.0).tex(f3, f4).color(1.0f, 1.0f, 1.0f, 1.0f).lightmap(j, k).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(-f7, 1.0f - f8, 0.0).tex(f2, f4).color(1.0f, 1.0f, 1.0f, 1.0f).lightmap(j, k).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
        GL11.glDisable(32826);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    public void doRender(final Entity entity, final double d, final double d1, final double d2, final float f, final float f1) {
        this.renderEntityAt(entity, d, d1, d2, f, f1);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
