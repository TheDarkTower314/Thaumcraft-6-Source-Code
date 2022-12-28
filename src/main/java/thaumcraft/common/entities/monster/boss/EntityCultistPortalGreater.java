package thaumcraft.common.entities.monster.boss;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;
import thaumcraft.common.entities.monster.cult.EntityCultistKnight;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockArc;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.misc.TileBanner;


public class EntityCultistPortalGreater extends EntityMob
{
    protected BossInfoServer bossInfo;
    int stage;
    int stagecounter;
    public int pulse;
    
    public EntityCultistPortalGreater(World par1World) {
        super(par1World);
        bossInfo = (BossInfoServer)new BossInfoServer(getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.NOTCHED_6).setDarkenSky(true);
        stage = 0;
        stagecounter = 200;
        pulse = 0;
        isImmuneToFire = true;
        experienceValue = 30;
        setSize(1.5f, 3.0f);
    }
    
    public int getTotalArmorValue() {
        return 5;
    }
    
    protected void entityInit() {
        super.entityInit();
    }
    
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("stage", stage);
    }
    
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        stage = nbt.getInteger("stage");
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(500.0);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.0);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
    }
    
    public boolean canBeCollidedWith() {
        return true;
    }
    
    public boolean canBePushed() {
        return false;
    }
    
    public void move(MoverType mt, double par1, double par3, double par5) {
    }
    
    public void onLivingUpdate() {
    }
    
    public boolean isInRangeToRenderDist(double par1) {
        return par1 < 4096.0;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
    
    public float getBrightness() {
        return 1.0f;
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote) {
            if (stagecounter > 0) {
                --stagecounter;
                if (stagecounter == 160 && stage == 0) {
                    world.setEntityState(this, (byte)16);
                    for (EnumFacing dir : EnumFacing.HORIZONTALS) {
                        BlockPos bp = new BlockPos((int) posX - dir.getFrontOffsetX() * 6, (int) posY, (int) posZ + dir.getFrontOffsetZ() * 6);
                        world.setBlockState(bp, BlocksTC.bannerCrimsonCult.getDefaultState(), 3);
                        TileEntity te = world.getTileEntity(new BlockPos((int) posX - dir.getFrontOffsetX() * 6, (int) posY, (int) posZ + dir.getFrontOffsetZ() * 6));
                        if (te != null && te instanceof TileBanner) {
                            int face = 0;
                            switch (dir.ordinal()) {
                                case 2: {
                                    face = 8;
                                    break;
                                }
                                case 3: {
                                    face = 0;
                                    break;
                                }
                                case 4: {
                                    face = 12;
                                    break;
                                }
                                case 5: {
                                    face = 4;
                                    break;
                                }
                            }
                            ((TileBanner)te).setBannerFacing((byte)face);
                            PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(new BlockPos((int) posX - dir.getFrontOffsetX() * 6, (int) posY, (int) posZ + dir.getFrontOffsetZ() * 6), this, 0.5f + rand.nextFloat() * 0.2f, 0.0f, 0.0f), new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 32.0));
                            playSound(SoundsTC.wandfail, 1.0f, 1.0f);
                        }
                    }
                }
                if (stagecounter > 20 && stagecounter < 150 && stage == 0 && stagecounter % 13 == 0) {
                    int a = (int) posX + rand.nextInt(5) - rand.nextInt(5);
                    int b = (int) posZ + rand.nextInt(5) - rand.nextInt(5);
                    BlockPos bp2 = new BlockPos(a, (int) posY, b);
                    if (a != (int) posX && b != (int) posZ && world.isAirBlock(bp2)) {
                        world.setEntityState(this, (byte)16);
                        float rr = world.rand.nextFloat();
                        int md = (rr < 0.05f) ? 2 : ((rr < 0.2f) ? 1 : 0);
                        Block bb = BlocksTC.lootCrateCommon;
                        switch (md) {
                            case 1: {
                                bb = BlocksTC.lootCrateUncommon;
                                break;
                            }
                            case 2: {
                                bb = BlocksTC.lootCrateRare;
                                break;
                            }
                        }
                        world.setBlockState(bp2, bb.getDefaultState());
                        PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(new BlockPos(a, (int) posY, b), this, 0.5f + rand.nextFloat() * 0.2f, 0.0f, 0.0f), new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 32.0));
                        playSound(SoundsTC.wandfail, 1.0f, 1.0f);
                    }
                }
            }
            else if (world.getClosestPlayerToEntity(this, 48.0) != null) {
                world.setEntityState(this, (byte)16);
                switch (stage) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4: {
                        stagecounter = 15 + rand.nextInt(10 - stage) - stage;
                        spawnMinions();
                        break;
                    }
                    case 12: {
                        stagecounter = 50 + getTiming() * 2 + rand.nextInt(50);
                        spawnBoss();
                        break;
                    }
                    default: {
                        int t = getTiming();
                        stagecounter = t + rand.nextInt(5 + t / 3);
                        spawnMinions();
                        break;
                    }
                }
                ++stage;
            }
            else {
                stagecounter = 30 + rand.nextInt(30);
            }
            if (stage < 12) {
                heal(1.0f);
            }
        }
        if (pulse > 0) {
            --pulse;
        }
    }
    
    int getTiming() {
        List<EntityCultist> l = EntityUtils.getEntitiesInRange(world, posX, posY, posZ, this, EntityCultist.class, 32.0);
        return l.size() * 20;
    }
    
    void spawnMinions() {
        EntityCultist cultist = null;
        if (rand.nextFloat() > 0.33) {
            cultist = new EntityCultistKnight(world);
        }
        else {
            cultist = new EntityCultistCleric(world);
        }
        cultist.setPosition(posX + rand.nextFloat() - rand.nextFloat(), posY + 0.25, posZ + rand.nextFloat() - rand.nextFloat());
        cultist.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(cultist.getPosition())), null);
        cultist.setHomePosAndDistance(getPosition(), 32);
        world.spawnEntity(cultist);
        cultist.spawnExplosionParticle();
        cultist.playSound(SoundsTC.wandfail, 1.0f, 1.0f);
        if (stage > 12) {
            attackEntityFrom(DamageSource.OUT_OF_WORLD, (float)(5 + rand.nextInt(5)));
        }
    }
    
    void spawnBoss() {
        EntityCultistLeader cultist = new EntityCultistLeader(world);
        cultist.setPosition(posX + rand.nextFloat() - rand.nextFloat(), posY + 0.25, posZ + rand.nextFloat() - rand.nextFloat());
        cultist.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(cultist.getPosition())), null);
        cultist.setHomePosAndDistance(getPosition(), 32);
        world.spawnEntity(cultist);
        cultist.spawnExplosionParticle();
        cultist.playSound(SoundsTC.wandfail, 1.0f, 1.0f);
    }
    
    public void onCollideWithPlayer(EntityPlayer p) {
        if (getDistanceSq(p) < 3.0 && p.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this), 8.0f)) {
            playSound(SoundsTC.zap, 1.0f, (rand.nextFloat() - rand.nextFloat()) * 0.1f + 1.0f);
        }
    }
    
    protected float getSoundVolume() {
        return 0.75f;
    }
    
    public int getTalkInterval() {
        return 540;
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundsTC.monolith;
    }
    
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsTC.zap;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundsTC.shock;
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected void dropFewItems(boolean flag, int fortune) {
        EntityUtils.entityDropSpecialItem(this, new ItemStack(ItemsTC.primordialPearl), height / 2.0f);
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte msg) {
        if (msg == 16) {
            pulse = 10;
        }
        else {
            super.handleStatusUpdate(msg);
        }
    }
    
    public void addPotionEffect(PotionEffect p_70690_1_) {
    }
    
    public void fall(float distance, float damageMultiplier) {
    }
    
    public void onDeath(DamageSource p_70645_1_) {
        if (!world.isRemote) {
            world.newExplosion(this, posX, posY, posZ, 2.0f, false, false);
        }
        super.onDeath(p_70645_1_);
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
    
    protected void updateAITasks() {
        super.updateAITasks();
        bossInfo.setPercent(getHealth() / getMaxHealth());
    }
}
