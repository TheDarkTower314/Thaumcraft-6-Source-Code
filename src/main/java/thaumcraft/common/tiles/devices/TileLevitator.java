// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.client.fx.FXDispatcher;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.Entity;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.util.ITickable;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileLevitator extends TileThaumcraft implements ITickable
{
    private int[] ranges;
    private int range;
    private int rangeActual;
    private int counter;
    private int vis;
    
    public TileLevitator() {
        this.ranges = new int[] { 4, 8, 16, 32 };
        this.range = 1;
        this.rangeActual = 0;
        this.counter = 0;
        this.vis = 0;
    }
    
    public void update() {
        final EnumFacing facing = BlockStateUtils.getFacing(this.getBlockMetadata());
        if (this.rangeActual > this.ranges[this.range]) {
            this.rangeActual = 0;
        }
        final int p = this.counter % this.ranges[this.range];
        if (this.world.getBlockState(this.pos.offset(facing, 1 + p)).isOpaqueCube()) {
            if (1 + p < this.rangeActual) {
                this.rangeActual = 1 + p;
            }
            this.counter = -1;
        }
        else if (1 + p > this.rangeActual) {
            this.rangeActual = 1 + p;
        }
        ++this.counter;
        if (!this.world.isRemote && this.vis < 10) {
            this.vis += (int)(AuraHelper.drainVis(this.world, this.getPos(), 1.0f, false) * 1200.0f);
            this.markDirty();
            this.syncTile(false);
        }
        if (this.rangeActual > 0 && this.vis > 0 && BlockStateUtils.isEnabled(this.getBlockMetadata())) {
            final List<Entity> targets = this.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.pos.getX() - ((facing.getFrontOffsetX() < 0) ? this.rangeActual : 0), this.pos.getY() - ((facing.getFrontOffsetY() < 0) ? this.rangeActual : 0), this.pos.getZ() - ((facing.getFrontOffsetZ() < 0) ? this.rangeActual : 0), this.pos.getX() + 1 + ((facing.getFrontOffsetX() > 0) ? this.rangeActual : 0), this.pos.getY() + 1 + ((facing.getFrontOffsetY() > 0) ? this.rangeActual : 0), this.pos.getZ() + 1 + ((facing.getFrontOffsetZ() > 0) ? this.rangeActual : 0)));
            boolean lifted = false;
            if (targets.size() > 0) {
                for (final Entity e : targets) {
                    if (!(e instanceof EntityItem) && !e.canBePushed() && !(e instanceof EntityHorse)) {
                        continue;
                    }
                    lifted = true;
                    this.drawFXAt(e);
                    this.drawFX(facing, 0.6);
                    if (e.isSneaking() && facing == EnumFacing.UP) {
                        if (e.motionY < 0.0) {
                            final Entity entity = e;
                            entity.motionY *= 0.8999999761581421;
                        }
                    }
                    else {
                        final Entity entity2 = e;
                        entity2.motionX += 0.1f * facing.getFrontOffsetX();
                        final Entity entity3 = e;
                        entity3.motionY += 0.1f * facing.getFrontOffsetY();
                        final Entity entity4 = e;
                        entity4.motionZ += 0.1f * facing.getFrontOffsetZ();
                        if (facing.getAxis() != EnumFacing.Axis.Y && !e.onGround) {
                            if (e.motionY < 0.0) {
                                final Entity entity5 = e;
                                entity5.motionY *= 0.8999999761581421;
                            }
                            final Entity entity6 = e;
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
                    this.vis -= this.getCost();
                    if (this.vis <= 0) {
                        break;
                    }
                }
            }
            this.drawFX(facing, 0.1);
            if (lifted && !this.world.isRemote && this.counter % 20 == 0) {
                this.markDirty();
            }
        }
    }
    
    private void drawFX(final EnumFacing facing, final double c) {
        if (this.world.isRemote && this.world.rand.nextFloat() < c) {
            final float x = this.pos.getX() + 0.25f + this.world.rand.nextFloat() * 0.5f;
            final float y = this.pos.getY() + 0.25f + this.world.rand.nextFloat() * 0.5f;
            final float z = this.pos.getZ() + 0.25f + this.world.rand.nextFloat() * 0.5f;
            FXDispatcher.INSTANCE.drawLevitatorParticles(x, y, z, facing.getFrontOffsetX() / 50.0, facing.getFrontOffsetY() / 50.0, facing.getFrontOffsetZ() / 50.0);
        }
    }
    
    private void drawFXAt(final Entity e) {
        if (this.world.isRemote && this.world.rand.nextFloat() < 0.1f) {
            final float x = (float)(e.posX + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * e.width);
            final float y = (float)(e.posY + this.world.rand.nextFloat() * e.height);
            final float z = (float)(e.posZ + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * e.width);
            FXDispatcher.INSTANCE.drawLevitatorParticles(x, y, z, (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.01, (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.01, (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.01);
        }
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbt) {
        this.range = nbt.getByte("range");
        this.vis = nbt.getInteger("vis");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbt) {
        nbt.setByte("range", (byte)this.range);
        nbt.setInteger("vis", this.vis);
        return nbt;
    }
    
    public int getCost() {
        return this.ranges[this.range] * 2;
    }
    
    public void increaseRange(final EntityPlayer playerIn) {
        this.rangeActual = 0;
        if (!this.world.isRemote) {
            ++this.range;
            if (this.range >= this.ranges.length) {
                this.range = 0;
            }
            this.markDirty();
            this.syncTile(false);
            playerIn.sendMessage(new TextComponentString(String.format(I18n.translateToLocal("tc.levitator"), this.ranges[this.range], this.getCost())));
        }
    }
    
    public RayTraceResult rayTrace(final World world, final Vec3d vec3d, final Vec3d vec3d1, final RayTraceResult fullblock) {
        return fullblock;
    }
    
    public void addTraceableCuboids(final List<IndexedCuboid6> cuboids) {
        final EnumFacing facing = BlockStateUtils.getFacing(this.getBlockMetadata());
        cuboids.add(new IndexedCuboid6(0, this.getCuboidByFacing(facing).add(new Vector3(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()))));
    }
    
    public Cuboid6 getCuboidByFacing(final EnumFacing facing) {
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
