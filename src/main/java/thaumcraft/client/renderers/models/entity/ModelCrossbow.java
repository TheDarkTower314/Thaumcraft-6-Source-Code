package thaumcraft.client.renderers.models.entity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.construct.EntityTurretCrossbow;


public class ModelCrossbow extends ModelBase
{
    ModelRenderer crossl3;
    ModelRenderer crossr3;
    ModelRenderer loadbarcross;
    ModelRenderer loadbarl;
    ModelRenderer loadbarr;
    ModelRenderer barrel;
    ModelRenderer basebarcross;
    ModelRenderer ammobox;
    ModelRenderer crossbow;
    ModelRenderer basebarr;
    ModelRenderer basebarl;
    ModelRenderer crossl1;
    ModelRenderer crossl2;
    ModelRenderer crossr1;
    ModelRenderer crossr2;
    ModelRenderer tripod;
    ModelRenderer leg3;
    ModelRenderer leg4;
    ModelRenderer leg1;
    ModelRenderer leg2;
    
    public ModelCrossbow() {
        textureWidth = 64;
        textureHeight = 32;
        (crossbow = new ModelRenderer(this, 28, 14)).addBox(-2.0f, 0.0f, -7.0f, 4, 2, 14);
        crossbow.setRotationPoint(0.0f, 10.0f, 0.0f);
        crossbow.setTextureSize(64, 32);
        crossbow.mirror = true;
        setRotation(crossbow, 0.0f, 0.0f, 0.0f);
        (basebarr = new ModelRenderer(this, 40, 23)).addBox(-1.0f, 0.0f, 7.0f, 1, 2, 5);
        basebarr.setRotationPoint(0.0f, 0.0f, 0.0f);
        basebarr.setTextureSize(64, 32);
        basebarr.mirror = true;
        setRotation(basebarr, 0.0f, -0.1396263f, 0.0f);
        (basebarl = new ModelRenderer(this, 40, 23)).addBox(0.0f, 0.0f, 7.0f, 1, 2, 5);
        basebarl.setRotationPoint(0.0f, 0.0f, 0.0f);
        basebarl.setTextureSize(64, 32);
        basebarl.mirror = true;
        setRotation(basebarl, 0.0f, 0.1396263f, 0.0f);
        (barrel = new ModelRenderer(this, 20, 28)).addBox(-1.0f, -1.0f, -8.0f, 2, 2, 2);
        barrel.setRotationPoint(0.0f, 0.0f, 0.0f);
        barrel.setTextureSize(64, 32);
        barrel.mirror = true;
        setRotation(barrel, 0.0f, 0.0f, 0.0f);
        (basebarcross = new ModelRenderer(this, 0, 13)).addBox(-2.0f, 0.5f, 10.0f, 4, 1, 1);
        basebarcross.setRotationPoint(0.0f, 0.0f, 0.0f);
        basebarcross.setTextureSize(64, 32);
        basebarcross.mirror = true;
        setRotation(basebarcross, 0.0f, 0.0f, 0.0f);
        (ammobox = new ModelRenderer(this, 38, 0)).addBox(-2.0f, -5.0f, -6.0f, 4, 5, 9);
        ammobox.setRotationPoint(0.0f, 0.0f, 0.0f);
        ammobox.setTextureSize(64, 32);
        ammobox.mirror = true;
        setRotation(ammobox, 0.0f, 0.0f, 0.0f);
        (loadbarcross = new ModelRenderer(this, 0, 13)).addBox(-2.0f, -8.5f, -0.5f, 4, 1, 1);
        loadbarcross.setRotationPoint(0.0f, 0.0f, 0.0f);
        loadbarcross.setTextureSize(64, 32);
        loadbarcross.mirror = true;
        setRotation(loadbarcross, -0.5585054f, 0.0f, 0.0f);
        (loadbarl = new ModelRenderer(this, 0, 15)).addBox(2.0f, -9.0f, -1.0f, 1, 11, 2);
        loadbarl.setRotationPoint(0.0f, 0.0f, 0.0f);
        loadbarl.setTextureSize(64, 32);
        loadbarl.mirror = true;
        setRotation(loadbarl, -0.5585054f, 0.0f, 0.0f);
        (loadbarr = new ModelRenderer(this, 0, 15)).addBox(-3.0f, -9.0f, -1.0f, 1, 11, 2);
        loadbarr.setRotationPoint(0.0f, 0.0f, 0.0f);
        loadbarr.setTextureSize(64, 32);
        loadbarr.mirror = true;
        setRotation(loadbarr, -0.5585054f, 0.0f, 0.0f);
        (crossl1 = new ModelRenderer(this, 0, 0)).addBox(0.0f, 0.0f, -6.0f, 5, 2, 1);
        crossl1.setRotationPoint(0.0f, 0.0f, 0.0f);
        crossl1.setTextureSize(64, 32);
        crossl1.mirror = true;
        setRotation(crossl1, 0.0f, -0.2443461f, 0.0f);
        (crossl2 = new ModelRenderer(this, 0, 0)).addBox(4.0f, 0.0f, -5.0f, 3, 2, 1);
        crossl2.setRotationPoint(0.0f, 0.0f, 0.0f);
        crossl2.setTextureSize(64, 32);
        crossl2.mirror = true;
        setRotation(crossl2, 0.0f, -0.2443461f, 0.0f);
        (crossl3 = new ModelRenderer(this, 0, 0)).addBox(6.0f, 0.0f, -4.0f, 2, 2, 1);
        crossl3.setRotationPoint(0.0f, 0.0f, 0.0f);
        crossl3.setTextureSize(64, 32);
        crossl3.mirror = true;
        setRotation(crossl3, 0.0f, -0.2443461f, 0.0f);
        (crossr1 = new ModelRenderer(this, 0, 0)).addBox(-5.0f, 0.0f, -6.0f, 5, 2, 1);
        crossr1.setRotationPoint(0.0f, 0.0f, 0.0f);
        crossr1.setTextureSize(64, 32);
        crossr1.mirror = true;
        setRotation(crossr1, 0.0f, 0.2443461f, 0.0f);
        (crossr2 = new ModelRenderer(this, 0, 0)).addBox(-7.0f, 0.0f, -5.0f, 3, 2, 1);
        crossr2.setRotationPoint(0.0f, 0.0f, 0.0f);
        crossr2.setTextureSize(64, 32);
        crossr2.mirror = true;
        setRotation(crossr2, 0.0f, 0.2443461f, 0.0f);
        (crossr3 = new ModelRenderer(this, 0, 0)).addBox(-8.0f, 0.0f, -4.0f, 2, 2, 1);
        crossr3.setRotationPoint(0.0f, 0.0f, 0.0f);
        crossr3.setTextureSize(64, 32);
        crossr3.mirror = true;
        setRotation(crossr3, 0.0f, 0.2443461f, 0.0f);
        (leg2 = new ModelRenderer(this, 20, 10)).addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        leg2.setRotationPoint(0.0f, 12.0f, 0.0f);
        leg2.setTextureSize(64, 32);
        leg2.mirror = true;
        setRotation(leg2, 0.5235988f, 1.570796f, 0.0f);
        (tripod = new ModelRenderer(this, 13, 0)).addBox(-1.5f, 0.0f, -1.5f, 3, 2, 3);
        tripod.setRotationPoint(0.0f, 12.0f, 0.0f);
        tripod.setTextureSize(64, 32);
        tripod.mirror = true;
        setRotation(tripod, 0.0f, 0.0f, 0.0f);
        (leg3 = new ModelRenderer(this, 20, 10)).addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        leg3.setRotationPoint(0.0f, 12.0f, 0.0f);
        leg3.setTextureSize(64, 32);
        leg3.mirror = true;
        setRotation(leg3, 0.5235988f, 3.141593f, 0.0f);
        (leg4 = new ModelRenderer(this, 20, 10)).addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        leg4.setRotationPoint(0.0f, 12.0f, 0.0f);
        leg4.setTextureSize(64, 32);
        leg4.mirror = true;
        setRotation(leg4, 0.5235988f, 4.712389f, 0.0f);
        (leg1 = new ModelRenderer(this, 20, 10)).addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        leg1.setRotationPoint(0.0f, 12.0f, 0.0f);
        leg1.setTextureSize(64, 32);
        leg1.mirror = true;
        setRotation(leg1, 0.5235988f, 0.0f, 0.0f);
        crossbow.addChild(ammobox);
        crossbow.addChild(barrel);
        crossbow.addChild(basebarcross);
        crossbow.addChild(basebarr);
        crossbow.addChild(basebarl);
        crossbow.addChild(loadbarcross);
        crossbow.addChild(loadbarl);
        crossbow.addChild(loadbarr);
        crossbow.addChild(crossl1);
        crossbow.addChild(crossl2);
        crossbow.addChild(crossl3);
        crossbow.addChild(crossr1);
        crossbow.addChild(crossr2);
        crossbow.addChild(crossr3);
    }
    
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        crossbow.render(f5);
        leg2.render(f5);
        tripod.render(f5);
        leg3.render(f5);
        leg4.render(f5);
        leg1.render(f5);
    }
    
    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float headpitch, float headyaw, float p_78087_6_, Entity entity) {
        crossbow.rotateAngleY = headpitch / 57.295776f;
        crossbow.rotateAngleX = headyaw / 57.295776f;
        if (swingProgress > -9990.0f) {
            float f6 = swingProgress;
            ModelRenderer crossl1 = this.crossl1;
            ModelRenderer crossl2 = this.crossl2;
            ModelRenderer crossl3 = this.crossl3;
            float rotateAngleY = -0.2f + MathHelper.sin(MathHelper.sqrt(f6) * 3.1415927f * 2.0f) * 0.2f;
            crossl3.rotateAngleY = rotateAngleY;
            crossl2.rotateAngleY = rotateAngleY;
            crossl1.rotateAngleY = rotateAngleY;
            ModelRenderer crossr1 = this.crossr1;
            ModelRenderer crossr2 = this.crossr2;
            ModelRenderer crossr3 = this.crossr3;
            float rotateAngleY2 = 0.2f - MathHelper.sin(MathHelper.sqrt(f6) * 3.1415927f * 2.0f) * 0.2f;
            crossr3.rotateAngleY = rotateAngleY2;
            crossr2.rotateAngleY = rotateAngleY2;
            crossr1.rotateAngleY = rotateAngleY2;
        }
        float lp = ((EntityTurretCrossbow)entity).loadProgressForRender;
        ModelRenderer loadbarcross = this.loadbarcross;
        ModelRenderer loadbarl = this.loadbarl;
        ModelRenderer loadbarr = this.loadbarr;
        float rotateAngleX = -0.5f + MathHelper.sin(MathHelper.sqrt(lp) * 3.1415927f * 2.0f) * 0.5f;
        loadbarr.rotateAngleX = rotateAngleX;
        loadbarl.rotateAngleX = rotateAngleX;
        loadbarcross.rotateAngleX = rotateAngleX;
        if (entity.isRiding() && entity.getRidingEntity() != null && entity.getRidingEntity() instanceof EntityMinecart) {
            leg1.offsetY = -0.5f;
            leg2.offsetY = -0.5f;
            leg3.offsetY = -0.5f;
            leg4.offsetY = -0.5f;
            leg1.rotateAngleX = 0.1f;
            leg2.rotateAngleX = 0.1f;
            leg3.rotateAngleX = 0.1f;
            leg4.rotateAngleX = 0.1f;
        }
        else {
            leg1.offsetY = 0.0f;
            leg2.offsetY = 0.0f;
            leg3.offsetY = 0.0f;
            leg4.offsetY = 0.0f;
            leg1.rotateAngleX = 0.5f;
            leg2.rotateAngleX = 0.5f;
            leg3.rotateAngleX = 0.5f;
            leg4.rotateAngleX = 0.5f;
        }
    }
}
