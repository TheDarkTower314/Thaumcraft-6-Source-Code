// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.projectile;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import java.util.Random;
import net.minecraft.client.renderer.entity.Render;

public class RenderFocusCloud extends Render
{
    private Random random;
    
    public RenderFocusCloud(final RenderManager rm) {
        super(rm);
        random = new Random();
        shadowSize = 0.0f;
    }
    
    public void renderEntityAt(final Entity entity, final double x, final double y, final double z, final float fq, final float pticks) {
    }
    
    public void doRender(final Entity entity, final double d, final double d1, final double d2, final float f, final float f1) {
        renderEntityAt(entity, d, d1, d2, f, f1);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
