package thaumcraft.common.lib.research.theorycraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class CardSpellbinding extends TheorycraftCard
{
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getResearchCategory() {
        return "AUROMANCY";
    }
    
    @Override
    public String getLocalizedName() {
        return new TextComponentTranslation("card.spellbinding.name").getFormattedText();
    }
    
    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.spellbinding.text").getFormattedText();
    }
    
    @Override
    public boolean initialize(EntityPlayer player, ResearchTableData data) {
        return player.experienceLevel > 0;
    }
    
    @Override
    public boolean activate(EntityPlayer player, ResearchTableData data) {
        if (player.experienceLevel <= 0) {
            return false;
        }
        int l = Math.min(5, player.experienceLevel);
        data.addTotal(getResearchCategory(), l * 5);
        player.addExperienceLevel(-l);
        return true;
    }
}
