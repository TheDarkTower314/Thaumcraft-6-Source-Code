// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.projectile;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.projectile.EntityThrowable;

public class EntityGolemOrb extends EntityThrowable implements IEntityAdditionalSpawnData
{
    int targetID;
    EntityLivingBase target;
    public boolean red;
    
    public EntityGolemOrb(final World par1World) {
        super(par1World);
        this.targetID = 0;
        this.red = false;
    }
    
    public EntityGolemOrb(final World par1World, final EntityLivingBase par2EntityLiving, final EntityLivingBase t, final boolean r) {
        super(par1World, par2EntityLiving);
        this.targetID = 0;
        this.red = false;
        this.target = t;
        this.red = r;
    }
    
    protected float getGravityVelocity() {
        return 0.0f;
    }
    
    public void writeSpawnData(final ByteBuf data) {
        int id = -1;
        if (this.target != null) {
            id = this.target.getEntityId();
        }
        data.writeInt(id);
        data.writeBoolean(this.red);
    }
    
    public void readSpawnData(final ByteBuf data) {
        final int id = data.readInt();
        try {
            if (id >= 0) {
                this.target = (EntityLivingBase)this.world.getEntityByID(id);
            }
        }
        catch (final Exception ex) {}
        this.red = data.readBoolean();
    }
    
    protected void onImpact(final RayTraceResult mop) {
        if (!this.world.isRemote && this.getThrower() != null && mop.typeOfHit == RayTraceResult.Type.ENTITY) {
            mop.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.getThrower()), (float)this.getThrower().getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * (this.red ? 1.0f : 0.6f));
        }
        this.playSound(SoundsTC.shock, 1.0f, 1.0f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f);
        if (this.world.isRemote) {
            FXDispatcher.INSTANCE.burst(this.posX, this.posY, this.posZ, 1.0f);
        }
        this.setDead();
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted > (this.red ? 240 : 160)) {
            this.setDead();
        }
        if (this.target != null) {
            final double d = this.getDistanceSq(this.target);
            double dx = this.target.posX - this.posX;
            double dy = this.target.getEntityBoundingBox().minY + this.target.height * 0.6 - this.posY;
            double dz = this.target.posZ - this.posZ;
            final double d2 = 0.2;
            dx /= d;
            dy /= d;
            dz /= d;
            this.motionX += dx * d2;
            this.motionY += dy * d2;
            this.motionZ += dz * d2;
            this.motionX = MathHelper.clamp((float)this.motionX, -0.25f, 0.25f);
            this.motionY = MathHelper.clamp((float)this.motionY, -0.25f, 0.25f);
            this.motionZ = MathHelper.clamp((float)this.motionZ, -0.25f, 0.25f);
        }
    }
    
    public boolean attackEntityFrom(final DamageSource source, final float damage) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        }
        if (source.getTrueSource() != null) {
            final Vec3d vec3 = source.getTrueSource().getLookVec();
            if (vec3 != null) {
                this.motionX = vec3.x;
                this.motionY = vec3.y;
                this.motionZ = vec3.z;
                this.motionX *= 0.9;
                this.motionY *= 0.9;
                this.motionZ *= 0.9;
                this.playSound(SoundsTC.zap, 1.0f, 1.0f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f);
            }
            return true;
        }
        return false;
    }
}
