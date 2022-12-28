package thaumcraft.common.items.consumables;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.common.items.ItemTCBase;


public class ItemBathSalts extends ItemTCBase
{
    public ItemBathSalts() {
        super("bath_salts");
        setHasSubtypes(false);
    }
    
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 200;
    }
}
