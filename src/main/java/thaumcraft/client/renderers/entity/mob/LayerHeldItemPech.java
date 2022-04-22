// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.mob;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBow;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHandSide;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;

@SideOnly(Side.CLIENT)
public class LayerHeldItemPech extends LayerHeldItem
{
    public LayerHeldItemPech(final RenderLivingBase<?> livingEntityRendererIn) {
        super(livingEntityRendererIn);
    }
    
    public void doRenderLayer(final EntityLivingBase entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
        final boolean flag = entitylivingbaseIn.getPrimaryHand() == EnumHandSide.RIGHT;
        final ItemStack itemstack = flag ? entitylivingbaseIn.getHeldItemOffhand() : entitylivingbaseIn.getHeldItemMainhand();
        final ItemStack itemstack2 = flag ? entitylivingbaseIn.getHeldItemMainhand() : entitylivingbaseIn.getHeldItemOffhand();
        if ((itemstack != null && !itemstack.isEmpty()) || (itemstack2 != null && !itemstack2.isEmpty())) {
            GlStateManager.pushMatrix();
            if (livingEntityRenderer.getMainModel().isChild) {
                final float f = 0.5f;
                GlStateManager.translate(0.0f, 0.625f, 0.0f);
                GlStateManager.rotate(-20.0f, -1.0f, 0.0f, 0.0f);
                GlStateManager.scale(f, f, f);
            }
            renderHeldItem(entitylivingbaseIn, itemstack2, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
            renderHeldItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
            GlStateManager.popMatrix();
        }
    }
    
    private void renderHeldItem(final EntityLivingBase entity, final ItemStack stack, final ItemCameraTransforms.TransformType p_188358_3_, final EnumHandSide p_188358_4_) {
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
            final boolean flag = p_188358_4_ == EnumHandSide.LEFT;
            GlStateManager.translate(flag ? -0.0625f : 0.0625f, 0.125f, -0.625f);
            Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, stack, p_188358_3_, flag);
            GlStateManager.popMatrix();
        }
    }
    
    public boolean shouldCombineTextures() {
        return false;
    }
}
