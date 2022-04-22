// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.seals;

import net.minecraft.world.IBlockAccess;
import thaumcraft.api.golems.seals.ISeal;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.item.Item;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.common.items.ItemTCBase;

public class ItemSealPlacer extends ItemTCBase implements ISealDisplayer
{
    public ItemSealPlacer() {
        super("seal", "blank");
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }
    
    @Override
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeTabs.SEARCH) {
            final String[] vn = this.getVariantNames();
            for (int a = 0; a < vn.length; ++a) {
                items.add(new ItemStack(this, 1, a));
            }
        }
    }
    
    @Override
    public String[] getVariantNames() {
        if (SealHandler.types.size() + 1 != this.VARIANTS.length) {
            final String[] rs = SealHandler.getRegisteredSeals();
            final String[] out = new String[rs.length + 1];
            out[0] = "blank";
            int indx = 1;
            for (final String s : rs) {
                final String[] sp = s.split(":");
                out[indx] = ((sp.length > 1) ? sp[1] : sp[0]);
                ++indx;
            }
            this.VARIANTS = out;
            this.VARIANTS_META = new int[this.VARIANTS.length];
            for (int m = 0; m < this.VARIANTS.length; ++m) {
                this.VARIANTS_META[m] = m;
            }
        }
        return this.VARIANTS;
    }
    
    public static ItemStack getSealStack(final String sealKey) {
        final String[] rs = SealHandler.getRegisteredSeals();
        int indx = 1;
        for (final String s : rs) {
            if (s.equals(sealKey)) {
                return new ItemStack(ItemsTC.seals, 1, indx);
            }
            ++indx;
        }
        return null;
    }
    
    public EnumActionResult onItemUseFirst(final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final EnumHand hand) {
        if (world.isRemote || player.getHeldItem(hand).getItemDamage() == 0 || player.isSneaking()) {
            return EnumActionResult.PASS;
        }
        if (!player.canPlayerEdit(pos, side, player.getHeldItem(hand))) {
            return EnumActionResult.FAIL;
        }
        final String[] rs = SealHandler.getRegisteredSeals();
        ISeal seal = null;
        try {
            seal = SealHandler.getSeal(rs[player.getHeldItem(hand).getItemDamage() - 1]).getClass().newInstance();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        if (seal == null || !seal.canPlaceAt(world, pos, side)) {
            return EnumActionResult.FAIL;
        }
        if (SealHandler.addSealEntity(world, pos, side, seal, player) && !player.capabilities.isCreativeMode) {
            player.getHeldItem(hand).shrink(1);
        }
        return EnumActionResult.SUCCESS;
    }
    
    public boolean doesSneakBypassUse(final ItemStack stack, final IBlockAccess world, final BlockPos pos, final EntityPlayer player) {
        return true;
    }
}
