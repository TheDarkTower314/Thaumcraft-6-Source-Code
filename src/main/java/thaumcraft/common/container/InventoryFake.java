// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.inventory.InventoryBasic;

public class InventoryFake extends InventoryBasic
{
    public InventoryFake(final int size) {
        super("container.fake", false, size);
    }
    
    public InventoryFake(final NonNullList<ItemStack> inv) {
        super("container.fake", false, inv.size());
        for (int a = 0; a < inv.size(); ++a) {
            setInventorySlotContents(a, inv.get(a));
        }
    }
    
    public InventoryFake(final ItemStack... stacks) {
        super("container.fake", false, stacks.length);
        for (int a = 0; a < stacks.length; ++a) {
            setInventorySlotContents(a, stacks[a]);
        }
    }
    
    public InventoryFake(final ArrayList<ItemStack> inv) {
        super("container.fake", false, inv.size());
        for (int a = 0; a < inv.size(); ++a) {
            setInventorySlotContents(a, inv.get(a));
        }
    }
}
