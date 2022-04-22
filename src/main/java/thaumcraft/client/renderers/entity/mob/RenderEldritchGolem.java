// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.mob;

import net.minecraft.entity.EntityLiving;
import org.lwjgl.opengl.GL11;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.entity.ModelEldritchGolem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;

@SideOnly(Side.CLIENT)
public class RenderEldritchGolem extends RenderLiving
{
    protected ModelEldritchGolem modelMain;
    private static final ResourceLocation skin;
    
    public RenderEldritchGolem(final RenderManager rm, final ModelEldritchGolem par1ModelBiped, final float par2) {
        super(rm, par1ModelBiped, par2);
        modelMain = par1ModelBiped;
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return RenderEldritchGolem.skin;
    }
    
    protected void preRenderCallback(final EntityLivingBase par1EntityLiving, final float par2) {
        GL11.glScalef(1.7f, 1.7f, 1.7f);
    }
    
    public void doRenderLiving(final EntityLiving golem, final double par2, final double par4, final double par6, final float par8, final float par9) {
        GL11.glEnable(3042);
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glBlendFunc(770, 771);
        super.doRender(golem, par2, par4, par6, par8, par9);
        GL11.glDisable(3042);
        GL11.glAlphaFunc(516, 0.1f);
    }
    
    public void doRender(final EntityLiving par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        doRenderLiving(par1Entity, par2, par4, par6, par8, par9);
    }
    
    static {
        skin = new ResourceLocation("thaumcraft", "textures/entity/eldritch_golem.png");
    }
}
