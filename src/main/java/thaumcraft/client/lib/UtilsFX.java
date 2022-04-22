// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.lib;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.OpenGlHelper;
import java.util.Iterator;
import java.util.ArrayList;
import org.lwjgl.input.Mouse;
import net.minecraft.client.gui.ScaledResolution;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import thaumcraft.client.fx.ParticleEngine;
import net.minecraft.util.EnumFacing;
import thaumcraft.common.config.ModConfig;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import java.awt.Color;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import java.text.DecimalFormat;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

public class UtilsFX
{
    public static final ResourceLocation nodeTexture;
    public static final VertexFormat VERTEXFORMAT_POS_TEX_CO_LM_NO;
    public static final String[] colorNames;
    public static final String[] colorCodes;
    public static final int[] colors;
    public static float sysPartialTicks;
    static DecimalFormat myFormatter;
    public static boolean hideStackOverlay;
    
    public static void renderFacingQuad(final double px, final double py, final double pz, final int gridX, final int gridY, final int frame, final float scale, final int color, final float alpha, final int blend, final float partialTicks) {
        if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer) {
            final Tessellator tessellator = Tessellator.getInstance();
            final BufferBuilder wr = tessellator.getBuffer();
            final float arX = ActiveRenderInfo.getRotationX();
            final float arZ = ActiveRenderInfo.getRotationZ();
            final float arYZ = ActiveRenderInfo.getRotationYZ();
            final float arXY = ActiveRenderInfo.getRotationXY();
            final float arXZ = ActiveRenderInfo.getRotationXZ();
            final EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().getRenderViewEntity();
            final double iPX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
            final double iPY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
            final double iPZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
            GlStateManager.pushMatrix();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, blend);
            GlStateManager.alphaFunc(516, 0.003921569f);
            GlStateManager.depthMask(false);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glTranslated(-iPX, -iPY, -iPZ);
            final Vec3d v1 = new Vec3d(-arX * scale - arYZ * scale, -arXZ * scale, -arZ * scale - arXY * scale);
            final Vec3d v2 = new Vec3d(-arX * scale + arYZ * scale, arXZ * scale, -arZ * scale + arXY * scale);
            final Vec3d v3 = new Vec3d(arX * scale + arYZ * scale, arXZ * scale, arZ * scale + arXY * scale);
            final Vec3d v4 = new Vec3d(arX * scale - arYZ * scale, -arXZ * scale, arZ * scale - arXY * scale);
            final int xm = frame % gridX;
            final int ym = frame / gridY;
            final float f1 = xm / (float)gridX;
            final float f2 = f1 + 1.0f / gridX;
            final float f3 = ym / (float)gridY;
            final float f4 = f3 + 1.0f / gridY;
            final TexturedQuadTC quad = new TexturedQuadTC(new PositionTextureVertex[] { new PositionTextureVertex((float)px + (float)v1.x, (float)py + (float)v1.y, (float)pz + (float)v1.z, f2, f4), new PositionTextureVertex((float)px + (float)v2.x, (float)py + (float)v2.y, (float)pz + (float)v2.z, f2, f3), new PositionTextureVertex((float)px + (float)v3.x, (float)py + (float)v3.y, (float)pz + (float)v3.z, f1, f3), new PositionTextureVertex((float)px + (float)v4.x, (float)py + (float)v4.y, (float)pz + (float)v4.z, f1, f4) });
            quad.draw(tessellator.getBuffer(), 1.0f, 220, color, alpha);
            GlStateManager.depthMask(true);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3042);
            GlStateManager.alphaFunc(516, 0.1f);
            GlStateManager.popMatrix();
        }
    }
    
    public static void drawTexturedQuad(final float par1, final float par2, final float par3, final float par4, final float par5, final float par6, final double zLevel) {
        final float var7 = 0.00390625f;
        final float var8 = 0.00390625f;
        final Tessellator var9 = Tessellator.getInstance();
        var9.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
        var9.getBuffer().pos(par1 + 0.0f, par2 + par6, zLevel).tex((par3 + 0.0f) * var7, (par4 + par6) * var8).endVertex();
        var9.getBuffer().pos(par1 + par5, par2 + par6, zLevel).tex((par3 + par5) * var7, (par4 + par6) * var8).endVertex();
        var9.getBuffer().pos(par1 + par5, par2 + 0.0f, zLevel).tex((par3 + par5) * var7, (par4 + 0.0f) * var8).endVertex();
        var9.getBuffer().pos(par1 + 0.0f, par2 + 0.0f, zLevel).tex((par3 + 0.0f) * var7, (par4 + 0.0f) * var8).endVertex();
        var9.draw();
    }
    
    public static void drawTexturedQuadF(final float par1, final float par2, final float par3, final float par4, final float par5, final float par6, final double zLevel) {
        final float d = 0.0625f;
        final Tessellator var9 = Tessellator.getInstance();
        var9.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
        var9.getBuffer().pos(par1 + 0.0f, par2 + 16.0f, zLevel).tex((par3 + 0.0f) * d, (par4 + par6) * d).endVertex();
        var9.getBuffer().pos(par1 + 16.0f, par2 + 16.0f, zLevel).tex((par3 + par5) * d, (par4 + par6) * d).endVertex();
        var9.getBuffer().pos(par1 + 16.0f, par2 + 0.0f, zLevel).tex((par3 + par5) * d, (par4 + 0.0f) * d).endVertex();
        var9.getBuffer().pos(par1 + 0.0f, par2 + 0.0f, zLevel).tex((par3 + 0.0f) * d, (par4 + 0.0f) * d).endVertex();
        var9.draw();
    }
    
    public static void drawTexturedQuadFull(final float par1, final float par2, final double zLevel) {
        final Tessellator var9 = Tessellator.getInstance();
        var9.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
        var9.getBuffer().pos(par1 + 0.0f, par2 + 16.0f, zLevel).tex(0.0, 1.0).endVertex();
        var9.getBuffer().pos(par1 + 16.0f, par2 + 16.0f, zLevel).tex(1.0, 1.0).endVertex();
        var9.getBuffer().pos(par1 + 16.0f, par2 + 0.0f, zLevel).tex(1.0, 0.0).endVertex();
        var9.getBuffer().pos(par1 + 0.0f, par2 + 0.0f, zLevel).tex(0.0, 0.0).endVertex();
        var9.draw();
    }
    
    public static void renderItemInGUI(final int x, final int y, final int z, final ItemStack stack) {
        final Minecraft mc = Minecraft.getMinecraft();
        try {
            GlStateManager.pushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            mc.getRenderItem().zLevel = (float)z;
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
            mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
            mc.getRenderItem().zLevel = 0.0f;
            GlStateManager.disableLighting();
            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
        }
        catch (final Exception ex) {}
    }
    
    public static void renderQuadCentered(final ResourceLocation texture, final float scale, final float red, final float green, final float blue, final int brightness, final int blend, final float opacity) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        renderQuadCentered(1, 1, 0, scale, red, green, blue, brightness, blend, opacity);
    }
    
    public static void renderQuadCentered(final ResourceLocation texture, final int gridX, final int gridY, final int frame, final float scale, final float red, final float green, final float blue, final int brightness, final int blend, final float opacity) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        renderQuadCentered(gridX, gridY, frame, scale, red, green, blue, brightness, blend, opacity);
    }
    
    public static void renderQuadCentered() {
        renderQuadCentered(1, 1, 0, 1.0f, 1.0f, 1.0f, 1.0f, 200, 771, 1.0f);
    }
    
    public static void renderQuadCentered(final int gridX, final int gridY, final int frame, final float scale, final float red, final float green, final float blue, final int brightness, final int blend, final float opacity) {
        final Tessellator tessellator = Tessellator.getInstance();
        final boolean blendon = GL11.glIsEnabled(3042);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, blend);
        final int xm = frame % gridX;
        final int ym = frame / gridY;
        final float f1 = xm / (float)gridX;
        final float f2 = f1 + 1.0f / gridX;
        final float f3 = ym / (float)gridY;
        final float f4 = f3 + 1.0f / gridY;
        final Color c = new Color(red, green, blue);
        final TexturedQuadTC quad = new TexturedQuadTC(new PositionTextureVertex[] { new PositionTextureVertex(-0.5f, 0.5f, 0.0f, f2, f4), new PositionTextureVertex(0.5f, 0.5f, 0.0f, f2, f3), new PositionTextureVertex(0.5f, -0.5f, 0.0f, f1, f3), new PositionTextureVertex(-0.5f, -0.5f, 0.0f, f1, f4) });
        quad.draw(tessellator.getBuffer(), scale, brightness, c.getRGB(), opacity);
        GL11.glBlendFunc(770, 771);
        if (!blendon) {
            GL11.glDisable(3042);
        }
    }
    
    public static void renderQuadFromIcon(final TextureAtlasSprite icon, final float scale, final float red, final float green, final float blue, final int brightness, final int blend, final float opacity) {
        final boolean blendon = GL11.glIsEnabled(3042);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        final Tessellator tessellator = Tessellator.getInstance();
        final float f1 = icon.getMaxU();
        final float f2 = icon.getMinV();
        final float f3 = icon.getMinU();
        final float f4 = icon.getMaxV();
        GL11.glScalef(scale, scale, scale);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, blend);
        GL11.glColor4f(red, green, blue, opacity);
        if (brightness > -1) {
            tessellator.getBuffer().begin(7, UtilsFX.VERTEXFORMAT_POS_TEX_CO_LM_NO);
        }
        else {
            tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        }
        final int j = brightness >> 16 & 0xFFFF;
        final int k = brightness & 0xFFFF;
        tessellator.getBuffer().pos(0.0, 0.0, 0.0).tex(f1, f4).color(red, green, blue, opacity);
        if (brightness > -1) {
            tessellator.getBuffer().lightmap(j, k);
        }
        tessellator.getBuffer().normal(0.0f, 0.0f, 1.0f);
        tessellator.getBuffer().endVertex();
        tessellator.getBuffer().pos(1.0, 0.0, 0.0).tex(f3, f4).color(red, green, blue, opacity);
        if (brightness > -1) {
            tessellator.getBuffer().lightmap(j, k);
        }
        tessellator.getBuffer().normal(0.0f, 0.0f, 1.0f);
        tessellator.getBuffer().endVertex();
        tessellator.getBuffer().pos(1.0, 1.0, 0.0).tex(f3, f2).color(red, green, blue, opacity);
        if (brightness > -1) {
            tessellator.getBuffer().lightmap(j, k);
        }
        tessellator.getBuffer().normal(0.0f, 0.0f, 1.0f);
        tessellator.getBuffer().endVertex();
        tessellator.getBuffer().pos(0.0, 1.0, 0.0).tex(f1, f2).color(red, green, blue, opacity);
        if (brightness > -1) {
            tessellator.getBuffer().lightmap(j, k);
        }
        tessellator.getBuffer().normal(0.0f, 0.0f, 1.0f);
        tessellator.getBuffer().endVertex();
        tessellator.draw();
        GlStateManager.blendFunc(770, 771);
        if (!blendon) {
            GL11.glDisable(3042);
        }
    }
    
    public static void drawTag(final int x, final int y, final Aspect aspect, final float amount, final int bonus, final double z, final int blend, final float alpha) {
        drawTag(x, y, aspect, amount, bonus, z, blend, alpha, false);
    }
    
    public static void drawTag(final int x, final int y, final Aspect aspect, final float amt, final int bonus, final double z) {
        drawTag(x, y, aspect, amt, bonus, z, 771, 1.0f, false);
    }
    
    public static void drawTag(final int x, final int y, final Aspect aspect) {
        drawTag(x, y, aspect, 0.0f, 0, 0.0, 771, 1.0f, true);
    }
    
    public static void drawTag(final int x, final int y, final Aspect aspect, final float amount, final int bonus, final double z, final int blend, final float alpha, final boolean bw) {
        drawTag(x, (double)y, aspect, amount, bonus, z, blend, alpha, bw);
    }
    
    public static void drawTag(final double x, final double y, final Aspect aspect, final float amount, final int bonus, final double z, final int blend, final float alpha, final boolean bw) {
        if (aspect == null) {
            return;
        }
        final boolean blendon = GL11.glIsEnabled(3042);
        final Minecraft mc = Minecraft.getMinecraft();
        final boolean isLightingEnabled = GL11.glIsEnabled(2896);
        final Color color = new Color(aspect.getColor());
        GL11.glPushMatrix();
        GL11.glDisable(2896);
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, blend);
        GL11.glPushMatrix();
        mc.renderEngine.bindTexture(aspect.getImage());
        if (!bw) {
            GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha);
        }
        else {
            GL11.glColor4f(0.1f, 0.1f, 0.1f, alpha * 0.8f);
        }
        final Tessellator var9 = Tessellator.getInstance();
        var9.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        if (!bw) {
            var9.getBuffer().pos(x + 0.0, y + 16.0, z).tex(0.0, 1.0).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
            var9.getBuffer().pos(x + 16.0, y + 16.0, z).tex(1.0, 1.0).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
            var9.getBuffer().pos(x + 16.0, y + 0.0, z).tex(1.0, 0.0).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
            var9.getBuffer().pos(x + 0.0, y + 0.0, z).tex(0.0, 0.0).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
        }
        else {
            var9.getBuffer().pos(x + 0.0, y + 16.0, z).tex(0.0, 1.0).color(0.1f, 0.1f, 0.1f, alpha * 0.8f).endVertex();
            var9.getBuffer().pos(x + 16.0, y + 16.0, z).tex(1.0, 1.0).color(0.1f, 0.1f, 0.1f, alpha * 0.8f).endVertex();
            var9.getBuffer().pos(x + 16.0, y + 0.0, z).tex(1.0, 0.0).color(0.1f, 0.1f, 0.1f, alpha * 0.8f).endVertex();
            var9.getBuffer().pos(x + 0.0, y + 0.0, z).tex(0.0, 0.0).color(0.1f, 0.1f, 0.1f, alpha * 0.8f).endVertex();
        }
        var9.draw();
        GL11.glPopMatrix();
        if (amount > 0.0f) {
            GL11.glPushMatrix();
            float q = 0.5f;
            if (!ModConfig.CONFIG_GRAPHICS.largeTagText) {
                GL11.glScalef(0.5f, 0.5f, 0.5f);
                q = 1.0f;
            }
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            final String am = UtilsFX.myFormatter.format(amount);
            final int sw = mc.fontRenderer.getStringWidth(am);
            for (final EnumFacing e : EnumFacing.HORIZONTALS) {
                mc.fontRenderer.drawString(am, (32 - sw + (int)x * 2) * q + e.getFrontOffsetX(), (32 - mc.fontRenderer.FONT_HEIGHT + (int)y * 2) * q + e.getFrontOffsetZ(), 0, false);
            }
            mc.fontRenderer.drawString(am, (32 - sw + (int)x * 2) * q, (32 - mc.fontRenderer.FONT_HEIGHT + (int)y * 2) * q, 16777215, false);
            GL11.glPopMatrix();
        }
        if (bonus > 0) {
            GL11.glPushMatrix();
            mc.renderEngine.bindTexture(ParticleEngine.particleTexture);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            final int px = 16 * (mc.player.ticksExisted % 16);
            drawTexturedQuad((float)((int)x - 4), (float)((int)y - 4), (float)px, 80.0f, 16.0f, 16.0f, z);
            if (bonus > 1) {
                float q2 = 0.5f;
                if (!ModConfig.CONFIG_GRAPHICS.largeTagText) {
                    GL11.glScalef(0.5f, 0.5f, 0.5f);
                    q2 = 1.0f;
                }
                final String am2 = "" + bonus;
                final int sw2 = mc.fontRenderer.getStringWidth(am2) / 2;
                GL11.glTranslated(0.0, 0.0, -1.0);
                mc.fontRenderer.drawStringWithShadow(am2, (8 - sw2 + (int)x * 2) * q2, (15 - mc.fontRenderer.FONT_HEIGHT + (int)y * 2) * q2, 16777215);
            }
            GL11.glPopMatrix();
        }
        GlStateManager.blendFunc(770, 771);
        if (!blendon) {
            GL11.glDisable(3042);
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glAlphaFunc(516, 0.1f);
        if (isLightingEnabled) {
            GL11.glEnable(2896);
        }
        GL11.glPopMatrix();
    }
    
    public static void drawCustomTooltip(final GuiScreen gui, final FontRenderer fr, final List<String> textList, final int x, final int y, final int subTipColor) {
        drawCustomTooltip(gui, fr, textList, x, y, subTipColor, false);
    }
    
    public static void drawCustomTooltip(final GuiScreen gui, final FontRenderer fr, List<String> textList, final int x, final int y, final int subTipColor, final boolean ignoremouse) {
        if (!textList.isEmpty()) {
            final Minecraft mc = Minecraft.getMinecraft();
            final ScaledResolution scaledresolution = new ScaledResolution(mc);
            final int sf = scaledresolution.getScaleFactor();
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int max = 240;
            final int mx = Mouse.getEventX();
            boolean flip = false;
            if (!ignoremouse && (max + 24) * sf + mx > mc.displayWidth) {
                max = (mc.displayWidth - mx) / sf - 24;
                if (max < 120) {
                    max = 240;
                    flip = true;
                }
            }
            int widestLineWidth = 0;
            final Iterator<String> textLineEntry = textList.iterator();
            boolean b = false;
            while (textLineEntry.hasNext()) {
                final String textLine = textLineEntry.next();
                if (fr.getStringWidth(textLine) > max) {
                    b = true;
                    break;
                }
            }
            if (b) {
                final List tl = new ArrayList();
                for (final Object o : textList) {
                    final String textLine2 = (String)o;
                    final List tl2 = fr.listFormattedStringToWidth(textLine2, textLine2.startsWith("@@") ? (max * 2) : max);
                    for (final Object o2 : tl2) {
                        String textLine3 = ((String)o2).trim();
                        if (textLine2.startsWith("@@")) {
                            textLine3 = "@@" + textLine3;
                        }
                        tl.add(textLine3);
                    }
                }
                textList = tl;
            }
            final Iterator<String> textLines = textList.iterator();
            int totalHeight = -2;
            while (textLines.hasNext()) {
                final String textLine4 = textLines.next();
                int lineWidth = fr.getStringWidth(textLine4);
                if (textLine4.startsWith("@@") && !fr.getUnicodeFlag()) {
                    lineWidth /= 2;
                }
                if (lineWidth > widestLineWidth) {
                    widestLineWidth = lineWidth;
                }
                totalHeight += ((textLine4.startsWith("@@") && !fr.getUnicodeFlag()) ? 7 : 10);
            }
            int sX = x + 12;
            int sY = y - 12;
            if (textList.size() > 1) {
                totalHeight += 2;
            }
            if (sY + totalHeight > scaledresolution.getScaledHeight()) {
                sY = scaledresolution.getScaledHeight() - totalHeight - 5;
            }
            if (flip) {
                sX -= widestLineWidth + 24;
            }
            Minecraft.getMinecraft().getRenderItem().zLevel = 300.0f;
            final int var10 = -267386864;
            drawGradientRect(sX - 3, sY - 4, sX + widestLineWidth + 3, sY - 3, var10, var10);
            drawGradientRect(sX - 3, sY + totalHeight + 3, sX + widestLineWidth + 3, sY + totalHeight + 4, var10, var10);
            drawGradientRect(sX - 3, sY - 3, sX + widestLineWidth + 3, sY + totalHeight + 3, var10, var10);
            drawGradientRect(sX - 4, sY - 3, sX - 3, sY + totalHeight + 3, var10, var10);
            drawGradientRect(sX + widestLineWidth + 3, sY - 3, sX + widestLineWidth + 4, sY + totalHeight + 3, var10, var10);
            final int var11 = 1347420415;
            final int var12 = (var11 & 0xFEFEFE) >> 1 | (var11 & 0xFF000000);
            drawGradientRect(sX - 3, sY - 3 + 1, sX - 3 + 1, sY + totalHeight + 3 - 1, var11, var12);
            drawGradientRect(sX + widestLineWidth + 2, sY - 3 + 1, sX + widestLineWidth + 3, sY + totalHeight + 3 - 1, var11, var12);
            drawGradientRect(sX - 3, sY - 3, sX + widestLineWidth + 3, sY - 3 + 1, var11, var11);
            drawGradientRect(sX - 3, sY + totalHeight + 2, sX + widestLineWidth + 3, sY + totalHeight + 3, var12, var12);
            for (int i = 0; i < textList.size(); ++i) {
                GL11.glPushMatrix();
                GL11.glTranslatef((float)sX, (float)sY, 0.0f);
                String tl3 = textList.get(i);
                boolean shift = false;
                GL11.glPushMatrix();
                if (tl3.startsWith("@@") && !fr.getUnicodeFlag()) {
                    sY += 7;
                    GL11.glScalef(0.5f, 0.5f, 1.0f);
                    shift = true;
                }
                else {
                    sY += 10;
                }
                tl3 = tl3.replaceAll("@@", "");
                if (subTipColor != -99) {
                    if (i == 0) {
                        tl3 = "§" + Integer.toHexString(subTipColor) + tl3;
                    }
                    else {
                        tl3 = "§7" + tl3;
                    }
                }
                GL11.glTranslated(0.0, 0.0, 301.0);
                fr.drawStringWithShadow(tl3, 0.0f, shift ? 3.0f : 0.0f, -1);
                GL11.glPopMatrix();
                if (i == 0) {
                    sY += 2;
                }
                GL11.glPopMatrix();
            }
            Minecraft.getMinecraft().getRenderItem().zLevel = 0.0f;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }
    
    public static void drawGradientRect(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6) {
        final boolean blendon = GL11.glIsEnabled(3042);
        final float var7 = (par5 >> 24 & 0xFF) / 255.0f;
        final float var8 = (par5 >> 16 & 0xFF) / 255.0f;
        final float var9 = (par5 >> 8 & 0xFF) / 255.0f;
        final float var10 = (par5 & 0xFF) / 255.0f;
        final float var11 = (par6 >> 24 & 0xFF) / 255.0f;
        final float var12 = (par6 >> 16 & 0xFF) / 255.0f;
        final float var13 = (par6 >> 8 & 0xFF) / 255.0f;
        final float var14 = (par6 & 0xFF) / 255.0f;
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        final Tessellator var15 = Tessellator.getInstance();
        var15.getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR);
        var15.getBuffer().pos(par3, par2, 300.0).color(var8, var9, var10, var7).endVertex();
        var15.getBuffer().pos(par1, par2, 300.0).color(var8, var9, var10, var7).endVertex();
        var15.getBuffer().pos(par1, par4, 300.0).color(var12, var13, var14, var11).endVertex();
        var15.getBuffer().pos(par3, par4, 300.0).color(var12, var13, var14, var11).endVertex();
        var15.draw();
        GL11.glShadeModel(7424);
        GlStateManager.blendFunc(770, 771);
        if (!blendon) {
            GL11.glDisable(3042);
        }
        GL11.glEnable(3008);
        GL11.glEnable(3553);
    }
    
    public static void renderBillboardQuad(final double scale) {
        GL11.glPushMatrix();
        rotateToPlayer();
        final Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        tessellator.getBuffer().pos(-scale, -scale, 0.0).tex(0.0, 0.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(-scale, scale, 0.0).tex(0.0, 1.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(scale, scale, 0.0).tex(1.0, 1.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(scale, -scale, 0.0).tex(1.0, 0.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.draw();
        GL11.glPopMatrix();
    }
    
    public static void renderBillboardQuad(final double scale, final int gridX, final int gridY, final int frame) {
        GL11.glPushMatrix();
        rotateToPlayer();
        final int xm = frame % gridX;
        final int ym = frame / gridY;
        final float f1 = xm / (float)gridX;
        final float f2 = f1 + 1.0f / gridX;
        final float f3 = ym / (float)gridY;
        final float f4 = f3 + 1.0f / gridY;
        final Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        tessellator.getBuffer().pos(-scale, -scale, 0.0).tex(f2, f4).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(-scale, scale, 0.0).tex(f2, f3).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(scale, scale, 0.0).tex(f1, f3).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(scale, -scale, 0.0).tex(f1, f4).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.draw();
        GL11.glPopMatrix();
    }
    
    public static void renderBillboardQuad(final double scale, final int gridX, final int gridY, final int frame, final float r, final float g, final float b, final float a, final int bright) {
        GL11.glPushMatrix();
        rotateToPlayer();
        final int xm = frame % gridX;
        final int ym = frame / gridY;
        final float f1 = xm / (float)gridX;
        final float f2 = f1 + 1.0f / gridX;
        final float f3 = ym / (float)gridY;
        final float f4 = f3 + 1.0f / gridY;
        final int j = bright >> 16 & 0xFFFF;
        final int k = bright & 0xFFFF;
        final Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        tessellator.getBuffer().pos(-scale, -scale, 0.0).tex(f2, f4).color(r, g, b, a).lightmap(j, k).endVertex();
        tessellator.getBuffer().pos(-scale, scale, 0.0).tex(f2, f3).color(r, g, b, a).lightmap(j, k).endVertex();
        tessellator.getBuffer().pos(scale, scale, 0.0).tex(f1, f3).color(r, g, b, a).lightmap(j, k).endVertex();
        tessellator.getBuffer().pos(scale, -scale, 0.0).tex(f1, f4).color(r, g, b, a).lightmap(j, k).endVertex();
        tessellator.draw();
        GL11.glPopMatrix();
    }
    
    public static void rotateToPlayer() {
        GL11.glRotatef(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
    }
    
    public static boolean renderItemStack(final Minecraft mc, final ItemStack itm, final int x, final int y, final String txt) {
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        final RenderItem itemRender = mc.getRenderItem();
        final boolean isLightingEnabled = GL11.glIsEnabled(2896);
        boolean rc = false;
        if (itm != null && !itm.isEmpty()) {
            rc = true;
            final boolean isRescaleNormalEnabled = GL11.glIsEnabled(32826);
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0f, 0.0f, 32.0f);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glEnable(32826);
            GL11.glEnable(2896);
            final short short1 = 240;
            final short short2 = 240;
            RenderHelper.enableGUIStandardItemLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0f, short2 / 1.0f);
            itemRender.renderItemAndEffectIntoGUI(itm, x, y);
            if (!UtilsFX.hideStackOverlay) {
                itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, itm, x, y, txt);
            }
            GL11.glPopMatrix();
            if (isRescaleNormalEnabled) {
                GL11.glEnable(32826);
            }
            else {
                GL11.glDisable(32826);
            }
        }
        if (isLightingEnabled) {
            GL11.glEnable(2896);
        }
        else {
            GL11.glDisable(2896);
        }
        return rc;
    }
    
    public static boolean renderItemStackShaded(final Minecraft mc, final ItemStack itm, final int x, final int y, final String txt, final float shade) {
        GlStateManager.color(shade, shade, shade, shade);
        final RenderItem itemRender = mc.getRenderItem();
        final boolean isLightingEnabled = GL11.glIsEnabled(2896);
        boolean rc = false;
        if (itm != null && !itm.isEmpty()) {
            rc = true;
            final boolean isRescaleNormalEnabled = GL11.glIsEnabled(32826);
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0f, 0.0f, 32.0f);
            GlStateManager.color(shade, shade, shade, shade);
            GL11.glEnable(32826);
            GL11.glEnable(2896);
            final short short1 = 240;
            final short short2 = 240;
            RenderHelper.enableGUIStandardItemLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0f, short2 / 1.0f);
            itemRender.renderItemAndEffectIntoGUI(itm, x, y);
            itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, itm, x, y, txt);
            GL11.glPopMatrix();
            if (isRescaleNormalEnabled) {
                GL11.glEnable(32826);
            }
            else {
                GL11.glDisable(32826);
            }
        }
        if (isLightingEnabled) {
            GL11.glEnable(2896);
        }
        else {
            GL11.glDisable(2896);
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        return rc;
    }
    
    public static void drawBeam(final Vector S, final Vector E, final Vector P, final float width, final int bright) {
        drawBeam(S, E, P, width, bright, 1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public static void drawBeam(final Vector S, final Vector E, final Vector P, final float width, final int bright, final float r, final float g, final float b, final float a) {
        final Vector PS = Sub(S, P);
        final Vector SE = Sub(E, S);
        Vector normal = Cross(PS, SE);
        normal = normal.normalize();
        final Vector half = Mul(normal, width);
        final Vector p1 = Add(S, half);
        final Vector p2 = Sub(S, half);
        final Vector p3 = Add(E, half);
        final Vector p4 = Sub(E, half);
        drawQuad(Tessellator.getInstance(), p1, p3, p4, p2, bright, r, g, b, a);
    }
    
    public static void drawQuad(final Tessellator tessellator, final Vector p1, final Vector p2, final Vector p3, final Vector p4, final int bright, final float r, final float g, final float b, final float a) {
        final int j = bright >> 16 & 0xFFFF;
        final int k = bright & 0xFFFF;
        tessellator.getBuffer().pos(p1.getX(), p1.getY(), p1.getZ()).tex(0.0, 0.0).lightmap(j, k).color(r, g, b, a).endVertex();
        tessellator.getBuffer().pos(p2.getX(), p2.getY(), p2.getZ()).tex(1.0, 0.0).lightmap(j, k).color(r, g, b, a).endVertex();
        tessellator.getBuffer().pos(p3.getX(), p3.getY(), p3.getZ()).tex(1.0, 1.0).lightmap(j, k).color(r, g, b, a).endVertex();
        tessellator.getBuffer().pos(p4.getX(), p4.getY(), p4.getZ()).tex(0.0, 1.0).lightmap(j, k).color(r, g, b, a).endVertex();
    }
    
    private static Vector Cross(final Vector a, final Vector b) {
        final float x = a.y * b.z - a.z * b.y;
        final float y = a.z * b.x - a.x * b.z;
        final float z = a.x * b.y - a.y * b.x;
        return new Vector(x, y, z);
    }
    
    public static Vector Sub(final Vector a, final Vector b) {
        return new Vector(a.x - b.x, a.y - b.y, a.z - b.z);
    }
    
    private static Vector Add(final Vector a, final Vector b) {
        return new Vector(a.x + b.x, a.y + b.y, a.z + b.z);
    }
    
    private static Vector Mul(final Vector a, final float f) {
        return new Vector(a.x * f, a.y * f, a.z * f);
    }
    
    public static void renderItemIn2D(final String sprite, final float thickness) {
        renderItemIn2D(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(sprite), thickness);
    }
    
    public static void renderItemIn2D(final TextureAtlasSprite icon, final float thickness) {
        GL11.glPushMatrix();
        final float f1 = icon.getMaxU();
        final float f2 = icon.getMinV();
        final float f3 = icon.getMinU();
        final float f4 = icon.getMaxV();
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        renderTextureIn3D(f1, f2, f3, f4, 16, 16, thickness);
        GL11.glPopMatrix();
    }
    
    public static void renderTextureIn3D(final float maxu, final float maxv, final float minu, final float minv, final int width, final int height, final float thickness) {
        final Tessellator tess = Tessellator.getInstance();
        final BufferBuilder wr = tess.getBuffer();
        wr.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        wr.pos(0.0, 0.0, 0.0).tex(maxu, minv).normal(0.0f, 0.0f, 1.0f).endVertex();
        wr.pos(1.0, 0.0, 0.0).tex(minu, minv).normal(0.0f, 0.0f, 1.0f).endVertex();
        wr.pos(1.0, 1.0, 0.0).tex(minu, maxv).normal(0.0f, 0.0f, 1.0f).endVertex();
        wr.pos(0.0, 1.0, 0.0).tex(maxu, maxv).normal(0.0f, 0.0f, 1.0f).endVertex();
        tess.draw();
        wr.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        wr.pos(0.0, 1.0, 0.0f - thickness).tex(maxu, maxv).normal(0.0f, 0.0f, -1.0f).endVertex();
        wr.pos(1.0, 1.0, 0.0f - thickness).tex(minu, maxv).normal(0.0f, 0.0f, -1.0f).endVertex();
        wr.pos(1.0, 0.0, 0.0f - thickness).tex(minu, minv).normal(0.0f, 0.0f, -1.0f).endVertex();
        wr.pos(0.0, 0.0, 0.0f - thickness).tex(maxu, minv).normal(0.0f, 0.0f, -1.0f).endVertex();
        tess.draw();
        final float f5 = 0.5f * (maxu - minu) / width;
        final float f6 = 0.5f * (minv - maxv) / height;
        wr.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        for (int k = 0; k < width; ++k) {
            final float f7 = k / (float)width;
            final float f8 = maxu + (minu - maxu) * f7 - f5;
            wr.pos(f7, 0.0, 0.0f - thickness).tex(f8, minv).normal(-1.0f, 0.0f, 0.0f).endVertex();
            wr.pos(f7, 0.0, 0.0).tex(f8, minv).normal(-1.0f, 0.0f, 0.0f).endVertex();
            wr.pos(f7, 1.0, 0.0).tex(f8, maxv).normal(-1.0f, 0.0f, 0.0f).endVertex();
            wr.pos(f7, 1.0, 0.0f - thickness).tex(f8, maxv).normal(-1.0f, 0.0f, 0.0f).endVertex();
        }
        tess.draw();
        wr.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        for (int k = 0; k < width; ++k) {
            final float f7 = k / (float)width;
            final float f8 = maxu + (minu - maxu) * f7 - f5;
            final float f9 = f7 + 1.0f / width;
            wr.pos(f9, 1.0, 0.0f - thickness).tex(f8, maxv).normal(1.0f, 0.0f, 0.0f).endVertex();
            wr.pos(f9, 1.0, 0.0).tex(f8, maxv).normal(1.0f, 0.0f, 0.0f).endVertex();
            wr.pos(f9, 0.0, 0.0).tex(f8, minv).normal(1.0f, 0.0f, 0.0f).endVertex();
            wr.pos(f9, 0.0, 0.0f - thickness).tex(f8, minv).normal(1.0f, 0.0f, 0.0f).endVertex();
        }
        tess.draw();
        wr.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        for (int k = 0; k < height; ++k) {
            final float f7 = k / (float)height;
            final float f8 = minv + (maxv - minv) * f7 - f6;
            final float f9 = f7 + 1.0f / height;
            wr.pos(0.0, f9, 0.0).tex(maxu, f8).normal(0.0f, 1.0f, 0.0f).endVertex();
            wr.pos(1.0, f9, 0.0).tex(minu, f8).normal(0.0f, 1.0f, 0.0f).endVertex();
            wr.pos(1.0, f9, 0.0f - thickness).tex(minu, f8).normal(0.0f, 1.0f, 0.0f).endVertex();
            wr.pos(0.0, f9, 0.0f - thickness).tex(maxu, f8).normal(0.0f, 1.0f, 0.0f).endVertex();
        }
        tess.draw();
        wr.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        for (int k = 0; k < height; ++k) {
            final float f7 = k / (float)height;
            final float f8 = minv + (maxv - minv) * f7 - f6;
            wr.pos(1.0, f7, 0.0).tex(minu, f8).normal(0.0f, -1.0f, 0.0f).endVertex();
            wr.pos(0.0, f7, 0.0).tex(maxu, f8).normal(0.0f, -1.0f, 0.0f).endVertex();
            wr.pos(0.0, f7, 0.0f - thickness).tex(maxu, f8).normal(0.0f, -1.0f, 0.0f).endVertex();
            wr.pos(1.0, f7, 0.0f - thickness).tex(minu, f8).normal(0.0f, -1.0f, 0.0f).endVertex();
        }
        tess.draw();
    }
    
    static {
        nodeTexture = new ResourceLocation("thaumcraft", "textures/misc/auranodes.png");
        VERTEXFORMAT_POS_TEX_CO_LM_NO = new VertexFormat().addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB).addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B).addElement(DefaultVertexFormats.PADDING_1B);
        colorNames = new String[] { "White", "Orange", "Magenta", "Light Blue", "Yellow", "Lime", "Pink", "Gray", "Light Gray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black" };
        colorCodes = new String[] { "§f", "§6", "§d", "§9", "§e", "§a", "§d", "§8", "§7", "§b", "§5", "§9", "§4", "§2", "§c", "§8" };
        colors = new int[] { 15790320, 15435844, 12801229, 6719955, 14602026, 4312372, 14188952, 4408131, 10526880, 2651799, 8073150, 2437522, 5320730, 3887386, 11743532, 1973019 };
        UtilsFX.sysPartialTicks = 0.0f;
        UtilsFX.myFormatter = new DecimalFormat("#######.##");
        UtilsFX.hideStackOverlay = false;
    }
    
    public static class Vector
    {
        public final float x;
        public final float y;
        public final float z;
        
        public Vector(final float x, final float y, final float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        public float getX() {
            return this.x;
        }
        
        public float getY() {
            return this.y;
        }
        
        public float getZ() {
            return this.z;
        }
        
        public float norm() {
            return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        }
        
        public Vector normalize() {
            final float n = this.norm();
            return new Vector(this.x / n, this.y / n, this.z / n);
        }
    }
}
