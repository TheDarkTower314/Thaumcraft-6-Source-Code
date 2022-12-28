package thaumcraft.common.golems.seals;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.seals.ISealConfigToggles;


public class SealGuardAdvanced extends SealGuard implements ISealConfigToggles
{
    ResourceLocation icon;
    
    public SealGuardAdvanced() {
        icon = new ResourceLocation("thaumcraft", "items/seals/seal_guard_advanced");
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:guard_advanced";
    }
    
    @Override
    public ResourceLocation getSealIcon() {
        return icon;
    }
    
    @Override
    public SealToggle[] getToggles() {
        return props;
    }
    
    @Override
    public void setToggle(int indx, boolean value) {
        props[indx].setValue(value);
    }
    
    @Override
    public int[] getGuiCategories() {
        return new int[] { 2, 3, 0, 4 };
    }
    
    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.FIGHTER, EnumGolemTrait.SMART };
    }
}
