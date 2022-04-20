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
        this.in = 1;
        this.out = 15;
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbt) {
        this.setIn(nbt.getByte("in"));
        this.setOut(nbt.getByte("out"));
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbt) {
        nbt.setByte("in", (byte)this.getIn());
        nbt.setByte("out", (byte)this.getOut());
        return nbt;
    }
    
    public void increaseIn() {
        if (!this.world.isRemote) {
            this.setIn(this.getIn() + 1);
            if (this.getIn() > 15) {
                this.setIn(1);
            }
            this.markDirty();
            this.syncTile(false);
        }
    }
    
    public void increaseOut() {
        if (!this.world.isRemote) {
            this.setOut(this.getOut() + 1);
            if (this.getOut() > 15) {
                this.setOut(1);
            }
            this.markDirty();
            this.syncTile(false);
        }
    }
    
    public RayTraceResult rayTrace(final World world, final Vec3d vec3d, final Vec3d vec3d1, final RayTraceResult fullblock) {
        return fullblock;
    }
    
    public void addTraceableCuboids(final List<IndexedCuboid6> cuboids) {
        final EnumFacing facing = BlockStateUtils.getFacing(this.getBlockMetadata());
        cuboids.add(new IndexedCuboid6(0, this.getCuboid0(facing)));
        cuboids.add(new IndexedCuboid6(1, this.getCuboid1(facing)));
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
        return new Cuboid6(-0.375, 0.0625, -0.375, -0.125, 0.25, -0.125).apply(rot).add(new Vector3(this.getPos().getX() + 0.5, this.getPos().getY(), this.getPos().getZ() + 0.5));
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
        return new Cuboid6(-0.125, 0.0625, 0.125, 0.125, 0.25, 0.375).apply(rot).add(new Vector3(this.getPos().getX() + 0.5, this.getPos().getY(), this.getPos().getZ() + 0.5));
    }
    
    public int getOut() {
        return this.out;
    }
    
    public void setOut(final int out) {
        this.out = out;
    }
    
    public int getIn() {
        return this.in;
    }
    
    public void setIn(final int in) {
        this.in = in;
    }
}
