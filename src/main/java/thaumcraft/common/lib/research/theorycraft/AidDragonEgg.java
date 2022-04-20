// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.research.theorycraft;

import thaumcraft.api.research.theorycraft.TheorycraftCard;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;

public class AidDragonEgg implements ITheorycraftAid
{
    @Override
    public Object getAidObject() {
        return new ItemStack(Blocks.DRAGON_EGG);
    }
    
    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[] { CardDragonEgg.class };
    }
}
