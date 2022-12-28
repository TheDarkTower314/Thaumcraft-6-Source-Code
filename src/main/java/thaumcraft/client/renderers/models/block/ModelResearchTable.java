package thaumcraft.client.renderers.models.block;
import java.awt.Color;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;


public class ModelResearchTable extends ModelBase
{
    ModelRenderer Inkwell;
    ModelRenderer ScrollTube;
    ModelRenderer ScrollRibbon;
    
    public ModelResearchTable() {
        textureWidth = 64;
        textureHeight = 32;
        (Inkwell = new ModelRenderer(this, 0, 16)).addBox(0.0f, 0.0f, 0.0f, 3, 2, 3);
        Inkwell.setRotationPoint(-6.0f, -2.0f, 3.0f);
        Inkwell.mirror = true;
        setRotation(Inkwell, 0.0f, 0.0f, 0.0f);
        (ScrollTube = new ModelRenderer(this, 0, 0)).addBox(-8.0f, -0.5f, 0.0f, 8, 2, 2);
        ScrollTube.setRotationPoint(-2.0f, -2.0f, 2.0f);
        ScrollTube.mirror = true;
        setRotation(ScrollTube, 0.0f, 10.0f, 0.0f);
        (ScrollRibbon = new ModelRenderer(this, 0, 4)).addBox(-4.25f, -0.275f, 0.0f, 1, 2, 2);
        ScrollRibbon.setRotationPoint(-2.0f, -2.0f, 2.0f);
        ScrollRibbon.mirror = true;
        setRotation(ScrollRibbon, 0.0f, 10.0f, 0.0f);
    }
    
    public void renderInkwell() {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        Inkwell.render(0.0625f);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    
    public void renderScroll(int color) {
        GL11.glPushMatrix();
        ScrollTube.render(0.0625f);
        Color c = new Color(color);
        GL11.glColor4f(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 1.0f);
        GL11.glScalef(1.2f, 1.2f, 1.2f);
        ScrollRibbon.render(0.0625f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
