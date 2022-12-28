package thaumcraft.common.golems.client.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.golems.seals.ISealConfigToggles;


public class GuiGolemPropButton extends GuiButton
{
    ISealConfigToggles.SealToggle prop;
    static ResourceLocation tex;
    
    public GuiGolemPropButton(int buttonId, int x, int y, int width, int height, String buttonText, ISealConfigToggles.SealToggle prop) {
        super(buttonId, x, y, width, height, buttonText);
        this.prop = prop;
    }
    
    public void drawButton(Minecraft mc, int xx, int yy, float partialTicks) {
        if (visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiGolemPropButton.tex);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            hovered = (xx >= x && yy >= y && xx < x + width && yy < y + height);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            drawTexturedModalRect(x - 2, y - 2, 2, 18, 12, 12);
            if (prop.getValue()) {
                drawTexturedModalRect(x - 2, y - 2, 18, 18, 12, 12);
            }
            drawString(fontrenderer, I18n.translateToLocal(displayString), x + 12, y, 16777215);
            mouseDragged(mc, xx, yy);
        }
    }
    
    static {
        GuiGolemPropButton.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
    }
}
