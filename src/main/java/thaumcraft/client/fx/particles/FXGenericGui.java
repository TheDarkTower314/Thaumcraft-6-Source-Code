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
        inGame = false;
        inGame = Minecraft.getMinecraft().inGameHasFocus;
    }
    
    public FXGenericGui(final World world, final double x, final double y, final double z, final double xx, final double yy, final double zz) {
        super(world, x, y, z, xx, yy, zz);
        inGame = false;
        inGame = Minecraft.getMinecraft().inGameHasFocus;
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!inGame && Minecraft.getMinecraft().inGameHasFocus) {
            setExpired();
        }
    }
    
    @Override
    public void draw(final BufferBuilder wr, final Entity entityIn, final float partialTicks, final float rotationX, final float rotationZ, final float rotationYZ, final float rotationXY, final float rotationXZ) {
        float tx1 = particleTextureIndexX / (float) gridSize;
        float tx2 = tx1 + 1.0f / gridSize;
        float ty1 = particleTextureIndexY / (float) gridSize;
        float ty2 = ty1 + 1.0f / gridSize;
        final float ts = 0.1f * particleScale;
        if (particleTexture != null) {
            tx1 = particleTexture.getMinU();
            tx2 = particleTexture.getMaxU();
            ty1 = particleTexture.getMinV();
            ty2 = particleTexture.getMaxV();
        }
        if (flipped) {
            final float t = tx1;
            tx1 = tx2;
            tx2 = t;
        }
        final float fs = MathHelper.clamp((particleAge + partialTicks) / particleMaxAge, 0.0f, 1.0f);
        final float pr = particleRed + (dr - particleRed) * fs;
        final float pg = particleGreen + (dg - particleGreen) * fs;
        final float pb = particleBlue + (db - particleBlue) * fs;
        final int i = getBrightnessForRender(partialTicks);
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        final float f5 = (float)(prevPosX + (posX - prevPosX) * partialTicks);
        final float f6 = (float)(prevPosY + (posY - prevPosY) * partialTicks);
        final float f7 = (float)(prevPosZ + (posZ - prevPosZ) * partialTicks);
        GL11.glPushMatrix();
        GL11.glTranslatef(f5, f6, -90.0f + f7);
        if (angled) {
            GL11.glRotatef(-angleYaw + 90.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(anglePitch + 90.0f, 1.0f, 0.0f, 0.0f);
        }
        if (particleAngle != 0.0f) {
            final float f8 = particleAngle + (particleAngle - prevParticleAngle) * partialTicks;
            GL11.glRotated(f8 * 57.29577951308232, 0.0, 0.0, 1.0);
        }
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        wr.pos(-ts, -ts, 0.0).tex(tx2, ty2).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
        wr.pos(-ts, ts, 0.0).tex(tx2, ty1).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
        wr.pos(ts, ts, 0.0).tex(tx1, ty1).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
        wr.pos(ts, -ts, 0.0).tex(tx1, ty2).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
        Tessellator.getInstance().draw();
        GL11.glPopMatrix();
    }
}
