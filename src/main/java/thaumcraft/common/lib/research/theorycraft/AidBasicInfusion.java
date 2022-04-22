package thaumcraft.common.lib.research.theorycraft;

import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;

public class AidBasicInfusion implements ITheorycraftAid
{
    @Override
    public Object getAidObject() {
        return BlocksTC.infusionMatrix;
    }
    
    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[] { CardMeasure.class, CardChannel.class, CardInfuse.class };
    }
}
