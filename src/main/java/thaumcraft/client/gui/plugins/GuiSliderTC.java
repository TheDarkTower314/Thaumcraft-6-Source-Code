// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui.plugins;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.gui.GuiButton;

public class GuiSliderTC extends GuiButton
{
    private float sliderPosition;
    public boolean isMouseDown;
    private final String name;
    private final float min;
    private float max;
    private final boolean vertical;
    static ResourceLocation tex;
    
    public GuiSliderTC(final int idIn, final int x, final int y, final int w, final int h, final String name, final float min, final float max, final float defaultValue, final boolean vertical) {
        super(idIn, x, y, w, h, "");
        sliderPosition = 1.0f;
        this.name = name;
        this.min = min;
        this.max = max;
        sliderPosition = (defaultValue - min) / (max - min);
        this.vertical = vertical;
    }
    
    public float getMax() {
        return max;
    }
    
    public float getMin() {
        return min;
    }
    
    public void setMax(final float max) {
        this.max = max;
        sliderPosition = 0.0f;
    }
    
    public float getSliderValue() {
        return min + (max - min) * sliderPosition;
    }
    
    public void setSliderValue(final float p_175218_1_, final boolean p_175218_2_) {
        sliderPosition = (p_175218_1_ - min) / (max - min);
    }
    
    public float getSliderPosition() {
        return sliderPosition;
    }
    
    protected int getHoverState(final boolean mouseOver) {
        return 0;
    }
    
    protected void mouseDragged(final Minecraft mc, final int mouseX, final int mouseY) {
        if (visible) {
            if (isMouseDown) {
                if (vertical) {
                    sliderPosition = (mouseY - (y + 4)) / (float)(height - 8);
                }
                else {
                    sliderPosition = (mouseX - (x + 4)) / (float)(width - 8);
                }
                if (sliderPosition < 0.0f) {
                    sliderPosition = 0.0f;
                }
                if (sliderPosition > 1.0f) {
                    sliderPosition = 1.0f;
                }
            }
            mc.getTextureManager().bindTexture(GuiSliderTC.tex);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (vertical) {
                drawTexturedModalRect(x, y + (int)(sliderPosition * (height - 8)), 20, 20, 8, 8);
            }
            else {
                drawTexturedModalRect(x + (int)(sliderPosition * (width - 8)), y, 20, 20, 8, 8);
            }
        }
    }
    
    public void setSliderPosition(final float p_175219_1_) {
        sliderPosition = p_175219_1_;
    }
    
    public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            if (vertical) {
                sliderPosition = (mouseY - (y + 4)) / (float)(height - 8);
            }
            else {
                sliderPosition = (mouseX - (x + 4)) / (float)(width - 8);
            }
            if (sliderPosition < 0.0f) {
                sliderPosition = 0.0f;
            }
            if (sliderPosition > 1.0f) {
                sliderPosition = 1.0f;
            }
            return isMouseDown = true;
        }
        return false;
    }
    
    public void mouseReleased(final int mouseX, final int mouseY) {
        isMouseDown = false;
    }
    
    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final float pt) {
        if (visible) {
            mc.getTextureManager().bindTexture(GuiSliderTC.tex);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            hovered = (mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height);
            final int i = getHoverState(hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.pushMatrix();
            if (vertical) {
                GlStateManager.translate((float)(x + 2), (float) y, 0.0f);
                GlStateManager.scale(1.0f, height / 32.0f, 1.0f);
                drawTexturedModalRect(0, 0, 240, 176, 4, 32);
            }
            else {
                GlStateManager.translate((float) x, (float)(y + 2), 0.0f);
                GlStateManager.scale(width / 32.0f, 1.0f, 1.0f);
                drawTexturedModalRect(0, 0, 208, 176, 32, 4);
            }
            GlStateManager.popMatrix();
            mouseDragged(mc, mouseX, mouseY);
        }
    }
    
    static {
        GuiSliderTC.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
    }
    
    @SideOnly(Side.CLIENT)
    public interface FormatHelper
    {
        String getText(final int p0, final String p1, final float p2);
    }
}
