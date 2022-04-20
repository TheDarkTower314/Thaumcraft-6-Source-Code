// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.tile;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.client.FMLClientHandler;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.world.IBlockAccess;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;
import thaumcraft.common.tiles.devices.TileMirror;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.ActiveRenderInfo;
import java.util.Random;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.ResourceLocation;
import java.nio.FloatBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

@SideOnly(Side.CLIENT)
public class TileMirrorRenderer extends TileEntitySpecialRenderer
{
    FloatBuffer fBuffer;
    private static final ResourceLocation t1;
    private static final ResourceLocation t2;
    private static ResourceLocation mp;
    private static ResourceLocation mpt;
    
    public TileMirrorRenderer() {
        this.fBuffer = GLAllocation.createDirectFloatBuffer(16);
    }
    
    public void drawPlaneYPos(final TileEntity tileentityendportal, final double x, final double y, final double z, final float f) {
        final float px = (float)TileEntityRendererDispatcher.staticPlayerX;
        final float py = (float)TileEntityRendererDispatcher.staticPlayerY;
        final float pz = (float)TileEntityRendererDispatcher.staticPlayerZ;
        GL11.glDisable(2896);
        final Random random = new Random(31100L);
        final float offset = 0.99f;
        final float p = 0.1875f;
        for (int i = 0; i < 16; ++i) {
            GL11.glPushMatrix();
            float f2 = (float)(16 - i);
            float f3 = 0.0625f;
            float f4 = 1.0f / (f2 + 1.0f);
            if (i == 0) {
                this.bindTexture(TileMirrorRenderer.t1);
                f4 = 0.1f;
                f2 = 65.0f;
                f3 = 0.125f;
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
            }
            if (i == 1) {
                this.bindTexture(TileMirrorRenderer.t2);
                GL11.glEnable(3042);
                GL11.glBlendFunc(1, 1);
                f3 = 0.5f;
            }
            final float f5 = (float)(y + offset);
            final float f6 = (float)(f5 - ActiveRenderInfo.getCameraPosition().y);
            final float f7 = (float)(f5 + f2 - ActiveRenderInfo.getCameraPosition().y);
            float f8 = f6 / f7;
            f8 += (float)(y + offset);
            GL11.glTranslatef(px, f8, pz);
            GL11.glTexGeni(8192, 9472, 9217);
            GL11.glTexGeni(8193, 9472, 9217);
            GL11.glTexGeni(8194, 9472, 9217);
            GL11.glTexGeni(8195, 9472, 9216);
            GL11.glTexGen(8192, 9473, this.calcFloatBuffer(1.0f, 0.0f, 0.0f, 0.0f));
            GL11.glTexGen(8193, 9473, this.calcFloatBuffer(0.0f, 0.0f, 1.0f, 0.0f));
            GL11.glTexGen(8194, 9473, this.calcFloatBuffer(0.0f, 0.0f, 0.0f, 1.0f));
            GL11.glTexGen(8195, 9474, this.calcFloatBuffer(0.0f, 1.0f, 0.0f, 0.0f));
            GL11.glEnable(3168);
            GL11.glEnable(3169);
            GL11.glEnable(3170);
            GL11.glEnable(3171);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5890);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0f, System.currentTimeMillis() % 700000L / 250000.0f, 0.0f);
            GL11.glScalef(f3, f3, f3);
            GL11.glTranslatef(0.5f, 0.5f, 0.0f);
            GL11.glRotatef((i * i * 4321 + i * 9) * 2.0f, 0.0f, 0.0f, 1.0f);
            GL11.glTranslatef(-0.5f, -0.5f, 0.0f);
            GL11.glTranslatef(-px, -pz, -py);
            GL11.glTranslated(ActiveRenderInfo.getCameraPosition().x * f2 / f6, ActiveRenderInfo.getCameraPosition().z * f2 / f6, -py);
            final Tessellator tessellator = Tessellator.getInstance();
            tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR);
            f8 = random.nextFloat() * 0.5f + 0.1f;
            float f9 = random.nextFloat() * 0.5f + 0.4f;
            float f10 = random.nextFloat() * 0.5f + 0.5f;
            if (i == 0) {
                f9 = (f8 = (f10 = 1.0f));
            }
            tessellator.getBuffer().pos(x + p, y + offset, z + 1.0 - p).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.getBuffer().pos(x + p, y + offset, z + p).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.getBuffer().pos(x + 1.0 - p, y + offset, z + p).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.getBuffer().pos(x + 1.0 - p, y + offset, z + 1.0 - p).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.draw();
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
        }
        GL11.glDisable(3042);
        GL11.glDisable(3168);
        GL11.glDisable(3169);
        GL11.glDisable(3170);
        GL11.glDisable(3171);
        GL11.glEnable(2896);
    }
    
    public void drawPlaneYNeg(final TileEntity tileentityendportal, final double x, final double y, final double z, final float f) {
        final float f2 = (float)TileEntityRendererDispatcher.staticPlayerX;
        final float f3 = (float)TileEntityRendererDispatcher.staticPlayerY;
        final float f4 = (float)TileEntityRendererDispatcher.staticPlayerZ;
        GL11.glDisable(2896);
        final Random random = new Random(31100L);
        final float offset = 0.01f;
        final float p = 0.1875f;
        for (int i = 0; i < 16; ++i) {
            GL11.glPushMatrix();
            float f5 = (float)(16 - i);
            float f6 = 0.0625f;
            float f7 = 1.0f / (f5 + 1.0f);
            if (i == 0) {
                this.bindTexture(TileMirrorRenderer.t1);
                f7 = 0.1f;
                f5 = 65.0f;
                f6 = 0.125f;
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
            }
            if (i == 1) {
                this.bindTexture(TileMirrorRenderer.t2);
                GL11.glEnable(3042);
                GL11.glBlendFunc(1, 1);
                f6 = 0.5f;
            }
            final float f8 = (float)(-(y + offset));
            final float f9 = (float)(f8 + ActiveRenderInfo.getCameraPosition().y);
            final float f10 = (float)(f8 + f5 + ActiveRenderInfo.getCameraPosition().y);
            float f11 = f9 / f10;
            f11 += (float)(y + offset);
            GL11.glTranslatef(f2, f11, f4);
            GL11.glTexGeni(8192, 9472, 9217);
            GL11.glTexGeni(8193, 9472, 9217);
            GL11.glTexGeni(8194, 9472, 9217);
            GL11.glTexGeni(8195, 9472, 9216);
            GL11.glTexGen(8192, 9473, this.calcFloatBuffer(1.0f, 0.0f, 0.0f, 0.0f));
            GL11.glTexGen(8193, 9473, this.calcFloatBuffer(0.0f, 0.0f, 1.0f, 0.0f));
            GL11.glTexGen(8194, 9473, this.calcFloatBuffer(0.0f, 0.0f, 0.0f, 1.0f));
            GL11.glTexGen(8195, 9474, this.calcFloatBuffer(0.0f, 1.0f, 0.0f, 0.0f));
            GL11.glEnable(3168);
            GL11.glEnable(3169);
            GL11.glEnable(3170);
            GL11.glEnable(3171);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5890);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0f, System.currentTimeMillis() % 700000L / 250000.0f, 0.0f);
            GL11.glScalef(f6, f6, f6);
            GL11.glTranslatef(0.5f, 0.5f, 0.0f);
            GL11.glRotatef((i * i * 4321 + i * 9) * 2.0f, 0.0f, 0.0f, 1.0f);
            GL11.glTranslatef(-0.5f, -0.5f, 0.0f);
            GL11.glTranslatef(-f2, -f4, -f3);
            GL11.glTranslated(ActiveRenderInfo.getCameraPosition().x * f5 / f9, ActiveRenderInfo.getCameraPosition().z * f5 / f9, -f3);
            final Tessellator tessellator = Tessellator.getInstance();
            tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR);
            f11 = random.nextFloat() * 0.5f + 0.1f;
            float f12 = random.nextFloat() * 0.5f + 0.4f;
            float f13 = random.nextFloat() * 0.5f + 0.5f;
            if (i == 0) {
                f12 = (f11 = (f13 = 1.0f));
            }
            tessellator.getBuffer().pos(x + p, y + offset, z + p).color(f11 * f7, f12 * f7, f13 * f7, 1.0f).endVertex();
            tessellator.getBuffer().pos(x + p, y + offset, z + 1.0 - p).color(f11 * f7, f12 * f7, f13 * f7, 1.0f).endVertex();
            tessellator.getBuffer().pos(x + 1.0 - p, y + offset, z + 1.0 - p).color(f11 * f7, f12 * f7, f13 * f7, 1.0f).endVertex();
            tessellator.getBuffer().pos(x + 1.0 - p, y + offset, z + p).color(f11 * f7, f12 * f7, f13 * f7, 1.0f).endVertex();
            tessellator.draw();
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
        }
        GL11.glDisable(3042);
        GL11.glDisable(3168);
        GL11.glDisable(3169);
        GL11.glDisable(3170);
        GL11.glDisable(3171);
        GL11.glEnable(2896);
    }
    
    public void drawPlaneZNeg(final TileEntity tileentityendportal, final double x, final double y, final double z, final float f) {
        final float px = (float)TileEntityRendererDispatcher.staticPlayerX;
        final float py = (float)TileEntityRendererDispatcher.staticPlayerY;
        final float pz = (float)TileEntityRendererDispatcher.staticPlayerZ;
        GL11.glDisable(2896);
        final Random random = new Random(31100L);
        final float offset = 0.01f;
        final float p = 0.1875f;
        for (int i = 0; i < 16; ++i) {
            GL11.glPushMatrix();
            float f2 = (float)(16 - i);
            float f3 = 0.0625f;
            float f4 = 1.0f / (f2 + 1.0f);
            if (i == 0) {
                this.bindTexture(TileMirrorRenderer.t1);
                f4 = 0.1f;
                f2 = 65.0f;
                f3 = 0.125f;
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
            }
            if (i == 1) {
                this.bindTexture(TileMirrorRenderer.t2);
                GL11.glEnable(3042);
                GL11.glBlendFunc(1, 1);
                f3 = 0.5f;
            }
            final float f5 = (float)(-(z + offset));
            final float f6 = (float)(f5 + ActiveRenderInfo.getCameraPosition().z);
            final float f7 = (float)(f5 + f2 + ActiveRenderInfo.getCameraPosition().z);
            float f8 = f6 / f7;
            f8 += (float)(z + offset);
            GL11.glTranslatef(px, py, f8);
            GL11.glTexGeni(8192, 9472, 9217);
            GL11.glTexGeni(8193, 9472, 9217);
            GL11.glTexGeni(8194, 9472, 9217);
            GL11.glTexGeni(8195, 9472, 9216);
            GL11.glTexGen(8192, 9473, this.calcFloatBuffer(1.0f, 0.0f, 0.0f, 0.0f));
            GL11.glTexGen(8193, 9473, this.calcFloatBuffer(0.0f, 1.0f, 0.0f, 0.0f));
            GL11.glTexGen(8194, 9473, this.calcFloatBuffer(0.0f, 0.0f, 0.0f, 1.0f));
            GL11.glTexGen(8195, 9474, this.calcFloatBuffer(0.0f, 0.0f, 1.0f, 0.0f));
            GL11.glEnable(3168);
            GL11.glEnable(3169);
            GL11.glEnable(3170);
            GL11.glEnable(3171);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5890);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0f, System.currentTimeMillis() % 700000L / 250000.0f, 0.0f);
            GL11.glScalef(f3, f3, f3);
            GL11.glTranslatef(0.5f, 0.5f, 0.0f);
            GL11.glRotatef((i * i * 4321 + i * 9) * 2.0f, 0.0f, 0.0f, 1.0f);
            GL11.glTranslatef(-0.5f, -0.5f, 0.0f);
            GL11.glTranslatef(-px, -py, -pz);
            GL11.glTranslated(ActiveRenderInfo.getCameraPosition().x * f2 / f6, ActiveRenderInfo.getCameraPosition().y * f2 / f6, -pz);
            final Tessellator tessellator = Tessellator.getInstance();
            tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR);
            f8 = random.nextFloat() * 0.5f + 0.1f;
            float f9 = random.nextFloat() * 0.5f + 0.4f;
            float f10 = random.nextFloat() * 0.5f + 0.5f;
            if (i == 0) {
                f9 = (f8 = (f10 = 1.0f));
            }
            tessellator.getBuffer().pos(x + p, y + 1.0 - p, z + offset).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.getBuffer().pos(x + p, y + p, z + offset).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.getBuffer().pos(x + 1.0 - p, y + p, z + offset).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.getBuffer().pos(x + 1.0 - p, y + 1.0 - p, z + offset).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.draw();
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
        }
        GL11.glDisable(3042);
        GL11.glDisable(3168);
        GL11.glDisable(3169);
        GL11.glDisable(3170);
        GL11.glDisable(3171);
        GL11.glEnable(2896);
    }
    
    public void drawPlaneZPos(final TileEntity tileentityendportal, final double x, final double y, final double z, final float f) {
        final float px = (float)TileEntityRendererDispatcher.staticPlayerX;
        final float py = (float)TileEntityRendererDispatcher.staticPlayerY;
        final float pz = (float)TileEntityRendererDispatcher.staticPlayerZ;
        GL11.glDisable(2896);
        final Random random = new Random(31100L);
        final float offset = 0.99f;
        final float p = 0.1875f;
        for (int i = 0; i < 16; ++i) {
            GL11.glPushMatrix();
            float f2 = (float)(16 - i);
            float f3 = 0.0625f;
            float f4 = 1.0f / (f2 + 1.0f);
            if (i == 0) {
                this.bindTexture(TileMirrorRenderer.t1);
                f4 = 0.1f;
                f2 = 65.0f;
                f3 = 0.125f;
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
            }
            if (i == 1) {
                this.bindTexture(TileMirrorRenderer.t2);
                GL11.glEnable(3042);
                GL11.glBlendFunc(1, 1);
                f3 = 0.5f;
            }
            final float f5 = (float)(z + offset);
            final float f6 = (float)(f5 - ActiveRenderInfo.getCameraPosition().z);
            final float f7 = (float)(f5 + f2 - ActiveRenderInfo.getCameraPosition().z);
            float f8 = f6 / f7;
            f8 += (float)(z + offset);
            GL11.glTranslatef(px, py, f8);
            GL11.glTexGeni(8192, 9472, 9217);
            GL11.glTexGeni(8193, 9472, 9217);
            GL11.glTexGeni(8194, 9472, 9217);
            GL11.glTexGeni(8195, 9472, 9216);
            GL11.glTexGen(8192, 9473, this.calcFloatBuffer(1.0f, 0.0f, 0.0f, 0.0f));
            GL11.glTexGen(8193, 9473, this.calcFloatBuffer(0.0f, 1.0f, 0.0f, 0.0f));
            GL11.glTexGen(8194, 9473, this.calcFloatBuffer(0.0f, 0.0f, 0.0f, 1.0f));
            GL11.glTexGen(8195, 9474, this.calcFloatBuffer(0.0f, 0.0f, 1.0f, 0.0f));
            GL11.glEnable(3168);
            GL11.glEnable(3169);
            GL11.glEnable(3170);
            GL11.glEnable(3171);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5890);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0f, System.currentTimeMillis() % 700000L / 250000.0f, 0.0f);
            GL11.glScalef(f3, f3, f3);
            GL11.glTranslatef(0.5f, 0.5f, 0.0f);
            GL11.glRotatef((i * i * 4321 + i * 9) * 2.0f, 0.0f, 0.0f, 1.0f);
            GL11.glTranslatef(-0.5f, -0.5f, 0.0f);
            GL11.glTranslatef(-px, -py, -pz);
            GL11.glTranslated(ActiveRenderInfo.getCameraPosition().x * f2 / f6, ActiveRenderInfo.getCameraPosition().y * f2 / f6, -pz);
            final Tessellator tessellator = Tessellator.getInstance();
            tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR);
            f8 = random.nextFloat() * 0.5f + 0.1f;
            float f9 = random.nextFloat() * 0.5f + 0.4f;
            float f10 = random.nextFloat() * 0.5f + 0.5f;
            if (i == 0) {
                f9 = (f8 = (f10 = 1.0f));
            }
            tessellator.getBuffer().pos(x + p, y + p, z + offset).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.getBuffer().pos(x + p, y + 1.0 - p, z + offset).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.getBuffer().pos(x + 1.0 - p, y + 1.0 - p, z + offset).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.getBuffer().pos(x + 1.0 - p, y + p, z + offset).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.draw();
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
        }
        GL11.glDisable(3042);
        GL11.glDisable(3168);
        GL11.glDisable(3169);
        GL11.glDisable(3170);
        GL11.glDisable(3171);
        GL11.glEnable(2896);
    }
    
    public void drawPlaneXNeg(final TileEntity tileentityendportal, final double x, final double y, final double z, final float f) {
        final float px = (float)TileEntityRendererDispatcher.staticPlayerX;
        final float py = (float)TileEntityRendererDispatcher.staticPlayerY;
        final float pz = (float)TileEntityRendererDispatcher.staticPlayerZ;
        GL11.glDisable(2896);
        final Random random = new Random(31100L);
        final float offset = 0.01f;
        final float p = 0.1875f;
        for (int i = 0; i < 16; ++i) {
            GL11.glPushMatrix();
            float f2 = (float)(16 - i);
            float f3 = 0.0625f;
            float f4 = 1.0f / (f2 + 1.0f);
            if (i == 0) {
                this.bindTexture(TileMirrorRenderer.t1);
                f4 = 0.1f;
                f2 = 65.0f;
                f3 = 0.125f;
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
            }
            if (i == 1) {
                this.bindTexture(TileMirrorRenderer.t2);
                GL11.glEnable(3042);
                GL11.glBlendFunc(1, 1);
                f3 = 0.5f;
            }
            final float f5 = (float)(-(x + offset));
            final float f6 = (float)(f5 + ActiveRenderInfo.getCameraPosition().x);
            final float f7 = (float)(f5 + f2 + ActiveRenderInfo.getCameraPosition().x);
            float f8 = f6 / f7;
            f8 += (float)(x + offset);
            GL11.glTranslatef(f8, py, pz);
            GL11.glTexGeni(8192, 9472, 9217);
            GL11.glTexGeni(8193, 9472, 9217);
            GL11.glTexGeni(8194, 9472, 9217);
            GL11.glTexGeni(8195, 9472, 9216);
            GL11.glTexGen(8193, 9473, this.calcFloatBuffer(0.0f, 1.0f, 0.0f, 0.0f));
            GL11.glTexGen(8192, 9473, this.calcFloatBuffer(0.0f, 0.0f, 1.0f, 0.0f));
            GL11.glTexGen(8194, 9473, this.calcFloatBuffer(0.0f, 0.0f, 0.0f, 1.0f));
            GL11.glTexGen(8195, 9474, this.calcFloatBuffer(1.0f, 0.0f, 0.0f, 0.0f));
            GL11.glEnable(3168);
            GL11.glEnable(3169);
            GL11.glEnable(3170);
            GL11.glEnable(3171);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5890);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0f, System.currentTimeMillis() % 700000L / 250000.0f, 0.0f);
            GL11.glScalef(f3, f3, f3);
            GL11.glTranslatef(0.5f, 0.5f, 0.0f);
            GL11.glRotatef((i * i * 4321 + i * 9) * 2.0f, 0.0f, 0.0f, 1.0f);
            GL11.glTranslatef(-0.5f, -0.5f, 0.0f);
            GL11.glTranslatef(-pz, -py, -px);
            GL11.glTranslated(ActiveRenderInfo.getCameraPosition().z * f2 / f6, ActiveRenderInfo.getCameraPosition().y * f2 / f6, -px);
            final Tessellator tessellator = Tessellator.getInstance();
            tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR);
            f8 = random.nextFloat() * 0.5f + 0.1f;
            float f9 = random.nextFloat() * 0.5f + 0.4f;
            float f10 = random.nextFloat() * 0.5f + 0.5f;
            if (i == 0) {
                f9 = (f8 = (f10 = 1.0f));
            }
            tessellator.getBuffer().pos(x + offset, y + 1.0 - p, z + p).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.getBuffer().pos(x + offset, y + 1.0 - p, z + 1.0 - p).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.getBuffer().pos(x + offset, y + p, z + 1.0 - p).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.getBuffer().pos(x + offset, y + p, z + p).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.draw();
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
        }
        GL11.glDisable(3042);
        GL11.glDisable(3168);
        GL11.glDisable(3169);
        GL11.glDisable(3170);
        GL11.glDisable(3171);
        GL11.glEnable(2896);
    }
    
    public void drawPlaneXPos(final TileEntity tileentityendportal, final double x, final double y, final double z, final float f) {
        final float px = (float)TileEntityRendererDispatcher.staticPlayerX;
        final float py = (float)TileEntityRendererDispatcher.staticPlayerY;
        final float pz = (float)TileEntityRendererDispatcher.staticPlayerZ;
        GL11.glDisable(2896);
        final Random random = new Random(31100L);
        final float offset = 0.99f;
        final float p = 0.1875f;
        for (int i = 0; i < 16; ++i) {
            GL11.glPushMatrix();
            float f2 = (float)(16 - i);
            float f3 = 0.0625f;
            float f4 = 1.0f / (f2 + 1.0f);
            if (i == 0) {
                this.bindTexture(TileMirrorRenderer.t1);
                f4 = 0.1f;
                f2 = 65.0f;
                f3 = 0.125f;
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
            }
            if (i == 1) {
                this.bindTexture(TileMirrorRenderer.t2);
                GL11.glEnable(3042);
                GL11.glBlendFunc(1, 1);
                f3 = 0.5f;
            }
            final float f5 = (float)(x + offset);
            final float f6 = (float)(f5 - ActiveRenderInfo.getCameraPosition().x);
            final float f7 = (float)(f5 + f2 - ActiveRenderInfo.getCameraPosition().x);
            float f8 = f6 / f7;
            f8 += (float)(x + offset);
            GL11.glTranslatef(f8, py, pz);
            GL11.glTexGeni(8192, 9472, 9217);
            GL11.glTexGeni(8193, 9472, 9217);
            GL11.glTexGeni(8194, 9472, 9217);
            GL11.glTexGeni(8195, 9472, 9216);
            GL11.glTexGen(8193, 9473, this.calcFloatBuffer(0.0f, 1.0f, 0.0f, 0.0f));
            GL11.glTexGen(8192, 9473, this.calcFloatBuffer(0.0f, 0.0f, 1.0f, 0.0f));
            GL11.glTexGen(8194, 9473, this.calcFloatBuffer(0.0f, 0.0f, 0.0f, 1.0f));
            GL11.glTexGen(8195, 9474, this.calcFloatBuffer(1.0f, 0.0f, 0.0f, 0.0f));
            GL11.glEnable(3168);
            GL11.glEnable(3169);
            GL11.glEnable(3170);
            GL11.glEnable(3171);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5890);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0f, System.currentTimeMillis() % 700000L / 250000.0f, 0.0f);
            GL11.glScalef(f3, f3, f3);
            GL11.glTranslatef(0.5f, 0.5f, 0.0f);
            GL11.glRotatef((i * i * 4321 + i * 9) * 2.0f, 0.0f, 0.0f, 1.0f);
            GL11.glTranslatef(-0.5f, -0.5f, 0.0f);
            GL11.glTranslatef(-pz, -py, -px);
            GL11.glTranslated(ActiveRenderInfo.getCameraPosition().z * f2 / f6, ActiveRenderInfo.getCameraPosition().y * f2 / f6, -px);
            final Tessellator tessellator = Tessellator.getInstance();
            tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR);
            f8 = random.nextFloat() * 0.5f + 0.1f;
            float f9 = random.nextFloat() * 0.5f + 0.4f;
            float f10 = random.nextFloat() * 0.5f + 0.5f;
            if (i == 0) {
                f9 = (f8 = (f10 = 1.0f));
            }
            tessellator.getBuffer().pos(x + offset, y + p, z + p).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.getBuffer().pos(x + offset, y + p, z + 1.0 - p).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.getBuffer().pos(x + offset, y + 1.0 - p, z + 1.0 - p).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.getBuffer().pos(x + offset, y + 1.0 - p, z + p).color(f8 * f4, f9 * f4, f10 * f4, 1.0f).endVertex();
            tessellator.draw();
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
        }
        GL11.glDisable(3042);
        GL11.glDisable(3168);
        GL11.glDisable(3169);
        GL11.glDisable(3170);
        GL11.glDisable(3171);
        GL11.glEnable(2896);
    }
    
    private FloatBuffer calcFloatBuffer(final float f, final float f1, final float f2, final float f3) {
        this.fBuffer.clear();
        this.fBuffer.put(f).put(f1).put(f2).put(f3);
        this.fBuffer.flip();
        return this.fBuffer;
    }
    
    public void render(final TileEntity te, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        final EnumFacing dir = BlockStateUtils.getFacing(te.getBlockMetadata());
        boolean linked = false;
        if (te instanceof TileMirror) {
            linked = ((TileMirror)te).linked;
        }
        if (te instanceof TileMirrorEssentia) {
            linked = ((TileMirrorEssentia)te).linked;
        }
        final int b = te.getBlockType().getPackedLightmapCoords(te.getWorld().getBlockState(te.getPos()), te.getWorld(), te.getPos());
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        this.translateFromOrientation((float)x, (float)y, (float)z, dir.ordinal(), 0.01f);
        UtilsFX.renderItemIn2D((te.getBlockType() == BlocksTC.mirror) ? "thaumcraft:blocks/mirrorframe" : "thaumcraft:blocks/mirrorframe2", 0.0625f);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        if (linked && FMLClientHandler.instance().getClient().player.getDistanceSqToCenter(te.getPos()) < 1024.0) {
            GL11.glPushMatrix();
            switch (dir) {
                case DOWN: {
                    this.drawPlaneYPos(te, x, y, z, partialTicks);
                    break;
                }
                case UP: {
                    this.drawPlaneYNeg(te, x, y, z, partialTicks);
                    break;
                }
                case WEST: {
                    this.drawPlaneXPos(te, x, y, z, partialTicks);
                    break;
                }
                case EAST: {
                    this.drawPlaneXNeg(te, x, y, z, partialTicks);
                    break;
                }
                case NORTH: {
                    this.drawPlaneZPos(te, x, y, z, partialTicks);
                    break;
                }
                case SOUTH: {
                    this.drawPlaneZNeg(te, x, y, z, partialTicks);
                    break;
                }
            }
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            this.translateFromOrientation((float)x, (float)y, (float)z, dir.ordinal(), 0.02f);
            GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
            GL11.glTranslated(0.5, -0.5, 0.0);
            UtilsFX.renderQuadCentered(TileMirrorRenderer.mpt, 1.0f, 1.0f, 1.0f, 1.0f, b, 771, 1.0f);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        }
        else {
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            this.translateFromOrientation((float)x, (float)y, (float)z, dir.ordinal(), 0.02f);
            GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
            GL11.glTranslated(0.5, -0.5, 0.0);
            UtilsFX.renderQuadCentered(TileMirrorRenderer.mp, 1.0f, 1.0f, 1.0f, 1.0f, b, 771, 1.0f);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        }
    }
    
    private void translateFromOrientation(final float x, final float y, final float z, final int orientation, final float off) {
        if (orientation == 0) {
            GL11.glTranslatef(x, y + 1.0f, z + 1.0f);
            GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        }
        else if (orientation == 1) {
            GL11.glTranslatef(x, y, z);
            GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        }
        else if (orientation == 2) {
            GL11.glTranslatef(x, y, z + 1.0f);
        }
        else if (orientation == 3) {
            GL11.glTranslatef(x + 1.0f, y, z);
            GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
        }
        else if (orientation == 4) {
            GL11.glTranslatef(x + 1.0f, y, z + 1.0f);
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        }
        else if (orientation == 5) {
            GL11.glTranslatef(x, y, z);
            GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        }
        GL11.glTranslatef(0.0f, 0.0f, -off);
    }
    
    static {
        t1 = new ResourceLocation("thaumcraft", "textures/misc/tunnel.png");
        t2 = new ResourceLocation("thaumcraft", "textures/misc/particlefield.png");
        TileMirrorRenderer.mp = new ResourceLocation("thaumcraft", "textures/blocks/mirrorpane.png");
        TileMirrorRenderer.mpt = new ResourceLocation("thaumcraft", "textures/blocks/mirrorpanetrans.png");
    }
}