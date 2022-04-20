// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.crafting;

import net.minecraft.util.NonNullList;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraftforge.common.util.RecipeMatcher;
import baubles.api.IBauble;
import net.minecraft.item.ItemArmor;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import java.util.List;
import thaumcraft.api.ThaumcraftApiHelper;
import java.util.ArrayList;
import thaumcraft.common.lib.events.PlayerEvents;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.item.crafting.Ingredient;
import thaumcraft.api.crafting.InfusionRecipe;

public class InfusionRunicAugmentRecipe extends InfusionRecipe
{
    public InfusionRunicAugmentRecipe() {
        super("RUNICSHIELDING", null, 0, null, Ingredient.EMPTY, new Object[] { "gemAmber", ItemsTC.salisMundus });
    }
    
    public InfusionRunicAugmentRecipe(final ItemStack in) {
        super("RUNICSHIELDING", null, 0, null, in, new Object[] { new ItemStack(ItemsTC.salisMundus), "gemAmber" });
        final int fc = PlayerEvents.getRunicCharge(in);
        if (fc > 0) {
            this.components.clear();
            final ArrayList<ItemStack> com = new ArrayList<ItemStack>();
            this.components.add(Ingredient.fromItem(ItemsTC.salisMundus));
            this.components.add(ThaumcraftApiHelper.getIngredient("gemAmber"));
            int c = 0;
            while (c < fc) {
                ++c;
                this.components.add(ThaumcraftApiHelper.getIngredient("gemAmber"));
            }
        }
    }
    
    @Override
    public boolean matches(final List<ItemStack> input, final ItemStack central, final World world, final EntityPlayer player) {
        return this.getRecipeInput() != null && ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(this.research) && (central.getItem() instanceof ItemArmor || central.getItem() instanceof IBauble) && (this.getRecipeInput() == Ingredient.EMPTY || this.getRecipeInput().apply(central)) && RecipeMatcher.findMatches((List)input, (List)this.getComponents(central)) != null;
    }
    
    @Override
    public Object getRecipeOutput(final EntityPlayer player, final ItemStack input, final List<ItemStack> comps) {
        if (input == null) {
            return null;
        }
        final ItemStack out = input.copy();
        final int base = PlayerEvents.getRunicCharge(input) + 1;
        out.setTagInfo("TC.RUNIC", new NBTTagByte((byte)base));
        return out;
    }
    
    @Override
    public AspectList getAspects(final EntityPlayer player, final ItemStack input, final List<ItemStack> comps) {
        final AspectList out = new AspectList();
        final int vis = 20 + (int)(20.0 * Math.pow(2.0, PlayerEvents.getRunicCharge(input)));
        if (vis > 0) {
            out.add(Aspect.PROTECT, vis);
            out.add(Aspect.CRYSTAL, vis / 2);
            out.add(Aspect.ENERGY, vis / 2);
        }
        return out;
    }
    
    @Override
    public int getInstability(final EntityPlayer player, final ItemStack input, final List<ItemStack> comps) {
        final int i = 5 + PlayerEvents.getRunicCharge(input) / 2;
        return i;
    }
    
    public NonNullList<Ingredient> getComponents(final ItemStack input) {
        final NonNullList<Ingredient> com = NonNullList.create();
        com.add(Ingredient.fromItem(ItemsTC.salisMundus));
        com.add(ThaumcraftApiHelper.getIngredient("gemAmber"));
        final int fc = PlayerEvents.getRunicCharge(input);
        if (fc > 0) {
            for (int c = 0; c < fc; ++c) {
                com.add(ThaumcraftApiHelper.getIngredient("gemAmber"));
            }
        }
        return com;
    }
}
