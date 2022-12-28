package thaumcraft.common.items.curios;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;


public class ItemPechWand extends ItemTCBase
{
    public ItemPechWand() {
        super("pech_wand");
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.RARE;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.translateToLocal("item.curio.text"));
    }
    
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
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
            int oProg = IPlayerKnowledge.EnumKnowledgeType.OBSERVATION.getProgression();
            ResearchCategory[] rc = ResearchCategories.researchCategories.values().toArray(new ResearchCategory[0]);
            ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, rc[player.getRNG().nextInt(rc.length)], MathHelper.getInt(player.getRNG(), oProg / 3, oProg / 2));
            int tProg = IPlayerKnowledge.EnumKnowledgeType.THEORY.getProgression();
            ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, rc[player.getRNG().nextInt(rc.length)], MathHelper.getInt(player.getRNG(), tProg / 5, tProg / 4));
        }
        player.addStat(StatList.getObjectUseStats(this));
        return super.onItemRightClick(worldIn, player, hand);
    }
}
