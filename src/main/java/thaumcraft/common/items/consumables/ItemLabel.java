package thaumcraft.common.items.consumables;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.blocks.ILabelable;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCEssentiaContainer;


public class ItemLabel extends ItemTCEssentiaContainer
{
    public ItemLabel() {
        super("label", 1, "blank", "filled");
    }
    
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + getVariantNames()[stack.getItemDamage()];
    }
    
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeTabs.SEARCH) {
            items.add(new ItemStack(this, 1, 0));
        }
    }
    
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (world.isRemote) {
            return EnumActionResult.PASS;
        }
        IBlockState bs = world.getBlockState(pos);
        if (bs.getBlock() instanceof ILabelable) {
            if (((ILabelable)bs.getBlock()).applyLabel(player, pos, side, player.getHeldItem(hand))) {
                player.getHeldItem(hand).shrink(1);
                player.inventoryContainer.detectAndSendChanges();
            }
            return EnumActionResult.SUCCESS;
        }
        TileEntity te = world.getTileEntity(pos);
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
    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
    }
    
    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
    }
    
    @Override
    public boolean ignoreContainedAspects() {
        return true;
    }
}
