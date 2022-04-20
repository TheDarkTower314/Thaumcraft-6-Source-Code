// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.projectile;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import thaumcraft.common.entities.projectile.EntityFocusProjectile;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;

public class RenderNoProjectile extends Render
{
    public RenderNoProjectile(final RenderManager rm) {
        super(rm);
        this.shadowSize = 0.1f;
    }
    
    public void renderEntityAt(final EntityThrowable tg, final double x, final double y, final double z, final float fq) {
        if (tg instanceof EntityFocusProjectile) {
            final EntityFocusProjectile gp = (EntityFocusProjectile)tg;
            float qq = fq - gp.lastRenderTick;
            if (qq < 0.0f) {
                ++qq;
            }
            if (qq > 0.2) {
                gp.renderParticle(fq);
            }
        }
    }
    
    public void doRender(final Entity entity, final double d, final double d1, final double d2, final float f, final float f1) {
        this.renderEntityAt((EntityThrowable)entity, d, d1, d2, f1);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
