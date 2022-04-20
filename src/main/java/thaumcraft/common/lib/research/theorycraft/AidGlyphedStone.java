// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.research.theorycraft;

import thaumcraft.api.research.theorycraft.TheorycraftCard;
import net.minecraft.item.ItemStack;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;

public class AidGlyphedStone implements ITheorycraftAid
{
    @Override
    public Object getAidObject() {
        return new ItemStack(BlocksTC.stoneAncientGlyphed);
    }
    
    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[] { CardGlyphs.class };
    }
}
