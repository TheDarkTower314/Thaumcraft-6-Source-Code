// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.block;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;

public class ModelBanner extends ModelBase
{
    ModelRenderer B1;
    ModelRenderer B2;
    ModelRenderer Beam;
    public ModelRenderer Banner;
    ModelRenderer Pole;
    
    public ModelBanner() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        (this.B1 = new ModelRenderer(this, 0, 29)).addBox(-5.0f, -7.5f, -1.5f, 2, 3, 3);
        this.B1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.B1.setTextureSize(128, 64);
        this.B1.mirror = true;
        this.setRotation(this.B1, 0.0f, 0.0f, 0.0f);
        (this.B2 = new ModelRenderer(this, 0, 29)).addBox(3.0f, -7.5f, -1.5f, 2, 3, 3);
        this.B2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.B2.setTextureSize(128, 64);
        this.B2.mirror = true;
        this.setRotation(this.B2, 0.0f, 0.0f, 0.0f);
        (this.Beam = new ModelRenderer(this, 30, 0)).addBox(-7.0f, -7.0f, -1.0f, 14, 2, 2);
        this.Beam.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Beam.setTextureSize(128, 64);
        this.Beam.mirror = true;
        this.setRotation(this.Beam, 0.0f, 0.0f, 0.0f);
        (this.Banner = new ModelRenderer(this, 0, 0)).addBox(-7.0f, 0.0f, -0.5f, 14, 28, 1);
        this.Banner.setRotationPoint(0.0f, -5.0f, 0.0f);
        this.Banner.setTextureSize(128, 64);
        this.Banner.mirror = true;
        this.setRotation(this.Banner, 0.0f, 0.0f, 0.0f);
        (this.Pole = new ModelRenderer(this, 62, 0)).addBox(0.0f, 0.0f, -1.0f, 2, 31, 2);
        this.Pole.setRotationPoint(-1.0f, -7.0f, -2.0f);
        this.Pole.setTextureSize(128, 64);
        this.Pole.mirror = true;
        this.setRotation(this.Pole, 0.0f, 0.0f, 0.0f);
    }
    
    public void renderPole() {
        this.Pole.render(0.0625f);
    }
    
    public void renderBeam() {
        this.Beam.render(0.0625f);
    }
    
    public void renderTabs() {
        this.B1.render(0.0625f);
        this.B2.render(0.0625f);
    }
    
    public void renderBanner() {
        this.Banner.render(0.0625f);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
