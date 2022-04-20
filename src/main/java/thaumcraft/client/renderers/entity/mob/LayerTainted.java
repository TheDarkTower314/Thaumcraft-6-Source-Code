// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.renderer.GlStateManager;
import java.util.ArrayList;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.EntityLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class LayerTainted implements LayerRenderer<EntityLiving>
{
    private static final ResourceLocation TAINT_TEXTURE;
    private final RenderLivingBase renderer;
    private final ModelBase model;
    public static ArrayList<Integer> taintLayers;
    
    public LayerTainted(final int i, final RenderLivingBase witherRendererIn, final ModelBase model) {
        this.renderer = witherRendererIn;
        this.model = model;
        LayerTainted.taintLayers.add(i);
    }
    
    public void doRenderLayer(final EntityLiving entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
        if (!LayerTainted.taintLayers.contains(entitylivingbaseIn.getEntityId())) {
            return;
        }
        final boolean flag = entitylivingbaseIn.isInvisible();
        GlStateManager.depthMask(!flag);
        this.renderer.bindTexture(LayerTainted.TAINT_TEXTURE);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        final float f = (float)entitylivingbaseIn.getEntityId();
        final float f2 = MathHelper.cos(f * 2.5E-4f);
        final float f3 = f * 0.001f;
        GlStateManager.scale(8.0f, 4.0f, 4.0f);
        GlStateManager.translate(f2, f3, 0.0f);
        GlStateManager.matrixMode(5888);
        GlStateManager.enableBlend();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.66f);
        GL11.glBlendFunc(770, 771);
        this.model.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
        this.model.setModelAttributes(this.renderer.getMainModel());
        this.model.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
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
