// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.consumables;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.ItemTCBase;

public class ItemBathSalts extends ItemTCBase
{
    public ItemBathSalts() {
        super("bath_salts");
        this.setHasSubtypes(false);
    }
    
    public int getEntityLifespan(final ItemStack itemStack, final World world) {
        return 200;
    }
}
