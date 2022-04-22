// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity;

import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.IBlockAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.renderer.texture.TextureMap;
import thaumcraft.common.entities.EntityFallingTaint;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;

@SideOnly(Side.CLIENT)
public class RenderFallingTaint extends Render
{
    public RenderFallingTaint(final RenderManager p_i46177_1_) {
        super(p_i46177_1_);
        shadowSize = 0.5f;
    }
    
    public void doRender(final EntityFallingTaint p_180557_1_, final double p_180557_2_, final double p_180557_4_, final double p_180557_6_, final float p_180557_8_, final float p_180557_9_) {
        if (p_180557_1_.getBlock() != null) {
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            final IBlockState iblockstate = p_180557_1_.getBlock();
            final Block block = iblockstate.getBlock();
            final BlockPos blockpos = new BlockPos(p_180557_1_);
            final World world = p_180557_1_.getWorld();
            if (iblockstate != world.getBlockState(blockpos) && block.getRenderType(iblockstate) != EnumBlockRenderType.INVISIBLE && block.getRenderType(iblockstate) == EnumBlockRenderType.MODEL) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)p_180557_2_, (float)p_180557_4_, (float)p_180557_6_);
                GlStateManager.disableLighting();
                final Tessellator tessellator = Tessellator.getInstance();
                final BufferBuilder BufferBuilder = tessellator.getBuffer();
                BufferBuilder.begin(7, DefaultVertexFormats.BLOCK);
                final int i = blockpos.getX();
                final int j = blockpos.getY();
                final int k = blockpos.getZ();
                BufferBuilder.setTranslation(-i - 0.5f, -j, -k - 0.5f);
                final BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
                final IBakedModel ibakedmodel = blockrendererdispatcher.getModelForState(iblockstate);
                blockrendererdispatcher.getBlockModelRenderer().renderModel(world, ibakedmodel, iblockstate, blockpos, BufferBuilder, false);
                BufferBuilder.setTranslation(0.0, 0.0, 0.0);
                tessellator.draw();
                GlStateManager.enableLighting();
                GlStateManager.popMatrix();
                super.doRender(p_180557_1_, p_180557_2_, p_180557_4_, p_180557_6_, p_180557_8_, p_180557_9_);
            }
        }
    }
    
    protected ResourceLocation getEntityTexture(final EntityFallingBlock entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return getEntityTexture((EntityFallingBlock)entity);
    }
    
    public void doRender(final Entity entity, final double x, final double y, final double z, final float p_76986_8_, final float partialTicks) {
        doRender((EntityFallingTaint)entity, x, y, z, p_76986_8_, partialTicks);
    }
}
