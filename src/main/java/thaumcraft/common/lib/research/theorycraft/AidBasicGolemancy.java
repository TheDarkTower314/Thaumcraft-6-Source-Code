package thaumcraft.common.lib.research.theorycraft;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class AidBasicGolemancy implements ITheorycraftAid
{
    @Override
    public Object getAidObject() {
        return BlocksTC.golemBuilder;
    }
    
    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[] { CardSculpting.class, CardScripting.class, CardSynergy.class };
    }
}
