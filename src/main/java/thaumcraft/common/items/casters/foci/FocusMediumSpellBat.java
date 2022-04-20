// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.casters.foci;

import thaumcraft.api.casters.NodeSetting;
import net.minecraft.entity.Entity;
import thaumcraft.common.entities.monster.EntitySpellBat;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusMedium;

public class FocusMediumSpellBat extends FocusMedium
{
    @Override
    public String getResearch() {
        return "FOCUSSPELLBAT";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.SPELLBAT";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.BEAST;
    }
    
    @Override
    public int getComplexity() {
        return 8;
    }
    
    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET };
    }
    
    @Override
    public boolean execute(final Trajectory trajectory) {
        final EntitySpellBat bat = new EntitySpellBat(this.getRemainingPackage(), this.getSettingValue("target") == 1);
        bat.setPosition(trajectory.source.x, trajectory.source.y, trajectory.source.z);
        return this.getPackage().getCaster().world.spawnEntity(bat);
    }
    
    @Override
    public boolean hasIntermediary() {
        return true;
    }
    
    @Override
    public float getPowerMultiplier() {
        return 0.33f;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        final int[] friend = { 0, 1 };
        final String[] friendDesc = { "focus.common.enemy", "focus.common.friend" };
        return new NodeSetting[] { new NodeSetting("target", "focus.common.target", new NodeSetting.NodeSettingIntList(friend, friendDesc)) };
    }
}