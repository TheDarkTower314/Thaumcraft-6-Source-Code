package thaumcraft.common.lib.crafting;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IRegistryDelegate;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.internal.CommonInternals;


public class ThaumcraftCraftingManager
{
    static int ASPECTCAP = 500;
    
    public static CrucibleRecipe findMatchingCrucibleRecipe(EntityPlayer player, AspectList aspects, ItemStack lastDrop) {
        int highest = 0;
        CrucibleRecipe out = null;
        for (Object re : ThaumcraftApi.getCraftingRecipes().values()) {
            if (re != null && re instanceof CrucibleRecipe) {
                CrucibleRecipe recipe = (CrucibleRecipe)re;
                ItemStack temp = lastDrop.copy();
                temp.setCount(1);
                if (player == null || !ThaumcraftCapabilities.knowsResearchStrict(player, recipe.getResearch()) || !recipe.matches(aspects, temp)) {
                    continue;
                }
                int result = recipe.getAspects().visSize();
                if (result <= highest) {
                    continue;
                }
                highest = result;
                out = recipe;
            }
        }
        return out;
    }
    
    public static IArcaneRecipe findMatchingArcaneRecipe(InventoryCrafting matrix, EntityPlayer player) {
        int var2 = 0;
        ItemStack var3 = null;
        ItemStack var4 = null;
        for (int var5 = 0; var5 < 15; ++var5) {
            ItemStack var6 = matrix.getStackInSlot(var5);
            if (!var6.isEmpty()) {
                if (var2 == 0) {
                    var3 = var6;
                }
                if (var2 == 1) {
                    var4 = var6;
                }
                ++var2;
            }
        }
        for (ResourceLocation key : CraftingManager.REGISTRY.getKeys()) {
            IRecipe recipe = CraftingManager.REGISTRY.getObject(key);
            if (recipe != null && recipe instanceof IArcaneRecipe && recipe.matches(matrix, player.world)) {
                return (IArcaneRecipe)recipe;
            }
        }
        return null;
    }
    
    public static ItemStack findMatchingArcaneRecipeResult(InventoryCrafting awb, EntityPlayer player) {
        IArcaneRecipe var13 = findMatchingArcaneRecipe(awb, player);
        return (var13 == null) ? null : var13.getCraftingResult(awb);
    }
    
    public static AspectList findMatchingArcaneRecipeCrystals(InventoryCrafting awb, EntityPlayer player) {
        IArcaneRecipe var13 = findMatchingArcaneRecipe(awb, player);
        return (var13 == null) ? null : var13.getCrystals();
    }
    
    public static int findMatchingArcaneRecipeVis(InventoryCrafting awb, EntityPlayer player) {
        IArcaneRecipe var13 = findMatchingArcaneRecipe(awb, player);
        return (var13 == null) ? 0 : ((var13.getVis() > 0) ? var13.getVis() : var13.getVis());
    }
    
    public static InfusionRecipe findMatchingInfusionRecipe(ArrayList<ItemStack> items, ItemStack input, EntityPlayer player) {
        for (Object recipe : ThaumcraftApi.getCraftingRecipes().values()) {
            if (recipe != null && recipe instanceof InfusionRecipe && ((InfusionRecipe)recipe).matches(items, input, player.world, player)) {
                return (InfusionRecipe)recipe;
            }
        }
        return null;
    }
    
    public static AspectList getObjectTags(ItemStack itemstack) {
        return getObjectTags(itemstack, null);
    }
    
    public static AspectList getObjectTags(ItemStack itemstack, ArrayList<String> history) {
        if (itemstack.isEmpty()) {
            return null;
        }
        int ss = CommonInternals.generateUniqueItemstackId(itemstack);
        AspectList tmp = CommonInternals.objectTags.get(ss);
        if (tmp == null) {
            try {
                ItemStack sc = itemstack.copy();
                sc.setItemDamage(32767);
                ss = CommonInternals.generateUniqueItemstackId(sc);
                tmp = CommonInternals.objectTags.get(ss);
                if (tmp == null) {
                    if (itemstack.getItemDamage() == 32767) {
                        int index = 0;
                        do {
                            sc.setItemDamage(index);
                            ss = CommonInternals.generateUniqueItemstackId(sc);
                            tmp = CommonInternals.objectTags.get(ss);
                        } while (++index < 16 && tmp == null);
                    }
                    if (tmp == null) {
                        sc = itemstack.copy();
                        ss = CommonInternals.generateUniqueItemstackIdStripped(sc);
                        tmp = CommonInternals.objectTags.get(ss);
                        if (tmp == null) {
                            sc = itemstack.copy();
                            sc.setItemDamage(32767);
                            ss = CommonInternals.generateUniqueItemstackIdStripped(sc);
                            tmp = CommonInternals.objectTags.get(ss);
                        }
                    }
                    if (tmp == null) {
                        tmp = generateTags(itemstack, history);
                    }
                }
            }
            catch (Exception ex) {}
        }
        return capAspects(getBonusTags(itemstack, tmp), 500);
    }
    
    private static AspectList capAspects(AspectList sourcetags, int amount) {
        if (sourcetags == null) {
            return sourcetags;
        }
        AspectList out = new AspectList();
        for (Aspect aspect : sourcetags.getAspects()) {
            if (aspect != null) {
                out.merge(aspect, Math.min(amount, sourcetags.getAmount(aspect)));
            }
        }
        return out;
    }
    
    private static AspectList getBonusTags(ItemStack itemstack, AspectList sourcetags) {
        AspectList tmp = new AspectList();
        if (itemstack.isEmpty()) {
            return tmp;
        }
        Item item = itemstack.getItem();
        if (item != null && item instanceof IEssentiaContainerItem && !((IEssentiaContainerItem)item).ignoreContainedAspects()) {
            if (sourcetags != null) {
                sourcetags.aspects.clear();
            }
            tmp = ((IEssentiaContainerItem)item).getAspects(itemstack);
            if (tmp != null && tmp.size() > 0) {
                for (Aspect tag : tmp.copy().getAspects()) {
                    if (tmp.getAmount(tag) <= 0) {
                        tmp.remove(tag);
                    }
                }
            }
        }
        if (tmp == null) {
            tmp = new AspectList();
        }
        if (sourcetags != null) {
            for (Aspect tag : sourcetags.getAspects()) {
                if (tag != null) {
                    tmp.add(tag, sourcetags.getAmount(tag));
                }
            }
        }
        if (item != null && (tmp != null || item == Items.POTIONITEM)) {
            if (item instanceof ItemArmor) {
                tmp.merge(Aspect.PROTECT, ((ItemArmor)item).damageReduceAmount * 4);
            }
            else if (item instanceof ItemSword && ((ItemSword)item).getAttackDamage() + 1.0f > 0.0f) {
                tmp.merge(Aspect.AVERSION, (int)(((ItemSword)item).getAttackDamage() + 1.0f) * 4);
            }
            else if (item instanceof ItemBow) {
                tmp.merge(Aspect.AVERSION, 10).merge(Aspect.FLIGHT, 5);
            }
            else if (item instanceof ItemTool) {
                String mat = ((ItemTool)item).getToolMaterialName();
                for (Item.ToolMaterial tm : Item.ToolMaterial.values()) {
                    if (tm.toString().equals(mat)) {
                        tmp.merge(Aspect.TOOL, (tm.getHarvestLevel() + 1) * 4);
                    }
                }
            }
            else if (item instanceof ItemShears || item instanceof ItemHoe) {
                if (item.getMaxDamage() <= Item.ToolMaterial.WOOD.getMaxUses()) {
                    tmp.merge(Aspect.TOOL, 4);
                }
                else if (item.getMaxDamage() <= Item.ToolMaterial.STONE.getMaxUses() || item.getMaxDamage() <= Item.ToolMaterial.GOLD.getMaxUses()) {
                    tmp.merge(Aspect.TOOL, 8);
                }
                else if (item.getMaxDamage() <= Item.ToolMaterial.IRON.getMaxUses()) {
                    tmp.merge(Aspect.TOOL, 12);
                }
                else {
                    tmp.merge(Aspect.TOOL, 16);
                }
            }
            String[] dyes = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite" };
            int[] ores = OreDictionary.getOreIDs(itemstack);
            if (ores != null && ores.length > 0) {
                Arrays.sort(dyes);
                for (int od : ores) {
                    String s = OreDictionary.getOreName(od);
                    if (s != null && Arrays.binarySearch(dyes, s) >= 0) {
                        tmp.merge(Aspect.SENSES, 5);
                        break;
                    }
                }
            }
            NBTTagList ench = itemstack.getEnchantmentTagList();
            if (item instanceof ItemEnchantedBook) {
                ItemEnchantedBook itemEnchantedBook = (ItemEnchantedBook)item;
                ench = ItemEnchantedBook.getEnchantments(itemstack);
            }
            if (ench != null) {
                int var5 = 0;
                for (int var6 = 0; var6 < ench.tagCount(); ++var6) {
                    short eid = ench.getCompoundTagAt(var6).getShort("id");
                    short lvl = (short)(ench.getCompoundTagAt(var6).getShort("lvl") * 3);
                    Enchantment e = Enchantment.getEnchantmentByID(eid);
                    if (e != null) {
                        if (e == Enchantments.AQUA_AFFINITY) {
                            tmp.merge(Aspect.WATER, lvl);
                        }
                        else if (e == Enchantments.BANE_OF_ARTHROPODS) {
                            tmp.merge(Aspect.BEAST, lvl / 2).merge(Aspect.AVERSION, lvl / 2);
                        }
                        else if (e == Enchantments.BLAST_PROTECTION) {
                            tmp.merge(Aspect.PROTECT, lvl / 2).merge(Aspect.ENTROPY, lvl / 2);
                        }
                        else if (e == Enchantments.EFFICIENCY) {
                            tmp.merge(Aspect.TOOL, lvl);
                        }
                        else if (e == Enchantments.FEATHER_FALLING) {
                            tmp.merge(Aspect.FLIGHT, lvl);
                        }
                        else if (e == Enchantments.FIRE_ASPECT) {
                            tmp.merge(Aspect.FIRE, lvl / 2).merge(Aspect.AVERSION, lvl / 2);
                        }
                        else if (e == Enchantments.FIRE_PROTECTION) {
                            tmp.merge(Aspect.PROTECT, lvl / 2).merge(Aspect.FIRE, lvl / 2);
                        }
                        else if (e == Enchantments.FLAME) {
                            tmp.merge(Aspect.FIRE, lvl);
                        }
                        else if (e == Enchantments.FORTUNE) {
                            tmp.merge(Aspect.DESIRE, lvl);
                        }
                        else if (e == Enchantments.INFINITY) {
                            tmp.merge(Aspect.CRAFT, lvl);
                        }
                        else if (e == Enchantments.KNOCKBACK) {
                            tmp.merge(Aspect.AIR, lvl);
                        }
                        else if (e == Enchantments.LOOTING) {
                            tmp.merge(Aspect.DESIRE, lvl);
                        }
                        else if (e == Enchantments.POWER) {
                            tmp.merge(Aspect.AVERSION, lvl);
                        }
                        else if (e == Enchantments.PROJECTILE_PROTECTION) {
                            tmp.merge(Aspect.PROTECT, lvl);
                        }
                        else if (e == Enchantments.PROTECTION) {
                            tmp.merge(Aspect.PROTECT, lvl);
                        }
                        else if (e == Enchantments.PUNCH) {
                            tmp.merge(Aspect.AIR, lvl);
                        }
                        else if (e == Enchantments.RESPIRATION) {
                            tmp.merge(Aspect.AIR, lvl);
                        }
                        else if (e == Enchantments.SHARPNESS) {
                            tmp.merge(Aspect.AVERSION, lvl);
                        }
                        else if (e == Enchantments.SILK_TOUCH) {
                            tmp.merge(Aspect.EXCHANGE, lvl);
                        }
                        else if (e == Enchantments.THORNS) {
                            tmp.merge(Aspect.AVERSION, lvl);
                        }
                        else if (e == Enchantments.SMITE) {
                            tmp.merge(Aspect.UNDEAD, lvl / 2).merge(Aspect.AVERSION, lvl / 2);
                        }
                        else if (e == Enchantments.UNBREAKING) {
                            tmp.merge(Aspect.EARTH, lvl);
                        }
                        else if (e == Enchantments.DEPTH_STRIDER) {
                            tmp.merge(Aspect.WATER, lvl);
                        }
                        else if (e == Enchantments.LUCK_OF_THE_SEA) {
                            tmp.merge(Aspect.DESIRE, lvl);
                        }
                        else if (e == Enchantments.LURE) {
                            tmp.merge(Aspect.BEAST, lvl);
                        }
                        else if (e == Enchantments.FROST_WALKER) {
                            tmp.merge(Aspect.COLD, lvl);
                        }
                        else if (e == Enchantments.MENDING) {
                            tmp.merge(Aspect.CRAFT, lvl);
                        }
                        if (e.getRarity() == Enchantment.Rarity.UNCOMMON) {
                            var5 += 2;
                        }
                        if (e.getRarity() == Enchantment.Rarity.RARE) {
                            var5 += 4;
                        }
                        if (e.getRarity() == Enchantment.Rarity.VERY_RARE) {
                            var5 += 6;
                        }
                    }
                    var5 += lvl;
                }
                if (var5 > 0) {
                    tmp.merge(Aspect.MAGIC, var5);
                }
            }
        }
        return AspectHelper.cullTags(tmp);
    }
    
    public static void getPotionReagentsRecursive(PotionType potion, HashSet<ItemStack> hashSet) {
        try {
            String mixPredicateName = "net.minecraft.potion.PotionHelper$MixPredicate";
            Class<?> mixPredicateClass = Class.forName(mixPredicateName);
            Field output = ReflectionHelper.findField(mixPredicateClass, ObfuscationReflectionHelper.remapFieldNames(mixPredicateName, "field_185200_c"));
            Field reagent = ReflectionHelper.findField(mixPredicateClass, ObfuscationReflectionHelper.remapFieldNames(mixPredicateName, "field_185199_b"));
            Field input = ReflectionHelper.findField(mixPredicateClass, ObfuscationReflectionHelper.remapFieldNames(mixPredicateName, "field_185198_a"));
            output.setAccessible(true);
            reagent.setAccessible(true);
            input.setAccessible(true);
            for (Object mixpre : PotionHelper.POTION_TYPE_CONVERSIONS) {
                if (((IRegistryDelegate)output.get(mixpre)).get() instanceof PotionType && ((PotionType)((IRegistryDelegate)output.get(mixpre)).get()).getRegistryName() == potion.getRegistryName()) {
                    try {
                        hashSet.add(((Ingredient)reagent.get(mixpre)).getMatchingStacks()[0]);
                        if (((IRegistryDelegate)input.get(mixpre)).get() != PotionTypes.WATER && ((IRegistryDelegate)input.get(mixpre)).get() instanceof PotionType) {
                            getPotionReagentsRecursive((PotionType)((IRegistryDelegate)input.get(mixpre)).get(), hashSet);
                        }
                        break;
                    }
                    catch (Exception ex) {}
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static AspectList generateTags(ItemStack is) {
        AspectList temp = generateTags(is, new ArrayList<String>());
        return temp;
    }
    
    public static AspectList generateTags(ItemStack is, ArrayList<String> history) {
        if (history == null) {
            history = new ArrayList<String>();
        }
        ItemStack stack = is.copy();
        stack.setCount(1);
        try {
            if (stack.getItem().isDamageable() || !stack.getItem().getHasSubtypes()) {
                stack.setItemDamage(32767);
            }
        }
        catch (Exception ex) {}
        if (ThaumcraftApi.exists(stack)) {
            return getObjectTags(stack, history);
        }
        String ss = stack.serializeNBT().toString();
        if (history.contains(ss)) {
            return null;
        }
        history.add(ss);
        if (history.size() < 100) {
            if (stack.getItemDamage() == 32767) {
                stack.setItemDamage(0);
            }
            AspectList ret = generateTagsFromRecipes(stack, history);
            ret = capAspects(ret, 500);
            ThaumcraftApi.registerObjectTag(is, ret);
            return ret;
        }
        return null;
    }
    
    private static AspectList generateTagsFromCrucibleRecipes(ItemStack stack, ArrayList<String> history) {
        CrucibleRecipe cr = ThaumcraftApi.getCrucibleRecipe(stack);
        if (cr == null) {
            return null;
        }
        AspectList ot = cr.getAspects().copy();
        int ss = cr.getRecipeOutput().getCount();
        ItemStack cat = cr.getCatalyst().getMatchingStacks()[0];
        if (cat == null || cat.isEmpty()) {
            return null;
        }
        AspectList ot2 = getObjectTags(cat, history);
        AspectList out = new AspectList();
        if (ot2 != null && ot2.size() > 0) {
            for (Aspect tt : ot2.getAspects()) {
                out.add(tt, ot2.getAmount(tt));
            }
        }
        for (Aspect tt : ot.getAspects()) {
            int amt = (int)(Math.sqrt(ot.getAmount(tt)) / ss);
            out.add(tt, amt);
        }
        for (Aspect as : out.getAspects()) {
            if (out.getAmount(as) <= 0) {
                out.remove(as);
            }
        }
        return out;
    }
    
    private static AspectList generateTagsFromInfusionRecipes(ItemStack stack, ArrayList<String> history) {
        InfusionRecipe cr = ThaumcraftApi.getInfusionRecipe(stack);
        if (cr != null) {
            AspectList ot = cr.getAspects().copy();
            NonNullList<Ingredient> ingredients = NonNullList.create();
            ingredients.add(cr.getRecipeInput());
            ingredients.addAll(cr.getComponents());
            AspectList out = new AspectList();
            AspectList ot2 = getAspectsFromIngredients(ingredients, (ItemStack)cr.getRecipeOutput(), null, history);
            for (Aspect tt : ot2.getAspects()) {
                out.add(tt, ot2.getAmount(tt));
            }
            for (Aspect tt : ot.getAspects()) {
                int amt = (int)(Math.sqrt(ot.getAmount(tt)) / ((ItemStack)cr.getRecipeOutput()).getCount());
                out.add(tt, amt);
            }
            for (Aspect as : out.getAspects()) {
                if (out.getAmount(as) <= 0) {
                    out.remove(as);
                }
            }
            return out;
        }
        return null;
    }
    
    private static AspectList generateTagsFromCraftingRecipes(ItemStack stack, ArrayList<String> history) {
        AspectList ret = null;
        int value = Integer.MAX_VALUE;
        for (ResourceLocation key : CraftingManager.REGISTRY.getKeys()) {
            IRecipe recipe = CraftingManager.REGISTRY.getObject(key);
            if (recipe != null && recipe.getRecipeOutput() != null && Item.getIdFromItem(recipe.getRecipeOutput().getItem()) > 0) {
                if (recipe.getRecipeOutput().getItem() == null) {
                    continue;
                }
                int idR = (recipe.getRecipeOutput().getItemDamage() == 32767) ? 0 : recipe.getRecipeOutput().getItemDamage();
                int idS = (stack.getItemDamage() == 32767) ? 0 : stack.getItemDamage();
                if (recipe.getRecipeOutput().getItem() != stack.getItem() || idR != idS) {
                    continue;
                }
                ArrayList<ItemStack> ingredients = new ArrayList<ItemStack>();
                AspectList ph = new AspectList();
                int cval = 0;
                try {
                    ph = getAspectsFromIngredients(recipe.getIngredients(), recipe.getRecipeOutput(), recipe, history);
                    if (recipe instanceof IArcaneRecipe) {
                        IArcaneRecipe ar = (IArcaneRecipe)recipe;
                        if (ar.getVis() > 0) {
                            ph.add(Aspect.MAGIC, (int)(Math.sqrt(1 + ar.getVis() / 2) / (float)recipe.getRecipeOutput().getCount()));
                        }
                    }
                    for (Aspect as : ph.copy().getAspects()) {
                        if (ph.getAmount(as) <= 0) {
                            ph.remove(as);
                        }
                    }
                    if (ph.visSize() >= value || ph.visSize() <= 0) {
                        continue;
                    }
                    ret = ph;
                    value = ph.visSize();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }
    
    private static AspectList getAspectsFromIngredients(NonNullList<Ingredient> nonNullList, ItemStack recipeOut, IRecipe recipe, ArrayList<String> history) {
        AspectList out = new AspectList();
        AspectList mid = new AspectList();
        NonNullList<ItemStack> exlist = NonNullList.create();
        if (recipe != null) {
            InventoryCrafting inv = new InventoryCrafting(new ContainerFake(), 3, 3);
            int index = 0;
            for (Ingredient is : nonNullList) {
                if (is.getMatchingStacks().length > 0) {
                    inv.setInventorySlotContents(index, is.getMatchingStacks()[0]);
                }
                ++index;
            }
            exlist = recipe.getRemainingItems(inv);
        }
        int index2 = -1;
        for (Ingredient is2 : nonNullList) {
            ++index2;
            if (is2.getMatchingStacks().length <= 0) {
                continue;
            }
            AspectList obj = getObjectTags(is2.getMatchingStacks()[0], history);
            if (obj == null) {
                continue;
            }
            for (Aspect as : obj.getAspects()) {
                if (as != null) {
                    mid.add(as, obj.getAmount(as));
                }
            }
        }
        if (exlist != null) {
            for (ItemStack ri : exlist) {
                if (!ri.isEmpty()) {
                    AspectList obj = getObjectTags(ri, history);
                    for (Aspect as : obj.getAspects()) {
                        mid.reduce(as, obj.getAmount(as));
                    }
                }
            }
        }
        for (Aspect as2 : mid.getAspects()) {
            if (as2 != null) {
                float v = mid.getAmount(as2) * 0.75f / recipeOut.getCount();
                if (v < 1.0f && v > 0.75) {
                    v = 1.0f;
                }
                out.add(as2, (int)v);
            }
        }
        for (Aspect as2 : out.getAspects()) {
            if (out.getAmount(as2) <= 0) {
                out.remove(as2);
            }
        }
        return out;
    }
    
    private static AspectList generateTagsFromRecipes(ItemStack stack, ArrayList<String> history) {
        AspectList ret = null;
        int value = 0;
        ret = generateTagsFromCrucibleRecipes(stack, history);
        if (ret != null) {
            return ret;
        }
        ret = generateTagsFromInfusionRecipes(stack, history);
        if (ret != null) {
            return ret;
        }
        ret = generateTagsFromCraftingRecipes(stack, history);
        return ret;
    }
}
