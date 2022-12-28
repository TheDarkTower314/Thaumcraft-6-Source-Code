package thaumcraft.client.renderers.tile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.tiles.crafting.TileCrucible;


public class TileCrucibleRenderer extends TileEntitySpecialRenderer
{
    public void renderEntityAt(TileCrucible cr, double x, double y, double z, float fq) {
        if (cr.tank.getFluidAmount() > 0) {
            renderFluid(cr, x, y, z);
        }
    }
    
    public void renderFluid(TileCrucible cr, double x, double y, double z) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y + cr.getFluidHeight(), z + 1.0);
        GL11.glRotatef(90.0f, -1.0f, 0.0f, 0.0f);
        if (cr.tank.getFluidAmount() > 0) {
            TextureAtlasSprite icon = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.WATER.getDefaultState());
            float n = (float)cr.aspects.visSize();
            cr.getClass();
            float recolor = n / 500.0f;
            if (recolor > 0.0f) {
                recolor = 0.5f + recolor / 2.0f;
            }
            if (recolor > 1.0f) {
                recolor = 1.0f;
            }
            UtilsFX.renderQuadFromIcon(icon, 1.0f, 1.0f - recolor / 3.0f, 1.0f - recolor, 1.0f - recolor / 2.0f, BlocksTC.crucible.getPackedLightmapCoords(cr.getWorld().getBlockState(cr.getPos()), cr.getWorld(), cr.getPos()), 771, 1.0f);
        }
        GL11.glPopMatrix();
    }
    
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        renderEntityAt((TileCrucible)te, x, y, z, partialTicks);
    }
}
