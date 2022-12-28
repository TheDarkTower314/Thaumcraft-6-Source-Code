package thaumcraft.common.lib.research.theorycraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class CardDragonEgg extends TheorycraftCard
{
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public boolean isAidOnly() {
        return true;
    }
    
    @Override
    public String getLocalizedName() {
        return new TextComponentTranslation("card.dragonegg.name").getFormattedText();
    }
    
    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.dragonegg.text").getFormattedText();
    }
    
    @Override
    public boolean activate(EntityPlayer player, ResearchTableData data) {
        String[] s = ResearchCategories.researchCategories.keySet().toArray(new String[0]);
        for (int a = 0; a < 10; ++a) {
            String cat = s[player.getRNG().nextInt(s.length)];
            data.addTotal(cat, MathHelper.getInt(player.getRNG(), 2, 5));
        }
        return true;
    }
}
