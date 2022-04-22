package thaumcraft.common.items.resources;

import java.util.Iterator;
import thaumcraft.api.aspects.AspectList;
import net.minecraft.item.Item;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import thaumcraft.common.items.ItemTCEssentiaContainer;

public class ItemCrystalEssence extends ItemTCEssentiaContainer
{
    public ItemCrystalEssence() {
        super("crystal_essence", 1);
    }
    
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeTabs.SEARCH) {
            for (Aspect tag : Aspect.aspects.values()) {
                ItemStack i = new ItemStack(this);
                setAspects(i, new AspectList().add(tag, base));
                items.add(i);
            }
        }
    }
    
    public String getItemStackDisplayName(ItemStack stack) {
        return (getAspects(stack) != null && !getAspects(stack).aspects.isEmpty()) ? String.format(super.getItemStackDisplayName(stack), getAspects(stack).getAspects()[0].getName()) : super.getItemStackDisplayName(stack);
    }
}
