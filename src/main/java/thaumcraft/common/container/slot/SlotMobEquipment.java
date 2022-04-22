// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container.slot;

import net.minecraft.util.EnumHand;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.Slot;

public class SlotMobEquipment extends Slot
{
    EntityLiving entity;
    
    public SlotMobEquipment(final EntityLiving entity, final int par3, final int par4, final int par5) {
        super(null, par3, par4, par5);
        this.entity = entity;
    }
    
    public ItemStack getStack() {
        return entity.getHeldItem(EnumHand.MAIN_HAND);
    }
    
    public void putStack(final ItemStack stack) {
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
    
    public ItemStack decrStackSize(final int amount) {
        if (getStack().isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (getStack().getCount() <= amount) {
            final ItemStack itemstack = getStack();
            putStack(ItemStack.EMPTY);
            return itemstack;
        }
        final ItemStack itemstack = getStack().splitStack(amount);
        if (getStack().getCount() == 0) {
            putStack(ItemStack.EMPTY);
        }
        return itemstack;
    }
    
    public boolean isHere(final IInventory inv, final int slotIn) {
        return slotIn == getSlotIndex();
    }
}
