// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.crafting;

import net.minecraftforge.common.ForgeHooks;
import net.minecraft.util.NonNullList;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.resources.ItemCrystalEssence;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.init.Items;
import java.util.ArrayList;
import net.minecraft.world.World;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeMagicDust extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    public boolean matches(final InventoryCrafting inv, final World worldIn) {
        boolean bowl = false;
        boolean flint = false;
        boolean redstone = false;
        final ArrayList<String> crystals = new ArrayList<String>();
        for (int a = 0; a < 3; ++a) {
            for (int b = 0; b < 3; ++b) {
                if (inv.getStackInRowAndColumn(a, b) != null) {
                    if (!inv.getStackInRowAndColumn(a, b).isEmpty()) {
                        final ItemStack stack = inv.getStackInRowAndColumn(a, b).copy();
                        if (stack.getItem() == Items.BOWL && bowl) {
                            return false;
                        }
                        if (stack.getItem() == Items.BOWL && !bowl) {
                            bowl = true;
                        }
                        else {
                            if (stack.getItem() == Items.FLINT && flint) {
                                return false;
                            }
                            if (stack.getItem() == Items.FLINT && !flint) {
                                flint = true;
                            }
                            else {
                                if (stack.getItem() == Items.REDSTONE && redstone) {
                                    return false;
                                }
                                if (stack.getItem() == Items.REDSTONE && !redstone) {
                                    redstone = true;
                                }
                                else {
                                    if (stack.getItem() != ItemsTC.crystalEssence) {
                                        return false;
                                    }
                                    final ItemCrystalEssence ice = (ItemCrystalEssence)stack.getItem();
                                    if (crystals.contains(ice.getAspects(stack).getAspects()[0].getTag()) || crystals.size() >= 3) {
                                        return false;
                                    }
                                    crystals.add(ice.getAspects(stack).getAspects()[0].getTag());
                                }
                            }
                        }
                    }
                }
            }
        }
        return bowl && redstone && flint && crystals.size() == 3;
    }
    
    public ItemStack getCraftingResult(final InventoryCrafting inv) {
        return new ItemStack(ItemsTC.salisMundus);
    }
    
    public boolean canFit(final int width, final int height) {
        return width * height >= 6;
    }
    
    public ItemStack getRecipeOutput() {
        return new ItemStack(ItemsTC.salisMundus);
    }
    
    public NonNullList<ItemStack> getRemainingItems(final InventoryCrafting inv) {
        final NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < ret.size(); ++i) {
            final ItemStack itemstack = inv.getStackInSlot(i);
            ItemStack itemstack2 = ForgeHooks.getContainerItem(itemstack);
            if (itemstack != null && !itemstack.isEmpty() && (itemstack.getItem() == Items.FLINT || itemstack.getItem() == Items.BOWL)) {
                final ItemStack is = itemstack.copy();
                is.setCount(1);
                itemstack2 = is;
            }
            ret.set(i, itemstack2);
        }
        return ret;
    }
}
