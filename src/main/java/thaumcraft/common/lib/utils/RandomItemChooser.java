// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.utils;

import java.util.Iterator;
import java.util.List;

public class RandomItemChooser
{
    public Item chooseOnWeight(final List<Item> items) {
        double completeWeight = 0.0;
        for (final Item item : items) {
            completeWeight += item.getWeight();
        }
        final double r = Math.random() * completeWeight;
        double countWeight = 0.0;
        for (final Item item2 : items) {
            countWeight += item2.getWeight();
            if (countWeight >= r) {
                return item2;
            }
        }
        throw new RuntimeException("Should never be shown.");
    }
    
    public interface Item
    {
        double getWeight();
    }
}
