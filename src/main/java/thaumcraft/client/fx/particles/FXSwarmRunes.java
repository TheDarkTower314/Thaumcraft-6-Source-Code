package thaumcraft.client.fx.particles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;


public class FXSwarmRunes extends Particle
{
    private Entity target;
    private float turnSpeed;
    private float speed;
    int deathtimer;
    float rotationPitch;
    float rotationYaw;
    public int particle;
    
    public FXSwarmRunes(World par1World, double x, double y, double z, Entity target, float r, float g, float b) {
        super(par1World, x, y, z, 0.0, 0.0, 0.0);
        turnSpeed = 10.0f;
        speed = 0.2f;
        deathtimer = 0;
        particle = 0;
        particleRed = r;
        particleGreen = g;
        particleBlue = b;
        particleScale = rand.nextFloat() * 0.5f + 1.0f;
        this.target = target;
        float f3 = 0.2f;
        motionX = (rand.nextFloat() - rand.nextFloat()) * f3;
        motionY = (rand.nextFloat() - rand.nextFloat()) * f3;
        motionZ = (rand.nextFloat() - rand.nextFloat()) * f3;
        particleGravity = 0.1f;
    }
    
    public FXSwarmRunes(World par1World, double x, double y, double z, Entity target, float r, float g, float b, float sp, float ts, float pg) {
        this(par1World, x, y, z, target, r, g, b);
        speed = sp;
        turnSpeed = ts;
        particleGravity = pg;
        particle = rand.nextInt(16);
    }
    
    public void renderParticle(BufferBuilder wr, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        float bob = MathHelper.sin(particleAge / 3.0f) * 0.25f + 1.0f;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.75f);
        float var8 = particle / 64.0f;
        float var9 = var8 + 0.015625f;
        float var10 = 0.09375f;
        float var11 = var10 + 0.015625f;
        float var12 = 0.07f * particleScale * bob;
        float var13 = (float)(prevPosX + (posX - prevPosX) * f - FXSwarmRunes.interpPosX);
        float var14 = (float)(prevPosY + (posY - prevPosY) * f - FXSwarmRunes.interpPosY);
        float var15 = (float)(prevPosZ + (posZ - prevPosZ) * f - FXSwarmRunes.interpPosZ);
        float var16 = 1.0f;
        float trans = (50.0f - deathtimer) / 50.0f * 0.66f;
        int i = 240;
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        wr.pos(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12).tex(var9, var11).color(particleRed * var16, particleGreen * var16, particleBlue * var16, trans).lightmap(j, k).endVertex();
        wr.pos(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12).tex(var9, var10).color(particleRed * var16, particleGreen * var16, particleBlue * var16, trans).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12).tex(var8, var10).color(particleRed * var16, particleGreen * var16, particleBlue * var16, trans).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12).tex(var8, var11).color(particleRed * var16, particleGreen * var16, particleBlue * var16, trans).lightmap(j, k).endVertex();
    }
    
    public int getFXLayer() {
        return 0;
    }
    
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        ++particleAge;
        if (particleAge > 200 || target == null || target.isDead || (target instanceof EntityLivingBase && ((EntityLivingBase) target).deathTime > 0)) {
            ++deathtimer;
            motionX *= 0.9;
            motionZ *= 0.9;
            motionY -= particleGravity / 2.0f;
            if (deathtimer > 50) {
                setExpired();
            }
        }
        else {
            motionY += particleGravity;
        }
        move(motionX, motionY, motionZ);
        motionX *= 0.985;
        motionY *= 0.985;
        motionZ *= 0.985;
        if (particleAge < 200 && target != null && !target.isDead && (!(target instanceof EntityLivingBase) || ((EntityLivingBase) target).deathTime <= 0)) {
            boolean hurt = false;
            if (target instanceof EntityLivingBase) {
                hurt = (((EntityLivingBase) target).hurtTime > 0);
            }
            Vec3d v1 = new Vec3d(posX, posY, posZ);
            if (v1.squareDistanceTo(target.posX, target.posY, target.posZ) > target.width * target.width && !hurt) {
                faceEntity(target, turnSpeed / 2.0f + rand.nextInt((int)(turnSpeed / 2.0f)), turnSpeed / 2.0f + rand.nextInt((int)(turnSpeed / 2.0f)));
            }
            else {
                if (hurt && v1.squareDistanceTo(target.posX, target.posY, target.posZ) < target.width * target.width) {
                    particleAge += 100;
                }
                faceEntity(target, -(turnSpeed / 2.0f + rand.nextInt((int)(turnSpeed / 2.0f))), -(turnSpeed / 2.0f + rand.nextInt((int)(turnSpeed / 2.0f))));
            }
            motionX = -MathHelper.sin(rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(rotationPitch / 180.0f * 3.1415927f);
            motionZ = MathHelper.cos(rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(rotationPitch / 180.0f * 3.1415927f);
            motionY = -MathHelper.sin(rotationPitch / 180.0f * 3.1415927f);
            setHeading(motionX, motionY, motionZ, speed, 15.0f);
        }
    }
    
    public void faceEntity(Entity par1Entity, float par2, float par3) {
        double d0 = par1Entity.posX - posX;
        double d2 = par1Entity.posZ - posZ;
        double d3 = (par1Entity.getEntityBoundingBox().minY + par1Entity.getEntityBoundingBox().maxY) / 2.0 - (getBoundingBox().minY + getBoundingBox().maxY) / 2.0;
        double d4 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        float f2 = (float)(Math.atan2(d2, d0) * 180.0 / 3.141592653589793) - 90.0f;
        float f3 = (float)(-(Math.atan2(d3, d4) * 180.0 / 3.141592653589793));
        rotationPitch = updateRotation(rotationPitch, f3, par3);
        rotationYaw = updateRotation(rotationYaw, f2, par2);
    }
    
    private float updateRotation(float par1, float par2, float par3) {
        float f3 = MathHelper.wrapDegrees(par2 - par1);
        if (f3 > par3) {
            f3 = par3;
        }
        if (f3 < -par3) {
            f3 = -par3;
        }
        return par1 + f3;
    }
    
    public void setHeading(double par1, double par3, double par5, float par7, float par8) {
        float f2 = MathHelper.sqrt(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= f2;
        par3 /= f2;
        par5 /= f2;
        par1 += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * par8;
        par3 += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * par8;
        par5 += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * par8;
        par1 *= par7;
        par3 *= par7;
        par5 *= par7;
        motionX = par1;
        motionY = par3;
        motionZ = par5;
    }
}
