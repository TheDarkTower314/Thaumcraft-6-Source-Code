// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui.plugins;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.casters.NodeSetting;
import net.minecraft.client.gui.GuiButton;

public class GuiFocusSettingSpinnerButton extends GuiButton
{
    private NodeSetting setting;
    static ResourceLocation tex;
    
    public GuiFocusSettingSpinnerButton(final int buttonId, final int x, final int y, final int width, final NodeSetting ns) {
        super(buttonId, x, y, width, 10, "");
        setting = ns;
    }
    
    public void drawButton(final Minecraft mc, final int xx, final int yy, final float pt) {
        if (visible) {
            final FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiFocusSettingSpinnerButton.tex);
            GlStateManager.color(0.9f, 0.9f, 0.9f, 0.9f);
            hovered = (xx >= x && yy >= y && xx < x + width + 10 && yy < y + height);
            final int k = getHoverState(hovered);
            if (k == 2) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            }
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            drawTexturedModalRect(x, y, 20, 0, 10, 10);
            drawTexturedModalRect(x + width, y, 30, 0, 10, 10);
            final String s = setting.getValueText();
            fontrenderer.drawStringWithShadow(s, (float)(x + (width + 10) / 2 - fontrenderer.getStringWidth(s) / 2), (float)(y + 1), 16777215);
            mouseDragged(mc, xx, yy);
        }
    }
    
    public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
        if (enabled && visible) {
            if (mouseX >= x && mouseY >= y && mouseX < x + 10 && mouseY < y + height) {
                setting.decrement();
                return true;
            }
            if (mouseX >= x + width && mouseY >= y && mouseX < x + width + 10 && mouseY < y + height) {
                setting.increment();
                return true;
            }
        }
        return false;
    }
    
    static {
        GuiFocusSettingSpinnerButton.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
    }
}
