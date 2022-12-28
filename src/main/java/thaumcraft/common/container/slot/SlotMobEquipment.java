package thaumcraft.common.container.slot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;


public class SlotMobEquipment extends Slot
{
    EntityLiving entity;
    
    public SlotMobEquipment(EntityLiving entity, int par3, int par4, int par5) {
        super(null, par3, par4, par5);
        this.entity = entity;
    }
    
    public ItemStack getStack() {
        return entity.getHeldItem(EnumHand.MAIN_HAND);
    }
    
    public void putStack(ItemStack stack) {
        entity.setHeldItem(EnumHand.MAIN_HAND, stack);
        if (stack != null && !stack.isEmpty() && stack.getCount() > getSlotStackLimit()) {
            stack.setCount(getSlotStackLimit());
        }
        onSlotChanged();
    }
    
    public void onSlotChanged() {
    }
    
    public int getSlotStackLimit() {
        return 64;
    }
    
    public ItemStack decrStackSize(int amount) {
        if (getStack().isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (getStack().getCount() <= amount) {
            ItemStack itemstack = getStack();
            putStack(ItemStack.EMPTY);
            return itemstack;
        }
        ItemStack itemstack = getStack().splitStack(amount);
        if (getStack().getCount() == 0) {
            putStack(ItemStack.EMPTY);
        }
        return itemstack;
    }
    
    public boolean isHere(IInventory inv, int slotIn) {
        return slotIn == getSlotIndex();
    }
}
