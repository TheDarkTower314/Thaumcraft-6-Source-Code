// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.construct;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityMinecart;
import org.lwjgl.opengl.GL11;
import net.minecraft.entity.Entity;
import thaumcraft.common.entities.construct.EntityTurretCrossbow;
import thaumcraft.client.lib.obj.AdvancedModelLoader;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.lib.obj.IModelCustom;
import net.minecraft.client.renderer.entity.RenderLiving;

public class RenderTurretCrossbowAdvanced extends RenderLiving
{
    private IModelCustom model;
    private static final ResourceLocation TURMODEL;
    private static final ResourceLocation rl;
    
    public RenderTurretCrossbowAdvanced(final RenderManager rm) {
        super(rm, null, 0.5f);
        this.model = AdvancedModelLoader.loadModel(RenderTurretCrossbowAdvanced.TURMODEL);
    }
    
    public void renderTurret(final EntityTurretCrossbow turret, final double x, final double y, final double z, final float par8, final float pTicks) {
        this.bindEntityTexture(turret);
        GL11.glPushMatrix();
        GL11.glEnable(32826);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glTranslated(x, y + 0.75, z);
        GL11.glPushMatrix();
        if (turret.isRiding() && turret.getRidingEntity() != null && turret.getRidingEntity() instanceof EntityMinecart) {
            GL11.glScaled(0.66, 0.75, 0.66);
        }
        this.model.renderPart("legs");
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        if (turret.hurtTime > 0) {
            GlStateManager.color(1.0f, 0.5f, 0.5f, 1.0f);
            final float jiggle = turret.hurtTime / 500.0f;
            GL11.glTranslated(turret.getRNG().nextGaussian() * jiggle, turret.getRNG().nextGaussian() * jiggle, turret.getRNG().nextGaussian() * jiggle);
        }
        GL11.glRotatef(turret.prevRotationYawHead + (turret.rotationYawHead - turret.prevRotationYawHead) * pTicks, 0.0f, -1.0f, 0.0f);
        GL11.glRotatef(turret.prevRotationPitch + (turret.rotationPitch - turret.prevRotationPitch) * pTicks, 1.0f, 0.0f, 0.0f);
        this.model.renderPart("mech");
        this.model.renderPart("box");
        this.model.renderPart("shield");
        this.model.renderPart("brain");
        GL11.glPushMatrix();
        GL11.glTranslated(0.0, 0.0, MathHelper.sin(MathHelper.sqrt(turret.loadProgressForRender) * 3.1415927f * 2.0f) / 12.0f);
        this.model.renderPart("loader");
        GL11.glPopMatrix();
        float sp = 0.0f;
        if (this.getSwingProgress(turret, pTicks) > -9990.0f) {
            sp = MathHelper.sin(MathHelper.sqrt(this.getSwingProgress(turret, pTicks)) * 3.1415927f * 2.0f) * 20.0f;
        }
        GL11.glTranslated(0.0, 0.0, 0.375);
        GL11.glPushMatrix();
        GL11.glRotatef(sp, 0.0f, 1.0f, 0.0f);
        this.model.renderPart("bow1");
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glRotatef(sp, 0.0f, -1.0f, 0.0f);
        this.model.renderPart("bow2");
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(3042);
        GL11.glDisable(32826);
        GL11.glPopMatrix();
    }
    
    protected float getSwingProgress(final EntityLivingBase e, final float p_77040_2_) {
        ((EntityTurretCrossbow)e).loadProgressForRender = ((EntityTurretCrossbow)e).getLoadProgress(p_77040_2_);
        return super.getSwingProgress(e, p_77040_2_);
    }
    
    private void translateFromOrientation(final int orientation) {
        GL11.glTranslated(0.0, 0.5, 0.0);
        if (orientation == 0) {
            GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        }
        else if (orientation != 1) {
            if (orientation == 2) {
                GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
            }
            else if (orientation == 3) {
                GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            }
            else if (orientation == 4) {
                GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
            }
            else if (orientation == 5) {
                GL11.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
            }
        }
        GL11.glTranslated(0.0, -0.5, 0.0);
    }
    
    public void doRender(final EntityLiving par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderTurret((EntityTurretCrossbow)par1Entity, par2, par4, par6, par8, par9);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return RenderTurretCrossbowAdvanced.rl;
    }
    
    static {
        TURMODEL = new ResourceLocation("thaumcraft", "models/obj/crossbow_advanced.obj");
        rl = new ResourceLocation("thaumcraft", "textures/entity/crossbow_advanced.png");
    }
}
