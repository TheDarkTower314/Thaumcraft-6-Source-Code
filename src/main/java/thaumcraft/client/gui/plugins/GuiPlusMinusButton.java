// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui.plugins;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.gui.GuiButton;

public class GuiPlusMinusButton extends GuiButton
{
    boolean minus;
    static ResourceLocation tex;
    
    public GuiPlusMinusButton(final int buttonId, final int x, final int y, final int width, final int height, final boolean left) {
        super(buttonId, x, y, width, height, "");
        minus = false;
        minus = left;
    }
    
    public void drawButton(final Minecraft mc, final int xx, final int yy, final float pt) {
        if (visible) {
            final FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiPlusMinusButton.tex);
            GlStateManager.color(0.9f, 0.9f, 0.9f, 0.9f);
            hovered = (xx >= x && yy >= y && xx < x + width && yy < y + height);
            final int k = getHoverState(hovered);
            if (k == 2) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            }
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            drawTexturedModalRect(x, y, minus ? 0 : 10, 0, 10, 10);
            mouseDragged(mc, xx, yy);
        }
    }
    
    static {
        GuiPlusMinusButton.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
    }
}
