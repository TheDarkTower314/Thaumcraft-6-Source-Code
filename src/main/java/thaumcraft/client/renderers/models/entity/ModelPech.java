// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.entity;

import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.monster.EntityPech;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBiped;

public class ModelPech extends ModelBiped
{
    ModelRenderer Jowls;
    ModelRenderer LowerPack;
    ModelRenderer UpperPack;
    
    public ModelPech() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        (this.bipedBody = new ModelRenderer(this, 34, 12)).addBox(-3.0f, 0.0f, 0.0f, 6, 10, 6);
        this.bipedBody.setRotationPoint(0.0f, 9.0f, -3.0f);
        this.bipedBody.setTextureSize(128, 64);
        this.bipedBody.mirror = true;
        this.setRotation(this.bipedBody, 0.3129957f, 0.0f, 0.0f);
        this.bipedRightLeg = new ModelRenderer(this, 35, 1);
        this.bipedRightLeg.mirror = true;
        this.bipedRightLeg.addBox(-2.9f, 0.0f, 0.0f, 3, 6, 3);
        this.bipedRightLeg.setRotationPoint(0.0f, 18.0f, 0.0f);
        this.bipedRightLeg.setTextureSize(128, 64);
        this.bipedRightLeg.mirror = true;
        this.setRotation(this.bipedRightLeg, 0.0f, 0.0f, 0.0f);
        this.bipedRightLeg.mirror = false;
        (this.bipedLeftLeg = new ModelRenderer(this, 35, 1)).addBox(-0.1f, 0.0f, 0.0f, 3, 6, 3);
        this.bipedLeftLeg.setRotationPoint(0.0f, 18.0f, 0.0f);
        this.bipedLeftLeg.setTextureSize(128, 64);
        this.bipedLeftLeg.mirror = true;
        this.setRotation(this.bipedLeftLeg, 0.0f, 0.0f, 0.0f);
        (this.bipedHead = new ModelRenderer(this, 2, 11)).addBox(-3.5f, -5.0f, -5.0f, 7, 5, 5);
        this.bipedHead.setRotationPoint(0.0f, 8.0f, 0.0f);
        this.bipedHead.setTextureSize(128, 64);
        this.bipedHead.mirror = true;
        this.setRotation(this.bipedHead, 0.0f, 0.0f, 0.0f);
        (this.Jowls = new ModelRenderer(this, 1, 21)).addBox(-4.0f, -1.0f, -6.0f, 8, 3, 5);
        this.Jowls.setRotationPoint(0.0f, 8.0f, 0.0f);
        this.Jowls.setTextureSize(128, 64);
        this.Jowls.mirror = true;
        this.setRotation(this.Jowls, 0.0f, 0.0f, 0.0f);
        (this.LowerPack = new ModelRenderer(this, 0, 0)).addBox(-5.0f, 0.0f, 0.0f, 10, 5, 5);
        this.LowerPack.setRotationPoint(0.0f, 10.0f, 3.5f);
        this.LowerPack.setTextureSize(128, 64);
        this.LowerPack.mirror = true;
        this.setRotation(this.LowerPack, 0.3013602f, 0.0f, 0.0f);
        (this.UpperPack = new ModelRenderer(this, 64, 1)).addBox(-7.5f, -14.0f, 0.0f, 15, 14, 11);
        this.UpperPack.setRotationPoint(0.0f, 10.0f, 3.0f);
        this.UpperPack.setTextureSize(128, 64);
        this.UpperPack.mirror = true;
        this.setRotation(this.UpperPack, 0.4537856f, 0.0f, 0.0f);
        this.bipedRightArm = new ModelRenderer(this, 52, 2);
        this.bipedRightArm.mirror = true;
        this.bipedRightArm.addBox(-2.0f, 0.0f, -1.0f, 2, 6, 2);
        this.bipedRightArm.setRotationPoint(-3.0f, 10.0f, -1.0f);
        this.bipedRightArm.setTextureSize(128, 64);
        this.bipedRightArm.mirror = true;
        this.setRotation(this.bipedRightArm, 0.0f, 0.0f, 0.0f);
        this.bipedRightArm.mirror = false;
        (this.bipedLeftArm = new ModelRenderer(this, 52, 2)).addBox(0.0f, 0.0f, -1.0f, 2, 6, 2);
        this.bipedLeftArm.setRotationPoint(3.0f, 10.0f, -1.0f);
        this.bipedLeftArm.setTextureSize(128, 64);
        this.bipedLeftArm.mirror = true;
        this.setRotation(this.bipedLeftArm, 0.0f, 0.0f, 0.0f);
    }
    
    public void render(final Entity par1Entity, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7) {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
        this.bipedBody.render(par7);
        this.bipedRightLeg.render(par7);
        this.bipedLeftLeg.render(par7);
        this.bipedHead.render(par7);
        this.Jowls.render(par7);
        this.LowerPack.render(par7);
        this.UpperPack.render(par7);
        this.bipedRightArm.render(par7);
        this.bipedLeftArm.render(par7);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    public void setRotationAngles(final float par1, final float par2, final float par3, final float par4, final float par5, final float par6, final Entity entity) {
        this.bipedHead.rotateAngleY = par4 / 57.295776f;
        this.bipedHead.rotateAngleX = par5 / 57.295776f;
        float mumble = 0.0f;
        if (entity instanceof EntityPech) {
            mumble = ((EntityPech)entity).mumble;
        }
        this.Jowls.rotateAngleY = this.bipedHead.rotateAngleY;
        this.Jowls.rotateAngleX = this.bipedHead.rotateAngleX + (0.2617994f + MathHelper.cos(par1 * 0.6662f) * par2 * 0.25f) + 0.34906587f * Math.abs(MathHelper.sin(mumble / 8.0f));
        this.bipedRightArm.rotateAngleX = MathHelper.cos(par1 * 0.6662f + 3.1415927f) * 2.0f * par2 * 0.5f;
        this.bipedLeftArm.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 2.0f * par2 * 0.5f;
        this.bipedRightArm.rotateAngleZ = 0.0f;
        this.bipedLeftArm.rotateAngleZ = 0.0f;
        this.bipedRightLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 1.4f * par2;
        this.bipedLeftLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662f + 3.1415927f) * 1.4f * par2;
        this.bipedRightLeg.rotateAngleY = 0.0f;
        this.bipedLeftLeg.rotateAngleY = 0.0f;
        this.LowerPack.rotateAngleY = MathHelper.cos(par1 * 0.6662f) * 2.0f * par2 * 0.125f;
        this.LowerPack.rotateAngleZ = MathHelper.cos(par1 * 0.6662f) * 2.0f * par2 * 0.125f;
        if (this.isRiding) {
            final ModelRenderer bipedRightArm = this.bipedRightArm;
            bipedRightArm.rotateAngleX -= 0.62831855f;
            final ModelRenderer bipedLeftArm = this.bipedLeftArm;
            bipedLeftArm.rotateAngleX -= 0.62831855f;
            this.bipedRightLeg.rotateAngleX = -1.2566371f;
            this.bipedLeftLeg.rotateAngleX = -1.2566371f;
            this.bipedRightLeg.rotateAngleY = 0.31415927f;
            this.bipedLeftLeg.rotateAngleY = -0.31415927f;
        }
        this.bipedRightArm.rotateAngleY = 0.0f;
        this.bipedLeftArm.rotateAngleY = 0.0f;
        if (this.swingProgress > -9990.0f) {
            float f6 = this.swingProgress;
            final ModelRenderer bipedRightArm2 = this.bipedRightArm;
            bipedRightArm2.rotateAngleY += this.bipedBody.rotateAngleY;
            final ModelRenderer bipedLeftArm2 = this.bipedLeftArm;
            bipedLeftArm2.rotateAngleY += this.bipedBody.rotateAngleY;
            final ModelRenderer bipedLeftArm3 = this.bipedLeftArm;
            bipedLeftArm3.rotateAngleX += this.bipedBody.rotateAngleY;
            f6 = 1.0f - this.swingProgress;
            f6 *= f6;
            f6 *= f6;
            f6 = 1.0f - f6;
            final float f7 = MathHelper.sin(f6 * 3.1415927f);
            final float f8 = MathHelper.sin(this.swingProgress * 3.1415927f) * -(this.bipedHead.rotateAngleX - 0.7f) * 0.75f;
            this.bipedRightArm.rotateAngleX -= (float)(f7 * 1.2 + f8);
            final ModelRenderer bipedRightArm3 = this.bipedRightArm;
            bipedRightArm3.rotateAngleY += this.bipedBody.rotateAngleY * 2.0f;
            this.bipedRightArm.rotateAngleZ = MathHelper.sin(this.swingProgress * 3.1415927f) * -0.4f;
        }
        if (entity.isSneaking()) {
            final ModelRenderer bipedRightArm4 = this.bipedRightArm;
            bipedRightArm4.rotateAngleX += 0.4f;
            final ModelRenderer bipedLeftArm4 = this.bipedLeftArm;
            bipedLeftArm4.rotateAngleX += 0.4f;
        }
        final ModelRenderer bipedRightArm5 = this.bipedRightArm;
        bipedRightArm5.rotateAngleZ += MathHelper.cos(par3 * 0.09f) * 0.05f + 0.05f;
        final ModelRenderer bipedLeftArm5 = this.bipedLeftArm;
        bipedLeftArm5.rotateAngleZ -= MathHelper.cos(par3 * 0.09f) * 0.05f + 0.05f;
        final ModelRenderer bipedRightArm6 = this.bipedRightArm;
        bipedRightArm6.rotateAngleX += MathHelper.sin(par3 * 0.067f) * 0.05f;
        final ModelRenderer bipedLeftArm6 = this.bipedLeftArm;
        bipedLeftArm6.rotateAngleX -= MathHelper.sin(par3 * 0.067f) * 0.05f;
    }
}
