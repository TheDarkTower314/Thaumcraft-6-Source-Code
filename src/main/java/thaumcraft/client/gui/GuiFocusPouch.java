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
        tex = new ResourceLocation("thaumcraft", "textures/gui/gui_focuspouch.png");
        blockSlot = par1InventoryPlayer.currentItem;
        xSize = 175;
        ySize = 232;
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        mc.renderEngine.bindTexture(tex);
        final float t = zLevel;
        zLevel = 300.0f;
        GL11.glEnable(3042);
        drawTexturedModalRect(8 + blockSlot * 18, 209, 240, 0, 16, 16);
        GL11.glDisable(3042);
        zLevel = t;
    }
    
    protected boolean checkHotbarKeys(final int par1) {
        return false;
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        if (mc.player.inventory.mainInventory.get(blockSlot).isEmpty()) {
            mc.player.closeScreen();
        }
        mc.renderEngine.bindTexture(tex);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int var5 = (width - xSize) / 2;
        final int var6 = (height - ySize) / 2;
        GL11.glEnable(3042);
        drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);
        GL11.glDisable(3042);
    }
}
