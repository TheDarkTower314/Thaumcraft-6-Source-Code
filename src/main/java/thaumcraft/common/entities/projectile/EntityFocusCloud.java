// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.projectile;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import java.util.Iterator;
import java.util.List;
import thaumcraft.common.lib.events.ServerEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.MoverType;
import thaumcraft.common.lib.utils.EntityUtils;
import net.minecraft.util.math.RayTraceResult;
import java.util.ArrayList;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.WorldServer;
import javax.annotation.Nullable;
import thaumcraft.api.casters.Trajectory;
import net.minecraft.world.World;
import thaumcraft.api.casters.FocusEffect;
import java.util.HashMap;
import net.minecraft.network.datasync.DataParameter;
import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.api.casters.FocusPackage;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.Entity;

public class EntityFocusCloud extends Entity implements IEntityAdditionalSpawnData
{
    FocusPackage focusPackage;
    private EntityLivingBase owner;
    private UUID ownerUniqueId;
    private int duration;
    private static final DataParameter<Float> RADIUS;
    static HashMap<Long, Long> cooldownMap;
    FocusEffect[] effects;
    
    public EntityFocusCloud(final World par1World) {
        super(par1World);
        this.effects = null;
    }
    
    public EntityFocusCloud(final FocusPackage pack, final Trajectory trajectory, final float rad, final int dur) {
        super(pack.world);
        this.effects = null;
        this.focusPackage = pack;
        this.setPosition(trajectory.source.x, trajectory.source.y, trajectory.source.z);
        this.setSize(0.15f, 0.15f);
        this.setOwner(pack.getCaster());
        this.setRadius(rad);
        this.setDuration(dur);
    }
    
    public int getDuration() {
        return this.duration;
    }
    
    public void setDuration(final int durationIn) {
        this.duration = durationIn;
    }
    
    public void setOwner(@Nullable final EntityLivingBase ownerIn) {
        this.owner = ownerIn;
        this.ownerUniqueId = ((ownerIn == null) ? null : ownerIn.getUniqueID());
    }
    
    @Nullable
    public EntityLivingBase getOwner() {
        if (this.owner == null && this.ownerUniqueId != null && this.world instanceof WorldServer) {
            final Entity entity = ((WorldServer)this.world).getEntityFromUuid(this.ownerUniqueId);
            if (entity instanceof EntityLivingBase) {
                this.owner = (EntityLivingBase)entity;
            }
        }
        return this.owner;
    }
    
    public void entityInit() {
        this.getDataManager().register(EntityFocusCloud.RADIUS, 0.5f);
    }
    
    public void setRadius(final float radiusIn) {
        final double d0 = this.posX;
        final double d2 = this.posY;
        final double d3 = this.posZ;
        this.setSize(radiusIn * 2.0f, 0.5f);
        this.setPosition(d0, d2, d3);
        if (!this.world.isRemote) {
            this.getDataManager().set(EntityFocusCloud.RADIUS, radiusIn);
        }
    }
    
    public float getRadius() {
        return (float)this.getDataManager().get((DataParameter)EntityFocusCloud.RADIUS);
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
        nbt.setInteger("Age", this.ticksExisted);
        nbt.setInteger("Duration", this.duration);
        nbt.setFloat("Radius", this.getRadius());
        if (this.ownerUniqueId != null) {
            nbt.setUniqueId("OwnerUUID", this.ownerUniqueId);
        }
        nbt.setTag("pack", this.focusPackage.serialize());
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        this.ticksExisted = nbt.getInteger("Age");
        this.duration = nbt.getInteger("Duration");
        this.setRadius(nbt.getFloat("Radius"));
        this.ownerUniqueId = nbt.getUniqueId("OwnerUUID");
        try {
            (this.focusPackage = new FocusPackage()).deserialize(nbt.getCompoundTag("pack"));
        }
        catch (final Exception ex) {}
    }
    
    public void onUpdate() {
        super.onUpdate();
        final float rad = this.getRadius();
        final int dur = this.getDuration();
        if (!this.world.isRemote && (this.ticksExisted > dur * 20 || this.getOwner() == null)) {
            this.setDead();
        }
        if (this.isEntityAlive()) {
            if (this.world.isRemote) {
                if (this.effects == null) {
                    this.effects = this.focusPackage.getFocusEffects();
                }
                if (this.effects != null && this.effects.length > 0) {
                    for (int a = 0; a < rad; ++a) {
                        final FocusEffect eff = this.effects[this.rand.nextInt(this.effects.length)];
                        FXDispatcher.INSTANCE.drawFocusCloudParticle(this.posX + this.world.rand.nextGaussian() * rad / 2.0 * 0.85, this.posY + this.world.rand.nextGaussian() * rad / 2.0 * 0.85, this.posZ + this.world.rand.nextGaussian() * rad / 2.0 * 0.85, this.world.rand.nextGaussian() * 0.01, this.world.rand.nextGaussian() * 0.01, this.world.rand.nextGaussian() * 0.01, FocusEngine.getElementColor(eff.getKey()));
                        eff.renderParticleFX(this.world, this.posX + this.world.rand.nextGaussian() * rad / 2.0, this.posY + this.world.rand.nextGaussian() * rad / 2.0, this.posZ + this.world.rand.nextGaussian() * rad / 2.0, this.world.rand.nextGaussian() * 0.009999999776482582, this.world.rand.nextGaussian() * 0.009999999776482582, this.world.rand.nextGaussian() * 0.009999999776482582);
                    }
                }
            }
            else if (this.ticksExisted % 5 == 0) {
                final long t = System.currentTimeMillis();
                final ArrayList<Trajectory> trajectories = new ArrayList<Trajectory>();
                final ArrayList<RayTraceResult> targets = new ArrayList<RayTraceResult>();
                final List<Entity> list = EntityUtils.getEntitiesInRange(this.world, this.posX, this.posY, this.posZ, this, Entity.class, rad);
                for (final Entity e : list) {
                    if (e.isDead) {
                        continue;
                    }
                    if (e instanceof EntityFocusCloud) {
                        final Vec3d v = e.getPositionVector().subtract(this.getPositionVector());
                        e.move(MoverType.SELF, v.x / 50.0, v.y / 50.0, v.z / 50.0);
                        ((EntityFocusCloud)e).pushOutOfBlocks(this.posX, this.posY, this.posZ);
                    }
                    if (!(e instanceof EntityLivingBase)) {
                        continue;
                    }
                    if (EntityFocusCloud.cooldownMap.containsKey(e.getEntityId()) && EntityFocusCloud.cooldownMap.get(e.getEntityId()) > t) {
                        continue;
                    }
                    EntityFocusCloud.cooldownMap.put((long)e.getEntityId(), t + 2000L);
                    final RayTraceResult ray = new RayTraceResult(e);
                    ray.hitVec = e.getPositionVector().addVector(0.0, e.height / 2.0f, 0.0);
                    final Trajectory tra = new Trajectory(this.getPositionVector(), this.getPositionVector().subtractReverse(ray.hitVec));
                    targets.add(ray);
                    trajectories.add(tra);
                }
                for (int a2 = 0; a2 < rad; ++a2) {
                    Vec3d dV = new Vec3d(this.rand.nextGaussian(), this.rand.nextGaussian(), this.rand.nextGaussian());
                    dV = dV.normalize();
                    RayTraceResult br = this.world.rayTraceBlocks(this.getPositionVector(), this.getPositionVector().add(dV.scale(rad)));
                    long bl = 0L;
                    if (br != null) {
                        bl = br.getBlockPos().toLong();
                        if (EntityFocusCloud.cooldownMap.containsKey(bl)) {
                            if (EntityFocusCloud.cooldownMap.get(bl) <= t) {
                                EntityFocusCloud.cooldownMap.remove(bl);
                            }
                            else {
                                br = null;
                            }
                        }
                    }
                    if (br != null) {
                        targets.add(br);
                        final Trajectory tra2 = new Trajectory(this.getPositionVector(), dV);
                        trajectories.add(tra2);
                        EntityFocusCloud.cooldownMap.put(bl, t + 2000L);
                    }
                }
                if (!targets.isEmpty()) {
                    ServerEvents.addRunnableServer(this.getEntityWorld(), new Runnable() {
                        @Override
                        public void run() {
                            FocusEngine.runFocusPackage(EntityFocusCloud.this.focusPackage.copy(EntityFocusCloud.this.getOwner()), trajectories.toArray(new Trajectory[0]), targets.toArray(new RayTraceResult[0]));
                        }
                    }, 0);
                }
            }
        }
    }
    
    static {
        RADIUS = EntityDataManager.createKey(EntityFocusCloud.class, DataSerializers.FLOAT);
        EntityFocusCloud.cooldownMap = new HashMap<Long, Long>();
    }
}
