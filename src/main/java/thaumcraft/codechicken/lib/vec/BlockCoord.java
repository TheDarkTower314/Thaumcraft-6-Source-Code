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
        return this.x == o2.x && this.y == o2.y && this.z == o2.z;
    }
    
    @Override
    public int hashCode() {
        return (this.x ^ this.z) * 31 + this.y;
    }
    
    @Override
    public int compareTo(final BlockCoord o) {
        if (this.x != o.x) {
            return (this.x < o.x) ? 1 : -1;
        }
        if (this.y != o.y) {
            return (this.y < o.y) ? 1 : -1;
        }
        if (this.z != o.z) {
            return (this.z < o.z) ? 1 : -1;
        }
        return 0;
    }
    
    public Vector3 toVector3Centered() {
        return new Vector3(this.x + 0.5, this.y + 0.5, this.z + 0.5);
    }
    
    public BlockCoord multiply(final int i) {
        this.x *= i;
        this.y *= i;
        this.z *= i;
        return this;
    }
    
    public double mag() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public int mag2() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }
    
    public boolean isZero() {
        return this.x == 0 && this.y == 0 && this.z == 0;
    }
    
    public boolean isAxial() {
        return (this.x == 0) ? (this.y == 0 || this.z == 0) : (this.y == 0 && this.z == 0);
    }
    
    public BlockCoord add(final BlockCoord coord2) {
        this.x += coord2.x;
        this.y += coord2.y;
        this.z += coord2.z;
        return this;
    }
    
    public BlockCoord add(final int i, final int j, final int k) {
        this.x += i;
        this.y += j;
        this.z += k;
        return this;
    }
    
    public BlockCoord sub(final BlockCoord coord2) {
        this.x -= coord2.x;
        this.y -= coord2.y;
        this.z -= coord2.z;
        return this;
    }
    
    public BlockCoord sub(final int i, final int j, final int k) {
        this.x -= i;
        this.y -= j;
        this.z -= k;
        return this;
    }
    
    public BlockCoord offset(final int side) {
        return this.offset(side, 1);
    }
    
    public BlockCoord offset(final int side, final int amount) {
        final BlockCoord offset = BlockCoord.sideOffsets[side];
        this.x += offset.x * amount;
        this.y += offset.y * amount;
        this.z += offset.z * amount;
        return this;
    }
    
    public BlockCoord inset(final int side) {
        return this.inset(side, 1);
    }
    
    public BlockCoord inset(final int side, final int amount) {
        return this.offset(side, -amount);
    }
    
    public int getSide(final int side) {
        switch (side) {
            case 0:
            case 1: {
                return this.y;
            }
            case 2:
            case 3: {
                return this.z;
            }
            case 4:
            case 5: {
                return this.x;
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
                this.y = v;
                break;
            }
            case 2:
            case 3: {
                this.z = v;
                break;
            }
            case 4:
            case 5: {
                this.x = v;
                break;
            }
            default: {
                throw new IndexOutOfBoundsException("Switch Falloff");
            }
        }
        return this;
    }
    
    public int[] intArray() {
        return new int[] { this.x, this.y, this.z };
    }
    
    public BlockPos pos() {
        return new BlockPos(this.x, this.y, this.z);
    }
    
    @Override
    public BlockCoord copy() {
        return new BlockCoord(this.x, this.y, this.z);
    }
    
    public BlockCoord set(final int i, final int j, final int k) {
        this.x = i;
        this.y = j;
        this.z = k;
        return this;
    }
    
    public BlockCoord set(final BlockCoord coord) {
        return this.set(coord.x, coord.y, coord.z);
    }
    
    public BlockCoord set(final BlockPos pos) {
        return this.set(pos.getX(), pos.getY(), pos.getZ());
    }
    
    public BlockCoord set(final int[] ia) {
        return this.set(ia[0], ia[1], ia[2]);
    }
    
    public BlockCoord set(final TileEntity tile) {
        return this.set(tile.getPos());
    }
    
    public int toSide() {
        if (!this.isAxial()) {
            return -1;
        }
        if (this.y < 0) {
            return 0;
        }
        if (this.y > 0) {
            return 1;
        }
        if (this.z < 0) {
            return 2;
        }
        if (this.z > 0) {
            return 3;
        }
        if (this.x < 0) {
            return 4;
        }
        if (this.x > 0) {
            return 5;
        }
        return -1;
    }
    
    public int absSum() {
        return ((this.x < 0) ? (-this.x) : this.x) + ((this.y < 0) ? (-this.y) : this.y) + ((this.z < 0) ? (-this.z) : this.z);
    }
    
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
    
    static {
        sideOffsets = new BlockCoord[] { new BlockCoord(0, -1, 0), new BlockCoord(0, 1, 0), new BlockCoord(0, 0, -1), new BlockCoord(0, 0, 1), new BlockCoord(-1, 0, 0), new BlockCoord(1, 0, 0) };
    }
}
