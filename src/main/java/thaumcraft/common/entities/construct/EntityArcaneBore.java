package thaumcraft.common.entities.construct;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;
import thaumcraft.common.lib.network.FakeNetHandlerPlayServer;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBoreDig;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.world.aura.AuraHandler;


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
    private static DataParameter<EnumFacing> FACING;
    private static DataParameter<Boolean> ACTIVE;
    public boolean clientDigging;
    
    public EntityArcaneBore(World worldIn) {
        super(worldIn);
        digTarget = null;
        digTargetPrev = null;
        digCost = 0.25f;
        paused = 100;
        maxPause = 100;
        soundDelay = 0L;
        beam1 = null;
        beamLength = 0.0;
        breakCounter = 0;
        digDelay = 0;
        digDelayMax = 0;
        radInc = 0.0f;
        spiral = 0;
        currentRadius = 0.0f;
        charge = 0.0f;
        clientDigging = false;
        setSize(0.9f, 0.9f);
    }
    
    public EntityArcaneBore(World worldIn, BlockPos pos, EnumFacing facing) {
        this(worldIn);
        setFacing(facing);
        setPositionAndRotation(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0f, 0.0f);
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote) {
            rotationYaw = rotationYawHead;
            if (ticksExisted % 50 == 0) {
                heal(1.0f);
            }
            if (ticksExisted % 10 == 0 && getCharge() < 10.0f) {
                rechargeVis();
            }
            int k = MathHelper.floor(posX);
            int l = MathHelper.floor(posY);
            int i1 = MathHelper.floor(posZ);
            if (BlockRailBase.isRailBlock(world, new BlockPos(k, l - 1, i1))) {
                --l;
            }
            BlockPos blockpos = new BlockPos(k, l, i1);
            IBlockState iblockstate = world.getBlockState(blockpos);
            if (BlockRailBase.isRailBlock(iblockstate)) {
                if (iblockstate.getBlock() == BlocksTC.activatorRail) {
                    boolean ac = (boolean)iblockstate.getValue((IProperty)BlockRailPowered.POWERED);
                    setActive(!ac);
                }
            }
            else if (!isRiding()) {
                setActive(world.isBlockPowered(new BlockPos(this).down()));
            }
            if (validInventory()) {
                try {
                    getHeldItemMainhand().updateAnimation(world, this, 0, true);
                }
                catch (Exception ex) {}
            }
        }
        if (!isActive()) {
            digTarget = null;
            getLookHelper().setLookPosition(posX + getFacing().getFrontOffsetX(), posY, posZ + getFacing().getFrontOffsetZ(), 10.0f, 33.0f);
        }
        if (digTarget != null && getCharge() >= digCost && !world.isRemote) {
            getLookHelper().setLookPosition(digTarget.getX() + 0.5, digTarget.getY(), digTarget.getZ() + 0.5, 10.0f, 90.0f);
            if (digDelay-- <= 0 && dig()) {
                setCharge((byte)(getCharge() - digCost));
                if (soundDelay < System.currentTimeMillis()) {
                    soundDelay = System.currentTimeMillis() + 1200L + world.rand.nextInt(100);
                    playSound(SoundsTC.rumble, 0.25f, 0.9f + world.rand.nextFloat() * 0.2f);
                }
            }
        }
        if (!world.isRemote && digTarget == null && isActive() && validInventory()) {
            findNextBlockToDig();
            if (digTarget != null) {
                world.setEntityState(this, (byte)16);
                PacketHandler.INSTANCE.sendToAllAround(new PacketFXBoreDig(digTarget, this, digDelayMax), new NetworkRegistry.TargetPoint(world.provider.getDimension(), digTarget.getX(), digTarget.getY(), digTarget.getZ(), 32.0));
            }
            else {
                world.setEntityState(this, (byte)17);
                getLookHelper().setLookPosition(posX + getFacing().getFrontOffsetX() * 2, posY + getFacing().getFrontOffsetY() * 2 + getEyeHeight(), posZ + getFacing().getFrontOffsetZ() * 2, 10.0f, 33.0f);
            }
        }
    }
    
    public boolean validInventory() {
        boolean b = getHeldItemMainhand() != null && !getHeldItemMainhand().isEmpty() && (getHeldItemMainhand().getItem() instanceof ItemPickaxe || getHeldItemMainhand().getItem().getToolClasses(getHeldItemMainhand()).contains("pickaxe"));
        if (b && getHeldItemMainhand().getItemDamage() + 1 >= getHeldItemMainhand().getMaxDamage()) {
            b = false;
        }
        return b;
    }
    
    public int getDigRadius() {
        int r = 0;
        if (getHeldItemMainhand() != null && !getHeldItemMainhand().isEmpty() && (getHeldItemMainhand().getItem() instanceof ItemPickaxe || getHeldItemMainhand().getItem().getToolClasses(getHeldItemMainhand()).contains("pickaxe"))) {
            r = getHeldItemMainhand().getItem().getItemEnchantability() / 3;
            r += EnumInfusionEnchantment.getInfusionEnchantmentLevel(getHeldItemMainhand(), EnumInfusionEnchantment.DESTRUCTIVE) * 2;
        }
        return (r <= 1) ? 2 : r;
    }
    
    public int getDigDepth() {
        int r = getDigRadius() * 8;
        r += EnumInfusionEnchantment.getInfusionEnchantmentLevel(getHeldItemMainhand(), EnumInfusionEnchantment.BURROWING) * 16;
        return r;
    }
    
    public int getFortune() {
        int r = 0;
        if (validInventory()) {
            r = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, getHeldItemMainhand());
            int r2 = EnumInfusionEnchantment.getInfusionEnchantmentLevel(getHeldItemMainhand(), EnumInfusionEnchantment.SOUNDING);
            r = Math.max(r, r2);
        }
        return r;
    }
    
    public int getDigSpeed(IBlockState blockState) {
        int speed = 0;
        if (validInventory()) {
            speed += (int)(getHeldItemMainhand().getItem().getDestroySpeed(getHeldItemMainhand(), blockState) / 2.0f);
            speed += EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, getHeldItemMainhand());
        }
        return speed;
    }
    
    public int getRefining() {
        int refining = 0;
        if (getHeldItemMainhand() != null && !getHeldItemMainhand().isEmpty()) {
            refining = EnumInfusionEnchantment.getInfusionEnchantmentLevel(getHeldItemMainhand(), EnumInfusionEnchantment.REFINING);
        }
        return refining;
    }
    
    public boolean hasSilkTouch() {
        return getHeldItemMainhand() != null && !getHeldItemMainhand().isEmpty() && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, getHeldItemMainhand()) > 0;
    }
    
    private boolean canSilkTouch(BlockPos pos, IBlockState state) {
        return hasSilkTouch() && state.getBlock().canSilkHarvest(world, pos, state, null);
    }
    
    @SubscribeEvent
    public static void harvestBlockEvent(BlockEvent.HarvestDropsEvent event) {
        if (event.getHarvester() != null && event.getHarvester().getName().equals("FakeThaumcraftBore")) {
            ArrayList<ItemStack> droplist = new ArrayList<ItemStack>();
            if (EntityArcaneBore.drops.containsKey(event.getHarvester().arrowHitTimer) && EntityArcaneBore.drops.get(event.getHarvester().arrowHitTimer) != null) {
                droplist = EntityArcaneBore.drops.get(event.getHarvester().arrowHitTimer);
            }
            for (ItemStack s : event.getDrops()) {
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
        if (digTarget != null && !world.isAirBlock(digTarget)) {
            IBlockState digBs = world.getBlockState(digTarget);
            if (!digBs.getBlock().isAir(digBs, world, digTarget)) {
                boolean silktouch = false;
                int fortune = getFortune();
                if (canSilkTouch(digTarget, digBs)) {
                    silktouch = true;
                    fortune = 0;
                }
                FakePlayer fp = FakePlayerFactory.get((WorldServer) world, new GameProfile(null, "FakeThaumcraftBore"));
                fp.connection = new FakeNetHandlerPlayServer(fp.mcServer, new NetworkManager(EnumPacketDirection.CLIENTBOUND), fp);
                fp.arrowHitTimer = getEntityId();
                fp.xpCooldown = 1;
                fp.setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
                fp.setHeldItem(EnumHand.MAIN_HAND, getHeldItemMainhand());
                if (BlockUtils.harvestBlock(getEntityWorld(), fp, digTarget, false, false, fortune, false)) {
                    ArrayList<ItemStack> items = EntityArcaneBore.drops.get(getEntityId());
                    if (items == null) {
                        items = new ArrayList<ItemStack>();
                    }
                    List<EntityItem> targets = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(digTarget.getX(), digTarget.getY(), digTarget.getZ(), digTarget.getX() + 1, digTarget.getY() + 1, digTarget.getZ() + 1).grow(1.5, 1.5, 1.5));
                    if (targets.size() > 0) {
                        for (EntityItem e : targets) {
                            items.add(e.getItem().copy());
                            e.setDead();
                        }
                    }
                    int refining = getRefining();
                    if (items.size() > 0) {
                        for (ItemStack is : items) {
                            ItemStack dropped = is.copy();
                            if (!silktouch && refining > 0) {
                                dropped = Utils.findSpecialMiningResult(is, (refining + 1) * 0.125f, world.rand);
                            }
                            if (dropped != null && !dropped.isEmpty()) {
                                boolean e2 = false;
                                for (EnumFacing f : EnumFacing.VALUES) {
                                    BlockPos p = getPosition().offset(f);
                                    IItemHandler inventory = ThaumcraftInvHelper.getItemHandlerAt(getEntityWorld(), p, f);
                                    if (inventory != null) {
                                        InventoryUtils.ejectStackAt(getEntityWorld(), getPosition(), f, dropped);
                                        e2 = true;
                                        break;
                                    }
                                }
                                if (e2) {
                                    continue;
                                }
                                InventoryUtils.ejectStackAt(getEntityWorld(), getPosition(), getFacing().getOpposite(), dropped);
                            }
                        }
                    }
                    breakCounter += fp.xpCooldown;
                    items.clear();
                }
            }
            if (getHeldItemMainhand() != null && !getHeldItemMainhand().isEmpty()) {
                if (breakCounter >= 50) {
                    breakCounter -= 50;
                    getHeldItemMainhand().damageItem(1, this);
                }
                if (getHeldItemMainhand().getCount() <= 0) {
                    setHeldItem(getActiveHand(), ItemStack.EMPTY);
                }
            }
            else {
                breakCounter = 0;
            }
            b = world.setBlockToAir(digTarget);
        }
        digTarget = null;
        return b;
    }
    
    private void findNextBlockToDig() {
        if (digTargetPrev == null || getDistanceSqToCenter(digTargetPrev) > (getDigRadius() + 1) * (getDigRadius() + 1)) {
            digTargetPrev = new BlockPos(this);
        }
        if (radInc == 0.0f) {
            radInc = 1.0f;
        }
        int digRadius = getDigRadius();
        int digDepth = getDigDepth();
        int x = digTargetPrev.getX();
        int z = digTargetPrev.getZ();
        int y = digTargetPrev.getY();
        int x2 = x + getFacing().getFrontOffsetX() * digDepth;
        int y2 = y + getFacing().getFrontOffsetY() * digDepth;
        int z2 = z + getFacing().getFrontOffsetZ() * digDepth;
        BlockPos end = new BlockPos(x2, y2, z2);
        RayTraceResult mop = world.rayTraceBlocks(new Vec3d(digTargetPrev).addVector(0.5, 0.5, 0.5), new Vec3d(end).addVector(0.5, 0.5, 0.5), false, true, false);
        if (mop != null) {
            Vec3d digger = new Vec3d(posX + getFacing().getFrontOffsetX(), posY + getEyeHeight() + getFacing().getFrontOffsetY(), posZ + getFacing().getFrontOffsetZ());
            mop = world.rayTraceBlocks(digger, new Vec3d(mop.getBlockPos()).addVector(0.5, 0.5, 0.5), false, true, false);
            if (mop != null) {
                IBlockState blockState = world.getBlockState(mop.getBlockPos());
                if (blockState.getBlockHardness(world, mop.getBlockPos()) > -1.0f && blockState.getCollisionBoundingBox(world, mop.getBlockPos()) != null) {
                    digDelay = Math.max(10 - getDigSpeed(blockState), (int)(blockState.getBlockHardness(world, mop.getBlockPos()) * 2.0f) - getDigSpeed(blockState) * 2);
                    if (digDelay < 1) {
                        digDelay = 1;
                    }
                    digDelayMax = digDelay;
                    if (!mop.getBlockPos().equals(getPosition()) && !mop.getBlockPos().equals(getPosition().down())) {
                        digTarget = mop.getBlockPos();
                        return;
                    }
                }
            }
        }
        while (x == digTargetPrev.getX() && z == digTargetPrev.getZ() && y == digTargetPrev.getY()) {
            if (Math.abs(currentRadius) > digRadius) {
                currentRadius = (float)digRadius;
            }
            spiral += (int)(3.0f + Math.max(0.0f, (10.0f - Math.abs(currentRadius)) * 2.0f));
            if (spiral >= 360) {
                spiral -= 360;
                currentRadius += radInc;
                if (currentRadius > digRadius || currentRadius < -digRadius) {
                    currentRadius = 0.0f;
                }
            }
            Vec3d vsource = new Vec3d((int) posX + 0.5 + getFacing().getFrontOffsetX(), posY + getFacing().getFrontOffsetY() + getEyeHeight(), (int) posZ + 0.5 + getFacing().getFrontOffsetZ());
            Vec3d vtar = new Vec3d(0.0, currentRadius, 0.0);
            vtar = Utils.rotateAroundZ(vtar, spiral / 180.0f * 3.1415927f);
            vtar = Utils.rotateAroundY(vtar, 1.5707964f * getFacing().getFrontOffsetX());
            vtar = Utils.rotateAroundX(vtar, 1.5707964f * getFacing().getFrontOffsetY());
            Vec3d vres = vsource.addVector(vtar.x, vtar.y, vtar.z);
            x = MathHelper.floor(vres.x);
            y = MathHelper.floor(vres.y);
            z = MathHelper.floor(vres.z);
        }
        digTargetPrev = new BlockPos(x, y, z);
    }
    
    public boolean attackEntityFrom(DamageSource source, float amount) {
        try {
            if (source.getTrueSource() != null && isOwner((EntityLivingBase)source.getTrueSource())) {
                EnumFacing f = EnumFacing.getDirectionFromEntityLiving(getPosition(), (EntityLivingBase)source.getTrueSource());
                if (f != EnumFacing.DOWN) {
                    setFacing(f);
                }
                return false;
            }
        }
        catch (Exception ex) {}
        rotationYaw += (float)(getRNG().nextGaussian() * 45.0);
        rotationPitch += (float)(getRNG().nextGaussian() * 20.0);
        return super.attackEntityFrom(source, amount);
    }
    
    protected void rechargeVis() {
        setCharge(getCharge() + AuraHandler.drainVis(world, getPosition(), 10.0f, false));
    }
    
    public boolean canBePushed() {
        return true;
    }
    
    public boolean canBeCollidedWith() {
        return true;
    }
    
    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (!world.isRemote) {
            dropStuff();
        }
    }
    
    protected void dropStuff() {
        if (getHeldItemMainhand() != null && !getHeldItemMainhand().isEmpty()) {
            entityDropItem(getHeldItemMainhand(), 0.5f);
        }
    }
    
    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (player.getHeldItem(hand).getItem() instanceof ItemNameTag) {
            return false;
        }
        if (!world.isRemote && isOwner(player) && !isDead) {
            if (player.isSneaking()) {
                playSound(SoundsTC.zap, 1.0f, 1.0f);
                dropStuff();
                entityDropItem(new ItemStack(ItemsTC.turretPlacer, 1, 2), 0.5f);
                setDead();
                player.swingArm(hand);
            }
            else {
                player.openGui(Thaumcraft.instance, 14, world, getEntityId(), 0, 0);
            }
            return true;
        }
        return super.processInteract(player, hand);
    }
    
    public void knockBack(Entity p_70653_1_, float p_70653_2_, double p_70653_3_, double p_70653_5_) {
        super.knockBack(p_70653_1_, p_70653_2_, p_70653_3_ / 10.0, p_70653_5_ / 10.0);
        if (motionY > 0.1) {
            motionY = 0.1;
        }
    }
    
    @Override
    protected void entityInit() {
        super.entityInit();
        getDataManager().register(EntityArcaneBore.FACING, EnumFacing.DOWN);
        dataManager.register(EntityArcaneBore.ACTIVE, false);
    }
    
    public boolean isActive() {
        return (boolean) dataManager.get((DataParameter)EntityArcaneBore.ACTIVE);
    }
    
    public void setActive(boolean attacking) {
        dataManager.set(EntityArcaneBore.ACTIVE, attacking);
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setCharge(nbt.getFloat("charge"));
        setFacing(EnumFacing.VALUES[nbt.getByte("faceing")]);
        setActive(nbt.getBoolean("active"));
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setFloat("charge", getCharge());
        nbt.setByte("faceing", (byte) getFacing().getIndex());
        nbt.setBoolean("active", isActive());
    }
    
    public EnumFacing getFacing() {
        return (EnumFacing) getDataManager().get((DataParameter)EntityArcaneBore.FACING);
    }
    
    public void setFacing(EnumFacing face) {
        getDataManager().set(EntityArcaneBore.FACING, face);
    }
    
    public float getCharge() {
        return charge;
    }
    
    public void setCharge(float c) {
        charge = c;
    }
    
    public void move(MoverType mt, double x, double y, double z) {
        super.move(mt, x / 5.0, y, z / 5.0);
    }
    
    public void onKillCommand() {
        attackEntityFrom(DamageSource.OUT_OF_WORLD, 400.0f);
    }
    
    protected void dropFewItems(boolean p_70628_1_, int treasure) {
        float b = treasure * 0.15f;
        if (rand.nextFloat() < 0.2f + b) {
            entityDropItem(new ItemStack(ItemsTC.mind), 0.5f);
        }
        if (rand.nextFloat() < 0.2f + b) {
            entityDropItem(new ItemStack(ItemsTC.morphicResonator), 0.5f);
        }
        if (rand.nextFloat() < 0.2f + b) {
            entityDropItem(new ItemStack(BlocksTC.crystalAir), 0.5f);
        }
        if (rand.nextFloat() < 0.2f + b) {
            entityDropItem(new ItemStack(BlocksTC.crystalEarth), 0.5f);
        }
        if (rand.nextFloat() < 0.5f + b) {
            entityDropItem(new ItemStack(ItemsTC.mechanismSimple), 0.5f);
        }
        if (rand.nextFloat() < 0.5f + b) {
            entityDropItem(new ItemStack(ItemsTC.plate), 0.5f);
        }
        if (rand.nextFloat() < 0.5f + b) {
            entityDropItem(new ItemStack(BlocksTC.plankGreatwood), 0.5f);
        }
    }
    
    public int getVerticalFaceSpeed() {
        return 10;
    }
    
    @Override
    public Team getTeam() {
        if (isOwned()) {
            EntityLivingBase entitylivingbase = getOwnerEntity();
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
    public void handleStatusUpdate(byte par1) {
        if (par1 == 16) {
            clientDigging = true;
        }
        else if (par1 == 17) {
            clientDigging = false;
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
