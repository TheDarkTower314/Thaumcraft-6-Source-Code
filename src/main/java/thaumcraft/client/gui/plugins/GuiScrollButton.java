// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui.plugins;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.gui.GuiButton;

public class GuiScrollButton extends GuiButton
{
    boolean minus;
    boolean vertical;
    static ResourceLocation tex;
    
    public GuiScrollButton(final int buttonId, final int x, final int y, final int width, final int height, final boolean minus, final boolean vertical) {
        super(buttonId, x, y, width, height, "");
        this.minus = false;
        this.vertical = false;
        this.minus = minus;
        this.vertical = vertical;
    }
    
    public GuiScrollButton(final int buttonId, final int x, final int y, final int width, final int height, final boolean minus) {
        super(buttonId, x, y, width, height, "");
        this.minus = false;
        vertical = false;
        this.minus = minus;
    }
    
    public void drawButton(final Minecraft mc, final int xx, final int yy, final float pt) {
        if (visible) {
            final FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiScrollButton.tex);
            GlStateManager.color(0.9f, 0.9f, 0.9f, 0.9f);
            hovered = (xx >= x && yy >= y && xx < x + width && yy < y + height);
            final int k = getHoverState(hovered);
            if (k == 2) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            }
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            drawTexturedModalRect(x, y, vertical ? 67 : (minus ? 20 : 30), vertical ? (minus ? 0 : 10) : 0, 10, 10);
            mouseDragged(mc, xx, yy);
        }
    }
    
    static {
        GuiScrollButton.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
    }
}
