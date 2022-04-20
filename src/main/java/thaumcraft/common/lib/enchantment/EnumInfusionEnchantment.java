// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.enchantment;

import com.google.common.collect.ImmutableSet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTBase;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.item.ItemStack;
import java.util.Set;

public enum EnumInfusionEnchantment
{
    COLLECTOR(ImmutableSet.of("axe", "pickaxe", "shovel", "weapon"), 1, "INFUSIONENCHANTMENT"),
    DESTRUCTIVE(ImmutableSet.of("axe", "pickaxe", "shovel"), 1, "INFUSIONENCHANTMENT"),
    BURROWING(ImmutableSet.of("axe", "pickaxe"), 1, "INFUSIONENCHANTMENT"),
    SOUNDING(ImmutableSet.of("pickaxe"), 4, "INFUSIONENCHANTMENT"),
    REFINING(ImmutableSet.of("pickaxe"), 4, "INFUSIONENCHANTMENT"),
    ARCING(ImmutableSet.of("weapon"), 4, "INFUSIONENCHANTMENT"),
    ESSENCE(ImmutableSet.of("weapon"), 5, "INFUSIONENCHANTMENT"),
    VISBATTERY(ImmutableSet.of("chargable"), 3, "?"),
    VISCHARGE(ImmutableSet.of("chargable"), 1, "?"),
    SWIFT(ImmutableSet.of("boots"), 4, "IEARMOR"),
    AGILE(ImmutableSet.of("legs"), 1, "IEARMOR"),
    INFESTED(ImmutableSet.of("chest"), 1, "IETAINT"),
    LAMPLIGHT(ImmutableSet.of("axe", "pickaxe", "shovel"), 1, "INFUSIONENCHANTMENT");
    
    public Set<String> toolClasses;
    public int maxLevel;
    public String research;
    
    private EnumInfusionEnchantment(final Set<String> toolClasses, final int ml, final String research) {
        this.toolClasses = toolClasses;
        this.maxLevel = ml;
        this.research = research;
    }
    
    public static NBTTagList getInfusionEnchantmentTagList(final ItemStack stack) {
        return (stack == null || stack.isEmpty() || stack.getTagCompound() == null) ? null : stack.getTagCompound().getTagList("infench", 10);
    }
    
    public static List<EnumInfusionEnchantment> getInfusionEnchantments(final ItemStack stack) {
        final NBTTagList nbttaglist = getInfusionEnchantmentTagList(stack);
        final List<EnumInfusionEnchantment> list = new ArrayList<EnumInfusionEnchantment>();
        if (nbttaglist != null) {
            for (int j = 0; j < nbttaglist.tagCount(); ++j) {
                final int k = nbttaglist.getCompoundTagAt(j).getShort("id");
                final int l = nbttaglist.getCompoundTagAt(j).getShort("lvl");
                if (k >= 0 && k < values().length) {
                    list.add(values()[k]);
                }
            }
        }
        return list;
    }
    
    public static int getInfusionEnchantmentLevel(final ItemStack stack, final EnumInfusionEnchantment enchantment) {
        final NBTTagList nbttaglist = getInfusionEnchantmentTagList(stack);
        final List<EnumInfusionEnchantment> list = new ArrayList<EnumInfusionEnchantment>();
        if (nbttaglist != null) {
            for (int j = 0; j < nbttaglist.tagCount(); ++j) {
                final int k = nbttaglist.getCompoundTagAt(j).getShort("id");
                final int l = nbttaglist.getCompoundTagAt(j).getShort("lvl");
                if (k >= 0 && k < values().length && values()[k] == enchantment) {
                    return l;
                }
            }
        }
        return 0;
    }
    
    public static void addInfusionEnchantment(final ItemStack stack, final EnumInfusionEnchantment ie, final int level) {
        if (stack == null || stack.isEmpty() || level > ie.maxLevel) {
            return;
        }
        NBTTagList nbttaglist = getInfusionEnchantmentTagList(stack);
        if (nbttaglist != null) {
            int j = 0;
            while (j < nbttaglist.tagCount()) {
                final int k = nbttaglist.getCompoundTagAt(j).getShort("id");
                final int l = nbttaglist.getCompoundTagAt(j).getShort("lvl");
                if (k == ie.ordinal()) {
                    if (level <= l) {
                        return;
                    }
                    nbttaglist.getCompoundTagAt(j).setShort("lvl", (short)level);
                    stack.setTagInfo("infench", nbttaglist);
                    return;
                }
                else {
                    ++j;
                }
            }
        }
        else {
            nbttaglist = new NBTTagList();
        }
        final NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setShort("id", (short)ie.ordinal());
        nbttagcompound.setShort("lvl", (short)level);
        nbttaglist.appendTag(nbttagcompound);
        if (nbttaglist.tagCount() > 0) {
            stack.setTagInfo("infench", nbttaglist);
        }
    }
}
