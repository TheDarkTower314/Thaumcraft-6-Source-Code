package thaumcraft.client.fx.particles;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;


public class FXGenericP2P extends FXGeneric
{
    private double targetX;
    private double targetY;
    private double targetZ;
    
    public FXGenericP2P(World world, double x, double y, double z, double xx, double yy, double zz) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        setSize(0.1f, 0.1f);
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        targetX = xx;
        targetY = yy;
        targetZ = zz;
        double dx = xx - posX;
        double dy = yy - posY;
        double dz = zz - posZ;
        int base = (int)(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 3.0f);
        if (base < 1) {
            base = 1;
        }
        particleMaxAge = base / 2 + rand.nextInt(base);
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
        double dx = targetX - posX;
        double dy = targetY - posY;
        double dz = targetZ - posZ;
        double d13 = 0.3;
        double d14 = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        if (d14 < 4.0) {
            particleScale *= 0.9f;
            d13 = 0.6;
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
