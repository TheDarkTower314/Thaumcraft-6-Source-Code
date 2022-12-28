package thaumcraft.client.renderers.models.entity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.monster.EntityPech;


public class ModelPech extends ModelBiped
{
    ModelRenderer Jowls;
    ModelRenderer LowerPack;
    ModelRenderer UpperPack;
    
    public ModelPech() {
        textureWidth = 128;
        textureHeight = 64;
        (bipedBody = new ModelRenderer(this, 34, 12)).addBox(-3.0f, 0.0f, 0.0f, 6, 10, 6);
        bipedBody.setRotationPoint(0.0f, 9.0f, -3.0f);
        bipedBody.setTextureSize(128, 64);
        bipedBody.mirror = true;
        setRotation(bipedBody, 0.3129957f, 0.0f, 0.0f);
        bipedRightLeg = new ModelRenderer(this, 35, 1);
        bipedRightLeg.mirror = true;
        bipedRightLeg.addBox(-2.9f, 0.0f, 0.0f, 3, 6, 3);
        bipedRightLeg.setRotationPoint(0.0f, 18.0f, 0.0f);
        bipedRightLeg.setTextureSize(128, 64);
        bipedRightLeg.mirror = true;
        setRotation(bipedRightLeg, 0.0f, 0.0f, 0.0f);
        bipedRightLeg.mirror = false;
        (bipedLeftLeg = new ModelRenderer(this, 35, 1)).addBox(-0.1f, 0.0f, 0.0f, 3, 6, 3);
        bipedLeftLeg.setRotationPoint(0.0f, 18.0f, 0.0f);
        bipedLeftLeg.setTextureSize(128, 64);
        bipedLeftLeg.mirror = true;
        setRotation(bipedLeftLeg, 0.0f, 0.0f, 0.0f);
        (bipedHead = new ModelRenderer(this, 2, 11)).addBox(-3.5f, -5.0f, -5.0f, 7, 5, 5);
        bipedHead.setRotationPoint(0.0f, 8.0f, 0.0f);
        bipedHead.setTextureSize(128, 64);
        bipedHead.mirror = true;
        setRotation(bipedHead, 0.0f, 0.0f, 0.0f);
        (Jowls = new ModelRenderer(this, 1, 21)).addBox(-4.0f, -1.0f, -6.0f, 8, 3, 5);
        Jowls.setRotationPoint(0.0f, 8.0f, 0.0f);
        Jowls.setTextureSize(128, 64);
        Jowls.mirror = true;
        setRotation(Jowls, 0.0f, 0.0f, 0.0f);
        (LowerPack = new ModelRenderer(this, 0, 0)).addBox(-5.0f, 0.0f, 0.0f, 10, 5, 5);
        LowerPack.setRotationPoint(0.0f, 10.0f, 3.5f);
        LowerPack.setTextureSize(128, 64);
        LowerPack.mirror = true;
        setRotation(LowerPack, 0.3013602f, 0.0f, 0.0f);
        (UpperPack = new ModelRenderer(this, 64, 1)).addBox(-7.5f, -14.0f, 0.0f, 15, 14, 11);
        UpperPack.setRotationPoint(0.0f, 10.0f, 3.0f);
        UpperPack.setTextureSize(128, 64);
        UpperPack.mirror = true;
        setRotation(UpperPack, 0.4537856f, 0.0f, 0.0f);
        bipedRightArm = new ModelRenderer(this, 52, 2);
        bipedRightArm.mirror = true;
        bipedRightArm.addBox(-2.0f, 0.0f, -1.0f, 2, 6, 2);
        bipedRightArm.setRotationPoint(-3.0f, 10.0f, -1.0f);
        bipedRightArm.setTextureSize(128, 64);
        bipedRightArm.mirror = true;
        setRotation(bipedRightArm, 0.0f, 0.0f, 0.0f);
        bipedRightArm.mirror = false;
        (bipedLeftArm = new ModelRenderer(this, 52, 2)).addBox(0.0f, 0.0f, -1.0f, 2, 6, 2);
        bipedLeftArm.setRotationPoint(3.0f, 10.0f, -1.0f);
        bipedLeftArm.setTextureSize(128, 64);
        bipedLeftArm.mirror = true;
        setRotation(bipedLeftArm, 0.0f, 0.0f, 0.0f);
    }
    
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
        setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
        bipedBody.render(par7);
        bipedRightLeg.render(par7);
        bipedLeftLeg.render(par7);
        bipedHead.render(par7);
        Jowls.render(par7);
        LowerPack.render(par7);
        UpperPack.render(par7);
        bipedRightArm.render(par7);
        bipedLeftArm.render(par7);
    }
    
    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        bipedHead.rotateAngleY = par4 / 57.295776f;
        bipedHead.rotateAngleX = par5 / 57.295776f;
        float mumble = 0.0f;
        if (entity instanceof EntityPech) {
            mumble = ((EntityPech)entity).mumble;
        }
        Jowls.rotateAngleY = bipedHead.rotateAngleY;
        Jowls.rotateAngleX = bipedHead.rotateAngleX + (0.2617994f + MathHelper.cos(par1 * 0.6662f) * par2 * 0.25f) + 0.34906587f * Math.abs(MathHelper.sin(mumble / 8.0f));
        bipedRightArm.rotateAngleX = MathHelper.cos(par1 * 0.6662f + 3.1415927f) * 2.0f * par2 * 0.5f;
        bipedLeftArm.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 2.0f * par2 * 0.5f;
        bipedRightArm.rotateAngleZ = 0.0f;
        bipedLeftArm.rotateAngleZ = 0.0f;
        bipedRightLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 1.4f * par2;
        bipedLeftLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662f + 3.1415927f) * 1.4f * par2;
        bipedRightLeg.rotateAngleY = 0.0f;
        bipedLeftLeg.rotateAngleY = 0.0f;
        LowerPack.rotateAngleY = MathHelper.cos(par1 * 0.6662f) * 2.0f * par2 * 0.125f;
        LowerPack.rotateAngleZ = MathHelper.cos(par1 * 0.6662f) * 2.0f * par2 * 0.125f;
        if (isRiding) {
            ModelRenderer bipedRightArm = this.bipedRightArm;
            bipedRightArm.rotateAngleX -= 0.62831855f;
            ModelRenderer bipedLeftArm = this.bipedLeftArm;
            bipedLeftArm.rotateAngleX -= 0.62831855f;
            bipedRightLeg.rotateAngleX = -1.2566371f;
            bipedLeftLeg.rotateAngleX = -1.2566371f;
            bipedRightLeg.rotateAngleY = 0.31415927f;
            bipedLeftLeg.rotateAngleY = -0.31415927f;
        }
        bipedRightArm.rotateAngleY = 0.0f;
        bipedLeftArm.rotateAngleY = 0.0f;
        if (swingProgress > -9990.0f) {
            float f6 = swingProgress;
            ModelRenderer bipedRightArm2 = bipedRightArm;
            bipedRightArm2.rotateAngleY += bipedBody.rotateAngleY;
            ModelRenderer bipedLeftArm2 = bipedLeftArm;
            bipedLeftArm2.rotateAngleY += bipedBody.rotateAngleY;
            ModelRenderer bipedLeftArm3 = bipedLeftArm;
            bipedLeftArm3.rotateAngleX += bipedBody.rotateAngleY;
            f6 = 1.0f - swingProgress;
            f6 *= f6;
            f6 *= f6;
            f6 = 1.0f - f6;
            float f7 = MathHelper.sin(f6 * 3.1415927f);
            float f8 = MathHelper.sin(swingProgress * 3.1415927f) * -(bipedHead.rotateAngleX - 0.7f) * 0.75f;
            bipedRightArm.rotateAngleX -= (float)(f7 * 1.2 + f8);
            ModelRenderer bipedRightArm3 = bipedRightArm;
            bipedRightArm3.rotateAngleY += bipedBody.rotateAngleY * 2.0f;
            bipedRightArm.rotateAngleZ = MathHelper.sin(swingProgress * 3.1415927f) * -0.4f;
        }
        if (entity.isSneaking()) {
            ModelRenderer bipedRightArm4 = bipedRightArm;
            bipedRightArm4.rotateAngleX += 0.4f;
            ModelRenderer bipedLeftArm4 = bipedLeftArm;
            bipedLeftArm4.rotateAngleX += 0.4f;
        }
        ModelRenderer bipedRightArm5 = bipedRightArm;
        bipedRightArm5.rotateAngleZ += MathHelper.cos(par3 * 0.09f) * 0.05f + 0.05f;
        ModelRenderer bipedLeftArm5 = bipedLeftArm;
        bipedLeftArm5.rotateAngleZ -= MathHelper.cos(par3 * 0.09f) * 0.05f + 0.05f;
        ModelRenderer bipedRightArm6 = bipedRightArm;
        bipedRightArm6.rotateAngleX += MathHelper.sin(par3 * 0.067f) * 0.05f;
        ModelRenderer bipedLeftArm6 = bipedLeftArm;
        bipedLeftArm6.rotateAngleX -= MathHelper.sin(par3 * 0.067f) * 0.05f;
    }
}
