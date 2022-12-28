package thaumcraft.client.renderers.tile;
import java.awt.Color;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.TexturedQuadTC;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.devices.TileDioptra;


public class TileDioptraRenderer extends TileEntitySpecialRenderer
{
    private ResourceLocation gridTexture;
    private ResourceLocation sideTexture;
    private float[] alphas;
    
    public TileDioptraRenderer() {
        gridTexture = new ResourceLocation("thaumcraft", "textures/misc/gridblock.png");
        sideTexture = new ResourceLocation("thaumcraft", "textures/models/dioptra_side.png");
        alphas = new float[] { 0.9f, 0.9f, 0.9f, 0.9f };
    }
    
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        TileDioptra tco = (TileDioptra)te;
        Tessellator tessellator = Tessellator.getInstance();
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        float t = (rendererDispatcher.entity != null) ? (rendererDispatcher.entity.ticksExisted + partialTicks) : 0.0f;
        float rc = 1.0f;
        float gc = 1.0f;
        float bc = 1.0f;
        if (BlockStateUtils.isEnabled(tco.getBlockMetadata())) {
            rc = MathHelper.sin(t / 12.0f) * 0.05f + 0.85f;
            gc = MathHelper.sin(t / 11.0f) * 0.05f + 0.9f;
            bc = MathHelper.sin(t / 10.0f) * 0.05f + 0.95f;
        }
        else {
            rc = MathHelper.sin(t / 12.0f) * 0.05f + 0.85f;
            gc = MathHelper.sin(t / 11.0f) * 0.05f + 0.45f;
            bc = MathHelper.sin(t / 10.0f) * 0.05f + 0.95f;
        }
        GL11.glShadeModel(7425);
        GL11.glBlendFunc(770, 1);
        GL11.glPushMatrix();
        GL11.glTranslated(-0.495, 0.501, -0.495);
        bindTexture(gridTexture);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glScaled(0.99, 1.0, 0.99);
        for (int a = 0; a < 12; ++a) {
            for (int b = 0; b < 12; ++b) {
                int[] colors = calcColorMap(new float[] { 0.0f, 0.0f, 0.0f, 0.0f }, rc, gc, bc);
                double d3 = a - 6;
                double d4 = b - 6;
                double dis = Math.sqrt(d3 * d3 + d4 * d4);
                float s = MathHelper.sin((float)((tco.counter - dis * 10.0) / 8.0));
                TexturedQuadTC quad = new TexturedQuadTC(new PositionTextureVertex[] { new PositionTextureVertex(a / 12.0f, tco.grid_amt[a + b * 13] / 96.0f, b / 12.0f, 0.0f, 1.0f), new PositionTextureVertex((a + 1) / 12.0f, tco.grid_amt[a + 1 + b * 13] / 96.0f, b / 12.0f, 1.0f, 1.0f), new PositionTextureVertex((a + 1) / 12.0f, tco.grid_amt[a + 1 + (b + 1) * 13] / 96.0f, (b + 1) / 12.0f, 1.0f, 0.0f), new PositionTextureVertex(a / 12.0f, tco.grid_amt[a + (b + 1) * 13] / 96.0f, (b + 1) / 12.0f, 0.0f, 0.0f) });
                quad.flipFace();
                quad.draw(tessellator.getBuffer(), 1.0f, (int)(200.0f + s * 15.0f), colors, alphas);
                if (a == 0) {
                    quad = new TexturedQuadTC(new PositionTextureVertex[] { new PositionTextureVertex(0.0f, 0.0f, b / 12.0f, 0.0f, 1.0f), new PositionTextureVertex(0.0f, tco.grid_amt[b * 13] / 96.0f, b / 12.0f, 1.0f, 1.0f), new PositionTextureVertex(0.0f, tco.grid_amt[(b + 1) * 13] / 96.0f, (b + 1) / 12.0f, 1.0f, 0.0f), new PositionTextureVertex(0.0f, 0.0f, (b + 1) / 12.0f, 0.0f, 0.0f) });
                    quad.flipFace();
                    quad.draw(tessellator.getBuffer(), 1.0f, (int)(200.0f + s * 15.0f), colors, new float[] { 0.0f, 0.9f, 0.9f, 0.0f });
                }
                if (a == 11) {
                    quad = new TexturedQuadTC(new PositionTextureVertex[] { new PositionTextureVertex(1.0f, 0.0f, b / 12.0f, 0.0f, 1.0f), new PositionTextureVertex(1.0f, tco.grid_amt[a + 1 + b * 13] / 96.0f, b / 12.0f, 1.0f, 1.0f), new PositionTextureVertex(1.0f, tco.grid_amt[a + 1 + (b + 1) * 13] / 96.0f, (b + 1) / 12.0f, 1.0f, 0.0f), new PositionTextureVertex(1.0f, 0.0f, (b + 1) / 12.0f, 0.0f, 0.0f) });
                    quad.draw(tessellator.getBuffer(), 1.0f, (int)(200.0f + s * 15.0f), colors, new float[] { 0.0f, 0.9f, 0.9f, 0.0f });
                }
                if (b == 0) {
                    quad = new TexturedQuadTC(new PositionTextureVertex[] { new PositionTextureVertex(a / 12.0f, 0.0f, 0.0f, 0.0f, 1.0f), new PositionTextureVertex((a + 1) / 12.0f, 0.0f, 0.0f, 1.0f, 1.0f), new PositionTextureVertex((a + 1) / 12.0f, tco.grid_amt[a + 1] / 96.0f, 0.0f, 1.0f, 0.0f), new PositionTextureVertex(a / 12.0f, tco.grid_amt[a] / 96.0f, 0.0f, 0.0f, 0.0f) });
                    quad.flipFace();
                    quad.draw(tessellator.getBuffer(), 1.0f, (int)(200.0f + s * 15.0f), colors, new float[] { 0.0f, 0.0f, 0.9f, 0.9f });
                }
                if (b == 11) {
                    quad = new TexturedQuadTC(new PositionTextureVertex[] { new PositionTextureVertex(a / 12.0f, 0.0f, 1.0f, 0.0f, 1.0f), new PositionTextureVertex((a + 1) / 12.0f, 0.0f, 1.0f, 1.0f, 1.0f), new PositionTextureVertex((a + 1) / 12.0f, tco.grid_amt[a + 1 + (b + 1) * 13] / 96.0f, 1.0f, 1.0f, 0.0f), new PositionTextureVertex(a / 12.0f, tco.grid_amt[a + (b + 1) * 13] / 96.0f, 1.0f, 0.0f, 0.0f) });
                    quad.draw(tessellator.getBuffer(), 1.0f, (int)(200.0f + s * 15.0f), colors, new float[] { 0.0f, 0.0f, 0.9f, 0.9f });
                }
            }
        }
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GlStateManager.disableCull();
        GL11.glTranslated(0.0, 1.0, 0.0);
        GL11.glRotatef(270.0f, 0.0f, 0.0f, 1.0f);
        for (int q = 0; q < 4; ++q) {
            GL11.glPushMatrix();
            GL11.glRotatef(90.0f * q, 1.0f, 0.0f, 0.0f);
            GL11.glTranslated(0.0, 0.0, -0.5);
            UtilsFX.renderQuadCentered(sideTexture, 1.0f, rc, gc, bc, 220, 1, 0.8f);
            GL11.glPopMatrix();
        }
        GlStateManager.enableCull();
        GL11.glPopMatrix();
        GL11.glShadeModel(7424);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    
    int[] calcColorMap(float[] fs, float r, float g, float b) {
        int[] colors = { 0, 0, 0, 0 };
        for (int a = 0; a < 4; ++a) {
            float g2 = g;
            if (fs[a] > 0.0f) {
                float ll = 1.0f - fs[a];
                g2 *= ll;
            }
            g2 = MathHelper.clamp(g2, 0.0f, 1.0f);
            Color color1 = new Color(r * 0.8f, g2, b);
            colors[a] = color1.getRGB();
        }
        return colors;
    }
}
