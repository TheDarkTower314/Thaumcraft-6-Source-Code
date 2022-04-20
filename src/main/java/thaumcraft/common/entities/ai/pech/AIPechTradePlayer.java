// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.ai.pech;

import thaumcraft.common.entities.monster.EntityPech;
import net.minecraft.entity.ai.EntityAIBase;

public class AIPechTradePlayer extends EntityAIBase
{
    private EntityPech villager;
    
    public AIPechTradePlayer(final EntityPech par1EntityVillager) {
        this.villager = par1EntityVillager;
        this.setMutexBits(5);
    }
    
    public boolean shouldExecute() {
        return this.villager.isEntityAlive() && !this.villager.isInWater() && this.villager.isTamed() && this.villager.onGround && !this.villager.velocityChanged && this.villager.trading;
    }
    
    public void startExecuting() {
        this.villager.getNavigator().clearPath();
    }
    
    public void resetTask() {
        this.villager.trading = false;
    }
}
