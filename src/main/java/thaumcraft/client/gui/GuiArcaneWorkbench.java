// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.text.translation.I18n;
import java.awt.Color;
import thaumcraft.common.blocks.world.ore.ShardType;
import net.minecraft.client.renderer.GlStateManager;
import thaumcraft.common.items.casters.CasterManager;
import net.minecraft.inventory.InventoryCrafting;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import org.lwjgl.opengl.GL11;
import net.minecraft.inventory.Container;
import thaumcraft.common.container.ContainerArcaneWorkbench;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.InventoryPlayer;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;

@SideOnly(Side.CLIENT)
public class GuiArcaneWorkbench extends GuiContainer
{
    private TileArcaneWorkbench tileEntity;
    private InventoryPlayer ip;
    private int[][] aspectLocs;
    ResourceLocation tex;
    
    public GuiArcaneWorkbench(final InventoryPlayer par1InventoryPlayer, final TileArcaneWorkbench e) {
        super(new ContainerArcaneWorkbench(par1InventoryPlayer, e));
        this.aspectLocs = new int[][] { { 72, 21 }, { 24, 43 }, { 24, 102 }, { 72, 124 }, { 120, 102 }, { 120, 43 } };
        this.tex = new ResourceLocation("thaumcraft", "textures/gui/arcaneworkbench.png");
        this.tileEntity = e;
        this.ip = par1InventoryPlayer;
        this.ySize = 234;
        this.xSize = 190;
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        this.mc.renderEngine.bindTexture(this.tex);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3042);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        int cost = 0;
        int discount = 0;
        final IArcaneRecipe result = ThaumcraftCraftingManager.findMatchingArcaneRecipe(this.tileEntity.inventoryCraft, this.ip.player);
        AspectList crystals = null;
        final float df = CasterManager.getTotalVisDiscount(this.ip.player);
        if (result != null) {
            cost = result.getVis();
            cost *= (int)(1.0f - df);
            discount = (int)(df * 100.0f);
            crystals = result.getCrystals();
        }
        if (crystals != null) {
            GlStateManager.blendFunc(770, 1);
            for (final Aspect a : crystals.getAspects()) {
                final int id = ShardType.getMetaByAspect(a);
                final Color col = new Color(a.getColor());
                GL11.glColor4f(col.getRed() / 255.0f, col.getGreen() / 255.0f, col.getBlue() / 255.0f, 0.33f);
                GL11.glPushMatrix();
                GL11.glTranslatef(var5 + ContainerArcaneWorkbench.xx[id] + 7.5f, var6 + ContainerArcaneWorkbench.yy[id] + 8.0f, 0.0f);
                GL11.glRotatef(id * 60 + this.mc.getRenderViewEntity().ticksExisted % 360 + par1, 0.0f, 0.0f, 1.0f);
                GL11.glScalef(0.5f, 0.5f, 0.0f);
                this.drawTexturedModalRect(-32, -32, 192, 0, 64, 64);
                GL11.glScalef(1.0f, 1.0f, 1.0f);
                GL11.glPopMatrix();
            }
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.blendFunc(770, 771);
        }
        GL11.glDisable(3042);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(var5 + 168), (float)(var6 + 46), 0.0f);
        GL11.glScalef(0.5f, 0.5f, 0.0f);
        String text = this.tileEntity.auraVisClient + " " + I18n.translateToLocal("workbench.available");
        int ll = this.fontRenderer.getStringWidth(text) / 2;
        this.fontRenderer.drawString(text, -ll, 0, (this.tileEntity.auraVisClient < cost) ? 15625838 : 7237358);
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        if (cost > 0) {
            if (this.tileEntity.auraVisClient < cost) {
                GL11.glPushMatrix();
                final float var7 = 0.33f;
                GL11.glColor4f(var7, var7, var7, 0.66f);
                GL11.glEnable(2896);
                GL11.glEnable(2884);
                GL11.glEnable(3042);
                this.itemRender.renderItemAndEffectIntoGUI(result.getCraftingResult(this.tileEntity.inventoryCraft), var5 + 160, var6 + 64);
                this.itemRender.renderItemOverlayIntoGUI(this.mc.fontRenderer, result.getCraftingResult(this.tileEntity.inventoryCraft), var5 + 160, var6 + 64, "");
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                RenderHelper.enableStandardItemLighting();
                GL11.glPopMatrix();
                RenderHelper.disableStandardItemLighting();
            }
            GL11.glPushMatrix();
            GL11.glTranslatef((float)(var5 + 168), (float)(var6 + 38), 0.0f);
            GL11.glScalef(0.5f, 0.5f, 0.0f);
            text = cost + " " + I18n.translateToLocal("workbench.cost");
            if (discount > 0) {
                text = text + " (" + discount + "% " + I18n.translateToLocal("workbench.discount") + ")";
            }
            ll = this.fontRenderer.getStringWidth(text) / 2;
            this.fontRenderer.drawString(text, -ll, 0, 12648447);
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
    }
}
