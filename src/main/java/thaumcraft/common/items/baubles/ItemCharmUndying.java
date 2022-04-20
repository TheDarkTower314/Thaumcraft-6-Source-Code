// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.baubles;

import baubles.api.BaubleType;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import baubles.api.IBauble;
import thaumcraft.common.items.ItemTCBase;

public class ItemCharmUndying extends ItemTCBase implements IBauble
{
    public ItemCharmUndying() {
        super("charm_undying", new String[0]);
        this.maxStackSize = 1;
        this.canRepair = false;
        this.setMaxDamage(0);
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.RARE;
    }
    
    public BaubleType getBaubleType(final ItemStack itemstack) {
        return BaubleType.CHARM;
    }
}
