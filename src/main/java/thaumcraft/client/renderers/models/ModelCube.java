// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;

public class ModelCube extends ModelBase
{
    ModelRenderer cube;
    
    public ModelCube() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        (this.cube = new ModelRenderer(this, 0, 0)).addBox(-8.0f, -8.0f, -8.0f, 16, 16, 16);
        this.cube.setRotationPoint(8.0f, 8.0f, 8.0f);
        this.cube.setTextureSize(64, 32);
        this.cube.mirror = true;
    }
    
    public ModelCube(final int shift) {
        this.textureWidth = 64;
        this.textureHeight = 64;
        (this.cube = new ModelRenderer(this, 0, shift)).addBox(-8.0f, -8.0f, -8.0f, 16, 16, 16);
        this.cube.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.cube.setTextureSize(64, 64);
        this.cube.mirror = true;
    }
    
    public void render() {
        this.cube.render(0.0625f);
    }
    
    public void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
