package thaumcraft.common.items.baubles;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.common.items.ItemTCBase;


public class ItemBaubles extends ItemTCBase implements IBauble, IVisDiscountGear
{
    public ItemBaubles() {
        super("baubles", "amulet_mundane", "ring_mundane", "girdle_mundane", "ring_apprentice", "amulet_fancy", "ring_fancy", "girdle_fancy");
        maxStackSize = 1;
        setMaxDamage(0);
    }
    
    public BaubleType getBaubleType(ItemStack itemstack) {
        switch (itemstack.getItemDamage()) {
            case 1:
            case 3:
            case 5: {
                return BaubleType.RING;
            }
            case 2:
            case 6: {
                return BaubleType.BELT;
            }
            default: {
                return BaubleType.AMULET;
            }
        }
    }
    
    public EnumRarity getRarity(ItemStack stack) {
        if (stack.getItemDamage() >= 3) {
            return EnumRarity.UNCOMMON;
        }
        return super.getRarity(stack);
    }
    
    public int getVisDiscount(ItemStack stack, EntityPlayer player) {
        if (stack.getItemDamage() == 3) {
            return 5;
        }
        return 0;
    }
}
