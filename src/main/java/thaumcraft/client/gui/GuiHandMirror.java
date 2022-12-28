package thaumcraft.client.gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.container.ContainerHandMirror;


@SideOnly(Side.CLIENT)
public class GuiHandMirror extends GuiContainer
{
    int ci;
    ResourceLocation tex;
    
    public GuiHandMirror(InventoryPlayer par1InventoryPlayer, World world, int x, int y, int z) {
        super(new ContainerHandMirror(par1InventoryPlayer, world, x, y, z));
        ci = 0;
        tex = new ResourceLocation("thaumcraft", "textures/gui/gui_handmirror.png");
        ci = par1InventoryPlayer.currentItem;
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerForegroundLayer() {
    }
    
    protected boolean checkHotbarKeys(int par1) {
        return false;
    }
    
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        mc.renderEngine.bindTexture(tex);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        int var5 = (width - xSize) / 2;
        int var6 = (height - ySize) / 2;
        drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        float t = zLevel;
        zLevel = 300.0f;
        drawTexturedModalRect(var5 + 8 + ci * 18, var6 + 142, 240, 0, 16, 16);
        zLevel = t;
    }
}
