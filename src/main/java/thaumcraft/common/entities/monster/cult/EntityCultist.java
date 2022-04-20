// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.cult;

import net.minecraft.world.storage.loot.LootTableList;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.monster.boss.EntityCultistLeader;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.item.Item;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.monster.EntityMob;

public class EntityCultist extends EntityMob
{
    public static final ResourceLocation LOOT;
    
    public EntityCultist(final World p_i1745_1_) {
        super(p_i1745_1_);
        this.setSize(0.6f, 1.8f);
        this.experienceValue = 10;
        ((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
        this.setDropChance(EntityEquipmentSlot.CHEST, 0.05f);
        this.setDropChance(EntityEquipmentSlot.FEET, 0.05f);
        this.setDropChance(EntityEquipmentSlot.HEAD, 0.05f);
        this.setDropChance(EntityEquipmentSlot.LEGS, 0.05f);
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
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
    
    protected void setLoot(final DifficultyInstance diff) {
    }
    
    protected void setEnchantmentBasedOnDifficulty(final DifficultyInstance diff) {
    }
    
    public IEntityLivingData onInitialSpawn(final DifficultyInstance diff, final IEntityLivingData data) {
        this.setLoot(diff);
        this.setEnchantmentBasedOnDifficulty(diff);
        return super.onInitialSpawn(diff, data);
    }
    
    protected boolean canDespawn() {
        return true;
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("HomeD")) {
            this.setHomePosAndDistance(new BlockPos(nbt.getInteger("HomeX"), nbt.getInteger("HomeY"), nbt.getInteger("HomeZ")), nbt.getInteger("HomeD"));
        }
    }
    
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        if (this.getHomePosition() != null && this.getMaximumHomeDistance() > 0.0f) {
            nbt.setInteger("HomeD", (int)this.getMaximumHomeDistance());
            nbt.setInteger("HomeX", this.getHomePosition().getX());
            nbt.setInteger("HomeY", this.getHomePosition().getY());
            nbt.setInteger("HomeZ", this.getHomePosition().getZ());
        }
    }
    
    public boolean isOnSameTeam(final Entity el) {
        return el instanceof EntityCultist || el instanceof EntityCultistLeader;
    }
    
    public boolean canAttackClass(final Class clazz) {
        return clazz != EntityCultistCleric.class && clazz != EntityCultistLeader.class && clazz != EntityCultistKnight.class && super.canAttackClass(clazz);
    }
    
    public void spawnExplosionParticle() {
        if (this.world.isRemote) {
            for (int i = 0; i < 20; ++i) {
                final double d0 = this.rand.nextGaussian() * 0.05;
                final double d2 = this.rand.nextGaussian() * 0.05;
                final double d3 = this.rand.nextGaussian() * 0.05;
                final double d4 = 2.0;
                FXDispatcher.INSTANCE.cultistSpawn(this.posX + this.rand.nextFloat() * this.width * 2.0f - this.width + d0 * d4, this.posY + this.rand.nextFloat() * this.height + d2 * d4, this.posZ + this.rand.nextFloat() * this.width * 2.0f - this.width + d3 * d4, d0, d2, d3);
            }
        }
        else {
            this.world.setEntityState(this, (byte)20);
        }
    }
    
    static {
        LOOT = LootTableList.register(new ResourceLocation("thaumcraft", "cultist"));
    }
}
