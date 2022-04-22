package thaumcraft.common.lib.research.theorycraft;

import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.ThaumcraftApi;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardTruth extends TheorycraftCard
{
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getResearchCategory() {
        return "ELDRITCH";
    }
    
    @Override
    public String getLocalizedName() {
        return new TextComponentTranslation("card.truth.name").getFormattedText();
    }
    
    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.truth.text").getFormattedText();
    }
    
    @Override
    public boolean activate(EntityPlayer player, ResearchTableData data) {
        data.addTotal("ELDRITCH", MathHelper.getInt(player.getRNG(), 10, 25));
        ++data.bonusDraws;
        ThaumcraftApi.internalMethods.addWarpToPlayer(player, 3, IPlayerWarp.EnumWarpType.TEMPORARY);
        return true;
    }
}
