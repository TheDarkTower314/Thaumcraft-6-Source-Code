package thaumcraft.common.container.slot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.casters.ItemFocus;


public class SlotFocus extends Slot
{
    int limit;
    
    public SlotFocus(IInventory par2IInventory, int par3, int par4, int par5) {
        super(par2IInventory, par3, par4, par5);
        limit = 64;
    }
    
    public SlotFocus(int limit, IInventory par2IInventory, int par3, int par4, int par5) {
        super(par2IInventory, par3, par4, par5);
        this.limit = 64;
        this.limit = limit;
    }
    
    public boolean isItemValid(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() != null && stack.getItem() instanceof ItemFocus;
    }
    
    public int getSlotStackLimit() {
        return limit;
    }
}
