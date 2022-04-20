// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.entity;

import net.minecraft.entity.item.EntityMinecart;
import thaumcraft.common.entities.construct.EntityTurretCrossbow;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;

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
        this.textureWidth = 64;
        this.textureHeight = 32;
        (this.crossbow = new ModelRenderer(this, 28, 14)).addBox(-2.0f, 0.0f, -7.0f, 4, 2, 14);
        this.crossbow.setRotationPoint(0.0f, 10.0f, 0.0f);
        this.crossbow.setTextureSize(64, 32);
        this.crossbow.mirror = true;
        this.setRotation(this.crossbow, 0.0f, 0.0f, 0.0f);
        (this.basebarr = new ModelRenderer(this, 40, 23)).addBox(-1.0f, 0.0f, 7.0f, 1, 2, 5);
        this.basebarr.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.basebarr.setTextureSize(64, 32);
        this.basebarr.mirror = true;
        this.setRotation(this.basebarr, 0.0f, -0.1396263f, 0.0f);
        (this.basebarl = new ModelRenderer(this, 40, 23)).addBox(0.0f, 0.0f, 7.0f, 1, 2, 5);
        this.basebarl.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.basebarl.setTextureSize(64, 32);
        this.basebarl.mirror = true;
        this.setRotation(this.basebarl, 0.0f, 0.1396263f, 0.0f);
        (this.barrel = new ModelRenderer(this, 20, 28)).addBox(-1.0f, -1.0f, -8.0f, 2, 2, 2);
        this.barrel.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.barrel.setTextureSize(64, 32);
        this.barrel.mirror = true;
        this.setRotation(this.barrel, 0.0f, 0.0f, 0.0f);
        (this.basebarcross = new ModelRenderer(this, 0, 13)).addBox(-2.0f, 0.5f, 10.0f, 4, 1, 1);
        this.basebarcross.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.basebarcross.setTextureSize(64, 32);
        this.basebarcross.mirror = true;
        this.setRotation(this.basebarcross, 0.0f, 0.0f, 0.0f);
        (this.ammobox = new ModelRenderer(this, 38, 0)).addBox(-2.0f, -5.0f, -6.0f, 4, 5, 9);
        this.ammobox.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.ammobox.setTextureSize(64, 32);
        this.ammobox.mirror = true;
        this.setRotation(this.ammobox, 0.0f, 0.0f, 0.0f);
        (this.loadbarcross = new ModelRenderer(this, 0, 13)).addBox(-2.0f, -8.5f, -0.5f, 4, 1, 1);
        this.loadbarcross.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.loadbarcross.setTextureSize(64, 32);
        this.loadbarcross.mirror = true;
        this.setRotation(this.loadbarcross, -0.5585054f, 0.0f, 0.0f);
        (this.loadbarl = new ModelRenderer(this, 0, 15)).addBox(2.0f, -9.0f, -1.0f, 1, 11, 2);
        this.loadbarl.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.loadbarl.setTextureSize(64, 32);
        this.loadbarl.mirror = true;
        this.setRotation(this.loadbarl, -0.5585054f, 0.0f, 0.0f);
        (this.loadbarr = new ModelRenderer(this, 0, 15)).addBox(-3.0f, -9.0f, -1.0f, 1, 11, 2);
        this.loadbarr.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.loadbarr.setTextureSize(64, 32);
        this.loadbarr.mirror = true;
        this.setRotation(this.loadbarr, -0.5585054f, 0.0f, 0.0f);
        (this.crossl1 = new ModelRenderer(this, 0, 0)).addBox(0.0f, 0.0f, -6.0f, 5, 2, 1);
        this.crossl1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.crossl1.setTextureSize(64, 32);
        this.crossl1.mirror = true;
        this.setRotation(this.crossl1, 0.0f, -0.2443461f, 0.0f);
        (this.crossl2 = new ModelRenderer(this, 0, 0)).addBox(4.0f, 0.0f, -5.0f, 3, 2, 1);
        this.crossl2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.crossl2.setTextureSize(64, 32);
        this.crossl2.mirror = true;
        this.setRotation(this.crossl2, 0.0f, -0.2443461f, 0.0f);
        (this.crossl3 = new ModelRenderer(this, 0, 0)).addBox(6.0f, 0.0f, -4.0f, 2, 2, 1);
        this.crossl3.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.crossl3.setTextureSize(64, 32);
        this.crossl3.mirror = true;
        this.setRotation(this.crossl3, 0.0f, -0.2443461f, 0.0f);
        (this.crossr1 = new ModelRenderer(this, 0, 0)).addBox(-5.0f, 0.0f, -6.0f, 5, 2, 1);
        this.crossr1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.crossr1.setTextureSize(64, 32);
        this.crossr1.mirror = true;
        this.setRotation(this.crossr1, 0.0f, 0.2443461f, 0.0f);
        (this.crossr2 = new ModelRenderer(this, 0, 0)).addBox(-7.0f, 0.0f, -5.0f, 3, 2, 1);
        this.crossr2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.crossr2.setTextureSize(64, 32);
        this.crossr2.mirror = true;
        this.setRotation(this.crossr2, 0.0f, 0.2443461f, 0.0f);
        (this.crossr3 = new ModelRenderer(this, 0, 0)).addBox(-8.0f, 0.0f, -4.0f, 2, 2, 1);
        this.crossr3.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.crossr3.setTextureSize(64, 32);
        this.crossr3.mirror = true;
        this.setRotation(this.crossr3, 0.0f, 0.2443461f, 0.0f);
        (this.leg2 = new ModelRenderer(this, 20, 10)).addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        this.leg2.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.leg2.setTextureSize(64, 32);
        this.leg2.mirror = true;
        this.setRotation(this.leg2, 0.5235988f, 1.570796f, 0.0f);
        (this.tripod = new ModelRenderer(this, 13, 0)).addBox(-1.5f, 0.0f, -1.5f, 3, 2, 3);
        this.tripod.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.tripod.setTextureSize(64, 32);
        this.tripod.mirror = true;
        this.setRotation(this.tripod, 0.0f, 0.0f, 0.0f);
        (this.leg3 = new ModelRenderer(this, 20, 10)).addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        this.leg3.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.leg3.setTextureSize(64, 32);
        this.leg3.mirror = true;
        this.setRotation(this.leg3, 0.5235988f, 3.141593f, 0.0f);
        (this.leg4 = new ModelRenderer(this, 20, 10)).addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        this.leg4.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.leg4.setTextureSize(64, 32);
        this.leg4.mirror = true;
        this.setRotation(this.leg4, 0.5235988f, 4.712389f, 0.0f);
        (this.leg1 = new ModelRenderer(this, 20, 10)).addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        this.leg1.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.leg1.setTextureSize(64, 32);
        this.leg1.mirror = true;
        this.setRotation(this.leg1, 0.5235988f, 0.0f, 0.0f);
        this.crossbow.addChild(this.ammobox);
        this.crossbow.addChild(this.barrel);
        this.crossbow.addChild(this.basebarcross);
        this.crossbow.addChild(this.basebarr);
        this.crossbow.addChild(this.basebarl);
        this.crossbow.addChild(this.loadbarcross);
        this.crossbow.addChild(this.loadbarl);
        this.crossbow.addChild(this.loadbarr);
        this.crossbow.addChild(this.crossl1);
        this.crossbow.addChild(this.crossl2);
        this.crossbow.addChild(this.crossl3);
        this.crossbow.addChild(this.crossr1);
        this.crossbow.addChild(this.crossr2);
        this.crossbow.addChild(this.crossr3);
    }
    
    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.crossbow.render(f5);
        this.leg2.render(f5);
        this.tripod.render(f5);
        this.leg3.render(f5);
        this.leg4.render(f5);
        this.leg1.render(f5);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    public void setRotationAngles(final float p_78087_1_, final float p_78087_2_, final float p_78087_3_, final float headpitch, final float headyaw, final float p_78087_6_, final Entity entity) {
        this.crossbow.rotateAngleY = headpitch / 57.295776f;
        this.crossbow.rotateAngleX = headyaw / 57.295776f;
        if (this.swingProgress > -9990.0f) {
            final float f6 = this.swingProgress;
            final ModelRenderer crossl1 = this.crossl1;
            final ModelRenderer crossl2 = this.crossl2;
            final ModelRenderer crossl3 = this.crossl3;
            final float rotateAngleY = -0.2f + MathHelper.sin(MathHelper.sqrt(f6) * 3.1415927f * 2.0f) * 0.2f;
            crossl3.rotateAngleY = rotateAngleY;
            crossl2.rotateAngleY = rotateAngleY;
            crossl1.rotateAngleY = rotateAngleY;
            final ModelRenderer crossr1 = this.crossr1;
            final ModelRenderer crossr2 = this.crossr2;
            final ModelRenderer crossr3 = this.crossr3;
            final float rotateAngleY2 = 0.2f - MathHelper.sin(MathHelper.sqrt(f6) * 3.1415927f * 2.0f) * 0.2f;
            crossr3.rotateAngleY = rotateAngleY2;
            crossr2.rotateAngleY = rotateAngleY2;
            crossr1.rotateAngleY = rotateAngleY2;
        }
        final float lp = ((EntityTurretCrossbow)entity).loadProgressForRender;
        final ModelRenderer loadbarcross = this.loadbarcross;
        final ModelRenderer loadbarl = this.loadbarl;
        final ModelRenderer loadbarr = this.loadbarr;
        final float rotateAngleX = -0.5f + MathHelper.sin(MathHelper.sqrt(lp) * 3.1415927f * 2.0f) * 0.5f;
        loadbarr.rotateAngleX = rotateAngleX;
        loadbarl.rotateAngleX = rotateAngleX;
        loadbarcross.rotateAngleX = rotateAngleX;
        if (entity.isRiding() && entity.getRidingEntity() != null && entity.getRidingEntity() instanceof EntityMinecart) {
            this.leg1.offsetY = -0.5f;
            this.leg2.offsetY = -0.5f;
            this.leg3.offsetY = -0.5f;
            this.leg4.offsetY = -0.5f;
            this.leg1.rotateAngleX = 0.1f;
            this.leg2.rotateAngleX = 0.1f;
            this.leg3.rotateAngleX = 0.1f;
            this.leg4.rotateAngleX = 0.1f;
        }
        else {
            this.leg1.offsetY = 0.0f;
            this.leg2.offsetY = 0.0f;
            this.leg3.offsetY = 0.0f;
            this.leg4.offsetY = 0.0f;
            this.leg1.rotateAngleX = 0.5f;
            this.leg2.rotateAngleX = 0.5f;
            this.leg3.rotateAngleX = 0.5f;
            this.leg4.rotateAngleX = 0.5f;
        }
    }
}
