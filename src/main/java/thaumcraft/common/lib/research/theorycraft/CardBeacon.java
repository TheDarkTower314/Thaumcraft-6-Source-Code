package thaumcraft.common.lib.research.theorycraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class CardBeacon extends TheorycraftCard
{
    @Override
    public int getInspirationCost() {
        return -2;
    }
    
    @Override
    public boolean isAidOnly() {
        return true;
    }
    
    @Override
    public String getLocalizedName() {
        return new TextComponentTranslation("card.beacon.name").getFormattedText();
    }
    
    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.beacon.text").getFormattedText();
    }
    
    @Override
    public boolean activate(EntityPlayer player, ResearchTableData data) {
        ++data.bonusDraws;
        ++data.penaltyStart;
        return true;
    }
}
