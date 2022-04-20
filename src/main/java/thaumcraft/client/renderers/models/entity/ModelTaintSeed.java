// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.entity;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;

public class ModelTaintSeed extends ModelBase
{
    public ModelRenderer tentacle;
    public ModelRenderer[] tents;
    public ModelRenderer orb;
    private int length;
    
    public ModelTaintSeed() {
        this.tentacle = new ModelRendererTaintSeed(this);
        this.orb = new ModelRendererTaintSeed(this);
        this.length = 8;
        this.textureHeight = 64;
        this.textureWidth = 64;
        (this.tentacle = new ModelRendererTaintSeed(this, 0, 0)).addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
        this.tentacle.rotationPointX = 0.0f;
        this.tentacle.rotationPointZ = 0.0f;
        this.tentacle.rotationPointY = 12.0f;
        this.tents = new ModelRendererTaintSeed[this.length];
        for (int k = 0; k < this.length - 1; ++k) {
            this.tents[k] = new ModelRendererTaintSeed(this, 0, (k < this.length - 4) ? 16 : ((k == this.length - 4) ? 48 : 56));
            if (k < this.length - 4) {
                this.tents[k].addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
                this.tents[k].rotationPointY = -8.0f;
            }
            else {
                this.tents[k].addBox(-2.0f, -2.0f, -2.0f, 4, 4, 4);
                this.tents[k].rotationPointY = ((k == this.length - 4) ? -8.0f : -4.0f);
            }
            if (k == 0) {
                this.tentacle.addChild(this.tents[k]);
            }
            else {
                this.tents[k - 1].addChild(this.tents[k]);
            }
        }
    }
    
    public void setRotationAngles(final float par1, final float par2, final float par3, final float par4, final float par5, final float par6, final Entity entity) {
        float flail = 0.0f;
        float ht = 0.0f;
        final int at = 0;
        final EntityTaintSeed seed = (EntityTaintSeed)entity;
        ht = seed.hurtTime / 200.0f;
        flail = 0.1f;
        final float mod = par6 * 0.2f;
        final float fs = (flail > 1.0f) ? 3.0f : (1.0f + ((flail > 1.0f) ? mod : (-mod)));
        float fi = flail + ((ht > 0.0f || at > 0) ? mod : (-mod));
        fi *= 3.0f;
        this.tentacle.rotateAngleX = 0.0f;
        for (int k = 0; k < this.length - 1; ++k) {
            this.tents[k].rotateAngleX = 0.1f / fi * MathHelper.sin(par3 * 0.06f - k / 2.0f) / 5.0f + ht + seed.attackAnim;
            this.tents[k].rotateAngleZ = 0.1f / fi * MathHelper.sin(par3 * 0.05f - k / 2.0f) / 5.0f;
        }
    }
    
    public void render(final Entity par1Entity, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7) {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        float height = 0.0f;
        final float hc = par1Entity.height * 10.0f;
        if (par1Entity.ticksExisted < hc) {
            height = (hc - par1Entity.ticksExisted) / hc * par1Entity.height;
        }
        GL11.glTranslatef(0.0f, ((par1Entity.height == 3.0f) ? 0.6f : 1.2f) + height, 0.0f);
        GL11.glScalef(par1Entity.height / 2.0f, par1Entity.height / 2.0f, par1Entity.height / 2.0f);
        ((ModelRendererTaintSeed)this.tentacle).render(par7, par1Entity.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks(), 1.6f);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
}
