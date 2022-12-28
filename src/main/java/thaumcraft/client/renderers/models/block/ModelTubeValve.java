package thaumcraft.client.renderers.models.block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;


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
    
    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
