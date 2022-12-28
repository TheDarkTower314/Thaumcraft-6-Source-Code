package thaumcraft.client.gui;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.*;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.research.ResearchAddendum;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.lib.events.HudHandler;
import thaumcraft.common.config.ConfigRecipes;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.crafting.InfusionEnchantmentRecipe;
import thaumcraft.common.lib.crafting.InfusionRunicAugmentRecipe;
import thaumcraft.common.lib.crafting.Matrix;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncProgressToServer;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.lib.utils.PosXY;



@SideOnly(Side.CLIENT)
public class GuiResearchPage extends GuiScreen
{
    public static LinkedList<ResourceLocation> history;
    protected int paneWidth;
    protected int paneHeight;
    protected double guiMapX;
    protected double guiMapY;
    protected int mouseX;
    protected int mouseY;
    private ResearchEntry research;
    private int currentStage;
    int lastStage;
    boolean hold;
    private int page;
    private int maxPages;
    private int maxAspectPages;
    AspectList knownPlayerAspects;
    IPlayerKnowledge playerKnowledge;
    int rhash;
    float transX;
    float transY;
    float rotX;
    float rotY;
    float rotZ;
    long lastCheck;
    float pt;
    ResourceLocation tex1;
    ResourceLocation tex2;
    ResourceLocation tex3;
    ResourceLocation tex4;
    ResourceLocation dummyResearch;
    ResourceLocation dummyMap;
    ResourceLocation dummyFlask;
    ResourceLocation dummyChest;
    int hrx;
    int hry;
    static ResourceLocation shownRecipe;
    int recipePage;
    int recipePageMax;
    private long lastCycle;
    private boolean showingAspects;
    private boolean showingKnowledge;
    private static int aspectsPage;
    LinkedHashMap<ResourceLocation, ArrayList> recipeLists;
    LinkedHashMap<ResourceLocation, ArrayList> recipeOutputs;
    LinkedHashMap<ResourceLocation, ArrayList> drilldownLists;
    boolean hasRecipePages;
    boolean renderingCompound;
    static boolean cycleMultiblockLines;
    BlueprintBlockAccess blockAccess;
    HashMap<ResourceLocation, BlueprintBlockAccess> blockAccessIcons;
    ArrayList<List> reference;
    private int cycle;
    boolean allowWithPagePopup;
    List tipText;
    private static int PAGEWIDTH = 140;
    private static int PAGEHEIGHT = 210;
    private static PageImage PILINE;
    private static PageImage PIDIV;
    private ArrayList<Page> pages;
    boolean isComplete;
    boolean hasAllRequisites;
    boolean[] hasItem;
    boolean[] hasCraft;
    boolean[] hasResearch;
    boolean[] hasKnow;
    boolean[] hasStats;
    public HashMap<Integer, String> keyCache;
    
    public GuiResearchPage(ResearchEntry research, ResourceLocation recipe, double x, double y) {
        paneWidth = 256;
        paneHeight = 181;
        mouseX = 0;
        mouseY = 0;
        currentStage = 0;
        lastStage = 0;
        hold = false;
        page = 0;
        maxPages = 0;
        maxAspectPages = 0;
        rhash = 0;
        transX = 0.0f;
        transY = 0.0f;
        rotX = 0.0f;
        rotY = 0.0f;
        rotZ = 0.0f;
        lastCheck = 0L;
        tex1 = new ResourceLocation("thaumcraft", "textures/gui/gui_researchbook.png");
        tex2 = new ResourceLocation("thaumcraft", "textures/gui/gui_researchbook_overlay.png");
        tex3 = new ResourceLocation("thaumcraft", "textures/aspects/_back.png");
        tex4 = new ResourceLocation("thaumcraft", "textures/gui/paper.png");
        dummyResearch = new ResourceLocation("thaumcraft", "textures/aspects/_unknown.png");
        dummyMap = new ResourceLocation("thaumcraft", "textures/research/rd_map.png");
        dummyFlask = new ResourceLocation("thaumcraft", "textures/research/rd_flask.png");
        dummyChest = new ResourceLocation("thaumcraft", "textures/research/rd_chest.png");
        hrx = 0;
        hry = 0;
        recipePage = 0;
        recipePageMax = 0;
        lastCycle = 0L;
        showingAspects = false;
        showingKnowledge = false;
        recipeLists = new LinkedHashMap<ResourceLocation, ArrayList>();
        recipeOutputs = new LinkedHashMap<ResourceLocation, ArrayList>();
        drilldownLists = new LinkedHashMap<ResourceLocation, ArrayList>();
        renderingCompound = false;
        blockAccess = null;
        blockAccessIcons = new HashMap<ResourceLocation, BlueprintBlockAccess>();
        reference = new ArrayList<List>();
        cycle = -1;
        allowWithPagePopup = false;
        tipText = null;
        pages = new ArrayList<Page>();
        isComplete = false;
        hasAllRequisites = false;
        hasItem = null;
        hasCraft = null;
        hasResearch = null;
        hasKnow = null;
        hasStats = null;
        keyCache = new HashMap<Integer, String>();
        this.research = research;
        guiMapX = x;
        guiMapY = y;
        mc = Minecraft.getMinecraft();
        playerKnowledge = ThaumcraftCapabilities.getKnowledge(mc.player);
        parsePages();
        knownPlayerAspects = new AspectList();
        for (Aspect a : Aspect.aspects.values()) {
            if (ThaumcraftCapabilities.knowsResearch(mc.player, "!" + a.getTag().toLowerCase())) {
                knownPlayerAspects.add(a, 1);
            }
        }
        maxAspectPages = ((knownPlayerAspects != null) ? MathHelper.ceil(knownPlayerAspects.size() / 5.0f) : 0);
        page = 0;
        if (recipe != null) {
            GuiResearchPage.shownRecipe = recipe;
        }
    }
    
    public void initGui() {
        rotX = 25.0f;
        rotY = -45.0f;
    }
    
    public void drawScreen(int par1, int par2, float par3) {
        hasRecipePages = false;
        long nano = System.nanoTime();
        if (nano > lastCheck) {
            parsePages();
            if (hold) {
                lastCheck = nano + 250000000L;
            }
            else {
                lastCheck = nano + 2000000000L;
            }
            if (currentStage > lastStage) {
                hold = false;
            }
        }
        pt = par3;
        drawDefaultBackground();
        genResearchBackground(par1, par2, par3);
        int sw = (width - paneWidth) / 2;
        int sh = (height - paneHeight) / 2;
        if (!GuiResearchPage.history.isEmpty()) {
            int mx = par1 - (sw + 118);
            int my = par2 - (sh + 190);
            if (mx >= 0 && my >= 0 && mx < 20 && my < 12) {
                mc.fontRenderer.drawStringWithShadow(I18n.translateToLocal("recipe.return"), (float)par1, (float)par2, 16777215);
            }
        }
    }
    
    protected void genResearchBackground(int par1, int par2, float par3) {
        int sw = (width - paneWidth) / 2;
        int sh = (height - paneHeight) / 2;
        float var10 = (width - paneWidth * 1.3f) / 2.0f;
        float var11 = (height - paneHeight * 1.3f) / 2.0f;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(tex1);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GL11.glPushMatrix();
        GL11.glTranslatef(var10, var11, 0.0f);
        GL11.glScalef(1.3f, 1.3f, 1.0f);
        drawTexturedModalRect(0, 0, 0, 0, paneWidth, paneHeight);
        GL11.glPopMatrix();
        reference.clear();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        int current = 0;
        for (int a = 0; a < pages.size(); ++a) {
            if ((current == page || current == page + 1) && current < maxPages) {
                drawPage(pages.get(a), current % 2, sw, sh - 10, par1, par2);
            }
            if (++current > page + 1) {
                break;
            }
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(tex1);
        float bob = MathHelper.sin(mc.player.ticksExisted / 3.0f) * 0.2f + 0.1f;
        if (!GuiResearchPage.history.isEmpty()) {
            drawTexturedModalRectScaled(sw + 118, sh + 190, 38, 202, 20, 12, bob);
        }
        if (page > 0 && GuiResearchPage.shownRecipe == null) {
            drawTexturedModalRectScaled(sw - 16, sh + 190, 0, 184, 12, 8, bob);
        }
        if (page < maxPages - 2 && GuiResearchPage.shownRecipe == null) {
            drawTexturedModalRectScaled(sw + 262, sh + 190, 12, 184, 12, 8, bob);
        }
        if (tipText != null) {
            UtilsFX.drawCustomTooltip(this, mc.fontRenderer, tipText, par1, par2 + 12, 11);
            tipText = null;
        }
    }
    
    private void drawPage(Page pageParm, int side, int x, int y, int mx, int my) {
        if (lastCycle < System.currentTimeMillis()) {
            ++cycle;
            lastCycle = System.currentTimeMillis() + 1000L;
            if (GuiResearchPage.cycleMultiblockLines && blockAccess != null) {
                BlueprintBlockAccess blockAccess = this.blockAccess;
                ++blockAccess.sliceLine;
            }
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        if (page == 0 && side == 0) {
            drawTexturedModalRect(x + 4, y - 7, 24, 184, 96, 4);
            drawTexturedModalRect(x + 4, y + 10, 24, 184, 96, 4);
            int offset = mc.fontRenderer.getStringWidth(research.getLocalizedName());
            if (offset <= 140) {
                mc.fontRenderer.drawString(research.getLocalizedName(), x - 15 + 140 / 2 - offset / 2, y, 2105376);
            }
            else {
                float vv = 140.0f / offset;
                GL11.glPushMatrix();
                GL11.glTranslatef(x - 15 + 140 / 2 - offset / 2 * vv, y + 1.0f * vv, 0.0f);
                GL11.glScalef(vv, vv, vv);
                mc.fontRenderer.drawString(research.getLocalizedName(), 0, 0, 2105376);
                GL11.glPopMatrix();
            }
            y += 28;
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        for (Object content : pageParm.contents) {
            if (content instanceof String) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                String ss = ((String)content).replace("~B", "");
                mc.fontRenderer.drawString(ss, x - 15 + side * 152, y - 6, 0);
                y += mc.fontRenderer.FONT_HEIGHT;
                if (!((String)content).endsWith("~B")) {
                    continue;
                }
                y += (int)(mc.fontRenderer.FONT_HEIGHT * 0.66);
            }
            else {
                if (!(content instanceof PageImage)) {
                    continue;
                }
                PageImage pi = (PageImage)content;
                GL11.glPushMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                mc.renderEngine.bindTexture(pi.loc);
                int pad = (140 - pi.aw) / 2;
                GL11.glTranslatef((float)(x - 15 + side * 152 + pad), (float)(y - 5), 0.0f);
                GL11.glScalef(pi.scale, pi.scale, pi.scale);
                drawTexturedModalRect(0, 0, pi.x, pi.y, pi.w, pi.h);
                GL11.glPopMatrix();
                y += pi.ah + 2;
            }
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        if (playerKnowledge.isResearchComplete("FIRSTSTEPS")) {
            y = (height - paneHeight) / 2 + 9;
            mc.renderEngine.bindTexture(tex1);
            int le = mouseInside(x - 48, y, 25, 16, mx, my) ? 0 : 3;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            drawPopupAt(x - 48, y, 25, 16, mx, my, "tc.aspect.name");
            drawTexturedModalRect(x - 48 + le, y, 76, 232, 24 - le, 16);
            drawTexturedModalRect(x - 28, y, 100, 232, 4, 16);
        }
        if (playerKnowledge.isResearchComplete("KNOWLEDGETYPES") && !research.getKey().equals("KNOWLEDGETYPES")) {
            y = (height - paneHeight) / 2 + 32;
            mc.renderEngine.bindTexture(tex1);
            int le = mouseInside(x - 48, y, 25, 16, mx, my) ? 0 : 3;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            drawPopupAt(x - 48, y, 25, 16, mx, my, "tc.knowledge.name");
            drawTexturedModalRect(x - 49 + le, y, 44, 232, 24 - le, 16);
            drawTexturedModalRect(x - 29, y, 100, 232, 4, 16);
        }
        ResearchStage stage = research.getStages()[currentStage];
        if (stage.getRecipes() != null) {
            drawRecipeBookmarks(x, mx, my, stage);
        }
        if (page == 0 && side == 0 && !isComplete) {
            drawRequirements(x, mx, my, stage);
        }
        if (playerKnowledge.isResearchComplete("KNOWLEDGETYPES") && research.getKey().equals("KNOWLEDGETYPES")) {
            drawKnowledges(x, (height - paneHeight) / 2 - 16 + 210, mx, my, true);
        }
        renderingCompound = false;
        if (showingAspects) {
            drawAspectsInsert(mx, my);
        }
        else if (showingKnowledge) {
            drawKnowledgesInsert(mx, my);
        }
        else if (GuiResearchPage.shownRecipe != null) {
            drawRecipe(mx, my);
        }
        else if (stage.getWarp() > 0 && !isComplete) {
            int warp = stage.getWarp();
            if (warp > 5) {
                warp = 5;
            }
            GuiResearchBrowser.drawForbidden(x - 57, y - 40);
            String s = I18n.translateToLocal("tc.forbidden.level." + warp);
            mc.fontRenderer.drawString(s, x - 56 - mc.fontRenderer.getStringWidth(s) / 2, y - 43, 11180543);
            String text = I18n.translateToLocal("tc.warp.warn");
            drawPopupAt(x - 67, y - 50, 20, 20, mx, my, text.replaceAll("%n", s));
        }
    }
    
    private void drawKnowledgesInsert(int mx, int my) {
        allowWithPagePopup = true;
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(tex4);
        int x = (width - 256) / 2;
        int y = (height - 256) / 2;
        GlStateManager.disableDepth();
        drawTexturedModalRect(x, y, 0, 0, 255, 255);
        GlStateManager.enableDepth();
        GL11.glPushMatrix();
        drawKnowledges(x + 60, (height - paneHeight) / 2 + 75, mx, my, false);
        GL11.glPopMatrix();
        mc.renderEngine.bindTexture(tex1);
        allowWithPagePopup = false;
    }
    
    private void drawKnowledges(int x, int y, int mx, int my, boolean inpage) {
        y -= 18;
        boolean drewSomething = false;
        int amt = 0;
        int par = 0;
        int tc = 0;
        int ka = ResearchCategories.researchCategories.values().size();
        for (IPlayerKnowledge.EnumKnowledgeType type : IPlayerKnowledge.EnumKnowledgeType.values()) {
            int fc = 0;
            int hs = (int)(164.0f / ka);
            boolean b = false;
            for (ResearchCategory category : ResearchCategories.researchCategories.values()) {
                if (!type.hasFields() && category != null) {
                    continue;
                }
                amt = playerKnowledge.getKnowledge(type, category);
                par = playerKnowledge.getKnowledgeRaw(type, category) % type.getProgression();
                if (amt <= 0 && par <= 0) {
                    continue;
                }
                drewSomething = true;
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glPushMatrix();
                mc.renderEngine.bindTexture(HudHandler.KNOW_TYPE[type.ordinal()]);
                GL11.glTranslatef((float)(x - 10 + (inpage ? 18 : hs) * fc), (float)(y - tc * (inpage ? 20 : 28)), 0.0f);
                GL11.glScaled(0.0625, 0.0625, 0.0625);
                drawTexturedModalRect(0, 0, 0, 0, 255, 255);
                if (type.hasFields() && category != null) {
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 0.75f);
                    mc.renderEngine.bindTexture(category.icon);
                    GL11.glTranslatef(0.0f, 0.0f, 1.0f);
                    GL11.glScaled(0.66, 0.66, 0.66);
                    drawTexturedModalRect(66, 66, 0, 0, 255, 255);
                }
                GL11.glPopMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glTranslatef(0.0f, 0.0f, 5.0f);
                String s = "" + amt;
                int m = mc.fontRenderer.getStringWidth(s);
                mc.fontRenderer.drawStringWithShadow(s, (float)(x - 10 + 16 - m + (inpage ? 18 : hs) * fc), (float)(y - tc * (inpage ? 20 : 28) + 8), 16777215);
                s = I18n.translateToLocal("tc.type." + type.toString().toLowerCase());
                if (type.hasFields() && category != null) {
                    s = s + ": " + ResearchCategories.getCategoryName(category.key);
                }
                drawPopupAt(x - 10 + (inpage ? 18 : hs) * fc, y - tc * (inpage ? 20 : 28), mx, my, s);
                if (par > 0) {
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 0.75f);
                    mc.renderEngine.bindTexture(tex1);
                    int l = (int)(par / (float)type.getProgression() * 16.0f);
                    drawTexturedModalRect(x - 10 + (inpage ? 18 : hs) * fc, y + 17 - tc * (inpage ? 20 : 28), 0, 232, l, 2);
                    drawTexturedModalRect(x - 10 + (inpage ? 18 : hs) * fc + l, y + 17 - tc * (inpage ? 20 : 28), l, 234, 16 - l, 2);
                }
                GL11.glTranslatef(0.0f, 0.0f, -5.0f);
                ++fc;
                b = true;
            }
            if (b) {
                ++tc;
            }
        }
        if (inpage && drewSomething) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            mc.renderEngine.bindTexture(tex1);
            drawTexturedModalRect(x + 4, y - tc * (inpage ? 20 : 28) + 12, 24, 184, 96, 8);
        }
    }
    
    private void drawRequirements(int x, int mx, int my, ResearchStage stage) {
        int y = (height - paneHeight) / 2 - 16 + 210;
        GL11.glPushMatrix();
        boolean b = false;
        if (stage.getResearch() != null) {
            y -= 18;
            b = true;
            int shift = 24;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 0.25f);
            mc.renderEngine.bindTexture(tex1);
            drawTexturedModalRect(x - 12, y - 1, 200, 232, 56, 16);
            drawPopupAt(x - 15, y, mx, my, "tc.need.research");
            Object loc = null;
            if (hasResearch != null) {
                if (hasResearch.length != stage.getResearch().length) {
                    hasResearch = new boolean[stage.getResearch().length];
                }
                int ss = 18;
                if (stage.getResearch().length > 6) {
                    ss = 110 / stage.getResearch().length;
                }
                for (int a = 0; a < stage.getResearch().length; ++a) {
                    String key = stage.getResearch()[a];
                    loc = ((stage.getResearchIcon()[a] != null) ? new ResourceLocation(stage.getResearchIcon()[a]) : dummyResearch);
                    String text = I18n.translateToLocal("research." + key + ".text");
                    if (key.startsWith("!")) {
                        String k = key.replaceAll("!", "");
                        Aspect as = Aspect.aspects.get(k);
                        if (as != null) {
                            loc = as;
                            text = as.getName();
                        }
                    }
                    ResearchEntry re = ResearchCategories.getResearch(key);
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    if (re != null && re.getIcons() != null) {
                        int idx = (int)(System.currentTimeMillis() / 1000L % re.getIcons().length);
                        loc = re.getIcons()[idx];
                        text = re.getLocalizedName();
                    }
                    else if (key.startsWith("m_")) {
                        loc = dummyMap;
                    }
                    else if (key.startsWith("c_")) {
                        loc = dummyChest;
                    }
                    else if (key.startsWith("f_")) {
                        loc = dummyFlask;
                    }
                    else {
                        GlStateManager.color(0.5f, 0.75f, 1.0f, 1.0f);
                    }
                    GL11.glPushMatrix();
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    if (loc instanceof Aspect) {
                        mc.renderEngine.bindTexture(((Aspect)loc).getImage());
                        Color cc = new Color(((Aspect)loc).getColor());
                        GlStateManager.color(cc.getRed() / 255.0f, cc.getGreen() / 255.0f, cc.getBlue() / 255.0f, 1.0f);
                        UtilsFX.drawTexturedQuadFull((float)(x - 15 + shift), (float)y, zLevel);
                    }
                    else if (loc instanceof ResourceLocation) {
                        mc.renderEngine.bindTexture((ResourceLocation)loc);
                        UtilsFX.drawTexturedQuadFull((float)(x - 15 + shift), (float)y, zLevel);
                    }
                    else if (loc instanceof ItemStack) {
                        RenderHelper.enableGUIStandardItemLighting();
                        GL11.glDisable(2896);
                        GL11.glEnable(32826);
                        GL11.glEnable(2903);
                        GL11.glEnable(2896);
                        itemRender.renderItemAndEffectIntoGUI(InventoryUtils.cycleItemStack(loc), x - 15 + shift, y);
                        GL11.glDisable(2896);
                        GL11.glDepthMask(true);
                        GL11.glEnable(2929);
                    }
                    GL11.glPopMatrix();
                    if (hasResearch[a]) {
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                        mc.renderEngine.bindTexture(tex1);
                        GlStateManager.disableDepth();
                        drawTexturedModalRect(x - 15 + shift + 8, y, 159, 207, 10, 10);
                        GlStateManager.enableDepth();
                    }
                    drawPopupAt(x - 15 + shift, y, mx, my, text);
                    shift += ss;
                }
            }
        }
        if (stage.getObtain() != null) {
            y -= 18;
            b = true;
            int shift = 24;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 0.25f);
            mc.renderEngine.bindTexture(tex1);
            drawTexturedModalRect(x - 12, y - 1, 200, 216, 56, 16);
            drawPopupAt(x - 15, y, mx, my, "tc.need.obtain");
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (hasItem != null) {
                if (hasItem.length != stage.getObtain().length) {
                    hasItem = new boolean[stage.getObtain().length];
                }
                int ss2 = 18;
                if (stage.getObtain().length > 6) {
                    ss2 = 110 / stage.getObtain().length;
                }
                for (int idx2 = 0; idx2 < stage.getObtain().length; ++idx2) {
                    ItemStack stack = InventoryUtils.cycleItemStack(stage.getObtain()[idx2], idx2);
                    drawStackAt(stack, x - 15 + shift, y, mx, my, true);
                    if (hasItem[idx2]) {
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                        mc.renderEngine.bindTexture(tex1);
                        GlStateManager.disableDepth();
                        drawTexturedModalRect(x - 15 + shift + 8, y, 159, 207, 10, 10);
                        GlStateManager.enableDepth();
                    }
                    shift += ss2;
                }
            }
        }
        if (stage.getCraft() != null) {
            y -= 18;
            b = true;
            int shift = 24;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 0.25f);
            mc.renderEngine.bindTexture(tex1);
            drawTexturedModalRect(x - 12, y - 1, 200, 200, 56, 16);
            drawPopupAt(x - 15, y, mx, my, "tc.need.craft");
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (hasCraft != null) {
                if (hasCraft.length != stage.getCraft().length) {
                    hasCraft = new boolean[stage.getCraft().length];
                }
                int ss2 = 18;
                if (stage.getCraft().length > 6) {
                    ss2 = 110 / stage.getCraft().length;
                }
                for (int idx2 = 0; idx2 < stage.getCraft().length; ++idx2) {
                    ItemStack stack = InventoryUtils.cycleItemStack(stage.getCraft()[idx2], idx2);
                    drawStackAt(stack, x - 15 + shift, y, mx, my, true);
                    if (hasCraft[idx2]) {
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                        mc.renderEngine.bindTexture(tex1);
                        GlStateManager.disableDepth();
                        drawTexturedModalRect(x - 15 + shift + 8, y, 159, 207, 10, 10);
                        GlStateManager.enableDepth();
                    }
                    shift += ss2;
                }
            }
        }
        if (stage.getKnow() != null) {
            y -= 18;
            b = true;
            int shift = 24;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 0.25f);
            mc.renderEngine.bindTexture(tex1);
            drawTexturedModalRect(x - 12, y - 1, 200, 184, 56, 16);
            drawPopupAt(x - 15, y, mx, my, "tc.need.know");
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (hasKnow != null) {
                if (hasKnow.length != stage.getKnow().length) {
                    hasKnow = new boolean[stage.getKnow().length];
                }
                int ss2 = 18;
                if (stage.getKnow().length > 6) {
                    ss2 = 110 / stage.getKnow().length;
                }
                for (int idx2 = 0; idx2 < stage.getKnow().length; ++idx2) {
                    ResearchStage.Knowledge kn = stage.getKnow()[idx2];
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    GL11.glPushMatrix();
                    mc.renderEngine.bindTexture(HudHandler.KNOW_TYPE[kn.type.ordinal()]);
                    GL11.glTranslatef((float)(x - 15 + shift), (float)y, 0.0f);
                    GL11.glScaled(0.0625, 0.0625, 0.0625);
                    drawTexturedModalRect(0, 0, 0, 0, 255, 255);
                    if (kn.type.hasFields() && kn.category != null) {
                        mc.renderEngine.bindTexture(kn.category.icon);
                        GL11.glTranslatef(32.0f, 32.0f, 1.0f);
                        GL11.glPushMatrix();
                        GL11.glScaled(0.75, 0.75, 0.75);
                        drawTexturedModalRect(0, 0, 0, 0, 255, 255);
                        GL11.glPopMatrix();
                    }
                    GL11.glPopMatrix();
                    String am = "" + (hasKnow[idx2] ? "" : TextFormatting.RED) + kn.amount;
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)(x - 15 + shift + 16 - mc.fontRenderer.getStringWidth(am) / 2), (float)(y + 12), 5.0f);
                    GL11.glScaled(0.5, 0.5, 0.5);
                    mc.fontRenderer.drawStringWithShadow(am, 0.0f, 0.0f, 16777215);
                    GL11.glPopMatrix();
                    if (hasKnow[idx2]) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef(0.0f, 0.0f, 1.0f);
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                        mc.renderEngine.bindTexture(tex1);
                        drawTexturedModalRect(x - 15 + shift + 8, y, 159, 207, 10, 10);
                        GL11.glPopMatrix();
                    }
                    String s = I18n.translateToLocal("tc.type." + kn.type.toString().toLowerCase());
                    if (kn.type.hasFields() && kn.category != null) {
                        s = s + ": " + ResearchCategories.getCategoryName(kn.category.key);
                    }
                    drawPopupAt(x - 15 + shift, y, mx, my, s);
                    shift += ss2;
                }
            }
        }
        if (b) {
            y -= 12;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            mc.renderEngine.bindTexture(tex1);
            drawTexturedModalRect(x + 4, y - 2, 24, 184, 96, 8);
            if (hasAllRequisites) {
                hrx = x + 20;
                hry = y - 6;
                if (hold) {
                    String s2 = I18n.translateToLocal("tc.stage.hold");
                    int m = mc.fontRenderer.getStringWidth(s2);
                    mc.fontRenderer.drawStringWithShadow(s2, x + 52 - m / 2.0f, (float)(y - 4), 16777215);
                }
                else {
                    if (mouseInside(hrx, hry, 64, 12, mx, my)) {
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    }
                    else {
                        GlStateManager.color(0.8f, 0.8f, 0.9f, 1.0f);
                    }
                    mc.renderEngine.bindTexture(tex1);
                    drawTexturedModalRect(hrx, hry, 84, 216, 64, 12);
                    String s2 = I18n.translateToLocal("tc.stage.complete");
                    int m = mc.fontRenderer.getStringWidth(s2);
                    mc.fontRenderer.drawStringWithShadow(s2, x + 52 - m / 2.0f, (float)(y - 4), 16777215);
                }
            }
        }
        GL11.glPopMatrix();
    }
    
    private void drawRecipeBookmarks(int x, int mx, int my, ResearchStage stage) {
        Random rng = new Random(rhash);
        GL11.glPushMatrix();
        int y = (height - paneHeight) / 2 - 8;
        allowWithPagePopup = true;
        if (recipeOutputs.size() > 0) {
            int space = Math.min(25, 200 / recipeOutputs.size());
            for (ResourceLocation rk : recipeOutputs.keySet()) {
                List list = recipeOutputs.get(rk);
                if (list != null && list.size() > 0) {
                    int i = cycle % list.size();
                    if (list.get(i) == null) {
                        continue;
                    }
                    int sh = rng.nextInt(3);
                    int le = rng.nextInt(3) + (mouseInside(x + 280, y - 1, 30, 16, mx, my) ? 0 : 3);
                    mc.renderEngine.bindTexture(tex1);
                    if (rk.equals(GuiResearchPage.shownRecipe)) {
                        GlStateManager.color(1.0f, 0.5f, 0.5f, 1.0f);
                    }
                    else {
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    }
                    drawTexturedModalRect(x + 280 + sh, y - 1, 120 + le, 232, 28, 16);
                    drawTexturedModalRect(x + 280 + sh, y - 1, 116, 232, 4, 16);
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    UtilsFX.hideStackOverlay = true;
                    if (list.get(i) instanceof ItemStack) {
                        drawStackAt((ItemStack) list.get(i), x + 287 + sh - le, y - 1, mx, my, false);
                    }
                    else if (list.get(i) instanceof Part[][][]) {
                        BlueprintBlockAccess ba = blockAccessIcons.get(rk);
                        if (ba == null) {
                            blockAccessIcons.put(rk, ba = new BlueprintBlockAccess((Part[][][]) list.get(i), true));
                        }
                        int h = ((Part[][][])list.get(i)).length;
                        renderBluePrint(ba, x + 295 + sh - le, y + 6 + h, 4.0f, (Part[][][]) list.get(i), -5000, -5000, null);
                    }
                    UtilsFX.hideStackOverlay = false;
                    y += space;
                }
            }
        }
        allowWithPagePopup = false;
        GL11.glPopMatrix();
    }
    
    private void generateRecipesLists(ResearchStage stage, ResearchAddendum[] addenda) {
        recipeLists.clear();
        recipeOutputs.clear();
        if (stage == null || stage.getRecipes() == null) {
            return;
        }
        for (ResourceLocation rk : stage.getRecipes()) {
            addRecipesToList(rk, recipeLists, recipeOutputs, rk);
        }
        if (addenda == null) {
            return;
        }
        for (ResearchAddendum addendum : addenda) {
            if (addendum.getRecipes() != null && ThaumcraftCapabilities.knowsResearchStrict(mc.player, addendum.getResearch())) {
                for (ResourceLocation rk2 : addendum.getRecipes()) {
                    addRecipesToList(rk2, recipeLists, recipeOutputs, rk2);
                }
            }
        }
    }
    
    private void addRecipesToList(ResourceLocation rk, LinkedHashMap<ResourceLocation, ArrayList> recipeLists2, LinkedHashMap<ResourceLocation, ArrayList> recipeOutputs2, ResourceLocation rkey) {
        Object recipe = CommonInternals.getCatalogRecipe(rk);
        if (recipe == null) {
            recipe = CommonInternals.getCatalogRecipeFake(rk);
        }
        if (recipe == null) {
            recipe = CraftingManager.getRecipe(rk);
        }
        if (recipe == null) {
            recipe = ConfigRecipes.recipeGroups.get(rk.toString());
        }
        if (recipe == null) {
            return;
        }
        if (recipe instanceof ArrayList) {
            for (ResourceLocation rl : (ArrayList<ResourceLocation>)recipe) {
                addRecipesToList(rl, recipeLists2, recipeOutputs2, rk);
            }
        }
        else {
            if (!recipeLists2.containsKey(rkey)) {
                recipeLists2.put(rkey, new ArrayList());
                recipeOutputs2.put(rkey, new ArrayList());
            }
            ArrayList list = recipeLists2.get(rkey);
            ArrayList outputs = recipeOutputs2.get(rkey);
            if (recipe instanceof ThaumcraftApi.BluePrint) {
                ThaumcraftApi.BluePrint r = (ThaumcraftApi.BluePrint)recipe;
                if (ThaumcraftCapabilities.knowsResearchStrict(mc.player, r.getResearch())) {
                    list.add(r);
                    if (r.getDisplayStack() != null) {
                        outputs.add(r.getDisplayStack());
                    }
                    else {
                        outputs.add(r.getParts());
                    }
                }
            }
            else if (recipe instanceof CrucibleRecipe) {
                CrucibleRecipe re = (CrucibleRecipe)recipe;
                ItemStack is = InventoryUtils.cycleItemStack(re.getCatalyst(), 0);
                if (is != null && !is.isEmpty() && ThaumcraftCapabilities.knowsResearchStrict(mc.player, re.getResearch())) {
                    list.add(re);
                    outputs.add(re.getRecipeOutput());
                }
            }
            else if (recipe instanceof InfusionRecipe) {
                InfusionRecipe re2 = (InfusionRecipe)recipe;
                ItemStack is = null;
                if (re2 instanceof InfusionEnchantmentRecipe) {
                    is = InventoryUtils.cycleItemStack(re2.getRecipeOutput(mc.player, re2.getRecipeInput().getMatchingStacks()[0].copy(), null), 0);
                }
                else if (re2 instanceof InfusionRunicAugmentRecipe) {
                    NonNullList<Ingredient> il = ((InfusionRunicAugmentRecipe)re2).getComponents(re2.getRecipeInput().getMatchingStacks()[0]);
                    List<ItemStack> cl = new ArrayList<ItemStack>();
                    for (Ingredient i : il) {
                        cl.add(i.getMatchingStacks()[0]);
                    }
                    is = InventoryUtils.cycleItemStack(re2.getRecipeOutput(mc.player, re2.getRecipeInput().getMatchingStacks()[0].copy(), cl), 0);
                }
                else if (re2.getRecipeOutput() instanceof ItemStack) {
                    is = InventoryUtils.cycleItemStack(re2.getRecipeOutput(), 0);
                }
                else {
                    is = InventoryUtils.cycleItemStack(re2.getRecipeInput());
                    if (is != null && !is.isEmpty()) {
                        is = is.copy();
                    }
                    try {
                        Object[] obj = (Object[])re2.getRecipeOutput();
                        NBTBase tag = (NBTBase)obj[1];
                        is.setTagInfo((String)obj[0], tag);
                    }
                    catch (Exception ex) {}
                }
                if (is != null && ThaumcraftCapabilities.knowsResearchStrict(mc.player, re2.research)) {
                    list.add(re2);
                    outputs.add(is);
                }
            }
            else if (recipe instanceof IArcaneRecipe) {
                IArcaneRecipe re3 = (IArcaneRecipe)recipe;
                ItemStack is = InventoryUtils.cycleItemStack(re3.getRecipeOutput(), 0);
                if (is != null && !is.isEmpty() && ThaumcraftCapabilities.knowsResearchStrict(mc.player, re3.getResearch())) {
                    list.add(re3);
                    outputs.add(re3.getRecipeOutput());
                }
            }
            else if (recipe instanceof IRecipe) {
                IRecipe re4 = (IRecipe)recipe;
                list.add(re4);
                outputs.add(re4.getRecipeOutput());
            }
            else if (recipe instanceof RecipeMisc) {
                RecipeMisc re5 = (RecipeMisc)recipe;
                list.add(re5);
                outputs.add(re5.getOutput());
            }
        }
    }
    
    private void drawRecipe(int mx, int my) {
        allowWithPagePopup = true;
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(tex4);
        int x = (width - 256) / 2;
        int y = (height - 256) / 2;
        GlStateManager.disableDepth();
        drawTexturedModalRect(x, y, 0, 0, 255, 255);
        GlStateManager.enableDepth();
        List list = recipeLists.get(GuiResearchPage.shownRecipe);
        if (list == null || list.size() == 0) {
            list = drilldownLists.get(GuiResearchPage.shownRecipe);
        }
        if (list != null && list.size() > 0) {
            hasRecipePages = (list.size() > 1);
            recipePageMax = list.size() - 1;
            if (recipePage > recipePageMax) {
                recipePage = recipePageMax;
            }
            Object recipe = list.get(recipePage % list.size());
            if (recipe != null) {
                if (recipe instanceof IArcaneRecipe) {
                    drawArcaneCraftingPage(x + 128, y + 128, mx, my, (IArcaneRecipe)recipe);
                }
                else if (recipe instanceof IRecipe) {
                    drawCraftingPage(x + 128, y + 128, mx, my, (IRecipe)recipe);
                }
                else if (recipe instanceof CrucibleRecipe) {
                    drawCruciblePage(x + 128, y + 128, mx, my, (CrucibleRecipe)recipe);
                }
                else if (recipe instanceof InfusionRecipe) {
                    drawInfusionPage(x + 128, y + 128, mx, my, (InfusionRecipe)recipe);
                }
                else if (recipe instanceof ThaumcraftApi.BluePrint) {
                    drawCompoundCraftingPage(x + 128, y + 128, mx, my, (ThaumcraftApi.BluePrint)recipe);
                    renderingCompound = true;
                }
            }
            if (hasRecipePages) {
                mc.renderEngine.bindTexture(tex1);
                float bob = MathHelper.sin(mc.player.ticksExisted / 3.0f) * 0.2f + 0.1f;
                if (recipePage > 0) {
                    drawTexturedModalRectScaled(x + 40, y + 232, 0, 184, 12, 8, bob);
                }
                if (recipePage < recipePageMax) {
                    drawTexturedModalRectScaled(x + 204, y + 232, 12, 184, 12, 8, bob);
                }
            }
        }
        allowWithPagePopup = false;
    }
    
    private void drawCompoundCraftingPage(int x, int y, int mx, int my, ThaumcraftApi.BluePrint recipe) {
        if (recipe.getParts() == null) {
            return;
        }
        if (blockAccess == null) {
            blockAccess = new BlueprintBlockAccess(recipe.getParts(), false);
        }
        int ySize = recipe.getParts().length;
        int xSize = recipe.getParts()[0].length;
        int zSize = recipe.getParts()[0][0].length;
        String text = I18n.translateToLocal("recipe.type.construct");
        int offset = mc.fontRenderer.getStringWidth(text);
        mc.fontRenderer.drawString(text, x - offset / 2, y - 104, 5263440);
        int s = Math.max(Math.max(xSize, zSize), ySize) * 2;
        float scale = (float)(38 - s);
        renderBluePrint(blockAccess, x, y, scale, recipe.getParts(), mx, my, recipe.getIngredientList());
        mc.renderEngine.bindTexture(tex1);
        GlStateManager.color(1.0f, 1.0f, 1.0f, mouseInside(x + 80, y + 100, 8, 8, mx, my) ? 1.0f : 0.75f);
        drawTexturedModalRect(x + 80, y + 100, GuiResearchPage.cycleMultiblockLines ? 168 : 160, 224, 8, 8);
    }
    
    private void renderBluePrint(BlueprintBlockAccess ba, int x, int y, float scale, Part[][][] blueprint, int mx, int my, ItemStack[] ingredients) {
        BlockRendererDispatcher blockRender = Minecraft.getMinecraft().getBlockRendererDispatcher();
        int ySize = blueprint.length;
        int xSize = blueprint[0].length;
        int zSize = blueprint[0][0].length;
        transX = (float)(x - xSize / 2);
        transY = y - (float)Math.sqrt(ySize * ySize + xSize * xSize + zSize * zSize) / 2.0f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.translate(transX, transY, (float)Math.max(ySize, Math.max(xSize, zSize)));
        GlStateManager.scale(scale, -scale, 1.0f);
        GlStateManager.rotate(rotX, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(rotY, 0.0f, 1.0f, 0.0f);
        GlStateManager.translate(zSize / -2.0f, ySize / -2.0f, xSize / -2.0f);
        GlStateManager.disableLighting();
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(7425);
        }
        else {
            GlStateManager.shadeModel(7424);
        }
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        ArrayList<ItemStack> blocks = new ArrayList<ItemStack>();
        for (int h = 0; h < ySize; ++h) {
            for (int l = 0; l < xSize; ++l) {
                for (int w = 0; w < zSize; ++w) {
                    BlockPos pos = new BlockPos(l, h, w);
                    if (!ba.isAirBlock(pos)) {
                        GlStateManager.translate((float)l, (float)h, (float)w);
                        GlStateManager.translate((float)(-l), (float)(-h), (float)(-w));
                        IBlockState state = ba.getBlockState(pos);
                        Tessellator tessellator = Tessellator.getInstance();
                        BufferBuilder buffer = tessellator.getBuffer();
                        buffer.begin(7, DefaultVertexFormats.BLOCK);
                        boolean b = blockRender.renderBlock(state, pos, ba, buffer);
                        tessellator.draw();
                        try {
                            if (!b && state.getBlock().hasTileEntity(state)) {
                                TileEntity te = state.getBlock().createTileEntity(mc.world, state);
                                RenderHelper.enableStandardItemLighting();
                                int i = 250;
                                int j = i % 65536;
                                int k = i / 65536;
                                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
                                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                                TileEntityRendererDispatcher.instance.render(te, pos.getX(), pos.getY(), pos.getZ(), mc.getRenderPartialTicks());
                            }
                        }
                        catch (Exception ex) {}
                    }
                }
            }
        }
        GlStateManager.popMatrix();
        if (ingredients != null) {
            for (int a = 0; a < ingredients.length; ++a) {
                if (ingredients[a] != null && !ingredients[a].isEmpty()) {
                    if (ingredients[a].getItem() != null) {
                        RenderHelper.enableGUIStandardItemLighting();
                        GL11.glDisable(2896);
                        GL11.glEnable(32826);
                        GL11.glEnable(2903);
                        GL11.glEnable(2896);
                        drawStackAt(ingredients[a], x - 85 + a * 17, y + 90, mx, my, true);
                        GL11.glDisable(2896);
                        GL11.glDepthMask(true);
                        GL11.glEnable(2929);
                    }
                }
            }
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.enableBlend();
        RenderHelper.disableStandardItemLighting();
    }
    
    private void drawAspectsInsert(int mx, int my) {
        allowWithPagePopup = true;
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(tex4);
        int x = (width - 256) / 2;
        int y = (height - 256) / 2;
        GlStateManager.disableDepth();
        drawTexturedModalRect(x, y, 0, 0, 255, 255);
        GlStateManager.enableDepth();
        drawAspectPage(x + 60, y + 24, mx, my);
        allowWithPagePopup = false;
    }
    
    private void drawAspectPage(int x, int y, int mx, int my) {
        if (knownPlayerAspects != null && knownPlayerAspects.size() > 0) {
            GL11.glPushMatrix();
            int mposx = mx;
            int mposy = my;
            int count = -1;
            int start = GuiResearchPage.aspectsPage * 5;
            for (Aspect aspect : knownPlayerAspects.getAspectsSortedByName()) {
                if (++count >= start) {
                    if (count > start + 4) {
                        break;
                    }
                    if (aspect.getImage() != null) {
                        int tx = x;
                        int ty = y + count % 5 * 40;
                        if (mposx >= tx && mposy >= ty && mposx < tx + 40 && mposy < ty + 40) {
                            mc.renderEngine.bindTexture(tex3);
                            GL11.glPushMatrix();
                            GlStateManager.enableBlend();
                            GlStateManager.blendFunc(770, 771);
                            GL11.glTranslated(x - 2, y + count % 5 * 40 - 2, 0.0);
                            GL11.glScaled(2.0, 2.0, 0.0);
                            GlStateManager.color(1.0f, 1.0f, 1.0f, 0.5f);
                            UtilsFX.drawTexturedQuadFull(0.0f, 0.0f, zLevel);
                            GL11.glPopMatrix();
                        }
                        GL11.glPushMatrix();
                        GL11.glTranslated(x + 2, y + 2 + count % 5 * 40, 0.0);
                        GL11.glScalef(1.5f, 1.5f, 1.5f);
                        UtilsFX.drawTag(0, 0, aspect, 0.0f, 0, zLevel);
                        GL11.glPopMatrix();
                        GL11.glPushMatrix();
                        GL11.glTranslated(x + 16, y + 29 + count % 5 * 40, 0.0);
                        GL11.glScalef(0.5f, 0.5f, 0.5f);
                        String text = aspect.getName();
                        int offset = mc.fontRenderer.getStringWidth(text) / 2;
                        mc.fontRenderer.drawString(text, -offset, 0, 5263440);
                        GL11.glPopMatrix();
                        if (aspect.getComponents() != null) {
                            GL11.glPushMatrix();
                            GL11.glTranslated(x + 60, y + 4 + count % 5 * 40, 0.0);
                            GL11.glScalef(1.25f, 1.25f, 1.25f);
                            if (playerKnowledge.isResearchKnown("!" + aspect.getComponents()[0].getTag().toLowerCase())) {
                                UtilsFX.drawTag(0, 0, aspect.getComponents()[0], 0.0f, 0, zLevel);
                            }
                            else {
                                mc.renderEngine.bindTexture(dummyResearch);
                                GlStateManager.color(0.8f, 0.8f, 0.8f, 1.0f);
                                UtilsFX.drawTexturedQuadFull(0.0f, 0.0f, zLevel);
                            }
                            GL11.glPopMatrix();
                            GL11.glPushMatrix();
                            GL11.glTranslated(x + 102, y + 4 + count % 5 * 40, 0.0);
                            GL11.glScalef(1.25f, 1.25f, 1.25f);
                            if (playerKnowledge.isResearchKnown("!" + aspect.getComponents()[1].getTag().toLowerCase())) {
                                UtilsFX.drawTag(0, 0, aspect.getComponents()[1], 0.0f, 0, zLevel);
                            }
                            else {
                                mc.renderEngine.bindTexture(dummyResearch);
                                GlStateManager.color(0.8f, 0.8f, 0.8f, 1.0f);
                                UtilsFX.drawTexturedQuadFull(0.0f, 0.0f, zLevel);
                            }
                            GL11.glPopMatrix();
                            if (playerKnowledge.isResearchKnown("!" + aspect.getComponents()[0].getTag().toLowerCase())) {
                                text = aspect.getComponents()[0].getName();
                                offset = mc.fontRenderer.getStringWidth(text) / 2;
                                GL11.glPushMatrix();
                                GL11.glTranslated(x + 22 + 50, y + 29 + count % 5 * 40, 0.0);
                                GL11.glScalef(0.5f, 0.5f, 0.5f);
                                mc.fontRenderer.drawString(text, -offset, 0, 5263440);
                                GL11.glPopMatrix();
                            }
                            if (playerKnowledge.isResearchKnown("!" + aspect.getComponents()[1].getTag().toLowerCase())) {
                                text = aspect.getComponents()[1].getName();
                                offset = mc.fontRenderer.getStringWidth(text) / 2;
                                GL11.glPushMatrix();
                                GL11.glTranslated(x + 22 + 92, y + 29 + count % 5 * 40, 0.0);
                                GL11.glScalef(0.5f, 0.5f, 0.5f);
                                mc.fontRenderer.drawString(text, -offset, 0, 5263440);
                                GL11.glPopMatrix();
                            }
                            mc.fontRenderer.drawString("=", x + 9 + 32, y + 12 + count % 5 * 40, 10066329);
                            mc.fontRenderer.drawString("+", x + 10 + 79, y + 12 + count % 5 * 40, 10066329);
                        }
                        else {
                            mc.fontRenderer.drawString(I18n.translateToLocal("tc.aspect.primal"), x + 54, y + 12 + count % 5 * 40, 7829367);
                        }
                    }
                }
            }
            mc.renderEngine.bindTexture(tex1);
            float bob = MathHelper.sin(mc.player.ticksExisted / 3.0f) * 0.2f + 0.1f;
            if (GuiResearchPage.aspectsPage > 0) {
                drawTexturedModalRectScaled(x - 20, y + 208, 0, 184, 12, 8, bob);
            }
            if (GuiResearchPage.aspectsPage < maxAspectPages - 1) {
                drawTexturedModalRectScaled(x + 144, y + 208, 12, 184, 12, 8, bob);
            }
            GL11.glPopMatrix();
        }
    }
    
    private void drawArcaneCraftingPage(int x, int y, int mx, int my, IArcaneRecipe recipe) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GL11.glPushMatrix();
        mc.renderEngine.bindTexture(tex2);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GL11.glTranslatef((float)x, (float)y, 0.0f);
        GL11.glScalef(2.0f, 2.0f, 1.0f);
        drawTexturedModalRect(-26, -26, 112, 15, 52, 52);
        drawTexturedModalRect(-8, -46, 20, 3, 16, 16);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.4f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GL11.glTranslatef((float)x, (float)y, 0.0f);
        GL11.glScalef(2.0f, 2.0f, 1.0f);
        drawTexturedModalRect(-6, 40, 68, 76, 12, 12);
        GL11.glPopMatrix();
        String text = "" + recipe.getVis();
        int offset = mc.fontRenderer.getStringWidth(text);
        mc.fontRenderer.drawString(text, x - offset / 2, y + 90, 5263440);
        drawPopupAt(x - offset / 2 - 15, y + 75, 30, 30, mx, my, "wandtable.text1");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslated(0.0, 0.0, 100.0);
        drawStackAt(InventoryUtils.cycleItemStack(recipe.getRecipeOutput(), 0), x - 8, y - 84, mx, my, false);
        AspectList crystals = recipe.getCrystals();
        if (crystals != null) {
            int a = 0;
            int sz = crystals.size();
            for (Aspect aspect : crystals.getAspects()) {
                drawStackAt(InventoryUtils.cycleItemStack(ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect)), a), x + 4 - sz * 10 + a * 20, y + 59, mx, my, true);
                ++a;
            }
        }
        if (recipe != null && recipe instanceof ShapedArcaneRecipe) {
            text = I18n.translateToLocal("recipe.type.arcane");
            offset = mc.fontRenderer.getStringWidth(text);
            mc.fontRenderer.drawString(text, x - offset / 2, y - 104, 5263440);
            int rw = ((ShapedArcaneRecipe)recipe).getRecipeWidth();
            int rh = ((ShapedArcaneRecipe)recipe).getRecipeHeight();
            NonNullList<Ingredient> items = recipe.getIngredients();
            for (int i = 0; i < rw && i < 3; ++i) {
                for (int j = 0; j < rh && j < 3; ++j) {
                    if (items.get(i + j * rw) != null) {
                        drawStackAt(InventoryUtils.cycleItemStack(items.get(i + j * rw), i + j * rw), x - 40 + i * 32, y - 40 + j * 32, mx, my, true);
                    }
                }
            }
        }
        if (recipe != null && recipe instanceof ShapelessArcaneRecipe) {
            text = I18n.translateToLocal("recipe.type.arcane.shapeless");
            offset = mc.fontRenderer.getStringWidth(text);
            mc.fontRenderer.drawString(text, x - offset / 2, y - 104, 5263440);
            NonNullList<Ingredient> items2 = recipe.getIngredients();
            for (int k = 0; k < items2.size() && k < 9; ++k) {
                if (items2.get(k) != null) {
                    drawStackAt(InventoryUtils.cycleItemStack(items2.get(k), k), x - 40 + k % 3 * 32, y - 40 + k / 3 * 32, mx, my, true);
                }
            }
        }
        GL11.glPopMatrix();
    }
    
    private void drawCraftingPage(int x, int y, int mx, int my, IRecipe recipe) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        if (recipe == null) {
            return;
        }
        GL11.glPushMatrix();
        mc.renderEngine.bindTexture(tex2);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GL11.glTranslatef((float)x, (float)y, 0.0f);
        GL11.glScalef(2.0f, 2.0f, 1.0f);
        drawTexturedModalRect(-26, -26, 60, 15, 51, 52);
        drawTexturedModalRect(-8, -46, 20, 3, 16, 16);
        GL11.glPopMatrix();
        drawStackAt(InventoryUtils.cycleItemStack(recipe.getRecipeOutput(), 0), x - 8, y - 84, mx, my, false);
        if (recipe != null && recipe instanceof IShapedRecipe) {
            String text = I18n.translateToLocal("recipe.type.workbench");
            int offset = mc.fontRenderer.getStringWidth(text);
            mc.fontRenderer.drawString(text, x - offset / 2, y - 104, 5263440);
            int rw = ((IShapedRecipe)recipe).getRecipeWidth();
            int rh = ((IShapedRecipe)recipe).getRecipeHeight();
            NonNullList<Ingredient> items = recipe.getIngredients();
            for (int i = 0; i < rw && i < 3; ++i) {
                for (int j = 0; j < rh && j < 3; ++j) {
                    if (items.get(i + j * rw) != null) {
                        drawStackAt(InventoryUtils.cycleItemStack(items.get(i + j * rw), i + j * rw), x - 40 + i * 32, y - 40 + j * 32, mx, my, true);
                    }
                }
            }
        }
        if (recipe != null && (recipe instanceof ShapelessRecipes || recipe instanceof ShapelessOreRecipe)) {
            String text = I18n.translateToLocal("recipe.type.workbenchshapeless");
            int offset = mc.fontRenderer.getStringWidth(text);
            mc.fontRenderer.drawString(text, x - offset / 2, y - 104, 5263440);
            NonNullList<Ingredient> items2 = recipe.getIngredients();
            for (int k = 0; k < items2.size() && k < 9; ++k) {
                if (items2.get(k) != null) {
                    drawStackAt(InventoryUtils.cycleItemStack(items2.get(k), k), x - 40 + k % 3 * 32, y - 40 + k / 3 * 32, mx, my, true);
                }
            }
        }
        GL11.glPopMatrix();
    }
    
    private void drawCruciblePage(int x, int y, int mx, int my, CrucibleRecipe rc) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        if (rc != null) {
            GL11.glPushMatrix();
            String text = I18n.translateToLocal("recipe.type.crucible");
            int offset = mc.fontRenderer.getStringWidth(text);
            mc.fontRenderer.drawString(text, x - offset / 2, y - 104, 5263440);
            mc.renderEngine.bindTexture(tex2);
            GL11.glPushMatrix();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GL11.glTranslatef((float)x, (float)y, 0.0f);
            GL11.glScalef(2.0f, 2.0f, 1.0f);
            drawTexturedModalRect(-28, -29, 0, 3, 56, 17);
            GL11.glTranslatef(0.0f, 32.0f, 0.0f);
            drawTexturedModalRect(-28, -44, 0, 20, 56, 48);
            GL11.glTranslatef(0.0f, -8.0f, 0.0f);
            drawTexturedModalRect(-25, -50, 100, 84, 11, 13);
            GL11.glPopMatrix();
            int mposx = mx;
            int mposy = my;
            int total = 0;
            int rows = (rc.getAspects().size() - 1) / 3;
            int shift = (3 - rc.getAspects().size() % 3) * 10;
            int sx = x - 28;
            int sy = y + 8 - 10 * rows;
            for (Aspect tag : rc.getAspects().getAspectsSortedByName()) {
                int m = 0;
                if (total / 3 >= rows && (rows > 1 || rc.getAspects().size() < 3)) {
                    m = 1;
                }
                int vx = sx + total % 3 * 20 + shift * m;
                int vy = sy + total / 3 * 20;
                UtilsFX.drawTag(vx, vy, tag, (float)rc.getAspects().getAmount(tag), 0, zLevel);
                ++total;
            }
            drawStackAt(rc.getRecipeOutput(), x - 8, y - 50, mx, my, false);
            drawStackAt(InventoryUtils.cycleItemStack(rc.getCatalyst(), 0), x - 64, y - 56, mx, my, true);
            total = 0;
            for (Aspect tag : rc.getAspects().getAspectsSortedByName()) {
                int m = 0;
                if (total / 3 >= rows && (rows > 1 || rc.getAspects().size() < 3)) {
                    m = 1;
                }
                int vx = sx + total % 3 * 20 + shift * m;
                int vy = sy + total / 3 * 20;
                if (mposx >= vx && mposy >= vy && mposx < vx + 16 && mposy < vy + 16) {
                    tipText = Arrays.asList(tag.getName(), tag.getLocalizedDescription());
                }
                ++total;
            }
            GL11.glPopMatrix();
        }
    }
    
    private void drawInfusionPage(int x, int y, int mx, int my, InfusionRecipe ri) {
        if (ri != null) {
            NonNullList<Ingredient> components = ri.getComponents();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GL11.glPushMatrix();
            AspectList aspects = ri.getAspects();
            Object output = ri.getRecipeOutput();
            if (ri instanceof InfusionRunicAugmentRecipe) {
                NonNullList<Ingredient> c = components = ((InfusionRunicAugmentRecipe)ri).getComponents(ri.getRecipeInput().getMatchingStacks()[0]);
                ArrayList<ItemStack> com = new ArrayList<ItemStack>();
                for (Ingredient s : c) {
                    com.add(s.getMatchingStacks()[0]);
                }
                aspects = ri.getAspects(mc.player, ri.getRecipeInput().getMatchingStacks()[0], com);
                output = ri.getRecipeOutput(mc.player, ri.getRecipeInput().getMatchingStacks()[0], com);
            }
            if (ri instanceof InfusionEnchantmentRecipe) {
                ArrayList<ItemStack> com2 = new ArrayList<ItemStack>();
                for (Object s2 : components) {
                    if (s2 instanceof ItemStack) {
                        com2.add((ItemStack)s2);
                    }
                }
                aspects = ri.getAspects(mc.player, ri.getRecipeInput().getMatchingStacks()[0], com2);
                output = ri.getRecipeOutput(null, ri.getRecipeInput().getMatchingStacks()[0], com2);
            }
            String text = I18n.translateToLocal("recipe.type.infusion");
            int offset = mc.fontRenderer.getStringWidth(text);
            mc.fontRenderer.drawString(text, x - offset / 2, y - 104, 5263440);
            mc.renderEngine.bindTexture(tex2);
            GL11.glPushMatrix();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GL11.glTranslatef((float)x, (float)(y + 20), 0.0f);
            GL11.glScalef(2.0f, 2.0f, 1.0f);
            drawTexturedModalRect(-28, -56, 0, 3, 56, 17);
            GL11.glTranslatef(0.0f, 19.0f, 0.0f);
            drawTexturedModalRect(-28, -55, 200, 77, 60, 44);
            GL11.glPopMatrix();
            int mposx = mx;
            int mposy = my;
            int total = 0;
            int rows = (aspects.size() - 1) / 5;
            int shift = (5 - aspects.size() % 5) * 10;
            int sx = x - 48;
            int sy = y + 50 - 10 * rows;
            for (Aspect tag : aspects.getAspectsSortedByName()) {
                int m = 0;
                if (total / 5 >= rows && (rows > 1 || aspects.size() < 5)) {
                    m = 1;
                }
                int vx = sx + total % 5 * 20 + shift * m;
                int vy = sy + total / 5 * 20;
                UtilsFX.drawTag(vx, vy, tag, (float)aspects.getAmount(tag), 0, zLevel);
                ++total;
            }
            ItemStack idisp = null;
            if (output instanceof ItemStack) {
                idisp = InventoryUtils.cycleItemStack(output);
            }
            else {
                idisp = InventoryUtils.cycleItemStack(ri.getRecipeInput()).copy();
                try {
                    Object[] obj = (Object[])output;
                    NBTBase tag2 = (NBTBase)obj[1];
                    idisp.setTagInfo((String)obj[0], tag2);
                }
                catch (Exception ex) {}
            }
            drawStackAt(idisp, x - 8, y - 85, mx, my, false);
            ItemStack rinp = InventoryUtils.cycleItemStack(ri.getRecipeInput()).copy();
            drawStackAt(rinp, x - 8, y - 16, mx, my, true);
            GL11.glPushMatrix();
            GL11.glTranslated(0.0, 0.0, 100.0);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            int le = components.size();
            ArrayList<PosXY> coords = new ArrayList<PosXY>();
            float pieSlice = (float)(360 / le);
            float currentRot = -90.0f;
            for (int a = 0; a < le; ++a) {
                int xx = (int)(MathHelper.cos(currentRot / 180.0f * 3.1415927f) * 40.0f) - 8;
                int yy = (int)(MathHelper.sin(currentRot / 180.0f * 3.1415927f) * 40.0f) - 8;
                currentRot += pieSlice;
                coords.add(new PosXY(xx, yy));
            }
            ArrayList<ItemStack> cmps = new ArrayList<ItemStack>();
            total = 0;
            sx = x;
            sy = y - 8;
            for (Ingredient ingredient : components) {
                int vx2 = sx + coords.get(total).x;
                int vy2 = sy + coords.get(total).y;
                ItemStack is = InventoryUtils.cycleItemStack(ingredient);
                drawStackAt(is.copy().splitStack(1), vx2, vy2, mx, my, true);
                ++total;
                cmps.add(is.copy());
            }
            GL11.glPopMatrix();
            int inst = Math.min(5, ri.getInstability(mc.player, rinp, cmps) / 2);
            text = I18n.translateToLocal("tc.inst") + " " + I18n.translateToLocal("tc.inst." + inst);
            offset = mc.fontRenderer.getStringWidth(text);
            mc.fontRenderer.drawString(text, x - offset / 2, y + 94, 5263440);
            total = 0;
            rows = (aspects.size() - 1) / 5;
            shift = (5 - aspects.size() % 5) * 10;
            sx = x - 48;
            sy = y + 50 - 10 * rows;
            for (Aspect tag3 : aspects.getAspectsSortedByName()) {
                int i = 0;
                if (total / 5 >= rows && (rows > 1 || aspects.size() < 5)) {
                    i = 1;
                }
                int vx3 = sx + total % 5 * 20 + shift * i;
                int vy3 = sy + total / 5 * 20;
                if (mposx >= vx3 && mposy >= vy3 && mposx < vx3 + 16 && mposy < vy3 + 16) {
                    tipText = Arrays.asList(tag3.getName(), tag3.getLocalizedDescription());
                }
                ++total;
            }
            GL11.glPopMatrix();
        }
    }
    
    protected void keyTyped(char par1, int par2) throws IOException {
        if (par2 == mc.gameSettings.keyBindInventory.getKeyCode() || par2 == 1) {
            GuiResearchPage.history.clear();
            if (GuiResearchPage.shownRecipe != null || showingAspects || showingKnowledge) {
                GuiResearchPage.shownRecipe = null;
                blockAccess = null;
                showingAspects = false;
                showingKnowledge = false;
                Minecraft.getMinecraft().player.playSound(SoundsTC.page, 0.4f, 1.1f);
            }
            else {
                mc.displayGuiScreen(new GuiResearchBrowser(guiMapX, guiMapY));
            }
        }
        else if (par2 == 203 || par2 == 200 || par2 == 201) {
            prevPage();
        }
        else if (par2 == 205 || par2 == 208 || par2 == 209) {
            nextPage();
        }
        else if (par2 == 14) {
            goBack();
        }
        else {
            super.keyTyped(par1, par2);
        }
    }
    
    private void nextPage() {
        if (page < maxPages - 2) {
            page += 2;
            lastCycle = 0L;
            cycle = -1;
            Minecraft.getMinecraft().player.playSound(SoundsTC.page, 0.66f, 1.0f);
        }
    }
    
    private void prevPage() {
        if (page >= 2) {
            page -= 2;
            lastCycle = 0L;
            cycle = -1;
            Minecraft.getMinecraft().player.playSound(SoundsTC.page, 0.66f, 1.0f);
        }
    }
    
    private void goBack() {
        if (!GuiResearchPage.history.isEmpty()) {
            Minecraft.getMinecraft().player.playSound(SoundsTC.page, 0.66f, 1.0f);
            GuiResearchPage.shownRecipe = GuiResearchPage.history.pop();
            blockAccess = null;
        }
        else {
            GuiResearchPage.shownRecipe = null;
        }
    }
    
    protected void mouseClicked(int par1, int par2, int par3) {
        checkRequisites();
        int var4 = (width - paneWidth) / 2;
        int var5 = (height - paneHeight) / 2;
        int mx = par1 - hrx;
        int my = par2 - hry;
        if (GuiResearchPage.shownRecipe == null && !hold && hasAllRequisites && mx >= 0 && my >= 0 && mx < 64 && my < 12) {
            PacketHandler.INSTANCE.sendToServer(new PacketSyncProgressToServer(research.getKey(), false, true, true));
            Minecraft.getMinecraft().player.playSound(SoundsTC.write, 0.66f, 1.0f);
            lastCheck = 0L;
            lastStage = currentStage;
            hold = true;
            keyCache.clear();
            drilldownLists.clear();
        }
        if (knownPlayerAspects != null && playerKnowledge.isResearchComplete("FIRSTSTEPS")) {
            mx = par1 - (var4 - 48);
            my = par2 - (var5 + 8);
            if (mx >= 0 && my >= 0 && mx < 25 && my < 16) {
                GuiResearchPage.shownRecipe = null;
                showingKnowledge = false;
                showingAspects = !showingAspects;
                blockAccess = null;
                GuiResearchPage.history.clear();
                if (GuiResearchPage.aspectsPage > maxAspectPages) {
                    GuiResearchPage.aspectsPage = 0;
                }
                Minecraft.getMinecraft().player.playSound(SoundsTC.page, 0.7f, 0.9f);
            }
        }
        if (playerKnowledge.isResearchComplete("KNOWLEDGETYPES") && !research.getKey().equals("KNOWLEDGETYPES")) {
            mx = par1 - (var4 - 48);
            my = par2 - (var5 + 31);
            if (mx >= 0 && my >= 0 && mx < 25 && my < 16) {
                GuiResearchPage.shownRecipe = null;
                showingAspects = false;
                showingKnowledge = !showingKnowledge;
                blockAccess = null;
                GuiResearchPage.history.clear();
                Minecraft.getMinecraft().player.playSound(SoundsTC.page, 0.7f, 0.9f);
            }
        }
        mx = par1 - (var4 + 205);
        my = par2 - (var5 + 192);
        if (showingAspects && GuiResearchPage.aspectsPage < maxAspectPages - 1 && mx >= 0 && my >= 0 && mx < 14 && my < 14) {
            ++GuiResearchPage.aspectsPage;
            Minecraft.getMinecraft().player.playSound(SoundsTC.page, 0.7f, 0.9f);
        }
        mx = par1 - (var4 + 38);
        my = par2 - (var5 + 192);
        if (showingAspects && GuiResearchPage.aspectsPage > 0 && mx >= 0 && my >= 0 && mx < 14 && my < 14) {
            --GuiResearchPage.aspectsPage;
            Minecraft.getMinecraft().player.playSound(SoundsTC.page, 0.7f, 0.9f);
        }
        if (recipeLists.size() > 0) {
            int aa = 0;
            int space = Math.min(25, 200 / recipeLists.size());
            for (ResourceLocation rk : recipeLists.keySet()) {
                mx = par1 - (var4 + 280);
                my = par2 - (var5 - 8 + aa * space);
                if (mx >= 0 && my >= 0 && mx < 30 && my < 16) {
                    if (rk.equals(GuiResearchPage.shownRecipe)) {
                        GuiResearchPage.shownRecipe = null;
                    }
                    else {
                        GuiResearchPage.shownRecipe = rk;
                    }
                    showingAspects = false;
                    showingKnowledge = false;
                    blockAccess = null;
                    GuiResearchPage.history.clear();
                    Minecraft.getMinecraft().player.playSound(SoundsTC.page, 0.7f, 0.9f);
                    break;
                }
                ++aa;
            }
        }
        mx = par1 - (var4 + 205);
        my = par2 - (var5 + 192);
        if (hasRecipePages && recipePage < recipePageMax && mx >= 0 && my >= 0 && mx < 14 && my < 14) {
            ++recipePage;
            Minecraft.getMinecraft().player.playSound(SoundsTC.page, 0.7f, 0.9f);
        }
        mx = par1 - (var4 + 38);
        my = par2 - (var5 + 192);
        if (hasRecipePages && recipePage > 0 && mx >= 0 && my >= 0 && mx < 14 && my < 14) {
            --recipePage;
            Minecraft.getMinecraft().player.playSound(SoundsTC.page, 0.7f, 0.9f);
        }
        mx = par1 - (var4 + 261);
        my = par2 - (var5 + 189);
        if (GuiResearchPage.shownRecipe == null && mx >= 0 && my >= 0 && mx < 14 && my < 10) {
            nextPage();
        }
        mx = par1 - (var4 - 17);
        my = par2 - (var5 + 189);
        if (GuiResearchPage.shownRecipe == null && mx >= 0 && my >= 0 && mx < 14 && my < 10) {
            prevPage();
        }
        mx = par1 - (var4 + 118);
        my = par2 - (var5 + 190);
        if (mx >= 0 && my >= 0 && mx < 20 && my < 12) {
            goBack();
        }
        mx = par1 - (var4 + 210);
        my = par2 - (var5 + 190);
        if (renderingCompound && mx >= 0 && my >= 0 && mx < 10 && my < 10) {
            Minecraft.getMinecraft().player.playSound(SoundsTC.clack, 0.66f, 1.0f);
            GuiResearchPage.cycleMultiblockLines = !GuiResearchPage.cycleMultiblockLines;
        }
        if (reference.size() > 0) {
            for (List coords : reference) { //(int, int, RL, String)
                if (par1 >= (int)coords.get(0) && par2 >= (int)coords.get(1) && par1 < (int)coords.get(0) + 16 && par2 < (int)coords.get(1) + 16) {
                    try {
                        Minecraft.getMinecraft().player.playSound(SoundsTC.page, 0.66f, 1.0f);
                    }
                    catch (Exception ex) {}
                    if (GuiResearchPage.shownRecipe != null) {
                        GuiResearchPage.history.push(new ResourceLocation(GuiResearchPage.shownRecipe.getResourceDomain(), GuiResearchPage.shownRecipe.getResourcePath()));
                    }
                    GuiResearchPage.shownRecipe = (ResourceLocation) coords.get(2);
                    recipePage = Integer.parseInt((String) coords.get(3));
                    if (!drilldownLists.containsKey(GuiResearchPage.shownRecipe)) {
                        addRecipesToList(GuiResearchPage.shownRecipe, drilldownLists, new LinkedHashMap<ResourceLocation, ArrayList>(), GuiResearchPage.shownRecipe);
                    }
                    blockAccess = null;
                    break;
                }
            }
        }
        try {
            super.mouseClicked(par1, par2, par3);
        }
        catch (IOException ex2) {}
    }
    
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    void drawPopupAt(int x, int y, int mx, int my, String text) {
        if ((GuiResearchPage.shownRecipe == null || allowWithPagePopup) && mx >= x && my >= y && mx < x + 16 && my < y + 16) {
            ArrayList<String> s = new ArrayList<String>();
            s.add(I18n.translateToLocal(text));
            tipText = s;
        }
    }
    
    void drawPopupAt(int x, int y, int w, int h, int mx, int my, String text) {
        if ((GuiResearchPage.shownRecipe == null || allowWithPagePopup) && mx >= x && my >= y && mx < x + w && my < y + h) {
            ArrayList<String> s = new ArrayList<String>();
            s.add(I18n.translateToLocal(text));
            tipText = s;
        }
    }
    
    boolean mouseInside(int x, int y, int w, int h, int mx, int my) {
        return mx >= x && my >= y && mx < x + w && my < y + h;
    }
    
    void drawStackAt(ItemStack itemstack, int x, int y, int mx, int my, boolean clickthrough) {
        UtilsFX.renderItemStack(mc, itemstack, x, y, null);
        if ((GuiResearchPage.shownRecipe == null || allowWithPagePopup) && mx >= x && my >= y && mx < x + 16 && my < y + 16 && itemstack != null && !itemstack.isEmpty() && itemstack.getItem() != null) {
            if (clickthrough) {
                List addtext = itemstack.getTooltip(mc.player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
                String ref = getCraftingRecipeKey(mc.player, itemstack);
                if (ref != null) {
                    String[] sr = ref.split(";", 2);
                    if (sr != null && sr.length > 1) {
                        ResourceLocation res = new ResourceLocation(sr[0]);
                        if (res.getResourcePath().equals("UNKNOWN")) {
                            addtext.add(TextFormatting.DARK_RED + "" + TextFormatting.ITALIC + I18n.translateToLocal("recipe.unknown"));
                        }
                        else {
                            addtext.add(TextFormatting.BLUE + "" + TextFormatting.ITALIC + I18n.translateToLocal("recipe.clickthrough"));
                            reference.add(Arrays.asList(mx, my, (Comparable)res, sr[1]));
                        }
                    }
                }
                tipText = addtext;
            }
            else {
                tipText = itemstack.getTooltip(mc.player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
            }
        }
    }
    
    public void drawTexturedModalRectScaled(int par1, int par2, int par3, int par4, int par5, int par6, float scale) {
        GL11.glPushMatrix();
        float var7 = 0.00390625f;
        float var8 = 0.00390625f;
        Tessellator var9 = Tessellator.getInstance();
        GL11.glTranslatef(par1 + par5 / 2.0f, par2 + par6 / 2.0f, 0.0f);
        GL11.glScalef(1.0f + scale, 1.0f + scale, 1.0f);
        var9.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
        var9.getBuffer().pos(-par5 / 2.0f, par6 / 2.0f, zLevel).tex((par3 + 0) * var7, (par4 + par6) * var8).endVertex();
        var9.getBuffer().pos(par5 / 2.0f, par6 / 2.0f, zLevel).tex((par3 + par5) * var7, (par4 + par6) * var8).endVertex();
        var9.getBuffer().pos(par5 / 2.0f, -par6 / 2.0f, zLevel).tex((par3 + par5) * var7, (par4 + 0) * var8).endVertex();
        var9.getBuffer().pos(-par5 / 2.0f, -par6 / 2.0f, zLevel).tex((par3 + 0) * var7, (par4 + 0) * var8).endVertex();
        var9.draw();
        GL11.glPopMatrix();
    }
    
    private void parsePages() {
        checkRequisites();
        pages.clear();
        if (research.getStages() == null) {
            return;
        }
        boolean complete = false;
        currentStage = ThaumcraftCapabilities.getKnowledge(mc.player).getResearchStage(research.getKey()) - 1;
        while (currentStage >= research.getStages().length) {
            --currentStage;
            complete = true;
        }
        if (currentStage < 0) {
            currentStage = 0;
        }
        ResearchStage stage = research.getStages()[currentStage];
        ResearchAddendum[] addenda = null;
        if (research.getAddenda() != null && complete) {
            addenda = research.getAddenda();
        }
        generateRecipesLists(stage, addenda);
        String rawText = stage.getTextLocalized();
        if (addenda != null) {
            int ac = 0;
            for (ResearchAddendum addendum : addenda) {
                if (ThaumcraftCapabilities.knowsResearchStrict(mc.player, addendum.getResearch())) {
                    ++ac;
                    ITextComponent text = new TextComponentTranslation("tc.addendumtext", ac);
                    rawText = rawText + "<PAGE>" + text.getFormattedText() + "<BR>" + addendum.getTextLocalized();
                }
            }
        }
        rawText = rawText.replaceAll("<BR>", "~B\n\n");
        rawText = rawText.replaceAll("<BR/>", "~B\n\n");
        rawText = rawText.replaceAll("<LINE>", "~L");
        rawText = rawText.replaceAll("<LINE/>", "~L");
        rawText = rawText.replaceAll("<DIV>", "~D");
        rawText = rawText.replaceAll("<DIV/>", "~D");
        rawText = rawText.replaceAll("<PAGE>", "~P");
        rawText = rawText.replaceAll("<PAGE/>", "~P");
        ArrayList<PageImage> images = new ArrayList<PageImage>();
        String[] split;
        String[] imgSplit = split = rawText.split("<IMG>");
        for (String s : split) {
            int i = s.indexOf("</IMG>");
            if (i >= 0) {
                String clean = s.substring(0, i);
                PageImage pi = PageImage.parse(clean);
                if (pi == null) {
                    rawText = rawText.replaceFirst(clean, "\n");
                }
                else {
                    images.add(pi);
                    rawText = rawText.replaceFirst(clean, "~I");
                }
            }
        }
        rawText = rawText.replaceAll("<IMG>", "");
        rawText = rawText.replaceAll("</IMG>", "");
        List<String> firstPassText = new ArrayList<String>();
        String[] temp = rawText.split("~P");
        for (int a = 0; a < temp.length; ++a) {
            String t = temp[a];
            String[] temp2 = t.split("~D");
            for (int x = 0; x < temp2.length; ++x) {
                String t2 = temp2[x];
                String[] temp3 = t2.split("~L");
                for (int b = 0; b < temp3.length; ++b) {
                    String t3 = temp3[b];
                    String[] temp4 = t3.split("~I");
                    for (int c = 0; c < temp4.length; ++c) {
                        String t4 = temp4[c];
                        firstPassText.add(t4);
                        if (c != temp4.length - 1) {
                            firstPassText.add("~I");
                        }
                    }
                    if (b != temp3.length - 1) {
                        firstPassText.add("~L");
                    }
                }
                if (x != temp2.length - 1) {
                    firstPassText.add("~D");
                }
            }
            if (a != temp.length - 1) {
                firstPassText.add("~P");
            }
        }
        List<String> parsedText = new ArrayList<String>();
        for (String s2 : firstPassText) {
            List<String> pt1 = mc.fontRenderer.listFormattedStringToWidth(s2, 140);
            for (String ln : pt1) {
                parsedText.add(ln);
            }
        }
        int lineHeight = mc.fontRenderer.FONT_HEIGHT;
        int heightRemaining = 182;
        int dividerSpace = 0;
        if (research.getKey().equals("KNOWLEDGETYPES")) {
            heightRemaining -= 2;
            int tc = 0;
            int amt = 0;
            for (IPlayerKnowledge.EnumKnowledgeType type : IPlayerKnowledge.EnumKnowledgeType.values()) {
                for (ResearchCategory category : ResearchCategories.researchCategories.values()) {
                    if (!type.hasFields() && category != null) {
                        continue;
                    }
                    amt = playerKnowledge.getKnowledgeRaw(type, category);
                    if (amt > 0) {
                        ++tc;
                        break;
                    }
                }
            }
            heightRemaining -= 20 * tc;
            dividerSpace = 12;
        }
        if (!isComplete) {
            if (stage.getCraft() != null) {
                heightRemaining -= 18;
                dividerSpace = 15;
            }
            if (stage.getObtain() != null) {
                heightRemaining -= 18;
                dividerSpace = 15;
            }
            if (stage.getKnow() != null) {
                heightRemaining -= 18;
                dividerSpace = 15;
            }
            if (stage.getResearch() != null) {
                heightRemaining -= 18;
                dividerSpace = 15;
            }
        }
        heightRemaining -= dividerSpace;
        Page page1 = new Page();
        ArrayList<PageImage> tempImages = new ArrayList<PageImage>();
        for (String line : parsedText) {
            if (line.contains("~I")) {
                if (!images.isEmpty()) {
                    tempImages.add(images.remove(0));
                }
                line = "";
            }
            if (line.contains("~L")) {
                tempImages.add(GuiResearchPage.PILINE);
                line = "";
            }
            if (line.contains("~D")) {
                tempImages.add(GuiResearchPage.PIDIV);
                line = "";
            }
            if (line.contains("~P")) {
                heightRemaining = 210;
                pages.add(page1.copy());
                page1 = new Page();
                line = "";
            }
            if (!line.isEmpty()) {
                line = line.trim();
                page1.contents.add(line);
                heightRemaining -= lineHeight;
                if (line.endsWith("~B")) {
                    heightRemaining -= (int)(lineHeight * 0.66);
                }
            }
            while (!tempImages.isEmpty() && heightRemaining >= tempImages.get(0).ah + 2) {
                heightRemaining -= tempImages.get(0).ah + 2;
                page1.contents.add(tempImages.remove(0));
            }
            if (heightRemaining < lineHeight && !page1.contents.isEmpty()) {
                heightRemaining = 210;
                pages.add(page1.copy());
                page1 = new Page();
            }
        }
        if (!page1.contents.isEmpty()) {
            pages.add(page1.copy());
        }
        page1 = new Page();
        heightRemaining = 210;
        while (!tempImages.isEmpty()) {
            if (heightRemaining < tempImages.get(0).ah + 2) {
                heightRemaining = 210;
                pages.add(page1.copy());
                page1 = new Page();
            }
            else {
                heightRemaining -= tempImages.get(0).ah + 2;
                page1.contents.add(tempImages.remove(0));
            }
        }
        if (!page1.contents.isEmpty()) {
            pages.add(page1.copy());
        }
        rhash = research.getKey().hashCode() + currentStage * 50;
        maxPages = pages.size();
    }
    
    private void checkRequisites() {
        if (research.getStages() != null) {
            isComplete = playerKnowledge.isResearchComplete(research.getKey());
            while (currentStage >= research.getStages().length) {
                --currentStage;
            }
            if (currentStage < 0) {
                return;
            }
            hasAllRequisites = true;
            hasItem = null;
            hasCraft = null;
            hasResearch = null;
            hasKnow = null;
            ResearchStage stage = research.getStages()[currentStage];
            Object[] o = stage.getObtain();
            if (o != null) {
                hasItem = new boolean[o.length];
                for (int a = 0; a < o.length; ++a) {
                    ItemStack ts = ItemStack.EMPTY;
                    boolean ore = false;
                    if (o[a] instanceof ItemStack) {
                        ts = (ItemStack)o[a];
                    }
                    else {
                        NonNullList<ItemStack> nnl = OreDictionary.getOres((String)o[a]);
                        ts = nnl.get(0);
                        ore = true;
                    }
                    if (!(hasItem[a] = InventoryUtils.isPlayerCarryingAmount(mc.player, ts, ore))) {
                        hasAllRequisites = false;
                    }
                }
            }
            Object[] c = stage.getCraft();
            if (c != null) {
                hasCraft = new boolean[c.length];
                for (int a2 = 0; a2 < c.length; ++a2) {
                    if (!playerKnowledge.isResearchKnown("[#]" + stage.getCraftReference()[a2])) {
                        hasAllRequisites = false;
                        hasCraft[a2] = false;
                    }
                    else {
                        hasCraft[a2] = true;
                    }
                }
            }
            String[] r = stage.getResearch();
            if (r != null) {
                hasResearch = new boolean[r.length];
                for (int a3 = 0; a3 < r.length; ++a3) {
                    if (!ThaumcraftCapabilities.knowsResearchStrict(mc.player, r[a3])) {
                        hasAllRequisites = false;
                        hasResearch[a3] = false;
                    }
                    else {
                        hasResearch[a3] = true;
                    }
                }
            }
            ResearchStage.Knowledge[] k = stage.getKnow();
            if (k != null) {
                hasKnow = new boolean[k.length];
                for (int a4 = 0; a4 < k.length; ++a4) {
                    int pk = playerKnowledge.getKnowledge(k[a4].type, k[a4].category);
                    if (pk < k[a4].amount) {
                        hasAllRequisites = false;
                        hasKnow[a4] = false;
                    }
                    else {
                        hasKnow[a4] = true;
                    }
                }
            }
        }
    }
    
    private int findRecipePage(ResourceLocation rk, ItemStack stack, int start) {
        Object recipe = CommonInternals.getCatalogRecipe(rk);
        if (recipe == null) {
            recipe = CommonInternals.getCatalogRecipeFake(rk);
        }
        if (recipe == null) {
            recipe = CraftingManager.getRecipe(rk);
        }
        if (recipe == null) {
            recipe = ConfigRecipes.recipeGroups.get(rk.toString());
        }
        if (recipe == null) {
            return -1;
        }
        if (recipe instanceof ArrayList) {
            int g = 0;
            for (ResourceLocation rl : (ArrayList<ResourceLocation>)recipe) {
                int q = findRecipePage(rl, stack, g);
                if (q >= 0) {
                    return q;
                }
                ++g;
            }
        }
        if (recipe instanceof CrucibleRecipe && ((CrucibleRecipe)recipe).getRecipeOutput().isItemEqual(stack)) {
            if (!ThaumcraftCapabilities.knowsResearchStrict(mc.player, ((CrucibleRecipe)recipe).getResearch())) {
                return -99;
            }
            return start;
        }
        else if (recipe instanceof InfusionRecipe && ((InfusionRecipe)recipe).getRecipeOutput() instanceof ItemStack && ((ItemStack)((InfusionRecipe)recipe).getRecipeOutput()).isItemEqual(stack)) {
            if (!ThaumcraftCapabilities.knowsResearchStrict(mc.player, ((InfusionRecipe)recipe).getResearch())) {
                return -99;
            }
            return start;
        }
        else if (recipe instanceof IRecipe && ((IRecipe)recipe).getRecipeOutput().isItemEqual(stack)) {
            if (recipe instanceof IArcaneRecipe && !ThaumcraftCapabilities.knowsResearchStrict(mc.player, ((IArcaneRecipe)recipe).getResearch())) {
                return -99;
            }
            return start;
        }
        else {
            if (recipe instanceof RecipeMisc && ((RecipeMisc)recipe).getOutput().isItemEqual(stack)) {
                return start;
            }
            return -1;
        }
    }
    
    private String getCraftingRecipeKey(EntityPlayer player, ItemStack stack) {
        int key = stack.serializeNBT().toString().hashCode();
        if (keyCache.containsKey(key)) {
            return keyCache.get(key);
        }
        for (ResearchCategory rcl : ResearchCategories.researchCategories.values()) {
            for (ResearchEntry ri : rcl.research.values()) {
                if (ri.getStages() == null) {
                    continue;
                }
                for (int a = 0; a < ri.getStages().length; ++a) {
                    ResearchStage stage = ri.getStages()[a];
                    if (stage.getRecipes() != null) {
                        for (ResourceLocation rec : stage.getRecipes()) {
                            int result = findRecipePage(rec, stack, 0);
                            if (result != -1) {
                                String s = rec.toString();
                                if (result == -99) {
                                    s = new ResourceLocation("UNKNOWN").toString();
                                }
                                else {
                                    s = s + ";" + result;
                                }
                                keyCache.put(key, s);
                                return s;
                            }
                        }
                    }
                }
            }
        }
        keyCache.put(key, null);
        return null;
    }
    
    static {
        GuiResearchPage.history = new LinkedList<ResourceLocation>();
        GuiResearchPage.aspectsPage = 0;
        GuiResearchPage.cycleMultiblockLines = false;
        PILINE = PageImage.parse("thaumcraft:textures/gui/gui_researchbook.png:24:184:95:6:1");
        PIDIV = PageImage.parse("thaumcraft:textures/gui/gui_researchbook.png:28:192:140:6:1");
    }
    
    private class Page
    {
        ArrayList contents;
        
        private Page() {
            contents = new ArrayList();
        }
        
        public Page copy() {
            Page p = new Page();
            p.contents.addAll(contents);
            return p;
        }
    }
    
    private static class PageImage
    {
        int x;
        int y;
        int w;
        int h;
        int aw;
        int ah;
        float scale;
        ResourceLocation loc;
        
        public static PageImage parse(String text) {
            String[] s = text.split(":");
            if (s.length != 7) {
                return null;
            }
            try {
                PageImage pi = new PageImage();
                pi.loc = new ResourceLocation(s[0], s[1]);
                pi.x = Integer.parseInt(s[2]);
                pi.y = Integer.parseInt(s[3]);
                pi.w = Integer.parseInt(s[4]);
                pi.h = Integer.parseInt(s[5]);
                pi.scale = Float.parseFloat(s[6]);
                pi.aw = (int)(pi.w * pi.scale);
                pi.ah = (int)(pi.h * pi.scale);
                if (pi.ah > 208 || pi.aw > 140) {
                    return null;
                }
                return pi;
            }
            catch (Exception ex) {
                return null;
            }
        }
    }
    
    public static class BlueprintBlockAccess implements IBlockAccess
    {
        private Part[][][] data;
        private IBlockState[][][] structure;
        public int sliceLine;
        
        public BlueprintBlockAccess(Part[][][] data, boolean target) {
            sliceLine = 0;
            this.data = new Part[data.length][data[0].length][data[0][0].length];
            for (int y = 0; y < data.length; ++y) {
                for (int x = 0; x < data[0].length; ++x) {
                    for (int z = 0; z < data[0][0].length; ++z) {
                        this.data[y][x][z] = data[y][x][z];
                    }
                }
            }
            structure = new IBlockState[data.length][data[0].length][data[0][0].length];
            if (target) {
                for (int y = 0; y < this.data.length; ++y) {
                    Matrix matrix = new Matrix(this.data[y]);
                    matrix.Rotate90DegRight(3);
                    this.data[y] = matrix.getMatrix();
                }
            }
            for (int y = 0; y < data.length; ++y) {
                for (int x = 0; x < data[0].length; ++x) {
                    for (int z = 0; z < data[0][0].length; ++z) {
                        structure[data.length - y - 1][x][z] = (target ? convertTarget(x, y, z) : convert(x, y, z));
                    }
                }
            }
        }
        
        private IBlockState convert(int x, int y, int z) {
            if (data[y][x][z] == null || data[y][x][z].getSource() == null) {
                return Blocks.AIR.getDefaultState();
            }
            if (data[y][x][z].getSource() instanceof ItemStack && Block.getBlockFromItem(((ItemStack) data[y][x][z].getSource()).getItem()) != null) {
                return Block.getBlockFromItem(((ItemStack) data[y][x][z].getSource()).getItem()).getStateFromMeta(((ItemStack) data[y][x][z].getSource()).getItemDamage());
            }
            if (data[y][x][z].getSource() instanceof Block) {
                return ((Block) data[y][x][z].getSource()).getDefaultState();
            }
            if (data[y][x][z].getSource() instanceof IBlockState) {
                return (IBlockState) data[y][x][z].getSource();
            }
            if (data[y][x][z].getSource() instanceof Material) {
                if (data[y][x][z].getSource() == Material.LAVA) {
                    return Blocks.LAVA.getDefaultState();
                }
                if (data[y][x][z].getSource() == Material.WATER) {
                    return Blocks.WATER.getDefaultState();
                }
            }
            return Blocks.AIR.getDefaultState();
        }
        
        private IBlockState convertTarget(int x, int y, int z) {
            if (data[y][x][z] == null) {
                return Blocks.AIR.getDefaultState();
            }
            if (data[y][x][z].getTarget() == null) {
                return convert(x, y, z);
            }
            if (data[y][x][z].getTarget() instanceof ItemStack && Block.getBlockFromItem(((ItemStack) data[y][x][z].getTarget()).getItem()) != null) {
                return Block.getBlockFromItem(((ItemStack) data[y][x][z].getTarget()).getItem()).getStateFromMeta(((ItemStack) data[y][x][z].getTarget()).getItemDamage());
            }
            if (data[y][x][z].getTarget() instanceof Block) {
                return ((Block) data[y][x][z].getTarget()).getDefaultState();
            }
            if (data[y][x][z].getTarget() instanceof IBlockState) {
                return (IBlockState) data[y][x][z].getTarget();
            }
            if (data[y][x][z].getTarget() instanceof Material) {
                if (data[y][x][z].getTarget() == Material.LAVA) {
                    return Blocks.LAVA.getDefaultState();
                }
                if (data[y][x][z].getTarget() == Material.WATER) {
                    return Blocks.WATER.getDefaultState();
                }
            }
            return Blocks.AIR.getDefaultState();
        }
        
        @Nullable
        public TileEntity getTileEntity(BlockPos pos) {
            return null;
        }
        
        public int getCombinedLight(BlockPos pos, int lightValue) {
            return 15728880;
        }
        
        public IBlockState getBlockState(BlockPos pos) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            if (sliceLine > structure.length) {
                sliceLine = 0;
            }
            if (y >= 0 && y < structure.length - sliceLine && x >= 0 && x < structure[y].length && z >= 0 && z < structure[y][x].length) {
                return structure[y][x][z];
            }
            return Blocks.AIR.getDefaultState();
        }
        
        public boolean isAirBlock(BlockPos pos) {
            return getBlockState(pos).getBlock() == Blocks.AIR;
        }
        
        public int getStrongPower(BlockPos pos, EnumFacing direction) {
            return 0;
        }
        
        public WorldType getWorldType() {
            return null;
        }
        
        public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
            return false;
        }
        
        public Biome getBiome(BlockPos pos) {
            return null;
        }
    }
}
