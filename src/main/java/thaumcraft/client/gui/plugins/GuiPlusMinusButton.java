package thaumcraft.client.gui.plugins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;


public class GuiPlusMinusButton extends GuiButton
{
    boolean minus;
    static ResourceLocation tex;
    
    public GuiPlusMinusButton(int buttonId, int x, int y, int width, int height, boolean left) {
        super(buttonId, x, y, width, height, "");
        minus = false;
        minus = left;
    }
    
    public void drawButton(Minecraft mc, int xx, int yy, float pt) {
        if (visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiPlusMinusButton.tex);
            GlStateManager.color(0.9f, 0.9f, 0.9f, 0.9f);
            hovered = (xx >= x && yy >= y && xx < x + width && yy < y + height);
            int k = getHoverState(hovered);
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
