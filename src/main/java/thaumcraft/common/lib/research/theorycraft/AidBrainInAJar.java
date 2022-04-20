// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.research.theorycraft;

import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;

public class AidBrainInAJar implements ITheorycraftAid
{
    @Override
    public Object getAidObject() {
        return BlocksTC.jarBrain;
    }
    
    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[] { CardDarkWhispers.class };
    }
}
