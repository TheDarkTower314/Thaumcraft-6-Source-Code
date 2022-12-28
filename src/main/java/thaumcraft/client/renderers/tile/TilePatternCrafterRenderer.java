package thaumcraft.client.renderers.tile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.models.block.ModelBoreBase;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.crafting.TilePatternCrafter;


public class TilePatternCrafterRenderer extends TileEntitySpecialRenderer
{
    private ModelBoreBase model;
    private static ResourceLocation TEX;
    private static ResourceLocation ICON;
    
    public TilePatternCrafterRenderer() {
        model = new ModelBoreBase();
    }
    
    public void renderTileEntityAt(TilePatternCrafter pc, double x, double y, double z, float fq) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        int f = 3;
        if (pc.getWorld() != null) {
            f = BlockStateUtils.getFacing(pc.getBlockMetadata()).ordinal();
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x + 0.5f, (float)y + 0.75f, (float)z + 0.5f);
        switch (f) {
            case 5: {
                GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case 4: {
                GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case 2: {
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
        }
        GL11.glPushMatrix();
        GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
        GL11.glTranslatef(0.0f, 0.0f, -0.5f);
        UtilsFX.renderQuadCentered(TilePatternCrafterRenderer.TEX, 10, 1, pc.type, 0.5f, 1.0f, 1.0f, 1.0f, pc.getBlockType().getPackedLightmapCoords(pc.getWorld().getBlockState(pc.getPos()), pc.getWorld(), pc.getPos()), 771, 1.0f);
        GL11.glPopMatrix();
        mc.renderEngine.bindTexture(TilePatternCrafterRenderer.ICON);
        GL11.glPushMatrix();
        GL11.glTranslatef(-0.2f, -0.40625f, 0.05f);
        GL11.glRotatef(-pc.rot % 360.0f, 0.0f, 0.0f, 1.0f);
        GL11.glScaled(0.5, 0.5, 1.0);
        GL11.glTranslatef(-0.5f, -0.5f, 0.0f);
        UtilsFX.renderTextureIn3D(1.0f, 0.0f, 0.0f, 1.0f, 16, 16, 0.1f);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.2f, -0.40625f, 0.05f);
        GL11.glRotatef(pc.rot % 360.0f, 0.0f, 0.0f, 1.0f);
        GL11.glScaled(0.5, 0.5, 1.0);
        GL11.glTranslatef(-0.5f, -0.5f, 0.0f);
        UtilsFX.renderTextureIn3D(1.0f, 0.0f, 0.0f, 1.0f, 16, 16, 0.1f);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }
    
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        renderTileEntityAt((TilePatternCrafter)te, x, y, z, partialTicks);
    }
    
    static {
        TEX = new ResourceLocation("thaumcraft", "textures/blocks/pattern_crafter_modes.png");
        ICON = new ResourceLocation("thaumcraft", "textures/misc/gear_brass.png");
    }
}
