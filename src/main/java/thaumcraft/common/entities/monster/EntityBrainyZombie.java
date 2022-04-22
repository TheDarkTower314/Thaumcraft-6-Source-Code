// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster;

import net.minecraft.item.ItemStack;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.world.World;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityZombie;

public class EntityBrainyZombie extends EntityZombie
{
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
        getEntityAttribute(EntityBrainyZombie.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0);
    }
    
    public EntityBrainyZombie(final World world) {
        super(world);
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
    }
    
    public int getTotalArmorValue() {
        return super.getTotalArmorValue() + 1;
    }
    
    protected void dropLoot(final boolean wasRecentlyHit, final int lootingModifier, final DamageSource source) {
        if (world.rand.nextInt(10) - lootingModifier <= 4) {
            entityDropItem(new ItemStack(ItemsTC.brain), 1.5f);
        }
        super.dropLoot(wasRecentlyHit, lootingModifier, source);
    }
}
