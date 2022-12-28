package thaumcraft.client.renderers.models.gear;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import thaumcraft.client.lib.UtilsFX;


public class ModelCustomArmor extends ModelBiped
{
    public ModelCustomArmor(float f, int i, int j, int k) {
        super(f, (float)i, j, k);
    }
    
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            swingProgress = ((EntityLivingBase)entityIn).getSwingProgress(UtilsFX.sysPartialTicks);
        }
        if (entityIn instanceof EntityArmorStand) {
            setRotationAnglesStand(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        }
        else if (entityIn instanceof EntitySkeleton || entityIn instanceof EntityZombie) {
            setRotationAnglesZombie(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        }
        else {
            boolean flag = entityIn instanceof EntityLivingBase && ((EntityLivingBase)entityIn).getTicksElytraFlying() > 4;
            bipedHead.rotateAngleY = netHeadYaw * 0.017453292f;
            if (flag) {
                bipedHead.rotateAngleX = -0.7853982f;
            }
            else {
                bipedHead.rotateAngleX = headPitch * 0.017453292f;
            }
            bipedBody.rotateAngleY = 0.0f;
            bipedRightArm.rotationPointZ = 0.0f;
            bipedRightArm.rotationPointX = -5.0f;
            bipedLeftArm.rotationPointZ = 0.0f;
            bipedLeftArm.rotationPointX = 5.0f;
            float f = 1.0f;
            if (flag) {
                f = (float)(entityIn.motionX * entityIn.motionX + entityIn.motionY * entityIn.motionY + entityIn.motionZ * entityIn.motionZ);
                f /= 0.2f;
                f *= f * f;
            }
            if (f < 1.0f) {
                f = 1.0f;
            }
            bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + 3.1415927f) * 2.0f * limbSwingAmount * 0.5f / f;
            bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 2.0f * limbSwingAmount * 0.5f / f;
            bipedRightArm.rotateAngleZ = 0.0f;
            bipedLeftArm.rotateAngleZ = 0.0f;
            bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount / f;
            bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + 3.1415927f) * 1.4f * limbSwingAmount / f;
            bipedRightLeg.rotateAngleY = 0.0f;
            bipedLeftLeg.rotateAngleY = 0.0f;
            bipedRightLeg.rotateAngleZ = 0.0f;
            bipedLeftLeg.rotateAngleZ = 0.0f;
            if (isRiding) {
                ModelRenderer bipedRightArm = this.bipedRightArm;
                bipedRightArm.rotateAngleX -= 0.62831855f;
                ModelRenderer bipedLeftArm = this.bipedLeftArm;
                bipedLeftArm.rotateAngleX -= 0.62831855f;
                bipedRightLeg.rotateAngleX = -1.4137167f;
                bipedRightLeg.rotateAngleY = 0.31415927f;
                bipedRightLeg.rotateAngleZ = 0.07853982f;
                bipedLeftLeg.rotateAngleX = -1.4137167f;
                bipedLeftLeg.rotateAngleY = -0.31415927f;
                bipedLeftLeg.rotateAngleZ = -0.07853982f;
            }
            bipedRightArm.rotateAngleY = 0.0f;
            bipedRightArm.rotateAngleZ = 0.0f;
            switch (leftArmPose) {
                case EMPTY: {
                    bipedLeftArm.rotateAngleY = 0.0f;
                    break;
                }
                case BLOCK: {
                    bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5f - 0.9424779f;
                    bipedLeftArm.rotateAngleY = 0.5235988f;
                    break;
                }
                case ITEM: {
                    bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5f - 0.31415927f;
                    bipedLeftArm.rotateAngleY = 0.0f;
                    break;
                }
            }
            switch (rightArmPose) {
                case EMPTY: {
                    bipedRightArm.rotateAngleY = 0.0f;
                    break;
                }
                case BLOCK: {
                    bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5f - 0.9424779f;
                    bipedRightArm.rotateAngleY = -0.5235988f;
                    break;
                }
                case ITEM: {
                    bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5f - 0.31415927f;
                    bipedRightArm.rotateAngleY = 0.0f;
                    break;
                }
            }
            if (swingProgress > 0.0f) {
                EnumHandSide enumhandside = getMainHand(entityIn);
                ModelRenderer modelrenderer = getArmForSide(enumhandside);
                float f2 = swingProgress;
                bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f2) * 6.2831855f) * 0.2f;
                if (enumhandside == EnumHandSide.LEFT) {
                    ModelRenderer bipedBody = this.bipedBody;
                    bipedBody.rotateAngleY *= -1.0f;
                }
                bipedRightArm.rotationPointZ = MathHelper.sin(bipedBody.rotateAngleY) * 5.0f;
                bipedRightArm.rotationPointX = -MathHelper.cos(bipedBody.rotateAngleY) * 5.0f;
                bipedLeftArm.rotationPointZ = -MathHelper.sin(bipedBody.rotateAngleY) * 5.0f;
                bipedLeftArm.rotationPointX = MathHelper.cos(bipedBody.rotateAngleY) * 5.0f;
                ModelRenderer bipedRightArm2 = bipedRightArm;
                bipedRightArm2.rotateAngleY += bipedBody.rotateAngleY;
                ModelRenderer bipedLeftArm2 = bipedLeftArm;
                bipedLeftArm2.rotateAngleY += bipedBody.rotateAngleY;
                ModelRenderer bipedLeftArm3 = bipedLeftArm;
                bipedLeftArm3.rotateAngleX += bipedBody.rotateAngleY;
                f2 = 1.0f - swingProgress;
                f2 *= f2;
                f2 *= f2;
                f2 = 1.0f - f2;
                float f3 = MathHelper.sin(f2 * 3.1415927f);
                float f4 = MathHelper.sin(swingProgress * 3.1415927f) * -(bipedHead.rotateAngleX - 0.7f) * 0.75f;
                modelrenderer.rotateAngleX -= (float)(f3 * 1.2 + f4);
                ModelRenderer modelRenderer = modelrenderer;
                modelRenderer.rotateAngleY += bipedBody.rotateAngleY * 2.0f;
                ModelRenderer modelRenderer2 = modelrenderer;
                modelRenderer2.rotateAngleZ += MathHelper.sin(swingProgress * 3.1415927f) * -0.4f;
            }
            if (isSneak) {
                bipedBody.rotateAngleX = 0.5f;
                ModelRenderer bipedRightArm3 = bipedRightArm;
                bipedRightArm3.rotateAngleX += 0.4f;
                ModelRenderer bipedLeftArm4 = bipedLeftArm;
                bipedLeftArm4.rotateAngleX += 0.4f;
                bipedRightLeg.rotationPointZ = 4.0f;
                bipedLeftLeg.rotationPointZ = 4.0f;
                bipedRightLeg.rotationPointY = 13.0f;
                bipedLeftLeg.rotationPointY = 13.0f;
                bipedHead.rotationPointY = 4.5f;
                bipedBody.rotationPointY = 4.5f;
                bipedRightArm.rotationPointY = 5.0f;
                bipedLeftArm.rotationPointY = 5.0f;
            }
            else {
                bipedBody.rotateAngleX = 0.0f;
                bipedRightLeg.rotationPointZ = 0.1f;
                bipedLeftLeg.rotationPointZ = 0.1f;
                bipedRightLeg.rotationPointY = 12.0f;
                bipedLeftLeg.rotationPointY = 12.0f;
                bipedHead.rotationPointY = 0.0f;
                bipedBody.rotationPointY = 0.0f;
                bipedRightArm.rotationPointY = 2.0f;
                bipedLeftArm.rotationPointY = 2.0f;
            }
            ModelRenderer bipedRightArm4 = bipedRightArm;
            bipedRightArm4.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09f) * 0.05f + 0.05f;
            ModelRenderer bipedLeftArm5 = bipedLeftArm;
            bipedLeftArm5.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09f) * 0.05f + 0.05f;
            ModelRenderer bipedRightArm5 = bipedRightArm;
            bipedRightArm5.rotateAngleX += MathHelper.sin(ageInTicks * 0.067f) * 0.05f;
            ModelRenderer bipedLeftArm6 = bipedLeftArm;
            bipedLeftArm6.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067f) * 0.05f;
            if (rightArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
                bipedRightArm.rotateAngleY = -0.1f + bipedHead.rotateAngleY;
                bipedLeftArm.rotateAngleY = 0.1f + bipedHead.rotateAngleY + 0.4f;
                bipedRightArm.rotateAngleX = -1.5707964f + bipedHead.rotateAngleX;
                bipedLeftArm.rotateAngleX = -1.5707964f + bipedHead.rotateAngleX;
            }
            else if (leftArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
                bipedRightArm.rotateAngleY = -0.1f + bipedHead.rotateAngleY - 0.4f;
                bipedLeftArm.rotateAngleY = 0.1f + bipedHead.rotateAngleY;
                bipedRightArm.rotateAngleX = -1.5707964f + bipedHead.rotateAngleX;
                bipedLeftArm.rotateAngleX = -1.5707964f + bipedHead.rotateAngleX;
            }
            copyModelAngles(bipedHead, bipedHeadwear);
        }
    }
    
    public void setRotationAnglesZombie(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        boolean flag = entityIn instanceof EntityZombie && ((EntityZombie)entityIn).isArmsRaised();
        float f = MathHelper.sin(swingProgress * 3.1415927f);
        float f2 = MathHelper.sin((1.0f - (1.0f - swingProgress) * (1.0f - swingProgress)) * 3.1415927f);
        bipedRightArm.rotateAngleZ = 0.0f;
        bipedLeftArm.rotateAngleZ = 0.0f;
        bipedRightArm.rotateAngleY = -(0.1f - f * 0.6f);
        bipedLeftArm.rotateAngleY = 0.1f - f * 0.6f;
        float f3 = -3.1415927f / (flag ? 1.5f : 2.25f);
        bipedRightArm.rotateAngleX = f3;
        bipedLeftArm.rotateAngleX = f3;
        ModelRenderer bipedRightArm = this.bipedRightArm;
        bipedRightArm.rotateAngleX += f * 1.2f - f2 * 0.4f;
        ModelRenderer bipedLeftArm = this.bipedLeftArm;
        bipedLeftArm.rotateAngleX += f * 1.2f - f2 * 0.4f;
        ModelRenderer bipedRightArm2 = this.bipedRightArm;
        bipedRightArm2.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09f) * 0.05f + 0.05f;
        ModelRenderer bipedLeftArm2 = this.bipedLeftArm;
        bipedLeftArm2.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09f) * 0.05f + 0.05f;
        ModelRenderer bipedRightArm3 = this.bipedRightArm;
        bipedRightArm3.rotateAngleX += MathHelper.sin(ageInTicks * 0.067f) * 0.05f;
        ModelRenderer bipedLeftArm3 = this.bipedLeftArm;
        bipedLeftArm3.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067f) * 0.05f;
    }
    
    public void setRotationAnglesStand(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        if (entityIn instanceof EntityArmorStand) {
            EntityArmorStand entityarmorstand = (EntityArmorStand)entityIn;
            bipedHead.rotateAngleX = 0.017453292f * entityarmorstand.getHeadRotation().getX();
            bipedHead.rotateAngleY = 0.017453292f * entityarmorstand.getHeadRotation().getY();
            bipedHead.rotateAngleZ = 0.017453292f * entityarmorstand.getHeadRotation().getZ();
            bipedHead.setRotationPoint(0.0f, 1.0f, 0.0f);
            bipedBody.rotateAngleX = 0.017453292f * entityarmorstand.getBodyRotation().getX();
            bipedBody.rotateAngleY = 0.017453292f * entityarmorstand.getBodyRotation().getY();
            bipedBody.rotateAngleZ = 0.017453292f * entityarmorstand.getBodyRotation().getZ();
            bipedLeftArm.rotateAngleX = 0.017453292f * entityarmorstand.getLeftArmRotation().getX();
            bipedLeftArm.rotateAngleY = 0.017453292f * entityarmorstand.getLeftArmRotation().getY();
            bipedLeftArm.rotateAngleZ = 0.017453292f * entityarmorstand.getLeftArmRotation().getZ();
            bipedRightArm.rotateAngleX = 0.017453292f * entityarmorstand.getRightArmRotation().getX();
            bipedRightArm.rotateAngleY = 0.017453292f * entityarmorstand.getRightArmRotation().getY();
            bipedRightArm.rotateAngleZ = 0.017453292f * entityarmorstand.getRightArmRotation().getZ();
            bipedLeftLeg.rotateAngleX = 0.017453292f * entityarmorstand.getLeftLegRotation().getX();
            bipedLeftLeg.rotateAngleY = 0.017453292f * entityarmorstand.getLeftLegRotation().getY();
            bipedLeftLeg.rotateAngleZ = 0.017453292f * entityarmorstand.getLeftLegRotation().getZ();
            bipedLeftLeg.setRotationPoint(1.9f, 11.0f, 0.0f);
            bipedRightLeg.rotateAngleX = 0.017453292f * entityarmorstand.getRightLegRotation().getX();
            bipedRightLeg.rotateAngleY = 0.017453292f * entityarmorstand.getRightLegRotation().getY();
            bipedRightLeg.rotateAngleZ = 0.017453292f * entityarmorstand.getRightLegRotation().getZ();
            bipedRightLeg.setRotationPoint(-1.9f, 11.0f, 0.0f);
            copyModelAngles(bipedHead, bipedHeadwear);
        }
    }
}
