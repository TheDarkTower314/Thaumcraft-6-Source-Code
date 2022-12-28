package thaumcraft.common.entities.projectile;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.SoundsTC;


public class EntityGolemOrb extends EntityThrowable implements IEntityAdditionalSpawnData
{
    int targetID;
    EntityLivingBase target;
    public boolean red;
    
    public EntityGolemOrb(World par1World) {
        super(par1World);
        targetID = 0;
        red = false;
    }
    
    public EntityGolemOrb(World par1World, EntityLivingBase par2EntityLiving, EntityLivingBase t, boolean r) {
        super(par1World, par2EntityLiving);
        targetID = 0;
        red = false;
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
            FXDispatcher.INSTANCE.burst(posX, posY, posZ, 1.0f);
        }
        setDead();
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (ticksExisted > (red ? 240 : 160)) {
            setDead();
        }
        if (target != null) {
            double d = getDistanceSq(target);
            double dx = target.posX - posX;
            double dy = target.getEntityBoundingBox().minY + target.height * 0.6 - posY;
            double dz = target.posZ - posZ;
            double d2 = 0.2;
            dx /= d;
            dy /= d;
            dz /= d;
            motionX += dx * d2;
            motionY += dy * d2;
            motionZ += dz * d2;
            motionX = MathHelper.clamp((float) motionX, -0.25f, 0.25f);
            motionY = MathHelper.clamp((float) motionY, -0.25f, 0.25f);
            motionZ = MathHelper.clamp((float) motionZ, -0.25f, 0.25f);
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
