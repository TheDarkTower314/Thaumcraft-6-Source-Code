// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.crafting;

import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.oredict.DyeUtils;
import thaumcraft.common.items.armor.ItemVoidRobeArmor;
import net.minecraft.item.ItemArmor;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.RecipesArmorDyes;

public class RecipesVoidRobeArmorDyes extends RecipesArmorDyes
{
    public boolean matches(final InventoryCrafting par1InventoryCrafting, final World par2World) {
        ItemStack itemstack = ItemStack.EMPTY;
        final ArrayList arraylist = new ArrayList();
        for (int i = 0; i < par1InventoryCrafting.getSizeInventory(); ++i) {
            final ItemStack itemstack2 = par1InventoryCrafting.getStackInSlot(i);
            if (itemstack2 != null && !itemstack2.isEmpty()) {
                if (itemstack2.getItem() instanceof ItemArmor) {
                    final ItemArmor itemarmor = (ItemArmor)itemstack2.getItem();
                    if (!(itemarmor instanceof ItemVoidRobeArmor) || !itemstack.isEmpty()) {
                        return false;
                    }
                    itemstack = itemstack2;
                }
                else {
                    if (!DyeUtils.isDye(itemstack2)) {
                        return false;
                    }
                    arraylist.add(itemstack2);
                }
            }
        }
        return !itemstack.isEmpty() && !arraylist.isEmpty();
    }
    
    public ItemStack getCraftingResult(final InventoryCrafting par1InventoryCrafting) {
        ItemStack itemstack = ItemStack.EMPTY;
        final int[] aint = new int[3];
        int i = 0;
        int j = 0;
        ItemArmor itemarmor = null;
        for (int k = 0; k < par1InventoryCrafting.getSizeInventory(); ++k) {
            final ItemStack itemstack2 = par1InventoryCrafting.getStackInSlot(k);
            if (itemstack2 != null && !itemstack2.isEmpty()) {
                if (itemstack2.getItem() instanceof ItemArmor) {
                    itemarmor = (ItemArmor)itemstack2.getItem();
                    if (!(itemarmor instanceof ItemVoidRobeArmor) || !itemstack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    itemstack = itemstack2.copy();
                    itemstack.setCount(1);
                    if (itemarmor.hasColor(itemstack2)) {
                        final int l = itemarmor.getColor(itemstack);
                        final float f = (l >> 16 & 0xFF) / 255.0f;
                        final float f2 = (l >> 8 & 0xFF) / 255.0f;
                        final float f3 = (l & 0xFF) / 255.0f;
                        i += (int)(Math.max(f, Math.max(f2, f3)) * 255.0f);
                        aint[0] += (int)(f * 255.0f);
                        aint[1] += (int)(f2 * 255.0f);
                        aint[2] += (int)(f3 * 255.0f);
                        ++j;
                    }
                }
                else {
                    if (!DyeUtils.isDye(itemstack2)) {
                        return ItemStack.EMPTY;
                    }
                    final float[] afloat = DyeUtils.colorFromStack(itemstack2).get().getColorComponentValues();
                    final int j2 = (int)(afloat[0] * 255.0f);
                    final int k2 = (int)(afloat[1] * 255.0f);
                    final int i2 = (int)(afloat[2] * 255.0f);
                    i += Math.max(j2, Math.max(k2, i2));
                    final int[] array = aint;
                    final int n = 0;
                    array[n] += j2;
                    final int[] array2 = aint;
                    final int n2 = 1;
                    array2[n2] += k2;
                    final int[] array3 = aint;
                    final int n3 = 2;
                    array3[n3] += i2;
                    ++j;
                }
            }
        }
        if (itemarmor == null) {
            return ItemStack.EMPTY;
        }
        int k = aint[0] / j;
        int l2 = aint[1] / j;
        int l = aint[2] / j;
        final float f = i / (float)j;
        final float f2 = (float)Math.max(k, Math.max(l2, l));
        k = (int)(k * f / f2);
        l2 = (int)(l2 * f / f2);
        l = (int)(l * f / f2);
        int i2 = (k << 8) + l2;
        i2 = (i2 << 8) + l;
        itemarmor.setColor(itemstack, i2);
        return itemstack;
    }
}
