package thaumcraft.client.gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.container.ContainerTurretBasic;
import thaumcraft.common.entities.construct.EntityTurretCrossbow;


@SideOnly(Side.CLIENT)
public class GuiTurretBasic extends GuiContainer
{
    EntityTurretCrossbow turret;
    ResourceLocation tex;
    
    public GuiTurretBasic(InventoryPlayer par1InventoryPlayer, World world, EntityTurretCrossbow t) {
        super(new ContainerTurretBasic(par1InventoryPlayer, world, t));
        tex = new ResourceLocation("thaumcraft", "textures/gui/gui_turret_basic.png");
        xSize = 175;
        ySize = 232;
        turret = t;
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
    }
    
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        mc.renderEngine.bindTexture(tex);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        GL11.glEnable(3042);
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
        int h = (int)(39.0f * (turret.getHealth() / turret.getMaxHealth()));
        drawTexturedModalRect(k + 68, l + 59, 192, 48, h, 6);
    }
}
