// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui;

import thaumcraft.common.lib.SoundsTC;
import java.io.IOException;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.common.lib.network.misc.PacketStartTheoryToServer;
import thaumcraft.common.lib.network.PacketHandler;
import net.minecraft.client.gui.GuiButton;
import thaumcraft.api.research.ResearchCategory;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.util.math.MathHelper;
import java.util.stream.Collector;
import java.util.function.Supplier;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.Map;
import thaumcraft.api.research.ResearchCategories;
import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import java.util.List;
import thaumcraft.client.lib.UtilsFX;
import java.util.Arrays;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.research.theorycraft.TheorycraftManager;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.RenderHelper;
import java.util.Iterator;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraft.client.gui.GuiScreen;
import java.util.HashSet;
import net.minecraft.inventory.Container;
import thaumcraft.common.container.ContainerResearchTable;
import java.util.ArrayList;
import thaumcraft.client.gui.plugins.GuiImageButton;
import java.util.Set;
import java.util.HashMap;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.gui.FontRenderer;
import thaumcraft.common.tiles.crafting.TileResearchTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;

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
    
    public GuiResearchTable(final EntityPlayer player, final TileResearchTable e) {
        super(new ContainerResearchTable(player.inventory, e));
        this.txBackground = new ResourceLocation("thaumcraft", "textures/gui/gui_research_table.png");
        this.txBase = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
        this.txPaper = new ResourceLocation("thaumcraft", "textures/gui/paper.png");
        this.txPaperGilded = new ResourceLocation("thaumcraft", "textures/gui/papergilded.png");
        this.txQuestion = new ResourceLocation("thaumcraft", "textures/aspects/_unknown.png");
        this.cardHover = new float[] { 0.0f, 0.0f, 0.0f };
        this.cardZoomOut = new float[] { 0.0f, 0.0f, 0.0f };
        this.cardZoomIn = new float[] { 0.0f, 0.0f, 0.0f };
        this.cardActive = new boolean[] { true, true, true };
        this.cardSelected = false;
        this.tempCatTotals = new HashMap<String, Integer>();
        this.nexCatCheck = 0L;
        this.nextCheck = 0L;
        this.dummyInspirationStart = 0;
        this.currentAids = new HashSet<String>();
        this.selectedAids = new HashSet<String>();
        this.buttonCreate = new GuiImageButton(this, 1, this.guiLeft + 128, this.guiTop + 22, 49, 11, "button.create.theory", null, this.txBase, 37, 66, 51, 13, 8978346);
        this.buttonComplete = new GuiImageButton(this, 7, this.guiLeft + 191, this.guiTop + 96, 49, 11, "button.complete.theory", null, this.txBase, 37, 66, 51, 13, 8978346);
        this.buttonScrap = new GuiImageButton(this, 9, this.guiLeft + 128, this.guiTop + 168, 49, 11, "button.scrap.theory", null, this.txBase, 37, 66, 51, 13, 16720418);
        this.cardChoices = new ArrayList<ResearchTableData.CardChoice>();
        this.table = e;
        this.xSize = 255;
        this.ySize = 255;
        this.galFontRenderer = FMLClientHandler.instance().getClient().standardGalacticFontRenderer;
        this.username = player.getName();
        this.player = player;
        if (this.table.data != null) {
            for (final String cat : this.table.data.categoryTotals.keySet()) {
                this.tempCatTotals.put(cat, this.table.data.categoryTotals.get(cat));
            }
            this.syncFromTableChoices();
            this.lastDraw = this.table.data.lastDraw;
        }
    }
    
    private void syncFromTableChoices() {
        this.cardChoices.clear();
        for (final ResearchTableData.CardChoice cc : this.table.data.cardChoices) {
            this.cardChoices.add(cc);
        }
    }
    
    protected void drawGuiContainerForegroundLayer(final int mx, final int my) {
    }
    
    public void drawScreen(final int mx, final int my, final float par3) {
        this.drawDefaultBackground();
        super.drawScreen(mx, my, par3);
        this.xSize_lo = (float)mx;
        this.ySize_lo = (float)my;
        final int xx = this.guiLeft;
        final int yy = this.guiTop;
        RenderHelper.disableStandardItemLighting();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.table.data == null) {
            if (!this.currentAids.isEmpty()) {
                final int side = Math.min(this.currentAids.size(), 6);
                int c = 0;
                int r = 0;
                GL11.glPushMatrix();
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.2f);
                this.mc.renderEngine.bindTexture(this.txBase);
                for (final String key : this.currentAids) {
                    final ITheorycraftAid mutator = TheorycraftManager.aids.get(key);
                    if (mutator == null) {
                        continue;
                    }
                    final int x = xx + 128 + 20 * c - side * 10;
                    final int y = yy + 85 + 35 * r;
                    if (this.isPointInRegion(x - xx, y - yy, 16, 16, mx, my) && !this.selectedAids.contains(key)) {
                        this.drawTexturedModalRect(x, y, 0, 96, 16, 16);
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
            final int cw = 110;
            final int sz = this.cardChoices.size();
            int a = 0;
            if (!this.cardSelected) {
                for (final ResearchTableData.CardChoice cardChoice : this.cardChoices) {
                    if (this.cardZoomOut[a] >= 1.0f) {
                        final float dx = (float)(55 + sx - 55 * sz + cw * a - 65);
                        float fx = 65.0f + dx * this.cardZoomOut[a];
                        final float qx = 191.0f - fx;
                        if (this.cardActive[a]) {
                            fx += qx * this.cardZoomIn[a];
                        }
                        this.drawSheetOverlay(fx, 100.0, cardChoice, mx, my);
                        ++a;
                    }
                }
            }
            int qq = 0;
            if (this.table.getStackInSlot(0) == null || this.table.getStackInSlot(0).isEmpty() || this.table.getStackInSlot(0).getItemDamage() == this.table.getStackInSlot(0).getMaxDamage()) {
                sx = Math.max(this.fontRenderer.getStringWidth(I18n.translateToLocal("tile.researchtable.noink.0")), this.fontRenderer.getStringWidth(I18n.translateToLocal("tile.researchtable.noink.1"))) / 2;
                UtilsFX.drawCustomTooltip(this, this.fontRenderer, Arrays.asList(I18n.translateToLocal("tile.researchtable.noink.0"), I18n.translateToLocal("tile.researchtable.noink.1")), xx - sx + 116, yy + 60 + qq, 11, true);
                qq += 40;
            }
            if (this.table.getStackInSlot(1) == null || this.table.getStackInSlot(1).isEmpty()) {
                sx = this.fontRenderer.getStringWidth(I18n.translateToLocal("tile.researchtable.nopaper.0")) / 2;
                UtilsFX.drawCustomTooltip(this, this.fontRenderer, Arrays.asList(I18n.translateToLocal("tile.researchtable.nopaper.0")), xx - sx + 116, yy + 60 + qq, 11, true);
            }
        }
        this.renderHoveredToolTip(mx, my);
    }
    
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mx, final int my) {
        this.checkButtons();
        final int xx = this.guiLeft;
        final int yy = this.guiTop;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        this.mc.renderEngine.bindTexture(this.txBackground);
        this.drawTexturedModalRect(xx, yy, 0, 0, 255, 255);
        this.fontRenderer.drawString(" ", 0, 0, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.table.data == null) {
            if (this.nextCheck < this.player.ticksExisted) {
                this.currentAids = this.table.checkSurroundingAids();
                this.dummyInspirationStart = ResearchTableData.getAvailableInspiration(this.player);
                this.nextCheck = this.player.ticksExisted + 100;
            }
            this.mc.renderEngine.bindTexture(this.txBase);
            GL11.glPushMatrix();
            GL11.glTranslated(xx + 128 - this.dummyInspirationStart * 5, yy + 55, 0.0);
            GL11.glScaled(0.5, 0.5, 0.0);
            for (int a = 0; a < this.dummyInspirationStart; ++a) {
                this.drawTexturedModalRect(20 * a, 0, (this.dummyInspirationStart - this.selectedAids.size() <= a) ? 48 : 32, 96, 16, 16);
            }
            GL11.glPopMatrix();
            if (!this.currentAids.isEmpty()) {
                final int side = Math.min(this.currentAids.size(), 6);
                int c = 0;
                int r = 0;
                for (final String key : this.currentAids) {
                    final ITheorycraftAid mutator = TheorycraftManager.aids.get(key);
                    if (mutator == null) {
                        continue;
                    }
                    final int x = xx + 128 + 20 * c - side * 10;
                    final int y = yy + 85 + 35 * r;
                    if (this.selectedAids.contains(key)) {
                        this.mc.renderEngine.bindTexture(this.txBase);
                        this.drawTexturedModalRect(x, y, 0, 96, 16, 16);
                    }
                    if (mutator.getAidObject() instanceof ItemStack || mutator.getAidObject() instanceof Block) {
                        GL11.glPushMatrix();
                        RenderHelper.enableGUIStandardItemLighting();
                        GlStateManager.disableLighting();
                        GlStateManager.enableRescaleNormal();
                        GlStateManager.enableColorMaterial();
                        GlStateManager.enableLighting();
                        final ItemStack s = (ItemStack)((mutator.getAidObject() instanceof ItemStack) ? mutator.getAidObject() : new ItemStack((Block)mutator.getAidObject()));
                        this.itemRender.renderItemAndEffectIntoGUI(s, x, y);
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
            this.checkCards();
            this.mc.renderEngine.bindTexture(this.txBase);
            GL11.glPushMatrix();
            GL11.glTranslated(xx + 15, yy + 150, 0.0);
            if (this.table.data != null) {
                for (int a = 0; a < this.table.data.bonusDraws; ++a) {
                    this.drawTexturedModalRect(a * 2, a, 64, 96, 16, 16);
                }
            }
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated(xx + 128 - this.table.data.inspirationStart * 5, yy + 16, 0.0);
            GL11.glScaled(0.5, 0.5, 0.0);
            for (int a = 0; a < this.table.data.inspirationStart; ++a) {
                this.drawTexturedModalRect(20 * a, 0, (this.table.data.inspiration <= a) ? 48 : 32, 96, 16, 16);
            }
            GL11.glPopMatrix();
            int sheets = 0;
            if (this.table.getStackInSlot(1) != null) {
                sheets = 1 + this.table.getStackInSlot(1).getCount() / 4;
            }
            Random r2 = new Random(55L);
            if (sheets > 0 && !this.table.data.isComplete()) {
                for (int a2 = 0; a2 < sheets; ++a2) {
                    this.drawSheet(xx + 65, yy + 100, 6.0, r2, 1.0f, 1.0f, null);
                }
                boolean highlight = false;
                final int var7 = mx - (25 + xx);
                final int var8 = my - (55 + yy);
                if (this.cardChoices.isEmpty() && var7 >= 0 && var8 >= 0 && var7 < 75 && var8 < 90) {
                    highlight = true;
                }
                GL11.glPushMatrix();
                GL11.glColor4f(1.0f, 1.0f, 1.0f, highlight ? 1.0f : 0.5f);
                GlStateManager.enableBlend();
                this.mc.renderEngine.bindTexture(this.txQuestion);
                GL11.glTranslated(xx + 65, yy + 100, 0.0);
                GL11.glScaled(highlight ? 1.75 : 1.5, highlight ? 1.75 : 1.5, 0.0);
                UtilsFX.drawTexturedQuadFull(-8.0f, -8.0f, 0.0);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glPopMatrix();
            }
            for (final Long seed : this.table.data.savedCards) {
                r2 = new Random(seed);
                this.drawSheet(xx + 191, yy + 100, 6.0, r2, 1.0f, 1.0f, null);
            }
            if (this.lastDraw != null) {
                r2 = new Random(this.lastDraw.card.getSeed());
                this.drawSheet(xx + 191, yy + 100, 6.0, r2, 1.0f, 1.0f, this.lastDraw);
            }
            final ArrayList<String> sparkle = new ArrayList<String>();
            if (this.nexCatCheck < this.player.ticksExisted) {
                for (final String cat : ResearchCategories.researchCategories.keySet()) {
                    int t0 = 0;
                    if (this.table.data.categoryTotals.containsKey(cat)) {
                        t0 = this.table.data.categoryTotals.get(cat);
                    }
                    int t2 = 0;
                    if (this.tempCatTotals.containsKey(cat)) {
                        t2 = this.tempCatTotals.get(cat);
                    }
                    if (t0 == 0 && t2 == 0) {
                        this.tempCatTotals.remove(cat);
                    }
                    else {
                        if (t2 > t0) {
                            --t2;
                        }
                        if (t2 < t0) {
                            ++t2;
                            sparkle.add(cat);
                        }
                        this.tempCatTotals.put(cat, t2);
                    }
                }
                this.nexCatCheck = this.player.ticksExisted + 1;
            }
            final HashMap<String, Integer> unsortedMap = new HashMap<String, Integer>();
            String hf = null;
            int lf = 0;
            for (final String cat2 : this.tempCatTotals.keySet()) {
                final int cf = this.tempCatTotals.get(cat2);
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
            final Comparator<Map.Entry<String, Integer>> valueComparator = (e1, e2) -> e2.getValue().compareTo(e1.getValue());
            final Map<String, Integer> sortedMap = unsortedMap.entrySet().stream().sorted(valueComparator).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            int i = 0;
            for (final String field : sortedMap.keySet()) {
                GL11.glPushMatrix();
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glTranslatef((float)(xx + 253), (float)(yy + 16 + i * 18 + ((i > 0) ? 4 : 0)), 0.0f);
                GL11.glScaled(0.0625, 0.0625, 0.0625);
                this.mc.renderEngine.bindTexture(ResearchCategories.getResearchCategory(field).icon);
                this.drawTexturedModalRect(0, 0, 0, 0, 255, 255);
                GL11.glPopMatrix();
                GL11.glTranslatef(0.0f, 0.0f, 5.0f);
                String s2 = sortedMap.get(field) + "%";
                if (i > this.table.data.penaltyStart) {
                    final int q = sortedMap.get(field) / 3;
                    s2 = s2 + " (-" + q + ")";
                }
                this.mc.fontRenderer.drawStringWithShadow(s2, (float)(xx + 276), (float)(yy + 20 + i * 18 + ((i > this.table.data.penaltyStart) ? 4 : 0)), this.table.data.categoriesBlocked.contains(field) ? 6316128 : ((i <= this.table.data.penaltyStart) ? 57536 : 16777215));
                if (sparkle.contains(field)) {
                    for (int q = 0; q < 2; ++q) {
                        final float rr = MathHelper.getInt(this.player.getRNG(), 255, 255) / 255.0f;
                        final float gg = MathHelper.getInt(this.player.getRNG(), 189, 255) / 255.0f;
                        final float bb = MathHelper.getInt(this.player.getRNG(), 64, 255) / 255.0f;
                        FXDispatcher.INSTANCE.drawSimpleSparkleGui(this.player.getRNG(), xx + 276 + this.player.getRNG().nextFloat() * this.fontRenderer.getStringWidth(s2), yy + 20 + this.player.getRNG().nextFloat() * 8.0f + i * 18 + ((i > this.table.data.penaltyStart) ? 4 : 0), this.player.world.rand.nextGaussian() * 0.5, this.player.world.rand.nextGaussian() * 0.5, 24.0f, rr, gg, bb, 0, 0.9f, -1.0f);
                    }
                }
                final int var9 = mx - (xx + 256);
                final int var10 = my - (yy + 16 + i * 18 + ((i > this.table.data.penaltyStart) ? 4 : 0));
                if (var9 >= 0 && var10 >= 0 && var9 < 16 && var10 < 16) {
                    GL11.glPushMatrix();
                    UtilsFX.drawCustomTooltip(this, this.fontRenderer, Arrays.asList(ResearchCategories.getCategoryName(field)), mx + 8, my + 8, 11);
                    GL11.glPopMatrix();
                    RenderHelper.disableStandardItemLighting();
                }
                ++i;
            }
            final int sx = 128;
            final int cw = 110;
            final int sz = this.cardChoices.size();
            int a3 = 0;
            for (final ResearchTableData.CardChoice cardChoice : this.cardChoices) {
                r2 = new Random(cardChoice.card.getSeed());
                final int var11 = mx - (5 + sx - 55 * sz + xx + cw * a3);
                final int var12 = my - (100 + yy - 60);
                if (this.cardZoomOut[a3] >= 0.95 && !this.cardSelected) {
                    if (var11 >= 0 && var12 >= 0 && var11 < 100 && var12 < 120) {
                        final float[] cardHover = this.cardHover;
                        final int n = a3;
                        cardHover[n] += Math.max((0.25f - this.cardHover[a3]) / 3.0f * partialTicks, 0.0025f);
                    }
                    else {
                        final float[] cardHover2 = this.cardHover;
                        final int n2 = a3;
                        cardHover2[n2] -= 0.1f * partialTicks;
                    }
                }
                if (a3 == sz - 1 || this.cardZoomOut[a3 + 1] > 0.6) {
                    final float f = this.cardZoomOut[a3];
                    final float[] cardZoomOut = this.cardZoomOut;
                    final int n3 = a3;
                    cardZoomOut[n3] += Math.max((1.0f - this.cardZoomOut[a3]) / 5.0f * partialTicks, 0.0025f);
                    if (this.cardZoomOut[a3] > 0.0f && f == 0.0f) {
                        this.playButtonPageFlip();
                    }
                }
                final float prevZoomIn = this.cardZoomIn[a3];
                if (this.cardSelected) {
                    final float[] cardZoomIn = this.cardZoomIn;
                    final int n4 = a3;
                    cardZoomIn[n4] += (float)(this.cardActive[a3] ? Math.max((1.0f - this.cardZoomIn[a3]) / 3.0f * partialTicks, 0.0025) : (0.3f * partialTicks));
                    this.cardHover[a3] = 1.0f - this.cardZoomIn[a3];
                }
                this.cardZoomIn[a3] = MathHelper.clamp(this.cardZoomIn[a3], 0.0f, 1.0f);
                this.cardHover[a3] = MathHelper.clamp(this.cardHover[a3], 0.0f, 0.25f);
                this.cardZoomOut[a3] = MathHelper.clamp(this.cardZoomOut[a3], 0.0f, 1.0f);
                final float dx = (float)(55 + sx - 55 * sz + xx + cw * a3 - (xx + 65));
                float fx = xx + 65 + dx * this.cardZoomOut[a3];
                final float qx = xx + 191 - fx;
                if (this.cardActive[a3]) {
                    fx += qx * this.cardZoomIn[a3];
                }
                this.drawSheet(fx, yy + 100, 6.0f + this.cardZoomOut[a3] * 2.0f - this.cardZoomIn[a3] * 2.0f + this.cardHover[a3], r2, this.cardActive[a3] ? 1.0f : (1.0f - this.cardZoomIn[a3]), Math.max(1.0f - this.cardZoomOut[a3], this.cardZoomIn[a3]), cardChoice);
                if (this.cardSelected && this.cardActive[a3] && this.cardZoomIn[a3] >= 1.0f && prevZoomIn < 1.0f) {
                    this.playButtonWrite();
                    this.cardChoices.clear();
                    this.cardSelected = false;
                    this.lastDraw = this.table.data.lastDraw;
                    break;
                }
                ++a3;
            }
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderHelper.disableStandardItemLighting();
    }
    
    private void drawSheet(final double x, final double y, final double scale, final Random r, final float alpha, final float tilt, final ResearchTableData.CardChoice cardChoice) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
        GL11.glTranslated(x + r.nextGaussian(), y + r.nextGaussian(), 0.0);
        GL11.glScaled(scale, scale, 0.0);
        GL11.glRotated(r.nextGaussian() * tilt, 0.0, 0.0, 1.0);
        GL11.glPushMatrix();
        if (cardChoice != null && cardChoice.fromAid) {
            this.mc.renderEngine.bindTexture(this.txPaperGilded);
        }
        else {
            this.mc.renderEngine.bindTexture(this.txPaper);
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
                final ResearchCategory rc = ResearchCategories.getResearchCategory(cardChoice.card.getResearchCategory());
                if (rc != null) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha / 6.0f);
                    GL11.glPushMatrix();
                    GL11.glScaled(0.5, 0.5, 0.0);
                    this.mc.renderEngine.bindTexture(rc.icon);
                    UtilsFX.drawTexturedQuadFull(-8.0f, -8.0f, 0.0);
                    GL11.glPopMatrix();
                }
            }
            GL11.glPushMatrix();
            GL11.glScaled(0.0625, 0.0625, 0.0);
            GL11.glColor4f(0.0f, 0.0f, 0.0f, alpha);
            final String name = TextFormatting.BOLD + cardChoice.card.getLocalizedName() + TextFormatting.RESET;
            final int sz = this.fontRenderer.getStringWidth(name);
            this.fontRenderer.drawString(name, -sz / 2, -65, 0);
            this.fontRenderer.drawSplitString(cardChoice.card.getLocalizedText(), -70, -48, 140, 0);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            this.mc.renderEngine.bindTexture(this.txBase);
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
                    this.drawTexturedModalRect(-10 * cc + 20 * a, -95, 48, 0, 16, 16);
                }
                else {
                    this.drawTexturedModalRect(-10 * cc + 20 * a, -95, 32, 96, 16, 16);
                }
            }
            GL11.glPopMatrix();
            if (cardChoice.card.getRequiredItems() != null) {
                final ItemStack[] items = cardChoice.card.getRequiredItems();
                GL11.glPushMatrix();
                for (int a2 = 0; a2 < items.length; ++a2) {
                    if (items[a2] == null || items[a2].isEmpty()) {
                        GL11.glPushMatrix();
                        this.mc.renderEngine.bindTexture(this.txQuestion);
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
                        this.itemRender.renderItemAndEffectIntoGUI(items[a2], -9 * items.length + 18 * a2, 35);
                        GlStateManager.disableLighting();
                        GlStateManager.depthMask(true);
                        GlStateManager.enableDepth();
                        GL11.glPopMatrix();
                        try {
                            if (cardChoice.card.getRequiredItemsConsumed()[a2]) {
                                GL11.glPushMatrix();
                                this.mc.renderEngine.bindTexture(this.txBase);
                                GL11.glScaled(0.125, 0.125, 0.0);
                                final float s = (float)Math.sin((this.player.ticksExisted + a2 * 2 + this.mc.getRenderPartialTicks()) / 2.0f) * 0.03f;
                                GL11.glTranslated(-2 - 9 * items.length + 18 * a2, 45.0f + s * 10.0f, 0.0);
                                GL11.glScaled(0.5, 0.5 + s, 0.0);
                                this.drawTexturedModalRect(0, 0, 64, 120, 16, 16);
                                GL11.glPopMatrix();
                            }
                        }
                        catch (final Exception ex) {}
                    }
                }
                GL11.glPopMatrix();
            }
        }
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }
    
    private void drawSheetOverlay(final double x, final double y, final ResearchTableData.CardChoice cardChoice, final int mx, final int my) {
        GL11.glPushMatrix();
        if (cardChoice != null && cardChoice.card.getRequiredItems() != null) {
            final ItemStack[] items = cardChoice.card.getRequiredItems();
            for (int a = 0; a < items.length; ++a) {
                if (this.isPointInRegion((int)(x - 9 * items.length + 18 * a), (int)(y + 36.0), 15, 15, mx, my)) {
                    if (items[a] == null || items[a].isEmpty()) {
                        this.drawHoveringText(Arrays.asList(I18n.translateToLocal("tc.card.unknown")), mx, my);
                    }
                    else {
                        this.renderToolTip(items[a], mx, my);
                    }
                }
            }
        }
        GL11.glPopMatrix();
    }
    
    private void drawCards() {
        this.cardSelected = false;
        this.cardHover = new float[] { 0.0f, 0.0f, 0.0f };
        this.cardZoomOut = new float[] { 0.0f, 0.0f, 0.0f };
        this.cardZoomIn = new float[] { 0.0f, 0.0f, 0.0f };
        this.cardActive = new boolean[] { true, true, true };
        int draw = 2;
        if (this.table.data.bonusDraws > 0) {
            ++draw;
            final ResearchTableData data = this.table.data;
            --data.bonusDraws;
        }
        this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, draw);
        this.cardChoices.clear();
    }
    
    public void initGui() {
        super.initGui();
        this.buttonList.add(this.buttonCreate);
        this.buttonCreate.x = this.guiLeft + 128;
        this.buttonCreate.y = this.guiTop + 22;
        this.buttonList.add(this.buttonComplete);
        this.buttonComplete.x = this.guiLeft + 191;
        this.buttonComplete.y = this.guiTop + 96;
        this.buttonList.add(this.buttonScrap);
        this.buttonScrap.x = this.guiLeft + 128;
        this.buttonScrap.y = this.guiTop + 168;
    }
    
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == 1) {
            this.playButtonClick();
            PacketHandler.INSTANCE.sendToServer(new PacketStartTheoryToServer(this.table.getPos(), this.selectedAids));
        }
        else if (button.id == 7) {
            this.playButtonClick();
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 7);
            this.tempCatTotals.clear();
            this.lastDraw = null;
        }
        else if (button.id == 9) {
            this.playButtonClick();
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 9);
            this.tempCatTotals.clear();
            this.lastDraw = null;
            this.table.data = null;
            this.cardChoices.clear();
        }
        else {
            super.actionPerformed(button);
        }
    }
    
    private void checkButtons() {
        this.buttonComplete.active = false;
        this.buttonComplete.visible = false;
        this.buttonScrap.active = false;
        this.buttonScrap.visible = false;
        if (this.table.data != null) {
            this.buttonCreate.active = false;
            this.buttonCreate.visible = false;
            if (this.table.data.isComplete()) {
                this.buttonComplete.active = true;
                this.buttonComplete.visible = true;
            }
            else {
                this.buttonScrap.active = true;
                this.buttonScrap.visible = true;
            }
        }
        else {
            this.buttonCreate.visible = true;
            if (this.table.getStackInSlot(1) == null || this.table.getStackInSlot(0) == null || this.table.getStackInSlot(0).getItemDamage() == this.table.getStackInSlot(0).getMaxDamage()) {
                this.buttonCreate.active = false;
            }
            else {
                this.buttonCreate.active = true;
            }
        }
    }
    
    protected void mouseClicked(final int mx, final int my, final int par3) throws IOException {
        super.mouseClicked(mx, my, par3);
        final int xx = (this.width - this.xSize) / 2;
        final int yy = (this.height - this.ySize) / 2;
        if (this.table.data == null) {
            if (!this.currentAids.isEmpty()) {
                final int side = Math.min(this.currentAids.size(), 6);
                int c = 0;
                int r = 0;
                for (final String key : this.currentAids) {
                    final ITheorycraftAid mutator = TheorycraftManager.aids.get(key);
                    if (mutator == null) {
                        continue;
                    }
                    final int x = 128 + 20 * c - side * 10;
                    final int y = 85 + 35 * r;
                    if (this.isPointInRegion(x, y, 16, 16, mx, my)) {
                        if (this.selectedAids.contains(key)) {
                            this.selectedAids.remove(key);
                        }
                        else if (this.selectedAids.size() + 1 < this.dummyInspirationStart) {
                            this.selectedAids.add(key);
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
            final int sx = 128;
            final int cw = 110;
            if (this.cardChoices.size() > 0) {
                int pressed = -1;
                for (int a = 0; a < this.cardChoices.size(); ++a) {
                    final int var7 = mx - (5 + sx - 55 * this.cardChoices.size() + xx + cw * a);
                    final int var8 = my - (100 + yy - 60);
                    if (this.cardZoomOut[a] >= 0.95 && !this.cardSelected && var7 >= 0 && var8 >= 0 && var7 < 100 && var8 < 120) {
                        pressed = a;
                        break;
                    }
                }
                if (pressed >= 0 && this.table.getStackInSlot(0) != null && this.table.getStackInSlot(0).getItemDamage() != this.table.getStackInSlot(0).getMaxDamage()) {
                    this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 4 + pressed);
                }
            }
            else {
                final int var9 = mx - (25 + xx);
                final int var10 = my - (55 + yy);
                if (var9 >= 0 && var10 >= 0 && var9 < 75 && var10 < 90 && this.table.getStackInSlot(1) != null) {
                    this.drawCards();
                }
            }
        }
    }
    
    void checkCards() {
        if (this.table.data.cardChoices.size() > 0 && this.cardChoices.isEmpty()) {
            this.syncFromTableChoices();
        }
        if (!this.cardSelected) {
            for (int a = 0; a < this.cardChoices.size(); ++a) {
                try {
                    if (this.table.data != null && this.table.data.cardChoices.size() > a && this.table.data.cardChoices.get(a).selected) {
                        for (int q = 0; q < this.cardChoices.size(); ++q) {
                            this.cardActive[q] = this.table.data.cardChoices.get(q).selected;
                        }
                        this.cardSelected = true;
                        this.playButtonPageSelect();
                        this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 1);
                        break;
                    }
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    private void playButtonPageFlip() {
        this.mc.getRenderViewEntity().playSound(SoundsTC.page, 1.0f, 1.0f);
    }
    
    private void playButtonPageSelect() {
        this.mc.getRenderViewEntity().playSound(SoundsTC.pageturn, 1.0f, 1.0f);
    }
    
    private void playButtonClick() {
        this.mc.getRenderViewEntity().playSound(SoundsTC.clack, 0.4f, 1.0f);
    }
    
    private void playButtonWrite() {
        this.mc.getRenderViewEntity().playSound(SoundsTC.write, 0.3f, 1.0f);
    }
}
