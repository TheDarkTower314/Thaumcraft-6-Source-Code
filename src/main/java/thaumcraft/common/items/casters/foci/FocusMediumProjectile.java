package thaumcraft.common.items.casters.foci;
import net.minecraft.entity.Entity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.entities.projectile.EntityFocusProjectile;


public class FocusMediumProjectile extends FocusMedium
{
    @Override
    public String getResearch() {
        return "FOCUSPROJECTILE@2";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.PROJECTILE";
    }
    
    @Override
    public int getComplexity() {
        int c = 4 + (getSettingValue("speed") - 1) / 2;
        switch (getSettingValue("option")) {
            case 1: {
                c += 3;
                break;
            }
            case 2:
            case 3: {
                c += 5;
                break;
            }
        }
        return c;
    }
    
    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET, EnumSupplyType.TRAJECTORY };
    }
    
    @Override
    public boolean execute(Trajectory trajectory) {
        float speed = getSettingValue("speed") / 3.0f;
        FocusPackage p = getRemainingPackage();
        if (p.getCaster() != null) {
            EntityFocusProjectile projectile = new EntityFocusProjectile(p, speed, trajectory, getSettingValue("option"));
            return getPackage().getCaster().world.spawnEntity(projectile);
        }
        return false;
    }
    
    @Override
    public boolean hasIntermediary() {
        return true;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        int[] option = { 0, 1, 2, 3 };
        String[] optionDesc = { "focus.common.none", "focus.projectile.bouncy", "focus.projectile.seeking.hostile", "focus.projectile.seeking.friendly" };
        return new NodeSetting[] { new NodeSetting("option", "focus.common.options", new NodeSetting.NodeSettingIntList(option, optionDesc), "FOCUSPROJECTILE"), new NodeSetting("speed", "focus.projectile.speed", new NodeSetting.NodeSettingIntRange(1, 5)) };
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.MOTION;
    }
}
