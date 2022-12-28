package thaumcraft.common.entities.projectile;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class EntityGrapple extends EntityThrowable implements IEntityAdditionalSpawnData
{
    public EnumHand hand;
    EntityLivingBase cthrower;
    boolean p;
    boolean boost;
    int prevDist;
    int count;
    boolean added;
    public float ampl;
    public static HashMap<Integer, Integer> grapples;
    
    public EntityGrapple(World par1World) {
        super(par1World);
        hand = EnumHand.MAIN_HAND;
        p = false;
        prevDist = 0;
        count = 0;
        added = false;
        ampl = 0.0f;
        setSize(0.1f, 0.1f);
    }
    
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 4096.0;
    }
    
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, 0.0f);
    }
    
    public EntityGrapple(World par1World, EntityLivingBase par2EntityLiving, EnumHand hand) {
        super(par1World, par2EntityLiving);
        this.hand = EnumHand.MAIN_HAND;
        p = false;
        prevDist = 0;
        count = 0;
        added = false;
        ampl = 0.0f;
        setSize(0.1f, 0.1f);
        this.hand = hand;
    }
    
    public EntityGrapple(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
        hand = EnumHand.MAIN_HAND;
        p = false;
        prevDist = 0;
        count = 0;
        added = false;
        ampl = 0.0f;
        setSize(0.1f, 0.1f);
    }
    
    public void writeSpawnData(ByteBuf data) {
        int id = -1;
        if (getThrower() != null) {
            id = getThrower().getEntityId();
        }
        data.writeInt(id);
        data.writeBoolean(hand == EnumHand.MAIN_HAND);
    }
    
    public void readSpawnData(ByteBuf data) {
        int id = data.readInt();
        hand = (data.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
        try {
            if (id >= 0) {
                cthrower = (EntityLivingBase) world.getEntityByID(id);
            }
        }
        catch (Exception ex) {}
    }
    
    public EntityLivingBase getThrower() {
        if (cthrower != null) {
            return cthrower;
        }
        return super.getThrower();
    }
    
    protected float getGravityVelocity() {
        return getPulling() ? 0.0f : 0.03f;
    }
    
    public void entityInit() {
        super.entityInit();
    }
    
    public void setPulling() {
        p = true;
    }
    
    public boolean getPulling() {
        return p;
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (!getPulling() && !isDead && (ticksExisted > 30 || getThrower() == null)) {
            if (getThrower() != null) {
                EntityGrapple.grapples.remove(getThrower().getEntityId());
            }
            setDead();
        }
        if (getThrower() != null) {
            if (!world.isRemote && !isDead && !added) {
                if (EntityGrapple.grapples.containsKey(getThrower().getEntityId())) {
                    int ii = EntityGrapple.grapples.get(getThrower().getEntityId());
                    if (ii != getEntityId()) {
                        Entity e = world.getEntityByID(ii);
                        if (e != null) {
                            e.setDead();
                        }
                    }
                }
                EntityGrapple.grapples.put(getThrower().getEntityId(), getEntityId());
                added = true;
            }
            try {
                if (getThrower() != null && EntityGrapple.grapples.containsKey(getThrower().getEntityId()) && EntityGrapple.grapples.get(getThrower().getEntityId()) != getEntityId()) {
                    setDead();
                }
            }
            catch (Exception ex) {}
            double dis = getThrower().getDistance(this);
            if (getThrower() != null && getPulling() && !isDead) {
                if (getThrower().isSneaking()) {
                    EntityGrapple.grapples.remove(getThrower().getEntityId());
                    setDead();
                }
                else {
                    if (!world.isRemote && getThrower() instanceof EntityPlayerMP) {
                        ((EntityPlayerMP) getThrower()).connection.floatingTickCount = 0;
                    }
                    getThrower().fallDistance = 0.0f;
                    double mx = posX - getThrower().posX;
                    double my = posY - getThrower().posY;
                    double mz = posZ - getThrower().posZ;
                    double dd = dis;
                    if (dis < 8.0) {
                        dd = dis * (8.0 - dis);
                    }
                    dd = Math.max(1.0E-9, dd);
                    mx /= dd * 5.0;
                    my /= dd * 5.0;
                    mz /= dd * 5.0;
                    Vec3d v2 = new Vec3d(mx, my, mz);
                    if (v2.lengthVector() > 0.25) {
                        v2 = v2.normalize();
                        mx = v2.x / 4.0;
                        my = v2.y / 4.0;
                        mz = v2.z / 4.0;
                    }
                    EntityLivingBase thrower = getThrower();
                    thrower.motionX += mx;
                    EntityLivingBase thrower2 = getThrower();
                    thrower2.motionY += my + 0.033;
                    EntityLivingBase thrower3 = getThrower();
                    thrower3.motionZ += mz;
                    if (!boost) {
                        EntityLivingBase thrower4 = getThrower();
                        thrower4.motionY += 0.4000000059604645;
                        boost = true;
                    }
                    int d = (int)(dis / 2.0);
                    if (d == prevDist) {
                        ++count;
                    }
                    else {
                        count = 0;
                    }
                    prevDist = d;
                }
            }
            if (world.isRemote) {
                if (!getPulling()) {
                    ampl += 0.02f;
                }
                else {
                    ampl *= 0.66f;
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 6) {
            setPulling();
            motionX = 0.0;
            motionY = 0.0;
            motionZ = 0.0;
        }
    }
    
    protected void onImpact(RayTraceResult mop) {
        if (!world.isRemote) {
            setPulling();
            motionX = 0.0;
            motionY = 0.0;
            motionZ = 0.0;
            posX = mop.hitVec.x;
            posY = mop.hitVec.y;
            posZ = mop.hitVec.z;
            world.setEntityState(this, (byte)6);
        }
    }
    
    static {
        EntityGrapple.grapples = new HashMap<Integer, Integer>();
    }
}
