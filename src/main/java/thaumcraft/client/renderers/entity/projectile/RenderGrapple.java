// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.projectile;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.Minecraft;
import thaumcraft.common.entities.projectile.EntityGrapple;
import thaumcraft.client.lib.UtilsFX;
import net.minecraft.util.math.MathHelper;
import thaumcraft.client.fx.ParticleEngine;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.Vec3d;
import java.util.ArrayList;
import com.sasmaster.glelwjgl.java.CoreGLE;
import thaumcraft.client.renderers.models.entity.ModelGrappler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.entity.Render;

public class RenderGrapple extends Render
{
    ResourceLocation beam;
    ResourceLocation rope;
    private ModelGrappler model;
    CoreGLE gle;
    public ArrayList<Vec3d> points;
    public float length;
    
    public RenderGrapple(final RenderManager rm) {
        super(rm);
        beam = new ResourceLocation("thaumcraft", "textures/entity/grappler.png");
        rope = new ResourceLocation("thaumcraft", "textures/misc/rope.png");
        gle = new CoreGLE();
        points = new ArrayList<Vec3d>();
        length = 1.0f;
        shadowSize = 0.0f;
        model = new ModelGrappler();
    }
    
    public void renderEntityAt(final Entity entity, final double x, final double y, final double z, final float fq, final float pticks) {
        final Tessellator tessellator = Tessellator.getInstance();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glPushMatrix();
        bindTexture(beam);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * pticks - 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * pticks, 0.0f, 0.0f, 1.0f);
        model.render();
        GL11.glPopMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        GL11.glDisable(2884);
        bindTexture(ParticleEngine.particleTexture);
        final float f2 = (1 + entity.ticksExisted % 6) / 32.0f;
        final float f3 = f2 + 0.03125f;
        final float f4 = 0.21875f;
        final float f5 = f4 + 0.03125f;
        final float f6 = 0.5f;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.8f);
        GL11.glRotatef(180.0f - renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
        final float bob = MathHelper.sin(entity.ticksExisted / 5.0f) * 0.2f + 0.2f;
        GL11.glScalef(1.0f + bob, 1.0f + bob, 1.0f + bob);
        tessellator.getBuffer().begin(7, UtilsFX.VERTEXFORMAT_POS_TEX_CO_LM_NO);
        final int i = 220;
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        tessellator.getBuffer().pos(-f6, -f6, 0.0).tex(f2, f5).color(1.0f, 1.0f, 1.0f, 0.21f).lightmap(j, k).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(f6, -f6, 0.0).tex(f3, f5).color(1.0f, 1.0f, 1.0f, 0.21f).lightmap(j, k).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(f6, f6, 0.0).tex(f3, f4).color(1.0f, 1.0f, 1.0f, 0.21f).lightmap(j, k).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(-f6, f6, 0.0).tex(f2, f4).color(1.0f, 1.0f, 1.0f, 0.21f).lightmap(j, k).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.draw();
        GL11.glEnable(2884);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
        GL11.glDisable(32826);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        calcPoints(((EntityGrapple)entity).getThrower(), (EntityGrapple)entity, pticks);
        GL11.glPushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(beam);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        if (points != null && points.size() > 2) {
            final double[][] pp = new double[points.size()][3];
            final float[][] colours = new float[points.size()][4];
            final double[] radii = new double[points.size()];
            for (int a = 0; a < points.size(); ++a) {
                pp[a][0] = points.get(a).x + x;
                pp[a][1] = points.get(a).y + y;
                pp[a][2] = points.get(a).z + z;
                colours[a][0] = 1.0f;
                colours[a][1] = 1.0f;
                colours[a][2] = 1.0f;
                colours[a][3] = 1.0f;
                radii[a] = 0.025;
            }
            Minecraft.getMinecraft().renderEngine.bindTexture(rope);
            gle.set_POLYCYL_TESS(4);
            gle.gleSetJoinStyle(1042);
            gle.glePolyCone(pp.length, pp, colours, radii, 2.0f, length - points.size());
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    
    private void calcPoints(final EntityLivingBase thrower, final EntityGrapple grapple, final float pt) {
        if (thrower == null || grapple == null) {
            return;
        }
        double tx = thrower.lastTickPosX + (thrower.posX - thrower.lastTickPosX) * pt;
        double ty = thrower.lastTickPosY + (thrower.posY - thrower.lastTickPosY) * pt;
        double tz = thrower.lastTickPosZ + (thrower.posZ - thrower.lastTickPosZ) * pt;
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
            final double yy = thrower.prevRotationYaw + (thrower.rotationYaw - thrower.prevRotationYaw) * (double)pt;
            final double px = -MathHelper.cos((float)((yy - 0.5) / 180.0 * 3.1415929794311523)) * 0.1f * ((grapple.hand == EnumHand.MAIN_HAND) ? 1 : -1);
            final double pz = -MathHelper.sin((float)((yy - 0.5) / 180.0 * 3.1415929794311523)) * 0.1f * ((grapple.hand == EnumHand.MAIN_HAND) ? 1 : -1);
            final Vec3d vl = thrower.getLookVec();
            tx += px + vl.x / 5.0;
            ty += thrower.getEyeHeight() / 2.6 + vl.y / 5.0;
            tz += pz + vl.z / 5.0;
        }
        final double gx = grapple.lastTickPosX + (grapple.posX - grapple.lastTickPosX) * pt;
        final double gy = grapple.lastTickPosY + (grapple.posY - grapple.lastTickPosY) * pt;
        final double gz = grapple.lastTickPosZ + (grapple.posZ - grapple.lastTickPosZ) * pt;
        points.clear();
        final Vec3d vs = new Vec3d(0.0, 0.0, 0.0);
        final Vec3d ve = new Vec3d(tx - gx, ty - gy + thrower.height / 2.0f, tz - gz);
        length = (float)(ve.lengthVector() * 5.0);
        final int steps = (int) length;
        points.add(vs);
        for (int a = 1; a < steps - 1; ++a) {
            final float dist = a * (length / steps);
            final float ss = 1.0f - a / (steps * 1.25f);
            final double dx = (tx - gx) / steps * a + MathHelper.sin(dist / 10.0f) * grapple.ampl * ss;
            final double dy = (ty - gy + thrower.height / 2.0f) / steps * a + MathHelper.sin(dist / 8.0f) * grapple.ampl * ss;
            final double dz = (tz - gz) / steps * a + MathHelper.sin(dist / 6.0f) * grapple.ampl * ss;
            final Vec3d vp = new Vec3d(dx, dy, dz);
            points.add(vp);
        }
        points.add(ve);
        points.add(ve);
    }
    
    public void doRender(final Entity entity, final double d, final double d1, final double d2, final float f, final float f1) {
        renderEntityAt(entity, d, d1, d2, f, f1);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
