// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui;

import thaumcraft.common.lib.SoundsTC;
import java.io.IOException;
import org.lwjgl.opengl.GL11;
import net.minecraft.inventory.Container;
import thaumcraft.common.container.ContainerPech;
import net.minecraft.world.World;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.monster.EntityPech;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;

@SideOnly(Side.CLIENT)
public class GuiPech extends GuiContainer
{
    EntityPech pech;
    ResourceLocation tex;
    
    public GuiPech(final InventoryPlayer par1InventoryPlayer, final World world, final EntityPech pech) {
        super(new ContainerPech(par1InventoryPlayer, world, pech));
        this.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_pech.png");
        this.xSize = 175;
        this.ySize = 232;
        this.pech = pech;
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
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        GL11.glEnable(3042);
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        if (this.pech.isValued(this.inventorySlots.getSlot(0).getStack()) && !this.inventorySlots.getSlot(0).getStack().isEmpty() && this.inventorySlots.getSlot(1).getStack().isEmpty() && this.inventorySlots.getSlot(2).getStack().isEmpty() && this.inventorySlots.getSlot(3).getStack().isEmpty() && this.inventorySlots.getSlot(4).getStack().isEmpty()) {
            this.drawTexturedModalRect(var5 + 67, var6 + 24, 176, 0, 25, 25);
        }
        GL11.glDisable(3042);
    }
    
    protected void mouseClicked(final int mx, final int my, final int par3) throws IOException {
        super.mouseClicked(mx, my, par3);
        final int gx = (this.width - this.xSize) / 2;
        final int gy = (this.height - this.ySize) / 2;
        final int var7 = mx - (gx + 67);
        final int var8 = my - (gy + 24);
        if (var7 >= 0 && var8 >= 0 && var7 < 25 && var8 < 25 && this.pech.isValued(this.inventorySlots.getSlot(0).getStack()) && !this.inventorySlots.getSlot(0).getStack().isEmpty() && this.inventorySlots.getSlot(1).getStack().isEmpty() && this.inventorySlots.getSlot(2).getStack().isEmpty() && this.inventorySlots.getSlot(3).getStack().isEmpty() && this.inventorySlots.getSlot(4).getStack().isEmpty()) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 0);
            this.playButton();
        }
    }
    
    private void playButton() {
        this.mc.getRenderViewEntity().playSound(SoundsTC.pech_dice, 0.5f, 0.95f + this.mc.getRenderViewEntity().world.rand.nextFloat() * 0.1f);
    }
}
