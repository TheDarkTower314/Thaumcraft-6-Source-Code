// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import java.awt.Color;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.model.ModelBat;
import thaumcraft.common.entities.monster.EntitySpellBat;
import net.minecraft.client.model.ModelBase;
import thaumcraft.client.renderers.models.entity.ModelFireBat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;

@SideOnly(Side.CLIENT)
public class RenderSpellBat extends RenderLiving
{
    private int renderedBatSize;
    private static final ResourceLocation rl;
    
    public RenderSpellBat(final RenderManager rm) {
        super(rm, new ModelFireBat(), 0.25f);
        this.renderedBatSize = ((ModelFireBat)this.mainModel).getBatSize();
    }
    
    public void func_82443_a(final EntitySpellBat bat, final double par2, final double par4, final double par6, final float par8, final float par9) {
        final int var10 = ((ModelFireBat)this.mainModel).getBatSize();
        if (var10 != this.renderedBatSize) {
            this.renderedBatSize = var10;
            this.mainModel = new ModelBat();
        }
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        final Color c = new Color(bat.color);
        final float r = c.getRed() / 255.0f;
        final float g = c.getGreen() / 255.0f;
        final float b = c.getBlue() / 255.0f;
        GL11.glColor4f(r, g, b, 0.5f);
        super.doRender(bat, par2, par4, par6, par8, par9);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    
    protected void func_82442_a(final EntitySpellBat par1EntityBat, final float par2) {
        GL11.glScalef(0.35f, 0.35f, 0.35f);
    }
    
    protected void func_82445_a(final EntitySpellBat par1EntityBat, final double par2, final double par4, final double par6) {
        super.renderLivingAt(par1EntityBat, par2, par4, par6);
    }
    
    protected void func_82444_a(final EntitySpellBat par1EntityBat, final float par2, final float par3, final float par4) {
        GL11.glTranslatef(0.0f, -0.1f, 0.0f);
        super.applyRotations(par1EntityBat, par2, par3, par4);
    }
    
    protected void preRenderCallback(final EntityLivingBase par1EntityLiving, final float par2) {
        this.func_82442_a((EntitySpellBat)par1EntityLiving, par2);
    }
    
    protected void applyRotations(final EntityLivingBase par1EntityLiving, final float par2, final float par3, final float par4) {
        this.func_82444_a((EntitySpellBat)par1EntityLiving, par2, par3, par4);
    }
    
    protected void renderLivingAt(final EntityLivingBase par1EntityLiving, final double par2, final double par4, final double par6) {
        this.func_82445_a((EntitySpellBat)par1EntityLiving, par2, par4, par6);
    }
    
    public void doRender(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.func_82443_a((EntitySpellBat)par1EntityLiving, par2, par4, par6, par8, par9);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return RenderSpellBat.rl;
    }
    
    static {
        rl = new ResourceLocation("thaumcraft", "textures/entity/spellbat.png");
    }
}
