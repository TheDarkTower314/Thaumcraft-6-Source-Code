// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container;

import thaumcraft.common.items.casters.ItemFocus;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;

public class InventoryFocusPouch extends InventoryBasic
{
    public InventoryFocusPouch(final IInventoryChangedListener listener) {
        super("container.focuspouch", false, 18);
        this.addInventoryChangeListener(listener);
    }
    
    public int getInventoryStackLimit() {
        return 1;
    }
    
    public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
        return !itemstack.isEmpty() && itemstack.getItem() instanceof ItemFocus;
    }
}
