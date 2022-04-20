// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.entity;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;

public class ModelGrappler extends ModelBase
{
    ModelRenderer core;
    ModelRenderer prong1;
    ModelRenderer prong2;
    ModelRenderer prong3;
    
    public ModelGrappler() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        (this.core = new ModelRenderer(this, 0, 0)).addBox(-1.5f, -1.5f, -1.5f, 3, 3, 3);
        this.core.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.core.setTextureSize(this.textureWidth, this.textureHeight);
        this.setRotation(this.core, 0.0f, 0.0f, 0.0f);
        (this.prong1 = new ModelRenderer(this, 0, 10)).addBox(-0.5f, -0.5f, -2.5f, 1, 1, 5);
        this.prong1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.prong1.setTextureSize(this.textureWidth, this.textureHeight);
        this.setRotation(this.prong1, 0.0f, 0.0f, 0.0f);
        (this.prong2 = new ModelRenderer(this, 0, 10)).addBox(-0.5f, -0.5f, -2.5f, 1, 1, 5);
        this.prong2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.prong2.setTextureSize(this.textureWidth, this.textureHeight);
        this.setRotation(this.prong2, 0.0f, 1.5707964f, 0.0f);
        (this.prong3 = new ModelRenderer(this, 0, 10)).addBox(-0.5f, -0.5f, -2.5f, 1, 1, 5);
        this.prong3.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.prong3.setTextureSize(this.textureWidth, this.textureHeight);
        this.setRotation(this.prong3, 1.5707964f, 1.5707964f, 0.0f);
    }
    
    public void render() {
        this.core.render(0.0625f);
        this.prong1.render(0.0625f);
        this.prong2.render(0.0625f);
        this.prong3.render(0.0625f);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
