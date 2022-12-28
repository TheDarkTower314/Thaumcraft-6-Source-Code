package thaumcraft.common.lib.research.theorycraft;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class AidBasicAlchemy implements ITheorycraftAid
{
    @Override
    public Object getAidObject() {
        return BlocksTC.crucible;
    }
    
    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[] { CardConcentrate.class, CardReactions.class, CardSynthesis.class };
    }
}
