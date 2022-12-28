package thaumcraft.client.fx.particles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;


public class FXSmokeSpiral extends Particle
{
    private float radius;
    private int start;
    private int miny;
    
    public FXSmokeSpiral(World world, double d, double d1, double d2, float radius, int start, int miny) {
        super(world, d, d1, d2, 0.0, 0.0, 0.0);
        this.radius = 1.0f;
        this.start = 0;
        this.miny = 0;
        particleGravity = -0.01f;
        double motionX = 0.0;
        motionZ = motionX;
        motionY = motionX;
        this.motionX = motionX;
        particleScale *= 1.0f;
        particleMaxAge = 20 + world.rand.nextInt(10);
        setSize(0.01f, 0.01f);
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        this.radius = radius;
        this.start = start;
        this.miny = miny;
    }
    
    public void renderParticle(BufferBuilder wr, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.66f * particleAlpha);
        int particle = (int)(1.0f + particleAge / (float) particleMaxAge * 4.0f);
        float r1 = start + 720.0f * ((particleAge + f) / particleMaxAge);
        float r2 = 90.0f - 180.0f * ((particleAge + f) / particleMaxAge);
        float mX = -MathHelper.sin(r1 / 180.0f * 3.1415927f) * MathHelper.cos(r2 / 180.0f * 3.1415927f);
        float mZ = MathHelper.cos(r1 / 180.0f * 3.1415927f) * MathHelper.cos(r2 / 180.0f * 3.1415927f);
        float mY = -MathHelper.sin(r2 / 180.0f * 3.1415927f);
        mX *= radius;
        mY *= radius;
        mZ *= radius;
        float var8 = particle % 16 / 64.0f;
        float var9 = var8 + 0.015625f;
        float var10 = particle / 16 / 64.0f;
        float var11 = var10 + 0.015625f;
        float var12 = 0.15f * particleScale;
        float var13 = (float)(posX + mX - FXSmokeSpiral.interpPosX);
        float var14 = (float)(Math.max(posY + mY, miny + 0.1f) - FXSmokeSpiral.interpPosY);
        float var15 = (float)(posZ + mZ - FXSmokeSpiral.interpPosZ);
        float var16 = 1.0f;
        int i = getBrightnessForRender(f);
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        wr.pos(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12).tex(var9, var11).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 0.66f * particleAlpha).lightmap(j, k).endVertex();
        wr.pos(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12).tex(var9, var10).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 0.66f * particleAlpha).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12).tex(var8, var10).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 0.66f * particleAlpha).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12).tex(var8, var11).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 0.66f * particleAlpha).lightmap(j, k).endVertex();
    }
    
    public int getFXLayer() {
        return 1;
    }
    
    public void onUpdate() {
        setAlphaF((particleMaxAge - particleAge) / (float) particleMaxAge);
        if (particleAge++ >= particleMaxAge) {
            setExpired();
        }
    }
}
