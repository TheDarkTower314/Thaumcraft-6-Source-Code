// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container.slot;

import thaumcraft.common.items.resources.ItemCrystalEssence;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.inventory.Slot;

public class SlotCrystal extends Slot
{
    private Aspect aspect;
    
    public SlotCrystal(final Aspect aspect, final IInventory par2IInventory, final int par3, final int par4, final int par5) {
        super(par2IInventory, par3, par4, par5);
        this.aspect = aspect;
    }
    
    public boolean isItemValid(final ItemStack stack) {
        return isValidCrystal(stack, this.aspect);
    }
    
    public static boolean isValidCrystal(final ItemStack stack, final Aspect aspect) {
        return stack != null && !stack.isEmpty() && stack.getItem() != null && stack.getItem() instanceof ItemCrystalEssence && ((ItemCrystalEssence)stack.getItem()).getAspects(stack).getAspects()[0] == aspect;
    }
}
