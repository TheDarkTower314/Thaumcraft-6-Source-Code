// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui.plugins;

import java.util.Iterator;
import net.minecraft.client.renderer.RenderHelper;
import java.util.List;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.util.ITooltipFlag;
import java.util.ArrayList;
import net.minecraft.client.gui.FontRenderer;
import thaumcraft.client.lib.UtilsFX;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.client.renderer.GlStateManager;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;

@SideOnly(Side.CLIENT)
public class GuiHoverButton extends GuiButton
{
    String description;
    GuiScreen screen;
    int color;
    Object tex;
    
    public GuiHoverButton(final GuiScreen screen, final int buttonId, final int x, final int y, final int width, final int height, final String buttonText, final String description, final Object tex) {
        super(buttonId, x, y, width, height, buttonText);
        this.tex = null;
        this.description = description;
        this.tex = tex;
        this.screen = screen;
        this.color = 16777215;
    }
    
    public GuiHoverButton(final GuiScreen screen, final int buttonId, final int x, final int y, final int width, final int height, final String buttonText, final String description, final Object tex, final int color) {
        super(buttonId, x, y, width, height, buttonText);
        this.tex = null;
        this.description = description;
        this.tex = tex;
        this.screen = screen;
        this.color = color;
    }
    
    public void drawButton(final Minecraft mc, final int xx, final int yy, final float pt) {
        if (this.visible) {
            final FontRenderer fontrenderer = mc.fontRenderer;
            final Color c = new Color(this.color);
            GlStateManager.color(0.9f * (c.getRed() / 255.0f), 0.9f * (c.getGreen() / 255.0f), 0.9f * (c.getBlue() / 255.0f), 0.9f);
            this.hovered = (xx >= this.x - this.width / 2 && yy >= this.y - this.height / 2 && xx < this.x - this.width / 2 + this.width && yy < this.y - this.height / 2 + this.height);
            final int k = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            if (k == 2) {
                GlStateManager.color(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 1.0f);
            }
            if (this.tex instanceof Aspect) {
                mc.getTextureManager().bindTexture(((Aspect)this.tex).getImage());
                final Color c2 = new Color(((Aspect)this.tex).getColor());
                if (k != 2) {
                    GlStateManager.color(c2.getRed() / 290.0f, c2.getGreen() / 290.0f, c2.getBlue() / 290.0f, 0.9f);
                }
                else {
                    GlStateManager.color(c2.getRed() / 255.0f, c2.getGreen() / 255.0f, c2.getBlue() / 255.0f, 1.0f);
                }
                drawModalRectWithCustomSizedTexture(this.x - this.width / 2, this.y - this.height / 2, 0.0f, 0.0f, 16, 16, 16.0f, 16.0f);
            }
            if (this.tex instanceof ResourceLocation) {
                mc.getTextureManager().bindTexture((ResourceLocation)this.tex);
                drawModalRectWithCustomSizedTexture(this.x - this.width / 2, this.y - this.height / 2, 0.0f, 0.0f, 16, 16, 16.0f, 16.0f);
            }
            if (this.tex instanceof TextureAtlasSprite) {
                this.drawTexturedModalRect(this.x - this.width / 2, this.y - this.height / 2, (TextureAtlasSprite)this.tex, 16, 16);
            }
            if (this.tex instanceof ItemStack) {
                this.zLevel -= 90.0f;
                UtilsFX.renderItemStackShaded(mc, (ItemStack)this.tex, this.x - this.width / 2, this.y - this.height / 2 - ((k == 2) ? 1 : 0), null, 1.0f);
                this.zLevel += 90.0f;
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.mouseDragged(mc, xx, yy);
        }
    }
    
    public void drawButtonForegroundLayer(final int xx, final int yy) {
        final FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
        this.zLevel += 90.0f;
        List<String> text = new ArrayList<String>();
        if (this.tex instanceof ItemStack) {
            text = ((ItemStack)this.tex).getTooltip(Minecraft.getMinecraft().player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
            int qq = 0;
            for (final String s : text) {
                if (s.endsWith(" " + TextFormatting.RESET)) {
                    text = text.subList(0, qq);
                    break;
                }
                ++qq;
            }
        }
        else {
            text.add(this.displayString);
        }
        int m = 8;
        if (this.description != null) {
            m = 0;
            text.add("§o§9" + this.description);
        }
        UtilsFX.drawCustomTooltip(this.screen, fontrenderer, text, xx + 4, yy + m, -99);
        this.zLevel -= 90.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
        return false;
    }
}
