package thaumcraft.client.renderers.models.block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;


public class ModelBore extends ModelBase
{
    ModelRenderer Base;
    ModelRenderer Side1;
    ModelRenderer Side2;
    ModelRenderer NozCrossbar;
    ModelRenderer NozFront;
    ModelRenderer NozMid;
    
    public ModelBore() {
        textureWidth = 128;
        textureHeight = 64;
        (Base = new ModelRenderer(this, 0, 32)).addBox(-6.0f, 0.0f, -6.0f, 12, 2, 12);
        Base.setRotationPoint(0.0f, 0.0f, 0.0f);
        Base.setTextureSize(64, 32);
        Base.mirror = true;
        setRotation(Base, 0.0f, 0.0f, 0.0f);
        (Side1 = new ModelRenderer(this, 0, 0)).addBox(-2.0f, 2.0f, -5.5f, 4, 8, 1);
        Side1.setRotationPoint(0.0f, 0.0f, 0.0f);
        Side1.setTextureSize(64, 32);
        Side1.mirror = true;
        setRotation(Side1, 0.0f, 0.0f, 0.0f);
        (Side2 = new ModelRenderer(this, 0, 0)).addBox(-2.0f, 2.0f, 4.5f, 4, 8, 1);
        Side2.setRotationPoint(0.0f, 0.0f, 0.0f);
        Side2.setTextureSize(64, 32);
        Side2.mirror = true;
        setRotation(Side2, 0.0f, 0.0f, 0.0f);
        (NozCrossbar = new ModelRenderer(this, 0, 48)).addBox(-1.0f, -1.0f, -6.0f, 2, 2, 12);
        NozCrossbar.setRotationPoint(0.0f, 8.0f, 0.0f);
        NozCrossbar.setTextureSize(64, 32);
        NozCrossbar.mirror = true;
        setRotation(NozCrossbar, 0.0f, 0.0f, 0.0f);
        (NozFront = new ModelRenderer(this, 30, 14)).addBox(4.0f, -2.5f, -2.5f, 4, 5, 5);
        NozFront.setRotationPoint(0.0f, 8.0f, 0.0f);
        NozFront.setTextureSize(64, 32);
        NozFront.mirror = true;
        setRotation(NozFront, 0.0f, 0.0f, 0.0f);
        (NozMid = new ModelRenderer(this, 0, 14)).addBox(-2.0f, -4.0f, -4.0f, 6, 8, 8);
        NozMid.setRotationPoint(0.0f, 8.0f, 0.0f);
        NozMid.setTextureSize(64, 32);
        NozMid.mirror = true;
        setRotation(NozMid, 0.0f, 0.0f, 0.0f);
    }
    
    public void renderBase() {
        float f5 = 0.0625f;
        Base.render(f5);
        Side1.render(f5);
        Side2.render(f5);
        NozCrossbar.render(f5);
    }
    
    public void renderNozzle() {
        float f5 = 0.0625f;
        NozFront.render(f5);
        NozMid.render(f5);
    }
    
    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
