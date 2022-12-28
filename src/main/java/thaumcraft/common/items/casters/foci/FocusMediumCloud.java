package thaumcraft.common.items.casters.foci;
import net.minecraft.entity.Entity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.entities.projectile.EntityFocusCloud;


public class FocusMediumCloud extends FocusMedium
{
    @Override
    public String getResearch() {
        return "FOCUSCLOUD";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.CLOUD";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.ALCHEMY;
    }
    
    @Override
    public int getComplexity() {
        return 4 + getSettingValue("radius") * 2 + getSettingValue("duration") / 5;
    }
    
    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET };
    }
    
    @Override
    public boolean execute(Trajectory trajectory) {
        EntityFocusCloud cloud = new EntityFocusCloud(getRemainingPackage(), trajectory, (float) getSettingValue("radius"), getSettingValue("duration"));
        return getPackage().getCaster().world.spawnEntity(cloud);
    }
    
    @Override
    public boolean hasIntermediary() {
        return true;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] { new NodeSetting("radius", "focus.common.radius", new NodeSetting.NodeSettingIntRange(1, 3)), new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntRange(5, 30)) };
    }
    
    @Override
    public float getPowerMultiplier() {
        return 0.5f;
    }
}
