// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui;

import thaumcraft.common.lib.SoundsTC;
import java.io.IOException;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.common.lib.network.misc.PacketSelectThaumotoriumRecipeToServer;
import thaumcraft.common.lib.network.PacketHandler;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import java.util.Iterator;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;
import java.awt.Color;
import thaumcraft.api.ThaumcraftApi;
import org.lwjgl.opengl.GL11;
import net.minecraft.inventory.Container;
import net.minecraft.entity.player.InventoryPlayer;
import thaumcraft.api.crafting.CrucibleRecipe;
import java.util.HashMap;
import java.util.ArrayList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.container.ContainerThaumatorium;
import thaumcraft.common.tiles.crafting.TileThaumatorium;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;

@SideOnly(Side.CLIENT)
public class GuiThaumatorium extends GuiContainer
{
    private TileThaumatorium inventory;
    private ContainerThaumatorium container;
    private int index;
    private int lastSize;
    private EntityPlayer player;
    ResourceLocation tex;
    ArrayList<Integer> hashList;
    long lastHLUpdate;
    static HashMap<Integer, CrucibleRecipe> recipeCache;
    
    public GuiThaumatorium(final InventoryPlayer par1InventoryPlayer, final TileThaumatorium par2TileEntityFurnace) {
        super(new ContainerThaumatorium(par1InventoryPlayer, par2TileEntityFurnace));
        this.container = null;
        this.index = 0;
        this.lastSize = 0;
        this.player = null;
        this.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_thaumatorium.png");
        this.hashList = new ArrayList<Integer>();
        this.lastHLUpdate = 0L;
        this.xSize = 175;
        this.ySize = 216;
        this.inventory = par2TileEntityFurnace;
        this.container = (ContainerThaumatorium)this.inventorySlots;
        this.player = par1InventoryPlayer.player;
        this.inventory.updateRecipes(this.player);
        this.lastSize = this.hashList.size();
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int mx, final int my) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(this.tex);
        final int k = (this.width - this.xSize) / 2;
        final int l = (this.height - this.ySize) / 2;
        GL11.glEnable(3042);
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        final long t = System.currentTimeMillis();
        if (t > this.lastHLUpdate) {
            this.hashList.clear();
            this.hashList = this.inventory.generateRecipeHashlist();
            this.lastHLUpdate = t + 500L;
        }
        if (this.hashList.size() > 0) {
            if (this.index > this.hashList.size() / 2) {
                this.index = this.hashList.size() / 2;
            }
            if (this.index < 0 || this.hashList.size() <= 6) {
                this.index = 0;
            }
            if (this.hashList.size() > 6) {
                if (this.index > 0) {
                    this.drawTexturedModalRect(k + 82, l + 56, 176, 56, 8, 11);
                }
                if (this.index < this.hashList.size() / 2.0f - 3.0f) {
                    this.drawTexturedModalRect(k + 82, l + 93, 176, 93, 8, 11);
                }
            }
        }
        this.drawAspects(k, l, t);
        if (this.inventory.maxRecipes > 1) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)(k + 64), (float)(l + 48), 0.0f);
            GL11.glScalef(0.5f, 0.5f, 0.0f);
            final String text = this.inventory.recipeHash.size() + "/" + this.inventory.maxRecipes;
            final int ll = this.fontRenderer.getStringWidth(text) / 2;
            this.fontRenderer.drawString(text, -ll, 0, 16777215);
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
        this.drawOutput(k, l, mx, my, t);
    }
    
    private static CrucibleRecipe getRecipeCached(final int hash) {
        if (GuiThaumatorium.recipeCache.containsKey(hash)) {
            return GuiThaumatorium.recipeCache.get(hash);
        }
        final CrucibleRecipe cr = ThaumcraftApi.getCrucibleRecipeFromHash(hash);
        if (cr != null) {
            GuiThaumatorium.recipeCache.put(hash, cr);
        }
        return cr;
    }
    
    private void drawAspects(final int k, final int l, final long time) {
        if (this.inventory.recipeHash.size() <= 0) {
            return;
        }
        int count = 0;
        int px = 0;
        int py = 0;
        GL11.glEnable(3042);
        final int hash = this.inventory.recipeHash.get((int)(time / 1000L % this.inventory.recipeHash.size()));
        if (this.inventory.recipeHash.contains(hash)) {
            final CrucibleRecipe cr = getRecipeCached(hash);
            if (cr == null) {
                return;
            }
            for (final Aspect aspect : cr.getAspects().getAspectsSortedByName()) {
                GL11.glPushMatrix();
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                this.drawTexturedModalRect(k + 98 + 16 * px, l + 40 + 20 * py, 176, 4, 12, 3);
                final int i1 = (int)(this.inventory.essentia.getAmount(aspect) / (float)cr.getAspects().getAmount(aspect) * 12.0f);
                final Color c = new Color(aspect.getColor());
                GL11.glColor4f(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 1.0f);
                this.drawTexturedModalRect(k + 98 + 16 * px, l + 40 + 20 * py, 176, 0, i1, 3);
                GL11.glPopMatrix();
                if (++px > 1) {
                    px = 0;
                    ++py;
                }
                if (++count >= 8) {
                    break;
                }
            }
            count = 0;
            px = 0;
            py = 0;
            for (final Aspect aspect : cr.getAspects().getAspectsSortedByName()) {
                UtilsFX.drawTag(k + 96 + 16 * px, l + 24 + 20 * py, aspect, (float)cr.getAspects().getAmount(aspect), 0, this.zLevel);
                if (++px > 1) {
                    px = 0;
                    ++py;
                }
                if (++count >= 8) {
                    break;
                }
            }
        }
    }
    
    private void drawOutput(final int x, final int y, final int mx, final int my, final long time) {
        GL11.glPushMatrix();
        int px = 0;
        int py = 0;
        int q = 0;
        int idx = 0;
        for (final int hash : this.hashList) {
            if (q++ < this.index * 2) {
                continue;
            }
            final CrucibleRecipe cr = getRecipeCached(hash);
            if (cr == null) {
                continue;
            }
            this.drawOutputIcon(x + 48 + px * 16, y + 56 + py * 16, getRecipeCached(hash), time);
            if (++px > 1) {
                px = 0;
                ++py;
            }
            if (++idx >= 6) {
                break;
            }
        }
        px = 0;
        py = 0;
        q = 0;
        idx = 0;
        for (final int hash : this.hashList) {
            if (q++ < this.index * 2) {
                continue;
            }
            final CrucibleRecipe cr = getRecipeCached(hash);
            if (cr == null) {
                continue;
            }
            final int xx = mx - (x + 48 + px * 16);
            final int yy = my - (y + 56 + py * 16);
            if (xx >= 0 && yy >= 0 && xx < 16 && yy < 16) {
                this.renderToolTip(cr.getRecipeOutput(), mx, my);
                break;
            }
            if (++px > 1) {
                px = 0;
                ++py;
            }
            if (++idx >= 6) {
                break;
            }
        }
        GL11.glDisable(3042);
        GL11.glDisable(2896);
        GL11.glPopMatrix();
    }
    
    private void drawOutputIcon(final int x, final int y, final CrucibleRecipe cr, final long time) {
        if (this.inventory.recipeHash.contains(cr.hash)) {
            final int hash = this.inventory.recipeHash.get((int)(time / 1000L % this.inventory.recipeHash.size()));
            this.mc.renderEngine.bindTexture(this.tex);
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.7f);
            this.drawTexturedModalRect(x, y, 176, 8, 16, 16);
            if (this.inventory.recipeHash.size() > 1 && hash == cr.hash) {
                this.drawTexturedModalRect(x, y, 176, 8, 16, 16);
            }
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();
        this.itemRender.zLevel = 100.0f;
        this.itemRender.renderItemAndEffectIntoGUI(cr.getRecipeOutput(), x, y);
        this.itemRender.renderItemOverlays(this.fontRenderer, cr.getRecipeOutput(), x, y);
        this.itemRender.zLevel = 0.0f;
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
    }
    
    protected void mouseClicked(final int mx, final int my, final int par3) throws IOException {
        super.mouseClicked(mx, my, par3);
        final int gx = (this.width - this.xSize) / 2;
        final int gy = (this.height - this.ySize) / 2;
        int px = 0;
        int py = 0;
        int q = 0;
        int idx = 0;
        for (final int hash : this.hashList) {
            if (q++ < this.index * 2) {
                continue;
            }
            final CrucibleRecipe cr = getRecipeCached(hash);
            if (cr == null) {
                continue;
            }
            final int x = mx - (gx + 48 + px * 16);
            final int y = my - (gy + 56 + py * 16);
            if (x >= 0 && y >= 0 && x < 16 && y < 16) {
                PacketHandler.INSTANCE.sendToServer(new PacketSelectThaumotoriumRecipeToServer(this.player, this.inventory.getPos(), hash));
                this.playButtonSelect();
                this.lastHLUpdate = 0L;
                break;
            }
            if (++px > 1) {
                px = 0;
                ++py;
            }
            if (++idx >= 6) {
                break;
            }
        }
        if (this.hashList.size() > 6) {
            if (this.index > 0) {
                final int x2 = mx - (gx + 82);
                final int y2 = my - (gy + 56);
                if (x2 >= 0 && y2 >= 0 && x2 < 8 && y2 < 11) {
                    --this.index;
                    this.playButtonClick();
                    this.lastHLUpdate = 0L;
                }
            }
            if (this.index < this.hashList.size() / 2.0f - 3.0f) {
                final int x2 = mx - (gx + 82);
                final int y2 = my - (gy + 93);
                if (x2 >= 0 && y2 >= 0 && x2 < 8 && y2 < 11) {
                    ++this.index;
                    this.playButtonClick();
                    this.lastHLUpdate = 0L;
                }
            }
        }
    }
    
    private void playButtonSelect() {
        this.mc.getRenderViewEntity().playSound(SoundsTC.hhon, 0.3f, 1.0f);
    }
    
    private void playButtonClick() {
        this.mc.getRenderViewEntity().playSound(SoundsTC.clack, 0.4f, 1.0f);
    }
    
    static {
        GuiThaumatorium.recipeCache = new HashMap<Integer, CrucibleRecipe>();
    }
}
