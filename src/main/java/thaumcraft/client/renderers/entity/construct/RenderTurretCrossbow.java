// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.construct;

import net.minecraft.entity.Entity;
import thaumcraft.common.entities.construct.EntityTurretCrossbow;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.model.ModelBase;
import thaumcraft.client.renderers.models.entity.ModelCrossbow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.entity.RenderLiving;

public class RenderTurretCrossbow extends RenderLiving
{
    private static final ResourceLocation rl;
    
    public RenderTurretCrossbow(final RenderManager rm) {
        super(rm, new ModelCrossbow(), 0.5f);
    }
    
    protected float getSwingProgress(final EntityLivingBase e, final float p_77040_2_) {
        ((EntityTurretCrossbow)e).loadProgressForRender = ((EntityTurretCrossbow)e).getLoadProgress(p_77040_2_);
        e.renderYawOffset = 0.0f;
        e.prevRenderYawOffset = 0.0f;
        return super.getSwingProgress(e, p_77040_2_);
    }
    
    protected void preRenderCallback(final EntityLivingBase entitylivingbaseIn, final float partialTickTime) {
        entitylivingbaseIn.renderYawOffset = 0.0f;
        entitylivingbaseIn.prevRenderYawOffset = 0.0f;
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return RenderTurretCrossbow.rl;
    }
    
    static {
        rl = new ResourceLocation("thaumcraft", "textures/entity/crossbow.png");
    }
}
