package thaumcraft.client.renderers.entity.mob;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSpider;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerSpiderEyes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.entities.monster.EntityMindSpider;


@SideOnly(Side.CLIENT)
public class RenderMindSpider extends RenderSpider
{
    public RenderMindSpider(RenderManager rm) {
        super(rm);
        shadowSize = 0.5f;
        addLayer(new LayerSpiderEyes(this));
    }
    
    protected void preRenderCallback(EntityLivingBase par1EntityLiving, float par2) {
        GL11.glScalef(0.6f, 0.6f, 0.6f);
    }
    
    public void doRender(EntityLiving p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
        if (((EntityMindSpider)p_76986_1_).getViewer().length() == 0 || ((EntityMindSpider)p_76986_1_).getViewer().equals(renderManager.renderViewEntity.getName())) {
            super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
        }
    }
    
    protected void renderModel(EntityLivingBase entity, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_) {
        bindEntityTexture(entity);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, Math.min(0.1f, entity.ticksExisted / 100.0f));
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glAlphaFunc(516, 0.003921569f);
        mainModel.render(entity, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
        GL11.glAlphaFunc(516, 0.1f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
    }
}
