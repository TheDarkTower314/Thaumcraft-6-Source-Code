// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.tiles.essentia.TileTubeBuffer;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.block.ModelBoreBase;
import thaumcraft.client.renderers.models.block.ModelBellows;
import thaumcraft.common.tiles.devices.TileBellows;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class TileBellowsRenderer extends TileEntitySpecialRenderer<TileBellows>
{
    private ModelBellows model;
    private ModelBoreBase model2;
    private static final ResourceLocation TEX;
    private static final ResourceLocation TEX_BORE;
    
    public TileBellowsRenderer() {
        this.model = new ModelBellows();
        this.model2 = new ModelBoreBase();
    }
    
    public void render(final TileBellows bellows, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float alpha) {
        super.render(bellows, x, y, z, partialTicks, destroyStage, alpha);
        float scale = 0.0f;
        EnumFacing dir = EnumFacing.WEST;
        boolean extension = false;
        if (bellows == null) {
            final EntityPlayer p = Minecraft.getMinecraft().player;
            scale = MathHelper.sin(p.ticksExisted / 8.0f) * 0.3f + 0.7f;
        }
        else {
            scale = bellows.inflation;
            dir = BlockStateUtils.getFacing(bellows.getBlockMetadata());
            final TileEntity te = bellows.getWorld().getTileEntity(bellows.getPos().offset(BlockStateUtils.getFacing(bellows.getBlockMetadata())));
            if (te != null && te instanceof TileTubeBuffer) {
                extension = true;
            }
        }
        final float tscale = 0.125f + scale * 0.875f;
        if (extension) {
            this.bindTexture(TileBellowsRenderer.TEX_BORE);
            GL11.glPushMatrix();
            GL11.glTranslatef((float)x + 0.5f + dir.getFrontOffsetX(), (float)y + dir.getFrontOffsetY(), (float)z + 0.5f + dir.getFrontOffsetZ());
            switch (dir.getOpposite().ordinal()) {
                case 0: {
                    GL11.glTranslatef(-0.5f, 0.5f, 0.0f);
                    GL11.glRotatef(90.0f, 0.0f, 0.0f, -1.0f);
                    break;
                }
                case 1: {
                    GL11.glTranslatef(0.5f, 0.5f, 0.0f);
                    GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                    break;
                }
                case 2: {
                    GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
                case 3: {
                    GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
                case 4: {
                    GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
                case 5: {
                    GL11.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
            }
            this.model2.renderNozzle();
            GL11.glPopMatrix();
        }
        this.bindTexture(TileBellowsRenderer.TEX);
        GL11.glPushMatrix();
        GL11.glEnable(2977);
        GL11.glEnable(32826);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.translateFromOrientation((float)x, (float)y, (float)z, dir.ordinal());
        if (destroyStage >= 0) {
            this.bindTexture(TileBellowsRenderer.DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0f, 4.0f, 1.0f);
            GlStateManager.translate(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        }
        GL11.glTranslatef(0.0f, 1.0f, 0.0f);
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, (scale + 0.1f) / 2.0f, 0.5f);
        this.model.Bag.setRotationPoint(0.0f, 0.5f, 0.0f);
        this.model.Bag.render(0.0625f);
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        GL11.glTranslatef(0.0f, -1.0f, 0.0f);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, -tscale / 2.0f + 0.5f, 0.0f);
        this.model.TopPlank.render(0.0625f);
        GL11.glTranslatef(0.0f, tscale / 2.0f - 0.5f, 0.0f);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, tscale / 2.0f - 0.5f, 0.0f);
        this.model.BottomPlank.render(0.0625f);
        GL11.glTranslatef(0.0f, -tscale / 2.0f + 0.5f, 0.0f);
        GL11.glPopMatrix();
        this.model.render();
        GL11.glDisable(32826);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        if (destroyStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
    
    private void translateFromOrientation(final double x, final double y, final double z, final int orientation) {
        GL11.glTranslatef((float)x + 0.5f, (float)y - 0.5f, (float)z + 0.5f);
        if (orientation == 0) {
            GL11.glTranslatef(0.0f, 1.0f, -1.0f);
            GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        }
        else if (orientation == 1) {
            GL11.glTranslatef(0.0f, 1.0f, 1.0f);
            GL11.glRotatef(270.0f, 1.0f, 0.0f, 0.0f);
        }
        else if (orientation == 2) {
            GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
        }
        else if (orientation == 4) {
            GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
        }
        else if (orientation == 5) {
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        }
    }
    
    static {
        TEX = new ResourceLocation("thaumcraft", "textures/blocks/bellows.png");
        TEX_BORE = new ResourceLocation("thaumcraft", "textures/models/bore.png");
    }
}
