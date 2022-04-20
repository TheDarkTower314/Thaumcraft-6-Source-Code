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
        this.sliderPosition = 1.0f;
        this.name = name;
        this.min = min;
        this.max = max;
        this.sliderPosition = (defaultValue - min) / (max - min);
        this.vertical = vertical;
    }
    
    public float getMax() {
        return this.max;
    }
    
    public float getMin() {
        return this.min;
    }
    
    public void setMax(final float max) {
        this.max = max;
        this.sliderPosition = 0.0f;
    }
    
    public float getSliderValue() {
        return this.min + (this.max - this.min) * this.sliderPosition;
    }
    
    public void setSliderValue(final float p_175218_1_, final boolean p_175218_2_) {
        this.sliderPosition = (p_175218_1_ - this.min) / (this.max - this.min);
    }
    
    public float getSliderPosition() {
        return this.sliderPosition;
    }
    
    protected int getHoverState(final boolean mouseOver) {
        return 0;
    }
    
    protected void mouseDragged(final Minecraft mc, final int mouseX, final int mouseY) {
        if (this.visible) {
            if (this.isMouseDown) {
                if (this.vertical) {
                    this.sliderPosition = (mouseY - (this.y + 4)) / (float)(this.height - 8);
                }
                else {
                    this.sliderPosition = (mouseX - (this.x + 4)) / (float)(this.width - 8);
                }
                if (this.sliderPosition < 0.0f) {
                    this.sliderPosition = 0.0f;
                }
                if (this.sliderPosition > 1.0f) {
                    this.sliderPosition = 1.0f;
                }
            }
            mc.getTextureManager().bindTexture(GuiSliderTC.tex);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (this.vertical) {
                this.drawTexturedModalRect(this.x, this.y + (int)(this.sliderPosition * (this.height - 8)), 20, 20, 8, 8);
            }
            else {
                this.drawTexturedModalRect(this.x + (int)(this.sliderPosition * (this.width - 8)), this.y, 20, 20, 8, 8);
            }
        }
    }
    
    public void setSliderPosition(final float p_175219_1_) {
        this.sliderPosition = p_175219_1_;
    }
    
    public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            if (this.vertical) {
                this.sliderPosition = (mouseY - (this.y + 4)) / (float)(this.height - 8);
            }
            else {
                this.sliderPosition = (mouseX - (this.x + 4)) / (float)(this.width - 8);
            }
            if (this.sliderPosition < 0.0f) {
                this.sliderPosition = 0.0f;
            }
            if (this.sliderPosition > 1.0f) {
                this.sliderPosition = 1.0f;
            }
            return this.isMouseDown = true;
        }
        return false;
    }
    
    public void mouseReleased(final int mouseX, final int mouseY) {
        this.isMouseDown = false;
    }
    
    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final float pt) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(GuiSliderTC.tex);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height);
            final int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.pushMatrix();
            if (this.vertical) {
                GlStateManager.translate((float)(this.x + 2), (float)this.y, 0.0f);
                GlStateManager.scale(1.0f, this.height / 32.0f, 1.0f);
                this.drawTexturedModalRect(0, 0, 240, 176, 4, 32);
            }
            else {
                GlStateManager.translate((float)this.x, (float)(this.y + 2), 0.0f);
                GlStateManager.scale(this.width / 32.0f, 1.0f, 1.0f);
                this.drawTexturedModalRect(0, 0, 208, 176, 32, 4);
            }
            GlStateManager.popMatrix();
            this.mouseDragged(mc, mouseX, mouseY);
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
