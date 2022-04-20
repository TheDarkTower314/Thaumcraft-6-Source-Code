// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.entity;

import org.lwjgl.opengl.GL11;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;

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
        this.textureWidth = 64;
        this.textureHeight = 32;
        (this.leg2 = new ModelRenderer(this, 20, 10)).addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        this.leg2.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.leg2.setTextureSize(64, 32);
        this.setRotation(this.leg2, 0.5235988f, 1.570796f, 0.0f);
        (this.tripod = new ModelRenderer(this, 13, 0)).addBox(-1.5f, 0.0f, -1.5f, 3, 2, 3);
        this.tripod.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.tripod.setTextureSize(64, 32);
        this.setRotation(this.tripod, 0.0f, 0.0f, 0.0f);
        (this.leg3 = new ModelRenderer(this, 20, 10)).addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        this.leg3.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.leg3.setTextureSize(64, 32);
        this.setRotation(this.leg3, 0.5235988f, 3.141593f, 0.0f);
        (this.leg4 = new ModelRenderer(this, 20, 10)).addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        this.leg4.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.leg4.setTextureSize(64, 32);
        this.setRotation(this.leg4, 0.5235988f, 4.712389f, 0.0f);
        (this.leg1 = new ModelRenderer(this, 20, 10)).addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        this.leg1.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.leg1.setTextureSize(64, 32);
        this.setRotation(this.leg1, 0.5235988f, 0.0f, 0.0f);
        (this.base = new ModelRenderer(this, 32, 0)).addBox(-3.0f, -6.0f, -3.0f, 6, 6, 6);
        this.base.setRotationPoint(0.0f, 13.0f, 0.0f);
        this.base.setTextureSize(64, 32);
        this.setRotation(this.base, 0.0f, 0.0f, 0.0f);
        (this.crystal = new ModelRenderer(this, 32, 25)).addBox(-1.0f, -4.0f, 5.0f, 2, 2, 2);
        this.crystal.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.crystal.setTextureSize(64, 32);
        this.setRotation(this.crystal, 0.0f, 0.0f, 0.0f);
        (this.domebase = new ModelRenderer(this, 32, 19)).addBox(-2.0f, -5.0f, 3.0f, 4, 4, 1);
        this.domebase.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.domebase.setTextureSize(64, 32);
        this.setRotation(this.domebase, 0.0f, 0.0f, 0.0f);
        (this.dome = new ModelRenderer(this, 44, 16)).addBox(-2.0f, -5.0f, 4.0f, 4, 4, 4);
        this.dome.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.dome.setTextureSize(64, 32);
        this.setRotation(this.dome, 0.0f, 0.0f, 0.0f);
        (this.magbase = new ModelRenderer(this, 0, 18)).addBox(-1.0f, -4.0f, -6.0f, 2, 2, 3);
        this.magbase.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.magbase.setTextureSize(64, 32);
        this.magbase.mirror = true;
        this.setRotation(this.magbase, 0.0f, 0.0f, 0.0f);
        (this.tip = new ModelRenderer(this, 0, 9)).addBox(-1.5f, 0.0f, -1.5f, 3, 3, 3);
        this.tip.setRotationPoint(0.0f, -3.0f, -6.0f);
        this.tip.setTextureSize(64, 32);
        this.tip.mirror = true;
        this.setRotation(this.tip, -1.570796f, 0.0f, 0.0f);
        this.base.addChild(this.crystal);
        this.base.addChild(this.dome);
        this.base.addChild(this.domebase);
        this.base.addChild(this.magbase);
        this.base.addChild(this.tip);
    }
    
    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.leg2.render(f5);
        this.tripod.render(f5);
        this.leg3.render(f5);
        this.leg4.render(f5);
        this.leg1.render(f5);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        this.base.render(f5);
        GL11.glDisable(3042);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    public void setRotationAngles(final float p_78087_1_, final float p_78087_2_, final float p_78087_3_, final float headpitch, final float headyaw, final float p_78087_6_, final Entity entity) {
        this.base.rotateAngleY = headpitch / 57.295776f;
        this.base.rotateAngleX = headyaw / 57.295776f;
    }
}
