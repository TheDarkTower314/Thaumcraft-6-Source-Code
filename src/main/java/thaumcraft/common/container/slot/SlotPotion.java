// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container.slot;

import net.minecraft.potion.PotionType;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionUtils;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotPotion extends Slot
{
    int limit;
    
    public SlotPotion(final IInventory par2IInventory, final int par3, final int par4, final int par5) {
        super(par2IInventory, par3, par4, par5);
        limit = 64;
    }
    
    public SlotPotion(final int limit, final IInventory par2IInventory, final int par3, final int par4, final int par5) {
        super(par2IInventory, par3, par4, par5);
        this.limit = 64;
        this.limit = limit;
    }
    
    public boolean isItemValid(final ItemStack stack) {
        return stack != null && !stack.isEmpty() && isValidPotion(stack);
    }
    
    public static boolean isValidPotion(final ItemStack stack) {
        if (stack.getItem() != Items.POTIONITEM && stack.getItem() != Items.LINGERING_POTION) {
            if (stack.getItem() != Items.SPLASH_POTION) {
                return false;
            }
        }
        try {
            final PotionType potion = PotionUtils.getPotionFromItem(stack);
            return potion != null && potion != PotionTypes.WATER && potion != PotionTypes.AWKWARD && potion != PotionTypes.EMPTY && potion != PotionTypes.MUNDANE && potion != PotionTypes.THICK;
        }
        catch (final Exception ex) {}
        return false;
    }
    
    public int getSlotStackLimit() {
        return limit;
    }
}
