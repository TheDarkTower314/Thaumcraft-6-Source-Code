package thaumcraft.common.golems.client.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;


class GuiGolemCategoryButton extends GuiButton
{
    int icon;
    boolean active;
    static ResourceLocation tex;
    
    public GuiGolemCategoryButton(int buttonId, int x, int y, int width, int height, String buttonText, int i, boolean act) {
        super(buttonId, x, y, width, height, buttonText);
        icon = i;
        active = act;
    }
    
    public void drawButton(Minecraft mc, int xx, int yy, float partialTicks) {
        if (visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiGolemCategoryButton.tex);
            GlStateManager.color(0.9f, 0.9f, 0.9f, 0.9f);
            hovered = (xx >= x - 8 && yy >= y - 8 && xx < x - 8 + width && yy < y - 8 + height);
            int k = getHoverState(hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            if (active) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            }
            else if (k != 2) {
                GlStateManager.color(0.7f, 0.7f, 0.7f, 0.7f);
            }
            drawTexturedModalRect(x - 8, y - 8, icon * 16, 120, 16, 16);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (k == 2) {
                zLevel += 90.0f;
                String s = I18n.translateToLocal(displayString);
                drawString(fontrenderer, s, x - 10 - fontrenderer.getStringWidth(s), y - 4, 16777215);
                zLevel -= 90.0f;
            }
            mouseDragged(mc, xx, yy);
        }
    }
    
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return enabled && visible && mouseX >= x - 8 && mouseY >= y - 8 && mouseX < x - 8 + width && mouseY < y - 8 + height;
    }
    
    static {
        GuiGolemCategoryButton.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
    }
}
