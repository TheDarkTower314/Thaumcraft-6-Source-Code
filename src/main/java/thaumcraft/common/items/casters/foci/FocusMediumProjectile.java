// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.casters.foci;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.FocusPackage;
import net.minecraft.entity.Entity;
import thaumcraft.common.entities.projectile.EntityFocusProjectile;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.FocusMedium;

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
        int c = 4 + (this.getSettingValue("speed") - 1) / 2;
        switch (this.getSettingValue("option")) {
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
    public boolean execute(final Trajectory trajectory) {
        final float speed = this.getSettingValue("speed") / 3.0f;
        final FocusPackage p = this.getRemainingPackage();
        if (p.getCaster() != null) {
            final EntityFocusProjectile projectile = new EntityFocusProjectile(p, speed, trajectory, this.getSettingValue("option"));
            return this.getPackage().getCaster().world.spawnEntity(projectile);
        }
        return false;
    }
    
    @Override
    public boolean hasIntermediary() {
        return true;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        final int[] option = { 0, 1, 2, 3 };
        final String[] optionDesc = { "focus.common.none", "focus.projectile.bouncy", "focus.projectile.seeking.hostile", "focus.projectile.seeking.friendly" };
        return new NodeSetting[] { new NodeSetting("option", "focus.common.options", new NodeSetting.NodeSettingIntList(option, optionDesc), "FOCUSPROJECTILE"), new NodeSetting("speed", "focus.projectile.speed", new NodeSetting.NodeSettingIntRange(1, 5)) };
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.MOTION;
    }
}
