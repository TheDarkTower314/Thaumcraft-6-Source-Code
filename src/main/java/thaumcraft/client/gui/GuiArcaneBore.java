// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui;

import net.minecraft.init.Blocks;
import org.lwjgl.opengl.GL11;
import net.minecraft.inventory.Container;
import thaumcraft.common.container.ContainerArcaneBore;
import net.minecraft.world.World;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.construct.EntityArcaneBore;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;

@SideOnly(Side.CLIENT)
public class GuiArcaneBore extends GuiContainer
{
    EntityArcaneBore turret;
    ResourceLocation tex;
    
    public GuiArcaneBore(final InventoryPlayer par1InventoryPlayer, final World world, final EntityArcaneBore t) {
        super(new ContainerArcaneBore(par1InventoryPlayer, world, t));
        this.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_arcanebore.png");
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
        if (this.turret.getHeldItemMainhand() != null && !this.turret.getHeldItemMainhand().isEmpty() && this.turret.getHeldItemMainhand().getItemDamage() + 1 >= this.turret.getHeldItemMainhand().getMaxDamage()) {
            this.drawTexturedModalRect(k + 80, l + 29, 240, 0, 16, 16);
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(k + 124), (float)(l + 18), 505.0f);
        GL11.glScalef(0.5f, 0.5f, 0.0f);
        String text = "Width: " + (1 + this.turret.getDigRadius() * 2);
        this.fontRenderer.drawStringWithShadow(text, 0.0f, 0.0f, 16777215);
        text = "Depth: " + this.turret.getDigDepth();
        this.fontRenderer.drawStringWithShadow(text, 64.0f, 0.0f, 16777215);
        text = "Speed: +" + this.turret.getDigSpeed(Blocks.STONE.getDefaultState());
        this.fontRenderer.drawStringWithShadow(text, 0.0f, 10.0f, 16777215);
        int base = 0;
        final int refining = this.turret.getRefining();
        final int fortune = this.turret.getFortune();
        if (this.turret.hasSilkTouch() || refining > 0 || fortune > 0) {
            text = "Other properties:";
            this.fontRenderer.drawStringWithShadow(text, 0.0f, 24.0f, 16777215);
        }
        if (refining > 0) {
            text = "Refining " + refining;
            this.fontRenderer.drawStringWithShadow(text, 4.0f, (float)(34 + base), 12632256);
            base += 9;
        }
        if (fortune > 0) {
            text = "Fortune " + fortune;
            this.fontRenderer.drawStringWithShadow(text, 4.0f, (float)(34 + base), 15648330);
            base += 9;
        }
        if (this.turret.hasSilkTouch()) {
            text = "Silk Touch";
            this.fontRenderer.drawStringWithShadow(text, 4.0f, (float)(34 + base), 8421631);
            base += 9;
        }
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
}
