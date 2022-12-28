package thaumcraft.common.items.curios;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.items.ItemTCBase;


public class ItemCelestialNotes extends ItemTCBase
{
    public ItemCelestialNotes() {
        super("celestial_notes", "sun", "stars_1", "stars_2", "stars_3", "stars_4", "moon_1", "moon_2", "moon_3", "moon_4", "moon_5", "moon_6", "moon_7", "moon_8");
    }
    
    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return "item.celestial_notes";
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        try {
            tooltip.add(TextFormatting.AQUA + I18n.translateToLocal("item.celestial_notes." + getVariantNames()[stack.getItemDamage()] + ".text"));
        }
        catch (Exception ex) {}
    }
}
