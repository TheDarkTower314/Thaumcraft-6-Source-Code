package thaumcraft.common.lib.research.theorycraft;

import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;

public class AidBasicAuromancy implements ITheorycraftAid
{
    @Override
    public Object getAidObject() {
        return BlocksTC.wandWorkbench;
    }
    
    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[] { CardFocus.class, CardAwareness.class, CardSpellbinding.class };
    }
}
