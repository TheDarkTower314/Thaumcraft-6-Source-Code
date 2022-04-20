// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.parts;

import thaumcraft.common.golems.ai.AIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.SharedMonsterAttributes;
import thaumcraft.common.entities.projectile.EntityGolemDart;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.GolemArm;

public class GolemArmDart implements GolemArm.IArmFunction
{
    @Override
    public void onMeleeAttack(final IGolemAPI golem, final Entity target) {
    }
    
    @Override
    public void onRangedAttack(final IGolemAPI golem, final EntityLivingBase target, final float range) {
        final EntityGolemDart entityarrow = new EntityGolemDart(golem.getGolemWorld(), golem.getGolemEntity());
        final float dmg = (float)golem.getGolemEntity().getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() / 3.0f;
        entityarrow.setDamage(dmg + range + golem.getGolemWorld().rand.nextGaussian() * 0.25);
        final double d0 = target.posX - golem.getGolemEntity().posX;
        final double d2 = target.getEntityBoundingBox().minY + target.getEyeHeight() + range * range - entityarrow.posY;
        final double d3 = target.posZ - golem.getGolemEntity().posZ;
        entityarrow.shoot(d0, d2, d3, 1.6f, 3.0f);
        golem.getGolemWorld().spawnEntity(entityarrow);
        golem.getGolemEntity().playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, 1.0f / (golem.getGolemWorld().rand.nextFloat() * 0.4f + 0.8f));
    }
    
    @Override
    public EntityAIAttackRanged getRangedAttackAI(final IRangedAttackMob golem) {
        return new AIArrowAttack(golem, 1.0, 20, 25, 16.0f);
    }
    
    @Override
    public void onUpdateTick(final IGolemAPI golem) {
    }
}
