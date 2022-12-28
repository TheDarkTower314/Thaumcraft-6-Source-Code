package thaumcraft.client.renderers.tile;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.lib.RenderCubes;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.models.block.ModelBrain;
import thaumcraft.client.renderers.models.block.ModelJar;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.tiles.devices.TileJarBrain;
import thaumcraft.common.tiles.essentia.TileJar;
import thaumcraft.common.tiles.essentia.TileJarFillable;


@SideOnly(Side.CLIENT)
public class TileJarRenderer extends TileEntitySpecialRenderer
{
    private ModelJar model;
    private ModelBrain brain;
    private static ResourceLocation TEX_LABEL;
    private static ResourceLocation TEX_BRAIN;
    private static ResourceLocation TEX_BRINE;
    
    public TileJarRenderer() {
        model = new ModelJar();
        brain = new ModelBrain();
    }
    
    public void renderTileEntityAt(TileJar tile, double x, double y, double z, float f) {
        GL11.glPushMatrix();
        GL11.glDisable(2884);
        GL11.glTranslatef((float)x + 0.5f, (float)y + 0.01f, (float)z + 0.5f);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (tile instanceof TileJarBrain) {
            renderBrain((TileJarBrain)tile, x, y, z, f);
        }
        else if (tile instanceof TileJarFillable) {
            GL11.glDisable(2896);
            if (((TileJarFillable)tile).blocked) {
                GL11.glPushMatrix();
                bindTexture(TileJarRenderer.TEX_BRINE);
                GL11.glScaled(1.001, 1.001, 1.001);
                model.renderLidBrace();
                GL11.glPopMatrix();
            }
            if (ThaumcraftApiHelper.getConnectableTile(tile.getWorld(), tile.getPos(), EnumFacing.UP) != null) {
                GL11.glPushMatrix();
                bindTexture(TileJarRenderer.TEX_BRINE);
                GL11.glScaled(0.9, 1.0, 0.9);
                model.renderLidExtension();
                GL11.glPopMatrix();
            }
            if (((TileJarFillable)tile).aspectFilter != null) {
                GL11.glPushMatrix();
                GL11.glBlendFunc(770, 771);
                switch (((TileJarFillable)tile).facing) {
                    case 3: {
                        GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                        break;
                    }
                    case 5: {
                        GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                        break;
                    }
                    case 4: {
                        GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                        break;
                    }
                }
                float rot = (float)((((TileJarFillable)tile).aspectFilter.getTag().hashCode() + tile.getPos().getX() + ((TileJarFillable)tile).facing) % 4 - 2);
                GL11.glPushMatrix();
                GL11.glTranslatef(0.0f, -0.4f, 0.315f);
                if (ModConfig.CONFIG_GRAPHICS.crooked) {
                    GL11.glRotatef(rot, 0.0f, 0.0f, 1.0f);
                }
                UtilsFX.renderQuadCentered(TileJarRenderer.TEX_LABEL, 0.5f, 1.0f, 1.0f, 1.0f, -99, 771, 1.0f);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslatef(0.0f, -0.4f, 0.316f);
                if (ModConfig.CONFIG_GRAPHICS.crooked) {
                    GL11.glRotatef(rot, 0.0f, 0.0f, 1.0f);
                }
                GL11.glScaled(0.021, 0.021, 0.021);
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                UtilsFX.drawTag(-8, -8, ((TileJarFillable)tile).aspectFilter);
                GL11.glPopMatrix();
                GL11.glPopMatrix();
            }
            if (((TileJarFillable)tile).amount > 0) {
                renderLiquid((TileJarFillable)tile, x, y, z, f);
            }
            GL11.glEnable(2896);
        }
        GL11.glEnable(2884);
        GL11.glPopMatrix();
    }
    
    public void renderLiquid(TileJarFillable te, double x, double y, double z, float f) {
        GL11.glPushMatrix();
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        World world = te.getWorld();
        RenderCubes renderBlocks = new RenderCubes();
        GL11.glDisable(2896);
        float level = te.amount / 250.0f * 0.625f;
        Tessellator t = Tessellator.getInstance();
        renderBlocks.setRenderBounds(0.25, 0.0625, 0.25, 0.75, 0.0625 + level, 0.75);
        t.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
        Color co = new Color(0);
        if (te.aspect != null) {
            co = new Color(te.aspect.getColor());
        }
        TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("thaumcraft:blocks/animatedglow");
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        renderBlocks.renderFaceYNeg(BlocksTC.jarNormal, -0.5, 0.0, -0.5, icon, co.getRed() / 255.0f, co.getGreen() / 255.0f, co.getBlue() / 255.0f, 200);
        renderBlocks.renderFaceYPos(BlocksTC.jarNormal, -0.5, 0.0, -0.5, icon, co.getRed() / 255.0f, co.getGreen() / 255.0f, co.getBlue() / 255.0f, 200);
        renderBlocks.renderFaceZNeg(BlocksTC.jarNormal, -0.5, 0.0, -0.5, icon, co.getRed() / 255.0f, co.getGreen() / 255.0f, co.getBlue() / 255.0f, 200);
        renderBlocks.renderFaceZPos(BlocksTC.jarNormal, -0.5, 0.0, -0.5, icon, co.getRed() / 255.0f, co.getGreen() / 255.0f, co.getBlue() / 255.0f, 200);
        renderBlocks.renderFaceXNeg(BlocksTC.jarNormal, -0.5, 0.0, -0.5, icon, co.getRed() / 255.0f, co.getGreen() / 255.0f, co.getBlue() / 255.0f, 200);
        renderBlocks.renderFaceXPos(BlocksTC.jarNormal, -0.5, 0.0, -0.5, icon, co.getRed() / 255.0f, co.getGreen() / 255.0f, co.getBlue() / 255.0f, 200);
        t.draw();
        GL11.glEnable(2896);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public void renderBrain(TileJarBrain te, double x, double y, double z, float f) {
        float bob = MathHelper.sin(Minecraft.getMinecraft().player.ticksExisted / 14.0f) * 0.03f + 0.03f;
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, -0.8f + bob, 0.0f);
        float f2;
        for (f2 = te.rota - te.rotb; f2 >= 3.141593f; f2 -= 6.283185f) {}
        while (f2 < -3.141593f) {
            f2 += 6.283185f;
        }
        float f3 = te.rotb + f2 * f;
        GL11.glRotatef(f3 * 180.0f / 3.141593f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        bindTexture(TileJarRenderer.TEX_BRAIN);
        GL11.glScalef(0.4f, 0.4f, 0.4f);
        brain.render();
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        bindTexture(TileJarRenderer.TEX_BRINE);
        model.renderBrine();
        GL11.glPopMatrix();
    }
    
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        renderTileEntityAt((TileJar)te, x, y, z, partialTicks);
    }
    
    static {
        TEX_LABEL = new ResourceLocation("thaumcraft", "textures/models/label.png");
        TEX_BRAIN = new ResourceLocation("thaumcraft", "textures/models/brain2.png");
        TEX_BRINE = new ResourceLocation("thaumcraft", "textures/models/jarbrine.png");
    }
}
