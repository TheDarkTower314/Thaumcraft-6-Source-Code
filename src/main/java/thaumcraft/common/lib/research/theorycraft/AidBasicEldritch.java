package thaumcraft.common.lib.research.theorycraft;

import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;

public class AidBasicEldritch implements ITheorycraftAid
{
    @Override
    public Object getAidObject() {
        return BlocksTC.eldritch;
    }
    
    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[] { CardRealization.class, CardRevelation.class, CardTruth.class };
    }
}
