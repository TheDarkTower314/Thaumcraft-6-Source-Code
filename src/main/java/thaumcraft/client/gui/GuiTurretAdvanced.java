package thaumcraft.client.gui;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.gui.plugins.GuiToggleButton;
import thaumcraft.common.container.ContainerTurretAdvanced;
import thaumcraft.common.entities.construct.EntityTurretCrossbowAdvanced;


@SideOnly(Side.CLIENT)
public class GuiTurretAdvanced extends GuiContainer
{
    EntityTurretCrossbowAdvanced turret;
    public static ResourceLocation tex;
    
    public GuiTurretAdvanced(InventoryPlayer par1InventoryPlayer, World world, EntityTurretCrossbowAdvanced t) {
        super(new ContainerTurretAdvanced(par1InventoryPlayer, world, t));
        xSize = 175;
        ySize = 232;
        turret = t;
    }
    
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiToggleButton(1, guiLeft + 90, guiTop + 13, 8, 8, "button.turretfocus.1", new Runnable() {
            @Override
            public void run() {
                GuiToggleButton.toggled = turret.getTargetAnimal();
            }
        }));
        buttonList.add(new GuiToggleButton(2, guiLeft + 90, guiTop + 27, 8, 8, "button.turretfocus.2", new Runnable() {
            @Override
            public void run() {
                GuiToggleButton.toggled = turret.getTargetMob();
            }
        }));
        buttonList.add(new GuiToggleButton(3, guiLeft + 90, guiTop + 41, 8, 8, "button.turretfocus.3", new Runnable() {
            @Override
            public void run() {
                GuiToggleButton.toggled = turret.getTargetPlayer();
            }
        }));
        buttonList.add(new GuiToggleButton(4, guiLeft + 90, guiTop + 55, 8, 8, "button.turretfocus.4", new Runnable() {
            @Override
            public void run() {
                GuiToggleButton.toggled = turret.getTargetFriendly();
            }
        }));
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
    }
    
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        mc.renderEngine.bindTexture(GuiTurretAdvanced.tex);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        GL11.glEnable(3042);
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
        int h = (int)(39.0f * (turret.getHealth() / turret.getMaxHealth()));
        drawTexturedModalRect(k + 30, l + 59, 192, 48, h, 6);
    }
    
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, 1);
        }
        else if (button.id == 2) {
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, 2);
        }
        else if (button.id == 3) {
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, 3);
        }
        else if (button.id == 4) {
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, 4);
        }
        else {
            super.actionPerformed(button);
        }
    }
    
    static {
        GuiTurretAdvanced.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_turret_advanced.png");
    }
}
