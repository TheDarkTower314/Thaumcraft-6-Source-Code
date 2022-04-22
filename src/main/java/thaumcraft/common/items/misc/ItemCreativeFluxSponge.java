// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.misc;

import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.entities.EntityFluxRift;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import thaumcraft.api.aura.AuraHelper;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.ItemTCBase;

public class ItemCreativeFluxSponge extends ItemTCBase
{
    public ItemCreativeFluxSponge() {
        super("creative_flux_sponge");
        this.setMaxStackSize(1);
        this.setHasSubtypes(false);
        this.setMaxDamage(0);
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(TextFormatting.GREEN + "Right-click to drain all");
        tooltip.add(TextFormatting.GREEN + "flux from 9x9 chunk area");
        tooltip.add(TextFormatting.DARK_AQUA + "Also removes flux rifts");
        tooltip.add(TextFormatting.DARK_AQUA + "if used while sneaking.");
        tooltip.add(TextFormatting.DARK_PURPLE + "Creative only");
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.EPIC;
    }
    
    public ActionResult<ItemStack> onItemRightClick(final World worldIn, final EntityPlayer playerIn, final EnumHand hand) {
        if (worldIn.isRemote) {
            playerIn.swingArm(hand);
            playerIn.world.playSound(playerIn.posX, playerIn.posY, playerIn.posZ, SoundsTC.craftstart, SoundCategory.PLAYERS, 0.15f, 1.0f, false);
        }
        else {
            int q = 0;
            final BlockPos p = playerIn.getPosition();
            for (int x = -4; x <= 4; ++x) {
                for (int z = -4; z <= 4; ++z) {
                    q += (int)AuraHelper.drainFlux(worldIn, p.add(16 * x, 0, 16 * z), 500.0f, false);
                }
            }
            playerIn.sendMessage(new TextComponentString(TextFormatting.GREEN + "" + q + " flux drained from 81 chunks."));
            if (playerIn.isSneaking()) {
                final List<EntityFluxRift> list = EntityUtils.getEntitiesInRange(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ, null, EntityFluxRift.class, 32.0);
                for (final EntityFluxRift fr : list) {
                    fr.setDead();
                }
                playerIn.sendMessage(new TextComponentString(TextFormatting.DARK_AQUA + "" + list.size() + " flux rifts removed."));
            }
        }
        return super.onItemRightClick(worldIn, playerIn, hand);
    }
}
