// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.block;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;

public class ModelJar extends ModelBase
{
    public ModelRenderer Core;
    public ModelRenderer Brine;
    public ModelRenderer Lid;
    public ModelRenderer LidExtension;
    
    public ModelJar() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        (this.Core = new ModelRenderer(this, 0, 0)).addBox(-5.0f, -12.0f, -5.0f, 10, 12, 10);
        this.Core.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Core.setTextureSize(64, 32);
        this.Core.mirror = true;
        this.setRotation(this.Core, 0.0f, 0.0f, 0.0f);
        (this.Brine = new ModelRenderer(this, 0, 0)).addBox(-4.0f, -11.0f, -4.0f, 8, 10, 8);
        this.Brine.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Brine.setTextureSize(64, 32);
        this.Brine.mirror = true;
        this.setRotation(this.Brine, 0.0f, 0.0f, 0.0f);
        (this.Lid = new ModelRenderer(this, 32, 24)).addBox(-3.0f, 0.0f, -3.0f, 6, 2, 6);
        this.Lid.setRotationPoint(0.0f, -14.0f, 0.0f);
        this.Lid.setTextureSize(64, 32);
        this.Lid.mirror = true;
        (this.LidExtension = new ModelRenderer(this, 0, 23)).addBox(-2.0f, -16.0f, -2.0f, 4, 2, 4);
        this.LidExtension.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.LidExtension.setTextureSize(64, 32);
        this.LidExtension.mirror = true;
    }
    
    public void renderBrine() {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        this.Brine.render(0.0625f);
        GL11.glDisable(3042);
    }
    
    public void renderLidExtension() {
        this.LidExtension.render(0.0625f);
    }
    
    public void renderLidBrace() {
        this.Lid.render(0.0625f);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
