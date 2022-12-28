package thaumcraft.common.entities.monster.boss;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.monster.tainted.EntityTaintacle;
import thaumcraft.common.lib.utils.EntityUtils;


public class EntityTaintacleGiant extends EntityTaintacle implements ITaintedMob, IEldritchMob
{
    protected BossInfoServer bossInfo;
    private static DataParameter<Integer> AGGRO;
    
    public EntityTaintacleGiant(World par1World) {
        super(par1World);
        bossInfo = (BossInfoServer)new BossInfoServer(getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS).setDarkenSky(true);
        setSize(1.1f, 6.0f);
        experienceValue = 20;
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(175.0);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(9.0);
    }
    
    public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData data) {
        EntityUtils.makeChampion(this, true);
        return data;
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (getAnger() > 0) {
            setAnger(getAnger() - 1);
        }
        if (world.isRemote && rand.nextInt(15) == 0 && getAnger() > 0) {
            double d0 = rand.nextGaussian() * 0.02;
            double d2 = rand.nextGaussian() * 0.02;
            double d3 = rand.nextGaussian() * 0.02;
            world.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, posX + rand.nextFloat() * width - width / 2.0, getEntityBoundingBox().minY + height + rand.nextFloat() * 0.5, posZ + rand.nextFloat() * width - width / 2.0, d0, d2, d3);
        }
        if (!world.isRemote && ticksExisted % 30 == 0) {
            heal(1.0f);
        }
    }
    
    protected void entityInit() {
        super.entityInit();
        getDataManager().register(EntityTaintacleGiant.AGGRO, 0);
    }
    
    public int getAnger() {
        return (int) getDataManager().get((DataParameter)EntityTaintacleGiant.AGGRO);
    }
    
    public void setAnger(int par1) {
        getDataManager().set(EntityTaintacleGiant.AGGRO, par1);
    }
    
    @Override
    public boolean getCanSpawnHere() {
        return false;
    }
    
    @Override
    protected void dropFewItems(boolean flag, int i) {
        List<EntityTaintacleGiant> ents = EntityUtils.getEntitiesInRange(world, posX, posY, posZ, this, EntityTaintacleGiant.class, 48.0);
        if (ents == null || ents.size() <= 0) {
            EntityUtils.entityDropSpecialItem(this, new ItemStack(ItemsTC.primordialPearl), height / 2.0f);
        }
    }
    
    protected boolean canDespawn() {
        return false;
    }
    
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    protected int decreaseAirSupply(int air) {
        return air;
    }
    
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (!world.isRemote && damage > 35.0f) {
            if (getAnger() == 0) {
                try {
                    addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, (int)(damage / 15.0f)));
                    addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, (int)(damage / 10.0f)));
                    addPotionEffect(new PotionEffect(MobEffects.HASTE, 200, (int)(damage / 40.0f)));
                    setAnger(200);
                }
                catch (Exception ex) {}
                if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
                    ((EntityPlayer)source.getTrueSource()).sendStatusMessage(new TextComponentTranslation(getName() + " " + I18n.translateToLocal("tc.boss.enrage")), true);
                }
            }
            damage = 35.0f;
        }
        return super.attackEntityFrom(source, damage);
    }
    
    protected void updateAITasks() {
        super.updateAITasks();
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
    
    static {
        AGGRO = EntityDataManager.createKey(EntityTaintacleGiant.class, DataSerializers.VARINT);
    }
}
