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
        this.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_turret_basic.png");
        this.xSize = 175;
        this.ySize = 232;
        this.turret = t;
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        this.mc.renderEngine.bindTexture(this.tex);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int k = (this.width - this.xSize) / 2;
        final int l = (this.height - this.ySize) / 2;
        GL11.glEnable(3042);
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        final int h = (int)(39.0f * (this.turret.getHealth() / this.turret.getMaxHealth()));
        this.drawTexturedModalRect(k + 68, l + 59, 192, 48, h, 6);
    }
}
