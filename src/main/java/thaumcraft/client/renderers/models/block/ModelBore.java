// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.block;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;

public class ModelBore extends ModelBase
{
    ModelRenderer Base;
    ModelRenderer Side1;
    ModelRenderer Side2;
    ModelRenderer NozCrossbar;
    ModelRenderer NozFront;
    ModelRenderer NozMid;
    
    public ModelBore() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        (this.Base = new ModelRenderer(this, 0, 32)).addBox(-6.0f, 0.0f, -6.0f, 12, 2, 12);
        this.Base.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Base.setTextureSize(64, 32);
        this.Base.mirror = true;
        this.setRotation(this.Base, 0.0f, 0.0f, 0.0f);
        (this.Side1 = new ModelRenderer(this, 0, 0)).addBox(-2.0f, 2.0f, -5.5f, 4, 8, 1);
        this.Side1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Side1.setTextureSize(64, 32);
        this.Side1.mirror = true;
        this.setRotation(this.Side1, 0.0f, 0.0f, 0.0f);
        (this.Side2 = new ModelRenderer(this, 0, 0)).addBox(-2.0f, 2.0f, 4.5f, 4, 8, 1);
        this.Side2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Side2.setTextureSize(64, 32);
        this.Side2.mirror = true;
        this.setRotation(this.Side2, 0.0f, 0.0f, 0.0f);
        (this.NozCrossbar = new ModelRenderer(this, 0, 48)).addBox(-1.0f, -1.0f, -6.0f, 2, 2, 12);
        this.NozCrossbar.setRotationPoint(0.0f, 8.0f, 0.0f);
        this.NozCrossbar.setTextureSize(64, 32);
        this.NozCrossbar.mirror = true;
        this.setRotation(this.NozCrossbar, 0.0f, 0.0f, 0.0f);
        (this.NozFront = new ModelRenderer(this, 30, 14)).addBox(4.0f, -2.5f, -2.5f, 4, 5, 5);
        this.NozFront.setRotationPoint(0.0f, 8.0f, 0.0f);
        this.NozFront.setTextureSize(64, 32);
        this.NozFront.mirror = true;
        this.setRotation(this.NozFront, 0.0f, 0.0f, 0.0f);
        (this.NozMid = new ModelRenderer(this, 0, 14)).addBox(-2.0f, -4.0f, -4.0f, 6, 8, 8);
        this.NozMid.setRotationPoint(0.0f, 8.0f, 0.0f);
        this.NozMid.setTextureSize(64, 32);
        this.NozMid.mirror = true;
        this.setRotation(this.NozMid, 0.0f, 0.0f, 0.0f);
    }
    
    public void renderBase() {
        final float f5 = 0.0625f;
        this.Base.render(f5);
        this.Side1.render(f5);
        this.Side2.render(f5);
        this.NozCrossbar.render(f5);
    }
    
    public void renderNozzle() {
        final float f5 = 0.0625f;
        this.NozFront.render(f5);
        this.NozMid.render(f5);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
