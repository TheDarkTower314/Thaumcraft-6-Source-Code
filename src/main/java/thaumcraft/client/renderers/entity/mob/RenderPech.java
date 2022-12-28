package thaumcraft.client.renderers.entity.mob;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.entity.ModelPech;
import thaumcraft.common.entities.monster.EntityPech;


@SideOnly(Side.CLIENT)
public class RenderPech extends RenderLiving
{
    protected ModelPech modelMain;
    protected ModelPech modelOverlay;
    private static ResourceLocation[] skin;
    
    public RenderPech(RenderManager rm, ModelPech par1ModelBiped, float par2) {
        super(rm, par1ModelBiped, par2);
        modelMain = par1ModelBiped;
        modelOverlay = new ModelPech();
        addLayer(new LayerHeldItemPech(this));
    }
    
    protected ResourceLocation getEntityTexture(Entity entity) {
        return RenderPech.skin[((EntityPech)entity).getPechType()];
    }
    
    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
        float f2 = 1.0f;
        GL11.glColor3f(f2, f2, f2);
        double d3 = par4 - par1EntityLiving.getYOffset();
        if (par1EntityLiving.isSneaking()) {
            d3 -= 0.125;
        }
        super.doRender(par1EntityLiving, par2, d3, par6, par8, par9);
    }
    
    public void doRender(EntityLiving par1Entity, double par2, double par4, double par6, float par8, float par9) {
        doRenderLiving(par1Entity, par2, par4, par6, par8, par9);
    }
    
    static {
        skin = new ResourceLocation[] { new ResourceLocation("thaumcraft", "textures/entity/pech_forage.png"), new ResourceLocation("thaumcraft", "textures/entity/pech_thaum.png"), new ResourceLocation("thaumcraft", "textures/entity/pech_stalker.png") };
    }
}
