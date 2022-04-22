// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.projectile;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import thaumcraft.client.lib.ender.ShaderHelper;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import thaumcraft.common.entities.projectile.EntityRiftBlast;
import org.lwjgl.opengl.ARBShaderObjects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import com.sasmaster.glelwjgl.java.CoreGLE;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.lib.ender.ShaderCallback;
import net.minecraft.client.renderer.entity.Render;

public class RenderRiftBlast extends Render
{
    private final ShaderCallback shaderCallback;
    private static final ResourceLocation starsTexture;
    CoreGLE gle;
    
    public RenderRiftBlast(final RenderManager rm) {
        super(rm);
        gle = new CoreGLE();
        shadowSize = 0.0f;
        shaderCallback = new ShaderCallback() {
            @Override
            public void call(final int shader) {
                final Minecraft mc = Minecraft.getMinecraft();
                final int x = ARBShaderObjects.glGetUniformLocationARB(shader, "yaw");
                ARBShaderObjects.glUniform1fARB(x, (float)(mc.player.rotationYaw * 2.0f * 3.141592653589793 / 360.0));
                final int z = ARBShaderObjects.glGetUniformLocationARB(shader, "pitch");
                ARBShaderObjects.glUniform1fARB(z, -(float)(mc.player.rotationPitch * 2.0f * 3.141592653589793 / 360.0));
            }
        };
    }
    
    public void renderEntityAt(final EntityRiftBlast entity, final double x, final double y, final double z, final float fq, final float pticks) {
        final Tessellator tessellator = Tessellator.getInstance();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        final float xx = (float)(entity.prevPosX + (entity.posX - entity.prevPosX) * pticks);
        final float yy = (float)(entity.prevPosY + (entity.posY - entity.prevPosY) * pticks);
        final float zz = (float)(entity.prevPosZ + (entity.posZ - entity.prevPosZ) * pticks);
        GL11.glTranslated(-xx, -yy, -zz);
        GL11.glEnable(3042);
        for (int q = 0; q <= 1; ++q) {
            if (q < 1) {
                GlStateManager.depthMask(false);
            }
            GL11.glBlendFunc(770, (q < 1) ? 1 : 771);
            if (entity.points != null && entity.points.length > 2) {
                Minecraft.getMinecraft().renderEngine.bindTexture(RenderRiftBlast.starsTexture);
                ShaderHelper.useShader(ShaderHelper.endShader, shaderCallback);
                final double[] r2 = new double[entity.radii.length];
                int ri = 0;
                final float m = (1.5f - q) / 1.0f;
                for (final double d : entity.radii) {
                    r2[ri] = entity.radii[ri] * m;
                    ++ri;
                }
                gle.set_POLYCYL_TESS(3);
                gle.set__ROUND_TESS_PIECES(1);
                gle.gleSetJoinStyle(1042);
                gle.glePolyCone(entity.points.length, entity.points, entity.colours, r2, 1.0f / entity.points.length, 0.0f);
                ShaderHelper.releaseShader();
            }
            if (q < 1) {
                GlStateManager.depthMask(true);
            }
        }
        GL11.glDisable(3042);
        GL11.glDisable(32826);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    public void doRender(final Entity entity, final double d, final double d1, final double d2, final float f, final float f1) {
        renderEntityAt((EntityRiftBlast)entity, d, d1, d2, f, f1);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
    
    static {
        starsTexture = new ResourceLocation("textures/entity/end_portal.png");
    }
}
