// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.item.ItemStack;
import net.minecraft.creativetab.CreativeTabs;

public final class CreativeTabThaumcraft extends CreativeTabs
{
    public CreativeTabThaumcraft(final int par1, final String par2Str) {
        super(par1, par2Str);
    }
    
    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        return new ItemStack(ItemsTC.goggles);
    }
}
