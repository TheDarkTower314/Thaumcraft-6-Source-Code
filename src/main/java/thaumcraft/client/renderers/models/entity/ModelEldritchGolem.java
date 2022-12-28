package thaumcraft.client.renderers.models.entity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.monster.boss.EntityEldritchGolem;


public class ModelEldritchGolem extends ModelBase
{
    ModelRenderer Frontcloth1;
    ModelRenderer CollarL;
    ModelRenderer Cloak1;
    ModelRenderer CloakCL;
    ModelRenderer CloakCR;
    ModelRenderer Cloak3;
    ModelRenderer Cloak2;
    ModelRenderer Head;
    ModelRenderer Head2;
    ModelRenderer Frontcloth0;
    ModelRenderer CollarB;
    ModelRenderer Torso;
    ModelRenderer CollarR;
    ModelRenderer CollarF;
    ModelRenderer CollarBlack;
    ModelRenderer ShoulderR1;
    ModelRenderer ArmL;
    ModelRenderer ShoulderR;
    ModelRenderer ShoulderR2;
    ModelRenderer ShoulderR0;
    ModelRenderer ArmR;
    ModelRenderer ShoulderL1;
    ModelRenderer ShoulderL0;
    ModelRenderer ShoulderL;
    ModelRenderer ShoulderL2;
    ModelRenderer BackpanelR1;
    ModelRenderer WaistR1;
    ModelRenderer WaistR2;
    ModelRenderer WaistR3;
    ModelRenderer LegR;
    ModelRenderer WaistL1;
    ModelRenderer WaistL2;
    ModelRenderer WaistL3;
    ModelRenderer Frontcloth2;
    ModelRenderer BackpanelL1;
    ModelRenderer LegL;
    
    public ModelEldritchGolem() {
        textureWidth = 128;
        textureHeight = 64;
        (Cloak1 = new ModelRenderer(this, 0, 47)).addBox(-5.0f, 1.5f, 4.0f, 10, 12, 1);
        Cloak1.setRotationPoint(0.0f, 0.0f, -2.5f);
        Cloak1.setTextureSize(128, 64);
        setRotation(Cloak1, 0.1396263f, 0.0f, 0.0f);
        (Cloak3 = new ModelRenderer(this, 0, 37)).addBox(-5.0f, 17.5f, -0.8f, 10, 4, 1);
        Cloak3.setRotationPoint(0.0f, 0.0f, -2.5f);
        Cloak3.setTextureSize(128, 64);
        setRotation(Cloak3, 0.4465716f, 0.0f, 0.0f);
        (Cloak2 = new ModelRenderer(this, 0, 59)).addBox(-5.0f, 13.5f, 1.7f, 10, 4, 1);
        Cloak2.setRotationPoint(0.0f, 0.0f, -2.5f);
        Cloak2.setTextureSize(128, 64);
        setRotation(Cloak2, 0.3069452f, 0.0f, 0.0f);
        (CloakCL = new ModelRenderer(this, 0, 43)).addBox(3.0f, 0.5f, 2.0f, 2, 1, 3);
        CloakCL.setRotationPoint(0.0f, 0.0f, -2.5f);
        CloakCL.setTextureSize(128, 64);
        setRotation(CloakCL, 0.1396263f, 0.0f, 0.0f);
        (CloakCR = new ModelRenderer(this, 0, 43)).addBox(-5.0f, 0.5f, 2.0f, 2, 1, 3);
        CloakCR.setRotationPoint(0.0f, 0.0f, -2.5f);
        CloakCR.setTextureSize(128, 64);
        setRotation(CloakCR, 0.1396263f, 0.0f, 0.0f);
        (Head = new ModelRenderer(this, 47, 12)).addBox(-3.5f, -6.0f, -2.5f, 7, 7, 5);
        Head.setRotationPoint(0.0f, 4.5f, -3.8f);
        Head.setTextureSize(128, 64);
        setRotation(Head, -0.1047198f, 0.0f, 0.0f);
        (Head2 = new ModelRenderer(this, 26, 16)).addBox(-2.0f, -2.0f, -2.0f, 4, 4, 4);
        Head2.setRotationPoint(0.0f, 0.0f, -5.0f);
        Head2.setTextureSize(128, 64);
        setRotation(Head2, -0.1047198f, 0.0f, 0.0f);
        (CollarL = new ModelRenderer(this, 75, 50)).addBox(3.5f, -0.5f, -7.0f, 1, 4, 10);
        CollarL.setRotationPoint(0.0f, 0.0f, -2.5f);
        CollarL.setTextureSize(128, 64);
        setRotation(CollarL, 0.837758f, 0.0f, 0.0f);
        (CollarR = new ModelRenderer(this, 67, 50)).addBox(-4.5f, -0.5f, -7.0f, 1, 4, 10);
        CollarR.setRotationPoint(0.0f, 0.0f, -2.5f);
        CollarR.setTextureSize(128, 64);
        setRotation(CollarR, 0.837758f, 0.0f, 0.0f);
        (CollarB = new ModelRenderer(this, 77, 59)).addBox(-3.5f, -0.5f, 2.0f, 7, 4, 1);
        CollarB.setRotationPoint(0.0f, 0.0f, -2.5f);
        CollarB.setTextureSize(128, 64);
        setRotation(CollarB, 0.837758f, 0.0f, 0.0f);
        (CollarF = new ModelRenderer(this, 77, 59)).addBox(-3.5f, -0.5f, -7.0f, 7, 4, 1);
        CollarF.setRotationPoint(0.0f, 0.0f, -2.5f);
        CollarF.setTextureSize(128, 64);
        setRotation(CollarF, 0.837758f, 0.0f, 0.0f);
        (CollarBlack = new ModelRenderer(this, 22, 0)).addBox(-3.5f, 0.0f, -6.0f, 7, 1, 8);
        CollarBlack.setRotationPoint(0.0f, 0.0f, -2.5f);
        CollarBlack.setTextureSize(128, 64);
        setRotation(CollarBlack, 0.837758f, 0.0f, 0.0f);
        (Frontcloth0 = new ModelRenderer(this, 114, 52)).addBox(-3.0f, 3.2f, -3.5f, 6, 10, 1);
        Frontcloth0.setRotationPoint(0.0f, 0.0f, -2.5f);
        Frontcloth0.setTextureSize(114, 64);
        setRotation(Frontcloth0, 0.1745329f, 0.0f, 0.0f);
        (Frontcloth1 = new ModelRenderer(this, 114, 39)).addBox(-1.0f, 1.5f, -3.5f, 6, 6, 1);
        Frontcloth1.setRotationPoint(-2.0f, 12.0f, 0.0f);
        Frontcloth1.setTextureSize(114, 64);
        setRotation(Frontcloth1, -0.1047198f, 0.0f, 0.0f);
        (Frontcloth2 = new ModelRenderer(this, 114, 47)).addBox(-1.0f, 8.5f, -1.5f, 6, 3, 1);
        Frontcloth2.setRotationPoint(-2.0f, 11.0f, 0.0f);
        Frontcloth2.setTextureSize(114, 64);
        setRotation(Frontcloth2, -0.3316126f, 0.0f, 0.0f);
        (Torso = new ModelRenderer(this, 34, 45)).addBox(-5.0f, 2.5f, -3.0f, 10, 10, 6);
        Torso.setRotationPoint(0.0f, 0.0f, -2.5f);
        Torso.setTextureSize(128, 64);
        Torso.mirror = true;
        setRotation(Torso, 0.1745329f, 0.0f, 0.0f);
        (ArmR = new ModelRenderer(this, 78, 32)).addBox(-3.5f, 1.5f, -2.0f, 4, 13, 5);
        ArmR.setRotationPoint(-5.0f, 3.0f, -2.0f);
        ArmR.setTextureSize(128, 64);
        setRotation(ArmR, 0.0f, 0.0f, 0.1047198f);
        (ShoulderR1 = new ModelRenderer(this, 0, 23)).addBox(-3.3f, 4.0f, -2.5f, 1, 2, 6);
        ShoulderR1.setTextureSize(128, 64);
        setRotation(ShoulderR1, 0.0f, 0.0f, 1.186824f);
        (ShoulderR = new ModelRenderer(this, 0, 0)).addBox(-4.3f, -1.0f, -3.0f, 4, 5, 7);
        ShoulderR.setTextureSize(128, 64);
        setRotation(ShoulderR, 0.0f, 0.0f, 1.186824f);
        (ShoulderR2 = new ModelRenderer(this, 0, 12)).addBox(-2.3f, 4.0f, -3.0f, 2, 3, 7);
        ShoulderR2.setTextureSize(128, 64);
        setRotation(ShoulderR2, 0.0f, 0.0f, 1.186824f);
        (ShoulderR0 = new ModelRenderer(this, 56, 31)).addBox(-4.5f, -1.5f, -2.5f, 5, 6, 6);
        ShoulderR0.setTextureSize(128, 64);
        setRotation(ShoulderR0, 0.0f, 0.0f, 0.0f);
        ArmL = new ModelRenderer(this, 78, 32);
        ArmL.mirror = true;
        ArmL.addBox(-0.5f, 1.5f, -2.0f, 4, 13, 5);
        ArmL.setRotationPoint(5.0f, 3.0f, -2.0f);
        ArmL.setTextureSize(128, 64);
        setRotation(ArmL, 0.0f, 0.0f, -0.1047198f);
        ShoulderL1 = new ModelRenderer(this, 0, 23);
        ShoulderL1.mirror = true;
        ShoulderL1.addBox(2.3f, 4.0f, -2.5f, 1, 2, 6);
        ShoulderL1.setTextureSize(128, 64);
        setRotation(ShoulderL1, 0.0f, 0.0f, -1.186824f);
        ShoulderL0 = new ModelRenderer(this, 56, 31);
        ShoulderL0.mirror = true;
        ShoulderL0.addBox(-0.5f, -1.5f, -2.5f, 5, 6, 6);
        ShoulderL0.setTextureSize(128, 64);
        setRotation(ShoulderL0, 0.0f, 0.0f, 0.0f);
        ShoulderL = new ModelRenderer(this, 0, 0);
        ShoulderL.mirror = true;
        ShoulderL.addBox(0.3f, -1.0f, -3.0f, 4, 5, 7);
        ShoulderL.setTextureSize(128, 64);
        setRotation(ShoulderL, 0.0f, 0.0f, -1.186824f);
        ShoulderL2 = new ModelRenderer(this, 0, 12);
        ShoulderL2.mirror = true;
        ShoulderL2.addBox(0.3f, 4.0f, -3.0f, 2, 3, 7);
        ShoulderL2.setTextureSize(128, 64);
        setRotation(ShoulderL2, 0.0f, 0.0f, -1.186824f);
        (BackpanelR1 = new ModelRenderer(this, 96, 7)).addBox(0.0f, 2.5f, -2.5f, 2, 2, 5);
        BackpanelR1.setRotationPoint(-2.0f, 12.0f, 0.0f);
        BackpanelR1.setTextureSize(128, 64);
        setRotation(BackpanelR1, 0.0f, 0.0f, 0.1396263f);
        (WaistR1 = new ModelRenderer(this, 96, 14)).addBox(-3.0f, -0.5f, -2.5f, 5, 3, 5);
        WaistR1.setRotationPoint(-2.0f, 12.0f, 0.0f);
        WaistR1.setTextureSize(128, 64);
        setRotation(WaistR1, 0.0f, 0.0f, 0.1396263f);
        (WaistR2 = new ModelRenderer(this, 116, 13)).addBox(-3.0f, 2.5f, -2.5f, 1, 4, 5);
        WaistR2.setRotationPoint(-2.0f, 12.0f, 0.0f);
        WaistR2.setTextureSize(128, 64);
        setRotation(WaistR2, 0.0f, 0.0f, 0.1396263f);
        WaistR3 = new ModelRenderer(this, 114, 5);
        WaistR3.mirror = true;
        WaistR3.addBox(-2.0f, 2.5f, -2.5f, 2, 3, 5);
        WaistR3.setRotationPoint(-2.0f, 12.0f, 0.0f);
        WaistR3.setTextureSize(128, 64);
        setRotation(WaistR3, 0.0f, 0.0f, 0.1396263f);
        (LegR = new ModelRenderer(this, 79, 19)).addBox(-2.5f, 2.5f, -2.0f, 4, 9, 4);
        LegR.setRotationPoint(-2.0f, 12.5f, 0.0f);
        LegR.setTextureSize(128, 64);
        setRotation(LegR, 0.0f, 0.0f, 0.0f);
        (WaistL1 = new ModelRenderer(this, 96, 14)).addBox(-2.0f, -0.5f, -2.5f, 5, 3, 5);
        WaistL1.setRotationPoint(2.0f, 12.0f, 0.0f);
        WaistL1.setTextureSize(128, 64);
        WaistL1.mirror = true;
        setRotation(WaistL1, 0.0f, 0.0f, -0.1396263f);
        (WaistL2 = new ModelRenderer(this, 116, 13)).addBox(2.0f, 2.5f, -2.5f, 1, 4, 5);
        WaistL2.setRotationPoint(2.0f, 12.0f, 0.0f);
        WaistL2.setTextureSize(128, 64);
        WaistL2.mirror = true;
        setRotation(WaistL2, 0.0f, 0.0f, -0.1396263f);
        (WaistL3 = new ModelRenderer(this, 114, 5)).addBox(0.0f, 2.5f, -2.5f, 2, 3, 5);
        WaistL3.setRotationPoint(2.0f, 12.0f, 0.0f);
        WaistL3.setTextureSize(128, 64);
        WaistL3.mirror = true;
        setRotation(WaistL3, 0.0f, 0.0f, -0.1396263f);
        (BackpanelL1 = new ModelRenderer(this, 96, 7)).addBox(-2.0f, 2.5f, -2.5f, 2, 2, 5);
        BackpanelL1.setRotationPoint(2.0f, 12.0f, 0.0f);
        BackpanelL1.setTextureSize(128, 64);
        BackpanelL1.mirror = true;
        setRotation(BackpanelL1, 0.0f, 0.0f, -0.1396263f);
        (LegL = new ModelRenderer(this, 79, 19)).addBox(-1.5f, 2.5f, -2.0f, 4, 9, 4);
        LegL.setRotationPoint(2.0f, 12.5f, 0.0f);
        LegL.setTextureSize(128, 64);
        LegL.mirror = true;
        setRotation(LegL, 0.0f, 0.0f, 0.0f);
        ArmL.addChild(ShoulderL);
        ArmL.addChild(ShoulderL0);
        ArmL.addChild(ShoulderL1);
        ArmL.addChild(ShoulderL2);
        ArmR.addChild(ShoulderR);
        ArmR.addChild(ShoulderR0);
        ArmR.addChild(ShoulderR1);
        ArmR.addChild(ShoulderR2);
    }
    
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        float a = MathHelper.cos(f * 0.44f) * 1.4f * f1;
        float b = MathHelper.cos(f * 0.44f + 3.1415927f) * 1.4f * f1;
        float c = Math.min(a, b);
        Frontcloth1.rotateAngleX = c - 0.1047198f;
        Frontcloth2.rotateAngleX = c - 0.3316126f;
        Cloak1.rotateAngleX = -c / 3.0f + 0.1396263f;
        Cloak2.rotateAngleX = -c / 3.0f + 0.3069452f;
        Cloak3.rotateAngleX = -c / 3.0f + 0.4465716f;
        Frontcloth1.render(f5);
        CollarL.render(f5);
        CollarBlack.render(f5);
        Cloak1.render(f5);
        CloakCL.render(f5);
        CloakCR.render(f5);
        Cloak3.render(f5);
        Cloak2.render(f5);
        if (entity instanceof EntityEldritchGolem && !((EntityEldritchGolem)entity).isHeadless()) {
            Head.render(f5);
        }
        else {
            Head2.render(f5);
        }
        Frontcloth0.render(f5);
        CollarB.render(f5);
        Torso.render(f5);
        CollarR.render(f5);
        CollarF.render(f5);
        Frontcloth1.render(f5);
        ArmL.render(f5);
        ArmR.render(f5);
        BackpanelR1.render(f5);
        WaistR1.render(f5);
        WaistR2.render(f5);
        WaistR3.render(f5);
        LegR.render(f5);
        WaistL1.render(f5);
        WaistL2.render(f5);
        WaistL3.render(f5);
        Frontcloth2.render(f5);
        BackpanelL1.render(f5);
        LegL.render(f5);
    }
    
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);
        if (entity instanceof EntityEldritchGolem && ((EntityEldritchGolem)entity).getSpawnTimer() > 0) {
            Head.rotateAngleY = 0.0f;
            Head.rotateAngleX = ((EntityEldritchGolem)entity).getSpawnTimer() / 2 / 57.295776f;
        }
        else {
            Head.rotateAngleY = par4 / 4.0f / 57.295776f;
            Head.rotateAngleX = par5 / 2.0f / 57.295776f;
            Head2.rotateAngleY = par4 / 57.295776f;
            Head2.rotateAngleX = par5 / 57.295776f;
        }
        LegR.rotateAngleX = MathHelper.cos(par1 * 0.4662f) * 1.4f * par2;
        LegL.rotateAngleX = MathHelper.cos(par1 * 0.4662f + 3.1415927f) * 1.4f * par2;
    }
    
    public void setLivingAnimations(EntityLivingBase p_78086_1_, float par1, float par2, float par3) {
        EntityEldritchGolem golem = (EntityEldritchGolem)p_78086_1_;
        int i = golem.getAttackTimer();
        if (i > 0) {
            ArmR.rotateAngleX = -2.0f + 1.5f * doAbs(i - par3, 10.0f);
            ArmL.rotateAngleX = -2.0f + 1.5f * doAbs(i - par3, 10.0f);
        }
        else {
            ArmR.rotateAngleX = MathHelper.cos(par1 * 0.4f + 3.1415927f) * 2.0f * par2 * 0.5f;
            ArmL.rotateAngleX = MathHelper.cos(par1 * 0.4f) * 2.0f * par2 * 0.5f;
        }
    }
    
    private float doAbs(float p_78172_1_, float p_78172_2_) {
        return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5f) - p_78172_2_ * 0.25f) / (p_78172_2_ * 0.25f);
    }
    
    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
