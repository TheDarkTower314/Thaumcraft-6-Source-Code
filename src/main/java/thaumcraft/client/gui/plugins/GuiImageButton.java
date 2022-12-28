package thaumcraft.client.gui.plugins;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.UtilsFX;


public class GuiImageButton extends GuiButton
{
    GuiScreen screen;
    ResourceLocation loc;
    int lx;
    int ly;
    int ww;
    int hh;
    public String description;
    public int color;
    public boolean active;
    
    public GuiImageButton(GuiScreen screen, int buttonId, int x, int y, int width, int height, String buttonText, String description, ResourceLocation loc, int lx, int ly, int ww, int hh) {
        super(buttonId, x, y, width, height, buttonText);
        active = true;
        this.description = description;
        this.screen = screen;
        color = 16777215;
        this.loc = loc;
        this.lx = lx;
        this.ly = ly;
        this.ww = ww;
        this.hh = hh;
    }
    
    public GuiImageButton(GuiScreen screen, int buttonId, int x, int y, int width, int height, String buttonText, String description, ResourceLocation loc, int lx, int ly, int ww, int hh, int color) {
        super(buttonId, x, y, width, height, buttonText);
        active = true;
        this.description = description;
        this.screen = screen;
        this.color = color;
        this.loc = loc;
        this.lx = lx;
        this.ly = ly;
        this.ww = ww;
        this.hh = hh;
    }
    
    public void drawButton(Minecraft mc, int xx, int yy, float pt) {
        if (visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            hovered = (xx >= x - width / 2 && yy >= y - height / 2 && xx < x - width / 2 + width && yy < y - height / 2 + height);
            int k = getHoverState(hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            Color c = new Color(color);
            float cc = 0.9f;
            float ac = 1.0f;
            if (k == 2) {
                ac = 1.0f;
                cc = 1.0f;
            }
            if (!active) {
                cc = 0.5f;
                ac = 0.9f;
            }
            GlStateManager.color(cc * (c.getRed() / 255.0f), cc * (c.getGreen() / 255.0f), cc * (c.getBlue() / 255.0f), ac);
            mc.getTextureManager().bindTexture(loc);
            drawTexturedModalRect(x - ww / 2, y - hh / 2, lx, ly, ww, hh);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (displayString != null) {
                int j = 16777215;
                if (!enabled) {
                    j = 10526880;
                }
                else if (hovered) {
                    j = 16777120;
                }
                GL11.glPushMatrix();
                GL11.glTranslated(x, y, 0.0);
                GL11.glScaled(0.5, 0.5, 0.0);
                drawCenteredString(fontrenderer, new TextComponentTranslation(displayString).getFormattedText(), 0, -4, j);
                GL11.glPopMatrix();
            }
            mouseDragged(mc, xx, yy);
        }
    }
    
    public void drawButtonForegroundLayer(int xx, int yy) {
        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
        zLevel += 90.0f;
        ArrayList<String> text = new ArrayList<String>();
        if (displayString != null) {
            text.add(displayString);
        }
        int m = 8;
        if (description != null) {
            m = 0;
            text.add("§o§9" + description);
        }
        UtilsFX.drawCustomTooltip(screen, fontrenderer, text, xx + 4, yy + m, -99);
        zLevel -= 90.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return active && enabled && visible && mouseX >= x - width / 2 && mouseY >= y - height / 2 && mouseX < x - width / 2 + width && mouseY < y - height / 2 + height;
    }
}
