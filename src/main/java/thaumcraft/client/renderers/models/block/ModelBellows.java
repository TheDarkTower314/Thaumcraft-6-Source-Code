package thaumcraft.client.renderers.models.block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;


public class ModelBellows extends ModelBase
{
    public ModelRenderer BottomPlank;
    public ModelRenderer MiddlePlank;
    public ModelRenderer TopPlank;
    public ModelRenderer Bag;
    public ModelRenderer Nozzle;
    
    public ModelBellows() {
        textureWidth = 128;
        textureHeight = 128;
        (BottomPlank = new ModelRenderer(this, 0, 0)).addBox(-6.0f, 0.0f, -6.0f, 12, 2, 12);
        BottomPlank.setRotationPoint(0.0f, 22.0f, 0.0f);
        BottomPlank.setTextureSize(128, 128);
        BottomPlank.mirror = true;
        setRotation(BottomPlank, 0.0f, 0.0f, 0.0f);
        (MiddlePlank = new ModelRenderer(this, 0, 0)).addBox(-6.0f, -1.0f, -6.0f, 12, 2, 12);
        MiddlePlank.setRotationPoint(0.0f, 16.0f, 0.0f);
        MiddlePlank.setTextureSize(128, 128);
        MiddlePlank.mirror = true;
        setRotation(MiddlePlank, 0.0f, 0.0f, 0.0f);
        (TopPlank = new ModelRenderer(this, 0, 0)).addBox(-6.0f, 0.0f, -6.0f, 12, 2, 12);
        TopPlank.setRotationPoint(0.0f, 8.0f, 0.0f);
        TopPlank.setTextureSize(128, 128);
        TopPlank.mirror = true;
        setRotation(TopPlank, 0.0f, 0.0f, 0.0f);
        (Bag = new ModelRenderer(this, 48, 0)).addBox(-10.0f, -12.03333f, -10.0f, 20, 24, 20);
        Bag.setRotationPoint(0.0f, 16.0f, 0.0f);
        Bag.setTextureSize(64, 32);
        Bag.mirror = true;
        setRotation(Bag, 0.0f, 0.0f, 0.0f);
        (Nozzle = new ModelRenderer(this, 0, 36)).addBox(-2.0f, -2.0f, 0.0f, 4, 4, 2);
        Nozzle.setRotationPoint(0.0f, 16.0f, 6.0f);
        Nozzle.setTextureSize(128, 128);
        Nozzle.mirror = true;
        setRotation(Nozzle, 0.0f, 0.0f, 0.0f);
    }
    
    public void render() {
        MiddlePlank.render(0.0625f);
        Nozzle.render(0.0625f);
    }
    
    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
