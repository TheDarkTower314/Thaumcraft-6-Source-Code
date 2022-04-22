// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.IInventory;
import thaumcraft.common.container.slot.SlotOutput;
import net.minecraft.entity.player.InventoryPlayer;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;
import net.minecraft.inventory.Container;

public class ContainerVoidSiphon extends Container
{
    private TileVoidSiphon siphon;
    private int lastProgress;
    
    public ContainerVoidSiphon(final InventoryPlayer par1InventoryPlayer, final TileVoidSiphon tileEntity) {
        siphon = tileEntity;
        addSlotToContainer(new SlotOutput(tileEntity, 0, 80, 32));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
        }
    }
    
    public void addListener(final IContainerListener par1ICrafting) {
        super.addListener(par1ICrafting);
        par1ICrafting.sendWindowProperty(this, 0, siphon.progress);
    }
    
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < listeners.size(); ++i) {
            final IContainerListener icrafting = listeners.get(i);
            if (lastProgress != siphon.progress) {
                icrafting.sendWindowProperty(this, 0, siphon.progress);
            }
        }
        lastProgress = siphon.progress;
    }
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(final int par1, final int par2) {
        if (par1 == 0) {
            siphon.progress = par2;
        }
    }
    
    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return siphon.isUsableByPlayer(par1EntityPlayer);
    }
    
    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int slot) {
        ItemStack stack = ItemStack.EMPTY;
        final Slot slotObject = inventorySlots.get(slot);
        if (slotObject != null && slotObject.getHasStack()) {
            final ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();
            if (slot == 0) {
                if (!siphon.isItemValidForSlot(slot, stackInSlot) || !mergeItemStack(stackInSlot, 1, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!siphon.isItemValidForSlot(slot, stackInSlot) || !mergeItemStack(stackInSlot, 0, 1, false)) {
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
