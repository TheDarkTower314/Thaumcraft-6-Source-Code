package thaumcraft.common.entities.construct;
import com.google.common.base.Optional;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemNameTag;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thaumcraft.common.lib.SoundsTC;


public class EntityOwnedConstruct extends EntityCreature implements IEntityOwnable
{
    protected static DataParameter<Byte> TAMED;
    protected static DataParameter<Optional<UUID>> OWNER_UNIQUE_ID;
    boolean validSpawn;
    
    public EntityOwnedConstruct(World worldIn) {
        super(worldIn);
        validSpawn = false;
    }
    
    protected void entityInit() {
        super.entityInit();
        getDataManager().register((DataParameter)EntityOwnedConstruct.TAMED, 0);
        getDataManager().register((DataParameter)EntityOwnedConstruct.OWNER_UNIQUE_ID, Optional.absent());
    }
    
    public boolean isOwned() {
        return ((byte) getDataManager().get((DataParameter)EntityOwnedConstruct.TAMED) & 0x4) != 0x0;
    }
    
    public void setOwned(boolean tamed) {
        byte b0 = (byte) getDataManager().get((DataParameter)EntityOwnedConstruct.TAMED);
        if (tamed) {
            getDataManager().set(EntityOwnedConstruct.TAMED, (byte)(b0 | 0x4));
        }
        else {
            getDataManager().set(EntityOwnedConstruct.TAMED, (byte)(b0 & 0xFFFFFFFB));
        }
    }
    
    public UUID getOwnerId() {
        return (UUID)((Optional) getDataManager().get((DataParameter)EntityOwnedConstruct.OWNER_UNIQUE_ID)).orNull();
    }
    
    public void setOwnerId(UUID p_184754_1_) {
        getDataManager().set(EntityOwnedConstruct.OWNER_UNIQUE_ID, Optional.fromNullable(p_184754_1_));
    }
    
    protected int decreaseAirSupply(int air) {
        return air;
    }
    
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundsTC.clack;
    }
    
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsTC.clack;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundsTC.tool;
    }
    
    public int getTalkInterval() {
        return 240;
    }
    
    protected boolean canDespawn() {
        return false;
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (getAttackTarget() != null && isOnSameTeam(getAttackTarget())) {
            setAttackTarget(null);
        }
        if (!world.isRemote && !validSpawn) {
            setDead();
        }
    }
    
    public void setValidSpawn() {
        validSpawn = true;
    }
    
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setBoolean("v", validSpawn);
        if (getOwnerId() == null) {
            tagCompound.setString("OwnerUUID", "");
        }
        else {
            tagCompound.setString("OwnerUUID", getOwnerId().toString());
        }
    }
    
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        validSpawn = tagCompound.getBoolean("v");
        String s = "";
        if (tagCompound.hasKey("OwnerUUID", 8)) {
            s = tagCompound.getString("OwnerUUID");
        }
        else {
            String s2 = tagCompound.getString("Owner");
            s = PreYggdrasilConverter.convertMobOwnerIfNeeded(getServer(), s2);
        }
        if (!s.isEmpty()) {
            try {
                setOwnerId(UUID.fromString(s));
                setOwned(true);
            }
            catch (Throwable var4) {
                setOwned(false);
            }
        }
    }
    
    public EntityLivingBase getOwnerEntity() {
        try {
            UUID uuid = getOwnerId();
            return (uuid == null) ? null : world.getPlayerEntityByUUID(uuid);
        }
        catch (IllegalArgumentException var2) {
            return null;
        }
    }
    
    public boolean isOwner(EntityLivingBase entityIn) {
        return entityIn == getOwnerEntity();
    }
    
    public Team getTeam() {
        if (isOwned()) {
            EntityLivingBase entitylivingbase = getOwnerEntity();
            if (entitylivingbase != null) {
                return entitylivingbase.getTeam();
            }
        }
        return super.getTeam();
    }
    
    public boolean isOnSameTeam(Entity otherEntity) {
        if (isOwned()) {
            EntityLivingBase entitylivingbase1 = getOwnerEntity();
            if (otherEntity == entitylivingbase1) {
                return true;
            }
            if (entitylivingbase1 != null) {
                return entitylivingbase1.isOnSameTeam(otherEntity);
            }
        }
        return super.isOnSameTeam(otherEntity);
    }
    
    public void onDeath(DamageSource cause) {
        if (!world.isRemote && world.getGameRules().getBoolean("showDeathMessages") && hasCustomName() && getOwnerEntity() instanceof EntityPlayerMP) {
            getOwnerEntity().sendMessage(getCombatTracker().getDeathMessage());
        }
        super.onDeath(cause);
    }
    
    public Entity getOwner() {
        return getOwnerEntity();
    }
    
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (isDead) {
            return false;
        }
        if (player.isSneaking() || (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ItemNameTag)) {
            return false;
        }
        if (!world.isRemote && !isOwner(player)) {
            player.sendStatusMessage(new TextComponentTranslation("§5§o" + I18n.translateToLocal("tc.notowned")), true);
            return true;
        }
        return super.processInteract(player, hand);
    }
    
    static {
        TAMED = EntityDataManager.createKey(EntityOwnedConstruct.class, DataSerializers.BYTE);
        OWNER_UNIQUE_ID = EntityDataManager.createKey(EntityOwnedConstruct.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    }
}
