package thaumcraft.api.crafting;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import thaumcraft.api.ThaumcraftInvHelper;



public class IngredientNBTTC extends Ingredient
{
	private ItemStack stack;
    public IngredientNBTTC(ItemStack stack)
    {
        super(stack);
        this.stack = stack;
    }

    @Override
    public boolean apply(@Nullable ItemStack input)
    {
        if (input == null) 
        	return false;
        return  stack.getItem() == input.getItem() &&
        		stack.getItemDamage() == input.getItemDamage() &&
        		ThaumcraftInvHelper.areItemStackTagsEqualRelaxed(stack, input);
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }
}
