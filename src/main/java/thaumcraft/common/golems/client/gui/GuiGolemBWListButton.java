package thaumcraft.common.golems.client.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.golems.seals.ISealConfigFilter;


public class GuiGolemBWListButton extends GuiButton
{
    ISealConfigFilter filter;
    static ResourceLocation tex;
    
    public GuiGolemBWListButton(int buttonId, int x, int y, int width, int height, ISealConfigFilter filter) {
        super(buttonId, x, y, width, height, "");
        this.filter = filter;
    }
    
    public void drawButton(Minecraft mc, int xx, int yy, float partialTicks) {
        if (visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiGolemBWListButton.tex);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            hovered = (xx >= x && yy >= y && xx < x + width && yy < y + height);
            int k = getHoverState(hovered);
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
