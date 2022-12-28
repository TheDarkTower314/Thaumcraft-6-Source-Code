package thaumcraft.common.items.tools;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.ItemTCBase;


public class ItemSanityChecker extends ItemTCBase
{
    public ItemSanityChecker() {
        super("sanity_checker");
        setMaxStackSize(1);
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
}
