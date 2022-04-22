// 
// Decompiled by Procyon v0.6.0
// 

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
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeTabs.SEARCH) {
            for (final Aspect tag : Aspect.aspects.values()) {
                final ItemStack i = new ItemStack(this);
                this.setAspects(i, new AspectList().add(tag, this.base));
                items.add(i);
            }
        }
    }
    
    public String getItemStackDisplayName(final ItemStack stack) {
        return (this.getAspects(stack) != null && !this.getAspects(stack).aspects.isEmpty()) ? String.format(super.getItemStackDisplayName(stack), this.getAspects(stack).getAspects()[0].getName()) : super.getItemStackDisplayName(stack);
    }
}
