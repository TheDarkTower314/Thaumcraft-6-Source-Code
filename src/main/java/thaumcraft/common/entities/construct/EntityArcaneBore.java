// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.construct;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.scoreboard.Team;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.Thaumcraft;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.item.ItemNameTag;
import thaumcraft.common.world.aura.AuraHandler;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.items.IItemHandler;
import java.util.List;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.common.lib.utils.Utils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.item.EntityItem;
import thaumcraft.common.lib.utils.BlockUtils;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayerMP;
import thaumcraft.common.lib.network.FakeNetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.EnumPacketDirection;
import net.minecraftforge.common.util.FakePlayerFactory;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.world.WorldServer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.Iterator;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.fx.PacketFXBoreDig;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.entity.Entity;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.BlockRailPowered;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.block.BlockRailBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EntityArcaneBore extends EntityOwnedConstruct
{
    BlockPos digTarget;
    BlockPos digTargetPrev;
    float digCost;
    int paused;
    int maxPause;
    long soundDelay;
    Object beam1;
    double beamLength;
    private static HashMap<Integer, ArrayList<ItemStack>> drops;
    int breakCounter;
    int digDelay;
    int digDelayMax;
    float radInc;
    public int spiral;
    public float currentRadius;
    private float charge;
    private static final DataParameter<EnumFacing> FACING;
    private static final DataParameter<Boolean> ACTIVE;
    public boolean clientDigging;
    
    public EntityArcaneBore(final World worldIn) {
        super(worldIn);
        this.digTarget = null;
        this.digTargetPrev = null;
        this.digCost = 0.25f;
        this.paused = 100;
        this.maxPause = 100;
        this.soundDelay = 0L;
        this.beam1 = null;
        this.beamLength = 0.0;
        this.breakCounter = 0;
        this.digDelay = 0;
        this.digDelayMax = 0;
        this.radInc = 0.0f;
        this.spiral = 0;
        this.currentRadius = 0.0f;
        this.charge = 0.0f;
        this.clientDigging = false;
        this.setSize(0.9f, 0.9f);
    }
    
    public EntityArcaneBore(final World worldIn, final BlockPos pos, final EnumFacing facing) {
        this(worldIn);
        this.setFacing(facing);
        this.setPositionAndRotation(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0f, 0.0f);
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.world.isRemote) {
            this.rotationYaw = this.rotationYawHead;
            if (this.ticksExisted % 50 == 0) {
                this.heal(1.0f);
            }
            if (this.ticksExisted % 10 == 0 && this.getCharge() < 10.0f) {
                this.rechargeVis();
            }
            final int k = MathHelper.floor(this.posX);
            int l = MathHelper.floor(this.posY);
            final int i1 = MathHelper.floor(this.posZ);
            if (BlockRailBase.isRailBlock(this.world, new BlockPos(k, l - 1, i1))) {
                --l;
            }
            final BlockPos blockpos = new BlockPos(k, l, i1);
            final IBlockState iblockstate = this.world.getBlockState(blockpos);
            if (BlockRailBase.isRailBlock(iblockstate)) {
                if (iblockstate.getBlock() == BlocksTC.activatorRail) {
                    final boolean ac = (boolean)iblockstate.getValue((IProperty)BlockRailPowered.POWERED);
                    this.setActive(!ac);
                }
            }
            else if (!this.isRiding()) {
                this.setActive(this.world.isBlockPowered(new BlockPos(this).down()));
            }
            if (this.validInventory()) {
                try {
                    this.getHeldItemMainhand().updateAnimation(this.world, this, 0, true);
                }
                catch (final Exception ex) {}
            }
        }
        if (!this.isActive()) {
            this.digTarget = null;
            this.getLookHelper().setLookPosition(this.posX + this.getFacing().getFrontOffsetX(), this.posY, this.posZ + this.getFacing().getFrontOffsetZ(), 10.0f, 33.0f);
        }
        if (this.digTarget != null && this.getCharge() >= this.digCost && !this.world.isRemote) {
            this.getLookHelper().setLookPosition(this.digTarget.getX() + 0.5, this.digTarget.getY(), this.digTarget.getZ() + 0.5, 10.0f, 90.0f);
            if (this.digDelay-- <= 0 && this.dig()) {
                this.setCharge((byte)(this.getCharge() - this.digCost));
                if (this.soundDelay < System.currentTimeMillis()) {
                    this.soundDelay = System.currentTimeMillis() + 1200L + this.world.rand.nextInt(100);
                    this.playSound(SoundsTC.rumble, 0.25f, 0.9f + this.world.rand.nextFloat() * 0.2f);
                }
            }
        }
        if (!this.world.isRemote && this.digTarget == null && this.isActive() && this.validInventory()) {
            this.findNextBlockToDig();
            if (this.digTarget != null) {
                this.world.setEntityState(this, (byte)16);
                PacketHandler.INSTANCE.sendToAllAround(new PacketFXBoreDig(this.digTarget, this, this.digDelayMax), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.digTarget.getX(), this.digTarget.getY(), this.digTarget.getZ(), 32.0));
            }
            else {
                this.world.setEntityState(this, (byte)17);
                this.getLookHelper().setLookPosition(this.posX + this.getFacing().getFrontOffsetX() * 2, this.posY + this.getFacing().getFrontOffsetY() * 2 + this.getEyeHeight(), this.posZ + this.getFacing().getFrontOffsetZ() * 2, 10.0f, 33.0f);
            }
        }
    }
    
    public boolean validInventory() {
        boolean b = this.getHeldItemMainhand() != null && !this.getHeldItemMainhand().isEmpty() && (this.getHeldItemMainhand().getItem() instanceof ItemPickaxe || this.getHeldItemMainhand().getItem().getToolClasses(this.getHeldItemMainhand()).contains("pickaxe"));
        if (b && this.getHeldItemMainhand().getItemDamage() + 1 >= this.getHeldItemMainhand().getMaxDamage()) {
            b = false;
        }
        return b;
    }
    
    public int getDigRadius() {
        int r = 0;
        if (this.getHeldItemMainhand() != null && !this.getHeldItemMainhand().isEmpty() && (this.getHeldItemMainhand().getItem() instanceof ItemPickaxe || this.getHeldItemMainhand().getItem().getToolClasses(this.getHeldItemMainhand()).contains("pickaxe"))) {
            r = this.getHeldItemMainhand().getItem().getItemEnchantability() / 3;
            r += EnumInfusionEnchantment.getInfusionEnchantmentLevel(this.getHeldItemMainhand(), EnumInfusionEnchantment.DESTRUCTIVE) * 2;
        }
        return (r <= 1) ? 2 : r;
    }
    
    public int getDigDepth() {
        int r = this.getDigRadius() * 8;
        r += EnumInfusionEnchantment.getInfusionEnchantmentLevel(this.getHeldItemMainhand(), EnumInfusionEnchantment.BURROWING) * 16;
        return r;
    }
    
    public int getFortune() {
        int r = 0;
        if (this.validInventory()) {
            r = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, this.getHeldItemMainhand());
            final int r2 = EnumInfusionEnchantment.getInfusionEnchantmentLevel(this.getHeldItemMainhand(), EnumInfusionEnchantment.SOUNDING);
            r = Math.max(r, r2);
        }
        return r;
    }
    
    public int getDigSpeed(final IBlockState blockState) {
        int speed = 0;
        if (this.validInventory()) {
            speed += (int)(this.getHeldItemMainhand().getItem().getDestroySpeed(this.getHeldItemMainhand(), blockState) / 2.0f);
            speed += EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, this.getHeldItemMainhand());
        }
        return speed;
    }
    
    public int getRefining() {
        int refining = 0;
        if (this.getHeldItemMainhand() != null && !this.getHeldItemMainhand().isEmpty()) {
            refining = EnumInfusionEnchantment.getInfusionEnchantmentLevel(this.getHeldItemMainhand(), EnumInfusionEnchantment.REFINING);
        }
        return refining;
    }
    
    public boolean hasSilkTouch() {
        return this.getHeldItemMainhand() != null && !this.getHeldItemMainhand().isEmpty() && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, this.getHeldItemMainhand()) > 0;
    }
    
    private boolean canSilkTouch(final BlockPos pos, final IBlockState state) {
        return this.hasSilkTouch() && state.getBlock().canSilkHarvest(this.world, pos, state, null);
    }
    
    @SubscribeEvent
    public static void harvestBlockEvent(final BlockEvent.HarvestDropsEvent event) {
        if (event.getHarvester() != null && event.getHarvester().getName().equals("FakeThaumcraftBore")) {
            ArrayList<ItemStack> droplist = new ArrayList<ItemStack>();
            if (EntityArcaneBore.drops.containsKey(event.getHarvester().arrowHitTimer) && EntityArcaneBore.drops.get(event.getHarvester().arrowHitTimer) != null) {
                droplist = EntityArcaneBore.drops.get(event.getHarvester().arrowHitTimer);
            }
            for (final ItemStack s : event.getDrops()) {
                if (event.getHarvester().world.rand.nextFloat() <= event.getDropChance()) {
                    droplist.add(s);
                }
            }
            EntityArcaneBore.drops.put(event.getHarvester().arrowHitTimer, droplist);
            event.getDrops().clear();
        }
    }
    
    private boolean dig() {
        boolean b = false;
        if (this.digTarget != null && !this.world.isAirBlock(this.digTarget)) {
            final IBlockState digBs = this.world.getBlockState(this.digTarget);
            if (!digBs.getBlock().isAir(digBs, this.world, this.digTarget)) {
                boolean silktouch = false;
                int fortune = this.getFortune();
                if (this.canSilkTouch(this.digTarget, digBs)) {
                    silktouch = true;
                    fortune = 0;
                }
                final FakePlayer fp = FakePlayerFactory.get((WorldServer)this.world, new GameProfile(null, "FakeThaumcraftBore"));
                fp.connection = new FakeNetHandlerPlayServer(fp.mcServer, new NetworkManager(EnumPacketDirection.CLIENTBOUND), fp);
                fp.arrowHitTimer = this.getEntityId();
                fp.xpCooldown = 1;
                fp.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
                fp.setHeldItem(EnumHand.MAIN_HAND, this.getHeldItemMainhand());
                if (BlockUtils.harvestBlock(this.getEntityWorld(), fp, this.digTarget, false, false, fortune, false)) {
                    ArrayList<ItemStack> items = EntityArcaneBore.drops.get(this.getEntityId());
                    if (items == null) {
                        items = new ArrayList<ItemStack>();
                    }
                    final List<EntityItem> targets = this.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(this.digTarget.getX(), this.digTarget.getY(), this.digTarget.getZ(), this.digTarget.getX() + 1, this.digTarget.getY() + 1, this.digTarget.getZ() + 1).grow(1.5, 1.5, 1.5));
                    if (targets.size() > 0) {
                        for (final EntityItem e : targets) {
                            items.add(e.getItem().copy());
                            e.setDead();
                        }
                    }
                    final int refining = this.getRefining();
                    if (items.size() > 0) {
                        for (final ItemStack is : items) {
                            ItemStack dropped = is.copy();
                            if (!silktouch && refining > 0) {
                                dropped = Utils.findSpecialMiningResult(is, (refining + 1) * 0.125f, this.world.rand);
                            }
                            if (dropped != null && !dropped.isEmpty()) {
                                boolean e2 = false;
                                for (final EnumFacing f : EnumFacing.VALUES) {
                                    final BlockPos p = this.getPosition().offset(f);
                                    final IItemHandler inventory = ThaumcraftInvHelper.getItemHandlerAt(this.getEntityWorld(), p, f);
                                    if (inventory != null) {
                                        InventoryUtils.ejectStackAt(this.getEntityWorld(), this.getPosition(), f, dropped);
                                        e2 = true;
                                        break;
                                    }
                                }
                                if (e2) {
                                    continue;
                                }
                                InventoryUtils.ejectStackAt(this.getEntityWorld(), this.getPosition(), this.getFacing().getOpposite(), dropped);
                            }
                        }
                    }
                    this.breakCounter += fp.xpCooldown;
                    items.clear();
                }
            }
            if (this.getHeldItemMainhand() != null && !this.getHeldItemMainhand().isEmpty()) {
                if (this.breakCounter >= 50) {
                    this.breakCounter -= 50;
                    this.getHeldItemMainhand().damageItem(1, this);
                }
                if (this.getHeldItemMainhand().getCount() <= 0) {
                    this.setHeldItem(this.getActiveHand(), ItemStack.EMPTY);
                }
            }
            else {
                this.breakCounter = 0;
            }
            b = this.world.setBlockToAir(this.digTarget);
        }
        this.digTarget = null;
        return b;
    }
    
    private void findNextBlockToDig() {
        if (this.digTargetPrev == null || this.getDistanceSqToCenter(this.digTargetPrev) > (this.getDigRadius() + 1) * (this.getDigRadius() + 1)) {
            this.digTargetPrev = new BlockPos(this);
        }
        if (this.radInc == 0.0f) {
            this.radInc = 1.0f;
        }
        final int digRadius = this.getDigRadius();
        final int digDepth = this.getDigDepth();
        int x = this.digTargetPrev.getX();
        int z = this.digTargetPrev.getZ();
        int y = this.digTargetPrev.getY();
        final int x2 = x + this.getFacing().getFrontOffsetX() * digDepth;
        final int y2 = y + this.getFacing().getFrontOffsetY() * digDepth;
        final int z2 = z + this.getFacing().getFrontOffsetZ() * digDepth;
        final BlockPos end = new BlockPos(x2, y2, z2);
        RayTraceResult mop = this.world.rayTraceBlocks(new Vec3d(this.digTargetPrev).addVector(0.5, 0.5, 0.5), new Vec3d(end).addVector(0.5, 0.5, 0.5), false, true, false);
        if (mop != null) {
            final Vec3d digger = new Vec3d(this.posX + this.getFacing().getFrontOffsetX(), this.posY + this.getEyeHeight() + this.getFacing().getFrontOffsetY(), this.posZ + this.getFacing().getFrontOffsetZ());
            mop = this.world.rayTraceBlocks(digger, new Vec3d(mop.getBlockPos()).addVector(0.5, 0.5, 0.5), false, true, false);
            if (mop != null) {
                final IBlockState blockState = this.world.getBlockState(mop.getBlockPos());
                if (blockState.getBlockHardness(this.world, mop.getBlockPos()) > -1.0f && blockState.getCollisionBoundingBox(this.world, mop.getBlockPos()) != null) {
                    this.digDelay = Math.max(10 - this.getDigSpeed(blockState), (int)(blockState.getBlockHardness(this.world, mop.getBlockPos()) * 2.0f) - this.getDigSpeed(blockState) * 2);
                    if (this.digDelay < 1) {
                        this.digDelay = 1;
                    }
                    this.digDelayMax = this.digDelay;
                    if (!mop.getBlockPos().equals(this.getPosition()) && !mop.getBlockPos().equals(this.getPosition().down())) {
                        this.digTarget = mop.getBlockPos();
                        return;
                    }
                }
            }
        }
        while (x == this.digTargetPrev.getX() && z == this.digTargetPrev.getZ() && y == this.digTargetPrev.getY()) {
            if (Math.abs(this.currentRadius) > digRadius) {
                this.currentRadius = (float)digRadius;
            }
            this.spiral += (int)(3.0f + Math.max(0.0f, (10.0f - Math.abs(this.currentRadius)) * 2.0f));
            if (this.spiral >= 360) {
                this.spiral -= 360;
                this.currentRadius += this.radInc;
                if (this.currentRadius > digRadius || this.currentRadius < -digRadius) {
                    this.currentRadius = 0.0f;
                }
            }
            final Vec3d vsource = new Vec3d((int)this.posX + 0.5 + this.getFacing().getFrontOffsetX(), this.posY + this.getFacing().getFrontOffsetY() + this.getEyeHeight(), (int)this.posZ + 0.5 + this.getFacing().getFrontOffsetZ());
            Vec3d vtar = new Vec3d(0.0, this.currentRadius, 0.0);
            vtar = Utils.rotateAroundZ(vtar, this.spiral / 180.0f * 3.1415927f);
            vtar = Utils.rotateAroundY(vtar, 1.5707964f * this.getFacing().getFrontOffsetX());
            vtar = Utils.rotateAroundX(vtar, 1.5707964f * this.getFacing().getFrontOffsetY());
            final Vec3d vres = vsource.addVector(vtar.x, vtar.y, vtar.z);
            x = MathHelper.floor(vres.x);
            y = MathHelper.floor(vres.y);
            z = MathHelper.floor(vres.z);
        }
        this.digTargetPrev = new BlockPos(x, y, z);
    }
    
    public boolean attackEntityFrom(final DamageSource source, final float amount) {
        try {
            if (source.getTrueSource() != null && this.isOwner((EntityLivingBase)source.getTrueSource())) {
                final EnumFacing f = EnumFacing.getDirectionFromEntityLiving(this.getPosition(), (EntityLivingBase)source.getTrueSource());
                if (f != EnumFacing.DOWN) {
                    this.setFacing(f);
                }
                return false;
            }
        }
        catch (final Exception ex) {}
        this.rotationYaw += (float)(this.getRNG().nextGaussian() * 45.0);
        this.rotationPitch += (float)(this.getRNG().nextGaussian() * 20.0);
        return super.attackEntityFrom(source, amount);
    }
    
    protected void rechargeVis() {
        this.setCharge(this.getCharge() + AuraHandler.drainVis(this.world, this.getPosition(), 10.0f, false));
    }
    
    public boolean canBePushed() {
        return true;
    }
    
    public boolean canBeCollidedWith() {
        return true;
    }
    
    @Override
    public void onDeath(final DamageSource cause) {
        super.onDeath(cause);
        if (!this.world.isRemote) {
            this.dropStuff();
        }
    }
    
    protected void dropStuff() {
        if (this.getHeldItemMainhand() != null && !this.getHeldItemMainhand().isEmpty()) {
            this.entityDropItem(this.getHeldItemMainhand(), 0.5f);
        }
    }
    
    @Override
    protected boolean processInteract(final EntityPlayer player, final EnumHand hand) {
        if (player.getHeldItem(hand).getItem() instanceof ItemNameTag) {
            return false;
        }
        if (!this.world.isRemote && this.isOwner(player) && !this.isDead) {
            if (player.isSneaking()) {
                this.playSound(SoundsTC.zap, 1.0f, 1.0f);
                this.dropStuff();
                this.entityDropItem(new ItemStack(ItemsTC.turretPlacer, 1, 2), 0.5f);
                this.setDead();
                player.swingArm(hand);
            }
            else {
                player.openGui(Thaumcraft.instance, 14, this.world, this.getEntityId(), 0, 0);
            }
            return true;
        }
        return super.processInteract(player, hand);
    }
    
    public void knockBack(final Entity p_70653_1_, final float p_70653_2_, final double p_70653_3_, final double p_70653_5_) {
        super.knockBack(p_70653_1_, p_70653_2_, p_70653_3_ / 10.0, p_70653_5_ / 10.0);
        if (this.motionY > 0.1) {
            this.motionY = 0.1;
        }
    }
    
    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(EntityArcaneBore.FACING, EnumFacing.DOWN);
        this.dataManager.register(EntityArcaneBore.ACTIVE, false);
    }
    
    public boolean isActive() {
        return (boolean)this.dataManager.get((DataParameter)EntityArcaneBore.ACTIVE);
    }
    
    public void setActive(final boolean attacking) {
        this.dataManager.set(EntityArcaneBore.ACTIVE, attacking);
    }
    
    @Override
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setCharge(nbt.getFloat("charge"));
        this.setFacing(EnumFacing.VALUES[nbt.getByte("faceing")]);
        this.setActive(nbt.getBoolean("active"));
    }
    
    @Override
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setFloat("charge", this.getCharge());
        nbt.setByte("faceing", (byte)this.getFacing().getIndex());
        nbt.setBoolean("active", this.isActive());
    }
    
    public EnumFacing getFacing() {
        return (EnumFacing)this.getDataManager().get((DataParameter)EntityArcaneBore.FACING);
    }
    
    public void setFacing(final EnumFacing face) {
        this.getDataManager().set(EntityArcaneBore.FACING, face);
    }
    
    public float getCharge() {
        return this.charge;
    }
    
    public void setCharge(final float c) {
        this.charge = c;
    }
    
    public void move(final MoverType mt, final double x, final double y, final double z) {
        super.move(mt, x / 5.0, y, z / 5.0);
    }
    
    public void onKillCommand() {
        this.attackEntityFrom(DamageSource.OUT_OF_WORLD, 400.0f);
    }
    
    protected void dropFewItems(final boolean p_70628_1_, final int treasure) {
        final float b = treasure * 0.15f;
        if (this.rand.nextFloat() < 0.2f + b) {
            this.entityDropItem(new ItemStack(ItemsTC.mind), 0.5f);
        }
        if (this.rand.nextFloat() < 0.2f + b) {
            this.entityDropItem(new ItemStack(ItemsTC.morphicResonator), 0.5f);
        }
        if (this.rand.nextFloat() < 0.2f + b) {
            this.entityDropItem(new ItemStack(BlocksTC.crystalAir), 0.5f);
        }
        if (this.rand.nextFloat() < 0.2f + b) {
            this.entityDropItem(new ItemStack(BlocksTC.crystalEarth), 0.5f);
        }
        if (this.rand.nextFloat() < 0.5f + b) {
            this.entityDropItem(new ItemStack(ItemsTC.mechanismSimple), 0.5f);
        }
        if (this.rand.nextFloat() < 0.5f + b) {
            this.entityDropItem(new ItemStack(ItemsTC.plate), 0.5f);
        }
        if (this.rand.nextFloat() < 0.5f + b) {
            this.entityDropItem(new ItemStack(BlocksTC.plankGreatwood), 0.5f);
        }
    }
    
    public int getVerticalFaceSpeed() {
        return 10;
    }
    
    @Override
    public Team getTeam() {
        if (this.isOwned()) {
            final EntityLivingBase entitylivingbase = this.getOwnerEntity();
            if (entitylivingbase != null) {
                return entitylivingbase.getTeam();
            }
        }
        return super.getTeam();
    }
    
    public float getEyeHeight() {
        return 0.8125f;
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(final byte par1) {
        if (par1 == 16) {
            this.clientDigging = true;
        }
        else if (par1 == 17) {
            this.clientDigging = false;
        }
        else {
            super.handleStatusUpdate(par1);
        }
    }
    
    static {
        EntityArcaneBore.drops = new HashMap<Integer, ArrayList<ItemStack>>();
        FACING = EntityDataManager.createKey(EntityArcaneBore.class, DataSerializers.FACING);
        ACTIVE = EntityDataManager.createKey(EntityArcaneBore.class, DataSerializers.BOOLEAN);
    }
}
