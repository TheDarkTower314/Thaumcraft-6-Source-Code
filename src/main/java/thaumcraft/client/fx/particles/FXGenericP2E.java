package thaumcraft.client.fx.particles;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;


public class FXGenericP2E extends FXGeneric
{
    private Entity target;
    
    public FXGenericP2E(World world, double x, double y, double z, Entity target) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        setSize(0.1f, 0.1f);
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        this.target = target;
        double dx = target.posX - posX;
        double dy = target.posY - posY;
        double dz = target.posZ - posZ;
        int base = (int)(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 5.0f);
        if (base < 1) {
            base = 1;
        }
        particleMaxAge = base;
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
        particleTextureJitterX = 0.0f;
        particleTextureJitterY = 0.0f;
        float f3 = 0.01f;
        motionX = (float)world.rand.nextGaussian() * f3;
        motionY = (float)world.rand.nextGaussian() * f3;
        motionZ = (float)world.rand.nextGaussian() * f3;
        particleGravity = 0.2f;
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        double dx = target.posX - posX;
        double dy = target.posY - posY;
        double dz = target.posZ - posZ;
        double d13 = 0.3;
        double d14 = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        if (d14 < 4.0) {
            particleScale *= 0.9f;
            d13 = 0.6;
        }
        if (d14 < 0.25) {
            setExpired();
        }
        dx /= d14;
        dy /= d14;
        dz /= d14;
        motionX += dx * d13;
        motionY += dy * d13;
        motionZ += dz * d13;
        motionX = MathHelper.clamp((float) motionX, -0.35f, 0.35f);
        motionY = MathHelper.clamp((float) motionY, -0.35f, 0.35f);
        motionZ = MathHelper.clamp((float) motionZ, -0.35f, 0.35f);
    }
}
