// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui;

import org.lwjgl.opengl.GL11;
import net.minecraft.inventory.Container;
import thaumcraft.common.container.ContainerHandMirror;
import net.minecraft.world.World;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;

@SideOnly(Side.CLIENT)
public class GuiHandMirror extends GuiContainer
{
    int ci;
    ResourceLocation tex;
    
    public GuiHandMirror(final InventoryPlayer par1InventoryPlayer, final World world, final int x, final int y, final int z) {
        super(new ContainerHandMirror(par1InventoryPlayer, world, x, y, z));
        ci = 0;
        tex = new ResourceLocation("thaumcraft", "textures/gui/gui_handmirror.png");
        ci = par1InventoryPlayer.currentItem;
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerForegroundLayer() {
    }
    
    protected boolean checkHotbarKeys(final int par1) {
        return false;
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        mc.renderEngine.bindTexture(tex);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int var5 = (width - xSize) / 2;
        final int var6 = (height - ySize) / 2;
        drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        final float t = zLevel;
        zLevel = 300.0f;
        drawTexturedModalRect(var5 + 8 + ci * 18, var6 + 142, 240, 0, 16, 16);
        zLevel = t;
    }
}
