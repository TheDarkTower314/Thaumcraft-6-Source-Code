// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui;

import org.lwjgl.opengl.GL11;
import net.minecraft.inventory.Container;
import thaumcraft.common.container.ContainerSmelter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.essentia.TileSmelter;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;

@SideOnly(Side.CLIENT)
public class GuiSmelter extends GuiContainer
{
    private TileSmelter furnaceInventory;
    ResourceLocation tex;
    
    public GuiSmelter(final InventoryPlayer par1InventoryPlayer, final TileSmelter par2TileEntityFurnace) {
        super(new ContainerSmelter(par1InventoryPlayer, par2TileEntityFurnace));
        this.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_smelter.png");
        this.furnaceInventory = par2TileEntityFurnace;
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(this.tex);
        final int k = (this.width - this.xSize) / 2;
        final int l = (this.height - this.ySize) / 2;
        GL11.glEnable(3042);
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        if (this.furnaceInventory.getBurnTimeRemainingScaled(20) > 0) {
            final int i1 = this.furnaceInventory.getBurnTimeRemainingScaled(20);
            this.drawTexturedModalRect(k + 80, l + 26 + 20 - i1, 176, 20 - i1, 16, i1);
        }
        int i1 = this.furnaceInventory.getCookProgressScaled(46);
        this.drawTexturedModalRect(k + 106, l + 13 + 46 - i1, 216, 46 - i1, 9, i1);
        i1 = this.furnaceInventory.getVisScaled(48);
        this.drawTexturedModalRect(k + 61, l + 12 + 48 - i1, 200, 48 - i1, 8, i1);
        this.drawTexturedModalRect(k + 60, l + 8, 232, 0, 10, 55);
    }
}
