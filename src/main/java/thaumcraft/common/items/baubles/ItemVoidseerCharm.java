package thaumcraft.common.items.baubles;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.items.ItemTCBase;


public class ItemVoidseerCharm extends ItemTCBase implements IBauble, IVisDiscountGear, IWarpingGear
{
    public ItemVoidseerCharm() {
        super("voidseer_charm");
        maxStackSize = 1;
        canRepair = false;
        setMaxDamage(0);
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.RARE;
    }
    
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.CHARM;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_BLUE + "" + TextFormatting.ITALIC + I18n.translateToLocal("item.voidseer_charm.text"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
    
    public int getVisDiscount(ItemStack stack, EntityPlayer player) {
        int q = 0;
        IPlayerWarp warp = ThaumcraftCapabilities.getWarp(player);
        if (warp != null) {
            int pw = Math.min(100, warp.get(IPlayerWarp.EnumWarpType.PERMANENT));
            q = (int)(pw / 100.0f * 25.0f);
        }
        return q;
    }
    
    public int getWarp(ItemStack itemstack, EntityPlayer player) {
        return getVisDiscount(itemstack, player) / 5;
    }
}
