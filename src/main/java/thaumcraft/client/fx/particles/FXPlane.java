package thaumcraft.client.fx.particles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;


public class FXPlane extends Particle
{
    float angle;
    float angleYaw;
    float anglePitch;
    
    public FXPlane(World world, double d, double d1, double d2, double m, double m1, double m2, int life) {
        super(world, d, d1, d2, 0.0, 0.0, 0.0);
        particleRed = 1.0f;
        particleGreen = 1.0f;
        particleBlue = 1.0f;
        particleGravity = 0.0f;
        double motionX = 0.0;
        motionZ = motionX;
        motionY = motionX;
        this.motionX = motionX;
        particleMaxAge = life;
        setSize(0.01f, 0.01f);
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        particleScale = 1.0f;
        particleAlpha = 0.0f;
        double dx = m - posX;
        double dy = m1 - posY;
        double dz = m2 - posZ;
        this.motionX = dx / particleMaxAge;
        motionY = dy / particleMaxAge;
        motionZ = dz / particleMaxAge;
        particleTextureIndexX = 22;
        particleTextureIndexY = 10;
        double d3 = MathHelper.sqrt(dx * dx + dz * dz);
        angleYaw = 0.0f;
        anglePitch = 0.0f;
        if (d3 >= 1.0E-7) {
            angleYaw = (float)(MathHelper.atan2(dz, dx) * 180.0 / 3.141592653589793) - 90.0f;
            anglePitch = (float)(-(MathHelper.atan2(dy, d3) * 180.0 / 3.141592653589793));
        }
        angle = (float)(rand.nextGaussian() * 20.0);
    }
    
    public int getFXLayer() {
        return 0;
    }
    
    public void renderParticle(BufferBuilder wr, Entity p_180434_2_, float f, float f1, float f2, float f3, float f4, float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, particleAlpha / 2.0f);
        float var13 = (float)(prevPosX + (posX - prevPosX) * f - FXPlane.interpPosX);
        float var14 = (float)(prevPosY + (posY - prevPosY) * f - FXPlane.interpPosY);
        float var15 = (float)(prevPosZ + (posZ - prevPosZ) * f - FXPlane.interpPosZ);
        GL11.glTranslated(var13, var14, var15);
        GL11.glRotatef(-angleYaw + 90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(anglePitch + 90.0f, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(angle, 0.0f, 1.0f, 0.0f);
        particleTextureIndexX = 22 + Math.round((particleAge + f) / particleMaxAge * 8.0f);
        float var16 = particleTextureIndexX / 32.0f;
        float var17 = var16 + 0.03125f;
        float var18 = particleTextureIndexY / 32.0f;
        float var19 = var18 + 0.03125f;
        float var20 = particleScale * (0.5f + (particleAge + f) / particleMaxAge);
        float var21 = 1.0f;
        int i = 240;
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        GL11.glDisable(2884);
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        wr.pos(-0.5 * var20, 0.5 * var20, 0.0).tex(var17, var19).color(particleRed * var21, particleGreen * var21, particleBlue * var21, particleAlpha / 2.0f).lightmap(j, k).endVertex();
        wr.pos(0.5 * var20, 0.5 * var20, 0.0).tex(var17, var18).color(particleRed * var21, particleGreen * var21, particleBlue * var21, particleAlpha / 2.0f).lightmap(j, k).endVertex();
        wr.pos(0.5 * var20, -0.5 * var20, 0.0).tex(var16, var18).color(particleRed * var21, particleGreen * var21, particleBlue * var21, particleAlpha / 2.0f).lightmap(j, k).endVertex();
        wr.pos(-0.5 * var20, -0.5 * var20, 0.0).tex(var16, var19).color(particleRed * var21, particleGreen * var21, particleBlue * var21, particleAlpha / 2.0f).lightmap(j, k).endVertex();
        Tessellator.getInstance().draw();
        GL11.glEnable(2884);
        GL11.glPopMatrix();
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }
    
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        float threshold = particleMaxAge / 5.0f;
        if (particleAge <= threshold) {
            particleAlpha = particleAge / threshold;
        }
        else {
            particleAlpha = (particleMaxAge - particleAge) / (float) particleMaxAge;
        }
        if (particleAge++ >= particleMaxAge) {
            setExpired();
        }
        posX += motionX;
        posY += motionY;
        posZ += motionZ;
    }
    
    public void setGravity(float value) {
        particleGravity = value;
    }
}
