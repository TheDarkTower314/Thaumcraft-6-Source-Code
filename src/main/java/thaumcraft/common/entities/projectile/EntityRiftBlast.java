package thaumcraft.common.entities.projectile;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.codechicken.lib.vec.Quat;
import thaumcraft.common.lib.SoundsTC;


public class EntityRiftBlast extends EntityThrowable implements IEntityAdditionalSpawnData
{
    int targetID;
    EntityLivingBase target;
    public boolean red;
    public double[][] points;
    public float[][] colours;
    public double[] radii;
    int growing;
    ArrayList<Quat> vecs;
    
    public EntityRiftBlast(World par1World) {
        super(par1World);
        targetID = 0;
        red = false;
        growing = -1;
        vecs = new ArrayList<Quat>();
    }
    
    public EntityRiftBlast(World par1World, EntityLivingBase par2EntityLiving, EntityLivingBase t, boolean r) {
        super(par1World, par2EntityLiving);
        targetID = 0;
        red = false;
        growing = -1;
        vecs = new ArrayList<Quat>();
        target = t;
        red = r;
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
        data.writeBoolean(red);
    }
    
    public void readSpawnData(ByteBuf data) {
        int id = data.readInt();
        try {
            if (id >= 0) {
                target = (EntityLivingBase) world.getEntityByID(id);
            }
        }
        catch (Exception ex) {}
        red = data.readBoolean();
    }
    
    protected void onImpact(RayTraceResult mop) {
        if (!world.isRemote && getThrower() != null && mop.typeOfHit == RayTraceResult.Type.ENTITY) {
            mop.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, getThrower()), (float) getThrower().getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * (red ? 1.0f : 0.6f));
        }
        playSound(SoundsTC.shock, 1.0f, 1.0f + (rand.nextFloat() - rand.nextFloat()) * 0.2f);
        if (world.isRemote) {
            FXDispatcher.INSTANCE.burst(posX, posY, posZ, 6.0f);
        }
        setDead();
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (ticksExisted > (red ? 240 : 160)) {
            setDead();
        }
        if (target != null) {
            if (target == null || target.isDead) {
                setDead();
            }
            double d = getDistanceSq(target);
            double dx = target.posX - posX;
            double dy = target.getEntityBoundingBox().minY + target.height * 0.6 - posY;
            double dz = target.posZ - posZ;
            double d2 = 1.0;
            dx /= d;
            dy /= d;
            dz /= d;
            motionX += dx * d2;
            motionY += dy * d2;
            motionZ += dz * d2;
            motionX = MathHelper.clamp((float) motionX, -0.33f, 0.33f);
            motionY = MathHelper.clamp((float) motionY, -0.33f, 0.33f);
            motionZ = MathHelper.clamp((float) motionZ, -0.33f, 0.33f);
            if (world.isRemote) {
                Quat q = new Quat(0.1, posX + rand.nextGaussian() * 0.05, posY + rand.nextGaussian() * 0.05, posZ + rand.nextGaussian() * 0.05);
                vecs.add(q);
                FXDispatcher.INSTANCE.drawCurlyWisp(q.x, q.y, q.z, 0.0, 0.0, 0.0, 0.3f + rand.nextFloat() * 0.2f, rand.nextFloat(), rand.nextFloat() * 0.2f, rand.nextFloat() * 0.2f, 0.5f, null, 1, rand.nextInt(2), 0);
                if (vecs.size() > 9) {
                    vecs.remove(0);
                }
                points = new double[vecs.size()][3];
                colours = new float[vecs.size()][4];
                radii = new double[vecs.size()];
                int c = 0;
                if (vecs.size() > 1) {
                    float vv = (float)(3.141592653589793 / (float)(vecs.size() - 1));
                    for (Quat v : vecs) {
                        float variance = 1.0f + MathHelper.sin((c + ticksExisted) / 3.0f) * 0.2f;
                        float xx = MathHelper.sin((c + ticksExisted) / 6.0f) * 0.01f;
                        float yy = MathHelper.sin((c + ticksExisted) / 7.0f) * 0.01f;
                        float zz = MathHelper.sin((c + ticksExisted) / 8.0f) * 0.01f;
                        points[c][0] = v.x + xx;
                        points[c][1] = v.y + yy;
                        points[c][2] = v.z + zz;
                        radii[c] = v.s * variance;
                        double[] radii = this.radii;
                        int n = c;
                        radii[n] *= MathHelper.sin(c * vv);
                        colours[c][0] = 1.0f;
                        colours[c][1] = 0.0f;
                        colours[c][2] = 0.0f;
                        colours[c][3] = 1.0f;
                        ++c;
                    }
                }
            }
        }
    }
    
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (isEntityInvulnerable(source)) {
            return false;
        }
        if (source.getTrueSource() != null) {
            Vec3d vec3 = source.getTrueSource().getLookVec();
            if (vec3 != null) {
                motionX = vec3.x;
                motionY = vec3.y;
                motionZ = vec3.z;
                motionX *= 0.9;
                motionY *= 0.9;
                motionZ *= 0.9;
                playSound(SoundsTC.zap, 1.0f, 1.0f + (rand.nextFloat() - rand.nextFloat()) * 0.2f);
            }
            return true;
        }
        return false;
    }
}
