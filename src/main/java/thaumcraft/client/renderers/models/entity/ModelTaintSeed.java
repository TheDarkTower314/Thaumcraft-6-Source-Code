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
        tentacle = new ModelRendererTaintSeed(this);
        orb = new ModelRendererTaintSeed(this);
        length = 8;
        textureHeight = 64;
        textureWidth = 64;
        (tentacle = new ModelRendererTaintSeed(this, 0, 0)).addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
        tentacle.rotationPointX = 0.0f;
        tentacle.rotationPointZ = 0.0f;
        tentacle.rotationPointY = 12.0f;
        tents = new ModelRendererTaintSeed[length];
        for (int k = 0; k < length - 1; ++k) {
            tents[k] = new ModelRendererTaintSeed(this, 0, (k < length - 4) ? 16 : ((k == length - 4) ? 48 : 56));
            if (k < length - 4) {
                tents[k].addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
                tents[k].rotationPointY = -8.0f;
            }
            else {
                tents[k].addBox(-2.0f, -2.0f, -2.0f, 4, 4, 4);
                tents[k].rotationPointY = ((k == length - 4) ? -8.0f : -4.0f);
            }
            if (k == 0) {
                tentacle.addChild(tents[k]);
            }
            else {
                tents[k - 1].addChild(tents[k]);
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
        tentacle.rotateAngleX = 0.0f;
        for (int k = 0; k < length - 1; ++k) {
            tents[k].rotateAngleX = 0.1f / fi * MathHelper.sin(par3 * 0.06f - k / 2.0f) / 5.0f + ht + seed.attackAnim;
            tents[k].rotateAngleZ = 0.1f / fi * MathHelper.sin(par3 * 0.05f - k / 2.0f) / 5.0f;
        }
    }
    
    public void render(final Entity par1Entity, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7) {
        setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
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
        ((ModelRendererTaintSeed) tentacle).render(par7, par1Entity.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks(), 1.6f);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
}
