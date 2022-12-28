package thaumcraft.client.renderers.models.entity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.entities.monster.EntityFireBat;


@SideOnly(Side.CLIENT)
public class ModelFireBat extends ModelBase
{
    private ModelRenderer batHead;
    private ModelRenderer batBody;
    private ModelRenderer batRightWing;
    private ModelRenderer batLeftWing;
    private ModelRenderer batOuterRightWing;
    private ModelRenderer batOuterLeftWing;
    
    public ModelFireBat() {
        textureWidth = 64;
        textureHeight = 64;
        (batHead = new ModelRenderer(this, 0, 0)).addBox(-3.0f, -3.0f, -3.0f, 6, 6, 6);
        ModelRenderer var1 = new ModelRenderer(this, 24, 0);
        var1.addBox(-4.0f, -6.0f, -2.0f, 3, 4, 1);
        batHead.addChild(var1);
        ModelRenderer var2 = new ModelRenderer(this, 24, 0);
        var2.mirror = true;
        var2.addBox(1.0f, -6.0f, -2.0f, 3, 4, 1);
        batHead.addChild(var2);
        (batBody = new ModelRenderer(this, 0, 16)).addBox(-3.0f, 4.0f, -3.0f, 6, 12, 6);
        batBody.setTextureOffset(0, 34).addBox(-5.0f, 16.0f, 0.0f, 10, 6, 1);
        (batRightWing = new ModelRenderer(this, 42, 0)).addBox(-12.0f, 1.0f, 1.5f, 10, 16, 1);
        (batOuterRightWing = new ModelRenderer(this, 24, 16)).setRotationPoint(-12.0f, 1.0f, 1.5f);
        batOuterRightWing.addBox(-8.0f, 1.0f, 0.0f, 8, 12, 1);
        batLeftWing = new ModelRenderer(this, 42, 0);
        batLeftWing.mirror = true;
        batLeftWing.addBox(2.0f, 1.0f, 1.5f, 10, 16, 1);
        batOuterLeftWing = new ModelRenderer(this, 24, 16);
        batOuterLeftWing.mirror = true;
        batOuterLeftWing.setRotationPoint(12.0f, 1.0f, 1.5f);
        batOuterLeftWing.addBox(0.0f, 1.0f, 0.0f, 8, 12, 1);
        batBody.addChild(batRightWing);
        batBody.addChild(batLeftWing);
        batRightWing.addChild(batOuterRightWing);
        batLeftWing.addChild(batOuterLeftWing);
    }
    
    public int getBatSize() {
        return 36;
    }
    
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
        if (par1Entity instanceof EntityFireBat && ((EntityFireBat)par1Entity).getIsBatHanging()) {
            batHead.rotateAngleX = par6 / 57.295776f;
            batHead.rotateAngleY = 3.1415927f - par5 / 57.295776f;
            batHead.rotateAngleZ = 3.1415927f;
            batHead.setRotationPoint(0.0f, -2.0f, 0.0f);
            batRightWing.setRotationPoint(-3.0f, 0.0f, 3.0f);
            batLeftWing.setRotationPoint(3.0f, 0.0f, 3.0f);
            batBody.rotateAngleX = 3.1415927f;
            batRightWing.rotateAngleX = -0.15707964f;
            batRightWing.rotateAngleY = -1.2566371f;
            batOuterRightWing.rotateAngleY = -1.7278761f;
            batLeftWing.rotateAngleX = batRightWing.rotateAngleX;
            batLeftWing.rotateAngleY = -batRightWing.rotateAngleY;
            batOuterLeftWing.rotateAngleY = -batOuterRightWing.rotateAngleY;
        }
        else {
            batHead.rotateAngleX = par6 / 57.295776f;
            batHead.rotateAngleY = par5 / 57.295776f;
            batHead.rotateAngleZ = 0.0f;
            batHead.setRotationPoint(0.0f, 0.0f, 0.0f);
            batRightWing.setRotationPoint(0.0f, 0.0f, 0.0f);
            batLeftWing.setRotationPoint(0.0f, 0.0f, 0.0f);
            batBody.rotateAngleX = 0.7853982f + MathHelper.cos(par4 * 0.1f) * 0.15f;
            batBody.rotateAngleY = 0.0f;
            batRightWing.rotateAngleY = MathHelper.cos(par4 * 1.3f) * 3.1415927f * 0.25f;
            batLeftWing.rotateAngleY = -batRightWing.rotateAngleY;
            batOuterRightWing.rotateAngleY = batRightWing.rotateAngleY * 0.5f;
            batOuterLeftWing.rotateAngleY = -batRightWing.rotateAngleY * 0.5f;
        }
        batHead.render(par7);
        batBody.render(par7);
    }
}
