package thaumcraft.common.lib.research.theorycraft;
import net.minecraft.init.Blocks;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.entities.monster.cult.EntityCultistPortalLesser;


public class AidPortal implements ITheorycraftAid
{
    Object portal;
    
    public AidPortal(Object o) {
        portal = o;
    }
    
    @Override
    public Object getAidObject() {
        return portal;
    }
    
    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[] { CardPortal.class };
    }
    
    public static class AidPortalEnd extends AidPortal
    {
        public AidPortalEnd() {
            super(Blocks.END_PORTAL);
        }
    }
    
    public static class AidPortalNether extends AidPortal
    {
        public AidPortalNether() {
            super(Blocks.PORTAL);
        }
    }
    
    public static class AidPortalCrimson extends AidPortal
    {
        public AidPortalCrimson() {
            super(EntityCultistPortalLesser.class);
        }
    }
}
