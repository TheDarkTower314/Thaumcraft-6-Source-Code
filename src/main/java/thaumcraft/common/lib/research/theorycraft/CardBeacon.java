// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.research.theorycraft;

import thaumcraft.api.research.theorycraft.ResearchTableData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
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
        return new TextComponentTranslation("card.beacon.name", new Object[0]).getFormattedText();
    }
    
    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.beacon.text", new Object[0]).getFormattedText();
    }
    
    @Override
    public boolean activate(final EntityPlayer player, final ResearchTableData data) {
        ++data.bonusDraws;
        ++data.penaltyStart;
        return true;
    }
}
