package thaumcraft.client.fx.particles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;


public class FXWispEG extends Particle
{
    Entity target;
    double rx;
    double ry;
    double rz;
    public int blendmode;
    
    public FXWispEG(World world, double posX, double posY, double posZ, Entity target2) {
        super(world, posX, posY, posZ, 0.0, 0.0, 0.0);
        target = null;
        rx = 0.0;
        ry = 0.0;
        rz = 0.0;
        blendmode = 1;
        target = target2;
        motionX = rand.nextGaussian() * 0.03;
        motionY = -0.05;
        motionZ = rand.nextGaussian() * 0.03;
        particleScale *= 0.4f;
        particleMaxAge = (int)(40.0 / (Math.random() * 0.3 + 0.7));
        setSize(0.01f, 0.01f);
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        blendmode = 771;
        particleRed = rand.nextFloat() * 0.05f;
        particleGreen = rand.nextFloat() * 0.05f;
        particleBlue = rand.nextFloat() * 0.05f;
    }
    
    public void renderParticle(BufferBuilder wr, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        Entity e = Minecraft.getMinecraft().getRenderViewEntity();
        float agescale = 1.0f - particleAge / (float) particleMaxAge;
        float d6 = 1024.0f;
        double dist = new Vec3d(e.posX, e.posY, e.posZ).squareDistanceTo(new Vec3d(posX, posY, posZ));
        float base = (float)(1.0 - Math.min(d6, dist) / d6);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.75f * base);
        float f6 = 0.5f * particleScale;
        float f7 = (float)(prevPosX + (posX - prevPosX) * f - FXWispEG.interpPosX);
        float f8 = (float)(prevPosY + (posY - prevPosY) * f - FXWispEG.interpPosY);
        float f9 = (float)(prevPosZ + (posZ - prevPosZ) * f - FXWispEG.interpPosZ);
        float var8 = particleAge % 13 / 64.0f;
        float var9 = var8 + 0.015625f;
        float var10 = 0.046875f;
        float var11 = var10 + 0.015625f;
        int i = 240;
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        wr.pos(f7 - f1 * f6 - f4 * f6, f8 - f2 * f6, f9 - f3 * f6 - f5 * f6).tex(var9, var11).color(particleRed, particleGreen, particleBlue, 0.2f * agescale * base).lightmap(j, k).endVertex();
        wr.pos(f7 - f1 * f6 + f4 * f6, f8 + f2 * f6, f9 - f3 * f6 + f5 * f6).tex(var9, var10).color(particleRed, particleGreen, particleBlue, 0.2f * agescale * base).lightmap(j, k).endVertex();
        wr.pos(f7 + f1 * f6 + f4 * f6, f8 + f2 * f6, f9 + f3 * f6 + f5 * f6).tex(var8, var10).color(particleRed, particleGreen, particleBlue, 0.2f * agescale * base).lightmap(j, k).endVertex();
        wr.pos(f7 + f1 * f6 - f4 * f6, f8 - f2 * f6, f9 + f3 * f6 - f5 * f6).tex(var8, var11).color(particleRed, particleGreen, particleBlue, 0.2f * agescale * base).lightmap(j, k).endVertex();
    }
    
    public int getFXLayer() {
        return (blendmode != 1) ? 1 : 0;
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (target != null && !onGround) {
            posX += target.motionX;
            posZ += target.motionZ;
        }
    }
}
