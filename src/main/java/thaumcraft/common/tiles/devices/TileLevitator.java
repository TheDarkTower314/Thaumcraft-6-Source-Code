package thaumcraft.common.tiles.devices;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileLevitator extends TileThaumcraft implements ITickable
{
    private int[] ranges;
    private int range;
    private int rangeActual;
    private int counter;
    private int vis;
    
    public TileLevitator() {
        ranges = new int[] { 4, 8, 16, 32 };
        range = 1;
        rangeActual = 0;
        counter = 0;
        vis = 0;
    }
    
    public void update() {
        EnumFacing facing = BlockStateUtils.getFacing(getBlockMetadata());
        if (rangeActual > ranges[range]) {
            rangeActual = 0;
        }
        int p = counter % ranges[range];
        if (world.getBlockState(pos.offset(facing, 1 + p)).isOpaqueCube()) {
            if (1 + p < rangeActual) {
                rangeActual = 1 + p;
            }
            counter = -1;
        }
        else if (1 + p > rangeActual) {
            rangeActual = 1 + p;
        }
        ++counter;
        if (!world.isRemote && vis < 10) {
            vis += (int)(AuraHelper.drainVis(world, getPos(), 1.0f, false) * 1200.0f);
            markDirty();
            syncTile(false);
        }
        if (rangeActual > 0 && vis > 0 && BlockStateUtils.isEnabled(getBlockMetadata())) {
            List<Entity> targets = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.getX() - ((facing.getFrontOffsetX() < 0) ? rangeActual : 0), pos.getY() - ((facing.getFrontOffsetY() < 0) ? rangeActual : 0), pos.getZ() - ((facing.getFrontOffsetZ() < 0) ? rangeActual : 0), pos.getX() + 1 + ((facing.getFrontOffsetX() > 0) ? rangeActual : 0), pos.getY() + 1 + ((facing.getFrontOffsetY() > 0) ? rangeActual : 0), pos.getZ() + 1 + ((facing.getFrontOffsetZ() > 0) ? rangeActual : 0)));
            boolean lifted = false;
            if (targets.size() > 0) {
                for (Entity e : targets) {
                    if (!(e instanceof EntityItem) && !e.canBePushed() && !(e instanceof EntityHorse)) {
                        continue;
                    }
                    lifted = true;
                    drawFXAt(e);
                    drawFX(facing, 0.6);
                    if (e.isSneaking() && facing == EnumFacing.UP) {
                        if (e.motionY < 0.0) {
                            Entity entity = e;
                            entity.motionY *= 0.8999999761581421;
                        }
                    }
                    else {
                        Entity entity2 = e;
                        entity2.motionX += 0.1f * facing.getFrontOffsetX();
                        Entity entity3 = e;
                        entity3.motionY += 0.1f * facing.getFrontOffsetY();
                        Entity entity4 = e;
                        entity4.motionZ += 0.1f * facing.getFrontOffsetZ();
                        if (facing.getAxis() != EnumFacing.Axis.Y && !e.onGround) {
                            if (e.motionY < 0.0) {
                                Entity entity5 = e;
                                entity5.motionY *= 0.8999999761581421;
                            }
                            Entity entity6 = e;
                            entity6.motionY += 0.07999999821186066;
                        }
                        if (e.motionX > 0.3499999940395355) {
                            e.motionX = 0.3499999940395355;
                        }
                        if (e.motionY > 0.3499999940395355) {
                            e.motionY = 0.3499999940395355;
                        }
                        if (e.motionZ > 0.3499999940395355) {
                            e.motionZ = 0.3499999940395355;
                        }
                        if (e.motionX < -0.3499999940395355) {
                            e.motionX = -0.3499999940395355;
                        }
                        if (e.motionY < -0.3499999940395355) {
                            e.motionY = -0.3499999940395355;
                        }
                        if (e.motionZ < -0.3499999940395355) {
                            e.motionZ = -0.3499999940395355;
                        }
                    }
                    e.fallDistance = 0.0f;
                    vis -= getCost();
                    if (vis <= 0) {
                        break;
                    }
                }
            }
            drawFX(facing, 0.1);
            if (lifted && !world.isRemote && counter % 20 == 0) {
                markDirty();
            }
        }
    }
    
    private void drawFX(EnumFacing facing, double c) {
        if (world.isRemote && world.rand.nextFloat() < c) {
            float x = pos.getX() + 0.25f + world.rand.nextFloat() * 0.5f;
            float y = pos.getY() + 0.25f + world.rand.nextFloat() * 0.5f;
            float z = pos.getZ() + 0.25f + world.rand.nextFloat() * 0.5f;
            FXDispatcher.INSTANCE.drawLevitatorParticles(x, y, z, facing.getFrontOffsetX() / 50.0, facing.getFrontOffsetY() / 50.0, facing.getFrontOffsetZ() / 50.0);
        }
    }
    
    private void drawFXAt(Entity e) {
        if (world.isRemote && world.rand.nextFloat() < 0.1f) {
            float x = (float)(e.posX + (world.rand.nextFloat() - world.rand.nextFloat()) * e.width);
            float y = (float)(e.posY + world.rand.nextFloat() * e.height);
            float z = (float)(e.posZ + (world.rand.nextFloat() - world.rand.nextFloat()) * e.width);
            FXDispatcher.INSTANCE.drawLevitatorParticles(x, y, z, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.01, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.01, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.01);
        }
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbt) {
        range = nbt.getByte("range");
        vis = nbt.getInteger("vis");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
        nbt.setByte("range", (byte) range);
        nbt.setInteger("vis", vis);
        return nbt;
    }
    
    public int getCost() {
        return ranges[range] * 2;
    }
    
    public void increaseRange(EntityPlayer playerIn) {
        rangeActual = 0;
        if (!world.isRemote) {
            ++range;
            if (range >= ranges.length) {
                range = 0;
            }
            markDirty();
            syncTile(false);
            playerIn.sendMessage(new TextComponentString(String.format(I18n.translateToLocal("tc.levitator"), ranges[range], getCost())));
        }
    }
    
    public RayTraceResult rayTrace(World world, Vec3d vec3d, Vec3d vec3d1, RayTraceResult fullblock) {
        return fullblock;
    }
    
    public void addTraceableCuboids(List<IndexedCuboid6> cuboids) {
        EnumFacing facing = BlockStateUtils.getFacing(getBlockMetadata());
        cuboids.add(new IndexedCuboid6(0, getCuboidByFacing(facing).add(new Vector3(getPos().getX(), getPos().getY(), getPos().getZ()))));
    }
    
    public Cuboid6 getCuboidByFacing(EnumFacing facing) {
        switch (facing) {
            default: {
                return new Cuboid6(0.375, 0.0625, 0.375, 0.625, 0.125, 0.625);
            }
            case DOWN: {
                return new Cuboid6(0.375, 0.875, 0.375, 0.625, 0.9375, 0.625);
            }
            case EAST: {
                return new Cuboid6(0.0625, 0.375, 0.375, 0.125, 0.625, 0.625);
            }
            case WEST: {
                return new Cuboid6(0.875, 0.375, 0.375, 0.9375, 0.625, 0.625);
            }
            case SOUTH: {
                return new Cuboid6(0.375, 0.375, 0.0625, 0.625, 0.625, 0.125);
            }
            case NORTH: {
                return new Cuboid6(0.375, 0.375, 0.875, 0.625, 0.625, 0.9375);
            }
        }
    }
}
