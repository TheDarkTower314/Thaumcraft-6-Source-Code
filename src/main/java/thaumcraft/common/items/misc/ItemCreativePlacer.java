package thaumcraft.common.items.misc;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.items.ItemTCBase;


public class ItemCreativePlacer extends ItemTCBase
{
    public ItemCreativePlacer() {
        super("creative_placer", "obelisk", "node", "caster");
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(TextFormatting.DARK_PURPLE + "Creative only");
    }
    
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        IBlockState bs = world.getBlockState(pos);
        if (!bs.getMaterial().isSolid()) {
            return EnumActionResult.FAIL;
        }
        if (world.isRemote) {
            return EnumActionResult.PASS;
        }
        pos = pos.offset(side);
        bs = world.getBlockState(pos);
        if (!player.canPlayerEdit(pos, side, player.getHeldItem(hand))) {
            return EnumActionResult.FAIL;
        }
        if (!bs.getBlock().isReplaceable(world, pos)) {
            return EnumActionResult.FAIL;
        }
        if (player.getHeldItem(hand).getItemDamage() == 0 && !world.getBlockState(pos.down()).getMaterial().isSolid()) {
            return EnumActionResult.FAIL;
        }
        world.setBlockToAir(pos);
        player.getHeldItem(hand).getItemDamage();
        return EnumActionResult.SUCCESS;
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.EPIC;
    }
}
