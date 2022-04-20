// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.research.theorycraft;

import thaumcraft.api.research.theorycraft.ResearchTableData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
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
        return new TextComponentTranslation("card.spellbinding.name", new Object[0]).getFormattedText();
    }
    
    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.spellbinding.text", new Object[0]).getFormattedText();
    }
    
    @Override
    public boolean initialize(final EntityPlayer player, final ResearchTableData data) {
        return player.experienceLevel > 0;
    }
    
    @Override
    public boolean activate(final EntityPlayer player, final ResearchTableData data) {
        if (player.experienceLevel <= 0) {
            return false;
        }
        final int l = Math.min(5, player.experienceLevel);
        data.addTotal(this.getResearchCategory(), l * 5);
        player.addExperienceLevel(-l);
        return true;
    }
}
