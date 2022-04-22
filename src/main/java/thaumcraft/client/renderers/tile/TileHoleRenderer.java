// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.tile;

import net.minecraft.block.state.IBlockState;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.util.EnumFacing;
import thaumcraft.client.lib.ender.ShaderHelper;
import org.lwjgl.opengl.GL11;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.ARBShaderObjects;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.lib.ender.ShaderCallback;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class TileHoleRenderer extends TileEntitySpecialRenderer
{
    private final ShaderCallback shaderCallback;
    private static final ResourceLocation starsTexture;
    
    public TileHoleRenderer() {
        shaderCallback = new ShaderCallback() {
            @Override
            public void call(final int shader) {
                final Minecraft mc = Minecraft.getMinecraft();
                final int x = ARBShaderObjects.glGetUniformLocationARB(shader, "yaw");
                ARBShaderObjects.glUniform1fARB(x, (float)(mc.player.rotationYaw * 2.0f * 3.141592653589793 / 360.0));
                final int z = ARBShaderObjects.glGetUniformLocationARB(shader, "pitch");
                ARBShaderObjects.glUniform1fARB(z, -(float)(mc.player.rotationPitch * 2.0f * 3.141592653589793 / 360.0));
            }
        };
    }
    
    public void render(final TileEntity te, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        GL11.glPushMatrix();
        bindTexture(TileHoleRenderer.starsTexture);
        ShaderHelper.useShader(ShaderHelper.endShader, shaderCallback);
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        for (final EnumFacing face : EnumFacing.values()) {
            final IBlockState bs = te.getWorld().getBlockState(te.getPos().offset(face));
            if (bs.isOpaqueCube() && bs.getBlock() != BlocksTC.hole) {
                GL11.glPushMatrix();
                GL11.glRotatef(90.0f, (float)(-face.getFrontOffsetY()), (float)face.getFrontOffsetX(), (float)(-face.getFrontOffsetZ()));
                if (face.getFrontOffsetZ() < 0) {
                    GL11.glTranslated(0.0, 0.0, -0.49900001287460327);
                    GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                }
                else {
                    GL11.glTranslated(0.0, 0.0, 0.49900001287460327);
                }
                GL11.glRotatef(90.0f, 0.0f, 0.0f, -1.0f);
                UtilsFX.renderQuadCentered();
                GL11.glPopMatrix();
            }
        }
        ShaderHelper.releaseShader();
        GL11.glPopMatrix();
    }
    
    static {
        starsTexture = new ResourceLocation("textures/entity/end_portal.png");
    }
}
