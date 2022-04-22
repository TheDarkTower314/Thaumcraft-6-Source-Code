// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui;

import org.lwjgl.opengl.GL11;
import net.minecraft.inventory.Container;
import thaumcraft.common.container.ContainerTurretBasic;
import net.minecraft.world.World;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.construct.EntityTurretCrossbow;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;

@SideOnly(Side.CLIENT)
public class GuiTurretBasic extends GuiContainer
{
    EntityTurretCrossbow turret;
    ResourceLocation tex;
    
    public GuiTurretBasic(final InventoryPlayer par1InventoryPlayer, final World world, final EntityTurretCrossbow t) {
        super(new ContainerTurretBasic(par1InventoryPlayer, world, t));
        tex = new ResourceLocation("thaumcraft", "textures/gui/gui_turret_basic.png");
        xSize = 175;
        ySize = 232;
        turret = t;
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        mc.renderEngine.bindTexture(tex);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int k = (width - xSize) / 2;
        final int l = (height - ySize) / 2;
        GL11.glEnable(3042);
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
        final int h = (int)(39.0f * (turret.getHealth() / turret.getMaxHealth()));
        drawTexturedModalRect(k + 68, l + 59, 192, 48, h, 6);
    }
}
