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
        this.textureWidth = 64;
        this.textureHeight = 32;
        (this.ValveRod = new ModelRenderer(this, 0, 10)).addBox(-1.0f, 2.0f, -1.0f, 2, 2, 2);
        this.ValveRod.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.ValveRod.setTextureSize(64, 32);
        this.ValveRod.mirror = true;
        this.setRotation(this.ValveRod, 0.0f, 0.0f, 0.0f);
        (this.ValveRing = new ModelRenderer(this, 0, 0)).addBox(-2.0f, 4.0f, -2.0f, 4, 1, 4);
        this.ValveRing.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.ValveRing.setTextureSize(64, 32);
        this.ValveRing.mirror = true;
        this.setRotation(this.ValveRing, 0.0f, 0.0f, 0.0f);
    }
    
    public void renderRod() {
        this.ValveRod.render(0.0625f);
    }
    
    public void renderRing() {
        this.ValveRing.render(0.0625f);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
