// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.consumables;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;
import thaumcraft.api.blocks.ILabelable;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.ItemTCEssentiaContainer;

public class ItemLabel extends ItemTCEssentiaContainer
{
    public ItemLabel() {
        super("label", 1, new String[] { "blank", "filled" });
    }
    
    public String getUnlocalizedName(final ItemStack stack) {
        return super.getUnlocalizedName() + "." + this.getVariantNames()[stack.getItemDamage()];
    }
    
    @Override
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeTabs.SEARCH) {
            items.add(new ItemStack(this, 1, 0));
        }
    }
    
    public EnumActionResult onItemUseFirst(final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final EnumHand hand) {
        if (world.isRemote) {
            return EnumActionResult.PASS;
        }
        final IBlockState bs = world.getBlockState(pos);
        if (bs.getBlock() instanceof ILabelable) {
            if (((ILabelable)bs.getBlock()).applyLabel(player, pos, side, player.getHeldItem(hand))) {
                player.getHeldItem(hand).shrink(1);
                player.inventoryContainer.detectAndSendChanges();
            }
            return EnumActionResult.SUCCESS;
        }
        final TileEntity te = world.getTileEntity(pos);
        if (te instanceof ILabelable) {
            if (((ILabelable)te).applyLabel(player, pos, side, player.getHeldItem(hand))) {
                player.getHeldItem(hand).shrink(1);
                player.inventoryContainer.detectAndSendChanges();
            }
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }
    
    @Override
    public void onUpdate(final ItemStack stack, final World world, final Entity entity, final int par4, final boolean par5) {
    }
    
    @Override
    public void onCreated(final ItemStack stack, final World world, final EntityPlayer player) {
    }
    
    @Override
    public boolean ignoreContainedAspects() {
        return true;
    }
}
