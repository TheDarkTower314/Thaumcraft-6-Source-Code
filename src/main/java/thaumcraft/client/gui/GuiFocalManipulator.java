// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.network.playerdata.PacketFocusNodesToServer;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.casters.ItemFocus;
import java.util.HashMap;
import thaumcraft.client.gui.plugins.GuiFocusSettingSpinnerButton;
import thaumcraft.client.gui.plugins.GuiHoverButton;
import java.util.Collection;
import java.util.Collections;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusMediumRoot;
import thaumcraft.client.lib.UtilsFX;
import java.awt.Color;
import net.minecraft.client.gui.FontRenderer;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.api.casters.NodeSetting;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.casters.FocusModSplit;
import thaumcraft.api.casters.FocusEffect;
import net.minecraft.util.text.TextFormatting;
import java.util.List;
import java.util.Iterator;
import thaumcraft.common.tiles.crafting.FocusElementNode;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusNode;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.common.lib.network.playerdata.PacketFocusNameToServer;
import thaumcraft.common.lib.network.PacketHandler;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.inventory.Container;
import thaumcraft.common.container.ContainerFocalManipulator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.text.DecimalFormat;
import net.minecraft.client.gui.GuiTextField;
import thaumcraft.client.gui.plugins.GuiSliderTC;
import thaumcraft.client.gui.plugins.GuiImageButton;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;

@SideOnly(Side.CLIENT)
public class GuiFocalManipulator extends GuiContainer
{
    private TileFocalManipulator table;
    private float xSize_lo;
    private float ySize_lo;
    private int isMouseButtonDown;
    protected int mouseX;
    protected int mouseY;
    protected double curMouseX;
    protected double curMouseY;
    ResourceLocation tex;
    ResourceLocation tex2;
    ResourceLocation tex3;
    ResourceLocation texbase;
    GuiImageButton buttonConfirm;
    GuiSliderTC scrollbarParts;
    GuiSliderTC scrollbarMainSide;
    GuiSliderTC scrollbarMainBottom;
    private GuiTextField nameField;
    int totalComplexity;
    int maxComplexity;
    int lastNodeHover;
    DecimalFormat myFormatter;
    ArrayList<String> shownParts;
    int partsStart;
    ItemStack[] components;
    boolean valid;
    static ResourceLocation iMedium;
    static ResourceLocation iEffect;
    private int nodeID;
    int sMinX;
    int sMinY;
    int sMaxX;
    int sMaxY;
    int selectedNode;
    float costCast;
    int costXp;
    int costVis;
    int scrollX;
    int scrollY;
    
    public GuiFocalManipulator(final InventoryPlayer par1InventoryPlayer, final TileFocalManipulator table) {
        super(new ContainerFocalManipulator(par1InventoryPlayer, table));
        this.isMouseButtonDown = 0;
        this.mouseX = 0;
        this.mouseY = 0;
        this.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_wandtable.png");
        this.tex2 = new ResourceLocation("thaumcraft", "textures/gui/gui_wandtable2.png");
        this.tex3 = new ResourceLocation("thaumcraft", "textures/gui/gui_wandtable3.png");
        this.texbase = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
        this.buttonConfirm = new GuiImageButton(this, 0, this.guiLeft + 232, this.guiTop + 7, 24, 16, null, I18n.translateToLocal("wandtable.text3"), this.texbase, 232, 240, 24, 16);
        this.totalComplexity = 0;
        this.maxComplexity = 0;
        this.lastNodeHover = -1;
        this.myFormatter = new DecimalFormat("#######.##");
        this.shownParts = new ArrayList<String>();
        this.partsStart = 0;
        this.components = null;
        this.valid = false;
        this.nodeID = 0;
        this.sMinX = 0;
        this.sMinY = 0;
        this.sMaxX = 0;
        this.sMaxY = 0;
        this.selectedNode = -1;
        this.costCast = 0.0f;
        this.costXp = 0;
        this.costVis = 0;
        this.scrollX = 0;
        this.scrollY = 0;
        this.table = table;
        this.xSize = 231;
        this.ySize = 231;
    }
    
    public void initGui() {
        super.initGui();
        this.gatherInfo(false);
        this.setupNameField();
        if (this.table.focusName.isEmpty() && !this.table.getStackInSlot(0).isEmpty()) {
            this.table.focusName = this.table.getStackInSlot(0).getDisplayName();
        }
        this.nameField.setText(this.table.focusName);
    }
    
    private void setupNameField() {
        Keyboard.enableRepeatEvents(true);
        final int i = (this.width - this.xSize) / 2;
        final int j = (this.height - this.ySize) / 2;
        (this.nameField = new GuiTextField(2, this.fontRenderer, i + 30, j + 11, 170, 12)).setTextColor(-1);
        this.nameField.setDisabledTextColour(-1);
        this.nameField.setEnableBackgroundDrawing(false);
        this.nameField.setMaxStringLength(50);
    }
    
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == 0 && this.buttonConfirm != null && this.buttonConfirm.active) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 0);
        }
        else if (button.id >= 10) {
            this.gatherInfo(true);
        }
        else {
            super.actionPerformed(button);
        }
    }
    
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if (this.nameField.textboxKeyTyped(typedChar, keyCode)) {
            this.table.focusName = this.nameField.getText();
            PacketHandler.INSTANCE.sendToServer(new PacketFocusNameToServer(this.table.getPos(), this.table.focusName));
        }
        else {
            super.keyTyped(typedChar, keyCode);
        }
    }
    
    public void drawScreen(final int mx, final int my, final float par3) {
        this.drawDefaultBackground();
        super.drawScreen(mx, my, par3);
        GlStateManager.enableBlend();
        GL11.glBlendFunc(770, 771);
        this.xSize_lo = (float)mx;
        this.ySize_lo = (float)my;
        final int gx = (this.width - this.xSize) / 2;
        final int gy = (this.height - this.ySize) / 2;
        if (Mouse.isButtonDown(0)) {
            if ((this.isMouseButtonDown == 0 || this.isMouseButtonDown == 1) && this.isPointInRegion(this.guiLeft + 63, this.guiTop + 31, 136, 160, mx + this.guiLeft, my + this.guiTop)) {
                if (this.isMouseButtonDown == 0) {
                    this.isMouseButtonDown = 1;
                }
                else {
                    final boolean b = this.scrollbarParts != null && this.scrollbarParts.isMouseOver();
                    if (this.scrollbarMainBottom != null && !this.scrollbarMainBottom.isMouseOver() && !b) {
                        this.scrollX -= mx - this.mouseX;
                        this.curMouseX = this.scrollX;
                        if (this.scrollX > this.scrollbarMainBottom.getMax()) {
                            this.scrollX = (int)this.scrollbarMainBottom.getMax();
                        }
                        if (this.scrollX < this.scrollbarMainBottom.getMin()) {
                            this.scrollX = (int)this.scrollbarMainBottom.getMin();
                        }
                        this.scrollbarMainBottom.setSliderValue((float)this.scrollX, false);
                    }
                    if (this.scrollbarMainSide != null && !this.scrollbarMainSide.isMouseOver() && !b) {
                        this.scrollY -= my - this.mouseY;
                        this.curMouseY = this.scrollY;
                        if (this.scrollY > this.scrollbarMainSide.getMax()) {
                            this.scrollY = (int)this.scrollbarMainSide.getMax();
                        }
                        if (this.scrollY < this.scrollbarMainSide.getMin()) {
                            this.scrollY = (int)this.scrollbarMainSide.getMin();
                        }
                        this.scrollbarMainSide.setSliderValue((float)this.scrollY, false);
                    }
                }
            }
            this.mouseX = mx;
            this.mouseY = my;
        }
        else {
            this.isMouseButtonDown = 0;
        }
        RenderHelper.disableStandardItemLighting();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        int count = 0;
        int index = 0;
        for (final String sk : this.shownParts) {
            if (++count - 1 < this.partsStart) {
                continue;
            }
            GL11.glTranslated(0.0, 0.0, 5.0);
            final FocusNode node = (FocusNode)FocusEngine.getElement(sk);
            drawPart(node, gx + 38, 43 + gy + 25 * index, (node.getType() == IFocusElement.EnumUnitType.MOD) ? 24.0f : 32.0f, 220, this.isPointInRegion(gx + 38 - 10, 32 + gy + 24 * index, 20, 20, mx + this.guiLeft, my + this.guiTop));
            GL11.glTranslated(0.0, 0.0, -5.0);
            if (++index > 5) {
                break;
            }
        }
        count = 0;
        index = 0;
        for (final String sk : this.shownParts) {
            if (++count - 1 < this.partsStart) {
                continue;
            }
            final FocusNode node = (FocusNode)FocusEngine.getElement(sk);
            if (this.isPointInRegion(gx + 38 - 10, 33 + gy + 25 * index, 20, 20, mx + this.guiLeft, my + this.guiTop)) {
                final List list = this.genPartText(node, -1);
                this.drawHoveringTextFixed(list, gx + 44, 46 + gy + 24 * index, this.fontRenderer, this.width - (this.guiLeft + this.xSize - 16));
            }
            if (++index > 5) {
                break;
            }
        }
        if (this.lastNodeHover >= 0) {
            final FocusElementNode fn = this.table.data.get(this.lastNodeHover);
            if (fn != null && fn.node != null) {
                final int xx = this.guiLeft + 140 - this.scrollX + fn.x * 24;
                final int yy = this.guiTop + 50 - this.scrollY + fn.y * 32;
                final List list = this.genPartText(fn.node, this.lastNodeHover);
                this.drawHoveringTextFixed(list, xx, yy, this.fontRenderer, this.width - (this.guiLeft + this.xSize - 16));
            }
        }
        this.buttonConfirm.active = (this.table.vis <= 0.0f && this.valid);
        GlStateManager.disableBlend();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.table.data != null && !this.table.data.isEmpty() && this.nameField != null) {
            this.nameField.drawTextBox();
        }
        this.renderHoveredToolTip(mx, my);
    }
    
    private List genPartText(final FocusNode node, final int idx) {
        final List list = new ArrayList();
        if (node != null) {
            FocusElementNode placed = null;
            if (idx >= 0) {
                placed = this.table.data.get(idx);
            }
            list.add(I18n.translateToLocal(node.getUnlocalizedName()));
            list.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal(node.getUnlocalizedText()));
            int c = node.getComplexity();
            if (placed != null) {
                c = (int)(node.getComplexity() * placed.complexityMultiplier);
            }
            list.add(TextFormatting.GOLD + I18n.translateToLocal("focuspart.com") + ((placed != null && placed.complexityMultiplier > 1.0f) ? TextFormatting.RED : "") + " " + c);
            float p = node.getPowerMultiplier();
            if (placed != null) {
                p = placed.getPower(this.table.data);
            }
            if (p != 1.0f) {
                list.add(TextFormatting.GOLD + I18n.translateToLocal("focuspart.eff") + ((p < 1.0f) ? TextFormatting.RED : TextFormatting.GREEN) + " x" + ItemStack.DECIMALFORMAT.format(p));
            }
            if (node instanceof FocusEffect) {
                final FocusEffect fe = (FocusEffect)node;
                float d = fe.getDamageForDisplay((placed == null) ? 1.0f : placed.getPower(this.table.data));
                if (d > 0.0f) {
                    list.add(TextFormatting.DARK_RED + "" + I18n.translateToLocalFormatted("attribute.modifier.equals.0", new Object[] { ItemStack.DECIMALFORMAT.format(d), I18n.translateToLocal("attribute.name.generic.attackDamage") }));
                }
                else if (d < 0.0f) {
                    d *= -1.0f;
                    list.add(TextFormatting.DARK_GREEN + "" + I18n.translateToLocalFormatted("attribute.modifier.equals.0", new Object[] { ItemStack.DECIMALFORMAT.format(d), I18n.translateToLocal("focus.heal.power") }));
                }
            }
        }
        return list;
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(this.tex2);
        final int k = (this.width - this.xSize) / 2;
        final int l = (this.height - this.ySize) / 2;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        this.mc.renderEngine.bindTexture(this.tex3);
        this.drawTexturedModalRect(k - 71, l - 3, 0, 0, 71, 239);
        if (this.table.getStackInSlot(0) == null || this.table.getStackInSlot(0).isEmpty() || this.table.doGuiReset) {
            if (this.table.doGuiReset) {
                this.resetNodes();
            }
            else {
                this.table.data.clear();
                this.gatherInfo(false);
            }
            this.table.focusName = "";
            if (this.table.getStackInSlot(0) != null && !this.table.getStackInSlot(0).isEmpty()) {
                this.table.focusName = this.table.getStackInSlot(0).getDisplayName();
                this.nameField.setText(this.table.focusName);
            }
            this.table.doGuiReset = false;
        }
        if (this.table.doGather) {
            this.gatherInfo(false);
            this.table.doGather = false;
        }
        this.drawNodes(this.guiLeft + 132 - this.scrollX, this.guiTop + 48 - this.scrollY, par2, par3);
        GL11.glTranslated(0.0, 0.0, 1.0);
        this.mc.renderEngine.bindTexture(this.tex);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        if (this.maxComplexity > 0) {
            this.fontRenderer.drawStringWithShadow(this.totalComplexity + "/" + this.maxComplexity, (float)(k + 242), (float)(l + 36), (this.totalComplexity > this.maxComplexity) ? 16151160 : 16761407);
        }
        this.fontRenderer.drawStringWithShadow("" + this.costXp, (float)(k + 242), (float)(l + 50), (this.costXp > this.mc.player.experienceLevel) ? 16151160 : 10092429);
        int v = this.costVis;
        if (this.table.vis > 0.0f) {
            v = (int)this.table.vis;
        }
        this.fontRenderer.drawStringWithShadow(TextFormatting.AQUA + "" + v, (float)(k + 242), (float)(l + 64), 10092429);
        if (this.costCast > 0.0f) {
            final String cost = this.myFormatter.format(this.costCast);
            this.fontRenderer.drawStringWithShadow(TextFormatting.AQUA + I18n.translateToLocal("item.Focus.cost1") + ": " + cost, (float)(k + 230), (float)(l + 80), 10092429);
        }
        if (this.components != null && this.components.length > 0) {
            this.fontRenderer.drawStringWithShadow(TextFormatting.GOLD + I18n.translateToLocal("wandtable.text4"), (float)(k + 230), (float)(l + 92), 10092429);
        }
        if (this.table.focusName != null && !this.table.data.isEmpty()) {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.renderEngine.bindTexture(this.texbase);
            this.drawTexturedModalRect(this.guiLeft + 24, this.guiTop + 8, 192, 224, 8, 14);
            int a;
            for (a = 1, a = 1; a < 22; ++a) {
                this.drawTexturedModalRect(this.guiLeft + 24 + a * 8, this.guiTop + 8, 200, 224, 8, 14);
            }
            this.drawTexturedModalRect(this.guiLeft + 24 + a * 8, this.guiTop + 8, 208, 224, 8, 14);
        }
    }
    
    private void drawClippedRect(final int x, final int y, final int sx, final int sy, final int w, final int h) {
        if (this.isPointInRegion(48, 26, 166, 174, x + w / 2, y + h / 2)) {
            this.drawTexturedModalRect(x, y, sx, sy, w, h);
        }
    }
    
    private void drawNodes(final int x, final int y, final int mx, final int my) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GlStateManager.disableLighting();
        if (this.table.data != null && !this.table.data.isEmpty()) {
            int hover = -1;
            for (final FocusElementNode fn : this.table.data.values()) {
                final int xx = x + fn.x * 24;
                final int yy = y + fn.y * 32;
                final boolean mouseover = this.isPointInRegion(this.guiLeft + 63, this.guiTop + 31, 136, 160, mx + this.guiLeft, my + this.guiTop) && this.isPointInRegion(xx - 10, yy - 10, 20, 20, mx + this.guiLeft, my + this.guiTop);
                if (mouseover && fn.parent >= 0) {
                    hover = fn.id;
                }
                if (fn.node != null) {
                    if (this.isPointInRegion(48, 16, 154, 192, xx - 8, yy - 8)) {
                        drawPart(fn.node, xx, yy, 32.0f, 220, mouseover);
                    }
                }
                else {
                    this.mc.renderEngine.bindTexture(this.tex);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    this.drawClippedRect(xx - 12, yy - 12, 120, 232, 24, 24);
                }
                if (this.selectedNode == fn.id || (mouseover && fn.parent >= 0)) {
                    this.mc.renderEngine.bindTexture(this.tex);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    this.drawClippedRect(xx - 12, yy - 12, 96, 232, 24, 24);
                }
                final FocusElementNode parent = this.table.data.get(fn.parent);
                if (parent != null) {
                    this.mc.renderEngine.bindTexture(this.tex);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    this.drawClippedRect(xx - 6, yy - 22, 54, 232, 12, 12);
                    if (parent.node instanceof FocusModSplit) {
                        for (int q = Math.abs(fn.x - parent.x), a = 0; a < q; ++a) {
                            if (fn.x < parent.x) {
                                if (a == 0) {
                                    this.drawClippedRect(xx - 4, yy - 36, 8, 240, 16, 16);
                                }
                                else {
                                    this.drawClippedRect(xx - 12 + a * 24, yy - 36, 72, 240, 24, 16);
                                }
                            }
                            else if (a == 0) {
                                this.drawClippedRect(xx - 12, yy - 36, 24, 240, 16, 16);
                            }
                            else {
                                this.drawClippedRect(xx - 12 - a * 24, yy - 36, 72, 240, 24, 16);
                            }
                        }
                    }
                    if (fn.node != null) {
                        continue;
                    }
                    final int s = (parent.target && parent.trajectory) ? 4 : 0;
                    if (!this.isPointInRegion(48, 16, 168, 192, xx - 4, yy - 4)) {
                        continue;
                    }
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    if (parent.target) {
                        GL11.glPushMatrix();
                        GL11.glTranslated(xx - s, yy, 0.0);
                        GL11.glScaled(0.5, 0.5, 0.5);
                        this.drawTexturedModalRect(-8, -8, 152, 240, 16, 16);
                        GL11.glPopMatrix();
                    }
                    if (!parent.trajectory) {
                        continue;
                    }
                    GL11.glPushMatrix();
                    GL11.glTranslated(xx + s, yy, 0.0);
                    GL11.glScaled(0.5, 0.5, 0.5);
                    this.drawTexturedModalRect(-8, -8, 168, 240, 16, 16);
                    GL11.glPopMatrix();
                }
            }
            if (hover >= 0 && this.lastNodeHover != hover) {
                this.playRollover();
                this.lastNodeHover = hover;
            }
            if (hover < 0) {
                this.lastNodeHover = -1;
            }
            if (this.selectedNode >= 0) {
                this.drawNodeSettings(this.selectedNode);
            }
        }
        GlStateManager.enableLighting();
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
    }
    
    private void drawNodeSettings(final int selectedNode2) {
        final FocusElementNode fn = this.table.data.get(selectedNode2);
        if (fn != null && fn.node != null && !fn.node.getSettingList().isEmpty()) {
            int a = 0;
            for (final String sk : fn.node.getSettingList()) {
                final NodeSetting setting = fn.node.getSetting(sk);
                if (setting.getResearch() != null && !ThaumcraftCapabilities.knowsResearchStrict(this.mc.player, setting.getResearch())) {
                    continue;
                }
                final int x = this.guiLeft + this.xSize;
                final int y = this.guiTop + this.ySize - 10 - fn.node.getSettingList().size() * 26 + a * 26;
                this.fontRenderer.drawStringWithShadow(TextFormatting.GOLD + setting.getLocalizedName(), (float)x, (float)y, 16777215);
                ++a;
            }
        }
    }
    
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        final int k = Mouse.getDWheel();
        final int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        final int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        if (this.scrollbarParts != null && this.shownParts.size() > 6 && this.isPointInRegion(24, 24, 32, 157, i, j)) {
            if (k > 0) {
                if (this.partsStart > 0) {
                    --this.partsStart;
                    this.scrollbarParts.setSliderValue((float)this.partsStart, false);
                }
            }
            else if (k < 0 && this.partsStart < this.shownParts.size() - 6) {
                ++this.partsStart;
                this.scrollbarParts.setSliderValue((float)this.partsStart, false);
            }
        }
    }
    
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        RenderHelper.disableStandardItemLighting();
        for (final GuiButton guibutton : this.buttonList) {
            if (guibutton.isMouseOver()) {
                guibutton.drawButtonForegroundLayer(mouseX - this.guiLeft, mouseY - this.guiTop);
                break;
            }
        }
        RenderHelper.enableGUIStandardItemLighting();
        if (this.scrollbarMainSide != null) {
            final int sv = Math.round(this.scrollbarMainSide.getSliderValue());
            if (sv != this.scrollY) {
                this.scrollY = sv;
            }
        }
        if (this.scrollbarMainBottom != null) {
            final int sv = Math.round(this.scrollbarMainBottom.getSliderValue());
            if (sv != this.scrollX) {
                this.scrollX = sv;
            }
        }
        if (this.scrollbarParts != null) {
            final int sv = Math.round(this.scrollbarParts.getSliderValue());
            if (sv != this.partsStart) {
                this.partsStart = sv;
            }
        }
    }
    
    protected void mouseClicked(final int mx, final int my, final int par3) {
        try {
            super.mouseClicked(mx, my, par3);
        }
        catch (final IOException ex) {}
        final int gx = (this.width - this.xSize) / 2;
        final int gy = (this.height - this.ySize) / 2;
        if (this.table.vis <= 0.0f && this.table.data != null && !this.table.data.isEmpty()) {
            if (this.nameField != null) {
                this.nameField.mouseClicked(mx, my, par3);
            }
            if (this.lastNodeHover >= 0) {
                this.selectedNode = this.lastNodeHover;
                final boolean r = false;
                if (par3 == 1 && this.table.data.get(this.selectedNode).node != null) {
                    final FocusElementNode fn = this.table.data.get(this.selectedNode);
                    if (this.table.data.get(fn.parent).node != null) {
                        this.addNodeAt(this.table.data.get(fn.parent).node.getClass(), fn.parent, true);
                    }
                }
                this.gatherInfo(false);
                this.playButtonClick();
            }
            int count = 0;
            int index = 0;
            if (this.selectedNode >= 0) {
                for (final String sk : this.shownParts) {
                    if (++count - 1 < this.partsStart) {
                        continue;
                    }
                    if (this.isPointInRegion(gx + 28, gy + 32 + 24 * index, 20, 20, mx + this.guiLeft, my + this.guiTop)) {
                        this.addNodeAt(FocusEngine.elements.get(sk), this.selectedNode, true);
                        return;
                    }
                    if (++index > 5) {
                        break;
                    }
                }
            }
        }
    }
    
    private void playButtonClick() {
        this.mc.getRenderViewEntity().playSound(SoundsTC.clack, 0.4f, 1.0f);
    }
    
    private void playRollover() {
        this.mc.getRenderViewEntity().playSound(SoundsTC.clack, 0.4f, 2.0f);
    }
    
    private void playSocketSound(final float pitch) {
        this.mc.getRenderViewEntity().playSound(SoundsTC.crystal, 0.4f, pitch);
    }
    
    protected void drawHoveringTextFixed(final List listin, final int x, final int y, final FontRenderer font, final int textWidth) {
        if (!listin.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            final List<String> list = new ArrayList<>();
            for (final Object o : listin) {
                String s = (String)o;
                s = this.trimStringNewline(s);
                final List<String> list2 = font.listFormattedStringToWidth(s, textWidth);
                for (final String s2 : list2) {
                    list.add(s2);
                }
            }
            int k = 0;
            for (String s : list) {
                final int l = font.getStringWidth(s);
                if (l > k) {
                    k = l;
                }
            }
            final int j2 = x + 12;
            int k2 = y - 12;
            int i1 = 8;
            if (list.size() > 1) {
                i1 += 2 + (list.size() - 1) * 10;
            }
            this.zLevel = 300.0f;
            this.itemRender.zLevel = 300.0f;
            final int j3 = -267386864;
            this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j3, j3);
            this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j3, j3);
            this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j3, j3);
            this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j3, j3);
            this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j3, j3);
            final int k3 = 1347420415;
            final int l2 = (k3 & 0xFEFEFE) >> 1 | (k3 & 0xFF000000);
            this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k3, l2);
            this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k3, l2);
            this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k3, k3);
            this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l2, l2);
            for (int i2 = 0; i2 < list.size(); ++i2) {
                final String s3 = list.get(i2);
                font.drawStringWithShadow(s3, (float)j2, (float)k2, -1);
                if (i2 == 0) {
                    k2 += 2;
                }
                k2 += 10;
            }
            this.zLevel = 0.0f;
            this.itemRender.zLevel = 0.0f;
            GlStateManager.enableRescaleNormal();
        }
    }
    
    private String trimStringNewline(String text) {
        while (text != null && text.endsWith("\n")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }
    
    public static void drawPart(final FocusNode node, final int x, final int y, float scale, final int bright, final boolean mouseover) {
        GL11.glPushMatrix();
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glTranslated(x, y, 0.0);
        GL11.glRotatef(90.0f, 0.0f, 0.0f, -1.0f);
        final boolean root = node.getType() == IFocusElement.EnumUnitType.MOD || node.getKey().equals("thaumcraft.ROOT");
        if (root) {
            scale *= 2.0f;
        }
        final Color color = new Color(FocusEngine.getElementColor(node.getKey()));
        final float r = color.getRed() / 255.0f;
        final float g = color.getGreen() / 255.0f;
        final float b = color.getBlue() / 255.0f;
        switch (node.getType()) {
            case EFFECT: {
                UtilsFX.renderQuadCentered(GuiFocalManipulator.iEffect, (float)(scale * 0.9 + (mouseover ? 2 : 0)), r, g, b, 220, 771, 1.0f);
                break;
            }
            case MEDIUM: {
                if (!root) {
                    UtilsFX.renderQuadCentered(GuiFocalManipulator.iMedium, (float)(scale * 0.9 + (mouseover ? 2 : 0)), r, g, b, 220, 771, 1.0f);
                    break;
                }
                break;
            }
        }
        GL11.glTranslated(0.0, 0.0, 1.0);
        UtilsFX.renderQuadCentered(FocusEngine.getElementIcon(node.getKey()), scale / 2.0f + (mouseover ? 2 : 0), 1.0f, 1.0f, 1.0f, bright, 771, 1.0f);
        GL11.glAlphaFunc(516, 0.1f);
        GL11.glPopMatrix();
    }
    
    private int getNextId() {
        while (this.table.data.containsKey(this.nodeID)) {
            ++this.nodeID;
        }
        return this.nodeID;
    }
    
    private void cullChildren(final int idx) {
        if (this.table.data.containsKey(idx)) {
            for (final int i : this.table.data.get(idx).children) {
                this.cullChildren(i);
                this.table.data.remove(i);
            }
        }
    }
    
    private ArrayList<Integer> addNodeAt(final Class nodeClass, final int idx, final boolean gather) {
        final ArrayList<Integer> ret = new ArrayList<Integer>();
        boolean same = false;
        FocusElementNode previous = null;
        if (this.table.data.containsKey(idx)) {
            this.cullChildren(idx);
            if (this.table.data.get(idx).node != null && this.table.data.get(idx).node.getClass() == nodeClass) {
                same = true;
            }
            else {
                previous = this.table.data.remove(idx);
            }
        }
        try {
            FocusElementNode fn = null;
            FocusNode node = null;
            if (!same) {
                fn = new FocusElementNode();
                node = (FocusNode) nodeClass.newInstance();
                fn.node = node;
                if (previous != null) {
                    fn.y = previous.y;
                    fn.x = previous.x;
                }
                fn.id = this.getNextId();
                ret.add(fn.id);
                this.selectedNode = fn.id;
                if (previous != null && this.table.data.containsKey(previous.parent)) {
                    fn.parent = previous.parent;
                    final int[] c = this.table.data.get(previous.parent).children;
                    for (int a = 0; a < c.length; ++a) {
                        if (c[a] == previous.id) {
                            this.table.data.get(previous.parent).children[a] = fn.id;
                            break;
                        }
                    }
                }
                fn.target = node.canSupply(FocusNode.EnumSupplyType.TARGET);
                fn.trajectory = node.canSupply(FocusNode.EnumSupplyType.TRAJECTORY);
                this.table.data.put(this.nodeID, fn);
            }
            else {
                fn = this.table.data.get(idx);
                node = fn.node;
            }
            if (fn.target || fn.trajectory) {
                if (node instanceof FocusModSplit) {
                    final FocusElementNode blank1 = new FocusElementNode();
                    blank1.parent = fn.id;
                    blank1.id = this.getNextId();
                    ret.add(this.nodeID);
                    blank1.x = fn.x - 1;
                    blank1.y = fn.y + 1;
                    this.table.data.put(this.nodeID, blank1);
                    this.selectedNode = this.nodeID;
                    final FocusElementNode blank2 = new FocusElementNode();
                    blank2.parent = fn.id;
                    blank2.x = fn.x + 1;
                    blank2.y = fn.y + 1;
                    blank2.id = this.getNextId();
                    ret.add(this.nodeID);
                    this.table.data.put(this.nodeID, blank2);
                    fn.children = new int[] { blank1.id, blank2.id };
                }
                else {
                    final FocusElementNode blank3 = new FocusElementNode();
                    blank3.parent = fn.id;
                    blank3.x = fn.x;
                    blank3.y = fn.y + 1;
                    blank3.id = this.getNextId();
                    ret.add(this.nodeID);
                    this.table.data.put(this.nodeID, blank3);
                    fn.children = new int[] { blank3.id };
                    this.selectedNode = this.nodeID;
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        if (gather) {
            this.calcNodeTreeLayout();
            this.gatherInfo(true);
        }
        return ret;
    }
    
    private void processLeftNodes(final FocusElementNode start, final Bounds bounds) {
        if (start.children.length > 0) {
            this.processLeftNodes(this.table.data.get(start.children[0]), bounds);
        }
        int ox = 0;
        if (start.children.length == 1) {
            ox = bounds.x - 1;
            bounds.x = this.table.data.get(start.children[0]).x;
        }
        start.x = bounds.x;
        if (start.children.length == 1) {
            bounds.x = ox;
        }
        ++bounds.x;
        if (start.children.length > 1) {
            this.processLeftNodes(this.table.data.get(start.children[1]), bounds);
        }
    }
    
    private void centerNodes(final int start, final Bounds bounds) {
        final int x = bounds.x / 2;
        final FocusElementNode sn = this.table.data.get(start);
        this.moveNodes(sn, x);
    }
    
    private void moveNodes(final FocusElementNode start, final int amt) {
        for (final int ci : start.children) {
            this.moveNodes(this.table.data.get(ci), amt);
        }
        start.x -= amt;
    }
    
    private void calcNodeTreeLayout() {
        int fsi = -1;
        for (final FocusElementNode node : this.table.data.values()) {
            if (fsi < 0 && node.node != null && node.node instanceof FocusModSplit) {
                fsi = node.id;
            }
        }
        if (fsi >= 0) {
            final Bounds bounds = new Bounds();
            this.processLeftNodes(this.table.data.get(fsi), bounds);
            this.centerNodes(fsi, bounds);
        }
        for (final FocusElementNode node : this.table.data.values()) {
            if (node.node != null && node.node instanceof FocusModSplit) {
                if (this.table.data.containsKey(node.parent) && this.table.data.get(node.parent).node != null && !(this.table.data.get(node.parent).node instanceof FocusModSplit)) {
                    node.x = this.table.data.get(node.parent).x;
                }
                else {
                    int xx = 0;
                    for (final int a : node.children) {
                        xx += this.table.data.get(a).x;
                    }
                    xx /= node.children.length;
                    node.x = xx;
                }
            }
        }
        if (this.selectedNode >= 0 && !this.table.data.containsKey(this.selectedNode)) {
            this.selectedNode = -1;
        }
    }
    
    private void resetNodes() {
        this.nodeID = 0;
        this.table.data.clear();
        final ArrayList<Integer> r1 = this.addNodeAt(FocusMediumRoot.class, 0, false);
        this.selectedNode = r1.get(1);
        this.calcNodeTreeLayout();
        this.gatherInfo(true);
    }
    
    private void calcScrollBounds() {
        this.sMinX = 0;
        this.sMinY = 0;
        this.sMaxX = 0;
        this.sMaxY = 0;
        for (final FocusElementNode fn : this.table.data.values()) {
            if (fn.x < this.sMinX) {
                this.sMinX = fn.x;
            }
            if (fn.y < this.sMinY) {
                this.sMinY = fn.y;
            }
            if (fn.x > this.sMaxX) {
                this.sMaxX = fn.x;
            }
            if (fn.y > this.sMaxY) {
                this.sMaxY = fn.y;
            }
        }
    }
    
    private void gatherPartsList() {
        this.shownParts.clear();
        if (this.mc == null) {
            return;
        }
        if (this.selectedNode >= 0 && this.table.data.containsKey(this.selectedNode)) {
            this.partsStart = 0;
            final ArrayList<String> pMed = new ArrayList<String>();
            final ArrayList<String> pEff = new ArrayList<String>();
            final ArrayList<String> pMod = new ArrayList<String>();
            final ArrayList<String> excluded = new ArrayList<String>();
            boolean hasExlusive = false;
            boolean hasMedium = false;
            for (final FocusElementNode fn : this.table.data.values()) {
                if (fn.node != null && fn.node instanceof FocusMedium) {
                    hasMedium = !(fn.node instanceof FocusMediumRoot);
                    if (fn.node.isExclusive()) {
                        hasExlusive = true;
                        break;
                    }
                }
                if (fn.node != null && fn.node.isExclusive()) {
                    excluded.add(fn.node.getKey());
                }
            }
            final FocusElementNode node = this.table.data.get(this.selectedNode);
            final FocusElementNode parent = this.table.data.get(node.parent);
            if (parent != null && parent.node != null) {
                for (final String key : FocusEngine.elements.keySet()) {
                    final IFocusElement element = FocusEngine.getElement(key);
                    if (!ThaumcraftCapabilities.knowsResearchStrict(this.mc.player, element.getResearch())) {
                        continue;
                    }
                    if (element.getKey().equals("thaumcraft.ROOT")) {
                        continue;
                    }
                    if (!(element instanceof FocusNode)) {
                        continue;
                    }
                    final FocusNode fn2 = (FocusNode)element;
                    if (excluded.contains(fn2.getKey())) {
                        continue;
                    }
                    if (fn2.mustBeSupplied() == null) {
                        continue;
                    }
                    for (final FocusNode.EnumSupplyType type : fn2.mustBeSupplied()) {
                        if (parent.node.canSupply(type)) {
                            switch (element.getType()) {
                                case EFFECT: {
                                    pEff.add(key);
                                    break;
                                }
                                case MEDIUM: {
                                    if (!hasExlusive && (!((FocusMedium)element).isExclusive() || !hasMedium)) {
                                        pMed.add(key);
                                        break;
                                    }
                                    break;
                                }
                                case MOD: {
                                    pMod.add(key);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            Collections.sort(pMed);
            Collections.sort(pEff);
            Collections.sort(pMod);
            this.shownParts.addAll(pMed);
            this.shownParts.addAll(pEff);
            this.shownParts.addAll(pMod);
        }
    }
    
    private void gatherInfo(final boolean sync) {
        this.buttonList.clear();
        this.buttonList.add(this.buttonConfirm);
        this.buttonConfirm.x = this.guiLeft + 242;
        this.buttonConfirm.y = this.guiTop + 18;
        this.buttonList.add(new GuiHoverButton(this, 1, this.guiLeft + 241, this.guiTop + 39, 32, 16, I18n.translateToLocal("wandtable.text5"), null, new ResourceLocation("thaumcraft", "textures/gui/complex.png")));
        this.buttonList.add(new GuiHoverButton(this, 2, this.guiLeft + 241, this.guiTop + 53, 32, 16, I18n.translateToLocal("wandtable.text2"), null, new ResourceLocation("thaumcraft", "textures/gui/costxp.png")));
        this.buttonList.add(new GuiHoverButton(this, 3, this.guiLeft + 241, this.guiTop + 67, 32, 16, I18n.translateToLocal("wandtable.text1"), null, new ResourceLocation("thaumcraft", "textures/gui/costvis.png")));
        final FocusElementNode fn = this.table.data.get(this.selectedNode);
        if (fn != null && fn.node != null && !fn.node.getSettingList().isEmpty()) {
            int a = 0;
            for (final String sk : fn.node.getSettingList()) {
                final NodeSetting setting = fn.node.getSetting(sk);
                if (setting.getResearch() != null && !ThaumcraftCapabilities.knowsResearchStrict(this.mc.player, setting.getResearch())) {
                    continue;
                }
                final int x = this.guiLeft + this.xSize;
                final int y = this.guiTop + this.ySize + 3 - fn.node.getSettingList().size() * 26 + a * 26;
                int w = 32;
                if (setting.getType() instanceof NodeSetting.NodeSettingIntList) {
                    w = 72;
                }
                this.buttonList.add(new GuiFocusSettingSpinnerButton(10 + a, x, y, w, setting));
                ++a;
            }
        }
        this.shownParts.clear();
        this.components = null;
        if (this.table.getStackInSlot(0) == null || this.table.getStackInSlot(0).isEmpty()) {
            return;
        }
        final HashMap<String, Integer> compCount = new HashMap<String, Integer>();
        this.totalComplexity = 0;
        this.maxComplexity = 0;
        if (this.inventorySlots.getSlot(0).getHasStack()) {
            final ItemStack is = this.inventorySlots.getSlot(0).getStack();
            if (is != null && !is.isEmpty() && is.getItem() instanceof ItemFocus) {
                this.maxComplexity = ((ItemFocus)is.getItem()).getMaxComplexity();
            }
        }
        boolean emptyNodes = false;
        final AspectList crystals = new AspectList();
        if (this.table.data != null && !this.table.data.isEmpty()) {
            for (final FocusElementNode node : this.table.data.values()) {
                if (node.node != null) {
                    int a2 = 0;
                    if (compCount.containsKey(node.node.getKey())) {
                        a2 = compCount.get(node.node.getKey());
                    }
                    ++a2;
                    node.complexityMultiplier = 0.5f * (a2 + 1);
                    compCount.put(node.node.getKey(), a2);
                    this.totalComplexity += (int)(node.node.getComplexity() * node.complexityMultiplier);
                    if (node.node.getAspect() == null) {
                        continue;
                    }
                    crystals.add(node.node.getAspect(), 1);
                }
                else {
                    emptyNodes = true;
                }
            }
        }
        this.costCast = this.totalComplexity / 5.0f;
        this.costVis = this.totalComplexity * 10 + this.maxComplexity / 5;
        this.costXp = (int)Math.min(1L, Math.round(Math.sqrt(this.totalComplexity)));
        boolean validCrystals = false;
        if (crystals.getAspects().length > 0) {
            validCrystals = true;
            this.components = new ItemStack[crystals.getAspects().length];
            int r = 0;
            for (final Aspect as : crystals.getAspects()) {
                this.components[r] = ThaumcraftApiHelper.makeCrystal(as, crystals.getAmount(as));
                ++r;
            }
            if (this.components.length >= 0) {
                final boolean[] owns = new boolean[this.components.length];
                for (int a3 = 0; a3 < this.components.length; ++a3) {
                    if (!(owns[a3] = InventoryUtils.isPlayerCarryingAmount(this.mc.player, this.components[a3], false))) {
                        validCrystals = false;
                    }
                }
            }
            if (this.components != null && this.components.length > 0) {
                int i = 0;
                int q = 0;
                int z = 0;
                for (final ItemStack stack : this.components) {
                    this.buttonList.add(new GuiHoverButton(this, 11 + z, this.guiLeft + 234 + i * 16, this.guiTop + 110 + 16 * q, 16, 16, stack.getDisplayName(), null, stack));
                    if (++i > 4) {
                        i = 0;
                        ++q;
                    }
                    ++z;
                }
            }
        }
        this.gatherPartsList();
        this.scrollbarParts = null;
        if (this.shownParts.size() > 6) {
            this.scrollbarParts = new GuiSliderTC(4, this.guiLeft + 51, this.guiTop + 30, 8, 149, "logistics.scrollbar", 0.0f, (float)(this.shownParts.size() - 6), 0.0f, true);
            this.buttonList.add(this.scrollbarParts);
        }
        this.valid = (this.totalComplexity <= this.maxComplexity && !emptyNodes && validCrystals && this.table.xpCost <= this.mc.player.experienceLevel);
        this.calcScrollBounds();
        if (this.scrollY > (this.sMaxY - 3) * 32) {
            this.scrollY = (this.sMaxY - 3) * 32;
        }
        if (this.scrollX > this.sMaxX * 24) {
            this.scrollX = this.sMaxX * 24;
        }
        if (this.scrollX < this.sMinX * 24) {
            this.scrollX = this.sMinX * 24;
        }
        this.scrollbarMainSide = null;
        this.scrollbarMainBottom = null;
        if (this.sMaxY * 32 > 130) {
            this.scrollbarMainSide = new GuiSliderTC(5, this.guiLeft + 203, this.guiTop + 32, 8, 156, "logistics.scrollbar", 0.0f, (float)((this.sMaxY - 3) * 32), (float)this.scrollY, true);
            this.buttonList.add(this.scrollbarMainSide);
        }
        else {
            this.scrollY = 0;
        }
        if (this.sMinX * 24 < -70 || this.sMaxX * 24 > 70) {
            this.scrollbarMainBottom = new GuiSliderTC(6, this.guiLeft + 64, this.guiTop + 195, 132, 8, "logistics.scrollbar", (float)(this.sMinX * 24), (float)(this.sMaxX * 24), (float)this.scrollX, false);
            this.buttonList.add(this.scrollbarMainBottom);
        }
        else {
            this.scrollX = 0;
        }
        if (this.table.focusName.isEmpty() && !this.table.getStackInSlot(0).isEmpty()) {
            this.table.focusName = this.table.getStackInSlot(0).getDisplayName();
        }
        if (this.nameField == null) {
            this.setupNameField();
        }
        this.nameField.setText(this.table.focusName);
        if (sync) {
            PacketHandler.INSTANCE.sendToServer(new PacketFocusNodesToServer(this.table.getPos(), this.table.data, this.table.focusName));
        }
    }
    
    static {
        GuiFocalManipulator.iMedium = new ResourceLocation("thaumcraft", "textures/foci/_medium.png");
        GuiFocalManipulator.iEffect = new ResourceLocation("thaumcraft", "textures/foci/_effect.png");
    }
    
    private class Bounds
    {
        int x;
        
        private Bounds() {
            this.x = 0;
        }
    }
}
