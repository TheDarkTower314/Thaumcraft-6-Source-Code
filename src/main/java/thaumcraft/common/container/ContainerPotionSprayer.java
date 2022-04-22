// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.IInventory;
import thaumcraft.common.container.slot.SlotPotion;
import net.minecraft.entity.player.InventoryPlayer;
import thaumcraft.common.tiles.devices.TilePotionSprayer;
import net.minecraft.inventory.Container;

public class ContainerPotionSprayer extends Container
{
    private TilePotionSprayer sprayer;
    private int lastBreakTime;
    
    public ContainerPotionSprayer(final InventoryPlayer par1InventoryPlayer, final TilePotionSprayer tilePotionSprayer) {
        sprayer = tilePotionSprayer;
        addSlotToContainer(new SlotPotion(tilePotionSprayer, 0, 56, 64));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 16 + j * 18, 151 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(par1InventoryPlayer, i, 16 + i * 18, 209));
        }
    }
    
    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return sprayer.isUsableByPlayer(par1EntityPlayer);
    }
    
    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int slot) {
        ItemStack stack = ItemStack.EMPTY;
        final Slot slotObject = inventorySlots.get(slot);
        if (slotObject != null && slotObject.getHasStack()) {
            final ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();
            if (slot == 0) {
                if (!sprayer.isItemValidForSlot(slot, stackInSlot) || !mergeItemStack(stackInSlot, 1, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!sprayer.isItemValidForSlot(slot, stackInSlot) || !mergeItemStack(stackInSlot, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
            if (stackInSlot.getCount() == 0) {
                slotObject.putStack(ItemStack.EMPTY);
            }
            else {
                slotObject.onSlotChanged();
            }
        }
        return stack;
    }
}
