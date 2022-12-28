package thaumcraft.common.lib.research.theorycraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


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
