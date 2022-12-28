package thaumcraft.common.entities.projectile;
import io.netty.buffer.ByteBuf;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.events.ServerEvents;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.Utils;


public class EntityFocusProjectile extends EntityThrowable implements IEntityAdditionalSpawnData
{
    FocusPackage focusPackage;
    private static DataParameter<Integer> SPECIAL;
    private static DataParameter<Integer> OWNER;
    boolean noTouchy;
    private Entity target;
    boolean firstParticle;
    public float lastRenderTick;
    FocusEffect[] effects;
    
    public EntityFocusProjectile(World par1World) {
        super(par1World);
        noTouchy = false;
        firstParticle = false;
        lastRenderTick = 0.0f;
        effects = null;
        setSize(0.15f, 0.15f);
    }
    
    public EntityFocusProjectile(FocusPackage pack, float speed, Trajectory trajectory, int special) {
        super(pack.world, pack.getCaster());
        noTouchy = false;
        firstParticle = false;
        lastRenderTick = 0.0f;
        effects = null;
        focusPackage = pack;
        setPosition(trajectory.source.x + trajectory.direction.x * pack.getCaster().width * 2.1, trajectory.source.y + trajectory.direction.y * pack.getCaster().width * 2.1, trajectory.source.z + trajectory.direction.z * pack.getCaster().width * 2.1);
        shoot(trajectory.direction.x, trajectory.direction.y, trajectory.direction.z, speed, 0.0f);
        setSize(0.15f, 0.15f);
        setSpecial(special);
        ignoreEntity = pack.getCaster();
        setOwner(getThrower().getEntityId());
    }
    
    protected float getGravityVelocity() {
        return (getSpecial() > 1) ? 0.005f : 0.01f;
    }
    
    public void entityInit() {
        super.entityInit();
        getDataManager().register(EntityFocusProjectile.SPECIAL, 0);
        getDataManager().register(EntityFocusProjectile.OWNER, 0);
    }
    
    public void setOwner(int s) {
        getDataManager().set(EntityFocusProjectile.OWNER, s);
    }
    
    public int getOwner() {
        return (int) getDataManager().get((DataParameter)EntityFocusProjectile.OWNER);
    }
    
    public EntityLivingBase getThrower() {
        if (world.isRemote) {
            Entity e = world.getEntityByID(getOwner());
            if (e != null && e instanceof EntityLivingBase) {
                return (EntityLivingBase)e;
            }
        }
        return super.getThrower();
    }
    
    public void setSpecial(int s) {
        getDataManager().set(EntityFocusProjectile.SPECIAL, s);
    }
    
    public int getSpecial() {
        return (int) getDataManager().get((DataParameter)EntityFocusProjectile.SPECIAL);
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
        nbt.setTag("pack", focusPackage.serialize());
        nbt.setInteger("special", getSpecial());
    }
    
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setSpecial(nbt.getInteger("special"));
        try {
            (focusPackage = new FocusPackage()).deserialize(nbt.getCompoundTag("pack"));
        }
        catch (Exception ex) {}
        if (getThrower() != null) {
            setOwner(getThrower().getEntityId());
        }
    }
    
    protected void onImpact(RayTraceResult mop) {
        if (mop != null) {
            if (getSpecial() == 1 && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
                IBlockState bs = world.getBlockState(mop.getBlockPos());
                AxisAlignedBB bb = bs.getCollisionBoundingBox(world, mop.getBlockPos());
                if (bb == null) {
                    return;
                }
                posX -= motionX;
                posY -= motionY;
                posZ -= motionZ;
                if (mop.sideHit.getFrontOffsetZ() != 0) {
                    motionZ *= -1.0;
                }
                if (mop.sideHit.getFrontOffsetX() != 0) {
                    motionX *= -1.0;
                }
                if (mop.sideHit.getFrontOffsetY() != 0) {
                    motionY *= -0.9;
                }
                motionX *= 0.9;
                motionY *= 0.9;
                motionZ *= 0.9;
                float var20 = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
                posX -= motionX / var20 * 0.05000000074505806;
                posY -= motionY / var20 * 0.05000000074505806;
                posZ -= motionZ / var20 * 0.05000000074505806;
                if (!world.isRemote) {
                    playSound(SoundEvents.ENTITY_LEASHKNOT_PLACE, 0.25f, 1.0f);
                }
                if (!world.isRemote && new Vec3d(motionX, motionY, motionZ).lengthVector() < 0.2) {
                    setDead();
                }
            }
            else if (!world.isRemote) {
                if (mop.entityHit != null) {
                    mop.hitVec = getPositionVector();
                }
                Vec3d pv = new Vec3d(prevPosX, prevPosY, prevPosZ);
                Vec3d vf = new Vec3d(motionX, motionY, motionZ);
                ServerEvents.addRunnableServer(getEntityWorld(), new Runnable() {
                    @Override
                    public void run() {
                        FocusEngine.runFocusPackage(focusPackage, new Trajectory[] { new Trajectory(pv, vf.normalize()) }, new RayTraceResult[] { mop });
                    }
                }, 0);
                setDead();
            }
        }
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (ticksExisted > 1200 || (!world.isRemote && getThrower() == null)) {
            setDead();
        }
        firstParticle = true;
        if (target == null && ticksExisted % 5 == 0 && getSpecial() > 1) {
            List<EntityLivingBase> list = EntityUtils.getEntitiesInRangeSorted(getEntityWorld(), this, EntityLivingBase.class, 16.0);
            for (EntityLivingBase pt : list) {
                if (!pt.isDead && EntityUtils.isVisibleTo(1.75f, this, pt, 16.0f)) {
                    if (!EntityUtils.canEntityBeSeen(this, pt)) {
                        continue;
                    }
                    boolean f = EntityUtils.isFriendly(getThrower(), pt);
                    if (f && getSpecial() == 3) {
                        target = pt;
                        break;
                    }
                    if (!f && getSpecial() == 2) {
                        target = pt;
                        break;
                    }
                    continue;
                }
            }
        }
        if (target != null) {
            double d = getDistanceSq(target);
            double dx = target.posX - posX;
            double dy = target.getEntityBoundingBox().minY + target.height * 0.6 - posY;
            double dz = target.posZ - posZ;
            Vec3d v = new Vec3d(dx, dy, dz);
            v = v.normalize();
            Vec3d mv = new Vec3d(motionX, motionY, motionZ);
            double lv = mv.lengthVector();
            mv = mv.normalize().add(v.scale(0.275));
            mv = mv.normalize().scale(lv);
            motionX = mv.x;
            motionY = mv.y;
            motionZ = mv.z;
            if (ticksExisted % 5 == 0 && (target.isDead || !EntityUtils.isVisibleTo(1.75f, this, target, 16.0f) || !EntityUtils.canEntityBeSeen(this, target))) {
                target = null;
            }
        }
    }
    
    public Vec3d getLookVec() {
        return new Vec3d(motionX, motionY, motionZ).normalize();
    }
    
    public void renderParticle(float coeff) {
        lastRenderTick = coeff;
        if (effects == null) {
            effects = focusPackage.getFocusEffects();
        }
        if (effects != null && effects.length > 0) {
            FocusEffect eff = effects[rand.nextInt(effects.length)];
            float scale = 1.0f;
            Color c1 = new Color(FocusEngine.getElementColor(eff.getKey()));
            FXDispatcher.INSTANCE.drawFireMote((float)(prevPosX + (posX - prevPosX) * coeff), (float)(prevPosY + (posY - prevPosY) * coeff) + height / 2.0f, (float)(prevPosZ + (posZ - prevPosZ) * coeff), 0.0125f * (rand.nextFloat() - 0.5f) * scale, 0.0125f * (rand.nextFloat() - 0.5f) * scale, 0.0125f * (rand.nextFloat() - 0.5f) * scale, c1.getRed() / 255.0f, c1.getGreen() / 255.0f, c1.getBlue() / 255.0f, 0.5f, 7.0f * scale);
            if (firstParticle) {
                firstParticle = false;
                eff.renderParticleFX(world, prevPosX + (posX - prevPosX) * coeff + world.rand.nextGaussian() * 0.10000000149011612, prevPosY + (posY - prevPosY) * coeff + height / 2.0f + world.rand.nextGaussian() * 0.10000000149011612, prevPosZ + (posZ - prevPosZ) * coeff + world.rand.nextGaussian() * 0.10000000149011612, world.rand.nextGaussian() * 0.009999999776482582, world.rand.nextGaussian() * 0.009999999776482582, world.rand.nextGaussian() * 0.009999999776482582);
            }
        }
    }
    
    static {
        SPECIAL = EntityDataManager.createKey(EntityFocusProjectile.class, DataSerializers.VARINT);
        OWNER = EntityDataManager.createKey(EntityFocusProjectile.class, DataSerializers.VARINT);
    }
}
