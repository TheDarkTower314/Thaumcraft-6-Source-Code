package thaumcraft.client.renderers.entity.mob;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.entity.ModelEldritchGolem;


@SideOnly(Side.CLIENT)
public class RenderEldritchGolem extends RenderLiving
{
    protected ModelEldritchGolem modelMain;
    private static ResourceLocation skin;
    
    public RenderEldritchGolem(RenderManager rm, ModelEldritchGolem par1ModelBiped, float par2) {
        super(rm, par1ModelBiped, par2);
        modelMain = par1ModelBiped;
    }
    
    protected ResourceLocation getEntityTexture(Entity entity) {
        return RenderEldritchGolem.skin;
    }
    
    protected void preRenderCallback(EntityLivingBase par1EntityLiving, float par2) {
        GL11.glScalef(1.7f, 1.7f, 1.7f);
    }
    
    public void doRenderLiving(EntityLiving golem, double par2, double par4, double par6, float par8, float par9) {
        GL11.glEnable(3042);
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glBlendFunc(770, 771);
        super.doRender(golem, par2, par4, par6, par8, par9);
        GL11.glDisable(3042);
        GL11.glAlphaFunc(516, 0.1f);
    }
    
    public void doRender(EntityLiving par1Entity, double par2, double par4, double par6, float par8, float par9) {
        doRenderLiving(par1Entity, par2, par4, par6, par8, par9);
    }
    
    static {
        skin = new ResourceLocation("thaumcraft", "textures/entity/eldritch_golem.png");
    }
}
