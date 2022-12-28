package thaumcraft.common.entities.monster.boss;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.utils.EntityUtils;


public class EntityThaumcraftBoss extends EntityMob
{
    protected BossInfoServer bossInfo;
    private static DataParameter<Integer> AGGRO;
    HashMap<Integer, Integer> aggro;
    int spawnTimer;
    
    public EntityThaumcraftBoss(World world) {
        super(world);
        bossInfo = (BossInfoServer)new BossInfoServer(getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS).setDarkenSky(true);
        aggro = new HashMap<Integer, Integer>();
        spawnTimer = 0;
        experienceValue = 50;
    }
    
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("HomeD")) {
            setHomePosAndDistance(new BlockPos(nbt.getInteger("HomeX"), nbt.getInteger("HomeY"), nbt.getInteger("HomeZ")), nbt.getInteger("HomeD"));
        }
    }
    
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        if (getHomePosition() != null && getMaximumHomeDistance() > 0.0f) {
            nbt.setInteger("HomeD", (int) getMaximumHomeDistance());
            nbt.setInteger("HomeX", getHomePosition().getX());
            nbt.setInteger("HomeY", getHomePosition().getY());
            nbt.setInteger("HomeZ", getHomePosition().getZ());
        }
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.95);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0);
    }
    
    protected void entityInit() {
        super.entityInit();
        getDataManager().register(EntityThaumcraftBoss.AGGRO, 0);
    }
    
    protected void updateAITasks() {
        if (getSpawnTimer() == 0) {
            super.updateAITasks();
        }
        if (getAttackTarget() != null && getAttackTarget().isDead) {
            setAttackTarget(null);
        }
        bossInfo.setPercent(getHealth() / getMaxHealth());
    }
    
    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        bossInfo.removePlayer(player);
    }
    
    public void addTrackingPlayer(EntityPlayerMP player) {
        super.addTrackingPlayer(player);
        bossInfo.addPlayer(player);
    }
    
    public boolean isNonBoss() {
        return false;
    }
    
    public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData data) {
        setHomePosAndDistance(getPosition(), 24);
        generateName();
        bossInfo.setName(getDisplayName());
        return data;
    }
    
    public int getAnger() {
        return (int) getDataManager().get((DataParameter)EntityThaumcraftBoss.AGGRO);
    }
    
    public void setAnger(int par1) {
        getDataManager().set(EntityThaumcraftBoss.AGGRO, par1);
    }
    
    public int getSpawnTimer() {
        return spawnTimer;
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (getSpawnTimer() > 0) {
            --spawnTimer;
        }
        if (getAnger() > 0) {
            setAnger(getAnger() - 1);
        }
        if (world.isRemote && rand.nextInt(15) == 0 && getAnger() > 0) {
            double d0 = rand.nextGaussian() * 0.02;
            double d2 = rand.nextGaussian() * 0.02;
            double d3 = rand.nextGaussian() * 0.02;
            world.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, posX + rand.nextFloat() * width - width / 2.0, getEntityBoundingBox().minY + height + rand.nextFloat() * 0.5, posZ + rand.nextFloat() * width - width / 2.0, d0, d2, d3);
        }
        if (!world.isRemote) {
            if (ticksExisted % 30 == 0) {
                heal(1.0f);
            }
            if (getAttackTarget() != null && ticksExisted % 20 == 0) {
                ArrayList<Integer> dl = new ArrayList<Integer>();
                int players = 0;
                int hei = getAttackTarget().getEntityId();
                int ld;
                int ad = ld = (aggro.containsKey(hei) ? aggro.get(hei) : 0);
                Entity newTarget = null;
                for (Integer ei : aggro.keySet()) {
                    int ca = aggro.get(ei);
                    if (ca > ad + 25 && ca > ad * 1.1 && ca > ld) {
                        newTarget = world.getEntityByID(hei);
                        if (newTarget == null || newTarget.isDead || getDistanceSq(newTarget) > 16384.0) {
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
                for (Integer ei : dl) {
                    aggro.remove(ei);
                }
                if (newTarget != null && hei != getAttackTarget().getEntityId()) {
                    setAttackTarget((EntityLivingBase)newTarget);
                }
                float om = getMaxHealth();
                IAttributeInstance iattributeinstance = getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
                IAttributeInstance iattributeinstance2 = getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                for (int a = 0; a < 5; ++a) {
                    iattributeinstance2.removeModifier(EntityUtils.DMGBUFF[a]);
                    iattributeinstance.removeModifier(EntityUtils.HPBUFF[a]);
                }
                for (int a = 0; a < Math.min(5, players - 1); ++a) {
                    iattributeinstance.applyModifier(EntityUtils.HPBUFF[a]);
                    iattributeinstance2.applyModifier(EntityUtils.DMGBUFF[a]);
                }
                double mm = getMaxHealth() / om;
                setHealth((float)(getHealth() * mm));
            }
        }
    }
    
    public boolean isEntityInvulnerable(DamageSource ds) {
        return super.isEntityInvulnerable(ds) || getSpawnTimer() > 0;
    }
    
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    public boolean canBePushed() {
        return super.canBePushed() && !isEntityInvulnerable(DamageSource.STARVE);
    }
    
    protected int decreaseAirSupply(int air) {
        return air;
    }
    
    public void setInWeb() {
    }
    
    public boolean canPickUpLoot() {
        return false;
    }
    
    protected void setEnchantmentBasedOnDifficulty(DifficultyInstance diff) {
    }
    
    protected boolean canDespawn() {
        return false;
    }
    
    public boolean isOnSameTeam(Entity el) {
        return el instanceof IEldritchMob;
    }
    
    protected void dropFewItems(boolean flag, int fortune) {
        EntityUtils.entityDropSpecialItem(this, new ItemStack(ItemsTC.primordialPearl), height / 2.0f);
        entityDropItem(new ItemStack(ItemsTC.lootBag, 1, 2), 1.5f);
    }
    
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (!world.isRemote) {
            if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityLivingBase) {
                int target = source.getTrueSource().getEntityId();
                int ad = (int)damage;
                if (aggro.containsKey(target)) {
                    ad += aggro.get(target);
                }
                aggro.put(target, ad);
            }
            if (damage > 35.0f) {
                if (getAnger() == 0) {
                    try {
                        try {
                            addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, (int)(damage / 15.0f)));
                            addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, (int)(damage / 10.0f)));
                            addPotionEffect(new PotionEffect(MobEffects.HASTE, 200, (int)(damage / 40.0f)));
                        }
                        catch (Exception ex) {}
                        setAnger(200);
                    }
                    catch (Exception ex2) {}
                    if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
                        ((EntityPlayer)source.getTrueSource()).sendStatusMessage(new TextComponentTranslation(getName() + " " + I18n.translateToLocal("tc.boss.enrage")), true);
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
