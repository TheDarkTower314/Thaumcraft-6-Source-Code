package thaumcraft.client.gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.container.ContainerSmelter;
import thaumcraft.common.tiles.essentia.TileSmelter;


@SideOnly(Side.CLIENT)
public class GuiSmelter extends GuiContainer
{
    private TileSmelter furnaceInventory;
    ResourceLocation tex;
    
    public GuiSmelter(InventoryPlayer par1InventoryPlayer, TileSmelter par2TileEntityFurnace) {
        super(new ContainerSmelter(par1InventoryPlayer, par2TileEntityFurnace));
        tex = new ResourceLocation("thaumcraft", "textures/gui/gui_smelter.png");
        furnaceInventory = par2TileEntityFurnace;
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
    }
    
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(tex);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        GL11.glEnable(3042);
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
        if (furnaceInventory.getBurnTimeRemainingScaled(20) > 0) {
            int i1 = furnaceInventory.getBurnTimeRemainingScaled(20);
            drawTexturedModalRect(k + 80, l + 26 + 20 - i1, 176, 20 - i1, 16, i1);
        }
        int i1 = furnaceInventory.getCookProgressScaled(46);
        drawTexturedModalRect(k + 106, l + 13 + 46 - i1, 216, 46 - i1, 9, i1);
        i1 = furnaceInventory.getVisScaled(48);
        drawTexturedModalRect(k + 61, l + 12 + 48 - i1, 200, 48 - i1, 8, i1);
        drawTexturedModalRect(k + 60, l + 8, 232, 0, 10, 55);
    }
}
