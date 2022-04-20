// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.projectile;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import java.util.Iterator;
import java.util.List;
import thaumcraft.common.lib.utils.EntityUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.client.lib.UtilsFX;
import java.util.ArrayList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.projectile.EntityThrowable;

public class EntityHomingShard extends EntityThrowable implements IEntityAdditionalSpawnData
{
    Class tclass;
    boolean persistant;
    int targetID;
    EntityLivingBase target;
    private static final DataParameter<Byte> STRENGTH;
    public ArrayList<UtilsFX.Vector> vl;
    
    public EntityHomingShard(final World par1World) {
        super(par1World);
        this.tclass = null;
        this.persistant = false;
        this.targetID = 0;
        this.vl = new ArrayList<UtilsFX.Vector>();
    }
    
    public EntityHomingShard(final World par1World, final EntityLivingBase p, final EntityLivingBase t, final int strength, final boolean b) {
        super(par1World, p);
        this.tclass = null;
        this.persistant = false;
        this.targetID = 0;
        this.vl = new ArrayList<UtilsFX.Vector>();
        this.target = t;
        this.tclass = t.getClass();
        this.persistant = b;
        this.setStrength(strength);
        final Vec3d v = p.getLookVec();
        this.setLocationAndAngles(p.posX + v.x / 2.0, p.posY + p.getEyeHeight() + v.y / 2.0, p.posZ + v.z / 2.0, p.rotationYaw, p.rotationPitch);
        final float f = 0.5f;
        final float ry = p.rotationYaw + (this.rand.nextFloat() - this.rand.nextFloat()) * 60.0f;
        final float rp = p.rotationPitch + (this.rand.nextFloat() - this.rand.nextFloat()) * 60.0f;
        this.motionX = -MathHelper.sin(ry / 180.0f * 3.1415927f) * MathHelper.cos(rp / 180.0f * 3.1415927f) * f;
        this.motionZ = MathHelper.cos(ry / 180.0f * 3.1415927f) * MathHelper.cos(rp / 180.0f * 3.1415927f) * f;
        this.motionY = -MathHelper.sin(rp / 180.0f * 3.1415927f) * f;
    }
    
    public void entityInit() {
        super.entityInit();
        this.getDataManager().register((DataParameter)EntityHomingShard.STRENGTH, 0);
    }
    
    public void setStrength(final int str) {
        this.getDataManager().set(EntityHomingShard.STRENGTH, (byte)str);
    }
    
    public int getStrength() {
        return (byte)this.getDataManager().get((DataParameter)EntityHomingShard.STRENGTH);
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
    }
    
    public void readSpawnData(final ByteBuf data) {
        final int id = data.readInt();
        try {
            if (id >= 0) {
                this.target = (EntityLivingBase)this.world.getEntityByID(id);
            }
        }
        catch (final Exception ex) {}
    }
    
    protected void onImpact(final RayTraceResult mop) {
        if (!this.world.isRemote && mop.typeOfHit == RayTraceResult.Type.ENTITY && (this.getThrower() == null || (this.getThrower() != null && mop.entityHit != this.getThrower()))) {
            mop.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.getThrower()), 1.0f + this.getStrength() * 0.5f);
            this.playSound(SoundsTC.zap, 1.0f, 1.0f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f);
            this.world.setEntityState(this, (byte)16);
            this.setDead();
        }
        if (mop.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (mop.sideHit.getFrontOffsetZ() != 0) {
                this.motionZ *= -0.8;
            }
            if (mop.sideHit.getFrontOffsetX() != 0) {
                this.motionX *= -0.8;
            }
            if (mop.sideHit.getFrontOffsetY() != 0) {
                this.motionY *= -0.8;
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(final byte par1) {
        if (par1 == 16) {
            FXDispatcher.INSTANCE.burst(this.posX, this.posY, this.posZ, 0.3f);
        }
        else {
            super.handleStatusUpdate(par1);
        }
    }
    
    public void onUpdate() {
        this.vl.add(0, new UtilsFX.Vector((float)this.lastTickPosX, (float)this.lastTickPosY, (float)this.lastTickPosZ));
        if (this.vl.size() > 6) {
            this.vl.remove(this.vl.size() - 1);
        }
        super.onUpdate();
        if (!this.world.isRemote) {
            if (this.persistant && (this.target == null || this.target.isDead || this.target.getDistanceSq(this) > 1250.0)) {
                final List<Entity> es = EntityUtils.getEntitiesInRange(this.world, this.posX, this.posY, this.posZ, this, (Class<? extends Entity>)this.tclass, 16.0);
                for (final Entity e : es) {
                    if (e instanceof EntityLivingBase && !e.isDead && (this.getThrower() == null || e.getEntityId() != this.getThrower().getEntityId())) {
                        this.target = (EntityLivingBase)e;
                        break;
                    }
                }
            }
            if (this.target == null || this.target.isDead) {
                this.world.setEntityState(this, (byte)16);
                this.setDead();
            }
        }
        if (this.ticksExisted > 300) {
            this.world.setEntityState(this, (byte)16);
            this.setDead();
        }
        if (this.ticksExisted % 20 == 0 && this.target != null && !this.target.isDead) {
            final double d = this.getDistance(this.target);
            double dx = this.target.posX - this.posX;
            double dy = this.target.getEntityBoundingBox().minY + this.target.height * 0.6 - this.posY;
            double dz = this.target.posZ - this.posZ;
            dx /= d;
            dy /= d;
            dz /= d;
            this.motionX = dx;
            this.motionY = dy;
            this.motionZ = dz;
        }
        this.motionX *= 0.85;
        this.motionY *= 0.85;
        this.motionZ *= 0.85;
    }
    
    static {
        STRENGTH = EntityDataManager.createKey(EntityHomingShard.class, DataSerializers.BYTE);
    }
}
