package thaumcraft.client.renderers.tile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.block.ModelBellows;
import thaumcraft.client.renderers.models.block.ModelBoreBase;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.devices.TileBellows;
import thaumcraft.common.tiles.essentia.TileTubeBuffer;


public class TileBellowsRenderer extends TileEntitySpecialRenderer<TileBellows>
{
    private ModelBellows model;
    private ModelBoreBase model2;
    private static ResourceLocation TEX;
    private static ResourceLocation TEX_BORE;
    
    public TileBellowsRenderer() {
        model = new ModelBellows();
        model2 = new ModelBoreBase();
    }
    
    public void render(TileBellows bellows, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(bellows, x, y, z, partialTicks, destroyStage, alpha);
        float scale = 0.0f;
        EnumFacing dir = EnumFacing.WEST;
        boolean extension = false;
        if (bellows == null) {
            EntityPlayer p = Minecraft.getMinecraft().player;
            scale = MathHelper.sin(p.ticksExisted / 8.0f) * 0.3f + 0.7f;
        }
        else {
            scale = bellows.inflation;
            dir = BlockStateUtils.getFacing(bellows.getBlockMetadata());
            TileEntity te = bellows.getWorld().getTileEntity(bellows.getPos().offset(BlockStateUtils.getFacing(bellows.getBlockMetadata())));
            if (te != null && te instanceof TileTubeBuffer) {
                extension = true;
            }
        }
        float tscale = 0.125f + scale * 0.875f;
        if (extension) {
            bindTexture(TileBellowsRenderer.TEX_BORE);
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
            model2.renderNozzle();
            GL11.glPopMatrix();
        }
        bindTexture(TileBellowsRenderer.TEX);
        GL11.glPushMatrix();
        GL11.glEnable(2977);
        GL11.glEnable(32826);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        translateFromOrientation((float)x, (float)y, (float)z, dir.ordinal());
        if (destroyStage >= 0) {
            bindTexture(TileBellowsRenderer.DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0f, 4.0f, 1.0f);
            GlStateManager.translate(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        }
        GL11.glTranslatef(0.0f, 1.0f, 0.0f);
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, (scale + 0.1f) / 2.0f, 0.5f);
        model.Bag.setRotationPoint(0.0f, 0.5f, 0.0f);
        model.Bag.render(0.0625f);
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        GL11.glTranslatef(0.0f, -1.0f, 0.0f);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, -tscale / 2.0f + 0.5f, 0.0f);
        model.TopPlank.render(0.0625f);
        GL11.glTranslatef(0.0f, tscale / 2.0f - 0.5f, 0.0f);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, tscale / 2.0f - 0.5f, 0.0f);
        model.BottomPlank.render(0.0625f);
        GL11.glTranslatef(0.0f, -tscale / 2.0f + 0.5f, 0.0f);
        GL11.glPopMatrix();
        model.render();
        GL11.glDisable(32826);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        if (destroyStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
    
    private void translateFromOrientation(double x, double y, double z, int orientation) {
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
