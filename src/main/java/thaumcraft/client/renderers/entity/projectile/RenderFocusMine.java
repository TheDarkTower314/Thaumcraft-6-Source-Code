package thaumcraft.client.renderers.entity.projectile;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.entity.ModelGrappler;
import thaumcraft.common.entities.projectile.EntityFocusMine;


public class RenderFocusMine extends Render
{
    ResourceLocation beam;
    private ModelGrappler model;
    
    public RenderFocusMine(RenderManager rm) {
        super(rm);
        beam = new ResourceLocation("thaumcraft", "textures/entity/mine.png");
        shadowSize = 0.0f;
        model = new ModelGrappler();
    }
    
    public void renderEntityAt(Entity entity, double x, double y, double z, float fq, float pticks) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glTranslated(x, y, z);
        EntityFocusMine mine = (EntityFocusMine)entity;
        float f = (mine.counter + pticks) % 8.0f / 8.0f;
        int i = 61680;
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
        GL11.glColor4f(1.0f, 1.0f - f, 1.0f - f, 1.0f);
        bindTexture(beam);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * pticks - 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * pticks, 0.0f, 0.0f, 1.0f);
        model.render();
        GL11.glDisable(3042);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
        renderEntityAt(entity, d, d1, d2, f, f1);
    }
    
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
