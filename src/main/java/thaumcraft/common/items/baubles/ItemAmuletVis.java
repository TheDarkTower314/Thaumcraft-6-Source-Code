package thaumcraft.common.items.baubles;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.items.ItemTCBase;


public class ItemAmuletVis extends ItemTCBase implements IBauble
{
    public ItemAmuletVis() {
        super("amulet_vis", "found", "crafted");
        maxStackSize = 1;
        setMaxDamage(0);
        setHasSubtypes(true);
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return (itemstack.getItemDamage() == 0) ? EnumRarity.UNCOMMON : EnumRarity.RARE;
    }
    
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET;
    }
    
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (player instanceof EntityPlayer && !player.world.isRemote && player.ticksExisted % ((itemstack.getItemDamage() == 0) ? 40 : 5) == 0) {
            NonNullList<ItemStack> inv = ((EntityPlayer)player).inventory.mainInventory;
            int a = 0;
            while (true) {
                int n = a;
                InventoryPlayer inventory = ((EntityPlayer)player).inventory;
                if (n >= InventoryPlayer.getHotbarSize()) {
                    IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer)player);
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
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.AQUA + I18n.translateToLocal("item.amulet_vis.text"));
    }
}
