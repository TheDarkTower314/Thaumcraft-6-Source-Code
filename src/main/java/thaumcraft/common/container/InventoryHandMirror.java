package thaumcraft.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;

public class InventoryHandMirror extends InventoryBasic
{
    Container container;
    
    public InventoryHandMirror(IInventoryChangedListener listener) {
        super("container.handmirror", false, 1);
        addInventoryChangeListener(listener);
        container = (Container)listener;
    }
    
    public void setInventorySlotContents(int index, ItemStack stack) {
        super.setInventorySlotContents(index, stack);
        if (!stack.isEmpty()) {
            container.onCraftMatrixChanged(this);
        }
    }
}
