// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui;

import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import net.minecraft.inventory.Container;
import thaumcraft.common.container.ContainerFocusPouch;
import net.minecraft.world.World;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;

@SideOnly(Side.CLIENT)
public class GuiFocusPouch extends GuiContainer
{
    private int blockSlot;
    ResourceLocation tex;
    
    public GuiFocusPouch(final InventoryPlayer par1InventoryPlayer, final World world, final int x, final int y, final int z) {
        super(new ContainerFocusPouch(par1InventoryPlayer, world, x, y, z));
        this.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_focuspouch.png");
        this.blockSlot = par1InventoryPlayer.currentItem;
        this.xSize = 175;
        this.ySize = 232;
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        this.mc.renderEngine.bindTexture(this.tex);
        final float t = this.zLevel;
        this.zLevel = 300.0f;
        GL11.glEnable(3042);
        this.drawTexturedModalRect(8 + this.blockSlot * 18, 209, 240, 0, 16, 16);
        GL11.glDisable(3042);
        this.zLevel = t;
    }
    
    protected boolean checkHotbarKeys(final int par1) {
        return false;
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        if (this.mc.player.inventory.mainInventory.get(this.blockSlot).isEmpty()) {
            this.mc.player.closeScreen();
        }
        this.mc.renderEngine.bindTexture(this.tex);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        GL11.glEnable(3042);
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        GL11.glDisable(3042);
    }
}
