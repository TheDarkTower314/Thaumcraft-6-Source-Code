// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.entity;

import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.monster.EntityEldritchCrab;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;

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
        this.textureWidth = 128;
        this.textureHeight = 64;
        (this.TailHelm = new ModelRenderer(this, 0, 0)).addBox(-4.5f, -4.5f, -0.4f, 9, 9, 9);
        this.TailHelm.setRotationPoint(0.0f, 18.0f, 0.0f);
        this.setRotation(this.TailHelm, 0.1047198f, 0.0f, 0.0f);
        (this.TailBare = new ModelRenderer(this, 64, 0)).addBox(-4.0f, -4.0f, -0.4f, 8, 8, 8);
        this.TailBare.setRotationPoint(0.0f, 18.0f, 0.0f);
        this.setRotation(this.TailBare, 0.1047198f, 0.0f, 0.0f);
        (this.RClaw1 = new ModelRenderer(this, 0, 47)).addBox(-2.0f, -1.0f, -5.066667f, 4, 3, 5);
        this.RClaw1.setRotationPoint(-6.0f, 15.5f, -10.0f);
        (this.Head1 = new ModelRenderer(this, 0, 38)).addBox(-2.0f, -1.5f, -9.066667f, 4, 4, 1);
        this.Head1.setRotationPoint(0.0f, 18.0f, 0.0f);
        (this.RClaw0 = new ModelRenderer(this, 0, 55)).addBox(-2.0f, -2.5f, -3.066667f, 4, 5, 3);
        this.RClaw0.setRotationPoint(-6.0f, 17.0f, -7.0f);
        (this.RClaw2 = new ModelRenderer(this, 14, 54)).addBox(-1.5f, -1.0f, -4.066667f, 3, 2, 5);
        this.RClaw2.setRotationPoint(-6.0f, 18.5f, -10.0f);
        this.setRotation(this.RClaw2, 0.3141593f, 0.0f, 0.0f);
        (this.RArm = new ModelRenderer(this, 44, 4)).addBox(-1.0f, -1.0f, -5.066667f, 2, 2, 6);
        this.RArm.setRotationPoint(-3.0f, 17.0f, -4.0f);
        this.setRotation(this.RArm, 0.0f, 0.7504916f, 0.0f);
        (this.LClaw2 = new ModelRenderer(this, 14, 54)).addBox(-1.5f, -1.0f, -4.066667f, 3, 2, 5);
        this.LClaw2.setRotationPoint(6.0f, 18.5f, -10.0f);
        this.setRotation(this.LClaw2, 0.3141593f, 0.0f, 0.0f);
        this.LClaw1 = new ModelRenderer(this, 0, 47);
        this.LClaw1.mirror = true;
        this.LClaw1.addBox(-2.0f, -1.0f, -5.066667f, 4, 3, 5);
        this.LClaw1.setRotationPoint(6.0f, 15.5f, -10.0f);
        this.LClaw0 = new ModelRenderer(this, 0, 55);
        this.LClaw0.mirror = true;
        this.LClaw0.addBox(-2.0f, -2.5f, -3.066667f, 4, 5, 3);
        this.LClaw0.setRotationPoint(6.0f, 17.0f, -7.0f);
        (this.LArm = new ModelRenderer(this, 44, 4)).addBox(-1.0f, -1.0f, -4.066667f, 2, 2, 6);
        this.LArm.setRotationPoint(4.0f, 17.0f, -5.0f);
        this.setRotation(this.LArm, 0.0f, -0.7504916f, 0.0f);
        (this.Torso = new ModelRenderer(this, 0, 18)).addBox(-3.5f, -3.5f, -6.066667f, 7, 7, 6);
        this.Torso.setRotationPoint(0.0f, 18.0f, 0.0f);
        this.setRotation(this.Torso, 0.0523599f, 0.0f, 0.0f);
        (this.Head0 = new ModelRenderer(this, 0, 31)).addBox(-2.5f, -2.0f, -8.066667f, 5, 5, 2);
        this.Head0.setRotationPoint(0.0f, 18.0f, 0.0f);
        (this.RRLeg1 = new ModelRenderer(this, 36, 4)).addBox(-4.5f, 1.0f, -0.9f, 2, 5, 2);
        this.RRLeg1.setRotationPoint(-4.0f, 20.0f, -1.5f);
        (this.RFLeg1 = new ModelRenderer(this, 36, 4)).addBox(-5.0f, 1.0f, -1.066667f, 2, 5, 2);
        this.RFLeg1.setRotationPoint(-4.0f, 20.0f, -3.5f);
        (this.LRLeg1 = new ModelRenderer(this, 36, 4)).addBox(2.5f, 1.0f, -0.9f, 2, 5, 2);
        this.LRLeg1.setRotationPoint(4.0f, 20.0f, -1.5f);
        (this.LFLeg1 = new ModelRenderer(this, 36, 4)).addBox(3.0f, 1.0f, -1.066667f, 2, 5, 2);
        this.LFLeg1.setRotationPoint(4.0f, 20.0f, -3.5f);
        (this.RRLeg0 = new ModelRenderer(this, 36, 0)).addBox(-4.5f, -1.0f, -0.9f, 6, 2, 2);
        this.RRLeg0.setRotationPoint(-4.0f, 20.0f, -1.5f);
        (this.RFLeg0 = new ModelRenderer(this, 36, 0)).addBox(-5.0f, -1.0f, -1.066667f, 6, 2, 2);
        this.RFLeg0.setRotationPoint(-4.0f, 20.0f, -3.5f);
        (this.LFLeg0 = new ModelRenderer(this, 36, 0)).addBox(-1.0f, -1.0f, -1.066667f, 6, 2, 2);
        this.LFLeg0.setRotationPoint(4.0f, 20.0f, -3.5f);
        (this.LRLeg0 = new ModelRenderer(this, 36, 0)).addBox(-1.5f, -1.0f, -0.9f, 6, 2, 2);
        this.LRLeg0.setRotationPoint(4.0f, 20.0f, -1.5f);
    }
    
    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        if (entity instanceof EntityEldritchCrab && ((EntityEldritchCrab)entity).hasHelm()) {
            this.TailHelm.render(f5);
        }
        else {
            this.TailBare.render(f5);
        }
        this.RFLeg1.render(f5);
        this.RClaw1.render(f5);
        this.Head1.render(f5);
        this.RClaw0.render(f5);
        this.RClaw2.render(f5);
        this.LClaw2.render(f5);
        this.LClaw1.render(f5);
        this.RArm.render(f5);
        this.Torso.render(f5);
        this.RRLeg1.render(f5);
        this.Head0.render(f5);
        this.LRLeg1.render(f5);
        this.LFLeg1.render(f5);
        this.RRLeg0.render(f5);
        this.RFLeg0.render(f5);
        this.LFLeg0.render(f5);
        this.LRLeg0.render(f5);
        this.LClaw0.render(f5);
        this.LArm.render(f5);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    public void setRotationAngles(final float par1, final float par2, final float par3, final float par4, final float par5, final float par6, final Entity entity) {
        this.setRotation(this.RRLeg1, 0.0f, 0.2094395f, 0.4363323f);
        this.setRotation(this.RFLeg1, 0.0f, -0.2094395f, 0.4363323f);
        this.setRotation(this.LRLeg1, 0.0f, -0.2094395f, -0.4363323f);
        this.setRotation(this.LFLeg1, 0.0f, 0.2094395f, -0.4363323f);
        this.setRotation(this.RRLeg0, 0.0f, 0.2094395f, 0.4363323f);
        this.setRotation(this.RFLeg0, 0.0f, -0.2094395f, 0.4363323f);
        this.setRotation(this.LFLeg0, 0.0f, 0.2094395f, -0.4363323f);
        this.setRotation(this.LRLeg0, 0.0f, -0.2094395f, -0.4363323f);
        final float f9 = -(MathHelper.cos(par1 * 0.6662f * 2.0f + 0.0f) * 0.4f) * par2;
        final float f10 = -(MathHelper.cos(par1 * 0.6662f * 2.0f + 3.1415927f) * 0.4f) * par2;
        final ModelRenderer rrLeg1 = this.RRLeg1;
        rrLeg1.rotateAngleY += f9;
        final ModelRenderer rrLeg2 = this.RRLeg0;
        rrLeg2.rotateAngleY += f9;
        final ModelRenderer lrLeg1 = this.LRLeg1;
        lrLeg1.rotateAngleY += -f9;
        final ModelRenderer lrLeg2 = this.LRLeg0;
        lrLeg2.rotateAngleY += -f9;
        final ModelRenderer rfLeg1 = this.RFLeg1;
        rfLeg1.rotateAngleY += f10;
        final ModelRenderer rfLeg2 = this.RFLeg0;
        rfLeg2.rotateAngleY += f10;
        final ModelRenderer lfLeg1 = this.LFLeg1;
        lfLeg1.rotateAngleY += -f10;
        final ModelRenderer lfLeg2 = this.LFLeg0;
        lfLeg2.rotateAngleY += -f10;
        final ModelRenderer rrLeg3 = this.RRLeg1;
        rrLeg3.rotateAngleZ += f9;
        final ModelRenderer rrLeg4 = this.RRLeg0;
        rrLeg4.rotateAngleZ += f9;
        final ModelRenderer lrLeg3 = this.LRLeg1;
        lrLeg3.rotateAngleZ += -f9;
        final ModelRenderer lrLeg4 = this.LRLeg0;
        lrLeg4.rotateAngleZ += -f9;
        final ModelRenderer rfLeg3 = this.RFLeg1;
        rfLeg3.rotateAngleZ += f10;
        final ModelRenderer rfLeg4 = this.RFLeg0;
        rfLeg4.rotateAngleZ += f10;
        final ModelRenderer lfLeg3 = this.LFLeg1;
        lfLeg3.rotateAngleZ += -f10;
        final ModelRenderer lfLeg4 = this.LFLeg0;
        lfLeg4.rotateAngleZ += -f10;
        final ModelRenderer tailBare = this.TailBare;
        final ModelRenderer tailHelm = this.TailHelm;
        final float n = MathHelper.cos(par1 * 0.6662f) * 2.0f * par2 * 0.125f;
        tailHelm.rotateAngleY = n;
        tailBare.rotateAngleY = n;
        final ModelRenderer tailBare2 = this.TailBare;
        final ModelRenderer tailHelm2 = this.TailHelm;
        final float n2 = MathHelper.cos(par1 * 0.6662f) * par2 * 0.125f;
        tailHelm2.rotateAngleZ = n2;
        tailBare2.rotateAngleZ = n2;
        this.RClaw2.rotateAngleX = 0.3141593f - MathHelper.sin(entity.ticksExisted / 4.0f) * 0.25f;
        this.LClaw2.rotateAngleX = 0.3141593f + MathHelper.sin(entity.ticksExisted / 4.1f) * 0.25f;
        this.RClaw1.rotateAngleX = MathHelper.sin(entity.ticksExisted / 4.0f) * 0.125f;
        this.LClaw1.rotateAngleX = -MathHelper.sin(entity.ticksExisted / 4.1f) * 0.125f;
    }
}
