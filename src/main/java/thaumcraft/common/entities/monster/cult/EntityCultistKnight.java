// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.cult;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.AbstractIllager;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import thaumcraft.common.entities.ai.combat.AICultistHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.world.World;

public class EntityCultistKnight extends EntityCultist
{
    public EntityCultistKnight(final World p_i1745_1_) {
        super(p_i1745_1_);
    }
    
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.0, false));
        this.tasks.addTask(4, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(5, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.8));
        this.tasks.addTask(7, new EntityAIWander(this, 0.8));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new AICultistHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityEldritchGuardian.class, true));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, AbstractIllager.class, true));
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
    }
    
    @Override
    protected void setLoot(final DifficultyInstance diff) {
        this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ItemsTC.crimsonPlateHelm));
        this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemsTC.crimsonPlateChest));
        this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ItemsTC.crimsonPlateLegs));
        this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(ItemsTC.crimsonBoots));
        if (this.rand.nextFloat() < ((this.world.getDifficulty() == EnumDifficulty.HARD) ? 0.05f : 0.01f)) {
            final int i = this.rand.nextInt(5);
            if (i == 0) {
                this.setHeldItem(this.getActiveHand(), new ItemStack(ItemsTC.voidSword));
                this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ItemsTC.crimsonRobeHelm));
            }
            else {
                this.setHeldItem(this.getActiveHand(), new ItemStack(ItemsTC.thaumiumSword));
                if (this.rand.nextBoolean()) {
                    this.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
                }
            }
        }
        else {
            this.setHeldItem(this.getActiveHand(), new ItemStack(Items.IRON_SWORD));
        }
    }
    
    @Override
    protected void setEnchantmentBasedOnDifficulty(final DifficultyInstance diff) {
        final float f = diff.getClampedAdditionalDifficulty();
        if (this.getHeldItemMainhand() != null && !this.getHeldItemMainhand().isEmpty() && this.rand.nextFloat() < 0.25f * f) {
            EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItemMainhand(), (int)(5.0f + f * this.rand.nextInt(18)), false);
        }
    }
}
