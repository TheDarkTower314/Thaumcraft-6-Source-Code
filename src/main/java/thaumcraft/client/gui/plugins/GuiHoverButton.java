package thaumcraft.client.gui.plugins;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;


@SideOnly(Side.CLIENT)
public class GuiHoverButton extends GuiButton
{
    String description;
    GuiScreen screen;
    int color;
    Object tex;
    
    public GuiHoverButton(GuiScreen screen, int buttonId, int x, int y, int width, int height, String buttonText, String description, Object tex) {
        super(buttonId, x, y, width, height, buttonText);
        this.tex = null;
        this.description = description;
        this.tex = tex;
        this.screen = screen;
        color = 16777215;
    }
    
    public GuiHoverButton(GuiScreen screen, int buttonId, int x, int y, int width, int height, String buttonText, String description, Object tex, int color) {
        super(buttonId, x, y, width, height, buttonText);
        this.tex = null;
        this.description = description;
        this.tex = tex;
        this.screen = screen;
        this.color = color;
    }
    
    public void drawButton(Minecraft mc, int xx, int yy, float pt) {
        if (visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            Color c = new Color(color);
            GlStateManager.color(0.9f * (c.getRed() / 255.0f), 0.9f * (c.getGreen() / 255.0f), 0.9f * (c.getBlue() / 255.0f), 0.9f);
            hovered = (xx >= x - width / 2 && yy >= y - height / 2 && xx < x - width / 2 + width && yy < y - height / 2 + height);
            int k = getHoverState(hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            if (k == 2) {
                GlStateManager.color(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 1.0f);
            }
            if (tex instanceof Aspect) {
                mc.getTextureManager().bindTexture(((Aspect) tex).getImage());
                Color c2 = new Color(((Aspect) tex).getColor());
                if (k != 2) {
                    GlStateManager.color(c2.getRed() / 290.0f, c2.getGreen() / 290.0f, c2.getBlue() / 290.0f, 0.9f);
                }
                else {
                    GlStateManager.color(c2.getRed() / 255.0f, c2.getGreen() / 255.0f, c2.getBlue() / 255.0f, 1.0f);
                }
                drawModalRectWithCustomSizedTexture(x - width / 2, y - height / 2, 0.0f, 0.0f, 16, 16, 16.0f, 16.0f);
            }
            if (tex instanceof ResourceLocation) {
                mc.getTextureManager().bindTexture((ResourceLocation) tex);
                drawModalRectWithCustomSizedTexture(x - width / 2, y - height / 2, 0.0f, 0.0f, 16, 16, 16.0f, 16.0f);
            }
            if (tex instanceof TextureAtlasSprite) {
                drawTexturedModalRect(x - width / 2, y - height / 2, (TextureAtlasSprite) tex, 16, 16);
            }
            if (tex instanceof ItemStack) {
                zLevel -= 90.0f;
                UtilsFX.renderItemStackShaded(mc, (ItemStack) tex, x - width / 2, y - height / 2 - ((k == 2) ? 1 : 0), null, 1.0f);
                zLevel += 90.0f;
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            mouseDragged(mc, xx, yy);
        }
    }
    
    public void drawButtonForegroundLayer(int xx, int yy) {
        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
        zLevel += 90.0f;
        List<String> text = new ArrayList<String>();
        if (tex instanceof ItemStack) {
            text = ((ItemStack) tex).getTooltip(Minecraft.getMinecraft().player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
            int qq = 0;
            for (String s : text) {
                if (s.endsWith(" " + TextFormatting.RESET)) {
                    text = text.subList(0, qq);
                    break;
                }
                ++qq;
            }
        }
        else {
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
        return false;
    }
}
