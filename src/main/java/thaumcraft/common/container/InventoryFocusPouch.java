package thaumcraft.common.container;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.casters.ItemFocus;


public class InventoryFocusPouch extends InventoryBasic
{
    public InventoryFocusPouch(IInventoryChangedListener listener) {
        super("container.focuspouch", false, 18);
        addInventoryChangeListener(listener);
    }
    
    public int getInventoryStackLimit() {
        return 1;
    }
    
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return !itemstack.isEmpty() && itemstack.getItem() instanceof ItemFocus;
    }
}
