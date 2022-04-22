// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.mob;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.Tessellator;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import java.util.Random;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import net.minecraft.client.renderer.entity.RenderBiped;

@SideOnly(Side.CLIENT)
public class RenderCultist extends RenderBiped<EntityCultist>
{
    private static final ResourceLocation skin;
    private static final ResourceLocation fl;
    
    public RenderCultist(final RenderManager p_i46127_1_) {
        super(p_i46127_1_, new ModelBiped(), 0.5f);
        addLayer((LayerRenderer)new LayerHeldItem(this));
        final LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this) {
            protected void initArmor() {
                modelLeggings = new ModelBiped();
                modelArmor = new ModelBiped();
            }
        };
        addLayer((LayerRenderer)layerbipedarmor);
    }
    
    protected ResourceLocation getEntityTexture(final EntityCultist p_110775_1_) {
        return RenderCultist.skin;
    }
    
    public void doRender(final EntityCultist entity, final double p_76986_2_, final double p_76986_4_, final double p_76986_6_, final float p_76986_8_, final float p_76986_9_) {
        GL11.glPushMatrix();
        float bob = 0.0f;
        final boolean rit = entity instanceof EntityCultistCleric && ((EntityCultistCleric)entity).getIsRitualist();
        if (rit) {
            final int val = new Random(entity.getEntityId()).nextInt(1000);
            final float c = entity.ticksExisted + p_76986_9_ + val;
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
    
    private void drawFloatyLine(final double x, final double y, final double z, final double x2, final double y2, final double z2, final float partialTicks, final int color, final float speed, final float distance, final float width) {
        final Entity player = Minecraft.getMinecraft().getRenderViewEntity();
        final double iPX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        final double iPY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
        final double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
        final double ePX = x2;
        final double ePY = y2;
        final double ePZ = z2;
        GL11.glTranslated(-iPX + ePX, -iPY + ePY, -iPZ + ePZ);
        final float time = (float)(System.nanoTime() / 30000000L);
        final Color co = new Color(color);
        final float r = co.getRed() / 255.0f;
        final float g = co.getGreen() / 255.0f;
        final float b = co.getBlue() / 255.0f;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        final Tessellator tessellator = Tessellator.getInstance();
        final double ds1x = ePX;
        final double ds1y = ePY;
        final double ds1z = ePZ;
        final double dd1x = x;
        final double dd1y = y;
        final double dd1z = z;
        final double dc1x = (float)(dd1x - ds1x);
        final double dc1y = (float)(dd1y - ds1y);
        final double dc1z = (float)(dd1z - ds1z);
        bindTexture(RenderCultist.fl);
        tessellator.getBuffer().begin(5, DefaultVertexFormats.POSITION_TEX_COLOR);
        final double dx2 = 0.0;
        final double dy2 = 0.0;
        final double dz2 = 0.0;
        final double d3 = x - ePX;
        final double d4 = y - ePY;
        final double d5 = z - ePZ;
        final float dist = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
        final float blocks = (float)Math.round(dist);
        final float length = blocks * 6.0f;
        final float f9 = 0.0f;
        final float f10 = 1.0f;
        for (int i = 0; i <= length * distance; ++i) {
            final float f11 = i / length;
            float f2a = i * 1.5f / length;
            f2a = Math.min(0.75f, f2a);
            final float f12 = 1.0f - Math.abs(i - length / 2.0f) / (length / 2.0f);
            final double dx3 = dc1x + MathHelper.sin((float)((z % 16.0 + dist * (1.0f - f11) * 6.0f - time % 32767.0f / 5.0f) / 4.0)) * 0.5f * f12;
            final double dy3 = dc1y + MathHelper.sin((float)((x % 16.0 + dist * (1.0f - f11) * 6.0f - time % 32767.0f / 5.0f) / 3.0)) * 0.5f * f12;
            final double dz3 = dc1z + MathHelper.sin((float)((y % 16.0 + dist * (1.0f - f11) * 6.0f - time % 32767.0f / 5.0f) / 2.0)) * 0.5f * f12;
            final float f13 = (1.0f - f11) * dist - time * speed;
            tessellator.getBuffer().pos(dx3 * f11, dy3 * f11 - width, dz3 * f11).tex(f13, f10).color(r, g, b, 0.8f).endVertex();
            tessellator.getBuffer().pos(dx3 * f11, dy3 * f11 + width, dz3 * f11).tex(f13, f9).color(r, g, b, 0.8f).endVertex();
        }
        tessellator.draw();
        tessellator.getBuffer().begin(5, DefaultVertexFormats.POSITION_TEX_COLOR);
        for (int i = 0; i <= length * distance; ++i) {
            final float f11 = i / length;
            float f2a = i * 1.5f / length;
            f2a = Math.min(0.75f, f2a);
            final float f12 = 1.0f - Math.abs(i - length / 2.0f) / (length / 2.0f);
            final double dx3 = dc1x + MathHelper.sin((float)((z % 16.0 + dist * (1.0f - f11) * 6.0f - time % 32767.0f / 5.0f) / 4.0)) * 0.5f * f12;
            final double dy3 = dc1y + MathHelper.sin((float)((x % 16.0 + dist * (1.0f - f11) * 6.0f - time % 32767.0f / 5.0f) / 3.0)) * 0.5f * f12;
            final double dz3 = dc1z + MathHelper.sin((float)((y % 16.0 + dist * (1.0f - f11) * 6.0f - time % 32767.0f / 5.0f) / 2.0)) * 0.5f * f12;
            final float f13 = (1.0f - f11) * dist - time * speed;
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
