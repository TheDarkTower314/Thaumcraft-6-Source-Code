package thaumcraft.client.renderers.models;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;


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
    
    public ModelCube(int shift) {
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
    
    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
