package thaumcraft.client.renderers.models.block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;


public class ModelBoreEmit extends ModelBase
{
    ModelRenderer Knob;
    ModelRenderer Cross1;
    ModelRenderer Cross3;
    ModelRenderer Cross2;
    ModelRenderer Rod;
    
    public ModelBoreEmit() {
        textureWidth = 128;
        textureHeight = 64;
        (Knob = new ModelRenderer(this, 66, 0)).addBox(-2.0f, 12.0f, -2.0f, 4, 4, 4);
        Knob.setRotationPoint(0.0f, 0.0f, 0.0f);
        Knob.setTextureSize(128, 64);
        Knob.mirror = true;
        setRotation(Knob, 0.0f, 0.0f, 0.0f);
        (Cross1 = new ModelRenderer(this, 56, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 1, 4);
        Cross1.setRotationPoint(0.0f, 8.0f, 0.0f);
        Cross1.setTextureSize(128, 64);
        Cross1.mirror = true;
        setRotation(Cross1, 0.0f, 0.0f, 0.0f);
        (Cross3 = new ModelRenderer(this, 56, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 1, 4);
        Cross3.setRotationPoint(0.0f, 0.0f, 0.0f);
        Cross3.setTextureSize(128, 64);
        Cross3.mirror = true;
        setRotation(Cross3, 0.0f, 0.0f, 0.0f);
        (Cross2 = new ModelRenderer(this, 56, 24)).addBox(-3.0f, 4.0f, -3.0f, 6, 1, 6);
        Cross2.setRotationPoint(0.0f, 0.0f, 0.0f);
        Cross2.setTextureSize(128, 64);
        Cross2.mirror = true;
        setRotation(Cross2, 0.0f, 0.0f, 0.0f);
        (Rod = new ModelRenderer(this, 56, 0)).addBox(-1.0f, 1.0f, -1.0f, 2, 11, 2);
        Rod.setRotationPoint(0.0f, 0.0f, 0.0f);
        Rod.setTextureSize(128, 64);
        Rod.mirror = true;
        setRotation(Rod, 0.0f, 0.0f, 0.0f);
    }
    
    public void render(boolean focus) {
        float f5 = 0.0625f;
        if (focus) {
            Knob.render(f5);
        }
        Cross1.render(f5);
        Cross3.render(f5);
        Cross2.render(f5);
        Rod.render(f5);
    }
    
    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
