package thaumcraft.client.gui;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.research.ResearchEntry;


public class ResearchToast implements IToast
{
    ResearchEntry entry;
    private long firstDrawTime;
    private boolean newDisplay;
    ResourceLocation tex;
    
    public ResearchToast(ResearchEntry entry) {
        tex = new ResourceLocation("thaumcraft", "textures/gui/hud.png");
        this.entry = entry;
    }
    
    public IToast.Visibility draw(GuiToast toastGui, long delta) {
        if (newDisplay) {
            firstDrawTime = delta;
            newDisplay = false;
        }
        toastGui.getMinecraft().getTextureManager().bindTexture(tex);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        toastGui.drawTexturedModalRect(0, 0, 0, 224, 160, 32);
        GuiResearchBrowser.drawResearchIcon(entry, 6, 8, 0.0f, false);
        toastGui.getMinecraft().fontRenderer.drawString(I18n.translateToLocal("research.complete"), 30, 7, 10631665);
        String s = entry.getLocalizedName();
        float w = (float)toastGui.getMinecraft().fontRenderer.getStringWidth(s);
        if (w > 124.0f) {
            w = 124.0f / w;
            GlStateManager.pushMatrix();
            GlStateManager.translate(30.0f, 18.0f, 0.0f);
            GlStateManager.scale(w, w, w);
            toastGui.getMinecraft().fontRenderer.drawString(s, 0, 0, 16755465);
            GlStateManager.popMatrix();
        }
        else {
            toastGui.getMinecraft().fontRenderer.drawString(s, 30, 18, 16755465);
        }
        return (delta - firstDrawTime < 5000L) ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
    }
}
