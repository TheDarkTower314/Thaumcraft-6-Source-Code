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
        this.loadProgressInt = 0;
        this.isLoadInProgress = false;
        this.loadProgress = 0.0f;
        this.prevLoadProgress = 0.0f;
        this.loadProgressForRender = 0.0f;
        this.attackedLastTick = false;
        this.attackCount = 0;
        this.setSize(0.95f, 1.25f);
        this.stepHeight = 0.0f;
    }
    
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAIAttackRanged(this, 0.0, 20, 60, 24.0f));
        this.tasks.addTask(2, new EntityAIWatchTarget(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestValidTarget(this, EntityLiving.class, 5, true, false, IMob.MOB_SELECTOR));
    }
    
    public EntityTurretCrossbow(final World worldIn, final BlockPos pos) {
        this(worldIn);
        this.setPositionAndRotation(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0f, 0.0f);
    }
    
    public void attackEntityWithRangedAttack(final EntityLivingBase target, final float range) {
        if (this.getHeldItemMainhand() != null && !this.getHeldItemMainhand().isEmpty() && this.getHeldItemMainhand().getCount() > 0) {
            final EntityTippedArrow entityarrow = new EntityTippedArrow(this.world, this);
            entityarrow.setDamage(2.25 + range * 2.0f + this.rand.nextGaussian() * 0.25);
            entityarrow.setPotionEffect(this.getHeldItemMainhand());
            final Vec3d vec3d = this.getLook(1.0f);
            if (!this.isRiding()) {
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
            final double d0 = target.posX - this.posX;
            final double d2 = target.getEntityBoundingBox().minY + target.getEyeHeight() + range * range * 3.0f - entityarrow.posY;
            final double d3 = target.posZ - this.posZ;
            entityarrow.shoot(d0, d2, d3, 2.0f, 2.0f);
            this.world.spawnEntity(entityarrow);
            this.world.setEntityState(this, (byte)16);
            this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, 1.0f / (this.getRNG().nextFloat() * 0.4f + 0.8f));
            this.getHeldItemMainhand().shrink(1);
            if (this.getHeldItemMainhand().getCount() <= 0) {
                this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(final byte par1) {
        if (par1 == 16) {
            if (!this.isSwingInProgress) {
                this.swingProgressInt = -1;
                this.isSwingInProgress = true;
            }
        }
        else if (par1 == 17) {
            if (!this.isLoadInProgress) {
                this.loadProgressInt = -1;
                this.isLoadInProgress = true;
            }
        }
        else {
            super.handleStatusUpdate(par1);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public float getLoadProgress(final float pt) {
        float f1 = this.loadProgress - this.prevLoadProgress;
        if (f1 < 0.0f) {
            ++f1;
        }
        return this.prevLoadProgress + f1 * pt;
    }
    
    protected void updateArmSwingProgress() {
        if (this.isSwingInProgress) {
            ++this.swingProgressInt;
            if (this.swingProgressInt >= 6) {
                this.swingProgressInt = 0;
                this.isSwingInProgress = false;
            }
        }
        else {
            this.swingProgressInt = 0;
        }
        this.swingProgress = this.swingProgressInt / 6.0f;
        if (this.isLoadInProgress) {
            ++this.loadProgressInt;
            if (this.loadProgressInt >= 10) {
                this.loadProgressInt = 0;
                this.isLoadInProgress = false;
            }
        }
        else {
            this.loadProgressInt = 0;
        }
        this.loadProgress = this.loadProgressInt / 10.0f;
    }
    
    public void onEntityUpdate() {
        this.prevLoadProgress = this.loadProgress;
        if (!this.world.isRemote && (this.getHeldItemMainhand() == null || this.getHeldItemMainhand().isEmpty() || this.getHeldItemMainhand().getCount() <= 0)) {
            final BlockPos p = this.getPosition().down();
            final TileEntity t = this.world.getTileEntity(p);
            if (t != null && t instanceof TileEntityDispenser && EnumFacing.getFront(t.getBlockMetadata() & 0x7) == EnumFacing.UP) {
                final TileEntityDispenser d = (TileEntityDispenser)t;
                for (int a = 0; a < d.getSizeInventory(); ++a) {
                    if (d.getStackInSlot(a) != null && !d.getStackInSlot(a).isEmpty() && d.getStackInSlot(a).getItem() instanceof ItemArrow) {
                        this.setHeldItem(EnumHand.MAIN_HAND, d.decrStackSize(a, d.getStackInSlot(a).getCount()));
                        this.playSound(SoundsTC.ticks, 1.0f, 1.0f);
                        this.world.setEntityState(this, (byte)17);
                        break;
                    }
                }
            }
        }
        super.onEntityUpdate();
    }
    
    @Override
    public Team getTeam() {
        if (this.isOwned()) {
            final EntityLivingBase entitylivingbase = this.getOwnerEntity();
            if (entitylivingbase != null) {
                return entitylivingbase.getTeam();
            }
        }
        return super.getTeam();
    }
    
    public float getEyeHeight() {
        return this.height * 0.66f;
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(24.0);
    }
    
    public int getTotalArmorValue() {
        return 2;
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.getAttackTarget() != null && (this.getAttackTarget().isDead || this.isOnSameTeam(this.getAttackTarget()))) {
            this.setAttackTarget(null);
        }
        if (!this.world.isRemote) {
            this.rotationYaw = this.rotationYawHead;
            if (this.ticksExisted % 80 == 0) {
                this.heal(1.0f);
            }
            final int k = MathHelper.floor(this.posX);
            int l = MathHelper.floor(this.posY);
            final int i1 = MathHelper.floor(this.posZ);
            if (BlockRailBase.isRailBlock(this.world, new BlockPos(k, l - 1, i1))) {
                --l;
            }
            final BlockPos blockpos = new BlockPos(k, l, i1);
            final IBlockState iblockstate = this.world.getBlockState(blockpos);
            if (BlockRailBase.isRailBlock(iblockstate) && iblockstate.getBlock() == BlocksTC.activatorRail) {
                final boolean ac = (boolean)iblockstate.getValue((IProperty)BlockRailPowered.POWERED);
                this.setNoAI(ac);
            }
        }
        else {
            this.updateArmSwingProgress();
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
        this.rotationYaw += (float)(this.getRNG().nextGaussian() * 45.0);
        this.rotationPitch += (float)(this.getRNG().nextGaussian() * 20.0);
        return super.attackEntityFrom(source, amount);
    }
    
    public void knockBack(final Entity p_70653_1_, final float p_70653_2_, final double p_70653_3_, final double p_70653_5_) {
        super.knockBack(p_70653_1_, p_70653_2_, p_70653_3_ / 10.0, p_70653_5_ / 10.0);
        if (this.motionY > 0.1) {
            this.motionY = 0.1;
        }
    }
    
    @Override
    protected boolean processInteract(final EntityPlayer player, final EnumHand hand) {
        if (!this.world.isRemote && this.isOwner(player) && !this.isDead) {
            if (player.isSneaking()) {
                this.playSound(SoundsTC.zap, 1.0f, 1.0f);
                this.dropAmmo();
                this.entityDropItem(new ItemStack(ItemsTC.turretPlacer, 1, 0), 0.5f);
                this.setDead();
                player.swingArm(hand);
            }
            else {
                player.openGui(Thaumcraft.instance, 16, this.world, this.getEntityId(), 0, 0);
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
        if (!this.world.isRemote) {
            this.dropAmmo();
        }
    }
    
    protected void dropAmmo() {
        if (this.getHeldItemMainhand() != null && !this.getHeldItemMainhand().isEmpty()) {
            this.entityDropItem(this.getHeldItemMainhand(), 0.5f);
        }
    }
    
    protected void dropFewItems(final boolean p_70628_1_, final int p_70628_2_) {
        final float b = p_70628_2_ * 0.15f;
        if (this.rand.nextFloat() < 0.2f + b) {
            this.entityDropItem(new ItemStack(ItemsTC.mind), 0.5f);
        }
        if (this.rand.nextFloat() < 0.5f + b) {
            this.entityDropItem(new ItemStack(ItemsTC.mechanismSimple), 0.5f);
        }
        if (this.rand.nextFloat() < 0.5f + b) {
            this.entityDropItem(new ItemStack(BlocksTC.plankGreatwood), 0.5f);
        }
        if (this.rand.nextFloat() < 0.5f + b) {
            this.entityDropItem(new ItemStack(BlocksTC.plankGreatwood), 0.5f);
        }
    }
    
    protected RayTraceResult getRayTraceResult() {
        final float f = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch);
        final float f2 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw);
        final double d0 = this.prevPosX + (this.posX - this.prevPosX);
        final double d2 = this.prevPosY + (this.posY - this.prevPosY) + this.getEyeHeight();
        final double d3 = this.prevPosZ + (this.posZ - this.prevPosZ);
        final Vec3d vec3 = new Vec3d(d0, d2, d3);
        final float f3 = MathHelper.cos(-f2 * 0.017453292f - 3.1415927f);
        final float f4 = MathHelper.sin(-f2 * 0.017453292f - 3.1415927f);
        final float f5 = -MathHelper.cos(-f * 0.017453292f);
        final float f6 = MathHelper.sin(-f * 0.017453292f);
        final float f7 = f4 * f5;
        final float f8 = f3 * f5;
        final double d4 = 5.0;
        final Vec3d vec4 = vec3.addVector(f7 * d4, f6 * d4, f8 * d4);
        return this.world.rayTraceBlocks(vec3, vec4, true, false, false);
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
            this.theWatcher = p_i1631_1_;
            this.setMutexBits(2);
        }
        
        public boolean shouldExecute() {
            if (this.theWatcher.getAttackTarget() != null) {
                this.closestEntity = this.theWatcher.getAttackTarget();
            }
            return this.closestEntity != null;
        }
        
        public boolean shouldContinueExecuting() {
            final float d = (float)this.getTargetDistance();
            return this.closestEntity.isEntityAlive() && this.theWatcher.getDistanceSq(this.closestEntity) <= d * d && this.lookTime > 0;
        }
        
        public void startExecuting() {
            this.lookTime = 40 + this.theWatcher.getRNG().nextInt(40);
        }
        
        public void resetTask() {
            this.closestEntity = null;
        }
        
        public void updateTask() {
            this.theWatcher.getLookHelper().setLookPosition(this.closestEntity.posX, this.closestEntity.posY + this.closestEntity.getEyeHeight(), this.closestEntity.posZ, 10.0f, (float)this.theWatcher.getVerticalFaceSpeed());
            --this.lookTime;
        }
        
        protected double getTargetDistance() {
            final IAttributeInstance iattributeinstance = this.theWatcher.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
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
            this.targetClass = cls;
            this.targetChance = targetChance;
            this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(owner);
            this.setMutexBits(1);
            this.targetEntitySelector = new Predicate<EntityLivingBase>() {
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
                        if (entity.getDistance(EntityAINearestValidTarget.this.taskOwner) > d0) {
                            return false;
                        }
                    }
                    return EntityAINearestValidTarget.this.isSuitableTarget(entity, false);
                }
                
                public boolean apply(final EntityLivingBase p_apply_1_) {
                    return this.applySelection(p_apply_1_);
                }
            };
        }
        
        protected boolean isSuitableTarget(final EntityLivingBase p_75296_1_, final boolean p_75296_2_) {
            return isSuitableTarget(this.taskOwner, p_75296_1_, p_75296_2_, this.shouldCheckSight) && this.taskOwner.isWithinHomeDistanceFromPosition(new BlockPos(p_75296_1_));
        }
        
        public boolean shouldExecute() {
            if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
                return false;
            }
            final double d0 = this.getTargetDistance();
            final List<EntityLivingBase> list = this.taskOwner.world.getEntitiesWithinAABB(this.targetClass, this.taskOwner.getEntityBoundingBox().grow(d0, 4.0, d0), Predicates.and(targetEntitySelector, EntitySelectors.NOT_SPECTATING));
            Collections.sort(list, this.theNearestAttackableTargetSorter);
            if (list.isEmpty()) {
                return false;
            }
            this.targetEntity = list.get(0);
            return true;
        }
        
        public void startExecuting() {
            this.taskOwner.setAttackTarget(this.targetEntity);
            super.startExecuting();
        }
        

        
        public class Sorter implements Comparator
        {
            private final Entity theEntity;
            private static final String __OBFID = "CL_00001622";
            
            public Sorter(final Entity p_i1662_1_) {
                this.theEntity = p_i1662_1_;
            }
            
            public int compare(final Entity p_compare_1_, final Entity p_compare_2_) {
                final double d0 = this.theEntity.getDistanceSq(p_compare_1_);
                final double d2 = this.theEntity.getDistanceSq(p_compare_2_);
                return (d0 < d2) ? -1 : ((d0 > d2) ? 1 : 0);
            }
            
            @Override
            public int compare(final Object p_compare_1_, final Object p_compare_2_) {
                return this.compare((Entity)p_compare_1_, (Entity)p_compare_2_);
            }
        }
    }
}
