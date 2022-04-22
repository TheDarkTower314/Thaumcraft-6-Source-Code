// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container;

import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;

public class InventoryLogistics extends InventoryBasic
{
    public InventoryLogistics(final IInventoryChangedListener listener) {
        super("container.logistics", false, 81);
        addInventoryChangeListener(listener);
    }
    
    public int getInventoryStackLimit() {
        return Integer.MAX_VALUE;
    }
}
