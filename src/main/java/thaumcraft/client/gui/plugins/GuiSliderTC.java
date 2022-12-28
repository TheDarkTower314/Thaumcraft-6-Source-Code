package thaumcraft.client.gui.plugins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class GuiSliderTC extends GuiButton
{
    private float sliderPosition;
    public boolean isMouseDown;
    private String name;
    private float min;
    private float max;
    private boolean vertical;
    static ResourceLocation tex;
    
    public GuiSliderTC(int idIn, int x, int y, int w, int h, String name, float min, float max, float defaultValue, boolean vertical) {
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
    
    public void setMax(float max) {
        this.max = max;
        sliderPosition = 0.0f;
    }
    
    public float getSliderValue() {
        return min + (max - min) * sliderPosition;
    }
    
    public void setSliderValue(float p_175218_1_, boolean p_175218_2_) {
        sliderPosition = (p_175218_1_ - min) / (max - min);
    }
    
    public float getSliderPosition() {
        return sliderPosition;
    }
    
    protected int getHoverState(boolean mouseOver) {
        return 0;
    }
    
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
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
    
    public void setSliderPosition(float p_175219_1_) {
        sliderPosition = p_175219_1_;
    }
    
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
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
    
    public void mouseReleased(int mouseX, int mouseY) {
        isMouseDown = false;
    }
    
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float pt) {
        if (visible) {
            mc.getTextureManager().bindTexture(GuiSliderTC.tex);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            hovered = (mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height);
            int i = getHoverState(hovered);
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
        String getText(int p0, String p1, float p2);
    }
}
