// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container;

import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.tiles.crafting.TileThaumatorium;
import net.minecraft.inventory.Container;

public class ContainerThaumatorium extends Container
{
    private TileThaumatorium thaumatorium;
    private EntityPlayer player;
    
    public ContainerThaumatorium(final InventoryPlayer par1InventoryPlayer, final TileThaumatorium tileEntity) {
        this.player = null;
        this.player = par1InventoryPlayer.player;
        this.thaumatorium = tileEntity;
        ((ContainerThaumatorium)(this.thaumatorium.eventHandler = this)).addSlotToContainer(new Slot(tileEntity, 0, 55, 24));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 135 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, i, 8 + i * 18, 193));
        }
        this.thaumatorium.updateRecipes(this.player);
    }
    
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        this.thaumatorium.updateRecipes(this.player);
    }
    
    public void onContainerClosed(final EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        if (!this.thaumatorium.getWorld().isRemote) {
            this.thaumatorium.eventHandler = null;
        }
    }
    
    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return this.thaumatorium.isUsableByPlayer(par1EntityPlayer);
    }
    
    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par2) {
        ItemStack itemstack = ItemStack.EMPTY;
        final Slot slot = this.inventorySlots.get(par2);
        if (slot != null && slot.getHasStack()) {
            final ItemStack itemstack2 = slot.getStack();
            itemstack = itemstack2.copy();
            if (par2 != 0) {
                if (!this.mergeItemStack(itemstack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (par2 >= 1 && par2 < 28) {
                if (!this.mergeItemStack(itemstack2, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else {
                if (par2 >= 28 && par2 < 37 && !this.mergeItemStack(itemstack2, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
                if (!this.mergeItemStack(itemstack2, 1, 37, false)) {
                    return ItemStack.EMPTY;
                }
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
