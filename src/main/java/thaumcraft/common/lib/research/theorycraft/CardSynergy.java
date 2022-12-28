package thaumcraft.common.lib.research.theorycraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class CardSynergy extends TheorycraftCard
{
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getResearchCategory() {
        return "GOLEMANCY";
    }
    
    @Override
    public String getLocalizedName() {
        return new TextComponentTranslation("card.synergy.name").getFormattedText();
    }
    
    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.synergy.text").getFormattedText();
    }
    
    @Override
    public boolean initialize(EntityPlayer player, ResearchTableData data) {
        int tot = 0;
        tot += data.getTotal("ARTIFICE");
        tot += data.getTotal("ALCHEMY");
        tot += data.getTotal("INFUSION");
        return tot >= 15;
    }
    
    @Override
    public boolean activate(EntityPlayer player, ResearchTableData data) {
        int tot = 0;
        tot += data.getTotal("ARTIFICE");
        tot += data.getTotal("ALCHEMY");
        tot += data.getTotal("INFUSION");
        if (tot >= 15) {
            tot = 15;
            String[] cats = { "ARTIFICE", "ALCHEMY", "INFUSION" };
            int tries = 0;
            while (tot > 0 && tries < 1000) {
                ++tries;
                for (String category : cats) {
                    if (data.getTotal(category) > 0) {
                        data.addTotal(category, -1);
                        if (--tot <= 0) {
                            break;
                        }
                    }
                }
            }
            data.addTotal("GOLEMANCY", 30);
            ++data.penaltyStart;
            return true;
        }
        return false;
    }
}
