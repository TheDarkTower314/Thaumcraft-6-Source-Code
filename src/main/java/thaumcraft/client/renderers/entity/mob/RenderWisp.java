package thaumcraft.client.renderers.entity.mob;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.entities.monster.EntityWisp;


public class RenderWisp extends Render
{
    public RenderWisp(RenderManager rm) {
        super(rm);
        shadowSize = 0.0f;
    }
    
    public void renderEntityAt(Entity entity, double x, double y, double z, float fq, float pticks) {
        if (((EntityLiving)entity).getHealth() <= 0.0f) {
            return;
        }
        double xx = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pticks;
        double yy = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pticks;
        double zz = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pticks;
        int color = 0;
        if (Aspect.getAspect(((EntityWisp)entity).getType()) != null) {
            color = Aspect.getAspect(((EntityWisp)entity).getType()).getColor();
        }
        GL11.glPushMatrix();
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glDepthMask(false);
        bindTexture(ParticleEngine.particleTexture);
        UtilsFX.renderFacingQuad(xx, yy, zz, 64, 64, 512 + entity.ticksExisted % 16, 0.4f, 16777215, 1.0f, 1, pticks);
        UtilsFX.renderFacingQuad(xx, yy, zz, 64, 64, 320 + entity.ticksExisted % 16, 0.75f, 16777215, 0.25f, 1, pticks);
        bindTexture(UtilsFX.nodeTexture);
        UtilsFX.renderFacingQuad(xx, yy, zz, 32, 32, 800 + entity.ticksExisted % 16, 0.75f, color, 0.5f, 1, pticks);
        GL11.glDepthMask(true);
        GL11.glAlphaFunc(516, 0.1f);
        GL11.glPopMatrix();
    }
    
    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
        renderEntityAt(entity, d, d1, d2, f, f1);
    }
    
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
