package thaumcraft.client.renderers.entity.mob;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.renderers.models.entity.ModelEldritchCrab;


@SideOnly(Side.CLIENT)
public class RenderEldritchCrab extends RenderLiving
{
    private static ResourceLocation skin;
    
    public RenderEldritchCrab(RenderManager renderManager) {
        super(renderManager, new ModelEldritchCrab(), 0.5f);
    }
    
    protected ResourceLocation getEntityTexture(Entity entity) {
        return RenderEldritchCrab.skin;
    }
    
    public void renderCrab(EntityLiving crab, double par2, double par4, double par6, float par8, float par9) {
        super.doRender(crab, par2, par4, par6, par8, par9);
    }
    
    public void doRender(EntityLiving par1Entity, double par2, double par4, double par6, float par8, float par9) {
        renderCrab(par1Entity, par2, par4, par6, par8, par9);
    }
    
    protected void preRenderCallback(EntityLivingBase entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(0.8f, 0.8f, 0.8f);
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
    }
    
    static {
        skin = new ResourceLocation("thaumcraft", "textures/entity/crab.png");
    }
}
