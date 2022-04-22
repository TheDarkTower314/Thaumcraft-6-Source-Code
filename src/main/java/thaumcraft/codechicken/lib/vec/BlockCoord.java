// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.vec;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import thaumcraft.codechicken.lib.util.Copyable;

public class BlockCoord implements Comparable<BlockCoord>, Copyable<BlockCoord>
{
    public static final BlockCoord[] sideOffsets;
    public int x;
    public int y;
    public int z;
    
    public BlockCoord(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public BlockCoord(final BlockPos pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }
    
    public BlockCoord(final Vector3 v) {
        this(MathHelper.floor(v.x), MathHelper.floor(v.y), MathHelper.floor(v.z));
    }
    
    public BlockCoord(final TileEntity tile) {
        this(tile.getPos());
    }
    
    public BlockCoord(final int[] ia) {
        this(ia[0], ia[1], ia[2]);
    }
    
    public BlockCoord() {
    }
    
    public static BlockCoord fromAxes(final int[] ia) {
        return new BlockCoord(ia[2], ia[0], ia[1]);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof BlockCoord)) {
            return false;
        }
        final BlockCoord o2 = (BlockCoord)obj;
        return x == o2.x && y == o2.y && z == o2.z;
    }
    
    @Override
    public int hashCode() {
        return (x ^ z) * 31 + y;
    }
    
    @Override
    public int compareTo(final BlockCoord o) {
        if (x != o.x) {
            return (x < o.x) ? 1 : -1;
        }
        if (y != o.y) {
            return (y < o.y) ? 1 : -1;
        }
        if (z != o.z) {
            return (z < o.z) ? 1 : -1;
        }
        return 0;
    }
    
    public Vector3 toVector3Centered() {
        return new Vector3(x + 0.5, y + 0.5, z + 0.5);
    }
    
    public BlockCoord multiply(final int i) {
        x *= i;
        y *= i;
        z *= i;
        return this;
    }
    
    public double mag() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    
    public int mag2() {
        return x * x + y * y + z * z;
    }
    
    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }
    
    public boolean isAxial() {
        return (x == 0) ? (y == 0 || z == 0) : (y == 0 && z == 0);
    }
    
    public BlockCoord add(final BlockCoord coord2) {
        x += coord2.x;
        y += coord2.y;
        z += coord2.z;
        return this;
    }
    
    public BlockCoord add(final int i, final int j, final int k) {
        x += i;
        y += j;
        z += k;
        return this;
    }
    
    public BlockCoord sub(final BlockCoord coord2) {
        x -= coord2.x;
        y -= coord2.y;
        z -= coord2.z;
        return this;
    }
    
    public BlockCoord sub(final int i, final int j, final int k) {
        x -= i;
        y -= j;
        z -= k;
        return this;
    }
    
    public BlockCoord offset(final int side) {
        return offset(side, 1);
    }
    
    public BlockCoord offset(final int side, final int amount) {
        final BlockCoord offset = BlockCoord.sideOffsets[side];
        x += offset.x * amount;
        y += offset.y * amount;
        z += offset.z * amount;
        return this;
    }
    
    public BlockCoord inset(final int side) {
        return inset(side, 1);
    }
    
    public BlockCoord inset(final int side, final int amount) {
        return offset(side, -amount);
    }
    
    public int getSide(final int side) {
        switch (side) {
            case 0:
            case 1: {
                return y;
            }
            case 2:
            case 3: {
                return z;
            }
            case 4:
            case 5: {
                return x;
            }
            default: {
                throw new IndexOutOfBoundsException("Switch Falloff");
            }
        }
    }
    
    public BlockCoord setSide(final int s, final int v) {
        switch (s) {
            case 0:
            case 1: {
                y = v;
                break;
            }
            case 2:
            case 3: {
                z = v;
                break;
            }
            case 4:
            case 5: {
                x = v;
                break;
            }
            default: {
                throw new IndexOutOfBoundsException("Switch Falloff");
            }
        }
        return this;
    }
    
    public int[] intArray() {
        return new int[] { x, y, z };
    }
    
    public BlockPos pos() {
        return new BlockPos(x, y, z);
    }
    
    @Override
    public BlockCoord copy() {
        return new BlockCoord(x, y, z);
    }
    
    public BlockCoord set(final int i, final int j, final int k) {
        x = i;
        y = j;
        z = k;
        return this;
    }
    
    public BlockCoord set(final BlockCoord coord) {
        return set(coord.x, coord.y, coord.z);
    }
    
    public BlockCoord set(final BlockPos pos) {
        return set(pos.getX(), pos.getY(), pos.getZ());
    }
    
    public BlockCoord set(final int[] ia) {
        return set(ia[0], ia[1], ia[2]);
    }
    
    public BlockCoord set(final TileEntity tile) {
        return set(tile.getPos());
    }
    
    public int toSide() {
        if (!isAxial()) {
            return -1;
        }
        if (y < 0) {
            return 0;
        }
        if (y > 0) {
            return 1;
        }
        if (z < 0) {
            return 2;
        }
        if (z > 0) {
            return 3;
        }
        if (x < 0) {
            return 4;
        }
        if (x > 0) {
            return 5;
        }
        return -1;
    }
    
    public int absSum() {
        return ((x < 0) ? (-x) : x) + ((y < 0) ? (-y) : y) + ((z < 0) ? (-z) : z);
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
    
    static {
        sideOffsets = new BlockCoord[] { new BlockCoord(0, -1, 0), new BlockCoord(0, 1, 0), new BlockCoord(0, 0, -1), new BlockCoord(0, 0, 1), new BlockCoord(-1, 0, 0), new BlockCoord(1, 0, 0) };
    }
}
