package thaumcraft.client.gui;
import java.io.IOException;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.gui.plugins.GuiImageButton;
import thaumcraft.client.gui.plugins.GuiPlusMinusButton;
import thaumcraft.client.gui.plugins.GuiScrollButton;
import thaumcraft.client.gui.plugins.GuiSliderTC;
import thaumcraft.common.container.ContainerLogistics;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketLogisticsRequestToServer;
import thaumcraft.common.lib.network.misc.PacketMiscStringToServer;


@SideOnly(Side.CLIENT)
public class GuiLogistics extends GuiContainer
{
    int selectedSlot;
    ContainerLogistics con;
    World world;
    EntityPlayer player;
    BlockPos target;
    EnumFacing side;
    ResourceLocation tex;
    long lu;
    int lastStackSize;
    int stackSize;
    boolean stacksizeUpdated;
    ItemStack selectedStack;
    int lastScrollPos;
    GuiSliderTC scrollbar;
    GuiSliderTC countbar;
    GuiPlusMinusButton countbutton1;
    GuiPlusMinusButton countbutton2;
    GuiImageButton requestbutton;
    GuiTextField searchField;
    
    public GuiLogistics(InventoryPlayer par1InventoryPlayer, World world, BlockPos pos, EnumFacing side) {
        super(new ContainerLogistics(par1InventoryPlayer, world));
        selectedSlot = -1;
        con = null;
        tex = new ResourceLocation("thaumcraft", "textures/gui/gui_logistics.png");
        lu = 0L;
        lastStackSize = 1;
        stackSize = 1;
        stacksizeUpdated = false;
        selectedStack = null;
        lastScrollPos = 0;
        this.world = world;
        player = par1InventoryPlayer.player;
        xSize = 215;
        ySize = 215;
        con = (ContainerLogistics) inventorySlots;
        target = pos;
        this.side = side;
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        long ct = System.currentTimeMillis();
        if (ct > lu) {
            lu = ct + 1000L;
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, 22);
        }
        mc.renderEngine.bindTexture(tex);
        GL11.glEnable(3042);
        if (scrollbar != null) {
            if (scrollbar.getMax() != con.end) {
                scrollbar.setMax((float) con.end);
            }
            int sv = Math.round(scrollbar.getSliderValue());
            if (sv != lastScrollPos) {
                lastScrollPos = sv;
                mc.playerController.sendEnchantPacket(inventorySlots.windowId, 100 + lastScrollPos);
            }
            else if (con.updated && sv != con.start) {
                scrollbar.setSliderValue((float) con.start, false);
                con.updated = false;
            }
        }
        countbar.visible = (selectedSlot >= 0);
        countbutton1.visible = (selectedSlot >= 0);
        countbutton2.visible = (selectedSlot >= 0);
        requestbutton.visible = (selectedSlot >= 0);
        if (selectedSlot >= 0 && selectedStack != null && !selectedStack.isEmpty() && (!selectedStack.isItemEqual(inventorySlots.getSlot(selectedSlot).getStack()) || !ItemStack.areItemStackTagsEqual(selectedStack, inventorySlots.getSlot(selectedSlot).getStack()))) {
            selectedSlot = -1;
            for (Slot slot : inventorySlots.inventorySlots) {
                if (selectedStack.isItemEqual(slot.getStack()) && ItemStack.areItemStackTagsEqual(selectedStack, slot.getStack())) {
                    selectedSlot = slot.slotNumber;
                    break;
                }
            }
        }
        if (selectedSlot >= 0 && !inventorySlots.getSlot(selectedSlot).getHasStack()) {
            selectedSlot = -1;
        }
        if (selectedSlot >= 0 && inventorySlots.getSlot(selectedSlot) != null && inventorySlots.getSlot(selectedSlot).getHasStack()) {
            ItemStack stack = inventorySlots.getSlot(selectedSlot).getStack();
            if (countbar.getMax() != stack.getCount()) {
                countbar.setMax((float)stack.getCount());
            }
            int sv2 = Math.round(countbar.getSliderValue());
            if (sv2 != lastStackSize) {
                lastStackSize = sv2;
                stackSize = lastStackSize;
            }
            else if (stacksizeUpdated && sv2 != stackSize) {
                countbar.setSliderValue((float) stackSize, false);
                stacksizeUpdated = false;
            }
            String s = "" + stackSize;
            fontRenderer.drawString(s, 83 - fontRenderer.getStringWidth(s) / 2, 196, 3355443);
        }
        GL11.glDisable(3042);
    }
    
    protected boolean checkHotbarKeys(int par1) {
        return false;
    }
    
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        mc.renderEngine.bindTexture(tex);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3042);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        if (selectedSlot >= 0) {
            drawTexturedModalRect(guiLeft + 17 + selectedSlot % 9 * 19, guiTop + 17 + selectedSlot / 9 * 19, 222, 46, 20, 20);
        }
        GL11.glDisable(3042);
        searchField.drawTextBox();
        if (!searchField.isFocused() && searchField.getText().isEmpty()) {
            fontRenderer.drawString(new TextComponentTranslation("tc.logistics.search").getFormattedText(), guiLeft + 146, guiTop + 197, 2236962);
        }
    }
    
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int k = Mouse.getDWheel();
        if (k < 0) {
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, 0);
        }
        else if (k > 0) {
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, 1);
        }
    }
    
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        if (slotIn != null && slotId < 81 && slotIn.getHasStack()) {
            Minecraft.getMinecraft().player.playSound(SoundsTC.clack, 0.66f, 1.0f);
            selectedSlot = slotId;
            selectedStack = slotIn.getStack();
        }
    }
    
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, 0);
        }
        if (button.id == 0) {
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, 1);
        }
        if (selectedSlot >= 0 && inventorySlots.getSlot(selectedSlot) != null && inventorySlots.getSlot(selectedSlot).getHasStack()) {
            ItemStack stack = inventorySlots.getSlot(selectedSlot).getStack();
            if (button.id == 2) {
                --stackSize;
                if (stackSize < 1) {
                    stackSize = 1;
                }
                stacksizeUpdated = true;
            }
            if (button.id == 3) {
                ++stackSize;
                if (stackSize > stack.getCount()) {
                    stackSize = stack.getCount();
                }
                stacksizeUpdated = true;
            }
            if (button.id == 7) {
                ItemStack s2 = stack.copy();
                s2.setCount(1);
                PacketHandler.INSTANCE.sendToServer(new PacketLogisticsRequestToServer(target, side, s2, stackSize));
            }
        }
    }
    
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonList.add(new GuiScrollButton(0, guiLeft + 195, guiTop + 16, 10, 10, true, true));
        buttonList.add(new GuiScrollButton(1, guiLeft + 195, guiTop + 180, 10, 10, false, true));
        countbutton1 = new GuiPlusMinusButton(2, guiLeft + 13, guiTop + 195, 10, 10, true);
        countbutton2 = new GuiPlusMinusButton(3, guiLeft + 57, guiTop + 195, 10, 10, false);
        buttonList.add(countbutton1);
        buttonList.add(countbutton2);
        scrollbar = new GuiSliderTC(5, guiLeft + 196, guiTop + 28, 8, 149, "logistics.scrollbar", 0.0f, (float) con.end, (float) con.start, true);
        countbar = new GuiSliderTC(6, guiLeft + 24, guiTop + 196, 32, 8, "logistics.countbar", 1.0f, 64.0f, 1.0f, false);
        buttonList.add(scrollbar);
        buttonList.add(countbar);
        requestbutton = new GuiImageButton(this, 7, guiLeft + 116, guiTop + 200, 40, 13, "tc.logistics.request", "logistics.request", new ResourceLocation("thaumcraft", "textures/gui/gui_base.png"), 37, 82, 40, 13);
        buttonList.add(requestbutton);
        (searchField = new GuiTextField(8, fontRenderer, guiLeft + 143, guiTop + 196, 55, fontRenderer.FONT_HEIGHT)).setMaxStringLength(10);
        searchField.setEnableBackgroundDrawing(true);
        searchField.setVisible(false);
        searchField.setTextColor(16777215);
        searchField.setVisible(true);
        searchField.setCanLoseFocus(true);
        searchField.setText("");
        Keyboard.enableRepeatEvents(true);
    }
    
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }
    
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (searchField.textboxKeyTyped(typedChar, keyCode)) {
            PacketHandler.INSTANCE.sendToServer(new PacketMiscStringToServer(0, searchField.getText()));
        }
        else {
            super.keyTyped(typedChar, keyCode);
        }
    }
}
