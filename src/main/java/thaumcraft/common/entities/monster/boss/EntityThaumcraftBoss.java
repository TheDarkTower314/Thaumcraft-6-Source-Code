// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.boss;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.entities.IEldritchMob;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import thaumcraft.common.lib.utils.EntityUtils;
import net.minecraft.entity.player.EntityPlayer;
import java.util.ArrayList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import java.util.HashMap;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.world.BossInfoServer;
import net.minecraft.entity.monster.EntityMob;

public class EntityThaumcraftBoss extends EntityMob
{
    protected final BossInfoServer bossInfo;
    private static final DataParameter<Integer> AGGRO;
    HashMap<Integer, Integer> aggro;
    int spawnTimer;
    
    public EntityThaumcraftBoss(final World world) {
        super(world);
        this.bossInfo = (BossInfoServer)new BossInfoServer(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS).setDarkenSky(true);
        this.aggro = new HashMap<Integer, Integer>();
        this.spawnTimer = 0;
        this.experienceValue = 50;
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
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.95);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0);
    }
    
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(EntityThaumcraftBoss.AGGRO, 0);
    }
    
    protected void updateAITasks() {
        if (this.getSpawnTimer() == 0) {
            super.updateAITasks();
        }
        if (this.getAttackTarget() != null && this.getAttackTarget().isDead) {
            this.setAttackTarget(null);
        }
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }
    
    public void removeTrackingPlayer(final EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }
    
    public void addTrackingPlayer(final EntityPlayerMP player) {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }
    
    public boolean isNonBoss() {
        return false;
    }
    
    public IEntityLivingData onInitialSpawn(final DifficultyInstance diff, final IEntityLivingData data) {
        this.setHomePosAndDistance(this.getPosition(), 24);
        this.generateName();
        this.bossInfo.setName(this.getDisplayName());
        return data;
    }
    
    public int getAnger() {
        return (int)this.getDataManager().get((DataParameter)EntityThaumcraftBoss.AGGRO);
    }
    
    public void setAnger(final int par1) {
        this.getDataManager().set(EntityThaumcraftBoss.AGGRO, par1);
    }
    
    public int getSpawnTimer() {
        return this.spawnTimer;
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (this.getSpawnTimer() > 0) {
            --this.spawnTimer;
        }
        if (this.getAnger() > 0) {
            this.setAnger(this.getAnger() - 1);
        }
        if (this.world.isRemote && this.rand.nextInt(15) == 0 && this.getAnger() > 0) {
            final double d0 = this.rand.nextGaussian() * 0.02;
            final double d2 = this.rand.nextGaussian() * 0.02;
            final double d3 = this.rand.nextGaussian() * 0.02;
            this.world.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, this.posX + this.rand.nextFloat() * this.width - this.width / 2.0, this.getEntityBoundingBox().minY + this.height + this.rand.nextFloat() * 0.5, this.posZ + this.rand.nextFloat() * this.width - this.width / 2.0, d0, d2, d3, new int[0]);
        }
        if (!this.world.isRemote) {
            if (this.ticksExisted % 30 == 0) {
                this.heal(1.0f);
            }
            if (this.getAttackTarget() != null && this.ticksExisted % 20 == 0) {
                final ArrayList<Integer> dl = new ArrayList<Integer>();
                int players = 0;
                int hei = this.getAttackTarget().getEntityId();
                int ld;
                final int ad = ld = (this.aggro.containsKey(hei) ? this.aggro.get(hei) : 0);
                Entity newTarget = null;
                for (final Integer ei : this.aggro.keySet()) {
                    final int ca = this.aggro.get(ei);
                    if (ca > ad + 25 && ca > ad * 1.1 && ca > ld) {
                        newTarget = this.world.getEntityByID(hei);
                        if (newTarget == null || newTarget.isDead || this.getDistanceSq(newTarget) > 16384.0) {
                            dl.add(ei);
                        }
                        else {
                            hei = ei;
                            ld = ei;
                            if (!(newTarget instanceof EntityPlayer)) {
                                continue;
                            }
                            ++players;
                        }
                    }
                }
                for (final Integer ei : dl) {
                    this.aggro.remove(ei);
                }
                if (newTarget != null && hei != this.getAttackTarget().getEntityId()) {
                    this.setAttackTarget((EntityLivingBase)newTarget);
                }
                final float om = this.getMaxHealth();
                final IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
                final IAttributeInstance iattributeinstance2 = this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                for (int a = 0; a < 5; ++a) {
                    iattributeinstance2.removeModifier(EntityUtils.DMGBUFF[a]);
                    iattributeinstance.removeModifier(EntityUtils.HPBUFF[a]);
                }
                for (int a = 0; a < Math.min(5, players - 1); ++a) {
                    iattributeinstance.applyModifier(EntityUtils.HPBUFF[a]);
                    iattributeinstance2.applyModifier(EntityUtils.DMGBUFF[a]);
                }
                final double mm = this.getMaxHealth() / om;
                this.setHealth((float)(this.getHealth() * mm));
            }
        }
    }
    
    public boolean isEntityInvulnerable(final DamageSource ds) {
        return super.isEntityInvulnerable(ds) || this.getSpawnTimer() > 0;
    }
    
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    public boolean canBePushed() {
        return super.canBePushed() && !this.isEntityInvulnerable(DamageSource.STARVE);
    }
    
    protected int decreaseAirSupply(final int air) {
        return air;
    }
    
    public void setInWeb() {
    }
    
    public boolean canPickUpLoot() {
        return false;
    }
    
    protected void setEnchantmentBasedOnDifficulty(final DifficultyInstance diff) {
    }
    
    protected boolean canDespawn() {
        return false;
    }
    
    public boolean isOnSameTeam(final Entity el) {
        return el instanceof IEldritchMob;
    }
    
    protected void dropFewItems(final boolean flag, final int fortune) {
        EntityUtils.entityDropSpecialItem(this, new ItemStack(ItemsTC.primordialPearl), this.height / 2.0f);
        this.entityDropItem(new ItemStack(ItemsTC.lootBag, 1, 2), 1.5f);
    }
    
    public boolean attackEntityFrom(final DamageSource source, float damage) {
        if (!this.world.isRemote) {
            if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityLivingBase) {
                final int target = source.getTrueSource().getEntityId();
                int ad = (int)damage;
                if (this.aggro.containsKey(target)) {
                    ad += this.aggro.get(target);
                }
                this.aggro.put(target, ad);
            }
            if (damage > 35.0f) {
                if (this.getAnger() == 0) {
                    try {
                        try {
                            this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, (int)(damage / 15.0f)));
                            this.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, (int)(damage / 10.0f)));
                            this.addPotionEffect(new PotionEffect(MobEffects.HASTE, 200, (int)(damage / 40.0f)));
                        }
                        catch (final Exception ex) {}
                        this.setAnger(200);
                    }
                    catch (final Exception ex2) {}
                    if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
                        ((EntityPlayer)source.getTrueSource()).sendStatusMessage(new TextComponentTranslation(this.getName() + " " + I18n.translateToLocal("tc.boss.enrage"), new Object[0]), true);
                    }
                }
                damage = 35.0f;
            }
        }
        return super.attackEntityFrom(source, damage);
    }
    
    public void generateName() {
    }
    
    static {
        AGGRO = EntityDataManager.createKey(EntityThaumcraftBoss.class, DataSerializers.VARINT);
    }
}
