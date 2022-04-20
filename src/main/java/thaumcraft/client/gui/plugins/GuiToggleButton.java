// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui.plugins;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.renderer.GlStateManager;
import thaumcraft.client.gui.GuiTurretAdvanced;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiToggleButton extends GuiButton
{
    Runnable runnable;
    public static boolean toggled;
    
    public GuiToggleButton(final int buttonId, final int x, final int y, final int width, final int height, final String buttonText, final Runnable runnable) {
        super(buttonId, x, y, width, height, buttonText);
        this.runnable = runnable;
    }
    
    public void drawButton(final Minecraft mc, final int xx, final int yy, final float partialTicks) {
        if (this.visible) {
            this.runnable.run();
            final FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiTurretAdvanced.tex);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = (xx >= this.x && yy >= this.y && xx < this.x + this.width && yy < this.y + this.height);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            this.drawTexturedModalRect(this.x, this.y, 192, 16, 8, 8);
            if (GuiToggleButton.toggled) {
                this.drawTexturedModalRect(this.x, this.y, 192, 24, 8, 8);
            }
            this.drawString(fontrenderer, I18n.translateToLocal(this.displayString), this.x + 12, this.y, 16777215);
            this.mouseDragged(mc, xx, yy);
        }
    }
}
