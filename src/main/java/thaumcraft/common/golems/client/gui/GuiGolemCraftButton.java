package thaumcraft.common.golems.client.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;


public class GuiGolemCraftButton extends GuiButton
{
    static ResourceLocation tex;
    
    public GuiGolemCraftButton(int buttonId, int x, int y) {
        super(buttonId, x, y, 24, 16, "");
    }
    
    public void drawButton(Minecraft mc, int xx, int yy, float partialTicks) {
        if (visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiGolemCraftButton.tex);
            GlStateManager.color(0.9f, 0.9f, 0.9f, 0.9f);
            hovered = (xx >= x && yy >= y && xx < x + width && yy < y + height);
            int k = getHoverState(hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            if (enabled && k == 2) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            }
            drawTexturedModalRect(x, y, 216, 64, 24, 16);
            if (!enabled) {
                drawTexturedModalRect(x, y, 216, 40, 24, 16);
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            mouseDragged(mc, xx, yy);
        }
    }
    
    static {
        GuiGolemCraftButton.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_golembuilder.png");
    }
}
