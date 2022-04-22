// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.baubles;

import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import net.minecraft.world.World;
import baubles.api.BaubleType;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.api.items.IVisDiscountGear;
import baubles.api.IBauble;
import thaumcraft.common.items.ItemTCBase;

public class ItemVoidseerCharm extends ItemTCBase implements IBauble, IVisDiscountGear, IWarpingGear
{
    public ItemVoidseerCharm() {
        super("voidseer_charm");
        maxStackSize = 1;
        canRepair = false;
        setMaxDamage(0);
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.RARE;
    }
    
    public BaubleType getBaubleType(final ItemStack itemstack) {
        return BaubleType.CHARM;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_BLUE + "" + TextFormatting.ITALIC + I18n.translateToLocal("item.voidseer_charm.text"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
    
    public int getVisDiscount(final ItemStack stack, final EntityPlayer player) {
        int q = 0;
        final IPlayerWarp warp = ThaumcraftCapabilities.getWarp(player);
        if (warp != null) {
            final int pw = Math.min(100, warp.get(IPlayerWarp.EnumWarpType.PERMANENT));
            q = (int)(pw / 100.0f * 25.0f);
        }
        return q;
    }
    
    public int getWarp(final ItemStack itemstack, final EntityPlayer player) {
        return getVisDiscount(itemstack, player) / 5;
    }
}
