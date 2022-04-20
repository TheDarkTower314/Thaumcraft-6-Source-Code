// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.curios;

import net.minecraft.util.EnumActionResult;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import thaumcraft.common.lib.utils.Utils;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.ItemTCBase;

public class ItemLootBag extends ItemTCBase
{
    public ItemLootBag() {
        super("loot_bag", new String[] { "common", "uncommon", "rare" });
        this.setMaxStackSize(16);
    }
    
    public EnumRarity getRarity(final ItemStack stack) {
        switch (stack.getItemDamage()) {
            case 1: {
                return EnumRarity.UNCOMMON;
            }
            case 2: {
                return EnumRarity.RARE;
            }
            default: {
                return EnumRarity.COMMON;
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(I18n.translateToLocal("tc.lootbag"));
    }
    
    public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
        if (!world.isRemote) {
            for (int q = 8 + world.rand.nextInt(5), a = 0; a < q; ++a) {
                final ItemStack is = Utils.generateLoot(player.getHeldItem(hand).getItemDamage(), world.rand);
                if (is != null && !is.isEmpty()) {
                    world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, is.copy()));
                }
            }
            player.playSound(SoundsTC.coins, 0.75f, 1.0f);
        }
        player.getHeldItem(hand).shrink(1);
        return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}
