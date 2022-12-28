package thaumcraft.common.tiles.essentia;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.casters.IInteractWithCaster;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileTube extends TileThaumcraft implements IEssentiaTransport, IInteractWithCaster, ITickable
{
    public static int freq = 5;
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
        facing = EnumFacing.NORTH;
        openSides = new boolean[] { true, true, true, true, true, true };
        essentiaType = null;
        essentiaAmount = 0;
        suctionType = null;
        suction = 0;
        venting = 0;
        count = 0;
        ventColor = 0;
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbttagcompound) {
        essentiaType = Aspect.getAspect(nbttagcompound.getString("type"));
        essentiaAmount = nbttagcompound.getInteger("amount");
        facing = EnumFacing.VALUES[nbttagcompound.getInteger("side")];
        byte[] sides = nbttagcompound.getByteArray("open");
        if (sides != null && sides.length == 6) {
            for (int a = 0; a < 6; ++a) {
                openSides[a] = (sides[a] == 1);
            }
        }
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
        if (essentiaType != null) {
            nbttagcompound.setString("type", essentiaType.getTag());
        }
        nbttagcompound.setInteger("amount", essentiaAmount);
        byte[] sides = new byte[6];
        for (int a = 0; a < 6; ++a) {
            sides[a] = (byte)(openSides[a] ? 1 : 0);
        }
        nbttagcompound.setInteger("side", facing.ordinal());
        nbttagcompound.setByteArray("open", sides);
        return nbttagcompound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        suctionType = Aspect.getAspect(nbttagcompound.getString("stype"));
        suction = nbttagcompound.getInteger("samount");
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        if (suctionType != null) {
            nbttagcompound.setString("stype", suctionType.getTag());
        }
        nbttagcompound.setInteger("samount", suction);
        return nbttagcompound;
    }
    
    public void update() {
        if (venting > 0) {
            --venting;
        }
        if (count == 0) {
            count = world.rand.nextInt(10);
        }
        if (!world.isRemote) {
            if (venting <= 0) {
                if (++count % 2 == 0) {
                    calculateSuction(null, false, false);
                    checkVenting();
                    if (essentiaType != null && essentiaAmount == 0) {
                        essentiaType = null;
                    }
                }
                if (count % 5 == 0 && suction > 0) {
                    equalizeWithNeighbours(false);
                }
            }
        }
        else if (venting > 0) {
            Random r = new Random(hashCode() * 4);
            float rp = r.nextFloat() * 360.0f;
            float ry = r.nextFloat() * 360.0f;
            double fx = -MathHelper.sin(ry / 180.0f * 3.1415927f) * MathHelper.cos(rp / 180.0f * 3.1415927f);
            double fz = MathHelper.cos(ry / 180.0f * 3.1415927f) * MathHelper.cos(rp / 180.0f * 3.1415927f);
            double fy = -MathHelper.sin(rp / 180.0f * 3.1415927f);
            FXDispatcher.INSTANCE.drawVentParticles(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, fx / 5.0, fy / 5.0, fz / 5.0, ventColor);
        }
    }
    
    void calculateSuction(Aspect filter, boolean restrict, boolean directional) {
        suction = 0;
        suctionType = null;
        EnumFacing loc = null;
        for (int dir = 0; dir < 6; ++dir) {
            try {
                loc = EnumFacing.VALUES[dir];
                if (!directional || facing == loc.getOpposite()) {
                    if (isConnectable(loc)) {
                        TileEntity te = ThaumcraftApiHelper.getConnectableTile(world, pos, loc);
                        if (te != null) {
                            IEssentiaTransport ic = (IEssentiaTransport)te;
                            if (filter == null || ic.getSuctionType(loc.getOpposite()) == null || ic.getSuctionType(loc.getOpposite()) == filter) {
                                if (filter != null || getEssentiaAmount(loc) <= 0 || ic.getSuctionType(loc.getOpposite()) == null || getEssentiaType(loc) == ic.getSuctionType(loc.getOpposite())) {
                                    if (filter == null || getEssentiaAmount(loc) <= 0 || getEssentiaType(loc) == null || ic.getSuctionType(loc.getOpposite()) == null || getEssentiaType(loc) == ic.getSuctionType(loc.getOpposite())) {
                                        int suck = ic.getSuctionAmount(loc.getOpposite());
                                        if (suck > 0 && suck > suction + 1) {
                                            Aspect st = ic.getSuctionType(loc.getOpposite());
                                            if (st == null) {
                                                st = filter;
                                            }
                                            setSuction(st, restrict ? (suck / 2) : (suck - 1));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception ex) {}
        }
    }
    
    void checkVenting() {
        EnumFacing loc = null;
        for (int dir = 0; dir < 6; ++dir) {
            try {
                loc = EnumFacing.VALUES[dir];
                if (isConnectable(loc)) {
                    TileEntity te = ThaumcraftApiHelper.getConnectableTile(world, pos, loc);
                    if (te != null) {
                        IEssentiaTransport ic = (IEssentiaTransport)te;
                        int suck = ic.getSuctionAmount(loc.getOpposite());
                        if (suction > 0 && (suck == suction || suck == suction - 1) && suctionType != ic.getSuctionType(loc.getOpposite()) && !(te instanceof TileTubeFilter)) {
                            int c = -1;
                            if (suctionType != null) {
                                c = ModConfig.aspectOrder.indexOf(suctionType);
                            }
                            world.addBlockEvent(pos, BlocksTC.tube, 1, c);
                            venting = 40;
                        }
                    }
                }
            }
            catch (Exception ex) {}
        }
    }
    
    void equalizeWithNeighbours(boolean directional) {
        EnumFacing loc = null;
        if (essentiaAmount > 0) {
            return;
        }
        for (int dir = 0; dir < 6; ++dir) {
            try {
                loc = EnumFacing.VALUES[dir];
                if (!directional || facing != loc.getOpposite()) {
                    if (isConnectable(loc)) {
                        TileEntity te = ThaumcraftApiHelper.getConnectableTile(world, pos, loc);
                        if (te != null) {
                            IEssentiaTransport ic = (IEssentiaTransport)te;
                            if (ic.canOutputTo(loc.getOpposite())) {
                                if ((getSuctionType(null) == null || getSuctionType(null) == ic.getEssentiaType(loc.getOpposite()) || ic.getEssentiaType(loc.getOpposite()) == null) && getSuctionAmount(null) > ic.getSuctionAmount(loc.getOpposite()) && getSuctionAmount(null) >= ic.getMinimumSuction()) {
                                    Aspect a = getSuctionType(null);
                                    if (a == null) {
                                        a = ic.getEssentiaType(loc.getOpposite());
                                        if (a == null) {
                                            a = ic.getEssentiaType(null);
                                        }
                                    }
                                    int am = addEssentia(a, ic.takeEssentia(a, 1, loc.getOpposite()), loc);
                                    if (am > 0) {
                                        if (world.rand.nextInt(100) == 0) {
                                            world.addBlockEvent(pos, BlocksTC.tube, 0, 0);
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception ex) {}
        }
    }
    
    @Override
    public boolean isConnectable(EnumFacing face) {
        return face != null && openSides[face.ordinal()];
    }
    
    @Override
    public boolean canInputFrom(EnumFacing face) {
        return face != null && openSides[face.ordinal()];
    }
    
    @Override
    public boolean canOutputTo(EnumFacing face) {
        return face != null && openSides[face.ordinal()];
    }
    
    @Override
    public void setSuction(Aspect aspect, int amount) {
        suctionType = aspect;
        suction = amount;
    }
    
    @Override
    public Aspect getSuctionType(EnumFacing loc) {
        return suctionType;
    }
    
    @Override
    public int getSuctionAmount(EnumFacing loc) {
        return suction;
    }
    
    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        return essentiaType;
    }
    
    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return essentiaAmount;
    }
    
    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        if (canOutputTo(face) && essentiaType == aspect && essentiaAmount > 0 && amount > 0) {
            --essentiaAmount;
            if (essentiaAmount <= 0) {
                essentiaType = null;
            }
            markDirty();
            return 1;
        }
        return 0;
    }
    
    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        if (canInputFrom(face) && essentiaAmount == 0 && amount > 0) {
            essentiaType = aspect;
            ++essentiaAmount;
            markDirty();
            return 1;
        }
        return 0;
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    public boolean receiveClientEvent(int i, int j) {
        if (i == 0) {
            if (world.isRemote) {
                world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.creak, SoundCategory.AMBIENT, 1.0f, 1.3f + world.rand.nextFloat() * 0.2f, false);
            }
            return true;
        }
        if (i == 1) {
            if (world.isRemote) {
                if (venting <= 0) {
                    world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.1f, 1.0f + world.rand.nextFloat() * 0.1f, false);
                }
                venting = 50;
                if (j == -1 || j >= ModConfig.aspectOrder.size()) {
                    ventColor = 11184810;
                }
                else {
                    ventColor = ModConfig.aspectOrder.get(j).getColor();
                }
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
    
    @Override
    public boolean onCasterRightClick(World world, ItemStack wandstack, EntityPlayer player, BlockPos bp, EnumFacing side, EnumHand hand) {
        RayTraceResult hit = RayTracer.retraceBlock(world, player, pos);
        if (hit == null) {
            return false;
        }
        if (hit.subHit >= 0 && hit.subHit < 6) {
            player.world.playSound(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5, SoundsTC.tool, SoundCategory.BLOCKS, 0.5f, 0.9f + player.world.rand.nextFloat() * 0.2f, false);
            player.swingArm(hand);
            markDirty();
            syncTile(true);
            openSides[hit.subHit] = !openSides[hit.subHit];
            EnumFacing dir = EnumFacing.VALUES[hit.subHit];
            TileEntity tile = world.getTileEntity(pos.offset(dir));
            if (tile != null && tile instanceof TileTube) {
                ((TileTube)tile).openSides[dir.getOpposite().ordinal()] = openSides[hit.subHit];
                ((TileTube)tile).syncTile(true);
                tile.markDirty();
            }
            return true;
        }
        if (hit.subHit == 6) {
            player.world.playSound(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5, SoundsTC.tool, SoundCategory.BLOCKS, 0.5f, 0.9f + player.world.rand.nextFloat() * 0.2f, false);
            player.swingArm(hand);
            int a = facing.ordinal();
            markDirty();
            while (++a < 20) {
                if (canConnectSide(EnumFacing.VALUES[a % 6].getOpposite()) && isConnectable(EnumFacing.VALUES[a % 6].getOpposite())) {
                    a %= 6;
                    facing = EnumFacing.VALUES[a];
                    syncTile(true);
                    markDirty();
                    break;
                }
            }
            return true;
        }
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1);
    }
    
    public RayTraceResult rayTrace(World world, Vec3d vec3d, Vec3d vec3d1, RayTraceResult fullblock) {
        return fullblock;
    }
    
    public boolean canConnectSide(EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos.offset(side));
        return tile != null && tile instanceof IEssentiaTransport;
    }
    
    public void addTraceableCuboids(List<IndexedCuboid6> cuboids) {
        float min = 0.375f;
        float max = 0.625f;
        if (canConnectSide(EnumFacing.DOWN)) {
            cuboids.add(new IndexedCuboid6(0, new Cuboid6(pos.getX() + min, pos.getY(), pos.getZ() + min, pos.getX() + max, pos.getY() + 0.375, pos.getZ() + max)));
        }
        if (canConnectSide(EnumFacing.UP)) {
            cuboids.add(new IndexedCuboid6(1, new Cuboid6(pos.getX() + min, pos.getY() + 0.625, pos.getZ() + min, pos.getX() + max, pos.getY() + 1, pos.getZ() + max)));
        }
        if (canConnectSide(EnumFacing.NORTH)) {
            cuboids.add(new IndexedCuboid6(2, new Cuboid6(pos.getX() + min, pos.getY() + min, pos.getZ(), pos.getX() + max, pos.getY() + max, pos.getZ() + 0.375)));
        }
        if (canConnectSide(EnumFacing.SOUTH)) {
            cuboids.add(new IndexedCuboid6(3, new Cuboid6(pos.getX() + min, pos.getY() + min, pos.getZ() + 0.625, pos.getX() + max, pos.getY() + max, pos.getZ() + 1)));
        }
        if (canConnectSide(EnumFacing.WEST)) {
            cuboids.add(new IndexedCuboid6(4, new Cuboid6(pos.getX(), pos.getY() + min, pos.getZ() + min, pos.getX() + 0.375, pos.getY() + max, pos.getZ() + max)));
        }
        if (canConnectSide(EnumFacing.EAST)) {
            cuboids.add(new IndexedCuboid6(5, new Cuboid6(pos.getX() + 0.625, pos.getY() + min, pos.getZ() + min, pos.getX() + 1, pos.getY() + max, pos.getZ() + max)));
        }
        cuboids.add(new IndexedCuboid6(6, new Cuboid6(pos.getX() + 0.375, pos.getY() + 0.375, pos.getZ() + 0.375, pos.getX() + 0.625, pos.getY() + 0.625, pos.getZ() + 0.625)));
    }
}
