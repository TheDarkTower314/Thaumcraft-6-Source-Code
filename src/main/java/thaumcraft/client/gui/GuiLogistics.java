// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui;

import thaumcraft.common.lib.network.misc.PacketMiscStringToServer;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;
import thaumcraft.client.gui.plugins.GuiScrollButton;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.common.lib.network.misc.PacketLogisticsRequestToServer;
import thaumcraft.common.lib.network.PacketHandler;
import net.minecraft.client.gui.GuiButton;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ClickType;
import java.io.IOException;
import org.lwjgl.input.Mouse;
import net.minecraft.util.text.TextComponentTranslation;
import java.util.Iterator;
import net.minecraft.inventory.Slot;
import org.lwjgl.opengl.GL11;
import net.minecraft.inventory.Container;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.client.gui.GuiTextField;
import thaumcraft.client.gui.plugins.GuiImageButton;
import thaumcraft.client.gui.plugins.GuiPlusMinusButton;
import thaumcraft.client.gui.plugins.GuiSliderTC;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thaumcraft.common.container.ContainerLogistics;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;

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
    
    public GuiLogistics(final InventoryPlayer par1InventoryPlayer, final World world, final BlockPos pos, final EnumFacing side) {
        super(new ContainerLogistics(par1InventoryPlayer, world));
        this.selectedSlot = -1;
        this.con = null;
        this.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_logistics.png");
        this.lu = 0L;
        this.lastStackSize = 1;
        this.stackSize = 1;
        this.stacksizeUpdated = false;
        this.selectedStack = null;
        this.lastScrollPos = 0;
        this.world = world;
        this.player = par1InventoryPlayer.player;
        this.xSize = 215;
        this.ySize = 215;
        this.con = (ContainerLogistics)this.inventorySlots;
        this.target = pos;
        this.side = side;
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        final long ct = System.currentTimeMillis();
        if (ct > this.lu) {
            this.lu = ct + 1000L;
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 22);
        }
        this.mc.renderEngine.bindTexture(this.tex);
        GL11.glEnable(3042);
        if (this.scrollbar != null) {
            if (this.scrollbar.getMax() != this.con.end) {
                this.scrollbar.setMax((float)this.con.end);
            }
            final int sv = Math.round(this.scrollbar.getSliderValue());
            if (sv != this.lastScrollPos) {
                this.lastScrollPos = sv;
                this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 100 + this.lastScrollPos);
            }
            else if (this.con.updated && sv != this.con.start) {
                this.scrollbar.setSliderValue((float)this.con.start, false);
                this.con.updated = false;
            }
        }
        this.countbar.visible = (this.selectedSlot >= 0);
        this.countbutton1.visible = (this.selectedSlot >= 0);
        this.countbutton2.visible = (this.selectedSlot >= 0);
        this.requestbutton.visible = (this.selectedSlot >= 0);
        if (this.selectedSlot >= 0 && this.selectedStack != null && !this.selectedStack.isEmpty() && (!this.selectedStack.isItemEqual(this.inventorySlots.getSlot(this.selectedSlot).getStack()) || !ItemStack.areItemStackTagsEqual(this.selectedStack, this.inventorySlots.getSlot(this.selectedSlot).getStack()))) {
            this.selectedSlot = -1;
            for (final Slot slot : this.inventorySlots.inventorySlots) {
                if (this.selectedStack.isItemEqual(slot.getStack()) && ItemStack.areItemStackTagsEqual(this.selectedStack, slot.getStack())) {
                    this.selectedSlot = slot.slotNumber;
                    break;
                }
            }
        }
        if (this.selectedSlot >= 0 && !this.inventorySlots.getSlot(this.selectedSlot).getHasStack()) {
            this.selectedSlot = -1;
        }
        if (this.selectedSlot >= 0 && this.inventorySlots.getSlot(this.selectedSlot) != null && this.inventorySlots.getSlot(this.selectedSlot).getHasStack()) {
            final ItemStack stack = this.inventorySlots.getSlot(this.selectedSlot).getStack();
            if (this.countbar.getMax() != stack.getCount()) {
                this.countbar.setMax((float)stack.getCount());
            }
            final int sv2 = Math.round(this.countbar.getSliderValue());
            if (sv2 != this.lastStackSize) {
                this.lastStackSize = sv2;
                this.stackSize = this.lastStackSize;
            }
            else if (this.stacksizeUpdated && sv2 != this.stackSize) {
                this.countbar.setSliderValue((float)this.stackSize, false);
                this.stacksizeUpdated = false;
            }
            final String s = "" + this.stackSize;
            this.fontRenderer.drawString(s, 83 - this.fontRenderer.getStringWidth(s) / 2, 196, 3355443);
        }
        GL11.glDisable(3042);
    }
    
    protected boolean checkHotbarKeys(final int par1) {
        return false;
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        this.mc.renderEngine.bindTexture(this.tex);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3042);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if (this.selectedSlot >= 0) {
            this.drawTexturedModalRect(this.guiLeft + 17 + this.selectedSlot % 9 * 19, this.guiTop + 17 + this.selectedSlot / 9 * 19, 222, 46, 20, 20);
        }
        GL11.glDisable(3042);
        this.searchField.drawTextBox();
        if (!this.searchField.isFocused() && this.searchField.getText().isEmpty()) {
            this.fontRenderer.drawString(new TextComponentTranslation("tc.logistics.search", new Object[0]).getFormattedText(), this.guiLeft + 146, this.guiTop + 197, 2236962);
        }
    }
    
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        final int k = Mouse.getDWheel();
        if (k < 0) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 0);
        }
        else if (k > 0) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 1);
        }
    }
    
    protected void handleMouseClick(final Slot slotIn, final int slotId, final int mouseButton, final ClickType type) {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        if (slotIn != null && slotId < 81 && slotIn.getHasStack()) {
            Minecraft.getMinecraft().player.playSound(SoundsTC.clack, 0.66f, 1.0f);
            this.selectedSlot = slotId;
            this.selectedStack = slotIn.getStack();
        }
    }
    
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == 1) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 0);
        }
        if (button.id == 0) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 1);
        }
        if (this.selectedSlot >= 0 && this.inventorySlots.getSlot(this.selectedSlot) != null && this.inventorySlots.getSlot(this.selectedSlot).getHasStack()) {
            final ItemStack stack = this.inventorySlots.getSlot(this.selectedSlot).getStack();
            if (button.id == 2) {
                --this.stackSize;
                if (this.stackSize < 1) {
                    this.stackSize = 1;
                }
                this.stacksizeUpdated = true;
            }
            if (button.id == 3) {
                ++this.stackSize;
                if (this.stackSize > stack.getCount()) {
                    this.stackSize = stack.getCount();
                }
                this.stacksizeUpdated = true;
            }
            if (button.id == 7) {
                final ItemStack s2 = stack.copy();
                s2.setCount(1);
                PacketHandler.INSTANCE.sendToServer(new PacketLogisticsRequestToServer(this.target, this.side, s2, this.stackSize));
            }
        }
    }
    
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        this.buttonList.add(new GuiScrollButton(0, this.guiLeft + 195, this.guiTop + 16, 10, 10, true, true));
        this.buttonList.add(new GuiScrollButton(1, this.guiLeft + 195, this.guiTop + 180, 10, 10, false, true));
        this.countbutton1 = new GuiPlusMinusButton(2, this.guiLeft + 13, this.guiTop + 195, 10, 10, true);
        this.countbutton2 = new GuiPlusMinusButton(3, this.guiLeft + 57, this.guiTop + 195, 10, 10, false);
        this.buttonList.add(this.countbutton1);
        this.buttonList.add(this.countbutton2);
        this.scrollbar = new GuiSliderTC(5, this.guiLeft + 196, this.guiTop + 28, 8, 149, "logistics.scrollbar", 0.0f, (float)this.con.end, (float)this.con.start, true);
        this.countbar = new GuiSliderTC(6, this.guiLeft + 24, this.guiTop + 196, 32, 8, "logistics.countbar", 1.0f, 64.0f, 1.0f, false);
        this.buttonList.add(this.scrollbar);
        this.buttonList.add(this.countbar);
        this.requestbutton = new GuiImageButton(this, 7, this.guiLeft + 116, this.guiTop + 200, 40, 13, "tc.logistics.request", "logistics.request", new ResourceLocation("thaumcraft", "textures/gui/gui_base.png"), 37, 82, 40, 13);
        this.buttonList.add(this.requestbutton);
        (this.searchField = new GuiTextField(8, this.fontRenderer, this.guiLeft + 143, this.guiTop + 196, 55, this.fontRenderer.FONT_HEIGHT)).setMaxStringLength(10);
        this.searchField.setEnableBackgroundDrawing(true);
        this.searchField.setVisible(false);
        this.searchField.setTextColor(16777215);
        this.searchField.setVisible(true);
        this.searchField.setCanLoseFocus(true);
        this.searchField.setText("");
        Keyboard.enableRepeatEvents(true);
    }
    
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }
    
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if (this.searchField.textboxKeyTyped(typedChar, keyCode)) {
            PacketHandler.INSTANCE.sendToServer(new PacketMiscStringToServer(0, this.searchField.getText()));
        }
        else {
            super.keyTyped(typedChar, keyCode);
        }
    }
}
