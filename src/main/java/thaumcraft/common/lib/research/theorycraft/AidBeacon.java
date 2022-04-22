package thaumcraft.common.lib.research.theorycraft;

import thaumcraft.api.research.theorycraft.TheorycraftCard;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;

public class AidBeacon implements ITheorycraftAid
{
    @Override
    public Object getAidObject() {
        return new ItemStack(Blocks.BEACON);
    }
    
    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[] { CardBeacon.class };
    }
}
