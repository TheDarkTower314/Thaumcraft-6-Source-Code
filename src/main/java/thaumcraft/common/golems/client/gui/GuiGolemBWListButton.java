// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import net.minecraft.client.gui.GuiButton;

public class GuiGolemBWListButton extends GuiButton
{
    ISealConfigFilter filter;
    static ResourceLocation tex;
    
    public GuiGolemBWListButton(final int buttonId, final int x, final int y, final int width, final int height, final ISealConfigFilter filter) {
        super(buttonId, x, y, width, height, "");
        this.filter = filter;
    }
    
    public void drawButton(final Minecraft mc, final int xx, final int yy, final float partialTicks) {
        if (visible) {
            final FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiGolemBWListButton.tex);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            hovered = (xx >= x && yy >= y && xx < x + width && yy < y + height);
            final int k = getHoverState(hovered);
            if (k == 2) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            }
            else {
                GlStateManager.color(0.9f, 0.9f, 0.9f, 0.9f);
            }
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            if (filter.isBlacklist()) {
                drawTexturedModalRect(x, y, 0, 136, 16, 16);
            }
            else {
                drawTexturedModalRect(x, y, 16, 136, 16, 16);
            }
            if (k == 2) {
                drawCenteredString(fontrenderer, I18n.translateToLocal(filter.isBlacklist() ? "button.bl" : "button.wl"), x + 8, y + 17, 16777215);
            }
            mouseDragged(mc, xx, yy);
        }
    }
    
    static {
        GuiGolemBWListButton.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
    }
}
