package thaumcraft.common.entities;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusMediumRoot;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.world.taint.TaintHelper;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeedPrime;
import thaumcraft.common.items.casters.foci.FocusEffectFlux;
import thaumcraft.common.items.casters.foci.FocusMediumCloud;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockBamf;
import thaumcraft.common.lib.potions.PotionInfectiousVisExhaust;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.RandomItemChooser;
import thaumcraft.common.world.aura.AuraHandler;


public class EntityFluxRift extends Entity
{
    private static DataParameter<Integer> SEED;
    private static DataParameter<Integer> SIZE;
    private static DataParameter<Float> STABILITY;
    private static DataParameter<Boolean> COLLAPSE;
    int maxSize;
    int lastSize;
    static ArrayList<RandomItemChooser.Item> events;
    public ArrayList<Vec3d> points;
    public ArrayList<Float> pointsWidth;
    
    public EntityFluxRift(World par1World) {
        super(par1World);
        maxSize = 0;
        lastSize = -1;
        points = new ArrayList<Vec3d>();
        pointsWidth = new ArrayList<Float>();
        setSize(2.0f, 2.0f);
    }
    
    protected void entityInit() {
        getDataManager().register(EntityFluxRift.SEED, 0);
        getDataManager().register(EntityFluxRift.SIZE, 5);
        getDataManager().register(EntityFluxRift.STABILITY, 0.0f);
        getDataManager().register(EntityFluxRift.COLLAPSE, false);
    }
    
    public boolean getCollapse() {
        return (boolean) getDataManager().get((DataParameter)EntityFluxRift.COLLAPSE);
    }
    
    public void setCollapse(boolean b) {
        if (b) {
            maxSize = getRiftSize();
        }
        getDataManager().set(EntityFluxRift.COLLAPSE, b);
    }
    
    public float getRiftStability() {
        return (float) getDataManager().get((DataParameter)EntityFluxRift.STABILITY);
    }
    
    public void setRiftStability(float s) {
        if (s > 100.0f) {
            s = 100.0f;
        }
        if (s < -100.0f) {
            s = -100.0f;
        }
        getDataManager().set(EntityFluxRift.STABILITY, s);
    }
    
    public int getRiftSize() {
        return (int) getDataManager().get((DataParameter)EntityFluxRift.SIZE);
    }
    
    public void setRiftSize(int s) {
        getDataManager().set(EntityFluxRift.SIZE, s);
        setSize();
    }
    
    public double getYOffset() {
        return -height / 2.0f;
    }
    
    protected void setSize() {
        calcSteps(points, pointsWidth, new Random(getRiftSeed()));
        lastSize = getRiftSize();
        double x0 = Double.MAX_VALUE;
        double y0 = Double.MAX_VALUE;
        double z0 = Double.MAX_VALUE;
        double x2 = Double.MIN_VALUE;
        double y2 = Double.MIN_VALUE;
        double z2 = Double.MIN_VALUE;
        for (Vec3d v : points) {
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
        setEntityBoundingBox(new AxisAlignedBB(posX + x0, posY + y0, posZ + z0, posX + x2, posY + y2, posZ + z2));
        width = Math.abs((float)Math.max(x2 - x0, z2 - z0));
        height = Math.abs((float)(y2 - y0));
    }
    
    public void setPosition(double x, double y, double z) {
        posX = x;
        posY = y;
        posZ = z;
        if (getDataManager() != null) {
            setSize();
        }
        else {
            super.setPosition(x, y, z);
        }
    }
    
    public int getRiftSeed() {
        return (int) getDataManager().get((DataParameter)EntityFluxRift.SEED);
    }
    
    public void setRiftSeed(int s) {
        getDataManager().set(EntityFluxRift.SEED, s);
    }
    
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger("MaxSize", maxSize);
        nbttagcompound.setInteger("RiftSize", getRiftSize());
        nbttagcompound.setInteger("RiftSeed", getRiftSeed());
        nbttagcompound.setFloat("Stability", getRiftStability());
        nbttagcompound.setBoolean("collapse", getCollapse());
    }
    
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        maxSize = nbttagcompound.getInteger("MaxSize");
        setRiftSize(nbttagcompound.getInteger("RiftSize"));
        setRiftSeed(nbttagcompound.getInteger("RiftSeed"));
        setRiftStability((float)nbttagcompound.getInteger("Stability"));
        setCollapse(nbttagcompound.getBoolean("collapse"));
    }
    
    public void move(MoverType type, double x, double y, double z) {
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (lastSize != getRiftSize()) {
            setSize();
        }
        if (!world.isRemote) {
            if (getRiftSeed() == 0) {
                setRiftSeed(rand.nextInt());
            }
            if (!points.isEmpty()) {
                int pi = rand.nextInt(points.size() - 1);
                Vec3d v1 = points.get(pi).addVector(posX, posY, posZ);
                Vec3d v2 = points.get(pi + 1).addVector(posX, posY, posZ);
                RayTraceResult rt = world.rayTraceBlocks(v1, v2, false);
                if (rt != null && rt.getBlockPos() != null) {
                    BlockPos p = new BlockPos(rt.getBlockPos());
                    IBlockState bs = world.getBlockState(p);
                    if (!world.isAirBlock(p) && bs.getBlockHardness(world, p) >= 0.0f && bs.getBlock().canCollideCheck(bs, false)) {
                        world.playEvent(null, 2001, p, Block.getStateId(world.getBlockState(p)));
                        world.setBlockToAir(p);
                    }
                }
                List<Entity> el = EntityUtils.getEntitiesInRange(getEntityWorld(), v1.x, v1.y, v1.z, this, Entity.class, 0.5);
                for (Entity e : el) {
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
                        catch (Exception ex) {}
                    }
                }
            }
            if (points.size() < 3 && !getCollapse()) {
                setCollapse(true);
            }
            if (getCollapse()) {
                setRiftSize(getRiftSize() - 1);
                if (rand.nextBoolean()) {
                    AuraHelper.addVis(world, getPosition(), 1.0f);
                }
                else {
                    AuraHelper.polluteAura(world, getPosition(), 1.0f, false);
                }
                if (rand.nextInt(10) == 0) {
                    world.createExplosion(this, posX + rand.nextGaussian() * 2.0, posY + rand.nextGaussian() * 2.0, posZ + rand.nextGaussian() * 2.0, rand.nextFloat() / 2.0f, false);
                }
                if (getRiftSize() <= 1) {
                    completeCollapse();
                    return;
                }
            }
            if (ticksExisted % 120 == 0) {
                setRiftStability(getRiftStability() - 0.2f);
            }
            if (ticksExisted % 600 == getEntityId() % 600) {
                float taint = AuraHandler.getFlux(world, getPosition());
                double size = Math.sqrt(getRiftSize() * 2);
                if (taint >= size && getRiftSize() < 100 && getStability() != EnumStability.VERY_STABLE) {
                    AuraHandler.drainFlux(getEntityWorld(), getPosition(), (float)size, false);
                    setRiftSize(getRiftSize() + 1);
                }
                if (getRiftStability() < 0.0f && rand.nextInt(1000) < Math.abs(getRiftStability()) + getRiftSize()) {
                    executeRiftEvent();
                }
            }
            if (!isDead && ticksExisted % 300 == 0) {
                playSound(SoundsTC.evilportal, (float)(0.15000000596046448 + rand.nextGaussian() * 0.066), (float)(0.75 + rand.nextGaussian() * 0.1));
            }
        }
        else {
            if (!points.isEmpty() && points.size() > 2 && !getCollapse() && getRiftStability() < 0.0f && rand.nextInt(150) < Math.abs(getRiftStability())) {
                int pi = 1 + rand.nextInt(points.size() - 2);
                Vec3d v1 = points.get(pi).addVector(posX, posY, posZ);
                FXDispatcher.INSTANCE.drawCurlyWisp(v1.x, v1.y, v1.z, 0.0, 0.0, 0.0, 0.1f + pointsWidth.get(pi) * 3.0f, 1.0f, 1.0f, 1.0f, 0.25f, null, 1, 0, 0);
            }
            if (!points.isEmpty() && points.size() > 2 && getCollapse()) {
                int pi = 1 + rand.nextInt(points.size() - 2);
                Vec3d v1 = points.get(pi).addVector(posX, posY, posZ);
                FXDispatcher.INSTANCE.drawCurlyWisp(v1.x, v1.y, v1.z, 0.0, 0.0, 0.0, 0.1f + pointsWidth.get(pi) * 3.0f, 1.0f, 0.3f + rand.nextFloat() * 0.1f, 0.3f + rand.nextFloat() * 0.1f, 0.4f, null, 1, 0, 0);
            }
        }
    }
    
    public static void createRift(World world, BlockPos pos) {
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
            EntityFluxRift rift = new EntityFluxRift(world);
            rift.setRiftSeed(world.rand.nextInt());
            rift.setLocationAndAngles(p2.getX() + 0.5, p2.getY() + 0.5, p2.getZ() + 0.5, (float)world.rand.nextInt(360), 0.0f);
            float taint = AuraHandler.getFlux(world, p2);
            double size = Math.sqrt(taint * 3.0f);
            if (size > 5.0 && world.spawnEntity(rift)) {
                rift.setRiftSize((int)size);
                AuraHandler.drainFlux(world, p2, (float)size, false);
                List<EntityPlayer> targets2 = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(p2.getX(), p2.getY(), p2.getZ(), p2.getX() + 1, p2.getY() + 1, p2.getZ() + 1).grow(32.0, 32.0, 32.0));
                if (targets2 != null && targets2.size() > 0) {
                    for (EntityPlayer target : targets2) {
                        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(target);
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
        RandomItemChooser ric = new RandomItemChooser();
        FluxEventEntry ei = (FluxEventEntry)ric.chooseOnWeight(EntityFluxRift.events);
        if (ei == null) {
            return;
        }
        if (!ei.nearTaintAllowed && TaintHelper.isNearTaintSeed(world, getPosition())) {
            return;
        }
        boolean didit = false;
        switch (ei.event) {
            case 0: {
                EntityWisp wisp = new EntityWisp(world);
                wisp.setLocationAndAngles(posX + rand.nextGaussian() * 5.0, posY + rand.nextGaussian() * 5.0, posZ + rand.nextGaussian() * 5.0, 0.0f, 0.0f);
                if (world.rand.nextInt(5) == 0) {
                    wisp.setType(Aspect.FLUX.getTag());
                }
                if (wisp.getCanSpawnHere() && world.spawnEntity(wisp)) {
                    didit = true;
                    break;
                }
                break;
            }
            case 1: {
                EntityTaintSeedPrime seed = new EntityTaintSeedPrime(world);
                seed.setLocationAndAngles((int)(posX + rand.nextGaussian() * 5.0) + 0.5, (int)(posY + rand.nextGaussian() * 5.0), (int)(posZ + rand.nextGaussian() * 5.0) + 0.5, (float) world.rand.nextInt(360), 0.0f);
                if (seed.getCanSpawnHere() && world.spawnEntity(seed)) {
                    didit = true;
                    seed.boost = getRiftSize();
                    AuraHelper.polluteAura(getEntityWorld(), getPosition(), (float)(getRiftSize() / 2), true);
                    setDead();
                    break;
                }
                break;
            }
            case 2: {
                List<EntityLivingBase> targets2 = world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox().grow(16.0, 16.0, 16.0));
                if (targets2 != null && targets2.size() > 0) {
                    for (EntityLivingBase target : targets2) {
                        didit = true;
                        if (target instanceof EntityPlayer) {
                            ((EntityPlayer)target).sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("tc.fluxevent.2")), true);
                        }
                        PotionEffect pe = new PotionEffect(PotionInfectiousVisExhaust.instance, 3000, 2);
                        pe.getCurativeItems().clear();
                        try {
                            target.addPotionEffect(pe);
                        }
                        catch (Exception ex) {}
                    }
                    break;
                }
                break;
            }
            case 3: {
                EntityPlayer target2 = world.getClosestPlayerToEntity(this, 16.0);
                if (target2 != null) {
                    FocusPackage p = new FocusPackage(target2);
                    FocusMediumRoot root = new FocusMediumRoot();
                    root.setupFromCasterToTarget(target2, target2, 0.5);
                    p.addNode(root);
                    FocusMediumCloud fp = new FocusMediumCloud();
                    fp.initialize();
                    fp.getSetting("radius").setValue(MathHelper.getInt(rand, 1, 3));
                    fp.getSetting("duration").setValue(MathHelper.getInt(rand, Math.min(getRiftSize() / 2, 30), Math.min(getRiftSize(), 120)));
                    p.addNode(fp);
                    p.addNode(new FocusEffectFlux());
                    FocusEngine.castFocusPackage(target2, p, true);
                    break;
                }
                break;
            }
            case 4: {
                setCollapse(true);
                break;
            }
        }
        if (didit) {
            setRiftStability(getRiftStability() + ei.cost);
        }
    }
    
    private void calcSteps(ArrayList<Vec3d> pp, ArrayList<Float> ww, Random rr) {
        pp.clear();
        ww.clear();
        Vec3d right = new Vec3d(rr.nextGaussian(), rr.nextGaussian(), rr.nextGaussian()).normalize();
        Vec3d left = right.scale(-1.0);
        Vec3d lr = new Vec3d(0.0, 0.0, 0.0);
        Vec3d ll = new Vec3d(0.0, 0.0, 0.0);
        int steps = MathHelper.ceil(getRiftSize() / 3.0f);
        float girth = getRiftSize() / 300.0f;
        double angle = 0.33;
        float dec = girth / steps;
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
        setRiftStability(getRiftStability() + 0.125f);
    }
    
    public EnumStability getStability() {
        return (getRiftStability() > 50.0f) ? EnumStability.VERY_STABLE : ((getRiftStability() >= 0.0f) ? EnumStability.STABLE : ((getRiftStability() > -25.0f) ? EnumStability.UNSTABLE : EnumStability.VERY_UNSTABLE));
    }
    
    public void setFire(int seconds) {
    }
    
    public boolean isBurning() {
        return false;
    }
    
    public boolean canRenderOnFire() {
        return false;
    }
    
    private void completeCollapse() {
        int qq = (int)Math.sqrt(maxSize);
        if (rand.nextInt(100) < qq) {
            entityDropItem(new ItemStack(ItemsTC.primordialPearl, 1, 4 + rand.nextInt(4)), 0.0f);
        }
        for (int a = 0; a < qq; ++a) {
            entityDropItem(new ItemStack(ItemsTC.voidSeed), 0.0f);
        }
        PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockBamf(posX, posY, posZ, 0, true, true, null), new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 64.0));
        List<EntityLivingBase> list = EntityUtils.getEntitiesInRange(world, posX, posY, posZ, this, EntityLivingBase.class, 32.0);
        switch (getStability()) {
            case VERY_UNSTABLE: {
                for (EntityLivingBase p : list) {
                    int w = (int)((1.0 - p.getDistanceSq(this) / 32.0) * 120.0);
                    if (w > 0) {
                        p.addPotionEffect(new PotionEffect(PotionFluxTaint.instance, w * 20, 0));
                    }
                }
            }
            case UNSTABLE: {
                for (EntityLivingBase p : list) {
                    int w = (int)((1.0 - p.getDistanceSq(this) / 32.0) * 300.0);
                    if (w > 0) {
                        p.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, w * 20, 0));
                    }
                }
            }
            case STABLE: {
                for (EntityLivingBase p : list) {
                    if (p instanceof EntityPlayer) {
                        int w = (int)((1.0 - p.getDistanceSq(this) / 32.0) * 25.0);
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
        setDead();
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
        
        protected FluxEventEntry(int event, int weight, int cost, boolean nearTaintAllowed) {
            this.weight = weight;
            this.event = event;
            this.cost = cost;
            this.nearTaintAllowed = nearTaintAllowed;
        }
        
        @Override
        public double getWeight() {
            return weight;
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
