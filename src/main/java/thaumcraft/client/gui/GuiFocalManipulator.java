package thaumcraft.client.gui;
import java.awt.Color;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusMediumRoot;
import thaumcraft.api.casters.FocusModSplit;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.client.gui.plugins.GuiFocusSettingSpinnerButton;
import thaumcraft.client.gui.plugins.GuiHoverButton;
import thaumcraft.client.gui.plugins.GuiImageButton;
import thaumcraft.client.gui.plugins.GuiSliderTC;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.container.ContainerFocalManipulator;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketFocusNameToServer;
import thaumcraft.common.lib.network.playerdata.PacketFocusNodesToServer;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.crafting.FocusElementNode;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;


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
    
    public GuiFocalManipulator(InventoryPlayer par1InventoryPlayer, TileFocalManipulator table) {
        super(new ContainerFocalManipulator(par1InventoryPlayer, table));
        isMouseButtonDown = 0;
        mouseX = 0;
        mouseY = 0;
        tex = new ResourceLocation("thaumcraft", "textures/gui/gui_wandtable.png");
        tex2 = new ResourceLocation("thaumcraft", "textures/gui/gui_wandtable2.png");
        tex3 = new ResourceLocation("thaumcraft", "textures/gui/gui_wandtable3.png");
        texbase = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
        buttonConfirm = new GuiImageButton(this, 0, guiLeft + 232, guiTop + 7, 24, 16, null, I18n.translateToLocal("wandtable.text3"), texbase, 232, 240, 24, 16);
        totalComplexity = 0;
        maxComplexity = 0;
        lastNodeHover = -1;
        myFormatter = new DecimalFormat("#######.##");
        shownParts = new ArrayList<String>();
        partsStart = 0;
        components = null;
        valid = false;
        nodeID = 0;
        sMinX = 0;
        sMinY = 0;
        sMaxX = 0;
        sMaxY = 0;
        selectedNode = -1;
        costCast = 0.0f;
        costXp = 0;
        costVis = 0;
        scrollX = 0;
        scrollY = 0;
        this.table = table;
        xSize = 231;
        ySize = 231;
    }
    
    public void initGui() {
        super.initGui();
        gatherInfo(false);
        setupNameField();
        if (table.focusName.isEmpty() && !table.getStackInSlot(0).isEmpty()) {
            table.focusName = table.getStackInSlot(0).getDisplayName();
        }
        nameField.setText(table.focusName);
    }
    
    private void setupNameField() {
        Keyboard.enableRepeatEvents(true);
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;
        (nameField = new GuiTextField(2, fontRenderer, i + 30, j + 11, 170, 12)).setTextColor(-1);
        nameField.setDisabledTextColour(-1);
        nameField.setEnableBackgroundDrawing(false);
        nameField.setMaxStringLength(50);
    }
    
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0 && buttonConfirm != null && buttonConfirm.active) {
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, 0);
        }
        else if (button.id >= 10) {
            gatherInfo(true);
        }
        else {
            super.actionPerformed(button);
        }
    }
    
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (nameField.textboxKeyTyped(typedChar, keyCode)) {
            table.focusName = nameField.getText();
            PacketHandler.INSTANCE.sendToServer(new PacketFocusNameToServer(table.getPos(), table.focusName));
        }
        else {
            super.keyTyped(typedChar, keyCode);
        }
    }
    
    public void drawScreen(int mx, int my, float par3) {
        drawDefaultBackground();
        super.drawScreen(mx, my, par3);
        GlStateManager.enableBlend();
        GL11.glBlendFunc(770, 771);
        xSize_lo = (float)mx;
        ySize_lo = (float)my;
        int gx = (width - xSize) / 2;
        int gy = (height - ySize) / 2;
        if (Mouse.isButtonDown(0)) {
            if ((isMouseButtonDown == 0 || isMouseButtonDown == 1) && isPointInRegion(guiLeft + 63, guiTop + 31, 136, 160, mx + guiLeft, my + guiTop)) {
                if (isMouseButtonDown == 0) {
                    isMouseButtonDown = 1;
                }
                else {
                    boolean b = scrollbarParts != null && scrollbarParts.isMouseOver();
                    if (scrollbarMainBottom != null && !scrollbarMainBottom.isMouseOver() && !b) {
                        scrollX -= mx - mouseX;
                        curMouseX = scrollX;
                        if (scrollX > scrollbarMainBottom.getMax()) {
                            scrollX = (int) scrollbarMainBottom.getMax();
                        }
                        if (scrollX < scrollbarMainBottom.getMin()) {
                            scrollX = (int) scrollbarMainBottom.getMin();
                        }
                        scrollbarMainBottom.setSliderValue((float) scrollX, false);
                    }
                    if (scrollbarMainSide != null && !scrollbarMainSide.isMouseOver() && !b) {
                        scrollY -= my - mouseY;
                        curMouseY = scrollY;
                        if (scrollY > scrollbarMainSide.getMax()) {
                            scrollY = (int) scrollbarMainSide.getMax();
                        }
                        if (scrollY < scrollbarMainSide.getMin()) {
                            scrollY = (int) scrollbarMainSide.getMin();
                        }
                        scrollbarMainSide.setSliderValue((float) scrollY, false);
                    }
                }
            }
            mouseX = mx;
            mouseY = my;
        }
        else {
            isMouseButtonDown = 0;
        }
        RenderHelper.disableStandardItemLighting();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        int count = 0;
        int index = 0;
        for (String sk : shownParts) {
            if (++count - 1 < partsStart) {
                continue;
            }
            GL11.glTranslated(0.0, 0.0, 5.0);
            FocusNode node = (FocusNode)FocusEngine.getElement(sk);
            drawPart(node, gx + 38, 43 + gy + 25 * index, (node.getType() == IFocusElement.EnumUnitType.MOD) ? 24.0f : 32.0f, 220, isPointInRegion(gx + 38 - 10, 32 + gy + 24 * index, 20, 20, mx + guiLeft, my + guiTop));
            GL11.glTranslated(0.0, 0.0, -5.0);
            if (++index > 5) {
                break;
            }
        }
        count = 0;
        index = 0;
        for (String sk : shownParts) {
            if (++count - 1 < partsStart) {
                continue;
            }
            FocusNode node = (FocusNode)FocusEngine.getElement(sk);
            if (isPointInRegion(gx + 38 - 10, 33 + gy + 25 * index, 20, 20, mx + guiLeft, my + guiTop)) {
                List list = genPartText(node, -1);
                drawHoveringTextFixed(list, gx + 44, 46 + gy + 24 * index, fontRenderer, width - (guiLeft + xSize - 16));
            }
            if (++index > 5) {
                break;
            }
        }
        if (lastNodeHover >= 0) {
            FocusElementNode fn = table.data.get(lastNodeHover);
            if (fn != null && fn.node != null) {
                int xx = guiLeft + 140 - scrollX + fn.x * 24;
                int yy = guiTop + 50 - scrollY + fn.y * 32;
                List list = genPartText(fn.node, lastNodeHover);
                drawHoveringTextFixed(list, xx, yy, fontRenderer, width - (guiLeft + xSize - 16));
            }
        }
        buttonConfirm.active = (table.vis <= 0.0f && valid);
        GlStateManager.disableBlend();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (table.data != null && !table.data.isEmpty() && nameField != null) {
            nameField.drawTextBox();
        }
        renderHoveredToolTip(mx, my);
    }
    
    private List genPartText(FocusNode node, int idx) {
        List list = new ArrayList();
        if (node != null) {
            FocusElementNode placed = null;
            if (idx >= 0) {
                placed = table.data.get(idx);
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
                p = placed.getPower(table.data);
            }
            if (p != 1.0f) {
                list.add(TextFormatting.GOLD + I18n.translateToLocal("focuspart.eff") + ((p < 1.0f) ? TextFormatting.RED : TextFormatting.GREEN) + " x" + ItemStack.DECIMALFORMAT.format(p));
            }
            if (node instanceof FocusEffect) {
                FocusEffect fe = (FocusEffect)node;
                float d = fe.getDamageForDisplay((placed == null) ? 1.0f : placed.getPower(table.data));
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
    
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(tex2);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
        mc.renderEngine.bindTexture(tex3);
        drawTexturedModalRect(k - 71, l - 3, 0, 0, 71, 239);
        if (table.getStackInSlot(0) == null || table.getStackInSlot(0).isEmpty() || table.doGuiReset) {
            if (table.doGuiReset) {
                resetNodes();
            }
            else {
                table.data.clear();
                gatherInfo(false);
            }
            table.focusName = "";
            if (table.getStackInSlot(0) != null && !table.getStackInSlot(0).isEmpty()) {
                table.focusName = table.getStackInSlot(0).getDisplayName();
                nameField.setText(table.focusName);
            }
            table.doGuiReset = false;
        }
        if (table.doGather) {
            gatherInfo(false);
            table.doGather = false;
        }
        drawNodes(guiLeft + 132 - scrollX, guiTop + 48 - scrollY, par2, par3);
        GL11.glTranslated(0.0, 0.0, 1.0);
        mc.renderEngine.bindTexture(tex);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
        if (maxComplexity > 0) {
            fontRenderer.drawStringWithShadow(totalComplexity + "/" + maxComplexity, (float)(k + 242), (float)(l + 36), (totalComplexity > maxComplexity) ? 16151160 : 16761407);
        }
        fontRenderer.drawStringWithShadow("" + costXp, (float)(k + 242), (float)(l + 50), (costXp > mc.player.experienceLevel) ? 16151160 : 10092429);
        int v = costVis;
        if (table.vis > 0.0f) {
            v = (int) table.vis;
        }
        fontRenderer.drawStringWithShadow(TextFormatting.AQUA + "" + v, (float)(k + 242), (float)(l + 64), 10092429);
        if (costCast > 0.0f) {
            String cost = myFormatter.format(costCast);
            fontRenderer.drawStringWithShadow(TextFormatting.AQUA + I18n.translateToLocal("item.Focus.cost1") + ": " + cost, (float)(k + 230), (float)(l + 80), 10092429);
        }
        if (components != null && components.length > 0) {
            fontRenderer.drawStringWithShadow(TextFormatting.GOLD + I18n.translateToLocal("wandtable.text4"), (float)(k + 230), (float)(l + 92), 10092429);
        }
        if (table.focusName != null && !table.data.isEmpty()) {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            mc.renderEngine.bindTexture(texbase);
            drawTexturedModalRect(guiLeft + 24, guiTop + 8, 192, 224, 8, 14);
            int a;
            for (a = 1, a = 1; a < 22; ++a) {
                drawTexturedModalRect(guiLeft + 24 + a * 8, guiTop + 8, 200, 224, 8, 14);
            }
            drawTexturedModalRect(guiLeft + 24 + a * 8, guiTop + 8, 208, 224, 8, 14);
        }
    }
    
    private void drawClippedRect(int x, int y, int sx, int sy, int w, int h) {
        if (isPointInRegion(48, 26, 166, 174, x + w / 2, y + h / 2)) {
            drawTexturedModalRect(x, y, sx, sy, w, h);
        }
    }
    
    private void drawNodes(int x, int y, int mx, int my) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GlStateManager.disableLighting();
        if (table.data != null && !table.data.isEmpty()) {
            int hover = -1;
            for (FocusElementNode fn : table.data.values()) {
                int xx = x + fn.x * 24;
                int yy = y + fn.y * 32;
                boolean mouseover = isPointInRegion(guiLeft + 63, guiTop + 31, 136, 160, mx + guiLeft, my + guiTop) && isPointInRegion(xx - 10, yy - 10, 20, 20, mx + guiLeft, my + guiTop);
                if (mouseover && fn.parent >= 0) {
                    hover = fn.id;
                }
                if (fn.node != null) {
                    if (isPointInRegion(48, 16, 154, 192, xx - 8, yy - 8)) {
                        drawPart(fn.node, xx, yy, 32.0f, 220, mouseover);
                    }
                }
                else {
                    mc.renderEngine.bindTexture(tex);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    drawClippedRect(xx - 12, yy - 12, 120, 232, 24, 24);
                }
                if (selectedNode == fn.id || (mouseover && fn.parent >= 0)) {
                    mc.renderEngine.bindTexture(tex);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    drawClippedRect(xx - 12, yy - 12, 96, 232, 24, 24);
                }
                FocusElementNode parent = table.data.get(fn.parent);
                if (parent != null) {
                    mc.renderEngine.bindTexture(tex);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    drawClippedRect(xx - 6, yy - 22, 54, 232, 12, 12);
                    if (parent.node instanceof FocusModSplit) {
                        for (int q = Math.abs(fn.x - parent.x), a = 0; a < q; ++a) {
                            if (fn.x < parent.x) {
                                if (a == 0) {
                                    drawClippedRect(xx - 4, yy - 36, 8, 240, 16, 16);
                                }
                                else {
                                    drawClippedRect(xx - 12 + a * 24, yy - 36, 72, 240, 24, 16);
                                }
                            }
                            else if (a == 0) {
                                drawClippedRect(xx - 12, yy - 36, 24, 240, 16, 16);
                            }
                            else {
                                drawClippedRect(xx - 12 - a * 24, yy - 36, 72, 240, 24, 16);
                            }
                        }
                    }
                    if (fn.node != null) {
                        continue;
                    }
                    int s = (parent.target && parent.trajectory) ? 4 : 0;
                    if (!isPointInRegion(48, 16, 168, 192, xx - 4, yy - 4)) {
                        continue;
                    }
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    if (parent.target) {
                        GL11.glPushMatrix();
                        GL11.glTranslated(xx - s, yy, 0.0);
                        GL11.glScaled(0.5, 0.5, 0.5);
                        drawTexturedModalRect(-8, -8, 152, 240, 16, 16);
                        GL11.glPopMatrix();
                    }
                    if (!parent.trajectory) {
                        continue;
                    }
                    GL11.glPushMatrix();
                    GL11.glTranslated(xx + s, yy, 0.0);
                    GL11.glScaled(0.5, 0.5, 0.5);
                    drawTexturedModalRect(-8, -8, 168, 240, 16, 16);
                    GL11.glPopMatrix();
                }
            }
            if (hover >= 0 && lastNodeHover != hover) {
                playRollover();
                lastNodeHover = hover;
            }
            if (hover < 0) {
                lastNodeHover = -1;
            }
            if (selectedNode >= 0) {
                drawNodeSettings(selectedNode);
            }
        }
        GlStateManager.enableLighting();
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
    }
    
    private void drawNodeSettings(int selectedNode2) {
        FocusElementNode fn = table.data.get(selectedNode2);
        if (fn != null && fn.node != null && !fn.node.getSettingList().isEmpty()) {
            int a = 0;
            for (String sk : fn.node.getSettingList()) {
                NodeSetting setting = fn.node.getSetting(sk);
                if (setting.getResearch() != null && !ThaumcraftCapabilities.knowsResearchStrict(mc.player, setting.getResearch())) {
                    continue;
                }
                int x = guiLeft + xSize;
                int y = guiTop + ySize - 10 - fn.node.getSettingList().size() * 26 + a * 26;
                fontRenderer.drawStringWithShadow(TextFormatting.GOLD + setting.getLocalizedName(), (float)x, (float)y, 16777215);
                ++a;
            }
        }
    }
    
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int k = Mouse.getDWheel();
        int i = Mouse.getEventX() * width / mc.displayWidth;
        int j = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        if (scrollbarParts != null && shownParts.size() > 6 && isPointInRegion(24, 24, 32, 157, i, j)) {
            if (k > 0) {
                if (partsStart > 0) {
                    --partsStart;
                    scrollbarParts.setSliderValue((float) partsStart, false);
                }
            }
            else if (k < 0 && partsStart < shownParts.size() - 6) {
                ++partsStart;
                scrollbarParts.setSliderValue((float) partsStart, false);
            }
        }
    }
    
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        RenderHelper.disableStandardItemLighting();
        for (GuiButton guibutton : buttonList) {
            if (guibutton.isMouseOver()) {
                guibutton.drawButtonForegroundLayer(mouseX - guiLeft, mouseY - guiTop);
                break;
            }
        }
        RenderHelper.enableGUIStandardItemLighting();
        if (scrollbarMainSide != null) {
            int sv = Math.round(scrollbarMainSide.getSliderValue());
            if (sv != scrollY) {
                scrollY = sv;
            }
        }
        if (scrollbarMainBottom != null) {
            int sv = Math.round(scrollbarMainBottom.getSliderValue());
            if (sv != scrollX) {
                scrollX = sv;
            }
        }
        if (scrollbarParts != null) {
            int sv = Math.round(scrollbarParts.getSliderValue());
            if (sv != partsStart) {
                partsStart = sv;
            }
        }
    }
    
    protected void mouseClicked(int mx, int my, int par3) {
        try {
            super.mouseClicked(mx, my, par3);
        }
        catch (IOException ex) {}
        int gx = (width - xSize) / 2;
        int gy = (height - ySize) / 2;
        if (table.vis <= 0.0f && table.data != null && !table.data.isEmpty()) {
            if (nameField != null) {
                nameField.mouseClicked(mx, my, par3);
            }
            if (lastNodeHover >= 0) {
                selectedNode = lastNodeHover;
                boolean r = false;
                if (par3 == 1 && table.data.get(selectedNode).node != null) {
                    FocusElementNode fn = table.data.get(selectedNode);
                    if (table.data.get(fn.parent).node != null) {
                        addNodeAt(table.data.get(fn.parent).node.getClass(), fn.parent, true);
                    }
                }
                gatherInfo(false);
                playButtonClick();
            }
            int count = 0;
            int index = 0;
            if (selectedNode >= 0) {
                for (String sk : shownParts) {
                    if (++count - 1 < partsStart) {
                        continue;
                    }
                    if (isPointInRegion(gx + 28, gy + 32 + 24 * index, 20, 20, mx + guiLeft, my + guiTop)) {
                        addNodeAt(FocusEngine.elements.get(sk), selectedNode, true);
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
        mc.getRenderViewEntity().playSound(SoundsTC.clack, 0.4f, 1.0f);
    }
    
    private void playRollover() {
        mc.getRenderViewEntity().playSound(SoundsTC.clack, 0.4f, 2.0f);
    }
    
    private void playSocketSound(float pitch) {
        mc.getRenderViewEntity().playSound(SoundsTC.crystal, 0.4f, pitch);
    }
    
    protected void drawHoveringTextFixed(List listin, int x, int y, FontRenderer font, int textWidth) {
        if (!listin.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            List<String> list = new ArrayList<>();
            for (Object o : listin) {
                String s = (String)o;
                s = trimStringNewline(s);
                List<String> list2 = font.listFormattedStringToWidth(s, textWidth);
                for (String s2 : list2) {
                    list.add(s2);
                }
            }
            int k = 0;
            for (String s : list) {
                int l = font.getStringWidth(s);
                if (l > k) {
                    k = l;
                }
            }
            int j2 = x + 12;
            int k2 = y - 12;
            int i1 = 8;
            if (list.size() > 1) {
                i1 += 2 + (list.size() - 1) * 10;
            }
            zLevel = 300.0f;
            itemRender.zLevel = 300.0f;
            int j3 = -267386864;
            drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j3, j3);
            drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j3, j3);
            drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j3, j3);
            drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j3, j3);
            drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j3, j3);
            int k3 = 1347420415;
            int l2 = (k3 & 0xFEFEFE) >> 1 | (k3 & 0xFF000000);
            drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k3, l2);
            drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k3, l2);
            drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k3, k3);
            drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l2, l2);
            for (int i2 = 0; i2 < list.size(); ++i2) {
                String s3 = list.get(i2);
                font.drawStringWithShadow(s3, (float)j2, (float)k2, -1);
                if (i2 == 0) {
                    k2 += 2;
                }
                k2 += 10;
            }
            zLevel = 0.0f;
            itemRender.zLevel = 0.0f;
            GlStateManager.enableRescaleNormal();
        }
    }
    
    private String trimStringNewline(String text) {
        while (text != null && text.endsWith("\n")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }
    
    public static void drawPart(FocusNode node, int x, int y, float scale, int bright, boolean mouseover) {
        GL11.glPushMatrix();
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glTranslated(x, y, 0.0);
        GL11.glRotatef(90.0f, 0.0f, 0.0f, -1.0f);
        boolean root = node.getType() == IFocusElement.EnumUnitType.MOD || node.getKey().equals("thaumcraft.ROOT");
        if (root) {
            scale *= 2.0f;
        }
        Color color = new Color(FocusEngine.getElementColor(node.getKey()));
        float r = color.getRed() / 255.0f;
        float g = color.getGreen() / 255.0f;
        float b = color.getBlue() / 255.0f;
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
        while (table.data.containsKey(nodeID)) {
            ++nodeID;
        }
        return nodeID;
    }
    
    private void cullChildren(int idx) {
        if (table.data.containsKey(idx)) {
            for (int i : table.data.get(idx).children) {
                cullChildren(i);
                table.data.remove(i);
            }
        }
    }
    
    private ArrayList<Integer> addNodeAt(Class nodeClass, int idx, boolean gather) {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        boolean same = false;
        FocusElementNode previous = null;
        if (table.data.containsKey(idx)) {
            cullChildren(idx);
            if (table.data.get(idx).node != null && table.data.get(idx).node.getClass() == nodeClass) {
                same = true;
            }
            else {
                previous = table.data.remove(idx);
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
                fn.id = getNextId();
                ret.add(fn.id);
                selectedNode = fn.id;
                if (previous != null && table.data.containsKey(previous.parent)) {
                    fn.parent = previous.parent;
                    int[] c = table.data.get(previous.parent).children;
                    for (int a = 0; a < c.length; ++a) {
                        if (c[a] == previous.id) {
                            table.data.get(previous.parent).children[a] = fn.id;
                            break;
                        }
                    }
                }
                fn.target = node.canSupply(FocusNode.EnumSupplyType.TARGET);
                fn.trajectory = node.canSupply(FocusNode.EnumSupplyType.TRAJECTORY);
                table.data.put(nodeID, fn);
            }
            else {
                fn = table.data.get(idx);
                node = fn.node;
            }
            if (fn.target || fn.trajectory) {
                if (node instanceof FocusModSplit) {
                    FocusElementNode blank1 = new FocusElementNode();
                    blank1.parent = fn.id;
                    blank1.id = getNextId();
                    ret.add(nodeID);
                    blank1.x = fn.x - 1;
                    blank1.y = fn.y + 1;
                    table.data.put(nodeID, blank1);
                    selectedNode = nodeID;
                    FocusElementNode blank2 = new FocusElementNode();
                    blank2.parent = fn.id;
                    blank2.x = fn.x + 1;
                    blank2.y = fn.y + 1;
                    blank2.id = getNextId();
                    ret.add(nodeID);
                    table.data.put(nodeID, blank2);
                    fn.children = new int[] { blank1.id, blank2.id };
                }
                else {
                    FocusElementNode blank3 = new FocusElementNode();
                    blank3.parent = fn.id;
                    blank3.x = fn.x;
                    blank3.y = fn.y + 1;
                    blank3.id = getNextId();
                    ret.add(nodeID);
                    table.data.put(nodeID, blank3);
                    fn.children = new int[] { blank3.id };
                    selectedNode = nodeID;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (gather) {
            calcNodeTreeLayout();
            gatherInfo(true);
        }
        return ret;
    }
    
    private void processLeftNodes(FocusElementNode start, Bounds bounds) {
        if (start.children.length > 0) {
            processLeftNodes(table.data.get(start.children[0]), bounds);
        }
        int ox = 0;
        if (start.children.length == 1) {
            ox = bounds.x - 1;
            bounds.x = table.data.get(start.children[0]).x;
        }
        start.x = bounds.x;
        if (start.children.length == 1) {
            bounds.x = ox;
        }
        ++bounds.x;
        if (start.children.length > 1) {
            processLeftNodes(table.data.get(start.children[1]), bounds);
        }
    }
    
    private void centerNodes(int start, Bounds bounds) {
        int x = bounds.x / 2;
        FocusElementNode sn = table.data.get(start);
        moveNodes(sn, x);
    }
    
    private void moveNodes(FocusElementNode start, int amt) {
        for (int ci : start.children) {
            moveNodes(table.data.get(ci), amt);
        }
        start.x -= amt;
    }
    
    private void calcNodeTreeLayout() {
        int fsi = -1;
        for (FocusElementNode node : table.data.values()) {
            if (fsi < 0 && node.node != null && node.node instanceof FocusModSplit) {
                fsi = node.id;
            }
        }
        if (fsi >= 0) {
            Bounds bounds = new Bounds();
            processLeftNodes(table.data.get(fsi), bounds);
            centerNodes(fsi, bounds);
        }
        for (FocusElementNode node : table.data.values()) {
            if (node.node != null && node.node instanceof FocusModSplit) {
                if (table.data.containsKey(node.parent) && table.data.get(node.parent).node != null && !(table.data.get(node.parent).node instanceof FocusModSplit)) {
                    node.x = table.data.get(node.parent).x;
                }
                else {
                    int xx = 0;
                    for (int a : node.children) {
                        xx += table.data.get(a).x;
                    }
                    xx /= node.children.length;
                    node.x = xx;
                }
            }
        }
        if (selectedNode >= 0 && !table.data.containsKey(selectedNode)) {
            selectedNode = -1;
        }
    }
    
    private void resetNodes() {
        nodeID = 0;
        table.data.clear();
        ArrayList<Integer> r1 = addNodeAt(FocusMediumRoot.class, 0, false);
        selectedNode = r1.get(1);
        calcNodeTreeLayout();
        gatherInfo(true);
    }
    
    private void calcScrollBounds() {
        sMinX = 0;
        sMinY = 0;
        sMaxX = 0;
        sMaxY = 0;
        for (FocusElementNode fn : table.data.values()) {
            if (fn.x < sMinX) {
                sMinX = fn.x;
            }
            if (fn.y < sMinY) {
                sMinY = fn.y;
            }
            if (fn.x > sMaxX) {
                sMaxX = fn.x;
            }
            if (fn.y > sMaxY) {
                sMaxY = fn.y;
            }
        }
    }
    
    private void gatherPartsList() {
        shownParts.clear();
        if (mc == null) {
            return;
        }
        if (selectedNode >= 0 && table.data.containsKey(selectedNode)) {
            partsStart = 0;
            ArrayList<String> pMed = new ArrayList<String>();
            ArrayList<String> pEff = new ArrayList<String>();
            ArrayList<String> pMod = new ArrayList<String>();
            ArrayList<String> excluded = new ArrayList<String>();
            boolean hasExlusive = false;
            boolean hasMedium = false;
            for (FocusElementNode fn : table.data.values()) {
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
            FocusElementNode node = table.data.get(selectedNode);
            FocusElementNode parent = table.data.get(node.parent);
            if (parent != null && parent.node != null) {
                for (String key : FocusEngine.elements.keySet()) {
                    IFocusElement element = FocusEngine.getElement(key);
                    if (!ThaumcraftCapabilities.knowsResearchStrict(mc.player, element.getResearch())) {
                        continue;
                    }
                    if (element.getKey().equals("thaumcraft.ROOT")) {
                        continue;
                    }
                    if (!(element instanceof FocusNode)) {
                        continue;
                    }
                    FocusNode fn2 = (FocusNode)element;
                    if (excluded.contains(fn2.getKey())) {
                        continue;
                    }
                    if (fn2.mustBeSupplied() == null) {
                        continue;
                    }
                    for (FocusNode.EnumSupplyType type : fn2.mustBeSupplied()) {
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
            shownParts.addAll(pMed);
            shownParts.addAll(pEff);
            shownParts.addAll(pMod);
        }
    }
    
    private void gatherInfo(boolean sync) {
        buttonList.clear();
        buttonList.add(buttonConfirm);
        buttonConfirm.x = guiLeft + 242;
        buttonConfirm.y = guiTop + 18;
        buttonList.add(new GuiHoverButton(this, 1, guiLeft + 241, guiTop + 39, 32, 16, I18n.translateToLocal("wandtable.text5"), null, new ResourceLocation("thaumcraft", "textures/gui/complex.png")));
        buttonList.add(new GuiHoverButton(this, 2, guiLeft + 241, guiTop + 53, 32, 16, I18n.translateToLocal("wandtable.text2"), null, new ResourceLocation("thaumcraft", "textures/gui/costxp.png")));
        buttonList.add(new GuiHoverButton(this, 3, guiLeft + 241, guiTop + 67, 32, 16, I18n.translateToLocal("wandtable.text1"), null, new ResourceLocation("thaumcraft", "textures/gui/costvis.png")));
        FocusElementNode fn = table.data.get(selectedNode);
        if (fn != null && fn.node != null && !fn.node.getSettingList().isEmpty()) {
            int a = 0;
            for (String sk : fn.node.getSettingList()) {
                NodeSetting setting = fn.node.getSetting(sk);
                if (setting.getResearch() != null && !ThaumcraftCapabilities.knowsResearchStrict(mc.player, setting.getResearch())) {
                    continue;
                }
                int x = guiLeft + xSize;
                int y = guiTop + ySize + 3 - fn.node.getSettingList().size() * 26 + a * 26;
                int w = 32;
                if (setting.getType() instanceof NodeSetting.NodeSettingIntList) {
                    w = 72;
                }
                buttonList.add(new GuiFocusSettingSpinnerButton(10 + a, x, y, w, setting));
                ++a;
            }
        }
        shownParts.clear();
        components = null;
        if (table.getStackInSlot(0) == null || table.getStackInSlot(0).isEmpty()) {
            return;
        }
        HashMap<String, Integer> compCount = new HashMap<String, Integer>();
        totalComplexity = 0;
        maxComplexity = 0;
        if (inventorySlots.getSlot(0).getHasStack()) {
            ItemStack is = inventorySlots.getSlot(0).getStack();
            if (is != null && !is.isEmpty() && is.getItem() instanceof ItemFocus) {
                maxComplexity = ((ItemFocus)is.getItem()).getMaxComplexity();
            }
        }
        boolean emptyNodes = false;
        AspectList crystals = new AspectList();
        if (table.data != null && !table.data.isEmpty()) {
            for (FocusElementNode node : table.data.values()) {
                if (node.node != null) {
                    int a2 = 0;
                    if (compCount.containsKey(node.node.getKey())) {
                        a2 = compCount.get(node.node.getKey());
                    }
                    ++a2;
                    node.complexityMultiplier = 0.5f * (a2 + 1);
                    compCount.put(node.node.getKey(), a2);
                    totalComplexity += (int)(node.node.getComplexity() * node.complexityMultiplier);
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
        costCast = totalComplexity / 5.0f;
        costVis = totalComplexity * 10 + maxComplexity / 5;
        costXp = (int)Math.min(1L, Math.round(Math.sqrt(totalComplexity)));
        boolean validCrystals = false;
        if (crystals.getAspects().length > 0) {
            validCrystals = true;
            components = new ItemStack[crystals.getAspects().length];
            int r = 0;
            for (Aspect as : crystals.getAspects()) {
                components[r] = ThaumcraftApiHelper.makeCrystal(as, crystals.getAmount(as));
                ++r;
            }
            if (components.length >= 0) {
                boolean[] owns = new boolean[components.length];
                for (int a3 = 0; a3 < components.length; ++a3) {
                    if (!(owns[a3] = InventoryUtils.isPlayerCarryingAmount(mc.player, components[a3], false))) {
                        validCrystals = false;
                    }
                }
            }
            if (components != null && components.length > 0) {
                int i = 0;
                int q = 0;
                int z = 0;
                for (ItemStack stack : components) {
                    buttonList.add(new GuiHoverButton(this, 11 + z, guiLeft + 234 + i * 16, guiTop + 110 + 16 * q, 16, 16, stack.getDisplayName(), null, stack));
                    if (++i > 4) {
                        i = 0;
                        ++q;
                    }
                    ++z;
                }
            }
        }
        gatherPartsList();
        scrollbarParts = null;
        if (shownParts.size() > 6) {
            scrollbarParts = new GuiSliderTC(4, guiLeft + 51, guiTop + 30, 8, 149, "logistics.scrollbar", 0.0f, (float)(shownParts.size() - 6), 0.0f, true);
            buttonList.add(scrollbarParts);
        }
        valid = (totalComplexity <= maxComplexity && !emptyNodes && validCrystals && table.xpCost <= mc.player.experienceLevel);
        calcScrollBounds();
        if (scrollY > (sMaxY - 3) * 32) {
            scrollY = (sMaxY - 3) * 32;
        }
        if (scrollX > sMaxX * 24) {
            scrollX = sMaxX * 24;
        }
        if (scrollX < sMinX * 24) {
            scrollX = sMinX * 24;
        }
        scrollbarMainSide = null;
        scrollbarMainBottom = null;
        if (sMaxY * 32 > 130) {
            scrollbarMainSide = new GuiSliderTC(5, guiLeft + 203, guiTop + 32, 8, 156, "logistics.scrollbar", 0.0f, (float)((sMaxY - 3) * 32), (float) scrollY, true);
            buttonList.add(scrollbarMainSide);
        }
        else {
            scrollY = 0;
        }
        if (sMinX * 24 < -70 || sMaxX * 24 > 70) {
            scrollbarMainBottom = new GuiSliderTC(6, guiLeft + 64, guiTop + 195, 132, 8, "logistics.scrollbar", (float)(sMinX * 24), (float)(sMaxX * 24), (float) scrollX, false);
            buttonList.add(scrollbarMainBottom);
        }
        else {
            scrollX = 0;
        }
        if (table.focusName.isEmpty() && !table.getStackInSlot(0).isEmpty()) {
            table.focusName = table.getStackInSlot(0).getDisplayName();
        }
        if (nameField == null) {
            setupNameField();
        }
        nameField.setText(table.focusName);
        if (sync) {
            PacketHandler.INSTANCE.sendToServer(new PacketFocusNodesToServer(table.getPos(), table.data, table.focusName));
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
            x = 0;
        }
    }
}
