// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.essentia;

import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import java.util.List;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.config.ModConfig;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.util.math.MathHelper;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import thaumcraft.api.casters.IInteractWithCaster;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileTube extends TileThaumcraft implements IEssentiaTransport, IInteractWithCaster, ITickable
{
    public static final int freq = 5;
    public EnumFacing facing;
    public boolean[] openSides;
    Aspect essentiaType;
    int essentiaAmount;
    Aspect suctionType;
    int suction;
    int venting;
    int count;
    int ventColor;
    
    public TileTube() {
        this.facing = EnumFacing.NORTH;
        this.openSides = new boolean[] { true, true, true, true, true, true };
        this.essentiaType = null;
        this.essentiaAmount = 0;
        this.suctionType = null;
        this.suction = 0;
        this.venting = 0;
        this.count = 0;
        this.ventColor = 0;
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        this.essentiaType = Aspect.getAspect(nbttagcompound.getString("type"));
        this.essentiaAmount = nbttagcompound.getInteger("amount");
        this.facing = EnumFacing.VALUES[nbttagcompound.getInteger("side")];
        final byte[] sides = nbttagcompound.getByteArray("open");
        if (sides != null && sides.length == 6) {
            for (int a = 0; a < 6; ++a) {
                this.openSides[a] = (sides[a] == 1);
            }
        }
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        if (this.essentiaType != null) {
            nbttagcompound.setString("type", this.essentiaType.getTag());
        }
        nbttagcompound.setInteger("amount", this.essentiaAmount);
        final byte[] sides = new byte[6];
        for (int a = 0; a < 6; ++a) {
            sides[a] = (byte)(this.openSides[a] ? 1 : 0);
        }
        nbttagcompound.setInteger("side", this.facing.ordinal());
        nbttagcompound.setByteArray("open", sides);
        return nbttagcompound;
    }
    
    @Override
    public void readFromNBT(final NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.suctionType = Aspect.getAspect(nbttagcompound.getString("stype"));
        this.suction = nbttagcompound.getInteger("samount");
    }
    
    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        if (this.suctionType != null) {
            nbttagcompound.setString("stype", this.suctionType.getTag());
        }
        nbttagcompound.setInteger("samount", this.suction);
        return nbttagcompound;
    }
    
    public void update() {
        if (this.venting > 0) {
            --this.venting;
        }
        if (this.count == 0) {
            this.count = this.world.rand.nextInt(10);
        }
        if (!this.world.isRemote) {
            if (this.venting <= 0) {
                if (++this.count % 2 == 0) {
                    this.calculateSuction(null, false, false);
                    this.checkVenting();
                    if (this.essentiaType != null && this.essentiaAmount == 0) {
                        this.essentiaType = null;
                    }
                }
                if (this.count % 5 == 0 && this.suction > 0) {
                    this.equalizeWithNeighbours(false);
                }
            }
        }
        else if (this.venting > 0) {
            final Random r = new Random(this.hashCode() * 4);
            final float rp = r.nextFloat() * 360.0f;
            final float ry = r.nextFloat() * 360.0f;
            final double fx = -MathHelper.sin(ry / 180.0f * 3.1415927f) * MathHelper.cos(rp / 180.0f * 3.1415927f);
            final double fz = MathHelper.cos(ry / 180.0f * 3.1415927f) * MathHelper.cos(rp / 180.0f * 3.1415927f);
            final double fy = -MathHelper.sin(rp / 180.0f * 3.1415927f);
            FXDispatcher.INSTANCE.drawVentParticles(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, fx / 5.0, fy / 5.0, fz / 5.0, this.ventColor);
        }
    }
    
    void calculateSuction(final Aspect filter, final boolean restrict, final boolean directional) {
        this.suction = 0;
        this.suctionType = null;
        EnumFacing loc = null;
        for (int dir = 0; dir < 6; ++dir) {
            try {
                loc = EnumFacing.VALUES[dir];
                if (!directional || this.facing == loc.getOpposite()) {
                    if (this.isConnectable(loc)) {
                        final TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.world, this.pos, loc);
                        if (te != null) {
                            final IEssentiaTransport ic = (IEssentiaTransport)te;
                            if (filter == null || ic.getSuctionType(loc.getOpposite()) == null || ic.getSuctionType(loc.getOpposite()) == filter) {
                                if (filter != null || this.getEssentiaAmount(loc) <= 0 || ic.getSuctionType(loc.getOpposite()) == null || this.getEssentiaType(loc) == ic.getSuctionType(loc.getOpposite())) {
                                    if (filter == null || this.getEssentiaAmount(loc) <= 0 || this.getEssentiaType(loc) == null || ic.getSuctionType(loc.getOpposite()) == null || this.getEssentiaType(loc) == ic.getSuctionType(loc.getOpposite())) {
                                        final int suck = ic.getSuctionAmount(loc.getOpposite());
                                        if (suck > 0 && suck > this.suction + 1) {
                                            Aspect st = ic.getSuctionType(loc.getOpposite());
                                            if (st == null) {
                                                st = filter;
                                            }
                                            this.setSuction(st, restrict ? (suck / 2) : (suck - 1));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    void checkVenting() {
        EnumFacing loc = null;
        for (int dir = 0; dir < 6; ++dir) {
            try {
                loc = EnumFacing.VALUES[dir];
                if (this.isConnectable(loc)) {
                    final TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.world, this.pos, loc);
                    if (te != null) {
                        final IEssentiaTransport ic = (IEssentiaTransport)te;
                        final int suck = ic.getSuctionAmount(loc.getOpposite());
                        if (this.suction > 0 && (suck == this.suction || suck == this.suction - 1) && this.suctionType != ic.getSuctionType(loc.getOpposite()) && !(te instanceof TileTubeFilter)) {
                            int c = -1;
                            if (this.suctionType != null) {
                                c = ModConfig.aspectOrder.indexOf(this.suctionType);
                            }
                            this.world.addBlockEvent(this.pos, BlocksTC.tube, 1, c);
                            this.venting = 40;
                        }
                    }
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    void equalizeWithNeighbours(final boolean directional) {
        EnumFacing loc = null;
        if (this.essentiaAmount > 0) {
            return;
        }
        for (int dir = 0; dir < 6; ++dir) {
            try {
                loc = EnumFacing.VALUES[dir];
                if (!directional || this.facing != loc.getOpposite()) {
                    if (this.isConnectable(loc)) {
                        final TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.world, this.pos, loc);
                        if (te != null) {
                            final IEssentiaTransport ic = (IEssentiaTransport)te;
                            if (ic.canOutputTo(loc.getOpposite())) {
                                if ((this.getSuctionType(null) == null || this.getSuctionType(null) == ic.getEssentiaType(loc.getOpposite()) || ic.getEssentiaType(loc.getOpposite()) == null) && this.getSuctionAmount(null) > ic.getSuctionAmount(loc.getOpposite()) && this.getSuctionAmount(null) >= ic.getMinimumSuction()) {
                                    Aspect a = this.getSuctionType(null);
                                    if (a == null) {
                                        a = ic.getEssentiaType(loc.getOpposite());
                                        if (a == null) {
                                            a = ic.getEssentiaType(null);
                                        }
                                    }
                                    final int am = this.addEssentia(a, ic.takeEssentia(a, 1, loc.getOpposite()), loc);
                                    if (am > 0) {
                                        if (this.world.rand.nextInt(100) == 0) {
                                            this.world.addBlockEvent(this.pos, BlocksTC.tube, 0, 0);
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    @Override
    public boolean isConnectable(final EnumFacing face) {
        return face != null && this.openSides[face.ordinal()];
    }
    
    @Override
    public boolean canInputFrom(final EnumFacing face) {
        return face != null && this.openSides[face.ordinal()];
    }
    
    @Override
    public boolean canOutputTo(final EnumFacing face) {
        return face != null && this.openSides[face.ordinal()];
    }
    
    @Override
    public void setSuction(final Aspect aspect, final int amount) {
        this.suctionType = aspect;
        this.suction = amount;
    }
    
    @Override
    public Aspect getSuctionType(final EnumFacing loc) {
        return this.suctionType;
    }
    
    @Override
    public int getSuctionAmount(final EnumFacing loc) {
        return this.suction;
    }
    
    @Override
    public Aspect getEssentiaType(final EnumFacing loc) {
        return this.essentiaType;
    }
    
    @Override
    public int getEssentiaAmount(final EnumFacing loc) {
        return this.essentiaAmount;
    }
    
    @Override
    public int takeEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        if (this.canOutputTo(face) && this.essentiaType == aspect && this.essentiaAmount > 0 && amount > 0) {
            --this.essentiaAmount;
            if (this.essentiaAmount <= 0) {
                this.essentiaType = null;
            }
            this.markDirty();
            return 1;
        }
        return 0;
    }
    
    @Override
    public int addEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        if (this.canInputFrom(face) && this.essentiaAmount == 0 && amount > 0) {
            this.essentiaType = aspect;
            ++this.essentiaAmount;
            this.markDirty();
            return 1;
        }
        return 0;
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    public boolean receiveClientEvent(final int i, final int j) {
        if (i == 0) {
            if (this.world.isRemote) {
                this.world.playSound(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, SoundsTC.creak, SoundCategory.AMBIENT, 1.0f, 1.3f + this.world.rand.nextFloat() * 0.2f, false);
            }
            return true;
        }
        if (i == 1) {
            if (this.world.isRemote) {
                if (this.venting <= 0) {
                    this.world.playSound(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.1f, 1.0f + this.world.rand.nextFloat() * 0.1f, false);
                }
                this.venting = 50;
                if (j == -1 || j >= ModConfig.aspectOrder.size()) {
                    this.ventColor = 11184810;
                }
                else {
                    this.ventColor = ModConfig.aspectOrder.get(j).getColor();
                }
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
    
    @Override
    public boolean onCasterRightClick(final World world, final ItemStack wandstack, final EntityPlayer player, final BlockPos bp, final EnumFacing side, final EnumHand hand) {
        final RayTraceResult hit = RayTracer.retraceBlock(world, player, this.pos);
        if (hit == null) {
            return false;
        }
        if (hit.subHit >= 0 && hit.subHit < 6) {
            player.world.playSound(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5, SoundsTC.tool, SoundCategory.BLOCKS, 0.5f, 0.9f + player.world.rand.nextFloat() * 0.2f, false);
            player.swingArm(hand);
            this.markDirty();
            this.syncTile(true);
            this.openSides[hit.subHit] = !this.openSides[hit.subHit];
            final EnumFacing dir = EnumFacing.VALUES[hit.subHit];
            final TileEntity tile = world.getTileEntity(this.pos.offset(dir));
            if (tile != null && tile instanceof TileTube) {
                ((TileTube)tile).openSides[dir.getOpposite().ordinal()] = this.openSides[hit.subHit];
                ((TileTube)tile).syncTile(true);
                tile.markDirty();
            }
            return true;
        }
        if (hit.subHit == 6) {
            player.world.playSound(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5, SoundsTC.tool, SoundCategory.BLOCKS, 0.5f, 0.9f + player.world.rand.nextFloat() * 0.2f, false);
            player.swingArm(hand);
            int a = this.facing.ordinal();
            this.markDirty();
            while (++a < 20) {
                if (this.canConnectSide(EnumFacing.VALUES[a % 6].getOpposite()) && this.isConnectable(EnumFacing.VALUES[a % 6].getOpposite())) {
                    a %= 6;
                    this.facing = EnumFacing.VALUES[a];
                    this.syncTile(true);
                    this.markDirty();
                    break;
                }
            }
            return true;
        }
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), this.getPos().getX() + 1, this.getPos().getY() + 1, this.getPos().getZ() + 1);
    }
    
    public RayTraceResult rayTrace(final World world, final Vec3d vec3d, final Vec3d vec3d1, final RayTraceResult fullblock) {
        return fullblock;
    }
    
    public boolean canConnectSide(final EnumFacing side) {
        final TileEntity tile = this.world.getTileEntity(this.pos.offset(side));
        return tile != null && tile instanceof IEssentiaTransport;
    }
    
    public void addTraceableCuboids(final List<IndexedCuboid6> cuboids) {
        final float min = 0.375f;
        final float max = 0.625f;
        if (this.canConnectSide(EnumFacing.DOWN)) {
            cuboids.add(new IndexedCuboid6(0, new Cuboid6(this.pos.getX() + min, this.pos.getY(), this.pos.getZ() + min, this.pos.getX() + max, this.pos.getY() + 0.375, this.pos.getZ() + max)));
        }
        if (this.canConnectSide(EnumFacing.UP)) {
            cuboids.add(new IndexedCuboid6(1, new Cuboid6(this.pos.getX() + min, this.pos.getY() + 0.625, this.pos.getZ() + min, this.pos.getX() + max, this.pos.getY() + 1, this.pos.getZ() + max)));
        }
        if (this.canConnectSide(EnumFacing.NORTH)) {
            cuboids.add(new IndexedCuboid6(2, new Cuboid6(this.pos.getX() + min, this.pos.getY() + min, this.pos.getZ(), this.pos.getX() + max, this.pos.getY() + max, this.pos.getZ() + 0.375)));
        }
        if (this.canConnectSide(EnumFacing.SOUTH)) {
            cuboids.add(new IndexedCuboid6(3, new Cuboid6(this.pos.getX() + min, this.pos.getY() + min, this.pos.getZ() + 0.625, this.pos.getX() + max, this.pos.getY() + max, this.pos.getZ() + 1)));
        }
        if (this.canConnectSide(EnumFacing.WEST)) {
            cuboids.add(new IndexedCuboid6(4, new Cuboid6(this.pos.getX(), this.pos.getY() + min, this.pos.getZ() + min, this.pos.getX() + 0.375, this.pos.getY() + max, this.pos.getZ() + max)));
        }
        if (this.canConnectSide(EnumFacing.EAST)) {
            cuboids.add(new IndexedCuboid6(5, new Cuboid6(this.pos.getX() + 0.625, this.pos.getY() + min, this.pos.getZ() + min, this.pos.getX() + 1, this.pos.getY() + max, this.pos.getZ() + max)));
        }
        cuboids.add(new IndexedCuboid6(6, new Cuboid6(this.pos.getX() + 0.375, this.pos.getY() + 0.375, this.pos.getZ() + 0.375, this.pos.getX() + 0.625, this.pos.getY() + 0.625, this.pos.getZ() + 0.625)));
    }
}
