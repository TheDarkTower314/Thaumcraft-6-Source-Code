package thaumcraft.client.renderers.entity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.entities.EntityFallingTaint;


@SideOnly(Side.CLIENT)
public class RenderFallingTaint extends Render
{
    public RenderFallingTaint(RenderManager p_i46177_1_) {
        super(p_i46177_1_);
        shadowSize = 0.5f;
    }
    
    public void doRender(EntityFallingTaint p_180557_1_, double p_180557_2_, double p_180557_4_, double p_180557_6_, float p_180557_8_, float p_180557_9_) {
        if (p_180557_1_.getBlock() != null) {
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            IBlockState iblockstate = p_180557_1_.getBlock();
            Block block = iblockstate.getBlock();
            BlockPos blockpos = new BlockPos(p_180557_1_);
            World world = p_180557_1_.getWorld();
            if (iblockstate != world.getBlockState(blockpos) && block.getRenderType(iblockstate) != EnumBlockRenderType.INVISIBLE && block.getRenderType(iblockstate) == EnumBlockRenderType.MODEL) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)p_180557_2_, (float)p_180557_4_, (float)p_180557_6_);
                GlStateManager.disableLighting();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder BufferBuilder = tessellator.getBuffer();
                BufferBuilder.begin(7, DefaultVertexFormats.BLOCK);
                int i = blockpos.getX();
                int j = blockpos.getY();
                int k = blockpos.getZ();
                BufferBuilder.setTranslation(-i - 0.5f, -j, -k - 0.5f);
                BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
                IBakedModel ibakedmodel = blockrendererdispatcher.getModelForState(iblockstate);
                blockrendererdispatcher.getBlockModelRenderer().renderModel(world, ibakedmodel, iblockstate, blockpos, BufferBuilder, false);
                BufferBuilder.setTranslation(0.0, 0.0, 0.0);
                tessellator.draw();
                GlStateManager.enableLighting();
                GlStateManager.popMatrix();
                super.doRender(p_180557_1_, p_180557_2_, p_180557_4_, p_180557_6_, p_180557_8_, p_180557_9_);
            }
        }
    }
    
    protected ResourceLocation getEntityTexture(EntityFallingBlock entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
    
    protected ResourceLocation getEntityTexture(Entity entity) {
        return getEntityTexture((EntityFallingBlock)entity);
    }
    
    public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float partialTicks) {
        doRender((EntityFallingTaint)entity, x, y, z, p_76986_8_, partialTicks);
    }
}
