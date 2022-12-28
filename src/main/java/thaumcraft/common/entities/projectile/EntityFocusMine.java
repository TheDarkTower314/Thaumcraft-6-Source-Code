package thaumcraft.common.entities.projectile;
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.lib.events.ServerEvents;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.Utils;


public class EntityFocusMine extends EntityThrowable implements IEntityAdditionalSpawnData
{
    FocusPackage focusPackage;
    boolean friendly;
    private static DataParameter<Boolean> ARMED;
    public int counter;
    FocusEffect[] effects;
    
    public EntityFocusMine(World par1World) {
        super(par1World);
        friendly = false;
        counter = 40;
        effects = null;
        setSize(0.15f, 0.15f);
    }
    
    public EntityFocusMine(FocusPackage pack, Trajectory trajectory, boolean friendly) {
        super(pack.world, pack.getCaster());
        this.friendly = false;
        counter = 40;
        effects = null;
        focusPackage = pack;
        this.friendly = friendly;
        setPosition(trajectory.source.x, trajectory.source.y, trajectory.source.z);
        shoot(trajectory.direction.x, trajectory.direction.y, trajectory.direction.z, 0.0f, 0.0f);
        setSize(0.15f, 0.15f);
    }
    
    public void entityInit() {
        super.entityInit();
        getDataManager().register(EntityFocusMine.ARMED, false);
    }
    
    public boolean getIsArmed() {
        return (boolean) getDataManager().get((DataParameter)EntityFocusMine.ARMED);
    }
    
    public void setIsArmed(boolean par1) {
        getDataManager().set(EntityFocusMine.ARMED, par1);
    }
    
    protected float getGravityVelocity() {
        return 0.01f;
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
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("armed", getIsArmed());
        nbt.setTag("pack", focusPackage.serialize());
        nbt.setBoolean("friendly", friendly);
    }
    
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        friendly = nbt.getBoolean("friendly");
        setIsArmed(nbt.getBoolean("armed"));
        if (getIsArmed()) {
            counter = 0;
        }
        try {
            (focusPackage = new FocusPackage()).deserialize(nbt.getCompoundTag("pack"));
        }
        catch (Exception ex) {}
    }
    
    protected void onImpact(RayTraceResult mop) {
        if (mop != null && getThrower() != null) {
            setIsArmed(true);
        }
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (pushOutOfBlocks(posX, posY, posZ)) {
            motionX *= 0.25;
            motionY *= 0.25;
            motionZ *= 0.25;
        }
        if (ticksExisted > 1200 || (!world.isRemote && getThrower() == null)) {
            setDead();
        }
        if (isEntityAlive() && getIsArmed()) {
            if (counter > 0) {
                --counter;
            }
            if (counter <= 0 && ticksExisted % 5 == 0) {
                if (world.isRemote) {
                    if (effects == null) {
                        effects = focusPackage.getFocusEffects();
                    }
                    if (effects != null && effects.length > 0) {
                        FocusEffect eff = effects[rand.nextInt(effects.length)];
                        eff.renderParticleFX(world, posX + world.rand.nextGaussian() * 0.1, posY + world.rand.nextGaussian() * 0.1, posZ + world.rand.nextGaussian() * 0.1, world.rand.nextGaussian() * 0.009999999776482582, world.rand.nextGaussian() * 0.009999999776482582, world.rand.nextGaussian() * 0.009999999776482582);
                    }
                }
                else {
                    List<EntityLivingBase> list2 = EntityUtils.getEntitiesInRange(world, posX, posY, posZ, this, EntityLivingBase.class, 1.0);
                    int d = 0;
                    for (EntityLivingBase e : list2) {
                        if (e.isDead) {
                            continue;
                        }
                        if (friendly) {
                            if (!EntityUtils.isFriendly(focusPackage.getCaster(), e)) {
                                continue;
                            }
                        }
                        else if (EntityUtils.isFriendly(focusPackage.getCaster(), e)) {
                            continue;
                        }
                        Vec3d epv = e.getPositionVector().addVector(0.0, e.height / 2.0f, 0.0);
                        ServerEvents.addRunnableServer(getEntityWorld(), new Runnable() {
                            @Override
                            public void run() {
                                RayTraceResult ray = new RayTraceResult(e);
                                ray.hitVec = e.getPositionVector().addVector(0.0, e.height / 2.0f, 0.0);
                                FocusEngine.runFocusPackage(focusPackage.copy(getThrower()), new Trajectory[] { new Trajectory(getPositionVector(), epv.subtract(getPositionVector()).normalize()) }, new RayTraceResult[] { ray });
                            }
                        }, d++);
                    }
                    if (d > 0) {
                        setDead();
                    }
                }
            }
        }
    }
    
    static {
        ARMED = EntityDataManager.createKey(EntityFocusMine.class, DataSerializers.BOOLEAN);
    }
}
