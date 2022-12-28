package thaumcraft.client.gui;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.container.ContainerSpa;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.devices.TileSpa;


@SideOnly(Side.CLIENT)
public class GuiSpa extends GuiContainer
{
    private TileSpa spa;
    private float xSize_lo;
    private float ySize_lo;
    ResourceLocation tex;
    
    public GuiSpa(InventoryPlayer par1InventoryPlayer, TileSpa teSpa) {
        super(new ContainerSpa(par1InventoryPlayer, teSpa));
        tex = new ResourceLocation("thaumcraft", "textures/gui/gui_spa.png");
        spa = teSpa;
    }
    
    public void drawScreen(int par1, int par2, float par3) {
        drawDefaultBackground();
        super.drawScreen(par1, par2, par3);
        xSize_lo = (float)par1;
        ySize_lo = (float)par2;
        int baseX = guiLeft;
        int baseY = guiTop;
        int mposx = par1 - (baseX + 104);
        int mposy = par2 - (baseY + 10);
        if (mposx >= 0 && mposy >= 0 && mposx < 10 && mposy < 55) {
            List list = new ArrayList();
            FluidStack fluid = spa.tank.getFluid();
            if (fluid != null) {
                list.add(fluid.getFluid().getLocalizedName(fluid));
                list.add(fluid.amount + " mb");
                drawHoveringText(list, par1, par2, fontRenderer);
            }
        }
        mposx = par1 - (baseX + 88);
        mposy = par2 - (baseY + 34);
        if (mposx >= 0 && mposy >= 0 && mposx < 10 && mposy < 10) {
            List list = new ArrayList();
            if (spa.getMix()) {
                list.add(I18n.translateToLocal("text.spa.mix.true"));
            }
            else {
                list.add(I18n.translateToLocal("text.spa.mix.false"));
            }
            drawHoveringText(list, par1, par2, fontRenderer);
        }
        renderHoveredToolTip(par2, par2);
    }
    
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(tex);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
        if (spa.getMix()) {
            drawTexturedModalRect(k + 89, l + 35, 208, 16, 8, 8);
        }
        else {
            drawTexturedModalRect(k + 89, l + 35, 208, 32, 8, 8);
        }
        if (spa.tank.getFluidAmount() > 0) {
            FluidStack fluid = spa.tank.getFluid();
            if (fluid != null) {
                TextureAtlasSprite icon = func_175371_a(fluid.getFluid().getBlock());
                if (icon != null) {
                    float bar = spa.tank.getFluidAmount() / (float) spa.tank.getCapacity();
                    renderFluid(icon, fluid.getFluid());
                    mc.renderEngine.bindTexture(tex);
                    drawTexturedModalRect(k + 107, l + 15, 107, 15, 10, (int)(48.0f - 48.0f * bar));
                }
            }
        }
        drawTexturedModalRect(k + 106, l + 11, 232, 0, 10, 55);
        GL11.glDisable(3042);
    }
    
    private TextureAtlasSprite func_175371_a(Block p_175371_1_) {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(p_175371_1_.getDefaultState());
    }
    
    public void renderFluid(TextureAtlasSprite icon, Fluid fluid) {
        GL11.glPushMatrix();
        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        Color cc = new Color(fluid.getColor());
        GL11.glColor3f(cc.getRed() / 255.0f, cc.getGreen() / 255.0f, cc.getBlue() / 255.0f);
        for (int a = 0; a < 6; ++a) {
            drawTexturedModalRect((guiLeft + 107) * 2, (guiTop + 15) * 2 + a * 16, icon, 16, 16);
        }
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    protected void mouseClicked(int mx, int my, int par3) throws IOException {
        super.mouseClicked(mx, my, par3);
        int gx = (width - xSize) / 2;
        int gy = (height - ySize) / 2;
        int var7 = mx - (gx + 89);
        int var8 = my - (gy + 35);
        if (var7 >= 0 && var8 >= 0 && var7 < 8 && var8 < 8) {
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, 1);
            playButtonClick();
        }
    }
    
    private void playButtonClick() {
        mc.getRenderViewEntity().playSound(SoundsTC.clack, 0.4f, 1.0f);
    }
}
