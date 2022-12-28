package thaumcraft.common.golems.client;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.PartModel;


public class PartModelDarts extends PartModel
{
    public PartModelDarts(ResourceLocation objModel, ResourceLocation objTexture, EnumAttachPoint attachPoint) {
        super(objModel, objTexture, attachPoint);
    }
    
    @Override
    public float preRenderArmRotationX(IGolemAPI golem, float partialTicks, EnumLimbSide side, float inputRot) {
        if (golem.isInCombat()) {
            inputRot = 90.0f - golem.getGolemEntity().prevRotationPitch + inputRot / 10.0f;
        }
        return inputRot;
    }
    
    @Override
    public float preRenderArmRotationY(IGolemAPI golem, float partialTicks, EnumLimbSide side, float inputRot) {
        if (golem.isInCombat()) {
            inputRot /= 10.0f;
        }
        return inputRot;
    }
    
    @Override
    public float preRenderArmRotationZ(IGolemAPI golem, float partialTicks, EnumLimbSide side, float inputRot) {
        if (golem.isInCombat()) {
            inputRot /= 10.0f;
        }
        return inputRot;
    }
}
