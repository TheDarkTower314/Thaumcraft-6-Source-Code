package thaumcraft.common.items.tools;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.common.items.ItemTCBase;


public class ItemScribingTools extends ItemTCBase implements IScribeTools
{
    public ItemScribingTools() {
        super("scribing_tools");
        maxStackSize = 1;
        setMaxDamage(100);
        setHasSubtypes(false);
    }
}
