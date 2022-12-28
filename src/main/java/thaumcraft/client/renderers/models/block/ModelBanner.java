package thaumcraft.client.renderers.models.block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;


public class ModelBanner extends ModelBase
{
    ModelRenderer B1;
    ModelRenderer B2;
    ModelRenderer Beam;
    public ModelRenderer Banner;
    ModelRenderer Pole;
    
    public ModelBanner() {
        textureWidth = 128;
        textureHeight = 64;
        (B1 = new ModelRenderer(this, 0, 29)).addBox(-5.0f, -7.5f, -1.5f, 2, 3, 3);
        B1.setRotationPoint(0.0f, 0.0f, 0.0f);
        B1.setTextureSize(128, 64);
        B1.mirror = true;
        setRotation(B1, 0.0f, 0.0f, 0.0f);
        (B2 = new ModelRenderer(this, 0, 29)).addBox(3.0f, -7.5f, -1.5f, 2, 3, 3);
        B2.setRotationPoint(0.0f, 0.0f, 0.0f);
        B2.setTextureSize(128, 64);
        B2.mirror = true;
        setRotation(B2, 0.0f, 0.0f, 0.0f);
        (Beam = new ModelRenderer(this, 30, 0)).addBox(-7.0f, -7.0f, -1.0f, 14, 2, 2);
        Beam.setRotationPoint(0.0f, 0.0f, 0.0f);
        Beam.setTextureSize(128, 64);
        Beam.mirror = true;
        setRotation(Beam, 0.0f, 0.0f, 0.0f);
        (Banner = new ModelRenderer(this, 0, 0)).addBox(-7.0f, 0.0f, -0.5f, 14, 28, 1);
        Banner.setRotationPoint(0.0f, -5.0f, 0.0f);
        Banner.setTextureSize(128, 64);
        Banner.mirror = true;
        setRotation(Banner, 0.0f, 0.0f, 0.0f);
        (Pole = new ModelRenderer(this, 62, 0)).addBox(0.0f, 0.0f, -1.0f, 2, 31, 2);
        Pole.setRotationPoint(-1.0f, -7.0f, -2.0f);
        Pole.setTextureSize(128, 64);
        Pole.mirror = true;
        setRotation(Pole, 0.0f, 0.0f, 0.0f);
    }
    
    public void renderPole() {
        Pole.render(0.0625f);
    }
    
    public void renderBeam() {
        Beam.render(0.0625f);
    }
    
    public void renderTabs() {
        B1.render(0.0625f);
        B2.render(0.0625f);
    }
    
    public void renderBanner() {
        Banner.render(0.0625f);
    }
    
    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
