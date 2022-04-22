// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.research;

import java.util.Iterator;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentHelper;
import thaumcraft.api.research.ScanningManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.enchantment.Enchantment;
import thaumcraft.api.research.IScanThing;

public class ScanEnchantment implements IScanThing
{
    Enchantment enchantment;
    
    public ScanEnchantment(final Enchantment ench) {
        enchantment = ench;
    }
    
    @Override
    public boolean checkThing(final EntityPlayer player, final Object obj) {
        return getEnchantment(player, obj) != null;
    }
    
    private Enchantment getEnchantment(final EntityPlayer player, final Object obj) {
        if (obj == null) {
            return null;
        }
        final ItemStack is = ScanningManager.getItemFromParms(player, obj);
        if (is != null && !is.isEmpty()) {
            final Map<Enchantment, Integer> e = EnchantmentHelper.getEnchantments(is);
            for (final Enchantment ench : e.keySet()) {
                if (ench == enchantment) {
                    return ench;
                }
            }
        }
        return null;
    }
    
    @Override
    public String getResearchKey(final EntityPlayer player, final Object obj) {
        return "!" + enchantment.getName();
    }
}
