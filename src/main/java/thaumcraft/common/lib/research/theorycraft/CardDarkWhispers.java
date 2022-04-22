// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.research.theorycraft;

import java.util.Iterator;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.ThaumcraftApi;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardDarkWhispers extends TheorycraftCard
{
    @Override
    public boolean isAidOnly() {
        return true;
    }
    
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
        return new TextComponentTranslation("card.darkwhisper.name").getFormattedText();
    }
    
    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.darkwhisper.text").getFormattedText();
    }
    
    @Override
    public boolean activate(final EntityPlayer player, final ResearchTableData data) {
        final int l = player.experienceLevel;
        player.addExperienceLevel(-(10 + l));
        if (l > 0) {
            for (final String k : ResearchCategories.researchCategories.keySet()) {
                if (player.getRNG().nextBoolean()) {
                    continue;
                }
                data.addTotal(k, MathHelper.getInt(player.getRNG(), 0, Math.max(1, (int)Math.sqrt(l))));
            }
        }
        data.addTotal("ELDRITCH", MathHelper.getInt(player.getRNG(), Math.max(1, l / 5), Math.max(5, l / 2)));
        ThaumcraftApi.internalMethods.addWarpToPlayer(player, Math.max(1, (int)Math.sqrt(l)), IPlayerWarp.EnumWarpType.NORMAL);
        if (player.getRNG().nextBoolean()) {
            ++data.bonusDraws;
        }
        return true;
    }
}
