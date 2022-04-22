package thaumcraft.common.entities.ai.pech;

import thaumcraft.common.entities.monster.EntityPech;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

public class AINearestAttackableTargetPech extends EntityAINearestAttackableTarget
{
    public AINearestAttackableTargetPech(EntityCreature p_i45878_1_, Class p_i45878_2_, boolean p_i45878_3_) {
        super(p_i45878_1_, p_i45878_2_, p_i45878_3_);
    }
    
    public boolean shouldExecute() {
        return (!(taskOwner instanceof EntityPech) || ((EntityPech) taskOwner).getAnger() != 0) && super.shouldExecute();
    }
}
