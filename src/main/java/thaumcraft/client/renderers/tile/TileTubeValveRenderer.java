package thaumcraft.client.renderers.tile;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.block.ModelTubeValve;
import thaumcraft.common.tiles.essentia.TileTubeValve;


public class TileTubeValveRenderer extends TileEntitySpecialRenderer
{
    private ModelTubeValve model;
    private static ResourceLocation TEX_VALVE;
    
    public TileTubeValveRenderer() {
        model = new ModelTubeValve();
    }
    
    public void renderEntityAt(TileTubeValve valve, double x, double y, double z, float fq) {
        bindTexture(TileTubeValveRenderer.TEX_VALVE);
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        if (valve.facing.getFrontOffsetY() == 0) {
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        }
        else {
            GL11.glRotatef(90.0f, -1.0f, 0.0f, 0.0f);
            GL11.glRotatef(90.0f, (float)valve.facing.getFrontOffsetY(), 0.0f, 0.0f);
        }
        GL11.glRotatef(90.0f, (float)valve.facing.getFrontOffsetX(), (float)valve.facing.getFrontOffsetY(), (float)valve.facing.getFrontOffsetZ());
        GL11.glRotated(-valve.rotation * 1.5, 0.0, 1.0, 0.0);
        GL11.glTranslated(0.0, -0.03f - valve.rotation / 360.0f * 0.09f, 0.0);
        GL11.glPushMatrix();
        model.renderRing();
        GL11.glScaled(0.75, 1.0, 0.75);
        model.renderRod();
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }
    
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        renderEntityAt((TileTubeValve)te, x, y, z, partialTicks);
    }
    
    static {
        TEX_VALVE = new ResourceLocation("thaumcraft", "textures/models/valve.png");
    }
}
