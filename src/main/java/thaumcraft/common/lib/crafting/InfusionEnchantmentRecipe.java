package thaumcraft.common.lib.crafting;
import baubles.api.IBauble;
import com.google.common.collect.Multimap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.world.World;
import net.minecraftforge.common.util.RecipeMatcher;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.IRechargable;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;


public class InfusionEnchantmentRecipe extends InfusionRecipe
{
    EnumInfusionEnchantment enchantment;
    
    public InfusionEnchantmentRecipe(EnumInfusionEnchantment ench, AspectList as, Object... components) {
        super(ench.research, null, 4, as, Ingredient.EMPTY, components);
        enchantment = ench;
    }
    
    public InfusionEnchantmentRecipe(InfusionEnchantmentRecipe recipe, ItemStack in) {
        super(recipe.enchantment.research, null, recipe.instability, recipe.aspects, in, recipe.components.toArray());
        enchantment = recipe.enchantment;
    }
    
    @Override
    public boolean matches(List<ItemStack> input, ItemStack central, World world, EntityPlayer player) {
        if (central == null || central.isEmpty() || !ThaumcraftCapabilities.knowsResearch(player, research)) {
            return false;
        }
        if (EnumInfusionEnchantment.getInfusionEnchantmentLevel(central, enchantment) >= enchantment.maxLevel) {
            return false;
        }
        if (!enchantment.toolClasses.contains("all")) {
            Multimap<String, AttributeModifier> itemMods = central.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
            boolean cool = false;
            if (itemMods != null && itemMods.containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.getName()) && enchantment.toolClasses.contains("weapon")) {
                cool = true;
            }
            if (!cool && central.getItem() instanceof ItemTool) {
                Set<String> tcs = central.getItem().getToolClasses(central);
                for (String tc : tcs) {
                    if (enchantment.toolClasses.contains(tc)) {
                        cool = true;
                        break;
                    }
                }
            }
            if (!cool && central.getItem() instanceof ItemArmor) {
                String at = "none";
                switch (((ItemArmor)central.getItem()).armorType) {
                    case HEAD: {
                        at = "helm";
                        break;
                    }
                    case CHEST: {
                        at = "chest";
                        break;
                    }
                    case LEGS: {
                        at = "legs";
                        break;
                    }
                    case FEET: {
                        at = "boots";
                        break;
                    }
                }
                if (enchantment.toolClasses.contains("armor") || enchantment.toolClasses.contains(at)) {
                    cool = true;
                }
            }
            if (!cool && central.getItem() instanceof IBauble) {
                String at = "none";
                switch (((IBauble)central.getItem()).getBaubleType(central)) {
                    case AMULET: {
                        at = "amulet";
                        break;
                    }
                    case BELT: {
                        at = "belt";
                        break;
                    }
                    case RING: {
                        at = "ring";
                        break;
                    }
                }
                if (enchantment.toolClasses.contains("bauble") || enchantment.toolClasses.contains(at)) {
                    cool = true;
                }
            }
            if (!cool && central.getItem() instanceof IRechargable && enchantment.toolClasses.contains("chargable")) {
                cool = true;
            }
            if (!cool) {
                return false;
            }
        }
        return (getRecipeInput() == Ingredient.EMPTY || getRecipeInput().apply(central)) && RecipeMatcher.findMatches((List)input, (List) getComponents()) != null;
    }
    
    @Override
    public Object getRecipeOutput(EntityPlayer player, ItemStack input, List<ItemStack> comps) {
        if (input == null) {
            return null;
        }
        ItemStack out = input.copy();
        int cl = EnumInfusionEnchantment.getInfusionEnchantmentLevel(out, enchantment);
        if (cl >= enchantment.maxLevel) {
            return null;
        }
        List<EnumInfusionEnchantment> el = EnumInfusionEnchantment.getInfusionEnchantments(input);
        Random rand = new Random(System.nanoTime());
        if (rand.nextInt(10) < el.size()) {
            int base = 1;
            if (input.hasTagCompound()) {
                base += input.getTagCompound().getByte("TC.WARP");
            }
            out.setTagInfo("TC.WARP", new NBTTagByte((byte)base));
        }
        EnumInfusionEnchantment.addInfusionEnchantment(out, enchantment, cl + 1);
        return out;
    }
    
    @Override
    public AspectList getAspects(EntityPlayer player, ItemStack input, List<ItemStack> comps) {
        AspectList out = new AspectList();
        if (input == null || input.isEmpty()) {
            return out;
        }
        int cl = EnumInfusionEnchantment.getInfusionEnchantmentLevel(input, enchantment) + 1;
        if (cl > enchantment.maxLevel) {
            return out;
        }
        List<EnumInfusionEnchantment> el = EnumInfusionEnchantment.getInfusionEnchantments(input);
        int otherEnchantments = el.size();
        if (el.contains(enchantment)) {
            --otherEnchantments;
        }
        float modifier = cl + otherEnchantments * 0.33f;
        for (Aspect a : getAspects().getAspects()) {
            out.add(a, (int)(getAspects().getAmount(a) * modifier));
        }
        return out;
    }
}
