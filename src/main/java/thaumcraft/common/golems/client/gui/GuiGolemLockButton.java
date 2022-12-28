package thaumcraft.common.golems.client.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.golems.seals.ISealEntity;


public class GuiGolemLockButton extends GuiButton
{
    ISealEntity seal;
    static ResourceLocation tex;
    
    public GuiGolemLockButton(int buttonId, int x, int y, int width, int height, ISealEntity seal) {
        super(buttonId, x, y, width, height, "");
        this.seal = seal;
    }
    
    public void drawButton(Minecraft mc, int xx, int yy, float partialTicks) {
        if (visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiGolemLockButton.tex);
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
            if (seal.isLocked()) {
                drawTexturedModalRect(x, y, 32, 136, 16, 16);
            }
            else {
                drawTexturedModalRect(x, y, 48, 136, 16, 16);
            }
            if (k == 2) {
                String s = seal.isLocked() ? I18n.translateToLocal("golem.prop.lock") : I18n.translateToLocal("golem.prop.unlock");
                drawCenteredString(fontrenderer, s, x + 8, y + 17, 16777215);
            }
            mouseDragged(mc, xx, yy);
        }
    }
    
    static {
        GuiGolemLockButton.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
    }
}
