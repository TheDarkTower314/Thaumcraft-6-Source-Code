package thaumcraft.client.gui;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftManager;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.client.gui.plugins.GuiImageButton;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.container.ContainerResearchTable;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketStartTheoryToServer;
import thaumcraft.common.tiles.crafting.TileResearchTable;


@SideOnly(Side.CLIENT)
public class GuiResearchTable extends GuiContainer
{
    private float xSize_lo;
    private float ySize_lo;
    private TileResearchTable table;
    private FontRenderer galFontRenderer;
    private String username;
    EntityPlayer player;
    ResourceLocation txBackground;
    ResourceLocation txBase;
    ResourceLocation txPaper;
    ResourceLocation txPaperGilded;
    ResourceLocation txQuestion;
    ResearchTableData.CardChoice lastDraw;
    float[] cardHover;
    float[] cardZoomOut;
    float[] cardZoomIn;
    boolean[] cardActive;
    boolean cardSelected;
    public HashMap<String, Integer> tempCatTotals;
    long nexCatCheck;
    long nextCheck;
    int dummyInspirationStart;
    Set<String> currentAids;
    Set<String> selectedAids;
    GuiImageButton buttonCreate;
    GuiImageButton buttonComplete;
    GuiImageButton buttonScrap;
    public ArrayList<ResearchTableData.CardChoice> cardChoices;
    
    public GuiResearchTable(EntityPlayer player, TileResearchTable e) {
        super(new ContainerResearchTable(player.inventory, e));
        txBackground = new ResourceLocation("thaumcraft", "textures/gui/gui_research_table.png");
        txBase = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
        txPaper = new ResourceLocation("thaumcraft", "textures/gui/paper.png");
        txPaperGilded = new ResourceLocation("thaumcraft", "textures/gui/papergilded.png");
        txQuestion = new ResourceLocation("thaumcraft", "textures/aspects/_unknown.png");
        cardHover = new float[] { 0.0f, 0.0f, 0.0f };
        cardZoomOut = new float[] { 0.0f, 0.0f, 0.0f };
        cardZoomIn = new float[] { 0.0f, 0.0f, 0.0f };
        cardActive = new boolean[] { true, true, true };
        cardSelected = false;
        tempCatTotals = new HashMap<String, Integer>();
        nexCatCheck = 0L;
        nextCheck = 0L;
        dummyInspirationStart = 0;
        currentAids = new HashSet<String>();
        selectedAids = new HashSet<String>();
        buttonCreate = new GuiImageButton(this, 1, guiLeft + 128, guiTop + 22, 49, 11, "button.create.theory", null, txBase, 37, 66, 51, 13, 8978346);
        buttonComplete = new GuiImageButton(this, 7, guiLeft + 191, guiTop + 96, 49, 11, "button.complete.theory", null, txBase, 37, 66, 51, 13, 8978346);
        buttonScrap = new GuiImageButton(this, 9, guiLeft + 128, guiTop + 168, 49, 11, "button.scrap.theory", null, txBase, 37, 66, 51, 13, 16720418);
        cardChoices = new ArrayList<ResearchTableData.CardChoice>();
        table = e;
        xSize = 255;
        ySize = 255;
        galFontRenderer = FMLClientHandler.instance().getClient().standardGalacticFontRenderer;
        username = player.getName();
        this.player = player;
        if (table.data != null) {
            for (String cat : table.data.categoryTotals.keySet()) {
                tempCatTotals.put(cat, table.data.categoryTotals.get(cat));
            }
            syncFromTableChoices();
            lastDraw = table.data.lastDraw;
        }
    }
    
    private void syncFromTableChoices() {
        cardChoices.clear();
        for (ResearchTableData.CardChoice cc : table.data.cardChoices) {
            cardChoices.add(cc);
        }
    }
    
    protected void drawGuiContainerForegroundLayer(int mx, int my) {
    }
    
    public void drawScreen(int mx, int my, float par3) {
        drawDefaultBackground();
        super.drawScreen(mx, my, par3);
        xSize_lo = (float)mx;
        ySize_lo = (float)my;
        int xx = guiLeft;
        int yy = guiTop;
        RenderHelper.disableStandardItemLighting();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (table.data == null) {
            if (!currentAids.isEmpty()) {
                int side = Math.min(currentAids.size(), 6);
                int c = 0;
                int r = 0;
                GL11.glPushMatrix();
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.2f);
                mc.renderEngine.bindTexture(txBase);
                for (String key : currentAids) {
                    ITheorycraftAid mutator = TheorycraftManager.aids.get(key);
                    if (mutator == null) {
                        continue;
                    }
                    int x = xx + 128 + 20 * c - side * 10;
                    int y = yy + 85 + 35 * r;
                    if (isPointInRegion(x - xx, y - yy, 16, 16, mx, my) && !selectedAids.contains(key)) {
                        drawTexturedModalRect(x, y, 0, 96, 16, 16);
                    }
                    if (++c < side) {
                        continue;
                    }
                    ++r;
                    c = 0;
                }
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glPopMatrix();
            }
        }
        else {
            int sx = 128;
            int cw = 110;
            int sz = cardChoices.size();
            int a = 0;
            if (!cardSelected) {
                for (ResearchTableData.CardChoice cardChoice : cardChoices) {
                    if (cardZoomOut[a] >= 1.0f) {
                        float dx = (float)(55 + sx - 55 * sz + cw * a - 65);
                        float fx = 65.0f + dx * cardZoomOut[a];
                        float qx = 191.0f - fx;
                        if (cardActive[a]) {
                            fx += qx * cardZoomIn[a];
                        }
                        drawSheetOverlay(fx, 100.0, cardChoice, mx, my);
                        ++a;
                    }
                }
            }
            int qq = 0;
            if (table.getStackInSlot(0) == null || table.getStackInSlot(0).isEmpty() || table.getStackInSlot(0).getItemDamage() == table.getStackInSlot(0).getMaxDamage()) {
                sx = Math.max(fontRenderer.getStringWidth(I18n.translateToLocal("tile.researchtable.noink.0")), fontRenderer.getStringWidth(I18n.translateToLocal("tile.researchtable.noink.1"))) / 2;
                UtilsFX.drawCustomTooltip(this, fontRenderer, Arrays.asList(I18n.translateToLocal("tile.researchtable.noink.0"), I18n.translateToLocal("tile.researchtable.noink.1")), xx - sx + 116, yy + 60 + qq, 11, true);
                qq += 40;
            }
            if (table.getStackInSlot(1) == null || table.getStackInSlot(1).isEmpty()) {
                sx = fontRenderer.getStringWidth(I18n.translateToLocal("tile.researchtable.nopaper.0")) / 2;
                UtilsFX.drawCustomTooltip(this, fontRenderer, Arrays.asList(I18n.translateToLocal("tile.researchtable.nopaper.0")), xx - sx + 116, yy + 60 + qq, 11, true);
            }
        }
        renderHoveredToolTip(mx, my);
    }
    
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mx, int my) {
        checkButtons();
        int xx = guiLeft;
        int yy = guiTop;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        mc.renderEngine.bindTexture(txBackground);
        drawTexturedModalRect(xx, yy, 0, 0, 255, 255);
        fontRenderer.drawString(" ", 0, 0, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (table.data == null) {
            if (nextCheck < player.ticksExisted) {
                currentAids = table.checkSurroundingAids();
                dummyInspirationStart = ResearchTableData.getAvailableInspiration(player);
                nextCheck = player.ticksExisted + 100;
            }
            mc.renderEngine.bindTexture(txBase);
            GL11.glPushMatrix();
            GL11.glTranslated(xx + 128 - dummyInspirationStart * 5, yy + 55, 0.0);
            GL11.glScaled(0.5, 0.5, 0.0);
            for (int a = 0; a < dummyInspirationStart; ++a) {
                drawTexturedModalRect(20 * a, 0, (dummyInspirationStart - selectedAids.size() <= a) ? 48 : 32, 96, 16, 16);
            }
            GL11.glPopMatrix();
            if (!currentAids.isEmpty()) {
                int side = Math.min(currentAids.size(), 6);
                int c = 0;
                int r = 0;
                for (String key : currentAids) {
                    ITheorycraftAid mutator = TheorycraftManager.aids.get(key);
                    if (mutator == null) {
                        continue;
                    }
                    int x = xx + 128 + 20 * c - side * 10;
                    int y = yy + 85 + 35 * r;
                    if (selectedAids.contains(key)) {
                        mc.renderEngine.bindTexture(txBase);
                        drawTexturedModalRect(x, y, 0, 96, 16, 16);
                    }
                    if (mutator.getAidObject() instanceof ItemStack || mutator.getAidObject() instanceof Block) {
                        GL11.glPushMatrix();
                        RenderHelper.enableGUIStandardItemLighting();
                        GlStateManager.disableLighting();
                        GlStateManager.enableRescaleNormal();
                        GlStateManager.enableColorMaterial();
                        GlStateManager.enableLighting();
                        ItemStack s = (ItemStack)((mutator.getAidObject() instanceof ItemStack) ? mutator.getAidObject() : new ItemStack((Block)mutator.getAidObject()));
                        itemRender.renderItemAndEffectIntoGUI(s, x, y);
                        GlStateManager.disableLighting();
                        GlStateManager.depthMask(true);
                        GlStateManager.enableDepth();
                        GL11.glPopMatrix();
                    }
                    if (++c < side) {
                        continue;
                    }
                    ++r;
                    c = 0;
                }
            }
        }
        else {
            checkCards();
            mc.renderEngine.bindTexture(txBase);
            GL11.glPushMatrix();
            GL11.glTranslated(xx + 15, yy + 150, 0.0);
            if (table.data != null) {
                for (int a = 0; a < table.data.bonusDraws; ++a) {
                    drawTexturedModalRect(a * 2, a, 64, 96, 16, 16);
                }
            }
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated(xx + 128 - table.data.inspirationStart * 5, yy + 16, 0.0);
            GL11.glScaled(0.5, 0.5, 0.0);
            for (int a = 0; a < table.data.inspirationStart; ++a) {
                drawTexturedModalRect(20 * a, 0, (table.data.inspiration <= a) ? 48 : 32, 96, 16, 16);
            }
            GL11.glPopMatrix();
            int sheets = 0;
            if (table.getStackInSlot(1) != null) {
                sheets = 1 + table.getStackInSlot(1).getCount() / 4;
            }
            Random r2 = new Random(55L);
            if (sheets > 0 && !table.data.isComplete()) {
                for (int a2 = 0; a2 < sheets; ++a2) {
                    drawSheet(xx + 65, yy + 100, 6.0, r2, 1.0f, 1.0f, null);
                }
                boolean highlight = false;
                int var7 = mx - (25 + xx);
                int var8 = my - (55 + yy);
                if (cardChoices.isEmpty() && var7 >= 0 && var8 >= 0 && var7 < 75 && var8 < 90) {
                    highlight = true;
                }
                GL11.glPushMatrix();
                GL11.glColor4f(1.0f, 1.0f, 1.0f, highlight ? 1.0f : 0.5f);
                GlStateManager.enableBlend();
                mc.renderEngine.bindTexture(txQuestion);
                GL11.glTranslated(xx + 65, yy + 100, 0.0);
                GL11.glScaled(highlight ? 1.75 : 1.5, highlight ? 1.75 : 1.5, 0.0);
                UtilsFX.drawTexturedQuadFull(-8.0f, -8.0f, 0.0);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glPopMatrix();
            }
            for (Long seed : table.data.savedCards) {
                r2 = new Random(seed);
                drawSheet(xx + 191, yy + 100, 6.0, r2, 1.0f, 1.0f, null);
            }
            if (lastDraw != null) {
                r2 = new Random(lastDraw.card.getSeed());
                drawSheet(xx + 191, yy + 100, 6.0, r2, 1.0f, 1.0f, lastDraw);
            }
            ArrayList<String> sparkle = new ArrayList<String>();
            if (nexCatCheck < player.ticksExisted) {
                for (String cat : ResearchCategories.researchCategories.keySet()) {
                    int t0 = 0;
                    if (table.data.categoryTotals.containsKey(cat)) {
                        t0 = table.data.categoryTotals.get(cat);
                    }
                    int t2 = 0;
                    if (tempCatTotals.containsKey(cat)) {
                        t2 = tempCatTotals.get(cat);
                    }
                    if (t0 == 0 && t2 == 0) {
                        tempCatTotals.remove(cat);
                    }
                    else {
                        if (t2 > t0) {
                            --t2;
                        }
                        if (t2 < t0) {
                            ++t2;
                            sparkle.add(cat);
                        }
                        tempCatTotals.put(cat, t2);
                    }
                }
                nexCatCheck = player.ticksExisted + 1;
            }
            HashMap<String, Integer> unsortedMap = new HashMap<String, Integer>();
            String hf = null;
            int lf = 0;
            for (String cat2 : tempCatTotals.keySet()) {
                int cf = tempCatTotals.get(cat2);
                if (cf == 0) {
                    continue;
                }
                if (cf > lf) {
                    lf = cf;
                    hf = cat2;
                }
                unsortedMap.put(cat2, cf);
            }
            if (hf != null) {
                unsortedMap.put(hf, unsortedMap.get(hf));
            }
            Comparator<Map.Entry<String, Integer>> valueComparator = (e1, e2) -> e2.getValue().compareTo(e1.getValue());
            Map<String, Integer> sortedMap = unsortedMap.entrySet().stream().sorted(valueComparator).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            int i = 0;
            for (String field : sortedMap.keySet()) {
                GL11.glPushMatrix();
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glTranslatef((float)(xx + 253), (float)(yy + 16 + i * 18 + ((i > 0) ? 4 : 0)), 0.0f);
                GL11.glScaled(0.0625, 0.0625, 0.0625);
                mc.renderEngine.bindTexture(ResearchCategories.getResearchCategory(field).icon);
                drawTexturedModalRect(0, 0, 0, 0, 255, 255);
                GL11.glPopMatrix();
                GL11.glTranslatef(0.0f, 0.0f, 5.0f);
                String s2 = sortedMap.get(field) + "%";
                if (i > table.data.penaltyStart) {
                    int q = sortedMap.get(field) / 3;
                    s2 = s2 + " (-" + q + ")";
                }
                mc.fontRenderer.drawStringWithShadow(s2, (float)(xx + 276), (float)(yy + 20 + i * 18 + ((i > table.data.penaltyStart) ? 4 : 0)), table.data.categoriesBlocked.contains(field) ? 6316128 : ((i <= table.data.penaltyStart) ? 57536 : 16777215));
                if (sparkle.contains(field)) {
                    for (int q = 0; q < 2; ++q) {
                        float rr = MathHelper.getInt(player.getRNG(), 255, 255) / 255.0f;
                        float gg = MathHelper.getInt(player.getRNG(), 189, 255) / 255.0f;
                        float bb = MathHelper.getInt(player.getRNG(), 64, 255) / 255.0f;
                        FXDispatcher.INSTANCE.drawSimpleSparkleGui(player.getRNG(), xx + 276 + player.getRNG().nextFloat() * fontRenderer.getStringWidth(s2), yy + 20 + player.getRNG().nextFloat() * 8.0f + i * 18 + ((i > table.data.penaltyStart) ? 4 : 0), player.world.rand.nextGaussian() * 0.5, player.world.rand.nextGaussian() * 0.5, 24.0f, rr, gg, bb, 0, 0.9f, -1.0f);
                    }
                }
                int var9 = mx - (xx + 256);
                int var10 = my - (yy + 16 + i * 18 + ((i > table.data.penaltyStart) ? 4 : 0));
                if (var9 >= 0 && var10 >= 0 && var9 < 16 && var10 < 16) {
                    GL11.glPushMatrix();
                    UtilsFX.drawCustomTooltip(this, fontRenderer, Arrays.asList(ResearchCategories.getCategoryName(field)), mx + 8, my + 8, 11);
                    GL11.glPopMatrix();
                    RenderHelper.disableStandardItemLighting();
                }
                ++i;
            }
            int sx = 128;
            int cw = 110;
            int sz = cardChoices.size();
            int a3 = 0;
            for (ResearchTableData.CardChoice cardChoice : cardChoices) {
                r2 = new Random(cardChoice.card.getSeed());
                int var11 = mx - (5 + sx - 55 * sz + xx + cw * a3);
                int var12 = my - (100 + yy - 60);
                if (cardZoomOut[a3] >= 0.95 && !cardSelected) {
                    if (var11 >= 0 && var12 >= 0 && var11 < 100 && var12 < 120) {
                        float[] cardHover = this.cardHover;
                        int n = a3;
                        cardHover[n] += Math.max((0.25f - this.cardHover[a3]) / 3.0f * partialTicks, 0.0025f);
                    }
                    else {
                        float[] cardHover2 = cardHover;
                        int n2 = a3;
                        cardHover2[n2] -= 0.1f * partialTicks;
                    }
                }
                if (a3 == sz - 1 || cardZoomOut[a3 + 1] > 0.6) {
                    float f = cardZoomOut[a3];
                    float[] cardZoomOut = this.cardZoomOut;
                    int n3 = a3;
                    cardZoomOut[n3] += Math.max((1.0f - this.cardZoomOut[a3]) / 5.0f * partialTicks, 0.0025f);
                    if (this.cardZoomOut[a3] > 0.0f && f == 0.0f) {
                        playButtonPageFlip();
                    }
                }
                float prevZoomIn = cardZoomIn[a3];
                if (cardSelected) {
                    float[] cardZoomIn = this.cardZoomIn;
                    int n4 = a3;
                    cardZoomIn[n4] += (float)(cardActive[a3] ? Math.max((1.0f - this.cardZoomIn[a3]) / 3.0f * partialTicks, 0.0025) : (0.3f * partialTicks));
                    cardHover[a3] = 1.0f - this.cardZoomIn[a3];
                }
                cardZoomIn[a3] = MathHelper.clamp(cardZoomIn[a3], 0.0f, 1.0f);
                cardHover[a3] = MathHelper.clamp(cardHover[a3], 0.0f, 0.25f);
                cardZoomOut[a3] = MathHelper.clamp(cardZoomOut[a3], 0.0f, 1.0f);
                float dx = (float)(55 + sx - 55 * sz + xx + cw * a3 - (xx + 65));
                float fx = xx + 65 + dx * cardZoomOut[a3];
                float qx = xx + 191 - fx;
                if (cardActive[a3]) {
                    fx += qx * cardZoomIn[a3];
                }
                drawSheet(fx, yy + 100, 6.0f + cardZoomOut[a3] * 2.0f - cardZoomIn[a3] * 2.0f + cardHover[a3], r2, cardActive[a3] ? 1.0f : (1.0f - cardZoomIn[a3]), Math.max(1.0f - cardZoomOut[a3], cardZoomIn[a3]), cardChoice);
                if (cardSelected && cardActive[a3] && cardZoomIn[a3] >= 1.0f && prevZoomIn < 1.0f) {
                    playButtonWrite();
                    cardChoices.clear();
                    cardSelected = false;
                    lastDraw = table.data.lastDraw;
                    break;
                }
                ++a3;
            }
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderHelper.disableStandardItemLighting();
    }
    
    private void drawSheet(double x, double y, double scale, Random r, float alpha, float tilt, ResearchTableData.CardChoice cardChoice) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
        GL11.glTranslated(x + r.nextGaussian(), y + r.nextGaussian(), 0.0);
        GL11.glScaled(scale, scale, 0.0);
        GL11.glRotated(r.nextGaussian() * tilt, 0.0, 0.0, 1.0);
        GL11.glPushMatrix();
        if (cardChoice != null && cardChoice.fromAid) {
            mc.renderEngine.bindTexture(txPaperGilded);
        }
        else {
            mc.renderEngine.bindTexture(txPaper);
        }
        if (r.nextBoolean()) {
            GL11.glRotated(180.0, 0.0, 0.0, 1.0);
        }
        if (r.nextBoolean()) {
            GL11.glRotated(180.0, 0.0, 1.0, 0.0);
        }
        GL11.glDisable(2884);
        UtilsFX.drawTexturedQuadFull(-8.0f, -8.0f, 0.0);
        GL11.glEnable(2884);
        GL11.glPopMatrix();
        if (cardChoice != null && alpha == 1.0f) {
            if (cardChoice.card.getResearchCategory() != null) {
                ResearchCategory rc = ResearchCategories.getResearchCategory(cardChoice.card.getResearchCategory());
                if (rc != null) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha / 6.0f);
                    GL11.glPushMatrix();
                    GL11.glScaled(0.5, 0.5, 0.0);
                    mc.renderEngine.bindTexture(rc.icon);
                    UtilsFX.drawTexturedQuadFull(-8.0f, -8.0f, 0.0);
                    GL11.glPopMatrix();
                }
            }
            GL11.glPushMatrix();
            GL11.glScaled(0.0625, 0.0625, 0.0);
            GL11.glColor4f(0.0f, 0.0f, 0.0f, alpha);
            String name = TextFormatting.BOLD + cardChoice.card.getLocalizedName() + TextFormatting.RESET;
            int sz = fontRenderer.getStringWidth(name);
            fontRenderer.drawString(name, -sz / 2, -65, 0);
            fontRenderer.drawSplitString(cardChoice.card.getLocalizedText(), -70, -48, 140, 0);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            mc.renderEngine.bindTexture(txBase);
            GL11.glScaled(0.0625, 0.0625, 0.0);
            int cc = cardChoice.card.getInspirationCost();
            boolean add = false;
            if (cc < 0) {
                add = true;
                cc = Math.abs(cc) + 1;
            }
            GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
            for (int a = 0; a < cc; ++a) {
                if (a == 0 && add) {
                    drawTexturedModalRect(-10 * cc + 20 * a, -95, 48, 0, 16, 16);
                }
                else {
                    drawTexturedModalRect(-10 * cc + 20 * a, -95, 32, 96, 16, 16);
                }
            }
            GL11.glPopMatrix();
            if (cardChoice.card.getRequiredItems() != null) {
                ItemStack[] items = cardChoice.card.getRequiredItems();
                GL11.glPushMatrix();
                for (int a2 = 0; a2 < items.length; ++a2) {
                    if (items[a2] == null || items[a2].isEmpty()) {
                        GL11.glPushMatrix();
                        mc.renderEngine.bindTexture(txQuestion);
                        GL11.glScaled(0.125, 0.125, 0.0);
                        GL11.glColor4f(0.75f, 0.75f, 0.75f, alpha);
                        GL11.glTranslated(-9 * items.length + 18 * a2, 35.0, 0.0);
                        UtilsFX.drawTexturedQuadFull(0.0f, 0.0f, 0.0);
                        GL11.glPopMatrix();
                    }
                    else {
                        GL11.glPushMatrix();
                        GL11.glScaled(0.125, 0.125, 0.0);
                        RenderHelper.enableGUIStandardItemLighting();
                        GlStateManager.disableLighting();
                        GlStateManager.enableRescaleNormal();
                        GlStateManager.enableColorMaterial();
                        GlStateManager.enableLighting();
                        itemRender.renderItemAndEffectIntoGUI(items[a2], -9 * items.length + 18 * a2, 35);
                        GlStateManager.disableLighting();
                        GlStateManager.depthMask(true);
                        GlStateManager.enableDepth();
                        GL11.glPopMatrix();
                        try {
                            if (cardChoice.card.getRequiredItemsConsumed()[a2]) {
                                GL11.glPushMatrix();
                                mc.renderEngine.bindTexture(txBase);
                                GL11.glScaled(0.125, 0.125, 0.0);
                                float s = (float)Math.sin((player.ticksExisted + a2 * 2 + mc.getRenderPartialTicks()) / 2.0f) * 0.03f;
                                GL11.glTranslated(-2 - 9 * items.length + 18 * a2, 45.0f + s * 10.0f, 0.0);
                                GL11.glScaled(0.5, 0.5 + s, 0.0);
                                drawTexturedModalRect(0, 0, 64, 120, 16, 16);
                                GL11.glPopMatrix();
                            }
                        }
                        catch (Exception ex) {}
                    }
                }
                GL11.glPopMatrix();
            }
        }
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }
    
    private void drawSheetOverlay(double x, double y, ResearchTableData.CardChoice cardChoice, int mx, int my) {
        GL11.glPushMatrix();
        if (cardChoice != null && cardChoice.card.getRequiredItems() != null) {
            ItemStack[] items = cardChoice.card.getRequiredItems();
            for (int a = 0; a < items.length; ++a) {
                if (isPointInRegion((int)(x - 9 * items.length + 18 * a), (int)(y + 36.0), 15, 15, mx, my)) {
                    if (items[a] == null || items[a].isEmpty()) {
                        drawHoveringText(Arrays.asList(I18n.translateToLocal("tc.card.unknown")), mx, my);
                    }
                    else {
                        renderToolTip(items[a], mx, my);
                    }
                }
            }
        }
        GL11.glPopMatrix();
    }
    
    private void drawCards() {
        cardSelected = false;
        cardHover = new float[] { 0.0f, 0.0f, 0.0f };
        cardZoomOut = new float[] { 0.0f, 0.0f, 0.0f };
        cardZoomIn = new float[] { 0.0f, 0.0f, 0.0f };
        cardActive = new boolean[] { true, true, true };
        int draw = 2;
        if (table.data.bonusDraws > 0) {
            ++draw;
            ResearchTableData data = table.data;
            --data.bonusDraws;
        }
        mc.playerController.sendEnchantPacket(inventorySlots.windowId, draw);
        cardChoices.clear();
    }
    
    public void initGui() {
        super.initGui();
        buttonList.add(buttonCreate);
        buttonCreate.x = guiLeft + 128;
        buttonCreate.y = guiTop + 22;
        buttonList.add(buttonComplete);
        buttonComplete.x = guiLeft + 191;
        buttonComplete.y = guiTop + 96;
        buttonList.add(buttonScrap);
        buttonScrap.x = guiLeft + 128;
        buttonScrap.y = guiTop + 168;
    }
    
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            playButtonClick();
            PacketHandler.INSTANCE.sendToServer(new PacketStartTheoryToServer(table.getPos(), selectedAids));
        }
        else if (button.id == 7) {
            playButtonClick();
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, 7);
            tempCatTotals.clear();
            lastDraw = null;
        }
        else if (button.id == 9) {
            playButtonClick();
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, 9);
            tempCatTotals.clear();
            lastDraw = null;
            table.data = null;
            cardChoices.clear();
        }
        else {
            super.actionPerformed(button);
        }
    }
    
    private void checkButtons() {
        buttonComplete.active = false;
        buttonComplete.visible = false;
        buttonScrap.active = false;
        buttonScrap.visible = false;
        if (table.data != null) {
            buttonCreate.active = false;
            buttonCreate.visible = false;
            if (table.data.isComplete()) {
                buttonComplete.active = true;
                buttonComplete.visible = true;
            }
            else {
                buttonScrap.active = true;
                buttonScrap.visible = true;
            }
        }
        else {
            buttonCreate.visible = true;
            if (table.getStackInSlot(1) == null || table.getStackInSlot(0) == null || table.getStackInSlot(0).getItemDamage() == table.getStackInSlot(0).getMaxDamage()) {
                buttonCreate.active = false;
            }
            else {
                buttonCreate.active = true;
            }
        }
    }
    
    protected void mouseClicked(int mx, int my, int par3) throws IOException {
        super.mouseClicked(mx, my, par3);
        int xx = (width - xSize) / 2;
        int yy = (height - ySize) / 2;
        if (table.data == null) {
            if (!currentAids.isEmpty()) {
                int side = Math.min(currentAids.size(), 6);
                int c = 0;
                int r = 0;
                for (String key : currentAids) {
                    ITheorycraftAid mutator = TheorycraftManager.aids.get(key);
                    if (mutator == null) {
                        continue;
                    }
                    int x = 128 + 20 * c - side * 10;
                    int y = 85 + 35 * r;
                    if (isPointInRegion(x, y, 16, 16, mx, my)) {
                        if (selectedAids.contains(key)) {
                            selectedAids.remove(key);
                        }
                        else if (selectedAids.size() + 1 < dummyInspirationStart) {
                            selectedAids.add(key);
                        }
                    }
                    if (++c < side) {
                        continue;
                    }
                    ++r;
                    c = 0;
                }
            }
        }
        else {
            int sx = 128;
            int cw = 110;
            if (cardChoices.size() > 0) {
                int pressed = -1;
                for (int a = 0; a < cardChoices.size(); ++a) {
                    int var7 = mx - (5 + sx - 55 * cardChoices.size() + xx + cw * a);
                    int var8 = my - (100 + yy - 60);
                    if (cardZoomOut[a] >= 0.95 && !cardSelected && var7 >= 0 && var8 >= 0 && var7 < 100 && var8 < 120) {
                        pressed = a;
                        break;
                    }
                }
                if (pressed >= 0 && table.getStackInSlot(0) != null && table.getStackInSlot(0).getItemDamage() != table.getStackInSlot(0).getMaxDamage()) {
                    mc.playerController.sendEnchantPacket(inventorySlots.windowId, 4 + pressed);
                }
            }
            else {
                int var9 = mx - (25 + xx);
                int var10 = my - (55 + yy);
                if (var9 >= 0 && var10 >= 0 && var9 < 75 && var10 < 90 && table.getStackInSlot(1) != null) {
                    drawCards();
                }
            }
        }
    }
    
    void checkCards() {
        if (table.data.cardChoices.size() > 0 && cardChoices.isEmpty()) {
            syncFromTableChoices();
        }
        if (!cardSelected) {
            for (int a = 0; a < cardChoices.size(); ++a) {
                try {
                    if (table.data != null && table.data.cardChoices.size() > a && table.data.cardChoices.get(a).selected) {
                        for (int q = 0; q < cardChoices.size(); ++q) {
                            cardActive[q] = table.data.cardChoices.get(q).selected;
                        }
                        cardSelected = true;
                        playButtonPageSelect();
                        mc.playerController.sendEnchantPacket(inventorySlots.windowId, 1);
                        break;
                    }
                }
                catch (Exception ex) {}
            }
        }
    }
    
    private void playButtonPageFlip() {
        mc.getRenderViewEntity().playSound(SoundsTC.page, 1.0f, 1.0f);
    }
    
    private void playButtonPageSelect() {
        mc.getRenderViewEntity().playSound(SoundsTC.pageturn, 1.0f, 1.0f);
    }
    
    private void playButtonClick() {
        mc.getRenderViewEntity().playSound(SoundsTC.clack, 0.4f, 1.0f);
    }
    
    private void playButtonWrite() {
        mc.getRenderViewEntity().playSound(SoundsTC.write, 0.3f, 1.0f);
    }
}
