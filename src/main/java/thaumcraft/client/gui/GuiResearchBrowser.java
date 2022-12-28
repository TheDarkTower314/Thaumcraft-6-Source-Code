package thaumcraft.client.gui;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncProgressToServer;
import thaumcraft.common.lib.network.playerdata.PacketSyncResearchFlagsToServer;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.InventoryUtils;


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
        mouseX = 0;
        mouseY = 0;
        screenZoom = 1.0f;
        isMouseButtonDown = 0;
        instance = null;
        startX = 16;
        startY = 16;
        t = 0L;
        research = new LinkedList<ResearchEntry>();
        currentHighlight = null;
        player = null;
        popuptime = 0L;
        popupmessage = "";
        categoriesTC = new ArrayList<String>();
        categoriesOther = new ArrayList<String>();
        addonShift = 0;
        invisible = new ArrayList<String>();
        searchResults = new ArrayList<Pair<String, SearchResult>>();
        tx1 = new ResourceLocation("thaumcraft", "textures/gui/gui_research_browser.png");
        double lastX = GuiResearchBrowser.lastX;
        tempMapX = lastX;
        guiMapX = lastX;
        curMouseX = lastX;
        double lastY = GuiResearchBrowser.lastY;
        tempMapY = lastY;
        guiMapY = lastY;
        curMouseY = lastY;
        player = Minecraft.getMinecraft().player;
        instance = this;
    }
    
    public GuiResearchBrowser(double x, double y) {
        mouseX = 0;
        mouseY = 0;
        screenZoom = 1.0f;
        isMouseButtonDown = 0;
        instance = null;
        startX = 16;
        startY = 16;
        t = 0L;
        research = new LinkedList<ResearchEntry>();
        currentHighlight = null;
        player = null;
        popuptime = 0L;
        popupmessage = "";
        categoriesTC = new ArrayList<String>();
        categoriesOther = new ArrayList<String>();
        addonShift = 0;
        invisible = new ArrayList<String>();
        searchResults = new ArrayList<Pair<String, SearchResult>>();
        tx1 = new ResourceLocation("thaumcraft", "textures/gui/gui_research_browser.png");
        tempMapX = x;
        guiMapX = x;
        curMouseX = x;
        tempMapY = y;
        guiMapY = y;
        curMouseY = y;
        player = Minecraft.getMinecraft().player;
        instance = this;
    }
    
    public void initGui() {
        updateResearch();
    }
    
    public void updateResearch() {
        if (mc == null) {
            mc = Minecraft.getMinecraft();
        }
        buttonList.clear();
        buttonList.add(new GuiSearchButton(2, 1, height - 17, 16, 16, I18n.translateToLocalFormatted("tc.search")));
        Keyboard.enableRepeatEvents(true);
        (searchField = new GuiTextField(0, fontRenderer, 20, 20, 89, fontRenderer.FONT_HEIGHT)).setMaxStringLength(15);
        searchField.setEnableBackgroundDrawing(true);
        searchField.setVisible(false);
        searchField.setTextColor(16777215);
        if (GuiResearchBrowser.searching) {
            searchField.setVisible(true);
            searchField.setCanLoseFocus(false);
            searchField.setFocused(true);
            searchField.setText("");
            updateSearch();
        }
        screenX = width - 32;
        screenY = height - 32;
        research.clear();
        if (GuiResearchBrowser.selectedCategory == null) {
            Collection<String> cats = ResearchCategories.researchCategories.keySet();
            GuiResearchBrowser.selectedCategory = cats.iterator().next();
        }
        int limit = (int)Math.floor((screenY - 28) / 24.0f);
        addonShift = 0;
        int count = 0;
        categoriesTC.clear();
        categoriesOther.clear();
    Label_0283:
        for (String rcl : ResearchCategories.researchCategories.keySet()) {
            int rt = 0;
            int rco = 0;
            Collection col = ResearchCategories.getResearchCategory(rcl).research.values();
            for (Object res : col) {
                if (((ResearchEntry)res).hasMeta(ResearchEntry.EnumResearchMeta.AUTOUNLOCK)) {
                    continue;
                }
                ++rt;
                if (!ThaumcraftCapabilities.knowsResearch(player, ((ResearchEntry)res).getKey())) {
                    continue;
                }
                ++rco;
            }
            int v = (int)(rco / (float)rt * 100.0f);
            ResearchCategory rc = ResearchCategories.getResearchCategory(rcl);
            if (rc.researchKey != null && !ThaumcraftCapabilities.knowsResearchStrict(player, rc.researchKey)) {
                continue;
            }
            for (String tcc : ConfigResearch.TCCategories) {
                if (tcc.equals(rcl)) {
                    categoriesTC.add(rcl);
                    buttonList.add(new GuiCategoryButton(rc, rcl, false, 20 + categoriesTC.size(), 1, 10 + categoriesTC.size() * 24, 16, 16, I18n.translateToLocalFormatted("tc.research_category." + rcl), v));
                    continue Label_0283;
                }
            }
            if (++count > limit + GuiResearchBrowser.catScrollPos) {
                continue;
            }
            if (count - 1 < GuiResearchBrowser.catScrollPos) {
                continue;
            }
            categoriesOther.add(rcl);
            buttonList.add(new GuiCategoryButton(rc, rcl, true, 50 + categoriesOther.size(), width - 17, 10 + categoriesOther.size() * 24, 16, 16, I18n.translateToLocalFormatted("tc.research_category." + rcl), v));
        }
        if (count > limit || count < GuiResearchBrowser.catScrollPos) {
            addonShift = (screenY - 28) % 24 / 2;
            buttonList.add(new GuiScrollButton(false, 3, width - 14, 20, 10, 11, ""));
            buttonList.add(new GuiScrollButton(true, 4, width - 14, screenY + 1, 10, 11, ""));
        }
        GuiResearchBrowser.catScrollMax = count - limit;
        if (GuiResearchBrowser.selectedCategory == null || GuiResearchBrowser.selectedCategory.equals("")) {
            return;
        }
        Collection col2 = ResearchCategories.getResearchCategory(GuiResearchBrowser.selectedCategory).research.values();
        for (Object res2 : col2) {
            research.add((ResearchEntry)res2);
        }
        GuiResearchBrowser.guiBoundsLeft = 99999;
        GuiResearchBrowser.guiBoundsTop = 99999;
        GuiResearchBrowser.guiBoundsRight = -99999;
        GuiResearchBrowser.guiBoundsBottom = -99999;
        for (ResearchEntry res3 : research) {
            if (res3 != null && isVisible(res3)) {
                if (res3.getDisplayColumn() * 24 - screenX + 48 < GuiResearchBrowser.guiBoundsLeft) {
                    GuiResearchBrowser.guiBoundsLeft = res3.getDisplayColumn() * 24 - screenX + 48;
                }
                if (res3.getDisplayColumn() * 24 - 24 > GuiResearchBrowser.guiBoundsRight) {
                    GuiResearchBrowser.guiBoundsRight = res3.getDisplayColumn() * 24 - 24;
                }
                if (res3.getDisplayRow() * 24 - screenY + 48 < GuiResearchBrowser.guiBoundsTop) {
                    GuiResearchBrowser.guiBoundsTop = res3.getDisplayRow() * 24 - screenY + 48;
                }
                if (res3.getDisplayRow() * 24 - 24 <= GuiResearchBrowser.guiBoundsBottom) {
                    continue;
                }
                GuiResearchBrowser.guiBoundsBottom = res3.getDisplayRow() * 24 - 24;
            }
        }
    }
    
    private boolean isVisible(ResearchEntry res) {
        if (ThaumcraftCapabilities.knowsResearch(player, res.getKey())) {
            return true;
        }
        if (invisible.contains(res.getKey()) || (res.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN) && !canUnlockResearch(res))) {
            return false;
        }
        if (res.getParents() == null && res.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN)) {
            return false;
        }
        if (res.getParents() != null) {
            for (String r : res.getParents()) {
                ResearchEntry ri = ResearchCategories.getResearch(r);
                if (ri != null && !isVisible(ri)) {
                    invisible.add(r);
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean canUnlockResearch(ResearchEntry res) {
        return ResearchManager.doesPlayerHaveRequisites(player, res.getKey());
    }
    
    public void onGuiClosed() {
        GuiResearchBrowser.lastX = guiMapX;
        GuiResearchBrowser.lastY = guiMapY;
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }
    
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        updateResearch();
        if (GuiResearchBrowser.lastX == -9999.0 || guiMapX > GuiResearchBrowser.guiBoundsRight || guiMapX < GuiResearchBrowser.guiBoundsLeft) {
            double n = (GuiResearchBrowser.guiBoundsLeft + GuiResearchBrowser.guiBoundsRight) / 2;
            tempMapX = n;
            guiMapX = n;
        }
        if (GuiResearchBrowser.lastY == -9999.0 || guiMapY > GuiResearchBrowser.guiBoundsBottom || guiMapY < GuiResearchBrowser.guiBoundsTop) {
            double n2 = (GuiResearchBrowser.guiBoundsBottom + GuiResearchBrowser.guiBoundsTop) / 2;
            tempMapY = n2;
            guiMapY = n2;
        }
    }
    
    protected void keyTyped(char par1, int par2) throws IOException {
        if (GuiResearchBrowser.searching && searchField.textboxKeyTyped(par1, par2)) {
            updateSearch();
        }
        else if (par2 == mc.gameSettings.keyBindInventory.getKeyCode()) {
            mc.displayGuiScreen(null);
            mc.setIngameFocus();
        }
        super.keyTyped(par1, par2);
    }
    
    private void updateSearch() {
        searchResults.clear();
        invisible.clear();
        String s1 = searchField.getText().toLowerCase();
        for (String cat : categoriesTC) {
            if (cat.toLowerCase().contains(s1)) {
                searchResults.add(Pair.of(I18n.translateToLocalFormatted("tc.research_category." + cat), new SearchResult(cat, null, true)));
            }
        }
        for (String cat : categoriesOther) {
            if (cat.toLowerCase().contains(s1)) {
                searchResults.add(Pair.of(I18n.translateToLocalFormatted("tc.research_category." + cat), new SearchResult(cat, null, true)));
            }
        }
        ArrayList<ResourceLocation> dupCheck = new ArrayList<ResourceLocation>();
        for (String pre : ThaumcraftCapabilities.getKnowledge(player).getResearchList()) {
            ResearchEntry ri = ResearchCategories.getResearch(pre);
            if (ri != null) {
                if (ri.getLocalizedName() == null) {
                    continue;
                }
                if (ri.getLocalizedName().toLowerCase().contains(s1)) {
                    searchResults.add(Pair.of(ri.getLocalizedName(), new SearchResult(pre, null)));
                }
                int stage = ThaumcraftCapabilities.getKnowledge(player).getResearchStage(pre);
                if (ri.getStages() == null) {
                    continue;
                }
                int s2 = (ri.getStages().length - 1 < stage + 1) ? (ri.getStages().length - 1) : (stage + 1);
                ResearchStage page = ri.getStages()[s2];
                if (page == null || page.getRecipes() == null) {
                    continue;
                }
                for (ResourceLocation rec : page.getRecipes()) {
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
                                searchResults.add(Pair.of(ro.getDisplayName(), new SearchResult(pre, rec)));
                            }
                        }
                    }
                }
            }
        }
        Collections.sort(searchResults);
    }
    
    public void drawScreen(int mx, int my, float par3) {
        if (!GuiResearchBrowser.searching) {
            if (Mouse.isButtonDown(0)) {
                if ((isMouseButtonDown == 0 || isMouseButtonDown == 1) && mx >= startX && mx < startX + screenX && my >= startY && my < startY + screenY) {
                    if (isMouseButtonDown == 0) {
                        isMouseButtonDown = 1;
                    }
                    else {
                        guiMapX -= (mx - mouseX) * (double) screenZoom;
                        guiMapY -= (my - mouseY) * (double) screenZoom;
                        double guiMapX = this.guiMapX;
                        curMouseX = guiMapX;
                        tempMapX = guiMapX;
                        double guiMapY = this.guiMapY;
                        curMouseY = guiMapY;
                        tempMapY = guiMapY;
                    }
                    mouseX = mx;
                    mouseY = my;
                }
                if (tempMapX < GuiResearchBrowser.guiBoundsLeft * (double) screenZoom) {
                    tempMapX = GuiResearchBrowser.guiBoundsLeft * (double) screenZoom;
                }
                if (tempMapY < GuiResearchBrowser.guiBoundsTop * (double) screenZoom) {
                    tempMapY = GuiResearchBrowser.guiBoundsTop * (double) screenZoom;
                }
                if (tempMapX >= GuiResearchBrowser.guiBoundsRight * (double) screenZoom) {
                    tempMapX = GuiResearchBrowser.guiBoundsRight * screenZoom - 1.0f;
                }
                if (tempMapY >= GuiResearchBrowser.guiBoundsBottom * (double) screenZoom) {
                    tempMapY = GuiResearchBrowser.guiBoundsBottom * screenZoom - 1.0f;
                }
            }
            else {
                isMouseButtonDown = 0;
            }
            int k = Mouse.getDWheel();
            if (k < 0) {
                screenZoom += 0.25f;
            }
            else if (k > 0) {
                screenZoom -= 0.25f;
            }
            screenZoom = MathHelper.clamp(screenZoom, 1.0f, 2.0f);
        }
        drawDefaultBackground();
        t = System.nanoTime() / 50000000L;
        int locX = MathHelper.floor(curMouseX + (guiMapX - curMouseX) * par3);
        int locY = MathHelper.floor(curMouseY + (guiMapY - curMouseY) * par3);
        if (locX < GuiResearchBrowser.guiBoundsLeft * screenZoom) {
            locX = (int)(GuiResearchBrowser.guiBoundsLeft * screenZoom);
        }
        if (locY < GuiResearchBrowser.guiBoundsTop * screenZoom) {
            locY = (int)(GuiResearchBrowser.guiBoundsTop * screenZoom);
        }
        if (locX >= GuiResearchBrowser.guiBoundsRight * screenZoom) {
            locX = (int)(GuiResearchBrowser.guiBoundsRight * screenZoom - 1.0f);
        }
        if (locY >= GuiResearchBrowser.guiBoundsBottom * screenZoom) {
            locY = (int)(GuiResearchBrowser.guiBoundsBottom * screenZoom - 1.0f);
        }
        genResearchBackgroundFixedPre(mx, my, par3, locX, locY);
        if (!GuiResearchBrowser.searching) {
            GL11.glPushMatrix();
            GL11.glScalef(1.0f / screenZoom, 1.0f / screenZoom, 1.0f);
            genResearchBackgroundZoomable(mx, my, par3, locX, locY);
            GL11.glPopMatrix();
        }
        else {
            searchField.drawTextBox();
            int q = 0;
            for (Pair p : searchResults) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                SearchResult sr = (SearchResult)p.getRight();
                int color = sr.cat ? 14527146 : ((sr.recipe == null) ? 14540253 : 11184861);
                if (sr.recipe != null) {
                    mc.renderEngine.bindTexture(tx1);
                    GL11.glPushMatrix();
                    GL11.glScaled(0.5, 0.5, 0.5);
                    drawTexturedModalRect(44, (32 + q * 10) * 2, 224, 48, 16, 16);
                    GL11.glPopMatrix();
                }
                if (mx > 22 && mx < 18 + screenX && my >= 32 + q * 10 && my < 40 + q * 10) {
                    color = ((sr.recipe == null) ? 16777215 : (sr.cat ? 16764108 : 13421823));
                }
                fontRenderer.drawString((String)p.getLeft(), 32, 32 + q * 10, color);
                ++q;
                if (32 + (q + 1) * 10 > screenY) {
                    fontRenderer.drawString(I18n.translateToLocalFormatted("tc.search.more"), 22, 34 + q * 10, 11184810);
                    break;
                }
            }
        }
        genResearchBackgroundFixedPost(mx, my, par3, locX, locY);
        if (popuptime > System.currentTimeMillis()) {
            ArrayList<String> text = new ArrayList<String>();
            text.add(popupmessage);
            UtilsFX.drawCustomTooltip(this, fontRenderer, text, 10, 34, -99);
        }
    }
    
    public void updateScreen() {
        curMouseX = guiMapX;
        curMouseY = guiMapY;
        double var1 = tempMapX - guiMapX;
        double var2 = tempMapY - guiMapY;
        if (var1 * var1 + var2 * var2 < 4.0) {
            guiMapX += var1;
            guiMapY += var2;
        }
        else {
            guiMapX += var1 * 0.85;
            guiMapY += var2 * 0.85;
        }
    }
    
    private void genResearchBackgroundFixedPre(int par1, int par2, float par3, int locX, int locY) {
        zLevel = 0.0f;
        GL11.glDepthFunc(518);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 0.0f, -200.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
    }
    
    protected void genResearchBackgroundZoomable(int mx, int my, float par3, int locX, int locY) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GL11.glEnable(3042);
        GlStateManager.blendFunc(770, 771);
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getMinecraft().renderEngine.bindTexture(ResearchCategories.getResearchCategory(GuiResearchBrowser.selectedCategory).background);
        drawTexturedModalRectWithDoubles((startX - 2) * screenZoom, (startY - 2) * screenZoom, locX / 2.0, locY / 2.0, (screenX + 4) * screenZoom, (screenY + 4) * screenZoom);
        if (ResearchCategories.getResearchCategory(GuiResearchBrowser.selectedCategory).background2 != null) {
            Minecraft.getMinecraft().renderEngine.bindTexture(ResearchCategories.getResearchCategory(GuiResearchBrowser.selectedCategory).background2);
            drawTexturedModalRectWithDoubles((startX - 2) * screenZoom, (startY - 2) * screenZoom, locX / 1.5, locY / 1.5, (screenX + 4) * screenZoom, (screenY + 4) * screenZoom);
        }
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1f);
        GL11.glPopMatrix();
        GL11.glEnable(2929);
        GL11.glDepthFunc(515);
        mc.renderEngine.bindTexture(tx1);
        if (ThaumcraftCapabilities.getKnowledge(player).getResearchList() != null) {
            for (int index = 0; index < research.size(); ++index) {
                ResearchEntry source = research.get(index);
                if (source.getParents() != null && source.getParents().length > 0) {
                    for (int a = 0; a < source.getParents().length; ++a) {
                        if (source.getParents()[a] != null && ResearchCategories.getResearch(source.getParentsClean()[a]) != null && ResearchCategories.getResearch(source.getParentsClean()[a]).getCategory().equals(GuiResearchBrowser.selectedCategory)) {
                            ResearchEntry parent = ResearchCategories.getResearch(source.getParentsClean()[a]);
                            if (parent.getSiblings() == null || !Arrays.asList(parent.getSiblings()).contains(source.getKey())) {
                                boolean knowsParent = ThaumcraftCapabilities.knowsResearchStrict(player, source.getParents()[a]);
                                boolean b = isVisible(source) && !source.getParents()[a].startsWith("~");
                                if (b) {
                                    if (knowsParent) {
                                        drawLine(source.getDisplayColumn(), source.getDisplayRow(), parent.getDisplayColumn(), parent.getDisplayRow(), 0.6f, 0.6f, 0.6f, locX, locY, 3.0f, true, source.hasMeta(ResearchEntry.EnumResearchMeta.REVERSE));
                                    }
                                    else if (isVisible(parent)) {
                                        drawLine(source.getDisplayColumn(), source.getDisplayRow(), parent.getDisplayColumn(), parent.getDisplayRow(), 0.2f, 0.2f, 0.2f, locX, locY, 2.0f, true, source.hasMeta(ResearchEntry.EnumResearchMeta.REVERSE));
                                    }
                                }
                            }
                        }
                    }
                }
                if (source.getSiblings() != null && source.getSiblings().length > 0) {
                    for (int a = 0; a < source.getSiblings().length; ++a) {
                        if (source.getSiblings()[a] != null && ResearchCategories.getResearch(source.getSiblings()[a]) != null && ResearchCategories.getResearch(source.getSiblings()[a]).getCategory().equals(GuiResearchBrowser.selectedCategory)) {
                            ResearchEntry sibling = ResearchCategories.getResearch(source.getSiblings()[a]);
                            boolean knowsSibling = ThaumcraftCapabilities.knowsResearchStrict(player, sibling.getKey());
                            if (isVisible(source)) {
                                if (!source.getSiblings()[a].startsWith("~")) {
                                    if (knowsSibling) {
                                        drawLine(sibling.getDisplayColumn(), sibling.getDisplayRow(), source.getDisplayColumn(), source.getDisplayRow(), 0.3f, 0.3f, 0.4f, locX, locY, 1.0f, false, source.hasMeta(ResearchEntry.EnumResearchMeta.REVERSE));
                                    }
                                    else if (isVisible(sibling)) {
                                        drawLine(sibling.getDisplayColumn(), sibling.getDisplayRow(), source.getDisplayColumn(), source.getDisplayRow(), 0.1875f, 0.1875f, 0.25f, locX, locY, 0.0f, false, source.hasMeta(ResearchEntry.EnumResearchMeta.REVERSE));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        currentHighlight = null;
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        for (int var24 = 0; var24 < research.size(); ++var24) {
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            ResearchEntry iconResearch = research.get(var24);
            boolean hasWarp = false;
            if (iconResearch.getStages() != null) {
                for (ResearchStage stage : iconResearch.getStages()) {
                    if (stage.getWarp() > 0) {
                        hasWarp = true;
                        break;
                    }
                }
            }
            int var25 = iconResearch.getDisplayColumn() * 24 - locX;
            int var26 = iconResearch.getDisplayRow() * 24 - locY;
            if (var25 >= -24 && var26 >= -24 && var25 <= screenX * screenZoom && var26 <= screenY * screenZoom) {
                int iconX = startX + var25;
                int iconY = startY + var26;
                if (isVisible(iconResearch)) {
                    if (hasWarp) {
                        drawForbidden(iconX + 8, iconY + 8);
                    }
                    if (ThaumcraftCapabilities.getKnowledge(player).isResearchComplete(iconResearch.getKey())) {
                        float var27 = 1.0f;
                        GL11.glColor4f(var27, var27, var27, 1.0f);
                    }
                    else if (canUnlockResearch(iconResearch)) {
                        float var27 = (float)Math.sin(Minecraft.getSystemTime() % 600L / 600.0 * 3.141592653589793 * 2.0) * 0.25f + 0.75f;
                        GL11.glColor4f(var27, var27, var27, 1.0f);
                    }
                    else {
                        float var27 = 0.3f;
                        GL11.glColor4f(var27, var27, var27, 1.0f);
                    }
                    mc.renderEngine.bindTexture(tx1);
                    GL11.glEnable(2884);
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    if (iconResearch.hasMeta(ResearchEntry.EnumResearchMeta.ROUND)) {
                        drawTexturedModalRect(iconX - 8, iconY - 8, 144, 48 + (iconResearch.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN) ? 32 : 0), 32, 32);
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
                        drawTexturedModalRect(iconX - 8, iconY - 8, ix, iy, 32, 32);
                    }
                    if (iconResearch.hasMeta(ResearchEntry.EnumResearchMeta.SPIKY)) {
                        drawTexturedModalRect(iconX - 8, iconY - 8, 176, 48 + (iconResearch.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN) ? 32 : 0), 32, 32);
                    }
                    boolean bw = false;
                    if (!canUnlockResearch(iconResearch)) {
                        float var28 = 0.1f;
                        GL11.glColor4f(var28, var28, var28, 1.0f);
                        bw = true;
                    }
                    if (ThaumcraftCapabilities.getKnowledge(player).hasResearchFlag(iconResearch.getKey(), IPlayerKnowledge.EnumResearchFlag.RESEARCH)) {
                        GL11.glPushMatrix();
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        GL11.glTranslatef((float)(iconX - 9), (float)(iconY - 9), 0.0f);
                        GL11.glScaled(0.5, 0.5, 1.0);
                        drawTexturedModalRect(0, 0, 176, 16, 32, 32);
                        GL11.glPopMatrix();
                    }
                    if (ThaumcraftCapabilities.getKnowledge(player).hasResearchFlag(iconResearch.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE)) {
                        GL11.glPushMatrix();
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        GL11.glTranslatef((float)(iconX - 9), (float)(iconY + 9), 0.0f);
                        GL11.glScaled(0.5, 0.5, 1.0);
                        drawTexturedModalRect(0, 0, 208, 16, 32, 32);
                        GL11.glPopMatrix();
                    }
                    drawResearchIcon(iconResearch, iconX, iconY, zLevel, bw);
                    if (!canUnlockResearch(iconResearch)) {
                        bw = false;
                    }
                    if (mx >= startX && my >= startY && mx < startX + screenX && my < startY + screenY && mx >= (iconX - 2) / screenZoom && mx <= (iconX + 18) / screenZoom && my >= (iconY - 2) / screenZoom && my <= (iconY + 18) / screenZoom) {
                        currentHighlight = iconResearch;
                    }
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        }
        GL11.glDisable(2929);
    }
    
    public static void drawResearchIcon(ResearchEntry iconResearch, int iconX, int iconY, float zLevel, boolean bw) {
        if (iconResearch.getIcons() != null && iconResearch.getIcons().length > 0) {
            int idx = (int)(System.currentTimeMillis() / 1000L % iconResearch.getIcons().length);
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            if (iconResearch.getIcons()[idx] instanceof ResourceLocation) {
                Minecraft.getMinecraft().renderEngine.bindTexture((ResourceLocation)iconResearch.getIcons()[idx]);
                if (bw) {
                    GL11.glColor4f(0.2f, 0.2f, 0.2f, 1.0f);
                }
                int w = GL11.glGetTexLevelParameteri(3553, 0, 4096);
                int h = GL11.glGetTexLevelParameteri(3553, 0, 4097);
                if (h > w && h % w == 0) {
                    int m = h / w;
                    float q = 16.0f / m;
                    float idx2 = System.currentTimeMillis() / 150L % m * q;
                    UtilsFX.drawTexturedQuadF((float)iconX, (float)iconY, 0.0f, idx2, 16.0f, q, zLevel);
                }
                else if (w > h && w % h == 0) {
                    int m = w / h;
                    float q = 16.0f / m;
                    float idx2 = System.currentTimeMillis() / 150L % m * q;
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
                String k = ((String)iconResearch.getIcons()[idx]).replaceAll("focus:", "");
                IFocusElement fp = FocusEngine.getElement(k.trim());
                if (fp != null && fp instanceof FocusNode) {
                    GuiFocalManipulator.drawPart((FocusNode)fp, iconX + 8, iconY + 8, 24.0f, bw ? 50 : 220, false);
                }
            }
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        }
    }
    
    private void genResearchBackgroundFixedPost(int mx, int my, float par3, int locX, int locY) {
        mc.renderEngine.bindTexture(tx1);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        for (int c = 16; c < width - 16; c += 64) {
            int p = 64;
            if (c + p > width - 16) {
                p = width - 16 - c;
            }
            if (p > 0) {
                drawTexturedModalRect(c, -2, 48, 13, p, 22);
                drawTexturedModalRect(c, height - 20, 48, 13, p, 22);
            }
        }
        for (int c = 16; c < height - 16; c += 64) {
            int p = 64;
            if (c + p > height - 16) {
                p = height - 16 - c;
            }
            if (p > 0) {
                drawTexturedModalRect(-2, c, 13, 48, 22, p);
                drawTexturedModalRect(width - 20, c, 13, 48, 22, p);
            }
        }
        drawTexturedModalRect(-2, -2, 13, 13, 22, 22);
        drawTexturedModalRect(-2, height - 20, 13, 13, 22, 22);
        drawTexturedModalRect(width - 20, -2, 13, 13, 22, 22);
        drawTexturedModalRect(width - 20, height - 20, 13, 13, 22, 22);
        GL11.glPopMatrix();
        zLevel = 0.0f;
        GL11.glDepthFunc(515);
        GL11.glDisable(2929);
        GL11.glEnable(3553);
        super.drawScreen(mx, my, par3);
        if (currentHighlight != null) {
            ArrayList<String> text = new ArrayList<String>();
            text.add("§6" + currentHighlight.getLocalizedName());
            if (canUnlockResearch(currentHighlight)) {
                if (!ThaumcraftCapabilities.getKnowledge(player).isResearchComplete(currentHighlight.getKey()) && currentHighlight.getStages() != null) {
                    int stage = ThaumcraftCapabilities.getKnowledge(player).getResearchStage(currentHighlight.getKey());
                    if (stage > 0) {
                        text.add("@@" + TextFormatting.AQUA + I18n.translateToLocal("tc.research.stage") + " " + stage + "/" + currentHighlight.getStages().length + TextFormatting.RESET);
                    }
                    else {
                        text.add("@@" + TextFormatting.GREEN + I18n.translateToLocal("tc.research.begin") + TextFormatting.RESET);
                    }
                }
            }
            else {
                text.add("@@§c" + I18n.translateToLocal("tc.researchmissing"));
                int a = 0;
                for (String p2 : currentHighlight.getParents()) {
                    if (!ThaumcraftCapabilities.knowsResearchStrict(player, p2)) {
                        String s = "?";
                        try {
                            s = ResearchCategories.getResearch(currentHighlight.getParentsClean()[a]).getLocalizedName();
                        }
                        catch (Exception ex) {}
                        text.add("@@§e - " + s);
                    }
                    ++a;
                }
            }
            if (ThaumcraftCapabilities.getKnowledge(player).hasResearchFlag(currentHighlight.getKey(), IPlayerKnowledge.EnumResearchFlag.RESEARCH)) {
                text.add("@@" + I18n.translateToLocal("tc.research.newresearch"));
            }
            if (ThaumcraftCapabilities.getKnowledge(player).hasResearchFlag(currentHighlight.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE)) {
                text.add("@@" + I18n.translateToLocal("tc.research.newpage"));
            }
            UtilsFX.drawCustomTooltip(this, fontRenderer, text, mx + 3, my - 3, -99);
        }
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        RenderHelper.disableStandardItemLighting();
    }
    
    protected void mouseClicked(int mx, int my, int par3) {
        popuptime = System.currentTimeMillis() - 1L;
        if (!GuiResearchBrowser.searching && currentHighlight != null && !ThaumcraftCapabilities.knowsResearch(player, currentHighlight.getKey()) && canUnlockResearch(currentHighlight)) {
            updateResearch();
            PacketHandler.INSTANCE.sendToServer(new PacketSyncProgressToServer(currentHighlight.getKey(), true));
            mc.displayGuiScreen(new GuiResearchPage(currentHighlight, null, guiMapX, guiMapY));
            popuptime = System.currentTimeMillis() + 3000L;
            popupmessage = new TextComponentTranslation(I18n.translateToLocal("tc.research.popup"), "" + currentHighlight.getLocalizedName()).getUnformattedText();
        }
        else if (currentHighlight != null && ThaumcraftCapabilities.knowsResearch(player, currentHighlight.getKey())) {
            ThaumcraftCapabilities.getKnowledge(player).clearResearchFlag(currentHighlight.getKey(), IPlayerKnowledge.EnumResearchFlag.RESEARCH);
            ThaumcraftCapabilities.getKnowledge(player).clearResearchFlag(currentHighlight.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE);
            PacketHandler.INSTANCE.sendToServer(new PacketSyncResearchFlagsToServer(mc.player, currentHighlight.getKey()));
            int stage = ThaumcraftCapabilities.getKnowledge(player).getResearchStage(currentHighlight.getKey());
            if (stage > 1 && stage >= currentHighlight.getStages().length) {
                PacketHandler.INSTANCE.sendToServer(new PacketSyncProgressToServer(currentHighlight.getKey(), false, true, false));
            }
            mc.displayGuiScreen(new GuiResearchPage(currentHighlight, null, guiMapX, guiMapY));
        }
        else if (GuiResearchBrowser.searching) {
            int q = 0;
            for (Pair p : searchResults) {
                SearchResult sr = (SearchResult)p.getRight();
                if (mx > 22 && mx < 18 + screenX && my >= 32 + q * 10 && my < 40 + q * 10) {
                    if (ThaumcraftCapabilities.knowsResearch(player, sr.key) && !sr.cat) {
                        mc.displayGuiScreen(new GuiResearchPage(ResearchCategories.getResearch(sr.key), sr.recipe, guiMapX, guiMapY));
                        break;
                    }
                    if (categoriesTC.contains(sr.key) || categoriesOther.contains(sr.key)) {
                        GuiResearchBrowser.searching = false;
                        searchField.setVisible(false);
                        searchField.setCanLoseFocus(true);
                        searchField.setFocused(false);
                        GuiResearchBrowser.selectedCategory = sr.key;
                        updateResearch();
                        double n = (GuiResearchBrowser.guiBoundsLeft + GuiResearchBrowser.guiBoundsRight) / 2;
                        tempMapX = n;
                        guiMapX = n;
                        double n2 = (GuiResearchBrowser.guiBoundsBottom + GuiResearchBrowser.guiBoundsTop) / 2;
                        tempMapY = n2;
                        guiMapY = n2;
                        break;
                    }
                }
                ++q;
                if (32 + (q + 1) * 10 > screenY) {
                    break;
                }
            }
        }
        try {
            super.mouseClicked(mx, my, par3);
        }
        catch (IOException ex) {}
    }
    
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 2) {
            GuiResearchBrowser.selectedCategory = "";
            GuiResearchBrowser.searching = true;
            searchField.setVisible(true);
            searchField.setCanLoseFocus(false);
            searchField.setFocused(true);
            searchField.setText("");
            updateSearch();
        }
        if (button.id == 3 && GuiResearchBrowser.catScrollPos > 0) {
            --GuiResearchBrowser.catScrollPos;
            updateResearch();
        }
        if (button.id == 4 && GuiResearchBrowser.catScrollPos < GuiResearchBrowser.catScrollMax) {
            ++GuiResearchBrowser.catScrollPos;
            updateResearch();
        }
        if (button.id >= 20 && button instanceof GuiCategoryButton && ((GuiCategoryButton)button).key != GuiResearchBrowser.selectedCategory) {
            GuiResearchBrowser.searching = false;
            searchField.setVisible(false);
            searchField.setCanLoseFocus(true);
            searchField.setFocused(false);
            GuiResearchBrowser.selectedCategory = ((GuiCategoryButton)button).key;
            updateResearch();
            double n = (GuiResearchBrowser.guiBoundsLeft + GuiResearchBrowser.guiBoundsRight) / 2;
            tempMapX = n;
            guiMapX = n;
            double n2 = (GuiResearchBrowser.guiBoundsBottom + GuiResearchBrowser.guiBoundsTop) / 2;
            tempMapY = n2;
            guiMapY = n2;
        }
    }
    
    private void playButtonClick() {
        mc.getRenderViewEntity().playSound(SoundsTC.clack, 0.4f, 1.0f);
    }
    
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    private void drawLine(int x, int y, int x2, int y2, float r, float g, float b, int locX, int locY, float zMod, boolean arrow, boolean flipped) {
        float zt = zLevel;
        zLevel += zMod;
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
            xx = x2 * 24 - 4 - locX + startX;
            yy = y2 * 24 - 4 - locY + startY;
        }
        else {
            xd = Math.abs(x - x2);
            yd = Math.abs(y - y2);
            xm = ((xd == 0) ? 0 : ((x - x2 > 0) ? -1 : 1));
            ym = ((yd == 0) ? 0 : ((y - y2 > 0) ? -1 : 1));
            if (xd > 1 && yd > 1) {
                bigCorner = true;
            }
            xx = x * 24 - 4 - locX + startX;
            yy = y * 24 - 4 - locY + startY;
        }
        GL11.glPushMatrix();
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(r, g, b, 1.0f);
        if (arrow) {
            if (flipped) {
                int xx2 = x * 24 - 8 - locX + startX;
                int yy2 = y * 24 - 8 - locY + startY;
                if (xm < 0) {
                    drawTexturedModalRect(xx2, yy2, 160, 112, 32, 32);
                }
                else if (xm > 0) {
                    drawTexturedModalRect(xx2, yy2, 128, 112, 32, 32);
                }
                else if (ym > 0) {
                    drawTexturedModalRect(xx2, yy2, 64, 112, 32, 32);
                }
                else if (ym < 0) {
                    drawTexturedModalRect(xx2, yy2, 96, 112, 32, 32);
                }
            }
            else if (ym < 0) {
                drawTexturedModalRect(xx - 4, yy - 4, 64, 112, 32, 32);
            }
            else if (ym > 0) {
                drawTexturedModalRect(xx - 4, yy - 4, 96, 112, 32, 32);
            }
            else if (xm > 0) {
                drawTexturedModalRect(xx - 4, yy - 4, 160, 112, 32, 32);
            }
            else if (xm < 0) {
                drawTexturedModalRect(xx - 4, yy - 4, 128, 112, 32, 32);
            }
        }
        int v = 1;
        int h = 0;
        while (v < yd - (bigCorner ? 1 : 0)) {
            drawTexturedModalRect(xx + xm * 24 * h, yy + ym * 24 * v, 0, 228, 24, 24);
            ++v;
        }
        if (bigCorner) {
            if (xm < 0 && ym > 0) {
                drawTexturedModalRect(xx + xm * 24 * h - 24, yy + ym * 24 * v, 0, 180, 48, 48);
            }
            if (xm > 0 && ym > 0) {
                drawTexturedModalRect(xx + xm * 24 * h, yy + ym * 24 * v, 48, 180, 48, 48);
            }
            if (xm < 0 && ym < 0) {
                drawTexturedModalRect(xx + xm * 24 * h - 24, yy + ym * 24 * v - 24, 96, 180, 48, 48);
            }
            if (xm > 0 && ym < 0) {
                drawTexturedModalRect(xx + xm * 24 * h, yy + ym * 24 * v - 24, 144, 180, 48, 48);
            }
        }
        else {
            if (xm < 0 && ym > 0) {
                drawTexturedModalRect(xx + xm * 24 * h, yy + ym * 24 * v, 48, 228, 24, 24);
            }
            if (xm > 0 && ym > 0) {
                drawTexturedModalRect(xx + xm * 24 * h, yy + ym * 24 * v, 72, 228, 24, 24);
            }
            if (xm < 0 && ym < 0) {
                drawTexturedModalRect(xx + xm * 24 * h, yy + ym * 24 * v, 96, 228, 24, 24);
            }
            if (xm > 0 && ym < 0) {
                drawTexturedModalRect(xx + xm * 24 * h, yy + ym * 24 * v, 120, 228, 24, 24);
            }
        }
        v += (bigCorner ? 1 : 0);
        for (h += (bigCorner ? 2 : 1); h < xd; ++h) {
            drawTexturedModalRect(xx + xm * 24 * h, yy + ym * 24 * v, 24, 228, 24, 24);
        }
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
        GL11.glAlphaFunc(516, 0.1f);
        GL11.glPopMatrix();
        zLevel = zt;
    }
    
    public static void drawForbidden(double x, double y) {
        int count = FMLClientHandler.instance().getClient().player.ticksExisted;
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0.0);
        UtilsFX.renderQuadCentered(UtilsFX.nodeTexture, 32, 32, 160 + count % 32, 90.0f, 0.33f, 0.0f, 0.44f, 220, 1, 0.9f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    public void drawTexturedModalRectWithDoubles(float xCoord, float yCoord, double minU, double minV, double maxU, double maxV) {
        float f2 = 0.00390625f;
        float f3 = 0.00390625f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder BufferBuilder = tessellator.getBuffer();
        BufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        BufferBuilder.pos(xCoord + 0.0f, yCoord + maxV, zLevel).tex((minU + 0.0) * f2, (minV + maxV) * f3).endVertex();
        BufferBuilder.pos(xCoord + maxU, yCoord + maxV, zLevel).tex((minU + maxU) * f2, (minV + maxV) * f3).endVertex();
        BufferBuilder.pos(xCoord + maxU, yCoord + 0.0f, zLevel).tex((minU + maxU) * f2, (minV + 0.0) * f3).endVertex();
        BufferBuilder.pos(xCoord + 0.0f, yCoord + 0.0f, zLevel).tex((minU + 0.0) * f2, (minV + 0.0) * f3).endVertex();
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
        
        private SearchResult(String key, ResourceLocation rec) {
            this.key = key;
            recipe = rec;
            cat = false;
        }
        
        private SearchResult(String key, ResourceLocation recipe, boolean cat) {
            this.key = key;
            this.recipe = recipe;
            this.cat = cat;
        }
        
        @Override
        public int compareTo(Object arg0) {
            SearchResult arg = (SearchResult)arg0;
            int k = key.compareTo(arg.key);
            return (k == 0 && recipe != null && arg.recipe != null) ? recipe.compareTo(arg.recipe) : k;
        }
    }
    
    private class GuiCategoryButton extends GuiButton
    {
        ResearchCategory rc;
        String key;
        boolean flip;
        int completion;
        
        public GuiCategoryButton(ResearchCategory rc, String key, boolean flip, int p_i1021_1_, int p_i1021_2_, int p_i1021_3_, int p_i1021_4_, int p_i1021_5_, String p_i1021_6_, int completion) {
            super(p_i1021_1_, p_i1021_2_, p_i1021_3_, p_i1021_4_, p_i1021_5_, p_i1021_6_);
            this.rc = rc;
            this.key = key;
            this.flip = flip;
            this.completion = completion;
        }
        
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            return enabled && visible && mouseX >= x && mouseY >= y + addonShift && mouseX < x + width && mouseY < y + height + addonShift;
        }
        
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (visible) {
                hovered = (mouseX >= x && mouseY >= y + addonShift && mouseX < x + width && mouseY < y + height + addonShift);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.blendFunc(770, 771);
                mc.renderEngine.bindTexture(tx1);
                GL11.glPushMatrix();
                if (!GuiResearchBrowser.selectedCategory.equals(key)) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
                else {
                    GL11.glColor4f(0.6f, 1.0f, 1.0f, 1.0f);
                }
                drawTexturedModalRect(x - 3, y - 3 + addonShift, 13, 13, 22, 22);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                mc.renderEngine.bindTexture(rc.icon);
                if (GuiResearchBrowser.selectedCategory.equals(key) || hovered) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
                else {
                    GL11.glColor4f(0.66f, 0.66f, 0.66f, 0.8f);
                }
                UtilsFX.drawTexturedQuadFull((float) x, (float)(y + addonShift), -80.0);
                GL11.glPopMatrix();
                mc.renderEngine.bindTexture(tx1);
                boolean nr = false;
                boolean np = false;
                for (String rk : rc.research.keySet()) {
                    if (ThaumcraftCapabilities.knowsResearch(player, rk)) {
                        if (!nr && ThaumcraftCapabilities.getKnowledge(player).hasResearchFlag(rk, IPlayerKnowledge.EnumResearchFlag.RESEARCH)) {
                            nr = true;
                        }
                        if (!np && ThaumcraftCapabilities.getKnowledge(player).hasResearchFlag(rk, IPlayerKnowledge.EnumResearchFlag.PAGE)) {
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
                    GL11.glTranslated(x - 2, y + addonShift - 2, 0.0);
                    GL11.glScaled(0.25, 0.25, 1.0);
                    drawTexturedModalRect(0, 0, 176, 16, 32, 32);
                    GL11.glPopMatrix();
                }
                if (np) {
                    GL11.glPushMatrix();
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.7f);
                    GL11.glTranslated(x - 2, y + addonShift + 9, 0.0);
                    GL11.glScaled(0.25, 0.25, 1.0);
                    drawTexturedModalRect(0, 0, 208, 16, 32, 32);
                    GL11.glPopMatrix();
                }
                if (hovered) {
                    String dp = displayString + " (" + completion + "%)";
                    drawString(mc.fontRenderer, dp, flip ? (screenX + 9 - mc.fontRenderer.getStringWidth(dp)) : (x + 22), y + 4 + addonShift, 16777215);
                    int t = 9;
                    if (nr) {
                        drawString(mc.fontRenderer, I18n.translateToLocal("tc.research.newresearch"), flip ? (screenX + 9 - mc.fontRenderer.getStringWidth(I18n.translateToLocal("tc.research.newresearch"))) : (x + 22), y + 4 + t + addonShift, 16777215);
                        t += 9;
                    }
                    if (np) {
                        drawString(mc.fontRenderer, I18n.translateToLocal("tc.research.newpage"), flip ? (screenX + 9 - mc.fontRenderer.getStringWidth(I18n.translateToLocal("tc.research.newpage"))) : (x + 22), y + 4 + t + addonShift, 16777215);
                    }
                }
                mouseDragged(mc, mouseX, mouseY);
            }
        }
    }
    
    private class GuiScrollButton extends GuiButton
    {
        boolean flip;
        
        public GuiScrollButton(boolean flip, int p_i1021_1_, int p_i1021_2_, int p_i1021_3_, int p_i1021_4_, int p_i1021_5_, String p_i1021_6_) {
            super(p_i1021_1_, p_i1021_2_, p_i1021_3_, p_i1021_4_, p_i1021_5_, p_i1021_6_);
            this.flip = flip;
        }
        
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (visible) {
                hovered = (mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.blendFunc(770, 771);
                mc.renderEngine.bindTexture(tx1);
                GL11.glPushMatrix();
                if (hovered) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
                else {
                    GL11.glColor4f(0.7f, 0.7f, 0.7f, 1.0f);
                }
                drawTexturedModalRect(x, y, 51, flip ? 71 : 55, 10, 11);
                GL11.glPopMatrix();
                mouseDragged(mc, mouseX, mouseY);
            }
        }
    }
    
    private class GuiSearchButton extends GuiButton
    {
        public GuiSearchButton(int p_i1021_1_, int p_i1021_2_, int p_i1021_3_, int p_i1021_4_, int p_i1021_5_, String p_i1021_6_) {
            super(p_i1021_1_, p_i1021_2_, p_i1021_3_, p_i1021_4_, p_i1021_5_, p_i1021_6_);
        }
        
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (visible) {
                hovered = (mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.blendFunc(770, 771);
                mc.renderEngine.bindTexture(tx1);
                GL11.glPushMatrix();
                if (hovered) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
                else {
                    GL11.glColor4f(0.8f, 0.8f, 0.8f, 1.0f);
                }
                drawTexturedModalRect(x, y, 160, 16, 16, 16);
                GL11.glPopMatrix();
                if (hovered) {
                    drawString(mc.fontRenderer, displayString, x + 19, y + 4, 16777215);
                }
                mouseDragged(mc, mouseX, mouseY);
            }
        }
    }
}
