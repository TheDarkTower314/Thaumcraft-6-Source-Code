// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.api.golems.parts;

import thaumcraft.api.golems.IGolemAPI;
import net.minecraft.util.ResourceLocation;

public class PartModel
{
    private ResourceLocation objModel;
    private ResourceLocation texture;
    private EnumAttachPoint attachPoint;
    
    public PartModel(final ResourceLocation objModel, final ResourceLocation objTexture, final EnumAttachPoint attachPoint) {
        this.objModel = objModel;
        this.texture = objTexture;
        this.attachPoint = attachPoint;
    }
    
    public ResourceLocation getObjModel() {
        return this.objModel;
    }
    
    public ResourceLocation getTexture() {
        return this.texture;
    }
    
    public EnumAttachPoint getAttachPoint() {
        return this.attachPoint;
    }
    
    public boolean useMaterialTextureForObjectPart(final String partName) {
        return partName.startsWith("bm");
    }
    
    public void preRenderObjectPart(final String partName, final IGolemAPI golem, final float partialTicks, final EnumLimbSide side) {
    }
    
    public void postRenderObjectPart(final String partName, final IGolemAPI golem, final float partialTicks, final EnumLimbSide side) {
    }
    
    public float preRenderArmRotationX(final IGolemAPI golem, final float partialTicks, final EnumLimbSide side, final float inputRot) {
        return inputRot;
    }
    
    public float preRenderArmRotationY(final IGolemAPI golem, final float partialTicks, final EnumLimbSide side, final float inputRot) {
        return inputRot;
    }
    
    public float preRenderArmRotationZ(final IGolemAPI golem, final float partialTicks, final EnumLimbSide side, final float inputRot) {
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
