// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.network.datasync.DataParameter;
import thaumcraft.api.entities.IEldritchMob;
import net.minecraft.entity.monster.EntitySpider;

public class EntityMindSpider extends EntitySpider implements IEldritchMob
{
    private int lifeSpan;
    private static final DataParameter<Boolean> HARMLESS;
    private static final DataParameter<String> VIEWER;
    
    public EntityMindSpider(final World par1World) {
        super(par1World);
        this.lifeSpan = Integer.MAX_VALUE;
        this.setSize(0.7f, 0.5f);
        this.experienceValue = 1;
    }
    
    @Nullable
    protected ResourceLocation getLootTable() {
        return null;
    }
    
    public float getEyeHeight() {
        return 0.45f;
    }
    
    protected int getExperiencePoints(final EntityPlayer p_70693_1_) {
        return this.isHarmless() ? 0 : super.getExperiencePoints(p_70693_1_);
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
    }
    
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(EntityMindSpider.HARMLESS, false);
        this.getDataManager().register(EntityMindSpider.VIEWER, String.valueOf(""));
    }
    
    public String getViewer() {
        return (String)this.getDataManager().get((DataParameter)EntityMindSpider.VIEWER);
    }
    
    public void setViewer(final String player) {
        this.getDataManager().set(EntityMindSpider.VIEWER, String.valueOf(player));
    }
    
    public boolean isHarmless() {
        return (boolean)this.getDataManager().get((DataParameter)EntityMindSpider.HARMLESS);
    }
    
    public void setHarmless(final boolean h) {
        if (h) {
            this.lifeSpan = 1200;
        }
        this.getDataManager().set(EntityMindSpider.HARMLESS, h);
    }
    
    protected float getSoundPitch() {
        return 0.7f;
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (!this.world.isRemote && this.ticksExisted > this.lifeSpan) {
            this.setDead();
        }
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected void dropFewItems(final boolean p_70628_1_, final int p_70628_2_) {
    }
    
    public boolean doesEntityNotTriggerPressurePlate() {
        return true;
    }
    
    protected boolean canTriggerWalking() {
        return false;
    }
    
    public boolean attackEntityAsMob(final Entity p_70652_1_) {
        return !this.isHarmless() && super.attackEntityAsMob(p_70652_1_);
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setHarmless(nbt.getBoolean("harmless"));
        this.setViewer(nbt.getString("viewer"));
    }
    
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("harmless", this.isHarmless());
        nbt.setString("viewer", this.getViewer());
    }
    
    public IEntityLivingData onInitialSpawn(final DifficultyInstance p_180482_1_, final IEntityLivingData p_180482_2_) {
        return p_180482_2_;
    }
    
    static {
        HARMLESS = EntityDataManager.createKey(EntityMindSpider.class, DataSerializers.BOOLEAN);
        VIEWER = EntityDataManager.createKey(EntityMindSpider.class, DataSerializers.STRING);
    }
}
