// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import net.minecraft.entity.EntityLiving;
import net.minecraft.client.model.ModelBat;
import thaumcraft.common.entities.monster.EntityFireBat;
import net.minecraft.client.model.ModelBase;
import thaumcraft.client.renderers.models.entity.ModelFireBat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;

@SideOnly(Side.CLIENT)
public class RenderFireBat extends RenderLiving
{
    private int renderedBatSize;
    private static final ResourceLocation rl;
    
    public RenderFireBat(final RenderManager rm) {
        super(rm, new ModelFireBat(), 0.25f);
        this.renderedBatSize = ((ModelFireBat)this.mainModel).getBatSize();
    }
    
    public void func_82443_a(final EntityFireBat par1EntityBat, final double par2, final double par4, final double par6, final float par8, final float par9) {
        final int var10 = ((ModelFireBat)this.mainModel).getBatSize();
        if (var10 != this.renderedBatSize) {
            this.renderedBatSize = var10;
            this.mainModel = new ModelBat();
        }
        super.doRender(par1EntityBat, par2, par4, par6, par8, par9);
    }
    
    protected void func_82442_a(final EntityFireBat par1EntityBat, final float par2) {
        GL11.glScalef(0.35f, 0.35f, 0.35f);
    }
    
    protected void func_82445_a(final EntityFireBat par1EntityBat, final double par2, final double par4, final double par6) {
        super.renderLivingAt(par1EntityBat, par2, par4, par6);
    }
    
    protected void func_82444_a(final EntityFireBat par1EntityBat, final float par2, final float par3, final float par4) {
        if (!par1EntityBat.getIsBatHanging()) {
            GL11.glTranslatef(0.0f, MathHelper.cos(par2 * 0.3f) * 0.1f, 0.0f);
        }
        else {
            GL11.glTranslatef(0.0f, -0.1f, 0.0f);
        }
        super.applyRotations(par1EntityBat, par2, par3, par4);
    }
    
    protected void preRenderCallback(final EntityLivingBase par1EntityLiving, final float par2) {
        this.func_82442_a((EntityFireBat)par1EntityLiving, par2);
    }
    
    protected void applyRotations(final EntityLivingBase par1EntityLiving, final float par2, final float par3, final float par4) {
        this.func_82444_a((EntityFireBat)par1EntityLiving, par2, par3, par4);
    }
    
    protected void renderLivingAt(final EntityLivingBase par1EntityLiving, final double par2, final double par4, final double par6) {
        this.func_82445_a((EntityFireBat)par1EntityLiving, par2, par4, par6);
    }
    
    public void doRender(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.func_82443_a((EntityFireBat)par1EntityLiving, par2, par4, par6, par8, par9);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return RenderFireBat.rl;
    }
    
    static {
        rl = new ResourceLocation("thaumcraft", "textures/entity/firebat.png");
    }
}
