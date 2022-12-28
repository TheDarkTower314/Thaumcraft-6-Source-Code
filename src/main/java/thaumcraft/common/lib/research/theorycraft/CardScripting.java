package thaumcraft.common.lib.research.theorycraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.tiles.crafting.TileResearchTable;


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
    public boolean activate(EntityPlayer player, ResearchTableData data) {
        if (data.table != null && ((TileResearchTable)data.table).getStackInSlot(0) != null && ((TileResearchTable)data.table).getStackInSlot(0).getItemDamage() < ((TileResearchTable)data.table).getStackInSlot(0).getMaxDamage() && ((TileResearchTable)data.table).getStackInSlot(1) != null) {
            ((TileResearchTable)data.table).consumeInkFromTable();
            ((TileResearchTable)data.table).consumepaperFromTable();
            data.addTotal(getResearchCategory(), 25);
            return true;
        }
        return false;
    }
}
