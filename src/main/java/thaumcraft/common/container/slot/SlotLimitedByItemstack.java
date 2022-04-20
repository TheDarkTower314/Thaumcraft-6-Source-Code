// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Slot;

public class SlotLimitedByItemstack extends Slot
{
    ItemStack limitItem;
    
    public SlotLimitedByItemstack(final ItemStack item, final IInventory par2IInventory, final int par3, final int par4, final int par5) {
        super(par2IInventory, par3, par4, par5);
        this.limitItem = null;
        this.limitItem = item;
    }
    
    public boolean isItemValid(final ItemStack stack1) {
        return stack1.isItemEqual(this.limitItem);
    }
}
