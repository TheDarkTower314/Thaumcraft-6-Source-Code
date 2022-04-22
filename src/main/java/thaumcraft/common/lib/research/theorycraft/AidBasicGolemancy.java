package thaumcraft.common.lib.research.theorycraft;

import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;

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
