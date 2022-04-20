// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.particles;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;

public class FXGenericP2E extends FXGeneric
{
    private Entity target;
    
    public FXGenericP2E(final World world, final double x, final double y, final double z, final Entity target) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.setSize(0.1f, 0.1f);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.target = target;
        final double dx = target.posX - this.posX;
        final double dy = target.posY - this.posY;
        final double dz = target.posZ - this.posZ;
        int base = (int)(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 5.0f);
        if (base < 1) {
            base = 1;
        }
        this.particleMaxAge = base;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.particleTextureJitterX = 0.0f;
        this.particleTextureJitterY = 0.0f;
        final float f3 = 0.01f;
        this.motionX = (float)world.rand.nextGaussian() * f3;
        this.motionY = (float)world.rand.nextGaussian() * f3;
        this.motionZ = (float)world.rand.nextGaussian() * f3;
        this.particleGravity = 0.2f;
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        double dx = this.target.posX - this.posX;
        double dy = this.target.posY - this.posY;
        double dz = this.target.posZ - this.posZ;
        double d13 = 0.3;
        final double d14 = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        if (d14 < 4.0) {
            this.particleScale *= 0.9f;
            d13 = 0.6;
        }
        if (d14 < 0.25) {
            this.setExpired();
        }
        dx /= d14;
        dy /= d14;
        dz /= d14;
        this.motionX += dx * d13;
        this.motionY += dy * d13;
        this.motionZ += dz * d13;
        this.motionX = MathHelper.clamp((float)this.motionX, -0.35f, 0.35f);
        this.motionY = MathHelper.clamp((float)this.motionY, -0.35f, 0.35f);
        this.motionZ = MathHelper.clamp((float)this.motionZ, -0.35f, 0.35f);
    }
}
