// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.block;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;

public class ModelCentrifuge extends ModelBase
{
    ModelRenderer Crossbar;
    ModelRenderer Dingus1;
    ModelRenderer Dingus2;
    ModelRenderer Core;
    ModelRenderer Top;
    ModelRenderer Bottom;
    
    public ModelCentrifuge() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        (this.Crossbar = new ModelRenderer(this, 16, 0)).addBox(-4.0f, -1.0f, -1.0f, 8, 2, 2);
        this.Crossbar.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Crossbar.setTextureSize(64, 32);
        this.Crossbar.mirror = true;
        this.setRotation(this.Crossbar, 0.0f, 0.0f, 0.0f);
        (this.Dingus1 = new ModelRenderer(this, 0, 16)).addBox(4.0f, -3.0f, -2.0f, 4, 6, 4);
        this.Dingus1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Dingus1.setTextureSize(64, 32);
        this.Dingus1.mirror = true;
        this.setRotation(this.Dingus1, 0.0f, 0.0f, 0.0f);
        (this.Dingus2 = new ModelRenderer(this, 0, 16)).addBox(-8.0f, -3.0f, -2.0f, 4, 6, 4);
        this.Dingus2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Dingus2.setTextureSize(64, 32);
        this.Dingus2.mirror = true;
        this.setRotation(this.Dingus2, 0.0f, 0.0f, 0.0f);
        (this.Core = new ModelRenderer(this, 0, 0)).addBox(-1.5f, -4.0f, -1.5f, 3, 8, 3);
        this.Core.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Core.setTextureSize(64, 32);
        this.Core.mirror = true;
        this.setRotation(this.Core, 0.0f, 0.0f, 0.0f);
        (this.Top = new ModelRenderer(this, 20, 16)).addBox(-4.0f, -8.0f, -4.0f, 8, 4, 8);
        this.Top.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Top.setTextureSize(64, 32);
        this.Top.mirror = true;
        this.setRotation(this.Top, 0.0f, 0.0f, 0.0f);
        (this.Bottom = new ModelRenderer(this, 20, 16)).addBox(-4.0f, 4.0f, -4.0f, 8, 4, 8);
        this.Bottom.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Bottom.setTextureSize(64, 32);
        this.Bottom.mirror = true;
        this.setRotation(this.Bottom, 0.0f, 0.0f, 0.0f);
    }
    
    public void renderBoxes() {
        this.Top.render(0.0625f);
        this.Bottom.render(0.0625f);
    }
    
    public void renderSpinnyBit() {
        this.Crossbar.render(0.0625f);
        this.Dingus1.render(0.0625f);
        this.Dingus2.render(0.0625f);
        this.Core.render(0.0625f);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
