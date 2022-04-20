// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.tainted;

import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;
import thaumcraft.api.entities.ITaintedMob;

public class EntityTaintacleSmall extends EntityTaintacle implements ITaintedMob
{
    int lifetime;
    
    public EntityTaintacleSmall(final World par1World) {
        super(par1World);
        this.lifetime = 200;
        this.setSize(0.22f, 1.0f);
        this.experienceValue = 0;
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.lifetime-- <= 0) {
            this.damageEntity(DamageSource.MAGIC, 10.0f);
        }
    }
    
    @Override
    public boolean getCanSpawnHere() {
        return false;
    }
    
    @Override
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    @Override
    protected void dropFewItems(final boolean flag, final int i) {
    }
}
