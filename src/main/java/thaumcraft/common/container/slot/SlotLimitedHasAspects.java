package thaumcraft.common.container.slot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;


public class SlotLimitedHasAspects extends Slot
{
    public SlotLimitedHasAspects(IInventory par2IInventory, int par3, int par4, int par5) {
        super(par2IInventory, par3, par4, par5);
    }
    
    public boolean isItemValid(ItemStack stack1) {
        AspectList al = ThaumcraftCraftingManager.getObjectTags(stack1);
        return al != null && al.size() > 0;
    }
}
