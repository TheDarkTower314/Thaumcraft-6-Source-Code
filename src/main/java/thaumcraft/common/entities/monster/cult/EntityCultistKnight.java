package thaumcraft.common.entities.monster.cult;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.ai.combat.AICultistHurtByTarget;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;


public class EntityCultistKnight extends EntityCultist
{
    public EntityCultistKnight(World p_i1745_1_) {
        super(p_i1745_1_);
    }
    
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(3, new EntityAIAttackMelee(this, 1.0, false));
        tasks.addTask(4, new EntityAIRestrictOpenDoor(this));
        tasks.addTask(5, new EntityAIOpenDoor(this, true));
        tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.8));
        tasks.addTask(7, new EntityAIWander(this, 0.8));
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        tasks.addTask(8, new EntityAILookIdle(this));
        targetTasks.addTask(1, new AICultistHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityEldritchGuardian.class, true));
        targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, AbstractIllager.class, true));
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
    }
    
    @Override
    protected void setLoot(DifficultyInstance diff) {
        setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ItemsTC.crimsonPlateHelm));
        setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemsTC.crimsonPlateChest));
        setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ItemsTC.crimsonPlateLegs));
        setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(ItemsTC.crimsonBoots));
        if (rand.nextFloat() < ((world.getDifficulty() == EnumDifficulty.HARD) ? 0.05f : 0.01f)) {
            int i = rand.nextInt(5);
            if (i == 0) {
                setHeldItem(getActiveHand(), new ItemStack(ItemsTC.voidSword));
                setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ItemsTC.crimsonRobeHelm));
            }
            else {
                setHeldItem(getActiveHand(), new ItemStack(ItemsTC.thaumiumSword));
                if (rand.nextBoolean()) {
                    setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
                }
            }
        }
        else {
            setHeldItem(getActiveHand(), new ItemStack(Items.IRON_SWORD));
        }
    }
    
    @Override
    protected void setEnchantmentBasedOnDifficulty(DifficultyInstance diff) {
        float f = diff.getClampedAdditionalDifficulty();
        if (getHeldItemMainhand() != null && !getHeldItemMainhand().isEmpty() && rand.nextFloat() < 0.25f * f) {
            EnchantmentHelper.addRandomEnchantment(rand, getHeldItemMainhand(), (int)(5.0f + f * rand.nextInt(18)), false);
        }
    }
}
