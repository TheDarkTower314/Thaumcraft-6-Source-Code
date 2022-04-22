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
        textureWidth = 64;
        textureHeight = 32;
        (cube = new ModelRenderer(this, 0, 0)).addBox(-8.0f, -8.0f, -8.0f, 16, 16, 16);
        cube.setRotationPoint(8.0f, 8.0f, 8.0f);
        cube.setTextureSize(64, 32);
        cube.mirror = true;
    }
    
    public ModelCube(final int shift) {
        textureWidth = 64;
        textureHeight = 64;
        (cube = new ModelRenderer(this, 0, shift)).addBox(-8.0f, -8.0f, -8.0f, 16, 16, 16);
        cube.setRotationPoint(0.0f, 0.0f, 0.0f);
        cube.setTextureSize(64, 64);
        cube.mirror = true;
    }
    
    public void render() {
        cube.render(0.0625f);
    }
    
    public void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
