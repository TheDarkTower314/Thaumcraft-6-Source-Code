// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.particles;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class FXGenericGui extends FXGeneric
{
    boolean inGame;
    
    public FXGenericGui(final World world, final double x, final double y, final double z) {
        super(world, x, y, z);
        this.inGame = false;
        this.inGame = Minecraft.getMinecraft().inGameHasFocus;
    }
    
    public FXGenericGui(final World world, final double x, final double y, final double z, final double xx, final double yy, final double zz) {
        super(world, x, y, z, xx, yy, zz);
        this.inGame = false;
        this.inGame = Minecraft.getMinecraft().inGameHasFocus;
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.inGame && Minecraft.getMinecraft().inGameHasFocus) {
            this.setExpired();
        }
    }
    
    @Override
    public void draw(final BufferBuilder wr, final Entity entityIn, final float partialTicks, final float rotationX, final float rotationZ, final float rotationYZ, final float rotationXY, final float rotationXZ) {
        float tx1 = this.particleTextureIndexX / (float)this.gridSize;
        float tx2 = tx1 + 1.0f / this.gridSize;
        float ty1 = this.particleTextureIndexY / (float)this.gridSize;
        float ty2 = ty1 + 1.0f / this.gridSize;
        final float ts = 0.1f * this.particleScale;
        if (this.particleTexture != null) {
            tx1 = this.particleTexture.getMinU();
            tx2 = this.particleTexture.getMaxU();
            ty1 = this.particleTexture.getMinV();
            ty2 = this.particleTexture.getMaxV();
        }
        if (this.flipped) {
            final float t = tx1;
            tx1 = tx2;
            tx2 = t;
        }
        final float fs = MathHelper.clamp((this.particleAge + partialTicks) / this.particleMaxAge, 0.0f, 1.0f);
        final float pr = this.particleRed + (this.dr - this.particleRed) * fs;
        final float pg = this.particleGreen + (this.dg - this.particleGreen) * fs;
        final float pb = this.particleBlue + (this.db - this.particleBlue) * fs;
        final int i = this.getBrightnessForRender(partialTicks);
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        final float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks);
        final float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks);
        final float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks);
        GL11.glPushMatrix();
        GL11.glTranslatef(f5, f6, -90.0f + f7);
        if (this.angled) {
            GL11.glRotatef(-this.angleYaw + 90.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(this.anglePitch + 90.0f, 1.0f, 0.0f, 0.0f);
        }
        if (this.particleAngle != 0.0f) {
            final float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
            GL11.glRotated(f8 * 57.29577951308232, 0.0, 0.0, 1.0);
        }
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        wr.pos(-ts, -ts, 0.0).tex(tx2, ty2).color(pr, pg, pb, this.particleAlpha).lightmap(j, k).endVertex();
        wr.pos(-ts, ts, 0.0).tex(tx2, ty1).color(pr, pg, pb, this.particleAlpha).lightmap(j, k).endVertex();
        wr.pos(ts, ts, 0.0).tex(tx1, ty1).color(pr, pg, pb, this.particleAlpha).lightmap(j, k).endVertex();
        wr.pos(ts, -ts, 0.0).tex(tx1, ty2).color(pr, pg, pb, this.particleAlpha).lightmap(j, k).endVertex();
        Tessellator.getInstance().draw();
        GL11.glPopMatrix();
    }
}
