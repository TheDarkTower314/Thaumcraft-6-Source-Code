// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.particles;

import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.World;
import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.client.particle.Particle;

public class FXSwarm extends Particle
{
    private Entity target;
    private float turnSpeed;
    private float speed;
    int deathtimer;
    private static ArrayList<Long> buzzcount;
    float rotationPitch;
    float rotationYaw;
    public int particle;
    
    public FXSwarm(final World par1World, final double x, final double y, final double z, final Entity target, final float r, final float g, final float b) {
        super(par1World, x, y, z, 0.0, 0.0, 0.0);
        this.turnSpeed = 10.0f;
        this.speed = 0.2f;
        this.deathtimer = 0;
        this.particle = 40;
        this.particleRed = r;
        this.particleGreen = g;
        this.particleBlue = b;
        this.particleScale = this.rand.nextFloat() * 0.5f + 1.0f;
        this.target = target;
        final float f3 = 0.2f;
        this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * f3;
        this.motionY = (this.rand.nextFloat() - this.rand.nextFloat()) * f3;
        this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * f3;
        this.particleGravity = 0.1f;
    }
    
    public FXSwarm(final World par1World, final double x, final double y, final double z, final Entity target, final float r, final float g, final float b, final float sp, final float ts, final float pg) {
        this(par1World, x, y, z, target, r, g, b);
        this.speed = sp;
        this.turnSpeed = ts;
        this.particleGravity = pg;
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        final float bob = MathHelper.sin(this.particleAge / 3.0f) * 0.25f + 1.0f;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.75f);
        final int part = 7 + this.particleAge % 8;
        final float var8 = part / 64.0f;
        final float var9 = var8 + 0.015625f;
        final float var10 = 0.0625f;
        final float var11 = var10 + 0.015625f;
        final float var12 = 0.1f * this.particleScale * bob;
        final float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * f - FXSwarm.interpPosX);
        final float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * f - FXSwarm.interpPosY);
        final float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * f - FXSwarm.interpPosZ);
        final float var16 = 1.0f;
        final float trans = (50.0f - this.deathtimer) / 50.0f;
        final int i = 240;
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        float dd = 1.0f;
        if (this.target instanceof EntityLivingBase && ((EntityLivingBase)this.target).hurtTime > 0) {
            dd = 2.0f;
        }
        wr.pos(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12).tex(var9, var11).color(this.particleRed * var16, this.particleGreen * var16 / dd, this.particleBlue * var16 / dd, trans).lightmap(j, k).endVertex();
        wr.pos(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12).tex(var9, var10).color(this.particleRed * var16, this.particleGreen * var16 / dd, this.particleBlue * var16 / dd, trans).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12).tex(var8, var10).color(this.particleRed * var16, this.particleGreen * var16 / dd, this.particleBlue * var16 / dd, trans).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12).tex(var8, var11).color(this.particleRed * var16, this.particleGreen * var16 / dd, this.particleBlue * var16 / dd, trans).lightmap(j, k).endVertex();
    }
    
    public int getFXLayer() {
        return 1;
    }
    
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        ++this.particleAge;
        if (this.target == null || this.target.isDead || (this.target instanceof EntityLivingBase && ((EntityLivingBase)this.target).deathTime > 0)) {
            ++this.deathtimer;
            this.motionX *= 0.9;
            this.motionZ *= 0.9;
            this.motionY -= this.particleGravity / 2.0f;
            if (this.deathtimer > 50) {
                this.setExpired();
            }
        }
        else {
            this.motionY += this.particleGravity;
        }
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.985;
        this.motionY *= 0.985;
        this.motionZ *= 0.985;
        if (this.target != null && !this.target.isDead && (!(this.target instanceof EntityLivingBase) || ((EntityLivingBase)this.target).deathTime <= 0)) {
            boolean hurt = false;
            if (this.target instanceof EntityLivingBase) {
                hurt = (((EntityLivingBase)this.target).hurtTime > 0);
            }
            final Vec3d v1 = new Vec3d(this.posX, this.posY, this.posZ);
            if (v1.squareDistanceTo(this.target.posX, this.target.posY, this.target.posZ) > this.target.width && !hurt) {
                this.faceEntity(this.target, this.turnSpeed / 2.0f + this.rand.nextInt((int)(this.turnSpeed / 2.0f)), this.turnSpeed / 2.0f + this.rand.nextInt((int)(this.turnSpeed / 2.0f)));
            }
            else {
                this.faceEntity(this.target, -(this.turnSpeed / 2.0f + this.rand.nextInt((int)(this.turnSpeed / 2.0f))), -(this.turnSpeed / 2.0f + this.rand.nextInt((int)(this.turnSpeed / 2.0f))));
            }
            this.motionX = -MathHelper.sin(this.rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(this.rotationPitch / 180.0f * 3.1415927f);
            this.motionZ = MathHelper.cos(this.rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(this.rotationPitch / 180.0f * 3.1415927f);
            this.motionY = -MathHelper.sin(this.rotationPitch / 180.0f * 3.1415927f);
            this.setHeading(this.motionX, this.motionY, this.motionZ, this.speed, 15.0f);
        }
        if (FXSwarm.buzzcount.size() < 3 && this.rand.nextInt(50) == 0 && this.world.getClosestPlayer(this.posX, this.posY, this.posZ, 8.0, false) != null) {
            this.world.playSound(this.posX, this.posY, this.posZ, SoundsTC.fly, SoundCategory.HOSTILE, 0.03f, 0.5f + this.rand.nextFloat() * 0.4f, false);
            FXSwarm.buzzcount.add(System.nanoTime() + 1500000L);
        }
        if (FXSwarm.buzzcount.size() >= 3 && FXSwarm.buzzcount.get(0) < System.nanoTime()) {
            FXSwarm.buzzcount.remove(0);
        }
    }
    
    public void faceEntity(final Entity par1Entity, final float par2, final float par3) {
        final double d0 = par1Entity.posX - this.posX;
        final double d2 = par1Entity.posZ - this.posZ;
        final double d3 = (par1Entity.getEntityBoundingBox().minY + par1Entity.getEntityBoundingBox().maxY) / 2.0 - (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0;
        final double d4 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        final float f2 = (float)(Math.atan2(d2, d0) * 180.0 / 3.141592653589793) - 90.0f;
        final float f3 = (float)(-(Math.atan2(d3, d4) * 180.0 / 3.141592653589793));
        this.rotationPitch = this.updateRotation(this.rotationPitch, f3, par3);
        this.rotationYaw = this.updateRotation(this.rotationYaw, f2, par2);
    }
    
    private float updateRotation(final float par1, final float par2, final float par3) {
        float f3 = MathHelper.wrapDegrees(par2 - par1);
        if (f3 > par3) {
            f3 = par3;
        }
        if (f3 < -par3) {
            f3 = -par3;
        }
        return par1 + f3;
    }
    
    public void setHeading(double par1, double par3, double par5, final float par7, final float par8) {
        final float f2 = MathHelper.sqrt(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= f2;
        par3 /= f2;
        par5 /= f2;
        par1 += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * par8;
        par3 += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * par8;
        par5 += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * par8;
        par1 *= par7;
        par3 *= par7;
        par5 *= par7;
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;
    }
    
    static {
        FXSwarm.buzzcount = new ArrayList<Long>();
    }
}
