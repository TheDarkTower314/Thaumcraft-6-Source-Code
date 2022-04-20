// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.tile;

import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.common.tiles.essentia.TileTubeOneway;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.block.ModelTubeValve;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class TileTubeOnewayRenderer extends TileEntitySpecialRenderer
{
    private ModelTubeValve model;
    private static final ResourceLocation TEX_VALVE;
    EnumFacing fd;
    
    public TileTubeOnewayRenderer() {
        this.fd = null;
        this.model = new ModelTubeValve();
    }
    
    public void renderEntityAt(final TileTubeOneway valve, final double x, final double y, final double z, final float fq) {
        this.bindTexture(TileTubeOnewayRenderer.TEX_VALVE);
        if (valve.getWorld() != null && ThaumcraftApiHelper.getConnectableTile(valve.getWorld(), valve.getPos(), valve.facing.getOpposite()) == null) {
            return;
        }
        GL11.glPushMatrix();
        this.fd = valve.facing;
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        if (this.fd.getFrontOffsetY() == 0) {
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        }
        else {
            GL11.glRotatef(90.0f, -1.0f, 0.0f, 0.0f);
            GL11.glRotatef(90.0f, (float)this.fd.getFrontOffsetY(), 0.0f, 0.0f);
        }
        GL11.glRotatef(90.0f, (float)this.fd.getFrontOffsetX(), (float)this.fd.getFrontOffsetY(), (float)this.fd.getFrontOffsetZ());
        GL11.glPushMatrix();
        GL11.glColor3f(0.45f, 0.5f, 1.0f);
        GL11.glScaled(2.0, 2.0, 2.0);
        GL11.glTranslated(0.0, -0.3199999928474426, 0.0);
        this.model.renderRod();
        GL11.glPopMatrix();
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    public void render(final TileEntity te, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        this.renderEntityAt((TileTubeOneway)te, x, y, z, partialTicks);
    }
    
    static {
        TEX_VALVE = new ResourceLocation("thaumcraft", "textures/models/valve.png");
    }
}
