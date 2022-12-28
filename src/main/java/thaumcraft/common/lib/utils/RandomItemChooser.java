package thaumcraft.common.lib.utils;
import java.util.Iterator;
import java.util.List;


public class RandomItemChooser
{
    public Item chooseOnWeight(List<Item> items) {
        double completeWeight = 0.0;
        for (Item item : items) {
            completeWeight += item.getWeight();
        }
        double r = Math.random() * completeWeight;
        double countWeight = 0.0;
        for (Item item2 : items) {
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
