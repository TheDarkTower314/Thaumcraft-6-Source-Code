package thaumcraft.client.renderers.models.block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;


public class ModelCentrifuge extends ModelBase
{
    ModelRenderer Crossbar;
    ModelRenderer Dingus1;
    ModelRenderer Dingus2;
    ModelRenderer Core;
    ModelRenderer Top;
    ModelRenderer Bottom;
    
    public ModelCentrifuge() {
        textureWidth = 64;
        textureHeight = 32;
        (Crossbar = new ModelRenderer(this, 16, 0)).addBox(-4.0f, -1.0f, -1.0f, 8, 2, 2);
        Crossbar.setRotationPoint(0.0f, 0.0f, 0.0f);
        Crossbar.setTextureSize(64, 32);
        Crossbar.mirror = true;
        setRotation(Crossbar, 0.0f, 0.0f, 0.0f);
        (Dingus1 = new ModelRenderer(this, 0, 16)).addBox(4.0f, -3.0f, -2.0f, 4, 6, 4);
        Dingus1.setRotationPoint(0.0f, 0.0f, 0.0f);
        Dingus1.setTextureSize(64, 32);
        Dingus1.mirror = true;
        setRotation(Dingus1, 0.0f, 0.0f, 0.0f);
        (Dingus2 = new ModelRenderer(this, 0, 16)).addBox(-8.0f, -3.0f, -2.0f, 4, 6, 4);
        Dingus2.setRotationPoint(0.0f, 0.0f, 0.0f);
        Dingus2.setTextureSize(64, 32);
        Dingus2.mirror = true;
        setRotation(Dingus2, 0.0f, 0.0f, 0.0f);
        (Core = new ModelRenderer(this, 0, 0)).addBox(-1.5f, -4.0f, -1.5f, 3, 8, 3);
        Core.setRotationPoint(0.0f, 0.0f, 0.0f);
        Core.setTextureSize(64, 32);
        Core.mirror = true;
        setRotation(Core, 0.0f, 0.0f, 0.0f);
        (Top = new ModelRenderer(this, 20, 16)).addBox(-4.0f, -8.0f, -4.0f, 8, 4, 8);
        Top.setRotationPoint(0.0f, 0.0f, 0.0f);
        Top.setTextureSize(64, 32);
        Top.mirror = true;
        setRotation(Top, 0.0f, 0.0f, 0.0f);
        (Bottom = new ModelRenderer(this, 20, 16)).addBox(-4.0f, 4.0f, -4.0f, 8, 4, 8);
        Bottom.setRotationPoint(0.0f, 0.0f, 0.0f);
        Bottom.setTextureSize(64, 32);
        Bottom.mirror = true;
        setRotation(Bottom, 0.0f, 0.0f, 0.0f);
    }
    
    public void renderBoxes() {
        Top.render(0.0625f);
        Bottom.render(0.0625f);
    }
    
    public void renderSpinnyBit() {
        Crossbar.render(0.0625f);
        Dingus1.render(0.0625f);
        Dingus2.render(0.0625f);
        Core.render(0.0625f);
    }
    
    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
