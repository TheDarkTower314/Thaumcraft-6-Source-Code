package thaumcraft.common.lib.research.theorycraft;

import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.ThaumcraftApi;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardAwareness extends TheorycraftCard
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
        return new TextComponentTranslation("card.awareness.name").getFormattedText();
    }
    
    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.awareness.text").getFormattedText();
    }
    
    @Override
    public boolean activate(EntityPlayer player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), 20);
        if (player.getRNG().nextFloat() < 0.33) {
            data.addTotal("ELDRITCH", MathHelper.getInt(player.getRNG(), 1, 5));
            ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.NORMAL);
        }
        return true;
    }
}
