package thaumcraft.client.renderers.entity.mob;
import java.awt.Color;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBat;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.entity.ModelFireBat;
import thaumcraft.common.entities.monster.EntitySpellBat;


@SideOnly(Side.CLIENT)
public class RenderSpellBat extends RenderLiving
{
    private int renderedBatSize;
    private static ResourceLocation rl;
    
    public RenderSpellBat(RenderManager rm) {
        super(rm, new ModelFireBat(), 0.25f);
        renderedBatSize = ((ModelFireBat) mainModel).getBatSize();
    }
    
    public void func_82443_a(EntitySpellBat bat, double par2, double par4, double par6, float par8, float par9) {
        int var10 = ((ModelFireBat) mainModel).getBatSize();
        if (var10 != renderedBatSize) {
            renderedBatSize = var10;
            mainModel = new ModelBat();
        }
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        Color c = new Color(bat.color);
        float r = c.getRed() / 255.0f;
        float g = c.getGreen() / 255.0f;
        float b = c.getBlue() / 255.0f;
        GL11.glColor4f(r, g, b, 0.5f);
        super.doRender(bat, par2, par4, par6, par8, par9);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    
    protected void func_82442_a(EntitySpellBat par1EntityBat, float par2) {
        GL11.glScalef(0.35f, 0.35f, 0.35f);
    }
    
    protected void func_82445_a(EntitySpellBat par1EntityBat, double par2, double par4, double par6) {
        super.renderLivingAt(par1EntityBat, par2, par4, par6);
    }
    
    protected void func_82444_a(EntitySpellBat par1EntityBat, float par2, float par3, float par4) {
        GL11.glTranslatef(0.0f, -0.1f, 0.0f);
        super.applyRotations(par1EntityBat, par2, par3, par4);
    }
    
    protected void preRenderCallback(EntityLivingBase par1EntityLiving, float par2) {
        func_82442_a((EntitySpellBat)par1EntityLiving, par2);
    }
    
    protected void applyRotations(EntityLivingBase par1EntityLiving, float par2, float par3, float par4) {
        func_82444_a((EntitySpellBat)par1EntityLiving, par2, par3, par4);
    }
    
    protected void renderLivingAt(EntityLivingBase par1EntityLiving, double par2, double par4, double par6) {
        func_82445_a((EntitySpellBat)par1EntityLiving, par2, par4, par6);
    }
    
    public void doRender(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
        func_82443_a((EntitySpellBat)par1EntityLiving, par2, par4, par6, par8, par9);
    }
    
    protected ResourceLocation getEntityTexture(Entity entity) {
        return RenderSpellBat.rl;
    }
    
    static {
        rl = new ResourceLocation("thaumcraft", "textures/entity/spellbat.png");
    }
}
