// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.curios;

import net.minecraft.item.Item;
import net.minecraft.stats.StatList;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.ThaumcraftApi;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
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

public class ItemPechWand extends ItemTCBase
{
    public ItemPechWand() {
        super("pech_wand");
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.RARE;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        tooltip.add(I18n.translateToLocal("item.curio.text"));
    }
    
    public ActionResult<ItemStack> onItemRightClick(final World worldIn, final EntityPlayer player, final EnumHand hand) {
        final IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        if (!knowledge.isResearchKnown("BASEAUROMANCY")) {
            if (!worldIn.isRemote) {
                player.sendMessage(new TextComponentString(TextFormatting.RED + I18n.translateToLocal("not.pechwand")));
            }
            return super.onItemRightClick(worldIn, player, hand);
        }
        if (!player.capabilities.isCreativeMode) {
            player.getHeldItem(hand).shrink(1);
        }
        worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundsTC.learn, SoundCategory.NEUTRAL, 0.5f, 0.4f / (ItemPechWand.itemRand.nextFloat() * 0.4f + 0.8f));
        if (!worldIn.isRemote) {
            if (!knowledge.isResearchKnown("FOCUSPECH")) {
                ThaumcraftApi.internalMethods.progressResearch(player, "FOCUSPECH");
                player.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.translateToLocal("got.pechwand")));
            }
            final int oProg = IPlayerKnowledge.EnumKnowledgeType.OBSERVATION.getProgression();
            final ResearchCategory[] rc = ResearchCategories.researchCategories.values().toArray(new ResearchCategory[0]);
            ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, rc[player.getRNG().nextInt(rc.length)], MathHelper.getInt(player.getRNG(), oProg / 3, oProg / 2));
            final int tProg = IPlayerKnowledge.EnumKnowledgeType.THEORY.getProgression();
            ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, rc[player.getRNG().nextInt(rc.length)], MathHelper.getInt(player.getRNG(), tProg / 5, tProg / 4));
        }
        player.addStat(StatList.getObjectUseStats(this));
        return super.onItemRightClick(worldIn, player, hand);
    }
}
