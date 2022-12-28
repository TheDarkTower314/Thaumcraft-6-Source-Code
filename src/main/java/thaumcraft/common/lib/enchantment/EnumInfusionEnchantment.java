package thaumcraft.common.lib.enchantment;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;


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
    
    private EnumInfusionEnchantment(Set<String> toolClasses, int ml, String research) {
        this.toolClasses = toolClasses;
        maxLevel = ml;
        this.research = research;
    }
    
    public static NBTTagList getInfusionEnchantmentTagList(ItemStack stack) {
        return (stack == null || stack.isEmpty() || stack.getTagCompound() == null) ? null : stack.getTagCompound().getTagList("infench", 10);
    }
    
    public static List<EnumInfusionEnchantment> getInfusionEnchantments(ItemStack stack) {
        NBTTagList nbttaglist = getInfusionEnchantmentTagList(stack);
        List<EnumInfusionEnchantment> list = new ArrayList<EnumInfusionEnchantment>();
        if (nbttaglist != null) {
            for (int j = 0; j < nbttaglist.tagCount(); ++j) {
                int k = nbttaglist.getCompoundTagAt(j).getShort("id");
                int l = nbttaglist.getCompoundTagAt(j).getShort("lvl");
                if (k >= 0 && k < values().length) {
                    list.add(values()[k]);
                }
            }
        }
        return list;
    }
    
    public static int getInfusionEnchantmentLevel(ItemStack stack, EnumInfusionEnchantment enchantment) {
        NBTTagList nbttaglist = getInfusionEnchantmentTagList(stack);
        List<EnumInfusionEnchantment> list = new ArrayList<EnumInfusionEnchantment>();
        if (nbttaglist != null) {
            for (int j = 0; j < nbttaglist.tagCount(); ++j) {
                int k = nbttaglist.getCompoundTagAt(j).getShort("id");
                int l = nbttaglist.getCompoundTagAt(j).getShort("lvl");
                if (k >= 0 && k < values().length && values()[k] == enchantment) {
                    return l;
                }
            }
        }
        return 0;
    }
    
    public static void addInfusionEnchantment(ItemStack stack, EnumInfusionEnchantment ie, int level) {
        if (stack == null || stack.isEmpty() || level > ie.maxLevel) {
            return;
        }
        NBTTagList nbttaglist = getInfusionEnchantmentTagList(stack);
        if (nbttaglist != null) {
            int j = 0;
            while (j < nbttaglist.tagCount()) {
                int k = nbttaglist.getCompoundTagAt(j).getShort("id");
                int l = nbttaglist.getCompoundTagAt(j).getShort("lvl");
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
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setShort("id", (short)ie.ordinal());
        nbttagcompound.setShort("lvl", (short)level);
        nbttaglist.appendTag(nbttagcompound);
        if (nbttaglist.tagCount() > 0) {
            stack.setTagInfo("infench", nbttaglist);
        }
    }
}
