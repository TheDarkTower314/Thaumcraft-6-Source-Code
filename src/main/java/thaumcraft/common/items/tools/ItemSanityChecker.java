// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.tools;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.ItemTCBase;

public class ItemSanityChecker extends ItemTCBase
{
    public ItemSanityChecker() {
        super("sanity_checker", new String[0]);
        this.setMaxStackSize(1);
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
}
