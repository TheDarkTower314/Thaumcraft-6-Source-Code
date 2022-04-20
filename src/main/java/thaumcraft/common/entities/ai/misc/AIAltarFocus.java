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
        this.field_48399_a = 0;
        this.entity = par1EntityLiving;
        this.world = par1EntityLiving.world;
        this.setMutexBits(7);
    }
    
    public boolean shouldExecute() {
        return this.entity.getIsRitualist() && this.entity.getHomePosition() != null;
    }
    
    public void startExecuting() {
    }
    
    public void resetTask() {
    }
    
    public boolean shouldContinueExecuting() {
        return this.entity.getIsRitualist() && this.entity.getHomePosition() != null;
    }
    
    public void updateTask() {
        if (this.entity.getHomePosition() != null && this.entity.ticksExisted % 40 == 0 && (this.entity.getHomePosition().distanceSq(this.entity.posX, this.entity.posY, this.entity.posZ) > 16.0 || this.world.getBlockState(this.entity.getHomePosition()).getBlock() != BlocksTC.eldritch)) {
            this.entity.setIsRitualist(false);
        }
    }
}
