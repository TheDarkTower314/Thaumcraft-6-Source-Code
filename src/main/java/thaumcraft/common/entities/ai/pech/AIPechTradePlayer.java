package thaumcraft.common.entities.ai.pech;
import net.minecraft.entity.ai.EntityAIBase;
import thaumcraft.common.entities.monster.EntityPech;


public class AIPechTradePlayer extends EntityAIBase
{
    private EntityPech villager;
    
    public AIPechTradePlayer(EntityPech par1EntityVillager) {
        villager = par1EntityVillager;
        setMutexBits(5);
    }
    
    public boolean shouldExecute() {
        return villager.isEntityAlive() && !villager.isInWater() && villager.isTamed() && villager.onGround && !villager.velocityChanged && villager.trading;
    }
    
    public void startExecuting() {
        villager.getNavigator().clearPath();
    }
    
    public void resetTask() {
        villager.trading = false;
    }
}
