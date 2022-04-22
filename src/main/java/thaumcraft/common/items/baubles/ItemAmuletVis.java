// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.baubles;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import net.minecraft.world.World;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.util.NonNullList;
import thaumcraft.api.items.RechargeHelper;
import baubles.api.BaublesApi;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import baubles.api.BaubleType;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import baubles.api.IBauble;
import thaumcraft.common.items.ItemTCBase;

public class ItemAmuletVis extends ItemTCBase implements IBauble
{
    public ItemAmuletVis() {
        super("amulet_vis", "found", "crafted");
        this.maxStackSize = 1;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return (itemstack.getItemDamage() == 0) ? EnumRarity.UNCOMMON : EnumRarity.RARE;
    }
    
    public BaubleType getBaubleType(final ItemStack itemstack) {
        return BaubleType.AMULET;
    }
    
    public void onWornTick(final ItemStack itemstack, final EntityLivingBase player) {
        if (player instanceof EntityPlayer && !player.world.isRemote && player.ticksExisted % ((itemstack.getItemDamage() == 0) ? 40 : 5) == 0) {
            NonNullList<ItemStack> inv = ((EntityPlayer)player).inventory.mainInventory;
            int a = 0;
            while (true) {
                final int n = a;
                final InventoryPlayer inventory = ((EntityPlayer)player).inventory;
                if (n >= InventoryPlayer.getHotbarSize()) {
                    final IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer)player);
                    for (int a2 = 0; a2 < baubles.getSlots(); ++a2) {
                        if (RechargeHelper.rechargeItem(player.world, baubles.getStackInSlot(a2), player.getPosition(), (EntityPlayer)player, 1) > 0.0f) {
                            return;
                        }
                    }
                    inv = ((EntityPlayer)player).inventory.armorInventory;
                    for (int a2 = 0; a2 < inv.size(); ++a2) {
                        if (RechargeHelper.rechargeItem(player.world, inv.get(a2), player.getPosition(), (EntityPlayer)player, 1) > 0.0f) {
                            return;
                        }
                    }
                    break;
                }
                if (RechargeHelper.rechargeItem(player.world, inv.get(a), player.getPosition(), (EntityPlayer)player, 1) > 0.0f) {
                    return;
                }
                ++a;
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.AQUA + I18n.translateToLocal("item.amulet_vis.text"));
    }
}
