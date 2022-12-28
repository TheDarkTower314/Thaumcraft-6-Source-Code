package thaumcraft.client.gui.plugins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;


public class GuiScrollButton extends GuiButton
{
    boolean minus;
    boolean vertical;
    static ResourceLocation tex;
    
    public GuiScrollButton(int buttonId, int x, int y, int width, int height, boolean minus, boolean vertical) {
        super(buttonId, x, y, width, height, "");
        this.minus = false;
        this.vertical = false;
        this.minus = minus;
        this.vertical = vertical;
    }
    
    public GuiScrollButton(int buttonId, int x, int y, int width, int height, boolean minus) {
        super(buttonId, x, y, width, height, "");
        this.minus = false;
        vertical = false;
        this.minus = minus;
    }
    
    public void drawButton(Minecraft mc, int xx, int yy, float pt) {
        if (visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiScrollButton.tex);
            GlStateManager.color(0.9f, 0.9f, 0.9f, 0.9f);
            hovered = (xx >= x && yy >= y && xx < x + width && yy < y + height);
            int k = getHoverState(hovered);
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
