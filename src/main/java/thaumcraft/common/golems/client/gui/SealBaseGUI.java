package thaumcraft.common.golems.client.gui;
import java.awt.Color;
import java.io.IOException;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.client.gui.plugins.GuiHoverButton;
import thaumcraft.client.gui.plugins.GuiPlusMinusButton;
import thaumcraft.client.lib.CustomRenderItem;


@SideOnly(Side.CLIENT)
public class SealBaseGUI extends GuiContainer
{
    ISealEntity seal;
    int middleX;
    int middleY;
    int category;
    int[] categories;
    ResourceLocation tex;
    
    public SealBaseGUI(InventoryPlayer player, World world, ISealEntity seal) {
        super(new SealBaseContainer(player, world, seal));
        category = -1;
        tex = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
        this.seal = seal;
        xSize = 176;
        ySize = 232;
        middleX = xSize / 2;
        middleY = (ySize - 72) / 2 - 8;
        if (seal.getSeal() instanceof ISealGui) {
            categories = ((ISealGui)seal.getSeal()).getGuiCategories();
        }
        else {
            categories = new int[] { 0, 4 };
        }
    }
    
    private ModelManager getModelmanager() {
        return Minecraft.getMinecraft().modelManager;
    }
    
    public void initGui() {
        super.initGui();
        itemRender = new CustomRenderItem();
        setupCategories();
    }
    
    private void setupCategories() {
        buttonList.clear();
        int c = 0;
        float slice = 60.0f / categories.length;
        float start = -180.0f + (categories.length - 1) * slice / 2.0f;
        if (slice > 24.0f) {
            slice = 24.0f;
        }
        if (slice < 12.0f) {
            slice = 12.0f;
        }
        for (int cat : categories) {
            if (category < 0) {
                category = cat;
            }
            if (categories.length > 1) {
                int xx = (int)(MathHelper.cos((start - c * slice) / 180.0f * 3.1415927f) * 86.0f);
                int yy = (int)(MathHelper.sin((start - c * slice) / 180.0f * 3.1415927f) * 86.0f);
                buttonList.add(new GuiGolemCategoryButton(c, guiLeft + middleX + xx, guiTop + middleY + yy, 16, 16, "button.category." + cat, cat, category == cat));
            }
            ++c;
        }
        int xx2 = (int)(MathHelper.cos((start - c * slice) / 180.0f * 3.1415927f) * 86.0f);
        int yy2 = (int)(MathHelper.sin((start - c * slice) / 180.0f * 3.1415927f) * 86.0f);
        buttonList.add(new GuiGolemRedstoneButton(27, guiLeft + middleX + xx2 - 8, guiTop + middleY + yy2 - 8, 16, 16, seal));
        switch (category) {
            case 0: {
                buttonList.add(new GuiPlusMinusButton(80, guiLeft + middleX - 5 - 14, guiTop + middleY - 17, 10, 10, true));
                buttonList.add(new GuiPlusMinusButton(81, guiLeft + middleX - 5 + 14, guiTop + middleY - 17, 10, 10, false));
                buttonList.add(new GuiPlusMinusButton(82, guiLeft + middleX + 18 - 12, guiTop + middleY + 4, 10, 10, true));
                buttonList.add(new GuiPlusMinusButton(83, guiLeft + middleX + 18 + 11, guiTop + middleY + 4, 10, 10, false));
                buttonList.add(new GuiGolemLockButton(25, guiLeft + middleX - 32, guiTop + middleY, 16, 16, seal));
                break;
            }
            case 1: {
                if (seal.getSeal() instanceof ISealConfigFilter) {
                    int s = ((ISealConfigFilter) seal.getSeal()).getFilterSize();
                    int sy = 16 + (s - 1) / 3 * 12;
                    buttonList.add(new GuiGolemBWListButton(20, guiLeft + middleX - 8, guiTop + middleY + (s - 1) / 3 * 24 - sy + 27, 16, 16, (ISealConfigFilter) seal.getSeal()));
                    break;
                }
                break;
            }
            case 2: {
                buttonList.add(new GuiPlusMinusButton(90, guiLeft + middleX - 5 - 14, guiTop + middleY - 25, 10, 10, true));
                buttonList.add(new GuiPlusMinusButton(91, guiLeft + middleX - 5 + 14, guiTop + middleY - 25, 10, 10, false));
                buttonList.add(new GuiPlusMinusButton(92, guiLeft + middleX - 5 - 14, guiTop + middleY, 10, 10, true));
                buttonList.add(new GuiPlusMinusButton(93, guiLeft + middleX - 5 + 14, guiTop + middleY, 10, 10, false));
                buttonList.add(new GuiPlusMinusButton(94, guiLeft + middleX - 5 - 14, guiTop + middleY + 25, 10, 10, true));
                buttonList.add(new GuiPlusMinusButton(95, guiLeft + middleX - 5 + 14, guiTop + middleY + 25, 10, 10, false));
                break;
            }
            case 3: {
                if (seal.getSeal() instanceof ISealConfigToggles) {
                    ISealConfigToggles cp = (ISealConfigToggles) seal.getSeal();
                    int s2 = (cp.getToggles().length < 4) ? 8 : ((cp.getToggles().length < 6) ? 7 : ((cp.getToggles().length < 9) ? 6 : 5));
                    int h = (cp.getToggles().length - 1) * s2;
                    int w = 12;
                    for (ISealConfigToggles.SealToggle prop : cp.getToggles()) {
                        int ww = 12 + Math.min(100, fontRenderer.getStringWidth(I18n.translateToLocal(prop.getName())));
                        ww /= 2;
                        if (ww > w) {
                            w = ww;
                        }
                    }
                    int p = 0;
                    for (ISealConfigToggles.SealToggle prop2 : cp.getToggles()) {
                        buttonList.add(new GuiGolemPropButton(30 + p, guiLeft + middleX - w, guiTop + middleY - 5 - h + p * (s2 * 2), 8, 8, prop2.getName(), prop2));
                        ++p;
                    }
                    break;
                }
                break;
            }
            case 4: {
                EnumGolemTrait[] tags = seal.getSeal().getRequiredTags();
                if (tags != null && tags.length > 0) {
                    int p2 = 0;
                    for (EnumGolemTrait tag : tags) {
                        buttonList.add(new GuiHoverButton(this, 500 + p2, guiLeft + middleX + p2 * 18 - (tags.length - 1) * 9, guiTop + middleY - 8, 16, 16, tag.getLocalizedName(), tag.getLocalizedDescription(), tag.icon));
                        ++p2;
                    }
                }
                tags = seal.getSeal().getForbiddenTags();
                if (tags != null && tags.length > 0) {
                    int p2 = 0;
                    for (EnumGolemTrait tag : tags) {
                        buttonList.add(new GuiHoverButton(this, 600 + p2, guiLeft + middleX + p2 * 18 - (tags.length - 1) * 9, guiTop + middleY + 24, 16, 16, tag.getLocalizedName(), tag.getLocalizedDescription(), tag.icon));
                        ++p2;
                    }
                    break;
                }
                break;
            }
        }
    }
    
    protected boolean checkHotbarKeys(int par1) {
        return false;
    }
    
    protected void drawGuiContainerBackgroundLayer(float par1, int mouseX, int mouseY) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        mc.renderEngine.bindTexture(tex);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        drawTexturedModalRect(guiLeft + middleX - 80, guiTop + middleY - 80, 96, 0, 160, 160);
        drawTexturedModalRect(guiLeft, guiTop + 143, 0, 167, 176, 89);
        drawCenteredString(fontRenderer, I18n.translateToLocal("button.category." + category), guiLeft + middleX, guiTop + middleY - 64, 16777215);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        mc.renderEngine.bindTexture(tex);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        switch (category) {
            case 0: {
                drawTexturedModalRect(guiLeft + middleX + 17, guiTop + middleY + 3, 2, 18, 12, 12);
                if (seal.getColor() >= 1 && seal.getColor() <= 16) {
                    Color c = new Color(EnumDyeColor.byMetadata(seal.getColor() - 1).getColorValue());
                    GL11.glColor4f(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 1.0f);
                    drawTexturedModalRect(guiLeft + middleX + 20, guiTop + middleY + 6, 74, 31, 6, 6);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
                int mx = mouseX - guiLeft;
                int my = mouseY - guiTop;
                if (mx >= middleX + 5 && mx <= middleX + 41 && my >= middleY + 3 && my <= middleY + 15) {
                    if (seal.getColor() >= 1 && seal.getColor() <= 16) {
                        String s = "color." + EnumDyeColor.byMetadata(seal.getColor() - 1).getName();
                        String s2 = I18n.translateToLocal("golem.prop.color");
                        s2 = s2.replace("%s", I18n.translateToLocal(s));
                        drawCenteredString(fontRenderer, s2, guiLeft + middleX + 23, guiTop + middleY + 17, 16777215);
                    }
                    else {
                        drawCenteredString(fontRenderer, I18n.translateToLocal("golem.prop.colorall"), guiLeft + middleX + 23, guiTop + middleY + 17, 16777215);
                    }
                }
                drawCenteredString(fontRenderer, I18n.translateToLocal("golem.prop.priority"), guiLeft + middleX, guiTop + middleY - 28, 12299007);
                drawCenteredString(fontRenderer, "" + seal.getPriority(), guiLeft + middleX, guiTop + middleY - 16, 16777215);
                if (seal.getOwner().equals(mc.player.getUniqueID().toString())) {
                    drawCenteredString(fontRenderer, I18n.translateToLocal("golem.prop.owner"), guiLeft + middleX, guiTop + middleY + 32, 12299007);
                    break;
                }
                break;
            }
            case 1: {
                if (seal.getSeal() instanceof ISealConfigFilter) {
                    int s3 = ((ISealConfigFilter) seal.getSeal()).getFilterSize();
                    int sx = 16 + (s3 - 1) % 3 * 12;
                    int sy = 16 + (s3 - 1) / 3 * 12;
                    for (int a = 0; a < s3; ++a) {
                        int x = a % 3;
                        int y = a / 3;
                        drawTexturedModalRect(guiLeft + middleX + x * 24 - sx, guiTop + middleY + y * 24 - sy, 0, 56, 32, 32);
                    }
                    break;
                }
                break;
            }
            case 2: {
                drawCenteredString(fontRenderer, I18n.translateToLocal("button.caption.y"), guiLeft + middleX, guiTop + middleY - 24 - 9, 14540253);
                drawCenteredString(fontRenderer, I18n.translateToLocal("button.caption.x"), guiLeft + middleX, guiTop + middleY - 9, 14540253);
                drawCenteredString(fontRenderer, I18n.translateToLocal("button.caption.z"), guiLeft + middleX, guiTop + middleY + 24 - 9, 14540253);
                drawCenteredString(fontRenderer, "" + seal.getArea().getY(), guiLeft + middleX, guiTop + middleY - 24, 16777215);
                drawCenteredString(fontRenderer, "" + seal.getArea().getX(), guiLeft + middleX, guiTop + middleY, 16777215);
                drawCenteredString(fontRenderer, "" + seal.getArea().getZ(), guiLeft + middleX, guiTop + middleY + 24, 16777215);
                break;
            }
            case 4: {
                drawCenteredString(fontRenderer, I18n.translateToLocal("button.caption.required"), guiLeft + middleX, guiTop + middleY - 26, 14540253);
                drawCenteredString(fontRenderer, I18n.translateToLocal("button.caption.forbidden"), guiLeft + middleX, guiTop + middleY + 6, 14540253);
                break;
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
    }
    
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id < categories.length && categories[button.id] != category) {
            category = categories[button.id];
            ((SealBaseContainer) inventorySlots).category = category;
            ((SealBaseContainer) inventorySlots).setupCategories();
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, button.id);
            setupCategories();
        }
        else if (category == 0 && button.id == 25 && seal.getOwner().equals(mc.player.getUniqueID().toString())) {
            seal.setLocked(!seal.isLocked());
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, seal.isLocked() ? 25 : 26);
        }
        else if (category == 1 && seal.getSeal() instanceof ISealConfigFilter && button.id == 20) {
            ISealConfigFilter cp = (ISealConfigFilter) seal.getSeal();
            cp.setBlacklist(!cp.isBlacklist());
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, cp.isBlacklist() ? 20 : 21);
        }
        else if (category == 3 && seal.getSeal() instanceof ISealConfigToggles && button.id >= 30 && button.id < 30 + ((ISealConfigToggles) seal.getSeal()).getToggles().length) {
            ISealConfigToggles cp2 = (ISealConfigToggles) seal.getSeal();
            cp2.setToggle(button.id - 30, !cp2.getToggles()[button.id - 30].getValue());
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, cp2.getToggles()[button.id - 30].getValue() ? button.id : (button.id + 30));
        }
        else if (button.id == 27 && seal.getOwner().equals(mc.player.getUniqueID().toString())) {
            seal.setRedstoneSensitive(!seal.isRedstoneSensitive());
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, seal.isRedstoneSensitive() ? 27 : 28);
        }
        else {
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, button.id);
        }
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (category == 1 && seal.getSeal() instanceof ISealConfigFilter && !((ISealConfigFilter) seal.getSeal()).isBlacklist()) {
            int x = guiLeft;
            int y = guiTop;
            ISealConfigFilter cp = (ISealConfigFilter) seal.getSeal();
            int k = 240;
            int l = 240;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            zLevel = 100.0f;
            for (int i1 = 0; i1 < cp.getFilterSize(); ++i1) {
                Slot slot = inventorySlots.inventorySlots.get(i1);
                if (slot.isEnabled() && !slot.getStack().isEmpty()) {
                    int j = slot.xPos;
                    int m = slot.yPos;
                    int sz = cp.getFilterSlotSize(i1);
                    String s = String.valueOf(cp.getFilterSlotSize(i1));
                    if (sz == 0) {
                        s = TextFormatting.GOLD.toString() + "*";
                    }
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    GlStateManager.disableBlend();
                    fontRenderer.drawStringWithShadow(s, (float)(x + j + 19 - 2 - fontRenderer.getStringWidth(s)), (float)(y + m + 6 + 3), 16777215);
                    GlStateManager.enableLighting();
                    GlStateManager.enableDepth();
                    GlStateManager.enableBlend();
                }
            }
            zLevel = 0.0f;
            RenderHelper.disableStandardItemLighting();
            RenderHelper.enableGUIStandardItemLighting();
            RenderHelper.enableStandardItemLighting();
        }
    }
}
