// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.FMLClientHandler;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.client.gui.GuiButton;
import thaumcraft.common.lib.network.playerdata.PacketSyncResearchFlagsToServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.common.lib.network.playerdata.PacketSyncProgressToServer;
import thaumcraft.common.lib.network.PacketHandler;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.common.lib.utils.InventoryUtils;
import net.minecraft.client.renderer.RenderHelper;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import java.util.Arrays;
import thaumcraft.client.lib.UtilsFX;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import thaumcraft.api.research.ResearchStage;
import java.util.List;
import java.util.Collections;
import thaumcraft.api.crafting.CrucibleRecipe;
import net.minecraft.item.ItemStack;
import thaumcraft.api.crafting.InfusionRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.CraftingManager;
import thaumcraft.api.internal.CommonInternals;
import java.io.IOException;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.api.research.ResearchCategory;
import java.util.Iterator;
import java.util.Collection;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import org.lwjgl.input.Keyboard;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.research.ResearchEntry;
import java.util.LinkedList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;

@SideOnly(Side.CLIENT)
public class GuiResearchBrowser extends GuiScreen
{
    private static int guiBoundsLeft;
    private static int guiBoundsTop;
    private static int guiBoundsRight;
    private static int guiBoundsBottom;
    protected int mouseX;
    protected int mouseY;
    protected float screenZoom;
    protected double curMouseX;
    protected double curMouseY;
    protected double guiMapX;
    protected double guiMapY;
    protected double tempMapX;
    protected double tempMapY;
    private int isMouseButtonDown;
    public static double lastX;
    public static double lastY;
    GuiResearchBrowser instance;
    private int screenX;
    private int screenY;
    private int startX;
    private int startY;
    long t;
    private LinkedList<ResearchEntry> research;
    static String selectedCategory;
    private ResearchEntry currentHighlight;
    private EntityPlayer player;
    long popuptime;
    String popupmessage;
    private GuiTextField searchField;
    private static boolean searching;
    private ArrayList<String> categoriesTC;
    private ArrayList<String> categoriesOther;
    static int catScrollPos;
    static int catScrollMax;
    public int addonShift;
    private ArrayList<String> invisible;
    ArrayList<Pair<String, SearchResult>> searchResults;
    ResourceLocation tx1;
    
    public GuiResearchBrowser() {
        this.mouseX = 0;
        this.mouseY = 0;
        this.screenZoom = 1.0f;
        this.isMouseButtonDown = 0;
        this.instance = null;
        this.startX = 16;
        this.startY = 16;
        this.t = 0L;
        this.research = new LinkedList<ResearchEntry>();
        this.currentHighlight = null;
        this.player = null;
        this.popuptime = 0L;
        this.popupmessage = "";
        this.categoriesTC = new ArrayList<String>();
        this.categoriesOther = new ArrayList<String>();
        this.addonShift = 0;
        this.invisible = new ArrayList<String>();
        this.searchResults = new ArrayList<Pair<String, SearchResult>>();
        this.tx1 = new ResourceLocation("thaumcraft", "textures/gui/gui_research_browser.png");
        final double lastX = GuiResearchBrowser.lastX;
        this.tempMapX = lastX;
        this.guiMapX = lastX;
        this.curMouseX = lastX;
        final double lastY = GuiResearchBrowser.lastY;
        this.tempMapY = lastY;
        this.guiMapY = lastY;
        this.curMouseY = lastY;
        this.player = Minecraft.getMinecraft().player;
        this.instance = this;
    }
    
    public GuiResearchBrowser(final double x, final double y) {
        this.mouseX = 0;
        this.mouseY = 0;
        this.screenZoom = 1.0f;
        this.isMouseButtonDown = 0;
        this.instance = null;
        this.startX = 16;
        this.startY = 16;
        this.t = 0L;
        this.research = new LinkedList<ResearchEntry>();
        this.currentHighlight = null;
        this.player = null;
        this.popuptime = 0L;
        this.popupmessage = "";
        this.categoriesTC = new ArrayList<String>();
        this.categoriesOther = new ArrayList<String>();
        this.addonShift = 0;
        this.invisible = new ArrayList<String>();
        this.searchResults = new ArrayList<Pair<String, SearchResult>>();
        this.tx1 = new ResourceLocation("thaumcraft", "textures/gui/gui_research_browser.png");
        this.tempMapX = x;
        this.guiMapX = x;
        this.curMouseX = x;
        this.tempMapY = y;
        this.guiMapY = y;
        this.curMouseY = y;
        this.player = Minecraft.getMinecraft().player;
        this.instance = this;
    }
    
    public void initGui() {
        this.updateResearch();
    }
    
    public void updateResearch() {
        if (this.mc == null) {
            this.mc = Minecraft.getMinecraft();
        }
        this.buttonList.clear();
        this.buttonList.add(new GuiSearchButton(2, 1, this.height - 17, 16, 16, I18n.translateToLocalFormatted("tc.search")));
        Keyboard.enableRepeatEvents(true);
        (this.searchField = new GuiTextField(0, this.fontRenderer, 20, 20, 89, this.fontRenderer.FONT_HEIGHT)).setMaxStringLength(15);
        this.searchField.setEnableBackgroundDrawing(true);
        this.searchField.setVisible(false);
        this.searchField.setTextColor(16777215);
        if (GuiResearchBrowser.searching) {
            this.searchField.setVisible(true);
            this.searchField.setCanLoseFocus(false);
            this.searchField.setFocused(true);
            this.searchField.setText("");
            this.updateSearch();
        }
        this.screenX = this.width - 32;
        this.screenY = this.height - 32;
        this.research.clear();
        if (GuiResearchBrowser.selectedCategory == null) {
            final Collection<String> cats = ResearchCategories.researchCategories.keySet();
            GuiResearchBrowser.selectedCategory = cats.iterator().next();
        }
        final int limit = (int)Math.floor((this.screenY - 28) / 24.0f);
        this.addonShift = 0;
        int count = 0;
        this.categoriesTC.clear();
        this.categoriesOther.clear();
    Label_0283:
        for (final String rcl : ResearchCategories.researchCategories.keySet()) {
            int rt = 0;
            int rco = 0;
            final Collection col = ResearchCategories.getResearchCategory(rcl).research.values();
            for (final Object res : col) {
                if (((ResearchEntry)res).hasMeta(ResearchEntry.EnumResearchMeta.AUTOUNLOCK)) {
                    continue;
                }
                ++rt;
                if (!ThaumcraftCapabilities.knowsResearch(this.player, ((ResearchEntry)res).getKey())) {
                    continue;
                }
                ++rco;
            }
            final int v = (int)(rco / (float)rt * 100.0f);
            final ResearchCategory rc = ResearchCategories.getResearchCategory(rcl);
            if (rc.researchKey != null && !ThaumcraftCapabilities.knowsResearchStrict(this.player, rc.researchKey)) {
                continue;
            }
            for (final String tcc : ConfigResearch.TCCategories) {
                if (tcc.equals(rcl)) {
                    this.categoriesTC.add(rcl);
                    this.buttonList.add(new GuiCategoryButton(rc, rcl, false, 20 + this.categoriesTC.size(), 1, 10 + this.categoriesTC.size() * 24, 16, 16, I18n.translateToLocalFormatted("tc.research_category." + rcl), v));
                    continue Label_0283;
                }
            }
            if (++count > limit + GuiResearchBrowser.catScrollPos) {
                continue;
            }
            if (count - 1 < GuiResearchBrowser.catScrollPos) {
                continue;
            }
            this.categoriesOther.add(rcl);
            this.buttonList.add(new GuiCategoryButton(rc, rcl, true, 50 + this.categoriesOther.size(), this.width - 17, 10 + this.categoriesOther.size() * 24, 16, 16, I18n.translateToLocalFormatted("tc.research_category." + rcl), v));
        }
        if (count > limit || count < GuiResearchBrowser.catScrollPos) {
            this.addonShift = (this.screenY - 28) % 24 / 2;
            this.buttonList.add(new GuiScrollButton(false, 3, this.width - 14, 20, 10, 11, ""));
            this.buttonList.add(new GuiScrollButton(true, 4, this.width - 14, this.screenY + 1, 10, 11, ""));
        }
        GuiResearchBrowser.catScrollMax = count - limit;
        if (GuiResearchBrowser.selectedCategory == null || GuiResearchBrowser.selectedCategory.equals("")) {
            return;
        }
        final Collection col2 = ResearchCategories.getResearchCategory(GuiResearchBrowser.selectedCategory).research.values();
        for (final Object res2 : col2) {
            this.research.add((ResearchEntry)res2);
        }
        GuiResearchBrowser.guiBoundsLeft = 99999;
        GuiResearchBrowser.guiBoundsTop = 99999;
        GuiResearchBrowser.guiBoundsRight = -99999;
        GuiResearchBrowser.guiBoundsBottom = -99999;
        for (final ResearchEntry res3 : this.research) {
            if (res3 != null && this.isVisible(res3)) {
                if (res3.getDisplayColumn() * 24 - this.screenX + 48 < GuiResearchBrowser.guiBoundsLeft) {
                    GuiResearchBrowser.guiBoundsLeft = res3.getDisplayColumn() * 24 - this.screenX + 48;
                }
                if (res3.getDisplayColumn() * 24 - 24 > GuiResearchBrowser.guiBoundsRight) {
                    GuiResearchBrowser.guiBoundsRight = res3.getDisplayColumn() * 24 - 24;
                }
                if (res3.getDisplayRow() * 24 - this.screenY + 48 < GuiResearchBrowser.guiBoundsTop) {
                    GuiResearchBrowser.guiBoundsTop = res3.getDisplayRow() * 24 - this.screenY + 48;
                }
                if (res3.getDisplayRow() * 24 - 24 <= GuiResearchBrowser.guiBoundsBottom) {
                    continue;
                }
                GuiResearchBrowser.guiBoundsBottom = res3.getDisplayRow() * 24 - 24;
            }
        }
    }
    
    private boolean isVisible(final ResearchEntry res) {
        if (ThaumcraftCapabilities.knowsResearch(this.player, res.getKey())) {
            return true;
        }
        if (this.invisible.contains(res.getKey()) || (res.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN) && !this.canUnlockResearch(res))) {
            return false;
        }
        if (res.getParents() == null && res.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN)) {
            return false;
        }
        if (res.getParents() != null) {
            for (final String r : res.getParents()) {
                final ResearchEntry ri = ResearchCategories.getResearch(r);
                if (ri != null && !this.isVisible(ri)) {
                    this.invisible.add(r);
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean canUnlockResearch(final ResearchEntry res) {
        return ResearchManager.doesPlayerHaveRequisites(this.player, res.getKey());
    }
    
    public void onGuiClosed() {
        GuiResearchBrowser.lastX = this.guiMapX;
        GuiResearchBrowser.lastY = this.guiMapY;
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }
    
    public void setWorldAndResolution(final Minecraft mc, final int width, final int height) {
        super.setWorldAndResolution(mc, width, height);
        this.updateResearch();
        if (GuiResearchBrowser.lastX == -9999.0 || this.guiMapX > GuiResearchBrowser.guiBoundsRight || this.guiMapX < GuiResearchBrowser.guiBoundsLeft) {
            final double n = (GuiResearchBrowser.guiBoundsLeft + GuiResearchBrowser.guiBoundsRight) / 2;
            this.tempMapX = n;
            this.guiMapX = n;
        }
        if (GuiResearchBrowser.lastY == -9999.0 || this.guiMapY > GuiResearchBrowser.guiBoundsBottom || this.guiMapY < GuiResearchBrowser.guiBoundsTop) {
            final double n2 = (GuiResearchBrowser.guiBoundsBottom + GuiResearchBrowser.guiBoundsTop) / 2;
            this.tempMapY = n2;
            this.guiMapY = n2;
        }
    }
    
    protected void keyTyped(final char par1, final int par2) throws IOException {
        if (GuiResearchBrowser.searching && this.searchField.textboxKeyTyped(par1, par2)) {
            this.updateSearch();
        }
        else if (par2 == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        }
        super.keyTyped(par1, par2);
    }
    
    private void updateSearch() {
        this.searchResults.clear();
        this.invisible.clear();
        final String s1 = this.searchField.getText().toLowerCase();
        for (final String cat : this.categoriesTC) {
            if (cat.toLowerCase().contains(s1)) {
                this.searchResults.add(Pair.of(I18n.translateToLocalFormatted("tc.research_category." + cat), new SearchResult(cat, null, true)));
            }
        }
        for (final String cat : this.categoriesOther) {
            if (cat.toLowerCase().contains(s1)) {
                this.searchResults.add(Pair.of(I18n.translateToLocalFormatted("tc.research_category." + cat), new SearchResult(cat, null, true)));
            }
        }
        final ArrayList<ResourceLocation> dupCheck = new ArrayList<ResourceLocation>();
        for (final String pre : ThaumcraftCapabilities.getKnowledge(this.player).getResearchList()) {
            final ResearchEntry ri = ResearchCategories.getResearch(pre);
            if (ri != null) {
                if (ri.getLocalizedName() == null) {
                    continue;
                }
                if (ri.getLocalizedName().toLowerCase().contains(s1)) {
                    this.searchResults.add(Pair.of(ri.getLocalizedName(), new SearchResult(pre, null)));
                }
                final int stage = ThaumcraftCapabilities.getKnowledge(this.player).getResearchStage(pre);
                if (ri.getStages() == null) {
                    continue;
                }
                final int s2 = (ri.getStages().length - 1 < stage + 1) ? (ri.getStages().length - 1) : (stage + 1);
                final ResearchStage page = ri.getStages()[s2];
                if (page == null || page.getRecipes() == null) {
                    continue;
                }
                for (final ResourceLocation rec : page.getRecipes()) {
                    if (!dupCheck.contains(rec)) {
                        dupCheck.add(rec);
                        Object recipeObject = CommonInternals.getCatalogRecipe(rec);
                        if (recipeObject == null) {
                            recipeObject = CommonInternals.getCatalogRecipeFake(rec);
                        }
                        if (recipeObject == null) {
                            recipeObject = CraftingManager.getRecipe(rec);
                        }
                        if (recipeObject != null) {
                            ItemStack ro = null;
                            if (recipeObject instanceof IRecipe) {
                                ro = ((IRecipe)recipeObject).getRecipeOutput();
                            }
                            else if (recipeObject instanceof InfusionRecipe && ((InfusionRecipe)recipeObject).getRecipeOutput() instanceof ItemStack) {
                                ro = (ItemStack)((InfusionRecipe)recipeObject).getRecipeOutput();
                            }
                            else if (recipeObject instanceof CrucibleRecipe) {
                                ro = ((CrucibleRecipe)recipeObject).getRecipeOutput();
                            }
                            if (ro != null && !ro.isEmpty() && ro.getDisplayName().toLowerCase().contains(s1)) {
                                this.searchResults.add(Pair.of(ro.getDisplayName(), new SearchResult(pre, rec)));
                            }
                        }
                    }
                }
            }
        }
        Collections.sort(this.searchResults);
    }
    
    public void drawScreen(final int mx, final int my, final float par3) {
        if (!GuiResearchBrowser.searching) {
            if (Mouse.isButtonDown(0)) {
                if ((this.isMouseButtonDown == 0 || this.isMouseButtonDown == 1) && mx >= this.startX && mx < this.startX + this.screenX && my >= this.startY && my < this.startY + this.screenY) {
                    if (this.isMouseButtonDown == 0) {
                        this.isMouseButtonDown = 1;
                    }
                    else {
                        this.guiMapX -= (mx - this.mouseX) * (double)this.screenZoom;
                        this.guiMapY -= (my - this.mouseY) * (double)this.screenZoom;
                        final double guiMapX = this.guiMapX;
                        this.curMouseX = guiMapX;
                        this.tempMapX = guiMapX;
                        final double guiMapY = this.guiMapY;
                        this.curMouseY = guiMapY;
                        this.tempMapY = guiMapY;
                    }
                    this.mouseX = mx;
                    this.mouseY = my;
                }
                if (this.tempMapX < GuiResearchBrowser.guiBoundsLeft * (double)this.screenZoom) {
                    this.tempMapX = GuiResearchBrowser.guiBoundsLeft * (double)this.screenZoom;
                }
                if (this.tempMapY < GuiResearchBrowser.guiBoundsTop * (double)this.screenZoom) {
                    this.tempMapY = GuiResearchBrowser.guiBoundsTop * (double)this.screenZoom;
                }
                if (this.tempMapX >= GuiResearchBrowser.guiBoundsRight * (double)this.screenZoom) {
                    this.tempMapX = GuiResearchBrowser.guiBoundsRight * this.screenZoom - 1.0f;
                }
                if (this.tempMapY >= GuiResearchBrowser.guiBoundsBottom * (double)this.screenZoom) {
                    this.tempMapY = GuiResearchBrowser.guiBoundsBottom * this.screenZoom - 1.0f;
                }
            }
            else {
                this.isMouseButtonDown = 0;
            }
            final int k = Mouse.getDWheel();
            if (k < 0) {
                this.screenZoom += 0.25f;
            }
            else if (k > 0) {
                this.screenZoom -= 0.25f;
            }
            this.screenZoom = MathHelper.clamp(this.screenZoom, 1.0f, 2.0f);
        }
        this.drawDefaultBackground();
        this.t = System.nanoTime() / 50000000L;
        int locX = MathHelper.floor(this.curMouseX + (this.guiMapX - this.curMouseX) * par3);
        int locY = MathHelper.floor(this.curMouseY + (this.guiMapY - this.curMouseY) * par3);
        if (locX < GuiResearchBrowser.guiBoundsLeft * this.screenZoom) {
            locX = (int)(GuiResearchBrowser.guiBoundsLeft * this.screenZoom);
        }
        if (locY < GuiResearchBrowser.guiBoundsTop * this.screenZoom) {
            locY = (int)(GuiResearchBrowser.guiBoundsTop * this.screenZoom);
        }
        if (locX >= GuiResearchBrowser.guiBoundsRight * this.screenZoom) {
            locX = (int)(GuiResearchBrowser.guiBoundsRight * this.screenZoom - 1.0f);
        }
        if (locY >= GuiResearchBrowser.guiBoundsBottom * this.screenZoom) {
            locY = (int)(GuiResearchBrowser.guiBoundsBottom * this.screenZoom - 1.0f);
        }
        this.genResearchBackgroundFixedPre(mx, my, par3, locX, locY);
        if (!GuiResearchBrowser.searching) {
            GL11.glPushMatrix();
            GL11.glScalef(1.0f / this.screenZoom, 1.0f / this.screenZoom, 1.0f);
            this.genResearchBackgroundZoomable(mx, my, par3, locX, locY);
            GL11.glPopMatrix();
        }
        else {
            this.searchField.drawTextBox();
            int q = 0;
            for (final Pair p : this.searchResults) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                final SearchResult sr = (SearchResult)p.getRight();
                int color = sr.cat ? 14527146 : ((sr.recipe == null) ? 14540253 : 11184861);
                if (sr.recipe != null) {
                    this.mc.renderEngine.bindTexture(this.tx1);
                    GL11.glPushMatrix();
                    GL11.glScaled(0.5, 0.5, 0.5);
                    this.drawTexturedModalRect(44, (32 + q * 10) * 2, 224, 48, 16, 16);
                    GL11.glPopMatrix();
                }
                if (mx > 22 && mx < 18 + this.screenX && my >= 32 + q * 10 && my < 40 + q * 10) {
                    color = ((sr.recipe == null) ? 16777215 : (sr.cat ? 16764108 : 13421823));
                }
                this.fontRenderer.drawString((String)p.getLeft(), 32, 32 + q * 10, color);
                ++q;
                if (32 + (q + 1) * 10 > this.screenY) {
                    this.fontRenderer.drawString(I18n.translateToLocalFormatted("tc.search.more"), 22, 34 + q * 10, 11184810);
                    break;
                }
            }
        }
        this.genResearchBackgroundFixedPost(mx, my, par3, locX, locY);
        if (this.popuptime > System.currentTimeMillis()) {
            final ArrayList<String> text = new ArrayList<String>();
            text.add(this.popupmessage);
            UtilsFX.drawCustomTooltip(this, this.fontRenderer, text, 10, 34, -99);
        }
    }
    
    public void updateScreen() {
        this.curMouseX = this.guiMapX;
        this.curMouseY = this.guiMapY;
        final double var1 = this.tempMapX - this.guiMapX;
        final double var2 = this.tempMapY - this.guiMapY;
        if (var1 * var1 + var2 * var2 < 4.0) {
            this.guiMapX += var1;
            this.guiMapY += var2;
        }
        else {
            this.guiMapX += var1 * 0.85;
            this.guiMapY += var2 * 0.85;
        }
    }
    
    private void genResearchBackgroundFixedPre(final int par1, final int par2, final float par3, final int locX, final int locY) {
        this.zLevel = 0.0f;
        GL11.glDepthFunc(518);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 0.0f, -200.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
    }
    
    protected void genResearchBackgroundZoomable(final int mx, final int my, final float par3, final int locX, final int locY) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GL11.glEnable(3042);
        GlStateManager.blendFunc(770, 771);
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getMinecraft().renderEngine.bindTexture(ResearchCategories.getResearchCategory(GuiResearchBrowser.selectedCategory).background);
        this.drawTexturedModalRectWithDoubles((this.startX - 2) * this.screenZoom, (this.startY - 2) * this.screenZoom, locX / 2.0, locY / 2.0, (this.screenX + 4) * this.screenZoom, (this.screenY + 4) * this.screenZoom);
        if (ResearchCategories.getResearchCategory(GuiResearchBrowser.selectedCategory).background2 != null) {
            Minecraft.getMinecraft().renderEngine.bindTexture(ResearchCategories.getResearchCategory(GuiResearchBrowser.selectedCategory).background2);
            this.drawTexturedModalRectWithDoubles((this.startX - 2) * this.screenZoom, (this.startY - 2) * this.screenZoom, locX / 1.5, locY / 1.5, (this.screenX + 4) * this.screenZoom, (this.screenY + 4) * this.screenZoom);
        }
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1f);
        GL11.glPopMatrix();
        GL11.glEnable(2929);
        GL11.glDepthFunc(515);
        this.mc.renderEngine.bindTexture(this.tx1);
        if (ThaumcraftCapabilities.getKnowledge(this.player).getResearchList() != null) {
            for (int index = 0; index < this.research.size(); ++index) {
                final ResearchEntry source = this.research.get(index);
                if (source.getParents() != null && source.getParents().length > 0) {
                    for (int a = 0; a < source.getParents().length; ++a) {
                        if (source.getParents()[a] != null && ResearchCategories.getResearch(source.getParentsClean()[a]) != null && ResearchCategories.getResearch(source.getParentsClean()[a]).getCategory().equals(GuiResearchBrowser.selectedCategory)) {
                            final ResearchEntry parent = ResearchCategories.getResearch(source.getParentsClean()[a]);
                            if (parent.getSiblings() == null || !Arrays.asList(parent.getSiblings()).contains(source.getKey())) {
                                final boolean knowsParent = ThaumcraftCapabilities.knowsResearchStrict(this.player, source.getParents()[a]);
                                final boolean b = this.isVisible(source) && !source.getParents()[a].startsWith("~");
                                if (b) {
                                    if (knowsParent) {
                                        this.drawLine(source.getDisplayColumn(), source.getDisplayRow(), parent.getDisplayColumn(), parent.getDisplayRow(), 0.6f, 0.6f, 0.6f, locX, locY, 3.0f, true, source.hasMeta(ResearchEntry.EnumResearchMeta.REVERSE));
                                    }
                                    else if (this.isVisible(parent)) {
                                        this.drawLine(source.getDisplayColumn(), source.getDisplayRow(), parent.getDisplayColumn(), parent.getDisplayRow(), 0.2f, 0.2f, 0.2f, locX, locY, 2.0f, true, source.hasMeta(ResearchEntry.EnumResearchMeta.REVERSE));
                                    }
                                }
                            }
                        }
                    }
                }
                if (source.getSiblings() != null && source.getSiblings().length > 0) {
                    for (int a = 0; a < source.getSiblings().length; ++a) {
                        if (source.getSiblings()[a] != null && ResearchCategories.getResearch(source.getSiblings()[a]) != null && ResearchCategories.getResearch(source.getSiblings()[a]).getCategory().equals(GuiResearchBrowser.selectedCategory)) {
                            final ResearchEntry sibling = ResearchCategories.getResearch(source.getSiblings()[a]);
                            final boolean knowsSibling = ThaumcraftCapabilities.knowsResearchStrict(this.player, sibling.getKey());
                            if (this.isVisible(source)) {
                                if (!source.getSiblings()[a].startsWith("~")) {
                                    if (knowsSibling) {
                                        this.drawLine(sibling.getDisplayColumn(), sibling.getDisplayRow(), source.getDisplayColumn(), source.getDisplayRow(), 0.3f, 0.3f, 0.4f, locX, locY, 1.0f, false, source.hasMeta(ResearchEntry.EnumResearchMeta.REVERSE));
                                    }
                                    else if (this.isVisible(sibling)) {
                                        this.drawLine(sibling.getDisplayColumn(), sibling.getDisplayRow(), source.getDisplayColumn(), source.getDisplayRow(), 0.1875f, 0.1875f, 0.25f, locX, locY, 0.0f, false, source.hasMeta(ResearchEntry.EnumResearchMeta.REVERSE));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        this.currentHighlight = null;
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        for (int var24 = 0; var24 < this.research.size(); ++var24) {
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            final ResearchEntry iconResearch = this.research.get(var24);
            boolean hasWarp = false;
            if (iconResearch.getStages() != null) {
                for (final ResearchStage stage : iconResearch.getStages()) {
                    if (stage.getWarp() > 0) {
                        hasWarp = true;
                        break;
                    }
                }
            }
            final int var25 = iconResearch.getDisplayColumn() * 24 - locX;
            final int var26 = iconResearch.getDisplayRow() * 24 - locY;
            if (var25 >= -24 && var26 >= -24 && var25 <= this.screenX * this.screenZoom && var26 <= this.screenY * this.screenZoom) {
                final int iconX = this.startX + var25;
                final int iconY = this.startY + var26;
                if (this.isVisible(iconResearch)) {
                    if (hasWarp) {
                        drawForbidden(iconX + 8, iconY + 8);
                    }
                    if (ThaumcraftCapabilities.getKnowledge(this.player).isResearchComplete(iconResearch.getKey())) {
                        final float var27 = 1.0f;
                        GL11.glColor4f(var27, var27, var27, 1.0f);
                    }
                    else if (this.canUnlockResearch(iconResearch)) {
                        final float var27 = (float)Math.sin(Minecraft.getSystemTime() % 600L / 600.0 * 3.141592653589793 * 2.0) * 0.25f + 0.75f;
                        GL11.glColor4f(var27, var27, var27, 1.0f);
                    }
                    else {
                        final float var27 = 0.3f;
                        GL11.glColor4f(var27, var27, var27, 1.0f);
                    }
                    this.mc.renderEngine.bindTexture(this.tx1);
                    GL11.glEnable(2884);
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    if (iconResearch.hasMeta(ResearchEntry.EnumResearchMeta.ROUND)) {
                        this.drawTexturedModalRect(iconX - 8, iconY - 8, 144, 48 + (iconResearch.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN) ? 32 : 0), 32, 32);
                    }
                    else {
                        int ix = 80;
                        int iy = 48;
                        if (iconResearch.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN)) {
                            iy += 32;
                        }
                        if (iconResearch.hasMeta(ResearchEntry.EnumResearchMeta.HEX)) {
                            ix += 32;
                        }
                        this.drawTexturedModalRect(iconX - 8, iconY - 8, ix, iy, 32, 32);
                    }
                    if (iconResearch.hasMeta(ResearchEntry.EnumResearchMeta.SPIKY)) {
                        this.drawTexturedModalRect(iconX - 8, iconY - 8, 176, 48 + (iconResearch.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN) ? 32 : 0), 32, 32);
                    }
                    boolean bw = false;
                    if (!this.canUnlockResearch(iconResearch)) {
                        final float var28 = 0.1f;
                        GL11.glColor4f(var28, var28, var28, 1.0f);
                        bw = true;
                    }
                    if (ThaumcraftCapabilities.getKnowledge(this.player).hasResearchFlag(iconResearch.getKey(), IPlayerKnowledge.EnumResearchFlag.RESEARCH)) {
                        GL11.glPushMatrix();
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        GL11.glTranslatef((float)(iconX - 9), (float)(iconY - 9), 0.0f);
                        GL11.glScaled(0.5, 0.5, 1.0);
                        this.drawTexturedModalRect(0, 0, 176, 16, 32, 32);
                        GL11.glPopMatrix();
                    }
                    if (ThaumcraftCapabilities.getKnowledge(this.player).hasResearchFlag(iconResearch.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE)) {
                        GL11.glPushMatrix();
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        GL11.glTranslatef((float)(iconX - 9), (float)(iconY + 9), 0.0f);
                        GL11.glScaled(0.5, 0.5, 1.0);
                        this.drawTexturedModalRect(0, 0, 208, 16, 32, 32);
                        GL11.glPopMatrix();
                    }
                    drawResearchIcon(iconResearch, iconX, iconY, this.zLevel, bw);
                    if (!this.canUnlockResearch(iconResearch)) {
                        bw = false;
                    }
                    if (mx >= this.startX && my >= this.startY && mx < this.startX + this.screenX && my < this.startY + this.screenY && mx >= (iconX - 2) / this.screenZoom && mx <= (iconX + 18) / this.screenZoom && my >= (iconY - 2) / this.screenZoom && my <= (iconY + 18) / this.screenZoom) {
                        this.currentHighlight = iconResearch;
                    }
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        }
        GL11.glDisable(2929);
    }
    
    public static void drawResearchIcon(final ResearchEntry iconResearch, final int iconX, final int iconY, final float zLevel, final boolean bw) {
        if (iconResearch.getIcons() != null && iconResearch.getIcons().length > 0) {
            final int idx = (int)(System.currentTimeMillis() / 1000L % iconResearch.getIcons().length);
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            if (iconResearch.getIcons()[idx] instanceof ResourceLocation) {
                Minecraft.getMinecraft().renderEngine.bindTexture((ResourceLocation)iconResearch.getIcons()[idx]);
                if (bw) {
                    GL11.glColor4f(0.2f, 0.2f, 0.2f, 1.0f);
                }
                final int w = GL11.glGetTexLevelParameteri(3553, 0, 4096);
                final int h = GL11.glGetTexLevelParameteri(3553, 0, 4097);
                if (h > w && h % w == 0) {
                    final int m = h / w;
                    final float q = 16.0f / m;
                    final float idx2 = System.currentTimeMillis() / 150L % m * q;
                    UtilsFX.drawTexturedQuadF((float)iconX, (float)iconY, 0.0f, idx2, 16.0f, q, zLevel);
                }
                else if (w > h && w % h == 0) {
                    final int m = w / h;
                    final float q = 16.0f / m;
                    final float idx2 = System.currentTimeMillis() / 150L % m * q;
                    UtilsFX.drawTexturedQuadF((float)iconX, (float)iconY, idx2, 0.0f, q, 16.0f, zLevel);
                }
                else {
                    UtilsFX.drawTexturedQuadFull((float)iconX, (float)iconY, zLevel);
                }
            }
            else if (iconResearch.getIcons()[idx] instanceof ItemStack) {
                RenderHelper.enableGUIStandardItemLighting();
                GL11.glDisable(2896);
                GL11.glEnable(32826);
                GL11.glEnable(2903);
                GL11.glEnable(2896);
                Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(InventoryUtils.cycleItemStack(iconResearch.getIcons()[idx]), iconX, iconY);
                GL11.glDisable(2896);
                GL11.glDepthMask(true);
                GL11.glEnable(2929);
            }
            else if (iconResearch.getIcons()[idx] instanceof String && ((String)iconResearch.getIcons()[idx]).startsWith("focus")) {
                final String k = ((String)iconResearch.getIcons()[idx]).replaceAll("focus:", "");
                final IFocusElement fp = FocusEngine.getElement(k.trim());
                if (fp != null && fp instanceof FocusNode) {
                    GuiFocalManipulator.drawPart((FocusNode)fp, iconX + 8, iconY + 8, 24.0f, bw ? 50 : 220, false);
                }
            }
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        }
    }
    
    private void genResearchBackgroundFixedPost(final int mx, final int my, final float par3, final int locX, final int locY) {
        this.mc.renderEngine.bindTexture(this.tx1);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        for (int c = 16; c < this.width - 16; c += 64) {
            int p = 64;
            if (c + p > this.width - 16) {
                p = this.width - 16 - c;
            }
            if (p > 0) {
                this.drawTexturedModalRect(c, -2, 48, 13, p, 22);
                this.drawTexturedModalRect(c, this.height - 20, 48, 13, p, 22);
            }
        }
        for (int c = 16; c < this.height - 16; c += 64) {
            int p = 64;
            if (c + p > this.height - 16) {
                p = this.height - 16 - c;
            }
            if (p > 0) {
                this.drawTexturedModalRect(-2, c, 13, 48, 22, p);
                this.drawTexturedModalRect(this.width - 20, c, 13, 48, 22, p);
            }
        }
        this.drawTexturedModalRect(-2, -2, 13, 13, 22, 22);
        this.drawTexturedModalRect(-2, this.height - 20, 13, 13, 22, 22);
        this.drawTexturedModalRect(this.width - 20, -2, 13, 13, 22, 22);
        this.drawTexturedModalRect(this.width - 20, this.height - 20, 13, 13, 22, 22);
        GL11.glPopMatrix();
        this.zLevel = 0.0f;
        GL11.glDepthFunc(515);
        GL11.glDisable(2929);
        GL11.glEnable(3553);
        super.drawScreen(mx, my, par3);
        if (this.currentHighlight != null) {
            final ArrayList<String> text = new ArrayList<String>();
            text.add("§6" + this.currentHighlight.getLocalizedName());
            if (this.canUnlockResearch(this.currentHighlight)) {
                if (!ThaumcraftCapabilities.getKnowledge(this.player).isResearchComplete(this.currentHighlight.getKey()) && this.currentHighlight.getStages() != null) {
                    final int stage = ThaumcraftCapabilities.getKnowledge(this.player).getResearchStage(this.currentHighlight.getKey());
                    if (stage > 0) {
                        text.add("@@" + TextFormatting.AQUA + I18n.translateToLocal("tc.research.stage") + " " + stage + "/" + this.currentHighlight.getStages().length + TextFormatting.RESET);
                    }
                    else {
                        text.add("@@" + TextFormatting.GREEN + I18n.translateToLocal("tc.research.begin") + TextFormatting.RESET);
                    }
                }
            }
            else {
                text.add("@@§c" + I18n.translateToLocal("tc.researchmissing"));
                int a = 0;
                for (final String p2 : this.currentHighlight.getParents()) {
                    if (!ThaumcraftCapabilities.knowsResearchStrict(this.player, p2)) {
                        String s = "?";
                        try {
                            s = ResearchCategories.getResearch(this.currentHighlight.getParentsClean()[a]).getLocalizedName();
                        }
                        catch (final Exception ex) {}
                        text.add("@@§e - " + s);
                    }
                    ++a;
                }
            }
            if (ThaumcraftCapabilities.getKnowledge(this.player).hasResearchFlag(this.currentHighlight.getKey(), IPlayerKnowledge.EnumResearchFlag.RESEARCH)) {
                text.add("@@" + I18n.translateToLocal("tc.research.newresearch"));
            }
            if (ThaumcraftCapabilities.getKnowledge(this.player).hasResearchFlag(this.currentHighlight.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE)) {
                text.add("@@" + I18n.translateToLocal("tc.research.newpage"));
            }
            UtilsFX.drawCustomTooltip(this, this.fontRenderer, text, mx + 3, my - 3, -99);
        }
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        RenderHelper.disableStandardItemLighting();
    }
    
    protected void mouseClicked(final int mx, final int my, final int par3) {
        this.popuptime = System.currentTimeMillis() - 1L;
        if (!GuiResearchBrowser.searching && this.currentHighlight != null && !ThaumcraftCapabilities.knowsResearch(this.player, this.currentHighlight.getKey()) && this.canUnlockResearch(this.currentHighlight)) {
            this.updateResearch();
            PacketHandler.INSTANCE.sendToServer(new PacketSyncProgressToServer(this.currentHighlight.getKey(), true));
            this.mc.displayGuiScreen(new GuiResearchPage(this.currentHighlight, null, this.guiMapX, this.guiMapY));
            this.popuptime = System.currentTimeMillis() + 3000L;
            this.popupmessage = new TextComponentTranslation(I18n.translateToLocal("tc.research.popup"), "" + this.currentHighlight.getLocalizedName()).getUnformattedText();
        }
        else if (this.currentHighlight != null && ThaumcraftCapabilities.knowsResearch(this.player, this.currentHighlight.getKey())) {
            ThaumcraftCapabilities.getKnowledge(this.player).clearResearchFlag(this.currentHighlight.getKey(), IPlayerKnowledge.EnumResearchFlag.RESEARCH);
            ThaumcraftCapabilities.getKnowledge(this.player).clearResearchFlag(this.currentHighlight.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE);
            PacketHandler.INSTANCE.sendToServer(new PacketSyncResearchFlagsToServer(this.mc.player, this.currentHighlight.getKey()));
            final int stage = ThaumcraftCapabilities.getKnowledge(this.player).getResearchStage(this.currentHighlight.getKey());
            if (stage > 1 && stage >= this.currentHighlight.getStages().length) {
                PacketHandler.INSTANCE.sendToServer(new PacketSyncProgressToServer(this.currentHighlight.getKey(), false, true, false));
            }
            this.mc.displayGuiScreen(new GuiResearchPage(this.currentHighlight, null, this.guiMapX, this.guiMapY));
        }
        else if (GuiResearchBrowser.searching) {
            int q = 0;
            for (final Pair p : this.searchResults) {
                final SearchResult sr = (SearchResult)p.getRight();
                if (mx > 22 && mx < 18 + this.screenX && my >= 32 + q * 10 && my < 40 + q * 10) {
                    if (ThaumcraftCapabilities.knowsResearch(this.player, sr.key) && !sr.cat) {
                        this.mc.displayGuiScreen(new GuiResearchPage(ResearchCategories.getResearch(sr.key), sr.recipe, this.guiMapX, this.guiMapY));
                        break;
                    }
                    if (this.categoriesTC.contains(sr.key) || this.categoriesOther.contains(sr.key)) {
                        GuiResearchBrowser.searching = false;
                        this.searchField.setVisible(false);
                        this.searchField.setCanLoseFocus(true);
                        this.searchField.setFocused(false);
                        GuiResearchBrowser.selectedCategory = sr.key;
                        this.updateResearch();
                        final double n = (GuiResearchBrowser.guiBoundsLeft + GuiResearchBrowser.guiBoundsRight) / 2;
                        this.tempMapX = n;
                        this.guiMapX = n;
                        final double n2 = (GuiResearchBrowser.guiBoundsBottom + GuiResearchBrowser.guiBoundsTop) / 2;
                        this.tempMapY = n2;
                        this.guiMapY = n2;
                        break;
                    }
                }
                ++q;
                if (32 + (q + 1) * 10 > this.screenY) {
                    break;
                }
            }
        }
        try {
            super.mouseClicked(mx, my, par3);
        }
        catch (final IOException ex) {}
    }
    
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == 2) {
            GuiResearchBrowser.selectedCategory = "";
            GuiResearchBrowser.searching = true;
            this.searchField.setVisible(true);
            this.searchField.setCanLoseFocus(false);
            this.searchField.setFocused(true);
            this.searchField.setText("");
            this.updateSearch();
        }
        if (button.id == 3 && GuiResearchBrowser.catScrollPos > 0) {
            --GuiResearchBrowser.catScrollPos;
            this.updateResearch();
        }
        if (button.id == 4 && GuiResearchBrowser.catScrollPos < GuiResearchBrowser.catScrollMax) {
            ++GuiResearchBrowser.catScrollPos;
            this.updateResearch();
        }
        if (button.id >= 20 && button instanceof GuiCategoryButton && ((GuiCategoryButton)button).key != GuiResearchBrowser.selectedCategory) {
            GuiResearchBrowser.searching = false;
            this.searchField.setVisible(false);
            this.searchField.setCanLoseFocus(true);
            this.searchField.setFocused(false);
            GuiResearchBrowser.selectedCategory = ((GuiCategoryButton)button).key;
            this.updateResearch();
            final double n = (GuiResearchBrowser.guiBoundsLeft + GuiResearchBrowser.guiBoundsRight) / 2;
            this.tempMapX = n;
            this.guiMapX = n;
            final double n2 = (GuiResearchBrowser.guiBoundsBottom + GuiResearchBrowser.guiBoundsTop) / 2;
            this.tempMapY = n2;
            this.guiMapY = n2;
        }
    }
    
    private void playButtonClick() {
        this.mc.getRenderViewEntity().playSound(SoundsTC.clack, 0.4f, 1.0f);
    }
    
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    private void drawLine(final int x, final int y, final int x2, final int y2, final float r, final float g, final float b, final int locX, final int locY, final float zMod, final boolean arrow, final boolean flipped) {
        final float zt = this.zLevel;
        this.zLevel += zMod;
        boolean bigCorner = false;
        int xd;
        int yd;
        int xm;
        int ym;
        int xx;
        int yy;
        if (flipped) {
            xd = Math.abs(x2 - x);
            yd = Math.abs(y2 - y);
            xm = ((xd == 0) ? 0 : ((x2 - x > 0) ? -1 : 1));
            ym = ((yd == 0) ? 0 : ((y2 - y > 0) ? -1 : 1));
            if (xd > 1 && yd > 1) {
                bigCorner = true;
            }
            xx = x2 * 24 - 4 - locX + this.startX;
            yy = y2 * 24 - 4 - locY + this.startY;
        }
        else {
            xd = Math.abs(x - x2);
            yd = Math.abs(y - y2);
            xm = ((xd == 0) ? 0 : ((x - x2 > 0) ? -1 : 1));
            ym = ((yd == 0) ? 0 : ((y - y2 > 0) ? -1 : 1));
            if (xd > 1 && yd > 1) {
                bigCorner = true;
            }
            xx = x * 24 - 4 - locX + this.startX;
            yy = y * 24 - 4 - locY + this.startY;
        }
        GL11.glPushMatrix();
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(r, g, b, 1.0f);
        if (arrow) {
            if (flipped) {
                final int xx2 = x * 24 - 8 - locX + this.startX;
                final int yy2 = y * 24 - 8 - locY + this.startY;
                if (xm < 0) {
                    this.drawTexturedModalRect(xx2, yy2, 160, 112, 32, 32);
                }
                else if (xm > 0) {
                    this.drawTexturedModalRect(xx2, yy2, 128, 112, 32, 32);
                }
                else if (ym > 0) {
                    this.drawTexturedModalRect(xx2, yy2, 64, 112, 32, 32);
                }
                else if (ym < 0) {
                    this.drawTexturedModalRect(xx2, yy2, 96, 112, 32, 32);
                }
            }
            else if (ym < 0) {
                this.drawTexturedModalRect(xx - 4, yy - 4, 64, 112, 32, 32);
            }
            else if (ym > 0) {
                this.drawTexturedModalRect(xx - 4, yy - 4, 96, 112, 32, 32);
            }
            else if (xm > 0) {
                this.drawTexturedModalRect(xx - 4, yy - 4, 160, 112, 32, 32);
            }
            else if (xm < 0) {
                this.drawTexturedModalRect(xx - 4, yy - 4, 128, 112, 32, 32);
            }
        }
        int v = 1;
        int h = 0;
        while (v < yd - (bigCorner ? 1 : 0)) {
            this.drawTexturedModalRect(xx + xm * 24 * h, yy + ym * 24 * v, 0, 228, 24, 24);
            ++v;
        }
        if (bigCorner) {
            if (xm < 0 && ym > 0) {
                this.drawTexturedModalRect(xx + xm * 24 * h - 24, yy + ym * 24 * v, 0, 180, 48, 48);
            }
            if (xm > 0 && ym > 0) {
                this.drawTexturedModalRect(xx + xm * 24 * h, yy + ym * 24 * v, 48, 180, 48, 48);
            }
            if (xm < 0 && ym < 0) {
                this.drawTexturedModalRect(xx + xm * 24 * h - 24, yy + ym * 24 * v - 24, 96, 180, 48, 48);
            }
            if (xm > 0 && ym < 0) {
                this.drawTexturedModalRect(xx + xm * 24 * h, yy + ym * 24 * v - 24, 144, 180, 48, 48);
            }
        }
        else {
            if (xm < 0 && ym > 0) {
                this.drawTexturedModalRect(xx + xm * 24 * h, yy + ym * 24 * v, 48, 228, 24, 24);
            }
            if (xm > 0 && ym > 0) {
                this.drawTexturedModalRect(xx + xm * 24 * h, yy + ym * 24 * v, 72, 228, 24, 24);
            }
            if (xm < 0 && ym < 0) {
                this.drawTexturedModalRect(xx + xm * 24 * h, yy + ym * 24 * v, 96, 228, 24, 24);
            }
            if (xm > 0 && ym < 0) {
                this.drawTexturedModalRect(xx + xm * 24 * h, yy + ym * 24 * v, 120, 228, 24, 24);
            }
        }
        v += (bigCorner ? 1 : 0);
        for (h += (bigCorner ? 2 : 1); h < xd; ++h) {
            this.drawTexturedModalRect(xx + xm * 24 * h, yy + ym * 24 * v, 24, 228, 24, 24);
        }
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
        GL11.glAlphaFunc(516, 0.1f);
        GL11.glPopMatrix();
        this.zLevel = zt;
    }
    
    public static void drawForbidden(final double x, final double y) {
        final int count = FMLClientHandler.instance().getClient().player.ticksExisted;
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0.0);
        UtilsFX.renderQuadCentered(UtilsFX.nodeTexture, 32, 32, 160 + count % 32, 90.0f, 0.33f, 0.0f, 0.44f, 220, 1, 0.9f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    public void drawTexturedModalRectWithDoubles(final float xCoord, final float yCoord, final double minU, final double minV, final double maxU, final double maxV) {
        final float f2 = 0.00390625f;
        final float f3 = 0.00390625f;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder BufferBuilder = tessellator.getBuffer();
        BufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        BufferBuilder.pos(xCoord + 0.0f, yCoord + maxV, this.zLevel).tex((minU + 0.0) * f2, (minV + maxV) * f3).endVertex();
        BufferBuilder.pos(xCoord + maxU, yCoord + maxV, this.zLevel).tex((minU + maxU) * f2, (minV + maxV) * f3).endVertex();
        BufferBuilder.pos(xCoord + maxU, yCoord + 0.0f, this.zLevel).tex((minU + maxU) * f2, (minV + 0.0) * f3).endVertex();
        BufferBuilder.pos(xCoord + 0.0f, yCoord + 0.0f, this.zLevel).tex((minU + 0.0) * f2, (minV + 0.0) * f3).endVertex();
        tessellator.draw();
    }
    
    static {
        GuiResearchBrowser.lastX = -9999.0;
        GuiResearchBrowser.lastY = -9999.0;
        GuiResearchBrowser.selectedCategory = null;
        GuiResearchBrowser.searching = false;
        GuiResearchBrowser.catScrollPos = 0;
        GuiResearchBrowser.catScrollMax = 0;
    }
    
    private class SearchResult implements Comparable
    {
        String key;
        ResourceLocation recipe;
        boolean cat;
        
        private SearchResult(final String key, final ResourceLocation rec) {
            this.key = key;
            this.recipe = rec;
            this.cat = false;
        }
        
        private SearchResult(final String key, final ResourceLocation recipe, final boolean cat) {
            this.key = key;
            this.recipe = recipe;
            this.cat = cat;
        }
        
        @Override
        public int compareTo(final Object arg0) {
            final SearchResult arg = (SearchResult)arg0;
            final int k = this.key.compareTo(arg.key);
            return (k == 0 && this.recipe != null && arg.recipe != null) ? this.recipe.compareTo(arg.recipe) : k;
        }
    }
    
    private class GuiCategoryButton extends GuiButton
    {
        ResearchCategory rc;
        String key;
        boolean flip;
        int completion;
        
        public GuiCategoryButton(final ResearchCategory rc, final String key, final boolean flip, final int p_i1021_1_, final int p_i1021_2_, final int p_i1021_3_, final int p_i1021_4_, final int p_i1021_5_, final String p_i1021_6_, final int completion) {
            super(p_i1021_1_, p_i1021_2_, p_i1021_3_, p_i1021_4_, p_i1021_5_, p_i1021_6_);
            this.rc = rc;
            this.key = key;
            this.flip = flip;
            this.completion = completion;
        }
        
        public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
            return this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y + GuiResearchBrowser.this.addonShift && mouseX < this.x + this.width && mouseY < this.y + this.height + GuiResearchBrowser.this.addonShift;
        }
        
        public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final float partialTicks) {
            if (this.visible) {
                this.hovered = (mouseX >= this.x && mouseY >= this.y + GuiResearchBrowser.this.addonShift && mouseX < this.x + this.width && mouseY < this.y + this.height + GuiResearchBrowser.this.addonShift);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.blendFunc(770, 771);
                mc.renderEngine.bindTexture(GuiResearchBrowser.this.tx1);
                GL11.glPushMatrix();
                if (!GuiResearchBrowser.selectedCategory.equals(this.key)) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
                else {
                    GL11.glColor4f(0.6f, 1.0f, 1.0f, 1.0f);
                }
                this.drawTexturedModalRect(this.x - 3, this.y - 3 + GuiResearchBrowser.this.addonShift, 13, 13, 22, 22);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                mc.renderEngine.bindTexture(this.rc.icon);
                if (GuiResearchBrowser.selectedCategory.equals(this.key) || this.hovered) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
                else {
                    GL11.glColor4f(0.66f, 0.66f, 0.66f, 0.8f);
                }
                UtilsFX.drawTexturedQuadFull((float)this.x, (float)(this.y + GuiResearchBrowser.this.addonShift), -80.0);
                GL11.glPopMatrix();
                mc.renderEngine.bindTexture(GuiResearchBrowser.this.tx1);
                boolean nr = false;
                boolean np = false;
                for (final String rk : this.rc.research.keySet()) {
                    if (ThaumcraftCapabilities.knowsResearch(GuiResearchBrowser.this.player, rk)) {
                        if (!nr && ThaumcraftCapabilities.getKnowledge(GuiResearchBrowser.this.player).hasResearchFlag(rk, IPlayerKnowledge.EnumResearchFlag.RESEARCH)) {
                            nr = true;
                        }
                        if (!np && ThaumcraftCapabilities.getKnowledge(GuiResearchBrowser.this.player).hasResearchFlag(rk, IPlayerKnowledge.EnumResearchFlag.PAGE)) {
                            np = true;
                        }
                        if (nr && np) {
                            break;
                        }
                        continue;
                    }
                }
                if (nr) {
                    GL11.glPushMatrix();
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.7f);
                    GL11.glTranslated(this.x - 2, this.y + GuiResearchBrowser.this.addonShift - 2, 0.0);
                    GL11.glScaled(0.25, 0.25, 1.0);
                    this.drawTexturedModalRect(0, 0, 176, 16, 32, 32);
                    GL11.glPopMatrix();
                }
                if (np) {
                    GL11.glPushMatrix();
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.7f);
                    GL11.glTranslated(this.x - 2, this.y + GuiResearchBrowser.this.addonShift + 9, 0.0);
                    GL11.glScaled(0.25, 0.25, 1.0);
                    this.drawTexturedModalRect(0, 0, 208, 16, 32, 32);
                    GL11.glPopMatrix();
                }
                if (this.hovered) {
                    final String dp = this.displayString + " (" + this.completion + "%)";
                    this.drawString(mc.fontRenderer, dp, this.flip ? (GuiResearchBrowser.this.screenX + 9 - mc.fontRenderer.getStringWidth(dp)) : (this.x + 22), this.y + 4 + GuiResearchBrowser.this.addonShift, 16777215);
                    int t = 9;
                    if (nr) {
                        this.drawString(mc.fontRenderer, I18n.translateToLocal("tc.research.newresearch"), this.flip ? (GuiResearchBrowser.this.screenX + 9 - mc.fontRenderer.getStringWidth(I18n.translateToLocal("tc.research.newresearch"))) : (this.x + 22), this.y + 4 + t + GuiResearchBrowser.this.addonShift, 16777215);
                        t += 9;
                    }
                    if (np) {
                        this.drawString(mc.fontRenderer, I18n.translateToLocal("tc.research.newpage"), this.flip ? (GuiResearchBrowser.this.screenX + 9 - mc.fontRenderer.getStringWidth(I18n.translateToLocal("tc.research.newpage"))) : (this.x + 22), this.y + 4 + t + GuiResearchBrowser.this.addonShift, 16777215);
                    }
                }
                this.mouseDragged(mc, mouseX, mouseY);
            }
        }
    }
    
    private class GuiScrollButton extends GuiButton
    {
        boolean flip;
        
        public GuiScrollButton(final boolean flip, final int p_i1021_1_, final int p_i1021_2_, final int p_i1021_3_, final int p_i1021_4_, final int p_i1021_5_, final String p_i1021_6_) {
            super(p_i1021_1_, p_i1021_2_, p_i1021_3_, p_i1021_4_, p_i1021_5_, p_i1021_6_);
            this.flip = flip;
        }
        
        public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final float partialTicks) {
            if (this.visible) {
                this.hovered = (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.blendFunc(770, 771);
                mc.renderEngine.bindTexture(GuiResearchBrowser.this.tx1);
                GL11.glPushMatrix();
                if (this.hovered) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
                else {
                    GL11.glColor4f(0.7f, 0.7f, 0.7f, 1.0f);
                }
                this.drawTexturedModalRect(this.x, this.y, 51, this.flip ? 71 : 55, 10, 11);
                GL11.glPopMatrix();
                this.mouseDragged(mc, mouseX, mouseY);
            }
        }
    }
    
    private class GuiSearchButton extends GuiButton
    {
        public GuiSearchButton(final int p_i1021_1_, final int p_i1021_2_, final int p_i1021_3_, final int p_i1021_4_, final int p_i1021_5_, final String p_i1021_6_) {
            super(p_i1021_1_, p_i1021_2_, p_i1021_3_, p_i1021_4_, p_i1021_5_, p_i1021_6_);
        }
        
        public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final float partialTicks) {
            if (this.visible) {
                this.hovered = (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.blendFunc(770, 771);
                mc.renderEngine.bindTexture(GuiResearchBrowser.this.tx1);
                GL11.glPushMatrix();
                if (this.hovered) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
                else {
                    GL11.glColor4f(0.8f, 0.8f, 0.8f, 1.0f);
                }
                this.drawTexturedModalRect(this.x, this.y, 160, 16, 16, 16);
                GL11.glPopMatrix();
                if (this.hovered) {
                    this.drawString(mc.fontRenderer, this.displayString, this.x + 19, this.y + 4, 16777215);
                }
                this.mouseDragged(mc, mouseX, mouseY);
            }
        }
    }
}
