package thaumcraft.client.renderers.models.block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;


public class ModelBoreBase extends ModelBase
{
    ModelRenderer Base1;
    ModelRenderer Base2;
    ModelRenderer PillarMid;
    ModelRenderer Pillar2;
    ModelRenderer Pillar3;
    ModelRenderer Pillar4;
    ModelRenderer Pillar1;
    ModelRenderer Nozzle1;
    ModelRenderer Nozzle2;
    
    public ModelBoreBase() {
        textureWidth = 128;
        textureHeight = 64;
        (Base1 = new ModelRenderer(this, 64, 24)).addBox(-8.0f, 0.0f, -8.0f, 16, 2, 16);
        Base1.setRotationPoint(0.0f, 0.0f, 0.0f);
        Base1.setTextureSize(128, 64);
        Base1.mirror = true;
        setRotation(Base1, 0.0f, 0.0f, 0.0f);
        (Base2 = new ModelRenderer(this, 64, 24)).addBox(-8.0f, 0.0f, -8.0f, 16, 2, 16);
        Base2.setRotationPoint(0.0f, 14.0f, 0.0f);
        Base2.setTextureSize(128, 64);
        Base2.mirror = true;
        setRotation(Base2, 0.0f, 0.0f, 0.0f);
        (PillarMid = new ModelRenderer(this, 84, 42)).addBox(-2.5f, 0.0f, -2.5f, 5, 12, 5);
        PillarMid.setRotationPoint(0.0f, 2.0f, 0.0f);
        PillarMid.setTextureSize(128, 64);
        PillarMid.mirror = true;
        setRotation(PillarMid, 0.0f, 0.0f, 0.0f);
        (Pillar2 = new ModelRenderer(this, 64, 42)).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4);
        Pillar2.setRotationPoint(-5.0f, 2.0f, -5.0f);
        Pillar2.setTextureSize(128, 64);
        Pillar2.mirror = true;
        setRotation(Pillar2, 0.0f, 0.0f, 0.0f);
        (Pillar3 = new ModelRenderer(this, 64, 42)).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4);
        Pillar3.setRotationPoint(-5.0f, 2.0f, 5.0f);
        Pillar3.setTextureSize(128, 64);
        Pillar3.mirror = true;
        setRotation(Pillar3, 0.0f, 0.0f, 0.0f);
        (Pillar4 = new ModelRenderer(this, 64, 42)).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4);
        Pillar4.setRotationPoint(5.0f, 2.0f, 5.0f);
        Pillar4.setTextureSize(128, 64);
        Pillar4.mirror = true;
        setRotation(Pillar4, 0.0f, 0.0f, 0.0f);
        (Pillar1 = new ModelRenderer(this, 64, 42)).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4);
        Pillar1.setRotationPoint(5.0f, 2.0f, -5.0f);
        Pillar1.setTextureSize(128, 64);
        Pillar1.mirror = true;
        setRotation(Pillar1, 0.0f, 0.0f, 0.0f);
        (Nozzle1 = new ModelRenderer(this, 106, 42)).addBox(2.5f, -2.0f, -2.0f, 5, 4, 4);
        Nozzle1.setRotationPoint(0.0f, 8.0f, 0.0f);
        Nozzle1.setTextureSize(128, 64);
        Nozzle1.mirror = true;
        setRotation(Nozzle1, 0.0f, 0.0f, 0.0f);
        (Nozzle2 = new ModelRenderer(this, 106, 51)).addBox(7.0f, -2.5f, -2.5f, 1, 5, 5);
        Nozzle2.setRotationPoint(0.0f, 8.0f, 0.0f);
        Nozzle2.setTextureSize(128, 64);
        Nozzle2.mirror = true;
        setRotation(Nozzle2, 0.0f, 0.0f, 0.0f);
    }
    
    public void render() {
        float f5 = 0.0625f;
        Base1.render(f5);
        Base2.render(f5);
        PillarMid.render(f5);
        Pillar2.render(f5);
        Pillar3.render(f5);
        Pillar4.render(f5);
        Pillar1.render(f5);
    }
    
    public void renderNozzle() {
        float f5 = 0.0625f;
        Nozzle1.render(f5);
        Nozzle2.render(f5);
    }
    
    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
