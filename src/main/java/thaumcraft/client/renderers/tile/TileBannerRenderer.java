package thaumcraft.client.renderers.tile;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.models.block.ModelBanner;
import thaumcraft.common.tiles.misc.TileBanner;


@SideOnly(Side.CLIENT)
public class TileBannerRenderer extends TileEntitySpecialRenderer
{
    private ModelBanner model;
    private static ResourceLocation TEX_CULT;
    private static ResourceLocation TEX_BLANK;
    
    public TileBannerRenderer() {
        model = new ModelBanner();
    }
    
    public void renderTileEntityAt(TileBanner banner, double par2, double par4, double par6, float par8) {
        GL11.glPushMatrix();
        if (banner.getAspect() == null && banner.getColor() == -1) {
            bindTexture(TileBannerRenderer.TEX_CULT);
        }
        else {
            bindTexture(TileBannerRenderer.TEX_BLANK);
        }
        GL11.glTranslatef((float)par2 + 0.5f, (float)par4 + 1.5f, (float)par6 + 0.5f);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (banner.getWorld() != null) {
            GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            float f2 = banner.getBannerFacing() * 360 / 16.0f;
            GL11.glRotatef(f2, 0.0f, 1.0f, 0.0f);
        }
        if (!banner.getWall()) {
            model.renderPole();
        }
        else {
            GL11.glTranslated(0.0, 1.0, -0.4125);
        }
        model.renderBeam();
        if (banner.getColor() != -1) {
            Color c = new Color(banner.getColor());
            GL11.glColor4f(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 1.0f);
        }
        model.renderTabs();
        EntityPlayer p = Minecraft.getMinecraft().player;
        float f3 = banner.getPos().getX() * 7 + banner.getPos().getY() * 9 + banner.getPos().getZ() * 13 + (float)p.ticksExisted + par8;
        float rx = 0.02f - MathHelper.sin(f3 / 11.0f) * 0.02f;
        model.Banner.rotateAngleX = rx;
        model.renderBanner();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (banner.getAspect() != null) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0f, 0.0f, 0.05001f);
            GL11.glScaled(0.0375, 0.0375, 0.0375);
            GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(-rx * 57.295776f * 2.0f, 1.0f, 0.0f, 0.0f);
            UtilsFX.drawTag(-8, 0, banner.getAspect(), 0.0f, 0, 0.0, 771, 0.75f, false);
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
    }
    
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        renderTileEntityAt((TileBanner)te, x, y, z, partialTicks);
    }
    
    static {
        TEX_CULT = new ResourceLocation("thaumcraft", "textures/models/banner_cultist.png");
        TEX_BLANK = new ResourceLocation("thaumcraft", "textures/models/banner_blank.png");
    }
}
