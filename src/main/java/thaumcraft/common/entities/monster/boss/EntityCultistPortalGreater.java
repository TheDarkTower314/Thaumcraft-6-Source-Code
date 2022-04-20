// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.boss;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.util.math.Vec3i;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;
import thaumcraft.common.entities.monster.cult.EntityCultistKnight;
import java.util.List;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.lib.SoundsTC;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.fx.PacketFXBlockArc;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.tiles.misc.TileBanner;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.BossInfoServer;
import net.minecraft.entity.monster.EntityMob;

public class EntityCultistPortalGreater extends EntityMob
{
    protected final BossInfoServer bossInfo;
    int stage;
    int stagecounter;
    public int pulse;
    
    public EntityCultistPortalGreater(final World par1World) {
        super(par1World);
        this.bossInfo = (BossInfoServer)new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.NOTCHED_6).setDarkenSky(true);
        this.stage = 0;
        this.stagecounter = 200;
        this.pulse = 0;
        this.isImmuneToFire = true;
        this.experienceValue = 30;
        this.setSize(1.5f, 3.0f);
    }
    
    public int getTotalArmorValue() {
        return 5;
    }
    
    protected void entityInit() {
        super.entityInit();
    }
    
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("stage", this.stage);
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.stage = nbt.getInteger("stage");
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(500.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.0);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
    }
    
    public boolean canBeCollidedWith() {
        return true;
    }
    
    public boolean canBePushed() {
        return false;
    }
    
    public void move(final MoverType mt, final double par1, final double par3, final double par5) {
    }
    
    public void onLivingUpdate() {
    }
    
    public boolean isInRangeToRenderDist(final double par1) {
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
        if (!this.world.isRemote) {
            if (this.stagecounter > 0) {
                --this.stagecounter;
                if (this.stagecounter == 160 && this.stage == 0) {
                    this.world.setEntityState(this, (byte)16);
                    for (final EnumFacing dir : EnumFacing.HORIZONTALS) {
                        final BlockPos bp = new BlockPos((int)this.posX - dir.getFrontOffsetX() * 6, (int)this.posY, (int)this.posZ + dir.getFrontOffsetZ() * 6);
                        this.world.setBlockState(bp, BlocksTC.bannerCrimsonCult.getDefaultState(), 3);
                        final TileEntity te = this.world.getTileEntity(new BlockPos((int)this.posX - dir.getFrontOffsetX() * 6, (int)this.posY, (int)this.posZ + dir.getFrontOffsetZ() * 6));
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
                            PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(new BlockPos((int)this.posX - dir.getFrontOffsetX() * 6, (int)this.posY, (int)this.posZ + dir.getFrontOffsetZ() * 6), this, 0.5f + this.rand.nextFloat() * 0.2f, 0.0f, 0.0f), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.posX, this.posY, this.posZ, 32.0));
                            this.playSound(SoundsTC.wandfail, 1.0f, 1.0f);
                        }
                    }
                }
                if (this.stagecounter > 20 && this.stagecounter < 150 && this.stage == 0 && this.stagecounter % 13 == 0) {
                    final int a = (int)this.posX + this.rand.nextInt(5) - this.rand.nextInt(5);
                    final int b = (int)this.posZ + this.rand.nextInt(5) - this.rand.nextInt(5);
                    final BlockPos bp2 = new BlockPos(a, (int)this.posY, b);
                    if (a != (int)this.posX && b != (int)this.posZ && this.world.isAirBlock(bp2)) {
                        this.world.setEntityState(this, (byte)16);
                        final float rr = this.world.rand.nextFloat();
                        final int md = (rr < 0.05f) ? 2 : ((rr < 0.2f) ? 1 : 0);
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
                        this.world.setBlockState(bp2, bb.getDefaultState());
                        PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(new BlockPos(a, (int)this.posY, b), this, 0.5f + this.rand.nextFloat() * 0.2f, 0.0f, 0.0f), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.posX, this.posY, this.posZ, 32.0));
                        this.playSound(SoundsTC.wandfail, 1.0f, 1.0f);
                    }
                }
            }
            else if (this.world.getClosestPlayerToEntity(this, 48.0) != null) {
                this.world.setEntityState(this, (byte)16);
                switch (this.stage) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4: {
                        this.stagecounter = 15 + this.rand.nextInt(10 - this.stage) - this.stage;
                        this.spawnMinions();
                        break;
                    }
                    case 12: {
                        this.stagecounter = 50 + this.getTiming() * 2 + this.rand.nextInt(50);
                        this.spawnBoss();
                        break;
                    }
                    default: {
                        final int t = this.getTiming();
                        this.stagecounter = t + this.rand.nextInt(5 + t / 3);
                        this.spawnMinions();
                        break;
                    }
                }
                ++this.stage;
            }
            else {
                this.stagecounter = 30 + this.rand.nextInt(30);
            }
            if (this.stage < 12) {
                this.heal(1.0f);
            }
        }
        if (this.pulse > 0) {
            --this.pulse;
        }
    }
    
    int getTiming() {
        final List<EntityCultist> l = EntityUtils.getEntitiesInRange(this.world, this.posX, this.posY, this.posZ, this, EntityCultist.class, 32.0);
        return l.size() * 20;
    }
    
    void spawnMinions() {
        EntityCultist cultist = null;
        if (this.rand.nextFloat() > 0.33) {
            cultist = new EntityCultistKnight(this.world);
        }
        else {
            cultist = new EntityCultistCleric(this.world);
        }
        cultist.setPosition(this.posX + this.rand.nextFloat() - this.rand.nextFloat(), this.posY + 0.25, this.posZ + this.rand.nextFloat() - this.rand.nextFloat());
        cultist.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(cultist.getPosition())), null);
        cultist.setHomePosAndDistance(this.getPosition(), 32);
        this.world.spawnEntity(cultist);
        cultist.spawnExplosionParticle();
        cultist.playSound(SoundsTC.wandfail, 1.0f, 1.0f);
        if (this.stage > 12) {
            this.attackEntityFrom(DamageSource.OUT_OF_WORLD, (float)(5 + this.rand.nextInt(5)));
        }
    }
    
    void spawnBoss() {
        final EntityCultistLeader cultist = new EntityCultistLeader(this.world);
        cultist.setPosition(this.posX + this.rand.nextFloat() - this.rand.nextFloat(), this.posY + 0.25, this.posZ + this.rand.nextFloat() - this.rand.nextFloat());
        cultist.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(cultist.getPosition())), null);
        cultist.setHomePosAndDistance(this.getPosition(), 32);
        this.world.spawnEntity(cultist);
        cultist.spawnExplosionParticle();
        cultist.playSound(SoundsTC.wandfail, 1.0f, 1.0f);
    }
    
    public void onCollideWithPlayer(final EntityPlayer p) {
        if (this.getDistanceSq(p) < 3.0 && p.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this), 8.0f)) {
            this.playSound(SoundsTC.zap, 1.0f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1f + 1.0f);
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
    
    protected SoundEvent getHurtSound(final DamageSource damageSourceIn) {
        return SoundsTC.zap;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundsTC.shock;
    }
    
    protected Item getDropItem() {
        return Item.getItemById(0);
    }
    
    protected void dropFewItems(final boolean flag, final int fortune) {
        EntityUtils.entityDropSpecialItem(this, new ItemStack(ItemsTC.primordialPearl), this.height / 2.0f);
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(final byte msg) {
        if (msg == 16) {
            this.pulse = 10;
        }
        else {
            super.handleStatusUpdate(msg);
        }
    }
    
    public void addPotionEffect(final PotionEffect p_70690_1_) {
    }
    
    public void fall(final float distance, final float damageMultiplier) {
    }
    
    public void onDeath(final DamageSource p_70645_1_) {
        if (!this.world.isRemote) {
            this.world.newExplosion(this, this.posX, this.posY, this.posZ, 2.0f, false, false);
        }
        super.onDeath(p_70645_1_);
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
    
    protected void updateAITasks() {
        super.updateAITasks();
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }
}
