// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.client.gui;

import thaumcraft.Thaumcraft;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.ClickType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.BlockPos;
import net.minecraft.inventory.IContainerListener;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.IInventory;
import thaumcraft.common.container.slot.SlotGhost;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import com.google.common.collect.Lists;
import net.minecraft.util.NonNullList;
import thaumcraft.api.golems.seals.ISealGui;
import net.minecraft.entity.player.InventoryPlayer;
import thaumcraft.common.container.InventoryFake;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.golems.seals.ISealEntity;
import net.minecraft.world.World;
import net.minecraft.inventory.Container;

public class SealBaseContainer extends Container
{
    private World world;
    ISealEntity seal;
    EntityPlayer player;
    InventoryFake temp;
    int[] categories;
    int category;
    InventoryPlayer pinv;
    int t;
    private byte lastPriority;
    private byte lastColor;
    private int lastAreaX;
    private int lastAreaY;
    private int lastAreaZ;
    
    public SealBaseContainer(final InventoryPlayer iinventory, final World par2World, final ISealEntity seal) {
        this.seal = null;
        this.player = null;
        this.category = -1;
        this.t = 0;
        this.world = par2World;
        this.player = iinventory.player;
        this.pinv = iinventory;
        this.seal = seal;
        if (seal.getSeal() instanceof ISealGui) {
            this.categories = ((ISealGui)seal.getSeal()).getGuiCategories();
        }
        else {
            this.categories = new int[] { 0 };
        }
        this.setupCategories();
    }
    
    void setupCategories() {
        this.inventoryItemStacks = NonNullList.create();
        this.inventorySlots = Lists.newArrayList();
        this.t = 0;
        if (this.category < 0) {
            this.category = this.categories[0];
        }
        switch (this.category) {
            case 1: {
                this.setupFilterInventory();
                break;
            }
        }
        this.bindPlayerInventory(this.pinv);
    }
    
    private void setupFilterInventory() {
        if (this.seal.getSeal() instanceof ISealConfigFilter) {
            final int s = ((ISealConfigFilter)this.seal.getSeal()).getFilterSize();
            final int sx = 16 + (s - 1) % 3 * 12;
            final int sy = 16 + (s - 1) / 3 * 12;
            final int middleX = 88;
            final int middleY = 72;
            this.temp = new InventoryFake(((ISealConfigFilter)this.seal.getSeal()).getInv());
            for (int a = 0; a < s; ++a) {
                final int x = a % 3;
                final int y = a / 3;
                this.addSlotToContainer(new SlotGhost(this.temp, a, middleX + x * 24 - sx + 8, middleY + y * 24 - sy + 8));
                ++this.t;
            }
        }
    }
    
    protected void bindPlayerInventory(final InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 150 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 208));
        }
    }
    
    public boolean canInteractWith(final EntityPlayer var1) {
        return true;
    }
    
    public boolean enchantItem(final EntityPlayer player, final int par2) {
        if (par2 >= 0 && par2 < this.categories.length) {
            this.category = this.categories[par2];
            this.setupCategories();
            return true;
        }
        if (this.category == 3 && this.seal.getSeal() instanceof ISealConfigToggles && par2 >= 30 && par2 < 30 + ((ISealConfigToggles)this.seal.getSeal()).getToggles().length) {
            final ISealConfigToggles cp = (ISealConfigToggles)this.seal.getSeal();
            cp.setToggle(par2 - 30, true);
            return true;
        }
        if (this.category == 3 && this.seal.getSeal() instanceof ISealConfigToggles && par2 >= 60 && par2 < 60 + ((ISealConfigToggles)this.seal.getSeal()).getToggles().length) {
            final ISealConfigToggles cp = (ISealConfigToggles)this.seal.getSeal();
            cp.setToggle(par2 - 60, false);
            return true;
        }
        if (this.category == 0 && par2 >= 25 && par2 <= 26) {
            this.seal.setLocked(par2 == 25);
            return true;
        }
        if (par2 >= 27 && par2 <= 28) {
            this.seal.setRedstoneSensitive(par2 == 27);
            return true;
        }
        if (this.category == 1 && this.seal.getSeal() instanceof ISealConfigFilter && par2 >= 20 && par2 <= 21) {
            final ISealConfigFilter cp2 = (ISealConfigFilter)this.seal.getSeal();
            cp2.setBlacklist(par2 == 20);
            return true;
        }
        if (par2 == 80 && this.seal.getPriority() > -5) {
            this.seal.setPriority((byte)(this.seal.getPriority() - 1));
            return true;
        }
        if (par2 == 81 && this.seal.getPriority() < 5) {
            this.seal.setPriority((byte)(this.seal.getPriority() + 1));
            return true;
        }
        if (par2 == 82 && this.seal.getColor() > 0) {
            this.seal.setColor((byte)(this.seal.getColor() - 1));
            return true;
        }
        if (par2 == 83 && this.seal.getColor() < 16) {
            this.seal.setColor((byte)(this.seal.getColor() + 1));
            return true;
        }
        if (this.seal.getSeal() instanceof ISealConfigArea) {
            if (par2 == 90 && this.seal.getArea().getY() > 1) {
                this.seal.setArea(this.seal.getArea().add(0, -1, 0));
                return true;
            }
            if (par2 == 91 && this.seal.getArea().getY() < 8) {
                this.seal.setArea(this.seal.getArea().add(0, 1, 0));
                return true;
            }
            if (par2 == 92 && this.seal.getArea().getX() > 1) {
                this.seal.setArea(this.seal.getArea().add(-1, 0, 0));
                return true;
            }
            if (par2 == 93 && this.seal.getArea().getX() < 8) {
                this.seal.setArea(this.seal.getArea().add(1, 0, 0));
                return true;
            }
            if (par2 == 94 && this.seal.getArea().getZ() > 1) {
                this.seal.setArea(this.seal.getArea().add(0, 0, -1));
                return true;
            }
            if (par2 == 95 && this.seal.getArea().getZ() < 8) {
                this.seal.setArea(this.seal.getArea().add(0, 0, 1));
                return true;
            }
        }
        return super.enchantItem(player, par2);
    }
    
    public void addListener(final IContainerListener crafting) {
        super.addListener(crafting);
        crafting.sendWindowProperty(this, 0, this.seal.getPriority());
        crafting.sendWindowProperty(this, 4, this.seal.getColor());
    }
    
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < this.listeners.size(); ++i) {
            final IContainerListener icrafting = this.listeners.get(i);
            if (this.lastPriority != this.seal.getPriority()) {
                icrafting.sendWindowProperty(this, 0, this.seal.getPriority());
            }
            if (this.lastAreaX != this.seal.getArea().getX()) {
                icrafting.sendWindowProperty(this, 1, this.seal.getArea().getX());
            }
            if (this.lastAreaY != this.seal.getArea().getY()) {
                icrafting.sendWindowProperty(this, 2, this.seal.getArea().getY());
            }
            if (this.lastAreaZ != this.seal.getArea().getZ()) {
                icrafting.sendWindowProperty(this, 3, this.seal.getArea().getZ());
            }
            if (this.lastColor != this.seal.getColor()) {
                icrafting.sendWindowProperty(this, 4, this.seal.getColor());
            }
        }
        this.lastPriority = this.seal.getPriority();
        this.lastColor = this.seal.getColor();
        this.lastAreaX = this.seal.getArea().getX();
        this.lastAreaY = this.seal.getArea().getY();
        this.lastAreaZ = this.seal.getArea().getZ();
        if (this.seal.getSeal() instanceof ISealConfigFilter && this.temp != null) {
            for (int a = 0; a < this.temp.getSizeInventory(); ++a) {
                ((ISealConfigFilter)this.seal.getSeal()).setFilterSlot(a, this.temp.getStackInSlot(a));
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(final int par1, final int par2) {
        if (par1 == 0) {
            this.seal.setPriority((byte)par2);
        }
        if (par1 == 1) {
            this.seal.setArea(new BlockPos(par2, this.seal.getArea().getY(), this.seal.getArea().getZ()));
        }
        if (par1 == 2) {
            this.seal.setArea(new BlockPos(this.seal.getArea().getX(), par2, this.seal.getArea().getZ()));
        }
        if (par1 == 3) {
            this.seal.setArea(new BlockPos(this.seal.getArea().getX(), this.seal.getArea().getY(), par2));
        }
        if (par1 == 4) {
            this.seal.setColor((byte)par2);
        }
    }
    
    public ItemStack slotClick(final int slotId, final int clickedButton, final ClickType mode, final EntityPlayer playerIn) {
        if (slotId >= 0) {
            final Slot slot = this.inventorySlots.get(slotId);
            final InventoryPlayer inventoryplayer = playerIn.inventory;
            ItemStack ic = ItemStack.EMPTY;
            if (inventoryplayer.getItemStack() != null && !inventoryplayer.getItemStack().isEmpty()) {
                ic = inventoryplayer.getItemStack().copy();
            }
            if (slot != null && slot instanceof SlotGhost) {
                final boolean filter = ((ISealConfigFilter)this.seal.getSeal()).hasStacksizeLimiters();
                if (playerIn.world.isRemote) {
                    final Thaumcraft instance = Thaumcraft.instance;
                    if (Thaumcraft.proxy.getSingleplayer()) {
                        return ItemStack.EMPTY;
                    }
                }
                if (clickedButton == 1) {
                    if (!filter) {
                        slot.putStack(ItemStack.EMPTY);
                        ((ISealConfigFilter)this.seal.getSeal()).setFilterSlotSize(slot.slotNumber, 0);
                    }
                    else if (ic == ItemStack.EMPTY) {
                        if (slot.getHasStack()) {
                            ((ISealConfigFilter)this.seal.getSeal()).setFilterSlotSize(slot.slotNumber, ((ISealConfigFilter)this.seal.getSeal()).getFilterSlotSize(slot.slotNumber) - ((mode == ClickType.QUICK_MOVE) ? 10 : 1));
                            if (((ISealConfigFilter)this.seal.getSeal()).getFilterSlotSize(slot.slotNumber) < 0) {
                                slot.putStack(ItemStack.EMPTY);
                                ((ISealConfigFilter)this.seal.getSeal()).setFilterSlotSize(slot.slotNumber, 0);
                            }
                        }
                    }
                    else if (slot.getHasStack() && slot.getStack().getCount() == 0) {
                        slot.putStack(ItemStack.EMPTY);
                        ((ISealConfigFilter)this.seal.getSeal()).setFilterSlotSize(slot.slotNumber, 0);
                    }
                    else if (slot.getHasStack() && ItemStack.areItemsEqual(ic, slot.getStack()) && ItemStack.areItemStackTagsEqual(ic, slot.getStack())) {
                        ((ISealConfigFilter)this.seal.getSeal()).setFilterSlotSize(slot.slotNumber, ((ISealConfigFilter)this.seal.getSeal()).getFilterSlotSize(slot.slotNumber) - ic.getCount());
                        if (((ISealConfigFilter)this.seal.getSeal()).getFilterSlotSize(slot.slotNumber) < 0) {
                            slot.putStack(ItemStack.EMPTY);
                            ((ISealConfigFilter)this.seal.getSeal()).setFilterSlotSize(slot.slotNumber, 0);
                        }
                    }
                }
                else if (ic == ItemStack.EMPTY) {
                    if (filter && slot.getHasStack()) {
                        ((ISealConfigFilter)this.seal.getSeal()).setFilterSlotSize(slot.slotNumber, ((ISealConfigFilter)this.seal.getSeal()).getFilterSlotSize(slot.slotNumber) + ((mode == ClickType.QUICK_MOVE) ? 10 : 1));
                    }
                }
                else {
                    if (!filter) {
                        ic.setCount(1);
                        ((ISealConfigFilter)this.seal.getSeal()).setFilterSlotSize(slot.slotNumber, 0);
                    }
                    else {
                        final int os = ic.getCount();
                        ic.setCount(1);
                        if (slot.getHasStack() && ItemStack.areItemsEqual(ic, slot.getStack()) && ItemStack.areItemStackTagsEqual(ic, slot.getStack())) {
                            ((ISealConfigFilter)this.seal.getSeal()).setFilterSlotSize(slot.slotNumber, ((ISealConfigFilter)this.seal.getSeal()).getFilterSlotSize(slot.slotNumber) + os);
                        }
                        else {
                            ((ISealConfigFilter)this.seal.getSeal()).setFilterSlotSize(slot.slotNumber, 0);
                        }
                    }
                    slot.putStack(ic);
                }
                if (slot.getHasStack() && slot.getStack().getCount() < 0) {
                    slot.getStack().setCount(0);
                }
                this.detectAndSendChanges();
                return ItemStack.EMPTY;
            }
        }
        return super.slotClick(slotId, clickedButton, mode, playerIn);
    }
    
    public ItemStack transferStackInSlot(final EntityPlayer player, final int par2) {
        ItemStack itemstack = null;
        final Slot slot = this.inventorySlots.get(par2);
        if (slot != null && slot.getHasStack()) {
            final ItemStack itemstack2 = slot.getStack();
            itemstack = itemstack2.copy();
            if (itemstack2.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                slot.onSlotChanged();
            }
            if (itemstack2.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemstack2);
        }
        return itemstack;
    }
}
