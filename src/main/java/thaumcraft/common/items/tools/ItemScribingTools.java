// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.tools;

import thaumcraft.api.items.IScribeTools;
import thaumcraft.common.items.ItemTCBase;

public class ItemScribingTools extends ItemTCBase implements IScribeTools
{
    public ItemScribingTools() {
        super("scribing_tools", new String[0]);
        this.maxStackSize = 1;
        this.setMaxDamage(100);
        this.setHasSubtypes(false);
    }
}
