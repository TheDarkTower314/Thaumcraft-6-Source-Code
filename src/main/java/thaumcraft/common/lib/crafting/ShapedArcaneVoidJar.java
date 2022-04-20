// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.crafting;

import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.crafting.ShapedArcaneRecipe;

public class ShapedArcaneVoidJar extends ShapedArcaneRecipe
{
    public ShapedArcaneVoidJar(final ResourceLocation group, final String res, final int vis, final AspectList crystals, final ItemStack result, final Object... recipe) {
        super(group, res, vis, crystals, result, recipe);
    }
    
    @Override
    public ItemStack getCraftingResult(final InventoryCrafting var1) {
        NBTTagCompound nbt = null;
        for (int a = 0; a < var1.getSizeInventory(); ++a) {
            if (Block.getBlockFromItem(var1.getStackInSlot(a).getItem()) == BlocksTC.jarNormal) {
                nbt = var1.getStackInSlot(a).getTagCompound();
                break;
            }
        }
        final ItemStack res = super.getCraftingResult(var1);
        res.setTagCompound(nbt);
        return res;
    }
}
