// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container.slot;

import thaumcraft.common.items.casters.ItemFocus;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotFocus extends Slot
{
    int limit;
    
    public SlotFocus(final IInventory par2IInventory, final int par3, final int par4, final int par5) {
        super(par2IInventory, par3, par4, par5);
        limit = 64;
    }
    
    public SlotFocus(final int limit, final IInventory par2IInventory, final int par3, final int par4, final int par5) {
        super(par2IInventory, par3, par4, par5);
        this.limit = 64;
        this.limit = limit;
    }
    
    public boolean isItemValid(final ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() != null && stack.getItem() instanceof ItemFocus;
    }
    
    public int getSlotStackLimit() {
        return limit;
    }
}
