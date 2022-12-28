package thaumcraft.common.entities.projectile;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;


public class EntityEldritchOrb extends EntityThrowable
{
    public EntityEldritchOrb(World par1World) {
        super(par1World);
    }
    
    public EntityEldritchOrb(World par1World, EntityLivingBase p) {
        super(par1World, p);
        shoot(p, p.rotationPitch, p.rotationYaw, -5.0f, 0.75f, 0.0f);
    }
    
    protected float getGravityVelocity() {
        return 0.0f;
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (ticksExisted > 100) {
            setDead();
        }
    }
    
    protected void onImpact(RayTraceResult mop) {
        if (!world.isRemote && getThrower() != null) {
            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(getThrower(), getEntityBoundingBox().grow(2.0, 2.0, 2.0));
            for (int i = 0; i < list.size(); ++i) {
                Entity entity1 = list.get(i);
                if (entity1 != null && entity1 instanceof EntityLivingBase && !((EntityLivingBase)entity1).isEntityUndead()) {
                    entity1.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, getThrower()), (float) getThrower().getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 0.666f);
                    try {
                        ((EntityLivingBase)entity1).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 160, 0));
                    }
                    catch (Exception ex) {}
                }
            }
            playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 0.5f, 2.6f + (rand.nextFloat() - rand.nextFloat()) * 0.8f);
            setDead();
        }
    }
}
