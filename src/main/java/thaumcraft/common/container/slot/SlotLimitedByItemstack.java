package thaumcraft.common.container.slot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;


public class SlotLimitedByItemstack extends Slot
{
    ItemStack limitItem;
    
    public SlotLimitedByItemstack(ItemStack item, IInventory par2IInventory, int par3, int par4, int par5) {
        super(par2IInventory, par3, par4, par5);
        limitItem = null;
        limitItem = item;
    }
    
    public boolean isItemValid(ItemStack stack1) {
        return stack1.isItemEqual(limitItem);
    }
}
