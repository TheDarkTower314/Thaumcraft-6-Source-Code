// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.curios;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.ItemTCBase;

public class ItemCelestialNotes extends ItemTCBase
{
    public ItemCelestialNotes() {
        super("celestial_notes", "sun", "stars_1", "stars_2", "stars_3", "stars_4", "moon_1", "moon_2", "moon_3", "moon_4", "moon_5", "moon_6", "moon_7", "moon_8");
    }
    
    @Override
    public String getUnlocalizedName(final ItemStack itemStack) {
        return "item.celestial_notes";
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        try {
            tooltip.add(TextFormatting.AQUA + I18n.translateToLocal("item.celestial_notes." + getVariantNames()[stack.getItemDamage()] + ".text"));
        }
        catch (final Exception ex) {}
    }
}
