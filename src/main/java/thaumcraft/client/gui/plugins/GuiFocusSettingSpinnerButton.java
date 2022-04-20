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
        this.setting = ns;
    }
    
    public void drawButton(final Minecraft mc, final int xx, final int yy, final float pt) {
        if (this.visible) {
            final FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiFocusSettingSpinnerButton.tex);
            GlStateManager.color(0.9f, 0.9f, 0.9f, 0.9f);
            this.hovered = (xx >= this.x && yy >= this.y && xx < this.x + this.width + 10 && yy < this.y + this.height);
            final int k = this.getHoverState(this.hovered);
            if (k == 2) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            }
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            this.drawTexturedModalRect(this.x, this.y, 20, 0, 10, 10);
            this.drawTexturedModalRect(this.x + this.width, this.y, 30, 0, 10, 10);
            final String s = this.setting.getValueText();
            fontrenderer.drawStringWithShadow(s, (float)(this.x + (this.width + 10) / 2 - fontrenderer.getStringWidth(s) / 2), (float)(this.y + 1), 16777215);
            this.mouseDragged(mc, xx, yy);
        }
    }
    
    public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
        if (this.enabled && this.visible) {
            if (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + 10 && mouseY < this.y + this.height) {
                this.setting.decrement();
                return true;
            }
            if (mouseX >= this.x + this.width && mouseY >= this.y && mouseX < this.x + this.width + 10 && mouseY < this.y + this.height) {
                this.setting.increment();
                return true;
            }
        }
        return false;
    }
    
    static {
        GuiFocusSettingSpinnerButton.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
    }
}
