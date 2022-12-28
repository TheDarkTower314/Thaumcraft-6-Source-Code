package thaumcraft.client.renderers.tile;
import java.awt.Color;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;


@SideOnly(Side.CLIENT)
public class TileFocalManipulatorRenderer extends TileEntitySpecialRenderer
{
    EntityItem entityitem;
    
    public TileFocalManipulatorRenderer() {
        entityitem = null;
    }
    
    public void renderTileEntityAt(TileFocalManipulator table, double par2, double par4, double par6, float par8) {
        if (table.getWorld() != null) {
            float ticks = Minecraft.getMinecraft().getRenderViewEntity().ticksExisted + par8;
            if (table.getSyncedStackInSlot(0) != null) {
                GL11.glPushMatrix();
                GL11.glTranslatef((float)par2 + 0.5f, (float)par4 + 0.8f, (float)par6 + 0.5f);
                GL11.glRotatef(ticks % 360.0f, 0.0f, 1.0f, 0.0f);
                ItemStack is = table.getSyncedStackInSlot(0).copy();
                entityitem = new EntityItem(table.getWorld(), 0.0, 0.0, 0.0, is);
                entityitem.hoverStart = MathHelper.sin(ticks / 14.0f) * 0.2f + 0.2f;
                RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
                rendermanager.renderEntity(entityitem, 0.0, 0.0, 0.0, 0.0f, 0.0f, false);
                GL11.glPopMatrix();
            }
            if (table.crystalsSync.getAspects().length > 0) {
                int q = table.crystalsSync.getAspects().length;
                float ang = (float)(360 / q);
                for (int a = 0; a < q; ++a) {
                    float angle = ticks % 720.0f / 2.0f + ang * a;
                    float bob = MathHelper.sin((ticks + a * 10) / 12.0f) * 0.02f + 0.02f;
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)par2 + 0.5f, (float)par4 + 1.3f, (float)par6 + 0.5f);
                    GL11.glRotatef(angle, 0.0f, 1.0f, 0.0f);
                    GL11.glTranslatef(0.0f, bob, 0.4f);
                    GL11.glRotatef(-angle, 0.0f, 1.0f, 0.0f);
                    bindTexture(ParticleEngine.particleTexture);
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 1);
                    GL11.glAlphaFunc(516, 0.003921569f);
                    GL11.glDepthMask(false);
                    Color c = new Color(table.crystalsSync.getAspects()[a].getColor());
                    float r = c.getRed() / 255.0f;
                    float g = c.getGreen() / 255.0f;
                    float b = c.getBlue() / 255.0f;
                    GL11.glColor4f(r, g, b, 0.66f);
                    UtilsFX.renderBillboardQuad(0.17499999701976776, 64, 64, 320 + Minecraft.getMinecraft().getRenderViewEntity().ticksExisted % 16);
                    GL11.glDepthMask(true);
                    GL11.glBlendFunc(770, 771);
                    GL11.glDisable(3042);
                    GlStateManager.alphaFunc(516, 0.1f);
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)par2 + 0.5f, (float)par4 + 1.05f, (float)par6 + 0.5f);
                    GL11.glRotatef(angle, 0.0f, 1.0f, 0.0f);
                    GL11.glTranslatef(0.0f, bob, 0.4f);
                    GL11.glScaled(0.5, 0.5, 0.5);
                    ItemStack is2 = ThaumcraftApiHelper.makeCrystal(table.crystalsSync.getAspects()[a]);
                    entityitem = new EntityItem(table.getWorld(), 0.0, 0.0, 0.0, is2);
                    entityitem.hoverStart = 0.0f;
                    renderRay(angle, a, bob, r, g, b, ticks);
                    renderRay(angle, (a + 1) * 5, bob, r, g, b, ticks);
                    RenderManager rendermanager2 = Minecraft.getMinecraft().getRenderManager();
                    rendermanager2.renderEntity(entityitem, 0.0, 0.0, 0.0, 0.0f, 0.0f, false);
                    GL11.glPopMatrix();
                }
            }
        }
    }
    
    private void renderRay(float angle, int num, float lift, float r, float g, float b, float ticks) {
        Random random = new Random(187L + num * num);
        GL11.glPushMatrix();
        float pan = MathHelper.sin((ticks + num * 10) / 15.0f) * 15.0f;
        float aparture = MathHelper.sin((ticks + num * 10) / 14.0f) * 2.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder wr = tessellator.getBuffer();
        RenderHelper.disableStandardItemLighting();
        GL11.glTranslatef(0.0f, 0.475f + lift, 0.0f);
        GL11.glDisable(3553);
        GL11.glShadeModel(7425);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        GL11.glDisable(3008);
        GL11.glEnable(2884);
        GL11.glDepthMask(false);
        GL11.glPushMatrix();
        GL11.glRotatef(90.0f, -1.0f, 0.0f, 0.0f);
        GL11.glRotatef(angle, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(random.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotated(pan, 1.0, 0.0, 0.0);
        wr.begin(6, DefaultVertexFormats.POSITION_COLOR);
        float fa = random.nextFloat() * 20.0f + 10.0f;
        float f4 = random.nextFloat() * 4.0f + 6.0f + aparture;
        fa /= 30.0f / (Math.min(ticks, 10.0f) / 10.0f);
        f4 /= 30.0f / (Math.min(ticks, 10.0f) / 10.0f);
        wr.pos(0.0, 0.0, 0.0).color(r, g, b, 0.66f).endVertex();
        wr.pos(-0.8 * f4, fa, -0.5f * f4).color(r, g, b, 0.0f).endVertex();
        wr.pos(0.8 * f4, fa, -0.5f * f4).color(r, g, b, 0.0f).endVertex();
        wr.pos(0.0, fa, 1.0f * f4).color(r, g, b, 0.0f).endVertex();
        wr.pos(-0.8 * f4, fa, -0.5f * f4).color(r, g, b, 0.0f).endVertex();
        tessellator.draw();
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glDisable(2884);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
        GL11.glShadeModel(7424);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
        GL11.glEnable(3008);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
    }
    
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        renderTileEntityAt((TileFocalManipulator)te, x, y, z, partialTicks);
    }
}
