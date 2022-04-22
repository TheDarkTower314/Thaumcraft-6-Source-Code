// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import thaumcraft.codechicken.lib.vec.Transformation;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.codechicken.lib.vec.Rotation;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import net.minecraft.util.EnumFacing;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import java.util.List;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileRedstoneRelay extends TileThaumcraft
{
    private int in;
    private int out;
    
    public TileRedstoneRelay() {
        in = 1;
        out = 15;
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbt) {
        setIn(nbt.getByte("in"));
        setOut(nbt.getByte("out"));
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbt) {
        nbt.setByte("in", (byte) getIn());
        nbt.setByte("out", (byte) getOut());
        return nbt;
    }
    
    public void increaseIn() {
        if (!world.isRemote) {
            setIn(getIn() + 1);
            if (getIn() > 15) {
                setIn(1);
            }
            markDirty();
            syncTile(false);
        }
    }
    
    public void increaseOut() {
        if (!world.isRemote) {
            setOut(getOut() + 1);
            if (getOut() > 15) {
                setOut(1);
            }
            markDirty();
            syncTile(false);
        }
    }
    
    public RayTraceResult rayTrace(final World world, final Vec3d vec3d, final Vec3d vec3d1, final RayTraceResult fullblock) {
        return fullblock;
    }
    
    public void addTraceableCuboids(final List<IndexedCuboid6> cuboids) {
        final EnumFacing facing = BlockStateUtils.getFacing(getBlockMetadata());
        cuboids.add(new IndexedCuboid6(0, getCuboid0(facing)));
        cuboids.add(new IndexedCuboid6(1, getCuboid1(facing)));
    }
    
    public Cuboid6 getCuboid0(final EnumFacing facing) {
        Transformation rot = Rotation.quarterRotations[0];
        switch (facing) {
            case WEST: {
                rot = Rotation.quarterRotations[1];
                break;
            }
            case NORTH: {
                rot = Rotation.quarterRotations[2];
                break;
            }
            case EAST: {
                rot = Rotation.quarterRotations[3];
                break;
            }
        }
        return new Cuboid6(-0.375, 0.0625, -0.375, -0.125, 0.25, -0.125).apply(rot).add(new Vector3(getPos().getX() + 0.5, getPos().getY(), getPos().getZ() + 0.5));
    }
    
    public Cuboid6 getCuboid1(final EnumFacing facing) {
        Transformation rot = Rotation.quarterRotations[0];
        switch (facing) {
            case WEST: {
                rot = Rotation.quarterRotations[1];
                break;
            }
            case NORTH: {
                rot = Rotation.quarterRotations[2];
                break;
            }
            case EAST: {
                rot = Rotation.quarterRotations[3];
                break;
            }
        }
        return new Cuboid6(-0.125, 0.0625, 0.125, 0.125, 0.25, 0.375).apply(rot).add(new Vector3(getPos().getX() + 0.5, getPos().getY(), getPos().getZ() + 0.5));
    }
    
    public int getOut() {
        return out;
    }
    
    public void setOut(final int out) {
        this.out = out;
    }
    
    public int getIn() {
        return in;
    }
    
    public void setIn(final int in) {
        this.in = in;
    }
}
