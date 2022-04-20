// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.projectile;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import thaumcraft.client.fx.FXDispatcher;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import thaumcraft.common.lib.utils.EntityUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.state.IBlockState;
import thaumcraft.common.lib.events.ServerEvents;
import thaumcraft.api.casters.FocusEngine;
import net.minecraft.util.math.Vec3d;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.api.casters.Trajectory;
import net.minecraft.world.World;
import thaumcraft.api.casters.FocusEffect;
import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataParameter;
import thaumcraft.api.casters.FocusPackage;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.projectile.EntityThrowable;

public class EntityFocusProjectile extends EntityThrowable implements IEntityAdditionalSpawnData
{
    FocusPackage focusPackage;
    private static final DataParameter<Integer> SPECIAL;
    private static final DataParameter<Integer> OWNER;
    boolean noTouchy;
    private Entity target;
    boolean firstParticle;
    public float lastRenderTick;
    FocusEffect[] effects;
    
    public EntityFocusProjectile(final World par1World) {
        super(par1World);
        this.noTouchy = false;
        this.firstParticle = false;
        this.lastRenderTick = 0.0f;
        this.effects = null;
        this.setSize(0.15f, 0.15f);
    }
    
    public EntityFocusProjectile(final FocusPackage pack, final float speed, final Trajectory trajectory, final int special) {
        super(pack.world, pack.getCaster());
        this.noTouchy = false;
        this.firstParticle = false;
        this.lastRenderTick = 0.0f;
        this.effects = null;
        this.focusPackage = pack;
        this.setPosition(trajectory.source.x + trajectory.direction.x * pack.getCaster().width * 2.1, trajectory.source.y + trajectory.direction.y * pack.getCaster().width * 2.1, trajectory.source.z + trajectory.direction.z * pack.getCaster().width * 2.1);
        this.shoot(trajectory.direction.x, trajectory.direction.y, trajectory.direction.z, speed, 0.0f);
        this.setSize(0.15f, 0.15f);
        this.setSpecial(special);
        this.ignoreEntity = pack.getCaster();
        this.setOwner(this.getThrower().getEntityId());
    }
    
    protected float getGravityVelocity() {
        return (this.getSpecial() > 1) ? 0.005f : 0.01f;
    }
    
    public void entityInit() {
        super.entityInit();
        this.getDataManager().register(EntityFocusProjectile.SPECIAL, 0);
        this.getDataManager().register(EntityFocusProjectile.OWNER, 0);
    }
    
    public void setOwner(final int s) {
        this.getDataManager().set(EntityFocusProjectile.OWNER, s);
    }
    
    public int getOwner() {
        return (int)this.getDataManager().get((DataParameter)EntityFocusProjectile.OWNER);
    }
    
    public EntityLivingBase getThrower() {
        if (this.world.isRemote) {
            final Entity e = this.world.getEntityByID(this.getOwner());
            if (e != null && e instanceof EntityLivingBase) {
                return (EntityLivingBase)e;
            }
        }
        return super.getThrower();
    }
    
    public void setSpecial(final int s) {
        this.getDataManager().set(EntityFocusProjectile.SPECIAL, s);
    }
    
    public int getSpecial() {
        return (int)this.getDataManager().get((DataParameter)EntityFocusProjectile.SPECIAL);
    }
    
    public void writeSpawnData(final ByteBuf data) {
        Utils.writeNBTTagCompoundToBuffer(data, this.focusPackage.serialize());
    }
    
    public void readSpawnData(final ByteBuf data) {
        try {
            (this.focusPackage = new FocusPackage()).deserialize(Utils.readNBTTagCompoundFromBuffer(data));
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setTag("pack", this.focusPackage.serialize());
        nbt.setInteger("special", this.getSpecial());
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setSpecial(nbt.getInteger("special"));
        try {
            (this.focusPackage = new FocusPackage()).deserialize(nbt.getCompoundTag("pack"));
        }
        catch (final Exception ex) {}
        if (this.getThrower() != null) {
            this.setOwner(this.getThrower().getEntityId());
        }
    }
    
    protected void onImpact(final RayTraceResult mop) {
        if (mop != null) {
            if (this.getSpecial() == 1 && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
                final IBlockState bs = this.world.getBlockState(mop.getBlockPos());
                final AxisAlignedBB bb = bs.getCollisionBoundingBox(this.world, mop.getBlockPos());
                if (bb == null) {
                    return;
                }
                this.posX -= this.motionX;
                this.posY -= this.motionY;
                this.posZ -= this.motionZ;
                if (mop.sideHit.getFrontOffsetZ() != 0) {
                    this.motionZ *= -1.0;
                }
                if (mop.sideHit.getFrontOffsetX() != 0) {
                    this.motionX *= -1.0;
                }
                if (mop.sideHit.getFrontOffsetY() != 0) {
                    this.motionY *= -0.9;
                }
                this.motionX *= 0.9;
                this.motionY *= 0.9;
                this.motionZ *= 0.9;
                final float var20 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                this.posX -= this.motionX / var20 * 0.05000000074505806;
                this.posY -= this.motionY / var20 * 0.05000000074505806;
                this.posZ -= this.motionZ / var20 * 0.05000000074505806;
                if (!this.world.isRemote) {
                    this.playSound(SoundEvents.ENTITY_LEASHKNOT_PLACE, 0.25f, 1.0f);
                }
                if (!this.world.isRemote && new Vec3d(this.motionX, this.motionY, this.motionZ).lengthVector() < 0.2) {
                    this.setDead();
                }
            }
            else if (!this.world.isRemote) {
                if (mop.entityHit != null) {
                    mop.hitVec = this.getPositionVector();
                }
                final Vec3d pv = new Vec3d(this.prevPosX, this.prevPosY, this.prevPosZ);
                final Vec3d vf = new Vec3d(this.motionX, this.motionY, this.motionZ);
                ServerEvents.addRunnableServer(this.getEntityWorld(), new Runnable() {
                    @Override
                    public void run() {
                        FocusEngine.runFocusPackage(EntityFocusProjectile.this.focusPackage, new Trajectory[] { new Trajectory(pv, vf.normalize()) }, new RayTraceResult[] { mop });
                    }
                }, 0);
                this.setDead();
            }
        }
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted > 1200 || (!this.world.isRemote && this.getThrower() == null)) {
            this.setDead();
        }
        this.firstParticle = true;
        if (this.target == null && this.ticksExisted % 5 == 0 && this.getSpecial() > 1) {
            final List<EntityLivingBase> list = EntityUtils.getEntitiesInRangeSorted(this.getEntityWorld(), this, EntityLivingBase.class, 16.0);
            for (final EntityLivingBase pt : list) {
                if (!pt.isDead && EntityUtils.isVisibleTo(1.75f, this, pt, 16.0f)) {
                    if (!EntityUtils.canEntityBeSeen(this, pt)) {
                        continue;
                    }
                    final boolean f = EntityUtils.isFriendly(this.getThrower(), pt);
                    if (f && this.getSpecial() == 3) {
                        this.target = pt;
                        break;
                    }
                    if (!f && this.getSpecial() == 2) {
                        this.target = pt;
                        break;
                    }
                    continue;
                }
            }
        }
        if (this.target != null) {
            final double d = this.getDistanceSq(this.target);
            final double dx = this.target.posX - this.posX;
            final double dy = this.target.getEntityBoundingBox().minY + this.target.height * 0.6 - this.posY;
            final double dz = this.target.posZ - this.posZ;
            Vec3d v = new Vec3d(dx, dy, dz);
            v = v.normalize();
            Vec3d mv = new Vec3d(this.motionX, this.motionY, this.motionZ);
            final double lv = mv.lengthVector();
            mv = mv.normalize().add(v.scale(0.275));
            mv = mv.normalize().scale(lv);
            this.motionX = mv.x;
            this.motionY = mv.y;
            this.motionZ = mv.z;
            if (this.ticksExisted % 5 == 0 && (this.target.isDead || !EntityUtils.isVisibleTo(1.75f, this, this.target, 16.0f) || !EntityUtils.canEntityBeSeen(this, this.target))) {
                this.target = null;
            }
        }
    }
    
    public Vec3d getLookVec() {
        return new Vec3d(this.motionX, this.motionY, this.motionZ).normalize();
    }
    
    public void renderParticle(final float coeff) {
        this.lastRenderTick = coeff;
        if (this.effects == null) {
            this.effects = this.focusPackage.getFocusEffects();
        }
        if (this.effects != null && this.effects.length > 0) {
            final FocusEffect eff = this.effects[this.rand.nextInt(this.effects.length)];
            final float scale = 1.0f;
            final Color c1 = new Color(FocusEngine.getElementColor(eff.getKey()));
            FXDispatcher.INSTANCE.drawFireMote((float)(this.prevPosX + (this.posX - this.prevPosX) * coeff), (float)(this.prevPosY + (this.posY - this.prevPosY) * coeff) + this.height / 2.0f, (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * coeff), 0.0125f * (this.rand.nextFloat() - 0.5f) * scale, 0.0125f * (this.rand.nextFloat() - 0.5f) * scale, 0.0125f * (this.rand.nextFloat() - 0.5f) * scale, c1.getRed() / 255.0f, c1.getGreen() / 255.0f, c1.getBlue() / 255.0f, 0.5f, 7.0f * scale);
            if (this.firstParticle) {
                this.firstParticle = false;
                eff.renderParticleFX(this.world, this.prevPosX + (this.posX - this.prevPosX) * coeff + this.world.rand.nextGaussian() * 0.10000000149011612, this.prevPosY + (this.posY - this.prevPosY) * coeff + this.height / 2.0f + this.world.rand.nextGaussian() * 0.10000000149011612, this.prevPosZ + (this.posZ - this.prevPosZ) * coeff + this.world.rand.nextGaussian() * 0.10000000149011612, this.world.rand.nextGaussian() * 0.009999999776482582, this.world.rand.nextGaussian() * 0.009999999776482582, this.world.rand.nextGaussian() * 0.009999999776482582);
            }
        }
    }
    
    static {
        SPECIAL = EntityDataManager.createKey(EntityFocusProjectile.class, DataSerializers.VARINT);
        OWNER = EntityDataManager.createKey(EntityFocusProjectile.class, DataSerializers.VARINT);
    }
}
