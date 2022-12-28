package thaumcraft.common.entities.monster;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import thaumcraft.api.entities.IEldritchMob;


public class EntityMindSpider extends EntitySpider implements IEldritchMob
{
    private int lifeSpan;
    private static DataParameter<Boolean> HARMLESS;
    private static DataParameter<String> VIEWER;
    
    public EntityMindSpider(World par1World) {
        super(par1World);
        lifeSpan = Integer.MAX_VALUE;
        setSize(0.7f, 0.5f);
        experienceValue = 1;
    }
    
    @Nullable
    protected ResourceLocation getLootTable() {
        return null;
    }
    
    public float getEyeHeight() {
        return 0.45f;
    }
    
    protected int getExperiencePoints(EntityPlayer p_70693_1_) {
        return isHarmless() ? 0 : super.getExperiencePoints(p_70693_1_);
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
    }
    
    protected void entityInit() {
        super.entityInit();
        getDataManager().register(EntityMindSpider.HARMLESS, false);
        getDataManager().register(EntityMindSpider.VIEWER, String.valueOf(""));
    }
    
    public String getViewer() {
        return (String) getDataManager().get((DataParameter)EntityMindSpider.VIEWER);
    }
    
    public void setViewer(String player) {
        getDataManager().set(EntityMindSpider.VIEWER, String.valueOf(player));
    }
    
    public boolean isHarmless() {
        return (boolean) getDataManager().get((DataParameter)EntityMindSpider.HARMLESS);
    }
    
    public void setHarmless(boolean h) {
        if (h) {
            lifeSpan = 1200;
        }
        getDataManager().set(EntityMindSpider.HARMLESS, h);
    }
    
    protected float getSoundPitch() {
        return 0.7f;
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote && ticksExisted > lifeSpan) {
            setDead();
        }
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
    }
    
    public boolean doesEntityNotTriggerPressurePlate() {
        return true;
    }
    
    protected boolean canTriggerWalking() {
        return false;
    }
    
    public boolean attackEntityAsMob(Entity p_70652_1_) {
        return !isHarmless() && super.attackEntityAsMob(p_70652_1_);
    }
    
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setHarmless(nbt.getBoolean("harmless"));
        setViewer(nbt.getString("viewer"));
    }
    
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("harmless", isHarmless());
        nbt.setString("viewer", getViewer());
    }
    
    public IEntityLivingData onInitialSpawn(DifficultyInstance p_180482_1_, IEntityLivingData p_180482_2_) {
        return p_180482_2_;
    }
    
    static {
        HARMLESS = EntityDataManager.createKey(EntityMindSpider.class, DataSerializers.BOOLEAN);
        VIEWER = EntityDataManager.createKey(EntityMindSpider.class, DataSerializers.STRING);
    }
}
