// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.projectile;

import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.Entity;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import java.util.HashMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.projectile.EntityThrowable;

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
    
    public EntityGrapple(final World par1World) {
        super(par1World);
        hand = EnumHand.MAIN_HAND;
        p = false;
        prevDist = 0;
        count = 0;
        added = false;
        ampl = 0.0f;
        setSize(0.1f, 0.1f);
    }
    
    public boolean isInRangeToRenderDist(final double distance) {
        return distance < 4096.0;
    }
    
    public void shoot(final double x, final double y, final double z, final float velocity, final float inaccuracy) {
        super.shoot(x, y, z, velocity, 0.0f);
    }
    
    public EntityGrapple(final World par1World, final EntityLivingBase par2EntityLiving, final EnumHand hand) {
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
    
    public EntityGrapple(final World par1World, final double par2, final double par4, final double par6) {
        super(par1World, par2, par4, par6);
        hand = EnumHand.MAIN_HAND;
        p = false;
        prevDist = 0;
        count = 0;
        added = false;
        ampl = 0.0f;
        setSize(0.1f, 0.1f);
    }
    
    public void writeSpawnData(final ByteBuf data) {
        int id = -1;
        if (getThrower() != null) {
            id = getThrower().getEntityId();
        }
        data.writeInt(id);
        data.writeBoolean(hand == EnumHand.MAIN_HAND);
    }
    
    public void readSpawnData(final ByteBuf data) {
        final int id = data.readInt();
        hand = (data.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
        try {
            if (id >= 0) {
                cthrower = (EntityLivingBase) world.getEntityByID(id);
            }
        }
        catch (final Exception ex) {}
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
                    final int ii = EntityGrapple.grapples.get(getThrower().getEntityId());
                    if (ii != getEntityId()) {
                        final Entity e = world.getEntityByID(ii);
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
            catch (final Exception ex) {}
            final double dis = getThrower().getDistance(this);
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
                    final EntityLivingBase thrower = getThrower();
                    thrower.motionX += mx;
                    final EntityLivingBase thrower2 = getThrower();
                    thrower2.motionY += my + 0.033;
                    final EntityLivingBase thrower3 = getThrower();
                    thrower3.motionZ += mz;
                    if (!boost) {
                        final EntityLivingBase thrower4 = getThrower();
                        thrower4.motionY += 0.4000000059604645;
                        boost = true;
                    }
                    final int d = (int)(dis / 2.0);
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
    public void handleStatusUpdate(final byte id) {
        if (id == 6) {
            setPulling();
            motionX = 0.0;
            motionY = 0.0;
            motionZ = 0.0;
        }
    }
    
    protected void onImpact(final RayTraceResult mop) {
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
