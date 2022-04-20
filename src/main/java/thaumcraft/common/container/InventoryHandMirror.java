// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;

public class InventoryHandMirror extends InventoryBasic
{
    Container container;
    
    public InventoryHandMirror(final IInventoryChangedListener listener) {
        super("container.handmirror", false, 1);
        this.addInventoryChangeListener(listener);
        this.container = (Container)listener;
    }
    
    public void setInventorySlotContents(final int index, final ItemStack stack) {
        super.setInventorySlotContents(index, stack);
        if (!stack.isEmpty()) {
            this.container.onCraftMatrixChanged(this);
        }
    }
}
