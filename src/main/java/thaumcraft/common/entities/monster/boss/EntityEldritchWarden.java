package thaumcraft.common.entities.monster.boss;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.common.entities.projectile.EntityEldritchOrb;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockArc;
import thaumcraft.common.lib.network.fx.PacketFXSonic;


public class EntityEldritchWarden extends EntityThaumcraftBoss implements IRangedAttackMob, IEldritchMob
{
    protected BossInfoServer bossInfo2;
    String[] titles;
    private static DataParameter<Byte> NAME;
    boolean fieldFrenzy;
    int fieldFrenzyCounter;
    boolean lastBlast;
    public float armLiftL;
    public float armLiftR;
    
    public EntityEldritchWarden(World p_i1745_1_) {
        super(p_i1745_1_);
        bossInfo2 = new BossInfoServer(new TextComponentString(""), BossInfo.Color.BLUE, BossInfo.Overlay.NOTCHED_10);
        titles = new String[] { "Aphoom-Zhah", "Basatan", "Chaugnar Faugn", "Mnomquah", "Nyogtha", "Oorn", "Shaikorth", "Rhan-Tegoth", "Rhogog", "Shudde M'ell", "Vulthoom", "Yag-Kosha", "Yibb-Tstll", "Zathog", "Zushakon" };
        fieldFrenzy = false;
        fieldFrenzyCounter = 0;
        lastBlast = false;
        armLiftL = 0.0f;
        armLiftR = 0.0f;
        setSize(1.5f, 3.5f);
    }
    
    @Override
    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        bossInfo2.removePlayer(player);
    }
    
    @Override
    public void addTrackingPlayer(EntityPlayerMP player) {
        super.addTrackingPlayer(player);
        bossInfo2.addPlayer(player);
    }
    
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(2, new AILongRangeAttack(this, 3.0, 1.0, 20, 40, 24.0f));
        tasks.addTask(3, new EntityAIAttackMelee(this, 1.1, false));
        tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.8));
        tasks.addTask(7, new EntityAIWander(this, 1.0));
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        tasks.addTask(8, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityCultist.class, true));
    }
    
    @Override
    public void generateName() {
        int t = (int) getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue();
        if (t >= 0) {
            setCustomNameTag(String.format(I18n.translateToLocal("entity.Thaumcraft.EldritchWarden.name.custom"), getTitle(), ChampionModifier.mods[t].getModNameLocalized()));
        }
    }
    
    private String getTitle() {
        return titles[(byte) getDataManager().get((DataParameter)EntityEldritchWarden.NAME)];
    }
    
    private void setTitle(int title) {
        getDataManager().set(EntityEldritchWarden.NAME, (byte)title);
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(400.0);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.33);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0);
    }
    
    @Override
    protected void entityInit() {
        super.entityInit();
        getDataManager().register((DataParameter)EntityEldritchWarden.NAME, 0);
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("title", (byte) getDataManager().get((DataParameter)EntityEldritchWarden.NAME));
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setTitle(nbt.getByte("title"));
    }
    
    public int getTotalArmorValue() {
        return super.getTotalArmorValue() + 4;
    }
    
    @Override
    protected void updateAITasks() {
        if (fieldFrenzyCounter == 0) {
            super.updateAITasks();
        }
        int bh = (int)(getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() * 0.66);
        if (hurtResistantTime <= 0 && ticksExisted % 25 == 0 && getAbsorptionAmount() < bh) {
            setAbsorptionAmount(getAbsorptionAmount() + 1.0f);
        }
        bossInfo2.setPercent(getAbsorptionAmount() / bh);
    }
    
    @Override
    public void onUpdate() {
        if (getSpawnTimer() == 150) {
            world.setEntityState(this, (byte)18);
        }
        super.onUpdate();
        if (world.isRemote) {
            if (armLiftL > 0.0f) {
                armLiftL -= 0.05f;
            }
            if (armLiftR > 0.0f) {
                armLiftR -= 0.05f;
            }
            float x = (float)(posX + (rand.nextFloat() - rand.nextFloat()) * 0.2f);
            float z = (float)(posZ + (rand.nextFloat() - rand.nextFloat()) * 0.2f);
            FXDispatcher.INSTANCE.wispFXEG(x, (float)(posY + 0.25 * height), z, this);
            if (spawnTimer > 0) {
                float he = Math.max(1.0f, height * ((150 - spawnTimer) / 150.0f));
                for (int a = 0; a < 33; ++a) {
                    FXDispatcher.INSTANCE.smokeSpiral(posX, getEntityBoundingBox().minY + he / 2.0f, posZ, he, rand.nextInt(360), MathHelper.floor(getEntityBoundingBox().minY) - 1, 2232623);
                }
            }
        }
    }
    
    public void onLivingUpdate() {
        super.onLivingUpdate();
        int i = MathHelper.floor(posX);
        int j = MathHelper.floor(posY);
        int k = MathHelper.floor(posZ);
        for (int l = 0; l < 4; ++l) {
            i = MathHelper.floor(posX + (l % 2 * 2 - 1) * 0.25f);
            j = MathHelper.floor(posY);
            k = MathHelper.floor(posZ + (l / 2 % 2 * 2 - 1) * 0.25f);
            BlockPos bp = new BlockPos(i, j, k);
            if (world.isAirBlock(bp) && BlocksTC.effectSap != null) {
                world.setBlockState(bp, BlocksTC.effectSap.getDefaultState());
            }
        }
        if (!world.isRemote && fieldFrenzyCounter > 0) {
            if (fieldFrenzyCounter == 150) {
                teleportHome();
            }
            performFieldFrenzy();
        }
    }
    
    private void performFieldFrenzy() {
        if (fieldFrenzyCounter < 121 && fieldFrenzyCounter % 10 == 0) {
            world.setEntityState(this, (byte)17);
            double radius = (150 - fieldFrenzyCounter) / 8.0;
            int d = 1 + fieldFrenzyCounter / 8;
            int i = MathHelper.floor(posX);
            int j = MathHelper.floor(posY);
            int k = MathHelper.floor(posZ);
            for (int q = 0; q < 180 / d; ++q) {
                double radians = Math.toRadians(q * 2 * d);
                int deltaX = (int)(radius * Math.cos(radians));
                int deltaZ = (int)(radius * Math.sin(radians));
                BlockPos bp = new BlockPos(i + deltaX, j, k + deltaZ);
                if (world.isAirBlock(bp) && world.isBlockNormalCube(bp.down(), false)) {
                    world.setBlockState(bp, BlocksTC.effectSap.getDefaultState());
                    world.scheduleUpdate(bp, BlocksTC.effectSap, 250 + rand.nextInt(150));
                    if (rand.nextFloat() < 0.3f) {
                        PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(bp, this, 0.5f + rand.nextFloat() * 0.2f, 0.0f, 0.5f + rand.nextFloat() * 0.2f), new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 32.0));
                    }
                }
            }
            playSound(SoundsTC.zap, 1.0f, 0.9f + rand.nextFloat() * 0.1f);
        }
        --fieldFrenzyCounter;
    }
    
    protected void teleportHome() {
        EnderTeleportEvent event = new EnderTeleportEvent(this, getHomePosition().getX(), getHomePosition().getY(), getHomePosition().getZ(), 0.0f);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return;
        }
        double d3 = posX;
        double d4 = posY;
        double d5 = posZ;
        posX = event.getTargetX();
        posY = event.getTargetY();
        posZ = event.getTargetZ();
        boolean flag = false;
        int i = MathHelper.floor(posX);
        int j = MathHelper.floor(posY);
        int k = MathHelper.floor(posZ);
        BlockPos bp = new BlockPos(i, j, k);
        if (world.isBlockLoaded(bp)) {
            bp = new BlockPos(i, j, k);
            boolean flag2 = false;
            int tries = 20;
            while (!flag2 && tries > 0) {
                IBlockState block = world.getBlockState(bp.down());
                IBlockState block2 = world.getBlockState(bp);
                if (block.getMaterial().blocksMovement() && !block2.getMaterial().blocksMovement()) {
                    flag2 = true;
                }
                else {
                    i = MathHelper.floor(posX) + rand.nextInt(8) - rand.nextInt(8);
                    k = MathHelper.floor(posZ) + rand.nextInt(8) - rand.nextInt(8);
                    --tries;
                }
            }
            if (flag2) {
                setPosition(i + 0.5, j + 0.1, k + 0.5);
                if (world.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty()) {
                    flag = true;
                }
            }
        }
        if (!flag) {
            setPosition(d3, d4, d5);
            return;
        }
        short short1 = 128;
        for (int l = 0; l < short1; ++l) {
            double d6 = l / (short1 - 1.0);
            float f = (rand.nextFloat() - 0.5f) * 0.2f;
            float f2 = (rand.nextFloat() - 0.5f) * 0.2f;
            float f3 = (rand.nextFloat() - 0.5f) * 0.2f;
            double d7 = d3 + (posX - d3) * d6 + (rand.nextDouble() - 0.5) * width * 2.0;
            double d8 = d4 + (posY - d4) * d6 + rand.nextDouble() * height;
            double d9 = d5 + (posZ - d5) * d6 + (rand.nextDouble() - 0.5) * width * 2.0;
            world.spawnParticle(EnumParticleTypes.PORTAL, d7, d8, d9, f, f2, f3);
        }
        playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0f, 1.0f);
    }
    
    @Override
    public boolean isEntityInvulnerable(DamageSource ds) {
        return fieldFrenzyCounter > 0 || super.isEntityInvulnerable(ds);
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (isEntityInvulnerable(source) || source == DamageSource.DROWN || source == DamageSource.WITHER) {
            return false;
        }
        boolean aef = super.attackEntityFrom(source, damage);
        if (!world.isRemote && aef && !fieldFrenzy && getAbsorptionAmount() <= 0.0f) {
            fieldFrenzy = true;
            fieldFrenzyCounter = 150;
        }
        return aef;
    }
    
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData data) {
        spawnTimer = 150;
        setTitle(rand.nextInt(titles.length));
        setAbsorptionAmount((float)(getAbsorptionAmount() + getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() * 0.66));
        return super.onInitialSpawn(diff, data);
    }
    
    public float getEyeHeight() {
        return 3.1f;
    }
    
    public void attackEntityWithRangedAttack(EntityLivingBase entitylivingbase, float f) {
        if (rand.nextFloat() > 0.2f) {
            EntityEldritchOrb blast = new EntityEldritchOrb(world, this);
            lastBlast = !lastBlast;
            world.setEntityState(this, (byte)(lastBlast ? 16 : 15));
            int rr = lastBlast ? 90 : 180;
            double xx = MathHelper.cos((rotationYaw + rr) % 360.0f / 180.0f * 3.1415927f) * 0.5f;
            double yy = 0.13;
            double zz = MathHelper.sin((rotationYaw + rr) % 360.0f / 180.0f * 3.1415927f) * 0.5f;
            blast.setPosition(blast.posX - xx, blast.posY - yy, blast.posZ - zz);
            double d0 = entitylivingbase.posX + entitylivingbase.motionX - posX;
            double d2 = entitylivingbase.posY - posY - entitylivingbase.height / 2.0f;
            double d3 = entitylivingbase.posZ + entitylivingbase.motionZ - posZ;
            blast.shoot(d0, d2, d3, 1.0f, 2.0f);
            playSound(SoundsTC.egattack, 2.0f, 1.0f + rand.nextFloat() * 0.1f);
            world.spawnEntity(blast);
        }
        else if (canEntityBeSeen(entitylivingbase)) {
            PacketHandler.INSTANCE.sendToAllAround(new PacketFXSonic(getEntityId()), new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 32.0));
            entitylivingbase.addVelocity(-MathHelper.sin(rotationYaw * 3.1415927f / 180.0f) * 1.5f, 0.1, MathHelper.cos(rotationYaw * 3.1415927f / 180.0f) * 1.5f);
            try {
                entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.WITHER, 400, 0));
                entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 400, 0));
            }
            catch (Exception ex) {}
            if (entitylivingbase instanceof EntityPlayer) {
                ThaumcraftApi.internalMethods.addWarpToPlayer((EntityPlayer)entitylivingbase, 3 + world.rand.nextInt(3), IPlayerWarp.EnumWarpType.TEMPORARY);
            }
            playSound(SoundsTC.egscreech, 4.0f, 1.0f + rand.nextFloat() * 0.1f);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte p_70103_1_) {
        if (p_70103_1_ == 15) {
            armLiftL = 0.5f;
        }
        else if (p_70103_1_ == 16) {
            armLiftR = 0.5f;
        }
        else if (p_70103_1_ == 17) {
            armLiftL = 0.9f;
            armLiftR = 0.9f;
        }
        else if (p_70103_1_ == 18) {
            spawnTimer = 150;
        }
        else {
            super.handleStatusUpdate(p_70103_1_);
        }
    }
    
    public boolean canAttackClass(Class clazz) {
        return clazz != EntityEldritchGuardian.class && super.canAttackClass(clazz);
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundsTC.egidle;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundsTC.egdeath;
    }
    
    public int getTalkInterval() {
        return 500;
    }
    
    public void setSwingingArms(boolean swingingArms) {
    }
    
    static {
        NAME = EntityDataManager.createKey(EntityEldritchWarden.class, DataSerializers.BYTE);
    }
}
