package thaumcraft.client.renderers.models.entity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;


public class ModelGrappler extends ModelBase
{
    ModelRenderer core;
    ModelRenderer prong1;
    ModelRenderer prong2;
    ModelRenderer prong3;
    
    public ModelGrappler() {
        textureWidth = 64;
        textureHeight = 32;
        (core = new ModelRenderer(this, 0, 0)).addBox(-1.5f, -1.5f, -1.5f, 3, 3, 3);
        core.setRotationPoint(0.0f, 0.0f, 0.0f);
        core.setTextureSize(textureWidth, textureHeight);
        setRotation(core, 0.0f, 0.0f, 0.0f);
        (prong1 = new ModelRenderer(this, 0, 10)).addBox(-0.5f, -0.5f, -2.5f, 1, 1, 5);
        prong1.setRotationPoint(0.0f, 0.0f, 0.0f);
        prong1.setTextureSize(textureWidth, textureHeight);
        setRotation(prong1, 0.0f, 0.0f, 0.0f);
        (prong2 = new ModelRenderer(this, 0, 10)).addBox(-0.5f, -0.5f, -2.5f, 1, 1, 5);
        prong2.setRotationPoint(0.0f, 0.0f, 0.0f);
        prong2.setTextureSize(textureWidth, textureHeight);
        setRotation(prong2, 0.0f, 1.5707964f, 0.0f);
        (prong3 = new ModelRenderer(this, 0, 10)).addBox(-0.5f, -0.5f, -2.5f, 1, 1, 5);
        prong3.setRotationPoint(0.0f, 0.0f, 0.0f);
        prong3.setTextureSize(textureWidth, textureHeight);
        setRotation(prong3, 1.5707964f, 1.5707964f, 0.0f);
    }
    
    public void render() {
        core.render(0.0625f);
        prong1.render(0.0625f);
        prong2.render(0.0625f);
        prong3.render(0.0625f);
    }
    
    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
