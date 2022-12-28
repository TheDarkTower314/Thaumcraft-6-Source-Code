package thaumcraft.client.renderers.tile;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.client.renderers.models.block.ModelTubeValve;
import thaumcraft.common.tiles.essentia.TileTubeBuffer;


public class TileTubeBufferRenderer extends TileEntitySpecialRenderer
{
    private ModelTubeValve model;
    private static ResourceLocation TEX_VALVE;
    
    public TileTubeBufferRenderer() {
        model = new ModelTubeValve();
    }
    
    public void renderEntityAt(TileTubeBuffer buffer, double x, double y, double z, float fq) {
        bindTexture(TileTubeBufferRenderer.TEX_VALVE);
        if (buffer.getWorld() != null) {
            for (EnumFacing dir : EnumFacing.VALUES) {
                if (buffer.chokedSides[dir.ordinal()] != 0 && buffer.openSides[dir.ordinal()]) {
                    if (ThaumcraftApiHelper.getConnectableTile(buffer.getWorld(), buffer.getPos(), dir) != null) {
                        GL11.glPushMatrix();
                        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
                        if (dir.getOpposite().getFrontOffsetY() == 0) {
                            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                        }
                        else {
                            GL11.glRotatef(90.0f, -1.0f, 0.0f, 0.0f);
                            GL11.glRotatef(90.0f, (float)dir.getOpposite().getFrontOffsetY(), 0.0f, 0.0f);
                        }
                        GL11.glRotatef(90.0f, (float)dir.getOpposite().getFrontOffsetX(), (float)dir.getOpposite().getFrontOffsetY(), (float)dir.getOpposite().getFrontOffsetZ());
                        GL11.glPushMatrix();
                        if (buffer.chokedSides[dir.ordinal()] == 2) {
                            GL11.glColor3f(1.0f, 0.3f, 0.3f);
                        }
                        else {
                            GL11.glColor3f(0.3f, 0.3f, 1.0f);
                        }
                        GL11.glScaled(2.0, 1.0, 2.0);
                        GL11.glTranslated(0.0, -0.5, 0.0);
                        model.renderRod();
                        GL11.glPopMatrix();
                        GL11.glColor3f(1.0f, 1.0f, 1.0f);
                        GL11.glPopMatrix();
                    }
                }
            }
        }
    }
    
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        renderEntityAt((TileTubeBuffer)te, x, y, z, partialTicks);
    }
    
    static {
        TEX_VALVE = new ResourceLocation("thaumcraft", "textures/models/valve.png");
    }
}
