// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.casters.foci;

import net.minecraft.util.math.RayTraceResult;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.FocusModSplit;

public class FocusModSplitTarget extends FocusModSplit
{
    @Override
    public String getResearch() {
        return "FOCUSSPLIT";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.SPLITTARGET";
    }
    
    @Override
    public int getComplexity() {
        return 4;
    }
    
    @Override
    public EnumSupplyType[] mustBeSupplied() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET };
    }
    
    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET };
    }
    
    @Override
    public RayTraceResult[] supplyTargets() {
        return getParent().supplyTargets();
    }
    
    @Override
    public boolean execute() {
        return true;
    }
}
