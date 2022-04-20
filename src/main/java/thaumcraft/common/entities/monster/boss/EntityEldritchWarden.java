// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.boss;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.SoundEvent;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.ThaumcraftApi;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import thaumcraft.common.lib.network.fx.PacketFXSonic;
import thaumcraft.common.entities.projectile.EntityEldritchOrb;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import thaumcraft.common.lib.SoundsTC;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.fx.PacketFXBlockArc;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.SharedMonsterAttributes;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.world.BossInfoServer;
import thaumcraft.api.entities.IEldritchMob;
import net.minecraft.entity.IRangedAttackMob;

public class EntityEldritchWarden extends EntityThaumcraftBoss implements IRangedAttackMob, IEldritchMob
{
    protected final BossInfoServer bossInfo2;
    String[] titles;
    private static final DataParameter<Byte> NAME;
    boolean fieldFrenzy;
    int fieldFrenzyCounter;
    boolean lastBlast;
    public float armLiftL;
    public float armLiftR;
    
    public EntityEldritchWarden(final World p_i1745_1_) {
        super(p_i1745_1_);
        this.bossInfo2 = new BossInfoServer(new TextComponentString(""), BossInfo.Color.BLUE, BossInfo.Overlay.NOTCHED_10);
        this.titles = new String[] { "Aphoom-Zhah", "Basatan", "Chaugnar Faugn", "Mnomquah", "Nyogtha", "Oorn", "Shaikorth", "Rhan-Tegoth", "Rhogog", "Shudde M'ell", "Vulthoom", "Yag-Kosha", "Yibb-Tstll", "Zathog", "Zushakon" };
        this.fieldFrenzy = false;
        this.fieldFrenzyCounter = 0;
        this.lastBlast = false;
        this.armLiftL = 0.0f;
        this.armLiftR = 0.0f;
        this.setSize(1.5f, 3.5f);
    }
    
    @Override
    public void removeTrackingPlayer(final EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        this.bossInfo2.removePlayer(player);
    }
    
    @Override
    public void addTrackingPlayer(final EntityPlayerMP player) {
        super.addTrackingPlayer(player);
        this.bossInfo2.addPlayer(player);
    }
    
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new AILongRangeAttack(this, 3.0, 1.0, 20, 40, 24.0f));
        this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.1, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.8));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityCultist.class, true));
    }
    
    @Override
    public void generateName() {
        final int t = (int)this.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue();
        if (t >= 0) {
            this.setCustomNameTag(String.format(I18n.translateToLocal("entity.Thaumcraft.EldritchWarden.name.custom"), this.getTitle(), ChampionModifier.mods[t].getModNameLocalized()));
        }
    }
    
    private String getTitle() {
        return this.titles[(byte)this.getDataManager().get((DataParameter)EntityEldritchWarden.NAME)];
    }
    
    private void setTitle(final int title) {
        this.getDataManager().set(EntityEldritchWarden.NAME, (byte)title);
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(400.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.33);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0);
    }
    
    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register((DataParameter)EntityEldritchWarden.NAME, 0);
    }
    
    @Override
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("title", (byte)this.getDataManager().get((DataParameter)EntityEldritchWarden.NAME));
    }
    
    @Override
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setTitle(nbt.getByte("title"));
    }
    
    public int getTotalArmorValue() {
        return super.getTotalArmorValue() + 4;
    }
    
    @Override
    protected void updateAITasks() {
        if (this.fieldFrenzyCounter == 0) {
            super.updateAITasks();
        }
        final int bh = (int)(this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() * 0.66);
        if (this.hurtResistantTime <= 0 && this.ticksExisted % 25 == 0 && this.getAbsorptionAmount() < bh) {
            this.setAbsorptionAmount(this.getAbsorptionAmount() + 1.0f);
        }
        this.bossInfo2.setPercent(this.getAbsorptionAmount() / bh);
    }
    
    @Override
    public void onUpdate() {
        if (this.getSpawnTimer() == 150) {
            this.world.setEntityState(this, (byte)18);
        }
        super.onUpdate();
        if (this.world.isRemote) {
            if (this.armLiftL > 0.0f) {
                this.armLiftL -= 0.05f;
            }
            if (this.armLiftR > 0.0f) {
                this.armLiftR -= 0.05f;
            }
            final float x = (float)(this.posX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f);
            final float z = (float)(this.posZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f);
            FXDispatcher.INSTANCE.wispFXEG(x, (float)(this.posY + 0.25 * this.height), z, this);
            if (this.spawnTimer > 0) {
                final float he = Math.max(1.0f, this.height * ((150 - this.spawnTimer) / 150.0f));
                for (int a = 0; a < 33; ++a) {
                    FXDispatcher.INSTANCE.smokeSpiral(this.posX, this.getEntityBoundingBox().minY + he / 2.0f, this.posZ, he, this.rand.nextInt(360), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, 2232623);
                }
            }
        }
    }
    
    public void onLivingUpdate() {
        super.onLivingUpdate();
        int i = MathHelper.floor(this.posX);
        int j = MathHelper.floor(this.posY);
        int k = MathHelper.floor(this.posZ);
        for (int l = 0; l < 4; ++l) {
            i = MathHelper.floor(this.posX + (l % 2 * 2 - 1) * 0.25f);
            j = MathHelper.floor(this.posY);
            k = MathHelper.floor(this.posZ + (l / 2 % 2 * 2 - 1) * 0.25f);
            final BlockPos bp = new BlockPos(i, j, k);
            if (this.world.isAirBlock(bp) && BlocksTC.effectSap != null) {
                this.world.setBlockState(bp, BlocksTC.effectSap.getDefaultState());
            }
        }
        if (!this.world.isRemote && this.fieldFrenzyCounter > 0) {
            if (this.fieldFrenzyCounter == 150) {
                this.teleportHome();
            }
            this.performFieldFrenzy();
        }
    }
    
    private void performFieldFrenzy() {
        if (this.fieldFrenzyCounter < 121 && this.fieldFrenzyCounter % 10 == 0) {
            this.world.setEntityState(this, (byte)17);
            final double radius = (150 - this.fieldFrenzyCounter) / 8.0;
            final int d = 1 + this.fieldFrenzyCounter / 8;
            final int i = MathHelper.floor(this.posX);
            final int j = MathHelper.floor(this.posY);
            final int k = MathHelper.floor(this.posZ);
            for (int q = 0; q < 180 / d; ++q) {
                final double radians = Math.toRadians(q * 2 * d);
                final int deltaX = (int)(radius * Math.cos(radians));
                final int deltaZ = (int)(radius * Math.sin(radians));
                final BlockPos bp = new BlockPos(i + deltaX, j, k + deltaZ);
                if (this.world.isAirBlock(bp) && this.world.isBlockNormalCube(bp.down(), false)) {
                    this.world.setBlockState(bp, BlocksTC.effectSap.getDefaultState());
                    this.world.scheduleUpdate(bp, BlocksTC.effectSap, 250 + this.rand.nextInt(150));
                    if (this.rand.nextFloat() < 0.3f) {
                        PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(bp, this, 0.5f + this.rand.nextFloat() * 0.2f, 0.0f, 0.5f + this.rand.nextFloat() * 0.2f), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.posX, this.posY, this.posZ, 32.0));
                    }
                }
            }
            this.playSound(SoundsTC.zap, 1.0f, 0.9f + this.rand.nextFloat() * 0.1f);
        }
        --this.fieldFrenzyCounter;
    }
    
    protected void teleportHome() {
        final EnderTeleportEvent event = new EnderTeleportEvent(this, this.getHomePosition().getX(), this.getHomePosition().getY(), this.getHomePosition().getZ(), 0.0f);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return;
        }
        final double d3 = this.posX;
        final double d4 = this.posY;
        final double d5 = this.posZ;
        this.posX = event.getTargetX();
        this.posY = event.getTargetY();
        this.posZ = event.getTargetZ();
        boolean flag = false;
        int i = MathHelper.floor(this.posX);
        final int j = MathHelper.floor(this.posY);
        int k = MathHelper.floor(this.posZ);
        BlockPos bp = new BlockPos(i, j, k);
        if (this.world.isBlockLoaded(bp)) {
            bp = new BlockPos(i, j, k);
            boolean flag2 = false;
            int tries = 20;
            while (!flag2 && tries > 0) {
                final IBlockState block = this.world.getBlockState(bp.down());
                final IBlockState block2 = this.world.getBlockState(bp);
                if (block.getMaterial().blocksMovement() && !block2.getMaterial().blocksMovement()) {
                    flag2 = true;
                }
                else {
                    i = MathHelper.floor(this.posX) + this.rand.nextInt(8) - this.rand.nextInt(8);
                    k = MathHelper.floor(this.posZ) + this.rand.nextInt(8) - this.rand.nextInt(8);
                    --tries;
                }
            }
            if (flag2) {
                this.setPosition(i + 0.5, j + 0.1, k + 0.5);
                if (this.world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty()) {
                    flag = true;
                }
            }
        }
        if (!flag) {
            this.setPosition(d3, d4, d5);
            return;
        }
        final short short1 = 128;
        for (int l = 0; l < short1; ++l) {
            final double d6 = l / (short1 - 1.0);
            final float f = (this.rand.nextFloat() - 0.5f) * 0.2f;
            final float f2 = (this.rand.nextFloat() - 0.5f) * 0.2f;
            final float f3 = (this.rand.nextFloat() - 0.5f) * 0.2f;
            final double d7 = d3 + (this.posX - d3) * d6 + (this.rand.nextDouble() - 0.5) * this.width * 2.0;
            final double d8 = d4 + (this.posY - d4) * d6 + this.rand.nextDouble() * this.height;
            final double d9 = d5 + (this.posZ - d5) * d6 + (this.rand.nextDouble() - 0.5) * this.width * 2.0;
            this.world.spawnParticle(EnumParticleTypes.PORTAL, d7, d8, d9, f, f2, f3, new int[0]);
        }
        this.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0f, 1.0f);
    }
    
    @Override
    public boolean isEntityInvulnerable(final DamageSource ds) {
        return this.fieldFrenzyCounter > 0 || super.isEntityInvulnerable(ds);
    }
    
    @Override
    public boolean attackEntityFrom(final DamageSource source, final float damage) {
        if (this.isEntityInvulnerable(source) || source == DamageSource.DROWN || source == DamageSource.WITHER) {
            return false;
        }
        final boolean aef = super.attackEntityFrom(source, damage);
        if (!this.world.isRemote && aef && !this.fieldFrenzy && this.getAbsorptionAmount() <= 0.0f) {
            this.fieldFrenzy = true;
            this.fieldFrenzyCounter = 150;
        }
        return aef;
    }
    
    @Override
    public IEntityLivingData onInitialSpawn(final DifficultyInstance diff, final IEntityLivingData data) {
        this.spawnTimer = 150;
        this.setTitle(this.rand.nextInt(this.titles.length));
        this.setAbsorptionAmount((float)(this.getAbsorptionAmount() + this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() * 0.66));
        return super.onInitialSpawn(diff, data);
    }
    
    public float getEyeHeight() {
        return 3.1f;
    }
    
    public void attackEntityWithRangedAttack(final EntityLivingBase entitylivingbase, final float f) {
        if (this.rand.nextFloat() > 0.2f) {
            final EntityEldritchOrb blast = new EntityEldritchOrb(this.world, this);
            this.lastBlast = !this.lastBlast;
            this.world.setEntityState(this, (byte)(this.lastBlast ? 16 : 15));
            final int rr = this.lastBlast ? 90 : 180;
            final double xx = MathHelper.cos((this.rotationYaw + rr) % 360.0f / 180.0f * 3.1415927f) * 0.5f;
            final double yy = 0.13;
            final double zz = MathHelper.sin((this.rotationYaw + rr) % 360.0f / 180.0f * 3.1415927f) * 0.5f;
            blast.setPosition(blast.posX - xx, blast.posY - yy, blast.posZ - zz);
            final double d0 = entitylivingbase.posX + entitylivingbase.motionX - this.posX;
            final double d2 = entitylivingbase.posY - this.posY - entitylivingbase.height / 2.0f;
            final double d3 = entitylivingbase.posZ + entitylivingbase.motionZ - this.posZ;
            blast.shoot(d0, d2, d3, 1.0f, 2.0f);
            this.playSound(SoundsTC.egattack, 2.0f, 1.0f + this.rand.nextFloat() * 0.1f);
            this.world.spawnEntity(blast);
        }
        else if (this.canEntityBeSeen(entitylivingbase)) {
            PacketHandler.INSTANCE.sendToAllAround(new PacketFXSonic(this.getEntityId()), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.posX, this.posY, this.posZ, 32.0));
            entitylivingbase.addVelocity(-MathHelper.sin(this.rotationYaw * 3.1415927f / 180.0f) * 1.5f, 0.1, MathHelper.cos(this.rotationYaw * 3.1415927f / 180.0f) * 1.5f);
            try {
                entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.WITHER, 400, 0));
                entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 400, 0));
            }
            catch (final Exception ex) {}
            if (entitylivingbase instanceof EntityPlayer) {
                ThaumcraftApi.internalMethods.addWarpToPlayer((EntityPlayer)entitylivingbase, 3 + this.world.rand.nextInt(3), IPlayerWarp.EnumWarpType.TEMPORARY);
            }
            this.playSound(SoundsTC.egscreech, 4.0f, 1.0f + this.rand.nextFloat() * 0.1f);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(final byte p_70103_1_) {
        if (p_70103_1_ == 15) {
            this.armLiftL = 0.5f;
        }
        else if (p_70103_1_ == 16) {
            this.armLiftR = 0.5f;
        }
        else if (p_70103_1_ == 17) {
            this.armLiftL = 0.9f;
            this.armLiftR = 0.9f;
        }
        else if (p_70103_1_ == 18) {
            this.spawnTimer = 150;
        }
        else {
            super.handleStatusUpdate(p_70103_1_);
        }
    }
    
    public boolean canAttackClass(final Class clazz) {
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
    
    public void setSwingingArms(final boolean swingingArms) {
    }
    
    static {
        NAME = EntityDataManager.createKey(EntityEldritchWarden.class, DataSerializers.BYTE);
    }
}
