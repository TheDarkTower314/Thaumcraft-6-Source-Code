package thaumcraft.common.entities.projectile;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.events.ServerEvents;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.Utils;


public class EntityFocusCloud extends Entity implements IEntityAdditionalSpawnData
{
    FocusPackage focusPackage;
    private EntityLivingBase owner;
    private UUID ownerUniqueId;
    private int duration;
    private static DataParameter<Float> RADIUS;
    static HashMap<Long, Long> cooldownMap;
    FocusEffect[] effects;
    
    public EntityFocusCloud(World par1World) {
        super(par1World);
        effects = null;
    }
    
    public EntityFocusCloud(FocusPackage pack, Trajectory trajectory, float rad, int dur) {
        super(pack.world);
        effects = null;
        focusPackage = pack;
        setPosition(trajectory.source.x, trajectory.source.y, trajectory.source.z);
        setSize(0.15f, 0.15f);
        setOwner(pack.getCaster());
        setRadius(rad);
        setDuration(dur);
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int durationIn) {
        duration = durationIn;
    }
    
    public void setOwner(@Nullable EntityLivingBase ownerIn) {
        owner = ownerIn;
        ownerUniqueId = ((ownerIn == null) ? null : ownerIn.getUniqueID());
    }
    
    @Nullable
    public EntityLivingBase getOwner() {
        if (owner == null && ownerUniqueId != null && world instanceof WorldServer) {
            Entity entity = ((WorldServer) world).getEntityFromUuid(ownerUniqueId);
            if (entity instanceof EntityLivingBase) {
                owner = (EntityLivingBase)entity;
            }
        }
        return owner;
    }
    
    public void entityInit() {
        getDataManager().register(EntityFocusCloud.RADIUS, 0.5f);
    }
    
    public void setRadius(float radiusIn) {
        double d0 = posX;
        double d2 = posY;
        double d3 = posZ;
        setSize(radiusIn * 2.0f, 0.5f);
        setPosition(d0, d2, d3);
        if (!world.isRemote) {
            getDataManager().set(EntityFocusCloud.RADIUS, radiusIn);
        }
    }
    
    public float getRadius() {
        return (float) getDataManager().get((DataParameter)EntityFocusCloud.RADIUS);
    }
    
    public void writeSpawnData(ByteBuf data) {
        Utils.writeNBTTagCompoundToBuffer(data, focusPackage.serialize());
    }
    
    public void readSpawnData(ByteBuf data) {
        try {
            (focusPackage = new FocusPackage()).deserialize(Utils.readNBTTagCompoundFromBuffer(data));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setInteger("Age", ticksExisted);
        nbt.setInteger("Duration", duration);
        nbt.setFloat("Radius", getRadius());
        if (ownerUniqueId != null) {
            nbt.setUniqueId("OwnerUUID", ownerUniqueId);
        }
        nbt.setTag("pack", focusPackage.serialize());
    }
    
    public void readEntityFromNBT(NBTTagCompound nbt) {
        ticksExisted = nbt.getInteger("Age");
        duration = nbt.getInteger("Duration");
        setRadius(nbt.getFloat("Radius"));
        ownerUniqueId = nbt.getUniqueId("OwnerUUID");
        try {
            (focusPackage = new FocusPackage()).deserialize(nbt.getCompoundTag("pack"));
        }
        catch (Exception ex) {}
    }
    
    public void onUpdate() {
        super.onUpdate();
        float rad = getRadius();
        int dur = getDuration();
        if (!world.isRemote && (ticksExisted > dur * 20 || getOwner() == null)) {
            setDead();
        }
        if (isEntityAlive()) {
            if (world.isRemote) {
                if (effects == null) {
                    effects = focusPackage.getFocusEffects();
                }
                if (effects != null && effects.length > 0) {
                    for (int a = 0; a < rad; ++a) {
                        FocusEffect eff = effects[rand.nextInt(effects.length)];
                        FXDispatcher.INSTANCE.drawFocusCloudParticle(posX + world.rand.nextGaussian() * rad / 2.0 * 0.85, posY + world.rand.nextGaussian() * rad / 2.0 * 0.85, posZ + world.rand.nextGaussian() * rad / 2.0 * 0.85, world.rand.nextGaussian() * 0.01, world.rand.nextGaussian() * 0.01, world.rand.nextGaussian() * 0.01, FocusEngine.getElementColor(eff.getKey()));
                        eff.renderParticleFX(world, posX + world.rand.nextGaussian() * rad / 2.0, posY + world.rand.nextGaussian() * rad / 2.0, posZ + world.rand.nextGaussian() * rad / 2.0, world.rand.nextGaussian() * 0.009999999776482582, world.rand.nextGaussian() * 0.009999999776482582, world.rand.nextGaussian() * 0.009999999776482582);
                    }
                }
            }
            else if (ticksExisted % 5 == 0) {
                long t = System.currentTimeMillis();
                ArrayList<Trajectory> trajectories = new ArrayList<Trajectory>();
                ArrayList<RayTraceResult> targets = new ArrayList<RayTraceResult>();
                List<Entity> list = EntityUtils.getEntitiesInRange(world, posX, posY, posZ, this, Entity.class, rad);
                for (Entity e : list) {
                    if (e.isDead) {
                        continue;
                    }
                    if (e instanceof EntityFocusCloud) {
                        Vec3d v = e.getPositionVector().subtract(getPositionVector());
                        e.move(MoverType.SELF, v.x / 50.0, v.y / 50.0, v.z / 50.0);
                        ((EntityFocusCloud)e).pushOutOfBlocks(posX, posY, posZ);
                    }
                    if (!(e instanceof EntityLivingBase)) {
                        continue;
                    }
                    if (EntityFocusCloud.cooldownMap.containsKey(e.getEntityId()) && EntityFocusCloud.cooldownMap.get(e.getEntityId()) > t) {
                        continue;
                    }
                    EntityFocusCloud.cooldownMap.put((long)e.getEntityId(), t + 2000L);
                    RayTraceResult ray = new RayTraceResult(e);
                    ray.hitVec = e.getPositionVector().addVector(0.0, e.height / 2.0f, 0.0);
                    Trajectory tra = new Trajectory(getPositionVector(), getPositionVector().subtractReverse(ray.hitVec));
                    targets.add(ray);
                    trajectories.add(tra);
                }
                for (int a2 = 0; a2 < rad; ++a2) {
                    Vec3d dV = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian());
                    dV = dV.normalize();
                    RayTraceResult br = world.rayTraceBlocks(getPositionVector(), getPositionVector().add(dV.scale(rad)));
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
                        Trajectory tra2 = new Trajectory(getPositionVector(), dV);
                        trajectories.add(tra2);
                        EntityFocusCloud.cooldownMap.put(bl, t + 2000L);
                    }
                }
                if (!targets.isEmpty()) {
                    ServerEvents.addRunnableServer(getEntityWorld(), new Runnable() {
                        @Override
                        public void run() {
                            FocusEngine.runFocusPackage(focusPackage.copy(getOwner()), trajectories.toArray(new Trajectory[0]), targets.toArray(new RayTraceResult[0]));
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
