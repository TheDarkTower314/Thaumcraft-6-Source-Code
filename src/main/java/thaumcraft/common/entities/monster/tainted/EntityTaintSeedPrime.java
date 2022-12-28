package thaumcraft.common.entities.monster.tainted;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigItems;


public class EntityTaintSeedPrime extends EntityTaintSeed
{
    public EntityTaintSeedPrime(World par1World) {
        super(par1World);
        setSize(2.0f, 2.0f);
        experienceValue = 12;
    }
    
    @Override
    protected int getArea() {
        return 2;
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(150.0);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0);
    }
    
    @Override
    protected void dropFewItems(boolean flag, int i) {
        entityDropItem(ConfigItems.FLUX_CRYSTAL.copy(), height / 2.0f);
        if (rand.nextBoolean()) {
            entityDropItem(ConfigItems.FLUX_CRYSTAL.copy(), height / 2.0f);
        }
        if (rand.nextBoolean()) {
            entityDropItem(ConfigItems.FLUX_CRYSTAL.copy(), height / 2.0f);
        }
    }
}
