// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.tile;

import thaumcraft.client.lib.UtilsFX;
import net.minecraft.util.EnumFacing;
import net.minecraft.client.renderer.GlStateManager;
import thaumcraft.client.lib.ender.ShaderHelper;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.ARBShaderObjects;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.lib.ender.ShaderCallback;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class TileVoidSiphonRenderer extends TileEntitySpecialRenderer
{
    private final ShaderCallback shaderCallback;
    private static final ResourceLocation starsTexture;
    
    public TileVoidSiphonRenderer() {
        this.shaderCallback = new ShaderCallback() {
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
        if (BlockStateUtils.isEnabled(te.getBlockMetadata())) {
            GL11.glPushMatrix();
            this.bindTexture(TileVoidSiphonRenderer.starsTexture);
            ShaderHelper.useShader(ShaderHelper.endShader, this.shaderCallback);
            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5, y + 0.875, z + 0.5);
            GlStateManager.depthMask(false);
            for (final EnumFacing face : EnumFacing.values()) {
                GL11.glPushMatrix();
                GL11.glRotatef(90.0f, (float)(-face.getFrontOffsetY()), (float)face.getFrontOffsetX(), (float)(-face.getFrontOffsetZ()));
                if (face.getFrontOffsetZ() < 0) {
                    GL11.glTranslated(0.0, 0.0, 0.126);
                    GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                }
                else {
                    GL11.glTranslated(0.0, 0.0, -0.126);
                }
                GL11.glRotatef(90.0f, 0.0f, 0.0f, -1.0f);
                GL11.glScaled(0.2, 0.2, 0.2);
                UtilsFX.renderQuadCentered(1, 1, 0, 1.0f, 1.0f, 1.0f, 1.0f, 200, 1, 1.0f);
                GL11.glPopMatrix();
            }
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5, y + 0.3125, z + 0.5);
            GlStateManager.depthMask(false);
            for (final EnumFacing face : EnumFacing.values()) {
                GL11.glPushMatrix();
                GL11.glRotatef(90.0f, (float)(-face.getFrontOffsetY()), (float)face.getFrontOffsetX(), (float)(-face.getFrontOffsetZ()));
                if (face.getFrontOffsetZ() < 0) {
                    GL11.glTranslated(0.0, 0.0, 0.26);
                    GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                }
                else {
                    GL11.glTranslated(0.0, 0.0, -0.26);
                }
                if (face.getFrontOffsetZ() != 0) {
                    GL11.glRotatef(90.0f, 0.0f, 0.0f, -1.0f);
                }
                GL11.glScaled(0.25, 0.5, 0.25);
                UtilsFX.renderQuadCentered(1, 1, 0, 1.0f, 1.0f, 1.0f, 1.0f, 200, 771, 1.0f);
                GL11.glPopMatrix();
            }
            GL11.glPopMatrix();
            GlStateManager.depthMask(true);
            ShaderHelper.releaseShader();
            GL11.glPopMatrix();
        }
    }
    
    static {
        starsTexture = new ResourceLocation("textures/entity/end_portal.png");
    }
}
