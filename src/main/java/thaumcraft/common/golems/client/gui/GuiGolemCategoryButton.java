// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.gui.GuiButton;

class GuiGolemCategoryButton extends GuiButton
{
    int icon;
    boolean active;
    static ResourceLocation tex;
    
    public GuiGolemCategoryButton(final int buttonId, final int x, final int y, final int width, final int height, final String buttonText, final int i, final boolean act) {
        super(buttonId, x, y, width, height, buttonText);
        this.icon = i;
        this.active = act;
    }
    
    public void drawButton(final Minecraft mc, final int xx, final int yy, final float partialTicks) {
        if (this.visible) {
            final FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiGolemCategoryButton.tex);
            GlStateManager.color(0.9f, 0.9f, 0.9f, 0.9f);
            this.hovered = (xx >= this.x - 8 && yy >= this.y - 8 && xx < this.x - 8 + this.width && yy < this.y - 8 + this.height);
            final int k = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            if (this.active) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            }
            else if (k != 2) {
                GlStateManager.color(0.7f, 0.7f, 0.7f, 0.7f);
            }
            this.drawTexturedModalRect(this.x - 8, this.y - 8, this.icon * 16, 120, 16, 16);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (k == 2) {
                this.zLevel += 90.0f;
                final String s = I18n.translateToLocal(this.displayString);
                this.drawString(fontrenderer, s, this.x - 10 - fontrenderer.getStringWidth(s), this.y - 4, 16777215);
                this.zLevel -= 90.0f;
            }
            this.mouseDragged(mc, xx, yy);
        }
    }
    
    public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
        return this.enabled && this.visible && mouseX >= this.x - 8 && mouseY >= this.y - 8 && mouseX < this.x - 8 + this.width && mouseY < this.y - 8 + this.height;
    }
    
    static {
        GuiGolemCategoryButton.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
    }
}
