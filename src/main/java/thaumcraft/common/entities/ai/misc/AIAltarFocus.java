package thaumcraft.common.entities.ai.misc;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;


public class AIAltarFocus extends EntityAIBase
{
    private EntityCultistCleric entity;
    private World world;
    int field_48399_a;
    
    public AIAltarFocus(EntityCultistCleric par1EntityLiving) {
        field_48399_a = 0;
        entity = par1EntityLiving;
        world = par1EntityLiving.world;
        setMutexBits(7);
    }
    
    public boolean shouldExecute() {
        return entity.getIsRitualist() && entity.getHomePosition() != null;
    }
    
    public void startExecuting() {
    }
    
    public void resetTask() {
    }
    
    public boolean shouldContinueExecuting() {
        return entity.getIsRitualist() && entity.getHomePosition() != null;
    }
    
    public void updateTask() {
        if (entity.getHomePosition() != null && entity.ticksExisted % 40 == 0 && (entity.getHomePosition().distanceSq(entity.posX, entity.posY, entity.posZ) > 16.0 || world.getBlockState(entity.getHomePosition()).getBlock() != BlocksTC.eldritch)) {
            entity.setIsRitualist(false);
        }
    }
}
