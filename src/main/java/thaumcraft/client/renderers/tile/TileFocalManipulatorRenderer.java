// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import java.util.Random;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApiHelper;
import net.minecraft.client.renderer.GlStateManager;
import thaumcraft.client.lib.UtilsFX;
import java.awt.Color;
import thaumcraft.client.fx.ParticleEngine;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

@SideOnly(Side.CLIENT)
public class TileFocalManipulatorRenderer extends TileEntitySpecialRenderer
{
    EntityItem entityitem;
    
    public TileFocalManipulatorRenderer() {
        this.entityitem = null;
    }
    
    public void renderTileEntityAt(final TileFocalManipulator table, final double par2, final double par4, final double par6, final float par8) {
        if (table.getWorld() != null) {
            final float ticks = Minecraft.getMinecraft().getRenderViewEntity().ticksExisted + par8;
            if (table.getSyncedStackInSlot(0) != null) {
                GL11.glPushMatrix();
                GL11.glTranslatef((float)par2 + 0.5f, (float)par4 + 0.8f, (float)par6 + 0.5f);
                GL11.glRotatef(ticks % 360.0f, 0.0f, 1.0f, 0.0f);
                final ItemStack is = table.getSyncedStackInSlot(0).copy();
                this.entityitem = new EntityItem(table.getWorld(), 0.0, 0.0, 0.0, is);
                this.entityitem.hoverStart = MathHelper.sin(ticks / 14.0f) * 0.2f + 0.2f;
                final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
                rendermanager.renderEntity(this.entityitem, 0.0, 0.0, 0.0, 0.0f, 0.0f, false);
                GL11.glPopMatrix();
            }
            if (table.crystalsSync.getAspects().length > 0) {
                final int q = table.crystalsSync.getAspects().length;
                final float ang = (float)(360 / q);
                for (int a = 0; a < q; ++a) {
                    final float angle = ticks % 720.0f / 2.0f + ang * a;
                    final float bob = MathHelper.sin((ticks + a * 10) / 12.0f) * 0.02f + 0.02f;
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)par2 + 0.5f, (float)par4 + 1.3f, (float)par6 + 0.5f);
                    GL11.glRotatef(angle, 0.0f, 1.0f, 0.0f);
                    GL11.glTranslatef(0.0f, bob, 0.4f);
                    GL11.glRotatef(-angle, 0.0f, 1.0f, 0.0f);
                    this.bindTexture(ParticleEngine.particleTexture);
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 1);
                    GL11.glAlphaFunc(516, 0.003921569f);
                    GL11.glDepthMask(false);
                    final Color c = new Color(table.crystalsSync.getAspects()[a].getColor());
                    final float r = c.getRed() / 255.0f;
                    final float g = c.getGreen() / 255.0f;
                    final float b = c.getBlue() / 255.0f;
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
                    final ItemStack is2 = ThaumcraftApiHelper.makeCrystal(table.crystalsSync.getAspects()[a]);
                    this.entityitem = new EntityItem(table.getWorld(), 0.0, 0.0, 0.0, is2);
                    this.entityitem.hoverStart = 0.0f;
                    this.renderRay(angle, a, bob, r, g, b, ticks);
                    this.renderRay(angle, (a + 1) * 5, bob, r, g, b, ticks);
                    final RenderManager rendermanager2 = Minecraft.getMinecraft().getRenderManager();
                    rendermanager2.renderEntity(this.entityitem, 0.0, 0.0, 0.0, 0.0f, 0.0f, false);
                    GL11.glPopMatrix();
                }
            }
        }
    }
    
    private void renderRay(final float angle, final int num, final float lift, final float r, final float g, final float b, final float ticks) {
        final Random random = new Random(187L + num * num);
        GL11.glPushMatrix();
        final float pan = MathHelper.sin((ticks + num * 10) / 15.0f) * 15.0f;
        final float aparture = MathHelper.sin((ticks + num * 10) / 14.0f) * 2.0f;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder wr = tessellator.getBuffer();
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
    
    public void render(final TileEntity te, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        this.renderTileEntityAt((TileFocalManipulator)te, x, y, z, partialTicks);
    }
}
