// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.construct;

import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import com.google.common.base.Predicates;
import net.minecraft.util.EntitySelectors;
import com.google.common.base.Predicate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.entity.MoverType;
import thaumcraft.Thaumcraft;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.BlockRailPowered;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.block.BlockRailBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.item.ItemArrow;
import net.minecraft.util.EnumFacing;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.world.World;
import net.minecraft.entity.IRangedAttackMob;

public class EntityTurretCrossbow extends EntityOwnedConstruct implements IRangedAttackMob
{
    int loadProgressInt;
    boolean isLoadInProgress;
    float loadProgress;
    float prevLoadProgress;
    public float loadProgressForRender;
    boolean attackedLastTick;
    int attackCount;
    
    public EntityTurretCrossbow(final World worldIn) {
        super(worldIn);
        loadProgressInt = 0;
        isLoadInProgress = false;
        loadProgress = 0.0f;
        prevLoadProgress = 0.0f;
        loadProgressForRender = 0.0f;
        attackedLastTick = false;
        attackCount = 0;
        setSize(0.95f, 1.25f);
        stepHeight = 0.0f;
    }
    
    protected void initEntityAI() {
        tasks.addTask(1, new EntityAIAttackRanged(this, 0.0, 20, 60, 24.0f));
        tasks.addTask(2, new EntityAIWatchTarget(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(2, new EntityAINearestValidTarget(this, EntityLiving.class, 5, true, false, IMob.MOB_SELECTOR));
    }
    
    public EntityTurretCrossbow(final World worldIn, final BlockPos pos) {
        this(worldIn);
        setPositionAndRotation(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0f, 0.0f);
    }
    
    public void attackEntityWithRangedAttack(final EntityLivingBase target, final float range) {
        if (getHeldItemMainhand() != null && !getHeldItemMainhand().isEmpty() && getHeldItemMainhand().getCount() > 0) {
            final EntityTippedArrow entityarrow = new EntityTippedArrow(world, this);
            entityarrow.setDamage(2.25 + range * 2.0f + rand.nextGaussian() * 0.25);
            entityarrow.setPotionEffect(getHeldItemMainhand());
            final Vec3d vec3d = getLook(1.0f);
            if (!isRiding()) {
                final EntityTippedArrow entityTippedArrow = entityarrow;
                entityTippedArrow.posX -= vec3d.x * 0.8999999761581421;
                final EntityTippedArrow entityTippedArrow2 = entityarrow;
                entityTippedArrow2.posY -= vec3d.y * 0.8999999761581421;
                final EntityTippedArrow entityTippedArrow3 = entityarrow;
                entityTippedArrow3.posZ -= vec3d.z * 0.8999999761581421;
            }
            else {
                final EntityTippedArrow entityTippedArrow4 = entityarrow;
                entityTippedArrow4.posX += vec3d.x * 1.75;
                final EntityTippedArrow entityTippedArrow5 = entityarrow;
                entityTippedArrow5.posY += vec3d.y * 1.75;
                final EntityTippedArrow entityTippedArrow6 = entityarrow;
                entityTippedArrow6.posZ += vec3d.z * 1.75;
            }
            entityarrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
            final double d0 = target.posX - posX;
            final double d2 = target.getEntityBoundingBox().minY + target.getEyeHeight() + range * range * 3.0f - entityarrow.posY;
            final double d3 = target.posZ - posZ;
            entityarrow.shoot(d0, d2, d3, 2.0f, 2.0f);
            world.spawnEntity(entityarrow);
            world.setEntityState(this, (byte)16);
            playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, 1.0f / (getRNG().nextFloat() * 0.4f + 0.8f));
            getHeldItemMainhand().shrink(1);
            if (getHeldItemMainhand().getCount() <= 0) {
                setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(final byte par1) {
        if (par1 == 16) {
            if (!isSwingInProgress) {
                swingProgressInt = -1;
                isSwingInProgress = true;
            }
        }
        else if (par1 == 17) {
            if (!isLoadInProgress) {
                loadProgressInt = -1;
                isLoadInProgress = true;
            }
        }
        else {
            super.handleStatusUpdate(par1);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public float getLoadProgress(final float pt) {
        float f1 = loadProgress - prevLoadProgress;
        if (f1 < 0.0f) {
            ++f1;
        }
        return prevLoadProgress + f1 * pt;
    }
    
    protected void updateArmSwingProgress() {
        if (isSwingInProgress) {
            ++swingProgressInt;
            if (swingProgressInt >= 6) {
                swingProgressInt = 0;
                isSwingInProgress = false;
            }
        }
        else {
            swingProgressInt = 0;
        }
        swingProgress = swingProgressInt / 6.0f;
        if (isLoadInProgress) {
            ++loadProgressInt;
            if (loadProgressInt >= 10) {
                loadProgressInt = 0;
                isLoadInProgress = false;
            }
        }
        else {
            loadProgressInt = 0;
        }
        loadProgress = loadProgressInt / 10.0f;
    }
    
    public void onEntityUpdate() {
        prevLoadProgress = loadProgress;
        if (!world.isRemote && (getHeldItemMainhand() == null || getHeldItemMainhand().isEmpty() || getHeldItemMainhand().getCount() <= 0)) {
            final BlockPos p = getPosition().down();
            final TileEntity t = world.getTileEntity(p);
            if (t != null && t instanceof TileEntityDispenser && EnumFacing.getFront(t.getBlockMetadata() & 0x7) == EnumFacing.UP) {
                final TileEntityDispenser d = (TileEntityDispenser)t;
                for (int a = 0; a < d.getSizeInventory(); ++a) {
                    if (d.getStackInSlot(a) != null && !d.getStackInSlot(a).isEmpty() && d.getStackInSlot(a).getItem() instanceof ItemArrow) {
                        setHeldItem(EnumHand.MAIN_HAND, d.decrStackSize(a, d.getStackInSlot(a).getCount()));
                        playSound(SoundsTC.ticks, 1.0f, 1.0f);
                        world.setEntityState(this, (byte)17);
                        break;
                    }
                }
            }
        }
        super.onEntityUpdate();
    }
    
    @Override
    public Team getTeam() {
        if (isOwned()) {
            final EntityLivingBase entitylivingbase = getOwnerEntity();
            if (entitylivingbase != null) {
                return entitylivingbase.getTeam();
            }
        }
        return super.getTeam();
    }
    
    public float getEyeHeight() {
        return height * 0.66f;
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(24.0);
    }
    
    public int getTotalArmorValue() {
        return 2;
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (getAttackTarget() != null && (getAttackTarget().isDead || isOnSameTeam(getAttackTarget()))) {
            setAttackTarget(null);
        }
        if (!world.isRemote) {
            rotationYaw = rotationYawHead;
            if (ticksExisted % 80 == 0) {
                heal(1.0f);
            }
            final int k = MathHelper.floor(posX);
            int l = MathHelper.floor(posY);
            final int i1 = MathHelper.floor(posZ);
            if (BlockRailBase.isRailBlock(world, new BlockPos(k, l - 1, i1))) {
                --l;
            }
            final BlockPos blockpos = new BlockPos(k, l, i1);
            final IBlockState iblockstate = world.getBlockState(blockpos);
            if (BlockRailBase.isRailBlock(iblockstate) && iblockstate.getBlock() == BlocksTC.activatorRail) {
                final boolean ac = (boolean)iblockstate.getValue((IProperty)BlockRailPowered.POWERED);
                setNoAI(ac);
            }
        }
        else {
            updateArmSwingProgress();
        }
    }
    
    public boolean canBePushed() {
        return true;
    }
    
    public boolean canBeCollidedWith() {
        return true;
    }
    
    @Override
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
    }
    
    @Override
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
    }
    
    public boolean attackEntityFrom(final DamageSource source, final float amount) {
        rotationYaw += (float)(getRNG().nextGaussian() * 45.0);
        rotationPitch += (float)(getRNG().nextGaussian() * 20.0);
        return super.attackEntityFrom(source, amount);
    }
    
    public void knockBack(final Entity p_70653_1_, final float p_70653_2_, final double p_70653_3_, final double p_70653_5_) {
        super.knockBack(p_70653_1_, p_70653_2_, p_70653_3_ / 10.0, p_70653_5_ / 10.0);
        if (motionY > 0.1) {
            motionY = 0.1;
        }
    }
    
    @Override
    protected boolean processInteract(final EntityPlayer player, final EnumHand hand) {
        if (!world.isRemote && isOwner(player) && !isDead) {
            if (player.isSneaking()) {
                playSound(SoundsTC.zap, 1.0f, 1.0f);
                dropAmmo();
                entityDropItem(new ItemStack(ItemsTC.turretPlacer, 1, 0), 0.5f);
                setDead();
                player.swingArm(hand);
            }
            else {
                player.openGui(Thaumcraft.instance, 16, world, getEntityId(), 0, 0);
            }
            return true;
        }
        return super.processInteract(player, hand);
    }
    
    public void move(final MoverType mt, final double x, final double y, final double z) {
        super.move(mt, x / 20.0, y, z / 20.0);
    }
    
    @Override
    public void onDeath(final DamageSource cause) {
        super.onDeath(cause);
        if (!world.isRemote) {
            dropAmmo();
        }
    }
    
    protected void dropAmmo() {
        if (getHeldItemMainhand() != null && !getHeldItemMainhand().isEmpty()) {
            entityDropItem(getHeldItemMainhand(), 0.5f);
        }
    }
    
    protected void dropFewItems(final boolean p_70628_1_, final int p_70628_2_) {
        final float b = p_70628_2_ * 0.15f;
        if (rand.nextFloat() < 0.2f + b) {
            entityDropItem(new ItemStack(ItemsTC.mind), 0.5f);
        }
        if (rand.nextFloat() < 0.5f + b) {
            entityDropItem(new ItemStack(ItemsTC.mechanismSimple), 0.5f);
        }
        if (rand.nextFloat() < 0.5f + b) {
            entityDropItem(new ItemStack(BlocksTC.plankGreatwood), 0.5f);
        }
        if (rand.nextFloat() < 0.5f + b) {
            entityDropItem(new ItemStack(BlocksTC.plankGreatwood), 0.5f);
        }
    }
    
    protected RayTraceResult getRayTraceResult() {
        final float f = prevRotationPitch + (rotationPitch - prevRotationPitch);
        final float f2 = prevRotationYaw + (rotationYaw - prevRotationYaw);
        final double d0 = prevPosX + (posX - prevPosX);
        final double d2 = prevPosY + (posY - prevPosY) + getEyeHeight();
        final double d3 = prevPosZ + (posZ - prevPosZ);
        final Vec3d vec3 = new Vec3d(d0, d2, d3);
        final float f3 = MathHelper.cos(-f2 * 0.017453292f - 3.1415927f);
        final float f4 = MathHelper.sin(-f2 * 0.017453292f - 3.1415927f);
        final float f5 = -MathHelper.cos(-f * 0.017453292f);
        final float f6 = MathHelper.sin(-f * 0.017453292f);
        final float f7 = f4 * f5;
        final float f8 = f3 * f5;
        final double d4 = 5.0;
        final Vec3d vec4 = vec3.addVector(f7 * d4, f6 * d4, f8 * d4);
        return world.rayTraceBlocks(vec3, vec4, true, false, false);
    }
    
    public int getVerticalFaceSpeed() {
        return 20;
    }
    
    public void setSwingingArms(final boolean swingingArms) {
    }
    
    protected class EntityAIWatchTarget extends EntityAIBase
    {
        protected EntityLiving theWatcher;
        protected Entity closestEntity;
        private int lookTime;
        
        public EntityAIWatchTarget(final EntityLiving p_i1631_1_) {
            theWatcher = p_i1631_1_;
            setMutexBits(2);
        }
        
        public boolean shouldExecute() {
            if (theWatcher.getAttackTarget() != null) {
                closestEntity = theWatcher.getAttackTarget();
            }
            return closestEntity != null;
        }
        
        public boolean shouldContinueExecuting() {
            final float d = (float) getTargetDistance();
            return closestEntity.isEntityAlive() && theWatcher.getDistanceSq(closestEntity) <= d * d && lookTime > 0;
        }
        
        public void startExecuting() {
            lookTime = 40 + theWatcher.getRNG().nextInt(40);
        }
        
        public void resetTask() {
            closestEntity = null;
        }
        
        public void updateTask() {
            theWatcher.getLookHelper().setLookPosition(closestEntity.posX, closestEntity.posY + closestEntity.getEyeHeight(), closestEntity.posZ, 10.0f, (float) theWatcher.getVerticalFaceSpeed());
            --lookTime;
        }
        
        protected double getTargetDistance() {
            final IAttributeInstance iattributeinstance = theWatcher.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
            return (iattributeinstance == null) ? 16.0 : iattributeinstance.getAttributeValue();
        }
    }
    
    protected class EntityAINearestValidTarget extends EntityAITarget
    {
        protected final Class<? extends EntityLivingBase> targetClass;
        private final int targetChance;
        protected final EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
        protected Predicate<EntityLivingBase> targetEntitySelector;
        protected EntityLivingBase targetEntity;
        
        //public EntityAINearestValidTarget(final EntityTurretCrossbow turret, final EntityCreature creature, final Class cls, final boolean checkSight) {
        //    this(turret, creature, cls, checkSight, false);
        //}
        
        //public EntityAINearestValidTarget(final EntityTurretCrossbow turret, final EntityCreature creature, final Class cls, final boolean checkSight, final boolean onlyNearby) {
        //    this(turret, creature, cls, 10, checkSight, onlyNearby, null);
        //}
        
        public EntityAINearestValidTarget(final EntityCreature owner, final Class<? extends EntityLivingBase> cls, final int targetChance, final boolean checkSight, final boolean onlyNearby, final Predicate<Entity> tselector) {
            super(owner, checkSight, onlyNearby);
            targetClass = cls;
            this.targetChance = targetChance;
            theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(owner);
            setMutexBits(1);
            targetEntitySelector = new Predicate<EntityLivingBase>() {
                private static final String __OBFID = "CL_00001621";
                
                public boolean applySelection(final EntityLivingBase entity) {
                    if (tselector != null && !tselector.apply(entity)) {
                        return false;
                    }
                    if (entity instanceof EntityPlayer) {
                        double d0 = getTargetDistance();
                        if (entity.isSneaking()) {
                            d0 *= 0.800000011920929;
                        }
                        if (entity.isInvisible()) {
                            float f = ((EntityPlayer)entity).getArmorVisibility();
                            if (f < 0.1f) {
                                f = 0.1f;
                            }
                            d0 *= 0.7f * f;
                        }
                        if (entity.getDistance(taskOwner) > d0) {
                            return false;
                        }
                    }
                    return isSuitableTarget(entity, false);
                }
                
                public boolean apply(final EntityLivingBase p_apply_1_) {
                    return applySelection(p_apply_1_);
                }
            };
        }
        
        protected boolean isSuitableTarget(final EntityLivingBase p_75296_1_, final boolean p_75296_2_) {
            return isSuitableTarget(taskOwner, p_75296_1_, p_75296_2_, shouldCheckSight) && taskOwner.isWithinHomeDistanceFromPosition(new BlockPos(p_75296_1_));
        }
        
        public boolean shouldExecute() {
            if (targetChance > 0 && taskOwner.getRNG().nextInt(targetChance) != 0) {
                return false;
            }
            final double d0 = getTargetDistance();
            final List<EntityLivingBase> list = taskOwner.world.getEntitiesWithinAABB(targetClass, taskOwner.getEntityBoundingBox().grow(d0, 4.0, d0), Predicates.and(targetEntitySelector, EntitySelectors.NOT_SPECTATING));
            Collections.sort(list, theNearestAttackableTargetSorter);
            if (list.isEmpty()) {
                return false;
            }
            targetEntity = list.get(0);
            return true;
        }
        
        public void startExecuting() {
            taskOwner.setAttackTarget(targetEntity);
            super.startExecuting();
        }
        

        
        public class Sorter implements Comparator
        {
            private final Entity theEntity;
            private static final String __OBFID = "CL_00001622";
            
            public Sorter(final Entity p_i1662_1_) {
                theEntity = p_i1662_1_;
            }
            
            public int compare(final Entity p_compare_1_, final Entity p_compare_2_) {
                final double d0 = theEntity.getDistanceSq(p_compare_1_);
                final double d2 = theEntity.getDistanceSq(p_compare_2_);
                return (d0 < d2) ? -1 : ((d0 > d2) ? 1 : 0);
            }
            
            @Override
            public int compare(final Object p_compare_1_, final Object p_compare_2_) {
                return compare((Entity)p_compare_1_, (Entity)p_compare_2_);
            }
        }
    }
}
