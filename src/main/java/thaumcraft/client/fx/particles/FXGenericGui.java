package thaumcraft.client.fx.particles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;


public class FXGenericGui extends FXGeneric
{
    boolean inGame;
    
    public FXGenericGui(World world, double x, double y, double z) {
        super(world, x, y, z);
        inGame = false;
        inGame = Minecraft.getMinecraft().inGameHasFocus;
    }
    
    public FXGenericGui(World world, double x, double y, double z, double xx, double yy, double zz) {
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
    public void draw(BufferBuilder wr, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float tx1 = particleTextureIndexX / (float) gridSize;
        float tx2 = tx1 + 1.0f / gridSize;
        float ty1 = particleTextureIndexY / (float) gridSize;
        float ty2 = ty1 + 1.0f / gridSize;
        float ts = 0.1f * particleScale;
        if (particleTexture != null) {
            tx1 = particleTexture.getMinU();
            tx2 = particleTexture.getMaxU();
            ty1 = particleTexture.getMinV();
            ty2 = particleTexture.getMaxV();
        }
        if (flipped) {
            float t = tx1;
            tx1 = tx2;
            tx2 = t;
        }
        float fs = MathHelper.clamp((particleAge + partialTicks) / particleMaxAge, 0.0f, 1.0f);
        float pr = particleRed + (dr - particleRed) * fs;
        float pg = particleGreen + (dg - particleGreen) * fs;
        float pb = particleBlue + (db - particleBlue) * fs;
        int i = getBrightnessForRender(partialTicks);
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        float f5 = (float)(prevPosX + (posX - prevPosX) * partialTicks);
        float f6 = (float)(prevPosY + (posY - prevPosY) * partialTicks);
        float f7 = (float)(prevPosZ + (posZ - prevPosZ) * partialTicks);
        GL11.glPushMatrix();
        GL11.glTranslatef(f5, f6, -90.0f + f7);
        if (angled) {
            GL11.glRotatef(-angleYaw + 90.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(anglePitch + 90.0f, 1.0f, 0.0f, 0.0f);
        }
        if (particleAngle != 0.0f) {
            float f8 = particleAngle + (particleAngle - prevParticleAngle) * partialTicks;
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
