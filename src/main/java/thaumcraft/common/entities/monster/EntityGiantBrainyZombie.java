// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.init.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.world.World;
import net.minecraft.network.datasync.DataParameter;

public class EntityGiantBrainyZombie extends EntityBrainyZombie
{
    private static final DataParameter<Float> ANGER;
    
    public EntityGiantBrainyZombie(final World world) {
        super(world);
        this.experienceValue = 15;
        this.tasks.addTask(2, new EntityAILeapAtTarget(this, 0.4f));
    }
    
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.getAnger() > 1.0f) {
            this.setAnger(this.getAnger() - 0.002f);
            this.setSize(0.6f + this.getAnger() * 0.6f, 1.95f + this.getAnger() * 1.95f);
            this.multiplySize(1.0f);
        }
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0f + (this.getAnger() - 1.0f) * 5.0f);
    }
    
    public float getEyeHeight() {
        float f = 1.74f + this.getAnger() * 1.74f;
        if (this.isChild()) {
            f -= (float)0.81;
        }
        return f;
    }
    
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(EntityGiantBrainyZombie.ANGER, 0.0f);
    }
    
    public float getAnger() {
        return (float)this.getDataManager().get((DataParameter)EntityGiantBrainyZombie.ANGER);
    }
    
    public void setAnger(final float par1) {
        this.getDataManager().set(EntityGiantBrainyZombie.ANGER, par1);
    }
    
    public boolean attackEntityFrom(final DamageSource par1DamageSource, final float par2) {
        this.setAnger(Math.min(2.0f, this.getAnger() + 0.1f));
        return super.attackEntityFrom(par1DamageSource, par2);
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0);
    }
    
    protected void dropFewItems(final boolean flag, final int i) {
        for (int a = 0; a < 6; ++a) {
            if (this.world.rand.nextBoolean()) {
                this.dropItem(Items.ROTTEN_FLESH, 2);
            }
        }
        for (int a = 0; a < 6; ++a) {
            if (this.world.rand.nextBoolean()) {
                this.dropItem(Items.ROTTEN_FLESH, 2);
            }
        }
    }
    
    static {
        ANGER = EntityDataManager.createKey(EntityGiantBrainyZombie.class, DataSerializers.FLOAT);
    }
}
