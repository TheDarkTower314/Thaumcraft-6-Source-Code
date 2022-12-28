package thaumcraft.client.renderers.entity;
import com.sasmaster.glelwjgl.java.CoreGLE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.ender.ShaderCallback;
import thaumcraft.client.lib.ender.ShaderHelper;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.utils.EntityUtils;


public class RenderFluxRift extends Render
{
    private ShaderCallback shaderCallback;
    private static ResourceLocation starsTexture;
    CoreGLE gle;
    
    public RenderFluxRift(RenderManager rm) {
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
    
    public void doRender(Entity entity, double x, double y, double z, float yaw, float pt) {
        EntityFluxRift rift = (EntityFluxRift)entity;
        boolean goggles = EntityUtils.hasGoggles(Minecraft.getMinecraft().player);
        GL11.glPushMatrix();
        bindTexture(RenderFluxRift.starsTexture);
        ShaderHelper.useShader(ShaderHelper.endShader, shaderCallback);
        float amp = 1.0f;
        float stab = MathHelper.clamp(1.0f - rift.getRiftStability() / 50.0f, 0.0f, 1.5f);
        GL11.glEnable(3042);
        for (int q = 0; q <= 3; ++q) {
            if (q < 3) {
                GlStateManager.depthMask(false);
                if (q == 0 && goggles) {
                    GL11.glDisable(2929);
                }
            }
            GL11.glBlendFunc(770, (q < 3) ? 1 : 771);
            if (rift.points.size() > 2) {
                GL11.glPushMatrix();
                double[][] pp = new double[rift.points.size()][3];
                float[][] colours = new float[rift.points.size()][4];
                double[] radii = new double[rift.points.size()];
                for (int a = 0; a < rift.points.size(); ++a) {
                    float var = rift.ticksExisted + pt;
                    if (a > rift.points.size() / 2) {
                        var -= a * 10;
                    }
                    else if (a < rift.points.size() / 2) {
                        var += a * 10;
                    }
                    pp[a][0] = rift.points.get(a).x + x + Math.sin(var / 50.0f * amp) * 0.10000000149011612 * stab;
                    pp[a][1] = rift.points.get(a).y + y + Math.sin(var / 60.0f * amp) * 0.10000000149011612 * stab;
                    pp[a][2] = rift.points.get(a).z + z + Math.sin(var / 70.0f * amp) * 0.10000000149011612 * stab;
                    colours[a][0] = 1.0f;
                    colours[a][1] = 1.0f;
                    colours[a][2] = 1.0f;
                    colours[a][3] = 1.0f;
                    double w = 1.0 - Math.sin(var / 8.0f * amp) * 0.10000000149011612 * stab;
                    radii[a] = rift.pointsWidth.get(a) * w * ((q < 3) ? (1.25f + 0.5f * q) : 1.0f);
                }
                gle.set_POLYCYL_TESS(6);
                gle.gleSetJoinStyle(1026);
                gle.glePolyCone(pp.length, pp, colours, radii, 1.0f, 0.0f);
                GL11.glPopMatrix();
            }
            if (q < 3) {
                GlStateManager.depthMask(true);
                if (q == 0 && goggles) {
                    GL11.glEnable(2929);
                }
            }
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
        ShaderHelper.releaseShader();
        GL11.glPopMatrix();
    }
    
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
    
    static {
        starsTexture = new ResourceLocation("textures/entity/end_portal.png");
    }
}
