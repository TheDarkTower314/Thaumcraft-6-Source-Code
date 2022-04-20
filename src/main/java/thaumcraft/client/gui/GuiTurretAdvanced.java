// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.gui.plugins.GuiToggleButton;
import net.minecraft.inventory.Container;
import thaumcraft.common.container.ContainerTurretAdvanced;
import net.minecraft.world.World;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.construct.EntityTurretCrossbowAdvanced;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;

@SideOnly(Side.CLIENT)
public class GuiTurretAdvanced extends GuiContainer
{
    EntityTurretCrossbowAdvanced turret;
    public static ResourceLocation tex;
    
    public GuiTurretAdvanced(final InventoryPlayer par1InventoryPlayer, final World world, final EntityTurretCrossbowAdvanced t) {
        super(new ContainerTurretAdvanced(par1InventoryPlayer, world, t));
        this.xSize = 175;
        this.ySize = 232;
        this.turret = t;
    }
    
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiToggleButton(1, this.guiLeft + 90, this.guiTop + 13, 8, 8, "button.turretfocus.1", new Runnable() {
            @Override
            public void run() {
                GuiToggleButton.toggled = GuiTurretAdvanced.this.turret.getTargetAnimal();
            }
        }));
        this.buttonList.add(new GuiToggleButton(2, this.guiLeft + 90, this.guiTop + 27, 8, 8, "button.turretfocus.2", new Runnable() {
            @Override
            public void run() {
                GuiToggleButton.toggled = GuiTurretAdvanced.this.turret.getTargetMob();
            }
        }));
        this.buttonList.add(new GuiToggleButton(3, this.guiLeft + 90, this.guiTop + 41, 8, 8, "button.turretfocus.3", new Runnable() {
            @Override
            public void run() {
                GuiToggleButton.toggled = GuiTurretAdvanced.this.turret.getTargetPlayer();
            }
        }));
        this.buttonList.add(new GuiToggleButton(4, this.guiLeft + 90, this.guiTop + 55, 8, 8, "button.turretfocus.4", new Runnable() {
            @Override
            public void run() {
                GuiToggleButton.toggled = GuiTurretAdvanced.this.turret.getTargetFriendly();
            }
        }));
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        this.mc.renderEngine.bindTexture(GuiTurretAdvanced.tex);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int k = (this.width - this.xSize) / 2;
        final int l = (this.height - this.ySize) / 2;
        GL11.glEnable(3042);
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        final int h = (int)(39.0f * (this.turret.getHealth() / this.turret.getMaxHealth()));
        this.drawTexturedModalRect(k + 30, l + 59, 192, 48, h, 6);
    }
    
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == 1) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 1);
        }
        else if (button.id == 2) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 2);
        }
        else if (button.id == 3) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 3);
        }
        else if (button.id == 4) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 4);
        }
        else {
            super.actionPerformed(button);
        }
    }
    
    static {
        GuiTurretAdvanced.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_turret_advanced.png");
    }
}
