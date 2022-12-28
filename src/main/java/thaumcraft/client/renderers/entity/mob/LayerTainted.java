package thaumcraft.client.renderers.entity.mob;
import java.util.ArrayList;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;


public class LayerTainted implements LayerRenderer<EntityLiving>
{
    private static ResourceLocation TAINT_TEXTURE;
    private RenderLivingBase renderer;
    private ModelBase model;
    public static ArrayList<Integer> taintLayers;
    
    public LayerTainted(int i, RenderLivingBase witherRendererIn, ModelBase model) {
        renderer = witherRendererIn;
        this.model = model;
        LayerTainted.taintLayers.add(i);
    }
    
    public void doRenderLayer(EntityLiving entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!LayerTainted.taintLayers.contains(entitylivingbaseIn.getEntityId())) {
            return;
        }
        boolean flag = entitylivingbaseIn.isInvisible();
        GlStateManager.depthMask(!flag);
        renderer.bindTexture(LayerTainted.TAINT_TEXTURE);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float f = (float)entitylivingbaseIn.getEntityId();
        float f2 = MathHelper.cos(f * 2.5E-4f);
        float f3 = f * 0.001f;
        GlStateManager.scale(8.0f, 4.0f, 4.0f);
        GlStateManager.translate(f2, f3, 0.0f);
        GlStateManager.matrixMode(5888);
        GlStateManager.enableBlend();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.66f);
        GL11.glBlendFunc(770, 771);
        model.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
        model.setModelAttributes(renderer.getMainModel());
        model.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(flag);
    }
    
    public boolean shouldCombineTextures() {
        return false;
    }
    
    static {
        TAINT_TEXTURE = new ResourceLocation("thaumcraft:textures/models/taint_fibres.png");
        LayerTainted.taintLayers = new ArrayList<Integer>();
    }
}
