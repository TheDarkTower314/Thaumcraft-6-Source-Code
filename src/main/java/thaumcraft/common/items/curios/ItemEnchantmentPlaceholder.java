// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.curios;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.ItemTCBase;

public class ItemEnchantmentPlaceholder extends ItemTCBase
{
    public ItemEnchantmentPlaceholder() {
        super("enchanted_placeholder", new String[0]);
        this.setMaxStackSize(1);
    }
    
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(final ItemStack stack) {
        return true;
    }
    
    public boolean isEnchantable(final ItemStack stack) {
        return false;
    }
    
    public EnumRarity getRarity(final ItemStack stack) {
        return EnumRarity.RARE;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(TextFormatting.ITALIC + "" + TextFormatting.DARK_AQUA + I18n.translateToLocal("item.enchanted_placeholder.text"));
    }
    
    public boolean canHarvestBlock(final IBlockState blockIn) {
        return true;
    }
    
    public boolean canHarvestBlock(final IBlockState state, final ItemStack stack) {
        return true;
    }
    
    public int getHarvestLevel(final ItemStack stack, final String toolClass, final EntityPlayer player, final IBlockState blockState) {
        return 99;
    }
}
