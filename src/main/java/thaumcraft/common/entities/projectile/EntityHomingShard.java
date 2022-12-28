package thaumcraft.common.entities.projectile;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;


public class EntityHomingShard extends EntityThrowable implements IEntityAdditionalSpawnData
{
    Class tclass;
    boolean persistant;
    int targetID;
    EntityLivingBase target;
    private static DataParameter<Byte> STRENGTH;
    public ArrayList<UtilsFX.Vector> vl;
    
    public EntityHomingShard(World par1World) {
        super(par1World);
        tclass = null;
        persistant = false;
        targetID = 0;
        vl = new ArrayList<UtilsFX.Vector>();
    }
    
    public EntityHomingShard(World par1World, EntityLivingBase p, EntityLivingBase t, int strength, boolean b) {
        super(par1World, p);
        tclass = null;
        persistant = false;
        targetID = 0;
        vl = new ArrayList<UtilsFX.Vector>();
        target = t;
        tclass = t.getClass();
        persistant = b;
        setStrength(strength);
        Vec3d v = p.getLookVec();
        setLocationAndAngles(p.posX + v.x / 2.0, p.posY + p.getEyeHeight() + v.y / 2.0, p.posZ + v.z / 2.0, p.rotationYaw, p.rotationPitch);
        float f = 0.5f;
        float ry = p.rotationYaw + (rand.nextFloat() - rand.nextFloat()) * 60.0f;
        float rp = p.rotationPitch + (rand.nextFloat() - rand.nextFloat()) * 60.0f;
        motionX = -MathHelper.sin(ry / 180.0f * 3.1415927f) * MathHelper.cos(rp / 180.0f * 3.1415927f) * f;
        motionZ = MathHelper.cos(ry / 180.0f * 3.1415927f) * MathHelper.cos(rp / 180.0f * 3.1415927f) * f;
        motionY = -MathHelper.sin(rp / 180.0f * 3.1415927f) * f;
    }
    
    public void entityInit() {
        super.entityInit();
        getDataManager().register((DataParameter)EntityHomingShard.STRENGTH, 0);
    }
    
    public void setStrength(int str) {
        getDataManager().set(EntityHomingShard.STRENGTH, (byte)str);
    }
    
    public int getStrength() {
        return (byte) getDataManager().get((DataParameter)EntityHomingShard.STRENGTH);
    }
    
    protected float getGravityVelocity() {
        return 0.0f;
    }
    
    public void writeSpawnData(ByteBuf data) {
        int id = -1;
        if (target != null) {
            id = target.getEntityId();
        }
        data.writeInt(id);
    }
    
    public void readSpawnData(ByteBuf data) {
        int id = data.readInt();
        try {
            if (id >= 0) {
                target = (EntityLivingBase) world.getEntityByID(id);
            }
        }
        catch (Exception ex) {}
    }
    
    protected void onImpact(RayTraceResult mop) {
        if (!world.isRemote && mop.typeOfHit == RayTraceResult.Type.ENTITY && (getThrower() == null || (getThrower() != null && mop.entityHit != getThrower()))) {
            mop.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, getThrower()), 1.0f + getStrength() * 0.5f);
            playSound(SoundsTC.zap, 1.0f, 1.0f + (rand.nextFloat() - rand.nextFloat()) * 0.2f);
            world.setEntityState(this, (byte)16);
            setDead();
        }
        if (mop.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (mop.sideHit.getFrontOffsetZ() != 0) {
                motionZ *= -0.8;
            }
            if (mop.sideHit.getFrontOffsetX() != 0) {
                motionX *= -0.8;
            }
            if (mop.sideHit.getFrontOffsetY() != 0) {
                motionY *= -0.8;
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte par1) {
        if (par1 == 16) {
            FXDispatcher.INSTANCE.burst(posX, posY, posZ, 0.3f);
        }
        else {
            super.handleStatusUpdate(par1);
        }
    }
    
    public void onUpdate() {
        vl.add(0, new UtilsFX.Vector((float) lastTickPosX, (float) lastTickPosY, (float) lastTickPosZ));
        if (vl.size() > 6) {
            vl.remove(vl.size() - 1);
        }
        super.onUpdate();
        if (!world.isRemote) {
            if (persistant && (target == null || target.isDead || target.getDistanceSq(this) > 1250.0)) {
                List<Entity> es = EntityUtils.getEntitiesInRange(world, posX, posY, posZ, this, (Class<? extends Entity>) tclass, 16.0);
                for (Entity e : es) {
                    if (e instanceof EntityLivingBase && !e.isDead && (getThrower() == null || e.getEntityId() != getThrower().getEntityId())) {
                        target = (EntityLivingBase)e;
                        break;
                    }
                }
            }
            if (target == null || target.isDead) {
                world.setEntityState(this, (byte)16);
                setDead();
            }
        }
        if (ticksExisted > 300) {
            world.setEntityState(this, (byte)16);
            setDead();
        }
        if (ticksExisted % 20 == 0 && target != null && !target.isDead) {
            double d = getDistance(target);
            double dx = target.posX - posX;
            double dy = target.getEntityBoundingBox().minY + target.height * 0.6 - posY;
            double dz = target.posZ - posZ;
            dx /= d;
            dy /= d;
            dz /= d;
            motionX = dx;
            motionY = dy;
            motionZ = dz;
        }
        motionX *= 0.85;
        motionY *= 0.85;
        motionZ *= 0.85;
    }
    
    static {
        STRENGTH = EntityDataManager.createKey(EntityHomingShard.class, DataSerializers.BYTE);
    }
}
