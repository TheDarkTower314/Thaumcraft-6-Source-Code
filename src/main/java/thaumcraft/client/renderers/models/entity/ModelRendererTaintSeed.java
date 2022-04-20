// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.entity;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelRendererTaintSeed extends ModelRenderer
{
    private int textureOffsetX;
    private int textureOffsetY;
    private boolean compiled;
    private int displayList;
    private ModelBase baseModel;
    static int q;
    
    public ModelRendererTaintSeed(final ModelBase par1ModelBase) {
        super(par1ModelBase);
    }
    
    public ModelRendererTaintSeed(final ModelBase par1ModelBase, final int par2, final int par3) {
        this(par1ModelBase);
        this.setTextureOffset(par2, par3);
    }
    
    @SideOnly(Side.CLIENT)
    public void render(final float par1, final float tt, final float height) {
        final float qq = (float)(3.141592653589793 * (ModelRendererTaintSeed.q / 7.0f));
        final float scale = height - (float)Math.sin(qq);
        float pulse = (float)Math.sin(tt / 12.0f - qq) * 0.33f;
        pulse *= pulse;
        ++ModelRendererTaintSeed.q;
        if (ModelRendererTaintSeed.q > 7) {
            ModelRendererTaintSeed.q = 0;
        }
        if (!this.isHidden && this.showModel) {
            if (!this.compiled) {
                this.compileDisplayList(par1);
            }
            GL11.glTranslatef(this.offsetX, this.offsetY, this.offsetZ);
            if (this.rotateAngleX == 0.0f && this.rotateAngleY == 0.0f && this.rotateAngleZ == 0.0f) {
                if (this.rotationPointX == 0.0f && this.rotationPointY == 0.0f && this.rotationPointZ == 0.0f) {
                    GL11.glCallList(this.displayList);
                    if (this.childModels != null) {
                        for (int i = 0; i < this.childModels.size(); ++i) {
                            GL11.glPushMatrix();
                            GL11.glScalef(scale + pulse, scale * 0.9f, scale + pulse);
                            ((ModelRendererTaintSeed)childModels.get(i)).render(par1, tt, height);
                            GL11.glPopMatrix();
                        }
                    }
                }
                else {
                    GL11.glTranslatef(this.rotationPointX * par1, this.rotationPointY * par1, this.rotationPointZ * par1);
                    GL11.glCallList(this.displayList);
                    if (this.childModels != null) {
                        for (int i = 0; i < this.childModels.size(); ++i) {
                            GL11.glPushMatrix();
                            GL11.glScalef(scale + pulse, scale * 0.9f, scale + pulse);
                            ((ModelRendererTaintSeed)childModels.get(i)).render(par1, tt, height);
                            GL11.glPopMatrix();
                        }
                    }
                    GL11.glTranslatef(-this.rotationPointX * par1, -this.rotationPointY * par1, -this.rotationPointZ * par1);
                }
            }
            else {
                GL11.glPushMatrix();
                GL11.glTranslatef(this.rotationPointX * par1, this.rotationPointY * par1, this.rotationPointZ * par1);
                if (this.rotateAngleZ != 0.0f) {
                    GL11.glRotatef(this.rotateAngleZ * 57.295776f, 0.0f, 0.0f, 1.0f);
                }
                if (this.rotateAngleY != 0.0f) {
                    GL11.glRotatef(this.rotateAngleY * 57.295776f, 0.0f, 1.0f, 0.0f);
                }
                if (this.rotateAngleX != 0.0f) {
                    GL11.glRotatef(this.rotateAngleX * 57.295776f, 1.0f, 0.0f, 0.0f);
                }
                GL11.glCallList(this.displayList);
                if (this.childModels != null) {
                    for (int i = 0; i < this.childModels.size(); ++i) {
                        GL11.glPushMatrix();
                        GL11.glScalef(scale + pulse, scale * 0.9f, scale + pulse);
                        ((ModelRendererTaintSeed)childModels.get(i)).render(par1, tt, height);
                        GL11.glPopMatrix();
                    }
                }
                GL11.glPopMatrix();
            }
            GL11.glTranslatef(-this.offsetX, -this.offsetY, -this.offsetZ);
        }
    }
    
    @SideOnly(Side.CLIENT)
    private void compileDisplayList(final float par1) {
        GL11.glNewList(this.displayList = GLAllocation.generateDisplayLists(1), 4864);
        final Tessellator tessellator = Tessellator.getInstance();
        for (int i = 0; i < this.cubeList.size(); ++i) {
            this.cubeList.get(i).render(tessellator.getBuffer(), par1);
        }
        GL11.glEndList();
        this.compiled = true;
    }
    
    static {
        ModelRendererTaintSeed.q = 0;
    }
}
