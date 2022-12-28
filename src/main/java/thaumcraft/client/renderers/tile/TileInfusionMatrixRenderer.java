package thaumcraft.client.renderers.tile;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.renderers.models.ModelCube;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;


@SideOnly(Side.CLIENT)
public class TileInfusionMatrixRenderer extends TileEntitySpecialRenderer<TileInfusionMatrix>
{
    private ModelCube model;
    private ModelCube model_over;
    private static ResourceLocation tex1;
    private static ResourceLocation tex2;
    private static ResourceLocation tex3;
    
    public TileInfusionMatrixRenderer() {
        model = new ModelCube(0);
        model_over = new ModelCube(32);
    }
    
    private void drawHalo(TileEntity is, double x, double y, double z, float par8, int count) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        int q = FMLClientHandler.instance().getClient().gameSettings.fancyGraphics ? 20 : 10;
        Tessellator tessellator = Tessellator.getInstance();
        RenderHelper.disableStandardItemLighting();
        float f1 = count / 500.0f;
        float f2 = 0.9f;
        float f3 = 0.0f;
        Random random = new Random(245L);
        GL11.glDisable(3553);
        GL11.glShadeModel(7425);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        GL11.glDisable(3008);
        GL11.glEnable(2884);
        GL11.glDepthMask(false);
        GL11.glPushMatrix();
        for (int i = 0; i < q; ++i) {
            GL11.glRotatef(random.nextFloat() * 360.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f + f1 * 360.0f, 0.0f, 0.0f, 1.0f);
            tessellator.getBuffer().begin(6, DefaultVertexFormats.POSITION_COLOR);
            float fa = random.nextFloat() * 20.0f + 5.0f + f3 * 10.0f;
            float f4 = random.nextFloat() * 2.0f + 1.0f + f3 * 2.0f;
            fa /= 20.0f / (Math.min(count, 50) / 50.0f);
            f4 /= 20.0f / (Math.min(count, 50) / 50.0f);
            tessellator.getBuffer().pos(0.0, 0.0, 0.0).color(255, 255, 255, (int)(255.0f * (1.0f - f1))).endVertex();
            tessellator.getBuffer().pos(-0.866 * f4, fa, -0.5f * f4).color(255, 0, 255, 0).endVertex();
            tessellator.getBuffer().pos(0.866 * f4, fa, -0.5f * f4).color(255, 0, 255, 0).endVertex();
            tessellator.getBuffer().pos(0.0, fa, 1.0f * f4).color(255, 0, 255, 0).endVertex();
            tessellator.getBuffer().pos(-0.866 * f4, fa, -0.5f * f4).color(255, 0, 255, 0).endVertex();
            tessellator.draw();
        }
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glDisable(2884);
        GL11.glDisable(3042);
        GL11.glShadeModel(7424);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
        GL11.glEnable(3008);
        RenderHelper.enableStandardItemLighting();
        GL11.glBlendFunc(770, 771);
        GL11.glPopMatrix();
    }
    
    public void renderInfusionMatrix(TileInfusionMatrix is, double par2, double par4, double par6, float par8, int destroyStage) {
        GL11.glPushMatrix();
        ResourceLocation t = TileInfusionMatrixRenderer.tex1;
        GL11.glTranslatef((float)par2 + 0.5f, (float)par4 + 0.5f, (float)par6 + 0.5f);
        float ticks = Minecraft.getMinecraft().getRenderViewEntity().ticksExisted + par8;
        float inst = 0.0f;
        int craftcount = 0;
        float startup = 0.0f;
        boolean active = false;
        boolean crafting = false;
        if (is != null && is.getWorld() != null) {
            GL11.glRotatef(ticks % 360.0f * is.startUp, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(35.0f * is.startUp, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(45.0f * is.startUp, 0.0f, 0.0f, 1.0f);
            IBlockState bs = is.getWorld().getBlockState(is.getPos().add(-1, -2, -1));
            if (bs.getBlock() == BlocksTC.pillarAncient) {
                t = TileInfusionMatrixRenderer.tex2;
            }
            if (bs.getBlock() == BlocksTC.pillarEldritch) {
                t = TileInfusionMatrixRenderer.tex3;
            }
            inst = is.stability;
            craftcount = is.craftCount;
            startup = is.startUp;
            active = is.active;
            crafting = is.crafting;
        }
        bindTexture(t);
        if (destroyStage >= 0) {
            bindTexture(TileInfusionMatrixRenderer.DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0f, 4.0f, 1.0f);
            GlStateManager.translate(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        }
        float instability = Math.min(6.0f, 1.0f + ((inst < 0.0f) ? (-inst * 0.66f) : 1.0f) * (Math.min(craftcount, 50) / 50.0f));
        float b1 = 0.0f;
        float b2 = 0.0f;
        float b3 = 0.0f;
        int aa = 0;
        int bb = 0;
        int cc = 0;
        for (int a = 0; a < 2; ++a) {
            for (int b4 = 0; b4 < 2; ++b4) {
                for (int c = 0; c < 2; ++c) {
                    if (active) {
                        b1 = MathHelper.sin((ticks + a * 10) / 15.0f) * 0.01f * startup * instability;
                        b2 = MathHelper.sin((ticks + b4 * 10) / 14.0f) * 0.01f * startup * instability;
                        b3 = MathHelper.sin((ticks + c * 10) / 13.0f) * 0.01f * startup * instability;
                    }
                    aa = ((a == 0) ? -1 : 1);
                    bb = ((b4 == 0) ? -1 : 1);
                    cc = ((c == 0) ? -1 : 1);
                    GL11.glPushMatrix();
                    GL11.glTranslatef(b1 + aa * 0.25f, b2 + bb * 0.25f, b3 + cc * 0.25f);
                    if (a > 0) {
                        GL11.glRotatef(90.0f, (float)a, 0.0f, 0.0f);
                    }
                    if (b4 > 0) {
                        GL11.glRotatef(90.0f, 0.0f, (float)b4, 0.0f);
                    }
                    if (c > 0) {
                        GL11.glRotatef(90.0f, 0.0f, 0.0f, (float)c);
                    }
                    GL11.glScaled(0.45, 0.45, 0.45);
                    model.render();
                    GL11.glPopMatrix();
                }
            }
        }
        if (active) {
            GL11.glPushMatrix();
            GL11.glAlphaFunc(516, 0.003921569f);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 1);
            for (int a = 0; a < 2; ++a) {
                for (int b4 = 0; b4 < 2; ++b4) {
                    for (int c = 0; c < 2; ++c) {
                        b1 = MathHelper.sin((ticks + a * 10) / 15.0f) * 0.01f * startup * instability;
                        b2 = MathHelper.sin((ticks + b4 * 10) / 14.0f) * 0.01f * startup * instability;
                        b3 = MathHelper.sin((ticks + c * 10) / 13.0f) * 0.01f * startup * instability;
                        aa = ((a == 0) ? -1 : 1);
                        bb = ((b4 == 0) ? -1 : 1);
                        cc = ((c == 0) ? -1 : 1);
                        GL11.glPushMatrix();
                        GL11.glTranslatef(b1 + aa * 0.25f, b2 + bb * 0.25f, b3 + cc * 0.25f);
                        if (a > 0) {
                            GL11.glRotatef(90.0f, (float)a, 0.0f, 0.0f);
                        }
                        if (b4 > 0) {
                            GL11.glRotatef(90.0f, 0.0f, (float)b4, 0.0f);
                        }
                        if (c > 0) {
                            GL11.glRotatef(90.0f, 0.0f, 0.0f, (float)c);
                        }
                        GL11.glScaled(0.45, 0.45, 0.45);
                        int j = 15728880;
                        int k = j % 65536;
                        int l = j / 65536;
                        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k / 1.0f, l / 1.0f);
                        GL11.glColor4f(0.8f, 0.1f, 1.0f, (MathHelper.sin((ticks + a * 2 + b4 * 3 + c * 4) / 4.0f) * 0.1f + 0.2f) * startup);
                        model_over.render();
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        GL11.glPopMatrix();
                    }
                }
            }
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3042);
            GL11.glAlphaFunc(516, 0.1f);
            GL11.glPopMatrix();
        }
        if (destroyStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
        GL11.glPopMatrix();
        if (crafting) {
            drawHalo(is, par2, par4, par6, par8, craftcount);
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public void render(TileInfusionMatrix te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        renderInfusionMatrix(te, x, y, z, partialTicks, destroyStage);
    }
    
    static {
        tex1 = new ResourceLocation("thaumcraft", "textures/blocks/infuser_normal.png");
        tex2 = new ResourceLocation("thaumcraft", "textures/blocks/infuser_ancient.png");
        tex3 = new ResourceLocation("thaumcraft", "textures/blocks/infuser_eldritch.png");
    }
}
