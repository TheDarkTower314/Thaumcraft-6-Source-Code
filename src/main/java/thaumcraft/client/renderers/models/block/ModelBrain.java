// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.block;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;

public class ModelBrain extends ModelBase
{
    ModelRenderer Shape1;
    ModelRenderer Shape2;
    ModelRenderer Shape3;
    
    public ModelBrain() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        (this.Shape1 = new ModelRenderer(this, 0, 0)).addBox(0.0f, 0.0f, 0.0f, 12, 10, 16);
        this.Shape1.setRotationPoint(-6.0f, 8.0f, -8.0f);
        this.Shape1.setTextureSize(128, 64);
        this.Shape1.mirror = true;
        this.setRotation(this.Shape1, 0.0f, 0.0f, 0.0f);
        (this.Shape2 = new ModelRenderer(this, 64, 0)).addBox(0.0f, 0.0f, 0.0f, 8, 3, 7);
        this.Shape2.setRotationPoint(-4.0f, 18.0f, 0.0f);
        this.Shape2.setTextureSize(128, 64);
        this.Shape2.mirror = true;
        this.setRotation(this.Shape2, 0.0f, 0.0f, 0.0f);
        (this.Shape3 = new ModelRenderer(this, 0, 32)).addBox(0.0f, 0.0f, 0.0f, 2, 6, 2);
        this.Shape3.setRotationPoint(-1.0f, 18.0f, -2.0f);
        this.Shape3.setTextureSize(128, 64);
        this.Shape3.mirror = true;
        this.setRotation(this.Shape3, 0.4089647f, 0.0f, 0.0f);
    }
    
    public void render() {
        this.Shape1.render(0.0625f);
        this.Shape2.render(0.0625f);
        this.Shape3.render(0.0625f);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
