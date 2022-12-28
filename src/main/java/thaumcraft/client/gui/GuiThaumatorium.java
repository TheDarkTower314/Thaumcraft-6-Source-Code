package thaumcraft.client.gui;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.container.ContainerThaumatorium;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketSelectThaumotoriumRecipeToServer;
import thaumcraft.common.tiles.crafting.TileThaumatorium;


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
    
    public GuiThaumatorium(InventoryPlayer par1InventoryPlayer, TileThaumatorium par2TileEntityFurnace) {
        super(new ContainerThaumatorium(par1InventoryPlayer, par2TileEntityFurnace));
        container = null;
        index = 0;
        lastSize = 0;
        player = null;
        tex = new ResourceLocation("thaumcraft", "textures/gui/gui_thaumatorium.png");
        hashList = new ArrayList<Integer>();
        lastHLUpdate = 0L;
        xSize = 175;
        ySize = 216;
        inventory = par2TileEntityFurnace;
        container = (ContainerThaumatorium) inventorySlots;
        player = par1InventoryPlayer.player;
        inventory.updateRecipes(player);
        lastSize = hashList.size();
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerBackgroundLayer(float par1, int mx, int my) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(tex);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        GL11.glEnable(3042);
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
        long t = System.currentTimeMillis();
        if (t > lastHLUpdate) {
            hashList.clear();
            hashList = inventory.generateRecipeHashlist();
            lastHLUpdate = t + 500L;
        }
        if (hashList.size() > 0) {
            if (index > hashList.size() / 2) {
                index = hashList.size() / 2;
            }
            if (index < 0 || hashList.size() <= 6) {
                index = 0;
            }
            if (hashList.size() > 6) {
                if (index > 0) {
                    drawTexturedModalRect(k + 82, l + 56, 176, 56, 8, 11);
                }
                if (index < hashList.size() / 2.0f - 3.0f) {
                    drawTexturedModalRect(k + 82, l + 93, 176, 93, 8, 11);
                }
            }
        }
        drawAspects(k, l, t);
        if (inventory.maxRecipes > 1) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)(k + 64), (float)(l + 48), 0.0f);
            GL11.glScalef(0.5f, 0.5f, 0.0f);
            String text = inventory.recipeHash.size() + "/" + inventory.maxRecipes;
            int ll = fontRenderer.getStringWidth(text) / 2;
            fontRenderer.drawString(text, -ll, 0, 16777215);
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
        drawOutput(k, l, mx, my, t);
    }
    
    private static CrucibleRecipe getRecipeCached(int hash) {
        if (GuiThaumatorium.recipeCache.containsKey(hash)) {
            return GuiThaumatorium.recipeCache.get(hash);
        }
        CrucibleRecipe cr = ThaumcraftApi.getCrucibleRecipeFromHash(hash);
        if (cr != null) {
            GuiThaumatorium.recipeCache.put(hash, cr);
        }
        return cr;
    }
    
    private void drawAspects(int k, int l, long time) {
        if (inventory.recipeHash.size() <= 0) {
            return;
        }
        int count = 0;
        int px = 0;
        int py = 0;
        GL11.glEnable(3042);
        int hash = inventory.recipeHash.get((int)(time / 1000L % inventory.recipeHash.size()));
        if (inventory.recipeHash.contains(hash)) {
            CrucibleRecipe cr = getRecipeCached(hash);
            if (cr == null) {
                return;
            }
            for (Aspect aspect : cr.getAspects().getAspectsSortedByName()) {
                GL11.glPushMatrix();
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                drawTexturedModalRect(k + 98 + 16 * px, l + 40 + 20 * py, 176, 4, 12, 3);
                int i1 = (int)(inventory.essentia.getAmount(aspect) / (float)cr.getAspects().getAmount(aspect) * 12.0f);
                Color c = new Color(aspect.getColor());
                GL11.glColor4f(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 1.0f);
                drawTexturedModalRect(k + 98 + 16 * px, l + 40 + 20 * py, 176, 0, i1, 3);
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
            for (Aspect aspect : cr.getAspects().getAspectsSortedByName()) {
                UtilsFX.drawTag(k + 96 + 16 * px, l + 24 + 20 * py, aspect, (float)cr.getAspects().getAmount(aspect), 0, zLevel);
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
    
    private void drawOutput(int x, int y, int mx, int my, long time) {
        GL11.glPushMatrix();
        int px = 0;
        int py = 0;
        int q = 0;
        int idx = 0;
        for (int hash : hashList) {
            if (q++ < index * 2) {
                continue;
            }
            CrucibleRecipe cr = getRecipeCached(hash);
            if (cr == null) {
                continue;
            }
            drawOutputIcon(x + 48 + px * 16, y + 56 + py * 16, getRecipeCached(hash), time);
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
        for (int hash : hashList) {
            if (q++ < index * 2) {
                continue;
            }
            CrucibleRecipe cr = getRecipeCached(hash);
            if (cr == null) {
                continue;
            }
            int xx = mx - (x + 48 + px * 16);
            int yy = my - (y + 56 + py * 16);
            if (xx >= 0 && yy >= 0 && xx < 16 && yy < 16) {
                renderToolTip(cr.getRecipeOutput(), mx, my);
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
    
    private void drawOutputIcon(int x, int y, CrucibleRecipe cr, long time) {
        if (inventory.recipeHash.contains(cr.hash)) {
            int hash = inventory.recipeHash.get((int)(time / 1000L % inventory.recipeHash.size()));
            mc.renderEngine.bindTexture(tex);
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.7f);
            drawTexturedModalRect(x, y, 176, 8, 16, 16);
            if (inventory.recipeHash.size() > 1 && hash == cr.hash) {
                drawTexturedModalRect(x, y, 176, 8, 16, 16);
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
        itemRender.zLevel = 100.0f;
        itemRender.renderItemAndEffectIntoGUI(cr.getRecipeOutput(), x, y);
        itemRender.renderItemOverlays(fontRenderer, cr.getRecipeOutput(), x, y);
        itemRender.zLevel = 0.0f;
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
    }
    
    protected void mouseClicked(int mx, int my, int par3) throws IOException {
        super.mouseClicked(mx, my, par3);
        int gx = (width - xSize) / 2;
        int gy = (height - ySize) / 2;
        int px = 0;
        int py = 0;
        int q = 0;
        int idx = 0;
        for (int hash : hashList) {
            if (q++ < index * 2) {
                continue;
            }
            CrucibleRecipe cr = getRecipeCached(hash);
            if (cr == null) {
                continue;
            }
            int x = mx - (gx + 48 + px * 16);
            int y = my - (gy + 56 + py * 16);
            if (x >= 0 && y >= 0 && x < 16 && y < 16) {
                PacketHandler.INSTANCE.sendToServer(new PacketSelectThaumotoriumRecipeToServer(player, inventory.getPos(), hash));
                playButtonSelect();
                lastHLUpdate = 0L;
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
        if (hashList.size() > 6) {
            if (index > 0) {
                int x2 = mx - (gx + 82);
                int y2 = my - (gy + 56);
                if (x2 >= 0 && y2 >= 0 && x2 < 8 && y2 < 11) {
                    --index;
                    playButtonClick();
                    lastHLUpdate = 0L;
                }
            }
            if (index < hashList.size() / 2.0f - 3.0f) {
                int x2 = mx - (gx + 82);
                int y2 = my - (gy + 93);
                if (x2 >= 0 && y2 >= 0 && x2 < 8 && y2 < 11) {
                    ++index;
                    playButtonClick();
                    lastHLUpdate = 0L;
                }
            }
        }
    }
    
    private void playButtonSelect() {
        mc.getRenderViewEntity().playSound(SoundsTC.hhon, 0.3f, 1.0f);
    }
    
    private void playButtonClick() {
        mc.getRenderViewEntity().playSound(SoundsTC.clack, 0.4f, 1.0f);
    }
    
    static {
        GuiThaumatorium.recipeCache = new HashMap<Integer, CrucibleRecipe>();
    }
}
