package thaumcraft.client.fx.particles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;


public class FXBoreSparkle extends Particle
{
    private Entity target;
    private double targetX;
    private double targetY;
    private double targetZ;
    public int particle;
    
    public FXBoreSparkle(World par1World, double par2, double par4, double par6, double tx, double ty, double tz) {
        super(par1World, par2, par4, par6, 0.0, 0.0, 0.0);
        particle = 24;
        float particleRed = 0.6f;
        particleBlue = particleRed;
        particleGreen = particleRed;
        this.particleRed = particleRed;
        particleScale = rand.nextFloat() * 0.5f + 0.5f;
        targetX = tx;
        targetY = ty;
        targetZ = tz;
        double dx = tx - posX;
        double dy = ty - posY;
        double dz = tz - posZ;
        int base = (int)(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 10.0f);
        if (base < 1) {
            base = 1;
        }
        particleMaxAge = base / 2 + rand.nextInt(base);
        float f3 = 0.01f;
        motionX = (float) rand.nextGaussian() * f3;
        motionY = (float) rand.nextGaussian() * f3;
        motionZ = (float) rand.nextGaussian() * f3;
        this.particleRed = 0.2f;
        particleGreen = 0.6f + rand.nextFloat() * 0.3f;
        particleBlue = 0.2f;
        particleGravity = 0.2f;
        Entity renderentity = FMLClientHandler.instance().getClient().getRenderViewEntity();
        int visibleDistance = 64;
        if (!FMLClientHandler.instance().getClient().gameSettings.fancyGraphics) {
            visibleDistance = 32;
        }
        if (renderentity.getDistance(posX, posY, posZ) > visibleDistance) {
            particleMaxAge = 0;
        }
    }
    
    public FXBoreSparkle(World par1World, double par2, double par4, double par6, Entity t) {
        this(par1World, par2, par4, par6, t.posX, t.posY + t.getEyeHeight(), t.posZ);
        target = t;
    }
    
    public void renderParticle(BufferBuilder wr, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        float bob = MathHelper.sin(particleAge / 3.0f) * 0.5f + 1.0f;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.75f);
        int part = particleAge % 4;
        float var8 = part / 64.0f;
        float var9 = var8 + 0.015625f;
        float var10 = 0.0625f;
        float var11 = var10 + 0.015625f;
        float var12 = 0.1f * particleScale * bob;
        float var13 = (float)(prevPosX + (posX - prevPosX) * f - FXBoreSparkle.interpPosX);
        float var14 = (float)(prevPosY + (posY - prevPosY) * f - FXBoreSparkle.interpPosY);
        float var15 = (float)(prevPosZ + (posZ - prevPosZ) * f - FXBoreSparkle.interpPosZ);
        float var16 = 1.0f;
        int i = 240;
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        wr.pos(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12).tex(var9, var11).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 1.0f).lightmap(j, k).endVertex();
        wr.pos(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12).tex(var9, var10).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 1.0f).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12).tex(var8, var10).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 1.0f).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12).tex(var8, var11).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 1.0f).lightmap(j, k).endVertex();
    }
    
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (target != null) {
            targetX = target.posX;
            targetY = target.posY + target.getEyeHeight();
            targetZ = target.posZ;
        }
        if (particleAge++ >= particleMaxAge || (MathHelper.floor(posX) == MathHelper.floor(targetX) && MathHelper.floor(posY) == MathHelper.floor(targetY) && MathHelper.floor(posZ) == MathHelper.floor(targetZ))) {
            setExpired();
            return;
        }
        move(motionX, motionY, motionZ);
        motionX *= 0.985;
        motionY *= 0.95;
        motionZ *= 0.985;
        double dx = targetX - posX;
        double dy = targetY - posY;
        double dz = targetZ - posZ;
        double d11 = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        double clamp = Math.min(0.25, d11 / 15.0);
        if (d11 < 2.0) {
            particleScale *= 0.9f;
        }
        dx /= d11;
        dy /= d11;
        dz /= d11;
        motionX += dx * clamp;
        motionY += dy * clamp;
        motionZ += dz * clamp;
        motionX = MathHelper.clamp((float) motionX, -clamp, clamp);
        motionY = MathHelper.clamp((float) motionY, -clamp, clamp);
        motionZ = MathHelper.clamp((float) motionZ, -clamp, clamp);
        motionX += rand.nextGaussian() * 0.01;
        motionY += rand.nextGaussian() * 0.01;
        motionZ += rand.nextGaussian() * 0.01;
    }
    
    public void setGravity(float value) {
        particleGravity = value;
    }
}
