// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.projectile;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import java.util.Iterator;
import java.util.List;
import thaumcraft.common.lib.events.ServerEvents;
import thaumcraft.api.casters.FocusEngine;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import thaumcraft.common.lib.utils.EntityUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;
import thaumcraft.api.casters.Trajectory;
import net.minecraft.world.World;
import thaumcraft.api.casters.FocusEffect;
import net.minecraft.network.datasync.DataParameter;
import thaumcraft.api.casters.FocusPackage;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.projectile.EntityThrowable;

public class EntityFocusMine extends EntityThrowable implements IEntityAdditionalSpawnData
{
    FocusPackage focusPackage;
    boolean friendly;
    private static final DataParameter<Boolean> ARMED;
    public int counter;
    FocusEffect[] effects;
    
    public EntityFocusMine(final World par1World) {
        super(par1World);
        this.friendly = false;
        this.counter = 40;
        this.effects = null;
        this.setSize(0.15f, 0.15f);
    }
    
    public EntityFocusMine(final FocusPackage pack, final Trajectory trajectory, final boolean friendly) {
        super(pack.world, pack.getCaster());
        this.friendly = false;
        this.counter = 40;
        this.effects = null;
        this.focusPackage = pack;
        this.friendly = friendly;
        this.setPosition(trajectory.source.x, trajectory.source.y, trajectory.source.z);
        this.shoot(trajectory.direction.x, trajectory.direction.y, trajectory.direction.z, 0.0f, 0.0f);
        this.setSize(0.15f, 0.15f);
    }
    
    public void entityInit() {
        super.entityInit();
        this.getDataManager().register(EntityFocusMine.ARMED, false);
    }
    
    public boolean getIsArmed() {
        return (boolean)this.getDataManager().get((DataParameter)EntityFocusMine.ARMED);
    }
    
    public void setIsArmed(final boolean par1) {
        this.getDataManager().set(EntityFocusMine.ARMED, par1);
    }
    
    protected float getGravityVelocity() {
        return 0.01f;
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
        nbt.setBoolean("armed", this.getIsArmed());
        nbt.setTag("pack", this.focusPackage.serialize());
        nbt.setBoolean("friendly", this.friendly);
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.friendly = nbt.getBoolean("friendly");
        this.setIsArmed(nbt.getBoolean("armed"));
        if (this.getIsArmed()) {
            this.counter = 0;
        }
        try {
            (this.focusPackage = new FocusPackage()).deserialize(nbt.getCompoundTag("pack"));
        }
        catch (final Exception ex) {}
    }
    
    protected void onImpact(final RayTraceResult mop) {
        if (mop != null && this.getThrower() != null) {
            this.setIsArmed(true);
        }
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (this.pushOutOfBlocks(this.posX, this.posY, this.posZ)) {
            this.motionX *= 0.25;
            this.motionY *= 0.25;
            this.motionZ *= 0.25;
        }
        if (this.ticksExisted > 1200 || (!this.world.isRemote && this.getThrower() == null)) {
            this.setDead();
        }
        if (this.isEntityAlive() && this.getIsArmed()) {
            if (this.counter > 0) {
                --this.counter;
            }
            if (this.counter <= 0 && this.ticksExisted % 5 == 0) {
                if (this.world.isRemote) {
                    if (this.effects == null) {
                        this.effects = this.focusPackage.getFocusEffects();
                    }
                    if (this.effects != null && this.effects.length > 0) {
                        final FocusEffect eff = this.effects[this.rand.nextInt(this.effects.length)];
                        eff.renderParticleFX(this.world, this.posX + this.world.rand.nextGaussian() * 0.1, this.posY + this.world.rand.nextGaussian() * 0.1, this.posZ + this.world.rand.nextGaussian() * 0.1, this.world.rand.nextGaussian() * 0.009999999776482582, this.world.rand.nextGaussian() * 0.009999999776482582, this.world.rand.nextGaussian() * 0.009999999776482582);
                    }
                }
                else {
                    final List<EntityLivingBase> list2 = EntityUtils.getEntitiesInRange(this.world, this.posX, this.posY, this.posZ, this, EntityLivingBase.class, 1.0);
                    int d = 0;
                    for (final EntityLivingBase e : list2) {
                        if (e.isDead) {
                            continue;
                        }
                        if (this.friendly) {
                            if (!EntityUtils.isFriendly(this.focusPackage.getCaster(), e)) {
                                continue;
                            }
                        }
                        else if (EntityUtils.isFriendly(this.focusPackage.getCaster(), e)) {
                            continue;
                        }
                        final Vec3d epv = e.getPositionVector().addVector(0.0, e.height / 2.0f, 0.0);
                        ServerEvents.addRunnableServer(this.getEntityWorld(), new Runnable() {
                            @Override
                            public void run() {
                                final RayTraceResult ray = new RayTraceResult(e);
                                ray.hitVec = e.getPositionVector().addVector(0.0, e.height / 2.0f, 0.0);
                                FocusEngine.runFocusPackage(EntityFocusMine.this.focusPackage.copy(EntityFocusMine.this.getThrower()), new Trajectory[] { new Trajectory(EntityFocusMine.this.getPositionVector(), epv.subtract(EntityFocusMine.this.getPositionVector()).normalize()) }, new RayTraceResult[] { ray });
                            }
                        }, d++);
                    }
                    if (d > 0) {
                        this.setDead();
                    }
                }
            }
        }
    }
    
    static {
        ARMED = EntityDataManager.createKey(EntityFocusMine.class, DataSerializers.BOOLEAN);
    }
}
