package thaumcraft.client.renderers.entity.projectile;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.entities.projectile.EntityHomingShard;


public class RenderHomingShard extends Render
{
    private Random random;
    private static ResourceLocation beamTexture;
    
    public RenderHomingShard(RenderManager rm) {
        super(rm);
        random = new Random();
        shadowSize = 0.0f;
    }
    
    public void renderEntityAt(EntityHomingShard entity, double x, double y, double z, float fq, float pticks) {
        Tessellator tessellator = Tessellator.getInstance();
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        bindTexture(ParticleEngine.particleTexture);
        float f2 = (8 + entity.ticksExisted % 8) / 64.0f;
        float f3 = f2 + 0.015625f;
        float f4 = 0.0625f;
        float f5 = f4 + 0.015625f;
        float f6 = 1.0f;
        float f7 = 0.5f;
        float f8 = 0.5f;
        GL11.glColor4f(0.9f, 0.075f, 0.9525f, 1.0f);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(180.0f - renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
        GL11.glScalef(0.4f + 0.1f * entity.getStrength(), 0.4f + 0.1f * entity.getStrength(), 0.4f + 0.1f * entity.getStrength());
        tessellator.getBuffer().begin(7, UtilsFX.VERTEXFORMAT_POS_TEX_CO_LM_NO);
        int i = 240;
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        tessellator.getBuffer().pos(-f7, -f8, 0.0).tex(f2, f5).color(0.9f, 0.075f, 0.9525f, 1.0f).lightmap(j, k).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(f6 - f7, -f8, 0.0).tex(f3, f5).color(0.9f, 0.075f, 0.9525f, 1.0f).lightmap(j, k).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(f6 - f7, 1.0f - f8, 0.0).tex(f3, f4).color(0.9f, 0.075f, 0.9525f, 1.0f).lightmap(j, k).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(-f7, 1.0f - f8, 0.0).tex(f2, f4).color(0.9f, 0.075f, 0.9525f, 1.0f).lightmap(j, k).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.draw();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 1);
        bindTexture(RenderHomingShard.beamTexture);
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP p = mc.player;
        double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * pticks;
        double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * pticks;
        double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * pticks;
        UtilsFX.Vector player = new UtilsFX.Vector((float)doubleX, (float)doubleY, (float)doubleZ);
        double dX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pticks;
        double dY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pticks;
        double dZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pticks;
        UtilsFX.Vector start = new UtilsFX.Vector((float)dX, (float)dY, (float)dZ);
        if (entity.vl.size() == 0) {
            entity.vl.add(start);
        }
        GL11.glTranslated(-doubleX, -doubleY, -doubleZ);
        UtilsFX.Vector vs = new UtilsFX.Vector((float)dX, (float)dY, (float)dZ);
        tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
        int c = entity.vl.size();
        for (UtilsFX.Vector nv : entity.vl) {
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
    
    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
        renderEntityAt((EntityHomingShard)entity, d, d1, d2, f, f1);
    }
    
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
    
    static {
        beamTexture = new ResourceLocation("thaumcraft", "textures/misc/beaml.png");
    }
}
