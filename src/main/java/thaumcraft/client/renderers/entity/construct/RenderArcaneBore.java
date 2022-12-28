package thaumcraft.client.renderers.entity.construct;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.models.entity.ModelArcaneBore;
import thaumcraft.common.entities.construct.EntityArcaneBore;
import thaumcraft.common.lib.utils.Utils;


public class RenderArcaneBore extends RenderLiving
{
    private static ResourceLocation rl;
    ResourceLocation beam;
    
    public RenderArcaneBore(RenderManager rm) {
        super(rm, new ModelArcaneBore(), 0.5f);
        beam = new ResourceLocation("thaumcraft", "textures/misc/beam1.png");
    }
    
    protected ResourceLocation getEntityTexture(Entity entity) {
        return RenderArcaneBore.rl;
    }
    
    protected float getSwingProgress(EntityLivingBase livingBase, float partialTickTime) {
        livingBase.renderYawOffset = 0.0f;
        livingBase.prevRenderYawOffset = 0.0f;
        return super.getSwingProgress(livingBase, partialTickTime);
    }
    
    protected void preRenderCallback(EntityLivingBase entitylivingbaseIn, float partialTickTime) {
        entitylivingbaseIn.renderYawOffset = 0.0f;
        entitylivingbaseIn.prevRenderYawOffset = 0.0f;
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
    }
    
    public void doRender(EntityLiving entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        EntityArcaneBore bore = (EntityArcaneBore)entity;
        if (bore.clientDigging && bore.isActive() && bore.validInventory()) {
            Tessellator tessellator = Tessellator.getInstance();
            GL11.glPushMatrix();
            GL11.glDepthMask(false);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 1);
            Minecraft.getMinecraft().renderEngine.bindTexture(UtilsFX.nodeTexture);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.66f);
            int part = entity.ticksExisted % 32;
            Vec3d lv2 = new Vec3d(0.5, 0.075, 0.0);
            Vec3d cv = new Vec3d(x, y + bore.getEyeHeight(), z);
            lv2 = Utils.rotateAroundZ(lv2, bore.rotationPitch / 180.0f * 3.1415927f);
            lv2 = Utils.rotateAroundY(lv2, -((bore.rotationYawHead + 90.0f) / 180.0f * 3.1415927f));
            cv = cv.add(lv2);
            double beamLength = 5.0;
            GL11.glTranslated(cv.x, cv.y, cv.z);
            GL11.glPushMatrix();
            UtilsFX.renderBillboardQuad(0.5, 32, 32, 96 + part, 0.0f, 1.0f, 0.4f, 0.8f, 210);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            float var9 = 1.0f;
            float rot = bore.world.provider.getWorldTime() % 72L * 5L + 5.0f * partialTicks;
            float size = 1.0f;
            float op = 0.4f;
            Minecraft.getMinecraft().renderEngine.bindTexture(beam);
            GL11.glTexParameterf(3553, 10242, 10497.0f);
            GL11.glTexParameterf(3553, 10243, 10497.0f);
            GL11.glDisable(2884);
            float var10 = entity.ticksExisted + partialTicks;
            var10 *= -1.0f;
            float var11 = -var10 * 0.2f - MathHelper.floor(-var10 * 0.1f);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 1);
            GL11.glDepthMask(false);
            float ry = bore.prevRotationYaw + (bore.rotationYaw - bore.prevRotationYaw) * partialTicks;
            float rp = bore.prevRotationPitch + (bore.rotationPitch - bore.prevRotationPitch) * partialTicks;
            GL11.glRotatef(90.0f, -1.0f, 0.0f, 0.0f);
            GL11.glRotatef(180.0f + ry, 0.0f, 0.0f, -1.0f);
            GL11.glRotatef(rp, -1.0f, 0.0f, 0.0f);
            double var12 = -0.15 * size;
            double var13 = 0.15 * size;
            double var44b = 0.0;
            double var17b = 0.0;
            int i = 200;
            int j = i >> 16 & 0xFFFF;
            int k = i & 0xFFFF;
            GL11.glRotatef(rot, 0.0f, 1.0f, 0.0f);
            for (int t = 0; t < 3; ++t) {
                double var14 = beamLength * size * var9;
                double var15 = 0.0;
                double var16 = 1.0;
                double var17 = -1.0f + var11 + t / 3.0f;
                double var18 = beamLength * size * var9 + var17;
                GL11.glRotatef(60.0f, 0.0f, 1.0f, 0.0f);
                tessellator.getBuffer().begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                tessellator.getBuffer().pos(var44b, var14, 0.0).tex(var16, var18).color(0.0f, 1.0f, 0.4f, op).lightmap(j, k).endVertex();
                tessellator.getBuffer().pos(var12, 0.0, 0.0).tex(var16, var17).color(0.0f, 1.0f, 0.4f, op).lightmap(j, k).endVertex();
                tessellator.getBuffer().pos(var13, 0.0, 0.0).tex(var15, var17).color(0.0f, 1.0f, 0.4f, op).lightmap(j, k).endVertex();
                tessellator.getBuffer().pos(var17b, var14, 0.0).tex(var15, var18).color(0.0f, 1.0f, 0.4f, op).lightmap(j, k).endVertex();
                Tessellator.getInstance().draw();
            }
            GL11.glPopMatrix();
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3042);
            GL11.glDepthMask(true);
            GL11.glPopMatrix();
        }
    }
    
    static {
        rl = new ResourceLocation("thaumcraft", "textures/entity/arcanebore.png");
    }
}
