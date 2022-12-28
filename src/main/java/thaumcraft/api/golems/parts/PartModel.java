package thaumcraft.api.golems.parts;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.IGolemAPI;


public class PartModel
{
    private ResourceLocation objModel;
    private ResourceLocation texture;
    private EnumAttachPoint attachPoint;
    
    public PartModel(ResourceLocation objModel, ResourceLocation objTexture, EnumAttachPoint attachPoint) {
        this.objModel = objModel;
        texture = objTexture;
        this.attachPoint = attachPoint;
    }
    
    public ResourceLocation getObjModel() {
        return objModel;
    }
    
    public ResourceLocation getTexture() {
        return texture;
    }
    
    public EnumAttachPoint getAttachPoint() {
        return attachPoint;
    }
    
    public boolean useMaterialTextureForObjectPart(String partName) {
        return partName.startsWith("bm");
    }
    
    public void preRenderObjectPart(String partName, IGolemAPI golem, float partialTicks, EnumLimbSide side) {
    }
    
    public void postRenderObjectPart(String partName, IGolemAPI golem, float partialTicks, EnumLimbSide side) {
    }
    
    public float preRenderArmRotationX(IGolemAPI golem, float partialTicks, EnumLimbSide side, float inputRot) {
        return inputRot;
    }
    
    public float preRenderArmRotationY(IGolemAPI golem, float partialTicks, EnumLimbSide side, float inputRot) {
        return inputRot;
    }
    
    public float preRenderArmRotationZ(IGolemAPI golem, float partialTicks, EnumLimbSide side, float inputRot) {
        return inputRot;
    }
    
    public enum EnumAttachPoint
    {
        ARMS, 
        LEGS, 
        BODY, 
        HEAD;
    }
    
    public enum EnumLimbSide
    {
        LEFT, 
        RIGHT, 
        MIDDLE;
    }
}
