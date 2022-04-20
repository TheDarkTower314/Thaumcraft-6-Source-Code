// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.mob;

import org.lwjgl.opengl.GL11;
import net.minecraft.entity.EntityLiving;
import thaumcraft.common.entities.monster.EntityPech;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.entity.ModelPech;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;

@SideOnly(Side.CLIENT)
public class RenderPech extends RenderLiving
{
    protected ModelPech modelMain;
    protected ModelPech modelOverlay;
    private static final ResourceLocation[] skin;
    
    public RenderPech(final RenderManager rm, final ModelPech par1ModelBiped, final float par2) {
        super(rm, par1ModelBiped, par2);
        this.modelMain = par1ModelBiped;
        this.modelOverlay = new ModelPech();
        this.addLayer(new LayerHeldItemPech(this));
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return RenderPech.skin[((EntityPech)entity).getPechType()];
    }
    
    public void doRenderLiving(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6, final float par8, final float par9) {
        final float f2 = 1.0f;
        GL11.glColor3f(f2, f2, f2);
        double d3 = par4 - par1EntityLiving.getYOffset();
        if (par1EntityLiving.isSneaking()) {
            d3 -= 0.125;
        }
        super.doRender(par1EntityLiving, par2, d3, par6, par8, par9);
    }
    
    public void doRender(final EntityLiving par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.doRenderLiving(par1Entity, par2, par4, par6, par8, par9);
    }
    
    static {
        skin = new ResourceLocation[] { new ResourceLocation("thaumcraft", "textures/entity/pech_forage.png"), new ResourceLocation("thaumcraft", "textures/entity/pech_thaum.png"), new ResourceLocation("thaumcraft", "textures/entity/pech_stalker.png") };
    }
}
