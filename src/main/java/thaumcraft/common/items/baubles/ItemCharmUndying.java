package thaumcraft.common.items.baubles;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.ItemTCBase;


public class ItemCharmUndying extends ItemTCBase implements IBauble
{
    public ItemCharmUndying() {
        super("charm_undying");
        maxStackSize = 1;
        canRepair = false;
        setMaxDamage(0);
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.RARE;
    }
    
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.CHARM;
    }
}
