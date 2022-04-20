// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.projectile;

import java.util.List;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.entity.projectile.EntityThrowable;

public class EntityEldritchOrb extends EntityThrowable
{
    public EntityEldritchOrb(final World par1World) {
        super(par1World);
    }
    
    public EntityEldritchOrb(final World par1World, final EntityLivingBase p) {
        super(par1World, p);
        this.shoot(p, p.rotationPitch, p.rotationYaw, -5.0f, 0.75f, 0.0f);
    }
    
    protected float getGravityVelocity() {
        return 0.0f;
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted > 100) {
            this.setDead();
        }
    }
    
    protected void onImpact(final RayTraceResult mop) {
        if (!this.world.isRemote && this.getThrower() != null) {
            final List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this.getThrower(), this.getEntityBoundingBox().grow(2.0, 2.0, 2.0));
            for (int i = 0; i < list.size(); ++i) {
                final Entity entity1 = list.get(i);
                if (entity1 != null && entity1 instanceof EntityLivingBase && !((EntityLivingBase)entity1).isEntityUndead()) {
                    entity1.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.getThrower()), (float)this.getThrower().getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 0.666f);
                    try {
                        ((EntityLivingBase)entity1).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 160, 0));
                    }
                    catch (final Exception ex) {}
                }
            }
            this.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 0.5f, 2.6f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8f);
            this.setDead();
        }
    }
}
