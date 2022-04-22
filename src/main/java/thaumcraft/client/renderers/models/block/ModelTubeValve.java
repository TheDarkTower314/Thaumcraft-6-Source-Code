// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.block;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;

public class ModelTubeValve extends ModelBase
{
    ModelRenderer ValveRod;
    ModelRenderer ValveRing;
    
    public ModelTubeValve() {
        textureWidth = 64;
        textureHeight = 32;
        (ValveRod = new ModelRenderer(this, 0, 10)).addBox(-1.0f, 2.0f, -1.0f, 2, 2, 2);
        ValveRod.setRotationPoint(0.0f, 0.0f, 0.0f);
        ValveRod.setTextureSize(64, 32);
        ValveRod.mirror = true;
        setRotation(ValveRod, 0.0f, 0.0f, 0.0f);
        (ValveRing = new ModelRenderer(this, 0, 0)).addBox(-2.0f, 4.0f, -2.0f, 4, 1, 4);
        ValveRing.setRotationPoint(0.0f, 0.0f, 0.0f);
        ValveRing.setTextureSize(64, 32);
        ValveRing.mirror = true;
        setRotation(ValveRing, 0.0f, 0.0f, 0.0f);
    }
    
    public void renderRod() {
        ValveRod.render(0.0625f);
    }
    
    public void renderRing() {
        ValveRing.render(0.0625f);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
