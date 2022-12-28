package thaumcraft.client.renderers.entity.projectile;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.projectile.EntityFocusProjectile;


public class RenderNoProjectile extends Render
{
    public RenderNoProjectile(RenderManager rm) {
        super(rm);
        shadowSize = 0.1f;
    }
    
    public void renderEntityAt(EntityThrowable tg, double x, double y, double z, float fq) {
        if (tg instanceof EntityFocusProjectile) {
            EntityFocusProjectile gp = (EntityFocusProjectile)tg;
            float qq = fq - gp.lastRenderTick;
            if (qq < 0.0f) {
                ++qq;
            }
            if (qq > 0.2) {
                gp.renderParticle(fq);
            }
        }
    }
    
    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
        renderEntityAt((EntityThrowable)entity, d, d1, d2, f1);
    }
    
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
