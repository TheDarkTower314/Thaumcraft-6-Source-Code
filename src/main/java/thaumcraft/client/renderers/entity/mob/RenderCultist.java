package thaumcraft.client.renderers.entity.mob;
import java.awt.Color;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;


@SideOnly(Side.CLIENT)
public class RenderCultist extends RenderBiped<EntityCultist>
{
    private static ResourceLocation skin;
    private static ResourceLocation fl;
    
    public RenderCultist(RenderManager p_i46127_1_) {
        super(p_i46127_1_, new ModelBiped(), 0.5f);
        addLayer((LayerRenderer)new LayerHeldItem(this));
        LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this) {
            protected void initArmor() {
                modelLeggings = new ModelBiped();
                modelArmor = new ModelBiped();
            }
        };
        addLayer((LayerRenderer)layerbipedarmor);
    }
    
    protected ResourceLocation getEntityTexture(EntityCultist p_110775_1_) {
        return RenderCultist.skin;
    }
    
    public void doRender(EntityCultist entity, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
        GL11.glPushMatrix();
        float bob = 0.0f;
        boolean rit = entity instanceof EntityCultistCleric && ((EntityCultistCleric)entity).getIsRitualist();
        if (rit) {
            int val = new Random(entity.getEntityId()).nextInt(1000);
            float c = entity.ticksExisted + p_76986_9_ + val;
            bob = MathHelper.sin(c / 9.0f) * 0.1f + 0.21f;
            GL11.glTranslated(0.0, bob, 0.0);
        }
        super.doRender(entity, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
        if (rit) {
            GL11.glPushMatrix();
            GL11.glDepthMask(false);
            drawFloatyLine(entity.posX, entity.posY + entity.getEyeHeight() * 1.2f, entity.posZ, entity.getHomePosition().getX() + 0.5, entity.getHomePosition().getY() + 1.5 - bob, entity.getHomePosition().getZ() + 0.5, p_76986_9_, 1114129, -0.03f, Math.min(entity.ticksExisted, 10) / 10.0f, 0.25f);
            GL11.glDepthMask(true);
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
    }
    
    private void drawFloatyLine(double x, double y, double z, double x2, double y2, double z2, float partialTicks, int color, float speed, float distance, float width) {
        Entity player = Minecraft.getMinecraft().getRenderViewEntity();
        double iPX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double iPY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
        double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
        double ePX = x2;
        double ePY = y2;
        double ePZ = z2;
        GL11.glTranslated(-iPX + ePX, -iPY + ePY, -iPZ + ePZ);
        float time = (float)(System.nanoTime() / 30000000L);
        Color co = new Color(color);
        float r = co.getRed() / 255.0f;
        float g = co.getGreen() / 255.0f;
        float b = co.getBlue() / 255.0f;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        Tessellator tessellator = Tessellator.getInstance();
        double ds1x = ePX;
        double ds1y = ePY;
        double ds1z = ePZ;
        double dd1x = x;
        double dd1y = y;
        double dd1z = z;
        double dc1x = (float)(dd1x - ds1x);
        double dc1y = (float)(dd1y - ds1y);
        double dc1z = (float)(dd1z - ds1z);
        bindTexture(RenderCultist.fl);
        tessellator.getBuffer().begin(5, DefaultVertexFormats.POSITION_TEX_COLOR);
        double dx2 = 0.0;
        double dy2 = 0.0;
        double dz2 = 0.0;
        double d3 = x - ePX;
        double d4 = y - ePY;
        double d5 = z - ePZ;
        float dist = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
        float blocks = (float)Math.round(dist);
        float length = blocks * 6.0f;
        float f9 = 0.0f;
        float f10 = 1.0f;
        for (int i = 0; i <= length * distance; ++i) {
            float f11 = i / length;
            float f2a = i * 1.5f / length;
            f2a = Math.min(0.75f, f2a);
            float f12 = 1.0f - Math.abs(i - length / 2.0f) / (length / 2.0f);
            double dx3 = dc1x + MathHelper.sin((float)((z % 16.0 + dist * (1.0f - f11) * 6.0f - time % 32767.0f / 5.0f) / 4.0)) * 0.5f * f12;
            double dy3 = dc1y + MathHelper.sin((float)((x % 16.0 + dist * (1.0f - f11) * 6.0f - time % 32767.0f / 5.0f) / 3.0)) * 0.5f * f12;
            double dz3 = dc1z + MathHelper.sin((float)((y % 16.0 + dist * (1.0f - f11) * 6.0f - time % 32767.0f / 5.0f) / 2.0)) * 0.5f * f12;
            float f13 = (1.0f - f11) * dist - time * speed;
            tessellator.getBuffer().pos(dx3 * f11, dy3 * f11 - width, dz3 * f11).tex(f13, f10).color(r, g, b, 0.8f).endVertex();
            tessellator.getBuffer().pos(dx3 * f11, dy3 * f11 + width, dz3 * f11).tex(f13, f9).color(r, g, b, 0.8f).endVertex();
        }
        tessellator.draw();
        tessellator.getBuffer().begin(5, DefaultVertexFormats.POSITION_TEX_COLOR);
        for (int i = 0; i <= length * distance; ++i) {
            float f11 = i / length;
            float f2a = i * 1.5f / length;
            f2a = Math.min(0.75f, f2a);
            float f12 = 1.0f - Math.abs(i - length / 2.0f) / (length / 2.0f);
            double dx3 = dc1x + MathHelper.sin((float)((z % 16.0 + dist * (1.0f - f11) * 6.0f - time % 32767.0f / 5.0f) / 4.0)) * 0.5f * f12;
            double dy3 = dc1y + MathHelper.sin((float)((x % 16.0 + dist * (1.0f - f11) * 6.0f - time % 32767.0f / 5.0f) / 3.0)) * 0.5f * f12;
            double dz3 = dc1z + MathHelper.sin((float)((y % 16.0 + dist * (1.0f - f11) * 6.0f - time % 32767.0f / 5.0f) / 2.0)) * 0.5f * f12;
            float f13 = (1.0f - f11) * dist - time * speed;
            tessellator.getBuffer().pos(dx3 * f11 - width, dy3 * f11, dz3 * f11).tex(f13, f10).color(r, g, b, 0.8f).endVertex();
            tessellator.getBuffer().pos(dx3 * f11 + width, dy3 * f11, dz3 * f11).tex(f13, f9).color(r, g, b, 0.8f).endVertex();
        }
        tessellator.draw();
        GL11.glDisable(3042);
    }
    
    static {
        skin = new ResourceLocation("thaumcraft", "textures/entity/cultist.png");
        fl = new ResourceLocation("thaumcraft", "textures/misc/wispy.png");
    }
}
