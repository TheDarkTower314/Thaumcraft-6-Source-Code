package thaumcraft.client.renderers.models.block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;


public class ModelJar extends ModelBase
{
    public ModelRenderer Core;
    public ModelRenderer Brine;
    public ModelRenderer Lid;
    public ModelRenderer LidExtension;
    
    public ModelJar() {
        textureWidth = 64;
        textureHeight = 32;
        (Core = new ModelRenderer(this, 0, 0)).addBox(-5.0f, -12.0f, -5.0f, 10, 12, 10);
        Core.setRotationPoint(0.0f, 0.0f, 0.0f);
        Core.setTextureSize(64, 32);
        Core.mirror = true;
        setRotation(Core, 0.0f, 0.0f, 0.0f);
        (Brine = new ModelRenderer(this, 0, 0)).addBox(-4.0f, -11.0f, -4.0f, 8, 10, 8);
        Brine.setRotationPoint(0.0f, 0.0f, 0.0f);
        Brine.setTextureSize(64, 32);
        Brine.mirror = true;
        setRotation(Brine, 0.0f, 0.0f, 0.0f);
        (Lid = new ModelRenderer(this, 32, 24)).addBox(-3.0f, 0.0f, -3.0f, 6, 2, 6);
        Lid.setRotationPoint(0.0f, -14.0f, 0.0f);
        Lid.setTextureSize(64, 32);
        Lid.mirror = true;
        (LidExtension = new ModelRenderer(this, 0, 23)).addBox(-2.0f, -16.0f, -2.0f, 4, 2, 4);
        LidExtension.setRotationPoint(0.0f, 0.0f, 0.0f);
        LidExtension.setTextureSize(64, 32);
        LidExtension.mirror = true;
    }
    
    public void renderBrine() {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        Brine.render(0.0625f);
        GL11.glDisable(3042);
    }
    
    public void renderLidExtension() {
        LidExtension.render(0.0625f);
    }
    
    public void renderLidBrace() {
        Lid.render(0.0625f);
    }
    
    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
