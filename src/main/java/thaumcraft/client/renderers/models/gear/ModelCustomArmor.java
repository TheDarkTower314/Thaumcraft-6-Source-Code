// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.gear;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.item.EntityArmorStand;
import thaumcraft.client.lib.UtilsFX;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBiped;

public class ModelCustomArmor extends ModelBiped
{
    public ModelCustomArmor(final float f, final int i, final int j, final int k) {
        super(f, (float)i, j, k);
    }
    
    public void setRotationAngles(final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            this.swingProgress = ((EntityLivingBase)entityIn).getSwingProgress(UtilsFX.sysPartialTicks);
        }
        if (entityIn instanceof EntityArmorStand) {
            this.setRotationAnglesStand(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        }
        else if (entityIn instanceof EntitySkeleton || entityIn instanceof EntityZombie) {
            this.setRotationAnglesZombie(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        }
        else {
            final boolean flag = entityIn instanceof EntityLivingBase && ((EntityLivingBase)entityIn).getTicksElytraFlying() > 4;
            this.bipedHead.rotateAngleY = netHeadYaw * 0.017453292f;
            if (flag) {
                this.bipedHead.rotateAngleX = -0.7853982f;
            }
            else {
                this.bipedHead.rotateAngleX = headPitch * 0.017453292f;
            }
            this.bipedBody.rotateAngleY = 0.0f;
            this.bipedRightArm.rotationPointZ = 0.0f;
            this.bipedRightArm.rotationPointX = -5.0f;
            this.bipedLeftArm.rotationPointZ = 0.0f;
            this.bipedLeftArm.rotationPointX = 5.0f;
            float f = 1.0f;
            if (flag) {
                f = (float)(entityIn.motionX * entityIn.motionX + entityIn.motionY * entityIn.motionY + entityIn.motionZ * entityIn.motionZ);
                f /= 0.2f;
                f *= f * f;
            }
            if (f < 1.0f) {
                f = 1.0f;
            }
            this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + 3.1415927f) * 2.0f * limbSwingAmount * 0.5f / f;
            this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 2.0f * limbSwingAmount * 0.5f / f;
            this.bipedRightArm.rotateAngleZ = 0.0f;
            this.bipedLeftArm.rotateAngleZ = 0.0f;
            this.bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount / f;
            this.bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + 3.1415927f) * 1.4f * limbSwingAmount / f;
            this.bipedRightLeg.rotateAngleY = 0.0f;
            this.bipedLeftLeg.rotateAngleY = 0.0f;
            this.bipedRightLeg.rotateAngleZ = 0.0f;
            this.bipedLeftLeg.rotateAngleZ = 0.0f;
            if (this.isRiding) {
                final ModelRenderer bipedRightArm = this.bipedRightArm;
                bipedRightArm.rotateAngleX -= 0.62831855f;
                final ModelRenderer bipedLeftArm = this.bipedLeftArm;
                bipedLeftArm.rotateAngleX -= 0.62831855f;
                this.bipedRightLeg.rotateAngleX = -1.4137167f;
                this.bipedRightLeg.rotateAngleY = 0.31415927f;
                this.bipedRightLeg.rotateAngleZ = 0.07853982f;
                this.bipedLeftLeg.rotateAngleX = -1.4137167f;
                this.bipedLeftLeg.rotateAngleY = -0.31415927f;
                this.bipedLeftLeg.rotateAngleZ = -0.07853982f;
            }
            this.bipedRightArm.rotateAngleY = 0.0f;
            this.bipedRightArm.rotateAngleZ = 0.0f;
            switch (this.leftArmPose) {
                case EMPTY: {
                    this.bipedLeftArm.rotateAngleY = 0.0f;
                    break;
                }
                case BLOCK: {
                    this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5f - 0.9424779f;
                    this.bipedLeftArm.rotateAngleY = 0.5235988f;
                    break;
                }
                case ITEM: {
                    this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5f - 0.31415927f;
                    this.bipedLeftArm.rotateAngleY = 0.0f;
                    break;
                }
            }
            switch (this.rightArmPose) {
                case EMPTY: {
                    this.bipedRightArm.rotateAngleY = 0.0f;
                    break;
                }
                case BLOCK: {
                    this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5f - 0.9424779f;
                    this.bipedRightArm.rotateAngleY = -0.5235988f;
                    break;
                }
                case ITEM: {
                    this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5f - 0.31415927f;
                    this.bipedRightArm.rotateAngleY = 0.0f;
                    break;
                }
            }
            if (this.swingProgress > 0.0f) {
                final EnumHandSide enumhandside = this.getMainHand(entityIn);
                final ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
                float f2 = this.swingProgress;
                this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f2) * 6.2831855f) * 0.2f;
                if (enumhandside == EnumHandSide.LEFT) {
                    final ModelRenderer bipedBody = this.bipedBody;
                    bipedBody.rotateAngleY *= -1.0f;
                }
                this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0f;
                this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0f;
                this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0f;
                this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0f;
                final ModelRenderer bipedRightArm2 = this.bipedRightArm;
                bipedRightArm2.rotateAngleY += this.bipedBody.rotateAngleY;
                final ModelRenderer bipedLeftArm2 = this.bipedLeftArm;
                bipedLeftArm2.rotateAngleY += this.bipedBody.rotateAngleY;
                final ModelRenderer bipedLeftArm3 = this.bipedLeftArm;
                bipedLeftArm3.rotateAngleX += this.bipedBody.rotateAngleY;
                f2 = 1.0f - this.swingProgress;
                f2 *= f2;
                f2 *= f2;
                f2 = 1.0f - f2;
                final float f3 = MathHelper.sin(f2 * 3.1415927f);
                final float f4 = MathHelper.sin(this.swingProgress * 3.1415927f) * -(this.bipedHead.rotateAngleX - 0.7f) * 0.75f;
                modelrenderer.rotateAngleX -= (float)(f3 * 1.2 + f4);
                final ModelRenderer modelRenderer = modelrenderer;
                modelRenderer.rotateAngleY += this.bipedBody.rotateAngleY * 2.0f;
                final ModelRenderer modelRenderer2 = modelrenderer;
                modelRenderer2.rotateAngleZ += MathHelper.sin(this.swingProgress * 3.1415927f) * -0.4f;
            }
            if (this.isSneak) {
                this.bipedBody.rotateAngleX = 0.5f;
                final ModelRenderer bipedRightArm3 = this.bipedRightArm;
                bipedRightArm3.rotateAngleX += 0.4f;
                final ModelRenderer bipedLeftArm4 = this.bipedLeftArm;
                bipedLeftArm4.rotateAngleX += 0.4f;
                this.bipedRightLeg.rotationPointZ = 4.0f;
                this.bipedLeftLeg.rotationPointZ = 4.0f;
                this.bipedRightLeg.rotationPointY = 13.0f;
                this.bipedLeftLeg.rotationPointY = 13.0f;
                this.bipedHead.rotationPointY = 4.5f;
                this.bipedBody.rotationPointY = 4.5f;
                this.bipedRightArm.rotationPointY = 5.0f;
                this.bipedLeftArm.rotationPointY = 5.0f;
            }
            else {
                this.bipedBody.rotateAngleX = 0.0f;
                this.bipedRightLeg.rotationPointZ = 0.1f;
                this.bipedLeftLeg.rotationPointZ = 0.1f;
                this.bipedRightLeg.rotationPointY = 12.0f;
                this.bipedLeftLeg.rotationPointY = 12.0f;
                this.bipedHead.rotationPointY = 0.0f;
                this.bipedBody.rotationPointY = 0.0f;
                this.bipedRightArm.rotationPointY = 2.0f;
                this.bipedLeftArm.rotationPointY = 2.0f;
            }
            final ModelRenderer bipedRightArm4 = this.bipedRightArm;
            bipedRightArm4.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09f) * 0.05f + 0.05f;
            final ModelRenderer bipedLeftArm5 = this.bipedLeftArm;
            bipedLeftArm5.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09f) * 0.05f + 0.05f;
            final ModelRenderer bipedRightArm5 = this.bipedRightArm;
            bipedRightArm5.rotateAngleX += MathHelper.sin(ageInTicks * 0.067f) * 0.05f;
            final ModelRenderer bipedLeftArm6 = this.bipedLeftArm;
            bipedLeftArm6.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067f) * 0.05f;
            if (this.rightArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
                this.bipedRightArm.rotateAngleY = -0.1f + this.bipedHead.rotateAngleY;
                this.bipedLeftArm.rotateAngleY = 0.1f + this.bipedHead.rotateAngleY + 0.4f;
                this.bipedRightArm.rotateAngleX = -1.5707964f + this.bipedHead.rotateAngleX;
                this.bipedLeftArm.rotateAngleX = -1.5707964f + this.bipedHead.rotateAngleX;
            }
            else if (this.leftArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
                this.bipedRightArm.rotateAngleY = -0.1f + this.bipedHead.rotateAngleY - 0.4f;
                this.bipedLeftArm.rotateAngleY = 0.1f + this.bipedHead.rotateAngleY;
                this.bipedRightArm.rotateAngleX = -1.5707964f + this.bipedHead.rotateAngleX;
                this.bipedLeftArm.rotateAngleX = -1.5707964f + this.bipedHead.rotateAngleX;
            }
            copyModelAngles(this.bipedHead, this.bipedHeadwear);
        }
    }
    
    public void setRotationAnglesZombie(final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        final boolean flag = entityIn instanceof EntityZombie && ((EntityZombie)entityIn).isArmsRaised();
        final float f = MathHelper.sin(this.swingProgress * 3.1415927f);
        final float f2 = MathHelper.sin((1.0f - (1.0f - this.swingProgress) * (1.0f - this.swingProgress)) * 3.1415927f);
        this.bipedRightArm.rotateAngleZ = 0.0f;
        this.bipedLeftArm.rotateAngleZ = 0.0f;
        this.bipedRightArm.rotateAngleY = -(0.1f - f * 0.6f);
        this.bipedLeftArm.rotateAngleY = 0.1f - f * 0.6f;
        final float f3 = -3.1415927f / (flag ? 1.5f : 2.25f);
        this.bipedRightArm.rotateAngleX = f3;
        this.bipedLeftArm.rotateAngleX = f3;
        final ModelRenderer bipedRightArm = this.bipedRightArm;
        bipedRightArm.rotateAngleX += f * 1.2f - f2 * 0.4f;
        final ModelRenderer bipedLeftArm = this.bipedLeftArm;
        bipedLeftArm.rotateAngleX += f * 1.2f - f2 * 0.4f;
        final ModelRenderer bipedRightArm2 = this.bipedRightArm;
        bipedRightArm2.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09f) * 0.05f + 0.05f;
        final ModelRenderer bipedLeftArm2 = this.bipedLeftArm;
        bipedLeftArm2.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09f) * 0.05f + 0.05f;
        final ModelRenderer bipedRightArm3 = this.bipedRightArm;
        bipedRightArm3.rotateAngleX += MathHelper.sin(ageInTicks * 0.067f) * 0.05f;
        final ModelRenderer bipedLeftArm3 = this.bipedLeftArm;
        bipedLeftArm3.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067f) * 0.05f;
    }
    
    public void setRotationAnglesStand(final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final Entity entityIn) {
        if (entityIn instanceof EntityArmorStand) {
            final EntityArmorStand entityarmorstand = (EntityArmorStand)entityIn;
            this.bipedHead.rotateAngleX = 0.017453292f * entityarmorstand.getHeadRotation().getX();
            this.bipedHead.rotateAngleY = 0.017453292f * entityarmorstand.getHeadRotation().getY();
            this.bipedHead.rotateAngleZ = 0.017453292f * entityarmorstand.getHeadRotation().getZ();
            this.bipedHead.setRotationPoint(0.0f, 1.0f, 0.0f);
            this.bipedBody.rotateAngleX = 0.017453292f * entityarmorstand.getBodyRotation().getX();
            this.bipedBody.rotateAngleY = 0.017453292f * entityarmorstand.getBodyRotation().getY();
            this.bipedBody.rotateAngleZ = 0.017453292f * entityarmorstand.getBodyRotation().getZ();
            this.bipedLeftArm.rotateAngleX = 0.017453292f * entityarmorstand.getLeftArmRotation().getX();
            this.bipedLeftArm.rotateAngleY = 0.017453292f * entityarmorstand.getLeftArmRotation().getY();
            this.bipedLeftArm.rotateAngleZ = 0.017453292f * entityarmorstand.getLeftArmRotation().getZ();
            this.bipedRightArm.rotateAngleX = 0.017453292f * entityarmorstand.getRightArmRotation().getX();
            this.bipedRightArm.rotateAngleY = 0.017453292f * entityarmorstand.getRightArmRotation().getY();
            this.bipedRightArm.rotateAngleZ = 0.017453292f * entityarmorstand.getRightArmRotation().getZ();
            this.bipedLeftLeg.rotateAngleX = 0.017453292f * entityarmorstand.getLeftLegRotation().getX();
            this.bipedLeftLeg.rotateAngleY = 0.017453292f * entityarmorstand.getLeftLegRotation().getY();
            this.bipedLeftLeg.rotateAngleZ = 0.017453292f * entityarmorstand.getLeftLegRotation().getZ();
            this.bipedLeftLeg.setRotationPoint(1.9f, 11.0f, 0.0f);
            this.bipedRightLeg.rotateAngleX = 0.017453292f * entityarmorstand.getRightLegRotation().getX();
            this.bipedRightLeg.rotateAngleY = 0.017453292f * entityarmorstand.getRightLegRotation().getY();
            this.bipedRightLeg.rotateAngleZ = 0.017453292f * entityarmorstand.getRightLegRotation().getZ();
            this.bipedRightLeg.setRotationPoint(-1.9f, 11.0f, 0.0f);
            copyModelAngles(this.bipedHead, this.bipedHeadwear);
        }
    }
}
