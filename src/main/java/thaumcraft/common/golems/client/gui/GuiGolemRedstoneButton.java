// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.seals.ISealEntity;
import net.minecraft.client.gui.GuiButton;

public class GuiGolemRedstoneButton extends GuiButton
{
    ISealEntity seal;
    static ResourceLocation tex;
    
    public GuiGolemRedstoneButton(final int buttonId, final int x, final int y, final int width, final int height, final ISealEntity seal) {
        super(buttonId, x, y, width, height, "");
        this.seal = seal;
    }
    
    public void drawButton(final Minecraft mc, final int xx, final int yy, final float partialTicks) {
        if (this.visible) {
            final FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiGolemRedstoneButton.tex);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = (xx >= this.x && yy >= this.y && xx < this.x + this.width && yy < this.y + this.height);
            final int k = this.getHoverState(this.hovered);
            if (k == 2) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            }
            else {
                GlStateManager.color(0.9f, 0.9f, 0.9f, 0.9f);
            }
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            if (this.seal.isRedstoneSensitive()) {
                this.drawTexturedModalRect(this.x, this.y, 64, 136, 16, 16);
            }
            else {
                this.drawTexturedModalRect(this.x, this.y, 80, 136, 16, 16);
            }
            if (k == 2) {
                this.zLevel += 90.0f;
                final String s = this.seal.isRedstoneSensitive() ? I18n.translateToLocal("golem.prop.redon") : I18n.translateToLocal("golem.prop.redoff");
                this.drawString(fontrenderer, s, this.x - 2 - fontrenderer.getStringWidth(s), this.y + 4, 16777215);
                this.zLevel -= 90.0f;
            }
            this.mouseDragged(mc, xx, yy);
        }
    }
    
    static {
        GuiGolemRedstoneButton.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
    }
}
