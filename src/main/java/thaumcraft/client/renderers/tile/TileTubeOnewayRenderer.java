package thaumcraft.client.renderers.tile;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.client.renderers.models.block.ModelTubeValve;
import thaumcraft.common.tiles.essentia.TileTubeOneway;


public class TileTubeOnewayRenderer extends TileEntitySpecialRenderer
{
    private ModelTubeValve model;
    private static ResourceLocation TEX_VALVE;
    EnumFacing fd;
    
    public TileTubeOnewayRenderer() {
        fd = null;
        model = new ModelTubeValve();
    }
    
    public void renderEntityAt(TileTubeOneway valve, double x, double y, double z, float fq) {
        bindTexture(TileTubeOnewayRenderer.TEX_VALVE);
        if (valve.getWorld() != null && ThaumcraftApiHelper.getConnectableTile(valve.getWorld(), valve.getPos(), valve.facing.getOpposite()) == null) {
            return;
        }
        GL11.glPushMatrix();
        fd = valve.facing;
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        if (fd.getFrontOffsetY() == 0) {
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        }
        else {
            GL11.glRotatef(90.0f, -1.0f, 0.0f, 0.0f);
            GL11.glRotatef(90.0f, (float) fd.getFrontOffsetY(), 0.0f, 0.0f);
        }
        GL11.glRotatef(90.0f, (float) fd.getFrontOffsetX(), (float) fd.getFrontOffsetY(), (float) fd.getFrontOffsetZ());
        GL11.glPushMatrix();
        GL11.glColor3f(0.45f, 0.5f, 1.0f);
        GL11.glScaled(2.0, 2.0, 2.0);
        GL11.glTranslated(0.0, -0.3199999928474426, 0.0);
        model.renderRod();
        GL11.glPopMatrix();
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        renderEntityAt((TileTubeOneway)te, x, y, z, partialTicks);
    }
    
    static {
        TEX_VALVE = new ResourceLocation("thaumcraft", "textures/models/valve.png");
    }
}
