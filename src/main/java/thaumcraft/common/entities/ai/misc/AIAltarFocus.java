// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.ai.misc;

import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.world.World;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;
import net.minecraft.entity.ai.EntityAIBase;

public class AIAltarFocus extends EntityAIBase
{
    private EntityCultistCleric entity;
    private World world;
    int field_48399_a;
    
    public AIAltarFocus(final EntityCultistCleric par1EntityLiving) {
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
