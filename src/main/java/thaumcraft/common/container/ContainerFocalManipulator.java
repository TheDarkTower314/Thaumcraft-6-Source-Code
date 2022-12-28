package thaumcraft.common.container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.container.slot.SlotFocus;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;


public class ContainerFocalManipulator extends Container
{
    private TileFocalManipulator table;
    private int lastBreakTime;
    
    public ContainerFocalManipulator(InventoryPlayer inventoryPlayer, TileFocalManipulator tileEntity) {
        table = tileEntity;
        addSlotToContainer(new SlotFocus(tileEntity, 0, 31, 191));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, i * 18 - 62, 64 + j * 18));
            }
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                addSlotToContainer(new Slot(inventoryPlayer, i + j * 3, i * 18 - 62, j * 18 + 7));
            }
        }
    }
    
    public boolean enchantItem(EntityPlayer p, int button) {
        if (button == 0 && !table.startCraft(button, p)) {
            table.getWorld().playSound(p, table.getPos(), SoundsTC.craftfail, SoundCategory.BLOCKS, 0.33f, 1.0f);
        }
        return false;
    }
    
    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return table.isUsableByPlayer(par1EntityPlayer);
    }
    
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(par2);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack2 = slot.getStack();
            itemstack = itemstack2.copy();
            if (par2 != 0) {
                if (itemstack2.getItem() instanceof ItemFocus) {
                    if (!mergeItemStack(itemstack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (par2 >= 1 && par2 < 28) {
                    if (!mergeItemStack(itemstack2, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (par2 >= 28 && par2 < 37 && !mergeItemStack(itemstack2, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!mergeItemStack(itemstack2, 1, 37, false)) {
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
