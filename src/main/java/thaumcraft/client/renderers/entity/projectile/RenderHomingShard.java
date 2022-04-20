// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.projectile;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import java.util.Iterator;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.Minecraft;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.fx.ParticleEngine;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import thaumcraft.common.entities.projectile.EntityHomingShard;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import java.util.Random;
import net.minecraft.client.renderer.entity.Render;

public class RenderHomingShard extends Render
{
    private Random random;
    private static final ResourceLocation beamTexture;
    
    public RenderHomingShard(final RenderManager rm) {
        super(rm);
        this.random = new Random();
        this.shadowSize = 0.0f;
    }
    
    public void renderEntityAt(final EntityHomingShard entity, final double x, final double y, final double z, final float fq, final float pticks) {
        final Tessellator tessellator = Tessellator.getInstance();
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        this.bindTexture(ParticleEngine.particleTexture);
        final float f2 = (8 + entity.ticksExisted % 8) / 64.0f;
        final float f3 = f2 + 0.015625f;
        final float f4 = 0.0625f;
        final float f5 = f4 + 0.015625f;
        final float f6 = 1.0f;
        final float f7 = 0.5f;
        final float f8 = 0.5f;
        GL11.glColor4f(0.9f, 0.075f, 0.9525f, 1.0f);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(180.0f - this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
        GL11.glScalef(0.4f + 0.1f * entity.getStrength(), 0.4f + 0.1f * entity.getStrength(), 0.4f + 0.1f * entity.getStrength());
        tessellator.getBuffer().begin(7, UtilsFX.VERTEXFORMAT_POS_TEX_CO_LM_NO);
        final int i = 240;
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        tessellator.getBuffer().pos(-f7, -f8, 0.0).tex(f2, f5).color(0.9f, 0.075f, 0.9525f, 1.0f).lightmap(j, k).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(f6 - f7, -f8, 0.0).tex(f3, f5).color(0.9f, 0.075f, 0.9525f, 1.0f).lightmap(j, k).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(f6 - f7, 1.0f - f8, 0.0).tex(f3, f4).color(0.9f, 0.075f, 0.9525f, 1.0f).lightmap(j, k).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(-f7, 1.0f - f8, 0.0).tex(f2, f4).color(0.9f, 0.075f, 0.9525f, 1.0f).lightmap(j, k).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.draw();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 1);
        this.bindTexture(RenderHomingShard.beamTexture);
        final Minecraft mc = Minecraft.getMinecraft();
        final EntityPlayerSP p = mc.player;
        final double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * pticks;
        final double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * pticks;
        final double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * pticks;
        final UtilsFX.Vector player = new UtilsFX.Vector((float)doubleX, (float)doubleY, (float)doubleZ);
        final double dX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pticks;
        final double dY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pticks;
        final double dZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pticks;
        final UtilsFX.Vector start = new UtilsFX.Vector((float)dX, (float)dY, (float)dZ);
        if (entity.vl.size() == 0) {
            entity.vl.add(start);
        }
        GL11.glTranslated(-doubleX, -doubleY, -doubleZ);
        UtilsFX.Vector vs = new UtilsFX.Vector((float)dX, (float)dY, (float)dZ);
        tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
        int c = entity.vl.size();
        for (final UtilsFX.Vector nv : entity.vl) {
            UtilsFX.drawBeam(vs, nv, player, 0.25f * (c / (float)entity.vl.size()), 240, 0.405f, 0.075f, 0.525f, 0.5f);
            vs = nv;
            --c;
        }
        tessellator.draw();
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
        GL11.glDisable(32826);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }
    
    public void doRender(final Entity entity, final double d, final double d1, final double d2, final float f, final float f1) {
        this.renderEntityAt((EntityHomingShard)entity, d, d1, d2, f, f1);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
    
    static {
        beamTexture = new ResourceLocation("thaumcraft", "textures/misc/beaml.png");
    }
}
