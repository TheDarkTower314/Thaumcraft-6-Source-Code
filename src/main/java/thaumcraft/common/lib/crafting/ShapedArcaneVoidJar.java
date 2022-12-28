package thaumcraft.common.lib.crafting;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.ShapedArcaneRecipe;


public class ShapedArcaneVoidJar extends ShapedArcaneRecipe
{
    public ShapedArcaneVoidJar(ResourceLocation group, String res, int vis, AspectList crystals, ItemStack result, Object... recipe) {
        super(group, res, vis, crystals, result, recipe);
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1) {
        NBTTagCompound nbt = null;
        for (int a = 0; a < var1.getSizeInventory(); ++a) {
            if (Block.getBlockFromItem(var1.getStackInSlot(a).getItem()) == BlocksTC.jarNormal) {
                nbt = var1.getStackInSlot(a).getTagCompound();
                break;
            }
        }
        ItemStack res = super.getCraftingResult(var1);
        res.setTagCompound(nbt);
        return res;
    }
}
