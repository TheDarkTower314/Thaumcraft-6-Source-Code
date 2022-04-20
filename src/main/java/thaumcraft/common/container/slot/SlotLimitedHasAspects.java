// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container.slot;

import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotLimitedHasAspects extends Slot
{
    public SlotLimitedHasAspects(final IInventory par2IInventory, final int par3, final int par4, final int par5) {
        super(par2IInventory, par3, par4, par5);
    }
    
    public boolean isItemValid(final ItemStack stack1) {
        final AspectList al = ThaumcraftCraftingManager.getObjectTags(stack1);
        return al != null && al.size() > 0;
    }
}
