// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.tainted;

import thaumcraft.common.config.ConfigItems;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityTaintSeedPrime extends EntityTaintSeed
{
    public EntityTaintSeedPrime(final World par1World) {
        super(par1World);
        this.setSize(2.0f, 2.0f);
        this.experienceValue = 12;
    }
    
    @Override
    protected int getArea() {
        return 2;
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(150.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0);
    }
    
    @Override
    protected void dropFewItems(final boolean flag, final int i) {
        this.entityDropItem(ConfigItems.FLUX_CRYSTAL.copy(), this.height / 2.0f);
        if (this.rand.nextBoolean()) {
            this.entityDropItem(ConfigItems.FLUX_CRYSTAL.copy(), this.height / 2.0f);
        }
        if (this.rand.nextBoolean()) {
            this.entityDropItem(ConfigItems.FLUX_CRYSTAL.copy(), this.height / 2.0f);
        }
    }
}
