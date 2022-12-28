package thaumcraft.client.renderers.entity.mob;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.renderers.models.entity.ModelTaintSeed;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;


@SideOnly(Side.CLIENT)
public class RenderTaintSeed extends RenderLiving<EntityTaintSeed>
{
    private static ResourceLocation rl;
    
    public RenderTaintSeed(RenderManager rm) {
        super(rm, new ModelTaintSeed(), 0.4f);
    }
    
    public RenderTaintSeed(RenderManager rm, ModelBase modelbase, float sz) {
        super(rm, modelbase, sz);
    }
    
    protected ResourceLocation getEntityTexture(EntityTaintSeed entity) {
        return RenderTaintSeed.rl;
    }
    
    public void doRender(EntityTaintSeed entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre(entity, this, x, y, z))) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        mainModel.swingProgress = getSwingProgress(entity, partialTicks);
        boolean shouldSit = entity.isRiding() && entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit();
        mainModel.isRiding = shouldSit;
        mainModel.isChild = entity.isChild();
        try {
            GlStateManager.pushMatrix();
            float f = 0.0f;
            float f2 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            renderLivingAt(entity, x, y, z);
            float f3 = handleRotationFloat(entity, partialTicks);
            applyRotations(entity, f3, f, partialTicks);
            float f4 = prepareScale(entity, partialTicks);
            float f5 = 0.0f;
            float f6 = 0.0f;
            f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
            f6 = entity.limbSwing - entity.limbSwingAmount * (1.0f - partialTicks);
            if (f5 > 1.0f) {
                f5 = 1.0f;
            }
            GlStateManager.enableAlpha();
            mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
            mainModel.setRotationAngles(f6, f5, f3, f, f2, f4, entity);
            if (renderOutlines) {
                boolean flag1 = setScoreTeamColor(entity);
                GlStateManager.enableColorMaterial();
                GlStateManager.enableOutlineMode(getTeamColor(entity));
                if (!renderMarker) {
                    renderModel(entity, f6, f5, f3, f, f2, f4);
                }
                renderLayers(entity, f6, f5, partialTicks, f3, f, f2, f4);
                GlStateManager.disableOutlineMode();
                GlStateManager.disableColorMaterial();
                if (flag1) {
                    unsetScoreTeamColor();
                }
            }
            else {
                boolean flag2 = setDoRenderBrightness(entity, partialTicks);
                renderModel(entity, f6, f5, f3, f, f2, f4);
                if (flag2) {
                    unsetBrightness();
                }
                GlStateManager.depthMask(true);
                renderLayers(entity, f6, f5, partialTicks, f3, f, f2, f4);
            }
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
        }
        catch (Exception ex) {}
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        if (!renderOutlines) {
            renderName(entity, x, y, z);
        }
        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(entity, this, x, y, z));
    }
    
    static {
        rl = new ResourceLocation("thaumcraft", "textures/entity/taintseed.png");
    }
}
