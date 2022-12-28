package thaumcraft.common.entities.monster.cult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.monster.boss.EntityCultistLeader;


public class EntityCultist extends EntityMob
{
    public static ResourceLocation LOOT;
    
    public EntityCultist(World p_i1745_1_) {
        super(p_i1745_1_);
        setSize(0.6f, 1.8f);
        experienceValue = 10;
        ((PathNavigateGround) getNavigator()).setBreakDoors(true);
        setDropChance(EntityEquipmentSlot.CHEST, 0.05f);
        setDropChance(EntityEquipmentSlot.FEET, 0.05f);
        setDropChance(EntityEquipmentSlot.HEAD, 0.05f);
        setDropChance(EntityEquipmentSlot.LEGS, 0.05f);
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
    }
    
    protected void entityInit() {
        super.entityInit();
    }
    
    public boolean canPickUpLoot() {
        return false;
    }
    
    protected boolean isValidLightLevel() {
        return true;
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected ResourceLocation getLootTable() {
        return EntityCultist.LOOT;
    }
    
    protected void setLoot(DifficultyInstance diff) {
    }
    
    protected void setEnchantmentBasedOnDifficulty(DifficultyInstance diff) {
    }
    
    public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData data) {
        setLoot(diff);
        setEnchantmentBasedOnDifficulty(diff);
        return super.onInitialSpawn(diff, data);
    }
    
    protected boolean canDespawn() {
        return true;
    }
    
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("HomeD")) {
            setHomePosAndDistance(new BlockPos(nbt.getInteger("HomeX"), nbt.getInteger("HomeY"), nbt.getInteger("HomeZ")), nbt.getInteger("HomeD"));
        }
    }
    
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        if (getHomePosition() != null && getMaximumHomeDistance() > 0.0f) {
            nbt.setInteger("HomeD", (int) getMaximumHomeDistance());
            nbt.setInteger("HomeX", getHomePosition().getX());
            nbt.setInteger("HomeY", getHomePosition().getY());
            nbt.setInteger("HomeZ", getHomePosition().getZ());
        }
    }
    
    public boolean isOnSameTeam(Entity el) {
        return el instanceof EntityCultist || el instanceof EntityCultistLeader;
    }
    
    public boolean canAttackClass(Class clazz) {
        return clazz != EntityCultistCleric.class && clazz != EntityCultistLeader.class && clazz != EntityCultistKnight.class && super.canAttackClass(clazz);
    }
    
    public void spawnExplosionParticle() {
        if (world.isRemote) {
            for (int i = 0; i < 20; ++i) {
                double d0 = rand.nextGaussian() * 0.05;
                double d2 = rand.nextGaussian() * 0.05;
                double d3 = rand.nextGaussian() * 0.05;
                double d4 = 2.0;
                FXDispatcher.INSTANCE.cultistSpawn(posX + rand.nextFloat() * width * 2.0f - width + d0 * d4, posY + rand.nextFloat() * height + d2 * d4, posZ + rand.nextFloat() * width * 2.0f - width + d3 * d4, d0, d2, d3);
            }
        }
        else {
            world.setEntityState(this, (byte)20);
        }
    }
    
    static {
        LOOT = LootTableList.register(new ResourceLocation("thaumcraft", "cultist"));
    }
}
