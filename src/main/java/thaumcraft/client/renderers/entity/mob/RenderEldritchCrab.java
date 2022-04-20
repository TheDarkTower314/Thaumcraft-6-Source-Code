// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.mob;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBase;
import thaumcraft.client.renderers.models.entity.ModelEldritchCrab;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;

@SideOnly(Side.CLIENT)
public class RenderEldritchCrab extends RenderLiving
{
    private static final ResourceLocation skin;
    
    public RenderEldritchCrab(final RenderManager renderManager) {
        super(renderManager, new ModelEldritchCrab(), 0.5f);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return RenderEldritchCrab.skin;
    }
    
    public void renderCrab(final EntityLiving crab, final double par2, final double par4, final double par6, final float par8, final float par9) {
        super.doRender(crab, par2, par4, par6, par8, par9);
    }
    
    public void doRender(final EntityLiving par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderCrab(par1Entity, par2, par4, par6, par8, par9);
    }
    
    protected void preRenderCallback(final EntityLivingBase entitylivingbaseIn, final float partialTickTime) {
        GlStateManager.scale(0.8f, 0.8f, 0.8f);
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
    }
    
    static {
        skin = new ResourceLocation("thaumcraft", "textures/entity/crab.png");
    }
}
