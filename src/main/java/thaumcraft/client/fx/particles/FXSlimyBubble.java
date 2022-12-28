package thaumcraft.client.fx.particles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;


public class FXSlimyBubble extends Particle
{
    int particle;
    
    public FXSlimyBubble(World world, double d, double d1, double d2, float f) {
        super(world, d, d1, d2, 0.0, 0.0, 0.0);
        particle = 144;
        particleRed = 1.0f;
        particleGreen = 1.0f;
        particleBlue = 1.0f;
        particleGravity = 0.0f;
        double motionX = 0.0;
        motionZ = motionX;
        motionY = motionX;
        this.motionX = motionX;
        particleScale = f;
        particleMaxAge = 15 + world.rand.nextInt(5);
        setSize(0.01f, 0.01f);
    }
    
    public void renderParticle(BufferBuilder wr, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, particleAlpha);
        float var8 = particle % 16 / 64.0f;
        float var9 = var8 + 0.015625f;
        float var10 = particle / 16 / 64.0f;
        float var11 = var10 + 0.015625f;
        float var12 = particleScale;
        float var13 = (float)(prevPosX + (posX - prevPosX) * f - FXSlimyBubble.interpPosX);
        float var14 = (float)(prevPosY + (posY - prevPosY) * f - FXSlimyBubble.interpPosY);
        float var15 = (float)(prevPosZ + (posZ - prevPosZ) * f - FXSlimyBubble.interpPosZ);
        int i = getBrightnessForRender(f);
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        wr.pos(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12).tex(var9, var11).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(j, k).endVertex();
        wr.pos(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12).tex(var9, var10).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12).tex(var8, var10).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12).tex(var8, var11).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(j, k).endVertex();
    }
    
    public int getFXLayer() {
        return 1;
    }
    
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (particleAge++ >= particleMaxAge) {
            setExpired();
        }
        if (particleAge - 1 < 6) {
            particle = 144 + particleAge / 2;
            if (particleAge == 5) {
                posY += 0.1;
            }
        }
        else if (particleAge < particleMaxAge - 4) {
            motionY += 0.005;
            particle = 147 + particleAge % 4 / 2;
        }
        else {
            motionY /= 2.0;
            particle = 150 - (particleMaxAge - particleAge) / 2;
        }
        posY += motionY;
    }
}
