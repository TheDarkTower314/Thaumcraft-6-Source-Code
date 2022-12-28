package thaumcraft.client.renderers.models.entity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;


public class ModelArcaneBore extends ModelBase
{
    ModelRenderer crystal;
    ModelRenderer leg2;
    ModelRenderer tripod;
    ModelRenderer leg3;
    ModelRenderer leg4;
    ModelRenderer leg1;
    ModelRenderer magbase;
    ModelRenderer base;
    ModelRenderer domebase;
    ModelRenderer dome;
    ModelRenderer tip;
    
    public ModelArcaneBore() {
        textureWidth = 64;
        textureHeight = 32;
        (leg2 = new ModelRenderer(this, 20, 10)).addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        leg2.setRotationPoint(0.0f, 12.0f, 0.0f);
        leg2.setTextureSize(64, 32);
        setRotation(leg2, 0.5235988f, 1.570796f, 0.0f);
        (tripod = new ModelRenderer(this, 13, 0)).addBox(-1.5f, 0.0f, -1.5f, 3, 2, 3);
        tripod.setRotationPoint(0.0f, 12.0f, 0.0f);
        tripod.setTextureSize(64, 32);
        setRotation(tripod, 0.0f, 0.0f, 0.0f);
        (leg3 = new ModelRenderer(this, 20, 10)).addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        leg3.setRotationPoint(0.0f, 12.0f, 0.0f);
        leg3.setTextureSize(64, 32);
        setRotation(leg3, 0.5235988f, 3.141593f, 0.0f);
        (leg4 = new ModelRenderer(this, 20, 10)).addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        leg4.setRotationPoint(0.0f, 12.0f, 0.0f);
        leg4.setTextureSize(64, 32);
        setRotation(leg4, 0.5235988f, 4.712389f, 0.0f);
        (leg1 = new ModelRenderer(this, 20, 10)).addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        leg1.setRotationPoint(0.0f, 12.0f, 0.0f);
        leg1.setTextureSize(64, 32);
        setRotation(leg1, 0.5235988f, 0.0f, 0.0f);
        (base = new ModelRenderer(this, 32, 0)).addBox(-3.0f, -6.0f, -3.0f, 6, 6, 6);
        base.setRotationPoint(0.0f, 13.0f, 0.0f);
        base.setTextureSize(64, 32);
        setRotation(base, 0.0f, 0.0f, 0.0f);
        (crystal = new ModelRenderer(this, 32, 25)).addBox(-1.0f, -4.0f, 5.0f, 2, 2, 2);
        crystal.setRotationPoint(0.0f, 0.0f, 0.0f);
        crystal.setTextureSize(64, 32);
        setRotation(crystal, 0.0f, 0.0f, 0.0f);
        (domebase = new ModelRenderer(this, 32, 19)).addBox(-2.0f, -5.0f, 3.0f, 4, 4, 1);
        domebase.setRotationPoint(0.0f, 0.0f, 0.0f);
        domebase.setTextureSize(64, 32);
        setRotation(domebase, 0.0f, 0.0f, 0.0f);
        (dome = new ModelRenderer(this, 44, 16)).addBox(-2.0f, -5.0f, 4.0f, 4, 4, 4);
        dome.setRotationPoint(0.0f, 0.0f, 0.0f);
        dome.setTextureSize(64, 32);
        setRotation(dome, 0.0f, 0.0f, 0.0f);
        (magbase = new ModelRenderer(this, 0, 18)).addBox(-1.0f, -4.0f, -6.0f, 2, 2, 3);
        magbase.setRotationPoint(0.0f, 0.0f, 0.0f);
        magbase.setTextureSize(64, 32);
        magbase.mirror = true;
        setRotation(magbase, 0.0f, 0.0f, 0.0f);
        (tip = new ModelRenderer(this, 0, 9)).addBox(-1.5f, 0.0f, -1.5f, 3, 3, 3);
        tip.setRotationPoint(0.0f, -3.0f, -6.0f);
        tip.setTextureSize(64, 32);
        tip.mirror = true;
        setRotation(tip, -1.570796f, 0.0f, 0.0f);
        base.addChild(crystal);
        base.addChild(dome);
        base.addChild(domebase);
        base.addChild(magbase);
        base.addChild(tip);
    }
    
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        leg2.render(f5);
        tripod.render(f5);
        leg3.render(f5);
        leg4.render(f5);
        leg1.render(f5);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        base.render(f5);
        GL11.glDisable(3042);
    }
    
    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float headpitch, float headyaw, float p_78087_6_, Entity entity) {
        base.rotateAngleY = headpitch / 57.295776f;
        base.rotateAngleX = headyaw / 57.295776f;
    }
}
