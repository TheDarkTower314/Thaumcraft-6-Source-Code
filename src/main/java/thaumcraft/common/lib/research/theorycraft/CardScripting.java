// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.research.theorycraft;

import thaumcraft.common.tiles.crafting.TileResearchTable;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardScripting extends TheorycraftCard
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
        return new TextComponentTranslation("card.scripting.name").getFormattedText();
    }
    
    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.scripting.text").getFormattedText();
    }
    
    @Override
    public boolean activate(final EntityPlayer player, final ResearchTableData data) {
        if (data.table != null && ((TileResearchTable)data.table).getStackInSlot(0) != null && ((TileResearchTable)data.table).getStackInSlot(0).getItemDamage() < ((TileResearchTable)data.table).getStackInSlot(0).getMaxDamage() && ((TileResearchTable)data.table).getStackInSlot(1) != null) {
            ((TileResearchTable)data.table).consumeInkFromTable();
            ((TileResearchTable)data.table).consumepaperFromTable();
            data.addTotal(getResearchCategory(), 25);
            return true;
        }
        return false;
    }
}
