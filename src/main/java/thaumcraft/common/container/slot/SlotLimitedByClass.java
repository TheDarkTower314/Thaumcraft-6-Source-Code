// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container.slot;

import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotLimitedByClass extends Slot
{
    Class clazz;
    int limit;
    
    public SlotLimitedByClass(final Class clazz, final IInventory par2IInventory, final int par3, final int par4, final int par5) {
        super(par2IInventory, par3, par4, par5);
        this.clazz = Object.class;
        this.limit = 64;
        this.clazz = clazz;
    }
    
    public SlotLimitedByClass(final Class clazz, final int limit, final IInventory par2IInventory, final int par3, final int par4, final int par5) {
        super(par2IInventory, par3, par4, par5);
        this.clazz = Object.class;
        this.limit = 64;
        this.clazz = clazz;
        this.limit = limit;
    }
    
    public boolean isItemValid(final ItemStack stack) {
        return !stack.isEmpty() && this.clazz.isAssignableFrom(stack.getItem().getClass());
    }
    
    public int getSlotStackLimit() {
        return this.limit;
    }
}
