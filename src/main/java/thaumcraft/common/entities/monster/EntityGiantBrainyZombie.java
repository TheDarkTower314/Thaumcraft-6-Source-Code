package thaumcraft.common.entities.monster;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.init.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;


public class EntityGiantBrainyZombie extends EntityBrainyZombie
{
    private static DataParameter<Float> ANGER;
    
    public EntityGiantBrainyZombie(World world) {
        super(world);
        experienceValue = 15;
        tasks.addTask(2, new EntityAILeapAtTarget(this, 0.4f));
    }
    
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (getAnger() > 1.0f) {
            setAnger(getAnger() - 0.002f);
            setSize(0.6f + getAnger() * 0.6f, 1.95f + getAnger() * 1.95f);
            multiplySize(1.0f);
        }
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0f + (getAnger() - 1.0f) * 5.0f);
    }
    
    public float getEyeHeight() {
        float f = 1.74f + getAnger() * 1.74f;
        if (isChild()) {
            f -= (float)0.81;
        }
        return f;
    }
    
    protected void entityInit() {
        super.entityInit();
        getDataManager().register(EntityGiantBrainyZombie.ANGER, 0.0f);
    }
    
    public float getAnger() {
        return (float) getDataManager().get((DataParameter)EntityGiantBrainyZombie.ANGER);
    }
    
    public void setAnger(float par1) {
        getDataManager().set(EntityGiantBrainyZombie.ANGER, par1);
    }
    
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        setAnger(Math.min(2.0f, getAnger() + 0.1f));
        return super.attackEntityFrom(par1DamageSource, par2);
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0);
    }
    
    protected void dropFewItems(boolean flag, int i) {
        for (int a = 0; a < 6; ++a) {
            if (world.rand.nextBoolean()) {
                dropItem(Items.ROTTEN_FLESH, 2);
            }
        }
        for (int a = 0; a < 6; ++a) {
            if (world.rand.nextBoolean()) {
                dropItem(Items.ROTTEN_FLESH, 2);
            }
        }
    }
    
    static {
        ANGER = EntityDataManager.createKey(EntityGiantBrainyZombie.class, DataSerializers.FLOAT);
    }
}
