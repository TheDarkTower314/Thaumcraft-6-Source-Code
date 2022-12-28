package thaumcraft.common.container;
import java.util.ArrayList;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;


public class InventoryFake extends InventoryBasic
{
    public InventoryFake(int size) {
        super("container.fake", false, size);
    }
    
    public InventoryFake(NonNullList<ItemStack> inv) {
        super("container.fake", false, inv.size());
        for (int a = 0; a < inv.size(); ++a) {
            setInventorySlotContents(a, inv.get(a));
        }
    }
    
    public InventoryFake(ItemStack... stacks) {
        super("container.fake", false, stacks.length);
        for (int a = 0; a < stacks.length; ++a) {
            setInventorySlotContents(a, stacks[a]);
        }
    }
    
    public InventoryFake(ArrayList<ItemStack> inv) {
        super("container.fake", false, inv.size());
        for (int a = 0; a < inv.size(); ++a) {
            setInventorySlotContents(a, inv.get(a));
        }
    }
}
