// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities;

import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import thaumcraft.api.capabilities.IPlayerWarp;
import net.minecraft.init.MobEffects;
import thaumcraft.api.potions.PotionFluxTaint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.fx.PacketFXBlockBamf;
import thaumcraft.common.lib.network.PacketHandler;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.common.items.casters.foci.FocusEffectFlux;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.items.casters.foci.FocusMediumCloud;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.FocusMediumRoot;
import thaumcraft.api.casters.FocusPackage;
import net.minecraft.potion.PotionEffect;
import thaumcraft.common.lib.potions.PotionInfectiousVisExhaust;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeedPrime;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.blocks.world.taint.TaintHelper;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.ThaumcraftApi;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.EnumFacing;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.world.aura.AuraHandler;
import thaumcraft.api.aura.AuraHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import thaumcraft.common.lib.utils.EntityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.Block;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import java.util.Iterator;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import thaumcraft.common.lib.utils.RandomItemChooser;
import java.util.ArrayList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.entity.Entity;

public class EntityFluxRift extends Entity
{
    private static final DataParameter<Integer> SEED;
    private static final DataParameter<Integer> SIZE;
    private static final DataParameter<Float> STABILITY;
    private static final DataParameter<Boolean> COLLAPSE;
    int maxSize;
    int lastSize;
    static ArrayList<RandomItemChooser.Item> events;
    public ArrayList<Vec3d> points;
    public ArrayList<Float> pointsWidth;
    
    public EntityFluxRift(final World par1World) {
        super(par1World);
        this.maxSize = 0;
        this.lastSize = -1;
        this.points = new ArrayList<Vec3d>();
        this.pointsWidth = new ArrayList<Float>();
        this.setSize(2.0f, 2.0f);
    }
    
    protected void entityInit() {
        this.getDataManager().register(EntityFluxRift.SEED, 0);
        this.getDataManager().register(EntityFluxRift.SIZE, 5);
        this.getDataManager().register(EntityFluxRift.STABILITY, 0.0f);
        this.getDataManager().register(EntityFluxRift.COLLAPSE, false);
    }
    
    public boolean getCollapse() {
        return (boolean)this.getDataManager().get((DataParameter)EntityFluxRift.COLLAPSE);
    }
    
    public void setCollapse(final boolean b) {
        if (b) {
            this.maxSize = this.getRiftSize();
        }
        this.getDataManager().set(EntityFluxRift.COLLAPSE, b);
    }
    
    public float getRiftStability() {
        return (float)this.getDataManager().get((DataParameter)EntityFluxRift.STABILITY);
    }
    
    public void setRiftStability(float s) {
        if (s > 100.0f) {
            s = 100.0f;
        }
        if (s < -100.0f) {
            s = -100.0f;
        }
        this.getDataManager().set(EntityFluxRift.STABILITY, s);
    }
    
    public int getRiftSize() {
        return (int)this.getDataManager().get((DataParameter)EntityFluxRift.SIZE);
    }
    
    public void setRiftSize(final int s) {
        this.getDataManager().set(EntityFluxRift.SIZE, s);
        this.setSize();
    }
    
    public double getYOffset() {
        return -this.height / 2.0f;
    }
    
    protected void setSize() {
        this.calcSteps(this.points, this.pointsWidth, new Random(this.getRiftSeed()));
        this.lastSize = this.getRiftSize();
        double x0 = Double.MAX_VALUE;
        double y0 = Double.MAX_VALUE;
        double z0 = Double.MAX_VALUE;
        double x2 = Double.MIN_VALUE;
        double y2 = Double.MIN_VALUE;
        double z2 = Double.MIN_VALUE;
        for (final Vec3d v : this.points) {
            if (v.x < x0) {
                x0 = v.x;
            }
            if (v.x > x2) {
                x2 = v.x;
            }
            if (v.y < y0) {
                y0 = v.y;
            }
            if (v.y > y2) {
                y2 = v.y;
            }
            if (v.z < z0) {
                z0 = v.z;
            }
            if (v.z > z2) {
                z2 = v.z;
            }
        }
        this.setEntityBoundingBox(new AxisAlignedBB(this.posX + x0, this.posY + y0, this.posZ + z0, this.posX + x2, this.posY + y2, this.posZ + z2));
        this.width = Math.abs((float)Math.max(x2 - x0, z2 - z0));
        this.height = Math.abs((float)(y2 - y0));
    }
    
    public void setPosition(final double x, final double y, final double z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        if (this.getDataManager() != null) {
            this.setSize();
        }
        else {
            super.setPosition(x, y, z);
        }
    }
    
    public int getRiftSeed() {
        return (int)this.getDataManager().get((DataParameter)EntityFluxRift.SEED);
    }
    
    public void setRiftSeed(final int s) {
        this.getDataManager().set(EntityFluxRift.SEED, s);
    }
    
    public void writeEntityToNBT(final NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger("MaxSize", this.maxSize);
        nbttagcompound.setInteger("RiftSize", this.getRiftSize());
        nbttagcompound.setInteger("RiftSeed", this.getRiftSeed());
        nbttagcompound.setFloat("Stability", this.getRiftStability());
        nbttagcompound.setBoolean("collapse", this.getCollapse());
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbttagcompound) {
        this.maxSize = nbttagcompound.getInteger("MaxSize");
        this.setRiftSize(nbttagcompound.getInteger("RiftSize"));
        this.setRiftSeed(nbttagcompound.getInteger("RiftSeed"));
        this.setRiftStability((float)nbttagcompound.getInteger("Stability"));
        this.setCollapse(nbttagcompound.getBoolean("collapse"));
    }
    
    public void move(final MoverType type, final double x, final double y, final double z) {
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (this.lastSize != this.getRiftSize()) {
            this.setSize();
        }
        if (!this.world.isRemote) {
            if (this.getRiftSeed() == 0) {
                this.setRiftSeed(this.rand.nextInt());
            }
            if (!this.points.isEmpty()) {
                final int pi = this.rand.nextInt(this.points.size() - 1);
                final Vec3d v1 = this.points.get(pi).addVector(this.posX, this.posY, this.posZ);
                final Vec3d v2 = this.points.get(pi + 1).addVector(this.posX, this.posY, this.posZ);
                final RayTraceResult rt = this.world.rayTraceBlocks(v1, v2, false);
                if (rt != null && rt.getBlockPos() != null) {
                    final BlockPos p = new BlockPos(rt.getBlockPos());
                    final IBlockState bs = this.world.getBlockState(p);
                    if (!this.world.isAirBlock(p) && bs.getBlockHardness(this.world, p) >= 0.0f && bs.getBlock().canCollideCheck(bs, false)) {
                        this.world.playEvent(null, 2001, p, Block.getStateId(this.world.getBlockState(p)));
                        this.world.setBlockToAir(p);
                    }
                }
                final List<Entity> el = EntityUtils.getEntitiesInRange(this.getEntityWorld(), v1.x, v1.y, v1.z, this, Entity.class, 0.5);
                for (final Entity e : el) {
                    if (!e.isDead) {
                        if (e instanceof EntityPlayer && ((EntityPlayer)e).isCreative()) {
                            continue;
                        }
                        try {
                            e.attackEntityFrom(DamageSource.OUT_OF_WORLD, 2.0f);
                            if (!(e instanceof EntityItem)) {
                                continue;
                            }
                            e.setDead();
                        }
                        catch (final Exception ex) {}
                    }
                }
            }
            if (this.points.size() < 3 && !this.getCollapse()) {
                this.setCollapse(true);
            }
            if (this.getCollapse()) {
                this.setRiftSize(this.getRiftSize() - 1);
                if (this.rand.nextBoolean()) {
                    AuraHelper.addVis(this.world, this.getPosition(), 1.0f);
                }
                else {
                    AuraHelper.polluteAura(this.world, this.getPosition(), 1.0f, false);
                }
                if (this.rand.nextInt(10) == 0) {
                    this.world.createExplosion(this, this.posX + this.rand.nextGaussian() * 2.0, this.posY + this.rand.nextGaussian() * 2.0, this.posZ + this.rand.nextGaussian() * 2.0, this.rand.nextFloat() / 2.0f, false);
                }
                if (this.getRiftSize() <= 1) {
                    this.completeCollapse();
                    return;
                }
            }
            if (this.ticksExisted % 120 == 0) {
                this.setRiftStability(this.getRiftStability() - 0.2f);
            }
            if (this.ticksExisted % 600 == this.getEntityId() % 600) {
                final float taint = AuraHandler.getFlux(this.world, this.getPosition());
                final double size = Math.sqrt(this.getRiftSize() * 2);
                if (taint >= size && this.getRiftSize() < 100 && this.getStability() != EnumStability.VERY_STABLE) {
                    AuraHandler.drainFlux(this.getEntityWorld(), this.getPosition(), (float)size, false);
                    this.setRiftSize(this.getRiftSize() + 1);
                }
                if (this.getRiftStability() < 0.0f && this.rand.nextInt(1000) < Math.abs(this.getRiftStability()) + this.getRiftSize()) {
                    this.executeRiftEvent();
                }
            }
            if (!this.isDead && this.ticksExisted % 300 == 0) {
                this.playSound(SoundsTC.evilportal, (float)(0.15000000596046448 + this.rand.nextGaussian() * 0.066), (float)(0.75 + this.rand.nextGaussian() * 0.1));
            }
        }
        else {
            if (!this.points.isEmpty() && this.points.size() > 2 && !this.getCollapse() && this.getRiftStability() < 0.0f && this.rand.nextInt(150) < Math.abs(this.getRiftStability())) {
                final int pi = 1 + this.rand.nextInt(this.points.size() - 2);
                final Vec3d v1 = this.points.get(pi).addVector(this.posX, this.posY, this.posZ);
                FXDispatcher.INSTANCE.drawCurlyWisp(v1.x, v1.y, v1.z, 0.0, 0.0, 0.0, 0.1f + this.pointsWidth.get(pi) * 3.0f, 1.0f, 1.0f, 1.0f, 0.25f, null, 1, 0, 0);
            }
            if (!this.points.isEmpty() && this.points.size() > 2 && this.getCollapse()) {
                final int pi = 1 + this.rand.nextInt(this.points.size() - 2);
                final Vec3d v1 = this.points.get(pi).addVector(this.posX, this.posY, this.posZ);
                FXDispatcher.INSTANCE.drawCurlyWisp(v1.x, v1.y, v1.z, 0.0, 0.0, 0.0, 0.1f + this.pointsWidth.get(pi) * 3.0f, 1.0f, 0.3f + this.rand.nextFloat() * 0.1f, 0.3f + this.rand.nextFloat() * 0.1f, 0.4f, null, 1, 0, 0);
            }
        }
    }
    
    public static void createRift(final World world, BlockPos pos) {
        pos = pos.add(world.rand.nextInt(16), 0, world.rand.nextInt(16));
        BlockPos p2 = world.getPrecipitationHeight(pos);
        if (!world.provider.hasSkyLight()) {
            for (p2 = new BlockPos(p2.getX(), 10, p2.getZ()); !world.isAirBlock(p2); p2 = p2.up(world.rand.nextInt(5) + 1)) {
                if (p2.getY() > world.getActualHeight() - 5) {
                    return;
                }
            }
        }
        if (p2.getY() < world.getActualHeight() - 4) {
            if (EntityUtils.getEntitiesInRange(world, p2, null, (Class<? extends Entity>)EntityFluxRift.class, 32.0).size() > 0) {
                return;
            }
            final EntityFluxRift rift = new EntityFluxRift(world);
            rift.setRiftSeed(world.rand.nextInt());
            rift.setLocationAndAngles(p2.getX() + 0.5, p2.getY() + 0.5, p2.getZ() + 0.5, (float)world.rand.nextInt(360), 0.0f);
            final float taint = AuraHandler.getFlux(world, p2);
            final double size = Math.sqrt(taint * 3.0f);
            if (size > 5.0 && world.spawnEntity(rift)) {
                rift.setRiftSize((int)size);
                AuraHandler.drainFlux(world, p2, (float)size, false);
                final List<EntityPlayer> targets2 = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(p2.getX(), p2.getY(), p2.getZ(), p2.getX() + 1, p2.getY() + 1, p2.getZ() + 1).grow(32.0, 32.0, 32.0));
                if (targets2 != null && targets2.size() > 0) {
                    for (final EntityPlayer target : targets2) {
                        final IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(target);
                        if (!knowledge.isResearchKnown("f_toomuchflux")) {
                            target.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("tc.fluxevent.3")), true);
                            ThaumcraftApi.internalMethods.completeResearch(target, "f_toomuchflux");
                        }
                    }
                }
            }
        }
    }
    
    private void executeRiftEvent() {
        final RandomItemChooser ric = new RandomItemChooser();
        final FluxEventEntry ei = (FluxEventEntry)ric.chooseOnWeight(EntityFluxRift.events);
        if (ei == null) {
            return;
        }
        if (!ei.nearTaintAllowed && TaintHelper.isNearTaintSeed(this.world, this.getPosition())) {
            return;
        }
        boolean didit = false;
        switch (ei.event) {
            case 0: {
                final EntityWisp wisp = new EntityWisp(this.world);
                wisp.setLocationAndAngles(this.posX + this.rand.nextGaussian() * 5.0, this.posY + this.rand.nextGaussian() * 5.0, this.posZ + this.rand.nextGaussian() * 5.0, 0.0f, 0.0f);
                if (this.world.rand.nextInt(5) == 0) {
                    wisp.setType(Aspect.FLUX.getTag());
                }
                if (wisp.getCanSpawnHere() && this.world.spawnEntity(wisp)) {
                    didit = true;
                    break;
                }
                break;
            }
            case 1: {
                final EntityTaintSeedPrime seed = new EntityTaintSeedPrime(this.world);
                seed.setLocationAndAngles((int)(this.posX + this.rand.nextGaussian() * 5.0) + 0.5, (int)(this.posY + this.rand.nextGaussian() * 5.0), (int)(this.posZ + this.rand.nextGaussian() * 5.0) + 0.5, (float)this.world.rand.nextInt(360), 0.0f);
                if (seed.getCanSpawnHere() && this.world.spawnEntity(seed)) {
                    didit = true;
                    seed.boost = this.getRiftSize();
                    AuraHelper.polluteAura(this.getEntityWorld(), this.getPosition(), (float)(this.getRiftSize() / 2), true);
                    this.setDead();
                    break;
                }
                break;
            }
            case 2: {
                final List<EntityLivingBase> targets2 = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(16.0, 16.0, 16.0));
                if (targets2 != null && targets2.size() > 0) {
                    for (final EntityLivingBase target : targets2) {
                        didit = true;
                        if (target instanceof EntityPlayer) {
                            ((EntityPlayer)target).sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("tc.fluxevent.2")), true);
                        }
                        final PotionEffect pe = new PotionEffect(PotionInfectiousVisExhaust.instance, 3000, 2);
                        pe.getCurativeItems().clear();
                        try {
                            target.addPotionEffect(pe);
                        }
                        catch (final Exception ex) {}
                    }
                    break;
                }
                break;
            }
            case 3: {
                final EntityPlayer target2 = this.world.getClosestPlayerToEntity(this, 16.0);
                if (target2 != null) {
                    final FocusPackage p = new FocusPackage(target2);
                    final FocusMediumRoot root = new FocusMediumRoot();
                    root.setupFromCasterToTarget(target2, target2, 0.5);
                    p.addNode(root);
                    final FocusMediumCloud fp = new FocusMediumCloud();
                    fp.initialize();
                    fp.getSetting("radius").setValue(MathHelper.getInt(this.rand, 1, 3));
                    fp.getSetting("duration").setValue(MathHelper.getInt(this.rand, Math.min(this.getRiftSize() / 2, 30), Math.min(this.getRiftSize(), 120)));
                    p.addNode(fp);
                    p.addNode(new FocusEffectFlux());
                    FocusEngine.castFocusPackage(target2, p, true);
                    break;
                }
                break;
            }
            case 4: {
                this.setCollapse(true);
                break;
            }
        }
        if (didit) {
            this.setRiftStability(this.getRiftStability() + ei.cost);
        }
    }
    
    private void calcSteps(final ArrayList<Vec3d> pp, final ArrayList<Float> ww, final Random rr) {
        pp.clear();
        ww.clear();
        Vec3d right = new Vec3d(rr.nextGaussian(), rr.nextGaussian(), rr.nextGaussian()).normalize();
        Vec3d left = right.scale(-1.0);
        Vec3d lr = new Vec3d(0.0, 0.0, 0.0);
        Vec3d ll = new Vec3d(0.0, 0.0, 0.0);
        final int steps = MathHelper.ceil(this.getRiftSize() / 3.0f);
        float girth = this.getRiftSize() / 300.0f;
        final double angle = 0.33;
        final float dec = girth / steps;
        for (int a = 0; a < steps; ++a) {
            girth -= dec;
            right = right.rotatePitch((float)(rr.nextGaussian() * angle));
            right = right.rotateYaw((float)(rr.nextGaussian() * angle));
            lr = lr.add(right.scale(0.2));
            pp.add(new Vec3d(lr.x, lr.y, lr.z));
            ww.add(girth);
            left = left.rotatePitch((float)(rr.nextGaussian() * angle));
            left = left.rotateYaw((float)(rr.nextGaussian() * angle));
            ll = ll.add(left.scale(0.2));
            pp.add(0, new Vec3d(ll.x, ll.y, ll.z));
            ww.add(0, girth);
        }
        lr = lr.add(right.scale(0.1));
        pp.add(new Vec3d(lr.x, lr.y, lr.z));
        ww.add(0.0f);
        ll = ll.add(left.scale(0.1));
        pp.add(0, new Vec3d(ll.x, ll.y, ll.z));
        ww.add(0, 0.0f);
    }
    
    public void addStability() {
        this.setRiftStability(this.getRiftStability() + 0.125f);
    }
    
    public EnumStability getStability() {
        return (this.getRiftStability() > 50.0f) ? EnumStability.VERY_STABLE : ((this.getRiftStability() >= 0.0f) ? EnumStability.STABLE : ((this.getRiftStability() > -25.0f) ? EnumStability.UNSTABLE : EnumStability.VERY_UNSTABLE));
    }
    
    public void setFire(final int seconds) {
    }
    
    public boolean isBurning() {
        return false;
    }
    
    public boolean canRenderOnFire() {
        return false;
    }
    
    private void completeCollapse() {
        final int qq = (int)Math.sqrt(this.maxSize);
        if (this.rand.nextInt(100) < qq) {
            this.entityDropItem(new ItemStack(ItemsTC.primordialPearl, 1, 4 + this.rand.nextInt(4)), 0.0f);
        }
        for (int a = 0; a < qq; ++a) {
            this.entityDropItem(new ItemStack(ItemsTC.voidSeed), 0.0f);
        }
        PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockBamf(this.posX, this.posY, this.posZ, 0, true, true, null), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.posX, this.posY, this.posZ, 64.0));
        final List<EntityLivingBase> list = EntityUtils.getEntitiesInRange(this.world, this.posX, this.posY, this.posZ, this, EntityLivingBase.class, 32.0);
        switch (this.getStability()) {
            case VERY_UNSTABLE: {
                for (final EntityLivingBase p : list) {
                    final int w = (int)((1.0 - p.getDistanceSq(this) / 32.0) * 120.0);
                    if (w > 0) {
                        p.addPotionEffect(new PotionEffect(PotionFluxTaint.instance, w * 20, 0));
                    }
                }
            }
            case UNSTABLE: {
                for (final EntityLivingBase p : list) {
                    final int w = (int)((1.0 - p.getDistanceSq(this) / 32.0) * 300.0);
                    if (w > 0) {
                        p.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, w * 20, 0));
                    }
                }
            }
            case STABLE: {
                for (final EntityLivingBase p : list) {
                    if (p instanceof EntityPlayer) {
                        final int w = (int)((1.0 - p.getDistanceSq(this) / 32.0) * 25.0);
                        if (w <= 0) {
                            continue;
                        }
                        ThaumcraftApi.internalMethods.addWarpToPlayer((EntityPlayer)p, w, IPlayerWarp.EnumWarpType.NORMAL);
                        ThaumcraftApi.internalMethods.addWarpToPlayer((EntityPlayer)p, w, IPlayerWarp.EnumWarpType.TEMPORARY);
                    }
                }
                break;
            }
        }
        this.setDead();
    }
    
    static {
        SEED = EntityDataManager.createKey(EntityFluxRift.class, DataSerializers.VARINT);
        SIZE = EntityDataManager.createKey(EntityFluxRift.class, DataSerializers.VARINT);
        STABILITY = EntityDataManager.createKey(EntityFluxRift.class, DataSerializers.FLOAT);
        COLLAPSE = EntityDataManager.createKey(EntityFluxRift.class, DataSerializers.BOOLEAN);
        (EntityFluxRift.events = new ArrayList<RandomItemChooser.Item>()).add(new FluxEventEntry(0, 50, 5, true));
        EntityFluxRift.events.add(new FluxEventEntry(1, 10, 0, false));
        EntityFluxRift.events.add(new FluxEventEntry(2, 20, 10, true));
        EntityFluxRift.events.add(new FluxEventEntry(3, 20, 10, true));
        EntityFluxRift.events.add(new FluxEventEntry(4, 1, 0, true));
    }
    
    static class FluxEventEntry implements RandomItemChooser.Item
    {
        int weight;
        int event;
        int cost;
        boolean nearTaintAllowed;
        
        protected FluxEventEntry(final int event, final int weight, final int cost, final boolean nearTaintAllowed) {
            this.weight = weight;
            this.event = event;
            this.cost = cost;
            this.nearTaintAllowed = nearTaintAllowed;
        }
        
        @Override
        public double getWeight() {
            return this.weight;
        }
    }
    
    public enum EnumStability
    {
        VERY_STABLE, 
        STABLE, 
        UNSTABLE, 
        VERY_UNSTABLE;
    }
}
