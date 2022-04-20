// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.gui.GuiButton;

public class GuiGolemCraftButton extends GuiButton
{
    static ResourceLocation tex;
    
    public GuiGolemCraftButton(final int buttonId, final int x, final int y) {
        super(buttonId, x, y, 24, 16, "");
    }
    
    public void drawButton(final Minecraft mc, final int xx, final int yy, final float partialTicks) {
        if (this.visible) {
            final FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiGolemCraftButton.tex);
            GlStateManager.color(0.9f, 0.9f, 0.9f, 0.9f);
            this.hovered = (xx >= this.x && yy >= this.y && xx < this.x + this.width && yy < this.y + this.height);
            final int k = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            if (this.enabled && k == 2) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            }
            this.drawTexturedModalRect(this.x, this.y, 216, 64, 24, 16);
            if (!this.enabled) {
                this.drawTexturedModalRect(this.x, this.y, 216, 40, 24, 16);
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.mouseDragged(mc, xx, yy);
        }
    }
    
    static {
        GuiGolemCraftButton.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_golembuilder.png");
    }
}
