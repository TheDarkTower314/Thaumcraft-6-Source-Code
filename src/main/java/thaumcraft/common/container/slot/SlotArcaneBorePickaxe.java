package thaumcraft.common.container.slot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import thaumcraft.common.entities.construct.EntityArcaneBore;


public class SlotArcaneBorePickaxe extends SlotMobEquipment
{
    public SlotArcaneBorePickaxe(EntityArcaneBore turret, int par3, int par4, int par5) {
        super(turret, par3, par4, par5);
    }
    
    public boolean isItemValid(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() != null && (stack.getItem() instanceof ItemPickaxe || stack.getItem().getToolClasses(stack).contains("pickaxe"));
    }
    
    @Override
    public void onSlotChanged() {
    }
}
