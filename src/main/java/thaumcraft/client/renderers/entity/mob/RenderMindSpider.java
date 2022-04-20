// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.mob;

import net.minecraft.entity.Entity;
import thaumcraft.common.entities.monster.EntityMindSpider;
import net.minecraft.entity.EntityLiving;
import org.lwjgl.opengl.GL11;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerSpiderEyes;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderSpider;

@SideOnly(Side.CLIENT)
public class RenderMindSpider extends RenderSpider
{
    public RenderMindSpider(final RenderManager rm) {
        super(rm);
        this.shadowSize = 0.5f;
        this.addLayer(new LayerSpiderEyes(this));
    }
    
    protected void preRenderCallback(final EntityLivingBase par1EntityLiving, final float par2) {
        GL11.glScalef(0.6f, 0.6f, 0.6f);
    }
    
    public void doRender(final EntityLiving p_76986_1_, final double p_76986_2_, final double p_76986_4_, final double p_76986_6_, final float p_76986_8_, final float p_76986_9_) {
        if (((EntityMindSpider)p_76986_1_).getViewer().length() == 0 || ((EntityMindSpider)p_76986_1_).getViewer().equals(this.renderManager.renderViewEntity.getName())) {
            super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
        }
    }
    
    protected void renderModel(final EntityLivingBase entity, final float p_77036_2_, final float p_77036_3_, final float p_77036_4_, final float p_77036_5_, final float p_77036_6_, final float p_77036_7_) {
        this.bindEntityTexture(entity);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, Math.min(0.1f, entity.ticksExisted / 100.0f));
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glAlphaFunc(516, 0.003921569f);
        this.mainModel.render(entity, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
        GL11.glAlphaFunc(516, 0.1f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
    }
}
