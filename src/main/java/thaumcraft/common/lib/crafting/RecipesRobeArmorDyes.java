package thaumcraft.common.lib.crafting;
import java.util.ArrayList;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipesArmorDyes;
import net.minecraft.world.World;
import net.minecraftforge.oredict.DyeUtils;
import thaumcraft.common.items.armor.ItemRobeArmor;


public class RecipesRobeArmorDyes extends RecipesArmorDyes
{
    public boolean matches(InventoryCrafting par1InventoryCrafting, World par2World) {
        ItemStack itemstack = ItemStack.EMPTY;
        ArrayList arraylist = new ArrayList();
        for (int i = 0; i < par1InventoryCrafting.getSizeInventory(); ++i) {
            ItemStack itemstack2 = par1InventoryCrafting.getStackInSlot(i);
            if (itemstack2 != null && !itemstack2.isEmpty()) {
                if (itemstack2.getItem() instanceof ItemArmor) {
                    ItemArmor itemarmor = (ItemArmor)itemstack2.getItem();
                    if (!(itemarmor instanceof ItemRobeArmor) || !itemstack.isEmpty()) {
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
    
    public ItemStack getCraftingResult(InventoryCrafting par1InventoryCrafting) {
        ItemStack itemstack = ItemStack.EMPTY;
        int[] aint = new int[3];
        int i = 0;
        int j = 0;
        ItemArmor itemarmor = null;
        for (int k = 0; k < par1InventoryCrafting.getSizeInventory(); ++k) {
            ItemStack itemstack2 = par1InventoryCrafting.getStackInSlot(k);
            if (itemstack2 != null && !itemstack2.isEmpty()) {
                if (itemstack2.getItem() instanceof ItemArmor) {
                    itemarmor = (ItemArmor)itemstack2.getItem();
                    if (!(itemarmor instanceof ItemRobeArmor) || !itemstack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    itemstack = itemstack2.copy();
                    itemstack.setCount(1);
                    if (itemarmor.hasColor(itemstack2)) {
                        int l = itemarmor.getColor(itemstack);
                        float f = (l >> 16 & 0xFF) / 255.0f;
                        float f2 = (l >> 8 & 0xFF) / 255.0f;
                        float f3 = (l & 0xFF) / 255.0f;
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
                    float[] afloat = DyeUtils.colorFromStack(itemstack2).get().getColorComponentValues();
                    int j2 = (int)(afloat[0] * 255.0f);
                    int k2 = (int)(afloat[1] * 255.0f);
                    int i2 = (int)(afloat[2] * 255.0f);
                    i += Math.max(j2, Math.max(k2, i2));
                    int[] array = aint;
                    int n = 0;
                    array[n] += j2;
                    int[] array2 = aint;
                    int n2 = 1;
                    array2[n2] += k2;
                    int[] array3 = aint;
                    int n3 = 2;
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
        float f = i / (float)j;
        float f2 = (float)Math.max(k, Math.max(l2, l));
        k = (int)(k * f / f2);
        l2 = (int)(l2 * f / f2);
        l = (int)(l * f / f2);
        int i2 = (k << 8) + l2;
        i2 = (i2 << 8) + l;
        itemarmor.setColor(itemstack, i2);
        return itemstack;
    }
}
