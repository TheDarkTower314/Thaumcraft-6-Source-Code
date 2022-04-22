// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.boss;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import java.util.List;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.entity.monster.EntityMob;
import thaumcraft.common.lib.utils.EntityUtils;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.world.BossInfoServer;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.entities.monster.tainted.EntityTaintacle;

public class EntityTaintacleGiant extends EntityTaintacle implements ITaintedMob, IEldritchMob
{
    protected final BossInfoServer bossInfo;
    private static final DataParameter<Integer> AGGRO;
    
    public EntityTaintacleGiant(final World par1World) {
        super(par1World);
        this.bossInfo = (BossInfoServer)new BossInfoServer(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS).setDarkenSky(true);
        this.setSize(1.1f, 6.0f);
        this.experienceValue = 20;
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(175.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(9.0);
    }
    
    public IEntityLivingData onInitialSpawn(final DifficultyInstance diff, final IEntityLivingData data) {
        EntityUtils.makeChampion(this, true);
        return data;
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.getAnger() > 0) {
            this.setAnger(this.getAnger() - 1);
        }
        if (this.world.isRemote && this.rand.nextInt(15) == 0 && this.getAnger() > 0) {
            final double d0 = this.rand.nextGaussian() * 0.02;
            final double d2 = this.rand.nextGaussian() * 0.02;
            final double d3 = this.rand.nextGaussian() * 0.02;
            this.world.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, this.posX + this.rand.nextFloat() * this.width - this.width / 2.0, this.getEntityBoundingBox().minY + this.height + this.rand.nextFloat() * 0.5, this.posZ + this.rand.nextFloat() * this.width - this.width / 2.0, d0, d2, d3);
        }
        if (!this.world.isRemote && this.ticksExisted % 30 == 0) {
            this.heal(1.0f);
        }
    }
    
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(EntityTaintacleGiant.AGGRO, 0);
    }
    
    public int getAnger() {
        return (int)this.getDataManager().get((DataParameter)EntityTaintacleGiant.AGGRO);
    }
    
    public void setAnger(final int par1) {
        this.getDataManager().set(EntityTaintacleGiant.AGGRO, par1);
    }
    
    @Override
    public boolean getCanSpawnHere() {
        return false;
    }
    
    @Override
    protected void dropFewItems(final boolean flag, final int i) {
        final List<EntityTaintacleGiant> ents = EntityUtils.getEntitiesInRange(this.world, this.posX, this.posY, this.posZ, this, EntityTaintacleGiant.class, 48.0);
        if (ents == null || ents.size() <= 0) {
            EntityUtils.entityDropSpecialItem(this, new ItemStack(ItemsTC.primordialPearl), this.height / 2.0f);
        }
    }
    
    protected boolean canDespawn() {
        return false;
    }
    
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    protected int decreaseAirSupply(final int air) {
        return air;
    }
    
    public boolean attackEntityFrom(final DamageSource source, float damage) {
        if (!this.world.isRemote && damage > 35.0f) {
            if (this.getAnger() == 0) {
                try {
                    this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, (int)(damage / 15.0f)));
                    this.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, (int)(damage / 10.0f)));
                    this.addPotionEffect(new PotionEffect(MobEffects.HASTE, 200, (int)(damage / 40.0f)));
                    this.setAnger(200);
                }
                catch (final Exception ex) {}
                if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
                    ((EntityPlayer)source.getTrueSource()).sendStatusMessage(new TextComponentTranslation(this.getName() + " " + I18n.translateToLocal("tc.boss.enrage")), true);
                }
            }
            damage = 35.0f;
        }
        return super.attackEntityFrom(source, damage);
    }
    
    protected void updateAITasks() {
        super.updateAITasks();
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
    
    static {
        AGGRO = EntityDataManager.createKey(EntityTaintacleGiant.class, DataSerializers.VARINT);
    }
}
