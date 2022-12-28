package thaumcraft.common.golems;
import java.nio.ByteBuffer;
import java.util.Iterator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.network.Packet;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.config.ConfigAspects;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import thaumcraft.common.golems.ai.AIFollowOwner;
import thaumcraft.common.golems.ai.AIGotoBlock;
import thaumcraft.common.golems.ai.AIGotoEntity;
import thaumcraft.common.golems.ai.AIGotoHome;
import thaumcraft.common.golems.ai.AIOwnerHurtByTarget;
import thaumcraft.common.golems.ai.AIOwnerHurtTarget;
import thaumcraft.common.golems.ai.PathNavigateGolemAir;
import thaumcraft.common.golems.ai.PathNavigateGolemGround;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.Utils;


public class EntityThaumcraftGolem extends EntityOwnedConstruct implements IGolemAPI, IRangedAttackMob
{
    int rankXp;
    private static DataParameter<Integer> PROPS1;
    private static DataParameter<Integer> PROPS2;
    private static DataParameter<Integer> PROPS3;
    private static DataParameter<Byte> CLIMBING;
    public boolean redrawParts;
    private boolean firstRun;
    protected Task task;
    protected int taskID;
    public static int XPM = 1000;
    
    public EntityThaumcraftGolem(World worldIn) {
        super(worldIn);
        rankXp = 0;
        redrawParts = false;
        firstRun = true;
        task = null;
        taskID = Integer.MAX_VALUE;
        setSize(0.4f, 0.9f);
        experienceValue = 5;
    }
    
    protected void initEntityAI() {
        targetTasks.taskEntries.clear();
        tasks.addTask(2, new AIGotoEntity(this));
        tasks.addTask(3, new AIGotoBlock(this));
        tasks.addTask(4, new AIGotoHome(this));
        tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        tasks.addTask(6, new EntityAILookIdle(this));
    }
    
    @Override
    protected void entityInit() {
        super.entityInit();
        getDataManager().register(EntityThaumcraftGolem.PROPS1, 0);
        getDataManager().register(EntityThaumcraftGolem.PROPS2, 0);
        getDataManager().register(EntityThaumcraftGolem.PROPS3, 0);
        getDataManager().register((DataParameter)EntityThaumcraftGolem.CLIMBING, 0);
    }
    
    @Override
    public IGolemProperties getProperties() {
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putInt((int) getDataManager().get((DataParameter)EntityThaumcraftGolem.PROPS1));
        bb.putInt((int) getDataManager().get((DataParameter)EntityThaumcraftGolem.PROPS2));
        return GolemProperties.fromLong(bb.getLong(0));
    }
    
    @Override
    public void setProperties(IGolemProperties prop) {
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putLong(prop.toLong());
        bb.rewind();
        getDataManager().set(EntityThaumcraftGolem.PROPS1, bb.getInt());
        getDataManager().set(EntityThaumcraftGolem.PROPS2, bb.getInt());
    }
    
    @Override
    public byte getGolemColor() {
        byte[] ba = Utils.intToByteArray((int) getDataManager().get((DataParameter)EntityThaumcraftGolem.PROPS3));
        return ba[0];
    }
    
    public void setGolemColor(byte b) {
        byte[] ba = Utils.intToByteArray((int) getDataManager().get((DataParameter)EntityThaumcraftGolem.PROPS3));
        ba[0] = b;
        getDataManager().set(EntityThaumcraftGolem.PROPS3, Utils.byteArraytoInt(ba));
    }
    
    public byte getFlags() {
        byte[] ba = Utils.intToByteArray((int) getDataManager().get((DataParameter)EntityThaumcraftGolem.PROPS3));
        return ba[1];
    }
    
    public void setFlags(byte b) {
        byte[] ba = Utils.intToByteArray((int) getDataManager().get((DataParameter)EntityThaumcraftGolem.PROPS3));
        ba[1] = b;
        getDataManager().set(EntityThaumcraftGolem.PROPS3, Utils.byteArraytoInt(ba));
    }
    
    public float getEyeHeight() {
        return 0.7f;
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0);
        getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.0);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0);
    }
    
    private void updateEntityAttributes() {
        int mh = 10 + getProperties().getMaterial().healthMod;
        if (getProperties().hasTrait(EnumGolemTrait.FRAGILE)) {
            mh *= (int)0.75;
        }
        mh += getProperties().getRank();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(mh);
        stepHeight = (getProperties().hasTrait(EnumGolemTrait.WHEELED) ? 0.5f : 0.6f);
        setHomePosAndDistance((getHomePosition() == BlockPos.ORIGIN) ? getPosition() : getHomePosition(), getProperties().hasTrait(EnumGolemTrait.SCOUT) ? 48 : 32);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(getProperties().hasTrait(EnumGolemTrait.SCOUT) ? 56.0 : 40.0);
        navigator = getGolemNavigator();
        if (getProperties().hasTrait(EnumGolemTrait.FLYER)) {
            moveHelper = new FlyingMoveControl(this);
        }
        if (getProperties().hasTrait(EnumGolemTrait.FIGHTER)) {
            double da = getProperties().getMaterial().damage;
            if (getProperties().hasTrait(EnumGolemTrait.BRUTAL)) {
                da = Math.max(da * 1.5, da + 1.0);
            }
            da += getProperties().getRank() * 0.25;
            getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(da);
        }
        else {
            getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.0);
        }
        createAI();
    }
    
    private void createAI() {
        tasks.taskEntries.clear();
        targetTasks.taskEntries.clear();
        if (isFollowingOwner()) {
            tasks.addTask(4, new AIFollowOwner(this, 1.0, 10.0f, 2.0f));
        }
        else {
            tasks.addTask(3, new AIGotoEntity(this));
            tasks.addTask(4, new AIGotoBlock(this));
            tasks.addTask(5, new AIGotoHome(this));
        }
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        tasks.addTask(9, new EntityAILookIdle(this));
        if (getProperties().hasTrait(EnumGolemTrait.FIGHTER)) {
            if (getNavigator() instanceof PathNavigateGround) {
                tasks.addTask(0, new EntityAISwimming(this));
            }
            if (getProperties().hasTrait(EnumGolemTrait.RANGED)) {
                EntityAIAttackRanged aa = null;
                if (getProperties().getArms().function != null) {
                    aa = getProperties().getArms().function.getRangedAttackAI(this);
                }
                if (aa != null) {
                    tasks.addTask(1, aa);
                }
            }
            tasks.addTask(2, new EntityAIAttackMelee(this, 1.15, false));
            if (isFollowingOwner()) {
                targetTasks.addTask(1, new AIOwnerHurtByTarget(this));
                targetTasks.addTask(2, new AIOwnerHurtTarget(this));
            }
            targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        }
    }
    
    public boolean isOnLadder() {
        return isBesideClimbableBlock();
    }
    
    public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData ld) {
        setHomePosAndDistance(getPosition(), 32);
        updateEntityAttributes();
        return ld;
    }
    
    public int getTotalArmorValue() {
        int armor = getProperties().getMaterial().armor;
        if (getProperties().hasTrait(EnumGolemTrait.ARMORED)) {
            armor = (int)Math.max(armor * 1.5, armor + 1);
        }
        if (getProperties().hasTrait(EnumGolemTrait.FRAGILE)) {
            armor *= (int)0.75;
        }
        return armor;
    }
    
    public void onLivingUpdate() {
        updateArmSwingProgress();
        super.onLivingUpdate();
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (getProperties().hasTrait(EnumGolemTrait.FLYER)) {
            setNoGravity(true);
        }
        if (!world.isRemote) {
            if (firstRun) {
                firstRun = false;
                if (hasHome() && !getPosition().equals(getHomePosition())) {
                    goHome();
                }
            }
            if (task != null && task.isSuspended()) {
                task = null;
            }
            if (getAttackTarget() != null && getAttackTarget().isDead) {
                setAttackTarget(null);
            }
            if (getAttackTarget() != null && getProperties().hasTrait(EnumGolemTrait.RANGED) && getDistanceSq(getAttackTarget()) > 1024.0) {
                setAttackTarget(null);
            }
            if (!FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled() && getAttackTarget() != null && getAttackTarget() instanceof EntityPlayer) {
                setAttackTarget(null);
            }
            if (ticksExisted % (getProperties().hasTrait(EnumGolemTrait.REPAIR) ? 40 : 100) == 0) {
                heal(1.0f);
            }
            if (getProperties().hasTrait(EnumGolemTrait.CLIMBER)) {
                setBesideClimbableBlock(collidedHorizontally);
            }
        }
        else if (ticksExisted < 20 || ticksExisted % 20 == 0) {
            redrawParts = true;
        }
        if (getProperties().getHead().function != null) {
            getProperties().getHead().function.onUpdateTick(this);
        }
        if (getProperties().getArms().function != null) {
            getProperties().getArms().function.onUpdateTick(this);
        }
        if (getProperties().getLegs().function != null) {
            getProperties().getLegs().function.onUpdateTick(this);
        }
        if (getProperties().getAddon().function != null) {
            getProperties().getAddon().function.onUpdateTick(this);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte par1) {
        if (par1 == 5) {
            FXDispatcher.INSTANCE.drawGenericParticles(posX, posY + height + 0.1, posZ, 0.0, 0.0, 0.0, 1.0f, 1.0f, 1.0f, 0.5f, false, 704 + (rand.nextBoolean() ? 0 : 3), 3, 1, 6, 0, 2.0f, 0.0f, 1);
        }
        else if (par1 == 6) {
            FXDispatcher.INSTANCE.drawGenericParticles(posX, posY + height + 0.1, posZ, 0.0, 0.025, 0.0, 0.1f, 1.0f, 1.0f, 0.5f, false, 15, 1, 1, 10, 0, 2.0f, 0.0f, 1);
        }
        else if (par1 == 7) {
            FXDispatcher.INSTANCE.drawGenericParticles(posX, posY + height + 0.1, posZ, 0.0, 0.05, 0.0, 1.0f, 1.0f, 1.0f, 0.5f, false, 640, 10, 1, 10, 0, 2.0f, 0.0f, 1);
        }
        else if (par1 == 8) {
            FXDispatcher.INSTANCE.drawGenericParticles(posX, posY + height + 0.1, posZ, 0.0, 0.01, 0.0, 1.0f, 1.0f, 0.1f, 0.5f, false, 14, 1, 1, 20, 0, 2.0f, 0.0f, 1);
        }
        else if (par1 == 9) {
            for (int a = 0; a < 5; ++a) {
                FXDispatcher.INSTANCE.drawGenericParticles(posX, posY + height, posZ, rand.nextGaussian() * 0.009999999776482582, rand.nextFloat() * 0.02, rand.nextGaussian() * 0.009999999776482582, 1.0f, 1.0f, 1.0f, 0.5f, false, 13, 1, 1, 20 + rand.nextInt(20), 0, 0.3f + rand.nextFloat() * 0.4f, 0.0f, 1);
            }
        }
        else {
            super.handleStatusUpdate(par1);
        }
    }
    
    public float getGolemMoveSpeed() {
        return 1.0f + getProperties().getRank() * 0.025f + (getProperties().hasTrait(EnumGolemTrait.LIGHT) ? 0.2f : 0.0f) + (getProperties().hasTrait(EnumGolemTrait.HEAVY) ? -0.175f : 0.0f) + (getProperties().hasTrait(EnumGolemTrait.FLYER) ? -0.33f : 0.0f) + (getProperties().hasTrait(EnumGolemTrait.WHEELED) ? 0.25f : 0.0f);
    }
    
    public PathNavigate getGolemNavigator() {
        return getProperties().hasTrait(EnumGolemTrait.FLYER) ? new PathNavigateGolemAir(this, world) : (getProperties().hasTrait(EnumGolemTrait.CLIMBER) ? new PathNavigateClimber(this, world) : new PathNavigateGolemGround(this, world));
    }
    
    protected boolean canTriggerWalking() {
        return getProperties().hasTrait(EnumGolemTrait.HEAVY) && !getProperties().hasTrait(EnumGolemTrait.FLYER);
    }
    
    public void fall(float distance, float damageMultiplier) {
        if (!getProperties().hasTrait(EnumGolemTrait.FLYER) && !getProperties().hasTrait(EnumGolemTrait.CLIMBER)) {
            super.fall(distance, damageMultiplier);
        }
    }
    
    private void goHome() {
        double d0 = posX;
        double d2 = posY;
        double d3 = posZ;
        posX = getHomePosition().getX() + 0.5;
        posY = getHomePosition().getY();
        posZ = getHomePosition().getZ() + 0.5;
        boolean flag = false;
        BlockPos blockpos = new BlockPos(this);
        boolean flag2 = false;
        while (!flag2 && blockpos.getY() < world.getActualHeight()) {
            BlockPos blockpos2 = blockpos.up();
            IBlockState iblockstate = world.getBlockState(blockpos2);
            if (iblockstate.getMaterial().blocksMovement()) {
                flag2 = true;
            }
            else {
                ++posY;
                blockpos = blockpos2;
            }
        }
        if (flag2) {
            setPositionAndUpdate(posX, posY, posZ);
            if (world.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty()) {
                flag = true;
            }
        }
        if (!flag) {
            setPositionAndUpdate(d0, d2, d3);
        }
        else if (this instanceof EntityCreature) {
            getNavigator().clearPath();
        }
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setProperties(GolemProperties.fromLong(nbt.getLong("props")));
        setHomePosAndDistance(BlockPos.fromLong(nbt.getLong("homepos")), 32);
        setFlags(nbt.getByte("gflags"));
        rankXp = nbt.getInteger("rankXP");
        setGolemColor(nbt.getByte("color"));
        updateEntityAttributes();
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setLong("props", getProperties().toLong());
        nbt.setLong("homepos", getHomePosition().toLong());
        nbt.setByte("gflags", getFlags());
        nbt.setInteger("rankXP", rankXp);
        nbt.setByte("color", getGolemColor());
    }
    
    protected void damageEntity(DamageSource ds, float damage) {
        if (ds.isFireDamage() && getProperties().hasTrait(EnumGolemTrait.FIREPROOF)) {
            return;
        }
        if (ds.isExplosion() && getProperties().hasTrait(EnumGolemTrait.BLASTPROOF)) {
            damage = Math.min(getMaxHealth() / 2.0f, damage * 0.3f);
        }
        if (ds == DamageSource.CACTUS) {
            return;
        }
        if (hasHome() && (ds == DamageSource.IN_WALL || ds == DamageSource.OUT_OF_WORLD)) {
            goHome();
        }
        super.damageEntity(ds, damage);
    }
    
    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (isDead) {
            return false;
        }
        if (player.getHeldItem(hand).getItem() instanceof ItemNameTag) {
            return false;
        }
        if (!world.isRemote && isOwner(player) && !isDead) {
            if (player.isSneaking()) {
                playSound(SoundsTC.zap, 1.0f, 1.0f);
                if (task != null) {
                    task.setReserved(false);
                }
                dropCarried();
                ItemStack placer = new ItemStack(ItemsTC.golemPlacer);
                placer.setTagInfo("props", new NBTTagLong(getProperties().toLong()));
                placer.setTagInfo("xp", new NBTTagInt(rankXp));
                entityDropItem(placer, 0.5f);
                setDead();
                player.swingArm(hand);
            }
            else if (player.getHeldItem(hand).getItem() instanceof ItemGolemBell && ThaumcraftCapabilities.getKnowledge(player).isResearchKnown("GOLEMDIRECT")) {
                if (task != null) {
                    task.setReserved(false);
                }
                playSound(SoundsTC.scan, 1.0f, 1.0f);
                setFollowingOwner(!isFollowingOwner());
                if (isFollowingOwner()) {
                    player.sendStatusMessage(new TextComponentTranslation("golem.follow", ""), true);
                    if (ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
                        world.setEntityState(this, (byte)5);
                    }
                    detachHome();
                }
                else {
                    player.sendStatusMessage(new TextComponentTranslation("golem.stay", ""), true);
                    if (ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
                        world.setEntityState(this, (byte)8);
                    }
                    setHomePosAndDistance(getPosition(), getProperties().hasTrait(EnumGolemTrait.SCOUT) ? 48 : 32);
                }
                updateEntityAttributes();
                player.swingArm(hand);
            }
            else if (!player.getHeldItem(hand).isEmpty()) {
                int[] ids = OreDictionary.getOreIDs(player.getHeldItem(hand));
                if (ids != null && ids.length > 0) {
                    for (int id : ids) {
                        String s = OreDictionary.getOreName(id);
                        if (s.startsWith("dye")) {
                            for (int a = 0; a < ConfigAspects.dyes.length; ++a) {
                                if (s.equals(ConfigAspects.dyes[a])) {
                                    playSound(SoundsTC.zap, 1.0f, 1.0f);
                                    setGolemColor((byte)(16 - a));
                                    player.getHeldItem(hand).shrink(1);
                                    player.swingArm(hand);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
        return super.processInteract(player, hand);
    }
    
    @Override
    public void onDeath(DamageSource cause) {
        if (task != null) {
            task.setReserved(false);
        }
        super.onDeath(cause);
        if (!world.isRemote) {
            dropCarried();
        }
    }
    
    protected void dropCarried() {
        for (ItemStack s : getCarrying()) {
            if (s != null && !s.isEmpty()) {
                entityDropItem(s, 0.25f);
            }
        }
    }
    
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
        float b = p_70628_2_ * 0.15f;
        for (ItemStack stack : getProperties().generateComponents()) {
            ItemStack s = stack.copy();
            if (rand.nextFloat() < 0.3f + b) {
                if (s.getCount() > 0) {
                    s.shrink(rand.nextInt(s.getCount()));
                }
                entityDropItem(s, 0.25f);
            }
        }
    }
    
    public boolean isBesideClimbableBlock() {
        return ((byte) dataManager.get((DataParameter)EntityThaumcraftGolem.CLIMBING) & 0x1) != 0x0;
    }
    
    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = (byte) dataManager.get((DataParameter)EntityThaumcraftGolem.CLIMBING);
        if (climbing) {
            b0 |= 0x1;
        }
        else {
            b0 &= 0xFFFFFFFE;
        }
        dataManager.set(EntityThaumcraftGolem.CLIMBING, b0);
    }
    
    public boolean isFollowingOwner() {
        return Utils.getBit(getFlags(), 1);
    }
    
    public void setFollowingOwner(boolean par1) {
        byte var2 = getFlags();
        if (par1) {
            setFlags((byte)Utils.setBit(var2, 1));
        }
        else {
            setFlags((byte)Utils.clearBit(var2, 1));
        }
    }
    
    public void setAttackTarget(EntityLivingBase entitylivingbaseIn) {
        super.setAttackTarget(entitylivingbaseIn);
        setInCombat(getAttackTarget() != null);
    }
    
    @Override
    public boolean isInCombat() {
        return Utils.getBit(getFlags(), 3);
    }
    
    public void setInCombat(boolean par1) {
        byte var2 = getFlags();
        if (par1) {
            setFlags((byte)Utils.setBit(var2, 3));
        }
        else {
            setFlags((byte)Utils.clearBit(var2, 3));
        }
    }
    
    public boolean attackEntityAsMob(Entity ent) {
        float dmg = (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        int kb = 0;
        if (ent instanceof EntityLivingBase) {
            dmg += EnchantmentHelper.getModifierForCreature(getHeldItemMainhand(), ((EntityLivingBase)ent).getCreatureAttribute());
            kb += EnchantmentHelper.getKnockbackModifier(this);
        }
        boolean flag = ent.attackEntityFrom(DamageSource.causeMobDamage(this), dmg);
        if (flag) {
            if (ent instanceof EntityLivingBase && getProperties().hasTrait(EnumGolemTrait.DEFT)) {
                ((EntityLivingBase)ent).recentlyHit = 100;
            }
            if (kb > 0) {
                ent.addVelocity(-MathHelper.sin(rotationYaw * 3.1415927f / 180.0f) * kb * 0.5f, 0.1, MathHelper.cos(rotationYaw * 3.1415927f / 180.0f) * kb * 0.5f);
                motionX *= 0.6;
                motionZ *= 0.6;
            }
            int j = EnchantmentHelper.getFireAspectModifier(this);
            if (j > 0) {
                ent.setFire(j * 4);
            }
            applyEnchantments(this, ent);
            if (getProperties().getArms().function != null) {
                getProperties().getArms().function.onMeleeAttack(this, ent);
            }
            if (ent instanceof EntityLiving && !ent.isEntityAlive()) {
                addRankXp(8);
            }
        }
        return flag;
    }
    
    public Task getTask() {
        if (task == null && taskID != Integer.MAX_VALUE) {
            task = TaskHandler.getTask(world.provider.getDimension(), taskID);
            taskID = Integer.MAX_VALUE;
        }
        return task;
    }
    
    public void setTask(Task task) {
        this.task = task;
    }
    
    @Override
    public void addRankXp(int xp) {
        if (!getProperties().hasTrait(EnumGolemTrait.SMART) || world.isRemote) {
            return;
        }
        int rank = getProperties().getRank();
        if (rank < 10) {
            rankXp += xp;
            int xn = (rank + 1) * (rank + 1) * 1000;
            if (rankXp >= xn) {
                rankXp -= xn;
                IGolemProperties props = getProperties();
                props.setRank(rank + 1);
                setProperties(props);
                if (ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
                    world.setEntityState(this, (byte)9);
                    playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 0.25f, 1.0f);
                }
            }
        }
    }
    
    @Override
    public ItemStack holdItem(ItemStack stack) {
        if (stack == null || stack.isEmpty() || stack.getCount() <= 0) {
            return stack;
        }
        for (int a = 0; a < (getProperties().hasTrait(EnumGolemTrait.HAULER) ? 2 : 1); ++a) {
            if (getItemStackFromSlot(EntityEquipmentSlot.values()[a]) == null || getItemStackFromSlot(EntityEquipmentSlot.values()[a]).isEmpty()) {
                setItemStackToSlot(EntityEquipmentSlot.values()[a], stack);
                return ItemStack.EMPTY;
            }
            if (getItemStackFromSlot(EntityEquipmentSlot.values()[a]).getCount() < getItemStackFromSlot(EntityEquipmentSlot.values()[a]).getMaxStackSize() && ItemStack.areItemsEqual(getItemStackFromSlot(EntityEquipmentSlot.values()[a]), stack) && ItemStack.areItemStackTagsEqual(getItemStackFromSlot(EntityEquipmentSlot.values()[a]), stack)) {
                int d = Math.min(stack.getCount(), getItemStackFromSlot(EntityEquipmentSlot.values()[a]).getMaxStackSize() - getItemStackFromSlot(EntityEquipmentSlot.values()[a]).getCount());
                stack.shrink(d);
                getItemStackFromSlot(EntityEquipmentSlot.values()[a]).grow(d);
                if (stack.getCount() <= 0) {
                    stack = ItemStack.EMPTY;
                }
            }
        }
        return stack;
    }
    
    @Override
    public ItemStack dropItem(ItemStack stack) {
        ItemStack out = ItemStack.EMPTY;
        for (int a = 0; a < (getProperties().hasTrait(EnumGolemTrait.HAULER) ? 2 : 1); ++a) {
            if (getItemStackFromSlot(EntityEquipmentSlot.values()[a]) != null) {
                if (!getItemStackFromSlot(EntityEquipmentSlot.values()[a]).isEmpty()) {
                    if (stack == null || stack.isEmpty()) {
                        out = getItemStackFromSlot(EntityEquipmentSlot.values()[a]).copy();
                        setItemStackToSlot(EntityEquipmentSlot.values()[a], ItemStack.EMPTY);
                    }
                    else if (ItemStack.areItemsEqual(getItemStackFromSlot(EntityEquipmentSlot.values()[a]), stack) && ItemStack.areItemStackTagsEqual(getItemStackFromSlot(EntityEquipmentSlot.values()[a]), stack)) {
                        out = getItemStackFromSlot(EntityEquipmentSlot.values()[a]).copy();
                        out.setCount(Math.min(stack.getCount(), out.getCount()));
                        getItemStackFromSlot(EntityEquipmentSlot.values()[a]).shrink(stack.getCount());
                        if (getItemStackFromSlot(EntityEquipmentSlot.values()[a]).getCount() <= 0) {
                            setItemStackToSlot(EntityEquipmentSlot.values()[a], ItemStack.EMPTY);
                        }
                    }
                    if (out != null && !out.isEmpty()) {
                        break;
                    }
                }
            }
        }
        if (getProperties().hasTrait(EnumGolemTrait.HAULER) && (getItemStackFromSlot(EntityEquipmentSlot.values()[0]) == null || getItemStackFromSlot(EntityEquipmentSlot.values()[0]).isEmpty()) && getItemStackFromSlot(EntityEquipmentSlot.values()[1]) != null && !getItemStackFromSlot(EntityEquipmentSlot.values()[1]).isEmpty()) {
            setItemStackToSlot(EntityEquipmentSlot.values()[0], getItemStackFromSlot(EntityEquipmentSlot.values()[1]).copy());
            setItemStackToSlot(EntityEquipmentSlot.values()[1], ItemStack.EMPTY);
        }
        return out;
    }
    
    @Override
    public int canCarryAmount(ItemStack stack) {
        int ss = 0;
        for (int a = 0; a < (getProperties().hasTrait(EnumGolemTrait.HAULER) ? 2 : 1); ++a) {
            if (getItemStackFromSlot(EntityEquipmentSlot.values()[a]) == null || getItemStackFromSlot(EntityEquipmentSlot.values()[a]).isEmpty()) {
                ss += getItemStackFromSlot(EntityEquipmentSlot.values()[a]).getMaxStackSize();
            }
            if (ItemStack.areItemsEqual(getItemStackFromSlot(EntityEquipmentSlot.values()[a]), stack) && ItemStack.areItemStackTagsEqual(getItemStackFromSlot(EntityEquipmentSlot.values()[a]), stack)) {
                ss += getItemStackFromSlot(EntityEquipmentSlot.values()[a]).getMaxStackSize() - getItemStackFromSlot(EntityEquipmentSlot.values()[a]).getCount();
            }
        }
        return ss;
    }
    
    @Override
    public boolean canCarry(ItemStack stack, boolean partial) {
        int ca = canCarryAmount(stack);
        if (ca > 0) {
            if (!partial) {
                if (ca < stack.getCount()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isCarrying(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        for (int a = 0; a < (getProperties().hasTrait(EnumGolemTrait.HAULER) ? 2 : 1); ++a) {
            if (getItemStackFromSlot(EntityEquipmentSlot.values()[a]) != null && !getItemStackFromSlot(EntityEquipmentSlot.values()[a]).isEmpty() && getItemStackFromSlot(EntityEquipmentSlot.values()[a]).getCount() > 0 && ItemStack.areItemsEqual(getItemStackFromSlot(EntityEquipmentSlot.values()[a]), stack) && ItemStack.areItemStackTagsEqual(getItemStackFromSlot(EntityEquipmentSlot.values()[a]), stack)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public NonNullList<ItemStack> getCarrying() {
        if (getProperties().hasTrait(EnumGolemTrait.HAULER)) {
            NonNullList<ItemStack> stacks = NonNullList.withSize(2, ItemStack.EMPTY);
            stacks.set(0, getItemStackFromSlot(EntityEquipmentSlot.values()[0]));
            stacks.set(1, getItemStackFromSlot(EntityEquipmentSlot.values()[1]));
            return stacks;
        }
        return NonNullList.withSize(1, getItemStackFromSlot(EntityEquipmentSlot.values()[0]));
    }
    
    @Override
    public EntityLivingBase getGolemEntity() {
        return this;
    }
    
    @Override
    public World getGolemWorld() {
        return getEntityWorld();
    }
    
    @Override
    public void swingArm() {
        if (!isSwingInProgress || swingProgressInt >= 3 || swingProgressInt < 0) {
            swingProgressInt = -1;
            isSwingInProgress = true;
            if (world instanceof WorldServer) {
                ((WorldServer) world).getEntityTracker().sendToTrackingAndSelf(this, new SPacketAnimation(this, 0));
            }
        }
    }
    
    public void attackEntityWithRangedAttack(EntityLivingBase target, float range) {
        if (getProperties().getArms().function != null) {
            getProperties().getArms().function.onRangedAttack(this, target, range);
        }
    }
    
    public void setSwingingArms(boolean swingingArms) {
    }
    
    static {
        PROPS1 = EntityDataManager.createKey(EntityThaumcraftGolem.class, DataSerializers.VARINT);
        PROPS2 = EntityDataManager.createKey(EntityThaumcraftGolem.class, DataSerializers.VARINT);
        PROPS3 = EntityDataManager.createKey(EntityThaumcraftGolem.class, DataSerializers.VARINT);
        CLIMBING = EntityDataManager.createKey(EntityThaumcraftGolem.class, DataSerializers.BYTE);
    }
    
    class FlyingMoveControl extends EntityMoveHelper
    {
        public FlyingMoveControl(EntityThaumcraftGolem vex) {
            super(vex);
        }
        
        public void onUpdateMoveHelper() {
            if (action == EntityMoveHelper.Action.MOVE_TO) {
                double d0 = posX - EntityThaumcraftGolem.this.posX;
                double d2 = posY - EntityThaumcraftGolem.this.posY;
                double d3 = posZ - EntityThaumcraftGolem.this.posZ;
                double d4 = d0 * d0 + d2 * d2 + d3 * d3;
                d4 = MathHelper.sqrt(d4);
                if (d4 < getEntityBoundingBox().getAverageEdgeLength()) {
                    action = EntityMoveHelper.Action.WAIT;
                    EntityThaumcraftGolem this$0 = EntityThaumcraftGolem.this;
                    this$0.motionX *= 0.5;
                    EntityThaumcraftGolem this$2 = EntityThaumcraftGolem.this;
                    this$2.motionY *= 0.5;
                    EntityThaumcraftGolem this$3 = EntityThaumcraftGolem.this;
                    this$3.motionZ *= 0.5;
                }
                else {
                    EntityThaumcraftGolem this$4 = EntityThaumcraftGolem.this;
                    this$4.motionX += d0 / d4 * 0.033 * speed;
                    EntityThaumcraftGolem this$5 = EntityThaumcraftGolem.this;
                    this$5.motionY += d2 / d4 * 0.0125 * speed;
                    EntityThaumcraftGolem this$6 = EntityThaumcraftGolem.this;
                    this$6.motionZ += d3 / d4 * 0.033 * speed;
                    if (getAttackTarget() == null) {
                        rotationYaw = -(float)MathHelper.atan2(motionX, motionZ) * 57.295776f;
                        renderYawOffset = rotationYaw;
                    }
                    else {
                        double d5 = getAttackTarget().posX - EntityThaumcraftGolem.this.posX;
                        double d6 = getAttackTarget().posZ - EntityThaumcraftGolem.this.posZ;
                        rotationYaw = -(float)MathHelper.atan2(d5, d6) * 57.295776f;
                        renderYawOffset = rotationYaw;
                    }
                }
            }
        }
    }
}
