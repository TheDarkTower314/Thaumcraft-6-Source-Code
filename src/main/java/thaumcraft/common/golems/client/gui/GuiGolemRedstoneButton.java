package thaumcraft.common.golems.client.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.golems.seals.ISealEntity;


public class GuiGolemRedstoneButton extends GuiButton
{
    ISealEntity seal;
    static ResourceLocation tex;
    
    public GuiGolemRedstoneButton(int buttonId, int x, int y, int width, int height, ISealEntity seal) {
        super(buttonId, x, y, width, height, "");
        this.seal = seal;
    }
    
    public void drawButton(Minecraft mc, int xx, int yy, float partialTicks) {
        if (visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiGolemRedstoneButton.tex);
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
            if (seal.isRedstoneSensitive()) {
                drawTexturedModalRect(x, y, 64, 136, 16, 16);
            }
            else {
                drawTexturedModalRect(x, y, 80, 136, 16, 16);
            }
            if (k == 2) {
                zLevel += 90.0f;
                String s = seal.isRedstoneSensitive() ? I18n.translateToLocal("golem.prop.redon") : I18n.translateToLocal("golem.prop.redoff");
                drawString(fontrenderer, s, x - 2 - fontrenderer.getStringWidth(s), y + 4, 16777215);
                zLevel -= 90.0f;
            }
            mouseDragged(mc, xx, yy);
        }
    }
    
    static {
        GuiGolemRedstoneButton.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
    }
}
