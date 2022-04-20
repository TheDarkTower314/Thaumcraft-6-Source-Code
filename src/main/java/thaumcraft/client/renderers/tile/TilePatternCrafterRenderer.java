// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.Minecraft;
import thaumcraft.client.lib.UtilsFX;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraftforge.fml.client.FMLClientHandler;
import thaumcraft.common.tiles.crafting.TilePatternCrafter;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.block.ModelBoreBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class TilePatternCrafterRenderer extends TileEntitySpecialRenderer
{
    private ModelBoreBase model;
    private static final ResourceLocation TEX;
    private static final ResourceLocation ICON;
    
    public TilePatternCrafterRenderer() {
        this.model = new ModelBoreBase();
    }
    
    public void renderTileEntityAt(final TilePatternCrafter pc, final double x, final double y, final double z, final float fq) {
        final Minecraft mc = FMLClientHandler.instance().getClient();
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
    
    public void render(final TileEntity te, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        this.renderTileEntityAt((TilePatternCrafter)te, x, y, z, partialTicks);
    }
    
    static {
        TEX = new ResourceLocation("thaumcraft", "textures/blocks/pattern_crafter_modes.png");
        ICON = new ResourceLocation("thaumcraft", "textures/misc/gear_brass.png");
    }
}
