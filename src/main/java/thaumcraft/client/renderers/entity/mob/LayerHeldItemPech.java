package thaumcraft.client.renderers.entity.mob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class LayerHeldItemPech extends LayerHeldItem
{
    public LayerHeldItemPech(RenderLivingBase<?> livingEntityRendererIn) {
        super(livingEntityRendererIn);
    }
    
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        boolean flag = entitylivingbaseIn.getPrimaryHand() == EnumHandSide.RIGHT;
        ItemStack itemstack = flag ? entitylivingbaseIn.getHeldItemOffhand() : entitylivingbaseIn.getHeldItemMainhand();
        ItemStack itemstack2 = flag ? entitylivingbaseIn.getHeldItemMainhand() : entitylivingbaseIn.getHeldItemOffhand();
        if ((itemstack != null && !itemstack.isEmpty()) || (itemstack2 != null && !itemstack2.isEmpty())) {
            GlStateManager.pushMatrix();
            if (livingEntityRenderer.getMainModel().isChild) {
                float f = 0.5f;
                GlStateManager.translate(0.0f, 0.625f, 0.0f);
                GlStateManager.rotate(-20.0f, -1.0f, 0.0f, 0.0f);
                GlStateManager.scale(f, f, f);
            }
            renderHeldItem(entitylivingbaseIn, itemstack2, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
            renderHeldItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
            GlStateManager.popMatrix();
        }
    }
    
    private void renderHeldItem(EntityLivingBase entity, ItemStack stack, ItemCameraTransforms.TransformType p_188358_3_, EnumHandSide p_188358_4_) {
        if (stack != null && !stack.isEmpty()) {
            GlStateManager.pushMatrix();
            ((ModelBiped) livingEntityRenderer.getMainModel()).postRenderArm(0.0625f, p_188358_4_);
            GlStateManager.translate(0.0f, -0.1f, 0.0625f);
            if (stack.getItem() instanceof ItemBow) {
                GlStateManager.translate(-0.07500000298023224, -0.1, 0.0);
            }
            if (entity.isSneaking()) {
                GlStateManager.translate(0.0f, 0.2f, 0.0f);
            }
            GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
            boolean flag = p_188358_4_ == EnumHandSide.LEFT;
            GlStateManager.translate(flag ? -0.0625f : 0.0625f, 0.125f, -0.625f);
            Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, stack, p_188358_3_, flag);
            GlStateManager.popMatrix();
        }
    }
    
    public boolean shouldCombineTextures() {
        return false;
    }
}
