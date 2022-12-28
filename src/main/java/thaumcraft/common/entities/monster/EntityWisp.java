package thaumcraft.common.entities.monster;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXWispZap;


public class EntityWisp extends EntityFlying implements IMob
{
    public int courseChangeCooldown;
    public double waypointX;
    public double waypointY;
    public double waypointZ;
    private int aggroCooldown;
    public int prevAttackCounter;
    public int attackCounter;
    private BlockPos currentFlightTarget;
    private static DataParameter<String> TYPE;
    
    public EntityWisp(World world) {
        super(world);
        courseChangeCooldown = 0;
        aggroCooldown = 0;
        prevAttackCounter = 0;
        attackCounter = 0;
        setSize(0.9f, 0.9f);
        experienceValue = 5;
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(22.0);
        getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
    }
    
    protected boolean canTriggerWalking() {
        return false;
    }
    
    public int decreaseAirSupply(int par1) {
        return par1;
    }
    
    public boolean attackEntityFrom(DamageSource damagesource, float i) {
        if (damagesource.getTrueSource() instanceof EntityLivingBase) {
            setAttackTarget((EntityLivingBase)damagesource.getTrueSource());
            aggroCooldown = 200;
        }
        return super.attackEntityFrom(damagesource, i);
    }
    
    protected void entityInit() {
        super.entityInit();
        getDataManager().register(EntityWisp.TYPE, String.valueOf(""));
    }
    
    public void onDeath(DamageSource par1DamageSource) {
        super.onDeath(par1DamageSource);
        if (world.isRemote) {
            FXDispatcher.INSTANCE.burst(posX, posY + 0.44999998807907104, posZ, 1.0f);
        }
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote && ticksExisted <= 1) {
            FXDispatcher.INSTANCE.burst(posX, posY, posZ, 10.0f);
        }
        if (world.isRemote && world.rand.nextBoolean() && Aspect.getAspect(getType()) != null) {
            FXDispatcher.INSTANCE.drawWispParticles(posX + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.7f, posY + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.7f, posZ + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.7f, 0.0, 0.0, 0.0, Aspect.getAspect(getType()).getColor(), 0);
        }
        motionY *= 0.6000000238418579;
    }
    
    public String getType() {
        return (String) getDataManager().get((DataParameter)EntityWisp.TYPE);
    }
    
    public void setType(String t) {
        getDataManager().set(EntityWisp.TYPE, String.valueOf(t));
    }
    
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (isServerWorld()) {
            if (!world.isRemote && Aspect.getAspect(getType()) == null) {
                if (world.rand.nextInt(10) != 0) {
                    ArrayList<Aspect> as = Aspect.getPrimalAspects();
                    setType(as.get(world.rand.nextInt(as.size())).getTag());
                }
                else {
                    ArrayList<Aspect> as = Aspect.getCompoundAspects();
                    setType(as.get(world.rand.nextInt(as.size())).getTag());
                }
            }
            if (!world.isRemote && world.getDifficulty() == EnumDifficulty.PEACEFUL) {
                setDead();
            }
            prevAttackCounter = attackCounter;
            double attackrange = 16.0;
            if (getAttackTarget() == null || !canEntityBeSeen(getAttackTarget())) {
                if (currentFlightTarget != null && (!world.isAirBlock(currentFlightTarget) || currentFlightTarget.getY() < 1 || currentFlightTarget.getY() > world.getPrecipitationHeight(currentFlightTarget).up(8).getY())) {
                    currentFlightTarget = null;
                }
                if (currentFlightTarget == null || rand.nextInt(30) == 0 || getDistanceSqToCenter(currentFlightTarget) < 4.0) {
                    currentFlightTarget = new BlockPos((int) posX + rand.nextInt(7) - rand.nextInt(7), (int) posY + rand.nextInt(6) - 2, (int) posZ + rand.nextInt(7) - rand.nextInt(7));
                }
                double var1 = currentFlightTarget.getX() + 0.5 - posX;
                double var2 = currentFlightTarget.getY() + 0.1 - posY;
                double var3 = currentFlightTarget.getZ() + 0.5 - posZ;
                motionX += (Math.signum(var1) * 0.5 - motionX) * 0.10000000149011612;
                motionY += (Math.signum(var2) * 0.699999988079071 - motionY) * 0.10000000149011612;
                motionZ += (Math.signum(var3) * 0.5 - motionZ) * 0.10000000149011612;
                float var4 = (float)(Math.atan2(motionZ, motionX) * 180.0 / 3.141592653589793) - 90.0f;
                float var5 = MathHelper.wrapDegrees(var4 - rotationYaw);
                moveForward = 0.15f;
                rotationYaw += var5;
            }
            else if (getDistanceSq(getAttackTarget()) > attackrange * attackrange / 2.0 && canEntityBeSeen(getAttackTarget())) {
                double var1 = getAttackTarget().posX - posX;
                double var2 = getAttackTarget().posY + getAttackTarget().getEyeHeight() * 0.66f - posY;
                double var3 = getAttackTarget().posZ - posZ;
                motionX += (Math.signum(var1) * 0.5 - motionX) * 0.10000000149011612;
                motionY += (Math.signum(var2) * 0.699999988079071 - motionY) * 0.10000000149011612;
                motionZ += (Math.signum(var3) * 0.5 - motionZ) * 0.10000000149011612;
                float var4 = (float)(Math.atan2(motionZ, motionX) * 180.0 / 3.141592653589793) - 90.0f;
                float var5 = MathHelper.wrapDegrees(var4 - rotationYaw);
                moveForward = 0.5f;
                rotationYaw += var5;
            }
            if (getAttackTarget() instanceof EntityPlayer && ((EntityPlayer) getAttackTarget()).capabilities.disableDamage) {
                setAttackTarget(null);
            }
            if (getAttackTarget() != null && getAttackTarget().isDead) {
                setAttackTarget(null);
            }
            --aggroCooldown;
            if (world.rand.nextInt(1000) == 0 && (getAttackTarget() == null || aggroCooldown-- <= 0)) {
                setAttackTarget(world.getClosestPlayerToEntity(this, 16.0));
                if (getAttackTarget() != null) {
                    aggroCooldown = 50;
                }
            }
            if (isEntityAlive() && getAttackTarget() != null && getAttackTarget().getDistanceSq(this) < attackrange * attackrange) {
                double d5 = getAttackTarget().posX - posX;
                double d6 = getAttackTarget().getEntityBoundingBox().minY + getAttackTarget().height / 2.0f - (posY + height / 2.0f);
                double d7 = getAttackTarget().posZ - posZ;
                float n = -(float)Math.atan2(d5, d7) * 180.0f / 3.141593f;
                rotationYaw = n;
                renderYawOffset = n;
                if (canEntityBeSeen(getAttackTarget())) {
                    ++attackCounter;
                    if (attackCounter == 20) {
                        playSound(SoundsTC.zap, 1.0f, 1.1f);
                        PacketHandler.INSTANCE.sendToAllAround(new PacketFXWispZap(getEntityId(), getAttackTarget().getEntityId()), new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 32.0));
                        float damage = (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                        if (Math.abs(getAttackTarget().motionX) > 0.10000000149011612 || Math.abs(getAttackTarget().motionY) > 0.10000000149011612 || Math.abs(getAttackTarget().motionZ) > 0.10000000149011612) {
                            if (world.rand.nextFloat() < 0.4f) {
                                getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), damage);
                            }
                        }
                        else if (world.rand.nextFloat() < 0.66f) {
                            getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), damage + 1.0f);
                        }
                        attackCounter = -20 + world.rand.nextInt(20);
                    }
                }
                else if (attackCounter > 0) {
                    --attackCounter;
                }
            }
        }
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundsTC.wisplive;
    }
    
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.BLOCK_LAVA_EXTINGUISH;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundsTC.wispdead;
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected void dropFewItems(boolean flag, int i) {
        if (Aspect.getAspect(getType()) != null) {
            entityDropItem(ThaumcraftApiHelper.makeCrystal(Aspect.getAspect(getType())), 0.0f);
        }
    }
    
    protected float getSoundVolume() {
        return 0.25f;
    }
    
    protected boolean canDespawn() {
        return true;
    }
    
    public boolean getCanSpawnHere() {
        int count = 0;
        try {
            List l = world.getEntitiesWithinAABB(EntityWisp.class, getEntityBoundingBox().grow(16.0, 16.0, 16.0));
            if (l != null) {
                count = l.size();
            }
        }
        catch (Exception ex) {}
        return count < 8 && world.getDifficulty() != EnumDifficulty.PEACEFUL && isValidLightLevel() && super.getCanSpawnHere();
    }
    
    protected boolean isValidLightLevel() {
        BlockPos blockpos = new BlockPos(posX, getEntityBoundingBox().minY, posZ);
        if (world.getLightFor(EnumSkyBlock.SKY, blockpos) > rand.nextInt(32)) {
            return false;
        }
        int i = world.getLightFromNeighbors(blockpos);
        if (world.isThundering()) {
            int j = world.getSkylightSubtracted();
            world.setSkylightSubtracted(10);
            i = world.getLightFromNeighbors(blockpos);
            world.setSkylightSubtracted(j);
        }
        return i <= rand.nextInt(8);
    }
    
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setString("Type", getType());
    }
    
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        super.readEntityFromNBT(nbttagcompound);
        setType(nbttagcompound.getString("Type"));
    }
    
    public int getMaxSpawnedInChunk() {
        return 2;
    }
    
    static {
        TYPE = EntityDataManager.createKey(EntityWisp.class, DataSerializers.STRING);
    }
}
