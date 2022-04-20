// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.block;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;

public class ModelBoreEmit extends ModelBase
{
    ModelRenderer Knob;
    ModelRenderer Cross1;
    ModelRenderer Cross3;
    ModelRenderer Cross2;
    ModelRenderer Rod;
    
    public ModelBoreEmit() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        (this.Knob = new ModelRenderer(this, 66, 0)).addBox(-2.0f, 12.0f, -2.0f, 4, 4, 4);
        this.Knob.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Knob.setTextureSize(128, 64);
        this.Knob.mirror = true;
        this.setRotation(this.Knob, 0.0f, 0.0f, 0.0f);
        (this.Cross1 = new ModelRenderer(this, 56, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 1, 4);
        this.Cross1.setRotationPoint(0.0f, 8.0f, 0.0f);
        this.Cross1.setTextureSize(128, 64);
        this.Cross1.mirror = true;
        this.setRotation(this.Cross1, 0.0f, 0.0f, 0.0f);
        (this.Cross3 = new ModelRenderer(this, 56, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 1, 4);
        this.Cross3.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Cross3.setTextureSize(128, 64);
        this.Cross3.mirror = true;
        this.setRotation(this.Cross3, 0.0f, 0.0f, 0.0f);
        (this.Cross2 = new ModelRenderer(this, 56, 24)).addBox(-3.0f, 4.0f, -3.0f, 6, 1, 6);
        this.Cross2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Cross2.setTextureSize(128, 64);
        this.Cross2.mirror = true;
        this.setRotation(this.Cross2, 0.0f, 0.0f, 0.0f);
        (this.Rod = new ModelRenderer(this, 56, 0)).addBox(-1.0f, 1.0f, -1.0f, 2, 11, 2);
        this.Rod.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Rod.setTextureSize(128, 64);
        this.Rod.mirror = true;
        this.setRotation(this.Rod, 0.0f, 0.0f, 0.0f);
    }
    
    public void render(final boolean focus) {
        final float f5 = 0.0625f;
        if (focus) {
            this.Knob.render(f5);
        }
        this.Cross1.render(f5);
        this.Cross3.render(f5);
        this.Cross2.render(f5);
        this.Rod.render(f5);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
