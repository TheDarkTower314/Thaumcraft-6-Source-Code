package thaumcraft.common.items.misc;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;


public class ItemCreativeFluxSponge extends ItemTCBase
{
    public ItemCreativeFluxSponge() {
        super("creative_flux_sponge");
        setMaxStackSize(1);
        setHasSubtypes(false);
        setMaxDamage(0);
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(TextFormatting.GREEN + "Right-click to drain all");
        tooltip.add(TextFormatting.GREEN + "flux from 9x9 chunk area");
        tooltip.add(TextFormatting.DARK_AQUA + "Also removes flux rifts");
        tooltip.add(TextFormatting.DARK_AQUA + "if used while sneaking.");
        tooltip.add(TextFormatting.DARK_PURPLE + "Creative only");
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.EPIC;
    }
    
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (worldIn.isRemote) {
            playerIn.swingArm(hand);
            playerIn.world.playSound(playerIn.posX, playerIn.posY, playerIn.posZ, SoundsTC.craftstart, SoundCategory.PLAYERS, 0.15f, 1.0f, false);
        }
        else {
            int q = 0;
            BlockPos p = playerIn.getPosition();
            for (int x = -4; x <= 4; ++x) {
                for (int z = -4; z <= 4; ++z) {
                    q += (int)AuraHelper.drainFlux(worldIn, p.add(16 * x, 0, 16 * z), 500.0f, false);
                }
            }
            playerIn.sendMessage(new TextComponentString(TextFormatting.GREEN + "" + q + " flux drained from 81 chunks."));
            if (playerIn.isSneaking()) {
                List<EntityFluxRift> list = EntityUtils.getEntitiesInRange(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ, null, EntityFluxRift.class, 32.0);
                for (EntityFluxRift fr : list) {
                    fr.setDead();
                }
                playerIn.sendMessage(new TextComponentString(TextFormatting.DARK_AQUA + "" + list.size() + " flux rifts removed."));
            }
        }
        return super.onItemRightClick(worldIn, playerIn, hand);
    }
}
