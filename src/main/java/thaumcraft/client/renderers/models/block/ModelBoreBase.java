// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.block;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;

public class ModelBoreBase extends ModelBase
{
    ModelRenderer Base1;
    ModelRenderer Base2;
    ModelRenderer PillarMid;
    ModelRenderer Pillar2;
    ModelRenderer Pillar3;
    ModelRenderer Pillar4;
    ModelRenderer Pillar1;
    ModelRenderer Nozzle1;
    ModelRenderer Nozzle2;
    
    public ModelBoreBase() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        (this.Base1 = new ModelRenderer(this, 64, 24)).addBox(-8.0f, 0.0f, -8.0f, 16, 2, 16);
        this.Base1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Base1.setTextureSize(128, 64);
        this.Base1.mirror = true;
        this.setRotation(this.Base1, 0.0f, 0.0f, 0.0f);
        (this.Base2 = new ModelRenderer(this, 64, 24)).addBox(-8.0f, 0.0f, -8.0f, 16, 2, 16);
        this.Base2.setRotationPoint(0.0f, 14.0f, 0.0f);
        this.Base2.setTextureSize(128, 64);
        this.Base2.mirror = true;
        this.setRotation(this.Base2, 0.0f, 0.0f, 0.0f);
        (this.PillarMid = new ModelRenderer(this, 84, 42)).addBox(-2.5f, 0.0f, -2.5f, 5, 12, 5);
        this.PillarMid.setRotationPoint(0.0f, 2.0f, 0.0f);
        this.PillarMid.setTextureSize(128, 64);
        this.PillarMid.mirror = true;
        this.setRotation(this.PillarMid, 0.0f, 0.0f, 0.0f);
        (this.Pillar2 = new ModelRenderer(this, 64, 42)).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4);
        this.Pillar2.setRotationPoint(-5.0f, 2.0f, -5.0f);
        this.Pillar2.setTextureSize(128, 64);
        this.Pillar2.mirror = true;
        this.setRotation(this.Pillar2, 0.0f, 0.0f, 0.0f);
        (this.Pillar3 = new ModelRenderer(this, 64, 42)).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4);
        this.Pillar3.setRotationPoint(-5.0f, 2.0f, 5.0f);
        this.Pillar3.setTextureSize(128, 64);
        this.Pillar3.mirror = true;
        this.setRotation(this.Pillar3, 0.0f, 0.0f, 0.0f);
        (this.Pillar4 = new ModelRenderer(this, 64, 42)).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4);
        this.Pillar4.setRotationPoint(5.0f, 2.0f, 5.0f);
        this.Pillar4.setTextureSize(128, 64);
        this.Pillar4.mirror = true;
        this.setRotation(this.Pillar4, 0.0f, 0.0f, 0.0f);
        (this.Pillar1 = new ModelRenderer(this, 64, 42)).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4);
        this.Pillar1.setRotationPoint(5.0f, 2.0f, -5.0f);
        this.Pillar1.setTextureSize(128, 64);
        this.Pillar1.mirror = true;
        this.setRotation(this.Pillar1, 0.0f, 0.0f, 0.0f);
        (this.Nozzle1 = new ModelRenderer(this, 106, 42)).addBox(2.5f, -2.0f, -2.0f, 5, 4, 4);
        this.Nozzle1.setRotationPoint(0.0f, 8.0f, 0.0f);
        this.Nozzle1.setTextureSize(128, 64);
        this.Nozzle1.mirror = true;
        this.setRotation(this.Nozzle1, 0.0f, 0.0f, 0.0f);
        (this.Nozzle2 = new ModelRenderer(this, 106, 51)).addBox(7.0f, -2.5f, -2.5f, 1, 5, 5);
        this.Nozzle2.setRotationPoint(0.0f, 8.0f, 0.0f);
        this.Nozzle2.setTextureSize(128, 64);
        this.Nozzle2.mirror = true;
        this.setRotation(this.Nozzle2, 0.0f, 0.0f, 0.0f);
    }
    
    public void render() {
        final float f5 = 0.0625f;
        this.Base1.render(f5);
        this.Base2.render(f5);
        this.PillarMid.render(f5);
        this.Pillar2.render(f5);
        this.Pillar3.render(f5);
        this.Pillar4.render(f5);
        this.Pillar1.render(f5);
    }
    
    public void renderNozzle() {
        final float f5 = 0.0625f;
        this.Nozzle1.render(f5);
        this.Nozzle2.render(f5);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
