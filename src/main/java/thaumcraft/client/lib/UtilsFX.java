package thaumcraft.client.lib;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.common.config.ModConfig;


public class UtilsFX
{
    public static ResourceLocation nodeTexture;
    public static VertexFormat VERTEXFORMAT_POS_TEX_CO_LM_NO;
    public static String[] colorNames;
    public static String[] colorCodes;
    public static int[] colors;
    public static float sysPartialTicks;
    static DecimalFormat myFormatter;
    public static boolean hideStackOverlay;
    
    public static void renderFacingQuad(double px, double py, double pz, int gridX, int gridY, int frame, float scale, int color, float alpha, int blend, float partialTicks) {
        if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder wr = tessellator.getBuffer();
            float arX = ActiveRenderInfo.getRotationX();
            float arZ = ActiveRenderInfo.getRotationZ();
            float arYZ = ActiveRenderInfo.getRotationYZ();
            float arXY = ActiveRenderInfo.getRotationXY();
            float arXZ = ActiveRenderInfo.getRotationXZ();
            EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().getRenderViewEntity();
            double iPX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
            double iPY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
            double iPZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
            GlStateManager.pushMatrix();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, blend);
            GlStateManager.alphaFunc(516, 0.003921569f);
            GlStateManager.depthMask(false);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glTranslated(-iPX, -iPY, -iPZ);
            Vec3d v1 = new Vec3d(-arX * scale - arYZ * scale, -arXZ * scale, -arZ * scale - arXY * scale);
            Vec3d v2 = new Vec3d(-arX * scale + arYZ * scale, arXZ * scale, -arZ * scale + arXY * scale);
            Vec3d v3 = new Vec3d(arX * scale + arYZ * scale, arXZ * scale, arZ * scale + arXY * scale);
            Vec3d v4 = new Vec3d(arX * scale - arYZ * scale, -arXZ * scale, arZ * scale - arXY * scale);
            int xm = frame % gridX;
            int ym = frame / gridY;
            float f1 = xm / (float)gridX;
            float f2 = f1 + 1.0f / gridX;
            float f3 = ym / (float)gridY;
            float f4 = f3 + 1.0f / gridY;
            TexturedQuadTC quad = new TexturedQuadTC(new PositionTextureVertex[] { new PositionTextureVertex((float)px + (float)v1.x, (float)py + (float)v1.y, (float)pz + (float)v1.z, f2, f4), new PositionTextureVertex((float)px + (float)v2.x, (float)py + (float)v2.y, (float)pz + (float)v2.z, f2, f3), new PositionTextureVertex((float)px + (float)v3.x, (float)py + (float)v3.y, (float)pz + (float)v3.z, f1, f3), new PositionTextureVertex((float)px + (float)v4.x, (float)py + (float)v4.y, (float)pz + (float)v4.z, f1, f4) });
            quad.draw(tessellator.getBuffer(), 1.0f, 220, color, alpha);
            GlStateManager.depthMask(true);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3042);
            GlStateManager.alphaFunc(516, 0.1f);
            GlStateManager.popMatrix();
        }
    }
    
    public static void drawTexturedQuad(float par1, float par2, float par3, float par4, float par5, float par6, double zLevel) {
        float var7 = 0.00390625f;
        float var8 = 0.00390625f;
        Tessellator var9 = Tessellator.getInstance();
        var9.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
        var9.getBuffer().pos(par1 + 0.0f, par2 + par6, zLevel).tex((par3 + 0.0f) * var7, (par4 + par6) * var8).endVertex();
        var9.getBuffer().pos(par1 + par5, par2 + par6, zLevel).tex((par3 + par5) * var7, (par4 + par6) * var8).endVertex();
        var9.getBuffer().pos(par1 + par5, par2 + 0.0f, zLevel).tex((par3 + par5) * var7, (par4 + 0.0f) * var8).endVertex();
        var9.getBuffer().pos(par1 + 0.0f, par2 + 0.0f, zLevel).tex((par3 + 0.0f) * var7, (par4 + 0.0f) * var8).endVertex();
        var9.draw();
    }
    
    public static void drawTexturedQuadF(float par1, float par2, float par3, float par4, float par5, float par6, double zLevel) {
        float d = 0.0625f;
        Tessellator var9 = Tessellator.getInstance();
        var9.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
        var9.getBuffer().pos(par1 + 0.0f, par2 + 16.0f, zLevel).tex((par3 + 0.0f) * d, (par4 + par6) * d).endVertex();
        var9.getBuffer().pos(par1 + 16.0f, par2 + 16.0f, zLevel).tex((par3 + par5) * d, (par4 + par6) * d).endVertex();
        var9.getBuffer().pos(par1 + 16.0f, par2 + 0.0f, zLevel).tex((par3 + par5) * d, (par4 + 0.0f) * d).endVertex();
        var9.getBuffer().pos(par1 + 0.0f, par2 + 0.0f, zLevel).tex((par3 + 0.0f) * d, (par4 + 0.0f) * d).endVertex();
        var9.draw();
    }
    
    public static void drawTexturedQuadFull(float par1, float par2, double zLevel) {
        Tessellator var9 = Tessellator.getInstance();
        var9.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
        var9.getBuffer().pos(par1 + 0.0f, par2 + 16.0f, zLevel).tex(0.0, 1.0).endVertex();
        var9.getBuffer().pos(par1 + 16.0f, par2 + 16.0f, zLevel).tex(1.0, 1.0).endVertex();
        var9.getBuffer().pos(par1 + 16.0f, par2 + 0.0f, zLevel).tex(1.0, 0.0).endVertex();
        var9.getBuffer().pos(par1 + 0.0f, par2 + 0.0f, zLevel).tex(0.0, 0.0).endVertex();
        var9.draw();
    }
    
    public static void renderItemInGUI(int x, int y, int z, ItemStack stack) {
        Minecraft mc = Minecraft.getMinecraft();
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
        catch (Exception ex) {}
    }
    
    public static void renderQuadCentered(ResourceLocation texture, float scale, float red, float green, float blue, int brightness, int blend, float opacity) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        renderQuadCentered(1, 1, 0, scale, red, green, blue, brightness, blend, opacity);
    }
    
    public static void renderQuadCentered(ResourceLocation texture, int gridX, int gridY, int frame, float scale, float red, float green, float blue, int brightness, int blend, float opacity) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        renderQuadCentered(gridX, gridY, frame, scale, red, green, blue, brightness, blend, opacity);
    }
    
    public static void renderQuadCentered() {
        renderQuadCentered(1, 1, 0, 1.0f, 1.0f, 1.0f, 1.0f, 200, 771, 1.0f);
    }
    
    public static void renderQuadCentered(int gridX, int gridY, int frame, float scale, float red, float green, float blue, int brightness, int blend, float opacity) {
        Tessellator tessellator = Tessellator.getInstance();
        boolean blendon = GL11.glIsEnabled(3042);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, blend);
        int xm = frame % gridX;
        int ym = frame / gridY;
        float f1 = xm / (float)gridX;
        float f2 = f1 + 1.0f / gridX;
        float f3 = ym / (float)gridY;
        float f4 = f3 + 1.0f / gridY;
        Color c = new Color(red, green, blue);
        TexturedQuadTC quad = new TexturedQuadTC(new PositionTextureVertex[] { new PositionTextureVertex(-0.5f, 0.5f, 0.0f, f2, f4), new PositionTextureVertex(0.5f, 0.5f, 0.0f, f2, f3), new PositionTextureVertex(0.5f, -0.5f, 0.0f, f1, f3), new PositionTextureVertex(-0.5f, -0.5f, 0.0f, f1, f4) });
        quad.draw(tessellator.getBuffer(), scale, brightness, c.getRGB(), opacity);
        GL11.glBlendFunc(770, 771);
        if (!blendon) {
            GL11.glDisable(3042);
        }
    }
    
    public static void renderQuadFromIcon(TextureAtlasSprite icon, float scale, float red, float green, float blue, int brightness, int blend, float opacity) {
        boolean blendon = GL11.glIsEnabled(3042);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        float f1 = icon.getMaxU();
        float f2 = icon.getMinV();
        float f3 = icon.getMinU();
        float f4 = icon.getMaxV();
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
        int j = brightness >> 16 & 0xFFFF;
        int k = brightness & 0xFFFF;
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
    
    public static void drawTag(int x, int y, Aspect aspect, float amount, int bonus, double z, int blend, float alpha) {
        drawTag(x, y, aspect, amount, bonus, z, blend, alpha, false);
    }
    
    public static void drawTag(int x, int y, Aspect aspect, float amt, int bonus, double z) {
        drawTag(x, y, aspect, amt, bonus, z, 771, 1.0f, false);
    }
    
    public static void drawTag(int x, int y, Aspect aspect) {
        drawTag(x, y, aspect, 0.0f, 0, 0.0, 771, 1.0f, true);
    }
    
    public static void drawTag(int x, int y, Aspect aspect, float amount, int bonus, double z, int blend, float alpha, boolean bw) {
        drawTag(x, (double)y, aspect, amount, bonus, z, blend, alpha, bw);
    }
    
    public static void drawTag(double x, double y, Aspect aspect, float amount, int bonus, double z, int blend, float alpha, boolean bw) {
        if (aspect == null) {
            return;
        }
        boolean blendon = GL11.glIsEnabled(3042);
        Minecraft mc = Minecraft.getMinecraft();
        boolean isLightingEnabled = GL11.glIsEnabled(2896);
        Color color = new Color(aspect.getColor());
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
        Tessellator var9 = Tessellator.getInstance();
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
            String am = UtilsFX.myFormatter.format(amount);
            int sw = mc.fontRenderer.getStringWidth(am);
            for (EnumFacing e : EnumFacing.HORIZONTALS) {
                mc.fontRenderer.drawString(am, (32 - sw + (int)x * 2) * q + e.getFrontOffsetX(), (32 - mc.fontRenderer.FONT_HEIGHT + (int)y * 2) * q + e.getFrontOffsetZ(), 0, false);
            }
            mc.fontRenderer.drawString(am, (32 - sw + (int)x * 2) * q, (32 - mc.fontRenderer.FONT_HEIGHT + (int)y * 2) * q, 16777215, false);
            GL11.glPopMatrix();
        }
        if (bonus > 0) {
            GL11.glPushMatrix();
            mc.renderEngine.bindTexture(ParticleEngine.particleTexture);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            int px = 16 * (mc.player.ticksExisted % 16);
            drawTexturedQuad((float)((int)x - 4), (float)((int)y - 4), (float)px, 80.0f, 16.0f, 16.0f, z);
            if (bonus > 1) {
                float q2 = 0.5f;
                if (!ModConfig.CONFIG_GRAPHICS.largeTagText) {
                    GL11.glScalef(0.5f, 0.5f, 0.5f);
                    q2 = 1.0f;
                }
                String am2 = "" + bonus;
                int sw2 = mc.fontRenderer.getStringWidth(am2) / 2;
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
    
    public static void drawCustomTooltip(GuiScreen gui, FontRenderer fr, List<String> textList, int x, int y, int subTipColor) {
        drawCustomTooltip(gui, fr, textList, x, y, subTipColor, false);
    }
    
    public static void drawCustomTooltip(GuiScreen gui, FontRenderer fr, List<String> textList, int x, int y, int subTipColor, boolean ignoremouse) {
        if (!textList.isEmpty()) {
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution scaledresolution = new ScaledResolution(mc);
            int sf = scaledresolution.getScaleFactor();
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int max = 240;
            int mx = Mouse.getEventX();
            boolean flip = false;
            if (!ignoremouse && (max + 24) * sf + mx > mc.displayWidth) {
                max = (mc.displayWidth - mx) / sf - 24;
                if (max < 120) {
                    max = 240;
                    flip = true;
                }
            }
            int widestLineWidth = 0;
            Iterator<String> textLineEntry = textList.iterator();
            boolean b = false;
            while (textLineEntry.hasNext()) {
                String textLine = textLineEntry.next();
                if (fr.getStringWidth(textLine) > max) {
                    b = true;
                    break;
                }
            }
            if (b) {
                List tl = new ArrayList();
                for (Object o : textList) {
                    String textLine2 = (String)o;
                    List tl2 = fr.listFormattedStringToWidth(textLine2, textLine2.startsWith("@@") ? (max * 2) : max);
                    for (Object o2 : tl2) {
                        String textLine3 = ((String)o2).trim();
                        if (textLine2.startsWith("@@")) {
                            textLine3 = "@@" + textLine3;
                        }
                        tl.add(textLine3);
                    }
                }
                textList = tl;
            }
            Iterator<String> textLines = textList.iterator();
            int totalHeight = -2;
            while (textLines.hasNext()) {
                String textLine4 = textLines.next();
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
            int var10 = -267386864;
            drawGradientRect(sX - 3, sY - 4, sX + widestLineWidth + 3, sY - 3, var10, var10);
            drawGradientRect(sX - 3, sY + totalHeight + 3, sX + widestLineWidth + 3, sY + totalHeight + 4, var10, var10);
            drawGradientRect(sX - 3, sY - 3, sX + widestLineWidth + 3, sY + totalHeight + 3, var10, var10);
            drawGradientRect(sX - 4, sY - 3, sX - 3, sY + totalHeight + 3, var10, var10);
            drawGradientRect(sX + widestLineWidth + 3, sY - 3, sX + widestLineWidth + 4, sY + totalHeight + 3, var10, var10);
            int var11 = 1347420415;
            int var12 = (var11 & 0xFEFEFE) >> 1 | (var11 & 0xFF000000);
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
    
    public static void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6) {
        boolean blendon = GL11.glIsEnabled(3042);
        float var7 = (par5 >> 24 & 0xFF) / 255.0f;
        float var8 = (par5 >> 16 & 0xFF) / 255.0f;
        float var9 = (par5 >> 8 & 0xFF) / 255.0f;
        float var10 = (par5 & 0xFF) / 255.0f;
        float var11 = (par6 >> 24 & 0xFF) / 255.0f;
        float var12 = (par6 >> 16 & 0xFF) / 255.0f;
        float var13 = (par6 >> 8 & 0xFF) / 255.0f;
        float var14 = (par6 & 0xFF) / 255.0f;
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        Tessellator var15 = Tessellator.getInstance();
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
    
    public static void renderBillboardQuad(double scale) {
        GL11.glPushMatrix();
        rotateToPlayer();
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        tessellator.getBuffer().pos(-scale, -scale, 0.0).tex(0.0, 0.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(-scale, scale, 0.0).tex(0.0, 1.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(scale, scale, 0.0).tex(1.0, 1.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(scale, -scale, 0.0).tex(1.0, 0.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.draw();
        GL11.glPopMatrix();
    }
    
    public static void renderBillboardQuad(double scale, int gridX, int gridY, int frame) {
        GL11.glPushMatrix();
        rotateToPlayer();
        int xm = frame % gridX;
        int ym = frame / gridY;
        float f1 = xm / (float)gridX;
        float f2 = f1 + 1.0f / gridX;
        float f3 = ym / (float)gridY;
        float f4 = f3 + 1.0f / gridY;
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        tessellator.getBuffer().pos(-scale, -scale, 0.0).tex(f2, f4).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(-scale, scale, 0.0).tex(f2, f3).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(scale, scale, 0.0).tex(f1, f3).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.getBuffer().pos(scale, -scale, 0.0).tex(f1, f4).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.draw();
        GL11.glPopMatrix();
    }
    
    public static void renderBillboardQuad(double scale, int gridX, int gridY, int frame, float r, float g, float b, float a, int bright) {
        GL11.glPushMatrix();
        rotateToPlayer();
        int xm = frame % gridX;
        int ym = frame / gridY;
        float f1 = xm / (float)gridX;
        float f2 = f1 + 1.0f / gridX;
        float f3 = ym / (float)gridY;
        float f4 = f3 + 1.0f / gridY;
        int j = bright >> 16 & 0xFFFF;
        int k = bright & 0xFFFF;
        Tessellator tessellator = Tessellator.getInstance();
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
    
    public static boolean renderItemStack(Minecraft mc, ItemStack itm, int x, int y, String txt) {
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        RenderItem itemRender = mc.getRenderItem();
        boolean isLightingEnabled = GL11.glIsEnabled(2896);
        boolean rc = false;
        if (itm != null && !itm.isEmpty()) {
            rc = true;
            boolean isRescaleNormalEnabled = GL11.glIsEnabled(32826);
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0f, 0.0f, 32.0f);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glEnable(32826);
            GL11.glEnable(2896);
            short short1 = 240;
            short short2 = 240;
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
    
    public static boolean renderItemStackShaded(Minecraft mc, ItemStack itm, int x, int y, String txt, float shade) {
        GlStateManager.color(shade, shade, shade, shade);
        RenderItem itemRender = mc.getRenderItem();
        boolean isLightingEnabled = GL11.glIsEnabled(2896);
        boolean rc = false;
        if (itm != null && !itm.isEmpty()) {
            rc = true;
            boolean isRescaleNormalEnabled = GL11.glIsEnabled(32826);
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0f, 0.0f, 32.0f);
            GlStateManager.color(shade, shade, shade, shade);
            GL11.glEnable(32826);
            GL11.glEnable(2896);
            short short1 = 240;
            short short2 = 240;
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
    
    public static void drawBeam(Vector S, Vector E, Vector P, float width, int bright) {
        drawBeam(S, E, P, width, bright, 1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public static void drawBeam(Vector S, Vector E, Vector P, float width, int bright, float r, float g, float b, float a) {
        Vector PS = Sub(S, P);
        Vector SE = Sub(E, S);
        Vector normal = Cross(PS, SE);
        normal = normal.normalize();
        Vector half = Mul(normal, width);
        Vector p1 = Add(S, half);
        Vector p2 = Sub(S, half);
        Vector p3 = Add(E, half);
        Vector p4 = Sub(E, half);
        drawQuad(Tessellator.getInstance(), p1, p3, p4, p2, bright, r, g, b, a);
    }
    
    public static void drawQuad(Tessellator tessellator, Vector p1, Vector p2, Vector p3, Vector p4, int bright, float r, float g, float b, float a) {
        int j = bright >> 16 & 0xFFFF;
        int k = bright & 0xFFFF;
        tessellator.getBuffer().pos(p1.getX(), p1.getY(), p1.getZ()).tex(0.0, 0.0).lightmap(j, k).color(r, g, b, a).endVertex();
        tessellator.getBuffer().pos(p2.getX(), p2.getY(), p2.getZ()).tex(1.0, 0.0).lightmap(j, k).color(r, g, b, a).endVertex();
        tessellator.getBuffer().pos(p3.getX(), p3.getY(), p3.getZ()).tex(1.0, 1.0).lightmap(j, k).color(r, g, b, a).endVertex();
        tessellator.getBuffer().pos(p4.getX(), p4.getY(), p4.getZ()).tex(0.0, 1.0).lightmap(j, k).color(r, g, b, a).endVertex();
    }
    
    private static Vector Cross(Vector a, Vector b) {
        float x = a.y * b.z - a.z * b.y;
        float y = a.z * b.x - a.x * b.z;
        float z = a.x * b.y - a.y * b.x;
        return new Vector(x, y, z);
    }
    
    public static Vector Sub(Vector a, Vector b) {
        return new Vector(a.x - b.x, a.y - b.y, a.z - b.z);
    }
    
    private static Vector Add(Vector a, Vector b) {
        return new Vector(a.x + b.x, a.y + b.y, a.z + b.z);
    }
    
    private static Vector Mul(Vector a, float f) {
        return new Vector(a.x * f, a.y * f, a.z * f);
    }
    
    public static void renderItemIn2D(String sprite, float thickness) {
        renderItemIn2D(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(sprite), thickness);
    }
    
    public static void renderItemIn2D(TextureAtlasSprite icon, float thickness) {
        GL11.glPushMatrix();
        float f1 = icon.getMaxU();
        float f2 = icon.getMinV();
        float f3 = icon.getMinU();
        float f4 = icon.getMaxV();
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        renderTextureIn3D(f1, f2, f3, f4, 16, 16, thickness);
        GL11.glPopMatrix();
    }
    
    public static void renderTextureIn3D(float maxu, float maxv, float minu, float minv, int width, int height, float thickness) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder wr = tess.getBuffer();
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
        float f5 = 0.5f * (maxu - minu) / width;
        float f6 = 0.5f * (minv - maxv) / height;
        wr.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        for (int k = 0; k < width; ++k) {
            float f7 = k / (float)width;
            float f8 = maxu + (minu - maxu) * f7 - f5;
            wr.pos(f7, 0.0, 0.0f - thickness).tex(f8, minv).normal(-1.0f, 0.0f, 0.0f).endVertex();
            wr.pos(f7, 0.0, 0.0).tex(f8, minv).normal(-1.0f, 0.0f, 0.0f).endVertex();
            wr.pos(f7, 1.0, 0.0).tex(f8, maxv).normal(-1.0f, 0.0f, 0.0f).endVertex();
            wr.pos(f7, 1.0, 0.0f - thickness).tex(f8, maxv).normal(-1.0f, 0.0f, 0.0f).endVertex();
        }
        tess.draw();
        wr.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        for (int k = 0; k < width; ++k) {
            float f7 = k / (float)width;
            float f8 = maxu + (minu - maxu) * f7 - f5;
            float f9 = f7 + 1.0f / width;
            wr.pos(f9, 1.0, 0.0f - thickness).tex(f8, maxv).normal(1.0f, 0.0f, 0.0f).endVertex();
            wr.pos(f9, 1.0, 0.0).tex(f8, maxv).normal(1.0f, 0.0f, 0.0f).endVertex();
            wr.pos(f9, 0.0, 0.0).tex(f8, minv).normal(1.0f, 0.0f, 0.0f).endVertex();
            wr.pos(f9, 0.0, 0.0f - thickness).tex(f8, minv).normal(1.0f, 0.0f, 0.0f).endVertex();
        }
        tess.draw();
        wr.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        for (int k = 0; k < height; ++k) {
            float f7 = k / (float)height;
            float f8 = minv + (maxv - minv) * f7 - f6;
            float f9 = f7 + 1.0f / height;
            wr.pos(0.0, f9, 0.0).tex(maxu, f8).normal(0.0f, 1.0f, 0.0f).endVertex();
            wr.pos(1.0, f9, 0.0).tex(minu, f8).normal(0.0f, 1.0f, 0.0f).endVertex();
            wr.pos(1.0, f9, 0.0f - thickness).tex(minu, f8).normal(0.0f, 1.0f, 0.0f).endVertex();
            wr.pos(0.0, f9, 0.0f - thickness).tex(maxu, f8).normal(0.0f, 1.0f, 0.0f).endVertex();
        }
        tess.draw();
        wr.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        for (int k = 0; k < height; ++k) {
            float f7 = k / (float)height;
            float f8 = minv + (maxv - minv) * f7 - f6;
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
        public float x;
        public float y;
        public float z;
        
        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        public float getX() {
            return x;
        }
        
        public float getY() {
            return y;
        }
        
        public float getZ() {
            return z;
        }
        
        public float norm() {
            return (float)Math.sqrt(x * x + y * y + z * z);
        }
        
        public Vector normalize() {
            float n = norm();
            return new Vector(x / n, y / n, z / n);
        }
    }
}
