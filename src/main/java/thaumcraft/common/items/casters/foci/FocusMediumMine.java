package thaumcraft.common.items.casters.foci;
import net.minecraft.entity.Entity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.entities.projectile.EntityFocusMine;


public class FocusMediumMine extends FocusMedium
{
    @Override
    public String getResearch() {
        return "FOCUSMINE";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.MINE";
    }
    
    @Override
    public int getComplexity() {
        return 4;
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.TRAP;
    }
    
    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET, EnumSupplyType.TRAJECTORY };
    }
    
    @Override
    public boolean execute(Trajectory trajectory) {
        EntityFocusMine projectile = new EntityFocusMine(getRemainingPackage(), trajectory, getSettingValue("target") == 1);
        return getPackage().getCaster().world.spawnEntity(projectile);
    }
    
    @Override
    public boolean hasIntermediary() {
        return true;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        int[] friend = { 0, 1 };
        String[] friendDesc = { "focus.common.enemy", "focus.common.friend" };
        return new NodeSetting[] { new NodeSetting("target", "focus.common.target", new NodeSetting.NodeSettingIntList(friend, friendDesc)) };
    }
}
