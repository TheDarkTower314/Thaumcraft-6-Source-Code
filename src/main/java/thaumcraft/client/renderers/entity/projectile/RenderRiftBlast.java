package thaumcraft.client.renderers.entity.projectile;
import com.sasmaster.glelwjgl.java.CoreGLE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.ender.ShaderCallback;
import thaumcraft.client.lib.ender.ShaderHelper;
import thaumcraft.common.entities.projectile.EntityRiftBlast;


public class RenderRiftBlast extends Render
{
    private ShaderCallback shaderCallback;
    private static ResourceLocation starsTexture;
    CoreGLE gle;
    
    public RenderRiftBlast(RenderManager rm) {
        super(rm);
        gle = new CoreGLE();
        shadowSize = 0.0f;
        shaderCallback = new ShaderCallback() {
            @Override
            public void call(int shader) {
                Minecraft mc = Minecraft.getMinecraft();
                int x = ARBShaderObjects.glGetUniformLocationARB(shader, "yaw");
                ARBShaderObjects.glUniform1fARB(x, (float)(mc.player.rotationYaw * 2.0f * 3.141592653589793 / 360.0));
                int z = ARBShaderObjects.glGetUniformLocationARB(shader, "pitch");
                ARBShaderObjects.glUniform1fARB(z, -(float)(mc.player.rotationPitch * 2.0f * 3.141592653589793 / 360.0));
            }
        };
    }
    
    public void renderEntityAt(EntityRiftBlast entity, double x, double y, double z, float fq, float pticks) {
        Tessellator tessellator = Tessellator.getInstance();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        float xx = (float)(entity.prevPosX + (entity.posX - entity.prevPosX) * pticks);
        float yy = (float)(entity.prevPosY + (entity.posY - entity.prevPosY) * pticks);
        float zz = (float)(entity.prevPosZ + (entity.posZ - entity.prevPosZ) * pticks);
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
                double[] r2 = new double[entity.radii.length];
                int ri = 0;
                float m = (1.5f - q) / 1.0f;
                for (double d : entity.radii) {
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
    
    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
        renderEntityAt((EntityRiftBlast)entity, d, d1, d2, f, f1);
    }
    
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
    
    static {
        starsTexture = new ResourceLocation("textures/entity/end_portal.png");
    }
}
