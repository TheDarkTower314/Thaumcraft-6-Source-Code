package thaumcraft.client.gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.container.ContainerFocusPouch;


@SideOnly(Side.CLIENT)
public class GuiFocusPouch extends GuiContainer
{
    private int blockSlot;
    ResourceLocation tex;
    
    public GuiFocusPouch(InventoryPlayer par1InventoryPlayer, World world, int x, int y, int z) {
        super(new ContainerFocusPouch(par1InventoryPlayer, world, x, y, z));
        tex = new ResourceLocation("thaumcraft", "textures/gui/gui_focuspouch.png");
        blockSlot = par1InventoryPlayer.currentItem;
        xSize = 175;
        ySize = 232;
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        mc.renderEngine.bindTexture(tex);
        float t = zLevel;
        zLevel = 300.0f;
        GL11.glEnable(3042);
        drawTexturedModalRect(8 + blockSlot * 18, 209, 240, 0, 16, 16);
        GL11.glDisable(3042);
        zLevel = t;
    }
    
    protected boolean checkHotbarKeys(int par1) {
        return false;
    }
    
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        if (mc.player.inventory.mainInventory.get(blockSlot).isEmpty()) {
            mc.player.closeScreen();
        }
        mc.renderEngine.bindTexture(tex);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        int var5 = (width - xSize) / 2;
        int var6 = (height - ySize) / 2;
        GL11.glEnable(3042);
        drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);
        GL11.glDisable(3042);
    }
}
