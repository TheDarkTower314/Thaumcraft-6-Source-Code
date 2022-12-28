package thaumcraft.client.renderers.models.entity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;


public class ModelRendererTaintacle extends ModelRenderer
{
    private int textureOffsetX;
    private int textureOffsetY;
    private boolean compiled;
    private int displayList;
    private ModelBase baseModel;
    
    public ModelRendererTaintacle(ModelBase par1ModelBase) {
        super(par1ModelBase);
    }
    
    public ModelRendererTaintacle(ModelBase par1ModelBase, int par2, int par3) {
        this(par1ModelBase);
        setTextureOffset(par2, par3);
    }
    
    @SideOnly(Side.CLIENT)
    public void render(float par1, float scale) {
        if (!isHidden && showModel) {
            if (!compiled) {
                compileDisplayList(par1);
            }
            GL11.glTranslatef(offsetX, offsetY, offsetZ);
            if (rotateAngleX == 0.0f && rotateAngleY == 0.0f && rotateAngleZ == 0.0f) {
                if (rotationPointX == 0.0f && rotationPointY == 0.0f && rotationPointZ == 0.0f) {
                    GL11.glCallList(displayList);
                    if (childModels != null) {
                        for (int i = 0; i < childModels.size(); ++i) {
                            GL11.glPushMatrix();
                            GL11.glScalef(scale, scale, scale);
                            ((ModelRendererTaintacle)childModels.get(i)).render(par1, scale);
                            GL11.glPopMatrix();
                        }
                    }
                }
                else {
                    GL11.glTranslatef(rotationPointX * par1, rotationPointY * par1, rotationPointZ * par1);
                    GL11.glCallList(displayList);
                    if (childModels != null) {
                        for (int i = 0; i < childModels.size(); ++i) {
                            GL11.glPushMatrix();
                            GL11.glScalef(scale, scale, scale);
                            ((ModelRendererTaintacle)childModels.get(i)).render(par1, scale);
                            GL11.glPopMatrix();
                        }
                    }
                    GL11.glTranslatef(-rotationPointX * par1, -rotationPointY * par1, -rotationPointZ * par1);
                }
            }
            else {
                GL11.glPushMatrix();
                GL11.glTranslatef(rotationPointX * par1, rotationPointY * par1, rotationPointZ * par1);
                if (rotateAngleZ != 0.0f) {
                    GL11.glRotatef(rotateAngleZ * 57.295776f, 0.0f, 0.0f, 1.0f);
                }
                if (rotateAngleY != 0.0f) {
                    GL11.glRotatef(rotateAngleY * 57.295776f, 0.0f, 1.0f, 0.0f);
                }
                if (rotateAngleX != 0.0f) {
                    GL11.glRotatef(rotateAngleX * 57.295776f, 1.0f, 0.0f, 0.0f);
                }
                GL11.glCallList(displayList);
                if (childModels != null) {
                    for (int i = 0; i < childModels.size(); ++i) {
                        GL11.glPushMatrix();
                        GL11.glScalef(scale, scale, scale);
                        ((ModelRendererTaintacle)childModels.get(i)).render(par1, scale);
                        GL11.glPopMatrix();
                    }
                }
                GL11.glPopMatrix();
            }
            GL11.glTranslatef(-offsetX, -offsetY, -offsetZ);
        }
    }
    
    @SideOnly(Side.CLIENT)
    private void compileDisplayList(float par1) {
        GL11.glNewList(displayList = GLAllocation.generateDisplayLists(1), 4864);
        Tessellator tessellator = Tessellator.getInstance();
        for (int i = 0; i < cubeList.size(); ++i) {
            cubeList.get(i).render(tessellator.getBuffer(), par1);
        }
        GL11.glEndList();
        compiled = true;
    }
}
