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
        this.hand = EnumHand.MAIN_HAND;
        this.p = false;
        this.prevDist = 0;
        this.count = 0;
        this.added = false;
        this.ampl = 0.0f;
        this.setSize(0.1f, 0.1f);
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
        this.p = false;
        this.prevDist = 0;
        this.count = 0;
        this.added = false;
        this.ampl = 0.0f;
        this.setSize(0.1f, 0.1f);
        this.hand = hand;
    }
    
    public EntityGrapple(final World par1World, final double par2, final double par4, final double par6) {
        super(par1World, par2, par4, par6);
        this.hand = EnumHand.MAIN_HAND;
        this.p = false;
        this.prevDist = 0;
        this.count = 0;
        this.added = false;
        this.ampl = 0.0f;
        this.setSize(0.1f, 0.1f);
    }
    
    public void writeSpawnData(final ByteBuf data) {
        int id = -1;
        if (this.getThrower() != null) {
            id = this.getThrower().getEntityId();
        }
        data.writeInt(id);
        data.writeBoolean(this.hand == EnumHand.MAIN_HAND);
    }
    
    public void readSpawnData(final ByteBuf data) {
        final int id = data.readInt();
        this.hand = (data.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
        try {
            if (id >= 0) {
                this.cthrower = (EntityLivingBase)this.world.getEntityByID(id);
            }
        }
        catch (final Exception ex) {}
    }
    
    public EntityLivingBase getThrower() {
        if (this.cthrower != null) {
            return this.cthrower;
        }
        return super.getThrower();
    }
    
    protected float getGravityVelocity() {
        return this.getPulling() ? 0.0f : 0.03f;
    }
    
    public void entityInit() {
        super.entityInit();
    }
    
    public void setPulling() {
        this.p = true;
    }
    
    public boolean getPulling() {
        return this.p;
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (!this.getPulling() && !this.isDead && (this.ticksExisted > 30 || this.getThrower() == null)) {
            if (this.getThrower() != null) {
                EntityGrapple.grapples.remove(this.getThrower().getEntityId());
            }
            this.setDead();
        }
        if (this.getThrower() != null) {
            if (!this.world.isRemote && !this.isDead && !this.added) {
                if (EntityGrapple.grapples.containsKey(this.getThrower().getEntityId())) {
                    final int ii = EntityGrapple.grapples.get(this.getThrower().getEntityId());
                    if (ii != this.getEntityId()) {
                        final Entity e = this.world.getEntityByID(ii);
                        if (e != null) {
                            e.setDead();
                        }
                    }
                }
                EntityGrapple.grapples.put(this.getThrower().getEntityId(), this.getEntityId());
                this.added = true;
            }
            try {
                if (this.getThrower() != null && EntityGrapple.grapples.containsKey(this.getThrower().getEntityId()) && EntityGrapple.grapples.get(this.getThrower().getEntityId()) != this.getEntityId()) {
                    this.setDead();
                }
            }
            catch (final Exception ex) {}
            final double dis = this.getThrower().getDistance(this);
            if (this.getThrower() != null && this.getPulling() && !this.isDead) {
                if (this.getThrower().isSneaking()) {
                    EntityGrapple.grapples.remove(this.getThrower().getEntityId());
                    this.setDead();
                }
                else {
                    if (!this.world.isRemote && this.getThrower() instanceof EntityPlayerMP) {
                        ((EntityPlayerMP)this.getThrower()).connection.floatingTickCount = 0;
                    }
                    this.getThrower().fallDistance = 0.0f;
                    double mx = this.posX - this.getThrower().posX;
                    double my = this.posY - this.getThrower().posY;
                    double mz = this.posZ - this.getThrower().posZ;
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
                    final EntityLivingBase thrower = this.getThrower();
                    thrower.motionX += mx;
                    final EntityLivingBase thrower2 = this.getThrower();
                    thrower2.motionY += my + 0.033;
                    final EntityLivingBase thrower3 = this.getThrower();
                    thrower3.motionZ += mz;
                    if (!this.boost) {
                        final EntityLivingBase thrower4 = this.getThrower();
                        thrower4.motionY += 0.4000000059604645;
                        this.boost = true;
                    }
                    final int d = (int)(dis / 2.0);
                    if (d == this.prevDist) {
                        ++this.count;
                    }
                    else {
                        this.count = 0;
                    }
                    this.prevDist = d;
                }
            }
            if (this.world.isRemote) {
                if (!this.getPulling()) {
                    this.ampl += 0.02f;
                }
                else {
                    this.ampl *= 0.66f;
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(final byte id) {
        if (id == 6) {
            this.setPulling();
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
        }
    }
    
    protected void onImpact(final RayTraceResult mop) {
        if (!this.world.isRemote) {
            this.setPulling();
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
            this.posX = mop.hitVec.x;
            this.posY = mop.hitVec.y;
            this.posZ = mop.hitVec.z;
            this.world.setEntityState(this, (byte)6);
        }
    }
    
    static {
        EntityGrapple.grapples = new HashMap<Integer, Integer>();
    }
}
