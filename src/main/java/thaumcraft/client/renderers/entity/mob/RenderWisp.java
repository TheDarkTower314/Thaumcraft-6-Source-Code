// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.mob;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.fx.ParticleEngine;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.entities.monster.EntityWisp;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;

public class RenderWisp extends Render
{
    public RenderWisp(final RenderManager rm) {
        super(rm);
        shadowSize = 0.0f;
    }
    
    public void renderEntityAt(final Entity entity, final double x, final double y, final double z, final float fq, final float pticks) {
        if (((EntityLiving)entity).getHealth() <= 0.0f) {
            return;
        }
        final double xx = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pticks;
        final double yy = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pticks;
        final double zz = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pticks;
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
    
    public void doRender(final Entity entity, final double d, final double d1, final double d2, final float f, final float f1) {
        renderEntityAt(entity, d, d1, d2, f, f1);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
