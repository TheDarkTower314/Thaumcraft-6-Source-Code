package thaumcraft.common.entities.monster.tainted;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import thaumcraft.api.entities.ITaintedMob;


public class EntityTaintacleSmall extends EntityTaintacle implements ITaintedMob
{
    int lifetime;
    
    public EntityTaintacleSmall(World par1World) {
        super(par1World);
        lifetime = 200;
        setSize(0.22f, 1.0f);
        experienceValue = 0;
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (lifetime-- <= 0) {
            damageEntity(DamageSource.MAGIC, 10.0f);
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
    protected void dropFewItems(boolean flag, int i) {
    }
}
