package thaumcraft.client.renderers.models.entity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.monster.EntityEldritchCrab;


public class ModelEldritchCrab extends ModelBase
{
    ModelRenderer TailHelm;
    ModelRenderer TailBare;
    ModelRenderer RFLeg1;
    ModelRenderer RClaw1;
    ModelRenderer Head1;
    ModelRenderer RClaw0;
    ModelRenderer RClaw2;
    ModelRenderer LClaw2;
    ModelRenderer LClaw1;
    ModelRenderer RArm;
    ModelRenderer Torso;
    ModelRenderer RRLeg1;
    ModelRenderer Head0;
    ModelRenderer LRLeg1;
    ModelRenderer LFLeg1;
    ModelRenderer RRLeg0;
    ModelRenderer RFLeg0;
    ModelRenderer LFLeg0;
    ModelRenderer LRLeg0;
    ModelRenderer LClaw0;
    ModelRenderer LArm;
    
    public ModelEldritchCrab() {
        textureWidth = 128;
        textureHeight = 64;
        (TailHelm = new ModelRenderer(this, 0, 0)).addBox(-4.5f, -4.5f, -0.4f, 9, 9, 9);
        TailHelm.setRotationPoint(0.0f, 18.0f, 0.0f);
        setRotation(TailHelm, 0.1047198f, 0.0f, 0.0f);
        (TailBare = new ModelRenderer(this, 64, 0)).addBox(-4.0f, -4.0f, -0.4f, 8, 8, 8);
        TailBare.setRotationPoint(0.0f, 18.0f, 0.0f);
        setRotation(TailBare, 0.1047198f, 0.0f, 0.0f);
        (RClaw1 = new ModelRenderer(this, 0, 47)).addBox(-2.0f, -1.0f, -5.066667f, 4, 3, 5);
        RClaw1.setRotationPoint(-6.0f, 15.5f, -10.0f);
        (Head1 = new ModelRenderer(this, 0, 38)).addBox(-2.0f, -1.5f, -9.066667f, 4, 4, 1);
        Head1.setRotationPoint(0.0f, 18.0f, 0.0f);
        (RClaw0 = new ModelRenderer(this, 0, 55)).addBox(-2.0f, -2.5f, -3.066667f, 4, 5, 3);
        RClaw0.setRotationPoint(-6.0f, 17.0f, -7.0f);
        (RClaw2 = new ModelRenderer(this, 14, 54)).addBox(-1.5f, -1.0f, -4.066667f, 3, 2, 5);
        RClaw2.setRotationPoint(-6.0f, 18.5f, -10.0f);
        setRotation(RClaw2, 0.3141593f, 0.0f, 0.0f);
        (RArm = new ModelRenderer(this, 44, 4)).addBox(-1.0f, -1.0f, -5.066667f, 2, 2, 6);
        RArm.setRotationPoint(-3.0f, 17.0f, -4.0f);
        setRotation(RArm, 0.0f, 0.7504916f, 0.0f);
        (LClaw2 = new ModelRenderer(this, 14, 54)).addBox(-1.5f, -1.0f, -4.066667f, 3, 2, 5);
        LClaw2.setRotationPoint(6.0f, 18.5f, -10.0f);
        setRotation(LClaw2, 0.3141593f, 0.0f, 0.0f);
        LClaw1 = new ModelRenderer(this, 0, 47);
        LClaw1.mirror = true;
        LClaw1.addBox(-2.0f, -1.0f, -5.066667f, 4, 3, 5);
        LClaw1.setRotationPoint(6.0f, 15.5f, -10.0f);
        LClaw0 = new ModelRenderer(this, 0, 55);
        LClaw0.mirror = true;
        LClaw0.addBox(-2.0f, -2.5f, -3.066667f, 4, 5, 3);
        LClaw0.setRotationPoint(6.0f, 17.0f, -7.0f);
        (LArm = new ModelRenderer(this, 44, 4)).addBox(-1.0f, -1.0f, -4.066667f, 2, 2, 6);
        LArm.setRotationPoint(4.0f, 17.0f, -5.0f);
        setRotation(LArm, 0.0f, -0.7504916f, 0.0f);
        (Torso = new ModelRenderer(this, 0, 18)).addBox(-3.5f, -3.5f, -6.066667f, 7, 7, 6);
        Torso.setRotationPoint(0.0f, 18.0f, 0.0f);
        setRotation(Torso, 0.0523599f, 0.0f, 0.0f);
        (Head0 = new ModelRenderer(this, 0, 31)).addBox(-2.5f, -2.0f, -8.066667f, 5, 5, 2);
        Head0.setRotationPoint(0.0f, 18.0f, 0.0f);
        (RRLeg1 = new ModelRenderer(this, 36, 4)).addBox(-4.5f, 1.0f, -0.9f, 2, 5, 2);
        RRLeg1.setRotationPoint(-4.0f, 20.0f, -1.5f);
        (RFLeg1 = new ModelRenderer(this, 36, 4)).addBox(-5.0f, 1.0f, -1.066667f, 2, 5, 2);
        RFLeg1.setRotationPoint(-4.0f, 20.0f, -3.5f);
        (LRLeg1 = new ModelRenderer(this, 36, 4)).addBox(2.5f, 1.0f, -0.9f, 2, 5, 2);
        LRLeg1.setRotationPoint(4.0f, 20.0f, -1.5f);
        (LFLeg1 = new ModelRenderer(this, 36, 4)).addBox(3.0f, 1.0f, -1.066667f, 2, 5, 2);
        LFLeg1.setRotationPoint(4.0f, 20.0f, -3.5f);
        (RRLeg0 = new ModelRenderer(this, 36, 0)).addBox(-4.5f, -1.0f, -0.9f, 6, 2, 2);
        RRLeg0.setRotationPoint(-4.0f, 20.0f, -1.5f);
        (RFLeg0 = new ModelRenderer(this, 36, 0)).addBox(-5.0f, -1.0f, -1.066667f, 6, 2, 2);
        RFLeg0.setRotationPoint(-4.0f, 20.0f, -3.5f);
        (LFLeg0 = new ModelRenderer(this, 36, 0)).addBox(-1.0f, -1.0f, -1.066667f, 6, 2, 2);
        LFLeg0.setRotationPoint(4.0f, 20.0f, -3.5f);
        (LRLeg0 = new ModelRenderer(this, 36, 0)).addBox(-1.5f, -1.0f, -0.9f, 6, 2, 2);
        LRLeg0.setRotationPoint(4.0f, 20.0f, -1.5f);
    }
    
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        if (entity instanceof EntityEldritchCrab && ((EntityEldritchCrab)entity).hasHelm()) {
            TailHelm.render(f5);
        }
        else {
            TailBare.render(f5);
        }
        RFLeg1.render(f5);
        RClaw1.render(f5);
        Head1.render(f5);
        RClaw0.render(f5);
        RClaw2.render(f5);
        LClaw2.render(f5);
        LClaw1.render(f5);
        RArm.render(f5);
        Torso.render(f5);
        RRLeg1.render(f5);
        Head0.render(f5);
        LRLeg1.render(f5);
        LFLeg1.render(f5);
        RRLeg0.render(f5);
        RFLeg0.render(f5);
        LFLeg0.render(f5);
        LRLeg0.render(f5);
        LClaw0.render(f5);
        LArm.render(f5);
    }
    
    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        setRotation(RRLeg1, 0.0f, 0.2094395f, 0.4363323f);
        setRotation(RFLeg1, 0.0f, -0.2094395f, 0.4363323f);
        setRotation(LRLeg1, 0.0f, -0.2094395f, -0.4363323f);
        setRotation(LFLeg1, 0.0f, 0.2094395f, -0.4363323f);
        setRotation(RRLeg0, 0.0f, 0.2094395f, 0.4363323f);
        setRotation(RFLeg0, 0.0f, -0.2094395f, 0.4363323f);
        setRotation(LFLeg0, 0.0f, 0.2094395f, -0.4363323f);
        setRotation(LRLeg0, 0.0f, -0.2094395f, -0.4363323f);
        float f9 = -(MathHelper.cos(par1 * 0.6662f * 2.0f + 0.0f) * 0.4f) * par2;
        float f10 = -(MathHelper.cos(par1 * 0.6662f * 2.0f + 3.1415927f) * 0.4f) * par2;
        ModelRenderer rrLeg1 = RRLeg1;
        rrLeg1.rotateAngleY += f9;
        ModelRenderer rrLeg2 = RRLeg0;
        rrLeg2.rotateAngleY += f9;
        ModelRenderer lrLeg1 = LRLeg1;
        lrLeg1.rotateAngleY += -f9;
        ModelRenderer lrLeg2 = LRLeg0;
        lrLeg2.rotateAngleY += -f9;
        ModelRenderer rfLeg1 = RFLeg1;
        rfLeg1.rotateAngleY += f10;
        ModelRenderer rfLeg2 = RFLeg0;
        rfLeg2.rotateAngleY += f10;
        ModelRenderer lfLeg1 = LFLeg1;
        lfLeg1.rotateAngleY += -f10;
        ModelRenderer lfLeg2 = LFLeg0;
        lfLeg2.rotateAngleY += -f10;
        ModelRenderer rrLeg3 = RRLeg1;
        rrLeg3.rotateAngleZ += f9;
        ModelRenderer rrLeg4 = RRLeg0;
        rrLeg4.rotateAngleZ += f9;
        ModelRenderer lrLeg3 = LRLeg1;
        lrLeg3.rotateAngleZ += -f9;
        ModelRenderer lrLeg4 = LRLeg0;
        lrLeg4.rotateAngleZ += -f9;
        ModelRenderer rfLeg3 = RFLeg1;
        rfLeg3.rotateAngleZ += f10;
        ModelRenderer rfLeg4 = RFLeg0;
        rfLeg4.rotateAngleZ += f10;
        ModelRenderer lfLeg3 = LFLeg1;
        lfLeg3.rotateAngleZ += -f10;
        ModelRenderer lfLeg4 = LFLeg0;
        lfLeg4.rotateAngleZ += -f10;
        ModelRenderer tailBare = TailBare;
        ModelRenderer tailHelm = TailHelm;
        float n = MathHelper.cos(par1 * 0.6662f) * 2.0f * par2 * 0.125f;
        tailHelm.rotateAngleY = n;
        tailBare.rotateAngleY = n;
        ModelRenderer tailBare2 = TailBare;
        ModelRenderer tailHelm2 = TailHelm;
        float n2 = MathHelper.cos(par1 * 0.6662f) * par2 * 0.125f;
        tailHelm2.rotateAngleZ = n2;
        tailBare2.rotateAngleZ = n2;
        RClaw2.rotateAngleX = 0.3141593f - MathHelper.sin(entity.ticksExisted / 4.0f) * 0.25f;
        LClaw2.rotateAngleX = 0.3141593f + MathHelper.sin(entity.ticksExisted / 4.1f) * 0.25f;
        RClaw1.rotateAngleX = MathHelper.sin(entity.ticksExisted / 4.0f) * 0.125f;
        LClaw1.rotateAngleX = -MathHelper.sin(entity.ticksExisted / 4.1f) * 0.125f;
    }
}
