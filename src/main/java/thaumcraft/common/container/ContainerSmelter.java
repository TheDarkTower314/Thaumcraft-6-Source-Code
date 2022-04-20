// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container;

import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.IInventory;
import thaumcraft.common.container.slot.SlotLimitedHasAspects;
import net.minecraft.entity.player.InventoryPlayer;
import thaumcraft.common.tiles.essentia.TileSmelter;
import net.minecraft.inventory.Container;

public class ContainerSmelter extends Container
{
    private TileSmelter furnace;
    private int lastCookTime;
    private int lastBurnTime;
    private int lastItemBurnTime;
    private int lastVis;
    private int lastSmelt;
    private int lastFlux;
    
    public ContainerSmelter(final InventoryPlayer par1InventoryPlayer, final TileSmelter tileEntity) {
        this.furnace = tileEntity;
        this.addSlotToContainer(new SlotLimitedHasAspects(tileEntity, 0, 80, 8));
        this.addSlotToContainer(new Slot(tileEntity, 1, 80, 48));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
        }
    }
    
    public void addListener(final IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.furnace);
        listener.sendWindowProperty(this, 0, this.furnace.furnaceCookTime);
        listener.sendWindowProperty(this, 1, this.furnace.furnaceBurnTime);
        listener.sendWindowProperty(this, 2, this.furnace.currentItemBurnTime);
        listener.sendWindowProperty(this, 3, this.furnace.vis);
        listener.sendWindowProperty(this, 4, this.furnace.smeltTime);
    }
    
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < this.listeners.size(); ++i) {
            final IContainerListener icrafting = this.listeners.get(i);
            if (this.lastCookTime != this.furnace.furnaceCookTime) {
                icrafting.sendWindowProperty(this, 0, this.furnace.furnaceCookTime);
            }
            if (this.lastBurnTime != this.furnace.furnaceBurnTime) {
                icrafting.sendWindowProperty(this, 1, this.furnace.furnaceBurnTime);
            }
            if (this.lastItemBurnTime != this.furnace.currentItemBurnTime) {
                icrafting.sendWindowProperty(this, 2, this.furnace.currentItemBurnTime);
            }
            if (this.lastVis != this.furnace.vis) {
                icrafting.sendWindowProperty(this, 3, this.furnace.vis);
            }
            if (this.lastSmelt != this.furnace.smeltTime) {
                icrafting.sendWindowProperty(this, 4, this.furnace.smeltTime);
            }
        }
        this.lastCookTime = this.furnace.furnaceCookTime;
        this.lastBurnTime = this.furnace.furnaceBurnTime;
        this.lastItemBurnTime = this.furnace.currentItemBurnTime;
        this.lastVis = this.furnace.vis;
        this.lastSmelt = this.furnace.smeltTime;
    }
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(final int par1, final int par2) {
        if (par1 == 0) {
            this.furnace.furnaceCookTime = par2;
        }
        if (par1 == 1) {
            this.furnace.furnaceBurnTime = par2;
        }
        if (par1 == 2) {
            this.furnace.currentItemBurnTime = par2;
        }
        if (par1 == 3) {
            this.furnace.vis = par2;
        }
        if (par1 == 4) {
            this.furnace.smeltTime = par2;
        }
    }
    
    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return this.furnace.isUsableByPlayer(par1EntityPlayer);
    }
    
    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par2) {
        ItemStack itemstack = ItemStack.EMPTY;
        final Slot slot = this.inventorySlots.get(par2);
        if (slot != null && slot.getHasStack()) {
            final ItemStack itemstack2 = slot.getStack();
            itemstack = itemstack2.copy();
            if (par2 != 1 && par2 != 0) {
                final AspectList al = ThaumcraftCraftingManager.getObjectTags(itemstack2);
                if (TileSmelter.isItemFuel(itemstack2)) {
                    if (!this.mergeItemStack(itemstack2, 1, 2, false) && !this.mergeItemStack(itemstack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (al != null && al.size() > 0) {
                    if (!this.mergeItemStack(itemstack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (par2 >= 2 && par2 < 29) {
                    if (!this.mergeItemStack(itemstack2, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (par2 >= 29 && par2 < 38 && !this.mergeItemStack(itemstack2, 2, 29, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack2, 2, 38, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack2.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                slot.onSlotChanged();
            }
            if (itemstack2.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(par1EntityPlayer, itemstack2);
        }
        return itemstack;
    }
}
