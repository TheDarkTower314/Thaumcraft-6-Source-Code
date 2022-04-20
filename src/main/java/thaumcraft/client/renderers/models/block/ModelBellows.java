// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.block;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;

public class ModelBellows extends ModelBase
{
    public ModelRenderer BottomPlank;
    public ModelRenderer MiddlePlank;
    public ModelRenderer TopPlank;
    public ModelRenderer Bag;
    public ModelRenderer Nozzle;
    
    public ModelBellows() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        (this.BottomPlank = new ModelRenderer(this, 0, 0)).addBox(-6.0f, 0.0f, -6.0f, 12, 2, 12);
        this.BottomPlank.setRotationPoint(0.0f, 22.0f, 0.0f);
        this.BottomPlank.setTextureSize(128, 128);
        this.BottomPlank.mirror = true;
        this.setRotation(this.BottomPlank, 0.0f, 0.0f, 0.0f);
        (this.MiddlePlank = new ModelRenderer(this, 0, 0)).addBox(-6.0f, -1.0f, -6.0f, 12, 2, 12);
        this.MiddlePlank.setRotationPoint(0.0f, 16.0f, 0.0f);
        this.MiddlePlank.setTextureSize(128, 128);
        this.MiddlePlank.mirror = true;
        this.setRotation(this.MiddlePlank, 0.0f, 0.0f, 0.0f);
        (this.TopPlank = new ModelRenderer(this, 0, 0)).addBox(-6.0f, 0.0f, -6.0f, 12, 2, 12);
        this.TopPlank.setRotationPoint(0.0f, 8.0f, 0.0f);
        this.TopPlank.setTextureSize(128, 128);
        this.TopPlank.mirror = true;
        this.setRotation(this.TopPlank, 0.0f, 0.0f, 0.0f);
        (this.Bag = new ModelRenderer(this, 48, 0)).addBox(-10.0f, -12.03333f, -10.0f, 20, 24, 20);
        this.Bag.setRotationPoint(0.0f, 16.0f, 0.0f);
        this.Bag.setTextureSize(64, 32);
        this.Bag.mirror = true;
        this.setRotation(this.Bag, 0.0f, 0.0f, 0.0f);
        (this.Nozzle = new ModelRenderer(this, 0, 36)).addBox(-2.0f, -2.0f, 0.0f, 4, 4, 2);
        this.Nozzle.setRotationPoint(0.0f, 16.0f, 6.0f);
        this.Nozzle.setTextureSize(128, 128);
        this.Nozzle.mirror = true;
        this.setRotation(this.Nozzle, 0.0f, 0.0f, 0.0f);
    }
    
    public void render() {
        this.MiddlePlank.render(0.0625f);
        this.Nozzle.render(0.0625f);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
